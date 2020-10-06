package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.List;

public class WebTabGroupContext implements Serializable {

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

    private String route;
    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route;
    }

    private int iconType = -1;
    public int getIconType() {
        return iconType;
    }
    public void setIconType(int iconType) {
        this.iconType = iconType;
    }

    private int order = -1;
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }

    private List<WebTabContext> webTabs;
    public List<WebTabContext> getWebTabs() {
		return webTabs;
	}
    public void setWebTabs(List<WebTabContext> webTabs) {
		this.webTabs = webTabs;
	}

    public WebTabGroupContext() {
    }

    private long featureLicense;

    public long getFeatureLicense() {
        return featureLicense;
    }

    public void setFeatureLicense(long featureLicense) {
        this.featureLicense = featureLicense;
    }

    public WebTabGroupContext(String name, String route, long layout, int iconType, int order) {
        this.name = name;
        this.route = route;
        this.layoutId = layout;
        this.iconType = iconType;
        this.order = order;
    }

    public WebTabGroupContext(String name, String route, long layout, int iconType, int order, long license) {
        this.name = name;
        this.route = route;
        this.layoutId = layout;
        this.iconType = iconType;
        this.order = order;
        this.featureLicense = license;
    }

    private long layoutId = -1;

    public long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(long layoutId) {
        this.layoutId = layoutId;
    }
}
