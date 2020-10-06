package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAllWebTabGroupCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        Long layoutId = (Long) context.get(FacilioConstants.ContextNames.LAYOUT_ID);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .innerJoin(ModuleFactory.getApplicationLayoutModule().getTableName())
                .on("Application_Layout.ID = WebTab_Group.LAYOUT_ID")
                .select(FieldFactory.getWebTabGroupFields());
        if (appId == null || appId < 0) {
            builder.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", "", CommonOperators.IS_EMPTY));
        }
        else {
            builder.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS));
        }
        if(layoutId != null && layoutId > 0){
            builder.andCondition(CriteriaAPI.getCondition("LAYOUT_ID", "layoutId", String.valueOf(layoutId), NumberOperators.EQUALS));
        }
        List<WebTabGroupContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabGroupContext.class);
        context.put(FacilioConstants.ContextNames.WEB_TAB_GROUPS, webTabGroups);

        return false;
    }
}
