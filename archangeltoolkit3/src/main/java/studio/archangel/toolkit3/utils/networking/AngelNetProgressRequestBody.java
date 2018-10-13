package studio.archangel.toolkit3.utils.networking;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by Administrator on 2015/10/16.
 */
public class AngelNetProgressRequestBody extends RequestBody {

	private RequestBody requestBody;
	private BufferedSink bufferedSink;
	private final AngelNetCallBack listener;

	public AngelNetProgressRequestBody(RequestBody requestBody, AngelNetCallBack listener) {
		this.requestBody = requestBody;
		this.listener = listener;
	}

	@Override
	public long contentLength() throws IOException {
		return requestBody.contentLength();
	}

	@Override
	public MediaType contentType() {
		return requestBody.contentType();
	}

	@Override
	public void writeTo(BufferedSink sink) throws IOException {
		bufferedSink = Okio.buffer(sink(sink));
		requestBody.writeTo(bufferedSink);
		bufferedSink.flush();
	}

	private Sink sink(Sink delegate) {
		return new ForwardingSink(delegate) {

			private long uploadedSize;

			@Override
			public void write(Buffer source, long byteCount) throws IOException {
				super.write(source, byteCount);
				uploadedSize += byteCount;
				((AngelNetProgressCallBack) listener).onProgress(uploadedSize, contentLength(), false);
			}
		};
	}
}