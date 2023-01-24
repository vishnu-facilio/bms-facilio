package com.facilio.bmsconsole.commands;

import com.facilio.beans.WebTabBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class DeleteTabCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id != null && id > 0) {
            WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
            tabBean.deleteTab(id);
        }
        return false;
    }
}
