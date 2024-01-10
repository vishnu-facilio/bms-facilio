package com.facilio.bmsconsoleV3.context.dashboard;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardFolderContext;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class DashboardListPropsContext
{
        public Long appId;
        public boolean withSharing;
        public boolean withFilters;
        public boolean withEmptyFolders;
        public boolean onlyPublished;
        public boolean onlyMobile;
        public boolean onlySelected;
        public boolean onlyFolders;
        public boolean withTabs;
        public boolean isMigrationDone;
        public boolean newFlow;

        public List<DashboardContext> dashboards = new ArrayList<>();
        public List<Long> published_dashboard_ids = new ArrayList<>();
        public List<Long> mobile_dashboard_ids = new ArrayList<>();
        public List<Long> all_dashboard_ids = new ArrayList<>();
        public List<DashboardFolderContext> folders = new ArrayList<>();
        public List<Long> folder_ids = new ArrayList<>();

        public  DashboardListPropsContext(Long appId, boolean withSharing, boolean withFilters, boolean withEmptyFolders,
               boolean onlyPublished, boolean onlyMobile, boolean onlySelected, boolean onlyFolders, boolean withTabs, boolean newFlow )throws Exception
        {
                this.appId = appId;
                this.withSharing = withSharing;
                this.withFilters = withFilters;
                this.withEmptyFolders = withEmptyFolders;
                this.onlyPublished = onlyPublished;
                this.onlyMobile = onlyMobile;
                this.onlySelected = onlySelected;
                this.onlyFolders = onlyFolders;
                this.withTabs = withTabs;
                this.newFlow = newFlow;
        }
}
