<template>
  <div class="f-page" :key="id">
    <div v-if="isLoading" class="text-center width100 pT50 mT50">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div v-else-if="page" class="width100">
      <el-tabs
        v-model="activeTab"
        class="fpage-tabs"
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
            <portal-target
              name="pagebuilder-sticky-top"
              style="position: sticky;top: 0;z-index: 1;"
            ></portal-target>
            <div class="height100 fpage-tab-content tab-content-pb">
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
            </div>
          </el-tab-pane>
        </template>
      </el-tabs>
    </div>
  </div>
</template>
<script>
import PageBuilder from 'src/components/page/PageBuilderDep.vue'

export default {
  extends: PageBuilder,
  methods: {
    resizeGridItem() {
      console.warn('Resize is not supported in fluid-height pages')
    },
    calculateRequiredDimensions() {
      console.warn(
        'calculateRequiredDimensions is not supported in fluid-height pages'
      )
    },
    onLayoutUpdate() {
      console.warn('onLayoutUpdate is not supported in fluid-height pages')
    },
  },
}
</script>
