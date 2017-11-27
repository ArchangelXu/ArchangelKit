package studio.archangel.toolkit3.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;

import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.models.FileInfo;
import studio.archangel.toolkit3.utils.CommonUtil;
import studio.archangel.toolkit3.utils.Logger;
import studio.archangel.toolkit3.utils.image.ImageProvider;
import studio.archangel.toolkit3.utils.ui.Notifier;


//import com.soundcloud.android.crop.Crop;

/**
 * Created by Michael on 2015/4/28.
 */
public class PickImageActivity extends AngelActivity {
	public static final String PICK_MODE = "pick_mode";
	public static final String CROP_MODE = "crop_mode";
	public static final String CROP_RATIO = "crop_ratio";
	public static final String MAIN_COLOR = "color_main";

	public static final int PICK_MODE_CAMERA = 20001;
	public static final int PICK_MODE_GALLERY = 20002;
	//	public static final int MODE_SELECT_MULTIPLE_FROM_GALLERY = 20003;
	public static final int CROP_MODE_NONE = 30001;
	public static final int CROP_MODE_SQUARE = 30002;
	public static final int CROP_MODE_SPECIFIC = 30003;
	public static final int CROP_MODE_FREE = 30004;

	File photo;
	String cropped_file;
	float crop_ratio;
	int color_main = -1;
	int crop_mode = 0;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("photo", photo.getAbsolutePath());
		outState.putBoolean("wait_for_result", true);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent it = getIntent();
		Bundle extras = it.getExtras();
		int pick_mode_code;
		if (extras == null) {
			Notifier.showNormalMsg(getSelf(), "数据错误");
			finish();
			return;
		} else {
			pick_mode_code = extras.getInt(PICK_MODE, PICK_MODE_CAMERA);
			color_main = extras.getInt(MAIN_COLOR, getResources().getColor(R.color.blue));
		}
		crop_mode = extras.getInt(CROP_MODE, CROP_MODE_SQUARE);
		if (crop_mode == CROP_MODE_SPECIFIC) {
			crop_ratio = extras.getFloat(CROP_RATIO, -1);
			if (crop_ratio == -1) {
				Notifier.showNormalMsg(getSelf(), "数据错误");
				finish();
				return;
			}
		}
		boolean wait_for_result = false;
		photo = new File(getExternalCacheDir() + "/temp_image_" + System.currentTimeMillis() + ".jpg");
		if (savedInstanceState != null) {
			wait_for_result = savedInstanceState.getBoolean("wait_for_result", false);
			String last_path = savedInstanceState.getString("photo", null);
			if (last_path != null) {
				photo = new File(last_path);
			}
		}
		if (wait_for_result) {
			return;
		}
		if (pick_mode_code == PICK_MODE_CAMERA) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			try {
				if (!isExtStorageReadable()) {
					Notifier.showNormalMsg(getSelf(), "外部存储不可用");
					finish();
					return;
				}
				if (!isExtStorageWritable()) {
					Notifier.showNormalMsg(getSelf(), "外部存储不可用");
					finish();
					return;
				}
				Uri uri = null;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//					uri = FileProvider.getUriForFile(getSelf(), "studio.archangel.toolkit3.AngelFileProvider", photo);
					uri = CommonUtil.getUriForFile(photo);
					intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				} else {
					uri = Uri.fromFile(photo);
				}
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				try {
					startActivityForResult(intent, PICK_MODE_CAMERA);
				} catch (Exception e) {
					e.printStackTrace();
					Notifier.showLongMsg(getSelf(), "调用相机失败");
					finish();
				}
			} catch (Exception e) {
				Notifier.showLongMsg(getSelf(), "存储文件失败");
				Logger.out("Cannot create file. Please check storage system.");
				e.printStackTrace();
			}
		} else if (pick_mode_code == PICK_MODE_GALLERY) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			try {
				startActivityForResult(intent, PICK_MODE_GALLERY);
			} catch (Exception e) {
				e.printStackTrace();
				Notifier.showLongMsg(getSelf(), "调用图库失败");
				finish();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	void cropPicture(String path) {
		File f = new File(getCacheDir(), "temp_" + System.currentTimeMillis() + ".jpg");
		cropped_file = f.getAbsolutePath();
		if (cropped_file.startsWith("content://")) {
//			cropped_file = getRealPathFromURI(getSelf(), Uri.parse(cropped_file));
			cropped_file = CommonUtil.getPath(getSelf(), Uri.parse(cropped_file));
		}

		UCrop crop = UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(f));
		switch (crop_mode) {
			case CROP_MODE_SQUARE: {
				crop = crop.withAspectRatio(1, 1);
				break;
			}
			case CROP_MODE_SPECIFIC: {
				crop = crop.withAspectRatio(crop_ratio, 1);
				break;
			}
			case CROP_MODE_FREE: {
				//do nothing
				break;
			}
		}
		UCrop.Options options = new UCrop.Options();
		options.setActiveWidgetColor(color_main);
		options.setStatusBarColor(color_main);
		options.setToolbarWidgetColor(getResources().getColor(R.color.white));
		options.setToolbarColor(color_main);
		crop.withOptions(options).start(this);
	}

	public static String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = {MediaStore.Images.Media.DATA};
			cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
			if (cursor == null) {
				return null;
			}
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	void handlePickedImage(Uri uri) {
		Logger.out(uri);
		FileInfo info = ImageProvider.getSmallPic(uri);
		if (info == null) {
			setResult(AngelApplication.result_fail);
			finish();
			return;
		}
		Logger.out(info.path);
		if (crop_mode != CROP_MODE_NONE) {
			cropPicture(info.path);
		} else {
			Intent it = new Intent();
			it.putExtra("file", info.path);
			it.putExtra("width", info.width);
			it.putExtra("height", info.height);
			setResult(AngelApplication.result_ok, it);
			finish();
		}
	}

	//檢查外部儲存體是否可以進行寫入
	public boolean isExtStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	//檢查外部儲存體是否可以進行讀取
	public boolean isExtStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	ArrayList<FileInfo> handlePickedImages(JSONArray ja) {
		Notifier.showNormalMsg(getSelf(), "处理中...");
		ArrayList<FileInfo> list = new ArrayList<>();
		for (int i = 0; i < ja.length(); i++) {
//            Uri uri = Uri.fromFile(new File(ja.optString(i)));
			Uri uri = Uri.parse(ja.optString(i));
			Logger.out(uri);
			FileInfo info = ImageProvider.getSmallPic(uri);
			Logger.out(info != null ? info.path : null);
			list.add(info);
		}
		return list;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Logger.out("PickImageActivity onActivityResult:requestCode=" + requestCode + " resultCode=" + resultCode + " data=" + data);
		if (requestCode == PICK_MODE_GALLERY && resultCode == AngelApplication.result_ok) {
			Uri uri = data.getData();
			handlePickedImage(uri);
		} else if (requestCode == PICK_MODE_CAMERA && resultCode == AngelApplication.result_ok) {
			Uri uri = Uri.fromFile(photo);
			handlePickedImage(uri);
//		} else if (requestCode == MODE_SELECT_MULTIPLE_FROM_GALLERY && resultCode == AngelApplication.result_ok) {
//			if (data == null) {
//				finish();
//				return;
//			}
//			Bundle extras = data.getExtras();
//			if (extras == null) {
//				finish();
//				return;
//			}
//			String image_data = extras.getString("list", null);
//			if (image_data == null) {
//				finish();
//				return;
//			}
//			try {
//				JSONArray ja = new JSONArray(image_data);
//				ArrayList<FileInfo> list = handlePickedImages(ja);
//				ja = new JSONArray();
//				for (FileInfo info : list) {
//					ja.put(info.toJson());
//				}
//				Intent it = new Intent();
//				it.putExtra("list", ja.toString());
//				setResult(AngelApplication.result_ok, it);
//				finish();
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}

		} else if (requestCode == UCrop.REQUEST_CROP) {
			if (resultCode == RESULT_OK) {
				Uri resultUri = UCrop.getOutput(data);
				if (resultUri == null) {
					Notifier.showNormalMsg(getSelf(), "裁剪图片时发生了错误");
					finish();
					return;
				}
				Intent it = new Intent();
				it.putExtra("file", resultUri.toString());
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				Bitmap bitmap = BitmapFactory.decodeFile(new File(resultUri.getPath()).getAbsolutePath(), options);
				if (bitmap != null) {
					bitmap.recycle();
					bitmap = null;
				}
				it.putExtra("width", options.outWidth);
				it.putExtra("height", options.outHeight);
				setResult(AngelApplication.result_ok, it);
				finish();
			} else if (resultCode == UCrop.RESULT_ERROR) {
				Throwable error = UCrop.getError(data);
				if (error != null) {
					error.printStackTrace();
				}
				Notifier.showNormalMsg(getSelf(), "裁剪图片时发生了错误");
				finish();
			} else if (resultCode == RESULT_CANCELED) {
				setResult(AngelApplication.result_cancel);
				finish();
			}
		} else {
			finish();
		}
	}

}
