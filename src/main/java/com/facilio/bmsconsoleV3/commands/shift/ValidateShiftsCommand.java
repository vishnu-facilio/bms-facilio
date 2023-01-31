package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class ValidateShiftsCommand extends FacilioCommand {

    private boolean emptyShiftNameBreach(Shift shift) {
        return StringUtils.isEmpty(shift.getName());
    }

    private boolean emptyStartAndEndTimeBreach(Shift s) {
        return s.getStartTime() == null || s.getEndTime() == null;
    }
    
    private boolean duplicateShiftNameBreach(Shift shift) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Shift.SHIFT);

        SelectRecordsBuilder<Shift> builder = new SelectRecordsBuilder<Shift>()
                .beanClass(Shift.class)
                .module(module)
                .select(Collections.singletonList(FieldFactory.getIdField(module)))
                .andCondition(CriteriaAPI.getCondition("NAME", "name", shift.getName(), StringOperators.IS));

        if (shift.getId() > 0) {
            builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(shift.getId()), NumberOperators.NOT_EQUALS));
        }

        List<Shift> list = builder.get();
        return CollectionUtils.isNotEmpty(list) && list.size() > 0;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<Shift> shifts = recordMap.get(moduleName);

        for (Shift s : shifts) {
            if (emptyShiftNameBreach(s)) {
                throw new IllegalArgumentException("Name is mandatory");
            }
            if (emptyStartAndEndTimeBreach(s)) {
                throw new IllegalArgumentException("Shift start and end time are mandatory");
            }
            if (duplicateShiftNameBreach(s)) {
                throw new IllegalArgumentException("Shift with name already exists");
            }

        }
        return false;
    }


}
