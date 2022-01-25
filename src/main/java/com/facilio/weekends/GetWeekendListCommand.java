package com.facilio.weekends;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetWeekendListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        FacilioModule weekendModule = ModuleFactory.getWeekendsModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getWeekendsFields(weekendModule))
                .table(weekendModule.getTableName());

        List<Map<String, Object>> weekendProps = builder.get();
        List<WeekendContext> weekendList = FieldUtil.getAsBeanListFromMapList(weekendProps, WeekendContext.class);
        context.put(FacilioConstants.ContextNames.WEEKEND_LIST, weekendList);
        return false;
    }
}
