package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetTimeLogCommand extends FacilioCommand {

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

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                 .select(FieldFactory.getTimeLogFields(null))
                 .table(module.getTableName())
                 .andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId", String.valueOf(id), NumberOperators.EQUALS));

        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        if (filterCriteria != null && !filterCriteria.isEmpty()) {
            builder.andCriteria(filterCriteria);
        }

        List<Map<String,Object>> timeLogs = builder.get();
        context.put(FacilioConstants.ContextNames.WORK_ORDER, timeLogs);
        return false;
    }

}
