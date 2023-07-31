package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.cache.CacheUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;

public class MultiCurrencyFieldMigrationAction extends FacilioAction{
    public String migrateMultiCurrencyField() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();

        long orgId = Long.parseLong(request.getParameter("orgId"));
        String moduleName = request.getParameter("moduleName");
        String fieldName = request.getParameter("fieldName");
        String baseCurrencyValueColumnName = request.getParameter("baseCurrencyValueColumnName");
        boolean revert = Boolean.parseBoolean(request.getParameter("revert"));
        int transactionTimeOut = StringUtils.isNotEmpty(request.getParameter("transactionTimeOut")) ? Integer.parseInt(request.getParameter("transactionTimeOut")) : -1;
        if(transactionTimeOut < 0){
            transactionTimeOut = 1800000; // by default 30 min
        }

        if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)){
            setResult("result", "Multi Currency License Not Enabled");
            return ERROR;
        }

        if(orgId > 0 && StringUtils.isNotEmpty(moduleName) && StringUtils.isNotEmpty(fieldName) && StringUtils.isNotEmpty(baseCurrencyValueColumnName)){
            AccountUtil.setCurrentAccount(orgId);
            FacilioChain updateFieldChain = TransactionChainFactory.getMultiCurrencyFieldUpdateChain(transactionTimeOut);
            FacilioContext context = updateFieldChain.getContext();
            context.put(FacilioConstants.ContextNames.ORGID, orgId);
            context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
            context.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, fieldName);
            context.put("baseCurrencyValueColumnName", baseCurrencyValueColumnName);
            context.put("revert", revert);
            updateFieldChain.setContext(context);
            updateFieldChain.execute();
            AccountUtil.cleanCurrentAccount();

            LRUCache.getModuleCache().removeStartsWith(CacheUtil.ORG_KEY(orgId));
            LRUCache.getFieldsCache().removeStartsWith(CacheUtil.ORG_KEY(orgId));
        }

        setResult("result", "success");
        ServletActionContext.getResponse().setStatus(200);

        return SUCCESS;
    }
}
