package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FetchOnlyViewGroupColumnFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> selectableFields = new ArrayList<>();

        boolean fetchOnlyViewGroupColumn=(boolean)context.getOrDefault(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN,false);
        FacilioView view=(FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);

        if(fetchOnlyViewGroupColumn && view!=null){
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            List<FacilioField> viewFileds=null;
            Map<Long,FacilioField> selectableFieldsMap=null;
            List<FacilioField>  allFields=modBean.getAllFields(module.getName());
            Map<Long,FacilioField> allFieldsMap=null;

            if(CollectionUtils.isNotEmpty(view.getFields())){
                viewFileds=view.getFields().stream().map(ViewField::getField).filter(viewFiled->viewFiled!=null).collect(Collectors.toList());
                allFieldsMap=allFields.stream().collect(Collectors.toMap(FacilioField::getFieldId,Function.identity()));

                for(FacilioField field:viewFileds){
                    if(allFieldsMap.containsKey(field.getFieldId())){
                        selectableFields.add(field);
                    }
                }

            }

            List<FacilioField> extraSelectableFields=(List<FacilioField>)context.get(FacilioConstants.ContextNames.EXTRA_SELECTABLE_FIELDS);
            if(CollectionUtils.isNotEmpty(extraSelectableFields)){
                selectableFields.addAll(extraSelectableFields);
            }

            selectableFieldsMap=selectableFields.stream().collect(Collectors.toMap(FacilioField::getFieldId,Function.identity(),(a,b)->b));

            List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
            if (CollectionUtils.isNotEmpty(supplementFields)) {
                filterSupplementFields(supplementFields,selectableFieldsMap);
            }

        }
        context.put(FacilioConstants.ContextNames.SELECTABLE_FIELDS,selectableFields);

        return false;
    }
    private static void filterSupplementFields(List<SupplementRecord> supplementFields,Map<Long,FacilioField> viewFieldsMap){
        supplementFields.removeIf(supplementRecord -> {
            if (supplementRecord instanceof FacilioField){
                return !viewFieldsMap.containsKey(((FacilioField) supplementRecord).getFieldId());
            }
           return false;
        });
    }

}
