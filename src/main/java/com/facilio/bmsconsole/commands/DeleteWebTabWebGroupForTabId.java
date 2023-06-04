package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import com.facilio.beans.WebTabBean;
import com.facilio.fw.BeanFactory;

public class DeleteWebTabWebGroupForTabId extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long webTabId = (long) context.get(FacilioConstants.ContextNames.WEB_TAB_ID);

        if(webTabId > 0) {
            WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
            tabBean.deleteWebTabWebGroupForTabId(webTabId);
        }

        return false;
    }
}
