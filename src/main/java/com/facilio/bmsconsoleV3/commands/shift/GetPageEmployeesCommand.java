package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetPageEmployeesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modBean = Constants.getModBean();
        List<FacilioField> peopleFields = modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
        Map<String,FacilioField> peopleFieldMap = FieldFactory.getAsMap(peopleFields);
        
        validateProps(context);

        // list of employees needed only for calendar export
        Boolean exportList = (Boolean) context.get(FacilioConstants.Shift.EXPORT_LIST);
        if (exportList != null && exportList) {
            return false;
        }

        int page = (Integer) context.get(FacilioConstants.ContextNames.PAGE);
        int perPage = (Integer) context.get(FacilioConstants.ContextNames.PER_PAGE);

        String moduleName = FacilioConstants.ContextNames.PEOPLE;
        String viewName = "all";
        String filters = (String) context.get(FacilioConstants.ContextNames.FILTERS);
        String clientCriteria = null;
        Object orderByType =  context.get(FacilioConstants.ContextNames.ORDER_TYPE);
        Object orderBy =  context.get(FacilioConstants.ContextNames.REPORT_ORDER_BY);
        String search = null;
        Map<String, List<Object>> queryParameters = null;

        Criteria serverCriteria = new Criteria();
        serverCriteria.addAndCondition(CriteriaAPI.getCondition(peopleFieldMap.get(FacilioConstants.ContextNames.PEOPLE_TYPE),"2,3", NumberOperators.EQUALS));

        Boolean isV3 = true;
        Boolean excludeParent = true;
        Boolean withCount = true;

        FacilioContext listContext = V3Util.fetchList(moduleName, isV3, viewName, filters,
                excludeParent, clientCriteria, orderBy, orderByType,
                search, page, perPage, withCount, queryParameters, serverCriteria,null);

        Map<String, Object> recordMap = (Map<String, Object>) listContext.get("recordMap");
        context.put(FacilioConstants.ContextNames.PEOPLE, recordMap.get(moduleName));
        context.put(FacilioConstants.ContextNames.COUNT,
                listContext.get(FacilioConstants.ContextNames.COUNT));

        return false;
    }

    private void validateProps(Context context) {

        Integer page = (Integer) context.get(FacilioConstants.ContextNames.PAGE);
        if (page == null || page <= 0){
            throw new IllegalArgumentException("page is a mandatory prop");
        }

        Integer perPage = (Integer) context.get(FacilioConstants.ContextNames.PER_PAGE);
        if (perPage == null || perPage <= 0){
            throw new IllegalArgumentException("perPage is a mandatory prop");
        }

        Long rangeFrom = (Long) context.get(FacilioConstants.Shift.RANGE_FROM);
        if (rangeFrom == null || rangeFrom <= 0){
            throw new IllegalArgumentException("rangeFrom is a mandatory prop");
        }

        Long rangeTo = (Long) context.get(FacilioConstants.Shift.RANGE_TO);
        if (rangeTo == null || rangeTo <= 0){
            throw new IllegalArgumentException("rangeTo is a mandatory prop");
        }
    }
}
