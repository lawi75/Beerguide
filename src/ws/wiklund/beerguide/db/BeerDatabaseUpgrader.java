package ws.wiklund.beerguide.db;

import ws.wiklund.guides.db.DatabaseUpgrader;
import android.database.sqlite.SQLiteDatabase;

public class BeerDatabaseUpgrader extends DatabaseUpgrader {
	public BeerDatabaseUpgrader(SQLiteDatabase db) {
		super(db);
	}

	//Available DB versions
	private static final int VERSION_1 = 1;

	public int upgrade(int oldVersion, int newVersion) {
		int version = -1;

		switch (oldVersion) {
			case VERSION_1:
				break;				
			default:
				break;
		}

		return version;
	}

}
