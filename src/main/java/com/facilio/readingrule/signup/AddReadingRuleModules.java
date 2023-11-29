package com.facilio.readingrule.signup;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddReadingRuleModules extends SignUpData {

    @Override
    public void addData() throws Exception {
        try {
            FacilioModule readingRule = addReadingRuleModule();

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(readingRule));
            addModuleChain.execute();
            addSystemButtonsForRules();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addSystemButtonsForRules() throws Exception {

        SystemButtonApi.addCreateButtonWithCustomName(FacilioConstants.ReadingRules.NEW_READING_RULE, "New Rule");
        SystemButtonApi.addListEditButton(FacilioConstants.ReadingRules.NEW_READING_RULE);
        SystemButtonApi.addListDeleteButton(FacilioConstants.ReadingRules.NEW_READING_RULE);

        SystemButtonRuleContext editNewRules = new SystemButtonRuleContext();
        editNewRules.setName("Edit");
        editNewRules.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editNewRules.setIdentifier("editNewRules");
        editNewRules.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editNewRules.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        editNewRules.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ReadingRules.NEW_READING_RULE, editNewRules);

        SystemButtonRuleContext deactivateRule = new SystemButtonRuleContext();
        deactivateRule.setName("Deactivate");
        deactivateRule.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        deactivateRule.setIdentifier("deactivateRule");
        deactivateRule.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        deactivateRule.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        deactivateRule.setPermissionRequired(true);
        Criteria deactivateCriteria=getRuleStatusCriteria(Boolean.TRUE);
        deactivateRule.setCriteria(deactivateCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ReadingRules.NEW_READING_RULE, deactivateRule);


        SystemButtonRuleContext activateRule = new SystemButtonRuleContext();
        activateRule.setName("Activate");
        activateRule.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        activateRule.setIdentifier("activateRule");
        activateRule.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        activateRule.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        activateRule.setPermissionRequired(true);
        Criteria activateCriteria=getRuleStatusCriteria(Boolean.FALSE);
        activateRule.setCriteria(activateCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ReadingRules.NEW_READING_RULE, activateRule);

        
        SystemButtonRuleContext fddToWo = new SystemButtonRuleContext();
        fddToWo.setName("Fault To Workorder");
        fddToWo.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        fddToWo.setIdentifier("fddToWO");
        fddToWo.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ReadingRules.NEW_READING_RULE, fddToWo);

        SystemButtonRuleContext editFddToWo = new SystemButtonRuleContext();
        editFddToWo.setName("Edit Fault To Workorder Form ");
        editFddToWo.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        editFddToWo.setIdentifier("editFddToWO");
        editFddToWo.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ReadingRules.NEW_READING_RULE, editFddToWo);

        SystemButtonRuleContext disable = new SystemButtonRuleContext();
        disable.setName("Disable Fault To Workorder Form ");
        disable.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        disable.setIdentifier("disableFDDToWo");
        disable.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ReadingRules.NEW_READING_RULE, disable);

        SystemButtonRuleContext enable = new SystemButtonRuleContext();
        enable.setName("Enable Fault To Workorder Form ");
        enable.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        enable.setIdentifier("enableFDDToWo");
        enable.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ReadingRules.NEW_READING_RULE, enable);

    }

    private Criteria getRuleStatusCriteria(boolean status) {
        Criteria statusCriteria=new Criteria();
        statusCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS","status",String.valueOf(status), BooleanOperators.IS));
        return statusCriteria;
    }

    private FacilioModule addReadingRuleModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule("newreadingrules",
                "NewReadingRules",
                "New_Reading_Rule",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        FacilioField nameField = FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true);
        fields.add(nameField);

        fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING));

        FacilioField linkNameField = FieldFactory.getDefaultField("linkName", "Link Name", "LINK_NAME", FieldType.STRING);
        fields.add(linkNameField);

        LookupField assetCategory = FieldFactory.getDefaultField("assetCategory", "Asset Category", "ASSET_CATEGORY_ID", FieldType.LOOKUP);
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);

        SystemEnumField resourceType = FieldFactory.getDefaultField("resourceType", "Resource Type", "RESOURCE_TYPE", FieldType.SYSTEM_ENUM);
        resourceType.setEnumName("ConnectedResourceAssignmentType");
        fields.add(resourceType);

        NumberField categoryId = FieldFactory.getDefaultField("categoryId", "Category ID", "CATEGORY_ID", FieldType.NUMBER);
        fields.add(categoryId);

        LookupField faultImpact = FieldFactory.getDefaultField("impact", "Impact", "IMPACT_ID", FieldType.LOOKUP);
        faultImpact.setLookupModule(modBean.getModule(FacilioConstants.FaultImpact.MODULE_NAME));
        fields.add(faultImpact);

        NumberField readingModuleId = FieldFactory.getDefaultField("readingModuleId", "Reading Module ID", "READING_MODULE_ID", FieldType.NUMBER);
        fields.add(readingModuleId);

        NumberField readingFieldId = FieldFactory.getDefaultField("readingFieldId", "Reading Field ID", "READING_FIELD_ID", FieldType.NUMBER);
        fields.add(readingFieldId);

        NumberField siteId = FieldFactory.getDefaultField("siteId", "Site ID", "SITE_ID", FieldType.NUMBER);
        fields.add(siteId);

        BooleanField status = FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.BOOLEAN);
        fields.add(status);

        BooleanField autoClear = FieldFactory.getDefaultField("autoClear", "AutoClear", "AUTO_CLEAR", FieldType.BOOLEAN);
        fields.add(autoClear);

        DateField sysCreatedTime = FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME);
        fields.add(sysCreatedTime);

        LookupField sysCreatedBy = FieldFactory.getField("sysCreatedBy", "Created By", "SYS_CREATED_BY", ModuleFactory.getUsersModule(), FieldType.LOOKUP);
        sysCreatedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(sysCreatedBy);

        DateField sysModifiedTime = FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME);
        fields.add(sysModifiedTime);

        LookupField sysModifiedBy = FieldFactory.getField("sysModifiedBy", "Modified By", "SYS_MODIFIED_BY", ModuleFactory.getUsersModule(), FieldType.LOOKUP);
        sysModifiedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(sysModifiedBy);

        module.setFields(fields);
        return module;

    }

}
