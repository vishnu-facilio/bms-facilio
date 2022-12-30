package com.facilio.attribute.command;

import com.facilio.classification.util.ClassificationUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class ClassificationAttributeListFilterCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Condition condition;
        Criteria criteria = new Criteria();

        if (Constants.containsQueryParam(context, "ignoreClassificationId")) {
            Long classificationId = FacilioUtil.parseLong(Constants.getQueryParam(context, "ignoreClassificationId"));

           Set<Long> ignoreAttributeIds= ClassificationUtil.getClassificationRelatedAttributesIds(classificationId);
           if(CollectionUtils.isNotEmpty(ignoreAttributeIds)){
               condition= CriteriaAPI.getCondition("Classification_Attribute.ID","id", StringUtils.join(ignoreAttributeIds,","), NumberOperators.NOT_EQUALS);
               criteria.addAndCondition(condition);
           }
        }

        context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, criteria.isEmpty() ? null : criteria);
        return false;
    }
}
