package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddFormSharingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        FacilioForm facilioForm = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);

        SharingContext<SingleSharingContext> formSharing = facilioForm.getFormSharing();

        SharingAPI.deleteSharingForParent(Collections.singletonList(facilioForm.getId()), ModuleFactory.getFormSharingModule());


        // if form sharing is null or empty that form is accessible for all roles

        if (CollectionUtils.isEmpty(formSharing)) {
            return false;
        }
        if (formSharing.stream().anyMatch(value -> value.getTypeEnum() != SingleSharingContext.SharingType.ROLE)) {
            throw new IllegalArgumentException("form sharing allowed only for roles");
        }

        FacilioModule module = ModuleFactory.getFormSharingModule();
        List<FacilioField> formSharingFields = FieldFactory.getFormSharingFields();
        SharingAPI.addSharing(formSharing,formSharingFields,facilioForm.getId(),module);

        context.put(FacilioConstants.ContextNames.FORM_SHARING, formSharing);

        return false;
    }
}
