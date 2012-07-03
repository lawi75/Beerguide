package ws.wiklund.beerguide.db;

import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.guides.db.DatabaseUpgrader;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class BeerDatabaseHelper extends BeverageDatabaseHelper {
	public static final String DATABASE_NAME = "beerguide.db";
	private static final int DATABASE_VERSION = 1;

	public BeerDatabaseHelper(Context context) {
		this(context, DATABASE_NAME);
	}

	//Used for testing so that db can be created and dropped with out destroying dev data
	public BeerDatabaseHelper(Context context, String dbName) {
		super(context, dbName, DATABASE_VERSION);
	}

	@Override
	public DatabaseUpgrader getDatabaseUpgrader(SQLiteDatabase db) {
		return new BeerDatabaseUpgrader(db);
	}

}
