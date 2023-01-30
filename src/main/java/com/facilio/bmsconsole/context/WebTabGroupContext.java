package com.facilio.bmsconsole.context;

import com.facilio.annotations.AnnotationEnums;
import com.facilio.annotations.ImmutableChildClass;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionType;
import com.facilio.util.FacilioEnumClassTypeIdResolverBase;
import com.facilio.util.FacilioUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@ImmutableChildClass(className = "WebTabGroupCacheContext", constructorPolicy = AnnotationEnums.ConstructorPolicy.REQUIRE_COPY_CONSTRUCTOR)
public class WebTabGroupContext implements Serializable {

    public WebTabGroupContext(WebTabGroupContext object) {
        if(object != null) {
            this.id = object.id;
            this.name = object.name;
            this.route = object.route;
            this.iconType = object.iconType;
            this.iconTypeEnum = object.iconTypeEnum;
            this.order = object.order;
            this.webTabs = object.webTabs;
            this.featureLicense = object.featureLicense;
            this.layoutId = object.layoutId;
        }
    }

    public WebTabGroupContext(List<WebTabContext> webTabs, String name, String route, int iconType, int order, Long featureLicense,long layoutId,IconType iconTypeEnum){
        this.webTabs = webTabs;
        this.name = name;
        this.route = route;
        this.iconType = iconType;
        this.order = order;
        if(featureLicense != null){
            this.featureLicense = featureLicense;
        }
        this.layoutId = layoutId;
        this.iconTypeEnum = iconTypeEnum;
    }

    public WebTabGroupContext(List<WebTabContext> webTabs, String name, String route, int iconType, int order, Long featureLicense, long layoutId){
        this.webTabs = webTabs;
        this.name = name;
        this.route = route;
        this.iconType = iconType;
        this.order = order;
        if(featureLicense != null){
            this.featureLicense = featureLicense;
        }
        this.layoutId = layoutId;
    }
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
    private IconType iconTypeEnum;

    public IconType getIconTypeEnum() {
        return iconTypeEnum;
    }

    public void setIconTypeEnum(IconType iconTypeEnum){
        this.iconTypeEnum =iconTypeEnum;
    }
    @JSON(deserialize = false)
    public void setIconTypeEnum(String iconTypeEnum) {
        if(StringUtils.isNotEmpty(iconTypeEnum)){
            this.iconTypeEnum = IconType.valueOf(iconTypeEnum);
        }

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
