package com.facilio.bmsconsoleV3.commands.basespace;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3ResourceAPI;
import com.facilio.bmsconsoleV3.util.V3SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.facilio.util.FacilioStreamUtil.distinctByKey;

public class FetchBasespaceChildrenCountCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
        List<Long> recordIds = (List<Long>) context.get("recordIds");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
        if (CollectionUtils.isNotEmpty(recordIds)) {
            for (Long id : recordIds) {
                Criteria criteria = V3SpaceAPI.getBaseSpaceChildrenCriteria(moduleName, id);
                if (MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("fetchChildCount")) {
                    List<V3BaseSpaceContext> basespaces = V3RecordAPI.getRecordsListWithSupplements(baseSpaceModule.getName(), null, V3BaseSpaceContext.class, criteria, null);
                    List<Long> basespaceIds = new ArrayList<>();
                    List<Long> assetIds = new ArrayList<>();
                    List<Long> allResourceIds = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(basespaces)) {
                        basespaceIds = basespaces.stream().map(V3BaseSpaceContext::getId).collect(Collectors.toList());
                    }
                    basespaceIds.add(id);
                    List<V3AssetContext> assets = V3ResourceAPI.getAssetsForSpaces(basespaceIds);
                    if(CollectionUtils.isNotEmpty(assets))  {
                        assetIds = assets.stream().map(V3AssetContext::getId).collect(Collectors.toList());
                    }
                    allResourceIds.addAll(basespaceIds);
                    allResourceIds.addAll(assetIds);

                    List<PMResourcePlannerContext> pms = PreventiveMaintenanceAPI.getPMForResources(allResourceIds);
                    Long spaceUsageInAccessibleSpaces = getAccessibleSpaceUsageCount(basespaceIds);
                    if (spaceUsageInAccessibleSpaces != null && spaceUsageInAccessibleSpaces > 0 || CollectionUtils.isNotEmpty(pms)) {
                        JSONObject errorObject = new JSONObject();
                        if(CollectionUtils.isNotEmpty(pms)){
                            pms = pms.stream().filter(distinctByKey(PMResourcePlannerContext::getPmId)).collect(Collectors.toList());
                            errorObject.put("Planned Maintenance",pms.size());
                        }
                        if(spaceUsageInAccessibleSpaces != null && spaceUsageInAccessibleSpaces > 0){
                            errorObject.put("Accessible Space" + (spaceUsageInAccessibleSpaces > 1 ? "s" : ""),spaceUsageInAccessibleSpaces);
                        }
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, errorObject.toString());
                    }

                    if (CollectionUtils.isNotEmpty(basespaces)) {
                        JSONObject warnMessage = constructSpaceWarningMessage(basespaces, moduleName);
                        if(CollectionUtils.isNotEmpty(assetIds)){
                            long assetsCount = assetIds.size();
                            warnMessage.put("Asset" + (assetsCount > 1 ? "s" : "") ,assetIds.size());
                        }
                        throw new RESTException(ErrorCode.DEPENDENCY_EXISTS, warnMessage.toString());
                    }
                }
            }
        }
        return false;
    }
    private Long getAccessibleSpaceUsageCount(List<Long> spaceIds) throws Exception {
        FacilioField spaceField = FieldFactory.getAsMap(AccountConstants.getAccessbileSpaceFields()).get("bsid");
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAccessibleSpaceModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("BS_ID", "bsid", StringUtils.join(spaceIds,","), NumberOperators.EQUALS))
                .select(new HashSet<>())
                .groupBy("Accessible_Space.SITE_ID")
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, spaceField);
        List<Map<String, Object>> props = selectBuilder.get();
        if (props.size() > 0) {
            return (long) props.get(0).get("bsid");
        }
        return null;
    }
    private JSONObject constructSpaceWarningMessage(List<V3BaseSpaceContext> basespaces,String moduleName) {
        Long buildingCount = 0l,floorCount = 0l,spaceCount = 0l,independentSpaceCount = 0l;
        JSONObject warnMessage = new JSONObject();
        for (V3BaseSpaceContext basespace : basespaces){
            if(basespace.getSpaceTypeEnum() == V3BaseSpaceContext.SpaceType.BUILDING){
                buildingCount++;
            }
            if(basespace.getSpaceTypeEnum() == V3BaseSpaceContext.SpaceType.FLOOR){
                floorCount++;
            }
            if(basespace.getSpaceTypeEnum() == V3BaseSpaceContext.SpaceType.SPACE) {
                spaceCount++;
            }
            if(moduleName.equals(FacilioConstants.ContextNames.SITE) && basespace.getBuilding() == null){
                independentSpaceCount++;
            }
        }
        if(buildingCount != null && buildingCount > 0){
            warnMessage.put("Building" + (buildingCount > 1 ? "s" : "") , buildingCount);
        }
        if(floorCount != null && floorCount > 0){
            warnMessage.put("Floor" + (floorCount > 1 ? "s" : "") , floorCount);
        }
        if(spaceCount != null && spaceCount > 0){
            warnMessage.put("Space" + (spaceCount > 1 ? "s" : "") , spaceCount);
        }
        if(independentSpaceCount != null && independentSpaceCount > 0){
            warnMessage.put("Independent Space" + (independentSpaceCount > 1 ? "s" : "") , independentSpaceCount);
        }

        return warnMessage;
    }
}
