package ws.wiklund.beerguide.activities.fragment;

import ws.wiklund.beerguide.R;
import ws.wiklund.beerguide.db.BeerCellarProvider;
import ws.wiklund.beerguide.list.CellarListCursorAdapter;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.util.ViewHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

public class CellarListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private View view;
	private Beverage beverage;
	private CellarListCursorAdapter adapter;

	public CellarListFragment(Beverage beverage) {
		Log.d(CellarListFragment.class.getName(), "Beverage: " + beverage);
		this.beverage = beverage;
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // this is really important in order to save the state across screen
        // configuration changes for example
        setRetainInstance(true);

    	ViewHelper viewHelper = new ViewHelper();

        if(!viewHelper.isLightVersion(Integer.valueOf(getString(R.string.version_type)))) {
        	view.findViewById(R.id.adView).setVisibility(View.GONE);
        	view.findViewById(R.id.adView1).setVisibility(View.GONE);        	
        }
        
        getLoaderManager().initLoader(0, null, this);
        
        // Now create a new list adapter bound to the cursor.
        adapter = new CellarListCursorAdapter(getActivity(), beverage);

        // Bind to our new adapter.
        setListAdapter(adapter);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		view = inflater.inflate(R.layout.cellarlist, container, false);
		
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.cellar_menu, menu);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		FragmentActivity a = getActivity();
		TabHost tabHost = (TabHost) a.findViewById(android.R.id.tabhost);
		if(BeerTabsFragment.TAB_CELLAR.equals(tabHost.getCurrentTabTag())) {
			menu.clear();
		    MenuInflater inflater = a.getMenuInflater();
			inflater.inflate(R.menu.cellar_menu, menu);
		}

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menuAddToCellar:
			ContentValues values = new ContentValues();
			values.put("beverage_id", beverage.getId());
			values.put("no_bottles", 1);
			values.put("added_to_cellar", SystemClock.elapsedRealtime());
			getActivity().getContentResolver().insert(BeerCellarProvider.CONTENT_URI, values);
			
			Log.d(CellarListFragment.class.getName(), "Added one bottle of " + beverage.getName() + " to cellar");
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), BeerCellarProvider.CONTENT_URI, null, "beverage_id = ?", new String[]{String.valueOf(beverage.getId())}, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.changeCursor(cursor);

        if(adapter.isEmpty()) {
        	//showAddToCellarDialog();
        }
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.changeCursor(null);		
	}

	@Override
	public void onStop() {
		adapter.persist();
		super.onStop();
	}

	public void advanced(View view) {    	
    }

}
