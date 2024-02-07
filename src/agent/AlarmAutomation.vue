<template>
  <div class="alarm-automation-page">
    <div class="height100 overflow-hidden">
      <div class="setting-header2 setting-header-color">
        <div class="setting-title-block">
          <div class="setting-page-title " v-if="currentAction != 'WorkFlow'">
            {{ $t('agent.agent.agent') }}{{ ' ' }}{{ currentAction }}
          </div>
          <div class="setting-page-title " v-else>
            {{ $t('agent.agent.agent') }}{{ ' Workflow' }}
          </div>
        </div>

        <div
          v-if="!$validation.isEmpty(modules)"
          class="flex-middle justify-content-space"
        >
          <pagination
            :currentPage.sync="page"
            :total="totalCount"
            :perPage="10"
          >
          </pagination>
          <el-button
            class="fc-agent-add-btn"
            @click="
              showNewCustomRule = true
              isNew = true
            "
            ><i class="el-icon-plus pR5 fwBold"></i> {{ $t('setup.add.add')
            }}{{ ' ' }}{{ currentActionTab.name }}</el-button
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

      <portal-target name="automation-actions"></portal-target>
      <div class="container-scroll">
        <div class="row setting-Rlayout ">
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
                      $validation.isEmpty(currentAction)
                        ? 'Rules'
                        : currentAction
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
                    <div v-if="rule.actions" class="flex position-relative">
                      <div
                        v-for="(actionText, idx) in setActionText(rule.actions)"
                        :key="idx"
                        class="mR10"
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
                          v-if="actionText === 'Script'"
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
                    <div class="text-left actions mT10 mR15 text-center">
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
  </div>
</template>

<script>
import moduleActions from 'pages/setup/notificationAndWorkflows/ModuleActionList'
export default {
  extends: moduleActions,

  computed: {
    workFlowActionHash() {
      let { modules } = this
      return this.$constants.WorkFlowAction.module[modules.name]
    },
  },
}
</script>
<style lang="scss">
.alarm-automation-page {
  .setting-header-color {
    background: #ffffff;
    padding: 20px;
    align-items: center;
  }
  .setting-Rlayout {
    padding: 0 10px !important;
  }
  .setting-page-title {
    font-size: 26px;
    font-weight: normal;
    line-height: 1.17;
    letter-spacing: normal;
    color: #2e384d;
    text-align: left;
    padding-bottom: 5px;
  }
}
</style>
