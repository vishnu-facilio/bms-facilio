package com.facilio.fsm.commands.serviceOrders;

import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ValidateServiceOrderRecordCreationCommandV3 Contains the validation required for the ServiceOrder module.
 */
@Log4j
public class ValidateServiceOrderRecordCreationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderContext> serviceOrders = recordMap.get(moduleName);

        if (CollectionUtils.isEmpty(serviceOrders)) {
            return false;
        }

        List<Long> assetIds = new ArrayList<>();
        for (ServiceOrderContext serviceOrder : serviceOrders) {
            if (serviceOrder.getAsset() != null && serviceOrder.getAsset().getId() > 0) {
                assetIds.add(serviceOrder.getAsset().getId());
            }
        }
        if (CollectionUtils.isEmpty(assetIds)) {
            LOGGER.info("Asset IDs are empty.");
            return false;
        }

        /* Asset/Space Validation */
        List<V3AssetContext> assetContextList = V3RecordAPI.getRecordsList("asset", assetIds, V3AssetContext.class);
        if (CollectionUtils.isEmpty(assetContextList)) {
            LOGGER.info("Asset List is empty.");
            return false;
        }

        Map<Long, V3AssetContext> assetContextListMap = FieldUtil.getAsMap(assetContextList);

        for (ServiceOrderContext serviceOrder : serviceOrders) {
            if (serviceOrder.getAsset() != null && serviceOrder.getAsset().getId() > 0 &&
                    serviceOrder.getSpace() != null && serviceOrder.getSpace().getId() > 0) {
                V3AssetContext asset = assetContextListMap.get(serviceOrder.getAsset().getId());
                if (asset != null) {
                    long serviceOrderSpaceId = serviceOrder.getSpace().getId();
                    if (asset.getSpace() != null && asset.getSpace().getId() > 0 &&
                            serviceOrderSpaceId != asset.getSpace().getId()) {
                        throw new FSMException(FSMErrorCode.SO_ASSET_SPACE_DOESNT_MATCH_SPACE_FIELD);
                    }
                } else {
                    LOGGER.info("Asset is not available: " + serviceOrder.getAsset().getId());
                }
            } else if (serviceOrder.getAsset() != null && serviceOrder.getAsset().getId() > 0 && serviceOrder.getSpace() == null) {
                // Fill the space with asset's space.
                V3AssetContext asset = assetContextListMap.get(serviceOrder.getAsset().getId());
                if (asset.getSpace() != null && asset.getSpace().getId() > 0) {
                    V3SpaceContext space = new V3SpaceContext();
                    space.setId(asset.getSpace().getId());
                    serviceOrder.setSpace(space);
                }
            }
        }


        return false;
    }
}
