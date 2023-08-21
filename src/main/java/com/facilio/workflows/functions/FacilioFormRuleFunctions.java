package com.facilio.workflows.functions;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiEnumField;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.workflows.context.WorkflowContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.FORM_RULE)
public class FacilioFormRuleFunctions {


    Map<String, FormField> formFieldMap = new HashMap<>();
    Map<String, FormSection> formSectionMap = new HashMap<>();

    public Object showSections(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);
        getFormDetails(scriptContext);

        List<String> componentNames = (List<String>) objects[0];
        List<Long> componentIds = getSectionComponentIds(componentNames);

        return getResultListForAction("showSection", componentIds);
    }

    public Object hideSections(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);
        getFormDetails(scriptContext);

        List<String> componentNames = (List<String>) objects[0];
        List<Long> componentIds = getSectionComponentIds(componentNames);

        return getResultListForAction("hideSection", componentIds);

    }


    public Object showFields(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);
        getFormDetails(scriptContext);

        List<String> componentNames = (List<String>) objects[0];
        List<Long> componentIds = getFieldComponentIds(componentNames);

        return getResultListForAction("show", componentIds);

    }

    public Object hideFields(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);
        getFormDetails(scriptContext);

        List<String> componentNames = (List<String>) objects[0];
        List<Long> componentIds = getFieldComponentIds(componentNames);

        return getResultListForAction("hide", componentIds);

    }

    public Object enableFields(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);
        getFormDetails(scriptContext);

        List<String> componentNames = (List<String>) objects[0];
        List<Long> componentIds = getFieldComponentIds(componentNames);

        return getResultListForAction("enable", componentIds);

    }

    public Object disableFields(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);
        getFormDetails(scriptContext);

        List<String> componentNames = (List<String>) objects[0];
        List<Long> componentIds = getFieldComponentIds(componentNames);

        return getResultListForAction("disable", componentIds);

    }

    public Object setMandatory(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);
        getFormDetails(scriptContext);

        List<String> componentNames = (List<String>) objects[0];
        List<Long> componentIds = getFieldComponentIds(componentNames);

        return getResultListForAction("setMandatory", componentIds);

    }

    public Object removeMandatory(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);
        getFormDetails(scriptContext);

        List<String> componentNames = (List<String>) objects[0];
        List<Long> componentIds = getFieldComponentIds(componentNames);

        return getResultListForAction("removeMandatory", componentIds);

    }

    public Object applyEnumFilter(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);
        getFormDetails(scriptContext);

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> componentNameVsValue = (Map<String, Object>) objects[0];

        for (Map.Entry<String, Object> recordEntry : componentNameVsValue.entrySet()) {

            String componentName = recordEntry.getKey();
            Object componentValue = recordEntry.getValue();

            FormField component = formFieldMap.get(componentName);

            if (component == null) {
                return null;
            }
            if (component.getDisplayTypeEnum() != FacilioField.FieldDisplayType.SELECTBOX) {
                return null;
            }
            FacilioField componentField = component.getField();
            Map<Integer, Object> enumMap = new HashMap<>();
            if(componentField instanceof EnumField){
                enumMap = ((EnumField) component.getField()).getEnumMap();
            }else {
                enumMap = ((MultiEnumField) component.getField()).getEnumMap();
            }

            Map<Object, Integer> inverseMap = MapUtils.invertMap(enumMap);

            List<Integer> enumComponentValue = new ArrayList<>();


            long componentId = component.getId();

            for (Object value : (ArrayList) componentValue) {
                Integer enumKey = inverseMap.get(value);
                enumComponentValue.add(enumKey);
            }

            Map<String, Object> valueObject = new HashMap<>();
            Map<String, Object> resultObject = new HashMap<>();
            Map<String, Object> actionObject = new HashMap<>();

            valueObject.put("show", false);
            valueObject.put("values", enumComponentValue);

            actionObject.put("isEnum", true);
            actionObject.put("value", valueObject);
            actionObject.put("actionName", "filter");
            resultObject.put("action", actionObject);
            resultObject.put("fieldId", componentId);

            result.add(resultObject);

        }


        return result;

    }

    public Object applyFilter(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);
        getFormDetails(scriptContext);

        List<Map<String, Object>> results = new ArrayList<>();
        Map<String, Object> componentNameVsValue = (Map<String, Object>) objects[0];

        for (Map.Entry<String, Object> recordEntry : componentNameVsValue.entrySet()) {

            String componentName = recordEntry.getKey();
            Map<String,Object> componentValueMap = (Map<String,Object>)recordEntry.getValue();

            for (Map.Entry<String, Object> recordEntryValue : componentValueMap.entrySet()) {
                String fieldName = recordEntryValue.getKey();
                Object componentValue = recordEntryValue.getValue();

                FormField component = formFieldMap.get(componentName);

                if (component == null) {
                    return null;
                }

                StringJoiner sj = new StringJoiner(",");
                for(Object value : (List<Object>)componentValue){
                    sj.add(String.valueOf(value));
                }

                long componentId = component.getId();

                Criteria filterCriteria = new Criteria();
                Condition filterCondition = new Condition();
                filterCondition.setOperatorId(3);
                filterCondition.setFieldName(fieldName);
                filterCondition.setValue(String.valueOf(sj));
                filterCriteria.addAndCondition(filterCondition);

                Map<String, Object> resultObject = new HashMap<>();
                Map<String, Object> actionObject = new HashMap<>();

                actionObject.put("value", filterCriteria);
                actionObject.put("actionName", "filter");
                resultObject.put("action", actionObject);
                resultObject.put("fieldId", componentId);

                results.add(resultObject);
            }
        }

        return results;

    }

    public Object setValue(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);
        getFormDetails(scriptContext);

        String actionName = "set";
        Map<String, Object> componentNameVsValue = (Map<String, Object>) objects[0];

        List<Map<String, Object>> result = new ArrayList<>();


        for (Map.Entry<String, Object> recordEntry : componentNameVsValue.entrySet()) {

            String componentName = recordEntry.getKey();
            Object componentValue = recordEntry.getValue();
            FormField component = formFieldMap.get(componentName);
            if (component == null) {
                return null;
            }
            long componentId = component.getId();

            if (component.getDisplayTypeEnum() == FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE && componentValue instanceof ArrayList) {
                List<Map<String, Object>> multiLookupValues = new ArrayList<>();
                for (Object value : (ArrayList) componentValue) {
                    Map<String, Object> multiLookupValue = new HashMap<>();
                    multiLookupValue.put("id", value);
                    multiLookupValues.add(multiLookupValue);
                }
                componentValue = multiLookupValues;
            } else if (component.getDisplayTypeEnum() == FacilioField.FieldDisplayType.TEAMSTAFFASSIGNMENT) {

                Long assignmentGroupId = ((Map<String,Long>)componentValue).get("assignmentGroup");
                Long assignedToId = ((Map<String,Long>)componentValue).get("assignedTo");

                Group group = AccountUtil.getGroupBean().getGroup(Long.parseLong(String.valueOf(assignmentGroupId)));
                List<GroupMember> groupMembers = group.getMembers();
                GroupMember groupMember = groupMembers.stream().filter(member -> member.getOuid()==assignedToId).findFirst().get();

                Map<String, Object> teamAndStaff = new HashMap<>();
                teamAndStaff.put("assignmentGroup", new HashMap<String, Object>() {{
                    put("id", assignmentGroupId);
                    put("name",group.getName());
                }});

                teamAndStaff.put("assignedTo", new HashMap<String, Object>() {{
                    put("id", assignedToId);
                    put("name",groupMember.getName());
                }});

                componentValue = teamAndStaff;
            } else if (component.getDisplayTypeEnum() == FacilioField.FieldDisplayType.SELECTBOX) {

                if (component.getField() instanceof EnumField) {
                    Map<Integer, Object> enumMap = ((EnumField) component.getField()).getEnumMap();
                    Map<Object, Integer> inverseMap = MapUtils.invertMap(enumMap);
                    componentValue = inverseMap.get(componentValue);
                } else {
                    Map<Integer, Object> enumMap = ((MultiEnumField) component.getField()).getEnumMap();
                    Map<Object, Integer> inverseMap = MapUtils.invertMap(enumMap);
                    List<Integer> fieldValues = new ArrayList<>();
                    for (String value : (List<String>) componentValue) {
                        componentValue = inverseMap.get(value);
                        fieldValues.add((Integer) componentValue);
                    }
                    componentValue = fieldValues;
                }

            } else if (component.getDisplayTypeEnum() == FacilioField.FieldDisplayType.GEO_LOCATION) {

                double lat = Double.parseDouble(((List<String>) componentValue).get(0));
                double lng = Double.parseDouble(((List<String>) componentValue).get(1));
                Map<String, Object> locationValue = new HashMap<>();
                locationValue.put("lat", lat);
                locationValue.put("lng", lng);
                componentValue = locationValue;

            } else if (component.getDisplayTypeEnum() == FacilioField.FieldDisplayType.URL_FIELD) {

                String name = ((List<String>) componentValue).get(0);
                String href = ((List<String>) componentValue).get(1);

                Map<String, Object> urlValue = new HashMap<>();
                urlValue.put("name", name);
                urlValue.put("href", href);
                componentValue = urlValue;

            }

            Map<String, Object> resultObject = new HashMap<>();
            Map<String, Object> actionObject = new HashMap<>();

            actionObject.put("value", componentValue);
            actionObject.put("actionName", actionName);
            resultObject.put("action", actionObject);
            resultObject.put("fieldId", componentId);

            result.add(resultObject);


        }

        return result;

    }

    private List<Long> getSectionComponentIds(List<String> componentNames) {

        List<Long> componentIds = new ArrayList<>();

        for (String componentName : componentNames) {
            long componentId = formSectionMap.get(componentName).getId();
            componentIds.add(componentId);
        }

        return componentIds;
    }

    private List<Long> getFieldComponentIds(List<String> componentNames) {

        List<Long> componentIds = new ArrayList<>();

        for (String componentName : componentNames) {
            long componentId = formFieldMap.get(componentName).getId();
            componentIds.add(componentId);
        }

        return componentIds;
    }

    private void getFormDetails(ScriptContext scriptContext) {

        Map<String,Object> formData = (Map<String,Object> ) ((WorkflowContext) scriptContext).getParams().get(0);
        FacilioForm form = (FacilioForm) formData.get(FacilioConstants.ContextNames.FORM_RULE_FORM_OBJECT);

        List<FormField> formFields = new ArrayList<>();
        List<FormSection> formSections = new ArrayList<>();


        if (form == null && CollectionUtils.isEmpty(form.getSections())) {
            return;
        }

        formFields = FormsAPI.getFormFieldsFromSections(form.getSections());
        formSections = form.getSections();

        for(FormField formField :formFields){
            FacilioField facilioField = formField.getField();
            if(facilioField == null){
                formFieldMap.put(formField.getName(),formField);
            }else{
                formFieldMap.put(facilioField.getName(),formField);
            }
        }

        for (FormSection section : formSections) {
            formSectionMap.put(section.getLinkName(), section);
            if (section.getSectionTypeEnum() == FormSection.SectionType.SUB_FORM) {
                List<FormField> subFormFields = FormsAPI.getFormFieldsFromSections(section.getSubForm().getSections());
                Map<String, FormField> subFormFieldMap = subFormFields.stream().collect(Collectors.toMap(FormField::getName, Function.identity(), (name1, name2) -> {
                    return name1;
                }));
                formFieldMap.putAll(subFormFieldMap);
            }
        }

    }

    private void checkParam(Object... objects) throws Exception {
        if (objects.length <= 0) {
            throw new FunctionParamException("Required Object is null");
        }
    }

    private List<Map<String, Object>> getResultListForAction(String actionName, List<Long> componentIds) {

        List<Map<String, Object>> result = new ArrayList<>();

        for (Long componentId : componentIds) {

            Map<String, Object> resultObject = new HashMap<>();
            Map<String, Object> actionObject = new HashMap<>();

            actionObject.put("value", null);
            actionObject.put("actionName", actionName);
            resultObject.put("action", actionObject);

            if (Objects.equals(actionName, "hideSection") || Objects.equals(actionName, "showSection")) {
                resultObject.put("sectionId", componentId);
            } else {
                resultObject.put("fieldId", componentId);
            }

            result.add(resultObject);
        }

        return result;

    }

}
