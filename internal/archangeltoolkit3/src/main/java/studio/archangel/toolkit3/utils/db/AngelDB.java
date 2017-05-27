package studio.archangel.toolkit3.utils.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;

import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.models.AngelDBEntry;
import studio.archangel.toolkit3.utils.Logger;

/**
 * Created by xmk on 16/5/16.
 */
public class AngelDB {
	public static final int SQL_ERROR = -1;
	AngelDBHelper helper;
	String db_name;
	AngelDBConfig config;
	OnSqlExecuteListener listener;

	public AngelDB(AngelDBConfig c) {
		config = c;
		listener = new OnSqlExecuteListener() {
			@Override
			public void onSqlExecute(String sql) {
//				Logger.out("[SQL] " + sql);
			}
		};
	}

	public static String escape(String s) {
		if (s == null) {
			return null;
		}
		return DatabaseUtils.sqlEscapeString(s);
	}

	synchronized AngelDBHelper getHelper() {
		if (helper == null) {
			if (db_name != null) {
				openDBWithRawName(db_name);
			}
		}
		return helper;
	}

	public void openDBWithRawName(String name) {
		releaseHelper();
		helper = new AngelDBHelper(AngelApplication.getInstance(), name, new SQLiteCursorFactory(AngelApplication.isDebug(), listener), config);
		db_name = name;
	}

	public void openDBWithCurrentUser() {
		openDBWithRawName(config.getDatabaseName());
	}

	public void releaseHelper() {
		if (helper != null) {
			helper.releaseDB();
			helper.close();
			helper = null;
		}
		db_name = null;
	}

	public String getCurrentDBName() {
		return db_name;
	}

	public synchronized long executeUpdateSql(String sql) {
		Cursor cursor = null;
		try {
			getHelper().getDb().beginTransaction();
			getHelper().getDb().execSQL(sql);
			cursor = getHelper().getDb().rawQuery("SELECT changes() AS affected_row_count", null);
			if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
				return cursor.getLong(cursor.getColumnIndex("affected_row_count"));
			} else {
				return SQL_ERROR;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return SQL_ERROR;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			getHelper().getDb().setTransactionSuccessful();
			getHelper().getDb().endTransaction();
		}
	}
//	public long executeUpdateSql(String sql) {
//		Cursor cursor = null;
//		try {
//			cursor = getHelper().getDb().rawQuery(sql, null);
//			if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
//				return cursor.getLong(cursor.getColumnIndex("affected_row_count"));
//			} else {
//				return SQL_ERROR;
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return SQL_ERROR;
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//	}

	public Cursor executeQuerySql(String sql) {
		return getHelper().getDb().rawQuery(sql, null);
	}


	interface OnSqlExecuteListener {
		void onSqlExecute(String sql);
	}

}


class AngelDBHelper extends SQLiteOpenHelper {
	SQLiteDatabase db;
	AngelDBConfig config;

	public AngelDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, AngelDBConfig c) {
		super(context, name, factory, c.getVersion());
		config = c;
		for (Class<? extends AngelDBEntry> clazz : config.getDbEntryClasses()) {
			try {
				getDb().execSQL(clazz.newInstance().getCreatingTableSql());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		showDatabaseStructure();
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		Logger.out("AngelDBHelper onCreate");

	}

	public void showDatabaseStructure() {

		Cursor cursor = getDb().rawQuery("select tbl_name from sqlite_master ", null);
		int rows_num = cursor.getCount();    //取得資料表列數
		StringBuilder sb = new StringBuilder();
		sb = sb.append("Structure of ").append(db.toString()).append(":\n");
		if (rows_num != 0) {
			cursor.moveToFirst();            //將指標移至第一筆資料
			for (int i = 0; i < rows_num; i++) {
				String name = cursor.getString(cursor.getColumnIndex("tbl_name"));
				sb = sb.append("[").append(name).append("]\n").append(showTableStructure(db, name)).append("\n");
				cursor.moveToNext();        //將指標移至下一筆資料
			}
		}
		cursor.close();
		Logger.out(sb.toString());
	}

	public static String showTableStructure(SQLiteDatabase db, String table_name) {
		Cursor cursor = db.query(table_name, null, null, null, null, null, null);
		String[] names = cursor.getColumnNames();
		StringBuilder sb = new StringBuilder();
		for (String name : names) {
			sb = sb.append(name).append("\t");
		}
		cursor.close();
		return sb.toString();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Logger.out("AngelDBHelper onUpgrade " + oldVersion + "->" + newVersion);
		db.beginTransaction();
		for (int i = oldVersion; i < newVersion; i++) {
			if (!Logger.isEnabled()) {
				System.out.println("db:" + i + "->" + (i + 1));
			}
			config.upgradeDB(db, i, i + 1);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		Logger.out("AngelDBHelper onOpen");
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onDowngrade(db, oldVersion, newVersion);
		Logger.out("AngelDBHelper onDowngrade");
	}

	public synchronized SQLiteDatabase getDb() {
		if (db == null) {
			db = getWritableDatabase();
		}
		return db;
	}

	public void releaseDB() {
		if (db != null) {
			db.close();
			db = null;
		}
	}
}

class SQLiteCursorFactory implements SQLiteDatabase.CursorFactory {
	AngelDB.OnSqlExecuteListener listener;
	private boolean is_debug = true;

	public SQLiteCursorFactory(boolean is_debug, AngelDB.OnSqlExecuteListener l) {
		this.is_debug = is_debug;
		listener = l;
	}

	@Override
	public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
		if (is_debug && listener != null) {
			listener.onSqlExecute(query.toString());
		}
		return new SQLiteCursor(db, masterQuery, editTable, query);
	}

}