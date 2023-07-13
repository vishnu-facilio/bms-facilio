package com.facilio.qa.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LoadSurveyExtraFieldsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        boolean fetchOnlyViewGroupColumn=(boolean)context.getOrDefault(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN,false);
        FacilioView view=(FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        if(fetchOnlyViewGroupColumn && view!=null) {
            List<FacilioField> extraReceivableFields = new ArrayList<>();
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> allFields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> allFieldsAsMap = FieldFactory.getAsMap(allFields);
            List<String> extraFieldNames = new ArrayList<>();
            extraFieldNames.add("fullScore");
            extraFieldNames.add("totalScore");
            if(context.get("currentModuleName")!=null){
                if(context.get("currentModuleName").equals(FacilioConstants.ContextNames.WORK_ORDER)){
                    extraFieldNames.add("workOrderId");

                } else if(context.get("currentModuleName").equals(FacilioConstants.ContextNames.SERVICE_REQUEST)){
                    extraFieldNames.add("serviceRequestId");
                }
            }
            for (String fieldName : extraFieldNames) {
                extraReceivableFields.add((allFieldsAsMap.get(fieldName)));
            }
            context.put(FacilioConstants.ContextNames.EXTRA_SELECTABLE_FIELDS, extraReceivableFields);
        }
        return false;
    }
}
