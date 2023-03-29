package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.util.MultiImportApi;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;

public class ListMultiImportDataDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        List<ImportDataDetails> importList = MultiImportApi.getImportList(pagination, searchString, filterCriteria);

        context.put(FacilioConstants.ContextNames.IMPORT_LIST, importList);


        return false;
    }
}
