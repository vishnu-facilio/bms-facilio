package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CloneScopingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long scopingId = (long) context.get(FacilioConstants.ContextNames.SCOPING_ID);
        if (scopingId > 0) {
            ScopingContext scoping = ApplicationApi.getScoping(scopingId);
            if(scoping != null) {
                ScopingContext cloned = FieldUtil.cloneBean(scoping, ScopingContext.class);
                cloned.setId(-1);
                cloned.setIsDefault(false);
                ApplicationApi.addScoping(cloned);
                Map<Long, ScopingConfigContext> scopingMap = ApplicationApi.getScopingMapForApp(scopingId);
                if(MapUtils.isNotEmpty(scopingMap)) {
                    Collection<ScopingConfigContext> scopingConfigList = scopingMap.values();
                    for(ScopingConfigContext config : scopingConfigList) {
                        config.setId(-1);
                        config.setScopingId(cloned.getId());
                    }
                    if(CollectionUtils.isNotEmpty(scopingConfigList)) {
                        ApplicationApi.addScopingConfigForApp(new ArrayList<>(scopingConfigList));
                    }
                }

            }
        }
        return false;
    }
}
