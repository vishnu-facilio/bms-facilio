package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class V3StoreroomApi {
    public static Map<Long, V3StoreRoomContext> getStoreRoomMap(long id) throws Exception
    {
        if(id <= 0) {
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.STORE_ROOM);
        SelectRecordsBuilder<V3StoreRoomContext> selectBuilder = new SelectRecordsBuilder<V3StoreRoomContext>()
                .select(fields)
                .table(module.getTableName())
                .moduleName(module.getName())
                .beanClass(V3StoreRoomContext.class)
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        return selectBuilder.getAsMap();
    }

    public static V3StoreRoomContext getStoreRoom(long id) throws Exception
    {
        if(id <= 0) {
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.STORE_ROOM);
        SelectRecordsBuilder<V3StoreRoomContext> selectBuilder = new SelectRecordsBuilder<V3StoreRoomContext>()
                .select(fields)
                .table(module.getTableName())
                .moduleName(module.getName())
                .beanClass(V3StoreRoomContext.class)
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        List<V3StoreRoomContext> storerooms =  selectBuilder.get();
        if(storerooms!=null &&!storerooms.isEmpty()) {
            return storerooms.get(0);
        }
        return null;
    }

    public static void updateStoreRoomLastPurchasedDate(long storeRoomId, long lastPurchasedDate) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.STORE_ROOM);
        V3StoreRoomContext storeRoom = new V3StoreRoomContext();
        storeRoom.setId(storeRoomId);
        storeRoom.setLastPurchasedDate(lastPurchasedDate);
        UpdateRecordBuilder<V3StoreRoomContext> updateBuilder = new UpdateRecordBuilder<V3StoreRoomContext>().module(module)
                .fields(fields).andCondition(CriteriaAPI.getIdCondition(storeRoomId, module));
        updateBuilder.update(storeRoom);
    }

    public static SelectRecordsBuilder<V3StoreRoomContext> getStoreRoomListBuilder(Long siteId, boolean includeServingSite) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.STORE_ROOM);
        SelectRecordsBuilder<V3StoreRoomContext> builder = new SelectRecordsBuilder<V3StoreRoomContext>().module(module)
                .beanClass(V3StoreRoomContext.class).select(fields);
        List<Long> accessibleSpaces = AccountUtil.getCurrentUser().getAccessibleSpace();

        if(includeServingSite) {
            builder.innerJoin("Storeroom_Sites").on("Storeroom_Sites.STORE_ROOM_ID = "+module.getTableName()+".ID");
        }
        if (siteId != null && siteId > 0) {
            if(includeServingSite) {
                builder.andCondition(CriteriaAPI.getCondition("Storeroom_Sites.SITE_ID", "siteId", String.valueOf(siteId), NumberOperators.EQUALS));
            }
            else {
                builder.andCondition(CriteriaAPI.getCondition("Store_room.SITE_ID", "siteId", String.valueOf(siteId), NumberOperators.EQUALS));
            }
        }
        else {
            if (accessibleSpaces != null && !accessibleSpaces.isEmpty()) {
                Set<Long> siteIds = new HashSet<Long>();
                for(Long accessibleSpace : accessibleSpaces) {
                    BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(accessibleSpace);
                    siteIds.add(baseSpace.getSite().getId());
                }
                if(includeServingSite) {
                    builder.andCondition(CriteriaAPI.getConditionFromList("Storeroom_Sites.SITE_ID", "siteId", siteIds, NumberOperators.EQUALS));
                }
                else {
                    builder.andCondition(CriteriaAPI.getConditionFromList("Store_room.SITE_ID", "siteId", siteIds, NumberOperators.EQUALS));
                }
            }
        }
        return builder;

    }

    public static Set<Long> getStoreRoomList(Long siteId, boolean includeServingSite) throws Exception {
        SelectRecordsBuilder<V3StoreRoomContext> builder = getStoreRoomListBuilder(siteId, includeServingSite);
        List<V3StoreRoomContext> storeRooms =  builder.get();
        Set<Long> storeIds = new HashSet<Long>();
        if(CollectionUtils.isNotEmpty(storeRooms)) {
            for(V3StoreRoomContext storeRoom : storeRooms) {
                storeIds.add(storeRoom.getId());
            }
        }
        return storeIds;
    }
}
