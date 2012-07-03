package ws.wiklund.beerguide.activities.fragment;

import ws.wiklund.beerguide.R;
import ws.wiklund.beerguide.db.BeerCellarProvider;
import ws.wiklund.guides.model.Beverage;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class BeerTabsFragment extends Fragment implements OnTabChangeListener {
	static final String TAB_BEER = "wine";
	static final String TAB_CELLAR = "cellar";
	private static final int BEER_TAB = 0;
	private static final int CELLAR_TAB = 1;

	private View root;
	private TabHost tabHost;
	private static int currentTab;
	private Beverage beverage;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.beer_tabs, null);
		tabHost = (TabHost) root.findViewById(android.R.id.tabhost);

		beverage = (Beverage) getActivity().getIntent().getSerializableExtra("ws.wiklund.beerguide.activities.Beverage");
		
		setupTabs();
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);

		tabHost.setOnTabChangedListener(this);
		tabHost.setCurrentTab(currentTab);

		// manually start loading stuff in the first tab
		updateTab(TAB_BEER, R.id.beer_tab);
	}

	private void setupTabs() {
		tabHost.setup(); // you must call this before adding your tabs!

		tabHost.addTab(newTab(TAB_BEER, R.id.beer_tab));
		//tabHost.addTab(newTab(TAB_CELLAR, R.id.cellar_tab));
	}

	private TabSpec newTab(String tag, int tabContentId) {
		Log.d(BeerTabsFragment.class.getName(), "buildTab(): tag=" + tag);

		View indicator = LayoutInflater.from(getActivity()).inflate(
				R.layout.tab, (ViewGroup) root.findViewById(android.R.id.tabs),
				false);

		if (TAB_BEER.equals(tag)) {
			((TextView) indicator.findViewById(R.id.text)).setText(beverage.getName());
		} else if (TAB_CELLAR.equals(tag)) {
			setCellarText(indicator);
		}
		
		TabSpec tabSpec = tabHost.newTabSpec(tag);
		tabSpec.setIndicator(indicator);
		tabSpec.setContent(tabContentId);
		return tabSpec;
	}

	private void setCellarText(View indicator) {
		Cursor c = getActivity().getContentResolver().query(BeerCellarProvider.CONTENT_URI, null, "beverage_id = ?", new String[]{String.valueOf(beverage.getId())}, null);
		
		String noBottles = null;
		if(c.moveToFirst()) {
			noBottles = " (" + c.getInt(2) + ")";
		}

		((TextView) indicator.findViewById(R.id.text)).setText(getActivity().getString(R.string.cellar) + (noBottles != null ? noBottles : ""));
	}

	@Override
	public void onTabChanged(String tabId) {
		Log.d(BeerTabsFragment.class.getName(), "onTabChanged(): tabId=" + tabId);
		if (TAB_BEER.equals(tabId)) {
			updateTab(tabId, R.id.beer_tab);
			currentTab = BEER_TAB;
			return;
		}

		if (TAB_CELLAR.equals(tabId)) {
			updateTab(tabId, R.id.cellar_tab);
			currentTab = CELLAR_TAB;
			return;
		}
	}

	private void updateTab(String tabId, int placeholder) {
		FragmentManager fm = getFragmentManager();
		if (fm.findFragmentByTag(tabId) == null) {
			fm.beginTransaction().replace(placeholder, tabId.equals(TAB_BEER) ? new BeerFragment(beverage) : new CellarListFragment(beverage), tabId).commit();
		}
	}

}
