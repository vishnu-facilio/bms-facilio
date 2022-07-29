package com.facilio.bmsconsole.commands;


import com.facilio.bmsconsole.context.ApplicationRelatedAppsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddApplicationRelatedAppsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> relatedApplicationIds = (List<Long>) context.get(FacilioConstants.ContextNames.APPLICATION_RELATED_APPS_LIST);
        Long appId=(Long)context.get(FacilioConstants.ContextNames.APPLICATION_ID);
        FacilioUtil.throwIllegalArgumentException(appId<=0||appId==null,"Application id cannot be empty");
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(relatedApplicationIds),"Related Application id cannot be empty");

        List<ApplicationRelatedAppsContext> relatedApplications=new ArrayList<>();
        for(Long relatedApplicationId:relatedApplicationIds){
            ApplicationRelatedAppsContext applicationRelatedApp=new ApplicationRelatedAppsContext();
            applicationRelatedApp.setApplicationId(appId);
            applicationRelatedApp.setRelatedApplicationId(relatedApplicationId);
            relatedApplications.add(applicationRelatedApp);
        }
        List<Map<String, Object>> props = FieldUtil.getAsMapList(relatedApplications, ApplicationRelatedAppsContext.class);
           GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getApplicationRelatedAppsModule().getTableName())
                .fields(FieldFactory.getApplicationRelatedAppsModuleFields());
        builder.addRecords(props);
        builder.save();

        return false;
    }
}
