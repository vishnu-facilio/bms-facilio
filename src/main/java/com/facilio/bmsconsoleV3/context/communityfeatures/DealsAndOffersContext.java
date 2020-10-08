package com.facilio.bmsconsoleV3.context.communityfeatures;

import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class DealsAndOffersContext extends V3Context {

    private String title;
    private String description;
    private NeighbourhoodContext neighbourhood;
    private Long expiryDate;
    private Long startDate;
    private Boolean active;
    private List<CommunitySharingInfoContext> dealsandofferssharing;
    private String dealer;

    public List<CommunitySharingInfoContext> getDealsandofferssharing() {
        return dealsandofferssharing;
    }

    public void setDealsandofferssharing(List<CommunitySharingInfoContext> dealsandofferssharing) {
        this.dealsandofferssharing = dealsandofferssharing;
    }


    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean isActive() {
        if (active != null) {
            return active;
        }
        return false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public NeighbourhoodContext getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(NeighbourhoodContext neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    private AudienceContext audience;

    public AudienceContext getAudience() {
        return audience;
    }

    public void setAudience(AudienceContext audience) {
        this.audience = audience;
    }
}
