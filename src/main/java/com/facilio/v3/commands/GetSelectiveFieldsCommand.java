package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.calendarview.CalendarViewContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetSelectiveFieldsCommand extends FacilioCommand {
    private final List<String> selectiveFieldNames = Arrays.asList("moduleState");
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        FacilioView viewObj = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        FacilioUtil.throwIllegalArgumentException(viewObj == null || viewObj.getCalendarViewContext() == null, "Invalid View details passed");
        CalendarViewContext calendarViewObj = viewObj.getCalendarViewContext();
        FacilioField startTimeField = calendarViewObj.getStartDateField();
        FacilioField endTimeField = calendarViewObj.getEndDateField();

        ModuleBean moduleBean = Constants.getModBean();

        List<FacilioField> selectableFields = new ArrayList<>();
        selectableFields.add(startTimeField);
        if (endTimeField != null) {
            selectableFields.add(endTimeField);
        }
        selectableFields.add(FieldFactory.getIdField(moduleBean.getModule(moduleName)));

        Criteria filterFieldsCriteria = new Criteria();
        filterFieldsCriteria.addAndCondition(CriteriaAPI.getCondition("IS_MAIN_FIELD", "isMainField", String.valueOf(true), BooleanOperators.IS));
        filterFieldsCriteria.addOrCondition(CriteriaAPI.getCondition("NAME", "name", StringUtils.join(selectiveFieldNames, ","), StringOperators.IS));

        List<FacilioField> dbFields = moduleBean.getAllFields(moduleName, null, null, filterFieldsCriteria);
        if (CollectionUtils.isNotEmpty(dbFields)) {
            selectableFields.addAll(dbFields);
        }

        Map<Long,FacilioField> selectableFieldsMap = null;
        selectableFieldsMap = selectableFields.stream().collect(Collectors.toMap(FacilioField::getFieldId, Function.identity(), (a, b)->b));

        if (CollectionUtils.isNotEmpty(supplementFields)) {
            filterSupplementFields(supplementFields, selectableFieldsMap);
        }

        context.put(FacilioConstants.ContextNames.SELECTABLE_FIELDS, selectableFields);

        return false;
    }

    private static void filterSupplementFields(List<SupplementRecord> supplementFields, Map<Long,FacilioField> selectableFieldsMap){
        supplementFields.removeIf(supplementRecord -> {
            if (supplementRecord instanceof FacilioField){
                return !selectableFieldsMap.containsKey(((FacilioField) supplementRecord).getFieldId());
            }
            return false;
        });
    }
}
