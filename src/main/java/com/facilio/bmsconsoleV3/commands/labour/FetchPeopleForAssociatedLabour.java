package com.facilio.bmsconsoleV3.commands.labour;

import com.facilio.bmsconsoleV3.context.labour.LabourContextV3;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FetchPeopleForAssociatedLabour extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Long> labourIds = (List<Long>) context.get("recordIds");

        FacilioModule module = Constants.getModBean().getModule(FacilioConstants.ContextNames.LABOUR);

        SelectRecordsBuilder<LabourContextV3> builder = new SelectRecordsBuilder<LabourContextV3>()
                .module(module)
                .select(Constants.getModBean().getAllFields(module.getName()))
                .beanClass(LabourContextV3.class)
                .andCondition(CriteriaAPI.getIdCondition(labourIds,module));

       List<LabourContextV3> labours = builder.get();

       List<Long> peopleIds = CollectionUtils.isNotEmpty(labours) ? labours.stream().map(p -> p.getPeople().getId()).collect(Collectors.toList()) : Collections.emptyList();

       context.put("peopleIds",peopleIds);

        return false;
    }
}
