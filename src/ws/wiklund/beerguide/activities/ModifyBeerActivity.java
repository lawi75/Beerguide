package ws.wiklund.beerguide.activities;

import java.util.ArrayList;
import java.util.Date;

import ws.wiklund.beerguide.db.BeerDatabaseHelper;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.model.BeverageType;
import ws.wiklund.guides.model.Category;
import ws.wiklund.guides.model.Country;
import ws.wiklund.guides.model.Producer;
import ws.wiklund.guides.model.Provider;
import ws.wiklund.guides.util.ViewHelper;
import ws.wiklund.beerguide.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class ModifyBeerActivity extends BaseActivity {
	private Beverage beverage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modifybeer);

        beverage = (Beverage) getIntent().getSerializableExtra("ws.wiklund.beerguide.activities.Beverage");

		Log.d(ModifyBeerActivity.class.getName(), "Beer: " + (beverage != null ? beverage.toString() : null));

		if (beverage != null) {
			setTitle(beverage.getName());
			populateUI();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.modify_beer_menu, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menuSaveBeer:
				Beverage b = getBeverageFromUI();
			   	BeerDatabaseHelper helper = new BeerDatabaseHelper(ModifyBeerActivity.this.getApplicationContext());
		    	helper.addBeverage(b);
		    	showBeerList();					
				return true;
			case R.id.menuCancel:
		    	showBeerList();
				return true;
		}
		
		return false;
	}	
	
    private void populateUI() {
    	viewHelper.setThumbFromUrl((ImageView) (findViewById(android.R.id.content).findViewById(R.id.Image_thumbUrl)), beverage.getThumb());
		TextView no = (TextView) findViewById(R.id.Text_no);
		no.setText(String.valueOf(beverage.getNo()));
		
		EditText name = (EditText) findViewById(R.id.Edit_name);
		ViewHelper.setText(name, beverage.getName());
		
		Spinner type = (Spinner) findViewById(R.id.Spinner_type);
		populateAndSetTypeSpinner(type, beerTypes.findTypeFromId(beverage.getBeverageTypeId()));
		
		Country c = beverage.getCountry();
		if (c != null) {
			viewHelper.setCountryThumbFromUrl((ImageView) (findViewById(android.R.id.content).findViewById(R.id.Image_country_thumbUrl)), c);
			EditText country = (EditText) findViewById(R.id.Edit_country);
			ViewHelper.setText(country, c.getName());
		}
		
		Spinner year = (Spinner) findViewById(R.id.Spinner_year);
		populateAndSetYearSpinner(year, beverage.getYear());
		
		Producer p = beverage.getProducer();
		if (p != null) {
			EditText producer = (EditText) findViewById(R.id.Edit_producer);
			ViewHelper.setText(producer, p.getName());
		}
		
		Spinner strength = (Spinner) findViewById(R.id.Spinner_strength);
		populateAndSetStrengthSpinner(strength, beverage.getStrength());
		
		EditText price = (EditText) findViewById(R.id.Edit_price);
		ViewHelper.setText(price, String.valueOf(beverage.getPrice()));

		EditText usage = (EditText) findViewById(R.id.Edit_usage);
		ViewHelper.setText(usage, beverage.getUsage());
		
		EditText taste = (EditText) findViewById(R.id.Edit_taste);
		ViewHelper.setText(taste, beverage.getTaste());
		
		Spinner category = (Spinner) findViewById(R.id.Spinner_category);
		if(!isLightVersion()) {
			populateAndSetCategorySpinner(category, beverage.getCategory());
		} else {
			TextView tv = (TextView) findViewById(R.id.Text_category);
			
			tv.setVisibility(View.GONE); 
			category.setVisibility(View.GONE); 
		}

		Provider p1 = beverage.getProvider();
		if (p1 != null) {
			EditText provider = (EditText) findViewById(R.id.Edit_provider);
			ViewHelper.setText(provider, p1.getName());
		}
		
		TextView added = (TextView) findViewById(R.id.Text_added);
		added.setText(ViewHelper.getDateAsString((beverage.getAdded() != null ? beverage.getAdded() : new Date())));
	}

	private void populateAndSetCategorySpinner(Spinner categorySpinner, Category category) {
		ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<Category>(getCategories()));
		categorySpinner.setAdapter(adapter);
		
		categorySpinner.setSelection(adapter.getPosition(category));
	}

	private void populateAndSetStrengthSpinner(Spinner strengthSpinner, double strength) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ViewHelper.getStrengths());
		strengthSpinner.setAdapter(adapter);
		
		strengthSpinner.setSelection(adapter.getPosition(ViewHelper.getDecimalStringFromNumber(strength) + " %"));
	}

	private void populateAndSetTypeSpinner(Spinner typeSpinner, BeverageType type) {
		ArrayAdapter<BeverageType> adapter = new ArrayAdapter<BeverageType>(this, android.R.layout.simple_spinner_dropdown_item, beerTypes.getAllBeverageTypes());
		typeSpinner.setAdapter(adapter);

		typeSpinner.setSelection(adapter.getPosition(type));
	}

	private void populateAndSetYearSpinner(Spinner yearSpinner, int year) {
		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, getYears());
		yearSpinner.setAdapter(adapter);
		
		if(year >= 1900 && year <= getCurrentYear()) {
			yearSpinner.setSelection(adapter.getPosition(year));
		}
	}

	private void showBeerList() {
		Intent intent;
        if(!isLightVersion()) {
    		intent = new Intent(getApplicationContext(), BeerListActivity.class);
        } else {
        	intent = new Intent(getApplicationContext(), FullAdActivity.class);

        	/* Removed PayPal for now
        	if (rand.nextInt(10) == 0) {
            	intent = new Intent(getApplicationContext(), DonateActivity.class);
            } else {
            	intent = new Intent(getApplicationContext(), FullAdActivity.class);
            }
            */
        }
        
    	startActivityForResult(intent, 0);
    	finish();
	}

    private Beverage getBeverageFromUI() {
    	String name = ((EditText) findViewById(R.id.Edit_name)).getText().toString();		
    	BeverageType type = (BeverageType) ((Spinner) findViewById(R.id.Spinner_type)).getSelectedItem();
		String country = ((EditText) findViewById(R.id.Edit_country)).getText().toString();
		
		int	year = (Integer) ((Spinner) findViewById(R.id.Spinner_year)).getSelectedItem();

		String producer = ((EditText) findViewById(R.id.Edit_producer)).getText().toString();
		String strength = getStrenghtFromSpinner();
		String price = ((EditText) findViewById(R.id.Edit_price)).getText().toString();
		String usage = ((EditText) findViewById(R.id.Edit_usage)).getText().toString();
		String taste = ((EditText) findViewById(R.id.Edit_taste)).getText().toString();
		String provider = ((EditText) findViewById(R.id.Edit_provider)).getText().toString();

		if(beverage == null) {
			beverage = new Beverage();

			String noStr = ((TextView) findViewById(R.id.Text_no)).getText().toString();
			if (noStr.length() > 0) {
				beverage.setNo(Integer.valueOf(noStr));
			}
    	}
		
		beverage.setName(name);
		beverage.setBeverageTypeId(type.getId());		
		beverage.setYear(year);
		beverage.setStrength(ViewHelper.getDoubleFromDecimalString(strength));
		
		if (price.length() > 0) {
			beverage.setPrice(ViewHelper.getDoubleFromDecimalString(price));
		}
		
		beverage.setUsage(usage);
		beverage.setTaste(taste);
		
		updateCountry(country);
		updateProducer(producer);
		updateProvider(provider);

		return beverage;
	}

	private void updateCountry(String country) {
		if(beverage != null && country.length() > 0) {
			Country c = beverage.getCountry();
			
			if(c == null || (!c.getName().equals(country))) {
				beverage.setCountry(new Country(country, null));
			}
		}
	}

	private void updateProducer(String producer) {
		if(beverage != null && producer.length() > 0) {
			Producer p = beverage.getProducer();
			
			if(p == null || (!p.getName().equals(producer))) {
				beverage.setProducer(new Producer(producer));
			}
		}
	}

	private void updateProvider(String provider) {
		if(beverage != null && provider.length() > 0) {
			Producer p = beverage.getProducer();
			
			if(p == null || (!p.getName().equals(provider))) {
				beverage.setProvider(new Provider(provider));
			}
		}
	}

	private String getStrenghtFromSpinner() {
		String s = (String) ((Spinner) findViewById(R.id.Spinner_strength)).getSelectedItem();
		return s.substring(0, s.indexOf(" "));
	}
        
}

