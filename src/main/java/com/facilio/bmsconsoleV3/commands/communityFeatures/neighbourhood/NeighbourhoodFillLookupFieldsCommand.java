package com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NeighbourhoodFillLookupFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        fetchLookupsList.add((LookupField) fieldsAsMap.get("location"));
        LookupField sysCreatedBy = (LookupField) FieldFactory.getSystemField("sysCreatedBy", modBean.getModule(moduleName));
        fetchLookupsList.add(sysCreatedBy);
        LookupField sysModifiedBy = (LookupField) FieldFactory.getSystemField("sysModifiedBy", modBean.getModule(moduleName));
        fetchLookupsList.add(sysModifiedBy);
        fetchLookupsList.add((LargeTextField)fieldsAsMap.get("description"));

        MultiLookupMeta audienceField = new MultiLookupMeta((MultiLookupField) fieldsAsMap.get("audience"));

        FacilioField nameField = FieldFactory.getField("name", "NAME", modBean.getModule(FacilioConstants.ContextNames.AUDIENCE), FieldType.STRING);
        audienceField.setSelectFields(Collections.singletonList(nameField));
        fetchLookupsList.add(audienceField);

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);


        return false;
    }
}
