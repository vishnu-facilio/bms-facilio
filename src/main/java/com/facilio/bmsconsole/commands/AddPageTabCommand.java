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
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class AddPageTabCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
        Map<Long, List<PageTabContext>> layoutTabsMap = (Map<Long, List<PageTabContext>>) context.get(FacilioConstants.CustomPage.LAYOUT_TABS_MAP);

        FacilioModule tabsModule = ModuleFactory.getPageTabsModule();
        List<PageTabContext> tabs = new ArrayList<>();

        if (MapUtils.isNotEmpty(layoutTabsMap)) {
            long currentUser = AccountUtil.getCurrentUser().getId();
            long currentTime = System.currentTimeMillis();
            FacilioField layoutIdField = FieldFactory.getNumberField("layoutId", "LAYOUT_ID", tabsModule);

            Criteria fetchExistingNamesCriteria = new Criteria();
            fetchExistingNamesCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(layoutIdField, layoutTabsMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(", "))));
            Map<Long, List<String>> existingNamesMap = CustomPageAPI.getExistingNameListAsMap(tabsModule, fetchExistingNamesCriteria, layoutIdField);

            for(Map.Entry<Long, List<PageTabContext>> entry : layoutTabsMap.entrySet()) {
                long layoutId = entry.getKey();
                List<PageTabContext> layoutTabs = entry.getValue();

                if(CollectionUtils.isNotEmpty(layoutTabs)) {
                    Criteria layoutIdCriteria = new Criteria();
                    layoutIdCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(layoutIdField, String.valueOf(layoutId)));
                    double sequenceNumber = CustomPageAPI.getMaxSequenceNumber(tabsModule, layoutIdCriteria);

                    for(PageTabContext tab : layoutTabs) {
                        tab.setLayoutId(layoutId);

                        String name = CustomPageAPI.getLinkNameFromObjectOrDefault(tab, "tab");
                        name = CustomPageAPI.generateUniqueName( name, existingNamesMap.get(layoutId), isSystem);
                        if ((isSystem != null && isSystem) && StringUtils.isNotEmpty(tab.getName()) && !tab.getName().equalsIgnoreCase(name)) {
                            throw new IllegalArgumentException("linkName already exists, given linkName for tab is invalid");
                        }
                        CustomPageAPI.addNameToMap(name, layoutId, existingNamesMap);

                        tab.setName(name);
                        if (tab.getTabType() == null) {
                            tab.setTabType(PageTabContext.TabType.SIMPLE);
                        }
                        if (tab.getSequenceNumber() <= 0) {
                            tab.setSequenceNumber(sequenceNumber+=10);
                        }
                        if (tab.getStatus() == null) {
                            tab.setStatus(false);
                        }
                        tab.setSysCreatedBy(currentUser);
                        tab.setSysCreatedTime(currentTime);
                        tabs.add(tab);
                    }
                }
            }

            CustomPageAPI.insertPageTabsToDB(tabs);

            //below codes update sysModifiedTime in parent tables
            Map<String, Object> sysModifiedProps = new HashMap<>();
            sysModifiedProps.put("sysModifiedBy", currentUser);
            sysModifiedProps.put("sysModifiedTime", currentTime);
            CustomPageAPI.updateSysModifiedFields(tabs.get(0).getLayoutId(), sysModifiedProps, CustomPageAPI.PageComponent.PAGE);
        }
        return false;
    }
}