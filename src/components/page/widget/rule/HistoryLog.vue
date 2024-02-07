<template>
  <div class="history-log-page">
    <div>
      <portal :to="tab.name + '-title-section'">
        <div
          class="widget-header d-flex flex-direction-row justify-content-space mB15 mT5"
        >
          <div class="flex-middle justify-content-space width100">
            <div class="widget-header-name">
              {{ $t('rule.create.activity_log') }}
            </div>
            <div class="flex-middle justify-end width90">
              <div
                class="fc-dark-blue4 f13 pointer"
                @click="showHistoricalDialog = true"
              >
                {{ $t('kpi.kpi.historical_header') }}
              </div>
              <div @click="refresh" class="pL10">
                <i
                  class="el-icon-refresh"
                  style="font-weight: bold;margin-right: 8px;font-size: 14px;color: #324056;cursor: pointer;"
                ></i>
              </div>
            </div>
          </div>
        </div>
      </portal>
      <div v-if="parentLogLoading" class="flex-middle fc-empty-white">
        <spinner :show="parentLogLoading" size="80"></spinner>
      </div>
      <div
        v-else-if="!parentLog || parentLog.length === 0"
        class="mT40 history-empty-state"
      >
        <InlineSvg
          src="svgs/emptystate/history"
          iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
        ></InlineSvg>
        <div class="fc-black-dark f18 bold">
          {{ $t('common._common.no_log_available') }}
        </div>
      </div>
      <div v-else class="mB10 position-relative pB100 overflow-y-scroll">
        <el-row class="history-log-header">
          <el-col :span="5">
            <div class="history-log-header-txt">
              {{ $t('common.header.assets') }}
            </div>
          </el-col>
          <el-col :span="4">
            <div class="history-log-header-txt">
              {{ $t('rule.create.start_end_date') }}
            </div>
          </el-col>
          <el-col :span="5">
            <div class="history-log-header-txt">
              {{ $t('setup.setupLabel.executed_by') }}
            </div>
          </el-col>
          <el-col :span="3">
            <div class="history-log-header-txt">
              {{ $t('setup.setupLabel.executed_time') }}
            </div>
          </el-col>
          <el-col :span="3">
            <div class="history-log-header-txt">
              {{ $t('alarm.sensor_alarm.alarms') }}
            </div>
          </el-col>
          <el-col :span="4">
            <div class="history-log-header-txt">
              {{ $t('maintenance._workorder.status') }}
            </div>
          </el-col>
        </el-row>
        <!-- collapse Parent Log  -->
        <el-collapse
          v-model="activeNames"
          :accordion="true"
          @change="handleChange"
          class="history-collapse"
        >
          <el-collapse-item
            v-for="(parent, index) in parentLog"
            :key="parent.id"
            :name="index"
            :data="parent"
          >
            <template slot="title">
              <el-row class="history-collapse-header">
                <el-col :span="5">
                  <div class="flex-middle pT10">
                    <i class="el-icon-caret-right"></i>
                    <i class="el-icon-caret-bottom"></i>
                    <div class="fc-black-com f13 line-height20">
                      {{ parent.noOfResources }}
                      {{ $t('common.header.assets') }}
                    </div>
                  </div>
                </el-col>
                <el-col :span="4" class="pB15">
                  <div class="flex-middle">
                    <div class="fc-grey2 f13 line-height20 fw4">
                      {{ $t('rule.create.start') }}
                    </div>
                    <div class="fc-black-com f13 pL5 line-height20 fw4">
                      {{ $helpers.formatDateFull(parent.startTime) }}
                    </div>
                  </div>
                  <div class="flex-middle">
                    <div class="fc-grey2 f13 line-height20 fw4">
                      {{ $t('rule.create.end') }}
                    </div>
                    <div class="fc-black-com f13 pL10 line-height20 fw4">
                      {{ $helpers.formatDateFull(parent.endTime) }}
                    </div>
                  </div>
                </el-col>
                <el-col :span="5">
                  <div class="fc-black-com f13 line-height20 fw4">
                    <span>
                      <avatar
                        size="md"
                        :user="{
                          name: parent.createdBy
                            ? $store.getters.getUser(parent.createdBy).name
                            : 'Unknown',
                        }"
                      ></avatar>
                      {{
                        parent.createdBy
                          ? $store.getters.getUser(parent.createdBy).name
                          : 'Unknown'
                      }}
                    </span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="fc-black-com f13 fw4 line-height20">
                    {{ $helpers.formatDateFull(parent.createdTime) }}
                  </div>
                  <div class="fc-black-com f13 fw4 line-height20">
                    {{ $helpers.formatTimeFull(parent.createdTime) }}
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="fc-black-com f13 line-height20 fw4 pT10">
                    {{
                      parent.totalAlarmCount > 0
                        ? parent.totalAlarmCount + ' Occurred'
                        : 'No occurrence'
                    }}
                  </div>
                </el-col>
                <el-col :span="4">
                  <div
                    class="fc-black-com f13 bold line-height20 flex-middle fw4 pT10"
                  >
                    <div>
                      {{ parent.resolvedResourcesCount }} /
                      {{ parent.noOfResources }}
                    </div>
                    <div
                      v-bind:style="{
                        color: $constants.RULE_JOB_STATUS[parent.status].color,
                      }"
                      :class="'f14 line-height20 fw4'"
                      class="pL10"
                    >
                      {{ $constants.RULE_JOB_STATUS[parent.status].label }}
                    </div>
                  </div>
                </el-col>
              </el-row>
            </template>
            <div>
              <el-row v-if="parent.loading" class="history-collapse-child">
                <spinner :show="parent.loading" size="80"></spinner>
              </el-row>
              <!--First Level log resource List -->
              <el-collapse
                v-else
                v-model="resourceName"
                :accordion="true"
                @change="handleResourceChange"
                class="history-resource-collapse sub-history-resource-collapse"
              >
                <el-collapse-item
                  v-for="(child, childIndex) in childrenMap[parent.id]"
                  :key="child.id + child.resourceId"
                  :name="childIndex"
                  :data="child"
                >
                  <template slot="title">
                    <el-row class="history-collapse-child">
                      <el-col :span="9" class>
                        <div class="flex-middle pT10 pL10">
                          <i class="el-icon-caret-right"></i>
                          <i class="el-icon-caret-bottom"></i>
                          <div class="label-txt-black">
                            {{ child.resourceContext.name }}
                          </div>
                        </div>
                      </el-col>
                      <el-col :span="5">
                        <div class="hide-v">
                          {{ $t('rule.create.dont_remove') }}
                        </div>
                      </el-col>
                      <el-col :span="3">
                        <div class="hide-v">
                          {{ $t('rule.create.dont_remove') }}
                        </div>
                      </el-col>
                      <el-col :span="3" class="text-left">
                        <div class="fc-black-com f13 line-height20 fw4">
                          {{
                            child.alarmCount > 0
                              ? child.alarmCount + ' Occurred'
                              : 'No occurrence'
                          }}
                        </div>
                      </el-col>
                      <el-col :span="4" class="text-left">
                        <div
                          v-bind:style="{
                            color:
                              $constants.CHILD_JOB_STATUS[child.status].color,
                          }"
                          :class="'f14'"
                        >
                          {{ $constants.CHILD_JOB_STATUS[child.status].label }}
                        </div>
                      </el-col>
                    </el-row>
                  </template>
                  <!-- List of date split job-->
                  <div>
                    <el-row v-if="child.loading" class="history-collapse-child">
                      <!-- {{child}} -->
                      <spinner :show="child.loading" size="80"></spinner>
                    </el-row>
                    <el-row
                      v-else
                      class="sub-history-collapse-child"
                      v-for="dateLog in dateLogMap[child.id]"
                      :key="dateLog.id"
                    >
                      <el-col :span="1">
                        <div class="hide-v">
                          {{ $t('rule.create.dont_remove') }}
                        </div>
                      </el-col>
                      <el-col :span="5" class="pB15">
                        <div class="flex-middle">
                          <div class="fc-black-com f13 pL5 line-height20 fw4">
                            {{
                              $helpers.formatDateFull(dateLog.splitStartTime) +
                                ' ' +
                                $helpers.formatTimeFull(dateLog.splitStartTime)
                            }}
                          </div>
                          -
                          <div
                            class="fc-black-com f13 pL10 line-height20 fw4 row"
                          >
                            {{
                              $helpers.formatDateFull(dateLog.splitEndTime) +
                                ' ' +
                                $helpers.formatTimeFull(dateLog.splitEndTime)
                            }}
                          </div>
                        </div>
                      </el-col>
                      <el-col :span="3">
                        <div class="hide-v">
                          {{ $t('rule.create.dont_remove') }}
                        </div>
                      </el-col>
                      <el-col v-if="!dateLog.errorMessage" :span="7">
                        <div class="hide-v">
                          {{ $t('rule.create.dont_remove') }}
                        </div>
                      </el-col>
                      <el-col v-else :span="7" class="text-left pR">
                        <div class="fc-black-com f11 line-height20 fw4">
                          {{ dateLog.errorMessage }}
                        </div>
                      </el-col>
                      <el-col :span="3" class="text-right"> </el-col>
                      <el-col :span="4" class="text-left pL30">
                        <div
                          v-bind:style="{
                            color: $constants.JOB_STATUS[dateLog.status].color,
                          }"
                          :class="'f14'"
                        >
                          {{ $constants.JOB_STATUS[dateLog.status].label }}
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </el-collapse-item>
              </el-collapse>
            </div>
            <div></div>
          </el-collapse-item>
        </el-collapse>
      </div>
      <historical-run-dialog
        v-if="showHistoricalDialog && !isNewRule"
        :visibility.sync="showHistoricalDialog"
        :details="details"
      />
      <HistoricalRun
        v-if="showHistoricalDialog && isNewRule"
        @close="showHistoricalDialog = false"
      ></HistoricalRun>

      <!-- alarm graph container new  -->
    </div>
  </div>
</template>

<script>
import Avatar from '@/Avatar'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import HistoricalRun from 'src/components/page/widget/rule/CreateRuleHistoricalForm'
import HistoricalRunDialog from '@/page/widget/rule/HistoricalRunDialog'

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
    Avatar,
    HistoricalRun,
    HistoricalRunDialog,
  },
  data() {
    return {
      childrenMap: {},
      dateLogMap: {},
      showHistoricalDialog: false,
      parentLog: null,
      parentLogLoading: false,
      activeRule: null,
      activeNames: [],
      focusParentId: null,
      resourceName: [],
      orderType: 'desc',
    }
  },
  mounted() {
    this.loadParentLogger()
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
    isNewRule() {
      let { details } = this
      let { moduleName } = details || {}
      return moduleName === 'newreadingrules'
    },
  },
  methods: {
    refresh() {
      this.loadParentLogger()
    },
    handleChange(data) {
      if (!isEmpty(data)) {
        this.focusParentId = this.parentLog[data].id
        if (!this.childrenMap[this.parentLog[data].id]) {
          this.handleNodeClick(this.parentLog[data])
        }
      }
    },
    handleResourceChange(data) {
      if (!isEmpty(data)) {
        if (!this.dateLogMap[this.childrenMap[this.focusParentId][data].id]) {
          this.handleResourceApiCall(this.childrenMap[this.focusParentId][data])
        }
      }
    },
    async loadParentLogger() {
      this.parentLogLoading = true
      let url =
        'v2/historicalLogger/getWorkflowRuleLoggers?ruleId=' + this.ruleId
      let { error, data } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(message || 'Error Occured')
      } else {
        let { workflowRuleLoggers } = data
        this.parentLog = workflowRuleLoggers
        this.parentLogLoading = false
        this.parentLog.sort((a, b) => {
          if (a.createdTime > b.createdTime) {
            return -1
          }
          if (a.createdTime < b.createdTime) {
            return 1
          }
          return 0
        })
      }
    },
    async handleResourceApiCall(resourceData) {
      let url =
        'v2/historicalLogger/getWorkflowRuleResourceHistoricalLogs?parentRuleResourceId=' +
        resourceData.id
      this.$set(this, 'loading', true)
      let { error, data } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(message || 'Error Occured')
      } else {
        let { workflowRuleHistoricalLogs } = data
        this.$set(resourceData, 'loading', false)
        this.dateLogMap[resourceData.id] = Object.assign(
          workflowRuleHistoricalLogs
        )
        resourceData.loading = false
        this.$forceUpdate()
      }
    },
    async handleNodeClick(nodeData) {
      let url =
        'v2/historicalLogger/getWorkflowRuleResourceLoggers?parentRuleLoggerId=' +
        nodeData.id
      this.$set(nodeData, 'loading', true)
      let { error, data } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(message || 'Error Occured')
      } else {
        let { workflowRuleResourceLoggers } = data
        this.$set(nodeData, 'loading', false)
        this.childrenMap[nodeData.id] = Object.assign(
          workflowRuleResourceLoggers
        )
        nodeData.loading = false
      }
    },
  },
}
</script>
