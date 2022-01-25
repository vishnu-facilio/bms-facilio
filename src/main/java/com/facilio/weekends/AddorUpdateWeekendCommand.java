package com.facilio.weekends;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddorUpdateWeekendCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        WeekendContext weekend = (WeekendContext) context.get(FacilioConstants.ContextNames.WEEKEND);
        FacilioModule weekendsModule = ModuleFactory.getWeekendsModule();
        long id = weekend.getId();
        Map<String, Object> props = FieldUtil.getAsProperties(weekend);
        if(id > 0)
        {
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(weekendsModule.getTableName())
                    .fields(FieldFactory.getWeekendsFields(weekendsModule))
                    .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(weekend.getId()), NumberOperators.EQUALS));
            updateRecordBuilder.update(props);
        }
        else
        {
            GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
                    .table(weekendsModule.getTableName())
                    .fields(FieldFactory.getWeekendsFields(weekendsModule));
            id = insert.insert(props);
        }
        weekend.setId(id);
        return false;
    }
}
