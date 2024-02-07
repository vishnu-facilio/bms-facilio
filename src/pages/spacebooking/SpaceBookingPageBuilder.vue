<template>
  <div class="f-page" :key="id">
    <div v-if="isLoading" class="text-center width100 pT50 mT50">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div v-else-if="page" class="width100 p-relative">
      <div class="p-absolute">
        <portal-target name="summary-btn"></portal-target>
      </div>
      <el-tabs
        v-model="activeTab"
        class="fpage-tabs space-booking-page-tabs"
        :class="[hideTab ? 'hide-tab' : '', hideScroll ? 'disable-scroll' : '']"
        @tab-click="updateTabQuery"
      >
        <template v-for="(tab, index) in page.tabs">
          <el-tab-pane
            :key="tab.key"
            :label="getTabDetails(tab).displayName || tab.name"
            :name="tab.name"
            v-if="!getTabDetails(tab).isHidden"
            lazy
          >
            <div
              class="d-flex flex-direction-column"
              style="position: sticky;top: 0;z-index: 1;"
            >
              <portal-target name="pagebuilder-sticky-top"></portal-target>
              <portal-target name="pagebuilder-fixed-top"></portal-target>
            </div>
            <div class="height100 fpage-tab-content tab-content-pb">
              <template v-if="tab.customFullPage">
                <div
                  v-if="getTabDetails(tab).isCustomTab"
                  :key="`${tab.key}c${index}`"
                  class="f-page-section mT20 mB20 mL20 mR20"
                >
                  <div>
                    <portal-target
                      :name="tab.name + '-title-section'"
                    ></portal-target>
                  </div>
                  <widget-tab
                    :tab="tab"
                    :isInPageBuilder="true"
                    :moduleName="module"
                    :details="record"
                    :primaryFields="primaryFields"
                    :size="containerSize"
                    v-bind="$attrs"
                  ></widget-tab>
                </div>

                <div
                  v-else
                  v-for="section in tab.sections"
                  :key="`${id}-section-${section.key}-container`"
                  class="f-page-section"
                >
                  <div
                    v-if="!$validation.isEmpty(section.name)"
                    class="section-header d-flex display-flex-between-space"
                  >
                    <div class="section-header-name">
                      {{ getSectionName(section.name) }}
                    </div>
                    <div>
                      <portal-target
                        :name="section.key + '-title-section'"
                      ></portal-target>
                    </div>
                  </div>
                  <div
                    v-if="section.widgets"
                    :key="`section-${section.key}-layout`"
                    :ref="section.key"
                    :margin="margins"
                  >
                    <div
                      v-for="item in section.widgets"
                      :key="`${id}-section-${section.key}-item-${item.key}`"
                      :ref="item.key"
                      class="f-page-grid-item"
                    >
                      <widget-group
                        :widget="item"
                        :moduleName="module"
                        :details="record"
                        :notesModuleName="notesModuleName"
                        :attachmentsModuleName="attachmentsModuleName"
                        :primaryFields="primaryFields"
                        :resizeWidget="params => resizeGridItem()"
                        :calculateDimensions="
                          params => calculateRequiredDimensions()
                        "
                        :sectionKey="section.key"
                        :isSidebarView="isSidebarView"
                        v-bind="$attrs"
                        :class="[
                          'page-widget-container',
                          isSidebarView && 'in-sidebar',
                          skipMargins && 'no-margin',
                        ]"
                        :key="
                          `${id}-section-${section.key}-item-${item.key}-group`
                        "
                      ></widget-group>
                    </div>
                  </div>
                </div>
              </template>
              <template v-else>
                <div
                  v-if="getTabDetails(tab).isCustomTab"
                  :key="`${tab.key}c${index}`"
                  class="f-page-section mT20 mB20 mL20 mR20"
                >
                  <div>
                    <portal-target
                      :name="tab.name + '-title-section'"
                    ></portal-target>
                  </div>
                  <widget-tab
                    :tab="tab"
                    :isInPageBuilder="true"
                    :moduleName="module"
                    :details="record"
                    :primaryFields="primaryFields"
                    :size="containerSize"
                    v-bind="$attrs"
                  ></widget-tab>
                </div>

                <div
                  v-else
                  v-for="(section, sectionIndex) in tab.sections"
                  :key="`${id}-section-${section.key}-container`"
                  class="f-page-section page-builder-tab-section"
                >
                  <div
                    v-if="!$validation.isEmpty(section.widgets)"
                    :class="[
                      'section-header-container',
                      sectionIndex === 0 &&
                        !$validation.isEmpty(section.name) &&
                        'mT20',
                    ]"
                  >
                    <div>
                      <div
                        v-if="!$validation.isEmpty(section.name)"
                        class="section-header-name"
                      >
                        {{ getSectionName(section.name) }}
                      </div>
                      <div
                        v-if="!$validation.isEmpty(section.description)"
                        class="section-header-description"
                      >
                        {{ section.description }}
                      </div>
                    </div>
                    <div>
                      <portal-target
                        :name="section.key + '-title-section'"
                      ></portal-target>
                    </div>
                  </div>

                  <grid-layout
                    v-if="section.widgets"
                    :layout="section.layout"
                    :key="`section-${section.key}-layout`"
                    :ref="section.key"
                    :col-num="24"
                    :rowHeight="25"
                    :margin="margins"
                    :is-draggable="false"
                    :is-resizable="false"
                    :use-css-transforms="false"
                    @layout-updated="() => onLayoutUpdate(section.key)"
                  >
                    <template v-if="section.widgets">
                      <grid-item
                        v-for="(item, index) in section.widgets"
                        :x="section.layout[index].x"
                        :y="section.layout[index].y"
                        :w="section.layout[index].w"
                        :h="section.layout[index].h"
                        :i="section.layout[index].i"
                        :key="`${id}-section-${section.key}-item-${item.key}`"
                        :ref="item.key"
                        :is-draggable="false"
                        :is-resizable="false"
                        @resize="() => onLayoutUpdate(section.key, item.key)"
                      >
                        <widget-group
                          :widget="item"
                          :moduleName="module"
                          :id="id"
                          :details="record"
                          :notesModuleName="notesModuleName"
                          :attachmentsModuleName="attachmentsModuleName"
                          :layoutParams="item.layoutParams"
                          :primaryFields="primaryFields"
                          :resizeWidget="
                            params =>
                              resizeGridItem(
                                section.key,
                                item.key,
                                item,
                                params
                              )
                          "
                          :calculateDimensions="
                            params =>
                              calculateRequiredDimensions(
                                section.key,
                                item.key,
                                item,
                                params
                              )
                          "
                          :sectionKey="section.key"
                          :isSidebarView="isSidebarView"
                          v-bind="$attrs"
                          :class="[
                            'page-widget-container',
                            isSidebarView && 'in-sidebar',
                            skipMargins && 'no-margin',
                          ]"
                          :key="
                            `${id}-section-${section.key}-item-${item.key}-group`
                          "
                        ></widget-group>
                      </grid-item>
                    </template>
                  </grid-layout>
                </div>
              </template>
            </div>
          </el-tab-pane>
        </template>
      </el-tabs>
    </div>
  </div>
</template>
<script>
import Page from 'src/components/page/PageBuilderDep.vue'
export default {
  extends: Page,
}
</script>
<style>
.p-relative {
  position: relative;
}
.p-absolute {
  position: absolute;
  z-index: 5;
  right: 15px;
  top: 15px;
}
.space-booking-page-tabs .el-tabs__nav.is-top {
  height: 44px;
  padding-top: 4px;
}
.employee-portal-homepage .summary-header {
  border-bottom: 1px solid #ebeff3;
}
.employee-portal-homepage .asset-el-btn,
.portfolio-transition-button,
.employee-portal-homepage .fc-wo-border-btn {
  height: auto !important;
  line-height: 1;
  display: inline-block;
  letter-spacing: 0.7px !important;
  border-radius: 3px;
  padding: 8px 15px !important;
  text-transform: inherit;
  border: solid 1px #0053cc;
}
.employee-portal-homepage .fc-wo-border-btn {
  color: #0053cc;
}
.employee-portal-homepage .asset-el-btn.disabled:hover {
  border-color: #39b2c2;
}
.employee-portal-homepage .stateflow-btn-wrapper .el-button--primary {
  background-color: #0053cc;
}
.employee-portal-homepage
  .stateflow-btn-wrapper
  .el-button--primary:not(.disabled):hover {
  background-color: #0544a1 !important;
}
.employee-portal-homepage .fc-wo-border-btn:hover {
  background-color: #0544a1 !important;
  color: #fff;
}
.employee-portal-homepage
  .stateflow-btn-wrapper
  .el-button:not(.el-button--primary) {
  border: solid 1px #0053cc;
  background-color: #ffffff;
  color: #0053cc;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  padding: 8px 15px !important;
}
.employee-portal-homepage
  .stateflow-btn-wrapper.el-dropdown
  .el-button.el-dropdown__caret-button {
  padding: 8px 5px !important;
}
.employee-portal-homepage
  .stateflow-btn-wrapper
  .el-button:not(.el-button--primary):not(.disabled):hover {
  border: solid 1px #0053cc !important;
  background: #0053cc !important;
  color: #fff;
}
</style>
