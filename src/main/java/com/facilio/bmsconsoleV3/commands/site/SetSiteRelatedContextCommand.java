package com.facilio.bmsconsoleV3.commands.site;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import lombok.var;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class SetSiteRelatedContextCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        for (ModuleBaseWithCustomFields record: records) {
            var siteContext = (V3SiteContext) record;
            siteContext.setSpaceType(V3BaseSpaceContext.SpaceType.SITE);
        }



        return false;
    }
}
