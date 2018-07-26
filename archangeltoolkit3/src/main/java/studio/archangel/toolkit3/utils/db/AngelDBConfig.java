package studio.archangel.toolkit3.utils.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.models.AngelDBEntry;

/**
 * Created by xumingke on 16/5/30.
 */
public abstract class AngelDBConfig {

	public String getDatabaseName() {//i.e. app_1_debug
		return AngelApplication.getInstance().getProjectPrefix() + "_" +
				AngelApplication.getInstance().getCurrentUserId() +
				(AngelApplication.isDebug() ? "_debug" : "");
	}

	public abstract ArrayList<Class<? extends AngelDBEntry>> getDbEntryClasses();

	public abstract void upgradeDB(SQLiteDatabase db, int old_version, int target_version);

	public abstract int getVersion();
}
