package ws.wiklund.beerguide.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ws.wiklund.beerguide.R;
import ws.wiklund.beerguide.db.BeerDatabaseHelper;
import ws.wiklund.beerguide.util.BeerTypes;
import ws.wiklund.guides.model.Category;
import ws.wiklund.guides.util.PayPalFactory;
import ws.wiklund.guides.util.ViewHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow.LayoutParams;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;

public class BaseActivity extends Activity {
	private Calendar calendar = Calendar.getInstance();
	private List<Integer> years = new ArrayList<Integer>();
	
	private static Set<Category> categories = new HashSet<Category>();

	private BeerDatabaseHelper helper;
	protected ViewHelper viewHelper;
	protected BeerTypes beerTypes;
	

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
		helper = new BeerDatabaseHelper(this);
		viewHelper = new ViewHelper();
		
		beerTypes = new BeerTypes();

		if (!isLightVersion()) {
			View ad = findViewById(R.id.adView);
			if(ad != null) {
				ad.setVisibility(View.GONE);
			}
			
			View ad1 = findViewById(R.id.adView1);
			if(ad1 != null) {
				ad1.setVisibility(View.GONE);
			}
		}
		
    }
    
    public void skip(View view) {    	
    	Intent intent = new Intent(getApplicationContext(), BeerListActivity.class);
    	startActivityForResult(intent, 0);
    	finish();
    }

    protected boolean isLightVersion() {
    	return viewHelper.isLightVersion(Integer.valueOf(getString(R.string.version_type)));
    }
    
	protected synchronized Set<Category> getCategories() {
		if(categories.isEmpty()) {
			categories.add(new Category(""));
			categories.add(new Category(Category.NEW_ID, getString(R.string.newStr)));
		}
		
		List<Category> c = helper.getCategories();
		
		if (c != null && !c.isEmpty()) {
			categories.addAll(c);
		}
		
		//TODO possibility to remove categories
		//TODO dialog if new is selected
		
		return categories;
	}
	
	protected CheckoutButton getCheckoutButton() {
		return getCheckoutButton(PayPal.BUTTON_152x33);
	}
	
	protected CheckoutButton getCheckoutButton(int btnSize) {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.span = 2;
		params.topMargin = 10;
		
		PayPal payPal = PayPalFactory.getPayPal();
		
		CheckoutButton btn = null;
		if (payPal != null) {
			btn = payPal.getCheckoutButton(this, btnSize,
					CheckoutButton.TEXT_DONATE);
			btn.setLayoutParams(params);
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					donate();
				}
			});
		}
		
		return btn;
	}
	
	public int getCurrentYear() {
		return calendar.get(Calendar.YEAR);
	}
	
	public synchronized List<Integer> getYears() {
		if(years.isEmpty()) {
			for(int i = 1900; i<= calendar.get(Calendar.YEAR); i++) {
				years.add(i);
			}	
		}
		
		return years;
	}

	private void donate() {
		/*
		NumberPickerDialog pickerDialog = new NumberPickerDialog(this, -1, 20, R.string.dialog_set_number, true);
		pickerDialog.setTitle(getString(R.string.donateTitle));
		pickerDialog.setOnNumberSetListener(new OnNumberSetListener() {
			@Override
			public void onNumberSet(int selectedNumber) {
				PayPalPayment payment = new PayPalPayment();

				payment.setSubtotal(new BigDecimal(selectedNumber));

				payment.setCurrencyType("SEK");

				payment.setRecipient("beerguide@wiklund.ws");

				payment.setPaymentType(PayPal.PAYMENT_TYPE_PERSONAL);

				Intent checkoutIntent = PayPal.getInstance().checkout(payment, BaseActivity.this);

				startActivityForResult(checkoutIntent, 1);					
			}
		});
		
		pickerDialog.show();
		*/			
    }
	
}
