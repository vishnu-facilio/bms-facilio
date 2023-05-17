package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateLayoutCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long mainAppId = (long) context.get(FacilioConstants.ContextNames.APP_ID);
        Map<String, Object> props = new HashMap<>();
        props.put("appLayoutType", 1);
        GenericUpdateRecordBuilder updateLayoutBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getApplicationLayoutModule().getTableName())
                .fields(FieldFactory.getApplicationLayoutFields())
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(mainAppId), NumberOperators.EQUALS));
        updateLayoutBuilder.update(props);

        ApplicationLayoutContext mobileLayout = new ApplicationLayoutContext(mainAppId, ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.MOBILE, "newapp");
        ApplicationLayoutContext setupLayout = new ApplicationLayoutContext(mainAppId, ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.SETUP, "newapp");
        List<Map<String, Object>> layoutList = new ArrayList<>();
        layoutList.add(FieldUtil.getAsProperties(mobileLayout));
        layoutList.add(FieldUtil.getAsProperties(setupLayout));


        GenericInsertRecordBuilder addLayoutBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getApplicationLayoutModule().getTableName())
                .fields(FieldFactory.getApplicationLayoutFields());
        addLayoutBuilder.addRecords(layoutList);

        addLayoutBuilder.save();
        return false;
    }
}
