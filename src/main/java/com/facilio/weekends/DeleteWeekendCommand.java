package com.facilio.weekends;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class DeleteWeekendCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        WeekendContext weekend = (WeekendContext) context.get(FacilioConstants.ContextNames.WEEKEND);
        FacilioModule weekendsModule = ModuleFactory.getWeekendsModule();
        long id = weekend.getId();
        if(id <= 0) {
            throw new IllegalArgumentException("Invalid weekend details passed");
        }
        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(weekendsModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(weekend.getId()), NumberOperators.EQUALS));
        deleteBuilder.delete();

        return false;
    }
}
