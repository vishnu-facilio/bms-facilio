package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import com.facilio.bmsconsole.util.FormsAPI;

import java.util.Map;

public class AddFormSectionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long formId = (long) context.get(FacilioConstants.ContextNames.PARENT_FORM_ID);
        FormSection formSection = (FormSection) context.get(FacilioConstants.ContextNames.FORM_SECTION);
        long nextFormSectionId = (long) context.get(FacilioConstants.FormContextNames.NEXT_FORM_SECTION_ID);
        long previousFormSectionId = (long) context.get(FacilioConstants.FormContextNames.PREVIOUS_FORM_SECTION_ID);

        double formSectionSequenceNumber = FormsAPI.getFormSectionSequenceNumber(previousFormSectionId, nextFormSectionId, formId);
        long orgId = AccountUtil.getCurrentOrg().getId();

        formSection.setId(-1);
        formSection.setFormId(formId);
        formSection.setOrgId(orgId);
        formSection.setSequenceNumber((int) formSectionSequenceNumber);
        formSection.setFormSectionSequenceNumber(formSectionSequenceNumber);

        long systemTime = System.currentTimeMillis();
        User currentUser = AccountUtil.getCurrentUser();
        formSection.setSysCreatedTime(systemTime);
        formSection.setSysModifiedTime(systemTime);
        if (currentUser != null) {
            formSection.setSysCreatedBy(AccountUtil.getCurrentUser().getPeopleId());
            formSection.setSysModifiedBy(AccountUtil.getCurrentUser().getPeopleId());
        }

        Map<String, Object> prop = FieldUtil.getAsProperties(formSection);
        context.put(FacilioConstants.ContextNames.FORM_SECTION,prop);

        FacilioModule module = ModuleFactory.getFormSectionModule();

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(module.getTableName())
                .fields(FieldFactory.getFormSectionFields())
                .addRecord(prop);
        builder.save();

        return false;
    }
}
