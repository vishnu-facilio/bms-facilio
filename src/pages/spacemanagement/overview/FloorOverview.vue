<template>
  <div v-if="isLoading">
    <spinner :show="true"></spinner>
  </div>
  <div v-else class="fc-v1-site-overview-main-sec">
    <FloorDetailsHeader
      v-if="record && !isLoading"
      :record="record"
      :moduleName="moduleName"
      :isApprovalEnabled="isApprovalEnabled"
      :onTransitionSuccess="() => refreshDetails()"
      :canEdit="canEdit"
    ></FloorDetailsHeader>

    <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
      <ApprovalBar
        :moduleName="moduleName"
        :key="record.id + 'approval-bar'"
        :record="record"
        @onSuccess="refreshDetails()"
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
    ></page>
    <SpaceManagentForms @saved="refreshDetails"></SpaceManagentForms>
  </div>
</template>
<script>
import Page from '@/page/PageBuilder'
import SpaceManagentForms from 'pages/spacemanagement/overview/components/SpaceManagementForms'
import { eventBus } from '@/page/widget/utils/eventBus'
import FloorDetailsHeader from 'pages/spacemanagement/overview/components/FloorDetailsHeader'
import ApprovalBar from '@/approval/ApprovalBar'
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  components: {
    Page,
    SpaceManagentForms,
    FloorDetailsHeader,
    ApprovalBar,
  },
  data() {
    return {
      loading: false,
      detailsLoading: false,
      primaryFields: [],
      record: null,
      notesModuleName: 'basespacenotes',
      attachmentsModuleName: 'basespaceattachments',
    }
  },
  created() {
    this.loading = true
    let promises = [
      this.$store.dispatch('loadTicketStatus', this.moduleName),
      this.$store.dispatch('loadApprovalStatus'),
      this.$store.dispatch('loadSpaceCategory'),
      this.loadFloorDetails(),
      this.$store.dispatch('view/loadModuleMeta', 'floor'),
    ]
    Promise.all(promises).then(() => {
      this.loading = false
    })

    eventBus.$on('refreshDetails', this.loadFloorDetails)
    eventBus.$on('refresh-overview', this.loadFloorDetails)
  },
  beforeDestroy() {
    eventBus.$off('refreshDetails', this.loadFloorDetails)
    eventBus.$off('refresh-overview', this.loadFloorDetails)
  },
  computed: {
    ...mapGetters(['isStatusLocked', 'getApprovalStatus']),

    siteId() {
      return parseInt(this.$route.params.siteid)
    },
    floorId() {
      return parseInt(this.$route.params.floorid)
    },
    moduleName() {
      return 'floor'
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
    floorId() {
      this.loadFloorDetails()
    },
  },
  methods: {
    async loadFloorDetails(reloadTree) {
      this.detailsLoading = true
      let { floor, error } = await API.fetchRecord(
        'floor',
        {
          id: this.floorId,
        },
        { force: true }
      )
      if (!isEmpty(error)) {
        let { message = 'Error Occured while fetching Floor' } = error
        this.$message.error(message)
      } else {
        this.record = floor
        if (reloadTree) {
          eventBus.$emit('reloadTree')
        } else {
          eventBus.$emit('overviewRecordDetails', this.record)
        }
      }
      this.detailsLoading = false
    },
    refreshDetails() {
      this.loadFloorDetails(true)
    },
  },
}
</script>
