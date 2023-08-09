package com.facilio.odataservice.action;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.odataservice.service.*;
import com.facilio.odataservice.util.ODataModuleViewsUtil;
import com.facilio.v3.context.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Getter
@Setter
public class ODataModuleAction extends FacilioAction {
    List<Long> selectedModuleIds;
    private String moduleName;
    private Map<String,Object> data;

    private static final Logger LOGGER = LogManager.getLogger(ODataModuleAction.class.getName());
    public String execute() throws Exception {
            HttpServletRequest req = ServletActionContext.getRequest();
            HttpServletResponse resp = ServletActionContext.getResponse();
            LOGGER.info(req.getRequestURL());
            if(req.getQueryString()!=null) {
                LOGGER.info("QueryString : " + req.getQueryString());
            }
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.ODATA_API)) {
                if (getModuleName().equalsIgnoreCase("index")) {
                    setModuleName((Arrays.asList(req.getServletPath().split("/api/odata/module/"))).get(1));
                }
                FacilioModule actionModule = Constants.getModBean().getModule(getModuleName());
                long moduleId = -1;
                if(actionModule != null){
                    moduleId = actionModule.getModuleId();
                }

                if (moduleId != -1 && ODataModuleViewsUtil.isModuleEnabled(moduleId)) {
//                if(true){
                    OData odata = OData.newInstance();
                    ModuleViewsEdmProvider provider = new ModuleViewsEdmProvider(getModuleName());
                    ServiceMetadata edm = odata.createServiceMetadata(provider, new ArrayList<EdmxReference>());
                    ODataHttpHandler handler = odata.createHandler(edm);
                    handler.register(new ModuleViewEntityCollectionProcessor(getModuleName()));
                    handler.register(new ModuleServiceDocumentProcessor());
                    handler.process(req, resp);
                } else {
                    if(moduleId == -1){
                        LOGGER.info("Invalid Modulename : "+getModuleName());
                    }else {
                        LOGGER.info(getModuleName() + "is not enabled in setup");
                    }
                }
            }else{
                LOGGER.info("ODataApi License is not enabled");
            }
        return NONE;
    }


    public String fetchODataModules() throws Exception {
        Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(FieldFactory.getODataModuleFields());
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getODataModuleFields())
                .table(ModuleFactory.getOdataModule().getTableName())
                .limit(getPerPage())
                .offset(getPerPage() * (getPage()-1));
        if(getSearch()!= null && !getSearch().isEmpty()){
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("name"),getSearch(),StringOperators.CONTAINS));
        }
        List<Map<String,Object>> modules = selectRecordBuilder.get();
        List<Object> odataModuleList = new ArrayList<>();
        for(Map<String,Object> module:modules){
            JSONObject fieldMap = new JSONObject();
            long moduleId = (Long)module.get("moduleId");
            FacilioModule module1 =  Constants.getModBean().getModule(moduleId);
            fieldMap.put("module",module1.getDisplayName());
            fieldMap.put("description",module1.getDescription());
            fieldMap.put("url",Constants.getModBean().getModule(moduleId).getName());
            fieldMap.put("status",module.get("isEnabled"));
            odataModuleList.add(fieldMap);
        }
        setResult("module",odataModuleList);
        return SUCCESS;
    }
    public String modulesCount() throws Exception{
        Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(FieldFactory.getODataModuleFields());
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singletonList(fieldsAsMap.get("moduleId")))
                .table(ModuleFactory.getOdataModule().getTableName());
        if(getSearch()!= null && !getSearch().isEmpty()){
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("name"),getSearch(),StringOperators.CONTAINS));
        }
        List<Long> moduleIds = new ArrayList<>();
        List<Map<String, Object>> props = selectRecordBuilder.get();
        if(getSearch()!=null && !getSearch().isEmpty()){
            List<Map<String, Object>> props1 = new GenericSelectRecordBuilder()
                    .select(Collections.singletonList(fieldsAsMap.get("moduleId")))
                    .table(ModuleFactory.getOdataModule().getTableName())
                    .get();
            props1.forEach(module -> {
                moduleIds.add((Long) module.get("moduleId"));
            });
        }else {
            props.forEach(module -> {
                moduleIds.add((Long) module.get("moduleId"));
            });
        }
        setSelectedModuleIds(moduleIds);
        setResult("count",selectRecordBuilder.get().size());
        setResult("selectedModuleIds",moduleIds);

        return SUCCESS;
    }
    public String addODataModules() throws Exception {
        if(getData().containsKey("selectedModule")) {
            List<Map<String, Object>> moduleMap = ((List) (getData()).get("selectedModule"));
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getOdataModule().getTableName())
                    .ignoreSplNullHandling()
                    .fields(FieldFactory.getODataModuleFields());
            insertBuilder.addRecords(moduleMap);
            insertBuilder.save();
        }
        return SUCCESS;
    }
    public String updateODataModules() throws Exception {
        Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(FieldFactory.getODataModuleFields());
        if(getData().containsKey("rowdata")) {
            Map<String, Object> moduleMap = ((Map) ((JSONObject) getData()).get("rowdata"));
            Map<String, Object> updateField = new HashMap<>();
            updateField.put("isEnabled", moduleMap.get("status"));
            String moduleName = moduleMap.get("url").toString();
            long moduleId = Constants.getModBean().getModule(moduleName).getModuleId();
            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getOdataModule().getTableName())
                    .fields(FieldFactory.getODataModuleFields())
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("moduleId"), Collections.singleton(moduleId), StringOperators.IS));
            updateBuilder.update(updateField);
        }
        return SUCCESS;
    }
    public String deleteODataModules() throws Exception {
        String moduleName = (String) getData().get("moduleName");
        FacilioModule module = Constants.getModBean().getModule(moduleName);
        Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(FieldFactory.getODataModuleFields());
        long moduleId = -1;
        if(module != null){
            moduleId = module.getModuleId();
        }
        if(moduleId!=-1) {
            GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
            deleteRecordBuilder.table(ModuleFactory.getOdataModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("moduleId"), moduleId + "", NumberOperators.EQUALS))
                    .delete();
        }else{
            LOGGER.error("Invalid modulename");
        }
        return SUCCESS;
    }
}



