<template>
  <div v-if="isLoading">
    <spinner :show="true"></spinner>
  </div>
  <div v-else class="fc-v1-site-overview-main-sec">
    <SpaceDetailsHeader
      v-if="record && !isLoading"
      :record="record"
      :moduleName="moduleName"
      :isApprovalEnabled="isApprovalEnabled"
      :onTransitionSuccess="() => refreshDetails()"
      :canEdit="canEdit"
    ></SpaceDetailsHeader>

    <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
      <ApprovalBar
        :moduleName="moduleName"
        :key="record.id + 'approval-bar'"
        :record="record"
        @onSuccess="refreshDetails"
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
import SpaceDetailsHeader from 'pages/spacemanagement/overview/components/SpaceDetailsHeader'
import { eventBus } from '@/page/widget/utils/eventBus'
import ApprovalBar from '@/approval/ApprovalBar'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { mapGetters } from 'vuex'

export default {
  components: {
    Page,
    SpaceManagentForms,
    SpaceDetailsHeader,
    ApprovalBar,
  },
  data() {
    return {
      loading: false,
      primaryFields: [],
      record: null,
      detailsLoading: false,
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
      this.loadSpaceDetails(),
      this.$store.dispatch('view/loadModuleMeta', 'space'),
    ]

    Promise.all(promises).then(() => {
      this.loading = false
    })
    eventBus.$on('refreshDetails', this.loadSpaceDetails)
    eventBus.$on('refresh-overview', this.loadSpaceDetails)
  },
  beforeDestroy() {
    eventBus.$off('refreshDetails', this.loadSpaceDetails)
    eventBus.$off('refresh-overview', this.loadSpaceDetails)
  },
  computed: {
    ...mapGetters(['isStatusLocked', 'getApprovalStatus']),
    siteId() {
      return parseInt(this.$route.params.siteid)
    },
    spaceId() {
      return parseInt(this.$route.params.id)
    },
    moduleName() {
      return 'space'
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
    spaceId(newVal, oldVal) {
      if (newVal != oldVal) {
        this.loadSpaceDetails()
      }
    },
  },
  methods: {
    async loadSpaceDetails(reloadTree) {
      this.detailsLoading = true
      let { space, error } = await API.fetchRecord(
        'space',
        {
          id: this.spaceId,
        },
        { force: true }
      )
      if (!isEmpty(error)) {
        let { message = 'Error Occured while fetching Space' } = error
        this.$message.error(message)
      } else {
        this.record = space
        if (reloadTree) {
          eventBus.$emit('reloadTree')
        } else {
          eventBus.$emit('overviewRecordDetails', this.record)
        }
      }
      this.detailsLoading = false
    },
    refreshDetails() {
      this.loadSpaceDetails(true)
    },
  },
}
</script>
