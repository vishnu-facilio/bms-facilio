package com.facilio.bmsconsole.imports.command;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportSiteAction;
import com.facilio.bmsconsole.commands.ImportProcessLogContext;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.imports.annotations.AfterRowFunction;
import com.facilio.bmsconsole.imports.annotations.RowFunction;
import com.facilio.bmsconsole.util.*;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class V3ProcessImportCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(V3ProcessImportCommand.class.getName());

    // TODO skip records if any of the value cannot be resolved.. like lookup name cannot be resolved to id
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List<Map<String, Object>>> groupedContext = new HashMap<>();
        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (StringUtils.isEmpty(moduleName)) {
            moduleName = importProcessContext.getModuleName();
        }
        if (StringUtils.isEmpty(moduleName)) {
            throw new IllegalArgumentException("Module name is empty");
        }

        JSONObject importMeta = importProcessContext.getImportJobMetaJson();
        RowFunction beforeImportFunction = (RowFunction) context.get(ImportAPI.ImportProcessConstants.BEFORE_IMPORT_FUNCTION);
        AfterRowFunction afterImportFunction = (AfterRowFunction) context.get(ImportAPI.ImportProcessConstants.AFTER_IMPORT_FUNCTION);

        List<Map<String, Object>> allRows = ImportAPI.getValidatedRows(importProcessContext.getId());

        // TODO change this behaviour since it will take only 5k sites
        List<SiteContext> sites = SpaceAPI.getAllSites();
        Map<String, SiteContext> sitesMap = null;
        if (CollectionUtils.isNotEmpty(sites)) {
            sitesMap = sites.stream().collect(Collectors.toMap(site -> site.getName().trim(), Function.identity(), (a,b) -> a));
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<BaseLookupField, Map<String, Object>> lookupMap = loadLookupMap(modBean, allRows, context, moduleName, fieldMapping);

        for(Map<String, Object> row: allRows) {
            ImportRowContext rowContext = getRowContext(row);
            if (rowContext == null) {
                continue;
            }

            int rowNo = rowContext.getRowNumber();
            HashMap<String, Object> rowVal = rowContext.getColVal();

            LOGGER.info("row -- " + rowNo + " rowVal --- " + rowVal);

            HashMap<String, Object> props = new LinkedHashMap<>();

            // adding source_type and source_id in the props
            props.put(FacilioConstants.ContextNames.SOURCE_TYPE, SourceType.IMPORT.getIndex());
            props.put(FacilioConstants.ContextNames.SOURCE_ID, importProcessContext.getId());

            if (fieldMapping.containsKey(moduleName + "__formId") && rowVal.get(fieldMapping.get(moduleName + "__formId")) != null) {
                props.put("formId", rowVal.get(fieldMapping.get(moduleName + "__formId")));
            }

            // add site only for insert update
            if (!(importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.UPDATE.getValue() || importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.UPDATE_NOT_NULL.getValue())) {
                if (MapUtils.isNotEmpty(sitesMap)) {
                    if (rowVal.containsKey(fieldMapping.get(moduleName + "__site"))) {
                        String siteName = (String) rowVal.get(fieldMapping.get(moduleName + "__site"));
                        SiteContext siteContext = sitesMap.get(siteName.trim().toLowerCase());
                        props.put("siteId", siteContext.getId());
                    }
                }
            }

            List<FacilioField> fields = getFields(modBean, context, moduleName);

            if (beforeImportFunction != null) {
                beforeImportFunction.apply(rowNo, rowVal, context);
            }

            for (FacilioField field : fields) {
                String key = field.getModule().getName() + "__" + field.getName();
                if (!fieldMapping.containsKey(key)) {
                    // there is no mapping for this field. Don't do anything
                    continue;
                }

                Object cellValue = rowVal.get(fieldMapping.get(key));
                if (isEmpty(cellValue)) {
                    // The value of row is empty. Set it as null. // todo check this flow
                    props.put(field.getName(), null);
                    continue;
                }

                try {
                    if (field.getName().equals(FacilioConstants.ContextNames.SITE_ID)) {
                        String cellValueString = cellValue.toString();
                        if (StringUtils.isNotEmpty(cellValueString) && MapUtils.isNotEmpty(sitesMap)) {
                            SiteContext site = sitesMap.get(cellValueString);
                            if (site != null && site.getId() > 0) {
                                props.put(field.getName(), site.getId());
                            }
                        }
                        continue;
                    }

                    switch (field.getDataTypeEnum()) {
                        case DATE:
                        case DATE_TIME: {
                            if (!(cellValue instanceof Long)) {
                                long millis;
                                JSONObject dateFormats = (JSONObject) importMeta.get(ImportAPI.ImportProcessConstants.DATE_FORMATS);
                                if (dateFormats.get(fieldMapping.get(key)).equals(ImportAPI.ImportProcessConstants.TIME_STAMP_STRING)) {
                                    millis = Long.parseLong(cellValue.toString());
                                } else {
                                    Instant dateInstant = DateTimeUtil.getTimeInstant(dateFormats.get(fieldMapping.get(key)).toString(), cellValue.toString());
                                    millis = dateInstant.toEpochMilli();
                                }
                                if (!props.containsKey(field.getName())) {
                                    props.put(field.getName(), millis);
                                }
                            }
                            break;
                        }
                        case SYSTEM_ENUM: {
                            SystemEnumField enumField = (SystemEnumField) field;
                            String enumString = (String) rowVal.get(fieldMapping.get(key));
                            int enumIndex = enumField.getIndex(enumString);

                            if (!props.containsKey(field.getName())) {
                                props.put(field.getName(), enumIndex);
                            }
                            break;
                        }
                        case ENUM: {
                            EnumField enumField = (EnumField) field;
                            String enumString = (String) rowVal.get(fieldMapping.get(key));
                            int enumIndex = enumField.getIndex(enumString);

                            if (!props.containsKey(field.getName())) {
                                props.put(field.getName(), enumIndex);
                            }
                            break;
                        }
                        case MULTI_ENUM: {
                            MultiEnumField multiEnumField = (MultiEnumField) field;
                            String enumString = (String) rowVal.get(fieldMapping.get(key));
                            ArrayList enumIndices = new ArrayList();
                            if (StringUtils.isNotEmpty(enumString)) {
                                for (String string : FacilioUtil.splitByComma(enumString)) {
                                    int enumIndex = multiEnumField.getIndex(string);
                                    if (enumIndex > 0) {
                                        enumIndices.add(enumIndex);
                                    }
                                }
                            }
                            if (!props.containsKey(field.getName())) {
                                props.put(field.getName(), enumIndices);
                            }
                            break;
                        }
                        case MULTI_LOOKUP: {
                            String multiLookupValue = cellValue.toString();
                            String[] split = multiLookupValue.split(",");
                            List<Object> values = new ArrayList<>();
                            Map<String, Object> nameVsIds = lookupMap.get(field);
                            for (String s : split) {
                                String trimmedStr = s.toLowerCase().trim();
                                if (nameVsIds.containsKey(trimmedStr) && nameVsIds.get(trimmedStr) != null) {
                                    values.add(nameVsIds.get(trimmedStr));
                                }
                            }
                            props.put(field.getName(), values);
                            break;
                        }
                        case LOOKUP: {
                            // check with Krishna on how to get data
                            Map<String, Object> nameVsIds = lookupMap.get(field);
                            props.put(field.getName(), nameVsIds.get(cellValue.toString().toLowerCase().trim()));
                            break;
                        }
                        case NUMBER:
                        case DECIMAL: {
                            String cellValueString = cellValue.toString();
                            if (cellValueString.contains(",")) {
                                cellValueString = cellValueString.replaceAll(",", "");
                            }

                            Double cellDoubleValue = Double.parseDouble(cellValueString);
                            if (!props.containsKey(field.getName())) {
                                props.put(field.getName(), cellDoubleValue);
                            }
                            break;
                        }
                        default: {
                            if (!props.containsKey(field.getName())) {
                                props.put(field.getName(), cellValue);
                            }
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.severe("Process Import Exception -- Row No --" + rowNo + " Fields Mapping --" + fieldMapping.get(key));
                    throw new ImportParseException(rowNo, fieldMapping.get(key), ex);
                }
            }

            if (afterImportFunction != null) {
                afterImportFunction.apply(rowNo, rowVal,props, context);
            }

            if(groupedContext.containsKey(moduleName)) {
                List<Map<String, Object>> existingList = groupedContext.get(moduleName);
                existingList.add(props);
            }
            else {
                List<Map<String, Object>> tempList = new ArrayList<>();
                tempList.add(props);
                groupedContext.put(moduleName, tempList);
            }
        }

        Constants.setBulkRawInput(context, groupedContext.get(moduleName));
//        c.put(ImportAPI.ImportProcessConstants.GROUPED_FIELDS, groupedFields);
//        context.put(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT, groupedContext);
//        c.put(FacilioConstants.ContextNames.RECORD_LIST, recordsList);

        return false;
    }

    private Map<BaseLookupField, Map<String, Object>> loadLookupMap(ModuleBean modBean, List<Map<String, Object>> allRows, Context context, String moduleName, HashMap<String, String> fieldMapping) throws Exception {
        Map<BaseLookupField, Map<String, Object>> lookupMap = new HashMap<>();
        for (Map<String, Object> row : allRows) {
            ImportRowContext rowContext = getRowContext(row);
            if (rowContext == null) {
                continue;
            }

            HashMap<String, Object> rowVal = rowContext.getColVal();

            List<FacilioField> fields = getFields(modBean, context, moduleName);
            for (FacilioField field : fields) {
                if (field.getDataTypeEnum() == FieldType.LOOKUP || field.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
                    String key = field.getModule().getName() + "__" + field.getName();
                    Object cellValue = rowVal.get(fieldMapping.get(key));
                    if (isEmpty(cellValue)) {
                        continue;
                    }

                    String[] values;
                    if (field.getDataTypeEnum() == FieldType.LOOKUP) {
                        values = new String[]{cellValue.toString()};
                    } else if (field.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
                        values = cellValue.toString().split(",");
                    } else {
                        continue;
                    }

                    BaseLookupField lookupField = (BaseLookupField) field;
                    Map<String, Object> numberIdPairList = lookupMap.get(lookupField);
                    if (numberIdPairList == null) {
                        numberIdPairList = new HashMap<>();
                        lookupMap.put(lookupField, numberIdPairList);
                    }
                    for (String value : values) {
                        numberIdPairList.put(value.toLowerCase().trim(), null);
                    }
                }
            }
        }

        for (BaseLookupField lookupField : lookupMap.keySet()) {
            String lookupModuleName="";
            if(lookupField.getLookupModule() == null && lookupField.getSpecialType() != null) {
                lookupModuleName = lookupField.getSpecialType();
            }
            else {
                lookupModuleName = lookupField.getLookupModule().getName();
            }
            Map<String, Object> nameIdMap = lookupMap.get(lookupField);
            if(LookupSpecialTypeUtil.isSpecialType(lookupModuleName)) {
                    for(String name:nameIdMap.keySet()){
                        Map<String,Object> propValue=getSpecialLookupProps(lookupField,name,lookupModuleName);
                        nameIdMap.put(name,propValue);
                    }
            }else{
                List<FacilioField> fieldsList = new ArrayList<>();
                fieldsList.add(FieldFactory.getIdField(lookupField.getLookupModule()));
                FacilioField primaryField = getImportLookupMainField(modBean, context, lookupField.getLookupModule().getName());
                fieldsList.add(primaryField);
                SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder =  new SelectRecordsBuilder<>()
                        .module(lookupField.getLookupModule())
                        .select(fieldsList);
                if (lookupField.getName().equals("moduleState")) {
                    selectBuilder.andCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", String.valueOf(lookupField.getModule().getModuleId()), NumberOperators.EQUALS));
                } else if (lookupField.getName().equals("approvalStatus")) {
                    selectBuilder.andCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", null, CommonOperators.IS_EMPTY));
                }


                Set<String> set = new HashSet<>();

                for (String name : nameIdMap.keySet()) {
                    set.add(name.replace(",", StringOperators.DELIMITED_COMMA));
                }

                selectBuilder.andCondition(CriteriaAPI.getCondition(primaryField, StringUtils.join(set, ","), StringOperators.IS));
                List<Map<String, Object>> props = selectBuilder.getAsProps();
                if (CollectionUtils.isNotEmpty(props)) {
                    Map<String, Map<String, Object>> propMap = props.stream().collect(Collectors.toMap(prop -> (String) prop.get(primaryField.getName()), Function.identity(), (a, b) -> a));
                    for (String name : propMap.keySet()) {
                        String nameLower = name.toLowerCase().trim();
                        if (propMap.containsKey(name)) {
                            nameIdMap.put(nameLower, propMap.get(name));
                        }
                    }
                }
            }

        }
        return lookupMap;
    }

    private FacilioField getImportLookupMainField(ModuleBean modBean, Context context, String moduleName) throws Exception {
        Map<String, String> lookupMainFieldMap = (Map<String, String>) context.get(ImportAPI.ImportProcessConstants.LOOKUP_MAIN_FIELD_MAP);
        FacilioField primaryField = null;
        if (MapUtils.isNotEmpty(lookupMainFieldMap) && lookupMainFieldMap.containsKey(moduleName)) {
            String fieldName = lookupMainFieldMap.get(moduleName);
            primaryField = modBean.getField(fieldName, moduleName);
        }
        if (primaryField == null) {
            primaryField = modBean.getPrimaryField(moduleName);
        }
        return primaryField;
    }

    private boolean isEmpty(Object cellValue) {
        if (cellValue == null || cellValue.toString().equals("") || (cellValue.toString().equals("n/a"))) {
            return true;
        }
        return false;
    }

    private ImportRowContext getRowContext(Map<String, Object> row) throws Exception {
        ImportProcessLogContext rowLogContext = FieldUtil.getAsBeanFromMap(row, ImportProcessLogContext.class);

        ImportRowContext rowContext = null;
        if(rowLogContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getValue()){
            rowContext = rowLogContext.getRowContexts().get(0);
        }
        else if(rowLogContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.RESOLVED.getValue()) {
            rowContext = rowLogContext.getCorrectedRow();
        }
        return rowContext;
    }

    private List<FacilioField> getFields(ModuleBean modBean, Context context, String moduleName) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        if (CollectionUtils.isEmpty(fields)) {
            fields = modBean.getAllFields(moduleName);
        }
        if (CollectionUtils.isEmpty(fields)) {
            throw new IllegalArgumentException("Fields not found for module " + moduleName);
        }
        return fields;
    }
    public static Map<String, Object> getSpecialLookupProps(BaseLookupField lookupField,Object value,String moduleName) throws Exception{
        String key = lookupField.getModule().getName() + "__" + lookupField.getName();
        try {
            if(value == null || value.toString().isEmpty()) {
                if (!lookupField.isRequired()) {
                    return null;
                } else {
                    throw new Exception("Field value missing under column " + key + ".");
                }
            }

            switch (moduleName) {
                case "users": {
                    long appId=ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
                    AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication(appId);

                    String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
                    Pattern pattern = Pattern.compile(emailRegex);
                    Matcher matcher= pattern.matcher(value.toString());

                    if(matcher.matches()){
                       User user=AccountUtil.getUserBean().getUserFromEmail(value.toString(),appDomainObj != null ? appDomainObj.getIdentifier() : null,AccountUtil.getCurrentOrg().getOrgId());
                       if(user!=null){
                           return FieldUtil.getAsProperties(user);
                       }

                    }
                }
                case "role":{
                    long appId=ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
                    Role role = AccountUtil.getRoleBean(AccountUtil.getCurrentOrg().getOrgId()).getRole(AccountUtil.getCurrentOrg().getOrgId(),value.toString());
                    if(role!=null){
                        return FieldUtil.getAsProperties(role);
                    }

                }
                case "groups": {
                    Group group = AccountUtil.getGroupBean().getGroup(value.toString());
                    if (group == null && lookupField.getName().equals("assignmentGroup")) {
                        throw new Exception("Value not found");
                    }
                    return FieldUtil.getAsProperties(group);
                }

            }

        } catch (Exception e) {
            LOGGER.severe("Exception occurred for special lookup: " + e.toString());
            throw e;
        }
        return null;
    }

}
