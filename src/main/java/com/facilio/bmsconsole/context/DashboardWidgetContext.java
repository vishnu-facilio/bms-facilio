package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.WidgetSectionContext;
import com.facilio.bmsconsoleV3.context.dashboard.WidgetDashboardFilterContext;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.util.FacilioUtil;

public abstract class DashboardWidgetContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String widgetName;
	private Integer type;
	private Long dashboardId;
	private Long dashboardTabId;

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	private String linkName;
	private List<DashboardUserFilterWidgetFieldMappingContext> widgetFieldMapping;
	public void setWidgetFieldMapping(List<DashboardUserFilterWidgetFieldMappingContext> widgetFieldMapping) {
		this.widgetFieldMapping = widgetFieldMapping;
	}
	public List<DashboardUserFilterWidgetFieldMappingContext> getWidgetFieldMapping(){
		return this.widgetFieldMapping;
	}

	public Long getSectionId() {
		return sectionId;
	}

	public void setSectionId(Long sectionId) {
		this.sectionId = sectionId;
	}

	private Long sectionId;
	
	private int layoutWidth = -1;
	private int layoutHeight= -1;
	private int xPosition= -1;
	private int yPosition= -1;
	private int layoutPosition= -1;


	public int x=-1;
	public int y=-1;
	public int w=-1;
	public int h=-1;
	public int minW=-1;
	public int maxW=-1;

	private void setX(Integer x){
		getMetaJSON().put("x", x);
	}
	public Integer getX(){
		if(getMetaJSON().get("x") != null){
			return Integer.parseInt(getMetaJSON().get("x").toString());
		}
		return null;
	}

	public void setY(Integer y){
		getMetaJSON().put("y", y);
	}
	public Integer getY(){
		if(getMetaJSON().get("y") != null){
			return Integer.parseInt(getMetaJSON().get("y").toString());
		}
		return null;
	}

	public void setW(Integer w){
		getMetaJSON().put("w", w);
	}
	public Integer getW(){
		if(getMetaJSON().get("w") != null){
			return Integer.parseInt(getMetaJSON().get("w").toString());
		}
		return null;
	}
	public void setH(Integer h){
		getMetaJSON().put("h", h);
	}
	public Integer getH(){
		if(getMetaJSON().get("h") != null){
			return Integer.parseInt(getMetaJSON().get("h").toString());
		}
		return null;
	}

	public void setMinW(Integer minW){
		getMetaJSON().put("minW", minW);
	}
	public Integer getMinW(){
		if(getMetaJSON().get("minW") != null){
			return Integer.parseInt(getMetaJSON().get("minW").toString());
		}
		return null;
	}
	public void setMaxW(Integer maxW){
		getMetaJSON().put("maxW", maxW);
	}

	public Integer getMaxW(){
		if(getMetaJSON().get("maxW") != null){
			return Integer.parseInt(getMetaJSON().get("maxW").toString());
		}
		return null;
	}

	boolean showHide = Boolean.TRUE;
	
//	boolean mShowHide = Boolean.TRUE;
//	private int mLayoutWidth = -1;
//	private int mLayoutHeight = -1;
//	private int mXPosition = -1;
//	private int mYPosition = -1;
//	private int mLayoutPosition = -1;
	
	private int dataRefreshIntervel;
	private String widgetUrl;
	private String headerText;
	private String headerSubText;
	private String helpText;
	
	private boolean headerIsExport;
	
	
	private String metaJSONString;
	private JSONObject metaJSON;
	
	private String widgetSettingsJsonString;
	
	@JSON(serialize = false)
	public String getWidgetSettingsJsonString() {
		if(this.widgetSettings!=null)
		{
			return this.widgetSettings.toJSONString();
		}
		else {
			return null;
		}
	}
	public void setWidgetSettingsJsonString(String widgetSettingsJsonString) {
		this.widgetSettingsJsonString = widgetSettingsJsonString;
	}



	JSONObject widgetSettings;
	
	public void setWidgetSettings(JSONObject widgetSettings)
	{
		//used while setting from client, json object sent
		this.widgetSettings=widgetSettings;
		
	}
	public JSONObject getWidgetSettings() 
	{
		//used while fetching from db ,widgetSettingsJsonString  set from db,parse as json to send to client
		
		if(this.widgetSettingsJsonString!=null)
		{
			
			try {
				return FacilioUtil.parseJson(this.widgetSettingsJsonString);
			} catch (ParseException e) {				
				e.printStackTrace();
				return null;
			}
		}
		else
		{
			return DEFAULT_WIDGET_SETTINGS;
		}
		
	}


	
  public static final JSONObject DEFAULT_WIDGET_SETTINGS=initDefaultWidgetSettingsJson();
  
  public static JSONObject initDefaultWidgetSettingsJson()
  {
	  
	  JSONObject defaultWidgetSettings=new JSONObject();
	  defaultWidgetSettings.put("excludeDbFilters", false);
	  defaultWidgetSettings.put("showHelpText", false);
	return defaultWidgetSettings;
  }


	public String getMetaJSONString() {
		if(metaJSON != null) {
			return metaJSON.toJSONString();
		}
		return metaJSONString;
	}
	public void setMetaJSONString(String metaJSONString) throws ParseException {
		this.metaJSONString = metaJSONString;
		if(metaJSONString != null) {
			JSONParser parser = new JSONParser();
			metaJSON = (JSONObject) parser.parse(metaJSONString);
		}
	}
	public JSONObject getMetaJSON() {
		if(metaJSON == null) {
			metaJSON =  new JSONObject();
		}
		return metaJSON;
	}
	public void setMetaJSON(JSONObject metaJSON) {
		this.metaJSON = metaJSON;
	}
	public Integer getLayoutWidth() {
		if(getMetaJSON().get("layoutWidth") != null) {
			return Integer.parseInt(getMetaJSON().get("layoutWidth").toString());
		}
		return null;
	}
	public void setLayoutWidth(int layoutWidth) {
		getMetaJSON().put("layoutWidth", layoutWidth);
	}
	public Integer getLayoutHeight() {
		if(getMetaJSON().get("layoutHeight") != null) {
			return Integer.parseInt(getMetaJSON().get("layoutHeight").toString());
		}
		return null;
	}
	public void setLayoutHeight(int layoutHeight) {
		getMetaJSON().put("layoutHeight", layoutHeight);
	}
	public Integer getLayoutPosition() {
		if(getMetaJSON().get("layoutPosition") != null) {
			return Integer.parseInt(getMetaJSON().get("layoutPosition").toString());
		}
		return null;
	}
	public void setLayoutPosition(int layoutPosition) {
		getMetaJSON().put("layoutPosition", layoutPosition);
	}
	public Integer getxPosition() {
		if(getMetaJSON().get("xPosition") != null) {
			return Integer.parseInt(getMetaJSON().get("xPosition").toString());
		}
		return null;
	}
	public void setxPosition(int xPosition) {
		getMetaJSON().put("xPosition", xPosition);
	}
	public Integer getyPosition() {
		
		if(getMetaJSON().get("yPosition") != null) {
			return Integer.parseInt(getMetaJSON().get("yPosition").toString());
		}
		return null;
	}
	public void setyPosition(int yPosition) {
		getMetaJSON().put("yPosition", yPosition);
	}	
	public Boolean getShowHide() {
		if(getMetaJSON().get("showHide") != null) {
			return (boolean) getMetaJSON().get("showHide");
		}
		return null;
	}
	public void setShowHide(boolean showHide) {
		getMetaJSON().put("showHide", showHide);
	}
	public Boolean getMShowHide() {
		if(getMetaJSON().get("mShowHide") != null) {
			return (boolean) getMetaJSON().get("mShowHide");
		}
		return null;
	}
	public void setmShowHide(boolean mShowHide) {
		getMetaJSON().put("mShowHide", mShowHide);
	}
	public Integer getmLayoutWidth() {
		if(getMetaJSON().get("mLayoutWidth") != null) {
			return Integer.parseInt(getMetaJSON().get("mLayoutWidth").toString());
		}
		return null;
	}
	public void setmLayoutWidth(int mLayoutWidth) {
		getMetaJSON().put("mLayoutWidth", mLayoutWidth);
	}
	public Integer getmLayoutHeight() {
		if(getMetaJSON().get("mLayoutHeight") != null) {
			return Integer.parseInt(getMetaJSON().get("mLayoutHeight").toString());
		}
		return null;
	}
	public void setmLayoutHeight(int mLayoutHeight) {
		getMetaJSON().put("mLayoutHeight", mLayoutHeight);
	}
	public Integer getmXPosition() {
		
		if(getMetaJSON().get("mXPosition") != null) {
			return Integer.parseInt(getMetaJSON().get("mXPosition").toString());
		}
		return null;
	}
	public void setmXPosition(int mXPosition) {
		getMetaJSON().put("mXPosition", mXPosition);
	}
	public Integer getmYPosition() {
		
		if(getMetaJSON().get("mYPosition") != null) {
			return Integer.parseInt(getMetaJSON().get("mYPosition").toString());
		}
		return null;
	}
	public void setmYPosition(int mYPosition) {
		getMetaJSON().put("mYPosition", mYPosition);
	}
	public Integer getmLayoutPosition() {
		if(getMetaJSON().get("mLayoutPosition") != null) {
			return Integer.parseInt(getMetaJSON().get("mLayoutPosition").toString());
		}
		return null;
	}
	public void setmLayoutPosition(int mLayoutPosition) {
		getMetaJSON().put("mLayoutPosition", mLayoutPosition);
	}
	
	public int getDataRefreshIntervel() {
		return dataRefreshIntervel;
	}
	public void setDataRefreshIntervel(int dataRefreshIntervel) {
		this.dataRefreshIntervel = dataRefreshIntervel;
	}
	private List<WidgetVsWorkflowContext> widgetVsWorkflowContexts;
	
	public List<WidgetVsWorkflowContext> getWidgetVsWorkflowContexts() {
		return widgetVsWorkflowContexts;
	}
	public void setWidgetVsWorkflowContexts(List<WidgetVsWorkflowContext> widgetVsWorkflowContexts) {
		this.widgetVsWorkflowContexts = widgetVsWorkflowContexts;
	}
	public void addWidgetVsWorkflowContexts(WidgetVsWorkflowContext widgetVsWorkflowContexts) {
		if(this.widgetVsWorkflowContexts == null) {
			this.widgetVsWorkflowContexts = new ArrayList<>();
		}
		this.widgetVsWorkflowContexts.add(widgetVsWorkflowContexts);
	}
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
	
	public abstract JSONObject widgetJsonObject(boolean optimize);
	
	public Long getDashboardTabId() {
		return dashboardTabId;
	}
	public void setDashboardTabId(Long dashboardTabId) {
		this.dashboardTabId = dashboardTabId;
	}
	
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
	public void setType(String type) {
		try {
			this.type = Integer.parseInt(type);
			return;
		}
		catch(Exception e) {
			
		}
		this.type = DashboardWidgetContext.WidgetType.getWidgetType(type).getValue();
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
	
	public String getHelpText() {
		return helpText;
	}
	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public enum WidgetType {
		STATIC(0,"static",WidgetStaticContext.class),
		CHART(1,"chart",WidgetChartContext.class),
		LIST_VIEW(2,"view",WidgetListViewContext.class),
		MAP(3,"map",WidgetListViewContext.class),
		WEB(4,"web",WidgetWebContext.class),
		GRAPHICS(5,"graphics",WidgetGraphicsContext.class),
		CARD(6,"card",WidgetCardContext.class),
		SECTION(7,"section", WidgetSectionContext.class),
		FILTER(8,"filter", WidgetDashboardFilterContext.class);
		
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
		
		public static WidgetType getWidgetType(String name) {
			return WIDGET_CHART_NAME_MAP.get(name);
		}

		private static final Map<Integer, WidgetType> WIDGET_CHART_TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static final Map<String, WidgetType> WIDGET_CHART_NAME_MAP = Collections.unmodifiableMap(initNameMap());
		private static Map<Integer, WidgetType> initTypeMap() {
			Map<Integer, WidgetType> typeMap = new HashMap<>();
			for(WidgetType type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
		private static Map<String, WidgetType> initNameMap() {
			Map<String, WidgetType> nameMap = new HashMap<>();
			for(WidgetType type : values()) {
				nameMap.put(type.getName(), type);
			}
			return nameMap;
		}
	}
}
