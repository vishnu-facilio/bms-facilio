package com.facilio.bmsconsole.commands;

import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import com.facilio.beans.WebTabBean;
import com.facilio.fw.BeanFactory;

import java.util.Collection;

public class DeleteWebTabWebGroupForTabId extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long webTabId = (long) context.get(FacilioConstants.ContextNames.WEB_TAB_ID);
        Collection<Long> webTabGroupIds = (Collection<Long>) context.get(PackageConstants.AppXMLConstants.WEB_TAB_GROUP_IDS);

        if(webTabId > 0) {
            ApplicationApi.deleteWebTabWebGroupForTabIdAndGroupIds(webTabId, webTabGroupIds);
        }

        return false;
    }
}
