package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsoleV3.context.V3BuildingContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.List;
import java.util.Map;

public class V3SpaceAPI {
    public static V3SiteContext getSiteSpace(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3SiteContext> selectBuilder = new SelectRecordsBuilder<V3SiteContext>()
                .select(fields)
                .table(module.getTableName())
                .moduleName(module.getName())
                .maxLevel(0)
                .beanClass(V3SiteContext.class)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                .andCustomWhere(module.getTableName()+".ID = ?", id);

        LookupField location = (LookupField) fieldsAsMap.get("location");
        selectBuilder.fetchSupplement(location);
        List<V3SiteContext> spaces = selectBuilder.get();

        if(spaces != null && !spaces.isEmpty()) {
            return spaces.get(0);
        }
        return null;
    }

    public static V3BuildingContext getBuildingSpace(long id, boolean fetchLocation) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3BuildingContext> selectBuilder = new SelectRecordsBuilder<V3BuildingContext>()
                .select(fields)
                .table(module.getTableName())
                .moduleName(module.getName())
                .maxLevel(0)
                .beanClass(V3BuildingContext.class)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                .andCustomWhere(module.getTableName()+".ID = ?", id);
        LookupField location = (LookupField) fieldsAsMap.get("location");
        selectBuilder.fetchSupplement(location);
        List<V3BuildingContext> spaces = selectBuilder.get();

        if(spaces != null && !spaces.isEmpty()) {
            return spaces.get(0);
        }
        return null;
    }

}
