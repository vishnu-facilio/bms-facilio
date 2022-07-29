package com.facilio.bmsconsoleV3.actions.readingimportapp;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.readingimportapp.V3ReadingImportAppContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


@Log4j
@Setter @Getter
public class ReadingImportAction extends V3Action{

    @Setter @Getter
    V3ReadingImportAppContext readingImportData;

    @Setter @Getter
    long id;

    private JSONObject data;

    private int page;
    private int perPage;
    private String filters;
    private String moduleName;
   // private boolean withCount;

    public JSONObject getData() {
        return this.data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public void setData(String key, Object result) {
        if (this.data == null) {
            this.data = new JSONObject();
        }
        this.data.put(key, result);
    }

    public String addImportMeta() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getReadingImportAppChain();
        FacilioContext context = chain.getContext();

        context.put("READING_IMPORT_CONTEXT", getReadingImportData());
        chain.execute();

        setData("readingimport", context.get("READING_IMPORT_CONTEXT"));


        return V3Action.SUCCESS;
    }

    public String getImportMetaById() throws Exception {

        FacilioChain chain = ReadOnlyChainFactoryV3.getReadingImportData();
        FacilioContext context = chain.getContext();

        context.put("READING_IMPORT_CONTEXT", getReadingImportData());
        chain.execute();

        setData("readingimport", context.get("READING_IMPORT_DATA"));

        return V3Action.SUCCESS;
    }

    public String getImportMetaList() throws Exception {

        FacilioChain chain = ReadOnlyChainFactoryV3.getReadingImportDataList();
        FacilioContext context = chain.getContext();

        JSONObject pagination = new JSONObject();
        pagination.put("page", this.getPage());
        pagination.put("perPage", this.getPerPage());
        pagination.put("withCount",this.getWithCount());

        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,this.getModuleName());

        if (filters != null && !filters.isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(filters);
            context.put(FacilioConstants.ContextNames.FILTERS, json);
        }
        chain.execute();

        setData("readingimport", context.get("READING_IMPORT_DATA_LIST"));
        setData("count",context.get("count"));

        return V3Action.SUCCESS;
    }
    public String getMyImportMetaList() throws Exception {

        FacilioChain chain = ReadOnlyChainFactoryV3.getMyReadingImportDataList();
        FacilioContext context = chain.getContext();

        JSONObject pagination = new JSONObject();
        pagination.put("page", this.getPage());
        pagination.put("perPage", this.getPerPage());
        pagination.put("withCount",this.getWithCount());

        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,this.getModuleName());

        if (filters != null && !filters.isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(filters);
            context.put(FacilioConstants.ContextNames.FILTERS, json);
        }
        chain.execute();

        setData("readingimport", context.get("READING_IMPORT_DATA_LIST"));
        setData("count",context.get("count"));

        return V3Action.SUCCESS;
    }

    public String updateReadingImportData() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.updateReadingImportChain();
        FacilioContext context = chain.getContext();

        context.put("READING_IMPORT_CONTEXT", getReadingImportData());
        chain.execute();

        setData("readingimport", context.get("READING_IMPORT_DATA"));

        return V3Action.SUCCESS;
    }

    public String deleteReadingImportData() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.deleteReadingImportChain();
        FacilioContext context = chain.getContext();

        context.put("READING_IMPORT_CONTEXT", getReadingImportData());
        chain.execute();

        return V3Action.SUCCESS;
    }



}
