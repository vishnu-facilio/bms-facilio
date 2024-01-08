package com.facilio.bmsconsoleV3.actions.picklist;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Arrays;
import java.util.List;


@Getter
@Setter
public class ReadingPicklistAction extends V3PIckListAction {

    public String readingModulePicklist() throws Exception {
        FacilioContext pickListContext =  new FacilioContext();
        pickListContext.put(FacilioConstants.ContextNames.IS_TO_FETCH_DECOMMISSIONED_RESOURCE, BooleanUtils.isTrue(isToFetchDecommissionedResource));
        pickListContext.put(FacilioConstants.ContextNames.FETCH_CONNECTED_ASSETS,fetchConnectedAssets);
        pickListContext.put(FacilioConstants.ContextNames.FIELD_IDS,fieldIds);

        PickListUtil.populatePicklistContext(pickListContext, getModuleName(), getFilters(), getSearch(), getCriteria(), getClientCriteria(), getDefault(), getViewName(), getPage(), getPerPage());
        pickListContext = PickListUtil.fetchPickListData(pickListContext);

        setData(FacilioConstants.ContextNames.PICKLIST, pickListContext.get(FacilioConstants.ContextNames.PICKLIST));
        setMeta("moduleType", ((FacilioModule)pickListContext.get(FacilioConstants.ContextNames.MODULE)).getTypeEnum().name());
        setMeta("localSearch", pickListContext.getOrDefault(FacilioConstants.PickList.LOCAL_SEARCH, true));

        return SUCCESS;
    }

    public String readingModuleCount() throws Exception {

        FacilioChain dataList = ReadOnlyChainFactory.fetchModuleDataListChain();
        FacilioContext context = dataList.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.FETCH_CONNECTED_ASSETS,fetchConnectedAssets);
        context.put(FacilioConstants.ContextNames.FIELD_IDS,fieldIds);
        context.put(FacilioConstants.ContextNames.FETCH_COUNT,true);
        dataList.execute();
        setData(FacilioConstants.ContextNames.COUNT, context.get(FacilioConstants.ContextNames.RECORD_COUNT));

        return SUCCESS;
    }


    private List<Long> fieldIds;
    private boolean fetchConnectedAssets;

}
