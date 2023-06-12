package com.facilio.bmsconsole.context;

import com.facilio.db.criteria.Criteria;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModulePages {
    @Getter
    List<PagesContext> customPages;

    public PagesContext addPage(String name, String displayName, String description, Criteria criteria, Boolean isTemplate, Boolean isDefaultPage, Boolean status) {
        PagesContext customPage = new PagesContext(name, displayName, description, criteria, isTemplate, isDefaultPage, status);
        if(this.customPages == null) {
            this.customPages = new ArrayList<>(Arrays.asList(customPage));
        }
        else {
            this.customPages.add(customPage);
        }
        customPage.setParentContext(this);
        return customPage;
    }



}
