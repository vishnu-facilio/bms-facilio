package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import java.util.Objects;
public class PatchCustomPageCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(PatchCustomPageCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        PagesContext customPage = (PagesContext) context.get(FacilioConstants.CustomPage.CUSTOM_PAGE);

        PagesContext existingPage = CustomPageAPI.getCustomPage(customPage.getId());
        if(existingPage == null){
            LOGGER.error("Page does not exists, unable to patch");
            throw new IllegalArgumentException("Page not found");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(existingPage.getModuleId());

        if (Boolean.TRUE.equals(customPage.getIsDefaultPage()) && !existingPage.getIsDefaultPage()) {
            FacilioChain changeDefaultChain = TransactionChainFactory.getChangeDefaultPageChain();
            FacilioContext defaultPageContext = changeDefaultChain.getContext();
            defaultPageContext.put(FacilioConstants.ContextNames.ID, customPage.getId());
            defaultPageContext.put(FacilioConstants.ContextNames.APP_ID,existingPage.getAppId());
            defaultPageContext.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
            changeDefaultChain.execute();
        }

        if ((customPage.getIsDefaultPage()==null && existingPage.getIsDefaultPage()) || Boolean.TRUE.equals(customPage.getIsDefaultPage())) {
            LOGGER.info("Criteria can't be validated for Default Pages");
            customPage.setStatus(Boolean.TRUE);
            LOGGER.info("Default Page status is always true");
        }

        if (customPage.getCriteria() != null) {

            Criteria criteria = customPage.getCriteria();
            for (String key : criteria.getConditions().keySet()) {
                Condition condition = criteria.getConditions().get(key);
                if(condition.getField() == null && StringUtils.isEmpty(condition.getColumnName())) {
                    FacilioField field = modBean.getField(condition.getFieldName(), condition.getModuleName() != null ? condition.getModuleName() : module.getName());
                    condition.setField(field);
                }
            }
            long criteriaId = CriteriaAPI.addCriteria(criteria);
            customPage.setCriteriaId(criteriaId);

        }
        else{
            customPage.setCriteriaId(-1);
        }

        CustomPageAPI.patchCustomPage(customPage);

        CriteriaAPI.deleteCriteria(existingPage.getCriteriaId());
        return false;
    }
}