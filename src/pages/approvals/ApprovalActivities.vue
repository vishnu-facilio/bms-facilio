<template>
  <ListLayout
    :moduleName="moduleName"
    :showViewRearrange="true"
    :visibleViewCount="2"
    :showViewEdit="false"
    :getPageTitle="getViewName"
    :pathPrefix="pathPrefix"
  >
    <template #views-list>
      <ApprovalViewSidePanel
        v-if="canShowViewsSidePanel"
        :moduleName="moduleName"
        :canShowViewsSidePanel.sync="canShowViewsSidePanel"
        :pathPrefix="pathPrefix"
      ></ApprovalViewSidePanel>
    </template>
    <template #views>
      <ApprovalViews
        :moduleName="moduleName"
        :isActivityView="true"
        :showEditIcon="false"
        :pathPrefix="pathPrefix"
        :canShowViewsSidePanel.sync="canShowViewsSidePanel"
      ></ApprovalViews>
    </template>

    <template #header>
      <pagination
        v-if="totalCount"
        :total="totalCount"
        :perPage="perPage"
        class="pL15 fc-black-small-txt-12"
      ></pagination>
    </template>

    <div :class="['height100 f-list-view height100vh', { m10: loading }]">
      <div v-if="loading" class="full-layout-white height100 text-center">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div
        v-if="$validation.isEmpty(activities)"
        class="flex-middle width100 m10 justify-center shadow-none white-bg-block flex-direction-column approval-activity-container"
      >
        <InlineSvg
          src="svgs/emptystate/history"
          iconClass="icon icon-xxxxlg mR10"
        ></InlineSvg>
        <div class="nowo-label text-center pT10">
          {{ $t('asset.history.no_history_available') }}
        </div>
      </div>
      <div
        class="fc-list-view fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser p10 pB100 approval-activities"
        v-else
      >
        <el-table
          :data="activities"
          ref="tableList"
          class="width100"
          height="auto"
          :fit="true"
          row-class-name="activity-row no-hover"
        >
          <el-table-column fixed prop label="Time" width="200px">
            <template slot-scope="data">
              <div>{{ data.row.ttime | formatDate }}</div>
            </template>
          </el-table-column>
          <el-table-column fixed prop label="User" width="220px">
            <template slot-scope="data">
              <avatar
                size="sm"
                :user="{ name: getUserName(data.row.doneBy.ouid) }"
              ></avatar>
              {{ getUserName(data.row.doneBy.ouid) }}
            </template>
          </el-table-column>
          <el-table-column prop label="Action" min-width="320px">
            <template slot-scope="data">
              <div>
                <span v-html="getActivityMessage(data.row).message"></span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop label="Approval Process" width="230px">
            <template slot-scope="data">
              {{ getRuleName(data.row) }}
            </template>
          </el-table-column>
          <el-table-column
            prop
            :label="getPrimaryFieldName(moduleName)"
            min-width="350px"
          >
            <template slot-scope="data">
              <div class="position-relative">
                <div class="ellipsis textoverflow-ellipsis">
                  {{ getModulePrimaryField(data.row) }}
                </div>
                <a
                  @click="goToModuleSummary({ id: data.row.parentId })"
                  class="summary-link pointer"
                >
                  Open Summary
                  <inline-svg
                    src="svgs/new-tab"
                    iconClass="icon vertical-middle icon-sm mL3"
                  ></inline-svg>
                </a>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </ListLayout>
</template>
<script>
import { API } from '@facilio/api'
import { mapState, mapGetters } from 'vuex'
import ListLayout from 'newapp/list/DeprecatedCommonLayout'
import Avatar from '@/Avatar'
import ApprovalViews from './components/ApprovalHeader'
import ApprovalViewSidePanel from './components/ApprovalViewSidePanel'
import ActivitiesMixin from '@/widget/ActivitiesMixin.vue'
import Pagination from './components/ActivityPagination'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  name: 'ApprovalActivities',
  props: ['moduleName'],
  mixins: [ActivitiesMixin],
  components: {
    ListLayout,
    ApprovalViews,
    ApprovalViewSidePanel,
    Pagination,
    Avatar,
  },

  created() {
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadGroups')
  },
  data() {
    return {
      loading: true,
      activities: [],
      primaryFields: {},
      approvalRules: {},
      portalUserList: [],
      perPage: 50,
      totalCount: null,
      canShowViewsSidePanel:
        JSON.parse(localStorage.getItem('fc-view-sidepanel')) || false,
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
      sites: state => state.site,
    }),
    ...mapGetters(['getUser', 'getGroup']),
    module() {
      return this.moduleName
    },
    pathPrefix() {
      return '/app/wo/newapprovals/'
    },
    page() {
      return this.$route.query.page || 1
    },
  },
  watch: {
    moduleName: {
      handler: 'init',
      immediate: true,
    },
    canShowViewsSidePanel(value) {
      localStorage.setItem('fc-view-sidepanel', value)
    },
    page: 'loadActivities',
  },
  methods: {
    init() {
      this.loadActivities()
      this.loadPortalUsers()
    },
    getViewName() {
      return 'Approval Activities'
    },
    async loadActivities() {
      this.loading = true

      let params = {
        moduleName: this.moduleName,
        page: this.page,
        perPage: this.perPage,
      }
      let { data, error } = await API.get('v2/approval/activityList', params)
      if (!error) {
        this.activities = data.activityList || []
        this.primaryFields = data.pickList
        this.approvalRules = data.workflowRule
      }

      let response = await API.get('v2/approval/activityList', {
        ...params,
        fetchCount: true,
      })
      if (!response.error) {
        this.totalCount = response.data.recordCount || null
      }

      this.loading = false
    },
    async loadPortalUsers() {
      API.get('/setup/portalusers').then(({ error, data }) => {
        if (!error) this.portalUserList = data.users || []
      })
    },
    getPrimaryFieldName(/*activity*/) {
      // TODO fetch fields and get the main field
      return this.moduleName === 'workorder' ? 'SUBJECT' : 'NAME'
    },
    getModuleDisplayName(module) {
      return this.$constants.moduleSingularDisplayNameMap[module]
        ? this.$constants.moduleSingularDisplayNameMap[module]
        : module
    },
    getRuleName(activity) {
      let ruleId = this.$getProperty(activity, 'info.ruleId')
      let ruleName = this.approvalRules[ruleId]
      return ruleName || '---'
    },
    getModulePrimaryField(activity) {
      let recordId = this.$getProperty(activity, 'parentId')
      let value = this.primaryFields[recordId]
      return value
    },
    getActivityMessage(activity) {
      let message = ``
      let approvalStatus = this.$getProperty(activity, 'info.status', '')
      let moduleSingularDisplayName = this.getModuleDisplayName(this.moduleName)

      if ((activity || {}).type === 72) {
        if (approvalStatus === 'Approved') {
          message = `<span class="fw5">Approved the ${moduleSingularDisplayName}</span>`
        } else if (approvalStatus === 'Rejected') {
          message = `<span class="fw5">Rejected the ${moduleSingularDisplayName}</span>`
        } else if (approvalStatus === 'Requested') {
          message = `<span class="fw5">Resent for Approval</span>`
        }
      } else if ((activity || {}).type === 73) {
        return {
          message: `<span class="fw5">Initiated Approval for the ${moduleSingularDisplayName}</span>`,
        }
      } else {
        message = this.getMessage(activity).message
      }
      return { message }
    },
    goToModuleSummary(record) {
      let { moduleName, $router } = this

      if (isWebTabsEnabled()) {
        let params = { viewname: 'all', id: record.id }
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW)

        if (name) {
          let { href } = $router.resolve({ name, params }) || {}
          href && window.open(href, '_blank')
        }
      } else {
        const summaryRoutes = {
          workorder: {
            name: 'wosummarynew',
            params: { id: record.id },
          },
          workpermit: {
            name: 'workPermitSummaryV3',
            params: { viewname: 'all', id: record.id },
          },
          inventoryrequest: {
            name: 'inventoryrequestSummary',
            params: { viewname: 'all', id: record.id },
          },
        }

        let route = summaryRoutes[moduleName]
        let routerData = route && $router.resolve(route)

        window.open(routerData.href, '_blank')
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.approval-activity-container {
  height: calc(100vh - 130px);
}
.approval-activities {
  .summary-link {
    position: absolute;
    right: 0;
    top: 0;
    padding: 0 10px;
    visibility: hidden;
    background: rgb(241, 248, 250);
  }
  .activity-row:hover .summary-link {
    visibility: visible;
  }
}
</style>
