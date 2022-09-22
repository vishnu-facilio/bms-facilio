package com.facilio.bmsconsoleV3.context.spacebooking;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.MultiLookupMeta;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SpaceBookingSupplementsCommand extends FacilioCommand{




    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        SupplementRecord space = (SupplementRecord) fieldsAsMap.get("space");
        SupplementRecord desk = (SupplementRecord) fieldsAsMap.get("desk");
        SupplementRecord parkingStall = (SupplementRecord) fieldsAsMap.get("parkingStall");
        SupplementRecord host = (SupplementRecord) fieldsAsMap.get("host");
        SupplementRecord reservedBy = (SupplementRecord) fieldsAsMap.get("reservedBy");
        SupplementRecord sysCreatedBy = (SupplementRecord) fieldsAsMap.get("sysCreatedBy");



        MultiLookupMeta internalAttendees = new MultiLookupMeta((MultiLookupField) fieldsAsMap.get("internalAttendees"));

        fetchLookupsList.add(space);
        fetchLookupsList.add(desk);
        fetchLookupsList.add(parkingStall);
        fetchLookupsList.add(host);
        fetchLookupsList.add(reservedBy);
        fetchLookupsList.add(sysCreatedBy);

        fetchLookupsList.add(internalAttendees);

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }

}
