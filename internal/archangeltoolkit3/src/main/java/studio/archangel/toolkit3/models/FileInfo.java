package studio.archangel.toolkit3.models;

import org.json.JSONException;
import org.json.JSONObject;

import studio.archangel.toolkit3.interfaces.JSONizable;


/**
 * Created by Michael on 2015/8/21.
 */
public class FileInfo implements JSONizable {
	public String path;
	public int width, height;

	public FileInfo(JSONObject jo) {
		if (jo == null) {
			return;
		}
		path = jo.optString("path");
		width = jo.optInt("width");
		height = jo.optInt("height");
	}

	public FileInfo() {

	}

	@Override
	public JSONObject toJson() {
		JSONObject jo = new JSONObject();
		try {
			jo.put("path", path);
			jo.put("width", width);
			jo.put("height", height);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jo;
	}
}
