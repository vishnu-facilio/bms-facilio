package com.facilio.bmsconsoleV3.commands.accessibleSpaces;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class FetchAccessibleSpacesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
       Long ouId = (Long)context.get(FacilioConstants.ContextNames.ORG_USER_ID);
        List<BaseSpaceContext> baseSpaces = null;
       if(ouId != null && ouId > 0) {
           baseSpaces = getAccessibleSpaceList(ouId);
       }
       context.put(FacilioConstants.ContextNames.ACCESSIBLE_SPACE, baseSpaces);
       return false;
    }

    private List<BaseSpaceContext> getAccessibleSpaceList(Long ouId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(AccountConstants.getAccessbileSpaceFields())
                .table(ModuleFactory.getAccessibleSpaceModule().getTableName()).andCondition(CriteriaAPI
                        .getCondition("ORG_USER_ID", "ouid", String.valueOf(ouId), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<BaseSpaceContext> baseSpaces = new ArrayList<>();
            for(Map<String, Object> map :props) {
                Long baseSpaceId = (Long)map.get("bsid");
                if(baseSpaceId != null && baseSpaceId > 0) {
                    BaseSpaceContext bs = SpaceAPI.getBaseSpace(baseSpaceId);
                    baseSpaces.add(bs);
                }
            }
            return baseSpaces;
        }
        return null;
    }

}
