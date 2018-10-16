package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class AssetTypeContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private Boolean movable;
	public Boolean getMovable() {
		return movable;
	}
	public void setMovable(boolean movable) {
		this.movable = movable;
	}
	public boolean isMovable() {
		if(movable != null) {
			return movable.booleanValue();
		}
		return false;
	}
}
