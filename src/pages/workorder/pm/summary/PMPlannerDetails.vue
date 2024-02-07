<template>
  <div ref="pm-planner-details" class="pm-planner-details height100">
    <div v-if="isListLoading" class="flex-middle fc-empty-white">
      <spinner :show="isListLoading" size="80"></spinner>
    </div>
    <div v-else class="d-flex">
      <div class="pm-navbar">
        <div
          v-for="tab in summaryTabs"
          :key="tab.name"
          class="pm-nav-item"
          :class="[isSelectedTab(tab) ? 'pm-selected-nav' : '']"
          @click="toggleTab(tab.name)"
        >
          {{ tab.displayName }}
        </div>
      </div>
      <div class="pm-tabs flex-direction-column">
        <el-tabs
          v-model="selectedPlannerId"
          class="fpage-tabs width100 planner-summary-main-container"
        >
          <el-tab-pane
            v-for="planner in plannerList"
            :key="planner.id"
            :label="planner.name"
            :name="`${planner.id}`"
            class="pm-tabs-container"
          >
            <PlannerSummary
              id="summary"
              :planner="planner"
              :resourcePlaceholder="resourcePlaceholder"
              :pm="details"
            />
            <ResourcePlanner
              id="resourcePlanner"
              :planner="planner"
              :pm="details"
              :placeholder="resourcePlaceholder"
            />
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import PlannerSummary from './PlannerSummary'
import ResourcePlanner from './ResourcePlannerList'
import VueScrollTo from 'vue-scrollto'
import { SCOPE_CATEGORY_PLACEHOLDER } from '../create/utils/pm-utils'
import { getRelatedFieldName } from 'src/util/relatedFieldUtil'

export default {
  name: 'PMPlannerDetails',
  props: ['details', 'resizeWidget'],
  components: { PlannerSummary, ResourcePlanner },
  created() {
    this.init()
  },
  mounted() {
    this.autoResize()
  },
  data: () => ({
    moduleName: 'pmPlanner',
    plannerList: [],
    isListLoading: false,
    selectedPlannerId: null,
    selectedTab: 'summary',
  }),
  computed: {
    resourcePlaceholder() {
      let { details } = this || {}
      let { assignmentTypeEnum } = details || {}

      return (
        SCOPE_CATEGORY_PLACEHOLDER[assignmentTypeEnum] ||
        this.$t('maintenance.pm.resources')
      )
    },
    summaryTabs() {
      let { resourcePlaceholder } = this || {}
      return [
        {
          displayName: 'Summary',
          name: 'summary',
        },
        {
          displayName: `${resourcePlaceholder} Planner`,
          name: 'resourcePlanner',
        },
      ]
    },
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['pm-planner-details']
        if (container) {
          let height = container.scrollHeight + 40
          let width = container.scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height: height, width })
          }
        }
      })
    },
    async init() {
      await this.loadPlannerList()
    },
    async loadPlannerList() {
      this.isListLoading = true
      let { moduleName, details } = this || {}
      let { id } = details || {}
      let relatedFieldName = getRelatedFieldName(
        'plannedmaintenance',
        moduleName
      )
      let relatedConfig = {
        moduleName: 'plannedmaintenance',
        id,
        relatedModuleName: moduleName,
        relatedFieldName,
      }

      let { error, list } = await API.fetchAllRelatedList(relatedConfig)

      if (!isEmpty(error)) {
        this.$message.error(
          error.message || this.$t('maintenance.pm.planner_list_error')
        )
      } else {
        this.plannerList = list || []
        if (!isEmpty(list)) {
          let selectedPlanner = list[0]
          let { id } = selectedPlanner || {}
          this.selectedPlannerId = `${id}`
        }
      }

      this.isListLoading = false
    },
    toggleTab(name) {
      this.selectedTab = name
      VueScrollTo.scrollTo(`#${name}`, 500, {
        container: '.pm-tabs-container',
        easing: 'ease-in',
        offset: -10,
        force: true,
      })
    },
    isSelectedTab(tab) {
      let { selectedTab } = this || {}
      let { name } = tab || {}
      return name === selectedTab
    },
  },
}
</script>

<style scoped lang="scss">
.pm-navbar {
  padding-top: 50px;
  height: 100%;
  border-right: solid 5px #f7f8f9;
  min-width: 200px !important;
}
.pm-nav-item {
  display: flex;
  justify-content: space-between;
  border-left: solid 3px #fff;
  padding: 15px 12px;
  cursor: pointer;
}
.pm-selected-nav {
  box-sizing: border-box;
  background-color: #f3f3f3;
  border-left: solid 3px #ed508f;
  border-radius: 3px;
  font-weight: 500;
  color: #324056;
}
.pm-tabs {
  display: flex;
  flex: 1;
}
.planner-summary-main-container {
  height: 100%;
  background-color: #f6f7f8;
  z-index: 0;
}
</style>

<style lang="scss">
.pm-planner-details {
  height: calc(100% - 55px) !important;
  .planner-summary-header {
    font-size: 16px;
    font-weight: bold;
    color: #2d2d51;
    margin-bottom: 10px;
  }
  .planner-summary-container {
    background-color: #ffffff;
    padding: 15px 30px;
    margin-top: 10px;
    width: 100%;
  }
  .pm-tabs-container {
    height: calc(100vh - 40px);
    overflow: scroll;
    width: calc(100vw - 270px);
  }
}
</style>
