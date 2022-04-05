package com.facilio.bmsconsoleV3.commands.accessibleSpaces;

import com.amazonaws.util.StringUtils;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
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
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FetchAccessibleSpacesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long ouId = (Long)context.get(FacilioConstants.ContextNames.ORG_USER_ID);
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        String searchQuery = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        Boolean fetchCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
        int page, perPage = -1, offset = -1;
        if(pagination != null){
            page = (int) pagination.get("page");
            perPage = (int) pagination.get("perPage");
            offset = (page - 1) * perPage;

            if(offset < 0) {
                offset = 0;
            }
        }
        List<BaseSpaceContext> baseSpaces = null;
       if(ouId != null && ouId > 0) {
           baseSpaces = getAccessibleSpaceList(ouId,perPage,offset,searchQuery);
       }
       if(fetchCount != null){
           Long baseSpacesCount = getAccessibleSpaceCount(ouId,searchQuery);
           context.put(FacilioConstants.ContextNames.COUNT,baseSpacesCount);
       }
       context.put(FacilioConstants.ContextNames.ACCESSIBLE_SPACE, baseSpaces);
       return false;
    }

    private GenericSelectRecordBuilder getSelectBuilderForAccessibleSpace(Long ouId){
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAccessibleSpaceModule().getTableName())
                .innerJoin("Resources")
                .on("Resources.ID = Accessible_Space.BS_ID")
                .andCondition(CriteriaAPI.getCondition("ORG_USER_ID", "ouid", String.valueOf(ouId), NumberOperators.EQUALS));

        return selectBuilder;
    }

    private List<BaseSpaceContext> getAccessibleSpaceList(Long ouId,int perPage,int offset,String searchQuery) throws Exception {
        GenericSelectRecordBuilder selectBuilder = getSelectBuilderForAccessibleSpace(ouId);
        selectBuilder
                .select(AccountConstants.getAccessbileSpaceFields());

        return selectBuilder;
    }

    private List<BaseSpaceContext> getAccessibleSpaceList(Long ouId,int perPage,int offset,String searchQuery) throws Exception {
        GenericSelectRecordBuilder selectBuilder = getSelectBuilderForAccessibleSpace(ouId);
        selectBuilder
                .select(AccountConstants.getAccessbileSpaceFields());

        if(perPage > 0 && offset >= 0) {
            selectBuilder.offset(offset);
            selectBuilder.limit(perPage);
        }

        if(!StringUtils.isNullOrEmpty(searchQuery)){
            selectBuilder.andCriteria(getUserSearchCriteria(searchQuery));
        }

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<BaseSpaceContext> baseSpaces = new ArrayList<>();
            for(Map<String, Object> map :props) {
                Long baseSpaceId = (Long)map.get("bsid");
                if(baseSpaceId != null && baseSpaceId > 0) {
                    BaseSpaceContext bs = SpaceAPI.getBaseSpace(baseSpaceId);
                    baseSpaces.add(bs);
                }
            }
            return baseSpaces;
        }
        return null;
    }

    private Long getAccessibleSpaceCount(Long ouId,String searchQuery) throws Exception {
        GenericSelectRecordBuilder selectBuilder = getSelectBuilderForAccessibleSpace(ouId);
        FacilioField bsid  = FieldFactory.getAsMap(AccountConstants.getAccessbileSpaceFields()).get("bsid");
        selectBuilder
                .select(new HashSet<>())
                .groupBy("Accessible_Space.ORG_USER_ID")
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, bsid);


        if(!StringUtils.isNullOrEmpty(searchQuery)){
            selectBuilder.andCriteria(getUserSearchCriteria(searchQuery));
        }

        List<Map<String, Object>> props = selectBuilder.get();
        if (props.size() > 0){
            return (long) props.get(0).get("bsid");
        }
        return null;
    }

     private Criteria getUserSearchCriteria(String searchQuery)
    {
        Criteria criteria = new Criteria();
        Condition condition_name = new Condition();
        condition_name.setColumnName("Resources.Name");
        condition_name.setFieldName("name");
        condition_name.setOperator(StringOperators.CONTAINS);
        condition_name.setValue(searchQuery);
        criteria.addOrCondition(condition_name);

    private Long getAccessibleSpaceCount(Long ouId,String searchQuery) throws Exception {
        GenericSelectRecordBuilder selectBuilder = getSelectBuilderForAccessibleSpace(ouId);
        FacilioField bsid  = FieldFactory.getAsMap(AccountConstants.getAccessbileSpaceFields()).get("bsid");
        selectBuilder
                .select(new HashSet<>())
                .groupBy("Accessible_Space.ORG_USER_ID")
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, bsid);


        if(!StringUtils.isNullOrEmpty(searchQuery)){
            selectBuilder.andCriteria(getUserSearchCriteria(searchQuery));
        }

        List<Map<String, Object>> props = selectBuilder.get();
        if (props.size() > 0){
            return (long) props.get(0).get("bsid");
        }
        return null;
    }

     private Criteria getUserSearchCriteria(String searchQuery)
    {
        Criteria criteria = new Criteria();
        Condition condition_name = new Condition();
        condition_name.setColumnName("Resources.Name");
        condition_name.setFieldName("name");
        condition_name.setOperator(StringOperators.CONTAINS);
        condition_name.setValue(searchQuery);
        criteria.addOrCondition(condition_name);

        return criteria;
    }
}
