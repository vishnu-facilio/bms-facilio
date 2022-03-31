package com.facilio.bmsconsoleV3.commands.people;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.List;
import java.util.Map;

public class MarkRandomContactAsPrimaryCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PeopleContext> peopleList = recordMap.get(moduleName);
        for (V3PeopleContext people : peopleList) {
            Long parentId = null;
            if (people.getPeopleType() == V3PeopleContext.PeopleType.TENANT_CONTACT.getIndex() && ((V3TenantContactContext) people).getTenant() != null) {
                parentId = ((V3TenantContactContext) people).getTenant().getId();
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(FieldFactory.getTenantContactFields()).get("tenant"), String.valueOf(parentId), NumberOperators.EQUALS));
                List<V3TenantContactContext> tenantContacts = V3RecordAPI.getRecordsListWithSupplements(moduleName, null, V3TenantContactContext.class, criteria, null);
                if(CollectionUtils.isNotEmpty(tenantContacts)){
                    V3TenantContactContext tenantContact = tenantContacts.get(0);
                    V3PeopleAPI.markPrimaryContact(tenantContact);
                    V3PeopleAPI.rollUpModulePrimarycontactFields(parentId, FacilioConstants.ContextNames.TENANT, tenantContact.getName(), tenantContact.getEmail(), tenantContact.getPhone());
                }
            }
            else if (people.getPeopleType() == V3PeopleContext.PeopleType.VENDOR_CONTACT.getIndex() && ((V3VendorContactContext) people).getVendor() != null) {
                parentId = ((V3VendorContactContext) people).getVendor().getId();
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(FieldFactory.getVendorContactFields()).get("vendor"), String.valueOf(parentId), NumberOperators.EQUALS));
                List<V3VendorContactContext> vendorContacts = V3RecordAPI.getRecordsListWithSupplements(moduleName, null, V3VendorContactContext.class, criteria, null);
                if(CollectionUtils.isNotEmpty(vendorContacts)){
                    V3VendorContactContext vendorContact = vendorContacts.get(0);
                    V3PeopleAPI.markPrimaryContact(vendorContact);
                    V3PeopleAPI.rollUpModulePrimarycontactFields(parentId, FacilioConstants.ContextNames.VENDORS, vendorContact.getName(), vendorContact.getEmail(), vendorContact.getPhone());
                }
            }
        }
        return false;
    }
}
