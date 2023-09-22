package com.facilio.fields.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class FilterFieldsBasedOnAddOrSkipFields extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);

        List<String> fieldsToAddList = (List<String>) context.getOrDefault(FacilioConstants.FieldsConfig.FIELDS_TO_ADD_LIST, null);
        List<String> fieldsToSkipList = (List<String>) context.getOrDefault(FacilioConstants.FieldsConfig.FIELDS_TO_SKIP_LIST, null);

        boolean isAddField = (boolean) context.get(FacilioConstants.FieldsConfig.IS_ADD_FIELD);
        List<String> addOrSkipFieldsList = isAddField ? CollectionUtils.isNotEmpty(fieldsToAddList) ? fieldsToAddList : new ArrayList<>()
                : CollectionUtils.isNotEmpty(fieldsToSkipList) ? fieldsToSkipList : new ArrayList<>();

        if(CollectionUtils.isNotEmpty(fields)) {
            fields = fields.stream()
                    .filter(field -> isAddField ? (addOrSkipFieldsList.contains(field.getName()) || !field.isDefault()) : !addOrSkipFieldsList.contains(field.getName()))
                    .collect(Collectors.toList());
            context.put(FacilioConstants.ContextNames.FIELDS, fields);
        }

        return false;
    }
}
