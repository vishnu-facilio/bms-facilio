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
                          :activeTab="activeTab"
                          :tab="tab"
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
import debounce from 'lodash/debounce'
import WidgetGroup from './widget/WidgetGroup'
import WidgetTab from './widget/WidgetTab'
import { GridLayout, GridItem } from 'vue-grid-layout'
import { isEmpty } from '@facilio/utils/validation'
import { eventBus } from './widget/utils/eventBus'
import { API } from '@facilio/api'
import isEqual from 'lodash/isEqual'

export default {
  props: [
    'module',
    'id',
    'details',
    'primaryFields',
    'notesModuleName',
    'attachmentsModuleName',
    'isApprovalView',
    'isSidebarView',
    'skipMargins',
    'hideScroll',
  ],
  components: {
    WidgetGroup,
    GridLayout,
    GridItem,
    WidgetTab,
  },
  created() {
    this.record = this.details
    this.fetchPage()
  },
  mounted() {
    eventBus.$on('reload', this.fetchPage)
    eventBus.$on('refreshdata', this.fetchPageWithoutLoading)

    this.registerNativeEvents()
  },
  beforeDestroy() {
    eventBus.$off('reload', this.fetchPage)
    eventBus.$off('refreshdata', this.fetchPageWithoutLoading)

    this.removeNativeEvents()
  },
  data() {
    return {
      page: null,
      record: {},
      isLoading: false,
      activeTab: null,
      eventBus,
      containerSize: {
        height: null,
        width: null,
      },
    }
  },
  computed: {
    hideTab() {
      let { page } = this
      let { tabs } = page || {}
      return (tabs || []).length <= 1
    },
    margins() {
      if (this.skipMargins) {
        return [0, 0]
      } else {
        return [20, 20]
      }
    },
  },
  watch: {
    module() {
      this.fetchPage()
    },
    details(value) {
      this.record = value
    },
    $route: {
      async handler(newVal, oldVal) {
        let { activeTab } = this
        let { query: newQuery } = newVal || {}
        let { query: oldQuery } = oldVal || {}

        if (!isEqual(newQuery, oldQuery) && !isEmpty(newQuery)) {
          let { tab } = newQuery || {}
          if (tab !== activeTab) {
            this.$set(this, 'activeTab', tab)
          }
        }
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    getSectionName(sectionName) {
      let sectionHash = {
        commands: this.$t('asset.assets.commands'),
        plannedWorkorder: this.$t('asset.assets.asset_pm'),
        unplannedWorkorder: this.$t('asset.assets.asset_upm'),
        portalWorkrequestFormDetails: this.$t('common._common.additional_info'),
        buildingRelatedList: this.$t('space.sites.building_details'),
        siteSpaces: this.$t('space.sites.spaces'),
        spaceReadings: this.$t('space.sites.readings'),
      }
      return sectionHash[sectionName] || sectionName
    },

    getTabDetails(tab) {
      let tabHash = {
        summary: {
          displayName: this.$t('common.tabs.summary'),
          isHidden: false,
        },
        maintenance: {
          displayName: this.$t('common.tabs.maintenance'),
        },
        readings: {
          displayName: this.$t('common.tabs.readings'),
        },
        performance: {
          displayName: this.$t('common.tabs.performance'),
        },
        cost: {
          displayName: this.$t('common.tabs.cost'),
        },
        history: {
          displayName: this.$t('common.tabs.history'),
        },
        assetMovement: {
          displayName: this.$t('common.tabs.movements'),
        },
        contracts: {
          displayName: this.$t('common.tabs.contracts'),
        },
        rule_insight: {
          displayName: this.$t('common.tabs.rule_insight'),
        },
        root_cause_impact: {
          displayName: this.$t('common.tabs.root_causes'),
        },
        graphics: {
          displayName: this.$t('common.tabs.graphics'),
        },
        metrics: {
          displayName: this.$t('common.tabs.metrics'),
        },
        history_log: {
          displayName: this.$t('common.tabs.logs'),
        },
        anomalyRCA: {
          displayName: this.$t('common.tabs.root_causes'),
        },
        occurrenceHistory: {
          displayName: this.$t('common.tabs.history'),
        },
        activityWidget: {
          displayName: this.$t('common.tabs.activity'),
        },
        anomalyMetrics: {
          displayName: this.$t('common.tabs.metrics'),
        },
        insight: {
          displayName: this.$t('common.tabs.insight'),
        },
        kpiViewer: {
          displayName: 'KPI VIEWER',
        },
        kpiLog: {
          displayName: this.$t('common.tabs.logs'),
        },
        historicalTrends: {
          displayName: this.$t('common.tabs.historical_trends'),
        },
        kpiViolations: {
          displayName: this.$t('common.tabs.violations'),
        },
        alarmRca: {
          displayName: this.$t('common.tabs.root_causes'),
        },
        rule_impact: {
          displayName: 'Impact',
        },
        // DONT ADD ANY DISPLAY NAMES HERE, CONFIGURE IN SERVER
      }
      if (!tabHash[tab.name] && tab.displayName) {
        tabHash[tab.name] = {
          displayName: tab.displayName,
        }
      }

      return {
        ...tabHash[tab.name],
        isCustomTab: !isEmpty(tab.component),
      }
    },

    async fetchPage() {
      this.isLoading = true

      if (!this.module) return

      let url = '/v2/pages/'

      if (this.$constants.SPECIAL_MODULE.includes(this.module)) {
        url += 'specialModule/'
      }

      url += this.module

      let params = { id: this.id }
      if (this.isApprovalView) {
        params.approval = true
      }

      let { data, error } = await API.get(url, params, { force: true })

      if (error) {
        this.$emit('error', error)
      } else {
        this.page = data.page

        if (isEmpty(this.details)) {
          this.record = data.record
          this.$emit('record', this.record)
        }

        this.prepareWidgetLayouts()
        this.setInitialTab()
      }

      this.isLoading = false
      this.$nextTick(() => {
        this.updateTabSize()
      })
    },
    async fetchPageWithoutLoading() {
      if (!this.module) return

      let url = '/v2/pages/'

      if (this.$constants.SPECIAL_MODULE.includes(this.module)) {
        url += 'specialModule/'
      }

      url += this.module

      let params = { id: this.id }
      if (this.isApprovalView) {
        params.approval = true
      }

      let { data, error } = await API.get(url, params, { force: true })

      if (error) {
        this.$emit('error', error)
      } else {
        this.page = data.page

        if (isEmpty(this.details)) {
          this.record = data.record
          this.$emit('record', this.record)
        }

        this.prepareWidgetLayouts()
        this.setInitialTab()
      }

      this.$nextTick(() => {
        this.updateTabSize()
      })
    },
    prepareWidgetLayouts() {
      for (let [tabIndex, tab] of this.page.tabs.entries()) {
        tab.key = `t${tabIndex}`

        if (isEmpty(tab.sections)) continue

        for (let [sectIndex, section] of tab.sections.entries()) {
          section.key = `${tabIndex}s${sectIndex}`
          section.layout = section.layout || []

          if (section.widgets) {
            for (let [index, widget] of section.widgets.entries()) {
              section.layout.push({
                i: `${tabIndex}${sectIndex}${index}`,
                ...widget.layoutParams,
              })
              widget.key = `${tabIndex}${sectIndex}${index}`
            }
          }
        }
      }
    },

    resizeGridItem(layoutRef, itemRef, item, params) {
      let layoutParams = this.$helpers.cloneObject(item.layoutParams)
      let gridItemComponent = this.$refs[itemRef][0]
      let gridLayoutComponent = this.$refs[layoutRef][0]

      if (params.width && params.height) {
        //Calculate new dimensions from params using gridItem method
        let result = gridItemComponent.calcWH(params.height, params.width)
        // Set the new values
        // layoutParams.w = result.w
        layoutParams.h = result.h
      } else {
        layoutParams = { ...layoutParams, ...params }
      }

      // Set innerH inside gridItem and trigger updates
      gridItemComponent.innerH = layoutParams.h
      gridItemComponent.createStyle()

      gridLayoutComponent.resizeEvent(
        'resizeend',
        item.key,
        layoutParams.x,
        layoutParams.y,
        layoutParams.h,
        layoutParams.w
      )
    },

    calculateRequiredDimensions(layoutRef, itemRef, item, params) {
      // Return new dimensions based on params using gridItem method
      let gridItemComponent = this.$refs[itemRef][0]
      let result = gridItemComponent.calcWH(params.height, params.width)

      return { h: result.h, w: result.w }
    },

    onLayoutUpdate(layoutRef, itemRef = null) {
      let gridLayoutComponent = this.$refs[layoutRef][0]
      this.$nextTick(() => {
        gridLayoutComponent.$forceUpdate()
      })

      if (itemRef) {
        let gridItemComponent = this.$refs[itemRef][0]
        this.$nextTick(() => gridItemComponent.$forceUpdate())
      }

      // Hack to make widgets resize always
      this.$nextTick(() => this.$forceUpdate())
    },

    setInitialTab() {
      if (
        this.$router.currentRoute.query &&
        this.$router.currentRoute.query.tab
      ) {
        this.activeTab = this.$router.currentRoute.query.tab
      } else if (this.page && this.page.tabs[0]) {
        this.activeTab = this.page.tabs[0].name
      }
    },

    updateTabQuery(tab) {
      let params = Object.assign({}, this.$route.query, { tab: tab.name })
      this.$router.replace({ query: params }).catch(() => {})
    },

    updateTabSize: debounce(function() {
      let container = document.querySelector('.fpage-tabs .el-tabs__content')
      let { offsetHeight, offsetWidth } = container || {}
      if (!isEmpty(offsetHeight)) {
        this.containerSize = {
          height: offsetHeight,
          width: offsetWidth,
        }
        this.eventBus.$emit('onWindowResize')
      }
    }, 2 * 1000),

    registerNativeEvents() {
      window.addEventListener('resize', this.updateTabSize, { passive: true })
    },
    removeNativeEvents() {
      window.removeEventListener('resize', this.updateTabSize)
    },
  },
}
</script>
<style lang="scss" scoped>
.page-builder-tab-section {
  .section-header-container {
    margin: 0px 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .section-header-name {
      font-size: 18px;
      color: #000;
      font-weight: normal;
      line-height: 18px;
      letter-spacing: 0.7px;
    }
    .section-header-description {
      font-size: 13px;
      color: #808080;
      line-height: 20px;
      letter-spacing: 0.3px;
      margin-top: 5px;
    }
  }
}
</style>
