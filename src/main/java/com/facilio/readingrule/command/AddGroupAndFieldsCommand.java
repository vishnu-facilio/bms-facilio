package com.facilio.readingrule.command;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AddGroupAndFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context ctx) throws Exception {
        //TODO:... currently implemented included assets only

        NewReadingRuleContext readingRule = (NewReadingRuleContext) ctx.get(FacilioConstants.ContextNames.NEW_READING_RULE);

        List<Long> includedAssetIds = readingRule.getAssets();
        List<AssetContext> includedAssets = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(includedAssetIds)) {
            includedAssets = AssetsAPI.getAssetInfo(includedAssetIds);
        }

//        NewReadingRuleAPI.addNamespaceAndFields(readingRule, includedAssets);

        return false;
    }
}
