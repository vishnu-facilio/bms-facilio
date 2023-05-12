package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import java.util.*;

public class ReorderPageComponentsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long previousId = (Long) context.get(FacilioConstants.CustomPage.PREVIOUS_ID);
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Long nextId = (Long) context.get(FacilioConstants.CustomPage.NEXT_ID);
        CustomPageAPI.PageComponent type = (CustomPageAPI.PageComponent) context.get(FacilioConstants.CustomPage.TYPE);

        Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(type.getFields());
        List<FacilioField> fieldsToQuery = new ArrayList<>();
        fieldsToQuery.add(fieldsMap.get("sequenceNumber"));
        Criteria criteria = new Criteria();

        switch (type) {
            case PAGE:
                long moduleId = (long) context.get(FacilioConstants.ContextNames.MODULE_ID);
                long appId = (long) context.get(FacilioConstants.ContextNames.APP_ID);
                criteria.addAndCondition(CriteriaAPI.getModuleIdIdCondition(moduleId, type.getModule()));
                criteria.addAndCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("appId"), String.valueOf(appId)));
                criteria.addAndCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("isTemplate"),String.valueOf(false)));
                criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getSysDeletedTimeField(type.getModule()), CommonOperators.IS_EMPTY));
                break;
            case TAB:
                Long layoutId = (Long) context.get(FacilioConstants.CustomPage.LAYOUT_ID);
                criteria.addAndCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("layoutId"), String.valueOf(layoutId)));
                break;
            /*case COLUMN:
                Long tabId = (Long) context.get(FacilioConstants.CustomPage.TAB_ID);
                fields.add(fieldsMap.get("tabId"));
                fieldNameVsFieldValue.put("tabId",tabId);
                break;*/
            case SECTION:
                Long columnId = (Long) context.get(FacilioConstants.CustomPage.COLUMN_ID);
                criteria.addAndCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("columnId"), String.valueOf(columnId)));
        }

        double sequenceNumber = CustomPageAPI.updateAndGetSequenceNumber(previousId, id, nextId,
                FacilioConstants.CustomPage.SEQUENCE_NUMBER, type.getModule(), criteria, fieldsToQuery);

        CustomPageAPI.updateSysModifiedFields(id, CustomPageAPI.getSysModifiedProps(), type);

        context.put(FacilioConstants.CustomPage.SEQUENCE_NUMBER, sequenceNumber);
        return false;
    }
}