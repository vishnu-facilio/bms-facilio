package com.facilio.bmsconsole.homepage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class HomePage {


    private long id;
    private long orgId;
    private long appId;
    private String linkName;
    private String displayName;

    private Long createdTime;

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }



    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    private List<Section> sections;
    public List<Section> getSections() {
        return sections;
    }
    public void setSections(List<Section> sections) {
        this.sections = sections;
    }
    public void addSection(Section section, Integer...index) {
        if (sections == null) {
            sections = new ArrayList<>();
        }
        if (index != null && index.length > 0) {
            sections.add(index[0], section);
        }
        sections.add(section);
    }

    private List<HomePageWidget> widgets;
    public List<HomePageWidget> getWidgets() {
        return widgets;
    }
    public void setWidgets(List<HomePageWidget> widgets) {
        this.widgets = widgets;
    }
    public void addWidget(HomePageWidget widget) {
        if (this.widgets == null) {
            this.widgets = new ArrayList<>();
        }
        this.widgets.add(widget);
    }

    public static class Section {

        public Section(){}

        public Section(String name) {
            this.name = name;
        }

        @Getter @Setter
        private long orgId = -1;
        @Getter @Setter
        private long id = -1;
        @Getter @Setter
        private String name;

        @Getter @Setter
        private long homePageId = -1;


        private List<HomePageWidget> widgets;
        public List<HomePageWidget> getWidgets() {
            return widgets;
        }
        public void setWidgets(List<HomePageWidget> widgets) {
            this.widgets = widgets;
        }
        public void addWidget(HomePageWidget widget) {
            if (this.widgets == null) {
                this.widgets = new ArrayList<>();
            }
            this.widgets.add(widget);
        }

        @Getter @Setter
        private int x = 0;
        @Getter @Setter
        private int y = 0;
        @Getter @Setter
        private int w = 12;
        @Getter @Setter
        private int h = 0;

        @JsonIgnore
        public void setXY(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }


}