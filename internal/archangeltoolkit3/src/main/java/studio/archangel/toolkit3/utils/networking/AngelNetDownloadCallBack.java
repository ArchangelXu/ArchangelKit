package studio.archangel.toolkit3.utils.networking;

import java.io.File;

import okhttp3.Call;


/**
 * Created by Administrator on 2015/10/14.
 */
public abstract class AngelNetDownloadCallBack {
	Call call;

	public void onStart() {
	}

	public void onPrepared(Call call) {
		this.call = call;
	}

	public abstract void onProgress(long last_downloaded_length, long current_downloaded_length, long total_length, Call call, AngelNetDownloadCallBack callBack);

	public void onEtagChange(String etag) {

	}

	public abstract void onSuccess(File file);

	public abstract void onCancel();

	public abstract void onFailure(String msg);

	public void onFailure(String msg, String err_msg_detail) {
		onFailure(msg);
	}
}
