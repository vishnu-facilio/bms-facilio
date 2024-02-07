<template>
  <div class="anomalies-occurences-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane
        v-for="(tab, index) in tabs"
        :key="tab.name"
        :name="tab.name"
        :label="tab.displayName"
        lazy
      >
        <div class="align-stuff text-right">
          <div v-if="activeTab === 'occurrences'">
            <pagination
              :currentPage.sync="occurrenceCurrentPage"
              :total="occurrenceCount"
              :perPage="perPage"
              @update:currentPage="handleNextPageOccurrence"
              class="pR10 pT10 pB10 fc-black-small-txt-12"
            ></pagination>
            <alarm-widget-list
              ref="occurrenceWidget"
              :key="`${tab.name}${index}`"
              :isActive="tab.isActive"
              :details="details"
              :loading="occurenceloading"
              :name="activeTab"
              :dateObj="dateObj"
              :portalName="activeTab + '-topbar'"
              :list="occurrenceList"
              :fields="occurrenceFields"
              @filter="filterOccurrence"
            ></alarm-widget-list>
          </div>
        </div>
        <div class="align-stuff text-right">
          <div v-if="activeTab === 'events'">
            <pagination
              :currentPage.sync="eventCurrentPage"
              :total="eventCount"
              :perPage="perPage"
              @update:currentPage="handleNextPageEvent"
              class="pR10 pT10 pB10 fc-black-small-txt-12"
            ></pagination>
            <alarm-widget-list
              ref="eventWidget"
              :key="`${tab.name}${index}`"
              :isActive="tab.isActive"
              :details="details"
              :name="activeTab"
              :dateObj="dateObj"
              :loading="eventloading"
              @filter="filterEvent"
              :portalName="activeTab + '-topbar'"
              :list="eventList"
              :fields="eventFields"
            ></alarm-widget-list>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    <div class="widget-topbar-actions">
      <portal-target :name="activeTab + '-topbar'"></portal-target>
    </div>
  </div>
</template>
<script>
import AlarmWidgetList from '@/AlarmWidgetList'
import NewDateHelper from '@/mixins/NewDateHelper'
import { API } from '@facilio/api'
import Pagination from 'pageWidgets/utils/WidgetPagination'

export default {
  mixins: [NewDateHelper],
  components: {
    AlarmWidgetList,
    Pagination,
  },
  data() {
    return {
      occurrenceList: null,
      eventList: null,
      currentParamsForFilter: null,
      recordCount: null,
      occurrenceCount: null,
      occurrenceCurrentPage: 1,
      eventCurrentPage: 1,
      eventCount: null,
      localDateFormat: [22, 25, 31, 30, 28, 27, 44, 45],
      defaultOccurrenceFields: [
        { displayName: 'ID', name: 'id', displayType: 'ID', width: 90 },
        {
          displayName: 'OCCURRED TIME',
          name: 'createdTime',
          displayType: 'DATE_TIME',
        },
        {
          displayName: 'CLEARED TIME',
          name: 'clearedTime',
          displayType: 'DATE_TIME',
        },
        {
          displayName: 'DURATION',
          name: 'clearedTime',
          displayType: 'DURATION',
        },
      ],
      eventFields: [
        { displayName: 'ID', name: 'id', displayType: 'ID', width: 90 },
        {
          displayName: 'EVENT MESSAGE',
          name: 'eventMessage',
          displayType: 'STRING',
        },
        {
          displayName: 'EVENT TIME',
          name: 'createdTime',
          displayType: 'DATE_TIME',
        },
        {
          displayName: 'SEVERITY',
          name: 'previousSeverity',
          displayType: 'PREVIOUS_SEVERITY',
        },
      ],
      activeTab: 'occurrences',
      occurrence: null,
      loading: false,
      dateObj: NewDateHelper.getDatePickerObject(
        20,
        this.details.alarm
          ? [
              new Date(
                new Date(
                  this.details.alarm.lastOccurredTime
                ).toLocaleDateString('en-US') + ' 00:00:00'
              ).getTime() -
                3 * 24 * 60 * 60 * 1000,
              new Date(
                new Date(
                  this.details.alarm.lastOccurredTime
                ).toLocaleDateString('en-US') + ' 23:59:59:999'
              ).getTime(),
            ]
          : null
      ),
      eventloading: false,
      occurenceloading: false,
      perPage: 50,
    }
  },
  computed: {
    tabs() {
      let activeTab = this.activeTab
      return [
        {
          type: 'occurrences',
          name: 'occurrences',
          displayName: 'Occurrences',
          isActive: activeTab === 'occurrences',
        },
        {
          type: 'events',
          name: 'events',
          displayName: 'Events',
          isActive: activeTab === 'events',
        },
      ]
    },
    isNewReadingAlarm() {
      let { details } = this
      let { alarm } = details || {}
      let { type } = alarm || {}
      return type === 1 ? true : false
    },
    occurrenceFields() {
      let { defaultOccurrenceFields, isNewReadingAlarm, constructField } = this
      let constructedFields = []
      if (isNewReadingAlarm) {
        constructedFields = [
          constructField('ENERGY IMPACT (kWh)', 'energyImpact', 'IMPACT'),
          constructField('COST IMPACT', 'costImpact', 'IMPACT'),
          constructField('SEVERITY', 'previousSeverity', 'PREVIOUS_SEVERITY'),
        ]
      } else {
        constructedFields = [
          constructField('SEVERITY', 'previousSeverity', 'PREVIOUS_SEVERITY'),
          constructField('ACKNOWLEDGED BY', 'acknowledgedBy', 'USER_AVATAR'),
        ]
      }
      return [...defaultOccurrenceFields, ...constructedFields]
    },
  },
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'sectionKey',
    'widget',
    'resizeWidget',
  ],

  methods: {
    async getOccurrence(payload, force = false) {
      try {
        let { isNewReadingAlarm } = this
        let moduleName = isNewReadingAlarm
          ? 'readingalarmoccurrence'
          : 'alarmoccurrence'
        let { filters, orderBy, orderType } = payload || {}
        let { perPage } = this
        let withCount = true
        let page = this.occurrenceCurrentPage
        let params = {
          moduleName,
          filters,
          page,
          force,
          orderBy,
          orderType,
          withCount,
          perPage,
        }
        this.currentParamsForFilter = params
        let { data, meta } = await API.get('v3/modules/data/list', params)
        let { pagination } = meta || {}
        let { totalCount } = pagination || {}
        if (isNewReadingAlarm) {
          let { readingalarmoccurrence } = data || {}
          this.occurrenceList = readingalarmoccurrence
        } else {
          let { alarmoccurrence } = data || {}
          this.occurrenceList = alarmoccurrence
        }
        this.occurrenceCount = totalCount
        this.recordCount = this.occurrenceCount
        this.occurenceloading = false
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
    },
    async getEvents(payload, force = false) {
      try {
        let moduleName = 'baseevent'
        let { perPage } = this
        let { filters, orderBy, orderType } = payload || {}
        let withCount = true
        let page = this.eventCurrentPage
        let params = {
          moduleName,
          filters,
          page,
          force,
          orderBy,
          orderType,
          withCount,
          perPage,
        }
        this.currentParamsForFilter = params
        let { data, meta } = await API.get('v3/modules/data/list', params)
        let { baseevent } = data || {}
        let { pagination } = meta || {}
        let { totalCount } = pagination || {}
        this.eventList = baseevent
        this.eventCount = totalCount
        this.eventloading = false
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
    },
    loadOccurrence(filter) {
      this.occurenceloading = true
      let params = {}
      params.page = this.page
      params.perPage = this.perPage
      params.filters = filter.filters
      params.orderBy = 'CREATED_TIME'
      params.orderType = 'desc'
      this.getOccurrence(params)
    },
    loadEvent(filter) {
      this.eventloading = true
      let params = {}
      params.page = this.page
      params.perPage = this.perPage
      params.filters = filter.filters
      params.orderBy = 'CREATED_TIME'
      params.orderType = 'desc'
      this.getEvents(params)
    },
    async handleNextPageOccurrence(page) {
      this.occurrenceCurrentPage = page
      await this.getOccurrence(this.currentParamsForFilter)
    },
    async handleNextPageEvent(page) {
      this.eventCurrentPage = page
      await this.getEvents(this.currentParamsForFilter)
    },
    filterOccurrence(filter) {
      this.dateObj = filter
      let newfilter = {}
      newfilter['alarm'] = {
        operatorId: 36,
        value: [JSON.stringify(this.details.alarm.id)],
      }
      newfilter['createdTime'] = { operatorId: filter.operatorId }
      if (NewDateHelper.isValueRequired(filter.operatorId)) {
        if (filter.value) {
          newfilter.createdTime['value'] = []
          newfilter.createdTime['value'][0] = JSON.stringify(filter.value[0])
          newfilter.createdTime['value'][1] = JSON.stringify(filter.value[1])
        }
      }
      this.loadOccurrence({ filters: JSON.stringify(newfilter) })
    },
    filterEvent(filter) {
      this.dateObj = filter
      let newfilter = {}
      newfilter['baseAlarm'] = {
        operatorId: 36,
        value: [JSON.stringify(this.details.alarm.id)],
      }
      newfilter['createdTime'] = { operatorId: filter.operatorId }
      if (NewDateHelper.isValueRequired(filter.operatorId)) {
        if (filter.value) {
          newfilter.createdTime['value'] = []
          newfilter.createdTime['value'][0] = JSON.stringify(filter.value[0])
          newfilter.createdTime['value'][1] = JSON.stringify(filter.value[1])
        }
      }
      this.loadEvent({ filters: JSON.stringify(newfilter) })
    },
    changeDateFilter(dateFilter) {
      this.dateObj = dateFilter
      if (this.localDateFormat.includes(dateFilter.operatorId)) {
        this.dateOperator = dateFilter.operatorId
        this.dateValue = null
      } else {
        this.dateOperator = 20
        this.dateValue = dateFilter.value.join()
      }
    },
    constructField(displayName, name, displayType) {
      return {
        displayName,
        name,
        displayType,
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.anomalies-occurences-page {
  .vue-portal-target {
    margin-top: 25px;
  }
  .el-table th.is-leaf {
    padding-top: 10px;
    padding-bottom: 10px;
    padding-left: 20px;
  }
}
.align-stuff {
  display: flex;
  flex-direction: column;
}
</style>
