package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

public class AddRotatingItemToolCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3AssetContext asset = (V3AssetContext) context.get(FacilioConstants.ContextNames.RECORD);
        context.put(FacilioConstants.ContextNames.PURCHASED_TOOL, null);

        if (asset != null) {
            if ((asset.getRotatingItem() != null && asset.getRotatingItem().getId() > 0) && (asset.getRotatingTool() != null && asset.getRotatingTool().getId() > 0)) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR ,"An asset can be either a Rotating Item or a Rotating Tool. Can't be both");
            } else {
                if (asset.getRotatingItem() != null && asset.getRotatingItem().getId() > 0) {
                    V3ItemContext item = asset.getRotatingItem();
                    FacilioChain c = TransactionChainFactory.getAddOrUpdateItemStockTransactionChain();
                    c.getContext().put(FacilioConstants.ContextNames.ITEM, item);
                    c.getContext().put(FacilioConstants.ContextNames.ROTATING_ASSET, asset);
                    c.getContext().put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, false);
                    c.execute();
                }
                if (asset.getRotatingTool() != null && asset.getRotatingTool().getId() > 0) {
                    V3ToolContext tool = asset.getRotatingTool();
                    FacilioChain c = TransactionChainFactory.getAddOrUpdateToolStockTransactionChain();
                    c.getContext().put(FacilioConstants.ContextNames.TOOL, tool);
                    c.getContext().put(FacilioConstants.ContextNames.ROTATING_ASSET, asset);
                    c.getContext().put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, false);
                    c.execute();
                }
            }
        }
        return false;
    }
}

