package com.facilio.connected;

import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResourceCategory<T extends V3Context> {

    T ctx;

    ResourceType resType;

    public ResourceCategory(ResourceType resType, T resCtx) {
        this.resType = resType;
        this.ctx = resCtx;
    }

    /**
     *
     * @return Category will be null while ResourceType is Other than Meter and Asset
     * in those cases category will be -1
     */
    public long fetchId() {
        return ctx != null ? ctx.getId() : -1l;
    }

    public String fetchDisplayName() {
        if (ctx instanceof V3AssetCategoryContext) {
            return ((V3AssetCategoryContext) ctx).getDisplayName();
        } else if (ctx instanceof V3SiteContext) {
            return ((V3SiteContext) ctx).getName();
        }
        return null;
    }
}
