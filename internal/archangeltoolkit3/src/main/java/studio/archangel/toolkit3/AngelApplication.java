package studio.archangel.toolkit3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import studio.archangel.toolkit3.activities.AngelActivity;
import studio.archangel.toolkit3.utils.Logger;
import studio.archangel.toolkit3.utils.networking.AngelNet;
import studio.archangel.toolkit3.utils.text.AmountProvider;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

//import com.squareup.leakcanary.LeakCanary;


/**
 * Created by Michael on 2014/9/24.
 */
public abstract class AngelApplication extends Application {


	private static AngelApplication instance;
	public static String cpu_arch;
	public static int screen_width;
	public static int screen_height;
	public static float screen_width_dp;
	public static float screen_height_dp;
	public static int status_bar_height;
	public static String device_des;
	public static int app_version_code;
	public static String app_version_name;
	public static String app_package_name;
	public static String device_model;
	public static String device_brand;
	static boolean is_debug = true;
	static boolean is_test_server = true;
	public static final int result_ok = Activity.RESULT_OK;
	public static final int result_fail = 1001;
	public static final int result_special = 1002;
	public static final int result_cancel = Activity.RESULT_CANCELED;
	private Timer activity_transition_timer;
	private TimerTask activity_transition_timer_task;
	public boolean was_in_background;
	public long activity_taken_to_back_time_stamp;
	boolean stop_timer_for_once = false;
	public boolean is_changing_language = false;

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.setEnable(true);
		getScreenSize();
		initAPPParameters();
		boolean should_init = shouldInit();
		Logger.setEnable(false);
		if (!should_init) {
			return;
		}
//		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//				.setDefaultFontPath("fonts/font.ttf")
//				.setFontAttrId(R.attr.fontPath)
//				.build()
//		);
		instance = this;
		initExtraTools();
		FrescoInit();
	}

	void initAPPParameters() {
		cpu_arch = Build.CPU_ABI;
		if (Build.CPU_ABI2 != null && !Build.CPU_ABI2.isEmpty()) {
			cpu_arch += "（" + Build.CPU_ABI2 + "）";
		}

		Logger.outSimple("cpu_arch\t" + cpu_arch);
		Runtime rt = Runtime.getRuntime();
		Logger.outSimple("max memory\t" + AmountProvider.getReadableFileSize(this, rt.maxMemory()));
		device_brand = Build.MANUFACTURER;
		device_model = Build.MODEL;
		device_des = device_brand + ":" + device_model + "(" + Build.VERSION.RELEASE + ")";
		Logger.outSimple("device\t" + device_des);
		try {
			app_version_name = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			app_version_name = "未知版本";
			e.printStackTrace();
		}

		try {
			app_package_name = getPackageManager().getPackageInfo(getPackageName(), 0).packageName;
		} catch (PackageManager.NameNotFoundException e) {
			app_package_name = "";
			e.printStackTrace();
		}
		try {
			app_version_code = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			app_version_code = -1;
			e.printStackTrace();
		}
	}


	private void FrescoInit() {
		int maxHeapSize = (int) Runtime.getRuntime().maxMemory();
		final int maxMemoryCacheSize = maxHeapSize / 8;
		//	public static final int maxDiskCacheSize = 40 * ByteConstants.MB;
		final int maxDiskCacheSize = maxMemoryCacheSize * 2 / 1000 / 1000;//in MB
		ImagePipelineConfig.Builder configBuilder = OkHttpImagePipelineConfigFactory.newBuilder(this, getAngelNetInstance().getClient());

		// 设置内存配置
		final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
				maxMemoryCacheSize, // Max total size of elements in the cache
				256,                     // Max entries in the cache
				Integer.MAX_VALUE, // Max total size of elements in eviction queue
				Integer.MAX_VALUE,                     // Max length of eviction queue
				Integer.MAX_VALUE);

		configBuilder.setBitmapMemoryCacheParamsSupplier(
				new Supplier<MemoryCacheParams>() {
					public MemoryCacheParams get() {
						return bitmapCacheParams;
					}
				});

		// 设置缓存配置
		DiskCacheConfig imagepipeline_cache = DiskCacheConfig.newBuilder(this)
				.setBaseDirectoryPath(getApplicationContext().getCacheDir())
				.setBaseDirectoryName("imagepipeline_cache")
				.setMaxCacheSize(maxDiskCacheSize)
				.build();

		configBuilder.setMainDiskCacheConfig(imagepipeline_cache);

		// 设置请求监听器配置
		Set<RequestListener> requestListeners = new HashSet<>();
		requestListeners.add(new RequestLoggingListener());

		configBuilder.setRequestListeners(requestListeners);

		configBuilder.setDownsampleEnabled(true);
		configBuilder.experiment().setWebpSupportEnabled(true);

		Fresco.initialize(this, configBuilder.build());
	}

	protected abstract AngelNet getAngelNetInstance();

	void initExtraTools() {

	}

	public static AngelApplication getInstance() {
		return instance;
	}

	public static boolean isDebug() {
		return is_debug;
	}

	public static void setDebug(boolean b) {
		is_debug = b;
	}

	public static boolean isTestServer() {
		return is_test_server;
	}

	public static void setIsTestServer(boolean is_test_server) {
		AngelApplication.is_test_server = is_test_server;
	}

	public static boolean shouldAutologin() {
		return getInstance().getPreference().getBoolean("should_auto_login", false);
	}

	public static void setShouldAutologin(boolean b) {
		getInstance().getEditor().putBoolean("should_auto_login", b).commit();
	}

	public static boolean hasLogin() {
		return getInstance().getPreference().getBoolean("user_has_login", false);
	}

	public static void setHasLogin(boolean b) {
		getInstance().getEditor().putBoolean("user_has_login", b).commit();
	}

	public static String getLanguage() {
		return getInstance().getPreference().getString("app_language", getInstance().getResources().getConfiguration().locale.getLanguage());
	}

	public static void setLanguage(String s) {
		getInstance().getEditor().putString("app_language", s).commit();
	}

	public static String getCountryCode() {
		return getInstance().getPreference().getString("app_country_code", getInstance().getResources().getConfiguration().locale.getCountry());
	}

	public static void setCountryCode(String s) {
		getInstance().getEditor().putString("app_country_code", s).commit();
	}

	public SharedPreferences getPreference() {
		return getSharedPreferences(getProjectPrefix(), MODE_PRIVATE);
	}

	@SuppressLint("CommitPrefEdits")
	public SharedPreferences.Editor getEditor() {
		SharedPreferences pref = getPreference();
		SharedPreferences.Editor editor = pref.edit();
		return editor;
	}

	public boolean shouldInit() {
		boolean result = false;
		try {
			File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
			BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
			String processName = mBufferedReader.readLine().trim();
			mBufferedReader.close();
			result = processName.equals(getPackageName());
		} catch (Exception e) {
			e.printStackTrace();
			result = shouldInitOld();
		}
		Logger.out("shouldInit = " + result + " pid = " + android.os.Process.myPid() + " package = " + getPackageName());
		return result;
	}

	public boolean shouldInitOld() {
		ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
		List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		if (processInfos != null) {
			String mainProcessName = getPackageName();
			int myPid = Process.myPid();
			for (ActivityManager.RunningAppProcessInfo info : processInfos) {
				if (info.pid == myPid && mainProcessName.equals(info.processName)) {
					return true;
				}
			}
		}
		return false;
	}

	public abstract String getProjectPrefix();

	public abstract int getGalleryActivityPlaceholder();

	public abstract ScalingUtils.ScaleType getGalleryActivityPlaceholderScaleType();

	public abstract String getCurrentUserId();

	public abstract long getTransitionTimeBetweenActivities();

	public abstract int getActionbarBackgroundResource();

	public abstract int getActionbarTextColor();

	void getScreenSize() {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Method mGetRawH = null, mGetRawW = null;

		try {
			// For JellyBean 4.2 (API 17) and onward
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
				display.getRealMetrics(metrics);

				screen_width = metrics.widthPixels;
				screen_height = metrics.heightPixels;
			} else {
				mGetRawH = Display.class.getMethod("getRawHeight");
				mGetRawW = Display.class.getMethod("getRawWidth");

				try {
					screen_width = (Integer) mGetRawW.invoke(display);
					screen_height = (Integer) mGetRawH.invoke(display);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (NoSuchMethodException e3) {
			e3.printStackTrace();
		}
		if (screen_width > screen_height) {
			int tmp = screen_width;
			screen_width = screen_height;
			screen_height = tmp;
		}
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			status_bar_height = getResources().getDimensionPixelSize(resourceId);
		}
		metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		float density = getResources().getDisplayMetrics().density;
		screen_width_dp = metrics.widthPixels / density;
		screen_height_dp = metrics.heightPixels / density;
		if (screen_width_dp > screen_height_dp) {
			float tmp = screen_width_dp;
			screen_width_dp = screen_height_dp;
			screen_height_dp = tmp;
		}
		Logger.outSimple("screen size:" + screen_width + "×" + screen_height);
		Logger.outSimple("screen size(dp):" + screen_width_dp + "×" + screen_height_dp);
	}

	//phone number
	public void setLastLoginUserName(String phone) {
		getEditor().putString("last_login_user_name", phone).commit();
	}

	//phone number
	public String getLastLoginUserName() {
		return getPreference().getString("last_login_user_name", "");
	}

	//username
	public void setLastLoginRealUserName(String username) {
		getEditor().putString("setLastLoginRealUserName", username).commit();
	}

	//username
	public String getLastLoginRealUserName() {
		return getPreference().getString("setLastLoginRealUserName", "");
	}

	public void stopActivityTransitionTimerForOnce() {
		stop_timer_for_once = true;
	}

	public void startActivityTransitionTimer(final WeakReference<AngelActivity> ref) {
		if (stop_timer_for_once) {
			stop_timer_for_once = false;
			Logger.out("Timer stopped for once");
			return;
		}
		this.activity_transition_timer = new Timer();
		final AngelActivity act = ref.get();
		final String name = act.getClass().getName();
		final int act_time = act.getActivityTransitionTime();
		final long time = act_time == -1 ? getTransitionTimeBetweenActivities() : act_time;
		this.activity_transition_timer_task = new TimerTask() {
			public void run() {
				activity_taken_to_back_time_stamp = System.currentTimeMillis();
				AngelActivity act = ref.get();
				if (act != null) {
					act.onTakenToBackground(activity_taken_to_back_time_stamp - time);
					Logger.out("App was brought to background through " + name);
				}
				was_in_background = true;
				onAppBroughtToBack(act);
			}
		};

		this.activity_transition_timer.schedule(activity_transition_timer_task, time);
	}

	public void stopActivityTransitionTimer() {
		if (this.activity_transition_timer_task != null) {
			this.activity_transition_timer_task.cancel();
		}

		if (this.activity_transition_timer != null) {
			this.activity_transition_timer.cancel();
		}

		this.was_in_background = false;
	}


	public void onAppBroughtToBack(AngelActivity act) {

	}

	public void onAppBroughtToFront(AngelActivity activity) {

	}

}
