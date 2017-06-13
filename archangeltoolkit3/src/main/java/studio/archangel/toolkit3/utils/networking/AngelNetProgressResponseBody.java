package studio.archangel.toolkit3.utils.networking;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by xmk on 16/6/5.
 */
public class AngelNetProgressResponseBody extends ResponseBody {
	long downloaded_size = 0;
	private final ResponseBody responseBody;
	private BufferedSource bufferedSource;

	private final AngelNetDownloadCallBack listener;
	private AngelNet handler;

	public AngelNetProgressResponseBody(ResponseBody responseBody, AngelNetDownloadCallBack listener, AngelNet angelNet) {
		this.responseBody = responseBody;
		this.listener = listener;
		handler = angelNet;
	}

	public AngelNetProgressResponseBody(ResponseBody body, long downloaded_size, AngelNetDownloadCallBack callBack, AngelNet angelNet) {
		this(body, callBack, angelNet);
		this.downloaded_size = downloaded_size;
	}

	@Override
	public MediaType contentType() {
		return responseBody.contentType();
	}

	@Override
	public long contentLength() {
		return responseBody.contentLength();
	}

	@Override
	public BufferedSource source() {
		if (bufferedSource == null) {
			bufferedSource = Okio.buffer(source(responseBody.source()));
		}
		return bufferedSource;
	}

	private Source source(Source source) {
		return new ForwardingSource(source) {
			long totalBytesRead = 0L;

			@Override
			public long read(Buffer sink, long byteCount) throws IOException {
				long bytesRead = 0;
				try {
					bytesRead = super.read(sink, byteCount);
				} catch (final Exception e) {
					close();
					throw e;
				}
				totalBytesRead += bytesRead != -1 ? bytesRead : 0;
				if (listener != null) {
					final long finalBytesRead = bytesRead;
					handler.execute(new Runnable() {
						@Override
						public void run() {
							listener.onProgress(downloaded_size + totalBytesRead, finalBytesRead, downloaded_size + responseBody.contentLength(), listener.call, listener);
						}
					});
				}
				return bytesRead;
			}
		};
	}


}