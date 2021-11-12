package com.facilio.bmsconsoleV3.commands.accessibleSpaces;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class DeleteAccessibleSpacesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> accessibleSpaceIds = (List<Long>)context.get(FacilioConstants.ContextNames.ACCESSIBLE_SPACE);
        Long ouId = (Long)context.get(FacilioConstants.ContextNames.ORG_USER_ID);

        if(CollectionUtils.isNotEmpty(accessibleSpaceIds)) {
            deleteAccessibleSpace(ouId, accessibleSpaceIds);
        }
        return false;
    }

    private void deleteAccessibleSpace(long ouId, List<Long> accessibleSpaceIds) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getAccessibleSpaceModule().getTableName())
                ;
        builder.andCondition(CriteriaAPI.getCondition("ORG_USER_ID", "orgUserId", String.valueOf(ouId), NumberOperators.EQUALS));
        builder.andCondition(CriteriaAPI.getCondition("BS_ID", "bsId", StringUtils.join(accessibleSpaceIds, ","), NumberOperators.EQUALS));

        builder.delete();
    }
}
