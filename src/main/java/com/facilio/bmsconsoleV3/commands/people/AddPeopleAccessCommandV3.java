package com.facilio.bmsconsoleV3.commands.people;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class AddPeopleAccessCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PeopleContext> people = recordMap.get(moduleName);

        Map<Long, List<UpdateChangeSet>> changeSet = Constants.getModuleChangeSets(context);

        if(CollectionUtils.isNotEmpty(people)  && MapUtils.isNotEmpty(changeSet)) {
            for(V3PeopleContext ppl : people) {
                List<UpdateChangeSet> changes = changeSet.get(ppl.getId());
                if(CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes, "isOccupantPortalAccess", FacilioConstants.ContextNames.PEOPLE)) {
                    V3PeopleAPI.updatePeoplePortalAccess(ppl, FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
                }
            }
        }
        return false;
    }
}
