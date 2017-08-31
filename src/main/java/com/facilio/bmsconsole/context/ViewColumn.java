package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

public class ViewColumn {
	String label;
	String id;
	String lookupId;
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getLookupId() {
		return lookupId;
	}
	public void setLookupId(String lookupId) {
		this.lookupId = lookupId;
	}

	public ColumnType getColumnType()
	{
		return this.columnType;
	}

	public enum ColumnType {
		TEXT, NUMBER, DATETIME, DATE, MULTICOLUMN, LOOKUP
	}

	public List<ViewColumn> getColumns()
	{
		return this.columns;
	}

	List<ViewColumn> columns;
	public ViewColumn addColumn(ViewColumn column)
	{
		if(this.columns == null)
		{
			columns = new ArrayList<>();
		}
		columns.add(column);
		return this;
	}

	ColumnType columnType;
	public ViewColumn(String label,String id, ColumnType columnType) {
		this(label, id, null, columnType);
	}

	public ViewColumn(String label,String id, String lookupId, ColumnType columnType) {
		this.label = label;
		this.id = id;
		this.lookupId = lookupId;
		this.columnType = columnType;
	}

	boolean showColumn = true;
	public ViewColumn setShowColumn(boolean showColumn) 
	{
		this.showColumn = showColumn;
		return this;
	}

	public boolean getShowColumn()
	{
		return this.showColumn;
	}

	boolean isEditable = false;
	public ViewColumn setIsEditable(boolean isEditable) 
	{
		this.isEditable = isEditable;
		return this;
	}

	public boolean getIsEditable()
	{
		return this.isEditable;
	}
}