package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ServiceCatalogContext;
import com.facilio.bmsconsole.context.ServiceCatalogGroupContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

public class ServiceCatalogApi {

    public static final String SERVICE_COMPLAINT_CATEGORY = "complaint";

    public static ServiceCatalogGroupContext getComplaintCategory() throws Exception {
        return getCategoryDetails(-1);
    }

    public static ServiceCatalogGroupContext getCategoryDetails(long id) throws Exception {

        List<FacilioField> fields = FieldFactory.getServiceCatalogGroupFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectRecord = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getServiceCatalogGroupModule().getTableName())
                .select(fields);

        if (id == -1) {
            selectRecord.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), SERVICE_COMPLAINT_CATEGORY, StringOperators.IS));
        } else {
            selectRecord.andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getServiceCatalogGroupModule()));
        }

        Map<String, Object> map = selectRecord.fetchFirst();
        return FieldUtil.getAsBeanFromMap(map, ServiceCatalogGroupContext.class);
    }

    public static List<Map<String, Object>> getAllCategory(List<Long> ids) throws Exception {
        List<FacilioField> fields = FieldFactory.getServiceCatalogGroupFields();

        GenericSelectRecordBuilder selectRecord = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getServiceCatalogGroupModule().getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getServiceCatalogGroupModule()));
        ;

        return selectRecord.get();
    }

    public static List<Map<String, Object>> getServiceCatalogList(List<Long> ids) throws Exception {
        List<FacilioField> fields = FieldFactory.getServiceCatalogFields();

        GenericSelectRecordBuilder selectRecord = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getServiceCatalogModule().getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getServiceCatalogModule()));
        ;

        List<Map<String, Object>> catalogList = selectRecord.get();

        for (Map<String, Object> prop : catalogList) {
            ServiceCatalogContext serviceCatalog = FieldUtil.getAsBeanFromMap(prop, ServiceCatalogContext.class);
            long formId = serviceCatalog.getFormId();


            Context formMetaContext = new FacilioContext();

            formMetaContext.put(FacilioConstants.ContextNames.FORM_ID, formId);
            formMetaContext.put(FacilioConstants.ContextNames.FETCH_FORM_RULE_FIELDS, true);

            FacilioChain c = FacilioChainFactory.getFormMetaChain();
            c.execute(formMetaContext);

            FacilioForm form = (FacilioForm) formMetaContext.get(FacilioConstants.ContextNames.FORM);
            prop.put("form", form);
        }

        return catalogList;

    }

}
