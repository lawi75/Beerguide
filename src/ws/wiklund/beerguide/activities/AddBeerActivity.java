package ws.wiklund.beerguide.activities;

import java.io.IOException;
import java.util.regex.Pattern;

import ws.wiklund.beerguide.R;
import ws.wiklund.beerguide.db.BeerDatabaseHelper;
import ws.wiklund.guides.bolaget.SystembolagetParser;
import ws.wiklund.guides.model.Beverage;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Toast;

public class AddBeerActivity extends BaseActivity {
	private BeerDatabaseHelper helper;
	private EditText searchStr;
	
	private ProgressDialog dialog;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addbeer);
        
        helper = new BeerDatabaseHelper(this);
        searchStr = (EditText)findViewById(R.id.EditNo);
        
        searchStr.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER  && event.getAction() == 0) {
					searchBeer(v);
					return true;
				}
				
				return false;
			}
		});
        
    }
    
    @Override
	protected void onPause() {
    	if(dialog != null && dialog.isShowing()) {
    		dialog.dismiss();
    	}
    	
		super.onPause();
	}

	public void searchBeer(View view) {    	
		search(searchStr.getText().toString());
    }
    
    public void search(String no) {
    	if(isValidNo(no)) {
    		new DownloadBeerTask().execute(no);
    	}
    }
	
	private boolean isValidNo(String no) {
		if(no != null && no.length() > 0 && Pattern.matches("^\\d*$", no)) {
			try {
				if(!exists(no)) {
					return true;
				} else {				
					Toast.makeText(getApplicationContext(), getString(R.string.beerExist) + " " + no, Toast.LENGTH_SHORT).show();  		
				}
			} catch (NumberFormatException e) {
	        	Log.d(AddBeerActivity.class.getName(), "Invalid search string: " + no);		        	
				Toast.makeText(getApplicationContext(), String.format(getString(R.string.invalidNoError), no), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.provideNo), Toast.LENGTH_SHORT).show();  		
		}
		
		return false;
	}

	private boolean exists(String no) throws NumberFormatException {
		return helper.getBeverageIdFromNo(Integer.valueOf(no)) != -1;
	}


	private class DownloadBeerTask extends AsyncTask<String, Void, Beverage> {
		private String no;
		
		private String errorMsg;

		@Override
		protected Beverage doInBackground(String... no) {
			this.no = no[0];

	        try {
				if(this.no == null) {
		        	Log.w(AddBeerActivity.class.getName(), "Failed to get info for beer,  no is null");		        	
		        	errorMsg = getString(R.string.genericParseError);
				} else {
					return SystembolagetParser.parseResponse(this.no, beerTypes);
				}
			} catch (IOException e) {
	        	Log.w(AddBeerActivity.class.getName(), "Failed to get info for beer with no: " + this.no, e);
	        	errorMsg = getString(R.string.genericParseError);
			}

	        return null;
		}

		@Override
		protected void onPostExecute(Beverage beverage) {
			Intent intent = new Intent(AddBeerActivity.this.getApplicationContext(), ModifyBeerActivity.class);

			if (beverage != null) {
				intent.putExtra("ws.wiklund.beerguide.activities.Beverage", beverage);
		    	startActivityForResult(intent, 0);
			} else {
				Toast.makeText(getApplicationContext(), errorMsg == null ? String.format(getString(R.string.missingNoError), this.no) : errorMsg, Toast.LENGTH_SHORT).show();
				errorMsg = null;
				dialog.dismiss();
			}
			
			super.onPostExecute(beverage);
		}

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(AddBeerActivity.this);
			dialog.setMessage("Vänligen vänta...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();

			super.onPreExecute();
		}
		
	}
    
}
