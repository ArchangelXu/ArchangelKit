package studio.archangel.toolkit3.utils.networking;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import studio.archangel.toolkit3.utils.Logger;


/**
 * Created by Administrator on 2015/10/15.
 */
public class AngelNetLogger {
	public void log(String url, AngelNet.Method method, HashMap<String, Object> parameters, HashMap<String, String> headers) {
		if (url.contains(":1920/api/app/")) {
			return;
		}
		JSONObject jo = new JSONObject();
		try {
			jo.put("url", url);
			jo.put("method", method.name());
			JSONObject sub = new JSONObject();
			if (parameters != null) {
				for (Map.Entry<String, Object> en : parameters.entrySet()) {
					Object value = en.getValue();
					if (value instanceof File) {
						sub.put(en.getKey(), AngelNet.FILE_PREFIX + ((File) value).getName());
					} else if (value instanceof Uri) {
						sub.put(en.getKey(), AngelNet.URI_PREFIX + value.toString());
					} else if (value instanceof List) {
						sub.put(en.getKey(), AngelNet.LIST_PREFIX + value.toString());
					} else {
						sub.put(en.getKey(), value);
					}
				}
			}
			jo.put("parameters", sub);
			sub = new JSONObject();
			if (headers != null) {
				for (Map.Entry<String, String> en : headers.entrySet()) {
					sub.put(en.getKey(), en.getValue());
				}
			}
			jo.put("headers", sub);
			Logger.json(jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
