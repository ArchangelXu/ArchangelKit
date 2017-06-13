package studio.archangel.toolkit3.utils.networking;

import org.json.JSONException;
import org.json.JSONObject;

import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.utils.Logger;


/**
 * Created by Administrator on 2015/10/14.
 */
public abstract class AngelNetCallBack {


	public void onStart() {
	}

	public void onSuccess(AngelNet executor, String response, AngelNet.AngelNetConfig config) {
		JSONObject jo;
		try {
			jo = new JSONObject(response);
			int status = jo.optInt(config.getReturnCodeName());
			String json = jo.optString(config.getReturnDataName());
			String error_code = jo.optString(config.getErrorCode());
			String readable_msg = jo.optString(config.getReturnMsgName());
			if (jo.length() != 0 && status == 0 && json.isEmpty() && readable_msg.isEmpty()) {
				Logger.err("response not empty but get no data. Maybe caused by wrong json key.");
			}
			Object obj = jo.opt(config.getReturnDataName());
			if (obj == JSONObject.NULL) {
				obj = null;
			}
			onSuccess(executor, status, obj, error_code, readable_msg, jo, AngelNetCallBack.this);
		} catch (JSONException e) {
			Logger.out(response);
			e.printStackTrace();
			executor.execute(new Runnable() {
				@Override
				public void run() {
					onFailure(AngelApplication.getInstance().getString(R.string.error_wrong_json_format));
				}
			});
		}

	}

	/**
	 * @param executor
	 * @param ret_code   返回值
	 * @param ret_data   返回数据
	 * @param error_code 错误码
	 * @param msg        信息
	 * @param raw        原始返回数据的JSON
	 * @param callback   此回调的引用
	 */
	public abstract void onSuccess(AngelNet executor, int ret_code, Object ret_data, String error_code, String msg, JSONObject raw, AngelNetCallBack callback);


	public abstract void onFailure(String msg);

	public void onFailure(String msg, String err_msg_detail) {
		onFailure(msg);
	}

	/**
	 * @return 如果子类覆盖了这个方法，应该返回true，否则会继续调用旧版的onFailure来作为没网的回调
	 */
	public boolean onNoNetwork() {
		return false;
	}

}
