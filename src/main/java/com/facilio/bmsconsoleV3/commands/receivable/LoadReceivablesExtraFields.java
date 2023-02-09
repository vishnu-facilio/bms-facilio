package com.facilio.bmsconsoleV3.commands.receivable;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LoadReceivablesExtraFields extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        boolean fetchOnlyViewGroupColumn=(boolean)context.getOrDefault(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN,false);
        FacilioView view=(FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);

        if(fetchOnlyViewGroupColumn && view!=null){
            List<FacilioField> extraReceivableFields = new ArrayList<>();
            List<FacilioField> viewFileds=null;

            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> allFields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> allFieldsAsMap = FieldFactory.getAsMap(allFields);

            String[] extraFieldNames = new String[]{"poId"};

            if(CollectionUtils.isNotEmpty(view.getFields())){
                viewFileds=view.getFields().stream().map(ViewField::getField).filter(viewFiled->viewFiled!=null).collect(Collectors.toList());
                Map<String,FacilioField> viewFieldsMap = FieldFactory.getAsMap(viewFileds);

                for (String fieldName :extraFieldNames) {
                    if(!viewFieldsMap.containsKey(fieldName)){
                        extraReceivableFields.add((allFieldsAsMap.get(fieldName)));
                    }
                }
            }
            context.put(FacilioConstants.ContextNames.EXTRA_SELECTABLE_FIELDS,extraReceivableFields);

        }

        return false;
    }
}
