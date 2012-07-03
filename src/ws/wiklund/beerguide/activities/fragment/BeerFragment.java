package ws.wiklund.beerguide.activities.fragment;

import java.util.Date;

import ws.wiklund.beerguide.R;
import ws.wiklund.beerguide.activities.ModifyBeerActivity;
import ws.wiklund.beerguide.db.BeerDatabaseHelper;
import ws.wiklund.beerguide.util.BeerTypes;
import ws.wiklund.guides.bolaget.SystembolagetParser;
import ws.wiklund.guides.model.Country;
import ws.wiklund.guides.model.Producer;
import ws.wiklund.guides.model.Provider;
import ws.wiklund.guides.model.Rating;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.util.ViewHelper;
import ws.wiklund.guides.util.facebook.FacebookConnector;
import ws.wiklund.guides.util.facebook.SessionEvents;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class BeerFragment extends Fragment {
	private final Handler facebookHandler = new Handler();

	private Beverage beverage;
	private View view;
	private ViewHelper viewHelper;
	private FacebookConnector connector;

	final Runnable updateFacebookNotification = new Runnable() {
        public void run() {
        	Toast.makeText(getActivity().getBaseContext(), getString(R.string.facebookPosted), Toast.LENGTH_LONG).show();
        }
    };

	public BeerFragment(Beverage beverage) {
		Log.d(BeerFragment.class.getName(), "Beverage: " + beverage);
		this.beverage = beverage;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.beer_menu, menu);
	}	

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		FragmentActivity a = getActivity();
		TabHost tabHost = (TabHost) a.findViewById(android.R.id.tabhost);
		if(BeerTabsFragment.TAB_BEER.equals(tabHost.getCurrentTabTag())) {
			menu.clear();
		    MenuInflater inflater = a.getMenuInflater();
			inflater.inflate(R.menu.beer_menu, menu);
		}

		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		view = inflater.inflate(R.layout.beer, container, false);
		
		return view;
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // this is really important in order to save the state across screen
        // configuration changes for example
        setRetainInstance(true);
        
		viewHelper = new ViewHelper();
        if(!viewHelper.isLightVersion(Integer.valueOf(getString(R.string.version_type)))) {
    		view.findViewById(R.id.adView).setVisibility(View.GONE);
        }

		connector = new FacebookConnector("263921010324730", getActivity(), getActivity().getApplicationContext(), new String[] {"publish_stream", "read_stream", "offline_access"});
		
        populateUI();
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menuShareOnFacebook:
				if (connector.getFacebook().isSessionValid()) {
					new FacebookPostMessageTask().execute();
				} else {
					SessionEvents.addAuthListener(new SessionEvents.AuthListener() {
						@Override
						public void onAuthSucceed() {
							new FacebookPostMessageTask().execute();
						}
						
						@Override
						public void onAuthFail(String error) {
						}
					});

					connector.login();
				}

				return true;
			case R.id.menuRateBeer:
				final Dialog viewDialog = new Dialog(getActivity()); 
				viewDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND); 
				viewDialog.setTitle(R.string.rate); 

				LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
				View dialogView = li.inflate(R.layout.rating, null); 
				viewDialog.setContentView(dialogView); 
				viewDialog.show(); 
				
				final RatingBar rating = (RatingBar) dialogView.findViewById(R.id.dialogRatingBar);
				
				if(beverage != null && beverage.getRating() != -1) {
					rating.setRating(beverage.getRating());
				}
				
				final Button okBtn = (Button) dialogView.findViewById(R.id.ratingOk);
				final Button cancelBtn = (Button) dialogView.findViewById(R.id.ratingCancel);
				
				okBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Rating r = Rating.fromFloat(rating.getRating()); 
						beverage.setRating(r.getRating());
					   	BeerDatabaseHelper helper = new BeerDatabaseHelper(getActivity().getApplicationContext());
				    	
				    	helper.addBeverage(beverage);

						RatingBar rating = (RatingBar) getActivity().findViewById(R.id.ratingBar);
						rating.setRating(r.getRating());
				    	viewDialog.dismiss();
					}
				});
				
				cancelBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
				    	viewDialog.hide();
					}
				});
				
				return true;
			case R.id.menuUpdateBeer:
				Intent intent = new Intent(getActivity().getApplicationContext(), ModifyBeerActivity.class);
				intent.putExtra("ws.wiklund.beerguide.activities.Beverage", beverage);
				
		    	startActivityForResult(intent, 0);
				return true;
		}
		
		return false;
	}
		

	private void populateUI() {
		viewHelper.setThumbFromUrl((ImageView) view.findViewById(R.id.Image_thumbUrl), beverage.getThumb());
		
		TextView no = (TextView) view.findViewById(R.id.Text_no);
		no.setText(String.valueOf(beverage.getNo()));

		TextView type = (TextView) view.findViewById(R.id.Text_type);
		
		
		ViewHelper.setText(type, new BeerTypes().findTypeFromId(beverage.getBeverageTypeId()).toString());

		Country c = beverage.getCountry();
		if (c != null) {
			viewHelper.setCountryThumbFromUrl((ImageView) view.findViewById(R.id.Image_country_thumbUrl), c);
			TextView country = (TextView) view.findViewById(R.id.Text_country);
			ViewHelper.setText(country, c.getName());
		}

		if (beverage.getYear() != -1) {
			TextView year = (TextView) view.findViewById(R.id.Text_year);
			ViewHelper.setText(year, String.valueOf(beverage.getYear()));
		}
		
		Producer p = beverage.getProducer();
		if (p != null) {
			TextView producer = (TextView) view.findViewById(R.id.Text_producer);
			ViewHelper.setText(producer, p.getName());
		}

		if (beverage.getStrength() != -1) {
			TextView strength = (TextView) view.findViewById(R.id.Text_strength);
			ViewHelper.setText(strength, String.valueOf(beverage.getStrength()) + " %");
		}
		
		if (beverage.hasPrice()) {
			TextView label = (TextView) view.findViewById(R.id.label_price);
			label.setVisibility(View.VISIBLE);

			TextView price = (TextView) view.findViewById(R.id.Text_price);
			ViewHelper.setText(price, ViewHelper.formatPrice(beverage.getPrice()));
		} else {
			TextView price = (TextView) view.findViewById(R.id.label_price);
			price.setVisibility(View.GONE);
		}
		
		if (beverage.hasBottlesInCellar()) {
			TextView cellar = (TextView) view.findViewById(R.id.Text_cellar);
			cellar.setText(
					String.format(getString(R.string.bottles_in_cellar), 
							new Object[]{
						String.valueOf(beverage.getBottlesInCellar()),
						ViewHelper.formatPrice(beverage.getPrice() * beverage.getBottlesInCellar())}));
		}
		
		TextView usage = (TextView) view.findViewById(R.id.Text_usage);
		ViewHelper.setText(usage, beverage.getUsage());

		TextView taste = (TextView) view.findViewById(R.id.Text_taste);
		ViewHelper.setText(taste, beverage.getTaste());

		Provider p1 = beverage.getProvider();
		if (p1 != null) {
			TextView provider = (TextView) view.findViewById(R.id.Text_provider);
			ViewHelper.setText(provider, p1.getName());
		}

		TextView tv = (TextView) view.findViewById(R.id.Text_category);

		if(!viewHelper.isLightVersion(Integer.valueOf(getString(R.string.version_type)))) {
			ViewHelper.setText(tv, beverage.getCategory().getName());
		} else {
			TextView lbl = (TextView) view.findViewById(R.id.Text_category_lbl);
			
			lbl.setVisibility(View.GONE); 
			tv.setVisibility(View.GONE); 
		}

		
		RatingBar rating = (RatingBar) view.findViewById(R.id.ratingBar);
		rating.setRating(beverage.getRating());

		TextView added = (TextView) view.findViewById(R.id.Text_added);
		
		added.setText(ViewHelper.getDateAsString((beverage.getAdded() != null ? beverage.getAdded() : new Date())));
	}
	
	
	private class FacebookPostMessageTask extends AsyncTask<Void, Void, Void> {
	    
		@Override
		protected Void doInBackground(Void... params) {
			Bundle bundle = new Bundle();
			bundle.putString("picture", SystembolagetParser.BASE_URL + beverage.getThumb());
			bundle.putString("name", getString(R.string.recommend_beer_header));
			bundle.putString("link", SystembolagetParser.BASE_URL + "/" + beverage.getNo());
			
			StringBuilder builder = new StringBuilder(getString(R.string.recommend_beer));
			builder.append(" ").append(beverage.getName());
			
			if(beverage.getNo() != -1) {
				builder.append(" (" + beverage.getNo() + ")"); 
			}
			
			if(beverage.getRating() != -1) {
				builder.append(" ").append(getString(R.string.recommend_beer1)).append(" ");					
				builder.append(ViewHelper.getDecimalStringFromNumber(beverage.getRating())).append(" ").append(getString(R.string.recommend_beer2));
			}
			
			bundle.putString("description", builder.toString());

			connector.postMessageOnWall(bundle);
			facebookHandler.post(updateFacebookNotification);
			return null;
		}
		
	}

}
