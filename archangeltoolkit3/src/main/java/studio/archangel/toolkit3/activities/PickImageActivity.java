package studio.archangel.toolkit3.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    public static final String USE_ORIGIN_FILE_FOR_CROP = "use_origin_file_for_crop";

    public static final String MAIN_COLOR = "color_main";
    public static final String TOOL_BAR_COLOR = "color_tool_bar";
    public static final String WIDGET_BAR_COLOR = "color_widget_bar";
    public static final String WIDGET_UNSELECTED_COLOR = "color_widget_unselected";
    public static final String WIDGET_SELECTED_COLOR = "color_widget_selected";

    public static final String IMAGE_TYPE_FILTERS = "image_type_filters";
    public static final String CROP_TITLE = "crop_title";

    public static final int ERROR_ILLEGAL_ARGUMENT = 1;

    public static final int PICK_MODE_CAMERA = 20001;
    public static final int PICK_MODE_GALLERY = 20002;
    //	public static final int MODE_SELECT_MULTIPLE_FROM_GALLERY = 20003;
    public static final int CROP_MODE_NONE = 30001;
    public static final int CROP_MODE_SQUARE = 30002;
    public static final int CROP_MODE_SPECIFIC = 30003;
    public static final int CROP_MODE_FREE = 30004;
    public static final int CROP_MODE_OVAL = 30005;

    File photo;
    String cropped_file;
    float crop_ratio;
    int color_main = -1;
    int color_tool_bar = -1;
    int color_widget_bar = -1;
    int color_widget_unselected = -1;
    int color_widget_selected = -1;
    int crop_mode = 0;
    String crop_title = null;
    boolean use_origin_file_for_crop = false;

    String[] image_type_filters = new String[]{"*"};

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
            color_tool_bar = extras.getInt(TOOL_BAR_COLOR, color_main);
            color_widget_bar = extras.getInt(WIDGET_BAR_COLOR, getResources().getColor(R.color.white));
            color_widget_unselected = extras.getInt(WIDGET_UNSELECTED_COLOR, getResources().getColor(R.color.black));
            color_widget_selected = extras.getInt(WIDGET_SELECTED_COLOR, color_main);
            use_origin_file_for_crop = extras.getBoolean(USE_ORIGIN_FILE_FOR_CROP, false);
            crop_title = extras.getString(CROP_TITLE, null);
            String[] strings = extras.getStringArray(IMAGE_TYPE_FILTERS);
            if (strings != null) {
                image_type_filters = strings;
            }
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
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
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
//			intent.putExtra(Intent.EXTRA_MIME_TYPES, image_type_filters);
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
        String s = path;
        if (s.contains(".") && !s.endsWith(".")) {
            s = s.substring(s.lastIndexOf(".") + 1, s.length());

        }
        File f = new File(getCacheDir(), "temp_" + System.currentTimeMillis() + "." + s);
        cropped_file = f.getAbsolutePath();
        if (cropped_file.startsWith("content://")) {
//			cropped_file = getRealPathFromURI(getSelf(), Uri.parse(cropped_file));
            cropped_file = CommonUtil.getPath(getSelf(), Uri.parse(cropped_file));
        }

        UCrop.Options options = new UCrop.Options();
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
            case CROP_MODE_OVAL: {
                crop.withAspectRatio(1, 1);
                options.setCircleDimmedLayer(true);
                break;
            }
        }
        options.setActiveWidgetColor(color_widget_selected);
        options.setStatusBarColor(color_tool_bar);
        options.setToolbarWidgetColor(color_widget_bar);
        options.setToolbarColor(color_tool_bar);
        if (crop_title != null) {
            options.setToolbarTitle(crop_title);
        }
        if (s.equalsIgnoreCase("png")) {
            options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        } else {
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        }
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
        FileInfo info;
        if (use_origin_file_for_crop) {
            info = new FileInfo();
            info.path = CommonUtil.getPath(getSelf(), uri);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(info.path, opts);
            info.width = opts.outWidth;
            info.height = opts.outHeight;
        } else {
            info = ImageProvider.getSmallPic(uri);
        }
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
            if (uri != null && image_type_filters != null) {
                String s = CommonUtil.getPath(getSelf(), uri);
                if (s.contains(".") && !s.endsWith(".")) {
                    s = s.substring(s.lastIndexOf(".") + 1, s.length());
                    boolean found = false;
                    for (String filter : image_type_filters) {
                        if (filter.equalsIgnoreCase("*")) {
                            found = true;
                            break;
                        }
                        if (filter.equalsIgnoreCase(s)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Intent result = new Intent();
                        result.putExtra("error_code", ERROR_ILLEGAL_ARGUMENT);
                        result.putExtra("selected_format", s);
                        setResult(AngelApplication.result_fail, result);
                        finish();
                        return;
                    }
                }
            }
            handlePickedImage(uri);
        } else if (requestCode == PICK_MODE_CAMERA && resultCode == AngelApplication.result_ok) {
            Uri uri = Uri.fromFile(photo);
            handlePickedImage(uri);
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
                Bitmap bitmap;
                if (crop_mode == CROP_MODE_OVAL) {
                    bitmap = BitmapFactory.decodeFile(new File(resultUri.getPath()).getAbsolutePath(), options).copy(Bitmap.Config.ARGB_8888, true);
                    if (bitmap != null) {
                        Canvas canvas = new Canvas(bitmap);
                        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
                        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                        p.setStyle(Paint.Style.FILL);
                        canvas.drawOval(new RectF(0, 0, options.outWidth, options.outHeight), p);

                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(resultUri.getPath());
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new ByteArrayOutputStream());
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (out != null) {
                                    out.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            bitmap.recycle();
                            bitmap = null;
                        }
                    }
                } else {
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(new File(resultUri.getPath()).getAbsolutePath(), options);
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
