package com.facilio.bmsconsoleV3.actions.utils;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Action;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

/**
 * BulkActions class implements following Bulk Actions,
 * - PATCH:
 *      For example, If first 50 items are shown in a page out of 500 items, using pagination,
 *      and we want to update a field in all 500 items, then this endpoint is for this action.
 *      Endpoint: /api/v3/bulkActionPatch/{$MODULE_NAME}
 *      Body: {
 *                  "moduleName": "$MODULE_NAME",
 *                  "data": {
 *                      "$MODULE_NAME": {
 *                      "field_name_1": "value",
 *                      "field_name_2": {
 *                              "id": {$ID}
 *                       },
 *                      "field_name_3": {
 *                              "id": {$ID}
 *                          }
 *                      }
 *                  }
 *              }
 *
 *  - DELETE:
 *          For example, If first 50 items are shown in a page out of 500 items, using pagination,
 *          and we want to delete all 500 items, then this endpoint is for this action.
 *          Endpoint: /api/v3/bulkActionDelete/{$MODULE_NAME}
 */
@Data
@Getter
@Setter
public class BulkActions extends V3Action {

    /**
     * bulkActionPatch() is called when we hit the endpoint -> /api/v3/bulkActionPatch/{$MODULE_NAME}
     * MODULE_NAME - Mandatory
     * DATA - JSONObject type, with key as MODULE_NAME is required.
     *
     * RESPONSE:{
     *              "code": 0,
     *              "data": {
     *                  "rowsUpdated": $N,
     *                  "moduleName": "MODULE_NAME"
     *              }
     *          }
     * @return
     * @throws Exception
     */
	
	public String dataModuleName;
	
	
	public String bulkActionCreate() throws Exception {

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.DATA_MODULE_NAME, getDataModuleName());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.DATA, getData());

        if (StringUtils.isNotEmpty(getFilters())) {
            JSONObject json = FacilioUtil.parseJson(getFilters());
            context.put(FacilioConstants.ContextNames.FILTERS, json);
        }

        FacilioChain facilioChain = ReadOnlyChainFactory.getBulkActionCreateChain();
        FacilioContext chainFacilioContext = facilioChain.getContext();
        chainFacilioContext.putAll(context);
        facilioChain.execute();

        setData(null);
        setData("moduleName", getModuleName());
        setData("rowsAdded", chainFacilioContext.get(FacilioConstants.ContextNames.ROWS_ADDED));

        return SUCCESS;
    }
	
    public String bulkActionPatch() throws Exception {

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.DATA, getData());

        if (StringUtils.isNotEmpty(getFilters())) {
            JSONObject json = FacilioUtil.parseJson(getFilters());
            context.put(FacilioConstants.ContextNames.FILTERS, json);
        }

        FacilioChain facilioChain = ReadOnlyChainFactory.getBulkActionPatchChain();
        FacilioContext chainFacilioContext = facilioChain.getContext();
        chainFacilioContext.putAll(context);
        facilioChain.execute();

        setData(null);
        setData("moduleName", getModuleName());
        setData("rowsUpdated", chainFacilioContext.get(FacilioConstants.ContextNames.ROWS_UPDATED));

        return SUCCESS;
    }


    /**
     * bulkActionDelete() is called when we hit the endpoint -> /api/v3/bulkActionDelete/{$MODULE_NAME}
     * MODULE_NAME - Mandatory
     *
     * RESPONSE:{
     *              "code": 0,
     *              "data": {
     *                  "rowsDeleted": $N,
     *                  "moduleName": "MODULE_NAME"
     *              }
     *          }
     * @return
     * @throws Exception
     */
    public String bulkActionDelete() throws Exception {

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());

        if (StringUtils.isNotEmpty(getFilters())) {
            JSONObject json = FacilioUtil.parseJson(getFilters());
            context.put(FacilioConstants.ContextNames.FILTERS, json);
        }

        FacilioChain facilioChain = ReadOnlyChainFactory.getBulkActionDeleteChain();
        FacilioContext chainFacilioContext = facilioChain.getContext();
        chainFacilioContext.putAll(context);
        facilioChain.execute();

        setData(null);
        setData("moduleName", getModuleName());
        setData("rowsDeleted", chainFacilioContext.get(FacilioConstants.ContextNames.ROWS_UPDATED));

        return SUCCESS;
    }
}
