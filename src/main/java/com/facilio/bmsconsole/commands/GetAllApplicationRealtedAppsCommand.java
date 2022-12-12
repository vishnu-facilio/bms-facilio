package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAllApplicationRealtedAppsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long appId=(Long)context.get(FacilioConstants.ContextNames.APPLICATION_ID);
        List<ApplicationContext> relatedApplication=null;
        FacilioUtil.throwIllegalArgumentException(appId<=0||appId==null,"Application id cannot be empty");


        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getApplicationFields())
                .table(ModuleFactory.getApplicationRelatedAppsModule().getTableName())
                .innerJoin(ModuleFactory.getApplicationModule().getTableName())
                .on("Application.ID=Application_Related_Apps.RELATED_APPLICATION_ID")
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID","applicationId",String.valueOf(appId),NumberOperators.EQUALS));
        relatedApplication=FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), ApplicationContext.class);


        ApplicationApi.setApplicationDomain(relatedApplication);

        context.put(FacilioConstants.ContextNames.RELATED_APPLICATIONS, relatedApplication);
        return false;
    }
}
