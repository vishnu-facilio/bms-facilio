package com.facilio.backgroundactivity.util;

import com.facilio.backgroundactivity.factory.Constants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.List;

public class AddBackgroundActivityModule extends SignUpData {
    @Override
    public void addData() throws Exception {
        addModule();
        addFields();
    }

    private void addFields() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = modBean.getModule(Constants.BACKGROUND_ACTIVITY_MODULE);
        fields.add(FieldFactory.getNameField(module));
        fields.add(FieldFactory.getSysDeletedTimeField(module));
        fields.add(FieldFactory.getSysDeletedByField(module));
        fields.add(FieldFactory.getIsDeletedField(module));
        fields.add(FieldFactory.getSystemField("sysCreatedBy", module));
        fields.add(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", module, FieldType.NUMBER));
        fields.add(FieldFactory.getSystemField("sysModifiedBy", module));
        fields.add(FieldFactory.getField("sysModifiedTime", "SYS_MODIFIED_TIME", module, FieldType.NUMBER));
        fields.add(FieldFactory.getNumberField("recordId","RECORD_ID",module));
        fields.add(FieldFactory.getStringField("recordType","RECORD_TYPE",module));
        fields.add(FieldFactory.getNumberField("startTime","STARTED_TIME",module));
        fields.add(FieldFactory.getNumberField("completedTime","COMPLETED_TIME",module));
        fields.add(FieldFactory.getNumberField("initiatedBy","INITIATED_BY",module));
        fields.add(FieldFactory.getStringField("systemStatus","SYSTEM_STATUS",module));
        fields.add(FieldFactory.getNumberField("percentage","PERCENTAGE",module));
        fields.add(FieldFactory.getStringField("message","MESSAGE",module));
        fields.add(FieldFactory.getNumberField("parentActivity","PARENT_ACTIVITY",module));
        for (FacilioField field : fields) {
            modBean.addField(field);
        }
    }
    private void addModule() throws Exception {
        FacilioModule backgroundActivityModule = new FacilioModule();
        backgroundActivityModule.setName(Constants.BACKGROUND_ACTIVITY_MODULE);
        backgroundActivityModule.setDisplayName("Background Activity");
        backgroundActivityModule.setExtendModule(null);
        backgroundActivityModule.setType(FacilioModule.ModuleType.BASE_ENTITY);
        backgroundActivityModule.setTableName("Background_Activity");
        backgroundActivityModule.setDescription("Records of background activities");
        backgroundActivityModule.setTrashEnabled(true);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        modBean.addModule(backgroundActivityModule);
    }
}