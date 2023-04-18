package com.facilio.bmsconsoleV3.commands.accessibleSpaces;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.util.AccessibleSpacesUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAccessibleSpacesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> accessibleSpaceIds = (List<Long>)context.get(FacilioConstants.ContextNames.ACCESSIBLE_SPACE);
        Long ouId = (Long)context.get(FacilioConstants.ContextNames.ORG_USER_ID);
        Long peopleId = (Long)context.get(FacilioConstants.ContextNames.PEOPLE_ID);

        if(CollectionUtils.isNotEmpty(accessibleSpaceIds)) {
            AccessibleSpacesUtil.addAccessibleSpace(ouId,peopleId, accessibleSpaceIds);
        }
        return false;
    }
}
