package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAllApplicationLayoutsCommand extends FacilioCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationLayoutModule().getTableName())
                .select(FieldFactory.getApplicationLayoutFields());
        if (appId == null || appId < 0) {
            throw new IllegalArgumentException("Application id cannot be empty");
        }
        builder.andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(appId), NumberOperators.EQUALS));

        List<ApplicationLayoutContext> layouts = FieldUtil.getAsBeanListFromMapList(builder.get(), ApplicationLayoutContext.class);
        context.put(FacilioConstants.ContextNames.APPLICATION_LAYOUT, layouts);

        return false;
    }
}
