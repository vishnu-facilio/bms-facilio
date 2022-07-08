package com.facilio.bmsconsole.page;

import java.util.ArrayList;
import java.util.List;

import com.facilio.modules.FacilioModule;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Page {
	private static final long serialVersionUID = 1L;
	
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

	private List<Tab> tabs;
	public List<Tab> getTabs() {
		return tabs;
	}
	public void setTabs(List<Tab> tabs) {
		this.tabs = tabs;
	}
	public void addTab(Tab tab) {
		if (tabs == null) {
			tabs = new ArrayList<>();
		}
		tabs.add(tab);
	}
	
	public void addTab(int index, Tab tab) {
		if (tabs == null) {
			tabs = new ArrayList<>();
		}
		tabs.add(index, tab);
	}
	
	public class Tab {
		
		public Tab(String name) {
			this(name, null);
		}
		
		public Tab(String name, String component) {
			this.name = name;
			this.component = component;
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
		
		private String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		private String component;
		public String getComponent() {
			return component;
		}
		public void setComponent(String component) {
			this.component = component;
		}
		
		private String displayName;
		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
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
		
		public Section(){}
		
		public Section(String name) {
			this.name = name;
		}

		public Section(String name, String description) {
			this.name = name;
			this.description = description;
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
		
		private String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}

		private String description;
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
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
		
		private long tabId = -1;
		public long getTabId() {
			return tabId;
		}
		public void setTabId(long tabId) {
			this.tabId = tabId;
		}
		
		private Tab tab;
		public Tab getTab() {
			return tab;
		}
		public void setTab(Tab tab) {
			this.tab = tab;
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
		
		private int latestX = 0;
		private int latestY = 0;
		@JsonIgnore
		public int getLatestX() {
			return latestX;
		}
		@JsonIgnore
		public int getLatestY() {
			return latestY;
		}
		@JsonIgnore
		public void setLatestXY(int latestX, int latestY) {
			this.latestX = latestX;
			this.latestY = latestY;
		}
	}

}
