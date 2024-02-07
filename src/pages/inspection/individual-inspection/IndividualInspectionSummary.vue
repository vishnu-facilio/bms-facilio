<template>
  <div class="qanda-response-summary">
    <div v-if="loading" class="flex-middle fc-empty-white">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-if="!$validation.isEmpty(record)" class="header pT10 pB15 pL20 pR20">
      <div class="qanda-details">
        <div class="d-flex flex-middle">
          <div class="mL5">
            <div class="qanda-id mT10">
              <i
                v-if="$account.portalInfo"
                class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                content="back"
                arrow
                v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                @click="back"
              ></i>
              #{{ record && record.id }}
            </div>
            <div class="qanda-name mb5 d-flex">
              {{ $getProperty(record, 'name', '') }}

              <div
                v-if="isStateFlowEnabled && currentModuleState"
                class="fc-badge text-uppercase inline vertical-middle mL15"
              >
                {{ currentModuleState }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div
        class="d-flex flex-direction-row align-center"
        style="margin-left: auto;"
      >
        <CustomButton
          class="p10"
          :record="record"
          :moduleName="moduleName"
          :position="POSITION.SUMMARY"
          @refresh="refreshObj(true)"
          @onError="() => {}"
        />
        <template v-if="isStateFlowEnabled">
          <TransitionButtons
            class="mR10"
            :key="record.id"
            :moduleName="moduleName"
            :record="record"
            :disabled="isApprovalEnabled"
            buttonClass="asset-el-btn"
            @currentState="() => {}"
            @transitionSuccess="refreshObj(true)"
            @transitionFailure="() => {}"
          ></TransitionButtons>
        </template>
        <el-button
          @click="openLiveForm"
          v-if="canShowLiveForm"
          class="open-survey-btn"
          >{{ liveFormBtnText }}</el-button
        >

        <el-dropdown
          class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
          trigger="click"
          @command="dropDownActions"
        >
          <span class="el-dropdown-link">
            <inline-svg
              src="svgs/menu"
              class="vertical-middle"
              iconClass="icon icon-md"
            >
            </inline-svg>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item :command="'downloadPdf'">{{
              $t('qanda.response.download_pdf')
            }}</el-dropdown-item>
            <el-dropdown-item :command="'openPrintPreview'">{{
              $t('qanda.response.print')
            }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <iframe
      v-if="downloadUrl"
      :src="downloadUrl"
      style="display: none;"
    ></iframe>
    <Page
      v-if="!$validation.isEmpty(record)"
      :key="record.id"
      :module="moduleName"
      :id="record.id"
      :details="record"
      :primaryFields="primaryFields"
      :notesModuleName="notesModuleName"
      :attachmentsModuleName="attachmentsModuleName"
      :isV3Api="true"
    ></Page>
    <router-view :key="`child-${id}`"></router-view>
  </div>
</template>

<script>
import Page from '@/page/PageBuilder'
import { API } from '@facilio/api'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import CustomButton from '@/custombutton/CustomButton'
import TransitionButtons from '@/stateflow/TransitionButtons'
import { mapState, mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { getApp } from '@facilio/router'
export default {
  props: ['moduleName'],
  components: { Page, CustomButton, TransitionButtons },
  data() {
    return {
      record: null,
      loading: true,
      activeTab: 'summary',
      primaryFields: [],
      executeBtnLoading: false,
      POSITION: POSITION_TYPE,
      downloadUrl: null,
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
    ...mapGetters(['getTicketStatus', 'isStatusLocked', 'getApprovalStatus']),
    notesModuleName() {
      let { moduleName } = this || {}
      return `${moduleName}notes`
    },
    attachmentsModuleName() {
      let { moduleName } = this || {}
      return `${moduleName}attachments`
    },
    id() {
      let paramId = this.$route.params.id
      return paramId && paramId !== 'null'
        ? parseInt(this.$route.params.id)
        : ''
    },
    isStateFlowEnabled() {
      let hasState = this.$getProperty(this.record, 'moduleState.id')
      let isEnabled = this.$getProperty(
        this.moduleMeta,
        'module.stateFlowEnabled'
      )
      return hasState && isEnabled
    },
    isApprovalEnabled() {
      let { record } = this
      let { approvalFlowId, approvalStatus } = record || {}
      return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
    },
    currentModuleState() {
      let { moduleName, record } = this
      let currentStateId = this.$getProperty(record, 'moduleState.id')
      let currentState = this.getTicketStatus(currentStateId, moduleName)
      let { displayName, status } = currentState || {}

      if (!isEmpty(displayName)) {
        return displayName
      }
      return status || null
    },
    pdfUrl() {
      let { linkName: appName = 'maintenance' } = getApp() || {}
      return (
        window.location.protocol +
        '//' +
        window.location.host +
        `/${appName}/pdf/inspectionpdf?id=${this.id}`
      )
    },
    canShowLiveForm() {
      let { record, $account } = this
      let peopleId = this.$getProperty(record, 'people.id')
      let assignedToId = this.$getProperty(record, 'assignedTo.id')
      let currUserPeopleId = this.$getProperty($account, 'user.peopleId')
      let userId = this.$getProperty($account, 'user.id')
      let isCurrentUser =
        peopleId === currUserPeopleId || assignedToId === userId
      let responseStatus = this.$getProperty(record, 'responseStatus')
      let canEditRecord = this.$getProperty(this, 'canEditRecord', true)

      let returnVal =
        record &&
        ![1, 4].includes(responseStatus) &&
        !this.$getProperty(record, 'parent.deleted') &&
        isCurrentUser &&
        canEditRecord

      return returnVal
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
    isRecordLocked() {
      let { moduleName, record } = this
      let moduleState = this.$getProperty(record, 'moduleState.id', null)
      return moduleState && this.isStatusLocked(moduleState, moduleName)
    },
    canEditRecord() {
      let { isRecordLocked, isRequestedState } = this
      return !isRecordLocked && !isRequestedState
    },
    liveFormBtnText() {
      let { record } = this
      let currOrg = this.$getProperty(this, '$account.org.id')
      let { picklist } = record || {}
      let valueMap = { inspectionType: 1 }

      if (currOrg !== 17) {
        return 'Conduct Inspection'
      } else {
        if (picklist === valueMap['inspectionType']) return 'Conduct Inspection'
        else return 'Conduct Audit'
      }
    },
  },
  watch: {
    id: {
      handler() {
        this.loadInspectionRecord()
      },
      immediate: true,
    },
  },
  methods: {
    refreshObj() {
      this.loadInspectionRecord()
    },
    async loadInspectionRecord(force = true) {
      let { moduleName, id } = this
      this.loading = true
      let { [moduleName]: data, error } = await API.fetchRecord(
        moduleName,
        {
          id,
        },
        { force }
      )
      if (error) {
        this.$message.error('Error Occured' || error.message)
      } else {
        this.record = data
      }
      this.loading = false
    },
    openLiveForm() {
      let { record, moduleName } = this
      let id = this.$getProperty(record, 'id', '')
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
          })
      } else {
        this.$router.push({
          name: 'inspection-live-form',
          params: { id },
        })
      }
    },
    openPrintPreview() {
      window.open(this.pdfUrl)
    },
    dropDownActions(action) {
      if (!isEmpty(this[action])) this[action]()
    },
    downloadPdf() {
      this.downloadUrl = null
      this.$message({
        message: 'Downloading...',
        showClose: true,
        duration: 0,
      })
      let additionalInfo = {
        showFooter: false,
        footerStyle: 'p {font-size:12px; margin-left:500px}',
        footerHtml:
          '<p>Page  <span class="pageNumber"></span> / <span class="totalPages"></span></p>',
      }
      API.post(`/v2/integ/pdf/create`, {
        url: this.pdfUrl,
        additionalInfo,
      }).then(({ data, error }) => {
        this.$message.closeAll()
        if (error) {
          let { message = 'Unable to fetch quote download link' } = error
          this.$message.error(message)
        } else {
          let { fileUrl } = data || {}
          this.downloadUrl = fileUrl || null
        }
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.qanda-response-summary {
  .header {
    background: #fff;
    display: flex;
  }
  .qanda-details {
    flex-grow: 1;
    text-align: left;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  .qanda-details .qanda-id {
    font-size: 12px;
    color: #39b2c2;
  }
  .qanda-details .qanda-name {
    font-size: 16px;
    color: #324056;
    font-weight: 500;
  }
  .open-survey-btn {
    border-radius: 3px;
    padding: 8px 18px;
    cursor: pointer;
    border: solid 1px #39b2c2;
    background-color: #39b2c2;
    color: #fff;
    letter-spacing: 1.1px;
    text-align: center;
    text-transform: uppercase;
    font-weight: 500;
    font-size: 12px;
    float: right;
    height: 40px;
    cursor: pointer;
    &:hover {
      background-color: #3cbfd0 !important;
      color: #fff !important;
    }
  }
  .fc-badge {
    color: #fff;
    background-color: #23b096;
    padding: 3px 12px 4px;
    font-weight: bold;
  }
}
</style>
