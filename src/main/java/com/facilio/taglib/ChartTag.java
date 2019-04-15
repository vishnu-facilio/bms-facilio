package com.facilio.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class ChartTag extends SimpleTagSupport {
   
	private String id;
	private String type;
	private Integer width;
	private Integer height;
	private String url;
	private String onclick;
	private String onmove;
	
	public void doTag() throws JspException, IOException {
      
		JspWriter out = getJspContext().getOut();
		
		if ("tree-collapsible".equalsIgnoreCase(getType())) {
			String onClick = (getOnclick() == null) ? "" : getOnclick();
			String onMove = (getOnmove() == null) ? "" : getOnmove();
			out.println("<div id=\""+getId()+"\"></div><script type=\"text/javascript\">loadMeters(\"#"+getId()+"\", "+getWidth()+", "+getHeight()+", \""+getUrl()+"\", \""+onClick+"\", \""+onMove+"\");</script>");
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

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getOnmove() {
		return onmove;
	}

	public void setOnmove(String onmove) {
		this.onmove = onmove;
	}
}
