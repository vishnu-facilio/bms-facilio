package com.facilio.weekends;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Map;

public class GetWeekendCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        WeekendContext weekend = (WeekendContext) context.get(FacilioConstants.ContextNames.WEEKEND);
        long id = weekend.getId();
        if(id <=0 ) {
            throw new IllegalArgumentException("Invalid ID passed");
        }
        context.put(FacilioConstants.ContextNames.WEEKEND, WeekendUtil.getWeekend(id));
        return false;
    }
}
