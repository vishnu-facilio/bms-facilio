<template>
  <div class="details-container">
    <Spinner
      v-if="loadingTabsAndLayouts"
      :show="loadingTabsAndLayouts"
      size="80"
    ></Spinner>
    <template v-else>
      <portal to="layout-switch" slim>
        <inline-svg
          src="svgs/web-view"
          class="mL5"
          :iconClass="getViewIconClass(layoutTypes.WEB_VIEW)"
          @click.native="selectView(layoutTypes.WEB_VIEW)"
        ></inline-svg>
        <inline-svg
          src="svgs/mobile-view"
          class="mL5"
          :iconClass="getViewIconClass(layoutTypes.MOBILE_VIEW)"
          @click.native="selectView(layoutTypes.MOBILE_VIEW)"
        ></inline-svg>
      </portal>
      <div class="groups-and-tabs">
        <div class="groups" v-for="(group, index) in webTabGroups" :key="index">
          <div class="group-label">{{ group.name }}</div>
          <div v-for="(tab, index) in group.webTabs" :key="index">
            <div
              :class="['tab-label', activeTabId == tab.id && 'selected']"
              @click="setActiveTab(tab, group.name, group.id)"
            >
              {{ tab.name }}
            </div>
            <div class="portfolio-dropdown" v-if="getPortfolioTab(tab)">
              <div
                v-for="(item, key) in portfolioCustomModule"
                :key="key"
                :class="[customModuleName === key && 'selected']"
                @click="setProps(key)"
              >
                {{ item }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-if="isEmpty(currentColumns)" class="empty-container">
        <InlineSvg
          src="svgs/emptystate/readings-empty"
          iconClass="icon text-center icon-130 emptystate-icon-size"
        ></InlineSvg>
        <div class="empty-state-text">
          {{ $t('setup.translation.no_data') }}
        </div>
      </div>
      <el-tabs v-else v-model="activeColumnId">
        <el-tab-pane
          v-for="column in currentColumns"
          :key="`${activeTabId}_${column.type}`"
          :label="column.label"
          :name="column.type"
          class="height-100"
          lazy
        >
          <component
            v-if="!isEmpty(getFilterComponent(column.type))"
            :is="getFilterComponent(column.type)"
            :moduleName="fetchCustomModuleName(activeTab)"
            :appId="appId"
            :setFilter="
              value => {
                setFilter(column.type, value)
              }
            "
          >
          </component>
          <TranslationFields
            :tabId="activeTabId"
            :activeColumnId="activeColumnId"
            :columnId="column.type"
            :appId="appId"
            :filter="customFilterCheck(column)"
            :hasFilter="!isEmpty(getFilterComponent(column.type))"
          />
        </el-tab-pane>
      </el-tabs>
    </template>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import sortBy from 'lodash/sortBy'
import TranslationFields from './TranslationFields.vue'
import Spinner from '@/Spinner'

const layoutTypes = {
  WEB_VIEW: 1,
  MOBILE_VIEW: 2,
}

const defaultFilterValue = {
  loading: true,
  query: null,
}

const filterHash = {
  STATE_TRANSITION: {
    component: () => import('./filters/StateFlows.vue'),
    default: defaultFilterValue,
  },
  VIEWS: {
    component: () => import('./filters/Views.vue'),
    default: defaultFilterValue,
  },
  FORMS: {
    component: () => import('./filters/Forms.vue'),
    default: defaultFilterValue,
  },
  DASHBOARD: {
    component: () => import('./filters/Dashboard.vue'),
    default: defaultFilterValue,
  },
  STATE_TRANSITION_FORM: {
    component: () => import('./filters/StateflowTranscationForm'),
    default: defaultFilterValue,
  },
  WORKORDER_FIELDS: {
    component: () => import('./filters/woAssetMapping'),
    default: defaultFilterValue,
  },
  ASSET_FIELDS: {
    component: () => import('./filters/woAssetMapping'),
    default: defaultFilterValue,
  },
}
const portfolioCustomModule = {
  site: 'Site',
  building: 'Building',
  floor: 'Floor',
  space: 'Space',
}
export default {
  props: ['appId'],
  components: {
    TranslationFields,
    Spinner,
  },
  data() {
    return {
      appLayouts: null,
      appDomain: null,
      webTabGroups: [],
      activeGroupId: null,
      activeTabId: null,
      activeTab: null,
      activeColumnId: null,
      loadingTabsAndLayouts: false,
      layoutTypes,
      portfolioCustomModule,
      iconClass: 'icon icon-40 p10 pointer',
      selectedLayout: 1,
      customModuleName: null,
      customQuery: {},
      filters: this.getDefaultFilters(),
    }
  },
  watch: {
    selectedLayout() {
      this.constructWebtabGroups()
    },
    appId: {
      handler(value) {
        if (!isEmpty(value)) {
          this.init()
        }
      },
      immediate: true,
    },
  },
  computed: {
    enabledLayouts() {
      let {
        appLayouts,
        layoutTypes: { WEB_VIEW, MOBILE_VIEW },
      } = this
      let enabledLayouts = {
        [WEB_VIEW]: false,
        [MOBILE_VIEW]: false,
      }

      if (!isEmpty(appLayouts)) {
        appLayouts.forEach(layout => {
          enabledLayouts[layout.layoutDeviceType] = true
        })
      }

      return enabledLayouts
    },
    currentAppLayout() {
      let { appLayouts, selectedLayout } = this
      return (
        (appLayouts || []).find(
          view => view.layoutDeviceType === selectedLayout
        ) || {}
      )
    },
    portfolioTabCheck() {
      let { activeTab } = this
      let { configJSON } = activeTab || {}
      let { type } = configJSON || {}
      return type === 'portfolio'
    },
    currentColumns() {
      const { activeTabId, activeTab } = this
      const { typeVsColumns } = activeTab || {}

      if (isEmpty(activeTabId) || isEmpty(typeVsColumns)) {
        return []
      }
      return typeVsColumns
    },
  },
  methods: {
    getDefaultFilters() {
      let filters = {}
      Object.keys(filterHash).forEach(
        key => (filters[key] = defaultFilterValue)
      )
      return filters
    },
    getPortfolioTab(tab) {
      let { activeTabId, portfolioTabCheck } = this
      let { id } = tab || {}
      if (portfolioTabCheck && activeTabId == id) return true
      else return false
    },
    customFilterCheck(col) {
      let { customQuery, filters, portfolioTabCheck } = this
      let { type } = col || {}
      if (portfolioTabCheck && !['FORMS', 'VIEWS'].includes(type))
        return customQuery
      else return filters[type]
    },
    isEmpty,
    getFilterComponent(columnId) {
      return filterHash[columnId] ? filterHash[columnId].component : null
    },
    setFilter(columnId, value) {
      this.filters[columnId] = value
    },
    selectView(layoutType) {
      if (!this.loadingTabsAndLayouts && this.enabledLayouts[layoutType]) {
        this.selectedLayout = layoutType
      }
    },
    getViewIconClass(layoutType) {
      let iconClass = this.iconClass

      if (!this.enabledLayouts[layoutType]) iconClass += ' fill-disabled-grey'
      if (layoutType === this.selectedLayout) iconClass += ' fill-pink'
      return iconClass
    },
    loadLayoutsAndTabs(appId) {
      return API.get('/v2/translation/fetchDetail', {
        applicationId: appId,
      }).then(({ data, error }) => {
        if (error) {
          return { error }
        } else {
          return { data: data || {} }
        }
      })
    },
    setActiveTab(tab, groupId) {
      this.filters = this.getDefaultFilters()
      this.activeTabId = tab.id
      this.activeTab = {
        ...tab,
      }
      let { configJSON } = tab || {}
      let { type } = configJSON || {}
      if (type === 'portfolio') {
        this.customModuleName = 'site'
        this.customQuery = {
          loading: true,
          query: { moduleName: this.customModuleName },
        }
      }
      this.activeGroupId = groupId
      const { typeVsColumns } = this.activeTab || {}

      if (!isEmpty(typeVsColumns)) {
        const [firstColumn] = typeVsColumns
        const { type } = firstColumn || {}
        this.activeColumnId = type
      }
    },
    fetchCustomModuleName(data) {
      let { customModuleName, $getProperty, portfolioTabCheck } = this
      let { modules } = data || {}
      if (!portfolioTabCheck) {
        return $getProperty(modules, '0.name', null)
      } else {
        return customModuleName
      }
    },
    setProps(name) {
      this.customModuleName = name
      this.customQuery = { loading: true, query: { moduleName: name } }
    },
    async loadAppLayoutsAndTabs() {
      this.loadingTabsAndLayouts = true
      let { error, data } = await this.loadLayoutsAndTabs(this.appId)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { webTabDetails } = data
        let { linkName, layouts, appDomain } = webTabDetails || {}

        this.appLayouts = layouts
        this.appName = linkName
        this.appDomain = appDomain
        this.selectedLayout = 1
        this.constructWebtabGroups()
      }
      this.loadingTabsAndLayouts = false
    },

    async init() {
      await this.loadAppLayoutsAndTabs()
    },
    constructWebtabGroups() {
      let { webTabGroupList: groups } = this.currentAppLayout

      if (!isEmpty(groups)) {
        this.webTabGroups = sortBy(groups, ['order'])

        const { webTabGroups } = this
        const [webTabGroup] = webTabGroups || []
        const { webTabs } = webTabGroup || {}
        const [webTab] = webTabs || []

        this.activeGroup = webTabGroup.id
        this.activeTabId = webTab.id
        this.activeTab = {
          ...webTab,
        }
        let { configJSON } = webTab || {}
        let { type } = configJSON || {}
        if (type === 'portfolio') {
          this.customModuleName = 'site'
          this.customQuery = {
            loading: true,
            query: { moduleName: this.customModuleName },
          }
        }
        const { typeVsColumns } = this.activeTab || {}
        if (!isEmpty(typeVsColumns)) {
          const [firstColumn] = typeVsColumns
          const { type } = firstColumn || {}
          this.activeColumnId = type
        }
      } else {
        this.webTabGroups = []
        this.activeTab = null
        this.activeGroupId = null
        this.activeTabId = null
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.details-container {
  margin: 15px;
  display: flex;
  flex: 1;
  background-color: white;
  height: calc(100vh - 200px);

  .empty-container {
    height: 100%;
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-content: center;
    align-items: center;
    margin: auto;

    .empty-state-text {
      font-size: 14px;
      letter-spacing: 0.5px;
      text-align: center;
      color: #324056;
      padding: 15px;
    }
  }

  .groups-and-tabs {
    min-width: 240px;
    border-right: 1px solid #ececec;
    display: flex;
    flex-direction: column;
    overflow-y: scroll;
    letter-spacing: 0.86px;
    color: #324056;

    .group-label {
      font-size: 14px;
      font-weight: bold;
      padding: 10px 10px 10px 20px;
    }

    .tab-label {
      font-size: 14px;
      padding: 10px 10px 10px 20px;
      &:hover {
        background-color: #f3f4f7;
        cursor: pointer;
      }
    }

    .selected {
      color: #ff0066;
    }
  }

  .el-tabs {
    overflow-x: scroll;
    padding-inline: 10px;
    flex-grow: 1;
  }
}
.portfolio-dropdown {
  margin: 0px !important;
  div {
    padding: 10px 10px 10px 35px;
    margin: 0px !important;
    font-size: 14px !important;
    &:hover {
      background-color: #f3f4f7;
      cursor: pointer;
    }
  }
  .selected {
    color: black !important;
    background-color: #f3f4f7;
    border-left: 3px solid #ef508f;
  }
}
</style>
