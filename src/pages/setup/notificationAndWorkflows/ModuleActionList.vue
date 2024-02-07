<template>
  <div class="height100 overflow-hidden">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title capitalize">
          {{ title }} {{ currentAction }}
        </div>
        <div class="heading-description">
          {{ $t('setup.list.list_of_all') }} {{ title }} {{ currentAction }}
        </div>
      </div>
      <div
        v-if="!$validation.isEmpty(modules)"
        class="action-btn setting-page-btn"
      >
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="
            showNewCustomRule = true
            isNew = true
          "
          >{{ $t('setup.add.add') }}{{ currentActionTab.name }}</el-button
        >
        <new-custom-type-rule
          v-if="showNewCustomRule"
          :module="modules.name"
          :moduleDisplayName="modules.displayName"
          :isNew="isNew"
          :selectedRuleId="selectedRuleId"
          :visibilityshow.sync="showNewCustomRule"
          @actionSaved="actionOnSave"
          :ruleType="currentRuleType"
          :activityTypes="activityTypes"
          :ruleActions="currentActionTab.actions"
          :currentAction="currentAction"
        ></new-custom-type-rule>
      </div>
    </div>
    <div
      class="flex-middle setting-Rlayout"
      style="justify-content:space-between"
    >
      <portal-target name="automation-modules"></portal-target>
      <pagination
        :currentPage.sync="page"
        :total="totalCount"
        :perPage="10"
        class="nowrap"
      >
      </pagination>
    </div>
    <div class="container-scroll mT30">
      <div class="row setting-Rlayout">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">
                  {{ $t('setup.setupLabel.rule_name') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('setup.approvalprocess.executeon') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('rule.create.actions') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('maintenance._workorder.status') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="!workFlowRules.length">
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('setup.empty.no') }}
                  {{
                    $validation.isEmpty(currentAction) ? 'Rules' : currentAction
                  }}
                  {{ $t('setup.empty.created_yet') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(rule, index) in workFlowRules"
                :key="index"
              >
                <td>{{ rule.name }}</td>
                <td>{{ checkActivityType(rule.event.activityType) }}</td>
                <td>
                  <div
                    v-if="rule.actions"
                    style="display: flex;position: relative;"
                  >
                    <div
                      v-for="(actionText, idx) in setActionText(rule.actions)"
                      :key="idx"
                      style="margin-right: 10px"
                    >
                      <el-tag size="small" v-if="actionText === 'SMS'">{{
                        actionText
                      }}</el-tag>
                      <el-tag
                        size="small"
                        v-if="actionText === 'Email'"
                        type="danger"
                        >{{ actionText }}</el-tag
                      >
                      <el-tag
                        size="small"
                        v-if="actionText === 'Mobile'"
                        type="info"
                        >{{ $t('setup.customButton.app') }}</el-tag
                      >
                      <el-tag
                        size="small"
                        v-if="actionText === 'Whatsapp'"
                        type="success"
                        >{{ actionText }}</el-tag
                      >
                      <el-tag
                        size="small"
                        v-if="actionText === 'Call'"
                        type="warning"
                        >{{ actionText }}</el-tag
                      >
                      <el-tag
                        size="small"
                        v-if="actionText === 'Create WorkOrder'"
                        type="warning"
                        >{{ actionText }}</el-tag
                      >
                      <el-tag
                        size="small"
                        v-if="actionText === 'Close WorkOrder'"
                        type="warning"
                        >{{ actionText }}</el-tag
                      >
                      <el-tag
                        size="small"
                        v-if="actionText === 'Field Change'"
                        :color="'#bbbbc'"
                        >{{ actionText }}</el-tag
                      >
                      <el-tag
                        size="small"
                        v-if="actionText === 'Control'"
                        type="warning"
                        >{{ actionText }}</el-tag
                      >
                      <el-tag
                        size="small"
                        v-if="actionText === 'Script'"
                        type="warning"
                        >{{ actionText }}</el-tag
                      >
                      <el-tag
                        size="small"
                        v-if="actionText === 'Change Status'"
                        :color="'#bbbbc'"
                        >{{ actionText }}</el-tag
                      >
                    </div>
                  </div>
                  <div v-else>---</div>
                </td>
                <td>
                  <el-switch
                    v-model="rule.status"
                    @change="changeRuleStatus(rule)"
                    class="Notification-toggle"
                    active-color="rgba(57, 178, 194, 0.8)"
                    inactive-color="#e5e5e5"
                  ></el-switch>
                </td>
                <td style="width: 15%;">
                  <div
                    v-if="!rule.locked"
                    class="text-left actions"
                    style="margin-top:0px;margin-right: 15px;text-align:center;"
                  >
                    <i
                      class="el-icon-edit pointer"
                      @click="editAlarmAction(rule)"
                    ></i>
                    &nbsp;&nbsp;
                    <i
                      class="el-icon-delete pointer"
                      @click="deleteRule(rule)"
                    ></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import NewCustomTypeRule from './NewCustomTypeRule'
import { isEmpty } from '@facilio/utils/validation'
import isEqual from 'lodash/isEqual'
import Pagination from 'src/components/list/FPagination'
import { API } from '@facilio/api'
export default {
  components: {
    NewCustomTypeRule,
    Pagination,
  },
  props: ['modules', 'isCustomModule'],
  computed: {
    currentAction() {
      let { $route } = this
      let {
        meta: { currentAction },
      } = $route
      return currentAction
    },
    currentRuleType() {
      let { currentAction } = this
      if (!isEmpty(currentAction)) {
        return currentAction === 'WorkFlow' ? 37 : 38
      }
      return null
    },
    workFlowActionHash() {
      let { isCustomModule, modules } = this
      let moduleName = isCustomModule ? 'customModules' : modules.name

      return (
        this.$constants.WorkFlowAction.module[moduleName] ||
        this.$constants.WorkFlowAction.module.customModules
      )
    },
    currentActionTab() {
      let { workFlowActionHash, currentAction } = this
      if (!isEmpty(workFlowActionHash)) {
        return workFlowActionHash.tabs.find(tab => tab.name === currentAction)
      }
      return {}
    },
    activityTypes() {
      let { workFlowActionHash } = this
      return workFlowActionHash.activityTypes
    },
    title() {
      let { modules } = this
      if (!isEmpty(modules)) {
        let { displayName } = modules
        return `${displayName}`
      }
      return ''
    },
    page() {
      return this.$route.query.page || 1
    },
    isWorkPermitLicenseEnabled() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers || {}

      return isLicenseEnabled('WORK_PERMIT')
    },
  },
  watch: {
    modules: {
      handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          if (!isEmpty(newVal)) {
            this.loadActions()
          }
        }
      },
    },
    page: function(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.loadActions()
        this.loadCount()
      }
    },
  },
  data() {
    return {
      showNewCustomRule: false,
      workFlowRules: [],
      isNew: true,
      showDeleteDialog: false,
      selectedRule: null,
      loading: true,
      totalCount: null,
      selectedRuleId: null,
    }
  },
  created() {
    let { modules, page } = this
    if (!isEmpty(modules) && page === 1) {
      this.loadActions()
      this.loadCount()
    }
  },
  methods: {
    async changeRuleStatus(rule) {
      let url
      if (rule.status) {
        url = 'turnonrule'
      } else {
        url = 'turnoffrule'
      }
      let params = { workflowId: rule.id }
      let { error } = await API.post('/setup/' + url, params)
      if (error) {
        this.$message.error(
          error.message || this.$t('setup.failed.rule_status_change_failed')
        )
      }
    },
    setActionText(actions) {
      let { isWorkPermitLicenseEnabled } = this
      if (actions) {
        let typeString = []
        actions.forEach(d => {
          if (d.actionType === 3) {
            if (typeString.indexOf('Email') === -1) {
              typeString.push('Email')
            }
          } else if (d.actionType === 4) {
            if (typeString.indexOf('SMS') === -1) {
              typeString.push('SMS')
            }
          } else if (d.actionType === 7) {
            if (typeString.indexOf('Mobile') === -1) {
              typeString.push('Mobile')
            }
          } else if (d.actionType === 11) {
            if (typeString.indexOf('Create WorkOrder') === -1) {
              typeString.push('Create WorkOrder')
            }
          } else if (d.actionType === 12) {
            if (typeString.indexOf('Close WorkOrder') === -1) {
              typeString.push('Close WorkOrder')
            }
          } else if (d.actionType === 13) {
            if (typeString.indexOf('Field Change') === -1) {
              typeString.push('Field Change')
            }
          } else if (d.actionType === 19) {
            if (typeString.indexOf('Change Status') === -1) {
              typeString.push('Change Status')
            }
          } else if (d.actionType === 18) {
            if (typeString.indexOf('Control') === -1) {
              typeString.push('Control')
            }
          } else if (d.actionType === 21) {
            if (typeString.indexOf('Script') === -1) {
              typeString.push('Script')
            }
          } else if (d.actionType === 26) {
            if (
              typeString.indexOf('Whatsapp') === -1 &&
              isWorkPermitLicenseEnabled
            ) {
              typeString.push('Whatsapp')
            }
          } else if (d.actionType === 27) {
            if (
              typeString.indexOf('Call') === -1 &&
              isWorkPermitLicenseEnabled
            ) {
              typeString.push('Call')
            }
          }
        })
        return typeString.sort()
      } else {
        return '---'
      }
    },
    async loadActions() {
      let { modules, currentRuleType } = this
      let { name } = modules
      this.loading = true
      let params = {
        ruleType: currentRuleType,
        moduleName: name,
        page: this.page,
        perPage: 10,
      }
      let { error, data } = await API.get('/v2/modules/rules/list', params)
      if (error) {
        this.$message.error(error.message)
      } else {
        let { workflowRuleList } = data
        this.workFlowRules = workflowRuleList || []
      }

      this.loading = false
    },
    async loadCount() {
      let {
        modules: { name },
        currentRuleType,
      } = this
      let params = {
        ruleType: currentRuleType,
        moduleName: name,
      }
      let { data, error } = await API.get('/v2/modules/rules/getCount', params)
      if (error) {
        this.$message.error(error.message)
      } else {
        this.totalCount = this.$getProperty(data, 'count', null)
      }
    },
    editAlarmAction(rule) {
      this.selectedRuleId = rule.id
      this.isNew = false
      this.showNewCustomRule = true
    },
    checkActivityType(value) {
      let { activityTypes } = this
      return (activityTypes.find(type => type.value === value) || {}).label
    },
    actionOnSave() {
      this.loadActions()
      this.loadCount()
    },
    async deleteRule(selectedRule) {
      let value = await this.$dialog.confirm({
        title: this.$t('setup.delete.delete_rule'),
        message: this.$t(
          'setup.delete.are_you_sure_you_want_to_delete_this_rule'
        ),
        rbDanger: true,
        rbLabel: this.$t('setup.delete.delete'),
      })
      if (value) {
        let params = {
          ids: [selectedRule.id],
        }
        let { error } = await API.post('/v2/modules/rules/delete', params)
        if (error) {
          this.$message.error(
            error.message || this.$t('setup.delete.rule_deletion_failed')
          )
        } else {
          this.$message.success(
            this.$t('setup.delete.rule_deleted_successfully')
          )
          let idx = this.workFlowRules.findIndex(
            rule => rule.id === selectedRule.id
          )
          this.workFlowRules.splice(idx, 1)
          this.totalCount--
        }
      }
    },
  },
}
</script>
