package com.facilio.bmsconsoleV3.commands.vendor.multi_import;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.constants.ImportConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeforeVendorImportProcessCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> batchCollectMap = ImportConstants.getBatchCollectMap(context);
        if(MapUtils.isEmpty(batchCollectMap)){
            return false;
        }

        Set<String> emails = (Set<String>) batchCollectMap.get("primaryContactEmail");

        if(CollectionUtils.isNotEmpty(emails)){
            List<V3PeopleContext> peopleContextList = V3PeopleAPI.getPeoples(emails);
            if(CollectionUtils.isNotEmpty(peopleContextList)){
                Map<String,V3PeopleContext> emailVsPeople = peopleContextList.stream().collect(Collectors.toMap(V3PeopleContext::getEmail, Function.identity(),(a, b)->a));
                context.put(FacilioConstants.ContextNames.EMAIL_VS_PEOPLE_MAP,emailVsPeople);
            }
        }
        return false;
    }
}
