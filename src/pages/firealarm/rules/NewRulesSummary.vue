<template>
  <div style="height: auto; overflow-y: scroll;">
    <div v-if="ruleOverviewLoading" class="flex-middle fc-empty-white">
      <spinner :show="ruleOverviewLoading" size="80"></spinner>
    </div>
    <div
      v-else-if="rule && rule"
      class="fc__layout__align fc__asset__main__header pT20 pB20 pL20 pR20"
      style="width: auto; align-items: center !important; border-bottom: none;height: 70px;"
    >
      <div v-if="rule && rule" class="flex-middle">
        <div class="wos-id" @click="goBack()" :title="$t('common.header.back')">
          <span class="fc-id">#{{ rule.id }}</span>
        </div>
        <div class="fc-black-22 fw3 flex-middle">
          <div class="fc-separator-lg mL20 mR20"></div>
          <div class="max-width500px textoverflow-ellipsis">
            {{ rule.name }}
          </div>
          <div class="fc-newsummary-chip">
            {{
              rule.status
                ? $t('common._common.active')
                : $t('common._common.inactive')
            }}
          </div>
        </div>
      </div>
      <div class="display-flex mR10">
        <div class="flRight fc-el-button mL10">
          <el-button
            v-if="$hasPermission('alarmrules:UPDATE,UPDATE_TEAM,UPDATE_OWN')"
            @click=";(isEditLoading = true), editThresholdRule(ruleId)"
            :loading="isEditLoading"
            >{{ $t('common._common.edit') }}</el-button
          >
          <el-button
            v-if="$hasPermission('alarmrules:UPDATE,UPDATE_TEAM,UPDATE_OWN')"
            v-model="rule.status"
            @click="updateStatus(rule)"
            :loading="isLoading"
            type="primary"
          >
            {{
              (rule.status
                ? $t('common._common.deactivate')
                : $t('common._common.activate')) + (isLoading ? 'ing' : '')
            }}
          </el-button>
          <el-dropdown
            class="mL10 fc-btn-ico-lg pointer"
            style="padding-top: 5px; padding-bottom: 5px;position: relative; top: 5px;"
            trigger="click"
          >
            <span class="el-dropdown-link">
              <img src="~assets/menu.svg" width="16" height="16" />
            </span>
            <el-dropdown-menu slot="dropdown" trigger="click" class="p10">
              <div
                class="pT5 pB5 fc-label-hover label-txt-black f14 pL10 pR10 pointer"
                @click="showHistoryDialog = true"
              >
                {{ $t('common.tabs.historical_run') }}
              </div>
              <div
                class="pT5 pB5 fc-label-hover label-txt-black f14 pL10 pR10 pointer"
                @click="enableWO"
              >
                <div v-if="isEditRuleToWo">
                  {{ $t('rule.faultToWorkorder.edit_fault_wo') }}
                </div>
                <div v-else>Fault To WorkOrder</div>
              </div>
              <div
                v-if="isEditRuleToWo"
                class="pT5 pB5 fc-label-hover label-txt-black f14 pL10 pR10 pointer"
                @click="disableFaultWO"
              >
                <span v-if="isFaultToWo">
                  {{ $t('rule.faultToWorkorder.disable_fault_wo') }}
                </span>
                <span v-else>
                  {{ $t('rule.faultToWorkorder.enable_fault_wo') }}
                </span>
              </div>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>
    <historical-run
      v-if="showHistoryDialog"
      :visibility.sync="showHistoryDialog"
      :details="details"
    ></historical-run>

    <RuleWorkOrderForm
      v-if="showForm"
      :visibility.sync="showForm"
      :modules="module"
      :moduleName="moduleWo"
      :module="module.name"
      :isNew="isNew"
      :moduleDisplayName="module.displayName"
      :ruleType="currentRuleType"
      :activityTypes="activityTypes"
      :ruleActions="currentActionTab.actions"
      :currentAction="currentAction"
      :ruleId="ruleId"
      :rulename="rule.name"
      :isEdit="isEditRuleToWo"
      :details="rulewoDetails"
      @loadPage="loadRuleDetails"
    />

    <page
      v-if="rule && rule"
      :key="rule.id"
      :module="currentModuleName"
      :id="rule.id"
      :details="rule"
      :primaryFields="primaryFields"
      :isFaultToWo="isFaultToWo"
    ></page>
  </div>
</template>
<script>
import Page from '@/page/PageBuilder'
import HistoricalRun from '@/page/widget/rule/HistoricalRunDialog'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import RuleWorkOrderForm from '@/page/widget/rule/RuleWorkOrderForm.vue'
import sortBy from 'lodash/sortBy'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  components: { Page, HistoricalRun, RuleWorkOrderForm },
  mounted() {
    this.loadRuleDetails()
    this.loadModuleMeta()
  },
  data() {
    return {
      showHistoryDialog: false,
      rule: null,
      thresholddialog: false,
      ruleOverviewLoading: false,
      isEditLoading: false,
      isLoading: false,
      currentModuleName: 'newreadingrules',
      primaryFields: [
        'name',
        'description',
        'category',
        'type',
        'siteId',
        'space',
        'qrVal',
      ],
      showForm: false,
      module: null,
      isNew: false,
      moduleWo: 'newreadingalarm',
      rulewoDetails: null,
    }
  },
  computed: {
    ruleId() {
      return parseInt(this.$route.params.id)
    },
    moduleName() {
      return 'newreadingrules'
    },
    viewname() {
      return this.$route.params.viewname
    },
    currentAction() {
      return 'WorkFlow'
    },
    currentRuleType() {
      let { currentAction } = this
      if (!isEmpty(currentAction)) {
        return currentAction === 'WorkFlow' ? 37 : 38
      }
      return null
    },
    workFlowActionHash() {
      return this.$constants.WorkFlowAction.module['newreadingalarm']
    },
    activityTypes() {
      let { workFlowActionHash } = this
      let activity = [
        'On Date',
        'Field Change',
        'On each Alarm Occurrence',
        'Clear',
      ]
      let activityTypes = []
      if (workFlowActionHash?.activityTypes) {
        workFlowActionHash.activityTypes.forEach(types => {
          if (activity.includes(types.label)) {
            activityTypes.push(types)
          }
        })
      }
      activityTypes = sortBy(activityTypes, ['label'])
      return activityTypes || []
    },
    currentActionTab() {
      let { workFlowActionHash, currentAction } = this
      if (!isEmpty(workFlowActionHash)) {
        let { tabs } = workFlowActionHash || {}
        return tabs.find(tab => tab.name === currentAction)
      }
      return {}
    },
    isEditRuleToWo() {
      let { rulewoDetails } = this || {}
      if (rulewoDetails && !isEmpty(rulewoDetails)) {
        return true
      }
      return false
    },
    isFaultToWo() {
      let { rulewoDetails } = this || {}
      let faultToWo = false
      if (rulewoDetails && !isEmpty(rulewoDetails)) {
        let { closeWo, woCreation } = rulewoDetails || {}
        let { status: closeStatus } = closeWo || {}
        let { status: createStatus } = woCreation || {}
        if (!isEmpty(createStatus) && !isEmpty(closeStatus)) {
          faultToWo = createStatus && closeStatus
        }
      }
      return faultToWo
    },
  },
  watch: {
    ruleId: function() {
      this.loadRuleDetails()
    },
  },
  methods: {
    loadReadingRules() {},
    goBack() {
      window.history.go(-1)
    },
    editThresholdRule(id) {
      let { viewname } = this
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        name && this.$router.push({ name, params: { viewname, id } })
      } else {
        this.$router.push({
          name: 'new-rule-edit',
          params: { viewname, id },
          query: this.$route.query,
        })
      }
    },
    async loadRuleDetails() {
      let self = this
      this.ruleOverviewLoading = true
      let params = { id: this.ruleId }
      try {
        let { newreadingrules } = await API.fetchRecord(this.moduleName, params)
        self.rule = newreadingrules
        self.ruleOverviewLoading = false
      } catch (errorMsg) {
        this.$message.error(errorMsg.message)
      }
      await this.loadRuleWoDetails()
    },
    async loadRuleWoDetails() {
      let { ruleId } = this
      let params = { ruleId }
      let { error, data } = await API.get(
        'v3/readingrule/actions/fetchRuleWo',
        params
      )
      if (error) {
        this.$message.error('Error Occured')
      } else if (data) {
        let { faultToWorkorder } = data || {}
        delete faultToWorkorder.ruleId
        this.rulewoDetails = faultToWorkorder
      }
    },
    async updateStatus(record) {
      this.isLoading = true
      this.rule.status = !this.rule.status
      let { id } = record || {}
      let url = 'v3/modules/data/patch'
      let params = { data: record, moduleName: this.moduleName, id: id }
      let { error, data } = await API.post(url, params)
      if (error) {
        this.$message.error('Error Occurred')
      } else {
        if (!isEmpty(data)) {
          let { newreadingrules } = data || {}
          let { status } = newreadingrules || {}
          this.isLoading = false
          this.$message.success(
            (status
              ? this.$t('common._common.activate')
              : this.$t('common._common.deactivate')) +
              this.$t('common._common.rule_successfully')
          )
        }
      }
    },
    enableWO() {
      this.isLoading = false
      this.isNew = true
      this.showForm = true
      this.loadModuleMeta()
    },
    loadModuleMeta() {
      this.$util
        .loadModuleMeta(this.moduleWo)
        .then(response => (this.module = response.module))
    },
    async disableFaultWO() {
      let { ruleId } = this
      let params = { ruleId }
      let { error, data } = await API.get(
        'v3/readingrule/actions/updatewfStatus',
        params
      )
      if (error) {
        this.$message.error('Error Occured')
      } else {
        await this.loadRuleWoDetails()
        let { isFaultToWo } = this
        if (isFaultToWo) {
          this.$message.success('Fault To Workorder configuration is enabled')
        } else {
          this.$message.success('Fault To Workorder configuration is disabled')
        }
      }
    },
  },
}
</script>
<style scoped>
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
  font-size: 22px;
  color: #324056;
}
.asset-details .asset-space {
  font-size: 13px;
  color: #8ca1ad;
}
</style>
