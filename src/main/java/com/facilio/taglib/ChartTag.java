package com.facilio.taglib;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import java.io.*;

public class ChartTag extends SimpleTagSupport {
   
	private String id;
	private String type;
	private Integer width;
	private Integer height;
	private String url;
	
	public void doTag() throws JspException, IOException {
      
		JspWriter out = getJspContext().getOut();
		
		if ("tree-collapsible".equalsIgnoreCase(getType())) {
			out.println("<div id=\""+getId()+"\"></div><script type=\"text/javascript\">loadMeters(\"#"+getId()+"\", "+getWidth()+", "+getHeight()+", \""+getUrl()+"\");</script>");
		}
		else {
			out.println("<div id=\""+getId()+"\"></div><script type=\"text/javascript\">loadMeterPerformance(\"#"+getId()+"\", "+getWidth()+", "+getHeight()+");</script>");
		}
   }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getWidth() {
		if (width == null) {
			return 600;
		}
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		if (height == null) {
			return 400;
		}
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
