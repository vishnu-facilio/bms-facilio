<template>
  <div class="default-portal-dashboard">
    <div v-if="loading">
      <div class="sk-cube-grid">
        <div class="sk-cube sk-cube1"></div>
        <div class="sk-cube sk-cube2"></div>
        <div class="sk-cube sk-cube3"></div>
        <div class="sk-cube sk-cube4"></div>
        <div class="sk-cube sk-cube5"></div>
        <div class="sk-cube sk-cube6"></div>
        <div class="sk-cube sk-cube7"></div>
        <div class="sk-cube sk-cube8"></div>
        <div class="sk-cube sk-cube9"></div>
      </div>
    </div>
    <div v-else>
      <el-row :gutter="20">
        <template v-if="!cardloading">
          <el-col :span="6" v-for="(card, index) in cards" :key="index"
            ><div class="portal-card" :style="getcardStyle(card)">
              <div class="row content">
                <div class="col-5">
                  <div class="card-label">{{ card.name }}</div>
                  <div class="cardArrow">
                    <img class="" src="~assets/arrow-pointing-img.svg" />
                  </div>
                </div>
                <div class="col-7">
                  <div class="card-value" :style="getStyle(card)">
                    {{ card.value }}
                  </div>
                </div>
              </div>
              <img class="cardbg" src="~assets/patten_1.png" />
            </div>
          </el-col>
        </template>
      </el-row>
      <el-row :gutter="20" class="pT20">
        <el-col :span="12" class="portal-dashboard-chart-width">
          <div class="sp-default-requestList">
            <div class="requestlist-header row">
              <div class="col-6 label">
                Request List
              </div>
              <div class="col-6 text-right opento">
                <div
                  class="opentobtn pointer"
                  @click="$router.push({ path: '/service/requests/myrequest' })"
                >
                  view all
                </div>
              </div>
            </div>
            <div
              class="requestlist-data"
              v-if="!$validation.isEmpty(requestList)"
            >
              <div
                class="row requestlist pointer"
                v-for="(request, index) in requestList"
                :key="index"
                @click="
                  $router.push({
                    path: '/service/requestsummary/' + request.id,
                  })
                "
              >
                <div class="col-7">
                  <div class="id">
                    #{{ request.id }}
                    <span
                      v-if="request.urgencyEnum"
                      class="wotag"
                      :class="request.urgencyEnum"
                      >{{ request.urgencyEnum }}</span
                    >
                  </div>
                  <div class="subject">
                    {{ request.subject }}
                  </div>
                </div>
                <div class="col-2 status">
                  <span v-if="request.status && request.status.displayName">{{
                    request.status.displayName | convertion()
                  }}</span>
                </div>
                <div class="col-3 requester text-center">
                  <avatar
                    size="sm"
                    :user="{
                      name: request.assignedTo
                        ? request.assignedTo.name
                        : 'Not Assigned',
                    }"
                    v-if="request.assignedTo"
                  ></avatar>
                  <span class="pL5">{{
                    request.assignedTo
                      ? request.assignedTo.name
                      : 'Not Assigned'
                  }}</span>
                </div>
              </div>
            </div>
            <div class="requestlist-data empty"></div>
          </div>
        </el-col>
        <el-col
          :span="12"
          class="portal-dashboard-chart-width"
          v-if="reportLoading"
        >
          <spinner :show="true" size="80"></spinner>
        </el-col>
        <el-col :span="12" class="portal-dashboard-chart-width" v-else>
          <div class="sp-default-graph">
            <div v-if="reportloading"></div>
            <div v-else class="graph-container">
              <div class="graph-header">Work Request Trend</div>
              <new-date-picker
                ref="newDatePicker"
                :zone="$timezone"
                class="filter-field date-filter-comp inline default-datefilter"
                :dateObj="serverConfig.dateFilter"
                @date="setDateFilter"
              ></new-date-picker>
              <f-new-analytic-modular-report
                ref="analyticReport"
                :module="moduleData"
                :serverConfig.sync="serverConfig"
                :height="370"
                :mergeOption="options"
                class="portal-legend-hide"
              ></f-new-analytic-modular-report>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import Avatar from '@/Avatar'
import newDateHelper from '@/mixins/NewDateHelper'
import FNewAnalyticModularReport from 'src/pages/energy/analytics/components/FNewAnalyticModularReport'
import NewDatePicker from '@/NewDatePicker'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'

export default {
  mixins: [newDateHelper],

  components: {
    Avatar,
    FNewAnalyticModularReport,
    NewDatePicker,
  },

  data() {
    return {
      requestList: [],
      loading: false,
      reportLoading: false,
      options: {
        padding: {
          top: 40,
          bottom: 10,
          left: 80,
          right: 50,
        },
      },
      cardloading: true,
      reportloading: false,
      fields: [],
      moduleData: {
        moduleName: 'workorder',
        moduleId: 21320,
        resourceField: {},
        meta: {
          fieldMeta: {
            xField: 21320,
          },
        },
      },
      serverConfig: {
        xField: {
          field_id: 337067,
          aggr: 10,
          module_id: 21320,
        },
        isTime: true,
        yField: null,
        groupBy: null,
        criteria: {
          pattern: '1',
          conditions: {
            '1': {
              fieldName: 'requester',
              columnName: 'REQUESTER_ID',
              operatorId: 36,
              value: '${LOGGED_USER}',
            },
          },
        },
        sortFields: null,
        sortOrder: null,
        limit: null,
        isCustomDateField: false,
        mode: 4,
        hideChart: false,
        dateFilter: newDateHelper.getDatePickerObject(44),
        dateField: {
          operator: 44,
          field_id: 337067,
          module_id: 21320,
          date_value: '1555785000000',
        },
        moduleType: 3,
      },
      cardData: {},
      cards: [
        {
          name: 'Open Request',
          bgcolor: '#5473e8',
          key: 'open',
          value: 0,
          Rkey: 'status',
          Rkey1: 'type',
          operator: 'NOTEQUAL',
          compareValue: 'CLOSED',
        },
        {
          name: 'Yet to Assigned',
          bgcolor: '#36c2cf',
          value: 0,
          key: 'unnassigned',
          Rkey: 'assignedTo',
          operator: 'EQUAL',
          compareValue: null,
        },
        {
          name: 'In progress',
          bgcolor: '#3e4eaa',
          value: 0,
          key: 'inprogress',
          Rkey: 'status',
          Rkey1: 'status',
          operator: 'EQUAL',
          compareValue: 'Work in Progress',
        },
        {
          name: 'Urgent',
          bgcolor: '#a570ff',
          value: 0,
          key: 'urgent',
          Rkey: 'urgencyEnum',
          operator: 'EQUAL',
          compareValue: 'URGENT',
        },
      ],
    }
  },

  created() {
    this.loadReportMetaData()
  },

  mounted() {
    this.loading = true
    this.cardloading = true

    let url = '/v2/workorders/count'
    let params = {
      viewName: 'myrequests',
      count: true,
      includeParentFilter: true,
    }

    Promise.all([
      this.loadOpenCount(url, params),
      this.loadUnassigned(url, params),
      this.loadUrgencyCount(url, params),
      this.loadInprogress(url, params),
    ]).then(() => {
      this.cardloading = false
      this.loadRequestList()
    })
  },

  filters: {
    convertion(data) {
      if (data) {
        if (data === 'Pre-Open') {
          return 'Open'
        } else {
          return data
        }
      }
    },
  },

  computed: {
    ...mapState({
      ticketstatus: state => state.ticketstatus,
    }),
  },

  methods: {
    getcardStyle(data) {
      let style = 'background:' + data.bgcolor
      return style
    },
    getStyle(obj) {
      if (obj && obj.value) {
        let length = obj.value.toString().length
        if (length > 4) {
          return 'font-size:60px'
        }
      }
      return ''
    },
    setFilters(criteria, state) {
      let filters = {}
      let value

      if (state === 'open') {
        value = this.ticketstatus
          .filter(rt => rt.type === 'CLOSED')
          .map(rt => rt.id)
          .toString()
          .split(',')

        filters[criteria] = { operatorId: 10, value }
      } else if (state === 'inprogress') {
        value = this.ticketstatus
          .filter(rt => rt.type === 'CLOSED')
          .map(rt => rt.id)
          .toString()
          .split(',')

        filters = {
          actualWorkStart: { operatorId: 2, value: [''] },
          status: { operatorId: 37, value },
        }
      } else if (state === 'urgent') {
        value = this.ticketstatus
          .filter(rt => rt.type === 'CLOSED')
          .map(rt => rt.id)
          .toString()
          .split(',')

        filters = {
          status: { operatorId: 37, value },
          urgency: { operatorId: 9, value: ['2'] },
        }
      } else if (state === 'assignedTo') {
        value = this.ticketstatus
          .filter(rt => rt.type === 'CLOSED')
          .map(rt => rt.id)
          .toString()
          .split(',')

        filters = {
          status: { operatorId: 37, value },
          assignedTo: { operatorId: 1 },
        }
      }
      return filters
    },
    loadOpenCount(url, params) {
      params.filters = JSON.stringify(this.setFilters('status', 'open'))
      return API.get(url, params).then(({ data, error }) => {
        if (!error && data) {
          this.cardData[this.cards[0].key] = data.woCount || 0
          this.cards[0].value = data.woCount || 0
        }
      })
    },
    loadInprogress(url, params) {
      params.filters = JSON.stringify(this.setFilters('status', 'inprogress'))
      return API.get(url, params).then(({ data, error }) => {
        if (!error && data) {
          this.cardData[this.cards[2].key] = data.woCount || 0
          this.cards[2].value = data.woCount || 0
        }
      })
    },
    loadUrgencyCount(url, params) {
      params.filters = JSON.stringify(this.setFilters('status', 'urgent'))
      return API.get(url, params).then(({ data, error }) => {
        if (!error && data) {
          this.cardData[this.cards[3].key] = data.woCount || 0
          this.cards[3].value = data.woCount || 0
        }
      })
    },
    loadUnassigned(url, params) {
      params.filters = JSON.stringify(this.setFilters('status', 'assignedTo'))
      return API.get(url, params).then(({ data, error }) => {
        if (!error && data) {
          this.cardData[this.cards[1].key] = data.woCount || 0
          this.cards[1].value = data.woCount || 0
        }
      })
    },
    loadRequestList() {
      let url = '/v2/workorders'
      let params = {
        page: 1,
        per_page: 40,
        orderBy: 'createdTime',
        orderType: 'asc',
        includeParentFilter: true,
      }

      API.get(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.requestList = data.workorders || []
        }

        this.loading = false
      })
    },
    loadReportMetaData() {
      let url = '/v2/report/getReportFields'
      let param = { moduleName: 'workorder' }

      this.reportloading = true
      API.post(url, param).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.prepareConfig(data.meta)
          this.prepareData(data.meta)
        }
        this.reportloading = false
      })
    },
    prepareConfig(data) {
      let { fields } = data || {}

      if (!isEmpty(fields)) {
        let fieldObj = fields.find(rt => rt.columnName === 'STATUS_ID')

        if (fieldObj && fieldObj.id) {
          this.serverConfig.xField.field_id = fieldObj.id
          this.serverConfig.field_id = fieldObj.id
        }

        this.fields = fields
      }
    },
    prepareData(data) {
      let { resource_fields, time } = this.$getProperty(data, 'dimension', {})

      if (!isEmpty(resource_fields)) {
        let resourceField = resource_fields.find(
          rt => rt.columnName === 'RESOURCE_ID'
        )

        this.moduleData.resourceField = resourceField || {}

        if (this.moduleData.resourceField.moduleId) {
          let moduleId = this.moduleData.resourceField.moduleId

          this.moduleData.moduleId = moduleId
          this.moduleData.meta.fieldMeta.xField = moduleId
          this.serverConfig.xField.module_id = moduleId
          this.serverConfig.dateField.module_id = moduleId
        }
      }

      if (!isEmpty(time)) {
        let fieldObj = time.find(rt => rt.columnName === 'CREATED_TIME') || {}

        if (!isEmpty(fieldObj)) {
          this.serverConfig.xField.field_id = fieldObj.fieldId
          this.serverConfig.dateField.field_id = fieldObj.fieldId
        }
      }
    },
    setDateFilter(date) {
      this.serverConfig.dateFilter = date
      this.serverConfig.dateField.operator = date.operatorId
    },
  },
}
</script>
<style>
.portal-card {
  height: 120px;
  position: relative;
  border-radius: 3px;
  overflow: hidden;
}
img.cardbg {
  position: absolute;
  bottom: -20px;
  left: 55px;
}
.arraow-icon-180 .el-icon-back {
  -webkit-transform: rotate(180deg);
  -moz-transform: rotate(180deg);
  -o-transform: rotate(180deg);
  -ms-transform: rotate(180deg);
  transform: rotate(180deg);
}
.portal-card .content {
  align-items: center;
  margin: auto;
  height: 100%;
  padding: 20px;
}
.portal-card .card-label {
  font-size: 16px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.4px;
  color: #ffffff;
  white-space: nowrap;
}
.portal-card .card-value {
  font-size: 70px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #ffffff;
  text-align: right;
}
.cardArrow {
  padding-top: 10px;
  position: relative;
  top: 10px;
}
.sp-default-requestList {
  background: #fff;
  border-radius: 3px;
  box-shadow: 0px 3px 14px 0 rgba(15, 14, 34, 0.04);
  background-color: #ffffff;
  border: 1px solid #eeeef2;
  height: 434px;
  overflow: hidden;
}

.requestlist-header.row {
  padding: 20px;
  border-bottom: 1px solid #eaecf1;
  align-items: center;
  padding-top: 10px;
  padding-bottom: 10px;
  height: 55px;
}

.requestlist-header .label {
  font-size: 1.1vw;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: #2f2e49;
  padding-bottom: 0;
}

.opentobtn {
  width: 85px;
  justify-content: right;
  margin-left: auto;
  /* text-align: center; */
  padding-top: 8px;
  padding-bottom: 8px;
  border-radius: 3px;
  background-color: #39b3c2;
  font-size: 9px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.4px;
  text-align: center;
  color: #ffffff;
  text-transform: uppercase;
}

.opento {
  justify-content: right;
}

.requestlist-data .requestlist {
  padding: 19px;
  align-items: center;
  border-bottom: 1px solid #f4f5f7;
  padding-left: 21px;
}
.requestlist-data .id {
  font-size: 12px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  color: #31a9b8;
}

.requestlist-data .subject {
  font-size: 14px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.4px;
  color: #324056;
  padding-top: 4px;
}
.requestlist-data .status {
  font-size: 12px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.4px;
  color: #324056;
}
.requester {
  font-size: 12px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.4px;
  color: #324056;
}
.report-tab.el-tabs.el-tabs--top {
  display: none;
}
.sp-default-graph {
  height: 434px;
  background: #fff;
  overflow: hidden;
  border-radius: 3px;
  box-shadow: 0px 3px 14px 0 rgba(15, 14, 34, 0.04);
  background-color: #ffffff;
  border: 1px solid #eeeef2;
  padding: 0px;
}
.default-portal-dashboard .report-graph-header {
  display: none;
}
.sp-default-graph .f-chart-type-single {
  margin-right: 30px;
  position: absolute;
  right: 0;
  top: 10px;
}
.graph-container {
  position: relative;
}
.filter-field.date-filter-comp.inline.default-datefilter {
  position: absolute;
  right: 0;
  top: 10px;
  z-index: 1;
}
.default-portal-dashboard .chart-icon {
  display: none !important;
}
.graph-header {
  padding: 20px;
  border-bottom: 1px solid #eaecf1;
  align-items: center;
  font-size: 1.1vw;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: #2f2e49;
  padding-bottom: 0;
  height: 55px;
}
.requestlist-data .requestlist:nth-child(5) {
  border-bottom: 0;
}
.portal-legend-hide .fLegendContainer-right {
  display: none;
}
.sp-dashboard-view .button-row {
  color: #ef508f !important;
}
</style>
