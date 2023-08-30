package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.timelineview.context.TimelineScheduledViewContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.timeline.context.TimelineRequest;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.TimelineViewUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class GetTimelineScheduledViewListCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(GetTimelineScheduledViewListCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        TimelineRequest timelineRequest = (TimelineRequest) context.get(FacilioConstants.ContextNames.TIMELINE_REQUEST);
        boolean getUnscheduledOnly = (boolean) context.get(FacilioConstants.ContextNames.TIMELINE_GET_UNSCHEDULED_DATA);

        if (!timelineRequest.isGetUnGrouped() && !getUnscheduledOnly) {
            if(CollectionUtils.isEmpty(timelineRequest.getGroupIds())) {
                throw new IllegalArgumentException("At least one group id should be passed");
            }
            else if(timelineRequest.getGroupIds().size() > 1){
                throw new IllegalArgumentException("Only one group id is allowed");
            }
        }

        V3Config config = ChainUtil.getV3Config(timelineRequest.getModuleName());

        FacilioView viewObj = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        TimelineScheduledViewContext timelineView = viewObj.getTimelineScheduledViewContext();

        if(timelineView == null)
        {
            throw new IllegalArgumentException("Invalid View details passed");
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(timelineRequest.getModuleName());
        List<FacilioField> allFields = modBean.getAllFields(module.getName());

        FacilioField idField = FieldFactory.getIdField(module);
        String idFieldColumnName = idField.getCompleteColumnName();

        FacilioField startTimeField = timelineView.getStartDateField();
        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);

        Criteria mainCriteria = TimelineViewUtil.buildMainCriteria(startTimeField, timelineView.getEndDateField(), timelineRequest,
                                                                    timelineView.getGroupByField(), viewObj.getCriteria(),
                                                                    filterCriteria, getUnscheduledOnly);

        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        int page;
        int perPage = 0;
        int offset = 0;
        if (pagination != null) {
            page = (int) (pagination.get("page") == null ? 1 : pagination.get("page"));
            perPage = (int) (pagination.get("perPage") == null ? 50 : pagination.get("perPage"));
            timelineRequest.setMaxResultPerCell(perPage);
            offset = ((page-1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
        }

        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                .module(module)
                .beanClass(ChainUtil.getBeanClass(config, module))
                .select(allFields);
        builder.andCriteria(mainCriteria);
        if (Boolean.TRUE.equals(viewObj.isExcludeModuleCriteria())) {
            builder.skipModuleCriteria();
        }
        if(perPage > 0 && offset >= 0) {
            builder.offset(offset);
            builder.limit(perPage);
        }
        builder.orderBy(startTimeField.getCompleteColumnName() + "," + idFieldColumnName);
        List<Map<String, Object>> recordMapList = builder.getAsProps();

        context.put(FacilioConstants.ContextNames.TIMELINE_V3_DATAMAP, recordMapList);
        return false;
    }

}
