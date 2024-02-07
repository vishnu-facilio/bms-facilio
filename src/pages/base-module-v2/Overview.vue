<template>
  <div
    class="custom-modules-overview"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <PortalAccessDialog
      v-if="showActionDialog"
      :user="customModuleData"
      :appLinkNames="allowedPortals"
      :onSave="savePortalUser"
      @onClose="hideDialog"
    ></PortalAccessDialog>

    <div v-if="!isLoading" class="header pT10 pB15 pL20 pR20">
      <div v-if="!$validation.isEmpty(customModuleData)" class="asset-details">
        <div class="d-flex flex-middle">
          <div v-if="showPhotoField" class="mR5">
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
            <div class="asset-id mT10">
              <i
                v-if="$account.portalInfo"
                class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                content="back"
                arrow
                v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                @click="back"
              ></i>
              #{{ customModuleData.id }}
            </div>
            <div class="asset-name mb5 d-flex">
              {{ customModuleData[mainFieldKey] }}
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
      <div class="d-flex flex-direction-row" style="margin-left: auto;">
        <!-- Spreading record.data because customButton expects v3. Remove when custom modules
          are changed to v3 apis
         -->
        <CustomButton
          class="p10"
          :record="{
            ...customModuleData,
            ...(customModuleData ? customModuleData.data || {} : {}),
          }"
          :moduleName="moduleName"
          :position="POSITION.SUMMARY"
          @refresh="refreshObj()"
          @onError="() => {}"
        />
        <el-button-group
          class="fc-group-btn2 flex-middle mR10"
          v-if="$org.id === 321 && moduleName === 'custom_tenantbilling'"
        >
          <el-button
            type="primary"
            @click="summaryDownload"
            style="height: 39px;"
          >
            <inline-svg
              src="svgs/download2"
              iconClass="icon vertical-middle icon-sm fill-grey2"
            ></inline-svg>
          </el-button>
          <el-button
            type="primary"
            @click="openPrintPreview"
            style="height: 39px;"
          >
            <inline-svg
              src="svgs/print2"
              iconClass="icon vertical-middle icon-sm fill-grey2"
            ></inline-svg>
          </el-button>
        </el-button-group>
        <iframe
          v-if="downloadUrl"
          :src="downloadUrl"
          style="display: none;"
        ></iframe>

        <template v-if="isStateFlowEnabled">
          <TransitionButtons
            class="mR10"
            :key="customModuleData.id"
            :moduleName="moduleName"
            :record="customModuleData"
            :updateUrl="updateUrl"
            :transformFn="transformFormData"
            :disabled="isApprovalEnabled"
            buttonClass="asset-el-btn"
            @currentState="() => {}"
            @transitionSuccess="refreshObj()"
            @transitionFailure="() => {}"
          ></TransitionButtons>
        </template>

        <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
          <ApprovalBar
            :moduleName="moduleName"
            :key="customModuleData.id + 'approval-bar'"
            :record="customModuleData"
            :updateUrl="updateUrl"
            :transformFn="transformFormData"
            :hideApprovers="shouldHideApprovers"
            @onSuccess="refreshObj()"
            @onFailure="() => {}"
            class="approval-bar-shadow"
          ></ApprovalBar>
        </portal>

        <el-button
          type="button"
          v-if="showEdit"
          class="fc-wo-border-btn pL15 pR15 self-center"
          @click="editCustomModuleData(customModuleData)"
        >
          <i class="el-icon-edit" title="Edit" v-tippy></i>
        </el-button>
        <el-button
          type="button"
          v-if="
            moduleName === 'tenantcontact' || moduleName === 'vendorcontact'
          "
          class="fc-wo-border-btn pL15 pR15 self-center"
          @click="showDialog(customModuleData)"
        >
          <i class="el-icon-setting" title="Application Access" v-tippy></i>
        </el-button>
        <el-dropdown
          trigger="click"
          v-if="moduleName === 'employee'"
          class="fc-btn-ico-lg mL15 fc-btn-ico-lg pT5 pB5 pL8 pR8 pointer height40 mT4"
        >
          <div class="flex-middle" ref>
            <i class="el-icon-more pointer overview-icon-more-btn"></i>
            <span class="pointer mL-auto text-fc-pink child-add"></span>
          </div>

          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>
              <div @click="showDialog(customModuleData)">Portal Access</div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-dropdown
          class="mL10 custom-module-dropdown self-center"
          trigger="hover"
          @command="moduleName => openNewForm(moduleName)"
          v-if="!$validation.isEmpty(subModulesList)"
        >
          <el-button class="fc-wo-border-btn pL15 pR15" type="primary">
            <i class="el-icon-plus"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(module, index) in subModulesList"
              :key="index"
              :command="module.name"
            >
              {{
                `Convert to ${
                  $constants.moduleSingularDisplayNameMap[module.name]
                    ? $constants.moduleSingularDisplayNameMap[module.name]
                    : module.displayName
                }`
              }}
            </el-dropdown-item>
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
    <el-dialog
      :visible.sync="showDeleteDialog"
      class="dialog-d"
      custom-class="setup-dialog45"
      :show-close="false"
    >
      <div class="text-center fc-black-20">
        Do you want to delete or dissociate from
        {{ moduleName ? moduleName : '' }} ?
      </div>
      <span
        slot="footer"
        class="fc-dialog-center-container delete-dialog-footer padding-px18"
      >
        <el-button @click="showDeleteDialog = false">CANCEL</el-button>
        <el-button class="delete-dissociate-buttons" @click="dissociate()"
          >DISSOCIATE</el-button
        >
        <el-button class="delete-dissociate-buttons" @click="deleteRecord()"
          >MOVE TO RECYCLE BIN</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
import Page from '@/page/PageBuilder'
import Avatar from '@/Avatar'
import TransitionButtons from '@/stateflow/TransitionButtons'
import PortalAccessDialog from './PortalAccessDialog'
import { mapState, mapGetters } from 'vuex'
import { eventBus } from '@/page/widget/utils/eventBus'
import { isEmpty } from '@facilio/utils/validation'
import ApprovalBar from '@/approval/ApprovalBar'
import { API } from '@facilio/api'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { getBaseURL } from 'util/baseUrl'

export default {
  components: {
    Page,
    TransitionButtons,
    ApprovalBar,
    Avatar,
    CustomButton,
    PortalAccessDialog,
  },
  data() {
    return {
      updateUrl: `/v2/module/data/update`,
      customModuleDataObj: {},
      showDeleteDialog: false,
      notesModuleName: 'cmdnotes',
      attachmentsModuleName: 'cmdattachments',
      primaryFields: ['name', 'photo', 'avatar'],
      customClass: null,
      isLoading: false,
      showChangeStatusDialog: false,
      showActionDialog: false,
      showEmployeeActionDialog: false,
      downloadUrl: null,
      POSITION: POSITION_TYPE,
      formFields: [],
      allowedPortals: [],
    }
  },
  created() {
    let { moduleName } = this
    this.$store.dispatch('loadTicketStatus', moduleName || '')
    this.$store.dispatch('loadApprovalStatus')
    this.$store.dispatch('loadSites')
    eventBus.$on('show-delete-dialog', relatedObj => {
      let { showDeleteDialog } = relatedObj
      this.openDeleteDialog(showDeleteDialog)
    })
  },
  mounted() {
    this.loadCustomModuleData()
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
      sites: state => state.sites,
    }),
    ...mapGetters(['getApprovalStatus', 'isStatusLocked', 'getTicketStatus']),
    moduleName() {
      return this.$route.params.moduleName
        ? this.$route.params.moduleName
        : this.$attrs.moduleName || ''
    },
    isV3Api() {
      return false
    },
    id() {
      let paramId = this.$attrs.id || this.$route.params.id
      return paramId && paramId !== 'null'
        ? parseInt(this.$route.params.id)
        : ''
    },
    customModuleData() {
      return this.$store.state.customModule.customModuledata
    },
    isStateFlowEnabled() {
      let hasState = this.$getProperty(this.customModuleData, 'moduleState.id')
      let isCustomModule = this.$getProperty(this.moduleMeta, 'module.custom')
      let isEnabled = this.$getProperty(
        this.moduleMeta,
        'module.stateFlowEnabled'
      )
      return hasState && (!isCustomModule || (isCustomModule && isEnabled))
    },
    isApprovalEnabled() {
      let { customModuleData } = this
      let { approvalFlowId, approvalStatus } = customModuleData || {}
      return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
    },
    currentModuleState() {
      let { moduleName, customModuleData } = this
      let currentStateId = this.$getProperty(customModuleData, 'moduleState.id')
      let currentState = this.getTicketStatus(currentStateId, moduleName)
      let { displayName, status } = currentState || {}

      if (!isEmpty(displayName)) {
        return displayName
      }
      return status || null
    },
    isRecordLocked() {
      let { customModuleData, moduleName } = this

      if (!this.isStateFlowEnabled) {
        return false
      } else {
        let moduleState = this.$getProperty(customModuleData, 'moduleState.id')
        return (
          moduleState &&
          this.isStatusLocked(customModuleData.moduleState.id, moduleName)
        )
      }
    },
    isRequestedState() {
      let { customModuleData } = this
      let { approvalStatus } = customModuleData || {}

      if (isEmpty(approvalStatus)) {
        return false
      } else {
        let statusObj = this.getApprovalStatus(approvalStatus.id)
        return this.$getProperty(statusObj, 'requestedState', false)
      }
    },
    mainFieldKey() {
      return 'name'
    },
    photoFieldName() {
      return 'photoId'
    },
    showEdit() {
      let canShowEdit
      if (this.$constants.isCustomModulePermissionsEnabled(this.$org.id)) {
        canShowEdit = this.$hasPermission(`${this.moduleName}:UPDATE`)
      } else {
        canShowEdit = true
      }
      let isNotLocked = this.isStateFlowEnabled
        ? !this.isRecordLocked && !this.isRequestedState
        : true
      return canShowEdit && isNotLocked
    },
    showPhotoField() {
      if (this.formFields) {
        let photoField = this.formFields.find(field => field.name === 'photo')
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
    subModulesList() {
      return []
    },
    shouldHideApprovers() {
      return false
    },
    isSuperAdmin() {
      return (
        (
          this.userRoles.find(
            role => role.id === this.customModuleData.roleId
          ) || {}
        ).name === 'Super Administrator'
      )
    },
    pdfUrl() {
      return (
        window.location.protocol +
        '//' +
        window.location.host +
        `/app/pdf/tenantbilling?id=${this.customModuleData.id}`
      )
    },
  },
  watch: {
    id(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.loadCustomModuleData()
      }
    },
  },
  methods: {
    handleCommand() {
      let { customModuleData, sites } = this
      let usedSite = sites.find(site => site.id === customModuleData.siteId)
      let queryParam = {}
      queryParam = {
        tenant: customModuleData.id,
        tenantLabel: customModuleData.name,
        siteId: customModuleData.siteId,
        siteIdLabel: usedSite.name,
      }
      if (this.$account.org.orgId === 320) {
        queryParam.resource = customModuleData.data.lookup.id
        queryParam.resourceLabel = customModuleData.data.lookup.name
      }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.CREATE) || {}

        if (name) {
          this.$router.push({ name, query: queryParam })
        }
      } else {
        this.$router.push({
          path: `/app/wo/create`,
          query: queryParam,
        })
      }
    },
    transformFormData(returnObj, data) {
      returnObj['moduleData'] = {
        id: this.customModuleData.id,
      }

      if (data) {
        if (data.comment) {
          returnObj.comment = data.comment
          delete data.comment
        }

        returnObj['moduleData'] = { ...returnObj['moduleData'], ...data }

        if (data.warrantyExpiryDate) {
          returnObj['moduleData'] = {
            ...returnObj['moduleData'],
            warrantyExpiryData: Date.parse(data.warrantyExpiryDate),
          }
        }
      }
      return returnObj
    },
    async getFormMeta() {
      let { customModuleData: { formId } = {}, moduleName } = this
      let url = `/v2/forms/${moduleName}`

      let { data, error } = await API.get(url, {
        formId: formId || -1,
      })
      if (!error) {
        let formFields = (data.form || {}).fields
        this.formFields = formFields
      }
    },
    loadCustomModuleData() {
      let { moduleName, id } = this
      let queryObj = {
        id: id,
        moduleName: moduleName,
      }
      this.$set(this, 'isLoading', true)
      this.$store
        .dispatch('customModule/fetch', queryObj)
        .then(() => {
          this.getFormMeta()
        })
        .catch(error => {
          error && this.$message.error(error)
        })
        .finally(() => {
          this.isLoading = false
        })
    },
    refreshObj() {
      this.loadCustomModuleData()
    },
    editCustomModuleData() {
      let { moduleName, id } = this

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
        let creationName = this.$helpers.isEtisalat()
          ? this.getEtisalatRouterName().edit
          : 'custommodules-edit'

        this.$router.push({
          name: creationName,
          params: {
            moduleName,
            id,
          },
        })
      }
    },
    getEtisalatRouterName() {
      let data = {
        new: 'custommodules-new',
        edit: 'custommodules-edit',
        list: 'custommodules-list',
        summary: 'custommodules-summary',
      }
      if (this.$route && this.$route.name) {
        let { name } = this.$route
        if (
          name === 'et1custommodules-list' ||
          name === 'et1custommodules-new' ||
          name === 'et1custommodules-edit' ||
          name === 'et1custommodules-summary'
        ) {
          ;(data['new'] = 'et1custommodules-new'),
            (data['edit'] = 'et1custommodules-edit')
          data['list'] = 'et1custommodules-edit'
          data['summary'] = 'et1custommodules-summary'

          data['modulePath'] = 'supp'
        } else if (
          name === 'et-custommodules-list' ||
          name === 'et-custommodules-edit' ||
          name === 'et-custommodules-new' ||
          name === 'et-custommodules-summary'
        ) {
          ;(data['new'] = 'et-custommodules-new'),
            (data['edit'] = 'et-custommodules-edit')
          data['list'] = 'et-custommodules-list'
          data['summary'] = 'et-custommodules-summary'
          data['modulePath'] = 'al'
        } else if (
          name === 'et2-custommodules-list' ||
          name === 'et2-custommodules-edit' ||
          name === 'et2-custommodules-new' ||
          name === 'et2-custommodules-summary'
        ) {
          ;(data['new'] = 'et2-custommodules-new'),
            (data['edit'] = 'et2-custommodules-edit')
          data['list'] = 'et2-custommodules-list'
          data['summary'] = 'et2-custommodules-summary'
          data['modulePath'] = 'home'
        }
      }
      return data
    },
    getImage(photoId) {
      return `${getBaseURL()}/v2/files/preview/${photoId}`
    },
    openDeleteDialog(showDeleteDialog) {
      this.showDeleteDialog = showDeleteDialog
    },
    deleteRecord() {
      eventBus.$emit('delete-record')
    },
    dissociate() {
      eventBus.$emit('dissociate')
    },
    back() {
      this.$router.go(-1)
    },
    showDialog() {
      this.showActionDialog = true
    },
    hideDialog() {
      this.showActionDialog = false
    },
    openNewForm() {
      return
    },
    // Temp added for demo.
    // Please remove after checking if still required
    summaryDownload() {
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
    openPrintPreview() {
      window.open(this.pdfUrl)
    },
  },
}
</script>
<style scoped>
.custom-modules-overview .header {
  background: #fff;
  display: flex;
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
