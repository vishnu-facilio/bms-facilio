package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class AddPageTabCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(AddPageTabCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long pageId = (long) context.get(FacilioConstants.CustomPage.PAGE_ID);
        if (pageId <= 0) {
            LOGGER.error("Invalid Page Id For Creating Tab");
            throw new IllegalArgumentException("Invalid Page Id");
        }

        PageTabContext tab = (PageTabContext) context.get(FacilioConstants.CustomPage.TAB);
        FacilioModule tabsModule = ModuleFactory.getPageTabsModule();

        if (tab != null) {

            FacilioField pageIdField = FieldFactory.getNumberField("pageId", "PAGE_ID", tabsModule);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getEqualsCondition(pageIdField, String.valueOf(pageId)));

            tab.setPageId(pageId);

            if (tab.getSequenceNumber() <= 0) {
                double sequenceNumber = CustomPageAPI.getMaxSequenceNumber(tabsModule, criteria);
                tab.setSequenceNumber(sequenceNumber + 10);
                LOGGER.info("Sequence Number For Tab named -- " + tab.getDisplayName() + " is " + tab.getSequenceNumber());
            }

            Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
            String name = tab.getDisplayName() != null ? tab.getDisplayName() : "tab";
            name = CustomPageAPI.getUniqueName(tabsModule, criteria, pageIdField, pageId, name, isSystem);

            tab.setName(name);
            tab.setStatus(Boolean.FALSE);
            tab.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
            tab.setSysCreatedTime(System.currentTimeMillis());

            CustomPageAPI.insertPageTabToDB(tab);

            context.put(FacilioConstants.CustomPage.TAB_ID, tab.getId());
        }
        return false;
    }
}