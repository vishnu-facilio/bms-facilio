package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3BuildingContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.V3SpaceCategoryContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class V3SpaceAPI {

    private static final Logger LOGGER = LogManager.getLogger(V3SpaceAPI.class.getName());

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
    public static V3SpaceCategoryContext getSpaceCategoryBySpaceModule(String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE_CATEGORY);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        FacilioModule spaceModule = modBean.getModule(moduleName);


        SelectRecordsBuilder<V3SpaceCategoryContext> selectBuilder = new SelectRecordsBuilder<V3SpaceCategoryContext>()
                .select(fields)
                .table(module.getTableName())
                .moduleName(module.getName())
                .beanClass(V3SpaceCategoryContext.class)
                .andCondition(CriteriaAPI.getCondition("SPACE_MODULE_ID", "spaceModuleId", String.valueOf(spaceModule.getModuleId()), NumberOperators.EQUALS))
                .limit(1);

        List<V3SpaceCategoryContext> spaces = selectBuilder.get();

        if(spaces != null && !spaces.isEmpty()) {
            return spaces.get(0);
        }
        return null;
    }

    public static V3BuildingContext getBuildingSpace(long id) throws Exception {
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
        LOGGER.info("Asset Summary building fetch query ===> "+selectBuilder.toString());
        if(spaces != null && !spaces.isEmpty()) {
            return spaces.get(0);
        }
        return null;
    }

    public static Criteria getBaseSpaceChildrenCriteria(String moduleName, Long id) {
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getBasespaceFields());
        Criteria criteria = new Criteria();
        switch (moduleName) {
            case FacilioConstants.ContextNames.SITE:
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("site"), String.valueOf(id), NumberOperators.EQUALS));
                break;
            case FacilioConstants.ContextNames.BUILDING:
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("building"), String.valueOf(id), NumberOperators.EQUALS));
                break;
            case FacilioConstants.ContextNames.FLOOR:
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("floor"), String.valueOf(id), NumberOperators.EQUALS));
                break;
            case FacilioConstants.ContextNames.SPACE:
                criteria.addOrCondition(CriteriaAPI.getCondition(fieldsMap.get("space1"), String.valueOf(id), NumberOperators.EQUALS));
                criteria.addOrCondition(CriteriaAPI.getCondition(fieldsMap.get("space2"), String.valueOf(id), NumberOperators.EQUALS));
                criteria.addOrCondition(CriteriaAPI.getCondition(fieldsMap.get("space3"), String.valueOf(id), NumberOperators.EQUALS));
                criteria.addOrCondition(CriteriaAPI.getCondition(fieldsMap.get("space4"), String.valueOf(id), NumberOperators.EQUALS));
                break;
            default:
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("id"), String.valueOf(-1), NumberOperators.EQUALS));
                break;
        }
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("id"), String.valueOf(id), NumberOperators.NOT_EQUALS));
        return criteria;
    }

    public static List<V3BaseSpaceContext> getBaseSpaceChildren(String moduleName, Long parentId) throws Exception {
        Map<Long, V3BaseSpaceContext> spacesChildren = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.BASE_SPACE,null,V3BaseSpaceContext.class,getBaseSpaceChildrenCriteria(moduleName,parentId));
        if(!spacesChildren.isEmpty()){
            return spacesChildren.values().stream().collect(Collectors.toList());
        }
        return null;
    }
}
