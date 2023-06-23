package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class V3ContactsAPI {

    public static V3ContactsContext getContactforPhone(String phone, long id, boolean isVendor) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.CONTACT);

        SelectRecordsBuilder<V3ContactsContext> builder = new SelectRecordsBuilder<V3ContactsContext>()
                .module(module)
                .beanClass(V3ContactsContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("PHONE", "phone", phone, StringOperators.IS))

                ;
        if(isVendor) {
            builder.andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(id), NumberOperators.EQUALS));
        }
        else {
            builder.andCondition(CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(id), NumberOperators.EQUALS));
        }
        V3ContactsContext records = builder.fetchFirst();
        return records;

    }

    public static int unMarkPrimaryContact(long contactId, long id, V3ContactsContext.ContactType contactType) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.CONTACT);
        Map<String, FacilioField> contactFieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> updatedfields = new ArrayList<FacilioField>();
        FacilioField primaryContactField = contactFieldMap.get("isPrimaryContact");
        updatedfields.add(primaryContactField);
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(updatedfields)
                .andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS))

                ;

        if(contactId > 0) {
            updateBuilder.andCondition(CriteriaAPI.getCondition("ID", "contactId", String.valueOf(contactId), NumberOperators.NOT_EQUALS));

        }
        if(contactType == V3ContactsContext.ContactType.VENDOR) {
            updateBuilder.andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(id), NumberOperators.EQUALS));
        }
        else if(contactType == V3ContactsContext.ContactType.TENANT){
            updateBuilder.andCondition(CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(id), NumberOperators.EQUALS));
        } else if(contactType == V3ContactsContext.ContactType.CLIENT){
            updateBuilder.andCondition(CriteriaAPI.getCondition("CLIENT_ID", "client", String.valueOf(id), NumberOperators.EQUALS));
        }

        Map<String, Object> value = new HashMap<>();
        value.put("isPrimaryContact", false);
        int count = updateBuilder.update(value);
        return count;

    }
    public static int unMarkPrimaryContact(List<Long> contactIds, List<Long> ids, V3ContactsContext.ContactType contactType) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.CONTACT);
        Map<String, FacilioField> contactFieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> updatedfields = new ArrayList<FacilioField>();
        FacilioField primaryContactField = contactFieldMap.get("isPrimaryContact");
        updatedfields.add(primaryContactField);
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(updatedfields)
                .andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS))

                ;

        if(CollectionUtils.isNotEmpty(contactIds)){
            updateBuilder.andCondition(CriteriaAPI.getCondition("ID", "contactId", StringUtils.join(contactIds,","), NumberOperators.NOT_EQUALS));
        }
        if(contactType == V3ContactsContext.ContactType.VENDOR) {
            updateBuilder.andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", StringUtils.join(ids,","), NumberOperators.EQUALS));
        }
        else if(contactType == V3ContactsContext.ContactType.TENANT){
            updateBuilder.andCondition(CriteriaAPI.getCondition("TENANT_ID", "tenant", StringUtils.join(ids,","), NumberOperators.EQUALS));
        } else if(contactType == V3ContactsContext.ContactType.CLIENT){
            updateBuilder.andCondition(CriteriaAPI.getCondition("CLIENT_ID", "client", StringUtils.join(ids,","), NumberOperators.EQUALS));
        }

        Map<String, Object> value = new HashMap<>();
        value.put("isPrimaryContact", false);
        int count = updateBuilder.update(value);
        return count;

    }

    public static List<Map<String,Object>> getTenantContacts(List<Long> tenantIds) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.CONTACT);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField tenantId = fieldMap.get("tenant");

        SelectRecordsBuilder<V3ContactsContext> builder = new SelectRecordsBuilder<V3ContactsContext>()
                .module(module)
                .beanClass(V3ContactsContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(tenantId, tenantIds, PickListOperators.IS))
                ;

        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        LookupField requesterField = (LookupField) fieldsAsMap.get("requester");

        builder.fetchSupplement(requesterField);

        List<Map<String,Object>> records = builder.getAsProps();
        return records;

    }

    public static V3ContactsContext getContactsIdForUser(Long userId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.CONTACT);

        SelectRecordsBuilder<V3ContactsContext> builder = new SelectRecordsBuilder<V3ContactsContext>()
                .module(module)
                .beanClass(V3ContactsContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("REQUESTER_ID", "requester", String.valueOf(userId), NumberOperators.EQUALS))
                ;

        V3ContactsContext record = builder.fetchFirst();
        return record;

    }
}
