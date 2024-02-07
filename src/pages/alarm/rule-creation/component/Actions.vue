<template>
  <div>
    <div id="actions-header" class="section-header">
      {{ $t('alarm.rules.actions') }}
    </div>
    <div class="p50 pT10 pB30 pR75">
      <div class="flex flex-col">
        <el-checkbox
          v-model="alertDownTime"
          @change="emitActions({ alertDownTime })"
          class="mB20"
          >{{ $t('rule.create.report_for_downtime') }}</el-checkbox
        >
        <el-checkbox v-model="createWo" @change="emitActions({ createWo })">{{
          $t('rule.create.create_wo_alarm_creation')
        }}</el-checkbox>
        <el-checkbox
          class="mT20 mL20"
          v-if="createWo"
          v-model="autoCloseWo"
          @change="emitActions({ autoCloseWo })"
          >{{ $t('rule.create.auto_close_wo') }}</el-checkbox
        >
      </div>
      <div class="mT30">
        <div class="header">{{ $t('alarm.rules.severity_change') }}</div>
        <div class="desc">{{ $t('alarm.rules.severity_change_desc') }}</div>
        <ActionsTable
          v-if="!$validation.isEmpty(severityChangeList)"
          :actionData="severityChangeList"
          :deleteAction="deleteAction"
        />
        <div
          class="flex flex-direction-row pointer items-center mT20"
          @click="openActionForm('severityAction', 'Severity Change')"
        >
          <div class="pT10 circle-font">
            <div class="expression-circle ruleAdd">{{ `+` }}</div>
          </div>
          <div class="fc-id bold pointer f14 pL5 pT10">
            {{ $t('alarm.rules.add_severity') }}
          </div>
        </div>
      </div>
      <div class="mT30">
        <div class="header">{{ $t('alarm.rules.notifications') }}</div>
        <div class="desc">{{ $t('alarm.rules.notifications_desc') }}</div>
        <ActionsTable
          v-if="!$validation.isEmpty(notificationsList)"
          :actionData="notificationsList"
          :deleteAction="deleteAction"
        />
        <div
          class="flex flex-direction-row pointer items-center mT20"
          @click="openActionForm('notification', 'Notification')"
        >
          <div class="pT10 circle-font">
            <div class="expression-circle ruleAdd">{{ `+` }}</div>
          </div>
          <div class="fc-id bold pointer f14 pL5 pT10">
            {{ $t('alarm.rules.add_notifications') }}
          </div>
        </div>
      </div>
    </div>
    <ActionFormDialog
      isNew="true"
      :title="dialogTitle"
      :module="'newreadingalarm'"
      :actionType="actionType"
      v-if="showActionForm"
      :visibilityshow.sync="showActionForm"
      @actionSaved="actionOnSave"
    />
  </div>
</template>

<script>
import ActionFormDialog from 'pages/alarm/rule/component/ActionFormDialog'
import ActionsTable from './ActionsTable'
import isEqual from 'lodash/isEqual'

export default {
  components: { ActionFormDialog, ActionsTable },
  data() {
    return {
      autoCloseWo: false,
      createWo: false,
      alertDownTime: false,
      showActionForm: false,
      dialogTitle: '',
      actionType: null,
      actions: [],
    }
  },
  computed: {
    severityChangeList() {
      let { actions = [] } = this
      return actions.filter(action => action.type === 'severityAction')
    },
    notificationsList() {
      let { actions = [] } = this
      return actions.filter(action => action.type === 'notification')
    },
  },
  methods: {
    openActionForm(type, title) {
      this.showActionForm = true
      this.dialogTitle = title
      this.actionType = type
    },
    actionOnSave(action) {
      this.actions.push(action)
      this.$emit('onActionsChange', { actions: this.actions })
    },
    deleteAction(action) {
      let { actions } = this
      let currIndex = actions.findIndex(currAction =>
        isEqual(currAction, action)
      )
      this.actions.splice(currIndex, 1)
    },
    emitActions(data) {
      this.$emit('onActionsChange', data)
    },
  },
}
</script>

<style scoped>
.header {
  font-size: 12px;
  font-weight: 500;
  line-height: normal;
  letter-spacing: 1px;
  color: #ee508f;
  text-transform: uppercase;
}
.desc {
  margin: 10px 0px 20px;
  font-size: 14px;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #6b7e91;
}
.configured-green {
  color: #5bc293;
}
</style>
