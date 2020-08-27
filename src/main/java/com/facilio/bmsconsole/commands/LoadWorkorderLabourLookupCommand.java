package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.LookupFieldMeta;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LoadWorkorderLabourLookupCommand extends FacilioCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        LookupFieldMeta labour = new LookupFieldMeta((LookupField) fieldsAsMap.get("labour"));
        List<LookupField>fetchLookup = Arrays.asList(labour,(LookupField) fieldsAsMap.get("parent"));
        context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST,fetchLookup);
        Long parentId = (Long)context.get(FacilioConstants.ContextNames.PARENT_ID);
        if(parentId != null && parentId > 0) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS));
            context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
        }
        return false;
    }
}
