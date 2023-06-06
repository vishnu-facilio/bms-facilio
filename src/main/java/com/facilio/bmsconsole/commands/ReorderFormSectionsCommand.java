package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReorderFormSectionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long nextFormSectionId = (long) context.get(FacilioConstants.FormContextNames.NEXT_FORM_SECTION_ID);
        long previousFormSectionId = (long) context.get(FacilioConstants.FormContextNames.PREVIOUS_FORM_SECTION_ID);
        long formSectionId = (long) context.get(FacilioConstants.FormContextNames.FORM_SECTION_ID);
        long formId = (long) context.get(FacilioConstants.ContextNames.FORM_ID);
        double formSectionSequenceNumber = FormsAPI.getFormSectionSequenceNumber(previousFormSectionId, nextFormSectionId, formId);

        FacilioModule formSectionModule = ModuleFactory.getFormSectionModule();
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(formSectionModule.getTableName())
                .fields(Collections.singletonList(FieldFactory.getField("formSectionSequenceNumber", "SECTION_SEQUENCE_NUMBER", formSectionModule, FieldType.DECIMAL)))
                .andCondition(CriteriaAPI.getIdCondition(formSectionId, formSectionModule));
        Map<String, Object> map = new HashMap<>();
        map.put("formSectionSequenceNumber", formSectionSequenceNumber);
        builder.update(map);

        return false;
    }
}
