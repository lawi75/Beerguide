package ws.wiklund.beerguide.util;

import java.util.ArrayList;
import java.util.List;

import ws.wiklund.guides.model.BeverageType;
import ws.wiklund.guides.model.BeverageTypes;

public class BeerTypes implements BeverageTypes {
	private static final long serialVersionUID = 3307688919567492900L;
	private static final List<BeverageType> types = new ArrayList<BeverageType>();

	static {
		if(types.isEmpty()) {
			types.add(new BeverageType(100, "Öl, Ljus lager"));
			types.add(new BeverageType(200, "Öl, Mörk lager"));
			types.add(new BeverageType(300, "Öl, Porter och Stout"));
			types.add(new BeverageType(400, "Öl, Ale"));
			types.add(new BeverageType(500, "Öl, Veteöl"));
			types.add(new BeverageType(600, "Öl, Specialöl"));
			types.add(new BeverageType(600, "Öl, Spontanjäst öl"));
			types.add(BeverageType.OTHER);
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
		
		return BeverageType.OTHER;
	}

	@Override
	public boolean useSubTypes() {
		return true;
	}
	
}
