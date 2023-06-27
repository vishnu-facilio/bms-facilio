package com.facilio.odataservice.action;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
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
    private  String moduleName;
    private JSONObject data;
    int page;
    List<Long> selectedModuleIds;
    int perPage;
    int count;
    String  search;
    String tableName = ModuleFactory.getOdataModule().getTableName();
    List<FacilioField> fields = FieldFactory.getODataModuleFields();
    Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

    private static final Logger LOGGER = LogManager.getLogger(ODataModuleAction.class.getName());
    public String execute() throws Exception {
            HttpServletRequest req = ServletActionContext.getRequest();
            HttpServletResponse resp = ServletActionContext.getResponse();
            if( true || AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.ODATA_API)) {
                if (moduleName.equalsIgnoreCase("index")) {
                    setModuleName((Arrays.asList(req.getServletPath().split("/api/odata/module/"))).get(1));
                }
                FacilioModule actionModule = Constants.getModBean().getModule(moduleName);
                long moduleId = -1;
                if(actionModule != null){
                    moduleId = actionModule.getModuleId();
                }

//                if (moduleId != -1 && ODataModuleViewsUtil.isModuleEnabled(moduleId)) {
                if(true){
                    OData odata = OData.newInstance();
                    ModuleViewsEdmProvider provider = new ModuleViewsEdmProvider(getModuleName());
                    ServiceMetadata edm = odata.createServiceMetadata(provider, new ArrayList<EdmxReference>());
                    ODataHttpHandler handler = odata.createHandler(edm);
                    handler.register(new ModuleViewEntityCollectionProcessor(getModuleName()));
                    handler.register(new ModuleServiceDocumentProcessor());
                    handler.process(req, resp);
                } else {
                    if(moduleId == -1){
                        LOGGER.info("Invalid Modulename : "+moduleName);
                    }else {
                        LOGGER.info(moduleName + "is not enabled in setup");
                    }
                }
            }else{
                LOGGER.info("ODataApi License is not enabled");
            }
        return NONE;
    }


    public String fetchODataModules() throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(tableName)
                .limit(perPage)
                .offset(perPage * (page-1));
        if(search!= null && !search.isEmpty()){
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("name"),search,StringOperators.CONTAINS));
        }
        List<Map<String,Object>> modules = selectRecordBuilder.get();
        List<Object> odataModuleList = new ArrayList<>();
        JSONObject modulesObj = new JSONObject();
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
        modulesObj.put("module",odataModuleList);
        setData(modulesObj);
        return SUCCESS;
    }
    public String modulesCount() throws Exception{
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singletonList(fieldsAsMap.get("moduleId")))
                .table(tableName);
        if(search!= null && !search.isEmpty()){
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("name"),search,StringOperators.CONTAINS));
        }
        List<Long> moduleIds = new ArrayList<>();
        List<Map<String, Object>> props = selectRecordBuilder.get();
        if(search!=null && !search.isEmpty()){
            List<Map<String, Object>> props1 = new GenericSelectRecordBuilder()
                    .select(Collections.singletonList(fieldsAsMap.get("moduleId")))
                    .table(tableName)
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
        setCount(selectRecordBuilder.get().size());
        return SUCCESS;
    }
    public String addODataModules() throws Exception {
        if(getData().containsKey("selectedModule")) {
            List<Map<String, Object>> moduleMap = ((List) (getData()).get("selectedModule"));
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(tableName)
                    .ignoreSplNullHandling()
                    .fields(fields);
            insertBuilder.addRecords(moduleMap);
            insertBuilder.save();
        }
        return SUCCESS;
    }
    public String updateODataModules() throws Exception {
        if(getData().containsKey("rowdata")) {
            Map<String, Object> moduleMap = ((Map) ((JSONObject) getData()).get("rowdata"));
            Map<String, Object> updateField = new HashMap<>();
            updateField.put("isEnabled", moduleMap.get("status"));
            String moduleName = moduleMap.get("url").toString();
            long moduleId = Constants.getModBean().getModule(moduleName).getModuleId();
            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .table(tableName)
                    .fields(fields)
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("moduleId"), Collections.singleton(moduleId), StringOperators.IS));
            updateBuilder.update(updateField);
        }
        return SUCCESS;
    }
    public String deleteODataModules() throws Exception {
        String moduleName = (String) getData().get("moduleName");
        FacilioModule module = Constants.getModBean().getModule(moduleName);
        long moduleId = -1;
        if(module != null){
            moduleId = module.getModuleId();
        }
        if(moduleId!=-1) {
            GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
            deleteRecordBuilder.table(tableName)
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("moduleId"), moduleId + "", NumberOperators.EQUALS))
                    .delete();
        }else{
            LOGGER.error("Invalid modulename");
        }
        return SUCCESS;
    }
}



