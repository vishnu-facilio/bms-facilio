<template>
  <div class="history-log-page kpi-logs">
    <portal :to="tab.name + '-title-section'">
      <div
        class="widget-header d-flex flex-direction-row justify-content-space mB15 mT5"
      >
        <div class="flex-middle justify-content-space width100">
          <div class="widget-header-name">{{ $t('kpi.kpi.logs') }}</div>
          <div
            class="fc-dark-blue4 marginL-auto f13 pointer mR30"
            @click="init"
          >
            {{ $t('common._common.refresh') }}
          </div>
          <div
            class="fc-dark-blue4 f13 pointer"
            @click="showHistoryDialog = true"
          >
            {{ $t('kpi.kpi.historical_dialog') }}
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
        src="svgs/emptystate/events"
        iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
      ></InlineSvg>
      <div class="pT20 fc-black-dark f18 bold">
        {{ $t('common.products.no_logs_available') }}
      </div>
      <div class="fc-grayish pT10">
        {{ $t('common.header.you_dont_have_any_logs') }}
      </div>
    </div>

    <div v-else class="position-relative overflow-y-scroll">
      <el-row class="history-log-header">
        <el-col :span="4">
          <div class="history-log-header-txt">
            {{ $t('setup.setupLabel.energy_meter_name') }}
          </div>
        </el-col>
        <el-col :span="4">
          <div class="history-log-header-txt">
            {{ $t('common.wo_report.start_date') }}
          </div>
        </el-col>
        <el-col :span="4">
          <div class="history-log-header-txt">
            {{ $t('common.wo_report.end_date') }}
          </div>
        </el-col>
        <el-col :span="4">
          <div class="history-log-header-txt">
            {{ $t('setup.setupLabel.executed_by') }}
          </div>
        </el-col>
        <el-col :span="4">
          <div class="history-log-header-txt">
            {{ $t('setup.setupLabel.executed_time') }}
          </div>
        </el-col>
        <el-col :span="4">
          <div class="history-log-header-txt pL3">
            {{ $t('maintenance._workorder.status') }}
          </div>
        </el-col>
      </el-row>

      <el-collapse
        v-model="activeNames"
        :accordion="true"
        class="history-collapse"
      >
        <el-collapse-item
          v-for="(parent, index) in parentLog"
          :key="index"
          :name="index"
        >
          <template slot="title">
            <el-row class="history-collapse-header">
              <el-col :span="4">
                <div class="flex-middle pT10">
                  <i class="el-icon-caret-right"></i>
                  <i class="el-icon-caret-bottom"></i>
                  <div class="fc-black-com f13 line-height20">
                    {{ parent.resourceLogCount }} {{ parentResourceName }}
                  </div>
                </div>
              </el-col>
              <el-col :span="4" class="pB15">
                <div class="pT10">
                  <div class="fc-black-com f13 pL5 line-height20 fw4">
                    {{ $helpers.formatDateFull(parent.startTime) }}
                  </div>
                </div>
              </el-col>
              <el-col :span="4" class="pB15">
                <div class="pT10">
                  <div class="fc-black-com f13 pL8 line-height20 fw4">
                    {{ $helpers.formatDateFull(parent.endTime) }}
                  </div>
                </div>
              </el-col>
              <el-col :span="4">
                <div class="fc-black-com f13 line-height20 fw4 pT5">
                  <span>
                    <user-avatar
                      size="md"
                      :user="$store.getters.getUser(parent.createdBy)"
                    ></user-avatar>
                  </span>
                </div>
              </el-col>
              <el-col :span="4" class="pB15">
                <div class="pT10">
                  <div class="fc-black-com f13 pL5 line-height20 fw4">
                    {{ $helpers.formatDateFull(parent.createdTime) }}
                  </div>
                </div>
              </el-col>
              <el-col :span="4">
                <div
                  class="fc-black-com f13 bold line-height20 d-flex flex-col fw4 pL18"
                >
                  <div class="pB10">
                    {{ parent.resolvedLogCount }} out of
                    {{ parent.resourceLogCount }}
                  </div>
                  <div
                    v-bind:style="{
                      color: $constants.JOB_STATUS[parent.status].color,
                    }"
                    :class="'f14 line-height20 fw4'"
                    class="pB10"
                  >
                    {{ $constants.JOB_STATUS[parent.status].label }}
                  </div>
                </div>
              </el-col>
            </el-row>
          </template>
          <div class="mT10">
            <el-row v-if="parent.loading" class="history-collapse-child">
              <spinner :show="parent.loading" size="80"></spinner>
            </el-row>
            <el-row
              v-else
              class="history-collapse-child"
              v-for="child in childrenMap[parent.id]"
              :key="parent.id + child.resourceId"
            >
              <el-col :span="4" class>
                <div class="label-txt-black pT10 pB10 pL30">
                  {{ child.resourceContext.name }}
                </div>
              </el-col>
              <el-col :span="4">
                <div class="hide-v">
                  {{ $t('common.header.dont_remove_this_line') }}
                </div>
              </el-col>
              <el-col :span="4">
                <div class="hide-v">
                  {{ $t('common.header.dont_remove_this_line') }}
                </div>
              </el-col>
              <el-col :span="4">
                <div class="hide-v">
                  {{ $t('common.header.dont_remove_this_line') }}
                </div>
              </el-col>
              <el-col :span="4">
                <div class="hide-v">
                  {{ $t('common.header.dont_remove_this_line') }}
                </div>
              </el-col>
              <el-col :span="4" class="text-left pT10 pB10">
                <div
                  v-bind:style="{
                    color: $constants.JOB_STATUS[child.status].color,
                  }"
                  :class="'f14'"
                >
                  {{ $constants.JOB_STATUS[child.status].label }}
                </div>
              </el-col>
            </el-row>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    <kpi-historical-run
      v-if="showHistoryDialog"
      @callBack="saveData"
      @close="() => (showHistoryDialog = false)"
      :details="details"
      :resourceType="parentResourceType"
    ></kpi-historical-run>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import KpiHistoricalRun from './kpiHistoricalDialog'

export default {
  components: { UserAvatar, KpiHistoricalRun },

  props: ['details', 'tab'],

  data() {
    return {
      childrenMap: {},
      showHistoryDialog: false,
      parentLog: null,
      parentLogLoading: false,
      activeNames: [],
    }
  },

  created() {
    this.$store.dispatch('loadAssetCategory')
    this.init()
  },

  computed: {
    parentResourceName() {
      return this.details.matchedResources[0].resourceType === 1
        ? 'Space(s)'
        : 'Asset(s)'
    },

    parentResourceType() {
      return this.details.matchedResources[0].resourceType === 1
        ? 'space'
        : 'asset'
    },
  },

  methods: {
    init() {
      this.parentLogLoading = true
      let url = `/v2/historicalLogger/formulaFieldParentLoggers?formulaId=${this.details.id}`
      this.$http.get(url).then(({ data }) => {
        if (data.responseCode === 0) {
          this.parentLog = data.result.historicalFormulaFieldParentLoggers
          this.parentLog.forEach(element => {
            element.loading = true
            this.loadChildren(element).then(data => {
              let index = this.parentLog.findIndex(log => log.id === data.id)
              this.$set(this.parentLog, index, data)

              if (
                Object.keys(this.childrenMap).length === this.parentLog.length
              )
                this.parentLogLoading = false
            })
          })

          if (this.parentLog.length === 0) this.parentLogLoading = false
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
      })
    },

    saveData(data) {
      let url = `/v2/reading/formula/historicalCalculation/${this.details.id}`
      let params = {
        startTime: data.startTime,
        endTime: data.endTime,
      }
      if (data.historicalLoggerAssetIds.length !== 0) {
        params.historicalLoggerAssetIds = data.historicalLoggerAssetIds
        params.isInclude = data.isInclude
      }

      this.$http
        .post(url, params)
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.$message.success(data.result.success)
            this.init()
          } else {
            this.$message.error(data.message)
          }
          this.showHistoryDialog = false
        })
        .catch(() => {
          this.$message.error(
            this.$t('common._common.failed_to_run_historical_rule')
          )
          this.showHistoryDialog = false
        })
    },

    loadChildren(parentData) {
      this.parentLogLoading = true
      let url = `/v2/historicalLogger/formulaFieldChildLoggers?loggerGroupId=${parentData.id}`
      return this.$http.get(url).then(({ data }) => {
        if (data.responseCode === 0) {
          this.childrenMap[parentData.loggerGroupId] =
            data.result.historicalFormulaFieldChildLoggers
          parentData.loading = false
          return parentData
        }
      })
    },
  },
}
</script>
<style lang="scss">
.kpi-logs {
  .el-collapse-item__arrow.el-icon-arrow-right {
    visibility: hidden;
  }
  .el-collapse-item__content {
    padding: 0px;
  }
  height: 100%;
}
</style>
