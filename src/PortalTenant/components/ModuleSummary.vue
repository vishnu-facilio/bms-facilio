<template>
  <div class="custom-module-overview overflow-y-scroll" :class="customClass">
    <div v-if="isLoading" class="text-center width100 pT50 mT50">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else>
      <div class="header pT10 pB15 pL20 pR20">
        <div
          v-if="!$validation.isEmpty(customModuleData)"
          class="custom-module-details"
        >
          <div class="d-flex flex-middle">
            <div v-if="showPhotoField">
              <div v-if="customModuleData[photoFieldName]">
                <img
                  :src="getImage(customModuleData[photoFieldName])"
                  class="img-container"
                />
              </div>
              <div v-else-if="showAvatar">
                <avatar
                  size="lg"
                  :user="{ name: customModuleData.name }"
                ></avatar>
              </div>
            </div>
            <div class="mL5">
              <div class="custom-module-id mT10">
                <i
                  v-if="$account.portalInfo"
                  class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                  content="back"
                  arrow
                  v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                  @click="back"
                ></i>
                #{{ customModuleData[idFieldKey] }}
              </div>
              <div class="custom-module-name mT5 d-flex align-center">
                <div class="d-flex align-center max-width300px">
                  <el-tooltip
                    placement="bottom"
                    effect="dark"
                    :content="$getProperty(customModuleData, mainFieldKey)"
                  >
                    <span class="whitespace-pre-wrap custom-header">{{
                      $getProperty(customModuleData, mainFieldKey)
                    }}</span>
                  </el-tooltip>
                </div>
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
            :record="customModuleData"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="refreshData()"
            @onError="() => {}"
          />
          <template v-if="isStateFlowEnabled">
            <TransitionButtons
              :key="customModuleData.id"
              :moduleName="moduleName"
              :record="customModuleData"
              :disabled="isApprovalEnabled"
              :transitionFilter="transitionFilter"
              buttonClass="asset-el-btn"
              @currentState="() => {}"
              @transitionSuccess="refreshData()"
              @transitionFailure="() => {}"
              class="mR10"
            ></TransitionButtons>
          </template>

          <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
            <ApprovalBar
              :moduleName="moduleName"
              :key="customModuleData.id + 'approval-bar'"
              :record="customModuleData"
              :hideApprovers="shouldHideApprovers"
              @onSuccess="refreshData()"
              @onFailure="() => {}"
              class="approval-bar-shadow"
            ></ApprovalBar>
          </portal>

          <el-dropdown
            v-if="canShowActions"
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
              <el-dropdown-item v-if="canShowEdit" :command="'edit'">{{
                $t('common._common.edit')
              }}</el-dropdown-item>
              <el-dropdown-item v-if="canShowDelete" :command="'delete'">{{
                $t('common._common.delete')
              }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <page
        v-if="customModuleData && customModuleData.id"
        :key="customModuleData.id"
        :module="moduleName"
        :id="customModuleData.id"
        :details="customModuleData"
        :primaryFields="primaryFields"
        :notesModuleName="notesModuleName"
        :attachmentsModuleName="attachmentsModuleName"
        :isV3Api="isV3Api"
      ></page>
    </template>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { mapGetters, mapState } from 'vuex'
import { isRecordLocked, isRequestedState } from 'PortalTenant/util'
import { findRouteForModule, pageTypes } from '@facilio/router'
import Page from '@/page/PageBuilder'
import { isEmpty } from '@facilio/utils/validation'
import { getBaseURL } from 'util/baseUrl'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import TransitionButtons from '@/stateflow/TransitionButtons'
import ApprovalBar from '@/approval/ApprovalBar'
import { eventBus } from '/src/components/page/widget/utils/eventBus.js'

export default {
  name: 'Overview',
  components: { Page, CustomButton, TransitionButtons, ApprovalBar },
  created() {
    this.$store.dispatch('loadApprovalStatus')
    this.$store.dispatch('loadTicketStatus', this.moduleName)
    this.loadMeta()
  },
  data() {
    return {
      metaInfo: null,
      isLoading: false,
      record: null,
      primaryFields: [],
      notesModuleName: 'cmdnotes',
      attachmentsModuleName: 'cmdattachments',
      formFields: null,
      POSITION: POSITION_TYPE,
      customClass: '',
    }
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
      currentTab: state => state.webtabs.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    ...mapGetters(['getTicketStatus']),
    moduleDisplayName() {
      return this.metaInfo?.displayName
    },
    idFieldKey() {
      return 'id'
    },
    shouldHideApprovers() {
      return true
    },
    isV3Api() {
      return true
    },
    mainFieldKey() {
      return 'name'
    },
    canShowEdit() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('UPDATE', currentTab)
    },
    canShowDelete() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('DELETE', currentTab)
    },
    canShowActions() {
      let { canShowEdit, canShowDelete, isNotLocked } = this
      return isNotLocked && (canShowEdit || canShowDelete)
    },
    isNotLocked() {
      let { moduleName, record } = this
      return !isRecordLocked(record, moduleName) && !isRequestedState(record)
    },
    customModuleData() {
      return this.record
    },
    currentView() {
      return this.$attrs.viewname || this.$route?.params?.viewname
    },
    moduleName() {
      return this.$route.params.moduleName
        ? this.$route.params.moduleName
        : this.$attrs.moduleName || ''
    },
    id() {
      let paramId = this.$attrs.id || this.$route.params.id
      return paramId && paramId !== 'null'
        ? parseInt(this.$route.params.id)
        : ''
    },
    isStateFlowEnabled() {
      let { record, moduleMeta } = this
      let hasState = this.$getProperty(record, 'moduleState.id')
      let isCustomModule = this.$getProperty(moduleMeta, 'module.custom')
      let isEnabled = this.$getProperty(moduleMeta, 'module.stateFlowEnabled')
      return hasState && (!isCustomModule || (isCustomModule && isEnabled))
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
    photoFieldName() {
      return 'photoId'
    },
    showPhotoField() {
      let { formFields } = this
      if (formFields) {
        let photoField = formFields.find(field => field.name === 'photo')
        if (photoField) {
          return !photoField.hideField
        } else {
          return false
        }
      }
      return false
    },
    showAvatar() {
      return false
    },
  },
  watch: {
    id: {
      handler() {
        this.loadRecord()
      },
      immediate: true,
    },
    moduleName: {
      handler() {
        this.loadRecord()
      },
    },
  },
  methods: {
    loadMeta() {
      API.get('/module/meta', {
        moduleName: this.moduleName,
      }).then(({ data, error }) => {
        if (!error && data) {
          this.metaInfo = data.meta
        }
      })
    },
    transitionFilter(state) {
      return this.canShowState(state)
    },
    dropDownActions(action) {
      if (action === 'edit') {
        this.editCustomModuleData(this.record)
      } else if (action === 'delete') {
        this.deleteRecord(this.record)
      }
    },
    canShowState(state) {
      let { user } = this.$account
      let { appType } = user || {}
      if (appType) {
        if (appType === 2) {
          return Boolean(state.showInTenantPortal)
        } else if (appType === 3) {
          return Boolean(state.showInVendorPortal)
        }
        return true
      }
      return true
    },
    editCustomModuleData({ id }) {
      let { moduleName } = this
      let route = findRouteForModule(moduleName, pageTypes.EDIT)
      this.$router.push({
        name: route.name,
        params: { id },
      })
    },
    redirectToList() {
      let { moduleName, currentView, $route } = this
      let route = findRouteForModule(moduleName, pageTypes.LIST)
      let { query } = $route
      if (route) {
        this.$router.push({
          name: route.name,
          params: { viewname: currentView },
          query,
        })
      } else {
        console.warn('Could not resolve route')
      }
    },
    async deleteRecord({ id }) {
      let { moduleName, moduleDisplayName } = this

      let value = await this.$dialog.confirm({
        title: `Delete ${moduleDisplayName}`,
        message: `Are you sure you want to delete this ${moduleDisplayName}?`,
        rbDanger: true,
        rbLabel: 'Delete',
      })

      if (!value) {
        return
      }

      let { error } = await API.deleteRecord(moduleName, id)

      if (!error) {
        this.$message.success(this.$t('Deleted Successfully'))
        this.redirectToList()
      } else {
        this.$message.error(error.message || 'Error Occurred')
      }
    },
    refreshData() {
      this.loadRecord(true)
    },
    async loadRecord(force = false, loadPage = true) {
      let { moduleName } = this
      this.isLoading = loadPage
      let { [moduleName]: data, error } = await API.fetchRecord(
        moduleName,
        {
          id: this.id,
        },
        { force }
      )
      if (!isEmpty(error)) {
        let { message = 'Error Occured' } = error
        this.$message.error(message)
      } else {
        this.record = data
        this.getFormMeta()
      }
      eventBus.$emit('postLoadRecordCallback', this.record)
      this.isLoading = false
    },
    modifyRecordData(recordModified) {
      this.$set(this, 'record', recordModified)
    },
    async getFormMeta() {
      let { record: { formId } = {}, moduleName } = this
      let url = `/v2/forms/${moduleName}`

      let { data, error } = await API.get(url, {
        formId: formId || -1,
      })
      if (!error) {
        let formFields = (data.form || {}).fields
        this.formFields = formFields
      }
    },
    getImage(photoId) {
      return `${getBaseURL()}/v2/files/preview/${photoId}`
    },
    back() {
      this.redirectToList()
    },
  },
}
</script>
<style lang="scss" scoped>
.custom-module-overview {
  .header {
    background: #fff;
    display: flex;
  }
  .custom-module-details {
    flex-grow: 1;
    text-align: left;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  .custom-module-details .custom-module-id {
    font-size: 12px;
    color: #39b2c2;
  }
  .custom-module-details .custom-module-name {
    font-size: 16px;
    color: #324056;
    font-weight: 500;
    .custom-header {
      display: -webkit-box;
      max-width: 300px;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }
  .fc-badge {
    color: #fff;
    background-color: #23b096;
    padding: 3px 12px 4px;
    font-weight: bold;
  }
  .img-container {
    width: 40px;
    height: 40px;
    border: 1px solid #f9f9f9;
    border-radius: 50%;
  }
}
.asset-details {
  flex-grow: 1;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.asset-details .asset-id {
  font-size: 12px;
  color: #39b2c2;
}
.asset-details .asset-name {
  font-size: 16px;
  color: #324056;
  font-weight: 500;
}
.asset-details .asset-space {
  font-size: 13px;
  color: #8ca1ad;
}
.fc-badge {
  color: #fff;
  background-color: #23b096;
  padding: 3px 12px 4px;
  font-weight: bold;
}
.img-container {
  width: 40px;
  height: 40px;
  border: 1px solid #f9f9f9;
  border-radius: 50%;
}
.delete-dialog-footer {
  display: flex;
  justify-content: center;
}
.dialog-d >>> .el-dialog__header {
  padding: 0px;
}
.delete-dissociate-buttons {
  letter-spacing: 1px;
  text-align: center;
  color: #ffffff;
  font-size: 12px;
  background-color: #ec7c7c;
}
</style>
