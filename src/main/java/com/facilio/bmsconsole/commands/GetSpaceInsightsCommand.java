package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class GetSpaceInsightsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long spaceId = (long) context.get(FacilioConstants.ContextNames.ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        JSONObject insights = new JSONObject();
        long floorCount = 0, spaceCount = 0, independantSpaceCount = 0, buildingCount = 0, assetCount = 0, subSpaceCount = 0, meterCount = 0;
        assetCount = SpaceAPI.getAssetsCount(spaceId);
        meterCount = SpaceAPI.getMetersCount(spaceId);
        if (spaceId > 0) {
            switch (moduleName) {
                case "site":
                    buildingCount = getAllSpaces(spaceId, BaseSpaceContext.SpaceType.BUILDING.getIntVal());
                    floorCount = getAllSpaces(spaceId, BaseSpaceContext.SpaceType.FLOOR.getIntVal());
                    spaceCount = getAllSpaces(spaceId, BaseSpaceContext.SpaceType.SPACE.getIntVal());
                    independantSpaceCount = SpaceAPI.getIndependentSpacesCount(spaceId);
                    break;
                case "building":
                    floorCount = getAllSpacesCount(spaceId,"building", BaseSpaceContext.SpaceType.FLOOR);
                    spaceCount = SpaceAPI.getSpacesCountForBuilding(spaceId);
                    independantSpaceCount = SpaceAPI.getIndependentSpacesCount(spaceId);
                    break;
                case "floor":
                    spaceCount = getAllSpacesCount(spaceId,"floor", BaseSpaceContext.SpaceType.SPACE);
                    break;
                case "space":
                    subSpaceCount = getBaseSpaceWithChildren(Collections.singletonList(spaceId));
                    break;
            }
        } else {
            throw new IllegalArgumentException("Invalid Site ID : " + spaceId);
        }
        insights.put("independent_spaces", independantSpaceCount);
        insights.put("spaces", spaceCount);
        insights.put("floors", floorCount);
        insights.put("buildings", buildingCount);
        insights.put("assets", assetCount);
        insights.put("sub_spaces", subSpaceCount);
        insights.put("meters", meterCount);
        context.put(FacilioConstants.ContextNames.COUNT, insights);
        return false;
    }

    private List<Long> getAllSpaceIds(long parentBaseSpaceId, String moduleName) throws Exception{
        FacilioModule baseSpaceModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.BASE_SPACE);
        List<FacilioField> baseSpaceFields = Constants.getModBean().getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
        Map<String,FacilioField> baseSpaceFieldMap = FieldFactory.getAsMap(baseSpaceFields);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singletonList(FieldFactory.getIdField(baseSpaceModule)))
                .table(baseSpaceModule.getTableName());
        if(!moduleName.equalsIgnoreCase(FacilioConstants.ContextNames.SPACE)){
                selectRecordBuilder.andCondition(CriteriaAPI.getCondition(baseSpaceFieldMap.get(moduleName),String.valueOf(parentBaseSpaceId),StringOperators.IS));
        }else{
            selectRecordBuilder
                    .orCondition(CriteriaAPI.getCondition(baseSpaceFieldMap.get("space1"),String.valueOf(parentBaseSpaceId),StringOperators.IS))
                    .orCondition(CriteriaAPI.getCondition(baseSpaceFieldMap.get("space2"),String.valueOf(parentBaseSpaceId),StringOperators.IS))
                    .orCondition(CriteriaAPI.getCondition(baseSpaceFieldMap.get("space3"),String.valueOf(parentBaseSpaceId),StringOperators.IS))
                    .orCondition(CriteriaAPI.getCondition(baseSpaceFieldMap.get("space4"),String.valueOf(parentBaseSpaceId),StringOperators.IS))
                    .orCondition(CriteriaAPI.getCondition(baseSpaceFieldMap.get("space5"),String.valueOf(parentBaseSpaceId),StringOperators.IS));
        }
        List<Map<String,Object>> props = selectRecordBuilder.get();
        List<Long> spaceIds = new ArrayList<>();
        for(Map<String,Object> prop:props){
            spaceIds.add((Long) prop.get("id"));
        }
        if(!spaceIds.contains(parentBaseSpaceId)){
            spaceIds.add(parentBaseSpaceId);
        }
        return spaceIds;
    }

    private long getAllSpaces(long siteId, long spaceType) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .select(new HashSet<>())
                .module(module)
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(module))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("spaceType"), String.valueOf(spaceType), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(module), String.valueOf(siteId), NumberOperators.EQUALS));

        List<Map<String, Object>> props = builder.getAsProps();
        long count = 0;
        if (CollectionUtils.isNotEmpty(props)) {
            count = ((Number) props.get(0).get("id")).longValue();
        }
        return count;

    }

    private long getAllSpacesCount(long buildingId, String field, BaseSpaceContext.SpaceType spaceType) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .select(new HashSet<>())
                .module(module)
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(module))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("spaceType"), String.valueOf(spaceType.getIntVal()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(field), String.valueOf(buildingId), NumberOperators.EQUALS));

        List<Map<String, Object>> props = builder.getAsProps();
        long count = 0;
        if (CollectionUtils.isNotEmpty(props)) {
            count = ((Number) props.get(0).get("id")).longValue();
        }
        return count;

    }

    private static long getBaseSpaceWithChildren(List<Long> spaceIDs) throws Exception {
        if (spaceIDs != null && !spaceIDs.isEmpty()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            Criteria criteria = new Criteria();
            criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("space1"), spaceIDs, NumberOperators.EQUALS));
            criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("space2"), spaceIDs, NumberOperators.EQUALS));
            criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("space3"), spaceIDs, NumberOperators.EQUALS));

            SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
                    .table(module.getTableName())
                    .moduleName(module.getName())
                    .beanClass(BaseSpaceContext.class)
                    .andCriteria(criteria);
            selectBuilder.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));
            List<BaseSpaceContext> spaces = selectBuilder.get();
            return spaces.get(0).getId();
        }
        return -1;
    }
}
