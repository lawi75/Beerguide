package ws.wiklund.beerguide.util;

import ws.wiklund.beerguide.activities.BeerTabsActivity;
import ws.wiklund.beerguide.db.BeerDatabaseHelper;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.beerguide.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

public class GetBeerFromCursorTask extends AsyncTask<Cursor, Void, Beverage> {
	private ProgressDialog dialog;
	private Activity activity;

	public GetBeerFromCursorTask(Activity activity) {
		this.activity = activity;
	}

	@Override
	protected Beverage doInBackground(Cursor... cursors) {
		return new BeerDatabaseHelper(activity).getBeverageFromCursor(cursors[0]);
	}

	@Override
	protected void onPostExecute(Beverage bevarage) {
		dialog.hide();

		Intent intent = new Intent(activity, BeerTabsActivity.class);
		intent.putExtra("ws.wiklund.beerguide.activities.Beverage", bevarage);

		activity.startActivityForResult(intent, 0);

		super.onPostExecute(bevarage);
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(activity);
		dialog.setMessage(activity.getString(R.string.wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();

		super.onPreExecute();
	}

}
