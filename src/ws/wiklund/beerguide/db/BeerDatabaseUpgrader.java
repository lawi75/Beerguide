package ws.wiklund.beerguide.db;

import ws.wiklund.guides.db.DatabaseUpgrader;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BeerDatabaseUpgrader extends DatabaseUpgrader {
	//Available DB versions
	static final int VERSION_1 = 1;
	static final int VERSION_2 = 2;
	static final int VERSION_3 = 3;

	public BeerDatabaseUpgrader(SQLiteDatabase db) {
		super(db);
	}

	public int upgrade(int oldVersion, int newVersion) {
		int version = -1;

		switch (oldVersion) {
			case VERSION_1:
				if(newVersion > VERSION_1) {
					version = moveToVersion2();
					Log.d(BeerDatabaseUpgrader.class.getName(), "Upgraded DB from version [" + oldVersion + "] to version [" + version + "]");
					
					if(version < newVersion) {
						return upgrade(version, newVersion);
					}
					
					return VERSION_2;
				}
				
				break;				
			case VERSION_2:
				if(newVersion > VERSION_2) {
					version = moveToVersion3();
					Log.d(BeerDatabaseUpgrader.class.getName(), "Upgraded DB from version [" + oldVersion + "] to version [" + version + "]");
					
					if(version < newVersion) {
						return upgrade(version, newVersion);
					}
					
					return VERSION_3;
				}
				
				break;				
			default:
				break;
		}

		return version;
	}

	private int moveToVersion2() {
		//1. Create and populate beverage type table
		createAndPopulateBeverageTypeTable(db);
		
		//2. Update beverage type ids in beverage table
		updateBeverageTypeIdInBeverageTable(100, 1);
		updateBeverageTypeIdInBeverageTable(200, 2);
		updateBeverageTypeIdInBeverageTable(300, 3);
		updateBeverageTypeIdInBeverageTable(400, 4);
		updateBeverageTypeIdInBeverageTable(500, 5);
		updateBeverageTypeIdInBeverageTable(600, 6);
		updateBeverageTypeIdInBeverageTable(700, 7);
		
		//3. back up beverage table
		db.execSQL("DROP TABLE IF EXISTS " + BeerDatabaseHelper.BEVERAGE_TABLE + "_TMP");
		db.execSQL("ALTER TABLE " + BeerDatabaseHelper.BEVERAGE_TABLE + " RENAME TO " + BeerDatabaseHelper.BEVERAGE_TABLE + "_TMP");
		
		//4. Create new beverage table
		db.execSQL(BeerDatabaseHelper.DB_CREATE_BEVERAGE);
		
		//5. Populate new beverage table
		db.execSQL("INSERT INTO " + BeerDatabaseHelper.BEVERAGE_TABLE + " ("
				+ "_id, "
				+ "name, "
				+ "no, "
				+ "thumb, "
				+ "country_id, "
				+ "year, "
				+ "beverage_type_id, "
				+ "producer_id, "
				+ "strength, "
				+ "usage, "
				+ "taste, "
				+ "provider_id, "
				+ "rating, "
				+ "comment, "			
				+ "category_id, "
				+ "added"
				+") "
			+ "SELECT " 			
				+ "_id, "
				+ "name, "
				+ "no, "
				+ "thumb, "
				+ "country_id, "
				+ "year, "
				+ "type, "
				+ "producer_id, "
				+ "strength, "
				+ "usage, "
				+ "taste, "
				+ "provider_id, "
				+ "rating, "
				+ "comment, "			
				+ "category_id, "
				+ "added "
			+ " FROM " + BeerDatabaseHelper.BEVERAGE_TABLE + "_TMP");
		
		//6. clean up
		db.execSQL("DROP TABLE IF EXISTS " + BeerDatabaseHelper.BEVERAGE_TABLE + "_TMP");
		
		//7. remove year
		db.execSQL("UPDATE " + BeerDatabaseHelper.BEVERAGE_TABLE + " SET year = -1 WHERE year = 1900");
		
		return VERSION_2;			
	}

	private int moveToVersion3() throws SQLException {
		insertImageColumnToBeverage();
		
		return VERSION_3;
	}

	@Override
	public void createAndPopulateBeverageTypeTable(SQLiteDatabase db) {
		//1. create beverage type table
		db.execSQL(BeerDatabaseHelper.DB_CREATE_BEVERAGE_TYPE);
		
		//2. populate beverage type table
		insertBeverageType(1, "Öl, Ljus lager");
		insertBeverageType(2, "Öl, Mörk lager");
		insertBeverageType(3, "Öl, Porter och Stout");
		insertBeverageType(4, "Öl, Ale");
		insertBeverageType(5, "Öl, Veteöl");
		insertBeverageType(6, "Öl, Specialöl");
		insertBeverageType(7, "Öl, Spontanjäst öl");
	}
	
}
