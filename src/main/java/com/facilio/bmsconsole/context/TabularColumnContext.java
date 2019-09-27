package com.facilio.bmsconsole.context;


public class TabularColumnContext {
	
	public String getColumnAlias() {
		return columnAlias;
	}

	public void setColumnAlias(String columnAlias) {
		this.columnAlias = columnAlias;
	}

	public String columnAlias;
	
	public String getColumnlabel() {
		return columnLabel;
	}

	public void setColumnlabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}

	public String columnLabel;
	
	public int order;
		
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

public DisplayType displayType;

	public DisplayType getDisplayType() {
	return displayType;
}

public void setDisplayType(DisplayType displayType) {
	this.displayType = displayType;
}

	public enum DisplayType {
		VALUE (1),
		PERCENTAGE (2),
		PROGRESS (3);
		
		private int type;
		
		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		DisplayType(int type) {
			this.type = type;
		}
		
	}

	boolean hideColumn;

	public boolean isHideColumn() {
		return hideColumn;
	}

	public void setHideColumn(boolean hideColumn) {
		this.hideColumn = hideColumn;
	}


}
