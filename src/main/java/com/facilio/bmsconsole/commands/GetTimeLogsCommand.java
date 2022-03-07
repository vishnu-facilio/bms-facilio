package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TimelogContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.*;

public class GetTimeLogsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if(moduleName == null){
            throw new IllegalArgumentException("Module cannot be empty");
        }

        long id = (long) context.get(FacilioConstants.ContextNames.ID);
        if(id <= 0){
            throw new IllegalArgumentException("Id is not found");
        }
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        String parentModueName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
        FacilioModule parentModule = moduleBean.getModule(parentModueName);
        List<SupplementRecord> supplementRecords = new ArrayList<>();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(moduleBean.getAllFields(moduleName));
        supplementRecords.add((SupplementRecord) fieldMap.get("fromStatus"));
        supplementRecords.add((SupplementRecord) fieldMap.get("doneBy"));

        SelectRecordsBuilder<TimelogContext> builder = new SelectRecordsBuilder<TimelogContext>()
                 .module(module)
                 .beanClass(TimelogContext.class)
                 .select(moduleBean.getAllFields(moduleName))
                 .andCondition(CriteriaAPI.getCondition("PARENT_ID","parent", String.valueOf(id), NumberOperators.EQUALS))
                 .fetchSupplements(supplementRecords)
                 .orderBy("ID DESC");

        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        if (filterCriteria != null && !filterCriteria.isEmpty()) {
            builder.andCriteria(filterCriteria);
        }
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }

            builder.offset(offset);
            builder.limit(perPage);
        }

        List<TimelogContext> timeLogs = builder.get();

        context.put(FacilioConstants.ContextNames.TIMELOGS, timeLogs);
        return false;
    }


}
