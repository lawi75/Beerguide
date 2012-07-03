package ws.wiklund.beerguide.activities;

import ws.wiklund.beerguide.db.BeerDatabaseHelper;
import ws.wiklund.beerguide.util.GetBeerFromCursorTask;
import ws.wiklund.beerguide.util.SelectableImpl;
import ws.wiklund.guides.bolaget.SystembolagetParser;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.util.BitmapManager;
import ws.wiklund.guides.util.CoverFlow;
import ws.wiklund.guides.util.Notifyable;
import ws.wiklund.guides.util.Selectable;
import ws.wiklund.guides.util.SelectableAdapter;
import ws.wiklund.guides.util.ViewHolder;
import ws.wiklund.beerguide.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class BeerFlowActivity extends BaseActivity implements Notifyable {
	private CoverFlowAdapter adapter;
	private SelectableAdapter selectableAdapter;
	private BeerDatabaseHelper helper;
	private int currentPosition;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
			startActivityForResult(new Intent(getApplicationContext(), BeerListActivity.class), 0);
		}
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.beerflow);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);

		helper = new BeerDatabaseHelper(this);			
		
		final CoverFlow flow = (CoverFlow) findViewById(R.id.coverFlow);
		adapter = new CoverFlowAdapter(this);

		flow.setAdapter(adapter);

		flow.setSpacing(-25);
		flow.setSelection(adapter.getOptimalSelection(), true);
		flow.setAnimationDuration(1000);
		
		flow.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				handleLongClick(position);
				return true;
			}
		});
		
		flow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				new GetBeerFromCursorTask(BeerFlowActivity.this).execute(adapter.getItem(position));
			}
			
		});
		
		selectableAdapter = new SelectableAdapter(this, R.layout.spinner_row, getLayoutInflater()){
			public boolean isAvailableInCellar() {
				final Beverage b = helper.getBeverageFromCursor(adapter.getItem(currentPosition));
				return b.hasBottlesInCellar();
			}
		};
		
		selectableAdapter.add(new SelectableImpl(getString(R.string.addToCellar), R.drawable.icon, Selectable.ADD_ACTION));
		selectableAdapter.add(new SelectableImpl(getString(R.string.removeFromCellar), R.drawable.from_cellar, Selectable.REMOVE_ACTION));
		selectableAdapter.add(new SelectableImpl(getString(R.string.deleteTitle), R.drawable.trash, Selectable.DELETE_ACTION));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.beer_list_menu, menu);

		return true;
	}
		
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menuStats).setEnabled(hasSomeStats());
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuStats:
			startActivityForResult(new Intent(BeerFlowActivity.this.getApplicationContext(), StatsActivity.class), 0);
			break;
		case R.id.menuAbout:
			startActivityForResult(new Intent(BeerFlowActivity.this.getApplicationContext(), AboutActivity.class), 0);
			break;
		}

		return true;
	}
	
	public void addBeer(View view) {
    	Intent intent = new Intent(view.getContext(), AddBeerActivity.class);
    	startActivityForResult(intent, 0);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		notifyDataSetChanged();
	}

	private boolean hasSomeStats() {
		return adapter.getCount() > 0;
	}
	
	private void handleLongClick(final int position) {
		currentPosition = position;
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		
		final Beverage b = helper.getBeverageFromCursor(adapter.getItem(position));
		alertDialog.setTitle(b != null ? b.getName() : "");
		
		alertDialog.setSingleChoiceItems( selectableAdapter, 0, new OnClickListener() { 
            @Override 
            public void onClick(DialogInterface dialog, int which) { 
                dialog.dismiss();
                ((SelectableImpl) selectableAdapter.getItem(which)).select(BeerFlowActivity.this, helper, b.getId(), b.getName());
            }
		}); 

		alertDialog.show(); 				
	}	

	public void notifyDataSetChanged() {
		int bottles = helper.getNoBottlesInCellar();
		// Update title with no beers in cellar
		if (bottles > 0) {
			TextView view = (TextView) BeerFlowActivity.this.findViewById(R.id.title);

			String text = view.getText().toString();
			if (text.contains("(")) {
				text = text.substring(0, text.indexOf("(") - 1);
			}

			view.setText(text + " (" + bottles + ")");
		}
		
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onDestroy() {
		adapter.destroy();
		super.onDestroy();
	}
	

	private class CoverFlowAdapter extends BaseAdapter {
		private LayoutInflater inflator;
		private SQLiteDatabase db;
		private Cursor cursor;

		public CoverFlowAdapter(Context c) {
			inflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			BitmapManager.INSTANCE.setPlaceholder(BitmapFactory.decodeResource(c.getResources(), R.drawable.icon));
		}

		public int getOptimalSelection() {
			int c = getCount();

			if(c > 4) {
				return 4;
			} else if(c > 0) {
				return c - 1;
			}

			return 0;
		}

		public void destroy() {
			if (cursor != null) {
				cursor.close();
			}

			if (db != null) {
				db.close();
			}
		}

		public int getCount() {
			return getNewOrReuseCursor().getCount();
		}

		public Cursor getItem(int position) {
			Cursor c = getNewOrReuseCursor();
			
			if (c.moveToPosition(position)) {
				return c;
			}

			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if (convertView == null) {  
				convertView = inflator.inflate(R.layout.coveritem, null);
				
				TextView titleView = (TextView) convertView.findViewById(R.id.itemTitle);  
		        TextView textView = (TextView) convertView.findViewById(R.id.itemText);  
		        TextView typeView = (TextView) convertView.findViewById(R.id.itemType);  
		        ImageView imageView = (ImageView) convertView.findViewById(R.id.itemImage);
		        RatingBar rating = (RatingBar) convertView.findViewById(R.id.itemRatingBar);

		         
		        holder = new ViewHolder();  
		        holder.titleView = titleView;  
		        holder.textView = textView;  
		        holder.imageView = imageView;
		        holder.rating = rating;
		        holder.typeView = typeView;
		         
		        convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag(); 
			}
			
			Cursor c = getItem(position);

			if (c != null) {
				int noBottles = c.getInt(22); 
				StringBuilder name = new StringBuilder(c.getString(1));
				
				if(noBottles > 0) {
					name.append("(").append(c.getInt(22)).append(")");
				}
						
				holder.titleView.setText(name.toString());
				holder.typeView.setText(beerTypes.findTypeFromId(c.getInt(3)).toString());
				
				int year = c.getInt(8); 
				holder.textView.setText(c.getString(6) + " " + (year != -1 ? year : ""));
				String url = SystembolagetParser.BASE_URL + c.getString(4);
				holder.rating.setRating(c.getFloat(16));
				holder.imageView.setTag(url);
				BitmapManager.INSTANCE.loadBitmap(url, holder.imageView, 50, 100);
			}
			
			return convertView;
		}

		private Cursor getNewOrReuseCursor() {
			if (db == null || !db.isOpen()) {
				db = helper.getReadableDatabase();
				cursor = db.rawQuery(BeerDatabaseHelper.SQL_SELECT_ALL_BEVERAGES_INCLUDING_NO_IN_CELLAR, null);
			}
			
			return cursor;
		}

	}

}




