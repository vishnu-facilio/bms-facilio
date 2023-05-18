package com.facilio.backgroundactivity.commands;

import com.facilio.backgroundactivity.context.BackgroundActivity;
import com.facilio.backgroundactivity.util.BackgroundActivityUtil;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FillColourCodeForBackgroundActivityCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<BackgroundActivity> backgroundActivities = Constants.getRecordList((FacilioContext) context);

        if(CollectionUtils.isNotEmpty(backgroundActivities)) {
            for(BackgroundActivity backgroundActivity : backgroundActivities) {
                String colorCode = BackgroundActivityUtil.getStatusColorCode(backgroundActivity.getRecordType(),backgroundActivity.getPercentage());
                if(StringUtils.isNotEmpty(colorCode)) {
                    backgroundActivity.setStatusColorCode(colorCode);
                } else {
                    //black color by default
                    backgroundActivity.setStatusColorCode("#000000");
                }
            }
        }
        return false;
    }
}
