package com.facilio.bmsconsoleV3.commands.peoplegroup;

import com.facilio.bmsconsoleV3.context.peoplegroup.V3PeopleGroupContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class V3FetchPeopleGroupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA);

        FacilioUtil.throwIllegalArgumentException(criteria == null,"Criteria should not be null while fetching People Groups");

        List<V3PeopleGroupContext> groups = PeopleGroupUtils.fetchGroups(criteria);

        PeopleGroupUtils.setPeopleGroupMembers(groups);

        context.put(FacilioConstants.ContextNames.GROUPS, CollectionUtils.isNotEmpty(groups) ? groups : Collections.emptyList());

        return false;
    }
}
