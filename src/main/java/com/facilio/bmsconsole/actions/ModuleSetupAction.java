package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ModuleSettingContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.List;

@Getter
@Setter
public class ModuleSetupAction extends FacilioAction {

    private int moduleType;
    private String tabName;
    private String moduleName;
    private Boolean defaultModules;
    private List<ModuleSettingContext> settings;

    public void addPagination(FacilioContext context) {
        if (getPage() != 0 ) {
            JSONObject pagination = new JSONObject();
            pagination.put("page", getPage());
            pagination.put("perPage", getPerPage());
            if (getPerPage() < 0) {
                pagination.put("perPage", 5000);
            }
            context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        }
    }

    public String getSystemModulesList() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getSystemModulesListChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        addPagination(context);

        chain.execute();

        setResult("modules", context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return SUCCESS;
    }

    public String getCustomModulesList() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getCustomModulesListChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        addPagination(context);
        
        chain.execute();

        setResult("modules", context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return SUCCESS;
    }

    public String getModuleFields() throws Exception {
        FacilioChain getFieldsChain = ReadOnlyChainFactory.getModuleFieldsChain();

        FacilioContext context = getFieldsChain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        context.put("skipStateField", true);
        addPagination(context);
        
        getFieldsChain.execute();

        setResult("fields", context.get(FacilioConstants.ContextNames.FIELDS));
        return SUCCESS;
    }

    public String getRelatedModules() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getRelatedModulesChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        addPagination(context);

        chain.execute();

        setResult("modules", context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return SUCCESS;
    }

    public String getExtendedModules() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getExtendedModulesChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        addPagination(context);
        
        chain.execute();

        setResult("modules", context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return SUCCESS;
    }

    public String getSummary() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getModuleSummaryChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();

        setResult("summary", context.get(FacilioConstants.ContextNames.SUMMARY));
        setResult("customization", context.get(FacilioConstants.ContextNames.CUSTOMIZATION));
        return SUCCESS;
    }

    public String getSetting() throws Exception {
        FacilioChain chain = TransactionChainFactory.getModuleSettingChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        chain.execute();

        setResult("setting", context.get(FacilioConstants.ContextNames.MODULE_SETTING));
        return SUCCESS;
    }

    public String updateSetting() throws Exception {
        FacilioChain chain = TransactionChainFactory.getUpdateModuleSettingChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.MODULE_SETTING, settings);
        chain.execute();

        setResult(FacilioConstants.ContextNames.MODULE, settings);
        return SUCCESS;
    }

    public String getSystemModulesCount() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getSystemModulesCountChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        chain.execute();

        setResult("count", context.get(FacilioConstants.ContextNames.COUNT));
        return SUCCESS;
    }

    public String getCustomModulesCount() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getCustomModulesCountChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        chain.execute();

        setResult("count", context.get(FacilioConstants.ContextNames.COUNT));

        return SUCCESS;
    }

    public String getModuleFieldsCount() throws Exception {
        FacilioChain getFieldsChain = ReadOnlyChainFactory.getModuleFieldsCountChain();

        FacilioContext context = getFieldsChain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        context.put("handleStateField", true);
        getFieldsChain.execute();

        setResult("count", context.get(FacilioConstants.ContextNames.COUNT));
        return SUCCESS;
    }

    public String getRelatedModulesCount() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getRelatedModulesCountChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        chain.execute();

        setResult("count", context.get(FacilioConstants.ContextNames.COUNT));

        return SUCCESS;
    }

    public String getExtendedModulesCount() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getExtendedModulesCountChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        chain.execute();

        setResult("count", context.get(FacilioConstants.ContextNames.COUNT));
        return SUCCESS;
    }

    public String getRelationsCount() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getRelationCountChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        chain.execute();

        setResult("count", context.get(FacilioConstants.ContextNames.COUNT));
        return SUCCESS;
    }

    public String getCustomButtonsCount() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getCustomButtonsCountChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        chain.execute();

        setResult("count", context.get(FacilioConstants.ContextNames.COUNT));
        return SUCCESS;
    }
}
