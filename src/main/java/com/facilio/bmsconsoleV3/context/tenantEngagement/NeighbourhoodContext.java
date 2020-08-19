package com.facilio.bmsconsoleV3.context.tenantEngagement;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class NeighbourhoodContext extends V3Context {

    private String title;
    private String description;
    private Integer category;
    private LocationContext location;
    private Long activeDealsCount;
    private List<NeighbourhoodSharingContext> neighbourhoodsharing;

    public List<NeighbourhoodSharingContext> getNeighbourhoodsharing() {
        return neighbourhoodsharing;
    }

    public void setNeighbourhoodsharing(List<NeighbourhoodSharingContext> neighbourhoodsharing) {
        this.neighbourhoodsharing = neighbourhoodsharing;
    }

    public Long getActiveDealsCount() {
        return activeDealsCount;
    }

    public void setActiveDealsCount(Long activeDealsCount) {
        this.activeDealsCount = activeDealsCount;
    }

    public LocationContext getLocation() {
        return location;
    }

    public void setLocation(LocationContext location) {
        this.location = location;
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

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }



}
