package com.facilio.bmsconsoleV3.signup.inspection;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;

import java.util.ArrayList;
import java.util.List;

public class AddInspectionModules extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule inspection = constructInspection(modBean);
        modules.add(inspection);
        modules.add(constructInspectionResponse(modBean, inspection));
        
        modules.add(constructInspectionTriggers(modBean, inspection));

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
    }


	private FacilioModule constructInspection(ModuleBean modBean) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE,
                                                "Inspection Templates",
                                                "Inspection_Templates",
                                                FacilioModule.ModuleType.Q_AND_A,
                                                modBean.getModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE)
                                                );

        List<FacilioField> fields = new ArrayList<>();
        LookupField siteField = (LookupField) FieldFactory.getDefaultField("site", "Site", "SITE_ID", FieldType.LOOKUP, true);
        siteField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SITE));
        fields.add(siteField);

        module.setFields(fields);
        return module;
    }
	
	private FacilioModule constructInspectionTriggers(ModuleBean modBean, FacilioModule inspection) throws Exception {
		
		FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_TRIGGER,
                "Inspection Triggers",
                "Inspection_Triggers",
                FacilioModule.ModuleType.SUB_ENTITY
        );

        List<FacilioField> fields = new ArrayList<>();
        
        FacilioField nameField = FieldFactory.getNameField(module);
        fields.add(nameField);
        
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(inspection);
        fields.add(parentField);
        
        NumberField type = (NumberField) FieldFactory.getDefaultField("type", "Trigger Type", "TRIGGER_TYPE", FieldType.NUMBER);
        fields.add(type);
        
        NumberField scheduleID = (NumberField) FieldFactory.getDefaultField("scheduleId", "Schedule", "SCHEDULE_ID", FieldType.NUMBER);
        fields.add(scheduleID);

        module.setFields(fields);
        return module;
	}

    private FacilioModule constructInspectionResponse (ModuleBean modBean, FacilioModule inspection) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_RESPONSE,
                "Inspections",
                "Inspection_Responses",
                FacilioModule.ModuleType.Q_AND_A_RESPONSE,
                modBean.getModule(FacilioConstants.QAndA.RESPONSE)
        );

        List<FacilioField> fields = new ArrayList<>();
        LookupField siteField = (LookupField) FieldFactory.getDefaultField("site", "Site", "SITE_ID", FieldType.LOOKUP, true);
        siteField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SITE));
        fields.add(siteField);
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(inspection);
        fields.add(parentField);
        LookupField staff = (LookupField) FieldFactory.getDefaultField("staff", "Staff", "STAFF_ID", FieldType.LOOKUP);
        staff.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(staff);
        LookupField team = (LookupField) FieldFactory.getDefaultField("team", "Team", "TEAM_ID", FieldType.LOOKUP);
        team.setSpecialType(FacilioConstants.ContextNames.GROUPS);
        fields.add(team);
        
        FacilioField createdTime = (FacilioField) FieldFactory.getDefaultField("createdTime", "Created Time", "CREATED_TIME", FieldType.DATE_TIME);
        fields.add(createdTime);
        
        NumberField status = (NumberField) FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.NUMBER);
        fields.add(status);

        module.setFields(fields);
        return module;
    }


}
