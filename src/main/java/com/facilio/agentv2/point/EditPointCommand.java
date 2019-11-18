package com.facilio.agentv2.point;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class EditPointCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if( containdAndNotNull(context,FacilioConstants.ContextNames.TO_UPDATE_MAP) ){
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getPointModule().getTableName())
                    .fields(FieldFactory.getPointFields());
            if( containdAndNotNull(context,FacilioConstants.ContextNames.CRITERIA) ){
                builder.andCriteria((Criteria) context.get(FacilioConstants.ContextNames.CRITERIA));
            }else {
                throw new Exception("criteris missing");
            }
        }else {
            throw new Exception(" to-update-map missing");
        }
        return false;
    }

    private static boolean containdAndNotNull(Context context, String key){
        return (context.containsKey(key) && (context.get(key) != null) );
    }
}
