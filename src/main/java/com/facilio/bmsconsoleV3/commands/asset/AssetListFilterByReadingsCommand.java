package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.stream.Collectors;

public class AssetListFilterByReadingsCommand  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3AssetContext> assetList = (List<V3AssetContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get("asset"));
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);

        if(CollectionUtils.isNotEmpty(assetList) && MapUtils.isNotEmpty(queryParams)){
            List<Long> assetIdList = assetList.stream().map(V3AssetContext::getId).collect(Collectors.toList());
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());


            List<FacilioField> fields = Collections.singletonList(fieldMap.get("resourceId"));

            Boolean assetWithReadings = queryParams.containsKey("withReadings") ? Boolean.valueOf(queryParams.get("withReadings").get(0).toString()) : false;
            Boolean assetWithWritableReadings = queryParams.containsKey("withWritableReadings") ? Boolean.valueOf(queryParams.get("withWritableReadings").get(0).toString()) : false;

            Long readingId = queryParams.containsKey("readingId") ? Long.valueOf(queryParams.get("readingId").get(0).toString()) : -1;
            Integer inputType =  queryParams.containsKey("inputType") ? Integer.valueOf(queryParams.get("inputType").get(0).toString()) : -1;

            Boolean assetWithReadingId = readingId > 0;

            if(assetWithReadings || assetWithWritableReadings || assetWithReadingId) {
                List<Long> assetWithRdIdList = new ArrayList<>();
                GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                        .select(fields)
                        .table(ModuleFactory.getReadingDataMetaModule().getTableName())
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), assetIdList, NumberOperators.EQUALS));

                if(assetWithReadingId){
                    builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), readingId.toString(), NumberOperators.EQUALS))
                            .andCondition(CriteriaAPI.getCondition(fieldMap.get("inputType"),inputType.toString(), NumberOperators.EQUALS));
                }
                if(assetWithReadings || assetWithWritableReadings){

                    builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("value"), "-1", StringOperators.ISN_T))
                            .andCondition(CriteriaAPI.getCondition(fieldMap.get("value"), CommonOperators.IS_NOT_EMPTY));

                    if(assetWithWritableReadings){
                        builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("isControllable"), Boolean.TRUE.toString(), BooleanOperators.IS));
                    }

                }

                List<Map<String, Object>> props = builder.get();

                for (Map<String, Object> prop : props) {
                    assetWithRdIdList.add((Long) prop.get("resourceId"));
                }
                assetList = assetList.stream().filter(at -> assetWithRdIdList.contains(at.getId())).collect(Collectors.toList());
            }

            Map<String,Object> recordMap = new HashMap<String, Object>();

            recordMap.put("asset", assetList);

            context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
        }


        return false;
    }
}
