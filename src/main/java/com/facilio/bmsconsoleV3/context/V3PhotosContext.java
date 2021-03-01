package com.facilio.bmsconsoleV3.context;

import com.facilio.services.factory.FacilioFactory;
import com.facilio.v3.context.V3Context;

public class V3PhotosContext extends V3Context {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Long parentId;
    public Long getParentId() {
        return parentId;
    }
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    private Long photoId;
    public Long getPhotoId() {
        return photoId;
    }
    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }
    public String getUrl() throws Exception {

      if (this.photoId > 0) {
            return FacilioFactory.getFileStore().getPrivateUrl(photoId);
        }
        return null;
    }

    public String getOriginalUrl() throws Exception {
        return getUrl();
    }
    private Long ttime;
    public Long getTtime() {
        return ttime;
    }
    public void setTtime(long ttime) {
        this.ttime = ttime;
    }
}
