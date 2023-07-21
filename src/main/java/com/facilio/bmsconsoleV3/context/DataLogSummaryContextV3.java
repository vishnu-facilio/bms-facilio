package com.facilio.bmsconsoleV3.context;


import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;

@Getter@Setter
public class DataLogSummaryContextV3 extends V3Context{

//    private long orgId = -1L;
//    private long moduleId = -1L;
//    private long id = -1L;
    private long recordId;
    private long controllerId;
    private String point;
    private int status;
    private String value;
    private long parentId;
    private String errorStackTrace;
    private long readingId = -1L;

    public void setValue(Object value){
      this.value =  String.valueOf(value);
    }
    public String getValue(){
        return value;
    }


    private long assetId = -1L;
    public long getAssetId(){return assetId;}
    private AssetContext asset;
    private String assetName;
    public void setAssetId(long assetId){
        AssetContext asset = new AssetContext();
        this.asset = asset;
        asset.setId(assetId);
        this.assetId = assetId;
    }
    public String getAssetName() {
        return assetName;
    }
    @JSON(serialize = false)
    public AssetContext getAsset(){return asset;}
    public void setAsset(AssetContext asset){
        this.asset = asset;
        if(asset!=null){
            this.assetName = asset.getName();
            this.assetId = asset.getId();
        }
    }



    public static enum TimeSeries_Status implements FacilioIntEnum {
        FAILURE(1,"Failure"),
        SUCCESS(2,"Success");
        private int key;
        private String label;
        TimeSeries_Status(int key,String label) {
            this.key = key;
            this.label = label;
        }

        public int getKey() {
            return key;
        }

        public  String getLabel() {
            return label;
        }


    }
}
