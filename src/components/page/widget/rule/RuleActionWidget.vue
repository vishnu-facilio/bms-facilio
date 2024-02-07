<template>
  <div>
    <div>
      <portal :to="tab.name + '-title-section'">
        <div class="widget-header d-flex flex-direction-row mB15 mT5">
          <div class="flex-middle justify-end width100">
            <el-popover
              v-if="moduleName !== 'newreadingrules'"
              placement="bottom"
              width="250"
              v-model="toggle"
              popper-class="popover-height asset-popover "
              trigger="click"
              visible-arrow="true"
            >
              <ul>
                <li
                  @click="
                    opennewActionForm('severityAction', 'Severity Change')
                  "
                >
                  {{ $t('common._common.severity_change') }}
                </li>
                <li @click="opennewActionForm('notification', 'Notification')">
                  {{ $t('common.header.notification') }}
                </li>
                <li v-if="!isWoCreated" @click="enableWo(11, 'Create Wo')">
                  {{ $t('common.header.enable_create_workOrder') }}
                </li>
                <li
                  v-if="!isAutoCloseWoCreated && isWoCreated"
                  @click="enableWo(12, 'Auto Close Wo')"
                >
                  {{ $t('common.header.enable_auto_close_workorder') }}
                </li>
              </ul>
              <div slot="reference" class="label-txt-blue bold pointer">
                <i class="el-icon-plus pR5 fwBold"></i>
                {{ $t('common.header.add_action') }}
              </div>
            </el-popover>
          </div>
        </div>
      </portal>
    </div>
    <template v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </template>
    <div
      v-if="!$validation.isEmpty(actions) && moduleName !== 'newreadingrules'"
    >
      <el-table
        :data="actions"
        style="width: 100%"
        :fit="true"
        class="impact-form-table rule-actions-form-table"
      >
        <el-table-column width="20"></el-table-column>
        <el-table-column :label="$t('common._common._rule_name')">
          <template v-slot="action">
            <div class="line-height20">{{ action.row.name }}</div>
          </template>
        </el-table-column>
        <el-table-column :label="$t('common._common.execute_on')">
          <template v-slot="action">
            <div class="line-height20">
              {{ checkActivityType(action.row.event.activityType) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="$t('common.products._actions')" width="250">
          <template v-slot="action">
            <div
              v-if="action.row.actions"
              style="display: flex;position: relative;"
            >
              <div
                v-for="(actionText, idx) in $helpers.setActionText(
                  action.row.actions
                )"
                :key="idx"
                class="fc-tag mR10"
              >
                <el-tag>{{ actionText }}</el-tag>
              </div>
            </div>
            <div v-else>---</div>
          </template>
        </el-table-column>
        <el-table-column>
          <template v-slot="action">
            <div class="text-right">
              <span
                v-if="action.row.isEdit"
                @click="openActionForm(action.row)"
              >
                <inline-svg
                  src="svgs/edit"
                  class="edit-icon-color visibility-hide-actions"
                  iconClass="icon icon-sm mR5 icon-edit"
                ></inline-svg>
              </span>
              <span @click="deleteRule(action.row.id)">
                <inline-svg
                  src="svgs/delete"
                  class="pointer edit-icon-color visibility-hide-actions mL10"
                  iconClass="icon icon-sm icon-remove fill-red"
                ></inline-svg>
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="80"></el-table-column>
      </el-table>
    </div>
    <div v-else class="block">
      <div class="mT40 mB40 text-center p30imp">
        <InlineSvg
          src="svgs/emptystate/alarmEmpty"
          iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
        ></InlineSvg>
        <div class="fc-black-dark f18 bold">
          {{ $t('common.products.no_action') }}!
        </div>
      </div>
    </div>
    <action-form-dialog
      :isNew="isNew"
      :isSummary="true"
      :title="dialogTitle"
      :module="'newreadingalarm'"
      :actionType="actionType"
      :ruleActions="selectedRule"
      :parentRuleId="ruleId"
      :details="details"
      v-if="showActionForm"
      :visibilityshow.sync="showActionForm"
      @actionSaved="updateActionOnSave"
    ></action-form-dialog>
  </div>
</template>
<script>
import ActionFormDialog from 'pages/alarm/rule/component/ActionFormDialog'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'

export default {
  props: [
    'widget',
    'groupKey',
    'moduleName',
    'details',
    'fields',
    'primaryFields',
    'layoutParams',
    'resizeWidget',
    'hideTitleSection',
    'activeTab',
    'tab',
    'sectionKey',
  ],
  components: {
    ActionFormDialog,
    Spinner,
  },
  data() {
    return {
      isNew: true,
      toggle: false,
      showActionForm: false,
      isLoading: false,
      isAutoCloseWoCreated: false,
      isWoCreated: false,
      actions: [],
      selectedRule: null,
      activityTypes: this.$constants.WorkFlowAction.module['alarm']
        .activityTypes,
    }
  },
  mounted() {
    this.getActions()
  },
  computed: {
    ruleId() {
      let { details } = this
      let { moduleName, alarmRule } = details || {}
      let ruleId = null
      if (moduleName === 'newreadingrules') {
        ruleId = this.$getProperty(details, 'id', null)
      } else {
        ruleId = this.$getProperty(alarmRule, 'preRequsite.id', null)
      }
      return ruleId
    },
  },
  methods: {
    updateActionOnSave() {
      this.getActions()
    },
    async getActions(force = false) {
      this.isLoading = true
      let { ruleId, details } = this
      let { moduleName } = details || {}
      let url = ''

      if (moduleName === 'newreadingrules') {
        url = 'v3/readingrule/actions/fetch'
      } else {
        url = '/v2/alarm/rules/fetchRuleActions'
      }
      let params = { ruleId: ruleId, force }
      let { error, data } = await API.get(url, params)

      if (error) {
        this.$message.error('Error Occured')
      } else {
        let { workflowRuleList } = data || []
        this.actions = workflowRuleList
        if (this.actions) {
          this.actions.forEach(d => {
            if (d.actions) {
              d.actions.forEach(action => {
                if (action.actionType === 11) {
                  this.isWoCreated = true
                  d.isEdit = false
                } else if (action.actionType === 12) {
                  this.isAutoCloseWoCreated = true
                  d.isEdit = false
                } else {
                  d.isEdit = true
                }
              })
            }
          })
        }
      }
      this.isLoading = false
    },
    checkActivityType(value) {
      let { activityTypes } = this
      return activityTypes.find(type => type.value === value).label
    },
    opennewActionForm(type, title) {
      this.showActionForm = true
      this.dialogTitle = title
      this.isNew = true
      this.actionType = type
    },
    async deleteRule(id) {
      let { details } = this
      let { moduleName } = details || {}
      let url
      let params
      if (moduleName === 'newreadingrules') {
        url = 'v3/readingrule/actions/delete'
        params == { actionId: id }
      } else {
        url = 'v2/setup/alarm/rules/delete'
        params = { id: [id] }
      }
      let { error, data } = await API.post(url, params)
      if (error) {
        this.$message.error('Error Occured' || error.message)
      } else {
        if (!isEmpty(data)) {
          await this.getActions()
        }
      }
    },
    openActionForm(data) {
      this.showActionForm = true
      this.isNew = false
      this.selectedRule = data
      if (data.actions[0].actionType === 17) {
        this.dialogTitle = 'Severity Change'
        this.actionType = 'severityAction'
      } else if ([3, 4, 7].includes(data.actions[0].actionType)) {
        this.dialogTitle = 'Notification'
        this.actionType = 'notification'
      } else {
        this.dialogTitle = 'Action'
      }
    },
    async enableWo(tye, name) {
      let data = {
        ruleId: this.ruleId,
        ruleType: 41,
        name: name,
        event: {
          moduleName: 'newreadingalarm',
          activityType: tye === 11 ? 1 : 2048,
        },
      }
      let { details } = this
      let { moduleName } = details || {}
      if (moduleName === 'newreadingrules') {
        let workflowRule = data
        let url = 'v3/readingrule/actions/add'
        let params = { workflowRule, moduleName: 'newreadingalarm' }
        let { error, data: actionData } = await API.post(url, params)

        if (error) {
          this.$message.error('Error Occurred')
        } else {
          if (!isEmpty(actionData)) {
            this.saving = false
            this.getActions()
          }
        }
      } else {
        await this.$util
          .addOrUpdateRuleAction(
            'newreadingalarm',
            {
              rule: data,
              actions: [
                {
                  actionType: tye,
                  templateJson: {},
                },
              ],
            },
            false,
            'addcustom'
          )
          .then(() => {
            this.saving = false
            this.getActions(true)
          })
          .catch(() => {
            this.saving = false
          })
      }
    },
  },
}
</script>
