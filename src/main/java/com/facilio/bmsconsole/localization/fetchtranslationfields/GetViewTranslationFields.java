package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationImpl.ViewTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;

import java.util.List;
import java.util.Properties;

@Log4j
public class GetViewTranslationFields implements TranslationTypeInterface {

    private static final String VIEWS = "views";

    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,String queryString,Properties properties ) throws Exception {
        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(context.getModuleIds().get(0));
        FacilioChain chain = FacilioChainFactory.getViewListChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME,module.getName());
        chain.getContext().put(FacilioConstants.ContextNames.GROUP_STATUS,true);
        chain.execute();
        JSONArray outerArray = new JSONArray();
        List<ViewGroups> groupViews = (List<ViewGroups>)chain.getContext().get(FacilioConstants.ContextNames.GROUP_VIEWS);
        if(CollectionUtils.isNotEmpty(groupViews)) {
            groupViews.forEach(groupView -> {
                String outerKey = ViewTranslationImpl.getTranslationKey(groupView.getName());
                List<FacilioView> views = groupView.getViews();
                outerArray.add(TranslationsUtil.constructJSON(groupView.getDisplayName(),VIEWS,TranslationConstants.DISPLAY_NAME,groupView.getName(),outerKey,properties));
                views.forEach(view -> {
                    String innerKey = ViewTranslationImpl.getTranslationKey(view.getName());
                    outerArray.add(TranslationsUtil.constructJSON(view.getDisplayName(),VIEWS,TranslationConstants.DISPLAY_NAME,view.getName(),innerKey,properties));
                });
            });
        }
        return outerArray;
    }
}
