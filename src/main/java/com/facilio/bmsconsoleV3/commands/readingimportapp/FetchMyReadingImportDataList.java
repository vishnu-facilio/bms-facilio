package com.facilio.bmsconsoleV3.commands.readingimportapp;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.readingimportapp.V3ReadingImportAppContext;
import com.facilio.command.FacilioCommand;
import com.facilio.accounts.dto.User;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchMyReadingImportDataList extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getReadingImportFields())
                .table(ModuleFactory.getReadingImportAPPModule().getTableName());

        selectBuilder.andCondition(CriteriaAPI.getCondition("Reading_Import_APP.CREATED_BY", "createdBy", String.valueOf(AccountUtil.getCurrentUser().getOuid()), NumberOperators.EQUALS));
        selectBuilder.orderBy("CREATED_TIME  desc");


        Map<Long, User> orgUsers = AccountUtil.getOrgBean().getOrgUsersAsMap(AccountUtil.getCurrentOrg().getOrgId());


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
        return false;
    }
}
