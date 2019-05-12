package com.facilio.bmsconsole.page;

import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.modules.FieldUtil;
import com.facilio.bmsconsole.page.Page.Section;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class PageWidget {
	private static final long serialVersionUID = 1L;
	
	public PageWidget () {}
	
	public PageWidget(WidgetType type) {
		this.widgetType = type;
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
	
	private WidgetType widgetType;
	public WidgetType getWidgetTypeEnum() {
		return widgetType;
	}
	public int getWidgetType() {
		return widgetType != null ?  widgetType.getValue() : -1;
	}
	public void setWidgetType(int widgetType) {
		this.widgetType = WidgetType.valueOf(widgetType);
	}
	public void setWidgetType(WidgetType widgetType) {
		this.widgetType = widgetType;
	}
	
	@JsonIgnore
	public Map<String, Object> getWidgetTypeObj() throws Exception {
		return FieldUtil.getAsProperties(widgetType);
	}
	
	private long sectionId;
	public long getSectionId() {
		return sectionId;
	}
	public void setSectionId(long sectionId) {
		this.sectionId = sectionId;
	}
	
	private Section section;
	public Section getSection() {
		return section;
	}
	public void setSection(Section section) {
		this.section = section;
	}
	
	private JSONObject widgetParams;
	public JSONObject getWidgetParams() {
		return widgetParams;
	}
	public void setWidgetParams(JSONObject widgetParams) {
		this.widgetParams = widgetParams;
	}
	public void setWidgetParams(String widgetParams) throws Exception {
		if(widgetParams != null) {
			JSONParser parser = new JSONParser();
			this.widgetParams = (JSONObject) parser.parse(widgetParams);
		}
	}
	
	public void addToWidgetParams (String key, Object value) {
		if (widgetParams == null) {
			widgetParams = new JSONObject();
		}
		widgetParams.put(key, value);
	}
	
	

	private JSONObject layoutParams;
	public JSONObject getLayoutParams() {
		return layoutParams;
	}
	public void setLayoutParams(JSONObject layoutParams) {
		this.layoutParams = layoutParams;
	}
	public void setLayoutParams(String layoutParams) throws Exception {
		if(layoutParams != null) {
			JSONParser parser = new JSONParser();
			this.layoutParams = (JSONObject) parser.parse(layoutParams);
		}
	}
	public void setXPoisition(int position) {
		addToLayoutParams("x", position);
	}
	public void setYPoisition(int position) {
		addToLayoutParams("y", position);
	}
	public void setWidth(int width) {
		addToLayoutParams("w", width);
	}
	public void setHeight(int height) {
		addToLayoutParams("h", height);
	}
	public void addToLayoutParams(Section section, int width, int height) {
		int x = section.getLatestX();
		int y = section.getLatestY();
		if (x != 0) {
			x += width;
		}
		addToLayoutParams(x, y, width, height);
		if (x >= 24 || width >= 24) {
			y += height;	// Assuming the height will be same for everywidget
			x = 0;
		}
		else if (x == 0) {
			x += width;
		}
		section.setLatestXY(x, y);
	}
	public void addToLayoutParams(int xPosition, int yPosition, int width, int height) {
		setXPoisition(xPosition);
		setYPoisition(yPosition);
		setWidth(width);
		setHeight(height);
	}
	
	private void addToLayoutParams (String key, Object value) {
		if (layoutParams == null) {
			layoutParams = new JSONObject();
		}
		layoutParams.put(key, value);
	}

	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
	public enum WidgetType {
 		PRIMARY_DETAILS_WIDGET("primaryDetailsWidget"),
 		SECONDARY_DETAILS_WIDGET("secondaryDetailsWidget"),
 		CARD("card"),
 		CHART("chart"),
 		LIST("list"),
 		COUNT("count"),
 		COMMENT("comment"),
 		ATTACHMENT("attachment"),
 		ACTIVITY("activity")
 		;
		
		private String name;
		
		WidgetType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
 		public int getValue() {
			return ordinal() + 1;
		}
		
		public static WidgetType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
 	}
	
}
