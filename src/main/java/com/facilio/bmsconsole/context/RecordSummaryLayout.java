package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

public class RecordSummaryLayout {
	
	public RecordSummaryLayout()
	{
		
	}
	
	private String titleViewColumnId;
	public RecordSummaryLayout setTitleViewColumnId(String titleViewColumnId) 
	{
		this.titleViewColumnId = titleViewColumnId;
		return this;
	}
	public String getTitleViewColumnId() 
	{
		return this.titleViewColumnId;
	}
	
	private boolean hasProgressBar = true;;
	public RecordSummaryLayout setHasProgressBar(boolean hasProgressBar) 
	{
		this.hasProgressBar = hasProgressBar;
		return this;
	}
	public boolean getHasProgressBar() 
	{
		return this.hasProgressBar;
	}
	
	private String recordTitleViewColumnId;
	public RecordSummaryLayout setRecordTitleViewColumnId(String recordTitleViewColumnId) 
	{
		this.recordTitleViewColumnId = recordTitleViewColumnId;
		return this;
	}
	public String getRecordTitleViewColumnId() 
	{
		return this.recordTitleViewColumnId;
	}
	
	private String pkViewColumnId;
	public RecordSummaryLayout setPkViewColumnId(String pkViewColumnId) 
	{
		this.pkViewColumnId = pkViewColumnId;
		return this;
	}
	public String getPkViewColumnId() 
	{
		return this.pkViewColumnId;
	}
	
	List<RelatedModule> relatedModules;
	public RecordSummaryLayout addRelatedModule(RelatedModule relatedModule)
	{
		if(this.relatedModules == null)
		{
			relatedModules = new ArrayList<>();
		}
		relatedModules.add(relatedModule);
		return this;
	}
	
	public List<RelatedModule> getRelatedModules()
	{
		return this.relatedModules;
	}
	
	List<ViewColumn> ViewColumns;
	public RecordSummaryLayout addViewColumn(ViewColumn ViewColumn)
	{
		if(this.ViewColumns == null)
		{
			ViewColumns = new ArrayList<>();
		}
		ViewColumns.add(ViewColumn);
		return this;
	}
	
	public List<ViewColumn> getViewColumns()
	{
		return this.ViewColumns;
	}

	public static RecordSummaryLayout getRecordSummaryTicketLayout()
	{
		RecordSummaryLayout recordSummaryLayout = new RecordSummaryLayout();
		recordSummaryLayout.setTitleViewColumnId("id");
		recordSummaryLayout.setHasProgressBar(true);
		recordSummaryLayout.setRecordTitleViewColumnId("subject");
		recordSummaryLayout.setPkViewColumnId("id");
		recordSummaryLayout.addViewColumn(new ViewColumn("Description", "description", ViewColumn.ColumnType.TEXT).setIsEditable(true));
		recordSummaryLayout.addViewColumn(new ViewColumn("Status", "status", "status", ViewColumn.ColumnType.LOOKUP));
		recordSummaryLayout.addViewColumn(new ViewColumn("Priority", "priority", "priority", ViewColumn.ColumnType.LOOKUP));
		recordSummaryLayout.addViewColumn(new ViewColumn("Due Date", "duedate", ViewColumn.ColumnType.DATETIME));
		recordSummaryLayout.addViewColumn(new ViewColumn("Requested By", "requester", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Assigned To", "assignedTo", "email", ViewColumn.ColumnType.LOOKUP));
		recordSummaryLayout.addViewColumn(new ViewColumn("Space", "space", "name", ViewColumn.ColumnType.LOOKUP));
		
		recordSummaryLayout.addRelatedModule(new RelatedModule("TASKS", "tasks", "Task", "task")
							.setIcon("fa fa-tasks")
							.setShowHeader(true)
							.addViewColumn(new ViewColumn("ID", "id", ViewColumn.ColumnType.NUMBER))
							.addViewColumn(new ViewColumn("Subject", "subject", ViewColumn.ColumnType.TEXT))
							.addViewColumn(new ViewColumn("Status", "status", "status", ViewColumn.ColumnType.LOOKUP))
							.addViewColumn(new ViewColumn("Assigned To", "assignedTo", "name", ViewColumn.ColumnType.LOOKUP))
							);
		recordSummaryLayout.addRelatedModule(new RelatedModule("ATTACHMENTS", "attachments", "Attachment", "attachment")
							.setIcon("fa fa-paperclip")
							.setShowHeader(false)
							.addViewColumn(new ViewColumn("ID", "fileId", ViewColumn.ColumnType.NUMBER).setShowColumn(false))
							.addViewColumn(new ViewColumn("Name", "fileName", ViewColumn.ColumnType.TEXT))
							);
		recordSummaryLayout.addRelatedModule(new RelatedModule("NOTES", "notes", "Note", "note")
							.setIcon("fa fa-sticky-note")
							.setShowHeader(false)
							.addViewColumn(new ViewColumn("ID", "noteId", ViewColumn.ColumnType.NUMBER).setShowColumn(false))
							.addViewColumn(new ViewColumn("Content", "body", ViewColumn.ColumnType.TEXT))
							);
		return recordSummaryLayout;
	}
	
	public static RecordSummaryLayout getRecordSummaryWorkOrderLayout() {
		RecordSummaryLayout recordSummaryLayout = getRecordSummaryTicketLayout();
		recordSummaryLayout.addViewColumn(new ViewColumn("Requester", "requester", ViewColumn.ColumnType.TEXT));
		
		return recordSummaryLayout;
	}

	public static RecordSummaryLayout getRecordSummaryTaskLayout()
	{
//		RecordSummaryLayout recordSummaryLayout = new RecordSummaryLayout();
//		recordSummaryLayout.setTitleViewColumnId("id");
//		recordSummaryLayout.setHasProgressBar(true);
//		recordSummaryLayout.setRecordTitleViewColumnId("subject");
//		
//		recordSummaryLayout.addViewColumn(new ViewColumn("Description", "description", ViewColumn.ColumnType.TEXT));
//		recordSummaryLayout.addViewColumn(new ViewColumn("Status", "status", "status", ViewColumn.ColumnType.LOOKUP));
//		recordSummaryLayout.addViewColumn(new ViewColumn("Assigned To", "assignedTo", ViewColumn.ColumnType.TEXT));
		RecordSummaryLayout recordSummaryLayout = getRecordSummaryTicketLayout();
		recordSummaryLayout.addViewColumn(new ViewColumn("Work Order", "parentWorkOrder", ViewColumn.ColumnType.TEXT));
		
		return recordSummaryLayout;
	}
	
	public static RecordSummaryLayout getRecordSummaryCampusLayout()
	{
		RecordSummaryLayout recordSummaryLayout = new RecordSummaryLayout();
		recordSummaryLayout.setTitleViewColumnId("name");
		recordSummaryLayout.setHasProgressBar(false);
		
		recordSummaryLayout.addViewColumn(new ViewColumn("Description", "description", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Managed By", "managedBy", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Location", "locationId", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Max Occupancy", "maxOccupancy", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Current Occupancy", "currentOccupancy", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Area", "area", ViewColumn.ColumnType.TEXT));
		return recordSummaryLayout;
	}
	
	public static RecordSummaryLayout getRecordSummaryBuildingLayout()
	{
		RecordSummaryLayout recordSummaryLayout = new RecordSummaryLayout();
		recordSummaryLayout.setTitleViewColumnId("name");
		recordSummaryLayout.setHasProgressBar(false);
		
		recordSummaryLayout.addViewColumn(new ViewColumn("Campus", "campusId", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Floors", "floors", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Location", "locationId", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Max Occupancy", "maxOccupancy", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Current Occupancy", "currentOccupancy", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Area", "area", ViewColumn.ColumnType.TEXT));
		return recordSummaryLayout;
	}
	
	public static RecordSummaryLayout getRecordSummaryFloorLayout()
	{
		RecordSummaryLayout recordSummaryLayout = new RecordSummaryLayout();
		recordSummaryLayout.setTitleViewColumnId("name");
		recordSummaryLayout.setHasProgressBar(false);
		
		recordSummaryLayout.addViewColumn(new ViewColumn("Building", "buildingId", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Main Level", "mainLevel", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Max Occupancy", "maxOccupancy", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Current Occupancy", "currentOccupancy", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Area", "area", ViewColumn.ColumnType.TEXT));
		return recordSummaryLayout;
	}
	
	public static RecordSummaryLayout getRecordSummarySpaceLayout()
	{
		RecordSummaryLayout recordSummaryLayout = new RecordSummaryLayout();
		recordSummaryLayout.setTitleViewColumnId("displayName");
		recordSummaryLayout.setHasProgressBar(false);
		
		recordSummaryLayout.addViewColumn(new ViewColumn("Name", "name", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Building", "buildingId", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Floor", "floorId", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Occupiable", "occupiable", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Availability", "availability", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Category", "category", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Max Occupancy", "maxOccupancy", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Current Occupancy", "currentOccupancy", ViewColumn.ColumnType.TEXT));
		recordSummaryLayout.addViewColumn(new ViewColumn("Area", "area", ViewColumn.ColumnType.TEXT));
		return recordSummaryLayout;
	}
	
	public static RecordSummaryLayout getRecordSummaryZoneLayout()
	{
		RecordSummaryLayout recordSummaryLayout = new RecordSummaryLayout();
		recordSummaryLayout.setTitleViewColumnId("name");
		recordSummaryLayout.setHasProgressBar(false);
		
		recordSummaryLayout.addViewColumn(new ViewColumn("Description", "shortDescription", ViewColumn.ColumnType.TEXT));
		
		recordSummaryLayout.addRelatedModule(new RelatedModule("SPACES", "basespaces", "Space", "basespace")
				.setIcon("fa fa-globe")
				.setShowHeader(true)
				.setDefaultPopup("view")
				.addViewColumn(new ViewColumn("Name", "name", ViewColumn.ColumnType.TEXT))
				.addViewColumn(new ViewColumn("Type", "type", ViewColumn.ColumnType.TEXT))
				);
		return recordSummaryLayout;
	}
}

class RelatedModule
{
	String displayName;
	String listName;
	String moduleName;
	String linkName;
	RelatedModule(String displayName, String listName, String moduleName, String linkName)
	{
		this.displayName = displayName;
		this.listName = listName;
		this.moduleName = moduleName;
		this.linkName = linkName;
	}
	
	public String getDisplayName()
	{
		return this.displayName;
	}
	
	public String getListName()
	{
		return this.listName;
	}
	
	public String getModuleName()
	{
		return this.moduleName;
	}
	
	public String getLinkName()
	{
		return this.linkName;
	}
	
	String defaultPopup = "form";
	public RelatedModule setDefaultPopup(String defaultPopup)
	{
		this.defaultPopup = defaultPopup;
		return this;
	}
	
	public String getDefaultPopup()
	{
		return this.defaultPopup;
	}
	
	String icon;
	public RelatedModule setIcon(String icon)
	{
		this.icon = icon;
		return this;
	}
	
	public String getIcon()
	{
		return this.icon;
	}
	
	boolean showHeader = false;
	public RelatedModule setShowHeader(boolean showHeader)
	{
		this.showHeader = showHeader;
		return this;
	}
	
	public boolean getShowHeader()
	{
		return this.showHeader;
	}
	
	List<ViewColumn> ViewColumns;
	public RelatedModule addViewColumn(ViewColumn ViewColumn)
	{
		if(this.ViewColumns == null)
		{
			ViewColumns = new ArrayList<>();
		}
		ViewColumns.add(ViewColumn);
		return this;
	}
	
	public List<ViewColumn> getViewColumns()
	{
		return this.ViewColumns;
	}
}