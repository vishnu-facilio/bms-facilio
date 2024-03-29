package com.facilio.agent.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FetchAgentDataLoggerSupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        List<LookupField> fetchLookupsList = new ArrayList<>();

        fetchLookupsList.add((LookupField) fieldsAsMap.get("agent"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("controller"));

        long agentAppId = ApplicationApi
                .getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP);

        FacilioView view = ViewAPI.getView(moduleName,module.getModuleId(), moduleName, Objects.requireNonNull(AccountUtil.getCurrentOrg()).getOrgId(), agentAppId);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        if(view !=null ){
            context.put(FacilioConstants.ContextNames.CUSTOM_VIEW,view);
        }


        return false;
    }
}
