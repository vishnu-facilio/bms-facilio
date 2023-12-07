package com.facilio.telemetry.context;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.facilio.annotations.AnnotationEnums;
import com.facilio.annotations.ImmutableChildClass;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@ImmutableChildClass(className = "TelemetryCriteriaCacheContext",constructorPolicy = AnnotationEnums.ConstructorPolicy.REQUIRE_COPY_CONSTRUCTOR)
public class TelemetryCriteriaContext extends V3Context {
    private String name;
    private String description;
    private NameSpaceContext namespace;
    private Long namespaceId;
    private V3AssetCategoryContext assetCategory;

    public TelemetryCriteriaContext() {
    }
    public TelemetryCriteriaContext(TelemetryCriteriaContext object) {
        if(object != null) {
            super.setId(object.getId());
            this.name = object.name;
            this.description = object.description;
            this.namespace = object.namespace;
            this.namespaceId = object.namespaceId;
            this.assetCategory = object.assetCategory;
        }
    }
}