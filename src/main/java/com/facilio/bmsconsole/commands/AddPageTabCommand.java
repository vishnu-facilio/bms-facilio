package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsole.context.PagesContext;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Objects;

public class AddPageTabCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(AddPageTabCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PagesContext.PageLayoutType layoutType = (PagesContext.PageLayoutType) context.get(FacilioConstants.CustomPage.LAYOUT_TYPE);
        long pageId = (long) context.get(FacilioConstants.CustomPage.PAGE_ID);
        if (pageId <= 0) {
            LOGGER.error("Invalid page Id For Creating Tab");
            throw new IllegalArgumentException("Invalid Page Id");
        }

        Long layoutId = CustomPageAPI.getLayoutIdForPageId(pageId, layoutType);

        PageTabContext tab = (PageTabContext) context.get(FacilioConstants.CustomPage.TAB);
        FacilioModule tabsModule = ModuleFactory.getPageTabsModule();

        if (tab != null) {

            FacilioField layoutIdField = FieldFactory.getNumberField("layoutId", "LAYOUT_ID", tabsModule);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getEqualsCondition(layoutIdField, String.valueOf(layoutId)));

            tab.setLayoutId(layoutId);

            if (tab.getSequenceNumber() <= 0) {
                double sequenceNumber = CustomPageAPI.getMaxSequenceNumber(tabsModule, criteria);
                tab.setSequenceNumber(sequenceNumber + 10);
                LOGGER.info("Sequence Number For Tab named -- " + tab.getDisplayName() + " is " + tab.getSequenceNumber());
            }

            Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
            String name = StringUtils.isNotEmpty(tab.getName()) ? tab.getName() :
                    StringUtils.isNotEmpty(tab.getDisplayName())? tab.getDisplayName(): "tab";
            name = CustomPageAPI.getUniqueName(tabsModule, criteria, layoutIdField, layoutId, name, isSystem);
            if((isSystem != null && isSystem) && StringUtils.isNotEmpty(tab.getName()) && !tab.getName().equalsIgnoreCase(name)) {
                throw new IllegalArgumentException("linkName already exists, given linkName for tab is invalid");
            }

            tab.setName(name);
            if(tab.getStatus() == null) {
                tab.setStatus(false);
            }
            tab.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
            tab.setSysCreatedTime(System.currentTimeMillis());

            CustomPageAPI.insertPageTabToDB(tab);

            context.put(FacilioConstants.CustomPage.TAB_ID, tab.getId());
        }
        return false;
    }
}