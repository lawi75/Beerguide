package ws.wiklund.beerguide.activities;

import ws.wiklund.beerguide.db.BeerDatabaseHelper;
import ws.wiklund.guides.activities.BeverageActivity;
import ws.wiklund.guides.db.BeverageDatabaseHelper;

public class BeerActivity extends BeverageActivity {

	@Override
	protected BeverageDatabaseHelper getDatabaseHelper() {
		return new BeerDatabaseHelper(this);
	}

	@Override
	protected Class<?> getModifyBeverageActivityClass() {
		return ModifyBeerActivity.class;
	}
	
}
