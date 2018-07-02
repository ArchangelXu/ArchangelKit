package studio.archangel.toolkit3.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.webkit.MimeTypeMap;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import studio.archangel.toolkit3.AngelApplication;

/**
 * Created by xmk on 16/5/15.
 */
public class CommonUtil {
	public static boolean isEmptyString(CharSequence cs) {
		return TextUtils.isEmpty(cs) || cs.toString().equalsIgnoreCase("null");
	}

	/**
	 * 计算两点之间距离
	 *
	 * @param a 点A
	 * @param b 点B
	 * @return AB间距离
	 */
	public static float getDistance(Point a, Point b) {
		return (float) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

	public static float getDistance(PointF a, PointF b) {
		return (float) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

	public static String getShortName(String name) {
		if (name == null) {
			return "";
		}
		String short_name = name.trim();
		if (short_name.isEmpty()) {
			short_name = "？";
		} else {
			Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]+");
			Matcher m = p.matcher(name);
			if (m.find()) {
				short_name = short_name.substring(m.start(), m.end());
				if (short_name.length() != 0) {
					short_name = short_name.substring(short_name.length() - 1, short_name.length());
				} else {
					short_name = "？";
				}
			} else {
				p = Pattern.compile("[a-zA-Z]+");
				m = p.matcher(name);
				if (m.find()) {
					short_name = short_name.substring(m.start(), m.end());
					short_name = short_name.substring(0, 1).toUpperCase();
				} else {
					short_name = "？";
				}
			}
		}
		return short_name;
	}

	/**
	 * Get a file path from a Uri. This will GET the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri     The Uri to query.
	 * @author paulburke
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[]{
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context       The context.
	 * @param uri           The Uri to query.
	 * @param selection     (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
	                                   String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} catch (Exception e) {
			Logger.err(e);
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	public static boolean hasNavigationBar(Activity act) {
		boolean hasSoftwareKeys = true;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Display d = act.getWindowManager().getDefaultDisplay();

			DisplayMetrics realDisplayMetrics = new DisplayMetrics();
			d.getRealMetrics(realDisplayMetrics);

			int realHeight = realDisplayMetrics.heightPixels;
			int realWidth = realDisplayMetrics.widthPixels;

			DisplayMetrics displayMetrics = new DisplayMetrics();
			d.getMetrics(displayMetrics);

			int displayHeight = displayMetrics.heightPixels;
			int displayWidth = displayMetrics.widthPixels;

			hasSoftwareKeys = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
		} else {
			boolean hasMenuKey = ViewConfiguration.get(act).hasPermanentMenuKey();
			boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
			hasSoftwareKeys = !hasMenuKey && !hasBackKey;
		}
		return hasSoftwareKeys;
	}

	public static int getStringWidth(String s, float text_size) {
		Paint p = new Paint();
		p.setTextSize(text_size);
		Rect rect = new Rect();
		p.getTextBounds(s, 0, s.length(), rect);
		return rect.width();
	}

	public static int getStringHeight(String s, float text_size) {
		Paint p = new Paint();
		p.setTextSize(text_size);
		Rect rect = new Rect();
		p.getTextBounds(s, 0, s.length(), rect);
		return rect.height();
	}

	public static boolean isFileExistsInAsset(Context context, String path) {
		if (path.startsWith("asset")) {
			path = path.substring(path.indexOf("/") + 1);
		}
		AssetManager am = context.getAssets();
		try {
			InputStream is = am.open(path);
			is.close();
			return true;
		} catch (IOException e) {
			Logger.err(e);//e.printStackTrace();
		}
		return false;
	}


	public enum NetWorkStatus {
		MOBILE, WIFI, VPN, NONE
	}

	public static boolean isNetWorkAvailable() {
		return getNetWorkStatus() != NetWorkStatus.NONE;
	}

	public static NetWorkStatus getNetWorkStatus() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			ConnectivityManager cm = (ConnectivityManager) AngelApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
			Network[] networks = cm.getAllNetworks();
			NetWorkStatus result = NetWorkStatus.NONE;
			for (Network network : networks) {
				NetworkInfo info = cm.getNetworkInfo(network);
				if (info != null && info.isAvailable()) { // connected to the internet
					if (info.getType() == ConnectivityManager.TYPE_WIFI) {
						result = NetWorkStatus.WIFI;
					} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
						result = NetWorkStatus.MOBILE;
//					} else if (info.getType() == ConnectivityManager.TYPE_VPN) {
//						return NetWorkStatus.VPN;
					}
				}
			}
			return result;
		} else {
			ConnectivityManager conMan = (ConnectivityManager) AngelApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (wifi != null && wifi.isConnected()) {
				return NetWorkStatus.WIFI;
			} else if (mobile != null && mobile.isConnected()) {
				return NetWorkStatus.MOBILE;
			} else {
				return NetWorkStatus.NONE;
			}
		}
	}

	/**
	 * 获取字符串长度，全角算两个
	 *
	 * @param value 目标字符串
	 * @return
	 */
	public static int getRealLength(String value) {
		int valueLength = 0;
//		String chinese = "[\u4e00-\u9fa5]";
//		for (int i = 0; i < value.length(); i++) {
//			String temp = value.substring(i, i + 1);
//			if (temp.matches(chinese)) {
//				valueLength += 2;
//			} else {
//				valueLength += 1;
//			}
//		}
		for (int i = 0; i < value.length(); i++) {
			valueLength += isHalfWidth(value.charAt(i)) ? 1 : 2;
		}
		return valueLength;
	}

	static boolean isHalfWidth(char c) {
		return '\u0000' <= c && c <= '\u00FF'
				|| '\uFF61' <= c && c <= '\uFFDC'
				|| '\uFFE8' <= c && c <= '\uFFEE';
	}

	/**
	 * 从头截取字符串到目标长度
	 *
	 * @param value 目标字符串
	 * @param limit 目标长度，中文算两个
	 * @return
	 */
	public static String getCutString(String value, int limit) {
		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese)) {
				valueLength += 2;
			} else {
				valueLength += 1;
			}
			if (valueLength > limit) {
				break;
			}
			sb = sb.append(temp);

		}
		return sb.toString();
	}

	/**
	 * A copy of the Android internals  insertImage method, this method populates the
	 * meta data with DATE_ADDED and DATE_TAKEN. This fixes a common problem where media
	 * that is inserted manually gets saved at the end of the gallery (because date is not populated).
	 *
	 * @see android.provider.MediaStore.Images.Media#insertImage(ContentResolver, Bitmap, String, String)
	 */
	public static final String insertImage(ContentResolver cr,
	                                       Bitmap source,
	                                       String title,
	                                       String description) {

		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, title);
		values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
		values.put(MediaStore.Images.Media.DESCRIPTION, description);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		// Add the date meta data to ensure the image is added at the front of the gallery
		values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
		values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

		Uri url = null;
		String stringUrl = null;    /* value to be returned */

		try {
			url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

			if (source != null) {
				OutputStream imageOut = cr.openOutputStream(url);
				try {
					source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
				} finally {
					imageOut.close();
				}

				long id = ContentUris.parseId(url);
				// Wait until MINI_KIND thumbnail is generated.
				Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
				// This is for backward compatibility.
				storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
			} else {
				cr.delete(url, null, null);
				url = null;
			}
		} catch (Exception e) {
			if (url != null) {
				cr.delete(url, null, null);
				url = null;
			}
		}

		if (url != null) {
			stringUrl = url.toString();
		}

		return stringUrl;
	}

	/**
	 * A copy of the Android internals StoreThumbnail method, it used with the insertImage to
	 * populate the android.provider.MediaStore.Images.Media#insertImage with all the correct
	 * meta data. The StoreThumbnail method is private so it must be duplicated here.
	 *
	 * @see android.provider.MediaStore.Images.Media (StoreThumbnail private method)
	 */
	private static final Bitmap storeThumbnail(
			ContentResolver cr,
			Bitmap source,
			long id,
			float width,
			float height,
			int kind) {

		// create the matrix to scale it
		Matrix matrix = new Matrix();

		float scaleX = width / source.getWidth();
		float scaleY = height / source.getHeight();

		matrix.setScale(scaleX, scaleY);

		Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
				source.getWidth(),
				source.getHeight(), matrix,
				true
		);

		ContentValues values = new ContentValues(4);
		values.put(MediaStore.Images.Thumbnails.KIND, kind);
		values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
		values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
		values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());

		Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

		try {
			OutputStream thumbOut = cr.openOutputStream(url);
			thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
			thumbOut.close();
			return thumb;
		} catch (FileNotFoundException ex) {
			return null;
		} catch (IOException ex) {
			return null;
		}
	}

	public static JSONObject getIntentJSON(Intent it) {
		if (it == null) {
			try {
				Logger.outUpper("intent is null");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		Bundle bundle = it.getExtras();
		if (bundle == null) {
			try {
				Logger.outUpper("bundle is null");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		JSONObject jo = new JSONObject();
		for (String key : bundle.keySet()) {
			Object value = bundle.get(key);
			if (value != null) {
				try {
					jo.put(key, value);
//					Logger.outUpper(String.format("%s=>%s (%s)", key, value.toString(), value.getClass().getName()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return jo;
	}

//	public static Uri getUriForFile(File file) {
////		return PublicFileProvider.getUriForFile(AngelApplication.getInstance(), AngelApplication.getInstance().getPackageName() + ".publicfileprovider", file);
//		return AngelFileProvider.getUriForFile(AngelApplication.getInstance(), AngelApplication.getInstance().getPackageName() + ".fileprovider", file);
//	}

	public static boolean hasFrontCamera() {
		Camera.CameraInfo ci = new Camera.CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			Camera.getCameraInfo(i, ci);
			if (ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				return true;
			}
		}
		return false;
	}

	public static String moveFile(String inputFile, String outputPath) {
		File file = new File(inputFile);
		String name = file.getName();
		if (copyFile(inputFile, outputPath)) {
			deleteFile(inputFile);
		}
		return outputPath + "/" + name;
	}

	public static void deleteFile(String path) {
//		try {
//			new File(path).delete();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		File file = new File(path);
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i].getAbsolutePath());
				}
			}
			file.delete();
		} else {
			Logger.errSimple("delete file no exists " + file.getAbsolutePath());
		}
	}

	public static boolean copyFile(String inputFile, String outputPath) {

		InputStream in = null;
		OutputStream out = null;
		try {

			//create output directory if it doesn't exist
			File dir = new File(outputPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}


			in = new FileInputStream(inputFile);
			out = new FileOutputStream(outputPath + new File(inputFile).getName());

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;

			// write the output file (You have now copied the file)
			out.flush();
			out.close();
			out = null;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getMimeType(Uri uri) {
		String mimeType = null;
		if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
			ContentResolver cr = AngelApplication.getInstance().getContentResolver();
			mimeType = cr.getType(uri);
		} else {
			String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
					.toString());
			mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
					fileExtension.toLowerCase());
		}
		if (mimeType == null) {
			mimeType = "application/octet-stream";
		}
		return mimeType;
	}

	public static boolean isVideoFile(Context context, String file_path) {
		boolean isVideo = false;
		try {
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			retriever.setDataSource(context, Uri.parse(file_path));

			String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
			isVideo = "yes".equals(hasVideo);
		} catch (Exception e) {
			Logger.err(e);//e.printStackTrace();
		}
		return isVideo;
	}

	public static boolean isEmulator() {
		return Build.FINGERPRINT.startsWith("generic")
				|| Build.FINGERPRINT.startsWith("unknown")
				|| Build.MODEL.contains("google_sdk")
				|| Build.MODEL.contains("Emulator")
				|| Build.MODEL.contains("Android SDK built for x86")
				|| Build.MANUFACTURER.contains("Genymotion")
				|| (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
				|| "google_sdk".equals(Build.PRODUCT);
	}
}
