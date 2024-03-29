package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadFacilityBookingLookupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pplModule = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);

        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        LookupFieldMeta facilityField = new LookupFieldMeta((LookupField) fieldsAsMap.get("facility"));
        LookupField facilityLocationField = (LookupField) modBean.getField("location", FacilioConstants.ContextNames.FacilityBooking.FACILITY);
        facilityField.addChildSupplement(facilityLocationField);

        LookupFieldMeta tenantField = new LookupFieldMeta((LookupField) fieldsAsMap.get("tenant"));

        SupplementRecord reservedFor = (SupplementRecord) fieldsAsMap.get("reservedFor");

        MultiLookupMeta internalAttendees = new MultiLookupMeta((MultiLookupField) fieldsAsMap.get("internalAttendees"));
        FacilioField emailField = FieldFactory.getField("email", "EMAIL", pplModule, FieldType.STRING);
        FacilioField phoneField = FieldFactory.getField("phone", "PHONE", pplModule, FieldType.STRING);

        LookupField createdByField = (LookupField) fieldsAsMap.get("sysCreatedBy");
        fetchLookupsList.add(createdByField);

        LookupField modifiedByField = (LookupField) fieldsAsMap.get("sysModifiedBy");
        fetchLookupsList.add(modifiedByField);


        List<FacilioField> selectFieldsList = new ArrayList<>();
        selectFieldsList.add(emailField);
        selectFieldsList.add(phoneField);

        internalAttendees.setSelectFields(selectFieldsList);

        fetchLookupsList.add(facilityField);
        fetchLookupsList.add(reservedFor);
        fetchLookupsList.add(tenantField);
        fetchLookupsList.add(internalAttendees);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
}
