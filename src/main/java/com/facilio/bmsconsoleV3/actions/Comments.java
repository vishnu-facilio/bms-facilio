package com.facilio.bmsconsoleV3.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.CommentSharingContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class Comments extends V3Action {

    private List<CommentSharingContext> commentSharingPreferences;

    public String getSharingOptions() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getCommentSharingOptionsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ORGID, AccountUtil.getCurrentOrg().getId());
        chain.execute();
        setData(FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.COMMENT_SHARING_OPTIONS)));
        return V3Action.SUCCESS;
    }

    public String updateSharingPreferences() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getCommentSharingPreferencesUpdateChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.COMMENT_SHARING_PREFERENCES,commentSharingPreferences);
        chain.execute();
        return V3Action.SUCCESS;
    }


    public String clearSharingPreferences() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getClearCommentSharingPreferencesUpdateChain();
        chain.execute();
        return V3Action.SUCCESS;
    }
}
