package ws.wiklund.beerguide.activities;

import ws.wiklund.beerguide.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class BeerTabsActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.tabs);
    }
    
}
