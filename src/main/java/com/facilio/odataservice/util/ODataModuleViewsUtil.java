package com.facilio.odataservice.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.odataservice.action.ODataViewAction;
import com.facilio.v3.context.Constants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.olingo.commons.api.data.EntityCollection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.facilio.modules.FieldFactory.getField;
import static com.facilio.odataservice.util.ODATAUtil.getMapAsEntityCollection;
import static com.facilio.odataservice.util.ODATAUtil.getModuleFields;

public class ODataModuleViewsUtil {

    public static long orgId = AccountUtil.getCurrentOrg().getOrgId();
    private static String moduleName;
    public static String getModuleName() {
        return moduleName;
    }
    public static void setModuleName(String moduleName) {
        ODataModuleViewsUtil.moduleName = moduleName;
    }
    private static ModuleBean modBean;
    private static FacilioModule module;
    private static List<FacilioField> fields;
    private static Long appId;

    public static Long getAppId() {
        return appId;
    }

    public static void setAppId(Long appId) {
        ODataModuleViewsUtil.appId = appId;
    }

    private static Map<String, FacilioField> fieldsAsMap;
    private static List<SupplementRecord> lookupFields;
    private static Class<ModuleBaseWithCustomFields> moduleClass;
    private static final Logger LOGGER = LogManager.getLogger(ODataModuleViewsUtil.class.getName());

    public static List<String> getViewNames(String moduleName) throws Exception {
        List<String> viewName = new ArrayList<>();
        fields = FieldFactory.getViewFields();
        fieldsAsMap = FieldFactory.getAsMap(fields);
        long appId = AccountUtil.getCurrentApp().getId();
        LOGGER.info("appid *********"+appId);
        setAppId(appId);
        modBean = Constants.getModBean();
        long moduleId = modBean.getModule(moduleName).getModuleId();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table("Views")
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("appId"), Collections.singleton(appId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("type"),String.valueOf(1),StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("isHidden"),String.valueOf(1),StringOperators.ISN_T));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldsAsMap.get("moduleName"),moduleName,StringOperators.IS));
        criteria.addOrCondition(CriteriaAPI.getCondition(fieldsAsMap.get("moduleId"), Collections.singleton(moduleId),StringOperators.IS));
        builder.andCriteria(criteria);
        List<Map<String,Object>> props = builder.get();
        for(Map<String,Object> prop:props){
                String name = (String) prop.get("name");
                String displayName = (String) prop.get("displayName");
                if(!displayName.contains("-") ) {
                    Pattern pattern = Pattern.compile("[a-zA-Z]");
                    Matcher matcher = pattern.matcher(displayName.substring(0,1));
                    boolean matchFind = matcher.find();
                    if(matchFind){
                        viewName.add(displayName.replace(" ","_"));
//                        viewName.add(name);
                    }
                }
        }
        if(viewName.isEmpty()){
            LOGGER.info("No views present for "+moduleName);
        }
        return viewName;
    }
    public static FacilioView getView(String view,String moduleName,long appId) throws Exception {
        long moduleId = -1;
        if(Constants.getModBean().getModule(moduleName)!= null && Constants.getModBean().getModule(moduleName).getModuleId() != -1){
            moduleId = Constants.getModBean().getModule(moduleName).getModuleId();
        }
        return ViewAPI.getView(view,moduleId , moduleName, orgId,appId);
    }
    public static List<ViewField> getViewFields(String view, String moduleName) throws Exception{
        return getView(view,moduleName,getAppId()).getFields();
    }
    public static List<FacilioField> getFields(List<ViewField> viewFields) throws Exception {
        LOGGER.info("view fields sec");
        Map<String,FacilioField> moduleFields = getModuleFields(moduleName);
        moduleFields.put("siteId",FieldFactory.getSiteIdField(module));
        moduleFields.put("moduleId",FieldFactory.getModuleIdField(module));
        if(FieldFactory.getAgentIdField(module) != null) {
            moduleFields.put("agentId", FieldFactory.getAgentIdField(module));
        }
        if(FieldFactory.getControllerIdField(module) != null) {
            moduleFields.put("controllerId", FieldFactory.getControllerIdField(module));
        }

        LOGGER.info("view fields sec module fields");
        List<FacilioField>  ViewFields= new ArrayList<>();
        LOGGER.info(viewFields+"view fields");
        for(int i=0;i<viewFields.size();i++) {
            LOGGER.info(viewFields.size()+"size");
            if(viewFields.get(i).getFieldName() != null && viewFields.get(i).getFieldName().equalsIgnoreCase("siteid")){
                LOGGER.info(viewFields.get(i).getFieldName() + "name" + i);
                ViewFields.add(moduleFields.get(viewFields.get(i).getFieldName()));
            }
            else if(viewFields.get(i).getField().getName() != null) {
                LOGGER.info(viewFields.get(i).getField().getName() + "name" +i);
                ViewFields.add(moduleFields.get(viewFields.get(i).getField().getName()));
            }
        }
        LOGGER.info("view fields :" + viewFields);
        return ViewFields;
    }
    public static void viewAction(String moduleName,String viewName) throws Exception {
        ODataViewAction.v2getViewDetail(moduleName,viewName);
        ODataViewAction.getResult();
        LOGGER.error("res -> "+ ODataViewAction.getResult());
    }
    public static Map<String,FacilioField> getFields(String view,String moduleName) throws Exception {
        view = getViewName(view);
        if(view!=null) {
            List<ViewField> viewFields = getViewFields(view, moduleName);
            Map<String, FacilioField> moduleFields = getModuleFields(moduleName);
            Map<String, FacilioField> moduleFields1 = new HashMap<>();
            List<FacilioField> SelectedFields = new ArrayList<>();
            if (viewFields != null) {
                for (int i = 0; i < viewFields.size(); i++) {
                    if (viewFields.get(i).getFieldName() != null && viewFields.get(i).getFieldName().equalsIgnoreCase("siteid")) {
                        moduleFields1.put(viewFields.get(i).getFieldName(), getField(viewFields.get(i).getFieldName(), viewFields.get(i).getColumnDisplayName(), FieldType.STRING));
                    } else {
                        SelectedFields.add(moduleFields.get(viewFields.get(i).getField().getName()));
                        moduleFields1.put(viewFields.get(i).getField().getName(), moduleFields.get(viewFields.get(i).getField().getName()));
                    }
                }
            }
            LOGGER.info("1view fields :" + viewFields);
            LOGGER.info("view module fields :" + moduleFields1);
            return moduleFields1;
        }else {
            return null;
        }

    }

    public static String getViewName(String displayName) throws Exception{
        fields = FieldFactory.getViewFields();
        fieldsAsMap = FieldFactory.getAsMap(fields);
        ApplicationContext application = AccountUtil.getCurrentApp();
        long appId = -1;
        if(application != null){
            appId = AccountUtil.getCurrentApp().getId();
        }else{
            appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }
        if(appId != -1) {
            setAppId(appId);
            modBean = Constants.getModBean();
            long moduleId = modBean.getModule(moduleName).getModuleId();
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table("Views")
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("appId"), Collections.singleton(appId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("type"), String.valueOf(1), StringOperators.IS))
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("isHidden"), String.valueOf(1), StringOperators.ISN_T))
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("displayName"), displayName.replace("_", " "), StringOperators.IS));
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldsAsMap.get("moduleName"), moduleName, StringOperators.IS));
            criteria.addOrCondition(CriteriaAPI.getCondition(fieldsAsMap.get("moduleId"), Collections.singleton(moduleId), StringOperators.IS));
            builder.andCriteria(criteria);
            List<Map<String, Object>> props = builder.get();
            if(props!=null){
                Map<String, Object> propsObj = props.get(0);
                if(propsObj!=null){
                    return propsObj.get("name").toString();
                }
            }
        }
        else {
            LOGGER.info("appid for current app is null");
        }
        return null;
    }

    public static EntityCollection getModuleViewRecords() throws Exception{
        modBean = Constants.getModBean();
        LOGGER.info("Fields : "+fields);
        module = modBean.getModule(moduleName);
        LOGGER.info("module :"+module);
        moduleClass = FacilioConstants.ContextNames.getClassFromModule(module);
        LOGGER.info("Module class : "+moduleClass.getName());
        LOGGER.info("lookup start");
        lookupFields = ODATAUtil.getSupplementRecords();
        LOGGER.info("lookupend");
        LOGGER.info("fields and fieldmap1");
        fields = modBean.getAllFields(moduleName);
        fieldsAsMap = FieldFactory.getAsMap(fields);
        LOGGER.info("fields and fieldmap");
        fieldsAsMap.put("id", FieldFactory.getIdField(module));
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<>()
                .module(module)
                .beanClass(moduleClass)
                .fetchSupplements(lookupFields);
        // fields
        LOGGER.info("fields section");
        LOGGER.info("result"+ODataViewAction.getResult());
        LOGGER.info(((FacilioView)ODataViewAction.getResult().get("viewDetail")).getFields()+"fields fetched successfully");
        fields = getFields(((FacilioView)ODataViewAction.getResult().get("viewDetail")).getFields());
        selectBuilder.select(fields);
        LOGGER.info("sort fields " + ((FacilioView)ODataViewAction.getResult().get("viewDetail")).getSortFields());
        LOGGER.info("criteria " + ((FacilioView)ODataViewAction.getResult().get("viewDetail")).getCriteria());
        //sorting
        if(((FacilioView)ODataViewAction.getResult().get("viewDetail")).getSortFields()!=null) {
            LOGGER.info("field name"+((FacilioView) ODataViewAction.getResult().get("viewDetail")).getSortFields().get(0).getSortField().getName());
            String OrderBy = ((FacilioView) ODataViewAction.getResult().get("viewDetail")).getSortFields().get(0).getSortField().getName();
            LOGGER.info("is ascending - "+((FacilioView) ODataViewAction.getResult().get("viewDetail")).getSortFields().get(0).getIsAscending());
            Boolean isAsc = ((FacilioView) ODataViewAction.getResult().get("viewDetail")).getSortFields().get(0).getIsAscending();
            String isAscOrDesc = !isAsc ? " DESC" : " ASC";
            OrderBy = fieldsAsMap.get(OrderBy).getColumnName();
            OrderBy = OrderBy + isAscOrDesc;
            LOGGER.info("Orderby" + OrderBy);
            selectBuilder.orderBy(OrderBy);
        }
        //conditions
        if(((FacilioView)ODataViewAction.getResult().get("viewDetail")).getCriteria()!=null){
            LOGGER.info("criteria block");
            Map<String, Condition> conditionMap =  ((FacilioView)ODataViewAction.getResult().get("viewDetail")).getCriteria().getConditions();
            for(Map.Entry<String,Condition> entry: conditionMap.entrySet()){
                Condition condition = entry.getValue();
                selectBuilder.andCondition(condition);
            }
        }
        LOGGER.info("limit block");
        selectBuilder.limit(5000);
        LOGGER.info("props fetch");
        for(FacilioField field : fields) {
            LOGGER.info("f "+field);
        }
        List<ModuleBaseWithCustomFields> props = selectBuilder.get();
        LOGGER.info("props fetch success");
        List<Map<String, Object>> propsMap = FieldUtil.getAsMapList(props, ModuleBaseWithCustomFields.class);
        LOGGER.info("selected all records as propsmap : "+propsMap);
        return getMapAsEntityCollection(propsMap);

    }

    public static Boolean isModuleEnabled(long moduleId) throws Exception {
        List<FacilioField> fields = FieldFactory.getODataModuleFields();
        Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getOdataModule().getTableName())
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("isEnabled"), String.valueOf(true), BooleanOperators.IS));
        if(selectRecordBuilder.get().isEmpty()){
            return false;
        }
        return true;
    }
}
