/**
 *
 */
package studio.archangel.toolkit3.activities;


import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.badoo.mobile.util.WeakHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.interfaces.OnPermissionCheckListener;
import studio.archangel.toolkit3.utils.Logger;
import studio.archangel.toolkit3.views.AngelActionBar;
import studio.archangel.toolkit3.views.dialogs.AngelLoadingDialog;


/**
 * @author Administrator
 */
public abstract class AngelActivity extends AppCompatActivity {
	public AngelLoadingDialog dialog;
	public boolean destroyed = false;
	//    Validator validator;
	private boolean orientation_set = false;
	//	private boolean is_immersive = false;
	protected AngelActionBar bar;
	public WeakHandler handler;
	public PermissionHelper permission_helper;
	//    public boolean track_app_status_change = true;
	/**
	 * this activity's hashCode
	 */
	protected int hash = -1;

	/**
	 * 加载指定的Feature
	 *
	 * @param savedInstanceState
	 * @param feature_id
	 */
	protected void onCreate(Bundle savedInstanceState, int[] feature_id, int orientation) {
		super.onCreate(savedInstanceState);
		reloadLanguageIfNeeded();
		init(feature_id, orientation);
	}

	protected void onCreate(Bundle savedInstanceState, int[] feature_id) {
		super.onCreate(savedInstanceState);
		reloadLanguageIfNeeded();
		init(feature_id, -1);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		reloadLanguageIfNeeded();
		init(null, -1);
	}
//	@Override
//	protected void attachBaseContext(Context newBase) {
//		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//	}

	protected void reloadLanguageIfNeeded() {
		Resources resources = getResources();
		Configuration config = resources.getConfiguration();

		boolean should_reload_language = !config.locale.getLanguage().equalsIgnoreCase(AngelApplication.getLanguage());
		Logger.out("should_reload_language=" + should_reload_language + " onRestoreInstanceState page=" + getClass().getSimpleName());
		if (should_reload_language) {
			config.locale = new Locale(AngelApplication.getLanguage(), AngelApplication.getCountryCode());
			resources.updateConfiguration(config, resources.getDisplayMetrics());
		}
	}

	protected AngelActivity getSelf() {
		return this;
	}

	public AngelActionBar getAngelActionBar() {
		return bar;
	}

	public void setAngelActionBar(AngelActionBar bar) {
		this.bar = bar;
	}

	@Override
	protected void onResume() {
		super.onResume();
		reloadLanguageIfNeeded();
		Application app = getApplication();
		if (app != null && app instanceof AngelApplication) {
			AngelApplication aa = (AngelApplication) app;
			if (aa.was_in_background) {
				Logger.out("App was brought to foreground through " + getSelf().getClass().getSimpleName());
				onTakenToForeground(aa.activity_taken_to_back_time_stamp);
				AngelApplication.getInstance().onAppBroughtToFront(this);
			}
			aa.stopActivityTransitionTimer();
		}
	}


	public void onTakenToBackground(long time) {

	}

	public void onTakenToForeground(long time) {

	}

	public int getActivityTransitionTime() {
		return -1;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Application app = getApplication();
		if (app != null && app instanceof AngelApplication) {
			AngelApplication aa = (AngelApplication) app;
			aa.startActivityTransitionTimer(new WeakReference<>(getSelf()));
		}
	}

	protected void setOrientation(int info) {
		if (!shouldOverrideOrientation()) {
			return;
		}
		if (info == -1) {
			return;
		}
		try {
			setRequestedOrientation(info);
			orientation_set = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		destroyed = true;
		if (dialog != null) {
			dialog.forceDismiss();
		}

		super.onDestroy();
	}

	/**
	 * 解决 Unable to add window -- token null is not valid; is your activity running?
	 */
	@Override
	public boolean isDestroyed() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return super.isDestroyed();
		} else {
			return destroyed;// 在onDestroy中设置true
		}
	}

	protected void hideStatusBar() {
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setAttributes(attrs);
	}

	protected void showStatusBar() {
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setAttributes(attrs);
	}

	protected boolean shouldOverrideOrientation() {
		return false;
	}

	/**
	 * 初始化。将Actionbar的图标设置为App图标，并将点击图标映射为返回键
	 *
	 * @param feature_id 要加载的Feature的id
	 */
	void init(int[] feature_id, int orientation) {
		setOrientation(orientation);
		if (!orientation_set) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		if (feature_id != null) {
			for (int id : feature_id) {
				requestWindowFeature(id);
				if (id == Window.FEATURE_NO_TITLE) {
					getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
			}
		}
		hash = hashCode();
		handler = new WeakHandler();
		permission_helper = new PermissionHelper();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		for (OnPermissionCheckListener listener : permission_helper.listeners) {
			if (listener.request_code == requestCode) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						listener.onRetry();
					}
				}, 500);
				break;
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		permission_helper.handlePermissionResult(requestCode, permissions, grantResults);
	}

	public void checkPermissions(String[] permission_strings, @NonNull OnPermissionCheckListener listener) {
		permission_helper.checkPermissions(permission_strings, listener);
	}

	public class PermissionHelper {
		int request_code = 40000;
		ArrayList<OnPermissionCheckListener> listeners;

		public PermissionHelper() {
			listeners = new ArrayList<>();
		}

		public void addListener(OnPermissionCheckListener listener) {
			if (listeners.contains(listener)) {
				return;
			}
			request_code++;
			listener.request_code = request_code;
			listeners.add(listener);
		}

		public void checkPermissions(String[] permission_strings, @NonNull OnPermissionCheckListener listener) {

			boolean all_granted = true;
			listener.permission_strings = permission_strings;
//			boolean should_show_explaination = false;
			for (String permission_string : permission_strings) {
				if (ContextCompat.checkSelfPermission(AngelActivity.this, permission_string) != PackageManager.PERMISSION_GRANTED) {
					all_granted = false;
//					if (ActivityCompat.shouldShowRequestPermissionRationale(AngelActivity.this, permission_string)) {
//						should_show_explaination = true;
//					}
					break;
				}
			}
			if (all_granted) {
				listeners.remove(listener);
				listener.onGrant();
			} else {
//				if (should_show_explaination) {
//					listener.onExplanationNeeded(listener);
//				} else {
				requestPermissions(AngelActivity.this, listener);
//				ActivityCompat.requestPermissions(this, permission_strings, REQ_PERMISSION);
//				}
			}
		}

		public void openSystemPermissionSettingActivity(OnPermissionCheckListener caller) {
			permission_helper.addListener(caller);
			Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts("package", getPackageName(), null);
			intent.setData(uri);
			startActivityForResult(intent, caller.request_code);
		}

		public boolean handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
			for (int i = 0; i < listeners.size(); i++) {
				OnPermissionCheckListener listener = listeners.get(i);
				if (listener.request_code == requestCode) {
					listeners.remove(i);
					i--;
					boolean all_granted = true;
					boolean should_show_explaination = false;
					if (permissions.length == 0) {
						listener.onDeny();
						return false;
					}
					for (int j = 0; j < permissions.length; j++) {
						if (grantResults[j] != PackageManager.PERMISSION_GRANTED) {
							all_granted = false;
//							if (ActivityCompat.shouldShowRequestPermissionRationale(AngelActivity.this, permissions[j])) {
							should_show_explaination = true;
//							}
							break;
						}
					}
					if (all_granted) {
						listener.onGrant();
					} else {
						if (should_show_explaination) {
							listener.onExplanationNeeded(listener);
						} else {
							listener.onDeny();
						}
					}
				}
			}
			return false;
		}

		public void requestPermissions(AngelActivity act, @NonNull OnPermissionCheckListener listener) {
			addListener(listener);
			ActivityCompat.requestPermissions(act, listener.permission_strings, listener.request_code);
		}
	}

}
