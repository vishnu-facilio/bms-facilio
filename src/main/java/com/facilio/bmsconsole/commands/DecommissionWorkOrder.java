package com.facilio.bmsconsole.commands;
import com.damnhandy.uri.template.impl.Operator;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.WorkOrderAction;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.actions.PlannedMaintenanceAction;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Level;
import org.apache.commons.chain.Context;
import org.apache.log4j.lf5.LogLevel;

import java.lang.reflect.Field;
import java.util.*;

import static com.facilio.modules.ModuleFactory.getPMResourcePlannerModule;

@Log4j
public class DecommissionWorkOrder extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.error("Decommission Work Order chain + Resource -"+ context.get(FacilioConstants.ContextNames.RESOURCE_ID) + " type of --" + context.get(FacilioConstants.ContextNames.RESOURCE_ID).getClass());
        String resource = String.valueOf(context.get(FacilioConstants.ContextNames.RESOURCE_ID));
        Long resourceId = FacilioUtil.parseLong(resource);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        DeleteRecordBuilder<WorkOrderContext> delete = new DeleteRecordBuilder<WorkOrderContext>()
                .module(workOrderModule)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("jobStatus"), String.valueOf(PMJobsContext.PMJobsStatus.ACTIVE.getValue()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(FacilioConstants.ContextNames.RESOURCE_ID, "resourceId", String.valueOf(resourceId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), CommonOperators.IS_EMPTY))
                .skipModuleCriteria();
        delete.markAsDelete();

        LOGGER.log(Level.ERROR, "All Preopen WorkOrders against the resource(" + resourceId +") has been deleted as the resource is decommissioned");

        return false;
    }
}

