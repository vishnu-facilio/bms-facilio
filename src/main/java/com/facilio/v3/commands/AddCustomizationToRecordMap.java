package com.facilio.v3.commands;

import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.recordcustomization.RecordCustomizationContext;
import com.facilio.timeline.context.CustomizationDataContext;
import com.facilio.v3.util.TimelineViewUtil;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AddCustomizationToRecordMap extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        TimelineViewContext viewObj = (TimelineViewContext)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        RecordCustomizationContext customizationObj = viewObj.getRecordCustomization();
        List<Map<String, Object>> recordMapList = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TIMELINE_V3_DATAMAP);

        List<CustomizationDataContext> customizationDataList = TimelineViewUtil.getCustomizationDataMap(recordMapList, customizationObj);
        context.put(FacilioConstants.ContextNames.TIMELINE_CUSTOMIZATONDATA_MAP, customizationDataList);
        return false;
    }

}
