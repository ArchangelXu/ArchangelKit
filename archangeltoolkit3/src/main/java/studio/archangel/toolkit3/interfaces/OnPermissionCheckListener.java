package studio.archangel.toolkit3.interfaces;

import studio.archangel.toolkit3.activities.AngelActivity;

/**
 * Created by xmk on 2016/12/8.
 */
public abstract class OnPermissionCheckListener {
	private AngelActivity act;
	public int request_code;
	public String[] permission_strings;

	public OnPermissionCheckListener(AngelActivity act) {
		this.act = act;
	}

	/**
	 * 用户授予了所有请求的权限
	 */
	public abstract void onGrant();

	/**
	 * 1.用户选择了不再询问（目前在这种情况下也会调用onExplainationNeeded）
	 * 2.用户在弹出的权限解释对话框选择了取消
	 */
	public abstract void onDeny();

	/**
	 * 用户授予了所有请求的权限
	 */
	public abstract void onExplainationNeeded(OnPermissionCheckListener this_listener);

	/**
	 * 用户从设置中的APP权限页面返回，应该重试之前请求的权限
	 */
	public void onRetry() {
		act.permission_helper.checkPermissions(permission_strings, this);
	}
}
