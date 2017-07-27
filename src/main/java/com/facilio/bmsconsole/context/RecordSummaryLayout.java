package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.context.Column.ColumnType;

public class RecordSummaryLayout {
	
	public RecordSummaryLayout()
	{
		
	}
	
	private String titleColumnId;
	public RecordSummaryLayout setTitleColumnId(String titleColumnId) 
	{
		this.titleColumnId = titleColumnId;
		return this;
	}
	public String getTitleColumnId() 
	{
		return this.titleColumnId;
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
	
	private String recordTitleColumnId;
	public RecordSummaryLayout setRecordTitleColumnId(String recordTitleColumnId) 
	{
		this.recordTitleColumnId = recordTitleColumnId;
		return this;
	}
	public String getRecordTitleColumnId() 
	{
		return this.recordTitleColumnId;
	}
	
	private String pkColumnId;
	public RecordSummaryLayout setPkColumnId(String pkColumnId) 
	{
		this.pkColumnId = pkColumnId;
		return this;
	}
	public String getPkColumnId() 
	{
		return this.pkColumnId;
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
	
	List<Column> columns;
	public RecordSummaryLayout addColumn(Column column)
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

	public static RecordSummaryLayout getRecordSummaryTicketLayout()
	{
		RecordSummaryLayout recordSummaryLayout = new RecordSummaryLayout();
		recordSummaryLayout.setTitleColumnId("id");
		recordSummaryLayout.setHasProgressBar(true);
		recordSummaryLayout.setRecordTitleColumnId("subject");
		recordSummaryLayout.setPkColumnId("id");
		recordSummaryLayout.addColumn(new Column("Description", "description", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Status", "status.status", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Priority", "priority.priority", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Due Date", "duedate", ColumnType.DATETIME));
		recordSummaryLayout.addColumn(new Column("Requested By", "requester", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Assigned To", "assignTo.email", ColumnType.TEXT));
		
		recordSummaryLayout.addRelatedModule(new RelatedModule("TASKS", "tasks", "Task", "task")
							.setIcon("fa fa-tasks")
							.setShowHeader(true)
							.addColumn(new Column("ID", "taskId", ColumnType.NUMBER))
							.addColumn(new Column("Subject", "subject", ColumnType.TEXT))
							.addColumn(new Column("Status", "status", ColumnType.TEXT))
							.addColumn(new Column("Assigned To", "assignToId", ColumnType.TEXT))
							);
		recordSummaryLayout.addRelatedModule(new RelatedModule("ATTACHMENTS", "attachments", "Attachment", "attachment")
							.setIcon("fa fa-paperclip")
							.setShowHeader(false)
							.addColumn(new Column("ID", "fileId", ColumnType.NUMBER).setShowColumn(false))
							.addColumn(new Column("Name", "fileName", ColumnType.TEXT))
							);
		recordSummaryLayout.addRelatedModule(new RelatedModule("NOTES", "notes", "Note", "note")
							.setIcon("fa fa-sticky-note")
							.setShowHeader(false)
							.addColumn(new Column("ID", "noteId", ColumnType.NUMBER).setShowColumn(false))
							.addColumn(new Column("Content", "body", ColumnType.TEXT))
							);
		return recordSummaryLayout;
	}

	public static RecordSummaryLayout getRecordSummaryTaskLayout()
	{
		RecordSummaryLayout recordSummaryLayout = new RecordSummaryLayout();
		recordSummaryLayout.setTitleColumnId("id");
		recordSummaryLayout.setHasProgressBar(true);
		recordSummaryLayout.setRecordTitleColumnId("subject");
		
		recordSummaryLayout.addColumn(new Column("Description", "description", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Status", "status", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Assigned To", "assignToId", ColumnType.TEXT));
		
		return recordSummaryLayout;
	}
	
	public static RecordSummaryLayout getRecordSummaryCampusLayout()
	{
		RecordSummaryLayout recordSummaryLayout = new RecordSummaryLayout();
		recordSummaryLayout.setTitleColumnId("name");
		recordSummaryLayout.setHasProgressBar(false);
		
		recordSummaryLayout.addColumn(new Column("Description", "description", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Managed By", "managedBy", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Location", "locationId", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Max Occupancy", "maxOccupancy", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Current Occupancy", "currentOccupancy", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Area", "area", ColumnType.TEXT));
		return recordSummaryLayout;
	}
	
	public static RecordSummaryLayout getRecordSummaryBuildingLayout()
	{
		RecordSummaryLayout recordSummaryLayout = new RecordSummaryLayout();
		recordSummaryLayout.setTitleColumnId("name");
		recordSummaryLayout.setHasProgressBar(false);
		
		recordSummaryLayout.addColumn(new Column("Campus", "campusId", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Floors", "floors", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Location", "locationId", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Max Occupancy", "maxOccupancy", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Current Occupancy", "currentOccupancy", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Area", "area", ColumnType.TEXT));
		return recordSummaryLayout;
	}
	
	public static RecordSummaryLayout getRecordSummaryFloorLayout()
	{
		RecordSummaryLayout recordSummaryLayout = new RecordSummaryLayout();
		recordSummaryLayout.setTitleColumnId("name");
		recordSummaryLayout.setHasProgressBar(false);
		
		recordSummaryLayout.addColumn(new Column("Building", "buildingId", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Main Level", "mainLevel", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Max Occupancy", "maxOccupancy", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Current Occupancy", "currentOccupancy", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Area", "area", ColumnType.TEXT));
		return recordSummaryLayout;
	}
	
	public static RecordSummaryLayout getRecordSummarySpaceLayout()
	{
		RecordSummaryLayout recordSummaryLayout = new RecordSummaryLayout();
		recordSummaryLayout.setTitleColumnId("displayName");
		recordSummaryLayout.setHasProgressBar(false);
		
		recordSummaryLayout.addColumn(new Column("Name", "name", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Building", "buildingId", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Floor", "floorId", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Occupiable", "occupiable", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Availability", "availability", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Category", "category", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Max Occupancy", "maxOccupancy", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Current Occupancy", "currentOccupancy", ColumnType.TEXT));
		recordSummaryLayout.addColumn(new Column("Area", "area", ColumnType.TEXT));
		return recordSummaryLayout;
	}
	
	public static RecordSummaryLayout getRecordSummaryZoneLayout()
	{
		RecordSummaryLayout recordSummaryLayout = new RecordSummaryLayout();
		recordSummaryLayout.setTitleColumnId("name");
		recordSummaryLayout.setHasProgressBar(false);
		
		recordSummaryLayout.addColumn(new Column("Description", "description", ColumnType.TEXT));
		
		recordSummaryLayout.addRelatedModule(new RelatedModule("SPACES", "spaces", "Space", "space"));
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
	
	List<Column> columns;
	public RelatedModule addColumn(Column column)
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
}