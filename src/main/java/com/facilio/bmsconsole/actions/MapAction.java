package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;

import java.util.List;

import com.facilio.constants.FacilioConstants;
import org.json.simple.JSONObject;

public class MapAction extends FacilioAction {

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String siteAlarmList() throws Exception {
        FacilioChain c = ReadOnlyChainFactory.getSiteAlarmList();
        FacilioContext context = c.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

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

    private List<JSONObject> readingModuleFieldList;
    
    public List<JSONObject> getReadingModuleFieldList() {
		return readingModuleFieldList;
	}
	public void setReadingModuleFieldList(List<JSONObject> readingModuleFieldList) {
		this.readingModuleFieldList = readingModuleFieldList;
	}

    public String assetAlarmList() throws Exception {
        FacilioChain c = ReadOnlyChainFactory.getAssetAlarmList();
        FacilioContext context = c.getContext();
        context.put("assetIds", getIds());
        context.put("readingModuleFieldList", readingModuleFieldList);
        c.execute();

        setResult("assetAlarmCount", context.get("assetAlarmCount"));
        setResult("assetReadings", context.get("assetReadings"));
        return SUCCESS;
    }
}
