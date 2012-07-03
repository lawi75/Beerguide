package ws.wiklund.beerguide.util;

import ws.wiklund.guides.model.BeverageType;

public class BeerType implements BeverageType{
	private int id = -1;
	private String name = null;

	public BeerType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}
