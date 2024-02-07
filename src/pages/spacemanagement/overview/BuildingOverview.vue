<template>
  <div v-if="isLoading">
    <spinner :show="true"></spinner>
  </div>
  <div v-else class="fc-v1-site-overview-main-sec site-scrollable-header">
    <BuildingHeader
      v-if="record && !isLoading"
      :moduleName="moduleName"
      :record="record"
      :isApprovalEnabled="isApprovalEnabled"
      :refreshDetails="() => refreshDetails()"
      :canEdit="canEdit"
    ></BuildingHeader>

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
      class="site-custom-f-page"
    ></page>
    <SpaceManagentForms
      @saved="moduleName => refreshDetails(moduleName)"
    ></SpaceManagentForms>
  </div>
</template>
<script>
import Page from '@/page/PageBuilder'
import SpaceManagentForms from 'pages/spacemanagement/overview/components/SpaceManagementForms'
import { eventBus } from '@/page/widget/utils/eventBus'
import BuildingHeader from './components/BuildingDetailsHeader'
import { API } from '@facilio/api'
import ApprovalBar from '@/approval/ApprovalBar'
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'

export default {
  components: {
    Page,
    SpaceManagentForms,
    BuildingHeader,
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
      this.$store.dispatch('view/loadModuleMeta', 'building'),
      this.loadBuildingDetails(),
    ]
    Promise.all(promises).then(() => {
      this.loading = false
    })
    eventBus.$on('refreshDetails', this.loadBuildingDetails)
    eventBus.$on('refresh-overview', this.loadBuildingDetails)
  },

  beforeDestroy() {
    eventBus.$off('refreshDetails', this.loadBuildingDetails)
    eventBus.$off('refresh-overview', this.loadBuildingDetails)
  },

  computed: {
    ...mapGetters(['isStatusLocked', 'getApprovalStatus']),

    siteId() {
      return parseInt(this.$route.params.siteid)
    },
    buildingId() {
      return parseInt(this.$route.params.buildingid)
    },
    moduleName() {
      return 'building'
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
    buildingId() {
      this.loadBuildingDetails()
    },
  },

  methods: {
    async loadBuildingDetails(reloadTree) {
      this.detailsLoading = true
      let { building, error } = await API.fetchRecord(
        'building',
        {
          id: this.buildingId,
        },
        { force: true }
      )
      if (!isEmpty(error)) {
        let { message = 'Error Occured while fetching Building' } = error
        this.$message.error(message)
      } else {
        this.record = building
        if (reloadTree) {
          eventBus.$emit('reloadTree')
        } else {
          eventBus.$emit('overviewRecordDetails', this.record)
        }
      }
      this.detailsLoading = false
    },
    refreshDetails() {
      this.loadBuildingDetails(true)
    },
    dropDownAction(action) {
      if (action === 'delete') {
        this.invokeDeleteDialog(this.record)
      }
    },
    async invokeDeleteDialog(data) {
      let promptObj = {
        title: this.$t(`space.sites.delete_building`),
        message: this.$t(`space.sites.delete_building_msg`),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      }
      let { id } = data
      let value = await this.$dialog.confirm(promptObj)
      if (value)
        API.post('/v2/space/delete', { id }).then(({ error }) => {
          if (!error) {
            this.$message.success(this.$t('space.sites.delete_success'))
          } else {
            this.$message.error(error.message || 'Error Occurred')
          }
        })
    },
  },
}
</script>
