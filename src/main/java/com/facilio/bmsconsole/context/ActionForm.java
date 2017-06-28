package com.facilio.bmsconsole.context;

import java.util.List;
import java.util.ArrayList;


public class ActionForm {
	static
	{
		System.out.println("The class is actionform");
	}
	public List< String> getLocations() {
		return locations;
	}
	public void setLocations(List< String> locations) {
		this.locations = locations;
	}
	public List< String> getCategories() {
		return categories;
	}
	public void setCategories(List< String> categories) {
		this.categories = categories;
	}
	List<String> locations =new ArrayList<String>();
	List<String> categories =new ArrayList<String>();
//	Map<Long,String> categories ;

	
	

}
