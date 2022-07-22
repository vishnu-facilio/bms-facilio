package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.plannedmaintenance.ExecuteNowExecutor;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PMExecuteNowContextCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long plannerId = (long) context.get("plannerId");
        PMPlanner pmPlanner = (PMPlanner) V3Util.getRecord("pmPlanner", plannerId, new HashMap<>());
        long pmId = pmPlanner.getPmId();
        PlannedMaintenance plannedmaintenance = (PlannedMaintenance) V3Util.getRecord("plannedmaintenance", pmId, new HashMap<>());
        ExecuteNowExecutor executeNowExecutor = new ExecuteNowExecutor();
        List<V3WorkOrderContext> workOrderContexts = executeNowExecutor.execute(context, plannedmaintenance, pmPlanner);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannedmaintenanceMod = modBean.getModule("plannedmaintenance");
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = workOrderContexts.stream().map(i -> (ModuleBaseWithCustomFields) i).collect(Collectors.toList());
        V3Util.createRecord(plannedmaintenanceMod, moduleBaseWithCustomFields);
        return false;
    }
}
