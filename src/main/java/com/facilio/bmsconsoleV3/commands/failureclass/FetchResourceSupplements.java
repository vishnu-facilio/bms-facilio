package com.facilio.bmsconsoleV3.commands.failureclass;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchResourceSupplements extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
        if (MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("fetchChildCount")) {
            List<Long> recordIds = (List<Long>) context.get("recordIds");
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Long buildingCount = 0l, floorCount = 0l, spaceCount = 0l, siteCount = 0l;
            List<FacilioField> resourcefields = modBean.getAllFields("resource");
            List<FacilioField> fields = new ArrayList<>();
            fields.addAll(resourcefields);
            fields.addAll(modBean.getAllFields("basespace"));
            for (Long id : recordIds) {
                List<V3BaseSpaceContext> baseSpace = getSelectBuilder("BaseSpace",fields,"Resources.ID = BaseSpace.ID",String.valueOf(id), V3BaseSpaceContext.class).get();
                List<V3AssetContext> assets = getSelectBuilder("Assets",resourcefields,"Resources.ID = Assets.ID",String.valueOf(id), V3AssetContext.class).get();
                JSONObject warnMessage = new JSONObject();
                if (CollectionUtils.isNotEmpty(baseSpace)) {
                    for (V3BaseSpaceContext prop : baseSpace) {
                        if (prop.getSpaceType() == 1) {
                            siteCount++;
                        } else if (prop.getSpaceType() == 2) {
                            buildingCount++;
                        } else if (prop.getSpaceType() == 3) {
                            floorCount++;
                        } else if (prop.getSpaceType() == 4) {
                            spaceCount++;
                        }
                    }
                    if (siteCount != null && siteCount > 0) {
                        warnMessage.put("Site" + (siteCount > 1 ? "s" : ""), siteCount);
                    }
                    if (buildingCount != null && buildingCount > 0) {
                        warnMessage.put("Building" + (buildingCount > 1 ? "s" : ""), buildingCount);
                    }
                    if (floorCount != null && floorCount > 0) {
                        warnMessage.put("Floor" + (floorCount > 1 ? "s" : ""), floorCount);
                    }
                    if (spaceCount != null && spaceCount > 0) {
                        warnMessage.put("Space" + (spaceCount > 1 ? "s" : ""), spaceCount);
                    }
                }
                if(CollectionUtils.isNotEmpty(assets)){
                    Long assetCount = Long.valueOf(assets.size());
                    warnMessage.put("Asset" + (assetCount > 1 ? "s" : ""), assetCount);
                }
               if(!warnMessage.isEmpty()) {
                   throw new RESTException(ErrorCode.DEPENDENCY_EXISTS, warnMessage.toString());
               }
            }
        }
        return false;
    }
    public static <T extends ModuleBaseWithCustomFields> SelectRecordsBuilder<T> getSelectBuilder(String tableNameToJoin, List<FacilioField> fields, String condition, String id,Class<T> beanClass) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<T> selectBuilder = new SelectRecordsBuilder()
                .module(modBean.getModule("resource"))
                .table("Resources")
                .select(fields)
                .beanClass(beanClass)
                .innerJoin(tableNameToJoin)
                .on(condition)
                .andCondition(CriteriaAPI.getCondition("Resources.FAILURE_CLASS", "failureclass", String.valueOf(id), NumberOperators.EQUALS));

        return selectBuilder;
    }
}
