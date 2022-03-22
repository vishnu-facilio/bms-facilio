package com.facilio.modules;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StoreRoomValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        List<Object> values = new ArrayList<Object>();
        try {
            List<V3StoreRoomContext> storeRooms = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.STORE_ROOM, null, V3StoreRoomContext.class);
            if(CollectionUtils.isNotEmpty(storeRooms)) {
                for (V3StoreRoomContext store : storeRooms) {
                    values.add(store.getId());
                }
                return StringUtils.join(values, ",");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.STOREROOM;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.StoreRoomValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.STORE_ROOM;
    }

    @Override
    public Boolean getIsHidden() {
        return false;
    }

    @Override
    public Integer getOperatorId() {
        return 36;
    }
}
