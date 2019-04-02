package com.facilio.bmsconsole.page;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.modules.FacilioModule;

public class Page {
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private FacilioModule module;
	public FacilioModule getModule() {
		return module;
	}
	public void setModule(FacilioModule module) {
		this.module = module;
	}

	private List<Column> columns;
	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	public void addColumns(Column column) {
		if (columns == null) {
			columns = new ArrayList<>();
		}
		columns.add(column);
	}

	public class Column {
		
		Column () {}
		
		Column (int width) {
			this(width, false);
		}
		Column (int width, boolean isFixed) {
			this.width = width;
			this.fixed = isFixed;
		}
		
		private long orgId = -1;
		public long getOrgId() {
			return orgId;
		}
		public void setOrgId(long orgId) {
			this.orgId = orgId;
		}
		
		private long id = -1;
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		
		private long pageId = -1;
		public long getPageId() {
			return pageId;
		}
		public void setPageId(long pageId) {
			this.pageId = pageId;
		}
		
		private Boolean fixed;
		public Boolean getFixed() {
			return fixed;
		}
		public void setFixed(Boolean fixed) {
			this.fixed = fixed;
		}
		public boolean isFixed() {
			if (fixed == null) {
				return false;
			}
			return fixed;
		}
		
		private int width = -1;	// in percent
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
		
		private List<Section> sections;
		public List<Section> getSections() {
			return sections;
		}
		public void setSections(List<Section> sections) {
			this.sections = sections;
		}
		public void addSection(Section section, Integer...index) {
			if (sections == null) {
				sections = new ArrayList<>();
			}
			if (index != null && index.length > 0) {
				sections.add(index[0], section);
			}
			sections.add(section);
		}
	}
	
	public class Section {
		private long orgId = -1;
		public long getOrgId() {
			return orgId;
		}
		public void setOrgId(long orgId) {
			this.orgId = orgId;
		}
		
		private long id = -1;
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		
		private String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}

		private String displayName;
		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		
		private long pageId = -1;
		public long getPageId() {
			return pageId;
		}
		public void setPageId(long pageId) {
			this.pageId = pageId;
		}
		
		private long columnId = -1;
		public long getColumnId() {
			return columnId;
		}
		public void setColumnId(long columnId) {
			this.columnId = columnId;
		}
		
		private Column column;
		public Column getColumn() {
			return column;
		}
		public void setColumn(Column column) {
			this.column = column;
		}
		
		private List<PageWidget> widgets;
		public List<PageWidget> getWidgets() {
			return widgets;
		}
		public void setWidgets(List<PageWidget> widgets) {
			this.widgets = widgets;
		}
		public void addWidget(PageWidget widget) {
			if (this.widgets == null) {
				this.widgets = new ArrayList<>();
			}
			this.widgets.add(widget);
		}
		
		private List<WidgetGroup> widgetGroups;
		public List<WidgetGroup> getWidgetGroups() {
			return widgetGroups;
		}
		public void setWidgetGroups(List<WidgetGroup> widgetGroups) {
			this.widgetGroups = widgetGroups;
		}
		public void addWidgetGroup(WidgetGroup widgetGroup) {
			if (this.widgetGroups == null) {
				this.widgetGroups = new ArrayList<>();
			}
			this.widgetGroups.add(widgetGroup);
		}
	}

}
