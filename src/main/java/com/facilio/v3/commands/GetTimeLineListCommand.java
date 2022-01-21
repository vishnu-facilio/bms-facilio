package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
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
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class GetTimeLineListCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(GetTimeLineListCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        TimelineRequest timelineRequest = (TimelineRequest) context.get(FacilioConstants.ContextNames.TIMELINE_REQUEST);

        if (!timelineRequest.isGetUnGrouped() && CollectionUtils.isEmpty(timelineRequest.getGroupIds())) {
            throw new IllegalArgumentException("At least one group id should be passed");
        }

        V3Config config = ChainUtil.getV3Config(timelineRequest.getModuleName());

        TimelineViewContext viewObj = (TimelineViewContext)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);

        if(viewObj == null)
        {
            throw new IllegalArgumentException("Invalid View details passed");
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(timelineRequest.getModuleName());
        List<FacilioField> allFields = modBean.getAllFields(module.getName());

        FacilioField idField = FieldFactory.getIdField(module);
        String idFieldColumnName = idField.getCompleteColumnName();

        FacilioField startTimeField = (FacilioField) context.get(FacilioConstants.ContextNames.TIMELINE_STARTTIME_FIELD);

        Criteria mainCriteria = (Criteria) context.get(FacilioConstants.ContextNames.TIMELINE_DATA_CRITERIA);

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
