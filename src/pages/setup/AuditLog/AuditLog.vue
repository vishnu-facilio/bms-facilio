<template>
  <div class="fc-table-expand-row fc-audit-log-page">
    <setup-header>
      <template #heading>
        {{ $t('setup.setup.audit_logs') }}
      </template>
      <template #description>
        {{ $t('setup.setup.audit_log_desc') }}
      </template>
      <template #filter>
        <div class="flex-middle">
          <div class="flex-middle">
            <div>
              <div class="fc-black-13 text-left bold pB5">
                {{ $t('setup.setupLabel.date_filter') }}
              </div>
              <el-select
                v-model="dateFilterModel"
                placeholder="Select time period"
                class="mR20 fc-input-full-border2"
                @change="searchLoadData(true)"
                clearable
                filterable
              >
                <el-option
                  v-for="(filter, f) in selectFilter"
                  :key="f"
                  :label="filter.label"
                  :value="filter.value"
                >
                </el-option>
              </el-select>
            </div>
            <div v-if="dateFilterModel === 'Custom'">
              <div class="fc-black-13 text-left bold pB5">
                {{ $t('setup.setupLabel.custom') }}
              </div>
              <el-date-picker
                v-model="datePicker"
                type="daterange"
                class="fc-input-full-border2 width250px"
                placeholder="Select date and time"
                range-separator="-"
                start-placeholder="Start date"
                end-placeholder="End date"
                @change="searchLoadData(true)"
                value-format="timestamp"
              >
              </el-date-picker>
            </div>
          </div>
        </div>
      </template>
      <template #searchAndPagination>
        <div class="pT20 flex-middle">
          <div
            @click="searchShowHide"
            v-if="searchHide"
            class="pointer mR15 fc-portal-filter-border"
            v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
            content="Search"
          >
            <i class="el-icon-search fc-black-14 text-right fwBold"></i>
          </div>
          <div class="relative mR15" v-if="searchShow">
            <input
              placeholder="Search subject"
              v-model="querySearch"
              ref="querySearch"
              class="fc-input-full-border2 fc-log-search width250px"
              @keyup.enter="searchLoadData(true)"
            />
            <i
              class="el-icon-close fc-search-close fc-black-14 text-right fwBold"
              @click="searchClose"
            ></i>
          </div>
          <pagination
            :total="auditCount"
            :current-page="auditPage"
            @pagechanged="setPage"
            class="mR15"
          ></pagination>
          <div
            class="pointer fwBold mR15 f16 fc-portal-filter-border "
            @click="searchLoadData(true)"
            v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
            content="Refresh"
          >
            <i class="el-icon-refresh fwBold f16"></i>
          </div>
          <div
            class="filter-border fc-portal-filter-border mL0"
            @click="showExtraFilter"
            :class="{ filterActive: showFilter }"
            v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
            content="Advanced filters"
          >
            <inline-svg
              class="pointer"
              src="svgs/dashboard/filter"
              iconClass="icon icon-sm-md fc-fill-path-grey"
            ></inline-svg>
            <div :class="{ 'dot-active-pink': userData || recordData }"></div>
            <!-- <i class="fc-dot"></i> -->
          </div>
        </div>
      </template>
    </setup-header>

    <!-- extra filter -->

    <template v-if="showFilter">
      <div class="flex-middle pB20 fc-extra-filter-block">
        <div class="mL20">
          <div class="fc-black-13 text-left bold pB5">
            {{ $t('setup.setupLabel.performed_by') }}
          </div>

          <el-select
            v-model="userData"
            reserve-keyword
            filterable
            clearable
            :remote="true"
            placeholder="Select performed by"
            :remote-method="remoteSearchUser"
            :loading="loading"
            @change="listLoading"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="(user, i) in userListData"
              :key="i.id"
              :label="user.name"
              :value="user.id.toString()"
            >
            </el-option>
          </el-select>

          <!-- <el-select
            v-model="userData"
            placeholder="Select performed by"
            class="fc-input-full-border2"
            @change="listLoading"
            filterable
            clearable
          >
            <el-option
              v-for="(user, i) in userListData"
              :key="i"
              :label="user.name"
              :value="user.id.toString()"
            >
            </el-option>
          </el-select> -->
        </div>

        <div class="mL20">
          <div class="fc-black-13 text-left bold pB5">
            {{ $t('setup.setupLabel.record_type') }}
          </div>
          <el-select
            v-model="recordData"
            placeholder="Select entity"
            class="fc-input-full-border2"
            @change="listLoading"
            filterable
            clearable
          >
            <el-option
              v-for="(record, inx) in recordTypeList"
              :key="inx"
              :label="record.name"
              :value="record.id.toString()"
            >
            </el-option>
          </el-select>
        </div>
        <!-- <div class="mL20">
            <div class="fc-black-13 text-left bold pB5">
              {{ $t('setup.setupLabel.type') }}
            </div>
            <el-select
              v-model="typeNameSearch"
              placeholder="Select"
              class="fc-input-full-border2"
              @change="listLoading"
              filterable
              clearable
            >
              <el-option
                v-for="(type, ieex) in auditLogList"
                :key="ieex.id"
                :label="type.typeName"
                :value="type.typeName.toString()"
              >
              </el-option>
            </el-select>
          </div> -->
        <div
          class="fc-blue-txt2-14 pL20 pointer pT20"
          @click="clearFiltersAll"
          v-if="userData || recordData"
        >
          {{ $t('common._common.clear_all_filters') }}
        </div>
      </div>
    </template>

    <!-- page list start-->
    <div class="p15 fc-setup-table-layout-scroll fc-page-scroll-top">
      <setup-loader v-if="loading">
        <template #setupLoading>
          <spinner :show="loading" size="80"></spinner>
        </template>
      </setup-loader>
      <setup-empty v-if="$validation.isEmpty(auditLogList) && !loading">
        <template #emptyImage>
          <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
        </template>
        <template #emptyHeading>
          {{ $t('common.products.no_audit_available') }}
        </template>
        <template #emptyDescription> </template>
      </setup-empty>

      <div class="d-flex flex-col-reverse" v-else>
        <div v-for="(time, iex) in sortValue" :key="iex">
          <div v-if="!$validation.isEmpty(resultMap[time])">
            <div>
              <div
                class="fc-setup-group-header header-lavender"
                v-if="!$validation.isEmpty(resultMap[time])"
              >
                <div class="fc-black-14 fwBold text-left flex-middle">
                  <div>
                    <!-- {{
                      $helpers.getFormattedValueForMillis(
                        time,
                        'dddd, DD MMM YYYY'
                      )
                    }} -->
                    {{ getFormatter(time) }}
                  </div>
                </div>
              </div>
              <div class="mB20 fc-audit-log-table">
                <div v-if="$validation.isEmpty(resultMap[time])"></div>
                <el-header class="fc-table-header-sticky" height="40">
                  <el-row>
                    <el-col :span="3" class="pL10">
                      {{ $t('setup.setupLabel.time') }}
                    </el-col>
                    <el-col :span="5" class="pL5">
                      {{ $t('setup.setupLabel.performed_by') }}
                    </el-col>
                    <el-col :span="8" class="pL10">
                      {{ $t('setup.setupLabel.subject') }}
                    </el-col>
                    <el-col :span="4" style="width: 18%;" class="pL25">
                      {{ $t('setup.setupLabel.app_id') }}
                    </el-col>
                    <el-col :span="3">
                      {{ $t('setup.setupLabel.record_type') }}
                    </el-col>
                  </el-row>
                </el-header>
                <el-table
                  v-if="!$validation.isEmpty(resultMap[time])"
                  :data="resultMap[time]"
                  :cell-style="{ padding: '12px 30px' }"
                  style="width: 100%"
                  height="auto"
                  class="fc-setup-table fc-setup-table-header-hide"
                  :fit="true"
                  ref="auditLogTable"
                  @row-click="(...args) => tableRowExpand(iex, ...args)"
                  :row-key="row => row.id"
                  :expand-row-keys="expandRowKeys"
                  @expand-change="handleExpandChange"
                >
                  <el-table-column label="" prop="time" width="120">
                    <template v-slot="audit">
                      <!-- {{ $helpers.formatTimeFull(audit.row.time) }} -->
                      {{ getFormatTime(audit.row.time) }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="performed" width="200">
                    <template v-slot="audit">
                      <div class="bold leading-none">
                        {{
                          $getProperty(audit.row, 'performedBy.name')
                            ? $getProperty(audit.row, 'performedBy.name')
                            : '---'
                        }}
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column prop="desc" width="330">
                    <template v-slot="audit">
                      <!-- <div v-html="audit.row.replaceText"></div> -->
                      <FHtml
                        :content="
                          replace(audit.row.subject, audit.row.linkConfig)
                        "
                      />
                    </template>
                  </el-table-column>
                  <el-table-column prop="appId" width="150">
                    <template v-slot="audit">
                      <div class="flex-middle">
                        <div class="nowrap">
                          {{ appHash[audit.row.appId] }}
                        </div>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column prop="recordType" width="140">
                    <template v-slot="audit">
                      <div class="flex-middle">
                        <div>
                          <div
                            v-if="$getProperty(audit, 'row.recordType') === 1"
                            class="nowrap width50px"
                          >
                            {{ $t('setup.auditLog.module') }}
                          </div>
                          <div
                            v-else-if="
                              $getProperty(audit, 'row.recordType') === 2
                            "
                            class="nowrap width50px"
                          >
                            {{ $t('setup.auditLog.setup') }}
                          </div>
                          <div v-else>---</div>
                        </div>
                        <div>
                          <div class="pL10">
                            <div
                              v-if="$getProperty(audit, 'row.sourceType') === 1"
                            >
                              <div
                                class="flex-middle"
                                v-tippy="{
                                  arrow: true,
                                  arrowType: 'round',
                                  animation: 'fade',
                                }"
                                content="Web"
                              >
                                <inline-svg
                                  src="svgs/web-icon"
                                  iconClass="icon text-center icon-lg vertical-baseline mL5 fc-black-path"
                                ></inline-svg>
                              </div>
                            </div>
                            <div
                              v-else-if="
                                $getProperty(audit, 'row.sourceType') === 2
                              "
                            >
                              <div
                                class="flex-middle"
                                v-tippy="{
                                  arrow: true,
                                  arrowType: 'round',
                                  animation: 'fade',
                                }"
                                content="Android Mobile"
                              >
                                <inline-svg
                                  src="svgs/mobile-blue"
                                  iconClass="icon text-center icon-lg vertical-baseline fill-black1 mL5 fc-black-path"
                                ></inline-svg>
                              </div>
                            </div>
                            <div
                              v-else-if="
                                $getProperty(audit, 'row.sourceType') === 3
                              "
                            >
                              <div
                                class="flex-middle"
                                v-tippy="{
                                  arrow: true,
                                  arrowType: 'round',
                                  animation: 'fade',
                                }"
                                content="Ios Mobile"
                              >
                                <inline-svg
                                  src="svgs/mobile-blue"
                                  iconClass="icon text-center icon-lg vertical-baseline fill-black1 mL5 fc-black-path"
                                ></inline-svg>
                              </div>
                            </div>
                            <div
                              v-else-if="
                                $getProperty(audit, 'row.sourceType') === 4
                              "
                            >
                              <div
                                class="flex-middle"
                                v-tippy="{
                                  arrow: true,
                                  arrowType: 'round',
                                  animation: 'fade',
                                }"
                                content="API"
                              >
                                <inline-svg
                                  src="svgs/api"
                                  iconClass="icon text-center icon-lg vertical-baseline fill-black1 mL5 fill-api-red"
                                ></inline-svg>
                              </div>
                            </div>
                            <div v-else></div>
                          </div>
                        </div>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column type="expand" class="fc-expand-row">
                    <template v-slot="audit">
                      <el-row>
                        <el-col :span="3" class="bold">
                          {{ $t('setup.approvalprocess.description') }}
                        </el-col>
                        <el-col :span="21">
                          {{
                            audit.row.description
                              ? audit.row.description
                              : '---'
                          }}
                        </el-col>
                      </el-row>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- page list end -->
    <SetupScrollToTop></SetupScrollToTop>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import moment from 'moment-timezone'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import SetupHeader from 'pages/setup/components/SetupHeaderTabs'
import Pagination from 'pages/setup/AuditLog/AuditLogPagination'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import { loadApps } from 'util/appUtil'
import SetupScrollToTop from 'pages/setup/components/SetupScrollToTop'
import debounce from 'lodash/debounce'
import { FHtml } from '@facilio/ui/app'
// import {
//   isWebTabsEnabled,
//   findRouteForModule,
//   pageTypes,
// } from '@facilio/router'
export default {
  mixins: [FetchViewsMixin],
  data() {
    return {
      loading: true,
      tableData: [],
      auditLogList: [],
      currentTimeLabel: [],
      logList: {},
      systemApps: [],
      querySearch: '',
      datePicker: [],
      dayMap: {},
      resultMap: {},
      description: null,
      userlist: [],
      userData: null,
      recordData: null,
      typeNameSearch: null,
      searchHide: true,
      searchShow: false,
      sortValue: {},
      showFilter: false,
      dateFilterModel: [],
      auditCount: 0,
      userListSearch: [],
      list: [],
      selectFilter: [
        {
          label: 'Today',
          value: 'Today',
        },
        {
          label: 'Till Now',
          value: 'Till Now',
        },
        {
          label: 'Yesterday',
          value: 'Yesterday',
        },
        {
          label: 'Till Yesterday',
          value: 'Till Yesterday',
        },
        {
          label: 'This Week',
          value: 'This Week',
        },
        {
          label: 'Last Week',
          value: 'Last Week',
        },
        {
          label: 'Last 2 Week',
          value: 'Last 2 Week',
        },
        {
          label: 'This Month',
          value: 'This Month',
        },
        {
          label: 'Last Month',
          value: 'Last Month',
        },
        {
          label: 'Custom',
          value: 'Custom',
        },
      ],
      recordTypeList: [
        {
          id: 1,
          name: 'Module',
        },
        {
          id: 2,
          name: 'Setup',
        },
      ],
      perPage: 50,
      auditPage: 1,
      expandRowKeys: [],
      userListData: [],
      userSearchData: [],
      appHash: {},
    }
  },
  components: {
    SetupHeader,
    SetupLoader,
    SetupEmpty,
    Pagination,
    SetupScrollToTop,
    FHtml,
  },
  title() {
    return 'Audit logs'
  },
  created() {
    this.fetchAuditLog()
    this.loadSystemApps()
    this.loadusers()
  },
  computed: {},
  watch: {
    page(newVal, oldVal) {
      if (oldVal != newVal) {
        this.fetchAuditLog()
      }
    },
  },
  methods: {
    async fetchAuditLog(force = true) {
      this.loading = true
      let filters = {}
      if (!isEmpty(this.querySearch)) {
        filters.subject = {
          operatorId: 5,
          value: [this.querySearch],
        }
      }
      if (!isEmpty(this.datePicker)) {
        filters.time = {
          operatorId: 20,
          value: this.datePicker.map(a => a.toString()),
        }
      }
      if (!isEmpty(this.userData)) {
        filters.performedBy = {
          operatorId: 36,
          value: [this.userData],
        }
      }
      if (!isEmpty(this.recordData)) {
        filters.recordType = {
          operatorId: 54,
          value: [this.recordData],
        }
      }
      if (!isEmpty(this.typeNameSearch)) {
        filters.typeName = {
          operatorId: 5,
          value: [this.typeNameSearch],
        }
      }
      if (this.dateFilterModel === 'This Month') {
        filters.time = {
          operatorId: 28,
        }
      }
      if (this.dateFilterModel === 'Today') {
        filters.time = {
          operatorId: 22,
        }
      }
      if (this.dateFilterModel === 'Yesterday') {
        filters.time = {
          operatorId: 25,
        }
      }
      if (this.dateFilterModel === 'Last Month') {
        filters.time = {
          operatorId: 27,
        }
      }
      if (this.dateFilterModel === 'Last Month') {
        filters.time = {
          operatorId: 27,
        }
      }
      if (this.dateFilterModel === 'Last Week') {
        filters.time = {
          operatorId: 30,
        }
      }
      if (this.dateFilterModel === 'Last 2 Week') {
        filters.time = {
          operatorId: 50,
          value: ['2'],
        }
      }
      if (this.dateFilterModel === 'This Week') {
        filters.time = {
          operatorId: 31,
        }
      }
      if (this.dateFilterModel === 'Till Now') {
        filters.time = {
          operatorId: 72,
        }
      }
      if (this.dateFilterModel === 'Till Yesterday') {
        filters.time = {
          operatorId: 26,
        }
      }

      let { error, data } = await API.get(
        `v2/auditLogs/list?&page=${this.auditPage}&perPage=${this.perPage}&includePrentFilter=true&orderType="asc"&orderBy="datetime"`,
        { filters: !isEmpty(filters) ? JSON.stringify(filters) : null },
        { force }
      )
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.auditLogList = data.auditLogs || []
        // this.auditLogList = await Promise.all(
        //   this.auditLogList.map(async audit => {
        //     let { subject, linkConfig } = audit || {}
        //     let replaceText = await this.replace(subject, linkConfig)
        //     return { ...audit, replaceText }
        //   })
        // )
        this.auditCount = this.auditLogList.length
        this.currentTimeLabel = []
        this.logList = {}
        let ids = data.auditLogs.map(rt => rt.time)
        ids = [...new Set(ids)]

        ids.forEach(time => {
          this.currentTimeLabel.push(
            data.auditLogs.find(rl => rl.time === time).time
          )
          this.logList[time] = data.auditLogs.filter(rl => rl.time === time)
        })

        this.auditLogList.forEach(rt => {
          this.$set(
            rt,
            'day',
            moment(rt.time)
              .tz(this.$timezone)
              .format('DDMMYYYY')
          )
          this.$set(
            this.dayMap,
            moment(rt.time)
              .tz(this.$timezone)
              .format('DDMMYYYY'),
            rt.time
          )
        })

        Object.keys(this.dayMap).forEach(key => {
          let d = this.auditLogList.filter(rt => rt.day === key)
          this.$set(this.resultMap, this.dayMap[key], d)
        })
      }
      this.sortValue = Object.values(this.dayMap)
      this.sortValue.sort()
      this.loading = false
    },
    replace(stringToReplace, linkConfig) {
      if (stringToReplace.indexOf('{') < 0) {
        return stringToReplace
      }
      const stringExtractor = this.extract(['{', '}'])
      const stuffIneed = stringExtractor(stringToReplace)
      if (isEmpty(linkConfig)) {
        stuffIneed.forEach(text => {
          stringToReplace = stringToReplace.replace(
            `{${text}}`,
            `<b>${text}</b>`
          )
        })
      } else if (!isEmpty(linkConfig)) {
        let linkConfigs = JSON.parse(linkConfig)
        if (linkConfigs && linkConfig.length) {
          let stateFlowId = linkConfigs[0].stateFlowId
          let moduleName = linkConfigs[0].moduleName
          // let id = linkConfigs[0].id
          if (stateFlowId) {
            if (moduleName) {
              let url = this.$router.resolve({
                path: `/app/setup/process/stateflows/${moduleName}/${stateFlowId}/edit`,
              }).href
              stuffIneed.forEach(text => {
                stringToReplace = stringToReplace.replace(
                  `{${text}}`,
                  `<a href="${url}" class="pointer f14 fc-audit-link" style="color: #2f6acf;" target="_blank">${text}</a>`
                )
              })
              this.text = stringToReplace
            } else {
              let url = this.$router.resolve({
                path: `/app/setup/process/stateflows/workorder/${stateFlowId}/edit`,
              }).href
              stuffIneed.forEach(text => {
                stringToReplace = stringToReplace.replace(
                  `{${text}}`,
                  `<a href="${url}" class="pointer f14 fc-audit-link" style="color: #2f6acf;" target="_blank">${text}</a>`
                )
              })
              this.text = stringToReplace
            }
          } else if (linkConfigs[0].reportId) {
            let routeName = this.getReportsRouteName(linkConfigs[0])
            let url = this.$router.resolve({
              path: routeName + linkConfigs[0].reportId,
            }).href
            stuffIneed.forEach(text => {
              stringToReplace = stringToReplace.replace(
                `{${text}}`,
                `<a href="${url}" class="pointer f14 fc-audit-link" style="color: #2f6acf;" target="_blank">${text}</a>`
              )
            })
            this.text = stringToReplace
          }
          // else if (moduleName && isWebTabsEnabled()) {
          //   let url = this
          //   let viewname = await this.fetchView(moduleName)
          //   let { name } =
          //     findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
          //   if (name) {
          //     url = this.$router.resolve({
          //       name,
          //       params: { viewname, id },
          //     }).href
          //     stuffIneed.forEach(text => {
          //       stringToReplace = stringToReplace.replace(
          //         `{${text}}`,
          //         `<a href="${url}" class="pointer f14 fc-audit-link" style="color: #2f6acf;" target="_blank">${text}</a>`
          //       )
          //     })
          //   } else {
          //     stuffIneed.forEach(text => {
          //       stringToReplace = stringToReplace.replace(
          //         `{${text}}`,
          //         `<b>${text} <i>(#${id})</i></b>`
          //       )
          //     })
          //   }
          //   this.text = stringToReplace
          // }
          else if (moduleName) {
            stuffIneed.forEach(text => {
              stringToReplace = stringToReplace.replace(
                `{${text}}`,
                `<b>${text}</b>`
              )
            })
          } else {
            stuffIneed.forEach(text => {
              stringToReplace = stringToReplace.replace(
                `{${text}}`,
                `<b>${text}</b>`
              )
            })
          }
        }
      }
      return stringToReplace
    },
    extract([beg, end]) {
      const matcher = new RegExp(`${beg}(.*?)${end}`, 'gm')
      const normalise = str => str.slice(beg.length, end.length * -1)
      return function(str) {
        return str.match(matcher).map(normalise)
      }
    },
    getTime(miliseconds) {
      return this.$options.filters.fromNow(miliseconds)
    },
    listLoading() {
      this.fetchAuditLog(true)
    },
    formatTime(date) {
      return moment(date)
        .tz(this.$timezone)
        .format('MMM-YYYY')
    },
    async loadSystemApps() {
      let { error, data } = await loadApps()
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.appHash = data.reduce((appHash, appObj) => {
          let { id, name } = appObj || {}
          appHash[id] = name
          return appHash
        }, {})
      }
    },
    nextPage() {
      this.fetchAuditLog()
    },
    loadusers() {
      this.loading = true
      API.get(`/v2/application/users/list?inviteAcceptStatus=true`).then(
        ({ data, error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.userlist = data.users || []
            const uniqueValuesSet = new Set()
            this.userListData = this.userlist.filter(obj => {
              const isPresentInSet = uniqueValuesSet.has(obj.ouid)
              uniqueValuesSet.add(obj.ouid)
              return !isPresentInSet
            })
          }
          this.loading = false
        }
      )
    },
    remoteSearchUser: debounce(async function(searchText) {
      this.loading = true
      API.get(
        `/v2/application/users/list?inviteAcceptStatus=true&search=${searchText}`
      ).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.userlist = data.users || []
          const uniqueValuesSet = new Set()
          this.userListData = this.userlist.filter(obj => {
            const isPresentInSet = uniqueValuesSet.has(obj.ouid)
            uniqueValuesSet.add(obj.ouid)
            return !isPresentInSet
          })
        }
        this.loading = false
      })
    }, 1000),
    searchShowHide() {
      this.searchHide = false
      this.searchShow = true
    },
    searchClose() {
      this.searchHide = true
      this.searchShow = false
      this.querySearch = null
      this.listLoading()
    },
    showExtraFilter() {
      this.showFilter = !this.showFilter
    },
    setPage(page) {
      this.auditPage = page
      this.fetchAuditLog()
    },
    clearFiltersAll() {
      this.userData = null
      this.recordData = null
      this.listLoading()
    },
    searchLoadData() {
      this.auditPage = 1
      this.perPage = 50
      this.auditCount = null
      this.fetchAuditLog()
    },
    handleExpandChange(row) {
      const id = row.id
      const lastId = this.expandRowKeys[0]
      this.expandRowKeys = id === lastId ? [] : [id]
    },
    tableRowExpand(tableIndex, row) {
      this.$refs.auditLogTable[tableIndex].toggleRowExpansion(row)
    },
    getFormatter(timeStamp) {
      return moment(timeStamp)
        .tz(this.$timezone)
        .format('dddd, DD MMM YYYY')
    },
    getFormatTime(time) {
      return moment(time)
        .tz(this.$timezone)
        .format('hh:mm A')
    },
    getReportsRouteName(linkJsonConfig) {
      let moduleName = linkJsonConfig.moduleName
      let report_type = linkJsonConfig.reportType
      if (
        moduleName === 'undefined' ||
        moduleName === null ||
        moduleName === 'energydata'
      ) {
        return '/app/em/reports/newview/'
      }
      if (
        report_type !== undefined &&
        report_type !== null &&
        report_type == 4
      ) {
        return '/app/em/pivot/view/'
      } else if (linkJsonConfig.moduleType) {
        return '/app/ca/reports/newview/'
      } else {
        switch (moduleName) {
          case 'workorder':
          case 'workorderLabour':
          case 'workorderCost':
          case 'workorderItem':
          case 'workorderTools':
          case 'workorderService':
          case 'workorderTimeLog':
          case 'workorderHazard':
          case 'plannedmaintenance':
            return '/app/wo/reports/newview/'
          case 'purchaseorder':
          case 'purchaseorderlineitems':
          case 'poterms':
          case 'purchaserequest':
          case 'purchaserequestlineitems':
            return '/app/purchase/reports/newview/'
          case 'asset':
            return '/app/at/reports/newview/'
          case 'alarm':
          case 'newreadingalarm':
          case 'readingalarmoccurrence':
          case 'bmsalarm':
          case 'mlAnomalyAlarm':
          case 'anomalyalarmoccurrence':
          case 'violationalarm':
          case 'violationalarmoccurrence':
          case 'operationalarm':
          case 'operationalarmoccurrence':
          case 'sensoralarm':
          case 'sensoralarmoccurrence':
          case 'sensorrollupalarm':
          case 'sensorrollupalarmoccurrence':
          case 'basealarm':
          case 'alarmoccurrence':
          case 'readingevent':
          case 'bmsevent':
          case 'mlAnomalyEvent':
          case 'violationevent':
          case 'operationevent':
          case 'sensorevent':
          case 'baseevent':
            return '/app/fa/reports/newview/'
          case 'servicerequest':
            return '/app/sr/reports/newview/'
          case 'visitor':
          case 'visitorlog':
          case 'invitevisitor':
          case 'watchlist':
            return '/app/vi/reports/newview/'
          case 'tenant':
          case 'tenantunit':
          case 'tenantcontact':
          case 'quote':
          case 'contact':
          case 'tenantspaces':
          case 'quotelineitems':
          case 'quoteterms':
          case 'people':
          case 'newsandinformationsharing':
          case 'neighbourhoodsharing':
          case 'dealsandofferssharing':
          case 'contactdirectorysharing':
          case 'admindocumentsharing':
          case 'audienceSharing':
            return '/app/tm/reports/newview/'
          case 'contracts':
          case 'purchasecontracts':
          case 'purchasecontractlineitems':
          case 'labourcontracts':
          case 'labourcontractlineitems':
          case 'warrantycontracts':
          case 'warrantycontractlineitems':
          case 'rentalleasecontracts':
          case 'rentalleasecontractlineitems':
            return '/app/ct/reports/newview/'
          case 'item':
          case 'tool':
          case 'itemTransactions':
          case 'tootTransactions':
          case 'itemTypes':
          case 'toolTypes':
          case 'storeRoom':
          case 'shipment':
          case 'transferrequest':
          case 'transferrequestshipmentreceivables':
          case 'transferrequestpurchaseditems':
            return '/app/inventory/reports/newview/'
          case 'inspectionTemplate':
          case 'inspectionResponse':
            return '/app/inspection/reports/newview/'
        }
      }
    },
  },
}
</script>
<style lang="scss">
.fc-log-search {
  height: 40px;
  line-height: 40px;
  padding-right: 30px;
  border-radius: 3px;
  background-color: #ffffff;
  border: solid 1px #d0d9e2;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.4px;
  color: #333333;
  font-size: 16px;
  padding-left: 15px !important;
  font-weight: 500;
  transition: 0.4s all;
}
.fc-search-close {
  position: absolute;
  right: 10px;
  top: 10px;
  background: #fff;
  cursor: pointer;
}
.filter-border {
  margin-left: 15px;
  padding-top: 5px;
  cursor: pointer;
  display: flex;
  position: relative;
  align-items: center;
  justify-content: center;
  border-radius: 2px;
  position: relative;
  .dot-active-pink {
    position: absolute;
    top: 2px;
    right: -4px;
  }
}
.filterActive {
  transition: 0.4s all;
  .fc-fill-path-grey path {
    fill: #39b2c2;
  }
}
.fc-audit-log-page {
  .el-table__body-wrapper {
    height: auto !important;
  }
  .el-table__expanded-cell {
    background: #f5f7fa;
  }
  .fc-filter-animation {
    border-bottom: 1px solid #e6e9f2;
  }
  .fc-setup-actions-header {
    padding-bottom: 10px;
  }
  .fc-extra-filter-block {
    background: #fff;
    margin-top: 10px;
    padding-top: 15px;
    margin-left: 15px;
    width: calc(100% - 30px);
    border: 1px solid rgba(230, 233, 242, 0.6);
    border-radius: 4px;
  }
  .fc-setup-table-header-hide {
    tbody tr {
      &:hover {
        background: #f1f8fa !important;
      }
    }
  }
}
.fc-audit-link {
  cursor: pointer;
  &:hover {
    text-decoration: underline;
  }
}
</style>
