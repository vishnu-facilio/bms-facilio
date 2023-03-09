package com.facilio.bmsconsoleV3.commands.accessibleSpaces;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facilio.bmsconsoleV3.util.AccessibleSpacesUtil.getAccessibleSpaceCount;

public class AddAccessibleSpacesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> accessibleSpaceIds = (List<Long>)context.get(FacilioConstants.ContextNames.ACCESSIBLE_SPACE);
        Long ouId = (Long)context.get(FacilioConstants.ContextNames.ORG_USER_ID);

        if(CollectionUtils.isNotEmpty(accessibleSpaceIds)) {
            addAccessibleSpace(ouId, accessibleSpaceIds);
        }
        return false;
    }

    private void addAccessibleSpace(long uid, List<Long> accessibleSpace) throws Exception {

        Long existingSpaces = getAccessibleSpaceCount(uid,null);
        if(existingSpaces != null && accessibleSpace.size() > (100L - existingSpaces)){
            throw new RESTException(ErrorCode.VALIDATION_ERROR,"Accessible spaces addition cancelled - breach of max limit");
        }

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getAccessibleSpaceModule().getTableName())
                .fields(AccountConstants.getAccessbileSpaceFields());

        Map<Long, BaseSpaceContext> idVsBaseSpace = SpaceAPI.getBaseSpaceMap(accessibleSpace);

        for (Long bsid : accessibleSpace) {
            Map<String, Object> props = new HashMap<>();
            props.put("ouid", uid);
            props.put("bsid", bsid);
            props.put("siteId", getParentSiteId(bsid, idVsBaseSpace));
            insertBuilder.addRecord(props);
        }
        insertBuilder.save();

    }

    private long getParentSiteId(long baseSpaceId, Map<Long, BaseSpaceContext> idVsBaseSpace) {
        BaseSpaceContext baseSpace = idVsBaseSpace.get(baseSpaceId);
        if (baseSpace.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SITE) {
            return baseSpace.getId();
        }
        return baseSpace.getSiteId();
    }
}
