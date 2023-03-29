package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

public class GetImportListCountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        Long count = MultiImportApi.getImportListCount(searchString, filterCriteria);

        context.put(Constants.COUNT, count);
        return false;
    }
}
