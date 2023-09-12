package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.RelatedListWidgetContext;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.*;

public class GetRelatedModulesForBuilder extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule currModule = modBean.getModule(moduleName);

        if(currModule != null) {
            JSONObject bulkRelatedList = RelatedListWidgetUtil.fetchAllRelatedListForModule(currModule);
            if(bulkRelatedList != null) {
                List<RelatedListWidgetContext> relatedLists = (List<RelatedListWidgetContext>) bulkRelatedList.get("relatedList");
                context.put(FacilioConstants.ContextNames.MODULE_LIST, relatedLists);
            }
        }
        return false;

    }

    public static Criteria fetchLookupFieldsCriteria() {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("DATA_TYPE", "dataType", String.valueOf(FieldType.LOOKUP.getTypeAsInt()), StringOperators.IS));
        return criteria;
    }
}
