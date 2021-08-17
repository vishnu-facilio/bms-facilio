package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationImpl.StateFlowTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Properties;

public class GetStateTranslationFields implements TranslationTypeInterface {
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,String queryString,Properties properties ) throws Exception {

        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");

        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        JSONArray jsonArray = new JSONArray();
        List<Long> moduleIds = context.getModuleIds();

        if(CollectionUtils.isNotEmpty(moduleIds)) {

            for (long moduleId : moduleIds) {

                FacilioModule module = moduleBean.getModule(moduleId);
                FacilioChain statusListChain = FacilioChainFactory.getTicketStatusListChain();
                FacilioContext stateContext = statusListChain.getContext();
                stateContext.put(FacilioConstants.ContextNames.PARENT_MODULE,module.getName());
                stateContext.put(FacilioConstants.ContextNames.APPROVAL_STATUS,false);
                statusListChain.execute();

                List<FacilioStatus> statues = (List<FacilioStatus>)stateContext.get(FacilioConstants.ContextNames.TICKET_STATUS_LIST);

                if(CollectionUtils.isNotEmpty(statues)) {

                    for (FacilioStatus status : statues) {
                        String key = StateFlowTranslationImpl.getTranslationKey(StateFlowTranslationImpl.STATE,String.valueOf(status.getId()));
                        jsonArray.add(TranslationsUtil.constructJSON(status.getDisplayName(),StateFlowTranslationImpl.STATE,TranslationConstants.DISPLAY_NAME,String.valueOf(status.getId()),key,properties));
                    }

                }
            }
        }

        JSONObject fieldObject = new JSONObject();
        fieldObject.put("fields",jsonArray);
        fieldObject.put("label","");

        JSONArray sectionArray = new JSONArray();
        sectionArray.add(fieldObject);

        return sectionArray;
    }
}
