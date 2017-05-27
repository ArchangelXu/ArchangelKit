package studio.archangel.toolkit3.models;

/**
 * Created by Michael on 2014/10/21.
 */
public abstract class AngelDBEntry {
//    public AngelDBEntry() {
//
//    }
//
//    public AngelDBEntry(Cursor c) {
//
//    }

	public abstract String getTableName();

	public abstract String getDeletingSql();

	public abstract String getSavingSql();

	public abstract String getCreatingTableSql();

	public abstract void merge(AngelDBEntry other);
}