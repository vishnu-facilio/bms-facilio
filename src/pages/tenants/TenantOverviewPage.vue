<template>
  <div
    class="custom-module-overview"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header p20">
        <div class="custom-module-details">
          <div class="d-flex flex-middle align-center">
            <div @click="showAvatarPreview()">
              <tenant-avatar
                :name="false"
                size="xlg"
                :tenant="record"
              ></tenant-avatar>
            </div>
            <div class="fL mL10">
              <div class="custom-module-id">#{{ record && record.id }}</div>
              <div class="custom-module-name d-flex mT5">
                {{ record[mainFieldKey] }}
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
              :key="record.id"
              :moduleName="moduleName"
              :record="record"
              :transformFn="transformFormData"
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
          <el-dropdown
            class="mL15 fc-btn-ico-lg pT5 pB5 pL8 pR8 mtb5 pointer"
            trigger="click"
          >
            <span class="el-dropdown-link">
              <img src="~assets/menu.svg" height="18" width="18" />
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <div @click="editRecord">
                  {{ $t('common._common.edit') }}
                </div>
              </el-dropdown-item>
              <el-dropdown-item>
                <div @click="handleCommand">
                  {{ $t('common._common.create_work_order') }}
                </div>
              </el-dropdown-item>
              <el-dropdown-item>
                <div @click="addTenantContact('tenantcontacts')">
                  {{ $t('common._common.add_tenant_contact') }}
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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
      v-if="photoUrl"
      :visible.sync="showPreviewImage"
      width="60%"
      :append-to-body="true"
      style="z-index: 9999999999;"
    >
      <div>
        <img style="width:100%" :src="photoUrl" />
      </div>
    </el-dialog>
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import { eventBus } from '@/page/widget/utils/eventBus'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import TenantAvatar from '@/avatar/Tenant'

export default {
  components: {
    TenantAvatar,
  },
  extends: CustomModuleSummary,
  data() {
    return {
      updateUrl: `/v2/tenant/update`,
      notesModuleName: 'tenantnotes',
      showChangeStatusDialog: false,
      selectedStatus: 1,
      attachmentsModuleName: 'tenantattachments',
      saving: false,
      recordDetails: {},
      primaryFields: [
        'inTime',
        'outTime',
        'description',
        'primaryContactName',
        'primaryContactEmail',
        'primaryContactPhone',
        'tenantType',
      ],
      showPreviewImage: false,
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', 'tenant')
    this.$store.dispatch('loadSites')
    eventBus.$on('refresh', this.refreshData)
  },
  computed: {
    ...mapState({
      ticketStatus: state => state.ticketStatus.tenant,
      sites: state => state.sites,
    }),
    moduleName() {
      return 'tenant'
    },
    showEdit() {
      return (
        this.$hasPermission(`${this.moduleName}:UPDATE`) && !this.isRecordLocked
      )
    },
    showPhotoField() {
      return true
    },
    isStateFlowEnabled() {
      return true
    },
    showAvatar() {
      return true
    },
    photoFieldName() {
      return 'avatarId'
    },
    tenantStatus() {
      let status = [
        { id: 1, name: 'Active' },
        { id: 2, name: 'Expired' },
      ]
      return status
    },
    photoUrl() {
      let { record } = this
      let { avatarUrl } = record || {}
      return avatarUrl || null
    },
  },
  mounted() {
    eventBus.$on('refesh-parent', () => {
      this.loadCustomModuleData()
    })
  },
  methods: {
    editRecord() {
      let { moduleName } = this
      let { id } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        if (name) {
          this.$router.push({ name, params: { id } })
        }
      } else {
        this.$router.push({
          name: 'tenant-edit',
          params: {
            moduleName,
            id,
          },
        })
      }
    },
    handleCommand() {
      let { record, sites } = this
      let { id, name, siteId } = record || {}
      let usedSite = sites.find(site => site.id === record.siteId)
      let queryParam = {}
      queryParam = {
        tenant: id,
        tenantLabel: name,
        siteId: siteId,
        siteIdLabel: usedSite?.name,
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
      returnObj['tenant'] = {
        id: this.record.id,
      }
      if (data) {
        let { comment } = data
        if (!isEmpty(comment)) {
          data.comment = comment
          delete data.comment
        }
      }
      returnObj['tenant'] = { ...returnObj['tenant'], ...data }
      return returnObj
    },
    editCustomModuleData() {
      let { moduleName } = this
      let { id } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        if (name) {
          this.$router.push({ name, params: { id } })
        }
      } else {
        this.$router.push({
          name: 'tenant-edit',
          params: {
            moduleName,
            id,
          },
        })
      }
    },
    showAvatarPreview() {
      if (this.photoUrl) {
        this.showPreviewImage = true
      }
    },
    openStatusChange() {
      this.showChangeStatusDialog = true
    },
    cancelForm() {
      this.showChangeStatusDialog = false
    },
    getTenantStatus(status) {
      switch (status) {
        case 1:
          return 'Active'
        case 2:
          return 'Expired'
      }
    },
    toggleStatus(status) {
      this.saving = true
      let params = {
        tenantId: parseInt(this.$route.params.id),
        status: status,
      }
      this.showChangeStatusDialog = false
      API.post('/v2/tenant/toggleStatus', params).then(({ error }) => {
        this.saving = false

        if (error) {
          this.$message.error(error)
        } else {
          this.$message.success('Tenant Updated Successfully')
          this.loadCustomModuleData()
        }
      })
    },
    loadCustomModuleData() {
      let { id } = this

      API.post('/v2/tenant/details', { id }).then(({ data, error }) => {
        if (isEmpty(error)) this.recordDetails = data.tenant
      })
    },
    addTenantContact(moduleName) {
      let { id } = this
      let queryParam = {}
      queryParam = {
        tenant: id,
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        if (name) {
          this.$router.push({ name, query: queryParam })
        }
      } else {
        this.$router.push({
          name: 'tenantcontact-new',
          params: {
            moduleName,
          },
          query: queryParam,
        })
      }
    },
    refreshRelatedList() {
      eventBus.$emit('refresh-related-list')
    },
  },
}
</script>
