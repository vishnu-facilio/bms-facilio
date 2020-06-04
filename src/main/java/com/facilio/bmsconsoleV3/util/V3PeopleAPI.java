package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ClientContactContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class V3PeopleAPI {

    public static List<V3VendorContactContext> getVendorContacts(long vendorId, boolean fetchPrimaryContact) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_CONTACT);

        SelectRecordsBuilder<V3VendorContactContext> builder = new SelectRecordsBuilder<V3VendorContactContext>()
                .module(module)
                .beanClass(V3VendorContactContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(vendorId), NumberOperators.EQUALS));

        if (fetchPrimaryContact) {
            builder.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS));
        }
        List<V3VendorContactContext> records = builder.get();
        return records;
    }

    public static List<V3TenantContactContext> getTenantContacts(Long tenantId, boolean fetchPrimarycontact) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_CONTACT);

        SelectRecordsBuilder<V3TenantContactContext> builder = new SelectRecordsBuilder<V3TenantContactContext>()
                .module(module)
                .beanClass(V3TenantContactContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(tenantId), NumberOperators.EQUALS));
        ;
        if(fetchPrimarycontact) {
            builder.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS));
        }
        List<V3TenantContactContext> records = builder.get();
        return records;

    }

    public static void addParentPrimaryContactAsPeople(V3PeopleContext tc, FacilioModule module, List<FacilioField> fields, long parentId, V3PeopleContext primaryContactForParent) throws Exception {

        if(primaryContactForParent != null) {
            if(StringUtils.isNotEmpty(tc.getEmail())) {
                if(StringUtils.isEmpty(primaryContactForParent.getEmail())) {
                    tc.setId(primaryContactForParent.getId());
                    updatePeopleRecord(tc, module, fields);
                    return;
                }
                else if(primaryContactForParent.getEmail().equals(tc.getEmail())) {
                    tc.setId(primaryContactForParent.getId());
                    RecordAPI.updateRecord(tc, module, fields);
                    return;
                }
                else {
                    addPeopleRecord(tc, module, fields, parentId);
                    return;
                }
            }
            else {
                tc.setId(primaryContactForParent.getId());
                RecordAPI.updateRecord(tc, module, fields);
                return;
            }
        }
        else {
            if(StringUtils.isNotEmpty(tc.getEmail())) {
                addPeopleRecord(tc, module, fields, parentId);
                return;
            }
            RecordAPI.addRecord(true, Collections.singletonList(tc), module, fields);
        }

    }

    public static void updatePeopleRecord(V3PeopleContext ppl, FacilioModule module, List<FacilioField> fields) throws Exception {
        V3PeopleContext peopleExisting = getPeople(ppl.getEmail());
        if(peopleExisting == null) {
            RecordAPI.updateRecord(ppl, module, fields);
            return;
        }
        throw new IllegalArgumentException("People with the same email id already exists");

    }

    public static void addPeopleRecord(V3PeopleContext ppl, FacilioModule module, List<FacilioField> fields, long parentId) throws Exception {
        V3PeopleContext peopleExisting = getPeople(ppl.getEmail());
        if(peopleExisting == null) {
            RecordAPI.addRecord(true, Collections.singletonList(ppl), module, fields);
            unMarkPrimaryContact(ppl, parentId);

            return;
        }
        throw new IllegalArgumentException("People with the same email id already exists");

    }

    public static V3PeopleContext getPeople(String email) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
        SelectRecordsBuilder<V3PeopleContext> builder = new SelectRecordsBuilder<V3PeopleContext>()
                .module(module)
                .beanClass(V3PeopleContext.class)
                .select(fields)
                ;

        if(StringUtils.isNotEmpty(email)) {
            builder.andCondition(CriteriaAPI.getCondition("EMAIL", "email", String.valueOf(email), StringOperators.IS));
        }

        V3PeopleContext records = builder.fetchFirst();
        return records;
    }

    public static int unMarkPrimaryContact(V3PeopleContext person, long parentId) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = null;
        List<FacilioField> fields = new ArrayList<FacilioField>();
        List<FacilioField> updatedfields = new ArrayList<FacilioField>();
        com.facilio.db.criteria.Condition condition = null;

        if(person instanceof V3TenantContactContext) {
            module = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
            fields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.TENANT_CONTACT));
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            FacilioField primaryContactField = fieldMap.get("isPrimaryContact");
            updatedfields.add(primaryContactField);
            condition = CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(parentId), NumberOperators.EQUALS);


        }
        else if(person instanceof V3VendorContactContext) {
            module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
            fields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_CONTACT));
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            FacilioField primaryContactField = fieldMap.get("isPrimaryContact");
            updatedfields.add(primaryContactField);
            condition = CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(parentId), NumberOperators.EQUALS);
        }
        else if(person instanceof V3ClientContactContext) {
            module = modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT);
            fields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.CLIENT_CONTACT));
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            FacilioField primaryContactField = fieldMap.get("isPrimaryContact");
            updatedfields.add(primaryContactField);
            condition = CriteriaAPI.getCondition("CLIENT_ID", "client", String.valueOf(parentId), NumberOperators.EQUALS);
        }

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(updatedfields)
                .andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS))

                ;

        updateBuilder.andCondition(CriteriaAPI.getCondition("ID", "peopleId", String.valueOf(person.getId()), NumberOperators.NOT_EQUALS));
        updateBuilder.andCondition(condition);

        Map<String, Object> value = new HashMap<>();
        value.put("isPrimaryContact", false);
        int count = updateBuilder.update(value);
        return count;

    }

    public static V3TenantContext getTenantForUser(long ouId) throws Exception {
        long pplId = PeopleAPI.getPeopleIdForUser(ouId);
        if(pplId <= 0) {
            throw new IllegalArgumentException("Invalid People Id mapped with ORG_User");
        }
        V3TenantContactContext tc = (V3TenantContactContext)RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT_CONTACT, pplId);
        if (tc != null && tc.getTenant() != null && tc.getTenant().getId() > 0) {
            return (V3TenantContext)RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT, tc.getTenant().getId());
        }
        return null;
    }

}
