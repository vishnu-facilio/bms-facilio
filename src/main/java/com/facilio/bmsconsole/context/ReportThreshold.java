package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReportThreshold {

	Long id;
	Long reportId;
	String name;
	Long value;
	String color;
	Integer lineStyle;
	
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public Integer getLineStyle() {
		return lineStyle;
	}
	public void setLineStyle(Integer lineStyle) {
		this.lineStyle = lineStyle;
	}
	
	public ReportThresholdLineStyle getReportThresholdLineStyle() {
		if(getLineStyle() != null) {
			return ReportThresholdLineStyle.getReportThresholdLineStyleType(getLineStyle());
		}
		return null;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getValue() {
		return value;
	}
	public void setValue(Long value) {
		this.value = value;
	}
	
	public enum ReportThresholdLineStyle {
		
		DASHED(1,"dashed"),
		DOTTED(2,"dotted"),
		LINE(3,"line");
		
		int value;
		String name;
		
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

		ReportThresholdLineStyle(int value,String name) {
			this.value = value;
			this.name= name;
		}
		
		public static ReportThresholdLineStyle getReportThresholdLineStyleType(int value) {
			return REPORT_THRESHOLD_LINE_STYLE_MAP.get(value);
		}

		private static final Map<Integer, ReportThresholdLineStyle> REPORT_THRESHOLD_LINE_STYLE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, ReportThresholdLineStyle> initTypeMap() {
			Map<Integer, ReportThresholdLineStyle> typeMap = new HashMap<>();
			for(ReportThresholdLineStyle type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
	}
}
