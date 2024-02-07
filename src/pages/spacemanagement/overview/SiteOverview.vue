<template>
  <div v-if="isLoading">
    <spinner :show="true"></spinner>
  </div>
  <div
    v-else
    class="fc-v1-site-overview-main-sec site-scrollable-header"
    :class="[newSiteSummary ? 'tabs-header-sticky' : '']"
  >
    <template v-if="newSiteSummary">
      <NewSiteHeader
        v-if="record && !isLoading"
        :key="`header_${record.id}`"
        :moduleName="moduleName"
        :record="record"
        :isApprovalEnabled="isApprovalEnabled"
        :onTransitionSuccess="() => refreshDetails(moduleName)"
        :canEdit="canEdit"
      ></NewSiteHeader>
    </template>
    <template v-else>
      <SiteHeader
        v-if="record && !isLoading"
        :key="`header_${record.id}`"
        :moduleName="moduleName"
        :record="record"
        :isApprovalEnabled="isApprovalEnabled"
        :onTransitionSuccess="() => refreshDetails(moduleName)"
        :canEdit="canEdit"
      ></SiteHeader>
    </template>
    <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
      <ApprovalBar
        :moduleName="moduleName"
        :key="record.id + 'approval-bar'"
        :record="record"
        @onSuccess="refreshDetails(moduleName)"
        @onFailure="() => {}"
        class="approval-bar-shadow"
      ></ApprovalBar>
    </portal>

    <page
      v-if="record && !isLoading"
      :key="record.id"
      :module="moduleName"
      :id="record.id"
      :details="record"
      :primaryFields="primaryFields"
      :notesModuleName="notesModuleName"
      :attachmentsModuleName="attachmentsModuleName"
      :isV3Api="true"
      class="site-custom-f-page"
    ></page>
    <SpaceManagentForms
      @saved="moduleName => refreshDetails(moduleName)"
    ></SpaceManagentForms>
  </div>
</template>
<script>
import Page from '@/page/PageBuilder'
import SiteHeader from './components/SiteDetailsHeader'
import NewSiteHeader from './components/NewSiteDetailsHeader'
import SpaceManagentForms from 'pages/spacemanagement/overview/components/SpaceManagementForms'
import { eventBus } from '@/page/widget/utils/eventBus'
import ApprovalBar from '@/approval/ApprovalBar'
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import SpaceMixin from './helpers/SpaceHelper'

export default {
  mixins: [SpaceMixin],
  components: {
    Page,
    SiteHeader,
    NewSiteHeader,
    SpaceManagentForms,
    ApprovalBar,
  },
  data() {
    return {
      loading: true,
      detailsLoading: false,
      primaryFields: [],
      notesModuleName: 'basespacenotes',
      attachmentsModuleName: 'basespaceattachments',
      siteFormVisibility: false,
      record: null,
    }
  },
  created() {
    this.loading = true

    let promises = [
      this.$store.dispatch('loadTicketStatus', this.moduleName),
      this.$store.dispatch('loadApprovalStatus'),
      this.$store.dispatch('loadSpaceCategory'),
      this.loadSiteDetails(),
      this.$store.dispatch('view/loadModuleMeta', 'site'),
    ]

    Promise.all(promises).finally(() => {
      this.loading = false
    })

    eventBus.$on('refreshDetails', this.loadSiteDetails)
    eventBus.$on('refresh-overview', this.loadSiteDetails)
  },
  beforeDestroy() {
    eventBus.$off('refreshDetails', this.loadSiteDetails)
    eventBus.$off('refresh-overview', this.loadSiteDetails)
  },
  computed: {
    ...mapGetters(['isStatusLocked', 'getApprovalStatus']),
    siteId() {
      let paramId = this.$route.params.id
      if (isEmpty(paramId)) {
        paramId = this.$route.params.siteid
      }
      let id = !isEmpty(paramId) && !isNaN(paramId) ? parseInt(paramId) : null
      return id
    },
    moduleName() {
      return 'site'
    },
    isLoading() {
      return this.loading || this.detailsLoading
    },
    isApprovalEnabled() {
      let { record = {} } = this
      let { approvalFlowId, approvalStatus } = record || {}
      return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
    },
    isRequestedState() {
      let { record } = this
      let { approvalStatus } = record || {}

      if (isEmpty(approvalStatus)) {
        return false
      } else {
        let statusObj = this.getApprovalStatus(approvalStatus.id)
        return this.$getProperty(statusObj, 'requestedState', false)
      }
    },
    canEdit() {
      let { record = {} } = this
      let { isRequestedState } = this

      let hasState = this.$getProperty(record, 'moduleState.id', null)
      let isLocked = hasState
        ? this.isStatusLocked(record.moduleState.id, this.moduleName)
        : false

      return hasState && !isLocked && !isRequestedState
    },
  },
  watch: {
    siteId() {
      this.loadSiteDetails()
    },
  },
  methods: {
    async loadSiteDetails(reloadTree) {
      this.detailsLoading = true
      let { site, error } = await API.fetchRecord(
        'site',
        {
          id: this.siteId,
        },
        { force: true }
      )
      if (!isEmpty(error)) {
        let { message = 'Error Occured while fetching Site' } = error
        this.$message.error(message)
      } else {
        this.record = site
        if (reloadTree) {
          eventBus.$emit('reloadTree')
        } else {
          eventBus.$emit('overviewRecordDetails', this.record)
        }
      }
      this.detailsLoading = false
    },
    refreshDetails() {
      this.loadSiteDetails(true)
    },
  },
}
</script>

<style lang="scss">
.fc-v1-site-overview-main-sec {
  &.tabs-header-sticky {
    .fpage-tabs {
      > .el-tabs__header {
        position: sticky;
        top: 101px;
        z-index: 22;
      }
    }
  }
}
</style>
