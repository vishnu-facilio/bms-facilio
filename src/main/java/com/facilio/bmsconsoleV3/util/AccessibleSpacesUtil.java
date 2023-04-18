package com.facilio.bmsconsoleV3.util;

import com.amazonaws.util.StringUtils;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class AccessibleSpacesUtil {


    private static GenericSelectRecordBuilder getSelectBuilderForAccessibleSpace(Long ouId,Long peopleId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAccessibleSpaceModule().getTableName())
                .innerJoin("Resources")
                .on("Resources.ID = Accessible_Space.BS_ID");
        if(peopleId != null && peopleId > 0){
            selectBuilder.andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "peopleId", String.valueOf(peopleId), NumberOperators.EQUALS));
        } else if(ouId != null && ouId>0) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_USER_ID", "ouid", String.valueOf(ouId), NumberOperators.EQUALS));
        } else {
            throw new RESTException(ErrorCode.VALIDATION_ERROR,"peopleId or userId is mandatory to fetch accessible spaces");
        }
        return selectBuilder;
    }

    public static List<BaseSpaceContext> getAccessibleSpaceList(Long ouId, Long peopleId, int perPage, int offset, String searchQuery) throws Exception {
        GenericSelectRecordBuilder selectBuilder = getSelectBuilderForAccessibleSpace(ouId,peopleId);
        selectBuilder
                .select(AccountConstants.getAccessbileSpaceFields());

        if (perPage > 0 && offset >= 0) {
            selectBuilder.offset(offset);
            selectBuilder.limit(perPage);
        }

        if (!StringUtils.isNullOrEmpty(searchQuery)) {
            selectBuilder.andCriteria(getUserSearchCriteria(searchQuery));
        }

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<BaseSpaceContext> baseSpaces = new ArrayList<>();
            for (Map<String, Object> map : props) {
                Long baseSpaceId = (Long) map.get("bsid");
                if (baseSpaceId != null && baseSpaceId > 0) {
                    BaseSpaceContext bs = SpaceAPI.getBaseSpace(baseSpaceId);
                    baseSpaces.add(bs);
                }
            }
            return baseSpaces;
        }
        return null;
    }

    public static Long getAccessibleSpaceCount(Long ouId,Long peopleId, String searchQuery) throws Exception {
        GenericSelectRecordBuilder selectBuilder = getSelectBuilderForAccessibleSpace(ouId,peopleId);
        FacilioField bsid = FieldFactory.getAsMap(AccountConstants.getAccessbileSpaceFields()).get("bsid");
        selectBuilder
                .select(new HashSet<>())
                .groupBy("Accessible_Space.ORG_USER_ID")
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, bsid);


        if (!StringUtils.isNullOrEmpty(searchQuery)) {
            selectBuilder.andCriteria(getUserSearchCriteria(searchQuery));
        }

        List<Map<String, Object>> props = selectBuilder.get();
        if (props.size() > 0) {
            return (long) props.get(0).get("bsid");
        }
        return null;
    }

    private static Criteria getUserSearchCriteria(String searchQuery) {
        Criteria criteria = new Criteria();
        Condition condition_name = new Condition();
        condition_name.setColumnName("Resources.Name");
        condition_name.setFieldName("name");
        condition_name.setOperator(StringOperators.CONTAINS);
        condition_name.setValue(searchQuery);
        criteria.addOrCondition(condition_name);

        return criteria;
    }

    public static void addAccessibleSpace(Long uid, Long peopleId, List<Long> accessibleSpace) throws Exception {

        Long existingSpaces = AccessibleSpacesUtil.getAccessibleSpaceCount(uid,peopleId,null);
        if(existingSpaces != null && accessibleSpace.size() > (100L - existingSpaces)){
            throw new RESTException(ErrorCode.VALIDATION_ERROR,"Accessible spaces addition cancelled - breach of max limit");
        }

        if(uid != null && (peopleId == null || peopleId <= 0)){
            User user = AccountUtil.getUserBean().getUser(uid,true);
            if(user != null) {
                peopleId = user.getPeopleId();
            }
        } else if(peopleId !=null && (uid == null || uid <= 0)) {
            List<Long> ouids = PeopleAPI.getUserIdForPeople(peopleId);
            if(CollectionUtils.isNotEmpty(ouids)){
                uid = ouids.get(0);
            } else {
                throw new RESTException(ErrorCode.VALIDATION_ERROR,"This record is not an active app/portal user");
            }
        }

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getAccessibleSpaceModule().getTableName())
                .fields(AccountConstants.getAccessbileSpaceFields());

        Map<Long, BaseSpaceContext> idVsBaseSpace = SpaceAPI.getBaseSpaceMap(accessibleSpace);

        for (Long bsid : accessibleSpace) {
            Map<String, Object> props = new HashMap<>();
            props.put("ouid", uid);
            props.put("peopleId",peopleId);
            props.put("bsid", bsid);
            props.put("siteId", getParentSiteId(bsid, idVsBaseSpace));
            insertBuilder.addRecord(props);
        }
        insertBuilder.save();

    }

    private static long getParentSiteId(long baseSpaceId, Map<Long, BaseSpaceContext> idVsBaseSpace) {
        BaseSpaceContext baseSpace = idVsBaseSpace.get(baseSpaceId);
        if (baseSpace.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SITE) {
            return baseSpace.getId();
        }
        return baseSpace.getSiteId();
    }
}
