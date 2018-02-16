package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public abstract class DashboardWidgetContext extends ModuleBaseWithCustomFields{

	private String widgetName;
	private Integer type;
	private Long dashboardId;
	private Integer layoutWidth;
	private Integer layoutHeight;
	private int layoutPosition;
	private int dataRefreshIntervel;
	private String widgetUrl;
	private String headerText;
	private String headerSubText;
	private boolean headerIsExport;
	
	public String getHeaderText() {
		return headerText;
	}
	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}
	public String getHeaderSubText() {
		return headerSubText;
	}
	public void setHeaderSubText(String headerSubText) {
		this.headerSubText = headerSubText;
	}
	public boolean isHeaderIsExport() {
		return headerIsExport;
	}
	public void setHeaderIsExport(boolean headerIsExport) {
		this.headerIsExport = headerIsExport;
	}
	public String getWidgetUrl() {
		return widgetUrl;
	}
	public void setWidgetUrl(String widgetUrl) {
		this.widgetUrl = widgetUrl;
	}
	
	public abstract JSONObject widgetJsonObject();
	
	public Long getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(Long dashboardId) {
		this.dashboardId = dashboardId;
	}
	public WidgetType getWidgetType() {
		if(getType() != null) {
			return WidgetType.getWidgetType(getType());
		}
		return null;
	}
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getWidgetName() {
		return widgetName;
	}
	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}
	public Integer getLayoutWidth() {
		return layoutWidth;
	}
	public void setLayoutWidth(Integer layoutWidth) {
		this.layoutWidth = layoutWidth;
	}
	public Integer getLayoutHeight() {
		return layoutHeight;
	}
	public void setLayoutHeight(Integer layoutHeight) {
		this.layoutHeight = layoutHeight;
	}
	public int getLayoutPosition() {
		return layoutPosition;
	}
	public void setLayoutPosition(int layoutPosition) {
		this.layoutPosition = layoutPosition;
	}
	public int getDataRefreshIntervel() {
		return dataRefreshIntervel;
	}
	public void setDataRefreshIntervel(int dataRefreshIntervel) {
		this.dataRefreshIntervel = dataRefreshIntervel;
	}
	public enum WidgetType {
		CHART(1,"chart",WidgetChartContext.class),
		LIST_VIEW(2,"view",WidgetListViewContext.class),
		MAP(3,"map",WidgetListViewContext.class);
		
		private int value;
		private String name;
		private Class<DashboardWidgetContext> widgetContextClass;
		
		WidgetType(int value,String name,Class widgetContextClass) {
			this.value = value;
			this.name = name;
			this.widgetContextClass = widgetContextClass;
		}
		public Class getWidgetContextClass() {
			return widgetContextClass;
		}
		public void setWidgetContextClass(Class widgetContextClass) {
			this.widgetContextClass = widgetContextClass;
		}
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public static WidgetType getWidgetType(int value) {
			return WIDGET_CHART_TYPE_MAP.get(value);
		}

		private static final Map<Integer, WidgetType> WIDGET_CHART_TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, WidgetType> initTypeMap() {
			Map<Integer, WidgetType> typeMap = new HashMap<>();
			for(WidgetType type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
	}

	
}
