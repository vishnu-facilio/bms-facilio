package com.facilio.bmsconsole.context;

import java.util.List;

public class ZoneContext extends BaseSpaceContext {
	
	private List<BaseSpaceContext> basespaces;
	public List<BaseSpaceContext> getBasespaces() {
		return basespaces;
	}
	public void setBasespaces(List<BaseSpaceContext> basespaces) {
		this.basespaces = basespaces;
	}
}
