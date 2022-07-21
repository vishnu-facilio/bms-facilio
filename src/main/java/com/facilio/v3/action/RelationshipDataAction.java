package com.facilio.v3.action;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.RESTAPIHandler;
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
import java.util.logging.Logger;

//TODO remove static methods, instantiate as object, better when testing
public class RelationshipDataAction extends RESTAPIHandler {
    private static final Logger LOGGER = Logger.getLogger(RelationshipDataAction.class.getName());

    public String associateRelation() throws Exception {

        FacilioChain relationDataChain = TransactionChainFactoryV3.getRelationAPIDataChain();
        FacilioContext relationContext = relationDataChain.getContext();
        relationContext.put(FacilioConstants.ContextNames.DATA, getData());
        relationContext.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        relationContext.put(Constants.QUERY_PARAMS, getQueryParameters());
        relationDataChain.execute();

        this.setModuleName((String) relationContext.get(FacilioConstants.ContextNames.RELATION_MODULE_NAME));

        List<Map<String, Object>> recordList = (List<Map<String, Object>>) getData().get(getModuleName());

        // remove restricted fields
        removeRestrictedFields(recordList, getModuleName(), true);

        FacilioModule module = ChainUtil.getModule(getModuleName());
        FacilioContext context = V3Util.createRecordList(module, recordList, getParams(), getQueryParameters());
        List<ModuleBaseWithCustomFields> addedRecords = Constants.getRecordList(context);
        JSONObject result = new JSONObject();

        V3Config v3Config = ChainUtil.getV3Config(module.getName());
        result.put(getModuleName(), FieldUtil.getAsMapList(addedRecords, ChainUtil.getBeanClass(v3Config, module)));
        setData(result);

        return SUCCESS;
    }

    public String dissociateRelation() throws Exception {

        FacilioChain relationDataChain = TransactionChainFactoryV3.getRelationDeleteAPIDataChain();
        FacilioContext relationContext = relationDataChain.getContext();
        relationContext.put(FacilioConstants.ContextNames.DATA, getData());
        relationContext.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        relationContext.put(Constants.QUERY_PARAMS, getQueryParameters());
        relationDataChain.execute();

        this.setModuleName((String) relationContext.get(FacilioConstants.ContextNames.RELATION_MODULE_NAME));

        FacilioContext context = V3Util.deleteRecords(getModuleName(), getData(), getParams(), getQueryParameters(), false);
        Map<String, Integer> countMap = Constants.getCountMap(context);
        if (MapUtils.isEmpty(countMap)) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        this.setData(FieldUtil.getAsJSON(countMap));
        return SUCCESS;
    }


}
