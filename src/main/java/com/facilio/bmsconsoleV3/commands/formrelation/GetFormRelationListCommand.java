package com.facilio.bmsconsoleV3.commands.formrelation;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.readingimportapp.V3ReadingImportAppContext;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingFormRelationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetFormRelationListCommand  extends FacilioCommand {


    @Override
    public boolean executeCommand(Context context) throws Exception {

        GenericSelectRecordBuilder selectBuilder = select(context);



        List<Map<String, Object>> props = selectBuilder.get();
        List<V3SpaceBookingFormRelationContext> readingDataList = new ArrayList<>();
        if (props != null && !props.isEmpty()) {
            for(Map<String, Object> prop : props) {
                V3SpaceBookingFormRelationContext relationContext = FieldUtil.getAsBeanFromMap(prop, V3SpaceBookingFormRelationContext.class);

                readingDataList.add(relationContext);
            }
            context.put("FORM_RELATION_CONTEXT", readingDataList);
        }
        return false;
    }
    public GenericSelectRecordBuilder select(Context context){

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getSpaceCategoryFormRelationFields())
                .table(ModuleFactory.getSpaceBookingFormRelationModule().getTableName());

        return selectBuilder;
    }
}