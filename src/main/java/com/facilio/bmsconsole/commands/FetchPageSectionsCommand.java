package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.PageSectionContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class FetchPageSectionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> columnIds = (List<Long>) context.get(FacilioConstants.CustomPage.COLUMN_IDS);

        if(CollectionUtils.isNotEmpty(columnIds)){

            Map<Long,List<PageSectionContext>> sections = CustomPageAPI.fetchSections(columnIds);

            if (MapUtils.isNotEmpty(sections)) {
                List<Long> sectionIds = sections.values().stream()
                        .flatMap(List::stream)
                        .map(PageSectionContext::getId)
                        .collect(Collectors.toList());

                context.put(FacilioConstants.CustomPage.SECTION_IDS,sectionIds);
                context.put(FacilioConstants.CustomPage.PAGE_SECTIONS,sections);
            }
        }
        return false;
    }
}