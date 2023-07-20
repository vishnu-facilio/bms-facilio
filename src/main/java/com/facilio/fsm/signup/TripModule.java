package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.DateField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TripModule extends BaseModuleConfig {
    public TripModule(){setModuleName(FacilioConstants.Trip.TRIP);}

    @Override
    public void addData() throws Exception {
        try {
            FacilioModule trip = addTripModule();

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(trip));
            addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
            addModuleChain.execute();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private FacilioModule addTripModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule(FacilioConstants.Trip.TRIP,
                "Trip",
                "TRIP",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        LookupField peopleId = FieldFactory.getDefaultField("people", "Field Agent", "PEOPLE_ID", FieldType.LOOKUP,true);
        peopleId.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(peopleId);

        LookupField serviceAppointmentId = new LookupField(module,"serviceAppointment","Service Appointment",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_APPOINTMENT_ID", FieldType.LOOKUP,true,false,true,true,null,modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
        fields.add(serviceAppointmentId);

        LookupField startLocation = FieldFactory.getDefaultField("startLocation","Start Location","START_LOCATION",FieldType.LOOKUP);
        startLocation.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        startLocation.setLookupModule(modBean.getModule("location"));
        fields.add(startLocation);

        LookupField endLocation = FieldFactory.getDefaultField("endLocation","End Location","END_LOCATION",FieldType.LOOKUP);
        endLocation.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        endLocation.setLookupModule(modBean.getModule("location"));
        fields.add(endLocation);

        DateField startTime = FieldFactory.getDefaultField("startTime", "Start Time", "START_TIME", FieldType.DATE_TIME);
        fields.add(startTime);

        DateField endTime = FieldFactory.getDefaultField("endTime", "End Time", "END_TIME", FieldType.DATE_TIME);
        fields.add(endTime);

        NumberField tripDuration = FieldFactory.getDefaultField("tripDuration", "Trip Duration", "TRIP_DURATION", FieldType.NUMBER);
        fields.add(tripDuration);

        fields.add(FieldFactory.getDefaultField("tripDistance","Trip Distance","TRIP_DISTANCE",FieldType.DECIMAL));


        LookupField serviceOrder = FieldFactory.getDefaultField("serviceOrder","Service Order","SERVICE_ORDER_ID",FieldType.LOOKUP);
        serviceOrder.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER));
        fields.add(serviceOrder);

        LookupField moduleStateField = FieldFactory.getDefaultField("moduleState", "Status", "MODULE_STATE", FieldType.LOOKUP);
        moduleStateField.setDefault(true);
        moduleStateField.setLookupModule(modBean.getModule("ticketstatus"));
        fields.add(moduleStateField);

        NumberField stateFlowIdField = FieldFactory.getDefaultField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", FieldType.NUMBER);
        stateFlowIdField.setDefault(true);
        fields.add(stateFlowIdField);

        LookupField approvalStateField = FieldFactory.getDefaultField("approvalStatus", "Approval Status", "APPROVAL_STATE", FieldType.LOOKUP);
        approvalStateField.setDefault(true);
        approvalStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        approvalStateField.setLookupModule(modBean.getModule("ticketstatus"));
        fields.add(approvalStateField);

        NumberField approvalFlowIdField = FieldFactory.getDefaultField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", FieldType.NUMBER);
        approvalFlowIdField.setDefault(true);
        fields.add(approvalFlowIdField);

        module.setFields(fields);
        return module;

    }
}
