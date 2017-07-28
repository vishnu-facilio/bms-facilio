package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.context.Column.ColumnType;

public class ViewLayout {
	
	public ViewLayout()
	{
		
	}
	
	private List<Column> columns;
	
	public ViewLayout addColumn(Column column)
	{
		if(this.columns == null)
		{
			columns = new ArrayList<>();
		}
		columns.add(column);
		return this;
	}
	
	public List<Column> getColumns()
	{
		return this.columns;
	}
	
	private String pkColumnId;
	public ViewLayout setPkColumnId(String pkColumnId) 
	{
		this.pkColumnId = pkColumnId;
		return this;
	}
	public String getPkColumnId() 
	{
		return this.pkColumnId;
	}
	
	public static ViewLayout getViewTicketLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new Column("ID", "id", ColumnType.NUMBER));
		viewLayout.addColumn(new Column("Subject & Description", "", ColumnType.MULTICOLUMN)
							.addColumn(new Column("Subject", "subject", ColumnType.TEXT))
							.addColumn(new Column("Description", "description", ColumnType.TEXT))
							);
		viewLayout.addColumn(new Column("Status", "status", "status", ColumnType.LOOKUP));
		viewLayout.addColumn(new Column("Priority", "priority", "priority", ColumnType.LOOKUP));
		viewLayout.addColumn(new Column("Due Date", "duedate", ColumnType.DATETIME));
		viewLayout.addColumn(new Column("Requested By", "requester", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Assigned To", "assignedTo", "email", ColumnType.LOOKUP));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewTicketStatusLayout() {
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new Column("ID", "id", ColumnType.NUMBER));
		viewLayout.addColumn(new Column("Status", "status", ColumnType.TEXT));
		return viewLayout;
	}
	
	public static ViewLayout getViewTaskLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new Column("ID", "id", ColumnType.NUMBER));
		viewLayout.addColumn(new Column("Subject & Description", "", ColumnType.MULTICOLUMN)
							.addColumn(new Column("Subject", "subject", ColumnType.TEXT))
							.addColumn(new Column("Description", "description", ColumnType.TEXT))
							);
		viewLayout.addColumn(new Column("Status", "status", "status", ColumnType.LOOKUP));
		viewLayout.addColumn(new Column("Assigned To", "assignedToId", ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewTaskStatusLayout() {
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new Column("ID", "id", ColumnType.NUMBER));
		viewLayout.addColumn(new Column("Status", "status", ColumnType.TEXT));
		return viewLayout;
	}
	
	public static ViewLayout getViewCampusLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new Column("Name", "name", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Max Occupancy", "maxOccupancy", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Current Occupancy", "currentOccupancy", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Area", "area", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Managed By", "managedBy", ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewBuildingLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new Column("Name", "name", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Campus", "campusId", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Floors", "floors", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Max Occupancy", "maxOccupancy", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Current Occupancy", "currentOccupancy", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Area", "area", ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewFloorLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new Column("Name", "name", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Building", "buildingId", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Main Level", "mainLevel", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Max Occupancy", "maxOccupancy", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Current Occupancy", "currentOccupancy", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Area", "area", ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewSpaceLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new Column("Display Name", "displayName", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Name", "name", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Category", "spaceCategoryId", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Occupiable", "occupiable", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Max Occupancy", "maxOccupancy", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Current Occupancy", "currentOccupancy", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Area", "area", ColumnType.TEXT));
		
		return viewLayout;
	}

	public static ViewLayout getViewZoneLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new Column("Name", "name", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Short Description", "shortDescription", ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewSkillLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.addColumn(new Column("Name", "name", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Description", "description", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Status", "isActive", ColumnType.TEXT));
		
		return viewLayout;
	}
}

class Column
{
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
	
	public List<Column> getColumns()
	{
		return this.columns;
	}
	
	List<Column> columns;
	public Column addColumn(Column column)
	{
		if(this.columns == null)
		{
			columns = new ArrayList<>();
		}
		columns.add(column);
		return this;
	}
	
	ColumnType columnType;
	public Column(String label,String id, ColumnType columnType) {
		this(label, id, null, columnType);
	}
	
	public Column(String label,String id, String lookupId, ColumnType columnType) {
		this.label = label;
		this.id = id;
		this.lookupId = lookupId;
		this.columnType = columnType;
	}

	boolean showColumn = true;
	public Column setShowColumn(boolean showColumn) 
	{
		this.showColumn = showColumn;
		return this;
	}
	
	public boolean getShowColumn()
	{
		return this.showColumn;
	}
}