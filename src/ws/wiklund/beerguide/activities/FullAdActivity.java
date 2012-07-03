package ws.wiklund.beerguide.activities;

import ws.wiklund.beerguide.R;
import android.os.Bundle;
import android.view.Window;

public class FullAdActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.fullad);
		
	}

}
