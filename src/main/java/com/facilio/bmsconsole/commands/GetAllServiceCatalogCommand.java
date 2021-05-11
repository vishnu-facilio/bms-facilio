package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ServiceCatalogContext;
import com.facilio.bmsconsole.context.ServiceCatalogGroupContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.ServiceCatalogApi;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetAllServiceCatalogCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long groupId = (long) context.get(FacilioConstants.ContextNames.GROUP_ID);
        long appId = (long) context.get(FacilioConstants.ContextNames.APP_ID);
        if (appId < 0) {
			ApplicationContext app = AccountUtil.getCurrentApp();
			appId = app.getId();			
		}
        Boolean fetchComplaintType = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COMPLAINT_TYPE);
        Boolean serviceCatalogGroupOrderBy = (Boolean) context.get(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUP_ORDER_BY);

        List<FacilioField> fields = FieldFactory.getServiceCatalogFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getServiceCatalogModule().getTableName())
                .select(fields);

        if (groupId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("GROUP_ID", "groupId", String.valueOf(groupId), NumberOperators.EQUALS));
        }
        
        if (appId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS));
        }

        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page-1) * perPage);
            if (offset < 0) {
                offset = 0;
            }

            builder.offset(offset);
            builder.limit(perPage);
        }

        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        if (StringUtils.isNotEmpty(searchString)) {
            builder.andCondition(CriteriaAPI.getCondition("NAME", "name", searchString, StringOperators.CONTAINS));
        }
        
        if (fetchComplaintType == null || !fetchComplaintType) {
        		ServiceCatalogGroupContext complaintCategory = ServiceCatalogApi.getComplaintCategory();
        		builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("groupId") ,String.valueOf(complaintCategory.getId()), NumberOperators.NOT_EQUALS));
        }

        if (serviceCatalogGroupOrderBy != null && serviceCatalogGroupOrderBy) {
            builder.orderBy(fieldMap.get("groupId").getCompleteColumnName() + " ASC, ID ASC");
        }
        

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (StringUtils.isNotEmpty(moduleName)) {
            FacilioModule module = modBean.getModule(moduleName);
            if (module != null) {
                builder.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
            }
        }

        List<Map<String, Object>> maps = builder.get();
        List<ServiceCatalogContext> serviceCatalogs = FieldUtil.getAsBeanListFromMapList(maps, ServiceCatalogContext.class);
        if (CollectionUtils.isNotEmpty(serviceCatalogs)) {

            List<Long> ids = new ArrayList<>();
            for (ServiceCatalogContext serviceCatalog : serviceCatalogs) {
                ids.add(serviceCatalog.getFormId());
            }

            Boolean fetchFullForm = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_FULL_FORM);
            if (fetchFullForm == null) {
                fetchFullForm = false;
            }

            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getFormModule()));
            List<FacilioForm> forms = FormsAPI.getDBFormList(null, criteria, null, fetchFullForm);
//            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
//                    .table(ModuleFactory.getFormModule().getTableName())
//                    .select(FieldFactory.getFormFields())
//                    .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getFormModule()));
//            List<FacilioForm> forms = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), FacilioForm.class);
            Map<Long, FacilioForm> formMap = forms.stream().collect(Collectors.toMap(FacilioForm::getId, Function.identity()));

            for (ServiceCatalogContext serviceCatalog : serviceCatalogs) {
                FacilioModule module = modBean.getModule(serviceCatalog.getModuleId());
                serviceCatalog.setModule(module);
                serviceCatalog.setForm(formMap.get(serviceCatalog.getFormId()));
                
                FacilioChain fetchCatalogGroupChain=ReadOnlyChainFactory.getServiceCatalogGroupDetailChain();
                FacilioContext catalogGroupContext=fetchCatalogGroupChain.getContext();
                catalogGroupContext.put(FacilioConstants.ContextNames.ID, serviceCatalog.getGroupId());
                fetchCatalogGroupChain.execute();
                Object catalogGroup=catalogGroupContext.get(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUP);
                if(catalogGroup!=null)
                {
                	 serviceCatalog.setGroup((ServiceCatalogGroupContext)catalogGroup);
                }
               
                
            }
        }

        context.put(FacilioConstants.ContextNames.SERVICE_CATALOGS, serviceCatalogs);

        return false;
    }
}
