package com.facilio.bmsconsoleV3.util;

import com.amazonaws.services.dynamodbv2.xspec.NULL;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.bmsconsoleV3.context.readingimportapp.V3ReadingImportAppContext;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingFormRelationContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.forms.FacilioForm;


@Log4j
public class V3SpaceBookingApi {
    private static Logger log = LogManager.getLogger(UserBeanImpl.class.getName());

    public static List<V3SpaceBookingContext> getBookingListFromSpaceIds(List<Long> spaceIds, Long startTime, Long endTime) throws Exception {


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> map = FieldFactory.getAsMap(fields);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(map.get("bookingStartTime"), String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL));
        criteria.addAndCondition(CriteriaAPI.getCondition(map.get("bookingEndTime"), String.valueOf(endTime), NumberOperators.LESS_THAN_EQUAL));
        criteria.addAndCondition(CriteriaAPI.getCondition(map.get("space"), StringUtils.join(spaceIds, ","), NumberOperators.EQUALS));

        List<SupplementRecord> supplementRecords = new ArrayList<>();
        supplementRecords.add((SupplementRecord)map.get("host"));
        supplementRecords.add((SupplementRecord)map.get("space"));

        List<V3SpaceBookingContext> bookingList =  V3RecordAPI.getRecordsListWithSupplements(module.getName(), null, V3SpaceBookingContext.class, criteria, supplementRecords);

        if (bookingList != null) {
            return bookingList;
        }
        return new ArrayList<>();
    }

    public static List<V3SpaceBookingContext> getActiveBookingListFromSpaceIds(List<Long> spaceIds, Long startTime, Long endTime) throws Exception {


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> map = FieldFactory.getAsMap(fields);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(map.get("bookingStartTime"), String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL));
        criteria.addAndCondition(CriteriaAPI.getCondition(map.get("bookingEndTime"), String.valueOf(endTime), NumberOperators.LESS_THAN_EQUAL));
        criteria.addAndCondition(CriteriaAPI.getCondition(map.get("space"), StringUtils.join(spaceIds, ","), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(map.get("isCancelled"), CommonOperators.IS_EMPTY));

        List<SupplementRecord> supplementRecords = new ArrayList<>();
        supplementRecords.add((SupplementRecord)map.get("host"));
        supplementRecords.add((SupplementRecord)map.get("space"));

        List<V3SpaceBookingContext> bookingList =  V3RecordAPI.getRecordsListWithSupplements(module.getName(), null, V3SpaceBookingContext.class, criteria, supplementRecords);

        if (bookingList != null) {
            return bookingList;
        }
        return new ArrayList<>();
    }

    public static Long getSpaceCategoryRelationFormId(Long categoryId, Long parentModuleId) throws Exception {
        Long formId = null;


        FacilioModule module = ModuleFactory.getSpaceBookingFormRelationModule();
//        FacilioModule module = moduleFac.getModule(FacilioConstants.ContextNames.SpaceCategoryFormRelation.SPACE_CATEGORY_FORM_RELATION);

        Long appId = AccountUtil.getCurrentApp().getId();

//        SelectRecordsBuilder<V3SpaceBookingFormRelationContext> selectBuilder = new SelectRecordsBuilder<V3SpaceBookingFormRelationContext>()
//                .module(module)
//                .select(FieldFactory.getSpaceCategoryFormRelationFields())
//                .beanClass(V3SpaceBookingFormRelationContext.class)
//                .andCondition(CriteriaAPI.getCondition(module.getTableName() + ".APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS))
//                .andCondition(CriteriaAPI.getCondition(module.getTableName() + ".CATEGORY_ID", "categoryId", String.valueOf(categoryId), NumberOperators.EQUALS))
//                .andCondition(CriteriaAPI.getCondition(module.getTableName() + ".PARENT_MODULE_ID", "parentModuleId", String.valueOf(parentModuleId), NumberOperators.EQUALS));


        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getSpaceCategoryFormRelationFields())
                .table(ModuleFactory.getSpaceBookingFormRelationModule().getTableName())
                .andCustomWhere("APP_ID = ?", appId)
                .andCustomWhere("CATEGORY_ID = ?", categoryId)
                .andCustomWhere("PARENT_MODULE_ID = ?", parentModuleId);



        List<Map<String, Object>> props = selectBuilder.get();

        if (props != null && !props.isEmpty()) {
            V3SpaceBookingFormRelationContext formrelation = FieldUtil.getAsBeanFromMap(props.get(0), V3SpaceBookingFormRelationContext.class);
            formId = formrelation.getModuleFormId();
        }
        return formId;
    }

    public static Long generatePolicyCriteriaId(Criteria criteria, String moduleName)throws Exception
    {
        if(criteria != null) {
            if (moduleName != null) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                for (String key : criteria.getConditions().keySet()) {
                    Condition condition = criteria.getConditions().get(key);
                    FacilioField field = modBean.getField(condition.getFieldName(), moduleName);
                    condition.setField(field);
                }
            }
            return CriteriaAPI.addCriteria(criteria, AccountUtil.getCurrentOrg().getId());
        }
        return -1l;
    }

    public static void updateSpaceBookingCancelState(List<V3SpaceBookingContext> bookings,String status) throws Exception {
        List<Long> ids= new ArrayList<>();
        for (V3SpaceBookingContext booking: bookings) {
            if(booking.getId() > 0) {
                ids.add(booking.getId());
            }
        }
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);
                List<FacilioField> updatedfields = new ArrayList<FacilioField>();
                FacilioStatus Status = TicketAPI.getStatus(module, status);
                Map<String, Object> updateMap = new HashMap<>();
                FacilioField statusField = modBean.getField("moduleState", module.getName());
                FacilioField isCancelled =modBean.getField("isCancelled", module.getName());
                updateMap.put("moduleState", FieldUtil.getAsProperties(Status));
                updateMap.put("isCancelled", true);
                updatedfields.add(statusField);
                updatedfields.add(isCancelled);
                UpdateRecordBuilder<V3SpaceBookingContext> updateBuilder = new UpdateRecordBuilder<V3SpaceBookingContext>()
                        .module(module)
                        .fields(updatedfields)
                        .andCondition(CriteriaAPI.getIdCondition(ids, module));
                updateBuilder.ignoreSplNullHandling();
                updateBuilder.updateViaMap(updateMap);
        }

    
}