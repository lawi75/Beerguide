package ws.wiklund.beerguide.db;

import android.content.UriMatcher;
import android.net.Uri;
import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.guides.db.CellarProvider;

public class BeerCellarProvider extends CellarProvider {
    private static final String AUTHORITY = "ws.wiklund.beerguide.db.BeerCellarProvider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CELLAR_BASE_PATH);
    
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	private BeverageDatabaseHelper db;
    
    static {
    	uriMatcher.addURI(AUTHORITY, CELLAR_BASE_PATH, CELLAR);
    	uriMatcher.addURI(AUTHORITY, CELLAR_BASE_PATH + "/#", BEVERAGE_ID);
    }

    @Override
	public UriMatcher getUriMatcher() {
		return uriMatcher;
	}
	
	@Override
	public boolean onCreate() {
		db = new BeerDatabaseHelper(getContext());
		return true;
	}

	@Override
	public BeverageDatabaseHelper getDatabase() {
		return db;
	}
    
}
