package com.facilio.bmsconsoleV3.context;

import com.facilio.modules.ModuleBaseWithCustomFields;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class V3PeopleContextExtendedProps extends V3PeopleContext {
    // Tenant, Vendor, Client, Employee
    ModuleBaseWithCustomFields baseProp;
    // TenantContact, VendorContact, ClientContact
    V3PeopleContext subProp;
}
