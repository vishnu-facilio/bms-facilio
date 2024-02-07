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
            </div>
            <div class="requestlist-data" v-if="requestList.length">
              <div
                class="row requestlist pointer"
                v-for="(request, index) in requestList"
                :key="index"
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
      reportLoading: true,
      options: {
        padding: {
          top: 40,
          bottom: 10,
          left: 60,
          right: 40,
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
      cardLoading: {
        open: false,
        assign: false,
        unassigned: false,
        inprogress: false,
      },
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
  mounted() {
    this.loadDefaultData()
    let self = this
    self.loading = true
    self.cardloading = true
    // this.loadOpenCount()
    // this.loadUnassigned()
    // this.loadUrgencyCount()
    // this.loadInprogress()
    // this.loadRequestList()
    Promise.all([
      this.loadOpenCount(),
      this.loadUnassigned(),
      this.loadUrgencyCount(),
      this.loadInprogress(),
    ]).then(() => {
      self.cardloading = false
      self.loadRequestList()
    })
    this.loadReportMetaData()
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
  methods: {
    loadDefaultData() {
      let self = this
      this.$http
        .post('/v2/report/getReportFields', { moduleName: 'workorder' })
        .then(response => {
          if (
            response &&
            response.data.result &&
            response.data.result.meta &&
            response.data.result.meta.fields
          ) {
            self.fields = response.data.result.meta.fields
            self.prepareConfig(response.data.result.meta.fields)
          }
          self.reportLoading = false
        })
    },
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
    prepareConfig(fields) {
      if (fields) {
        let fieldObj = fields.find(rt => rt.columnName === 'STATUS_ID')
        if (fieldObj && fieldObj.id) {
          this.serverConfig.xField.field_id = fieldObj.id
          this.serverConfig.field_id = fieldObj.id
        }
        console.log('******* fierlds', this.serverConfig)
      }
    },
    setFilters2(criteria, state) {
      let filters = {}
      let value
      let operatorId = null
      if (state === 'open') {
        let id = []
        id = this.$account.data.ticketStatus.filter(function(rt) {
          if (rt.type === 'CLOSED') {
            return rt.id
          }
        })
        value = id
          .map(rt => rt.id)
          .toString()
          .split(',')
        operatorId = 37
      } else if (state === 'inprogress') {
        let id = []
        id = this.$account.data.ticketStatus.filter(function(rt) {
          if (rt.status === 'Work in Progress') {
            return rt.id
          }
        })
        value = id
          .map(rt => rt.id)
          .toString()
          .split(',')
        operatorId = 36
        let id1 = this.$account.data.ticketStatus.filter(function(rt) {
          if (rt.type === 'CLOSED') {
            return rt.id
          }
        })
        let value1 = id1
          .map(rt => rt.id)
          .toString()
          .split(',')
        filters = {
          actualWorkStart: { operatorId: 2, value: [''] },
          status: { operatorId: 37, value: value1 },
        }
        return filters
      } else if (state === 'urgent') {
        let id = []
        id = this.$account.data.ticketStatus.filter(function(rt) {
          if (rt.type === 'CLOSED') {
            return rt.id
          }
        })
        value = id
          .map(rt => rt.id)
          .toString()
          .split(',')
        operatorId = 9
        let id1 = this.$account.data.ticketStatus.filter(function(rt) {
          if (rt.type === 'CLOSED') {
            return rt.id
          }
        })
        let value1 = id1
          .map(rt => rt.id)
          .toString()
          .split(',')
        filters = {
          status: { operatorId: 37, value: value1 },
          urgency: { operatorId: 9, value: ['2'] },
        }
        return filters
      } else if (state === 'assignedTo') {
        let id = []
        id = this.$account.data.ticketStatus.filter(function(rt) {
          if (rt.type === 'CLOSED') {
            return rt.id
          }
        })
        value = id
          .map(rt => rt.id)
          .toString()
          .split(',')
        operatorId = 37
        filters = {
          status: { operatorId: 37, value: value },
          assignedTo: { operatorId: 1 },
        }
        return filters
      }
      filters[criteria] = { operatorId: operatorId, value: value }
      return filters
    },
    setFilters(criteria, state) {
      let filters = {}
      let value
      let operatorId = null
      if (state === 'open') {
        let id = []
        id = this.$account.data.ticketStatus.filter(function(rt) {
          if (rt.type === 'CLOSED') {
            return rt.id
          }
        })
        value = id
          .map(rt => rt.id)
          .toString()
          .split(',')
        operatorId = 10
      } else if (state === 'inprogress') {
        let id = []
        id = this.$account.data.ticketStatus.filter(function(rt) {
          if (rt.status === 'Work in Progress') {
            return rt.id
          }
        })
        value = id
          .map(rt => rt.id)
          .toString()
          .split(',')
        operatorId = 36
      } else if (state === 'urgent') {
        let id = []
        id = this.$account.data.ticketStatus.filter(function(rt) {
          if (rt.type === 'CLOSED') {
            return rt.id
          }
        })
        value = id
          .map(rt => rt.id)
          .toString()
          .split(',')
        operatorId = 9
      } else if (state === 'assignedTo') {
        let id = []
        id = this.$account.data.ticketStatus.filter(function(rt) {
          if (rt.type !== 'CLOSED') {
            return rt.id
          }
        })
        value = id
          .map(rt => rt.id)
          .toString()
          .split(',')
        operatorId = 6
      }
      filters[criteria] = { operatorId: operatorId, value: value }
      return filters
    },
    loadOpenCount() {
      let self = this
      let params = '?viewName=myrequests&count=true&includeParentFilter=true'
      let filters = this.setFilters('status', 'open')
      params += '&filters=' + encodeURIComponent(JSON.stringify(filters))
      this.$http
        .get('/v2/workorders/workOrderCount' + params)
        .then(function(response) {
          self.cardData[self.cards[0].key] =
            response.data && response.data.woCount ? response.data.woCount : 0
          self.cards[0].value =
            response.data && response.data.woCount ? response.data.woCount : 0
        })
    },
    loadInprogress() {
      let self = this
      let params = '?viewName=myrequests&count=true&includeParentFilter=true'
      let filters = this.setFilters2('status', 'inprogress')
      params += '&filters=' + encodeURIComponent(JSON.stringify(filters))
      this.$http
        .get('/v2/workorders/workOrderCount' + params)
        .then(function(response) {
          self.cardData[self.cards[2].key] = response.data.woCount || 0
          self.cards[2].value =
            response.data && response.data.woCount ? response.data.woCount : 0
        })
    },
    loadUrgencyCount() {
      let self = this
      let params = '?viewName=myrequests&count=true&includeParentFilter=true'
      // let filters = this.setFilters('urgent')
      let filters = this.setFilters2('status', 'urgent')
      params += '&filters=' + encodeURIComponent(JSON.stringify(filters))
      this.$http
        .get('/v2/workorders/workOrderCount' + params)
        .then(function(response) {
          self.cardData[self.cards[3].key] = response.data.woCount || 0
          self.cards[3].value =
            response.data && response.data.woCount ? response.data.woCount : 0
        })
    },
    loadUnassigned() {
      let self = this
      let params = '?viewName=myrequests&count=true&includeParentFilter=true'
      let filters = this.setFilters2('status', 'assignedTo')
      params += '&filters=' + encodeURIComponent(JSON.stringify(filters))
      // params +=
      //   "&filters=" +
      //   encodeURIComponent(JSON.stringify({ assignedTo: { operatorId: 1 } }));
      this.$http
        .get('/v2/workorders/workOrderCount' + params)
        .then(function(response) {
          self.cardData[self.cards[1].key] = response.data.woCount || 0
          self.cards[1].value =
            response.data && response.data.woCount ? response.data.woCount : 0
        })
    },
    // requestCountData () {
    //   for (let i = 0; i < this.cards.length; i++) {
    //     let list = this.cards[i]
    //     list.value = this.requestList.filter(function (rt) {
    //       if (list.Rkey1) {
    //         if (list.operator === 'EQUAL' ? rt[list.Rkey][list.Rkey1] === list.compareValue : rt[list.Rkey][list.Rkey1] !== list.compareValue) {
    //           return rt
    //         }
    //       }
    //       else {
    //         if (list.operator === 'EQUAL' ? rt[list.Rkey] === list.compareValue : rt[list.Rkey] !== list.compareValue) {
    //           return rt
    //         }
    //       }
    //     }).length
    //   }
    // },
    loadRequestList() {
      let self = this
      this.$http
        .get(
          '/v2/workorders?page=1&per_page=40&orderBy=createdTime&orderType=asc&includeParentFilter=true'
        )
        .then(function(response) {
          if (
            response.data &&
            response.data.result &&
            response.data.result.workorders
          ) {
            self.requestList = response.data.result.workorders
            self.loading = false
            // self.requestCountData()
          }
        })
    },
    loadReportMetaData() {
      let self = this
      self.reportloading = true
      this.$http
        .post('/v2/report/getReportFields', { moduleName: 'workorder' })
        .then(response => {
          this.prepareData(response.data.result)
          this.reportloading = false
        })
    },
    prepareData(data) {
      if (
        data &&
        data.meta &&
        data.meta.dimension &&
        data.meta.dimension.resource_fields
      ) {
        this.moduleData.resourceField = data.meta.dimension.resource_fields.find(
          rt => rt.columnName === 'RESOURCE_ID'
        )
        if (this.moduleData.resourceField.moduleId) {
          let moduleId = this.moduleData.resourceField.moduleI
          this.moduleData.moduleId = moduleId
          this.moduleData.meta.fieldMeta.xField = moduleId
          this.serverConfig.xField.module_id = moduleId
          this.serverConfig.dateField.module_id = moduleId
        }
      }
      if (
        data &&
        data.meta &&
        data.meta.dimension &&
        data.meta.dimension.time
      ) {
        this.serverConfig.xField.field_id = data.meta.dimension.time.find(
          rt => rt.columnName === 'CREATED_TIME'
        )
          ? data.meta.dimension.time.find(
              rt => rt.columnName === 'CREATED_TIME'
            ).fieldId
          : null
        this.serverConfig.dateField.field_id = data.meta.dimension.time.find(
          rt => rt.columnName === 'CREATED_TIME'
        )
          ? data.meta.dimension.time.find(
              rt => rt.columnName === 'CREATED_TIME'
            ).fieldId
          : null
      }
    },
    setDateFilter(date) {
      this.serverConfig.dateFilter = date
      this.serverConfig.dateField.operator = date.operatorId
    },
  },
}
</script>
