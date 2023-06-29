package com.facilio.bmsconsoleV3.commands.readingimportapp;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.readingimportapp.V3ReadingImportAppContext;
import com.facilio.command.FacilioCommand;
import com.facilio.accounts.dto.User;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchReadingImportDataList extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        GenericSelectRecordBuilder selectBuilder = select(context);

        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);

        int perPage;
        int page;
        boolean withCount = false;
        if (pagination != null) {
            page = (int) (pagination.get("page") == null ? 1 : pagination.get("page"));
            perPage = (int) (pagination.get("perPage") == null ? 50 : pagination.get("perPage"));
            withCount = (boolean) pagination.get("withCount");
        } else {
            page = 1;
            perPage = 50;
        }
        int offset = ((page-1) * perPage);
        if (offset < 0) {
            offset = 0;
        }
        selectBuilder.offset(offset);
        selectBuilder.limit(perPage);

        Criteria filterCriteria = (Criteria) context.get(Constants.FILTER_CRITERIA);
        if(filterCriteria != null) {
            selectBuilder.andCriteria(filterCriteria);
        }

        selectBuilder.orderBy("CREATED_TIME  desc");

        Map<Long, User> orgUsers = AccountUtil.getOrgBean().getOrgUsersAsMap(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.ApplicationLinkNames.DATA_LOADER_APP);


        List<Map<String, Object>> props = selectBuilder.get();
        List<V3ReadingImportAppContext> readingDataList = new ArrayList<>();
        if (props != null && !props.isEmpty()) {
                for(Map<String, Object> prop : props) {
                    V3ReadingImportAppContext readingData = FieldUtil.getAsBeanFromMap(prop, V3ReadingImportAppContext.class);
                    if (readingData.getCreatedBy() != null) {
                        User createdUser = orgUsers.get(readingData.getCreatedBy());
                        User modifiedUser = orgUsers.get(readingData.getCreatedBy());

                        if (createdUser != null) {
                            readingData.setCreatedUser(createdUser);
                        }
                        if (modifiedUser != null) {
                            readingData.setModifiedUser(modifiedUser);
                        }

                    }
                    readingDataList.add(readingData);
                }
            context.put("READING_IMPORT_DATA_LIST", readingDataList);
        }
        if(withCount){
            GenericSelectRecordBuilder countSelectBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getCountField())
                    .table(ModuleFactory.getReadingImportAPPModule().getTableName());
            List<Map<String,Object>> countRecord = countSelectBuilder.get();
            if(CollectionUtils.isNotEmpty(countRecord)){
                context.put("count", countRecord.get(0).get("count"));
            }
        }
        return false;
    }
    public GenericSelectRecordBuilder select(Context context){

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getReadingImportFields())
                .table(ModuleFactory.getReadingImportAPPModule().getTableName());

        Criteria filterCriteria = (Criteria) context.get(Constants.FILTER_CRITERIA);
        if(filterCriteria != null) {
            selectBuilder.andCriteria(filterCriteria);
        }

//        selectBuilder.andCondition(CriteriaAPI.getCondition("Reading_Import_APP.CREATED_BY", "createdBy", String.valueOf(AccountUtil.getCurrentUser().getOuid()), NumberOperators.EQUALS));
        selectBuilder.orderBy("CREATED_TIME  desc");

        return selectBuilder;
    }
}
