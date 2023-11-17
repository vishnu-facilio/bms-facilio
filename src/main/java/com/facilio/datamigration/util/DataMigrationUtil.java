package com.facilio.datamigration.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import com.opencsv.CSVReader;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class DataMigrationUtil {

    public static List<SupplementRecord> getSupplementFields(Collection<FacilioField> fields) {
        List<SupplementRecord> supplementFields = new ArrayList<>();
        for (FacilioField f : fields) {
            if ((!f.isDefault() && (f.getDataTypeEnum() == FieldType.LOOKUP || f.getDataTypeEnum() == FieldType.MULTI_LOOKUP
                    || f.getDataTypeEnum() == FieldType.MULTI_ENUM))
                    || (f.getDataTypeEnum().isRelRecordField())) {
                supplementFields.add((SupplementRecord) f);
            }
        }

        return supplementFields;
    }

    public static Map<Long, List<Long>> getIdsAndLookupIdsToFetch(List<Map<String, Object>> props, long moduleId,
                                                                  Map<String, FacilioField> targetFieldNameVsFields, Map<String, Map<String, Object>> numberLookups) throws Exception {
        Map<Long, List<Long>> moduleIdVsOldRecordIds = new HashMap<>();
        for (Map<String, Object> prop : props) {
            for (Map.Entry<String, Object> value : prop.entrySet()) {
                String fieldName = value.getKey();
                Object data = value.getValue();
                if (data != null && StringUtils.isNotEmpty(data.toString())) {
                    FacilioField fieldObj = targetFieldNameVsFields.get(fieldName);
                    if (fieldObj != null) {
                        switch (fieldObj.getDataTypeEnum()) {
                            case LOOKUP:
                                FacilioModule lookupModule = ((LookupField) fieldObj).getLookupModule();
                                if (lookupModule.getModuleId() > 0 && MapUtils.isNotEmpty(((Map<String, Object>) data))) {
                                    Long lookupDataId = (Long) ((Map<String, Object>) data).get("id");
                                    if (moduleIdVsOldRecordIds.containsKey(lookupModule.getModuleId())) {
                                        moduleIdVsOldRecordIds.get(lookupModule.getModuleId()).add(lookupDataId);
                                    } else {
                                        moduleIdVsOldRecordIds.put(lookupModule.getModuleId(), new ArrayList<Long>() {{
                                            add(lookupDataId);
                                        }});
                                    }
                                }
                                break;
                            case MULTI_LOOKUP:
                                FacilioModule multilookupModule = ((MultiLookupField) fieldObj).getLookupModule();
                                if (multilookupModule.getModuleId() > 0 && CollectionUtils.isNotEmpty((List<Map<String, Object>>) data)) {
                                    for (Map<String, Object> lookupData : (List<Map<String, Object>>) data) {
                                        Long lookupDataId = (Long) (lookupData).get("id");
                                        if (moduleIdVsOldRecordIds.containsKey(multilookupModule.getModuleId())) {
                                            moduleIdVsOldRecordIds.get(multilookupModule.getModuleId()).add(lookupDataId);
                                        } else {
                                            moduleIdVsOldRecordIds.put(multilookupModule.getModuleId(), new ArrayList<Long>() {{
                                                add(lookupDataId);
                                            }});
                                        }
                                    }
                                }
                                break;
                            case NUMBER:
                                if (MapUtils.isNotEmpty(numberLookups) && numberLookups.containsKey(fieldName)) {
                                    Long lookupDataId = (Long) data;
                                    if (lookupDataId != null && lookupDataId > 0) {
                                        String lookupModuleName = (String) numberLookups.get(fieldName).get("lookupModuleName");
                                        if (!(FacilioConstants.ContextNames.USERS.equals(lookupModuleName))) {
                                            Long parentModuleId = (Long) numberLookups.get(fieldName).get("lookupModuleId");
                                            if (moduleIdVsOldRecordIds.containsKey(parentModuleId)) {
                                                moduleIdVsOldRecordIds.get(parentModuleId).add(lookupDataId);
                                            } else {
                                                moduleIdVsOldRecordIds.put(parentModuleId, new ArrayList<Long>() {{
                                                    add(lookupDataId);
                                                }});
                                            }
                                        }
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    if (fieldName.equals("id")) {
                        if (moduleIdVsOldRecordIds.containsKey(moduleId)) {
                            moduleIdVsOldRecordIds.get(moduleId).add((long) data);
                        } else {
                            moduleIdVsOldRecordIds.put(moduleId, new ArrayList<Long>() {{
                                add((long) data);
                            }});
                        }
                    }
                }
            }
        }
        return moduleIdVsOldRecordIds;
    }

    public static Criteria getModuleSpecificCriteria(FacilioModule module) {
        Criteria cr = null;
        switch (module.getName()) {
            case "people":
                cr = new Criteria();
                cr.addAndCondition(CriteriaAPI.getCondition("PEOPLE_TYPE", "peopleType", String.valueOf(V3PeopleContext.PeopleType.EMPLOYEE.getIndex()), NumberOperators.NOT_EQUALS));
                break;
            default:
                break;
        }
        return cr;
    }

    public static List<String> getNumberFileFields(FacilioModule module) {
        List<String> numberFieldsVsFileFields = new ArrayList<>();

        switch (module.getTypeEnum()) {
            case PHOTOS:
                numberFieldsVsFileFields.add("photoId");
                break;
            case ATTACHMENTS:
                numberFieldsVsFileFields.add("fileId");
                break;
            default:
                break;
        }

        switch (module.getName()) {
            case "tenant":
                numberFieldsVsFileFields.add("logoId");
                break;
            case "announcement":
                numberFieldsVsFileFields.add("fileId");
                numberFieldsVsFileFields.add("photoId");
                break;
            case "neighbourhood":
                numberFieldsVsFileFields.add("fileId");
                break;
            case "dealsandoffers":
                numberFieldsVsFileFields.add("fileId");
                break;
            case "newsandinformation":
                numberFieldsVsFileFields.add("fileId");
                break;
            case "toolTypes":
                numberFieldsVsFileFields.add("photoId");
                break;
            case "itemTypes":
                numberFieldsVsFileFields.add("photoId");
                break;
            case "resource":
                numberFieldsVsFileFields.add("photoId");
                break;
            case "qandaHeadingQuestionRichText":
                numberFieldsVsFileFields.add("fileId");
                break;
            default:
                break;
        }
        return numberFieldsVsFileFields;
    }

    public static Map<String, Map<String, Object>> getNumberLookups(FacilioModule module, ModuleBean targetModuleBean, Map<String, FacilioModule> targetModuleNameVsObj) throws Exception {
        Map<String, Map<String, Object>> numberFieldsVsLookupModules = new HashMap<>();

        switch (module.getName()) {
            case "site":
                addNumberLookupDetails("weatherStation", "weatherstation", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "floor":
                addNumberLookupDetails("floorPlanId", "indoorfloorplan", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("defaultFloorPlanId", "indoorfloorplan", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("indoorFloorPlanId", "indoorfloorplan", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "asset":
                addNumberLookupDetails("parentAssetId", "asset", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("currentSpaceId", "basespace", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("lastIssuedToWo", "workorder", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("lastDowntimeId", "assetbreakdown", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "assetdepreciationCalculation":
                addNumberLookupDetails("depreciationId", "assetdepreciation", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "people":
                addNumberLookupDetails("roleId", "role", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "workorder":
                addNumberLookupDetails("pmPlanner", "pmPlanner", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("pmV2", "plannedmaintenance", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("pmResourcePlanner", "pmResourcePlanner", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("pmTriggerV2", "pmTriggerV2", targetModuleNameVsObj, numberFieldsVsLookupModules);
                //addNumberLookupDetails("approvalState", "ticketstatus", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "task":
                addNumberLookupDetails("parentTicketId", "ticket", targetModuleNameVsObj, numberFieldsVsLookupModules);
//                addNumberLookupDetails("statusNew", "ticketstatus", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "workorderLabour":
                addNumberLookupDetails("parentId", "ticket", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "workorderService":
                addNumberLookupDetails("parentId", "ticket", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("vendor", "vendor", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "assetmovement":
                addNumberLookupDetails("fromSite", "site", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("toSite", "site", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("fromSpace", "basespace", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("toSpace", "basespace", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("assetId", "asset", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "customMailMessages":
                addNumberLookupDetails("parentId", "customMailMessages", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "qandaMcqSingleOptions":
                addNumberLookupDetails("parentId", "qandaMcqSingle", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "qandaMcqMultiOptions":
                addNumberLookupDetails("parentId", "qandaMcqMulti", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "qandaMatrixQuestionRow":
                addNumberLookupDetails("parentId", "matrixQuestion", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "qandaMatrixQuestionColumn":
                addNumberLookupDetails("parentId", "matrixQuestion", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "pmTriggerV2":
                addNumberLookupDetails("pmId", "plannedmaintenance", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "pmPlanner":
                addNumberLookupDetails("pmId", "plannedmaintenance", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "pmResourcePlanner":
                addNumberLookupDetails("pmId", "plannedmaintenance", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "peopleGroupMember":
                addNumberLookupDetails("memberId", "peopleGroup", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("memberRole", "role", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("ouid", "users", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "kpiResourceLogger":
                addNumberLookupDetails("kpiId", "readingkpi", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("resourceId", "resource", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "readingrulerca":
                addNumberLookupDetails("ruleid", "newreadingrules", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "readingrulerca_group":
                addNumberLookupDetails("rcaid", "readingrulerca", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "readingrulerca_score_condition":
                addNumberLookupDetails("groupid", "readingrulerca_group", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "readingrulerca_score_readings":
                addNumberLookupDetails("rcaGroupId", "readingrulerca_group", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "faultimpact":
                addNumberLookupDetails("workflowid", "workflow", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "faultimpactfields":
                addNumberLookupDetails("impact_id", "faultimpact", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "peopleannouncement":
                addNumberLookupDetails("parentId", "announcement", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "audienceSharing":
                addNumberLookupDetails("sharedToRoleId", "role", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "gatePassLineItems":
                addNumberLookupDetails("gatePass", "gatePass", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "poLineItemSerialNumbers":
                addNumberLookupDetails("poId", "purchaseorder", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("receiptId", "receipts", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "serviceVendors":
                addNumberLookupDetails("serviceId", "service", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "shipmentLineItem":
                addNumberLookupDetails("shipment", "shipment", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "warrantycontractlineitems":
                addNumberLookupDetails("warrantyContractId", "warrantycontracts", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "itemTransactions":
                addNumberLookupDetails("shipment", "shipment", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("parentTransactionId", "itemTransactions", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "toolTransactions":
                addNumberLookupDetails("shipment", "shipment", targetModuleNameVsObj, numberFieldsVsLookupModules);
                addNumberLookupDetails("parentTransactionId", "toolTransactions", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "receipts":
                addNumberLookupDetails("receivableId", "receivable", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "contractterms":
                addNumberLookupDetails("contractId", "contracts", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "inventoryrequestlineitems":
                addNumberLookupDetails("parentId", "inventoryrequest", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;
            case "hvac":
                addNumberLookupDetails("currentSpaceId", "basespace", targetModuleNameVsObj, numberFieldsVsLookupModules);
                break;

            default:
                break;
        }

        if (module.getTypeEnum().equals(FacilioModule.ModuleType.PHOTOS) ||
                module.getTypeEnum().equals(FacilioModule.ModuleType.NOTES) ||
                module.getTypeEnum().equals(FacilioModule.ModuleType.ATTACHMENTS) ||
                module.getTypeEnum().equals(FacilioModule.ModuleType.ACTIVITY)) {
            FacilioModule parentModule = targetModuleBean.getParentModule(module.getModuleId());
            // "customactivity" has no relation in SubModuleRel
            if (module.getName().equals("customactivity")) {
                parentModule = module;
            }
            addNumberLookupDetails("parentId", parentModule.getName(), targetModuleNameVsObj, numberFieldsVsLookupModules);
        } else if (module.getTypeEnum().equals(FacilioModule.ModuleType.READING)) {
            addNumberLookupDetails("parentId", "resource", targetModuleNameVsObj, numberFieldsVsLookupModules);
        }
        return numberFieldsVsLookupModules;
    }

    private static void addNumberLookupDetails(String fieldName, String lookupModuleName, Map<String, FacilioModule> targetModuleNameVsObj, Map<String, Map<String, Object>> numberFieldsVsLookupModules) throws Exception{
        HashMap lookupDetails = new HashMap<>();

        lookupDetails.put("lookupModuleName", lookupModuleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(lookupModuleName);
        lookupDetails.put("lookupModule",module);
        numberFieldsVsLookupModules.put(fieldName, lookupDetails);
    }

    public static List<FacilioModule> getModuleDetails(List<String> dataMigrationModules,Map<String,FacilioModule> allSystemModulesMap) throws Exception {

        List<FacilioModule> moduleDetails = new ArrayList<>();
        for(String moduleName : dataMigrationModules){
            if(allSystemModulesMap.get(moduleName)!=null){
                moduleDetails.add(allSystemModulesMap.get(moduleName));
            }
        }

        return moduleDetails;
    }

    public static List<FacilioModule> getSelectedModulesExtendedModules(List<FacilioModule> selectedModules,Map<String,FacilioModule> allSystemModulesMap) throws Exception{

        List<FacilioModule> allExtendedModules = new ArrayList<>();
        List<FacilioModule> allSystemModules = new ArrayList<>(allSystemModulesMap.values());

        for(FacilioModule selectedModule : selectedModules){
            if(LookupSpecialTypeUtil.isSpecialType(selectedModule.getName())) {
                continue;
            }
            List<FacilioModule> extendedModules = allSystemModules.stream().filter(facilioModule -> (facilioModule.getExtendModule()!=null && facilioModule.getExtendModule().getModuleId()==selectedModule.getModuleId())).collect(Collectors.toList());
            allExtendedModules.addAll(extendedModules);
        }

        return allExtendedModules;

    }



    public static List<Integer> getModuleTypesToSkip() {

        List<Integer> moduleTypesToSkip = new ArrayList<Integer>() {{

            add(FacilioModule.ModuleType.CLASSIFICATION_DATA.getValue());
            add(FacilioModule.ModuleType.RELATION_DATA.getValue());
            add(FacilioModule.ModuleType.LOOKUP_REL_MODULE.getValue());
            add(FacilioModule.ModuleType.ENUM_REL_MODULE.getValue());
            add(FacilioModule.ModuleType.SYSTEM_LOOKUP.getValue());
            add(FacilioModule.ModuleType.LARGE_TEXT_DATA_MODULE.getValue());

        }};

        return moduleTypesToSkip;
    }

    public static Set<String> getAllSkipMigrationModules(){

        Set<String> skipModules = new HashSet<>();
        skipModules.addAll(getConfigurationTypeModulesModules());
        skipModules.addAll(getReadingTypeModulesModules());

        return skipModules;
    }

    public static Set<String> getConfigurationTypeModulesModules() {
        return CONFIGURATION_TYPE_MODULES;
    }

    private static final Set<String> CONFIGURATION_TYPE_MODULES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    "alarmseverity",
                    "assetcategory",
                    "assetdepartment",
                    "assettype",
                    "energymeterpurpose",
                    "itemStatus",
                    "readingalarmcategory",
                    "servicerequestpriority",
                    "spacecategory",
                    "ticketcategory",
                    "ticketpriority",
                    "ticketstatus",
                    "tickettype",
                    "toolStatus",
                    "workpermittype",
                    "weatherservice",
                    "weatherstation",
                    "inspectionPriority",
                    "peopleGroup",
                    "peopleGroupMember",
                    "basespace"))
    );

    public static Set<String> getReadingTypeModulesModules() {
        return READING_TYPE_MODULES;
    }

    private static final Set<String> READING_TYPE_MODULES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList( "baseEvent",
                    "alarmoccurrence",
                    "baseAlarm",
                    "newreadingalarm",
                    "readingrcaalarm",
                    "bmsalarm",
                    "violationalarm",
                    "mlAnomalyAlarm",
                    "prealarm",
                    "sensoralarm",
                    "sensorrollupalarm",
                    "rulerollupalarm",
                    "assetrollupalarm",
                    "agentAlarm",
                    "controllerAlarm",
                    "operationalarm",
                    "violationalarmoccurrence",
                    "prealarmoccurrence",
                    "sensoralarmoccurrence",
                    "sensorrollupalarmoccurrence",
                    "rulerollupoccurrence",
                    "assetrollupoccurrence",
                    "anomalyalarmoccurrence",
                    "readingalarmoccurrence",
                    "agentAlarmOccurrence",
                    "controllerAlarmOccurrence",
                    "operationalarmoccurrence",
                    "bmsalarmoccurrence",
                    "readingevent",
                    "readingrcaevent",
                    "mlAnomalyEvent",
                    "bmsevent",
                    "violationevent",
                    "preevent",
                    "sensorevent",
                    "sensorrollupevent",
                    "rulerollupevent",
                    "assetrollupevent",
                    "agentAlarmEvent",
                    "controllerAlarmEvent",
                    "operationevent",
                    "newreadingrules",
                    "readingrulerca",
                    "readingrulerca_group",
                    "readingrulerca_score_readings",
                    "readingrulerca_score_condition",
                    "readingkpi",
                    "kpiLogger",
                    "kpiResourceLogger"))
    );

    public static List<Map<String, Object>> getInsertDataPropsFromCsv(File moduleCsvFile, FacilioModule targetModule, Map<ComponentType, List<PackageChangeSetMappingContext>> packageChangSets,Map<String,Map<String,String>> nonNullableModuleVsFieldVsLookupModules,DataMigrationBean targetConnection,DataMigrationStatusContext dataMigrationObj,Map<Long,Long>siteIdMappings,boolean allowNotesAndAttachments,Map<String, Map<String, Object>> numberLookupDetails) throws Exception {

        List<Map<String, Object>> insertDataProps = new ArrayList<>();

        if (moduleCsvFile == null) {
            return insertDataProps;
        }

        try (CSVReader csvReader = new CSVReader(new FileReader(moduleCsvFile.getAbsolutePath()))) {
            List<String[]> csvData = csvReader.readAll();
            String[] fieldNames = csvData.get(0);
            for (int i = 1; i < csvData.size(); i++) {
                String[] fieldValues = csvData.get(i);

                int length = fieldNames.length;
                String[] newFieldValues = new String[length];
                System.arraycopy(fieldValues, 0, newFieldValues, 0, fieldValues.length);

                Map<String, Object> dataProp = getCsvInsertDataAsMap(fieldNames, newFieldValues, targetModule, packageChangSets, nonNullableModuleVsFieldVsLookupModules,targetConnection,dataMigrationObj,siteIdMappings,allowNotesAndAttachments,numberLookupDetails);
                insertDataProps.add(dataProp);
            }
        }catch (Exception ex){
            LOGGER.info("####Sandbox --- Exception while creating csv file from insert props modulename :" +targetModule.getName() +"Exception : "+ex);
            throw new Exception(ex);
        }
        return insertDataProps;
    }

    private static Map<String, Object> getCsvInsertDataAsMap(String[] fieldNames, String[] fieldValues, FacilioModule targetModule, Map<ComponentType, List<PackageChangeSetMappingContext>> packageChangSets,Map<String,Map<String,String>> nonNullableModuleVsFieldVsLookupModules,DataMigrationBean targetConnection,DataMigrationStatusContext dataMigrationObj,Map<Long,Long>siteIdMappings,boolean allowNotesAndAttachments,Map<String, Map<String, Object>> numberLookupDetails) throws Exception {

        Map<String,String> fieldNameVsLookupModuleName = new HashMap<>();
        Map<String,Long> lookupModuleNameVsId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        if(nonNullableModuleVsFieldVsLookupModules.containsKey(targetModule.getName())){
            fieldNameVsLookupModuleName = nonNullableModuleVsFieldVsLookupModules.get(targetModule.getName());
            List<String> lookupModuleNames = new ArrayList<>(fieldNameVsLookupModuleName.values());
            List<FacilioModule> lookupModules = moduleBean.getModuleList(lookupModuleNames);
            lookupModuleNameVsId = lookupModules.stream().collect(Collectors.toMap(FacilioModule::getName, FacilioModule::getModuleId));
        }

        if(MapUtils.isEmpty(siteIdMappings)) {
            FacilioModule siteModule = moduleBean.getModule(FacilioConstants.ContextNames.SITE);
            Map<Long, Long> siteMappings = targetConnection.getOldVsNewId(dataMigrationObj.getId(), siteModule.getModuleId(), null);
            siteIdMappings = new HashMap<>(siteMappings);
        }

        List<String> skipFields = new ArrayList<String>(){
            {
                add("photoId");
                add("operatingHour");
                add("vendorLogoId");
            }};

        Map<String, ComponentType> nameVsComponentType = PackageUtil.nameVsComponentType;
        Map<String, Object> dataProp = new HashMap<>();
        for (int i = 0; i < fieldNames.length; i++) {
            if (StringUtils.isNotEmpty(fieldValues[i])) {
                FacilioField field = moduleBean.getField(fieldNames[i], targetModule.getName());
                if (Objects.equals(fieldNames[i], FacilioConstants.ContextNames.FORM_ID)) {
                    Object formId = getComponentIdFromChaneSet(nameVsComponentType, packageChangSets, FacilioConstants.ContextNames.FORM_ID, fieldValues[i]);
                    dataProp.put(fieldNames[i], Long.parseLong(String.valueOf(formId)));
                    continue;
                } else if (Objects.equals(fieldNames[i], FacilioConstants.ContextNames.STATE_FLOW_ID)) {
                    Object stateFlowId = getComponentIdFromChaneSet(nameVsComponentType, packageChangSets, FacilioConstants.ContextNames.STATE_FLOW_ID, fieldValues[i]);
                    dataProp.put(fieldNames[i], Long.parseLong(String.valueOf(stateFlowId)));
                    continue;
                } else if (Objects.equals(fieldNames[i], FacilioConstants.ContextNames.SITE_ID)) {
                    dataProp.put(fieldNames[i], siteIdMappings.getOrDefault(Long.parseLong(fieldValues[i]),-1l));
                    continue;
                } else if (Objects.equals(field.getName(), "approvalRuleId") || Objects.equals(field.getName(), "approvalFlowId")) {
                    Object approvalRuleId = getComponentIdFromChaneSet(nameVsComponentType, packageChangSets, field.getName(), fieldValues[i]);
                    dataProp.put(fieldNames[i], Long.parseLong(String.valueOf(approvalRuleId)));
                    continue;
                }else if (Objects.equals(field.getName(), "parentModuleId")) {
                    Object parentModuleId = getComponentIdFromChaneSet(nameVsComponentType, packageChangSets, FacilioConstants.ContextNames.MODULE, fieldValues[i]);
                    dataProp.put(fieldNames[i], Long.parseLong(String.valueOf(parentModuleId)));
                    continue;
                }

                if (field == null) {
                    continue;
                }

                if (MapUtils.isNotEmpty(fieldNameVsLookupModuleName) && fieldNameVsLookupModuleName.containsKey(field.getName())) {
                    String lookupModuleName = null;
                    Map<Long, Long> idMappings = null;
                    long oldFieldValue = -1;
                    switch (field.getDataTypeEnum()) {
                        case NUMBER:
                            oldFieldValue = Long.parseLong(fieldValues[i]);
                            lookupModuleName = fieldNameVsLookupModuleName.get(field.getName());
                            // TODO - To be remove conditional class after proper component type for vendor and client
                            if(nameVsComponentType.containsKey(lookupModuleName) && nameVsComponentType.get(lookupModuleName) != null){
                                Object newFieldValue = getComponentIdFromChaneSet(nameVsComponentType, packageChangSets, lookupModuleName, fieldValues[i]);
                                dataProp.put(fieldNames[i],Long.parseLong(newFieldValue.toString()));
                            }else{
                                idMappings = targetConnection.getOldVsNewId(dataMigrationObj.getId(), lookupModuleNameVsId.get(lookupModuleName), Arrays.asList(oldFieldValue));
                                dataProp.put(fieldNames[i],idMappings.getOrDefault(oldFieldValue,-1l));
                            }
                            break;
                        case LOOKUP:
                            Map<String, Object> lookupValue = new HashMap<>();
                            oldFieldValue = Long.parseLong(fieldValues[i]);
                            lookupModuleName = ((LookupField) field).getLookupModule().getName();
                            long newLookupFieldValue;
                            if(nameVsComponentType.containsKey(lookupModuleName) && nameVsComponentType.get(lookupModuleName) != null) {
                                Object newFieldValueObj = getComponentIdFromChaneSet(nameVsComponentType, packageChangSets, lookupModuleName, oldFieldValue);
                                newLookupFieldValue = Long.parseLong(newFieldValueObj.toString());
                            }else{
                                idMappings = targetConnection.getOldVsNewId(dataMigrationObj.getId(), lookupModuleNameVsId.get(lookupModuleName), Arrays.asList(oldFieldValue));
                                newLookupFieldValue = idMappings.getOrDefault(oldFieldValue,-1l);
                            }

                            lookupValue.put("id",newLookupFieldValue);
                            dataProp.put(fieldNames[i], lookupValue);
                            break;
                        case MULTI_LOOKUP:
                            List<Map<String, Object>> multiLookupValuesMap = new ArrayList<>();
                            String[] multiLookupValues = fieldValues[i].split(",");
                            List<Long> oldIds = new ArrayList<>();
                            for (String multiLookupValue : multiLookupValues) {
                                oldIds.add(Long.parseLong(multiLookupValue));
                            }
                            lookupModuleName = fieldNameVsLookupModuleName.get(field.getName());
                            long newFieldValue;
                            idMappings = targetConnection.getOldVsNewId(dataMigrationObj.getId(), lookupModuleNameVsId.get(lookupModuleName),oldIds);
                            for(Long oldId : oldIds){
                                Map<String, Object> multiLookupValueMap = new HashMap<>();
                                if(nameVsComponentType.containsKey(lookupModuleName) && nameVsComponentType.get(lookupModuleName) != null) {
                                    Object newFieldValueObj = getComponentIdFromChaneSet(nameVsComponentType, packageChangSets, lookupModuleName, oldId);
                                    newFieldValue = Long.parseLong(newFieldValueObj.toString());
                                }else{
                                    idMappings = targetConnection.getOldVsNewId(dataMigrationObj.getId(), lookupModuleNameVsId.get(lookupModuleName), Arrays.asList(oldFieldValue));
                                    newFieldValue = idMappings.getOrDefault(oldFieldValue,-1l);
                                }
                                multiLookupValueMap.put("id", newFieldValue);
                                multiLookupValuesMap.add(multiLookupValueMap);
                            }
                            dataProp.put(fieldNames[i], multiLookupValuesMap);
                            break;
                    }
                    continue;
                }

                if(numberLookupDetails.containsKey(fieldNames[i])){
                    dataProp.put(fieldNames[i],-1l);
                    continue;
                }

                switch (field.getDataTypeEnum()) {
                    case DATE:
                    case ID:
                    case DATE_TIME:
                    case NUMBER:
                        // TODO handle insert -1 to db
                        if (field.getDisplayType() == FacilioField.FieldDisplayType.DECIMAL || field.getDisplayType() == FacilioField.FieldDisplayType.TEXTBOX) {
                            Double fieldValue = Double.parseDouble(fieldValues[i]);
                            dataProp.put(fieldNames[i], fieldValue);
                        } else  {
                            long fieldValue;
                            if (Objects.equals(fieldValues[i], "-1.0") || Objects.equals(fieldValues[i], "-1") ) {
                                //fieldValues[i] = "-1";
                                fieldValue = -2;
                            }else{
                                fieldValue = Long.parseLong(fieldValues[i]);
                            }
                            dataProp.put(fieldNames[i], fieldValue);
                        }
                        break;
                    case DECIMAL:
                        dataProp.put(fieldNames[i], Double.parseDouble(fieldValues[i]));
                        break;
                    case URL_FIELD:
                        String[] valueArray = fieldValues[i].split(",");
                        String href = valueArray[0];
                        String target = valueArray[1];
                        String name = null;
                        UrlField.UrlTarget targetEnum = UrlField.UrlTarget.valueOf(target);
                        Map<String, Object> prop = new HashMap<>();
                        prop.put("href", href);
                        prop.put("target", targetEnum);
                        if (valueArray.length > 2) {
                            name = valueArray[2];
                            prop.put("name", name);
                        }
                        dataProp.put(fieldNames[i], prop);
                        break;
                    case BOOLEAN:
                        String booleanValue = fieldValues[i];
                        Boolean value = Boolean.valueOf(booleanValue);
                        dataProp.put(fieldNames[i], value);
                        break;
                    case LOOKUP:
                        Map<String, Object> lookupValue = new HashMap<>();
                        dataProp.put(fieldNames[i], lookupValue);
                        break;
                    case MULTI_LOOKUP:
                        List<Map<String, Object>> multiLookupValuesMap = new ArrayList<>();
                        dataProp.put(fieldNames[i], multiLookupValuesMap);
                        break;
                    case MULTI_ENUM:
                        List<Integer> multiEnumValuesList = new ArrayList<>();
                        String[] multiEnumValues = fieldValues[i].split(",");
                        for (String multiEnumValue : multiEnumValues) {
                            multiEnumValuesList.add(Integer.parseInt(multiEnumValue));
                        }
                        dataProp.put(fieldNames[i], multiEnumValuesList);
                        break;
                    case SYSTEM_ENUM:
                    case ENUM:
                        dataProp.put(fieldNames[i], Integer.parseInt(fieldValues[i]));
                        break;
                    default:
                        dataProp.put(fieldNames[i], fieldValues[i]);
                        break;
                }

                if(!allowNotesAndAttachments && skipFields.contains(fieldNames[i])){
                    dataProp.put(fieldNames[i],-1l);
                }

            } else {
                dataProp.put(fieldNames[i], null);
            }
        }
        return dataProp;
    }

    private static void addModuleVsOldIds(Map<Long, List<Long>> moduleIdVsOldIds, long moduleId, long fieldValue) {
        List<Long> oldIds = new ArrayList<>();
        if (moduleIdVsOldIds.containsKey(moduleId)) {
            oldIds = moduleIdVsOldIds.get(moduleId);
            if (!oldIds.contains(fieldValue)) {
                oldIds.add(fieldValue);
            }
        } else {
            oldIds.add(fieldValue);
        }
        moduleIdVsOldIds.put(moduleId, oldIds);
    }

    private static Map<String,Object> getCsvUpdateDataAsMap(String[] fieldNames, String[] fieldValues,FacilioModule targetModule,Map<String, FacilioField> targetFieldNameVsFields,Map<String, Map<String, Object>> numberLookupDetails,Map<Long,List<Long>> moduleIdVsOldIds,Map<String,Map<String,String>> nonNullableModuleVsFieldVsLookupModules) throws Exception{

        Map<String, Object> updateDataProp = new HashMap<>();

        Set<String> targetFieldNames = targetFieldNameVsFields.keySet();

        for (int i = 0; i < fieldNames.length; i++) {
            if (i < fieldValues.length) {
                String fieldName = fieldNames[i];
                String fieldValue = fieldValues[i];
                if (!(targetFieldNames.contains(fieldName) || Objects.equals(fieldName, "id") || numberLookupDetails.containsKey(fieldName))) {
                    continue;
                }
                if(StringUtils.isEmpty(fieldValue)){
                    continue;
                }

                List<String> nonNullableFields = new ArrayList<>();
                Map<String,String> fieldNameVsLookupModules = nonNullableModuleVsFieldVsLookupModules.get(targetModule.getName());
                if(MapUtils.isNotEmpty(fieldNameVsLookupModules)){
                    nonNullableFields = new ArrayList<>(fieldNameVsLookupModules.keySet());
                    if(nonNullableFields.contains(fieldName)){
                        continue;
                    }
                }

                FacilioField field = targetFieldNameVsFields.get(fieldName);

                if(Objects.equals(fieldName,"id")){
                    long id = Long.parseLong(fieldValue);
                    updateDataProp.put(fieldName,fieldValue);
                    addModuleVsOldIds(moduleIdVsOldIds,targetModule.getModuleId(),id);
                } else if (Objects.equals(fieldName, "siteId")) {
                    long siteId = Long.parseLong(fieldValue);
                    updateDataProp.put(fieldName,fieldValue);
                    ModuleBean moduleBean = Constants.getModBean();
                    FacilioModule siteModule = moduleBean.getModule(FacilioConstants.ContextNames.SITE);
                    addModuleVsOldIds(moduleIdVsOldIds,siteModule.getModuleId(),siteId);
                } else if (field instanceof LookupField || field instanceof MultiLookupField){
                    long lookupModuleId = ((BaseLookupField)field).getLookupModule().getModuleId();
                    if(field instanceof LookupField){
                        Map<String, Object> lookupValue = new HashMap<>();
                        lookupValue.put("id", Long.parseLong(fieldValues[i]));
                        updateDataProp.put(fieldName, lookupValue);
                        addModuleVsOldIds(moduleIdVsOldIds,lookupModuleId,Long.parseLong(fieldValue));
                    }else {
                        List<Map<String, Object>> multiLookupValuesMap = new ArrayList<>();
                        String[] multiLookupValues = fieldValue.split(",");
                        for(String multiLookupValue : multiLookupValues){
                            Map<String, Object> multiLookupValueMap = new HashMap<>();
                            multiLookupValueMap.put("id", Long.parseLong(multiLookupValue));
                            multiLookupValuesMap.add(multiLookupValueMap);
                            updateDataProp.put(fieldName,multiLookupValuesMap);
                            addModuleVsOldIds(moduleIdVsOldIds,lookupModuleId,Long.parseLong(multiLookupValue));
                        }
                    }
                }else{
                    Map<String, Object> lookupDetails = (Map<String, Object>) numberLookupDetails.get(fieldName);
                    FacilioModule lookupModule = (FacilioModule) lookupDetails.get("lookupModule");
                    addModuleVsOldIds(moduleIdVsOldIds,lookupModule.getModuleId(),Long.parseLong(fieldValue));
                    updateDataProp.put(fieldName,fieldValue);
                }
            }
        }
        return updateDataProp;
    }


    public static List<Map<String,Object>> getUpdateDataProps(File moduleCsvFile, Map<String, FacilioField> targetFieldNameVsFields, FacilioModule targetModule, Map<String, Map<String, Object>> numberLookupDetails, DataMigrationBean targetConnection, DataMigrationStatusContext dataMigrationObj,Map<String,Map<String,String>> nonNullableModuleVsFieldVsLookupModules,Map<ComponentType, List<PackageChangeSetMappingContext>> packageChangSets) throws Exception{

        List<Map<String,Object>> updatedDataProps = new ArrayList<>();

        if(moduleCsvFile==null){
            return updatedDataProps;
        }

        Map<String, ComponentType> nameVsComponentType = PackageUtil.nameVsComponentType;
        List<Map<String,Object>> updateDataProps = new ArrayList<>();
        Map<Long,List<Long>> moduleIdVsOldIds = new HashMap<>();
        Map<Long,Map<Long,Long>> moduleIdVsOldIdVsNewId = new HashMap<>();

        try(CSVReader csvReader = new CSVReader(new FileReader(moduleCsvFile.getAbsolutePath()))){
            List<String[]> csvData = csvReader.readAll();
            String[] fieldNames = csvData.get(0);
            for(int i = 1; i < csvData.size(); i++){
                String[] fieldValues = csvData.get(i);
                Map<String, Object> dataProp = getCsvUpdateDataAsMap(fieldNames, fieldValues, targetModule,targetFieldNameVsFields,numberLookupDetails,moduleIdVsOldIds,nonNullableModuleVsFieldVsLookupModules);
                updateDataProps.add(dataProp);
            }
        }catch (Exception ex){
            LOGGER.info("####Sandbox --- Exception while creating csv file from update props modulename :" +targetModule.getName() +"Exception : "+ex);
            throw new Exception(ex);
        }

        for(Map.Entry<Long,List<Long>> entry :moduleIdVsOldIds.entrySet()) {
            Long moduleId = entry.getKey();
            List<Long> oldIds = entry.getValue();
            Map<Long, Long> idMappings = targetConnection.getOldVsNewId(dataMigrationObj.getId(), moduleId, oldIds);
            moduleIdVsOldIdVsNewId.put(moduleId,idMappings);
        }

        updatedDataProps = modifyUpdateProps(updateDataProps,moduleIdVsOldIdVsNewId,targetFieldNameVsFields,targetModule,numberLookupDetails,packageChangSets);

        return updatedDataProps;

    }

    private static List<Map<String,Object>> modifyUpdateProps(List<Map<String,Object>> updateDataProps,Map<Long,Map<Long,Long>> moduleIdVsOldIdVsNewId,Map<String, FacilioField> targetFieldNameVsFields,FacilioModule targetModule,Map<String, Map<String, Object>> numberLookupDetails,Map<ComponentType, List<PackageChangeSetMappingContext>> packageChangSets) throws Exception{

        if(CollectionUtils.isEmpty(updateDataProps)){
            return new ArrayList<>();
        }

        List<Map<String,Object>> updatedDataProps = new ArrayList<>();
        Map<String, ComponentType> nameVsComponentType = PackageUtil.nameVsComponentType;

        for(Map<String,Object> updateProp : updateDataProps){
            Map<String,Object> updatedProp = new HashMap<>();
            for(Map.Entry<String,Object> entry : updateProp.entrySet()){
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();

                FacilioField field = targetFieldNameVsFields.get(fieldName);

                if(Objects.equals(fieldName,"id")){
                    long oldId = Long.parseLong(fieldValue.toString());
                    Map<Long,Long> idMapping = moduleIdVsOldIdVsNewId.get(targetModule.getModuleId());
                    long newId = idMapping.getOrDefault(oldId,-1l);
                    updatedProp.put(fieldName,newId);
                } else if (Objects.equals(fieldName,"siteId")) {
                    long siteId = Long.parseLong(fieldValue.toString());
                    ModuleBean moduleBean = Constants.getModBean();
                    FacilioModule siteModule = moduleBean.getModule(FacilioConstants.ContextNames.SITE);
                    Map<Long,Long> idMapping = moduleIdVsOldIdVsNewId.get(siteModule.getModuleId());
                    long newId = idMapping.getOrDefault(siteId,-1l);
                    updatedProp.put(fieldName,newId);
                } else if (field instanceof LookupField || field instanceof MultiLookupField) {
                    long lookupModuleId = ((BaseLookupField)field).getLookupModule().getModuleId();
                    String lookupModuleTypeName = ((BaseLookupField) field).getLookupModule().getName();
                    if(field instanceof LookupField){
                        Map<String, Object> lookupValue = (Map<String, Object>) fieldValue;
                        Map<String, Object> newLookupValue = new HashMap<>();
                        long lookupValueId = (long) lookupValue.get("id");
                        long newLookupId;
                        if(nameVsComponentType.containsKey(lookupModuleTypeName) && nameVsComponentType.get(lookupModuleTypeName) != null){
                            Object newFieldValueObj = getComponentIdFromChaneSet(nameVsComponentType, packageChangSets, lookupModuleTypeName, lookupValueId);
                            newLookupId = Long.parseLong(newFieldValueObj.toString());
                        }else{
                            Map<Long,Long> idMapping = moduleIdVsOldIdVsNewId.get(lookupModuleId);
                            newLookupId = idMapping.getOrDefault(lookupValueId,-1l);
                        }
                        newLookupValue.put("id",newLookupId);
                        updatedProp.put(fieldName,newLookupValue);
                    }else{
                        List<Map<String, Object>> multiLookupValuesList = (List<Map<String, Object>>) fieldValue;
                        List<Map<String, Object>> newMultiLookupValuesList = new ArrayList<>();
                        if(CollectionUtils.isNotEmpty(multiLookupValuesList)){
                            for (Map<String, Object> multiLookupValue : multiLookupValuesList) {
                                Map<String, Object> newMultiLookupValue = new HashMap<>();
                                long multiLookupId = (long) multiLookupValue.get("id");
                                long newLookupId;
                                if(nameVsComponentType.containsKey(lookupModuleTypeName) && nameVsComponentType.get(lookupModuleTypeName) != null){
                                    Object newFieldValueObj = getComponentIdFromChaneSet(nameVsComponentType, packageChangSets, lookupModuleTypeName, multiLookupId);
                                    newLookupId = Long.parseLong(newFieldValueObj.toString());
                                }else{
                                    Map<Long,Long> idMapping = moduleIdVsOldIdVsNewId.get(lookupModuleId);
                                    newLookupId = idMapping.getOrDefault(multiLookupId,-1l);
                                }
                                newMultiLookupValue.put("id", newLookupId);
                                newMultiLookupValuesList.add(newMultiLookupValue);
                            }
                            updatedProp.put(fieldName, newMultiLookupValuesList);
                        }
                    }
                }else{
                    Map<String, Object> lookupDetails = (Map<String, Object>) numberLookupDetails.get(fieldName);
                    FacilioModule lookupModule = (FacilioModule) lookupDetails.get("lookupModule");
                    String lookupModuleTypeName = lookupModule.getName();
                    long oldId = Long.parseLong(fieldValue.toString());
                    long newId;
                    if(nameVsComponentType.containsKey(lookupModuleTypeName) && nameVsComponentType.get(lookupModuleTypeName) != null){
                        Object newFieldValueObj = getComponentIdFromChaneSet(nameVsComponentType, packageChangSets, lookupModuleTypeName, oldId);
                        newId = Long.parseLong(newFieldValueObj.toString());
                    }else{
                        Map<Long,Long> idMapping = moduleIdVsOldIdVsNewId.get(lookupModule.getModuleId());
                        newId = idMapping.getOrDefault(oldId,-1l);
                    }
                    updatedProp.put(fieldName,newId);
                }

            }
            updatedDataProps.add(updatedProp);
        }

        return updatedDataProps;

    }

    public static Object getComponentIdFromChaneSet(Map<String,ComponentType> nameVsComponentType,Map<ComponentType, List<PackageChangeSetMappingContext>> packageChangSets,String typeName,Object uniqueIdentifier){

        Object componentValue= uniqueIdentifier.toString();
        Object newComponentValue = -1;
        ComponentType componentType = nameVsComponentType.get(typeName);
        if(componentType == null){
            return newComponentValue;
        }
        List<PackageChangeSetMappingContext> packageChangeSet= packageChangSets.get(componentType);
        if(CollectionUtils.isEmpty(packageChangeSet)){
            return newComponentValue;
        }
        for(PackageChangeSetMappingContext packageSet : packageChangeSet){
            if(Objects.equals(packageSet.getUniqueIdentifier(), componentValue)){
                newComponentValue = packageSet.getComponentId();
                break;
            }
        }
        return newComponentValue;
    }

    public static final Map<String,Map<String,String>> getNonNullableModuleVsFieldVsLookupModules(){
        return NON_NULLABLE_MODULE_LOOKUP_RECORDS;
    }

    private static final Map<String,Map<String,String>> NON_NULLABLE_MODULE_LOOKUP_RECORDS = Collections.unmodifiableMap(
            new HashMap<String,Map<String,String>>(){{
                put(FacilioConstants.PM_V2.PM_V2_SITES,new HashMap<String,String>(){{
                    put("left",FacilioConstants.PM_V2.PM_V2_MODULE_NAME);
                    put("right",FacilioConstants.ContextNames.SITE);
                }});

                put("workorderTimeLog",new HashMap<String,String>(){{
                    put("parent",FacilioConstants.ContextNames.WORK_ORDER);
                    put(FacilioConstants.ContextNames.SITE,FacilioConstants.ContextNames.SITE);
                    put(FacilioConstants.ContextNames.SITE_ID,FacilioConstants.ContextNames.SITE);
                }});

                put("inventoryrequest",new HashMap<String,String>(){{
                    put("parentId",FacilioConstants.ContextNames.TICKET);
                }});
                put("inventoryrequestlineitems",new HashMap<String,String>(){{
                    put("inventoryRequestId",FacilioConstants.ContextNames.INVENTORY_REQUEST);
                    put("parentId",FacilioConstants.ContextNames.TICKET);
                }});

                put("inspectionTriggerResourceInclExcl",new HashMap<String,String>(){{
                    put("inspectionTemplate","inspectionTemplate");
                }});

                put("inspectionResponse",new HashMap<String,String>(){{
                    put("parent","inspectionTemplate");
                    put("template","qandaTemplate");
                }});

                put("qandaResponse",new HashMap<String,String>(){{
                    put("template","qandaTemplate");
                }});

                put("moves",new HashMap<String,String>(){{
                    put("employee","employee");
                }});

                put("energydata",new HashMap<String,String>(){{
                    put("parentId","energymeter");
                }});

                put("purchaseorderlineitems",new HashMap<String,String>(){{
                    put("purchaseOrder","purchaseorder");
                }});

                put("quotelineitems",new HashMap<String,String>(){{
                    put("quote","quote");
                }});

                put("bookingslot",new HashMap<String,String>(){{
                    put("booking","facilitybooking");
                }});

            }}
    );


    public static FacilioModule getBaseExtendedModule(String moduleName , ModuleBean moduleBean) throws Exception{

        FacilioModule targetModule = moduleBean.getModule(moduleName);

        FacilioModule parentModule = targetModule;
        while (parentModule.getExtendModule() != null) {
            parentModule = parentModule.getExtendModule();
        }

        return parentModule;

    }

}
