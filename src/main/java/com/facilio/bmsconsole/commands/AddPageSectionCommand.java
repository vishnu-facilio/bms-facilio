package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageColumnContext;
import com.facilio.bmsconsole.context.PageSectionContext;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class AddPageSectionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
        Map<Long, List<PageSectionContext>> columnSectionsMap = (Map<Long, List<PageSectionContext>>) context.get(FacilioConstants.CustomPage.COLUMN_SECTIONS_MAP);

        FacilioModule sectionsModule = ModuleFactory.getPageSectionsModule();
        List<PageSectionContext> sections = new ArrayList<>();

        if(MapUtils.isNotEmpty(columnSectionsMap)) {
            long currentUser = AccountUtil.getCurrentUser().getId();
            long currentTime = System.currentTimeMillis();
            FacilioField columnIdField = FieldFactory.getNumberField("columnId", "COLUMN_ID", sectionsModule);

            Criteria fetchExistingNamesCriteria = new Criteria();
            fetchExistingNamesCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(columnIdField, columnSectionsMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(", "))));
            Map<Long, List<String>> existingNamesMap = CustomPageAPI.getExistingNameListAsMap(sectionsModule, fetchExistingNamesCriteria, columnIdField);

            for(Map.Entry<Long, List<PageSectionContext>> entry : columnSectionsMap.entrySet()) {
                long columnId = entry.getKey();
                List<PageSectionContext> columnSections = entry.getValue();

                if(CollectionUtils.isNotEmpty(columnSections)) {
                    Criteria columnIdCriteria = new Criteria();
                    columnIdCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(columnIdField, String.valueOf(columnId)));
                    double sequenceNumber = CustomPageAPI.getMaxSequenceNumber(sectionsModule, columnIdCriteria);

                    for(PageSectionContext section : columnSections) {
                        section.setColumnId(columnId);

                        String name = CustomPageAPI.getLinkNameFromObjectOrDefault(section, "section");
                        name = CustomPageAPI.generateUniqueName(name, existingNamesMap.get(columnId), isSystem);
                        if ((isSystem != null && isSystem) && StringUtils.isNotEmpty(section.getName()) && !section.getName().equalsIgnoreCase(name)) {
                            throw new IllegalArgumentException("linkName already exists, given linkName for section is invalid");
                        }
                        CustomPageAPI.addNameToMap(name, columnId, existingNamesMap);

                        section.setName(name);
                        if (section.getSequenceNumber() <= 0) {
                            section.setSequenceNumber(sequenceNumber+=10);
                        }
                        section.setSysCreatedBy(currentUser);
                        section.setSysCreatedTime(currentTime);

                        sections.add(section);
                    }
                }

            }
            CustomPageAPI.insertPageSectionsToDB(sections);

            //below codes update sysModifiedTime in parent tables
            Map<String, Object> sysModifiedProps = new HashMap<>();
            sysModifiedProps.put("sysModifiedBy", currentUser);
            sysModifiedProps.put("sysModifiedTime", currentTime);
            CustomPageAPI.updateSysModifiedFields(sections.get(0).getColumnId(), sysModifiedProps, CustomPageAPI.PageComponent.COLUMN);
        }
        return false;
    }

}