package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.TripStatusContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.StringField;

import java.util.ArrayList;
import java.util.List;

public class TripStatus extends BaseModuleConfig {
    public TripStatus(){setModuleName(FacilioConstants.Trip.TRIP_TICKET_STATUS);}

    @Override
    public void addData() throws Exception {
        addTripStatus();
        addStatus();

    }
    private void addTripStatus() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule tripStatus = new FacilioModule(FacilioConstants.Trip.TRIP_TICKET_STATUS,"Trip Status","Trip_Status", FacilioModule.ModuleType.PICK_LIST,false);
        List<FacilioField> fields = new ArrayList<>();

        fields.add(new StringField(tripStatus,"status","Status",FacilioField.FieldDisplayType.TEXTBOX,"STATUS", FieldType.STRING,true,false,true,false));

        fields.add(new StringField(tripStatus,"displayName","Display Name",FacilioField.FieldDisplayType.TEXTBOX,"DISPLAY_NAME",FieldType.STRING,false,false,true,true));

        fields.add(new StringField(tripStatus,"color","Color",FacilioField.FieldDisplayType.TEXTBOX,"COLOR",FieldType.STRING,true,false,true,false));

        fields.add(new StringField(tripStatus,"backgroundColor","Background Color",FacilioField.FieldDisplayType.TEXTBOX,"BACKGROUND_COLOR",FieldType.STRING,true,false,true,false));

        fields.add(new StringField(tripStatus,"textColor","Text Color",FacilioField.FieldDisplayType.TEXTBOX,"TEXT_COLOR",FieldType.STRING,false,false,true,false));


        NumberField typeCode = FieldFactory.getDefaultField("typeCode", "Type", "STATUS_TYPE", FieldType.NUMBER);
        typeCode.setDefault(true);
        fields.add(typeCode);

        BooleanField recordLocked = FieldFactory.getDefaultField("recordLocked","Record Locked","RECORD_LOCKED",FieldType.BOOLEAN);
        recordLocked.setDefault(true);
        fields.add(recordLocked);

        BooleanField deleteLocked = FieldFactory.getDefaultField("deleteLocked","Delete Locked","DELETE_LOCKED",FieldType.BOOLEAN);
        deleteLocked.setDefault(true);
        fields.add(deleteLocked);

        tripStatus.setFields(fields);
        modules.add(tripStatus);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
    }

    private void addStatus() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        InsertRecordBuilder<TripStatusContext> insertRecordBuilder = new InsertRecordBuilder<TripStatusContext>()
                .moduleName(FacilioConstants.Trip.TRIP_TICKET_STATUS)
                .fields(modBean.getAllFields(FacilioConstants.Trip.TRIP_TICKET_STATUS));

        List<TripStatusContext> ticketStatusContextList = new ArrayList<>();

        TripStatusContext inProgressState = new TripStatusContext();

        inProgressState.setStatus(FacilioConstants.Trip.IN_PROGRESS);
        inProgressState.setDisplayName("In Progress");
        inProgressState.setColor("information");
        inProgressState.setBackgroundColor("#F7BA02");
        inProgressState.setTextColor("#000000");
        inProgressState.setTypeCode(2);
        inProgressState.setRecordLocked(true);
        inProgressState.setDeleteLocked(true);
        ticketStatusContextList.add(inProgressState);

        TripStatusContext completedState = new TripStatusContext();

        completedState.setStatus(FacilioConstants.Trip.COMPLETED);
        completedState.setDisplayName("Completed");
        completedState.setColor("success");
        completedState.setBackgroundColor("#058545");
        completedState.setTextColor("#ffffff");
        completedState.setTypeCode(3);
        completedState.setRecordLocked(false);
        completedState.setDeleteLocked(false);
        ticketStatusContextList.add(completedState);

        insertRecordBuilder.addRecords(ticketStatusContextList);
        insertRecordBuilder.save();


    }


}

