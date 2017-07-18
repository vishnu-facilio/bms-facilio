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
	
	public static ViewLayout getViewTicketLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.addColumn(new Column("ID", "ticketId", ColumnType.NUMBER).setIsPrimaryColumn(true));
		viewLayout.addColumn(new Column("Subject & Description", "", ColumnType.MULTICOLUMN)
							.addColumn(new Column("Subject", "subject", ColumnType.TEXT))
							.addColumn(new Column("Description", "description", ColumnType.TEXT))
							);
		viewLayout.addColumn(new Column("Status", "status", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Priority", "priority", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Due Date", "duedate", ColumnType.DATETIME));
		viewLayout.addColumn(new Column("Requested By", "requester", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Assigned To", "agentId", ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewTaskLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.addColumn(new Column("ID", "taskId", ColumnType.NUMBER).setIsPrimaryColumn(true));
		viewLayout.addColumn(new Column("Subject & Description", "", ColumnType.MULTICOLUMN)
							.addColumn(new Column("Subject", "subject", ColumnType.TEXT))
							.addColumn(new Column("Description", "description", ColumnType.TEXT))
							);
		viewLayout.addColumn(new Column("Assigned To", "assignedToId", ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewCampusLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.addColumn(new Column("ID", "campusId", ColumnType.NUMBER).setIsPrimaryColumn(true));
		viewLayout.addColumn(new Column("Name", "name", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Current Occupancy", "currentOccupancy", ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewBuildingLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.addColumn(new Column("ID", "buildingId", ColumnType.NUMBER).setIsPrimaryColumn(true));
		viewLayout.addColumn(new Column("Name", "name", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Current Occupancy", "currentOccupancy", ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewFloorLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.addColumn(new Column("ID", "floorId", ColumnType.NUMBER).setIsPrimaryColumn(true));
		viewLayout.addColumn(new Column("Name", "name", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Current Occupancy", "currentOccupancy", ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewSpaceLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.addColumn(new Column("ID", "spaceId", ColumnType.NUMBER).setIsPrimaryColumn(true));
		viewLayout.addColumn(new Column("Name", "name", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Current Occupancy", "currentOccupancy", ColumnType.TEXT));
		
		return viewLayout;
	}

	public static ViewLayout getViewZoneLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.addColumn(new Column("ID", "zoneId", ColumnType.NUMBER).setIsPrimaryColumn(true));
		viewLayout.addColumn(new Column("Name", "name", ColumnType.TEXT));
		viewLayout.addColumn(new Column("Short Description", "shortDescription", ColumnType.TEXT));
		
		return viewLayout;
	}
}
class Column
{
	String label;
	String id;
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
	
	boolean isPrimaryColumn = false;
	public Column setIsPrimaryColumn(boolean isPrimaryColumn) 
	{
		this.isPrimaryColumn = isPrimaryColumn;
		return this;
	}
	public boolean getIsPrimaryColumn() 
	{
		return this.isPrimaryColumn;
	}
	
	public ColumnType getColumnType()
	{
		return this.columnType;
	}
	
	public enum ColumnType {
	    TEXT, NUMBER, DATETIME, DATE, MULTICOLUMN
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
		this.label = label;
		this.id = id;
		this.columnType = columnType;
	}
}