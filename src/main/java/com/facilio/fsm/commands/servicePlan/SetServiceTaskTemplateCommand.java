package com.facilio.fsm.commands.servicePlan;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.ServicePlanContext;
import com.facilio.fsm.context.ServiceTaskTemplateContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetServiceTaskTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        ServicePlanContext servicePlan = (ServicePlanContext) CommandUtil.getModuleData(context, FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN, id);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String taskModuleName = FacilioConstants.ServicePlannedMaintenance.SERVICE_TASK_TEMPLATE;
        List<FacilioField> fields = modBean.getAllFields(taskModuleName);

        SelectRecordsBuilder<ServiceTaskTemplateContext> builder = new SelectRecordsBuilder<ServiceTaskTemplateContext>()
                .moduleName(taskModuleName)
                .select(fields)
                .beanClass(ServiceTaskTemplateContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_PLAN", "servicePlan", String.valueOf(id), NumberOperators.EQUALS));
        List<ServiceTaskTemplateContext> list = builder.get();
        if(CollectionUtils.isNotEmpty(list)){
            List<Long> serviceTaskIds = list.stream().map(serviceTask -> serviceTask.getId()).collect(Collectors.toList());
            FacilioContext serviceTask = V3Util.getSummary(taskModuleName,serviceTaskIds);
            List<ServiceTaskTemplateContext> serviceTasks  =((Map<String, List>) serviceTask.get("recordMap")).get(taskModuleName);
            servicePlan.setServiceTaskTemplate(serviceTasks);
        }

        return false;
    }
}
