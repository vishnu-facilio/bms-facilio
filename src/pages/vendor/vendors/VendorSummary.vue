<template>
  <div class="custom-module-overview vendor-overview" :class="customClass">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header p20">
        <div class="d-flex flex-middle">
          <div @click="showAvatarPreview()">
            <img
              v-if="record[photoFieldName] > 0"
              :src="record.getImage(photoFieldName)"
              class="vendor-img-container pointer"
            />

            <tenant-avatar
              v-else
              :name="false"
              size="xlg"
              :tenant="record"
            ></tenant-avatar>
          </div>
          <div class="fL custom-module-details d-flex mL10">
            <div class="custom-module-id">#{{ record && record.id }}</div>
            <div class="custom-module-name d-flex mT5">
              <div class="d-flex max-width300px ">
                <el-tooltip
                  placement="bottom"
                  effect="dark"
                  :content="record[mainFieldKey]"
                >
                  <span class="whitespace-pre-wrap custom-header">{{
                    record[mainFieldKey]
                  }}</span>
                </el-tooltip>
              </div>
              <div
                v-if="
                  record.isStateFlowEnabled() && record.currentModuleState()
                "
                class="fc-badge text-uppercase inline vertical-middle mL15"
              >
                {{ record.currentModuleState() }}
              </div>
            </div>
          </div>
        </div>
        <div class="marginL-auto flex-middle">
          <CustomButton
            class="pR10"
            :record="record"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="refreshData()"
            @onError="() => {}"
          />
          <template v-if="record.isStateFlowEnabled()">
            <TransitionButtons
              class="mR10"
              :key="`${record.id}transitions`"
              :moduleName="moduleName"
              :record="record"
              :disabled="record.isApprovalEnabled()"
              buttonClass="asset-el-btn"
              @currentState="() => {}"
              @transitionSuccess="refreshData()"
              @transitionFailure="() => {}"
            ></TransitionButtons>
          </template>
          <portal to="pagebuilder-sticky-top" v-if="record.isApprovalEnabled()">
            <ApprovalBar
              :moduleName="moduleName"
              :key="record.id + 'approval-bar'"
              :record="record"
              :hideApprovers="shouldHideApprovers"
              @onSuccess="refreshData()"
              @onFailure="() => {}"
              class="approval-bar-shadow"
            ></ApprovalBar>
          </portal>
          <el-button
            v-if="showEdit"
            type="button"
            class="fc-wo-border-btn pL15 pR15 self-center"
            @click="editRecord"
          >
            <i class="el-icon-edit"></i>
          </el-button>
        </div>
      </div>

      <Page
        :key="record.id"
        :module="moduleName"
        :id="record.id"
        :details="record"
        :primaryFields="primaryFields"
        :notesModuleName="notesModuleName"
        :isV3Api="true"
        :attachmentsModuleName="attachmentsModuleName"
      ></Page>
    </template>
    <el-dialog
      v-if="photoId"
      :visible.sync="showPreviewImage"
      width="60%"
      :append-to-body="true"
      style="z-index: 9999999999;"
    >
      <img style="width:100%" :src="record.getImage(photoFieldName)" />
    </el-dialog>
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import TenantAvatar from '@/avatar/Tenant'

import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: CustomModuleSummary,
  props: ['viewname'],
  components: {
    TenantAvatar,
  },
  data() {
    return {
      notesModuleName: 'vendorsNotes',
      attachmentsModuleName: 'vendorattachments',
      showPreviewImage: false,
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
  },
  computed: {
    ...mapState({
      ticketStatus: state => state.ticketStatus,
    }),
    states() {
      let { ticketStatus, moduleName, $getProperty } = this
      return ticketStatus ? $getProperty(ticketStatus, moduleName) : []
    },
    moduleName() {
      return 'vendors'
    },
    mainFieldKey() {
      return 'name'
    },
    moduleState() {
      let currentStateId = this.$getProperty(this.record, 'moduleState.id')
      let currentState = (this.states || []).find(
        state => state.id === currentStateId
      )

      return currentState ? currentState.displayName : null
    },
    photoId() {
      return this.$getProperty(this.record, 'vendorLogoId', null)
    },
    photoFieldName() {
      return 'vendorLogoId'
    },
  },
  title() {
    'Vendor'
  },
  methods: {
    showAvatarPreview() {
      if (this.photoId) {
        this.showPreviewImage = true
      }
    },
    async savePortalUser(userObj) {
      let {
        id,
        email,
        isTenantPortalAccess,
        isOccupantPortalAccess,
        rolesMap,
      } = userObj || {}

      if (isEmpty(email)) {
        this.$message.error(
          this.$t('common._common.please_enter_valid_email_enabling')
        )
        return
      }

      let { moduleName } = this
      let params = {
        data: { id, isTenantPortalAccess, isOccupantPortalAccess, rolesMap },
        moduleName,
        id,
      }

      let { error } = await API.updateRecord(moduleName, params)

      if (error) {
        this.$message.error(
          this.$t('home.tenantcontact.contact_update_failure')
        )
      } else {
        this.$message.success(
          this.$t('home.tenantcontact.contact_update_success')
        )
        this.hideDialog()
        this.refreshData()
      }
    },
    editRecord() {
      let { id } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
          })
      } else {
        id &&
          this.$router.push({
            name: 'vendorEdit',
            params: { id },
          })
      }
    },
  },
}
</script>
<style lang="scss">
.vendor-overview .vendor-img-container {
  width: 50px;
  height: 50px;
  border: 1px solid #f9f9f9;
  border-radius: 50%;
}
</style>
