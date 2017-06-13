/**
 *
 */
package studio.archangel.toolkit3.activities;


import android.support.v7.app.AppCompatActivity;


/**
 * @author Administrator
 */
public abstract class AngelActivityV4 extends AppCompatActivity {
//	public AngelLoadingDialog dialog;
//	public boolean destroyed = false;
//	//    Validator validator;
//	private boolean orientation_set = false;
//	private boolean is_immersive = false;
//	protected AngelActionBar bar;
//	public WeakHandler handler;
//	//    public boolean track_app_status_change = true;
//
//	/**
//	 * 加载指定的Feature
//	 *
//	 * @param savedInstanceState
//	 * @param feature_id
//	 */
//	protected void onCreate(Bundle savedInstanceState, int[] feature_id, int orientation) {
//		super.onCreate(savedInstanceState);
//		init(feature_id, orientation);
//	}
//
//	protected void onCreate(Bundle savedInstanceState, int[] feature_id) {
//		super.onCreate(savedInstanceState);
//		init(feature_id, -1);
//	}
//
//	protected AngelActivityV4 getSelf() {
//		return this;
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		init(null, -1);
//	}
//
//	public AngelActionBar getAngelActionBar() {
//		return bar;
//	}
//
//	public void setAngelActionBar(AngelActionBar bar) {
//		this.bar = bar;
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		Application app = getApplication();
//		if (app != null && app instanceof AngelApplication) {
//			AngelApplication aa = (AngelApplication) app;
//			if (aa.was_in_background) {
//				Logger.out("App was brought to foreground through " + getSelf().getClass().getSimpleName());
//				onTakenToForeground(aa.activity_taken_to_back_time_stamp);
//				AngelApplication.getInstance().onAppBroughtToFront();
//			}
//			aa.stopActivityTransitionTimer();
//		}
//	}
//
////	protected abstract String getRealName();
//
//	public void onTakenToBackground(long time) {
//
//	}
//
//	public void onTakenToForeground(long time) {
//
//	}
//
//	public int getActivityTransitionTime() {
//		return -1;
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		Application app = getApplication();
//		if (app != null && app instanceof AngelApplication) {
//			AngelApplication aa = (AngelApplication) app;
//			aa.startActivityTransitionTimerV4(new WeakReference<>(getSelf()));
//		}
//	}
//
//	protected void setOrientation(int info) {
//		if (!shouldOverrideOrientation()) {
//			return;
//		}
//		if (info == -1) {
//			return;
//		}
//		try {
//			setRequestedOrientation(info);
//			orientation_set = true;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		destroyed = true;
//		if (dialog != null) {
//			dialog.forceDismiss();
//		}
//
//		super.onDestroy();
//	}
//
//	protected void hideStatusBar() {
//		WindowManager.LayoutParams attrs = getWindow().getAttributes();
//		attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//		getWindow().setAttributes(attrs);
//	}
//
//	protected void showStatusBar() {
//		WindowManager.LayoutParams attrs = getWindow().getAttributes();
//		attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
//		getWindow().setAttributes(attrs);
//	}
//
//	protected boolean shouldOverrideOrientation() {
//		return false;
//	}
//
//	/**
//	 * 初始化。将Actionbar的图标设置为App图标，并将点击图标映射为返回键
//	 *
//	 * @param feature_id 要加载的Feature的id
//	 */
//	void init(int[] feature_id, int orientation) {
//		setOrientation(orientation);
//		if (!orientation_set) {
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		}
//		if (feature_id != null) {
//			for (int id : feature_id) {
//				requestWindowFeature(id);
//				if (id == Window.FEATURE_NO_TITLE) {
//					getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//				}
//			}
//		}
//
//		handler = new WeakHandler();
//	}


}
