package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServicePMTriggerModule  extends BaseModuleConfig {
    public ServicePMTriggerModule(){
        setModuleName(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER);
    }
    @Override
    public void addData() throws Exception {
        FacilioModule servicePMTriggerModule = constructServicePMTriggerModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(servicePMTriggerModule));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        addTriggerFieldInServiceOrder();
    }
    private FacilioModule constructServicePMTriggerModule(){
        FacilioModule module = new FacilioModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER, "Service PM Trigger", "Service_PM_Trigger", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("name","Name","NAME",FieldType.STRING,true));
        fields.add(FieldFactory.getDefaultField("schedule","Schedule","SCHEDULE_INFO",FieldType.STRING, FacilioField.FieldDisplayType.TEXTBOX));
        SystemEnumField frequency = FieldFactory.getDefaultField("frequency","Frequency","FREQUENCY",FieldType.SYSTEM_ENUM);
        frequency.setEnumName("ServicePMTriggerFrequency");
        fields.add(frequency);
        fields.add(FieldFactory.getDefaultField("startTime","Execution Start Date","EXECUTION_START_DATE",FieldType.DATE, FacilioField.FieldDisplayType.DATE));
        fields.add(FieldFactory.getDefaultField("endTime","Execution End Date","EXECUTION_END_DATE",FieldType.DATE, FacilioField.FieldDisplayType.DATE));


        module.setFields(fields);
        return module;
    }
    private void addTriggerFieldInServiceOrder() throws Exception{
        ModuleBean bean = Constants.getModBean();
        LookupField trigger = FieldFactory.getDefaultField("servicePMTrigger","Service PM Trigger","SERVICE_PM_TRIGGER", FieldType.LOOKUP);
        trigger.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER),"Service PM Trigger module doesn't exist."));
        trigger.setModule(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER));
        bean.addField(trigger);
    }
}
