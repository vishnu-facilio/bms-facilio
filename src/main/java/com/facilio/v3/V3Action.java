package com.facilio.v3;

import com.opensymphony.xwork2.ActionSupport;
import org.json.simple.JSONObject;

public class V3Action extends ActionSupport {
    private JSONObject data;

	public JSONObject getData() {
		return this.data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public void setData(String key, Object result) {
		if (this.data == null) {
			this.data = new JSONObject();
		}
		this.data.put(key, result);
	}

	private String moduleName;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private String search;

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	private String filters;

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	private String viewName;

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	private boolean includeParentFilter;
	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}
	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}

	private boolean overrideViewOrderBy;
	public boolean getOverrideViewOrderBy() {
		return overrideViewOrderBy;
	}
	public void setOverrideViewOrderBy(boolean overrideViewOrderBy) {
		this.overrideViewOrderBy = overrideViewOrderBy;
	}

	private String orderBy;
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getOrderBy() {
		return this.orderBy;
	}

	private String orderType;
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getOrderType() {
		return this.orderType;
	}

	private int page = 1;
	public void setPage(int page) {
		this.page = page;
	}
	public int getPage() {
		return this.page;
	}

	private int perPage = 50;
	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}
	public int getPerPage() {
		return this.perPage;
	}


}
