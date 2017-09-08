package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

public class ViewLayout {
	
	public ViewLayout()
	{
		
	}
	
	private List<ViewColumn> columns;
	
	public ViewLayout addColumn(ViewColumn column)
	{
		if(this.columns == null)
		{
			columns = new ArrayList<>();
		}
		columns.add(column);
		return this;
	}
	
	public List<ViewColumn> getColumns()
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
		viewLayout.addColumn(new ViewColumn("ID", "id", ViewColumn.ColumnType.NUMBER));
		viewLayout.addColumn(new ViewColumn("Subject & Description", "", ViewColumn.ColumnType.MULTICOLUMN)
							.addColumn(new ViewColumn("Subject", "subject", ViewColumn.ColumnType.TEXT))
							.addColumn(new ViewColumn("Description", "description", ViewColumn.ColumnType.TEXT))
							);
		viewLayout.addColumn(new ViewColumn("Status", "status", "status", ViewColumn.ColumnType.LOOKUP));
		viewLayout.addColumn(new ViewColumn("Priority", "priority", "priority", ViewColumn.ColumnType.LOOKUP));
		viewLayout.addColumn(new ViewColumn("Due Date", "duedate", ViewColumn.ColumnType.DATETIME));
		viewLayout.addColumn(new ViewColumn("Assigned To", "assignedTo", "email", ViewColumn.ColumnType.LOOKUP));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewWorkOrderLayout()
	{
		ViewLayout viewLayout = getViewTicketLayout();
		viewLayout.addColumn(new ViewColumn("Requester", "requester", "email", ViewColumn.ColumnType.LOOKUP));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewTaskLayout()
	{
		ViewLayout viewLayout = getViewTicketLayout();
//		viewLayout.addColumn(new ViewColumn("Work Order", "requester", ViewColumn.ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewTicketStatusLayout() {
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new ViewColumn("ID", "id", ViewColumn.ColumnType.NUMBER));
		viewLayout.addColumn(new ViewColumn("Status", "status", ViewColumn.ColumnType.TEXT));
		return viewLayout;
	}
	
//	public static ViewLayout getViewTaskLayout()
//	{
//		ViewLayout viewLayout = new ViewLayout();
//		viewLayout.setPkColumnId("id");
//		viewLayout.addColumn(new ViewColumn("ID", "id", ViewColumn.ColumnType.NUMBER));
//		viewLayout.addColumn(new ViewColumn("Subject & Description", "", ViewColumn.ColumnType.MULTICOLUMN)
//							.addColumn(new ViewColumn("Subject", "subject", ViewColumn.ColumnType.TEXT))
//							.addColumn(new ViewColumn("Description", "description", ViewColumn.ColumnType.TEXT))
//							);
//		viewLayout.addColumn(new ViewColumn("Status", "status", "status", ViewColumn.ColumnType.LOOKUP));
//		viewLayout.addColumn(new ViewColumn("Assigned To", "assignedToId", ViewColumn.ColumnType.TEXT));
//		
//		return viewLayout;
//	}
	
	public static ViewLayout getViewTaskStatusLayout() {
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new ViewColumn("ID", "id", ViewColumn.ColumnType.NUMBER));
		viewLayout.addColumn(new ViewColumn("Status", "status", ViewColumn.ColumnType.TEXT));
		return viewLayout;
	}
	
	public static ViewLayout getViewCampusLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new ViewColumn("Name", "name", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Max Occupancy", "maxOccupancy", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Current Occupancy", "currentOccupancy", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Area", "area", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Managed By", "managedBy", ViewColumn.ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewLocationLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new ViewColumn("Name", "name", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Contact", "contact", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Phone", "phone", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Lat", "lat", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Lng", "lng", ViewColumn.ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewBuildingLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new ViewColumn("Name", "name", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Campus", "campus", "name", ViewColumn.ColumnType.LOOKUP));
		viewLayout.addColumn(new ViewColumn("Floors", "floors", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Max Occupancy", "maxOccupancy", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Current Occupancy", "currentOccupancy", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Area", "area", ViewColumn.ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewFloorLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new ViewColumn("Name", "name", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Building", "building", "name", ViewColumn.ColumnType.LOOKUP));
		viewLayout.addColumn(new ViewColumn("Main Level", "mainLevel", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Max Occupancy", "maxOccupancy", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Current Occupancy", "currentOccupancy", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Area", "area", ViewColumn.ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewSpaceLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new ViewColumn("Display Name", "displayName", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Name", "name", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Category", "spaceCategoryId", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Occupiable", "occupiable", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Max Occupancy", "maxOccupancy", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Current Occupancy", "currentOccupancy", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Area", "area", ViewColumn.ColumnType.TEXT));
		
		return viewLayout;
	}

	public static ViewLayout getViewZoneLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new ViewColumn("Name", "name", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Short Description", "shortDescription", ViewColumn.ColumnType.TEXT));
		
		return viewLayout;
	}
	
	public static ViewLayout getViewSkillLayout()
	{
		ViewLayout viewLayout = new ViewLayout();
		viewLayout.setPkColumnId("id");
		viewLayout.addColumn(new ViewColumn("Name", "name", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Description", "description", ViewColumn.ColumnType.TEXT));
		viewLayout.addColumn(new ViewColumn("Status", "isActive", ViewColumn.ColumnType.TEXT));
		
		return viewLayout;
	}
}