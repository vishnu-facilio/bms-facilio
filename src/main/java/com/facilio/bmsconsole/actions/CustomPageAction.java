package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.ModuleSettingConfig.impl.PageBuilderConfigUtil;
import com.facilio.bmsconsole.ModuleSettingConfig.util.ModuleSettingConfigUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.util.SummaryWidgetUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@Getter
@Setter
public class CustomPageAction extends FacilioAction {
    private static final Logger LOGGER = Logger.getLogger(CustomPageAction.class.getName());
    private long id;
    private long previousId;
    private long recordId;
    private long nextId;
    private long appId = -1;
    private String moduleName;
    private long pageId;
    private PagesContext customPage;
    private String tabName;
    private Boolean status;
    private Boolean approval = false;
    private PagesContext.PageLayoutType layoutType;
    private Boolean excludeTabs = false;
    private boolean showNewPageBuilder = false;

    public String createCustomPage() throws Exception{
        FacilioChain chain = TransactionChainFactory.getCreateCustomPageChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.CustomPage.CUSTOM_PAGE, customPage);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        context.put(FacilioConstants.CustomPage.IS_DEFAULT_PAGE,false);
        context.put(FacilioConstants.ContextNames.APP_ID,appId);
        chain.execute();
        pageId = (long) context.get(FacilioConstants.CustomPage.PAGE_ID);
        setResult(FacilioConstants.CustomPage.PAGE_ID,pageId);
        Map<String, Long> layoutMap = (Map<String, Long>) context.get(FacilioConstants.CustomPage.LAYOUT_IDS);
        setResult(FacilioConstants.CustomPage.LAYOUT_IDS, layoutMap);
        return SUCCESS;
    }
    public String getAllCustomPage() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getAllCustomPageChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        context.put(FacilioConstants.CustomPage.LAYOUT_TYPE, layoutType);
        context.put(FacilioConstants.ContextNames.APP_ID,appId);
        chain.execute();

        List<Map<String, Object>> customPages = (List<Map<String, Object>>) context.get(FacilioConstants.CustomPage.CUSTOM_PAGES);
        setResult(FacilioConstants.CustomPage.CUSTOM_PAGES, FieldUtil.getAsJSONArray(customPages, PagesContext.class));
        return SUCCESS;
    }
    public String fetchPageForRecord()throws Exception{

        boolean isNewPage = AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PAGE_BUILDER) &&
                ModuleSettingConfigUtil.isConfigEnabledForModule(moduleName, FacilioConstants.SettingConfigurationContextNames.PAGE_BUILDER);
        if(isNewPage || showNewPageBuilder) {
            FacilioChain chain = ReadOnlyChainFactory.getPageForRecordChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.APP_ID, getAppId());
            context.put(FacilioConstants.ContextNames.RECORD_ID, getRecordId());
            context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
            context.put(FacilioConstants.CustomPage.LAYOUT_TYPE, layoutType);
            context.put(FacilioConstants.CustomPage.IS_BUILDER_REQUEST, false);
            context.put(FacilioConstants.CustomPage.TAB_NAME, getTabName());
            chain.execute();
            customPage = (PagesContext) context.get(FacilioConstants.CustomPage.CUSTOM_PAGE);
            setResult("isNewPage", true);
            setResult(FacilioConstants.CustomPage.CUSTOM_PAGE, customPage);
        }

        if(!isNewPage || customPage == null) {
            setResult("isNewPage", false);
        }
        return SUCCESS;
    }
    public String fetchCustomPage() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getCustomPageChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APP_ID,getAppId());
        context.put(FacilioConstants.ContextNames.MODULE_NAME,getModuleName());
        context.put(FacilioConstants.CustomPage.PAGE_ID,id);
        context.put(FacilioConstants.CustomPage.LAYOUT_TYPE, layoutType);
        context.put(FacilioConstants.CustomPage.IS_BUILDER_REQUEST,true);
        context.put(FacilioConstants.CustomPage.TAB_NAME,tabName);
        if(excludeTabs) {
            context.put(FacilioConstants.CustomPage.EXCLUDE_TABS, getExcludeTabs());
        }
        chain.execute();
        PagesContext customPage = (PagesContext) context.get(FacilioConstants.CustomPage.CUSTOM_PAGE);
        if(customPage.getId()!=-1) {
            setResult(FacilioConstants.CustomPage.CUSTOM_PAGE, customPage);
        }
        else{
            setResult(FacilioConstants.CustomPage.MESSAGE, "Page does not exists");
        }
        return SUCCESS;
    }
    public String patchCustomPage() throws Exception{
        FacilioChain chain = TransactionChainFactory.getPatchCustomPageChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.CustomPage.CUSTOM_PAGE, customPage);
        chain.execute();
        setResult("result",SUCCESS);
        return SUCCESS;
    }
    public String changePageStatus() throws Exception {
        FacilioChain chain = TransactionChainFactory.getChangeStatusForPageChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,id);
        context.put(FacilioConstants.ContextNames.STATUS,status);
        chain.execute();
        setResult("result",SUCCESS);
        return SUCCESS;
    }

    public String deleteCustomPage() throws Exception{
        FacilioChain chain = TransactionChainFactory.getDeleteCustomPageChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE, ModuleFactory.getPagesModule());
        context.put(FacilioConstants.ContextNames.ID,id);
        chain.execute();
        setResult("result",SUCCESS);
        return SUCCESS;
    }
    public String reorderPage() throws Exception{
        FacilioChain chain = TransactionChainFactory.getReorderPageChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.CustomPage.PREVIOUS_ID,previousId);
        context.put(FacilioConstants.ContextNames.ID,id);
        context.put(FacilioConstants.CustomPage.NEXT_ID,nextId);
        context.put(FacilioConstants.CustomPage.TYPE,CustomPageAPI.PageComponent.PAGE);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        chain.execute();
        double sequenceNumber = (double) context.get(FacilioConstants.CustomPage.SEQUENCE_NUMBER);
        setResult(FacilioConstants.CustomPage.SEQUENCE_NUMBER, sequenceNumber);
        return SUCCESS;
    }

    public String changeDefaultPage() throws Exception{
        FacilioChain chain = TransactionChainFactory.getChangeDefaultPageChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        context.put(FacilioConstants.ContextNames.ID,id);
        chain.execute();
        setResult("result",SUCCESS);
        return SUCCESS;
    }

    //Temp handlings
    public String getPageCriteriaFieldsForModule() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getCriteriaFieldsForPageBuilderChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);
        setResult("fields", FieldUtil.getAsJSONArray(fields, FacilioField.class));
        return SUCCESS;
    }

    public String checkPageBuilderEnabled() throws Exception {
        status = false;
        if(StringUtils.isNotEmpty(moduleName)) {
            try {
                status = ModuleSettingConfigUtil.isConfigEnabledForModule(moduleName, FacilioConstants.SettingConfigurationContextNames.PAGE_BUILDER);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
        setResult("status", status);
        return SUCCESS;
    }

    public String fetchPageBuilderEnabledModules() throws Exception {
        List<String> moduleNames = CustomPageAPI.getPageBuilderEnabledModules();
        setResult("modules", moduleNames);
        return SUCCESS;
    }
}