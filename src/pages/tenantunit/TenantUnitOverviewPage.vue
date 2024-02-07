<template>
  <div class="custom-module-overview" :class="customClass">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header p20">
        <div class="fL custom-module-details">
          <div class="custom-module-id">#{{ record && record.id }}</div>
          <div
            class="custom-module-name d-flex flex-direction-row align-center"
          >
            <span
              v-if="decommission"
              v-tippy
              :title="$t('setup.decommission.decommissioned')"
              class="align-center mR5 flex pointer"
              ><fc-icon
                group="alert"
                class="pR5"
                name="decommissioning"
                size="16"
              ></fc-icon
            ></span>
            <span>
              {{ record[mainFieldKey] }}
            </span>
            <div
              v-if="moduleState"
              class="fc-badge text-uppercase inline vertical-middle mL15"
            >
              {{ moduleState }}
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
            v-if="showMarkVacant"
            slot="reference"
            class="fc__border__btn"
            @click="markVacant()"
          >
            {{ 'Mark Vacant' }}
          </el-button>
          <el-dropdown
            class="mL15 fc-btn-ico-lg pT5 pB5 pL8 pR8 mtb5 pointer"
            trigger="click"
          >
            <span class="el-dropdown-link">
              <img src="~assets/menu.svg" height="18" width="18" />
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-if="hasPermission('UPDATE,UPDATE_OWN')">
                <div @click="editRecord">
                  {{ $t('common._common.edit') }}
                </div>
              </el-dropdown-item>
              <el-dropdown-item>
                <div @click="handleCommand">
                  {{ $t('common._common.create_work_order') }}
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
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { API } from '@facilio/api'

export default {
  extends: CustomModuleSummary,
  props: ['viewname'],
  data() {
    return {
      notesModuleName: 'basespacenotes',
      attachmentsModuleName: 'basespaceattachments',
      primaryFields: [
        'area',
        'building',
        'site',
        'description',
        'tenant',
        'floor',
      ],
      customClass: 'tenantunit-summary',
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
  },
  computed: {
    states() {
      let { ticketStatus, moduleName, $getProperty } = this
      return ticketStatus ? $getProperty(ticketStatus, moduleName) : []
    },
    moduleName() {
      return 'tenantunit'
    },
    mainFieldKey() {
      return 'name'
    },
    moduleState() {
      let { record } = this
      return (this.$getProperty(record, 'moduleState', null) || {}).displayName
    },
    showMarkVacant() {
      let { record } = this
      let { occupied } = record || {}
      return this.$hasPermission('tenantunit:UPDATE') && (occupied || false)
    },
    decommission() {
      return this.$getProperty(this, 'record.decommission', false)
    },
  },
  title() {
    'Tenant Unit'
  },
  methods: {
    hasPermission(action) {
      return this.$hasPermission(`tenantunit:${action}`)
    },
    showDialog() {
      this.showActionDialog = true
    },
    hideDialog() {
      this.showActionDialog = false
    },
    editRecord() {
      let { moduleName, id } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        name && this.$router.push({ name, params: { id } })
      } else {
        let creationName = 'tenantunit-edit'
        this.$router.push({ name: creationName, params: { moduleName, id } })
      }
    },
    markVacant() {
      let { record } = this
      let { id } = record || {}
      this.$dialog
        .confirm({
          title: 'Confirmation',
          message: 'Are you sure you want to mark the unit vacant?',
          rbDanger: true,
          rbLabel: 'Mark Vacant',
        })
        .then(value => {
          if (value) {
            let params = {
              data: {},
              id: id,
              moduleName: 'tenantunit',
            }
            API.post('v3/modules/data/patch?markVacant=true', params).then(
              async ({ error }) => {
                if (error) {
                  this.$message.error(error.message)
                } else {
                  this.$message.success('Tenant unit is marked as vacant')
                  this.refreshData()
                }
              }
            )
          }
        })
    },
    handleCommand() {
      let { record } = this
      let { id, site, name } = record || {}
      let queryParam = {}
      queryParam = {
        resource: id,
        resourceLabel: name,
        siteId: site.id,
        siteIdLabel: site.name,
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
  },
}
</script>
