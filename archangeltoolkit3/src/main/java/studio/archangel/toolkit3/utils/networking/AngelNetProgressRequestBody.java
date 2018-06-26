package studio.archangel.toolkit3.utils.networking;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.utils.Logger;

/**
 * Created by Administrator on 2015/10/16.
 */
public class AngelNetProgressRequestBody extends RequestBody {

	//	private final File file;
	private final Uri uri;
	private final AngelNetCallBack listener;
	private final String contentType;
	private AngelNet handler;
	long length;

	public AngelNetProgressRequestBody(Uri uri, String contentType, AngelNetCallBack listener, AngelNet angelNet) {
//		this.file = file;
		this.uri = uri;
		this.contentType = contentType;
		this.listener = listener;
		handler = angelNet;
		String scheme = uri.getScheme();
		if (scheme.equals(ContentResolver.SCHEME_FILE)) {
			File file = new File(uri.getPath());
			length = file.length();
		} else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
			Cursor cursor = AngelApplication.getInstance().getContentResolver().query(uri, null, null, null, null);
			cursor.moveToFirst();
			length = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
			cursor.close();
		}
	}

	@Override
	public long contentLength() {
		return length;
	}

	@Override
	public MediaType contentType() {
		return MediaType.parse(contentType);
	}

	@Override
	public void writeTo(BufferedSink sink) throws IOException {
		Source source = null;
		try {
			source = Okio.source(AngelApplication.getInstance().getContentResolver().openInputStream(uri));
			long total = 0;
			long read;
			Buffer buffer = sink.buffer();
			while ((read = source.read(buffer, 32 * 1024)) != -1) {
				total += read;
//				sink.flush();
//				Logger.out("uploading:" + total + "/" + contentLength());
				final long finalTotal = total;
				if (listener != null) {
					if (listener instanceof AngelNetProgressCallBack) {
						handler.execute(new Runnable() {
							@Override
							public void run() {
								((AngelNetProgressCallBack) listener).onProgress(finalTotal, contentLength(), false);
							}
						});
					}
				}
			}
			sink.flush();
		} finally {
			Util.closeQuietly(source);
		}
	}
}