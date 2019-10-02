package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;

import java.util.List;

public class MapAction extends FacilioAction {

    public String siteAlarmList() throws Exception {
        FacilioChain c = ReadOnlyChainFactory.getSiteAlarmList();
        FacilioContext context = c.getContext();

        c.execute();

        setResult("siteAlarmCount", context.get("siteAlarmCount"));
        return SUCCESS;
    }

    private List<Long> ids;
    public List<Long> getIds() {
        return ids;
    }
    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    private String readingModuleName;
    public String getReadingModuleName() {
        return readingModuleName;
    }
    public void setReadingModuleName(String readingModuleName) {
        this.readingModuleName = readingModuleName;
    }

    private List<String> readingFieldNames;
    public List<String> getReadingFieldNames() {
        return readingFieldNames;
    }
    public void setReadingFieldNames(List<String> readingFieldNames) {
        this.readingFieldNames = readingFieldNames;
    }

    public String assetAlarmList() throws Exception {
        FacilioChain c = ReadOnlyChainFactory.getAssetAlarmList();
        FacilioContext context = c.getContext();
        context.put("assetIds", getIds());
        context.put("readingModule", readingModuleName);
        context.put("readingFields", readingFieldNames);
        c.execute();

        setResult("assetAlarmCount", context.get("assetAlarmCount"));
        setResult("assetReadings", context.get("assetReadings"));
        return SUCCESS;
    }
}
