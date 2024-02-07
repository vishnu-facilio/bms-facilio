<template>
  <div>
    <portal :to="tab.name + '-title-section'">
      <div
        class="widget-header d-flex flex-direction-row justify-content-space flex-middle rca-header pL20"
      >
        <div class="fc-black-com f18 ">Root Causes</div>
        <div class="fR position-relative" style="margin-left: auto;top: 5px;">
          <new-date-picker
            :zone="$timezone"
            class="filter-field date-filter-comp"
            style="margin-left: auto;"
            :dateObj.sync="dateObj"
            @date="changeDateFilter"
          ></new-date-picker>
        </div>
      </div>
    </portal>
    <div v-if="loading" class="flex-middle fc-empty-white block">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else-if="rcaAlarms.length > 0">
      <div class="width100 pT5 pB10">
        <el-row class="flex-middle pL20 pR20">
          <el-col :span="24">
            <div class="fc-black-com f16 bold text-capitalize pT10 pB10">
              {{ details.alarm.resource ? details.alarm.resource.name : '---' }}
            </div>
          </el-col>
        </el-row>
        <div class="fc-anomalies-head-barcell">
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
        class="fc-anamoly-rca-collapse"
      >
        <el-collapse-item
          v-for="(rca, index) in rcaAlarms"
          :key="index"
          :name="rca.id"
          class="position-relative"
        >
          <template slot="title">
            <div class="width100" v-if="rca.viewDetails">
              <el-row class="flex-middle pL20 pR20">
                <el-col :span="14">
                  <div
                    class="fc-black-com bold f14 text-capitalize visibility-visible-actions"
                  >
                    {{ rca.resource ? rca.resource.name : '---' }}
                    <i
                      @click="openRcaAlarm(rca)"
                      class="visibility-hide-actions el-icon-right fc-grey"
                    ></i>
                  </div>
                </el-col>
                <el-col :span="10">
                  <div class="flex-middle justify-content-end">
                    <div class="fc-black3-13 text-capitalize bold">
                      <span class="fc-grey2 text-uppercase f11"
                        >No of Anomalies :</span
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
                </el-col>
              </el-row>
              <div class="alarm-bar-cell">
                <alarm-bar
                  :parentId="rca.alarm.id"
                  :sourceKey="'alarmId'"
                  :isRca="true"
                  :parentAlarmId="details.alarm.id"
                  :dateOperator="dateOperator"
                  :dateValue="dateValue"
                ></alarm-bar>
              </div>
            </div>
          </template>
          <div class="clear" v-if="rca.relatedAsset">
            <el-collapse
              ref="assetlistcollapse"
              v-model="assetActiveRule"
              accordion
              class="position-relative"
            >
              <el-collapse-item
                v-for="asset in rca.relatedAsset"
                :key="asset.resource.id"
                :name="asset.id"
                class="position-relative"
              >
                <template slot="title">
                  <div class="width100">
                    <el-row class="flex-middle pL20 pR20">
                      <el-col :span="14">
                        <div class="fc-black-com bold f14 text-capitalize">
                          {{ asset.subject }}
                        </div>
                      </el-col>
                      <el-col :span="10">
                        <div class="flex-middle justify-content-end">
                          <div class="fc-black3-13 text-capitalize bold">
                            <span class="fc-grey2 text-uppercase f11"
                              >Total Alarms :</span
                            >
                            {{ asset.count ? asset.count : '---' }}
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
                              asset.count
                                ? $helpers.getFormattedDuration(asset.duration)
                                : '---'
                            }}
                          </div>
                        </div>
                      </el-col>
                    </el-row>

                    <div class="alarm-bar-cell">
                      <alarm-bar
                        ref="assetBarRef"
                        :parentAlarmId="details.alarm.id"
                        :isRCA="'true'"
                        :rca="isRca"
                        :isResize="isResize"
                        :parentId="asset.resource.id"
                        :sourceKey="'parentId'"
                        :dateOperator="dateOperator"
                        :dateValue="dateValue"
                      ></alarm-bar>
                    </div>
                  </div>
                </template>
                <!-- <div v-for="(asset) in rca.relatedAsset" :key="asset.resource.id" :name="asset.id"> -->
                <div
                  v-for="rule in asset.ruleBar"
                  :key="rule.rule.id"
                  class="clearboth"
                >
                  <div class="width100">
                    <el-row class="flex-middle pL20 pR20 pB10">
                      <el-col :span="14">
                        <div class="fc-black-com f12 bold text-capitalize">
                          {{ rule.subject }}
                        </div>
                      </el-col>
                      <el-col :span="10">
                        <div class="flex-middle justify-content-end">
                          <div class="fc-black3-13 text-capitalize bold">
                            <span class="fc-grey2 text-uppercase f11"
                              >Total Alarms :</span
                            >
                            {{ rule.count ? rule.count : '---' }}
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
                              rule.count
                                ? $helpers.getFormattedDuration(rule.duration)
                                : '---'
                            }}
                          </div>
                        </div>
                      </el-col>
                    </el-row>
                    <div class="alarm-bar-cell clearboth">
                      <alarm-bar
                        :parentAlarmId="details.alarm.id"
                        :isRCA="'true'"
                        :rca="isRca"
                        :isNoDropButtom="true"
                        :parentId="rule.rule.id"
                        :isResize="isResize"
                        :resourceId="asset.resource.id"
                        :sourceKey="'ruleId'"
                        :dateOperator="dateOperator"
                        :dateValue="dateValue"
                        class="clearboth"
                      ></alarm-bar>
                    </div>
                  </div>
                </div>
                <!-- </div> -->
              </el-collapse-item>
            </el-collapse>
            <div class="clear">
              <div class="">
                <div class="">
                  <f-new-analytic-report
                    v-if="
                      rca.latestAlarmOccurrence.id &&
                        rca.analyticsConfig.intersections
                    "
                    class="alarm-summary-chart anomaliesrca-summary-chart"
                    ref="newAlarmAnalyticReport"
                    :config="rca.analyticsConfig"
                  ></f-new-analytic-report>
                </div>
              </div>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    <div v-else-if="parentRelatedAssets.length > 0">
      <div class="width100 pT5 pB10">
        <el-row class="flex-middle pL20 pR20">
          <el-col :span="24">
            <div class="fc-black-com f16 bold text-capitalize pT10 pB10">
              {{ details.alarm.resource ? details.alarm.resource.name : '---' }}
            </div>
          </el-col>
        </el-row>
        <div class="fc-anomalies-head-barcell">
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
        v-model="parentActiveRule"
        accordion
        class="fc-anamoly-rca-collapse"
      >
        <el-collapse-item
          v-for="(rca, index) in parentRelatedAssets"
          :key="index"
          :name="rca.id"
          class="position-relative"
        >
          <template slot="title">
            <div class="width100">
              <el-row class="flex-middle pL20 pR20">
                <el-col :span="14">
                  <div class="fc-black-com bold f14 text-capitalize">
                    {{ rca.subject ? rca.subject : '---' }}
                  </div>
                </el-col>
                <el-col :span="10">
                  <div class="flex-middle justify-content-end">
                    <div class="fc-black3-13 text-capitalize bold">
                      <span class="fc-grey2 text-uppercase f11"
                        >No of Alarms :</span
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
                </el-col>
              </el-row>
              <div class="alarm-bar-cell">
                <alarm-bar
                  :parentId="rca.resource.id"
                  :sourceKey="'parentId'"
                  :dateOperator="dateOperator"
                  :dateValue="dateValue"
                  :parentAlarmId="details.alarm.id"
                  :isRCA="'true'"
                  :rca="isRca"
                ></alarm-bar>
              </div>
            </div>
          </template>
          <div>
            <div v-for="rule in rca.ruleBar" :key="rule.rule.id">
              <div class="width100">
                <el-row class="flex-middle pL20 pR20">
                  <el-col :span="14">
                    <div class="fc-black-com f12 bold text-capitalize">
                      {{ rule.subject }}
                    </div>
                  </el-col>
                  <el-col :span="10">
                    <div class="flex-middle justify-content-end">
                      <div class="fc-black3-13 text-capitalize bold">
                        <span class="fc-grey2 text-uppercase f11"
                          >Total Alarms :</span
                        >
                        {{ rule.count ? rule.count : '---' }}
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
                          rule.count
                            ? $helpers.getFormattedDuration(rule.duration)
                            : '---'
                        }}
                      </div>
                    </div>
                  </el-col>
                </el-row>
                <div class="alarm-bar-cell">
                  <alarm-bar
                    :isResize="isResize"
                    :parentId="rule.rule.id"
                    :resourceId="rule.resource.id"
                    :sourceKey="'ruleId'"
                    :dateOperator="dateOperator"
                    :dateValue="dateValue"
                    :parentAlarmId="details.alarm.id"
                    :isRCA="'true'"
                    :rca="isRca"
                  ></alarm-bar>
                </div>
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
        <div class="pT20 fc-black-dark f18 bold">No Root Cause Analysis!</div>
      </div>
    </div>
  </div>
</template>
<script>
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import NewDateHelper from '@/mixins/NewDateHelper'
import NewDatePicker from '@/NewDatePicker'
import AlarmBar from '@/AlarmBar'
import FNewAnalyticReport from 'pages/energy/analytics/newTools/v1/FNewAnalyticsReportV1'
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
        }
      },
    },
  },
  components: {
    NewDatePicker,
    AlarmBar,
    FNewAnalyticReport,
  },
  props: ['details', 'widget', 'tab'],
  data() {
    return {
      rcaAlarms: [],
      parentRelatedAssets: [],
      parentActiveRule: null,
      list: null,
      isRca: true,
      isResize: false,
      loading: {},
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
    this.dateValue = [
      this.details.alarm.lastOccurredTime - 6 * 24 * 60 * 60 * 1000,
      this.details.alarm.lastOccurredTime,
    ]
    this.dateObj = NewDateHelper.getDatePickerObject(20, [
      this.details.alarm.lastOccurredTime - 6 * 24 * 60 * 60 * 1000,
      this.details.alarm.lastOccurredTime,
    ])
    this.loadRCAReport()
    this.loadParentRelatedAssets()
  },
  methods: {
    loadParentRelatedAssets() {
      let url = `/v2/mlAnomalyAlarm/relatedAsset`
      if (this.details.alarm.resource.id > 0) {
        url += `?resourceId=${this.details.alarm.resource.id}`
        url += `&parentAlarmId=${this.details.alarm.id}`

        if (this.dateObj && this.dateObj.operatorId) {
          let operatorId = this.dateOperator

          url += NewDateHelper.isValueRequired(operatorId)
            ? `&dateOperator=${operatorId}&dateOperatorValue=${this.dateValue}`
            : `&dateOperator=${operatorId}`
        }
        let promise = this.$http.get(url).then(response => {
          if (response.data.responseCode === 0) {
            this.parentRelatedAssets = response.data.result.alarms
            if (this.parentRelatedAssets.length > 0) {
              this.parentRelatedAssets.forEach(d => {
                this.loadAlarmInsightsResource(d)
              })
            }
          }
        })
        Promise.all([promise]).finally(() => (this.loading = false))
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
      this.loadParentRelatedAssets()
    },
    openRcaAlarm(id) {
      let url = '/app/fa/anomalies/all/' + id + '/summary'
      this.$router.replace(url)
    },
    loadRCAReport() {
      this.loading = true
      if (this.details.alarm) {
        let url =
          'v2/mlAnomalyAlarm/rca?mlAnomalyAlarmId=' + this.details.alarm.id
        if (this.dateObj && this.dateObj.operatorId) {
          let operatorId = this.dateOperator
          this.dateValue = this.dateObj.value.join()
          url += NewDateHelper.isValueRequired(operatorId)
            ? `&dateOperator=${operatorId}&dateOperatorValue=${this.dateValue}`
            : `&dateOperator=${operatorId}`
        }
        let self = this
        self.$http
          .get(url)
          .then(function(response) {
            self.loading = false
            self.rcaAlarms = response.data.result.mlRcaAlarms
            if (self.rcaAlarms.length > 0) {
              self.rcaAlarms.forEach(d => {
                self.$set(d, 'analyticsConfig', null)
                self.$set(d, 'analyticsConfig', {
                  alarmId: d.latestAlarmOccurrence.id,
                  dateFilter: NewDateHelper.getDatePickerObject(
                    self.dateOperator,
                    self.dateValue
                  ),
                  hidechartoptions: true,
                  hidetabular: true,
                  hidedatepicker: true,
                  hidecharttypechanger: true,
                  fixedChartHeight: 300,
                  showAlarms: false,
                  isFromAlarmSummary: true,
                  chartType: 'line',
                  period: 20,
                  size: { width: self.$el.clientWidth + 100 },
                  axis: { y: { show: false } },
                  colors: {
                    A: '#39c2b0',
                    B: '#c7d1e9',
                  },
                  axes: {
                    A: 'y',
                    B: 'y',
                  },
                  xFormat: 'MM-DD-YYYY HH',
                  pointShowRule: {
                    //based on the dataRange object so it will custamize the rule show options
                    offset: 25,
                    operationOn: 'hour',
                    period: 'hourly',
                    show: true,
                  },
                  point: {
                    show: false,
                  },
                  disbaleNiceTickMarks: true,
                  intersections: [
                    {
                      from: {
                        point: 'B',
                        color: 'red',
                        disable: false,
                        patter: '',
                        class: 'above',
                      },
                      to: {
                        point: 'A',
                        color: 'blue',
                        disable: false,
                        patter: '',
                        class: 'below',
                      },
                      context: {
                        id: 'rcabounds-' + d.latestAlarmOccurrence.id,
                        class: 'rcabounds',
                      },
                    },
                  ],
                })
                self.loadAlarmInsights(d)
              })
            }
          })
          .catch(() => {})
      }
    },
    loadRelatedAssets(rca) {
      let url = `/v2/mlAnomalyAlarm/relatedAsset`
      if (rca.resource.id > 0) {
        this.$set(rca, 'relatedAsset', [])
        url += `?resourceId=${rca.resource.id}`
        url += `&parentAlarmId=${this.details.alarm.id}`

        if (this.dateObj && this.dateObj.operatorId) {
          let operatorId = this.dateOperator

          url += NewDateHelper.isValueRequired(operatorId)
            ? `&dateOperator=${operatorId}&dateOperatorValue=${this.dateValue}`
            : `&dateOperator=${operatorId}`
        }
        let promise = this.$http.get(url).then(response => {
          if (response.data.responseCode === 0) {
            this.$set(rca, 'relatedAsset', response.data.result.alarms)
            rca.relatedAsset.forEach(d => {
              this.loadAlarmInsightsResource(d)
            })
          }
        })
        Promise.all([promise]).finally(() => (this.loading = false))
      }
    },
    loadAlarmDetails(rca) {
      let url = `/v2/alarm/get?id=${rca.alarm.id}`
      let promise = this.$http.get(url).then(response => {
        if (response.data.responseCode === 0) {
          this.$set(rca, 'alarmDetails', response.data.result)
          this.$set(rca, 'analyticsConfig', null)
          this.$set(rca, 'analyticsConfig', {
            alarmId: response.data.result.latestAlarmOccurrence.id,
            dateFilter: NewDateHelper.getDatePickerObject(
              self.dateOperator,
              self.dateValue
            ),
            hidechartoptions: true,
            hidetabular: true,
            hidecharttypechanger: true,
            showAlarms: false,
            fixedChartHeight: 300,
            isFromAlarmSummary: true,
          })
        }
      })
      Promise.all([promise]).finally(() => (this.loading = false))
    },
    loadAlarmInsightsResource(d) {
      this.loading = true
      let url = `/v2/alarms/insights`
      url += `?assetId=${d.resource.id}`
      url += `&parentAlarmId=${this.details.alarm.id}`

      if (this.dateObj && this.dateObj.operatorId) {
        let operatorId = this.dateOperator

        url += NewDateHelper.isValueRequired(operatorId)
          ? `&dateOperator=${operatorId}&dateOperatorValue=${this.dateValue}`
          : `&dateOperator=${operatorId}`
      }
      let promise = this.$http.get(url).then(response => {
        if (response.data.responseCode === 0) {
          this.$set(d, 'ruleBar', response.data.result.alarms)
        }
      })
      Promise.all([promise]).finally(() => (this.loading = false))
    },
    loadAlarmInsights(rca) {
      this.loading = true
      rca.rcaloading = true
      let url = `/v2/mlAnomalyAlarm/rcaInsights`

      let ruleId = rca.alarm.id
      url += `?alarmId=${ruleId}`
      url += `&parentAlarmId=${this.details.alarm.id}`

      if (this.dateObj && this.dateObj.operatorId) {
        let operatorId = this.dateOperator

        url += NewDateHelper.isValueRequired(operatorId)
          ? `&dateOperator=${operatorId}&dateOperatorValue=${this.dateValue}`
          : `&dateOperator=${operatorId}`
      }
      if (this.isRCA) {
        url += `&isRca=${this.isRCA}`
      }
      this.$set(rca, 'viewDetails', null)
      let promise = this.$http
        .get(url)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$set(rca, 'viewDetails', response.data.result.alarm[0])
            if (rca.viewDetails) {
              this.loadRelatedAssets(rca)
            }
          } else {
            this.list = []
          }
        })
        .catch(() => (this.list = []))
      Promise.all([promise]).finally(() => (this.loading = false))
    },
  },
}
</script>
<style lang="scss">
.fc-anamoly-rca-collapse .el-collapse-item__header {
  height: 100px;
  padding-bottom: 14px;
}
.fc-anamoly-rca-collapse .el-collapse-item__header .el-collapse-item__arrow {
  display: block !important;
  position: absolute;
  right: 0;
  top: 45px;
  width: 26px;
  height: 26px;
  border: solid 1px #e7eeee;
  background-color: #f3f6f6;
  border-radius: 50%;
  text-align: center;
  line-height: 26px;
  font-weight: bold;
  transform: rotate(-90deg);
}
.fc-anamoly-rca-collapse .el-collapse-item__arrow.is-active {
  transform: rotate(90deg);
}
.fc-anamoly-rca-collapse
  .widget-alarmbar.booleancard-chart
  .fc-alarms-chart.pdf-chart.bb,
.fc-anomalies-head-barcell
  .widget-alarmbar.booleancard-chart
  .fc-alarms-chart.pdf-chart.bb {
  right: 10px !important;
}
.rca-header {
  background: #fff;
  padding: 5px 20px;
  border-bottom: 1px solid #edf5f6;
}
.anomaliesrca-summary-chart .f-singlechart .fc-new-chart {
  right: 66px;
}

.anomaliesrca-summary-chart .analytics-section {
  margin-top: 0px !important;
}

.fc-anamoly-rca-collapse .f-multichart,
.fc-anomalies-head-barcell .f-multichart {
  padding-left: 24px !important;
  padding-left: 40px;
}

.fc-anamoly-rca-collapse .fc-alarms-chart.pdf-chart.bb,
.fc-anomalies-head-barcell .fc-alarms-chart.pdf-chart.bb {
  right: 0 !important;
}
.alarm-bar-cell .animated-background {
  width: 92.8%;
  top: -8px;
}
</style>
