package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.PriorityContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.StringField;

import java.util.ArrayList;
import java.util.List;

public class PriorityModule extends BaseModuleConfig {
    public PriorityModule(){setModuleName(FacilioConstants.Priority.PRIORITY);}

    @Override
    public void addData() throws Exception {
        addPriority();
        addPriorityList();

    }
    private void addPriority() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule priority = new FacilioModule(FacilioConstants.Priority.PRIORITY,"Priority","PRIORITY", FacilioModule.ModuleType.PICK_LIST,false);
        List<FacilioField> fields = new ArrayList<>();

        fields.add(new StringField(priority,"priority","Priority",FacilioField.FieldDisplayType.TEXTBOX,"PRIORITY", FieldType.STRING,true,false,true,false));

        fields.add(new StringField(priority,"displayName","Display Name",FacilioField.FieldDisplayType.TEXTBOX,"DISPLAY_NAME",FieldType.STRING,false,false,true,true));

        fields.add(new StringField(priority,"color","Color",FacilioField.FieldDisplayType.TEXTBOX,"COLOR",FieldType.STRING,true,false,true,false));

        priority.setFields(fields);
        modules.add(priority);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
    }

    private void addPriorityList() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        InsertRecordBuilder<PriorityContext> insertRecordBuilder = new InsertRecordBuilder<PriorityContext>()
                .moduleName(FacilioConstants.Priority.PRIORITY)
                .fields(modBean.getAllFields(FacilioConstants.Priority.PRIORITY));

        List<PriorityContext> priorityList = new ArrayList<>();

        PriorityContext low = new PriorityContext();
        low.setPriority(FacilioConstants.Priority.LOW);
        low.setDisplayName("Low");
        low.setColor("blue");
        priorityList.add(low);

        PriorityContext medium = new PriorityContext();
        medium.setPriority(FacilioConstants.Priority.MEDIUM);
        medium.setDisplayName("Medium");
        medium.setColor("yellow");
        priorityList.add(medium);

        PriorityContext high = new PriorityContext();
        high.setPriority(FacilioConstants.Priority.HIGH);
        high.setDisplayName("High");
        high.setColor("red");
        priorityList.add(high);

        insertRecordBuilder.addRecords(priorityList);
        insertRecordBuilder.save();


    }


}

