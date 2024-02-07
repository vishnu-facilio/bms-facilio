<template lang="">
  <div ref="page-builder-wrapper" class="pb-container">
    <div v-if="loading" class="fc-empty-center height100vh">
      <spinner
        :show="loading"
        size="80"
        class="flex-middle height450"
      ></spinner>
    </div>
    <el-tabs
      v-else
      v-model="currentTab"
      class="pb-tabs fpage-tabs"
      @change="tabPaneChanged"
      lazy
      @tab-click="pushCurrentTab"
    >
      <el-tab-pane
        ref="tab-comp"
        v-for="tab in tabs"
        :name="tab.name"
        :label="tab.displayName"
        :key="`${tab.id} ${tab.displayName}`"
        :force-render="true"
      >
        <div v-if="loadingPage" class="fc-empty-center height-100">
          <spinner
            :show="loadingPage"
            size="80"
            class="flex-middle height450"
          ></spinner>
        </div>
        <div v-else-if="currentTab === tab.name">
          <Page
            :grid="tab"
            :ref="`page-builder-${tab.name}`"
            :key="`${tab.id}_${tab.displayName}`"
            class="page-builder-wrapping"
            :cellHeight="cellHeight"
          >
            <template #sectionHeader="{ section }">
              <div
                v-if="
                  !isEmpty(section.displayName) || !isEmpty(section.description)
                "
                style="margin-top:8px"
              >
                <div
                  class="npb-section-title mL10"
                  v-if="!isEmpty(section.displayName)"
                >
                  {{ section.displayName }}
                </div>
                <div
                  class="npb-section-desc mL10"
                  v-if="!isEmpty(section.description)"
                >
                  {{ section.description }}
                </div>
              </div>
            </template>
            <template v-slot="{ widget }">
              <widget-supplier
                :widget="widget"
                :groupKey="widget.key"
                v-bind="$attrs"
                :details="details"
                :id="id"
                :module="module"
                :record="details"
                :reloadSummary="reloadSummary"
                :resizeWidgetOnPage="resizeWidgetOnPage"
                :cellHeight="cellHeight"
                :cellWidth="cellWidth"
                :moduleName="module"
                @fitToViewArea="fitToViewArea"
                class="page-widget-container"
              >
              </widget-supplier>
            </template>
          </Page>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import { Page } from '@facilio/ui/app'
import WidgetSupplier from './WidgetSupplier'
import { getApp } from '@facilio/router'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { eventBus } from 'src/components/page/widget/utils/eventBus'

const BULK_WIDGET = ['bulkRelatedList', 'bulkRelationShipWidget']

export default {
  props: ['module', 'id', 'details'],
  data() {
    return {
      pageId: null,
      currentTab: null,
      loading: false,
      cellHeight: 12,
      tabs: [],
      loadingPage: false,
      preserveQueryTab: false,
      isEmpty,
    }
  },
  components: { Page, WidgetSupplier },
  async mounted() {
    this.loading = true
    let { tabName } = this
    await this.fetchPageData(tabName)
    this.pushCurrentTab()
    this.loading = false
  },
  computed: {
    tabName() {
      let { query } = this.$route
      let { tabName } = query || {}
      return tabName || null
    },
    showNewPageBuilder() {
      let { query } = this.$route
      let { showNewPageBuilder } = query
      return showNewPageBuilder
    },
    gridCell() {
      return 12
    },
    cellWidth() {
      return (
        Math.ceil(
          this.$refs['page-builder-wrapper']?.scrollWidth / this.gridCell
        ) || null
      )
    },
    appId() {
      let { id } = getApp() || {}
      return id
    },
  },
  beforeDestroy() {
    !this.preserveQueryTab && this.removeTabQuery()
  },
  watch: {
    currentTab(newVal) {
      this.tabPaneChanged(newVal)
    },
  },
  methods: {
    removeTabQuery() {
      let { query } = this.$route
      if (!isEmpty(query.tabName)) {
        this.$router.replace({
          query: { ...query, tabName: null },
        })
      }
    },
    fitToViewArea(widgetId) {
      let [tabComp] = this.$refs['tab-comp']
      let h = tabComp.$el.offsetHeight
      this.resizeWidgetOnPage(widgetId, { h: h - 48 })
    },
    async fetchPageData(tabName) {
      let { module: moduleName, appId, id: recordId, showNewPageBuilder } = this
      let { data, error } = await API.get('v2/customPage/fetchForRecord', {
        moduleName,
        appId,
        recordId,
        tabName,
        layoutType: 'WEB',
        showNewPageBuilder,
      })
      if (!error) {
        let { isNewPage } = data || {}
        !isNewPage && this.$emit('forceOldPage')
        let { id } = data?.customPage || {}
        this.pageId = id
        let tabs = data?.customPage?.layouts?.WEB || []

        let foundTab = (tabs || []).filter(tab => tab.name == tabName)
        this.currentTab = !isEmpty(foundTab)
          ? tabName
          : this.$getProperty(tabs, `0.name`) || null
        this.tabs = this.deserializeTabs(tabs)
      } else {
        this.$message.error(this.$t('common._common.error_occured'))
      }
    },
    reloadSummary() {
      this.preserveQueryTab = true
      eventBus.$emit('refresh-overview')
    },

    deserializeTabs(rawTabs) {
      let tabs = rawTabs.map((tab, tabIndex) => {
        return {
          ...tab,
          id: tab.id,
          key: `t${tabIndex}`,
          columns: (tab.columns ?? []).map((column, columnIndex, columns) => {
            let { width: columnWidth } = column

            if (columnIndex > 0) {
              const previousColumn = columns[columnIndex - 1]
              let { x, width } = previousColumn
              column.x = x + width
            } else {
              column.x = 0
            }
            return {
              ...column,
              sections: (column?.sections ?? []).map(section => {
                return {
                  ...section,
                  widgets: this.getSerializedWidgets(
                    section?.widgets,
                    columnWidth
                  ),
                }
              }),
            }
          }),
        }
      })
      return tabs
    },
    getSerializedWidgets(widgets, columnWidth) {
      let iter = (widgets || []).length - 1
      while (iter > -1) {
        // to spread bulk widgets as seperate widgets  we iterate from end to start
        const {
          positionX,
          positionY,
          width,
          height,
          widgetTypeObj,
          widgetDetail,
          configType,
        } = widgets[iter] ?? {}
        let { name: widgetTypeName } = widgetTypeObj || {}
        widgets[iter].key = widgets[iter].id
        let h = height
        let w = width

        if (configType === 'FLEXIBLE') w = columnWidth
        if (BULK_WIDGET.includes(widgetTypeName)) {
          let { relatedList, relationships } = widgetDetail || {}
          let relatedWidgets = (relatedList || relationships || []).map(
            relatedObj => {
              return Object.assign(
                {
                  ...widgets[iter],
                  ...relatedObj,
                  h,
                  w,
                  noResize: true,
                  x: 0,
                  y: 0,
                  noMove: true,
                  id: `${relatedObj.id} ${widgets[iter].id}`,
                  originalId: relatedObj.id, // revert handling once fixed in UI
                },
                {
                  relatedList: relatedObj,
                }
              )
            }
          )
          widgets.splice(iter, 1, ...relatedWidgets)
        } else {
          widgets[iter] = {
            ...widgets[iter],
            x: positionX,
            y: positionY,
            h,
            w,
            noResize: true,
            noMove: true,
          }
        }
        iter--
      }

      widgets.splice(length, -1)
      return widgets
    },
    resizeWidgetOnPage(widgetId, dimensions) {
      let { currentTab } = this
      let [tabRef] = this.$refs[`page-builder-${currentTab}`]
      tabRef?.resize(widgetId, dimensions)
    },
    pushCurrentTab() {
      let { currentTab } = this
      let { query } = this.$route
      if (currentTab && currentTab !== query.tabName)
        this.$router
          .replace({
            path: this.$route.fullPath,
            query: { ...query, tabName: currentTab },
          })
          .catch({})
    },

    async fetchCurrentTab(tabIndex) {
      this.loadingPage = true
      let { currentTab, pageId, module: moduleName } = this
      let { data, error } = await API.get('v2/customPage/tabs/fetch', {
        tabName: currentTab,
        moduleName,
        layoutType: 'WEB',
        pageId,
      })
      if (!error) {
        let tab = this.deserializeTabs([data.tab])
        this.$set(this.tabs, `${tabIndex}`, tab[0])
      }
      this.loadingPage = false
    },
    tabPaneChanged(tabName) {
      let { tabs } = this
      this.$nextTick(() => {
        let tabIndex = tabs.findIndex(tab => tab.name == tabName)

        if (tabIndex !== -1) {
          let columns =
            this.$getProperty(this.tabs, `${tabIndex}.columns`) || null
          isEmpty(columns) && this.fetchCurrentTab(tabIndex)
        }
      })
    },
  },
}
</script>
<style lang="scss">
.pb-container {
  background-color: #f6f7f8;

  .npb-section-title {
    font-weight: normal;
    line-height: 18px;
    letter-spacing: 0.7px;
    font-size: 18px;
    color: #000;
  }

  .el-tabs__nav-wrap {
    padding-left: 20px;
  }

  .npb-section-desc {
    font-size: 13px;
    color: gray;
    line-height: 20px;
    letter-spacing: 0.3px;
    margin-top: 5px;
  }
}
.pb-tabs {
  .el-tabs__content {
    height: calc(100vh - 170px) !important;

    .el-tab-pane {
      padding: 8px;
      height: 100% !important;
    }
  }
}

.page-builder-wrapping {
  overflow: scroll;
  height: calc(100% - 32px);

  & > .grid-stack-item {
    & > .grid-stack-item-content {
      .grid-stack-item-content {
        .grid-stack-item-content {
          background: #fff;
        }
      }
    }
  }
}
</style>
