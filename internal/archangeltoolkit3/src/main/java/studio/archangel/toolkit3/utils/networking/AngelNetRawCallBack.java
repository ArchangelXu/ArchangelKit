package studio.archangel.toolkit3.utils.networking;

import org.json.JSONObject;

/**
 * Created by xmk on 16/9/12.
 */

public abstract class AngelNetRawCallBack extends AngelNetCallBack {
	public void onStart() {
	}

	@Override
	public void onSuccess(AngelNet executor, String response, AngelNet.AngelNetConfig config) {
		onSuccess(executor, response);
	}

	public abstract void onSuccess(AngelNet executor, String response);

	@Override
	public void onSuccess(AngelNet executor, int ret_code, Object ret_data, String error_code, String msg, JSONObject raw, AngelNetCallBack callback) {

	}

}
