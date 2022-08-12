package com.facilio.bmsconsoleV3.commands.labour;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.labour.LabourContextV3;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class UpdatePeopleAsALabour extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);



        if (CollectionUtils.isNotEmpty(records)) {
            List<Long> peopleIds = new ArrayList<>();
            for (ModuleBaseWithCustomFields record : records) {
                LabourContextV3 labour = (LabourContextV3) record;
                if (labour != null) {
                    peopleIds.add(labour.getPeople().getId());
                }
            }
            updatePeopleAsALabour(peopleIds);
        }

        return false;
    }

    private void updatePeopleAsALabour(List<Long> peopleIds) throws Exception {

        ModuleBean bean = Constants.getModBean();
        FacilioModule module = bean.getModule(FacilioConstants.ContextNames.PEOPLE);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(bean.getAllFields(module.getName()));

        Map<String,Object> prop = new HashMap<>();
        prop.put("labour",true);

        UpdateRecordBuilder<V3PeopleContext> builder = new UpdateRecordBuilder<V3PeopleContext>()
                .module(module)
                .fields(Collections.singletonList(fieldMap.get("labour")))
                .andCondition(CriteriaAPI.getIdCondition(peopleIds,module));
        builder.updateViaMap(prop);
    }
}
