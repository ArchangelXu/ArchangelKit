package studio.archangel.toolkit3.utils.networking;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by Administrator on 2015/10/16.
 */
public class AngelNetProgressRequestBody extends RequestBody {

	private final File file;
	private final AngelNetCallBack listener;
	private final String contentType;
	private AngelNet handler;

	public AngelNetProgressRequestBody(File file, String contentType, AngelNetCallBack listener, AngelNet angelNet) {
		this.file = file;
		this.contentType = contentType;
		this.listener = listener;
		handler = angelNet;
	}

	@Override
	public long contentLength() {
		return file.length();
	}

	@Override
	public MediaType contentType() {
		return MediaType.parse(contentType);
	}

	@Override
	public void writeTo(BufferedSink sink) throws IOException {
		Source source = null;
		try {
			source = Okio.source(file);
			final long length = file.length();
			long total = 0;
			long read;

			while ((read = source.read(sink.buffer(), 2048)) != -1) {
				total += read;
				sink.flush();
				final long finalTotal = total;
				if (listener != null) {
					if (listener instanceof AngelNetProgressCallBack) {
						handler.execute(new Runnable() {
							@Override
							public void run() {
								((AngelNetProgressCallBack) listener).onProgress(finalTotal, length, false);
							}
						});
					}
				}
			}
		} finally {
			Util.closeQuietly(source);
		}
	}

}