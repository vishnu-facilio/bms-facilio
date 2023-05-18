package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class ChangeViewStatusCommand extends FacilioCommand {
    private static final Logger log = LogManager.getLogger(ChangeViewStatusCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.EXISTING_CV);
        Boolean viewStatus = (Boolean) context.get(FacilioConstants.ContextNames.VIEW_STATUS);
        long viewId = (long) context.get(FacilioConstants.ContextNames.VIEWID);
        Map<String, Object> viewProp = new HashMap<>();
        viewProp.put("status", viewStatus);
        FacilioModule viewModule = ModuleFactory.getViewsModule();
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(viewModule.getTableName())
                .fields(FieldFactory.getViewFields())
                .andCondition(CriteriaAPI.getIdCondition(viewId, viewModule));
        int count = updateBuilder.update(viewProp);
        view.setStatus(viewStatus);
        ViewAPI.addViewGroupSharing(view.getGroupId());
        context.put(FacilioConstants.ContextNames.EXISTING_CV, view);
        return false;
    }
}
