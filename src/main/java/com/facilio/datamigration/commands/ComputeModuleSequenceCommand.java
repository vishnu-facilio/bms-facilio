package com.facilio.datamigration.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ComputeModuleSequenceCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(ComputeModuleSequenceCommand.class);
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long sourceOrgId = (long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        long targetOrgId = (long) context.get(DataMigrationConstants.TARGET_ORG_ID);
        boolean isSiteScoped = (boolean) context.getOrDefault(DataMigrationConstants.IS_SITE_SCOPED, false);
        LOGGER.info("Module sequence computation started");
        List<String> skipModules = new ArrayList<>();//(List<FacilioModule>) context.get(DataMigrationConstants.MODULES_TO_SKIP);
        if(isSiteScoped) {
            skipModules.add("site");
        }

        //RUn time issues
        //skipModules.add("bacnetipcontroller"); // unique constraint fails


        //Modules as a configuration
        skipModules.add("alarmseverity");
        skipModules.add("assetcategory");
        skipModules.add("assetdepartment");
        skipModules.add("assettype");
        skipModules.add("energymeterpurpose");
        skipModules.add("itemStatus");
        skipModules.add("readingalarmcategory");
        skipModules.add("servicerequestpriority");
        skipModules.add("spacecategory");
        skipModules.add("ticketcategory");
        skipModules.add("ticketpriority");
        skipModules.add("ticketstatus");
        skipModules.add("tickettype");
        skipModules.add("toolStatus");
        skipModules.add("workpermittype");
        skipModules.add("weatherservice");
        skipModules.add("weatherstation");
        skipModules.add("inspectionPriority");
        skipModules.add("peopleGroup");
        skipModules.add("peopleGroupMember");


        //Modules with no relation in SubModuleRel
        skipModules.add("cmdattachments");
        skipModules.add("cmdnotes");
        skipModules.add("dashboardnotes");
        skipModules.add("reportnotes");

        //Modules with invalid tablename or column names
//        skipModules.add("assetdepreciationRel");
        skipModules.add("leedconfiguration");   // table not present
        skipModules.add("mlBmsPointsTagging");  // ML_BmsPredictedPoints.MODULEID column not present
        skipModules.add("occupantattachments"); // Occupants_Attachments.PARENT_CONTACT column not present
        skipModules.add("readingalarm");        // Reading_Alarms.IS_NEW_READING_RULE column not present
        skipModules.add("assetdepreciation");  // Skipping this since one of the number/decimal field doesnt have entry in sub table-numberfields

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", sourceOrgId);
        ModuleBean targetModuleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", targetOrgId);
        Criteria typeCriteria = new Criteria();
        List<Integer> moduleTypesToSkip = new ArrayList<Integer>(){{
            add(FacilioModule.ModuleType.CLASSIFICATION_DATA.getValue());
            add(FacilioModule.ModuleType.RELATION_DATA.getValue());
            add(FacilioModule.ModuleType.LOOKUP_REL_MODULE.getValue());
            add(FacilioModule.ModuleType.ENUM_REL_MODULE.getValue());
            add(FacilioModule.ModuleType.SYSTEM_LOOKUP.getValue());
            add(FacilioModule.ModuleType.LARGE_TEXT_DATA_MODULE.getValue());

        }};
        typeCriteria.addAndCondition(CriteriaAPI.getCondition("MODULE_TYPE", "type", StringUtils.join(moduleTypesToSkip, ","), NumberOperators.NOT_EQUALS));
        if(CollectionUtils.isNotEmpty(skipModules)) {
            typeCriteria.addAndCondition(CriteriaAPI.getCondition("NAME", "name", StringUtils.join(skipModules, ","), StringOperators.ISN_T));
        }

        List<FacilioModule> modules = moduleBean.getModuleList(typeCriteria);
        List<FacilioModule> targetModules = targetModuleBean.getModuleList(typeCriteria);
        Map<String, FacilioModule> targetModuleNameVsObj = targetModules.stream().collect(Collectors.toMap(FacilioModule::getName, Function.identity()));

        List<FacilioModule> targetModulesWithoutFilter = targetModuleBean.getModuleList((Criteria)null);
        Map<String, FacilioModule> targetModuleNameWithoutFilterVsObj = targetModulesWithoutFilter.stream().collect(Collectors.toMap(FacilioModule::getName, Function.identity()));

        Set<Long> extendedIds = modules.stream().filter(module -> module.getExtendModule() != null).map(module ->  module.getExtendModule().getModuleId()).collect(Collectors.toSet());

        Map<String, Map<String, Object>> moduleNameVsDetails = new HashMap<>();
        for(FacilioModule module : modules ) {
            String moduleName = module.getName();

            if(!targetModuleNameVsObj.containsKey(moduleName)) {
                continue;
            }
            FacilioModule targetModule = targetModuleNameVsObj.get(moduleName);
            Map<String, Object> details = (moduleNameVsDetails.containsKey(moduleName))? moduleNameVsDetails.get(moduleName): new HashMap<>();
            details.put("sourceModule", module);
            Map<String,Map<String, Object>> numberLookupUps = new HashMap<>();
            List<String> numberFileFields = new ArrayList<>();
            FacilioModule currModule = targetModuleBean.getModule(moduleName);
            while (currModule != null) {
                numberLookupUps.putAll(getNumberLookups(currModule, targetModuleBean, targetModuleNameWithoutFilterVsObj));
                numberFileFields.addAll(getNumberFileFields(currModule));
                currModule = currModule.getExtendModule();
            }
            details.put("numberLookups", numberLookupUps);
            details.put("fileFields", numberFileFields);
            Criteria moduleSpecificCriteria = getModuleSpecificCriteria(targetModule);
            if(moduleSpecificCriteria != null) {
                details.put("criteria", moduleSpecificCriteria);
            }
            moduleNameVsDetails.put(moduleName, details);

            if(module.getExtendModule() != null) {
                String parentModuleName = module.getExtendModule().getName();
                Map<String, Object> parentModuleDetails = moduleNameVsDetails.containsKey(parentModuleName) ? moduleNameVsDetails.get(parentModuleName) : new HashMap();

                List<String> childModuleNames = (parentModuleDetails.containsKey("childModules")) ? (List)parentModuleDetails.get("childModules") : new ArrayList<String>();
                List<Long> childModuleIds = (parentModuleDetails.containsKey("childModuleIds")) ? (List)parentModuleDetails.get("childModuleIds") : new ArrayList<Long>();
                childModuleNames.add(moduleName);
                parentModuleDetails.put("childModules", childModuleNames);
                childModuleIds.add(targetModule.getModuleId());
                parentModuleDetails.put("childModuleIds", childModuleIds);
                moduleNameVsDetails.put(parentModuleName, parentModuleDetails);
            }
        }


        Map<String, Map<String, Object>> orderedModuleNameVsDetails = new LinkedHashMap<>();
        Map<String, Map<String, Object>> moduleWithParentWithoutChild = new LinkedHashMap<>();
        Map<String, Map<String, Object>> moduleWithBothParentChild = new LinkedHashMap<>();
        Map<String, Map<String, Object>> moduleWithOnlyChild = new LinkedHashMap<>();
        Map<String, Map<String, Object>> baseEntityModules = new LinkedHashMap<>();
        Map<String, Map<String, Object>> otherModules = new LinkedHashMap<>();

        for(Map.Entry<String, Map<String,Object>> moduleVsDetails : moduleNameVsDetails.entrySet()) {
            String key = moduleVsDetails.getKey();
            Map<String,Object> value = moduleVsDetails.getValue();
            FacilioModule module = (FacilioModule)value.get("sourceModule");
            if(module.getExtendModule() != null && !extendedIds.contains(module.getModuleId())) {
                moduleWithParentWithoutChild.put(key, value);
            } else if(module.getExtendModule() != null && extendedIds.contains(module.getModuleId())) {
                moduleWithBothParentChild.put(key, value);
            } else if(module.getExtendModule() == null && extendedIds.contains(module.getModuleId())) {
                moduleWithOnlyChild.put(key, value);
            } else if(module.getTypeEnum() == FacilioModule.ModuleType.BASE_ENTITY ||
                    module.getTypeEnum() == FacilioModule.ModuleType.Q_AND_A_RESPONSE ||
                    module.getTypeEnum() == FacilioModule.ModuleType.Q_AND_A) {
                baseEntityModules.put(key, value);
            } else {
                otherModules.put(key, value);
            }
        }
        orderedModuleNameVsDetails.putAll(moduleWithParentWithoutChild);
        orderedModuleNameVsDetails.putAll(moduleWithBothParentChild);
        orderedModuleNameVsDetails.putAll(moduleWithOnlyChild);
        orderedModuleNameVsDetails.putAll(baseEntityModules);
        orderedModuleNameVsDetails.putAll(otherModules);

        context.put("ModulesVsInfo", orderedModuleNameVsDetails);
        LOGGER.info("Module sequence computation completed");
        return false;
    }

    private static Map<String,Map<String, Object>> getNumberLookups(FacilioModule module, ModuleBean targetModuleBean, Map<String, FacilioModule> targetModuleNameVsObj) throws Exception {
        Map<String,Map<String, Object>> numberFieldsVsLookupModules = new HashMap<>();

        switch(module.getName()) {
            case "site" :
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

            default:
                break;
        }

        if(module.getTypeEnum().equals(FacilioModule.ModuleType.PHOTOS) ||
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

    private static void addNumberLookupDetails(String fieldName, String lookupModuleName, Map<String, FacilioModule> targetModuleNameVsObj, Map<String,Map<String, Object>> numberFieldsVsLookupModules) {
        HashMap lookupDetails = new HashMap<>();
        boolean isSpecialModule = LookupSpecialTypeUtil.isSpecialType(lookupModuleName);
        if(!isSpecialModule && !targetModuleNameVsObj.containsKey(lookupModuleName)) {
            return;
        }
        lookupDetails.put("lookupModuleName", lookupModuleName);
        if(!isSpecialModule) {
            lookupDetails.put("lookupModuleId", targetModuleNameVsObj.get(lookupModuleName).getModuleId());
        }
        numberFieldsVsLookupModules.put(fieldName, lookupDetails);
    }

    private static List<String> getNumberFileFields(FacilioModule module) {
        List<String> numberFieldsVsFileFields = new ArrayList<>();

        switch(module.getTypeEnum()) {
            case PHOTOS:
                numberFieldsVsFileFields.add("photoId");
                break;
            case ATTACHMENTS:
                numberFieldsVsFileFields.add("fileId");
                break;
            default:
                break;
        }

        switch(module.getName()) {
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

    private static Criteria getModuleSpecificCriteria(FacilioModule module) {
        Criteria cr = null;
        switch(module.getName()) {
            case "people" :
                cr = new Criteria();
                cr.addAndCondition(CriteriaAPI.getCondition("PEOPLE_TYPE","peopleType", String.valueOf(V3PeopleContext.PeopleType.EMPLOYEE.getIndex()), NumberOperators.NOT_EQUALS));
                break;
            default:
                break;
        }
        return cr;
    }

}
