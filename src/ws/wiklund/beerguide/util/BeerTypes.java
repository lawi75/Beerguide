package ws.wiklund.beerguide.util;

import java.util.ArrayList;
import java.util.List;

import ws.wiklund.guides.model.BeverageType;
import ws.wiklund.guides.model.BeverageTypes;

public class BeerTypes implements BeverageTypes {
	private static final List<BeverageType> types = new ArrayList<BeverageType>();
	private static final BeerType OTHER = new BeerType(999, "�vriga");

	static {
		if(types.isEmpty()) {
			types.add(new BeerType(100, "�l, Ljus lager"));
			types.add(new BeerType(200, "�l, M�rk lager"));
			types.add(new BeerType(300, "�l, Porter och Stout"));
			types.add(new BeerType(400, "�l, Ale"));
			types.add(new BeerType(500, "�l, Vete�l"));
			types.add(new BeerType(600, "�l, Special�l"));
			types.add(new BeerType(600, "�l, Spontanj�st �l"));
			types.add(OTHER);
		}
	}
	
	@Override
	public List<BeverageType> getAllBeverageTypes() {
		return types;
	}
	
	@Override
	public BeverageType findTypeFromId(int id){
		for(BeverageType type : types) {
			if(type.getId() == id) {
				return type;
			}
		}
		
		return null;
	}

	@Override
	public BeverageType findTypeFromString(String text) {
		for(BeverageType type : types) {
			if(type.getName().equals(text)) {
				return type;
			}
		}
		
		return OTHER;
	}

	@Override
	public boolean useSubTypes() {
		return true;
	}
	
}
