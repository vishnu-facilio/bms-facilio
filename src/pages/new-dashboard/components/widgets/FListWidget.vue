<template>
  <spinner v-if="loading" style="mT15" :show="loading" size="80"></spinner>
  <div class="row f-list-view-widget" v-else>
    <div class="fc-report-pdf-heading list-print-header hide">
      {{ config.widget.header.title }}
    </div>
    <div class="col table-scroll fList-widget-page">
      <table :class="['fc-list-view-table', zoomClass]" ref="f-list-table">
        <thead v-if="!printMode || (records && records.length)">
          <tr>
            <th
              class="text-left uppercase fc-chart-table-header"
              v-for="(header, index) in viewColumns"
              :key="index"
            >
              {{ header.displayName }}
            </th>
          </tr>
        </thead>
        <tbody v-if="recordsLoading">
          <tr>
            <td colspan="100%" class="text-center">
              <spinner :show="recordsLoading" size="80"></spinner>
            </td>
          </tr>
        </tbody>
        <tbody v-else>
          <tr class="nowotd" v-if="!records || !records.length">
            <td colspan="100%">
              <div class="row container">
                <div class="justify-center nowo no-table-data">
                  <inline-svg
                    src="svgs/emptystate/workorder"
                    iconClass="icon text-center icon-xxxlg"
                  ></inline-svg>
                  <div class="nowo-label">
                    {{ $t('common._common.no_data_available') }}
                  </div>
                </div>
              </div>
            </td>
          </tr>
          <tr
            class="tablerow list-widget-row"
            v-for="(row, index) in records"
            tag="tr"
            :key="index"
            v-if="!$mobile"
          >
            <td
              class="fc-chart-table-body"
              v-if="row && row !== null"
              v-for="(col, key) in viewColumns"
              :key="key"
            >
              <div
                v-if="
                  [
                    'totalEnergyConsumptionDelta',
                    'totalEnergyConsumption',
                  ].includes(col.name) && row.readings
                "
                @click="$router.push({ path: getSummaryUrl(row) })"
              >
                {{ Math.round(row.readings[col.name]) }}
              </div>
              <div
                v-else-if="
                  col.field && isDecimalField(col.field) && row[col.name]
                "
              >
                {{ decimalValue(row[col.name]) }}
              </div>
              <div
                v-else-if="
                  col.field &&
                    isDecimalField(col.field) &&
                    row.data &&
                    row.data[col.name]
                "
              >
                {{ decimalValue(row.data[col.name]) }}
              </div>
              <div
                style="width: 210px"
                v-else-if="['subject'].includes(col.name)"
                v-tippy
                :title="getColumnDisplayValue(col, row) || '---'"
                @click="$router.push({ path: getSummaryUrl(row) })"
              >
                {{ getColumnDisplayValue(col, row) || '---' }}
              </div>
              <div
                v-else-if="row.readings && row.readings[col.name]"
                @click="$router.push({ path: getSummaryUrl(row) })"
              >
                {{ row.readings[col.name] }}
              </div>
              <div
                v-else-if="col.name === 'parentId'"
                @click="$router.push({ path: getSummaryUrl(row) })"
              >
                {{ getIDDisplayValue(row) }}
              </div>
              <div
                v-else-if="
                  col.field &&
                    col.field.columnName === 'SEVERITY_ID' &&
                    row.severity
                "
                style="max-width: 72px; overflow: hidden"
              >
                <div
                  class="q-item-label uppercase severityTag"
                  v-bind:style="{
                    'background-color': getAlarmSeverity(row.severity.id).color,
                    'max-width': '90px',
                  }"
                >
                  {{
                    row.severity.id
                      ? getAlarmSeverity(row.severity.id).displayName
                      : '---'
                  }}
                </div>
              </div>
              <div
                v-else-if="col.name === 'attachmentPreview'"
                class="text-align-center"
              >
                <f-list-attachment-preview
                  :module="attachmentsModuleMap[moduleName]"
                  :record="row"
                ></f-list-attachment-preview>
              </div>
              <div v-else-if="col.field && col.field.displayType === 'IMAGE'">
                <f-image-preview
                  :file="$helpers.getFileObjectFromRecord(row, col)"
                ></f-image-preview>
              </div>
              <div v-else-if="col.field && col.field.dataTypeEnum === 'FILE'">
                <a
                  v-if="row[col.field.name + 'FileName']"
                  :href="row[col.field.name + 'DownloadUrl']"
                >
                  {{ row[col.field.name + 'FileName'] }}
                </a>
                <span v-else>--</span>
              </div>
              <div
                v-else-if="
                  col.field && col.field.columnName === 'ACKNOWLEDGED_BY'
                "
                class=""
              >
                <q-btn
                  color="secondary"
                  class="uppercase fia-alert-btn"
                  small
                  outline
                  @click="
                    acknowledgeAlarm(
                      {
                        row: row,
                        occurrence: {
                          id:
                            row && row.lastOccurrenceId
                              ? row.lastOccurrenceId
                              : null,
                        },
                        acknowledged: true,
                        acknowledgedBy: $account.user,
                      },
                      index
                    )
                  "
                  v-if="!row.acknowledged && isActiveAlarm(row)"
                  >{{ $t('alarm.alarm.acknowledge') }}</q-btn
                >
                <span
                  class="q-item-label"
                  v-else-if="!isActiveAlarm(row)"
                ></span>
                <div class="ackhover-row" v-else>
                  <div class="hover-row">
                    {{ getColumnDisplayValue(col, row) || '' }}
                  </div>
                </div>
              </div>
              <div v-else @click="$router.push({ path: getSummaryUrl(row) })">
                {{ getColumnDisplayValue(col, row) || '---' }}
              </div>
            </td>
            <td
              class="fc-chart-table-body"
              v-for="(col, key) in viewColumns"
              :key="key + 'i'"
              v-if="
                col.field &&
                  col.field.columnName === 'ACKNOWLEDGED_BY' &&
                  row &&
                  row !== null &&
                  $helpers.isLicenseEnabled('NEW_ALARMS')
              "
            >
              <div class="text-align-center">
                <div style="float: left" @click.stop="openComment(row)">
                  <span>
                    <img
                      src="~assets/comment.svg"
                      style="width: 14px; height: 12.8px"
                    />
                  </span>
                  <span
                    class="width5px q-item-sublabel comment-counnt-txt"
                    v-if="row.hasOwnProperty('noOfNotes')"
                    >{{
                      (row.noOfNotes && row.noOfNotes === -1) ||
                      row.noOfNotes === 0
                        ? '0'
                        : row.noOfNotes
                    }}</span
                  >
                </div>
              </div>
            </td>
          </tr>
          <tr
            class="tablerow list-widget-row"
            v-for="(row, index) in records"
            :key="index"
            v-if="$mobile"
          >
            <td
              class="fc-chart-table-body"
              v-if="row && row !== null"
              v-for="(col, key) in viewColumns"
              :key="key"
            >
              <div
                v-if="
                  [
                    'totalEnergyConsumptionDelta',
                    'totalEnergyConsumption',
                  ].includes(col.name) && row.readings
                "
              >
                {{ Math.round(row.readings[col.name]) }}
              </div>
              <div
                v-else-if="['subject'].includes(col.name)"
                v-tippy
                :title="getColumnDisplayValue(col, row) || '---'"
              >
                {{ getColumnDisplayValue(col, row) || '---' }}
              </div>
              <div v-else-if="row.readings && row.readings[col.name]">
                {{ row.readings[col.name] }}
              </div>
              <div v-else-if="col.name === 'parentId'">
                {{ row.parent ? row.parent.name : '---' }}
              </div>
              <div v-else>
                {{ getIDDisplayValue(row) }}
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <outside-click
      :visibility.sync="showcomment"
      class="comment-dialog"
      style="top: 0px; position: fixed; height: 100%"
      v-if="selectedRecord && $helpers.isLicenseEnabled('NEW_ALARMS')"
    >
      <div>
        <div class="comment-dialog-header">
          <h3 class="comment-dialog-heading" style="text-transform: uppercase">
            {{ $t('maintenance.wr_list.comments') }}
            <span>{{
              selectedRecord.noOfNotes && selectedRecord.noOfNotes > 0
                ? '(' + selectedRecord.noOfNotes + ')'
                : ''
            }}</span>
          </h3>
          <div class="comment-close">
            <el-tooltip
              class="item"
              effect="dark"
              content="Close"
              placement="bottom"
            >
              <i
                class="el-icon-close"
                aria-hidden="true"
                v-on:click="showcomment = false"
              ></i>
            </el-tooltip>
          </div>
        </div>
        <div class="comment-dialog-body widget-comment-dialog-body">
          <comments
            :module="
              selectedRecord.moduleName
                ? selectedRecord.moduleName
                : 'basealarmnotes'
            "
            parentModule="basealarm"
            :record="selectedRecord"
            :notify="false"
          ></comments>
        </div>
      </div>
    </outside-click>
  </div>
</template>

<script>
import { isNumber, isEmpty } from '@facilio/utils/validation'
import { isEqual } from 'lodash'
import { cloneDeep } from 'lodash'
import ViewMixinHelper from '@/mixins/ViewMixin'
import FListAttachmentPreview from '@/relatedlist/ListAttachmentPreview'
import Comments from '@/relatedlist/Comments2'
import { QBtn } from 'quasar'
import OutsideClick from '@/OutsideClick'
import FImagePreview from '@/relatedlist/ListImagePreview'
import { mapGetters } from 'vuex'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { API } from '@facilio/api'
import BaseWidgetMixin from 'src/pages/new-dashboard/components/widgets/BaseWidgetMixin.js'
import { isDecimalField } from '@facilio/utils/field'

export default {
  mixins: [ViewMixinHelper, BaseWidgetMixin],
  components: {
    FListAttachmentPreview,
    FImagePreview,
    QBtn,
    Comments,
    OutsideClick,
  },
  props: {
    item: {
      type: Object,
      required: true,
    },
    printMode: {
      type: Boolean,
      default: false,
    },
    componentVisibleInViewPort: {
      type: Boolean,
      default: true, // Default is true, since this component is used in multiple places not just in dashboard widgets.
    },
    updateWidget: {
      type: Function,
    },
  },
  data() {
    return {
      dbFilterJson: {},
      dbTimelineFilter: {},
      loading: true,
      showcomment: false,
      selectedRecord: null,
      viewDetail: null,
      v2apiUrl: '',
      isDecimalField,
      recordsLoading: true,
      records: null,
      modulesList: [],
      modules: [
        {
          label: 'Maintenance',
          moduleName: 'workorder',
          license: 'MAINTENANCE',
        },
        {
          label: 'Fault detection and diagnostics',
          moduleName: 'alarm',
          license: 'ALARMS',
        },
        {
          label: 'Building performance',
          moduleName: 'energydata',
          license: 'ENERGY',
        },
        {
          label: 'Asset',
          moduleName: 'asset',
          license: 'SPACE_ASSET',
        },
        {
          label: 'Inventory Request',
          moduleName: 'inventoryrequest',
          api: 'v2',
          list: [],
          expand: false,
          license: 'INVENTORY',
        },
        {
          label: 'Item',
          moduleName: 'item',
          api: 'v2',
          list: [],
          expand: false,
          license: 'INVENTORY',
        },
        {
          label: 'Contracts',
          moduleName: 'contracts',
          api: 'v2',
          list: [],
          expand: false,
          license: 'INVENTORY',
        },
        {
          label: 'Purchaseorder',
          moduleName: 'purchaseorder',
          api: 'v2',
          list: [],
          expand: false,
          license: 'PURCHASE',
        },
        {
          label: 'Purchaserequest',
          moduleName: 'purchaserequest',
          api: 'v2',
          list: [],
          expand: false,
          license: 'PURCHASE',
        },
        {
          label: 'Visitor',
          moduleName: 'visitor',
          api: 'v2',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Visits',
          moduleName: 'visitorlog',
          api: 'v2',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Visitor Invites',
          moduleName: 'invitevisitor',
          api: 'v2',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Watchlist',
          moduleName: 'watchlist',
          api: 'v2',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Contacts',
          moduleName: 'contact',
          api: 'v2',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Tenants',
          moduleName: 'tenant',
          api: 'v2',
          list: [],
          expand: false,
          license: 'TENANTS',
        },
        {
          label: 'Vendors',
          moduleName: 'vendors',
          api: 'v2',
          list: [],
          expand: false,
          license: 'VENDOR',
        },
      ],
      attachmentsModuleMap: {
        workorder: 'ticketattachments',
      },
      zoomClass: null,
    }
  },
  computed: {
    id() {
      const {
        item: { id },
      } = this
      return id
    },
    config() {
      const { widget } = this
      return {
        widget: widget,
      }
    },
    ...mapGetters(['getAlarmSeverity']),
    buildingId() {
      if (this.config && this.config.currentDashboard) {
        return this.config.currentDashboard.buildingId
      }
      if (this.$route.params.buildingid) {
        return this.$route.params.buildingid
      }
      return null
    },
    isCustomeModule() {
      if (this.modulesList.length) {
        let filteredModule = this.modulesList.find(
          rt => rt.name === this.moduleName
        )
        if (filteredModule && filteredModule.custom) {
          return true
        }
      }
      return false
    },
    widget() {
      return this.item.widget
    },
    widgetConfig() {
      const { id } = this ?? {}
      return {
        id: id,
        minW: 25,
        maxW: 96,
        minH: 10,
        maxH: 50,
        showHeader: true,
        showExpand: false,
        noResize: false,
        showDropDown: true,
        editMenu: [],
        borderAroundWidget: true,
        viewMenu: [],
      }
    },
    reportFilters() {
      if (this.$route.query.filters) {
        return this.$route.query.filters
      }
      return null
    },
    moduleName() {
      let moduleName = this.$getProperty(
        this.config,
        'widget.dataOptions.moduleName',
        null
      )
      return moduleName || 'workorder'
    },
    viewName() {
      if (this.viewDetail?.name) {
        return this.viewDetail.name
      }
      return 'all'
    },
  },
  created() {
    this.initWidget(this.widgetConfig)
  },
  mounted() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
    this.initData()
    this.getAllModules()
  },
  watch: {
    componentVisibleInViewPort(componentVisibleInViewPort) {
      if (componentVisibleInViewPort) {
        this.initData()
      }
    },
    'config.widget.dataOptions.viewName': function(newVal, oldVal) {
      this.initData()
    },
    buildingId: {
      handler(newData, oldData) {
        this.initData()
      },
    },
    reportFilters: {
      handler(newData, oldData) {
        this.initData()
      },
    },
    dbFilterJson(newValue, oldValue) {
      if (!isEqual(newValue, oldValue)) {
        this.initData()
      }
    },
    dbTimelineFilter(newValue, oldValue) {
      if (!isEqual(newValue, oldValue)) {
        this.initData()
      }
    },
  },
  methods: {
    decimalValue(value) {
      return isNumber(value) ? parseFloat(value.toFixed(2)) : '---'
    },
    getAllModules() {
      API.get(`v2/automation/module`).then(({ data }) => {
        this.modulesList = data.modules
        this.modulesList.forEach(rt => {
          this.$set(rt, 'id', rt.moduleId)
        })
        this.modulesList.push({
          displayName: 'Building performance',
          name: 'energydata',
          id: -1,
        })
        this.modulesList.push({
          displayName: 'Fault detection and diagnostics',
          name: 'alarm',
          id: -2,
        })
      })
    },
    openComment(row) {
      if (row) {
        this.selectedRecord = row
        this.showcomment = true
      }
    },
    getAckDetails(col, row) {
      if (col && row) {
        return row
      }
      return 'xv'
    },
    refresh() {
      this.initData()
    },
    isActiveAlarm(alarm) {
      if (this.getAlarmSeverity(alarm.severity.id).severity !== 'Clear') {
        return true
      }
      return false
    },
    acknowledgeAlarm(updateObj, index) {
      let self = this

      let data = {
        ids: [updateObj.occurrence.id],
        alarmOccurrence: {
          acknowledged: true,
        },
      }
      this.$http
        .post('/v2/alarm/updateOccurrence', data)
        .then(function(response) {
          if (response) {
            self.updateAck(index)
          }
        })
    },
    updateAck(index) {
      this.records[index].acknowledged = true
      this.records[index].acknowledgedTime = new Date()
      this.records[index].acknowledgedBy = this.$account.user
    },
    getIDDisplayValue(row) {
      if (row.parent && row.parent.name) {
        return row.parent.name
      } else if (row.id) {
        return '#' + row.id
      } else {
        return '---'
      }
    },
    initData() {
      //when dashboard in lazy mode and widget ain't visible dont load
      if (this.componentVisibleInViewPort == false) {
        return
      }
      let self = this
      self.loading = true
      let v2api = false
      if (
        self.config.widget.dataOptions.moduleName === 'alarm' &&
        this.$helpers.isLicenseEnabled('NEW_ALARMS')
      ) {
        self.config.widget.dataOptions.moduleName = 'newreadingalarm'
      }
      let url = ''
      if (
        this.modules.find(
          rt => rt.moduleName === self.config.widget.dataOptions.moduleName
        ) &&
        this.modules.find(
          rt => rt.moduleName === self.config.widget.dataOptions.moduleName
        ).api
      ) {
        v2api = true
        self.v2apiUrl =
          '/v2/' +
          self.config.widget.dataOptions.moduleName +
          '/view' +
          '/' +
          self.config.widget.dataOptions.viewName
      }
      url =
        '/view/' +
        self.config.widget.dataOptions.viewName +
        '?moduleName=' +
        self.config.widget.dataOptions.moduleName
      self.$http
        .get(url)
        .then(function(response) {
          self.viewDetail = response.data
          if (self.printMode) {
            let columns = self.viewDetail.fields.length
            self.zoomClass =
              columns > 10 ? 'zoom40' : columns > 7 ? 'zoom50' : 'zoom90'
          }

          self.loading = false
          self.initRecords(v2api, self.v2apiUrl)
        })
        .catch(() => {
          self.loading = false
        })
    },
    initRecords(v2api, urlx) {
      let self = this
      self.recordsLoading = true
      let url = ''
      if (
        (self.config.widget.dataOptions.moduleName === 'alarm' ||
          self.config.widget.dataOptions.moduleName === 'newreadingalarm') &&
        this.$helpers.isLicenseEnabled('NEW_ALARMS')
      ) {
        url += 'v2/'
      }
      if (
        self.config.widget.dataOptions.moduleName === 'newreadingalarm' &&
        this.$helpers.isLicenseEnabled('NEW_ALARMS')
      ) {
        url += 'readingalarms/'
      } else {
        url += self.config.widget.dataOptions.moduleName + '/'
      }
      if (
        (self.config.widget.dataOptions.moduleName === 'alarm' ||
          self.config.widget.dataOptions.moduleName === 'newreadingalarm') &&
        this.$helpers.isLicenseEnabled('NEW_ALARMS')
      ) {
        url += 'view/'
      }

      if (
        !this.modules.find(
          rt => rt.moduleName === self.config.widget.dataOptions.moduleName
        )
      ) {
        url = `v2/module/data/list?page=1&perPage=50&moduleName=${self.config.widget.dataOptions.moduleName}&viewName=${self.config.widget.dataOptions.viewName}`
      } else {
        url += self.config.widget.dataOptions.viewName //v1 api
        url += '?page=1&perPage=50&includeParentFilter=true'
        // TO DO STOP appending '?' '&'  . MOVE http to API.get ASAP
        //NEED TO PAGINATE LIST WIDGET
      }

      if (self.reportFilters) {
        //no longer used
        url += '&filters=' + encodeURIComponent(self.reportFilters)
      }
      const { dbFilterJson, dbTimelineFilter } = self
      if (!isEmpty(dbFilterJson) || !isEmpty(dbTimelineFilter)) {
        //for list widget ,combine dbuser and dbtimeline filters
        let timelineFilterObj = null
        if (!isEmpty(dbTimelineFilter)) {
          const { startTime, endTime, dateField } = dbTimelineFilter
          timelineFilterObj = {
            [dateField]: {
              operatorId: 20,
              value: [String(startTime), String(endTime)],
            },
          }
        }
        let filterObj = {
          ...dbFilterJson,
          ...timelineFilterObj,
        }
        // '?' already appended by now
        url += '&filters=' + encodeURIComponent(JSON.stringify(filterObj))
      }
      if (self.buildingId) {
        let filters = {
          resource: {
            operatorId: 38,
            value: [self.buildingId + ''],
          },
        }
        url += '&filters=' + encodeURIComponent(JSON.stringify(filters))
      }
      if (urlx) {
        url = urlx
      }
      self.$http
        .get(url)
        .then(function(response) {
          if (
            !self.modules.find(
              rt => rt.moduleName === self.config.widget.dataOptions.moduleName
            )
          ) {
            self.records = response.data.result.moduleDatas
            self.recordsLoading = false
          } else if (
            (self.config.widget.dataOptions.moduleName === 'alarm' ||
              self.config.widget.dataOptions.moduleName ===
                'newreadingalarm') &&
            self.$helpers.isLicenseEnabled('NEW_ALARMS')
          ) {
            self.records =
              response.data.result[Object.keys(response.data.result)[0]]
            self.recordsLoading = false
          } else {
            if (v2api) {
              self.records =
                response.data.result[Object.keys(response.data.result)[0]]
            } else {
              self.records = response.data[Object.keys(response.data)[0]]
            }
            self.recordsLoading = false
          }
          self.$nextTick(() => {
            let height = self.$refs['f-list-table'].scrollHeight + 90
            self.$emit('resizeWidget', height)
          })
          if (self.printMode) {
            self.$nextTick(() => {
              // Spread the entire wiget height and width wise when puppeteer tries to print the dashboard.
              const height =
                (self.$refs['f-list-table'].scrollHeight + 100) / 15
              const clonedWidget = cloneDeep(self.item)
              clonedWidget.h = parseInt(Math.ceil(height))
              self.updateWidget(clonedWidget)
            })
          }
        })
        .catch(function() {
          self.recordsLoading = false
        })
    },
    contractTypeEnumVsRoute(contractEnum) {
      if (contractEnum > 0) {
        switch (contractEnum) {
          case 1:
            return 'purchasecontracts'
          case 2:
            return 'labourcontracts'
          case 3:
            return 'warrantycontracts'
          case 4:
            return 'rentalleasecontracts'
        }
      } else {
        return null
      }
    },
    getSummaryUrl(row) {
      let id = row.id

      if (isWebTabsEnabled()) {
        let { moduleName, viewName } = this.config.widget.dataOptions || {}
        let viewname = moduleName === 'newreadingalarm' ? viewName : 'all'
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          return this.$router.resolve({ name, params: { viewname, id } }).href
        }
      } else {
        if (this.isCustomeModule) {
          return `/app/ca/modules/${this.moduleName}/${this.viewName}/${id}/summary`
        }
        if (this.config.widget.dataOptions.moduleName === 'workorder') {
          return '/app/wo/orders/summary/' + id
        } else if (this.config.widget.dataOptions.moduleName === 'alarm') {
          return '/app/fa/faults/summary/' + id
        } else if (
          this.config.widget.dataOptions.moduleName === 'newreadingalarm'
        ) {
          return (
            '/app/fa/faults/' +
            this.config.widget.dataOptions.viewName +
            '/newsummary/' +
            id
          )
        } else if (this.config.widget.dataOptions.moduleName === 'asset') {
          return '/app/at/assets/all/' + id + '/overview'
        } else if (this.config.widget.dataOptions.moduleName === 'item') {
          return `/app/inventory/item/all/${id}/summary`
        } else if (
          this.config.widget.dataOptions.moduleName === 'inventoryrequest'
        ) {
          return '/app/wo/approvals/ir/all/summary/' + id
        } else if (this.config.widget.dataOptions.moduleName === 'contracts') {
          return (
            '/app/ct/' +
            this.contractTypeEnumVsRoute(row.contractType) +
            '/all/summary/' +
            id
          )
        } else if (
          this.config.widget.dataOptions.moduleName === 'purchaseorder'
        ) {
          return '/app/purchase/po/all/summary/' + id
        } else if (
          this.config.widget.dataOptions.moduleName === 'purchaserequest'
        ) {
          return '/app/purchase/pr/all/summary/' + id
        } else if (this.config.widget.dataOptions.moduleName === 'visitor') {
          return '/app/purchase/pr/all/summary/' + id
        } else if (this.config.widget.dataOptions.moduleName === 'visitorlog') {
          return '/app/vi/visits/all/' + (row.visitor || {}).id + '/overview'
        } else if (this.config.widget.dataOptions.moduleName === 'tenant') {
          return '/app/tm/tenants/all/' + id + '/overview'
        } else if (this.config.widget.dataOptions.moduleName === 'vendors') {
          return '/app/vendor/vendors/all/summary/' + id
        } else if (
          this.config.widget.dataOptions.moduleName === 'invitevisitor'
        ) {
          return '/app/vi/invites/all/summary/' + id
        } else if (this.config.widget.dataOptions.moduleName === 'watchlist') {
          return '/app/vi/watchlist/all/summary/' + id
        } else if (this.config.widget.dataOptions.moduleName === 'contact') {
          return '/app/home/contact/all/summary/' + id
        } else if (this.config.widget.dataOptions.moduleName === 'insurance') {
          return '/app/vendor/insurance/all/summary/' + id
        }
      }
    },
  },
}
</script>

<style>
.list-widget-row td {
  padding-top: 15px;
  padding-bottom: 15px;
  font-size: 0.8125rem !important;
  border-top: 1px solid #f2f5f6 !important;
  color: #525f7f !important;
}
.no-table-data .no-chart-data {
  margin-left: 50%;
}

.no-data-label {
  font-size: 15px;
  margin-left: 10%;
  margin-top: 20%;
}

.f-list-view-widget {
  height: calc(100% - 45px);
  overflow: hidden;
}

.f-list-view-widget .table-scroll {
  height: 100%;
  padding-bottom: 30px;
}

.f-list-view-widget thead th {
  font-size: 0.65rem !important;
  background: #f6f9fc !important;
  padding: 14px 15px;
  font-weight: 500;
  white-space: nowrap;
  letter-spacing: 1px;
  color: #718296 !important;
  font-weight: bold;
  padding-top: 15px;
  padding-bottom: 15px;
}

.f-list-view-widget .fc-list-view-table tbody tr:nth-child(even) {
  background: rgba(246, 249, 252, 0.65) !important;
}

.list-widget-row .hover-row {
  background: transparent !important;
}
.widget-comment-dialog-body {
  height: 100% !important;
}
.widget-comment-dialog-body .scroll-new {
  height: 100% !important;
}
.widget-comment-dialog-body .comment-area {
  position: absolute;
  bottom: 0 !important;
  padding-bottom: 20px;
}
@media print {
  .f-list-view-widget {
    page-break-after: always;
  }

  .f-list-view-widget .table-scroll {
    border: 1px solid #2d394c !important;
  }

  .f-list-view-widget table {
    border: none !important;
  }

  .f-list-view-widget .list-print-header {
    display: block;
    padding: 10px;
    font-size: 12px;
  }
  .hide {
    display: none !important;
  }
  .f-list-view-widget thead th {
    font-size: 0.65rem !important;
    background: #2c394c !important;
    padding: 0 15px;
    font-weight: 500;
    white-space: nowrap;
    letter-spacing: 1px;
    color: #ffffff !important;
    font-weight: bold;
    border: none !important;
  }

  .f-list-view-widget .fc-list-view-table tbody tr:nth-child(even) {
    background: #f0f9fb !important;
  }
  .f-list-view-widget tbody td {
    word-break: break-all;
    padding-left: 14px;
    page-break-inside: avoid;
    page-break-after: auto;
  }
  .f-list-view-widget tbody td:first-child {
    word-break: break-all;
    padding-right: 40px;
  }
}
</style>

<style lang="scss">
@media print {
  .f-list-view-widget {
    table,
    th,
    td {
      border-collapse: separate;
    }
  }
}
</style>
