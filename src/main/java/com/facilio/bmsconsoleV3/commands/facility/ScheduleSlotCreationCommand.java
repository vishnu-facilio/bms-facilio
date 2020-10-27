package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduleSlotCreationCommand extends FacilioCommand {
    private boolean isEdit = false;

    public ScheduleSlotCreationCommand(boolean b) {
        this.isEdit = b;
    }

    public ScheduleSlotCreationCommand() {}

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FacilityContext> facilities = recordMap.get(moduleName);
        List<Long> facilityIds = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(facilities)) {
            facilities.forEach(i -> facilityIds.add(i.getId()));
        }

        int delay = 0;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY);

        for (FacilityContext facility : facilities) {
            BmsJobUtil.scheduleOneTimeJobWithProps(facility.getId(), "CreateSlotForFacilities", delay, "priority", null);
        }
        return false;
    }
}
