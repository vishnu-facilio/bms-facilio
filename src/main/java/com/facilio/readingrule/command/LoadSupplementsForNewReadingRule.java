package com.facilio.readingrule.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadSupplementsForNewReadingRule extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ReadingRules.NEW_READING_RULE);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("assetCategory"));
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("impact"));
        List<FacilioField> extraFields = new ArrayList<>();

        if (fieldsAsMap.get("resourceType") != null) {
            extraFields.add(fieldsAsMap.get("resourceType"));
        }
        if (fieldsAsMap.get("categoryId") != null) {
            extraFields.add(fieldsAsMap.get("categoryId"));
        }
        if (fieldsAsMap.get("assetCategory") != null) {
            extraFields.add(fieldsAsMap.get("assetCategory"));
        }

        context.put(FacilioConstants.ContextNames.EXTRA_SELECTABLE_FIELDS, extraFields);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
}
