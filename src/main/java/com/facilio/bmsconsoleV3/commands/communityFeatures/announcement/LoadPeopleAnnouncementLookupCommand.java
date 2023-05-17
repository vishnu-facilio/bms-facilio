package com.facilio.bmsconsoleV3.commands.communityFeatures.announcement;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.*;
import com.facilio.qa.context.QuestionContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.*;

public class LoadPeopleAnnouncementLookupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> additionaLookups = new ArrayList<SupplementRecord>();
        LookupField sysCreatedBy = (LookupField) FieldFactory.getSystemField("sysCreatedBy", modBean.getModule(FacilioConstants.ContextNames.ANNOUNCEMENT));
        additionaLookups.add(sysCreatedBy);
        LookupField sysModifiedBy = (LookupField) FieldFactory.getSystemField("sysModifiedBy", modBean.getModule(FacilioConstants.ContextNames.ANNOUNCEMENT));
        additionaLookups.add(sysModifiedBy);
        additionaLookups.add((LookupField)fieldsAsMap.get("people"));
        additionaLookups.add((LookupField)fieldsAsMap.get("createdBy"));
        MultiLookupMeta audienceField = new MultiLookupMeta((MultiLookupField) fieldsAsMap.get("audience"));

        FacilioField nameField = FieldFactory.getField("name", "NAME", modBean.getModule(FacilioConstants.ContextNames.AUDIENCE), FieldType.STRING);
        audienceField.setSelectFields(Collections.singletonList(nameField));
        additionaLookups.add(audienceField);
        additionaLookups.add((LargeTextField)fieldsAsMap.get("longDescription"));

        
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionaLookups);

        return false;
    }
}
