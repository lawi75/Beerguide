package ws.wiklund.beerguide.activities;

import ws.wiklund.beerguide.db.BeerDatabaseHelper;
import ws.wiklund.guides.activities.BeverageStatsActivity;
import ws.wiklund.guides.db.BeverageDatabaseHelper;

public class StatsActivity extends BeverageStatsActivity {

	@Override
	protected BeverageDatabaseHelper getDatabaseHelper() {
		return new BeerDatabaseHelper(this);
	}

}
