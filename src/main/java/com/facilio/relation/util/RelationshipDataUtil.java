package com.facilio.relation.util;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class RelationshipDataUtil {

    public static JSONObject associateRelation(String moduleName, JSONObject data, Map<String, List<Object>> queryParameters, JSONObject params) throws Exception {
        FacilioChain relationDataChain = TransactionChainFactoryV3.getRelationAPIDataChain();
        FacilioContext relationContext = relationDataChain.getContext();
        relationContext.put(FacilioConstants.ContextNames.DATA, data);
        relationContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        relationContext.put(Constants.QUERY_PARAMS, queryParameters);
        relationDataChain.execute();

        moduleName = (String) relationContext.get(FacilioConstants.ContextNames.RELATION_MODULE_NAME);

        List<Map<String, Object>> recordList = (List<Map<String, Object>>) data.get(moduleName);

        // remove restricted fields
        V3Util.removeRestrictedFields(recordList, moduleName, true);

        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(module.getName());
        FacilioContext context = V3Util.createRecordList(module, recordList, params, queryParameters);

        List<ModuleBaseWithCustomFields> addedRecords = Constants.getRecordList(context);
        JSONObject result = new JSONObject();
        result.put(moduleName, FieldUtil.getAsMapList(addedRecords, ChainUtil.getBeanClass(v3Config, module)));

        return result;
    }

    public static JSONObject dissociateRelation(String moduleName, JSONObject data, Map<String, List<Object>> queryParameters, JSONObject params) throws Exception {

        FacilioChain relationDataChain = TransactionChainFactoryV3.getRelationDeleteAPIDataChain();
        FacilioContext relationContext = relationDataChain.getContext();
        relationContext.put(FacilioConstants.ContextNames.DATA, data);
        relationContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        relationContext.put(Constants.QUERY_PARAMS, queryParameters);
        relationDataChain.execute();

        moduleName = (String) relationContext.get(FacilioConstants.ContextNames.RELATION_MODULE_NAME);

        FacilioContext context = V3Util.deleteRecords(moduleName, data, params, queryParameters, false);
        Map<String, Integer> countMap = Constants.getCountMap(context);
        if (MapUtils.isEmpty(countMap)) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, "Relationship not present");
        }
        return FieldUtil.getAsJSON(countMap);
    }

}
