package com.facilio.bmsconsoleV3.signup.readingkpi;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddScheduledKpiLoggerModule extends SignUpData {

    @Override
    public void addData() throws Exception {
        // TODO Auto-generated method stub

        FacilioModule kpiModule = addScheduledKpiHistoricalLoggerModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(kpiModule));
        addModuleChain.execute();

    }

    private FacilioModule addScheduledKpiHistoricalLoggerModule() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        // TODO Auto-generated method stub
        FacilioModule module = new FacilioModule("scheduledKpiLogger",
                "Scheduled KPI Logger",
                "Scheduled_KPI_Logger",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        NumberField kpiId = (NumberField) FieldFactory.getDefaultField("kpiId", "KPI ID", "KPI_ID", FieldType.NUMBER, true);
        fields.add(kpiId);

        NumberField resourceId = (NumberField) FieldFactory.getDefaultField("resourceId", "Resource ID", "RESOURCE_ID", FieldType.NUMBER);
        fields.add(resourceId);

        NumberField status = (NumberField) FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.NUMBER);
        fields.add(status);

        BooleanField isHistorical = (BooleanField) FieldFactory.getDefaultField("isHistorical", "Is Historical", "IS_HISTORICAL", FieldType.BOOLEAN);
        fields.add(isHistorical);

        FacilioField message = (FacilioField) FieldFactory.getDefaultField("message", "Message", "MESSAGE", FieldType.STRING);
        fields.add(message);

        NumberField startTime = (NumberField) FieldFactory.getDefaultField("startTime", "Interval Start Time", "START_TIME", FieldType.NUMBER);
        fields.add(startTime);

        NumberField endTime = (NumberField) FieldFactory.getDefaultField("endTime", "Interval End Time", "END_TIME", FieldType.NUMBER);
        fields.add(endTime);

        NumberField calculationStartTime = (NumberField) FieldFactory.getDefaultField("calculationStartTime", "Calculation Start Time", "CALCULATION_START_TIME", FieldType.NUMBER);
        fields.add(calculationStartTime);

        NumberField calculationEndTime = (NumberField) FieldFactory.getDefaultField("calculationEndTime", "Calculation End Time", "CALCULATION_END_TIME", FieldType.NUMBER);
        fields.add(calculationEndTime);

        LookupField createdBy = (LookupField) FieldFactory.getField("createdBy", "Created By", "CREATED_BY", ModuleFactory.getUsersModule(), FieldType.LOOKUP);
        createdBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(createdBy);

        NumberField createdTime = (NumberField) FieldFactory.getDefaultField("createdTime", "Created Time", "CREATED_TIME", FieldType.NUMBER);
        fields.add(createdTime);

        module.setFields(fields);
        return module;
    }
}
