package com.facilio.bmsconsoleV3.commands.labour;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class UpdateStatusForDeletedLabour extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Long> peopleIds = (List<Long>) context.get("peopleIds");
        if (CollectionUtils.isEmpty(peopleIds)){
            return false;
        }
        FacilioModule module = Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(module.getName()));

        Map<String,Object> props = new HashMap<>();
        props.put("labour",false);

        UpdateRecordBuilder<V3PeopleContext> builder = new UpdateRecordBuilder<V3PeopleContext>()
                .fields(Collections.singletonList(fieldMap.get("labour")))
                .moduleName(module.getName())
                .andCondition(CriteriaAPI.getIdCondition(peopleIds,module));
        builder.updateViaMap(props);


        return false;
    }
}
