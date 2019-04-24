package com.facilio.bmsconsole.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.page.Page.Section;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class WidgetGroup {
	
	public WidgetGroup(WidgetGroupType widgetGroupType) {
		this.widgetGroupType = widgetGroupType;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
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
	
	private WidgetGroupType widgetGroupType;
	public WidgetGroupType getWidgetGroupTypeEnum() {
		return widgetGroupType;
	}
	public int getWidgetGroupType() {
		return widgetGroupType != null ?  widgetGroupType.getValue() : -1;
	}
	public void setWidgetGroupType(int widgetGroupType) {
		this.widgetGroupType = WidgetGroupType.valueOf(widgetGroupType);
	}
	public void setWidgetGroupType(WidgetGroupType widgetGroupType) {
		this.widgetGroupType = widgetGroupType;
	}
	
	@JsonIgnore
	public Map<String, Object> getWidgetTypeObj() throws Exception {
		return FieldUtil.getAsProperties(widgetGroupType);
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
	public void addToLayoutParams(int width, int height) {
		addToLayoutParams(0, 0, width, height);
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
	public enum WidgetGroupType {
		TAB,
		CAROUSEL
 		;
		
 		public int getValue() {
			return ordinal() + 1;
		}
		
		public static WidgetGroupType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
 	}

}
