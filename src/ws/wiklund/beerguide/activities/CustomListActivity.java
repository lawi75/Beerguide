package ws.wiklund.beerguide.activities;

import java.util.ArrayList;
import java.util.List;

import ws.wiklund.beerguide.R;
import ws.wiklund.beerguide.util.SelectableImpl;
import ws.wiklund.beerguide.util.BeerTypes;
import ws.wiklund.guides.util.Selectable;
import ws.wiklund.guides.util.SelectableAdapter;
import ws.wiklund.guides.util.Sortable;
import ws.wiklund.guides.util.ViewHelper;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class CustomListActivity extends ListActivity {
	private List<Sortable> sortableItems;

	private SortableAdapter sortableAdapter;
	private SelectableAdapter selectableAdapter;

	protected ViewHelper viewHelper;
	protected BeerTypes beerTypes;

	private int currentPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.beerlist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
		
		beerTypes = new BeerTypes();
		viewHelper = new ViewHelper();

		if (!viewHelper.isLightVersion(Integer.valueOf(getString(R.string.version_type)))) {
			View ad = findViewById(R.id.adView);
			if(ad != null) {
				ad.setVisibility(View.GONE);
			}
			
			View ad1 = findViewById(R.id.adView1);
			if(ad1 != null) {
				ad1.setVisibility(View.GONE);
			}
		}

		sortableItems = new ArrayList<Sortable>();
        
        sortableItems.add(new Sortable(
        		getString(R.string.sortOnName), 
        		getString(R.string.sortOnNameSub), 
        		R.drawable.descending, 
        		"beverage.name asc"));

        sortableItems.add(new Sortable(
        		getString(R.string.sortOnRank), 
        		getString(R.string.sortOnRankSub), 
        		R.drawable.rating, 
        		"beverage.rating desc"));

        sortableItems.add(new Sortable(
        		getString(R.string.sortOnType), 
        		getString(R.string.sortOnTypeSub), 
        		R.drawable.icon, 
        		"beverage.type asc"));
        
        if(!viewHelper.isLightVersion(Integer.valueOf(getString(R.string.version_type)))) {
            sortableItems.add(new Sortable(
            		getString(R.string.sortOnCategory), 
            		getString(R.string.sortOnCategorySub), 
            		R.drawable.category, 
            		"category.name asc"));
        }
        
		sortableAdapter = new SortableAdapter(this, R.layout.spinner_row);
		
		selectableAdapter = new SelectableAdapter(this, R.layout.spinner_row, getLayoutInflater()){
			public boolean isAvailableInCellar() {
				Cursor cursor = (Cursor) getListView().getItemAtPosition(currentPosition);
				return cursor.getInt(22) > 0;
			}
		};
		
		selectableAdapter.add(new SelectableImpl(getString(R.string.addToCellar), R.drawable.icon, Selectable.ADD_ACTION));
		selectableAdapter.add(new SelectableImpl(getString(R.string.removeFromCellar), R.drawable.from_cellar, Selectable.REMOVE_ACTION));
		selectableAdapter.add(new SelectableImpl(getString(R.string.deleteTitle), R.drawable.trash, Selectable.DELETE_ACTION));
	}
	
	public void addBeer(View view) {
    	Intent intent = new Intent(view.getContext(), AddBeerActivity.class);
    	startActivityForResult(intent, 0);
    }

	public void sortList(View view) {
		AlertDialog.Builder sortingDialog = new AlertDialog.Builder(this); 
		sortingDialog.setTitle(R.string.sort_list);
		sortingDialog.setSingleChoiceItems( sortableAdapter, 0, new OnClickListener() { 
	        @Override 
	        public void onClick(DialogInterface dialog, int which) { 
                dialog.dismiss();
                sort(sortableItems.get(which));
            }
        }); 

		sortingDialog.show(); 
	}
	
	protected void handleLongClick(final int position) {
		currentPosition = position;
		
		Cursor cursor = (Cursor) getListView().getItemAtPosition(position);
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(cursor.getString(1));
		
		alertDialog.setSingleChoiceItems( selectableAdapter, 0, new OnClickListener() { 
            @Override 
            public void onClick(DialogInterface dialog, int which) { 
                dialog.dismiss();
                select(selectableAdapter.getItem(which), position);
            }
		}); 

		alertDialog.show(); 				
	}	

	abstract void sort(Sortable sortable);
	abstract void select(Selectable selectable, int position);
	
	
	class SortableAdapter extends ArrayAdapter<Sortable>{
		
		public SortableAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId, sortableItems);
		}
		     
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView, ViewGroup parent) {
			Sortable s = sortableItems.get(position);
			
			LayoutInflater inflater=getLayoutInflater();
			View row=inflater.inflate(R.layout.spinner_row, parent, false);
			TextView label=(TextView)row.findViewById(R.id.spinner_header);
			label.setText(s.getHeader());
			TextView sub=(TextView)row.findViewById(R.id.spinner_sub);
			sub.setText(s.getSub());
			ImageView icon=(ImageView)row.findViewById(R.id.spinner_image);
			icon.setImageResource(s.getDrawable());
		    
			return row;		    
		}
		
	}

}