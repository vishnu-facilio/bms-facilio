package com.facilio.fsm.commands.serviceOrders;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceTaskContext;
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

public class SetServiceTaskCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        ServiceOrderContext serviceOrder = (ServiceOrderContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.SERVICE_ORDER, id);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String taskModuleName = FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK;
        List<FacilioField> fields = modBean.getAllFields(taskModuleName);

        SelectRecordsBuilder<ServiceTaskContext> builder = new SelectRecordsBuilder<ServiceTaskContext>()
                .moduleName(taskModuleName)
                .select(fields)
                .beanClass(ServiceTaskContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_ORDER", "serviceOrder", String.valueOf(id), NumberOperators.EQUALS));
        List<ServiceTaskContext> list = builder.get();
        if(CollectionUtils.isNotEmpty(list)){
            List<Long> serviceTaskIds = list.stream().map(serviceTask -> serviceTask.getId()).collect(Collectors.toList());
            FacilioContext serviceTask = V3Util.getSummary(taskModuleName,serviceTaskIds);
            List<ServiceTaskContext> serviceTasks  =((Map<String, List>) serviceTask.get("recordMap")).get(taskModuleName);
            serviceOrder.setServiceTask(serviceTasks);
        }

        return false;
    }
}
