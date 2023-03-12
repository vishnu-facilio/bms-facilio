package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.util.V3SiteQRDownloadAPI;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class V3SiteQRDownloadAction extends V3Action {

    public Long getSiteId() {
        if (siteId == null){
            return -1L;
        }
        return siteId;
    }
    private Long siteId;

    public String siteQrDownloadUrl() throws Exception {

        String downloadURL = V3SiteQRDownloadAPI.getSiteQRDownloadURL(getSiteId());
        setData("downloadURL",downloadURL);
        return SUCCESS;
    }
}
