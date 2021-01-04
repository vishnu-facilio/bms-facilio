package com.facilio.v4.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public class SearchCommand extends FacilioCommand {
    private FacilioModule module;
    private String[] searchFields;

    public SearchCommand(FacilioModule module, String... searchFields) {
        this.module = module;
        this.searchFields = searchFields;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map queryParams = Constants.getQueryParams(context);
        if (MapUtils.isEmpty(queryParams)) {
            return false;
        }

        Set keySet = queryParams.keySet();
        Set<String> checkWords = new HashSet<>(Arrays.asList("filter_by","sort_by","sort_type","limit","page"));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = module.getName();
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        Criteria cr = new Criteria();

        for (Object keyObj: keySet) {
            String key = (String) keyObj;
            if (checkWords.contains(key)) {
                continue;
            }

            boolean isExactMatch = false;
            boolean isContains = false;
            boolean isStartsWith = false;
            FacilioField facilioField = null;
            if (fieldMap.containsKey(key)) {
                isExactMatch = true;
                facilioField = fieldMap.get(key);
            } else if (key.endsWith("_starts_with")) {
                String str = key.replace("_starts_with", "");
                if (fieldMap.containsKey(str)) {
                    isStartsWith = true;
                    facilioField = fieldMap.get(str);
                } else  {
                    continue;
                }
            } else if (key.endsWith("_contains")) {
                String str = key.replace("_contains", "");
                if (fieldMap.containsKey(str)) {
                    isContains = true;
                    facilioField = fieldMap.get(str);
                } else {
                    continue;
                }
            } else {
                continue;
            }

            if (isExactMatch) {
                cr.addAndCondition(CriteriaAPI.getCondition(facilioField, ((List<String>) queryParams.get(key)).get(0),StringOperators.IS));
            } else if (isContains) {
                cr.addAndCondition(CriteriaAPI.getCondition(facilioField, ((List<String>) queryParams.get(key)).get(0),StringOperators.CONTAINS));
            } else if (isStartsWith) {
                cr.addAndCondition(CriteriaAPI.getCondition(facilioField, ((List<String>) queryParams.get(key)).get(0),StringOperators.STARTS_WITH));
            }
        }

        if (cr.isEmpty()) {
            return false;
        }

        Constants.setSearchCriteria(context, cr);
        return false;
    }
}
