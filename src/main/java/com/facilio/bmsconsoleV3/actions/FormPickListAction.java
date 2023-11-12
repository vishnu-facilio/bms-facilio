package com.facilio.bmsconsoleV3.actions;


import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsoleV3.actions.picklist.PickListUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.RESTAPIHandler;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Getter@Setter
public class FormPickListAction extends RESTAPIHandler {
    private String fieldName;
    private String extendedModuleName;
    private String relMappingName;

    public String getPickListData() throws Exception{

        FacilioChain chain = ReadOnlyChainFactory.getFormsPickListChain();
        FacilioContext context=new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME,this.getFieldName());
        context.put(FacilioConstants.ContextNames.MODULE_NAME,this.getModuleName());
        context.put(FacilioConstants.ContextNames.EXTENDED_MODULE_NAME ,extendedModuleName);

        JSONObject pagination = new JSONObject();
        pagination.put("page", this.getPage());
        pagination.put("perPage", this.getPerPage());

        context.put(FacilioConstants.ContextNames.PAGINATION,pagination);
        context.put(FacilioConstants.ContextNames.SEARCH,this.getSearch());
        context.put(FacilioConstants.ContextNames.FILTERS,this.getFilters());
        context.put(FacilioConstants.ContextNames.DEFAULT,this.getDefault());
        context.put(FacilioConstants.ContextNames.EXCLUDE_PARENT_FILTER,this.getExcludeParentFilter());
        context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA,this.getClientCriteria());
        context.put(FacilioConstants.ContextNames.ORDER_BY,this.getOrderBy());
        context.put(FacilioConstants.ContextNames.ORDER_TYPE,this.getOrderType());
        context.put(FacilioConstants.ContextNames.WITH_COUNT,this.getWithCount());
        context.put(FacilioConstants.ContextNames.QUERY_PARAMS,this.getQueryParameters());
        chain.execute(context);


        this.setData(FacilioConstants.ContextNames.PICKLIST,context.get(FacilioConstants.ContextNames.PICKLIST));

        if((boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_SPECIAL_MODULE,false)){
            this.setMeta(FacilioConstants.ContextNames.MODULE_TYPE,context.get(FacilioConstants.ContextNames.MODULE_TYPE));
            this.setMeta(FacilioConstants.PickList.LOCAL_SEARCH, context.get(FacilioConstants.PickList.LOCAL_SEARCH));
        }
        else{
           JSONObject meta= (JSONObject) context.getOrDefault(FacilioConstants.ContextNames.META,null);
           if(meta!=null){
               this.setMeta(meta);
           }
        }
        return SUCCESS;
    }

    public String getRelationshipPickListData() throws Exception{

        FacilioChain chain = ReadOnlyChainFactory.getFormsRelationPickListChain();
        FacilioContext context=new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,this.getModuleName());
        context.put(FacilioConstants.ContextNames.RELATIONSHIP, relMappingName);

        addParamsToContext(context);
        chain.execute(context);


        this.setData(FacilioConstants.ContextNames.PICKLIST,context.get(FacilioConstants.ContextNames.PICKLIST));

        if((boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_SPECIAL_MODULE,false)){
            this.setMeta(FacilioConstants.ContextNames.MODULE_TYPE,context.get(FacilioConstants.ContextNames.MODULE_TYPE));
            this.setMeta(FacilioConstants.PickList.LOCAL_SEARCH, context.get(FacilioConstants.PickList.LOCAL_SEARCH));
        }
        else{
            JSONObject meta= (JSONObject) context.getOrDefault(FacilioConstants.ContextNames.META,null);
            if(meta!=null){
                this.setMeta(meta);
            }
        }
        return SUCCESS;
    }

    public void addParamsToContext(FacilioContext context) throws Exception {
        JSONObject pagination = new JSONObject();
        pagination.put("page", this.getPage());
        pagination.put("perPage", this.getPerPage());

        context.put(FacilioConstants.ContextNames.PAGINATION,pagination);
        context.put(FacilioConstants.ContextNames.SEARCH,this.getSearch());
        context.put(FacilioConstants.ContextNames.FILTERS,this.getFilters());
        context.put(FacilioConstants.ContextNames.DEFAULT,this.getDefault());
        context.put(FacilioConstants.ContextNames.EXCLUDE_PARENT_FILTER,this.getExcludeParentFilter());
        context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA,this.getClientCriteria());
        context.put(FacilioConstants.ContextNames.ORDER_BY,this.getOrderBy());
        context.put(FacilioConstants.ContextNames.ORDER_TYPE,this.getOrderType());
        context.put(FacilioConstants.ContextNames.WITH_COUNT,this.getWithCount());
        context.put(FacilioConstants.ContextNames.QUERY_PARAMS,this.getQueryParameters());
    }

}
