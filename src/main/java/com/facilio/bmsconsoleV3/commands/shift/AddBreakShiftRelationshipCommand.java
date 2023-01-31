package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.Break;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.sql.SQLException;
import java.util.*;


public class AddBreakShiftRelationshipCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        String moduleName = Constants.getModuleName(context);
        List<Break> breaks = recordMap.get(moduleName);

        if (FacilioUtil.isEmptyOrNull(breaks)) {
            return false;
        }

        for (Break br : breaks) {
            if (FacilioUtil.isEmptyOrNull(br.getShifts())) {
                continue;
            }
            addBreakShiftRelationship(br);
        }
        return false;
    }

    private void addBreakShiftRelationship(Break br) throws Exception {
        List<Map<String, Object>> relationships = new ArrayList<>();
        List<Shift> shiftsFromDB = new ArrayList<>();
        for (Shift shift : br.getShifts()) {
            Shift shiftFromDB = getShift(shift.getId());
            if (shiftFromDB == null) {
                throw new IllegalArgumentException("Invalid shift");
            }
            shiftsFromDB.add(shiftFromDB);

            Map<String, Object> link = new HashMap<>();
            link.put("shiftId", shift.getId());
            link.put("breakId", br.getId());
            relationships.add(link);
        }
        br.setShifts(shiftsFromDB);
        saveRelationships(relationships);
    }

    private void saveRelationships(List<Map<String, Object>> relationships) throws SQLException {
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getShiftBreakRelModule().getTableName())
                .fields(FieldFactory.getShiftBreakRelModuleFields());

        insertBuilder.addRecords(relationships);
        insertBuilder.save();
    }

    private Shift getShift(long shiftID) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SHIFT);
        SelectRecordsBuilder<Shift> builder = new SelectRecordsBuilder<Shift>()
                .beanClass(Shift.class)
                .module(module)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getIdCondition(shiftID, module));
        return builder.fetchFirst();
    }
}
