package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Log4j
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

    @Override
    public Criteria getCriteria(FacilioField field,List<Long> value) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = new Criteria();
            if(CollectionUtils.isNotEmpty(value)) {
                criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM)), StringUtils.join(value, ","), NumberOperators.EQUALS));
            }
            return criteria;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }
}
