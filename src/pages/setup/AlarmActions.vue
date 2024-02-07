<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Alarm Action</div>
        <div class="heading-description">List of all Alarm Actions</div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="
            showNewCustomRule = true
            isNew = true
          "
          >Add Alarm Action</el-button
        >
        <new-custom-rule
          :module="alarmModuleName"
          v-if="showNewCustomRule"
          :rule="selectedRule"
          :isNew="isNew"
          :visibilityshow.sync="showNewCustomRule"
          @actionSaved="actionOnSave"
          :activityTypes="activityTypes"
        ></new-custom-rule>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">RULE NAME</th>
                <th class="setting-table-th setting-th-text">EXECUTE ON</th>
                <th class="setting-table-th setting-th-text">ACTIONS</th>
                <th class="setting-table-th setting-th-text">STATUS</th>
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
                  No rules created yet.
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
                      <el-tag v-if="actionText === 'SMS'">{{
                        actionText
                      }}</el-tag>
                      <el-tag v-if="actionText === 'Email'" type="success">{{
                        actionText
                      }}</el-tag>
                      <el-tag v-if="actionText === 'Mobile'" type="info">{{
                        actionText
                      }}</el-tag>
                      <el-tag
                        v-if="actionText === 'Create WorkOrder'"
                        type="warning"
                        >{{ actionText }}</el-tag
                      >
                      <el-tag
                        v-else-if="actionText === 'Close WorkOrder'"
                        type="warning"
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
                    v-if="rule.ruleType === 8"
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
                      @click="
                        selectedRule = rule
                        showDeleteDialog = true
                      "
                    ></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <el-dialog
      title="Confirm Delete"
      :visible.sync="showDeleteDialog"
      width="40%"
    >
      <div>Are you sure you want to delete this?</div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showDeleteDialog = false">CANCEL</el-button>
        <el-button
          type="primary"
          @click="
            showDeleteDialog = false
            deleteRule()
          "
          >CONFIRM</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
import NewCustomRule from 'pages/setup/new/NewCustomRule'
export default {
  components: {
    NewCustomRule,
  },
  title() {
    return 'Alarm Actions'
  },
  data() {
    return {
      showNewCustomRule: false,
      workFlowRules: null,
      isNew: true,
      alarmModuleName: this.$helpers.isLicenseEnabled('NEW_ALARMS')
        ? 'newreadingalarm'
        : 'alarm',
      showDeleteDialog: false,
      selectedRule: null,
      loading: true,
      activityTypes: [
        { label: 'Create', value: 1 },
        { label: 'Severity Change', value: 1024 },
        { label: 'On date ', value: 524288 },
        { label: 'Field Changes', value: 1048576 },
        { label: 'Create and Severity Change', value: 1025 },
        { label: 'Clear', value: 2048 },
        { label: 'Delete', value: 4 },
      ],
    }
  },
  mounted() {
    this.loadActions()
  },
  methods: {
    changeRuleStatus(rule) {
      let url
      if (rule.status) {
        url = 'turnonrule'
      } else {
        url = 'turnoffrule'
      }
      this.$http
        .post('/setup/' + url, { workflowId: rule.id })
        .then(function(response) {})
        .catch(function(error) {
          console.log(error)
        })
    },
    setActionText(actions) {
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
          }
        })
        return typeString.sort()
      } else {
        return '---'
      }
    },
    loadActions() {
      let self = this
      self.loading = true
      self.workFlowRules = {}
      let promise2 = self.$http.get('/setup/workflowRuleType?ruleType=8')
      Promise.all([promise2]).then(function(values) {
        values[0].data = values[0].data === null ? [] : values[0].data
        self.workFlowRules = [...values[0].data]
        self.loading = false
      })
    },
    editAlarmAction(rule) {
      this.selectedRule = this.$helpers.cloneObject(rule)
      this.isNew = false
      this.showNewCustomRule = true
    },
    checkActivityType(value) {
      let label = this.activityTypes.find(type => type.value === value)
      return label ? label.label : null
    },
    actionOnSave(res) {
      this.loadActions()
    },
    deleteRule() {
      this.$http
        .post('/setup/deleteRule', { id: [this.selectedRule.id] })
        .then(response => {
          if (response.status === 200 && typeof response.data === 'object') {
            this.$message.success('Rule deleted successfully')
            let idx = this.workFlowRules.findIndex(
              rule => rule.id === this.selectedRule.id
            )
            this.workFlowRules.splice(idx, 1)
          } else {
            this.$message.error('Rule deletion failed')
          }
        })
    },
  },
}
</script>
