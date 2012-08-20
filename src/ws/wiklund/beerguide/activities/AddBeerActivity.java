package ws.wiklund.beerguide.activities;

import java.util.regex.Pattern;

import ws.wiklund.beerguide.R;
import ws.wiklund.beerguide.db.BeerDatabaseHelper;
import ws.wiklund.beerguide.util.BeerTypes;
import ws.wiklund.guides.activities.BaseActivity;
import ws.wiklund.guides.util.DownloadBeverageTask;
import android.content.Intent;
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
					searchWine(v);
					return true;
				}
				
				return false;
			}
		});
        
    }
    
    @Override
	protected void onPause() {
		super.onPause();
	}

	public void searchWine(View view) {    	
		search(searchStr.getText().toString());
    }
    
    private void search(String no) {
    	if(isValidNo(no)) {
    		new DownloadBeverageTask(this, ModifyBeerActivity.class , new BeerTypes()).execute(no);
    	}
    }
	
	public void addWineManually(View view) {    	
		Intent intent = new Intent(AddBeerActivity.this.getApplicationContext(), ModifyBeerActivity.class);
    	startActivityForResult(intent, 0);
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
    
}
