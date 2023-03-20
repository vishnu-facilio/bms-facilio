package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ValueGeneratorBean;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import java.util.List;
public class GetGlobalScopeMetaCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ValueGeneratorBean valGenBean = (ValueGeneratorBean) BeanFactory.lookup("ValueGeneratorBean");
        boolean includeOthers;
        if(valGenBean.getValueGenerator("com.facilio.modules.CurrentUserValueGenerator") != null ||
                valGenBean.getValueGenerator("com.facilio.modules.PeopleValueGenerator") != null) {
            includeOthers = true;
        } else {
            includeOthers = false;
        }
        context.put(FacilioConstants.ContextNames.MODULE_LIST,ScopingUtil.getModulesList(includeOthers));
        return false;
    }
}
