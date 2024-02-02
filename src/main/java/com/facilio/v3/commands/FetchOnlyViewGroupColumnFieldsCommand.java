package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FetchOnlyViewGroupColumnFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> selectableFields = new ArrayList<>();

        boolean fetchOnlyViewGroupColumn=(boolean)context.getOrDefault(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN,false);
        FacilioView view=(FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);

        String selectableFieldNames=(String)context.get(FacilioConstants.ContextNames.SELECTABLE_FIELD_NAMES);

        if(fetchOnlyViewGroupColumn && view!=null){
            fillSelectableFieldsForWeb(context,selectableFields);
        }
        else if(StringUtils.isNotEmpty(selectableFieldNames)){
            fillSelectableFieldsForMobile(context,selectableFieldNames,selectableFields);
        }
        context.put(FacilioConstants.ContextNames.SELECTABLE_FIELDS,selectableFields);
        return false;
    }

    private static void fillSelectableFieldsForWeb(Context context,List<FacilioField> selectableFields) throws Exception {   //filled by viewConfigured fields
        FacilioView view=(FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        List<FacilioField> viewFileds=null;
        List<FacilioField>  allFields = getAllFields(context);
        Map<Long,FacilioField> allFieldsMap=null;
        Map<String,FacilioField> fieldsNameVsFacilioFieldMap=null;
        Map<LookupField,List<LookupField>> parentLookupFieldVsChildLookupFieldsMap;

        if(CollectionUtils.isNotEmpty(view.getFields())){
            viewFileds=view.getFields().stream().filter(viewField -> viewField.getField()!=null && viewField.getParentField()==null)
                    .map(ViewField::getField).collect(Collectors.toList());

            allFieldsMap=allFields.stream().collect(Collectors.toMap(FacilioField::getFieldId,Function.identity()));
            fieldsNameVsFacilioFieldMap=allFields.stream().collect(Collectors.toMap(FacilioField::getName,Function.identity(),(a,b)->b));
            for(FacilioField field:viewFileds){
                if(allFieldsMap.containsKey(field.getFieldId())){
                    selectableFields.add(field);
                }
            }

            parentLookupFieldVsChildLookupFieldsMap =  view.getFields().stream().filter(viewField -> viewField.getField() instanceof LookupField && viewField.getParentField() instanceof LookupField)
                    .collect(Collectors.groupingBy(v->(LookupField)v.getParentField(), Collectors.mapping(viewField -> (LookupField)viewField.getField(), Collectors.toList())));

            context.put(FacilioConstants.ContextNames.ONE_LEVEL_LOOKUP_FIELD_MAP,parentLookupFieldVsChildLookupFieldsMap);
        }

        addExtraSelectableFieldsIfExists(context,selectableFields);
        addSortQueryFieldsInSelectableFields(context,fieldsNameVsFacilioFieldMap,selectableFields);
        filterSupplementFields(context,selectableFields);



    }
    private static void fillSelectableFieldsForMobile(Context context,String selectableFieldNames,List<FacilioField> selectableFields) throws Exception{  //filled by selectableField Names from Payload

        List<FacilioField> allFields=getAllFields(context);
        Map<String,FacilioField> fieldsNameVsFacilioFieldMap=allFields.stream().collect(Collectors.toMap(FacilioField::getName,Function.identity(),(a,b)->b));

        String[] selectableFieldNamesList=selectableFieldNames.split(",");
        for(String fieldName:selectableFieldNamesList){
            FacilioField field=fieldsNameVsFacilioFieldMap.get(fieldName);
            if(field!=null){
                selectableFields.add(field);
            }
        }

        addExtraSelectableFieldsIfExists(context,selectableFields);
        addSortQueryFieldsInSelectableFields(context,fieldsNameVsFacilioFieldMap,selectableFields);
        filterSupplementFields(context,selectableFields);

    }
    private static void filterSupplementFields(Context context,List<FacilioField> selectableFields){
        Map<Long,FacilioField> selectableFieldsMap=selectableFields.stream().collect(Collectors.toMap(FacilioField::getFieldId,Function.identity(),(a,b)->b));

        List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        if (CollectionUtils.isNotEmpty(supplementFields)) {
            filterSupplementFields(supplementFields,selectableFieldsMap);
        }
    }
    private static void filterSupplementFields(List<SupplementRecord> supplementFields,Map<Long,FacilioField> selectableFieldsMap){
        supplementFields.removeIf(supplementRecord -> {
            if (supplementRecord instanceof FacilioField){
                return !selectableFieldsMap.containsKey(((FacilioField) supplementRecord).getFieldId());
            }
            return false;
        });
    }
    private static void addSortQueryFieldsInSelectableFields(Context context,Map<String,FacilioField> fieldsNameVsFacilioFieldMap,List<FacilioField> selectableFields){
        FacilioField sortByFacilioField=null;
        String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
        if (orderBy != null && !orderBy.isEmpty()) {
            JSONObject sorting = (JSONObject) context.get(FacilioConstants.ContextNames.SORTING);
            if (sorting != null && !sorting.isEmpty()) {
                String sortBy = (String) sorting.get("orderBy");
                sortByFacilioField=fieldsNameVsFacilioFieldMap.get(sortBy);
                if(sortByFacilioField!=null){
                    selectableFields.add(sortByFacilioField);
                }
            }
        }
    }

    private static List<FacilioField> getAllFields(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        return modBean.getAllFields(module.getName());
    }
    private static void addExtraSelectableFieldsIfExists(Context context, List<FacilioField> selectableFields){
        List<FacilioField> extraSelectableFields=(List<FacilioField>)context.get(FacilioConstants.ContextNames.EXTRA_SELECTABLE_FIELDS);
        if(CollectionUtils.isNotEmpty(extraSelectableFields)){
            selectableFields.addAll(extraSelectableFields);
        }
    }
}

