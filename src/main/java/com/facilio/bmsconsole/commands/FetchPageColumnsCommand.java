package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.PageColumnContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class FetchPageColumnsCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(FetchPageColumnsCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean excludeColumns = (boolean) context.getOrDefault(FacilioConstants.CustomPage.EXCLUDE_COLUMNS, false);

        if(!excludeColumns) {
            List<Long> tabId = (List<Long>) context.get(FacilioConstants.CustomPage.TAB_ID);

            if (CollectionUtils.isNotEmpty(tabId)) {
                Map<Long, List<PageColumnContext>> columns = CustomPageAPI.fetchColumns(tabId);

                if (MapUtils.isNotEmpty(columns)) {

                    List<Long> columnIds = columns.values().stream()
                            .flatMap(List::stream)
                            .map(PageColumnContext::getId)
                            .collect(Collectors.toList());

                    context.put(FacilioConstants.CustomPage.PAGE_COLUMNS, columns);
                    context.put(FacilioConstants.CustomPage.COLUMN_IDS, columnIds);
                }
            }
        }
        return false;
    }
}