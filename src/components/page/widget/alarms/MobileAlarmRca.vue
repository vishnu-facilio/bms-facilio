<template>
  <div>
    <div v-if="loading" class="flex-middle fc-empty-white block">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else-if="rcaAlarms.length > 0">
      <div class="width100 pT5 pB10">
        <el-row class="flex-middle pL20 pR20">
          <el-col :span="24">
            <div class="fc-black-com f16 bold text-capitalize pT10 pB10">
              {{ details.alarm.rule ? details.alarm.rule.name : '---' }}
            </div>
          </el-col>
        </el-row>
        <div class="fc-alarm-rca-collapse">
          <alarm-bar
            :parentId="details.alarm.id"
            :sourceKey="'alarmId'"
            :dateOperator="dateOperator"
            :dateValue="dateValue"
          ></alarm-bar>
        </div>
      </div>
      <el-collapse
        ref="alarmlistcollapse"
        v-model="activeRule"
        accordion
        class="fc-alarm-rca-collapse"
      >
        <el-collapse-item
          v-for="(rca, index) in rcaAlarms"
          :key="index"
          :name="rca.id"
          class="position-relative"
        >
          <template slot="title">
            <div class="width100 rca-head">
              <el-row class="flex-middle pL20 pR20">
                <el-col :span="14">
                  <div
                    class="fc-black-com bold f14 text-capitalize visibility-visible-actionsn rca-label"
                  >
                    {{ rca.rule ? ruleList[rca.rule.id] : '---' }}
                    <i
                      @click="openRcaAlarm(rca)"
                      class="visibility-hide-actions el-icon-right fc-grey"
                    ></i>
                  </div>
                </el-col>
                <!-- <el-col :span="10">
                  <div class="flex-middle justify-content-end">
                    <div class="fc-black3-13 text-capitalize bold">
                      <span class="fc-grey2 text-uppercase f11"
                        >No of Occurrence :</span
                      >
                      {{ rca.count ? rca.count : '---' }}
                    </div>
                    <el-divider
                      direction="vertical"
                      class="mL20 mR20"
                    ></el-divider>
                    <div class="fc-black3-13 text-capitalize bold">
                      <span class="fc-grey2 text-uppercase f11"
                        >Duration :</span
                      >
                      {{
                        rca.duration
                          ? $helpers.getFormattedDuration(rca.duration)
                          : '---'
                      }}
                    </div>
                  </div>
                </el-col> -->
              </el-row>
              <div class="alarm-bar-cell width100">
                <alarm-bar
                  :parentId="rca.alarm.id"
                  :alarmId="rca.alarm.id"
                  :sourceKey="'alarmId'"
                  :isRCA="'true'"
                  :rca="isRca"
                  :dateOperator="dateOperator"
                  :dateValue="dateValue"
                  class="fc-v1-alarm-cell"
                  :parentAlarmId="details.alarm.id"
                ></alarm-bar>
              </div>
            </div>
          </template>
          <div class="clear">
            <div class="">
              <div class="">
                <f-new-analytic-report
                  v-if="rca.latestAlarmOccurrence.id"
                  class="alarm-summary-chart fc-v1-alarm-chart alarmrca-summary-chart width100 pT30"
                  ref="newAlarmAnalyticReport"
                  :config="rca.analyticsConfig"
                ></f-new-analytic-report>
              </div>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    <div v-else class="block">
      <div class="mT40 mB40 text-center p30imp">
        <InlineSvg
          src="svgs/emptystate/alarmEmpty"
          iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
        >
        </InlineSvg>
        <div class="fc-black-dark f18 bold">No Root Cause Analysis!</div>
      </div>
    </div>
  </div>
</template>
<script>
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import NewDateHelper from '@/mixins/NewDateHelper'
import AlarmBar from '@/AlarmBar'
import FNewAnalyticReport from 'pages/energy/analytics/newTools/v1/FNewAnalyticsReportV1'
import { getFieldOptions } from 'util/picklist'

export default {
  watch: {
    parentActiveRule: {
      handler: function(newValue) {
        if (newValue !== null) {
          this.isResize = !this.isResize
        }
      },
    },
    assetActiveRule: {
      handler: function(newValue) {
        if (newValue !== null) {
          this.isResize = !this.isResize
        }
      },
    },
    activeRule: {
      handler: function(newValue) {
        if (newValue !== null) {
          this.isResize = !this.isResize
          if (newValue != '') {
            for (let i = 0; i < this.rcaAlarms.length; i++) {
              this.$refs['newAlarmAnalyticReport'][i].resize()
            }
          }
        }
      },
    },
  },
  components: {
    AlarmBar,
    FNewAnalyticReport,
  },
  props: ['details', 'widget', 'tab', 'width'],
  data() {
    return {
      ruleList: [],
      rcaAlarms: [],
      parentRelatedAssets: [],
      parentActiveRule: null,
      list: null,
      isResize: false,
      isRca: true,
      loading: false,
      assetActiveRule: null,
      localDateFormat: [22, 25, 31, 30, 28, 27, 44, 45],
      configData: {
        height: 40,
        width: 100,
        dateFilter: NewDateHelper.getDatePickerObject(20),
      },
      dateObj: NewDateHelper.getDatePickerObject(
        20,
        this.details.alarm
          ? [
              this.details.alarm.lastOccurredTime - 6 * 24 * 60 * 60 * 1000,
              this.details.alarm.lastOccurredTime,
            ]
          : null
      ),
      dateOperator: 20,
      assetChart: {},
      dateValue: null,
      activeRule: null,
    }
  },
  mixins: [NewDataFormatHelper, NewDateHelper],

  mounted() {
    this.loadReadingRulePickListData()
    this.dateValue = [
      this.details.alarm.lastOccurredTime - 6 * 24 * 60 * 60 * 1000,
      this.details.alarm.lastOccurredTime,
    ]
    this.dateObj = NewDateHelper.getDatePickerObject(20, [
      this.details.alarm.lastOccurredTime - 6 * 24 * 60 * 60 * 1000,
      this.details.alarm.lastOccurredTime,
    ])
    this.loadRCAReport()
  },
  methods: {
    async loadReadingRulePickListData() {
      let fetchOptions = {
        lookupModuleName: 'readingrule',
        skipDeserialize: true,
      }
      let { error, options } = await getFieldOptions({ field: fetchOptions })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.ruleList = options
      }
    },
    changeDateFilter(dateFilter) {
      this.dateObj = dateFilter
      // TODO...use alarmbarmixin for booleancard and alarminsight
      if (this.localDateFormat.includes(dateFilter.operatorId)) {
        this.dateOperator = dateFilter.operatorId
        this.dateValue = null
      } else {
        this.dateOperator = 20
        this.dateValue = dateFilter.value.join()
      }
      this.loadRCAReport()
    },
    openRcaAlarm(rca) {
      let url = '/app/fa/faults/all/newsummary/' + rca.alarm.id
      this.$router.replace(url)
    },
    loadRCAReport() {
      this.loading = true
      if (this.details.alarm) {
        let url =
          'v2/alarm/rcaAlarms?id=' +
          this.details.alarm.id +
          '&ruleId=' +
          this.details.alarm.rule.id
        if (this.dateObj && this.dateObj.operatorId) {
          let operatorId = this.dateOperator
          this.dateValue = this.dateObj.value.join()
          url += NewDateHelper.isValueRequired(operatorId)
            ? `&dateOperator=${operatorId}&dateOperatorValue=${this.dateValue}`
            : `&dateOperator=${operatorId}`
        }
        this.$http
          .get(url)
          .then(response => {
            this.loading = false
            this.rcaAlarms = response.data.result.rcaAlarm
              ? response.data.result.rcaAlarm
              : []
            if (this.rcaAlarms.length > 0) {
              this.rcaAlarms.forEach(d => {
                this.$set(d, 'analyticsConfig', null)
                this.$set(d, 'analyticsConfig', {
                  alarmId: d.latestAlarmOccurrence.id,
                  dateFilter: NewDateHelper.getDatePickerObject(
                    this.dateOperator,
                    this.dateValue
                  ),
                  hidedatepicker: true,
                  hidechartoptions: true,
                  hidetabular: true,
                  size: { width: this.width },
                  hidecharttypechanger: true,
                  showAlarms: false,
                  showAlarmBar: false,
                  isWithPrerequsite: true,
                  fixedChartHeight: 300,
                  isFromAlarmSummary: true,
                })
              })
            }
          })
          .catch(() => {})
      }
    },
  },
}
</script>
<style lang="scss">
.fc-v1-alarm-chart {
  position: relative;
  right: 0px;
}
.rca-label {
  white-space: nowrap;
}
.widget-alarmbar.booleancard-chart .f-multichart {
  margin-top: 0px;
}
.el-collapse-item.position-relative {
  min-height: 120px;
}
.rca-head {
  position: relative;
  top: 30px;
}
</style>
