<template>
  <div class="calendar-view-container height100">
    <div v-if="isLoading" class="calendar-loading-spinner">
      <Spinner :show="isLoading" size="80"></Spinner>
    </div>
    <div v-else class="calendar-wrapper-container">
      <Calendar
        ref="calendar"
        calendarView="month"
        :availableViews="['month']"
        :calendarEvents="calendarEvent"
        :viewDetails="viewDetailObj"
        :referenceDate="referenceDate"
        :account="$account"
        :outOfScopeCellClicked="outOfScope"
        class="calendar-wrapper"
        @OnCellClicked="onViewMore"
        @OnViewMoreClick="onViewMore"
      >
        <template #eventPopup="{event}">
          <glimpse-card
            :key="`event - ${event.eventId}`"
            :config="getConfig(event, true)"
            @redirectToSummary="eventClick(event.eventId)"
            class="p12"
          >
            <template #fields>
              <div class="gc-container">
                <div
                  v-for="fld in getFields(event)"
                  :key="`${fld.value}${fld}-field`"
                  class="fields-container"
                >
                  <div class="fld-name d-flex bold" style="color:#1d384e">
                    {{ fld.name }}
                  </div>
                  <div class="d-flex fld-value">
                    {{ fld.value }}
                  </div>
                </div>
              </div>
            </template>
          </glimpse-card>
        </template>
      </Calendar>
      <div class="view-more-dialog">
        <div class="dialog-header justify-content-space">
          <span class="f16 bold">{{ selectedDate }}</span>
          <div class="cv-action-container">
            <ToggleSearch ref="toggle-search" @onChange="onCalendarSearch" />
          </div>
        </div>

        <v-infinite-scroll
          v-if="!isEmpty(viewMoreList)"
          :loading="isLoading"
          @bottom="nextPage"
          :offset="20"
          class="infinte-scroll-view"
        >
          <div
            v-for="viewData in viewMoreList"
            :key="`viewData ${viewData.data.id}`"
            class="view-data-container"
          >
            <div class="cv-horiz-line"></div>
            <div class="view-data-items">
              <div class="f12 id-tag d-flex justify-content-space">
                <span>{{ `#${getRecordId(viewData.data)}` }}</span>

                <span class="pR6">{{ viewData.data.moduleState.name }}</span>
              </div>
              <el-popover trigger="click" placement="bottom-start">
                <div
                  slot="reference"
                  class="f16 bold pointer cal-text-ellipsis"
                >
                  {{ viewData.data[mainField] }}
                </div>
                <glimpse-card
                  :key="`event - ${viewData.data.id}`"
                  :config="getConfig(viewData.data)"
                  @redirectToSummary="eventClick(viewData.data.id)"
                >
                  <template #fields>
                    <div class="gc-container">
                      <div
                        v-for="fld in getFields(viewData.data)"
                        :key="`${fld.value}-key`"
                        class="fields-container"
                      >
                        <div
                          class=" fld-name d-flex bold "
                          style="color: #1d384e"
                        >
                          {{ fld.name }}
                        </div>
                        <div class="d-flex fld-value">
                          {{ fld.value }}
                        </div>
                      </div>
                    </div>
                  </template>
                </glimpse-card>
              </el-popover>
            </div>
          </div>
          <spinner v-if="viewMoreLoading" :show="viewMoreLoading"></spinner>
        </v-infinite-scroll>
        <spinner
          v-else-if="viewMoreLoading"
          :show="viewMoreLoading"
          class="mT40"
        ></spinner>
        <div
          v-else-if="isEmpty(viewMoreList)"
          class="mT30 d-flex justify-center"
        >
          {{ $t('common._common.no_record_found') }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { Calendar } from '@facilio/ui/app'
import Spinner from '@/Spinner'
import moment from 'moment-timezone'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import ToggleSearch from 'src/newapp/list/components/ToggleSearch.vue'
import {
  findRouterForModuleInApp,
  moduleRouteHash,
} from 'src/newapp/viewmanager/routeUtil.js'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import GlimpseCard from 'src/newapp/components/GlimpseCard'
import { getDisplayValue } from 'util/field-utils'
import debounce from 'lodash/debounce'
import VInfiniteScroll from 'v-infinite-scroll'
import isEqual from 'lodash/isEqual'
import { moduleByLocalId } from 'src/newapp/viewmanager/calendarSupportUtil'
import { eventBus } from '@/page/widget/utils/eventBus'
import { mapState } from 'vuex'

const ViewType = {
  DAY: 1,
  WEEK: 2,
  MONTH: 3,
}

export default {
  props: ['viewDetail', 'record', 'moduleName', 'viewname', 'filters'],
  components: { Calendar, Spinner, ToggleSearch, GlimpseCard, VInfiniteScroll },
  data() {
    return {
      isEmpty,
      selectedDate: null,
      viewMoreData: null,
      currentMonthData: null,
      isLoading: true,
      viewMoreLoading: false,
      fetchingMore: false,
      outOfScope: false,
      searchText: null,
      recordCount: null,
      page: 1,
      calendarEvent: {},
      moduleStateObj: {},
      filteredList: [],
      viewMoreTimeEvent: {},
    }
  },
  created() {
    this.getCurrentDate()
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
    mainFieldObj() {
      let { fields } = this.metaInfo || {}
      let mainFieldObj = (fields || []).find(field => field.mainField)
      return mainFieldObj
    },
    viewDetailObj() {
      let viewDetailObj = this.viewDetail
      if (isEmpty(this.viewDetail?.fields)) {
        viewDetailObj = {
          ...this.viewDetail,
          fields: [
            {
              name: this.mainFieldObj?.name,
              displayName: this.mainFieldObj?.displayName,
              field: this.mainFieldObj,
            },
          ],
        }
      }
      return viewDetailObj
    },
    viewMoreList() {
      let { moduleStateObj = {} } = this
      let list = (this.viewMoreData || []).map(list => {
        let { data = {} } = list || {}
        let { moduleState } = data || {}
        let { id } = moduleState || {}
        let name = moduleStateObj[id]?.primaryValue
        moduleState = { ...moduleState, name }
        data = { ...data, moduleState }
        return { data }
      })
      return list
    },
    onChangeCal() {
      let { query } = this.$route || {}
      let { onChange = false } = query || {}
      return onChange
    },
    preservedTimeLimit() {
      let { query } = this.$route || {}
      let { startTime, endTime = null } = query || {}

      return {
        startTime: startTime ? parseInt(startTime) : null,
        endTime: endTime ? parseInt(endTime) : null,
      }
    },
    isToday() {
      let { query } = this.$route || {}
      let { today = null } = query || {}

      return JSON.parse(today)
    },
    referenceDate() {
      let { refDate } = this.$route?.query || {}
      let { startTime } = this.preservedTimeLimit || {}

      return parseInt(refDate) || startTime
    },
    calendarContext() {
      let { calendarViewContext } = this.viewDetail || {}
      let { startDateField, endDateField } = calendarViewContext || {}

      return { startDateField, endDateField }
    },
    mainField() {
      let { name } = this.mainFieldObj || {}
      return name
    },
    scrollDisabled() {
      return this.isLoading || this.fetchingMore || this.viewMoreLoading
    },
    webTabRouteName() {
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        return name
      }
      return null
    },
  },
  watch: {
    preservedTimeLimit: {
      handler(oldVal, newVal) {
        if (!isEqual(oldVal, newVal)) {
          this.isLoading = true
          this.fetchEvents()
          if (this.onChangeCal) this.getCurrentDate()
        }
      },
      immediate: true,
    },
    referenceDate: {
      handler(newVal) {
        if (newVal) {
          let startTime = moment
            .tz(newVal, this.$timezone)
            .startOf('month')
            .valueOf()
          let endTime = moment
            .tz(newVal, this.$timezone)
            .endOf('month')
            .valueOf()
          let { query } = this.$route || {}

          query = { ...query, startTime, endTime, refDate: newVal }
          this.$router.push({ query }).catch(() => {})
        }
      },
    },
    isToday: {
      handler(newVal) {
        if (newVal) {
          let endDate = moment
            .tz(this.referenceDate, this.$timezone)
            .endOf('day')
            .valueOf()

          this.onViewMore({ startDate: this.referenceDate, endDate })
        }
      },
    },
  },
  methods: {
    deserialize(startTime, endTime) {
      let calendarViewRequest = {
        startTime,
        endTime,
        maxResultPerCell: 3,
        dateAggregateOperator: 12,
        calendarViewType: ViewType.MONTH,
      }

      let params = {
        calendarViewRequest: JSON.stringify(calendarViewRequest),
        page: 1,
        perPage: 20,
        filters: !isEmpty(this.filters) ? JSON.stringify(this.filters) : null,
      }
      return params
    },
    getFields(event) {
      let { eventsKey = {} } = event || {}
      let { startDateField, endDateField } = this.calendarContext || {}

      let data = []

      if (!isEmpty(startDateField)) {
        let eventStart =
          this.$getProperty(event, `${startDateField?.name}`) ||
          this.$getProperty(eventsKey, `${startDateField?.name}`)
        let actualStart = getDisplayValue(startDateField, eventStart) || '---'

        data.push({
          name: startDateField?.displayName,
          value: actualStart,
        })
      }
      if (!isEmpty(endDateField)) {
        let eventEnd =
          this.$getProperty(event, `${endDateField?.name}`) ||
          this.$getProperty(eventsKey, `${endDateField?.name}`)
        let actualEnd = getDisplayValue(endDateField, eventEnd) || '---'

        data.push({
          name: endDateField?.displayName,
          value: actualEnd,
          id: event.localId,
        })
      }

      return data
    },
    getRecordId(data) {
      let { localId, id } = data || {}
      return moduleByLocalId.includes(this.moduleName) ? localId || id : id
    },
    getConfig(data, event = false) {
      let { mainField = '' } = this
      let { id = '', moduleState = {}, localId } = data || {}
      let { id: moduleStateId, name: moduleStateName } = moduleState || {}
      let name = data[mainField]

      if (event) {
        let { displayName } = this.moduleStateObj[moduleStateId] || {}

        id = data?.eventId
        name = data?.title
        moduleStateName = displayName
      }

      id = this.getRecordId({ localId, id })

      let eventData = {
        id,
        displayName: name,
        moduleState: moduleStateName,
        canShowRedirect: true,
      }

      return eventData
    },
    getCurrentDate() {
      let dateToCheck = moment
        .tz(this.referenceDate, this.$timezone)
        .format('MM YY')
      let isCurrentMonth =
        moment.tz(this.$timezone).format('MM YY') === dateToCheck

      const startOfMonth = isCurrentMonth
        ? moment.tz(this.$timezone)
        : moment.tz(this.referenceDate, this.$timezone)

      const startDate = startOfMonth
        .clone()
        .startOf('day')
        .valueOf()

      const endDate = startOfMonth
        .clone()
        .endOf('day')
        .valueOf()

      if (isNaN(startDate)) return
      this.onViewMore({ startDate, endDate })
    },
    getCurrentTimeStamp() {
      let { startTime, endTime } = this.preservedTimeLimit

      const startOfMonth = moment.tz(startTime, this.$timezone)
      const endOfMonth = moment.tz(endTime, this.$timezone)
      const startOfWeek = startOfMonth
        .clone()
        .isoWeekday(1) //since start of week in our calendar is monday
        .startOf('isoWeek')
        .valueOf()

      const endOfWeek = endOfMonth
        .clone()
        .isoWeekday(1)
        .endOf('isoWeek')
        .valueOf()

      return { startTime: startOfWeek, endTime: endOfWeek }
    },
    nextPage() {
      if (!this.scrollDisabled && this.recordCount === 20) {
        this.page++
        this.fetchingMore = true
        this.onViewMore(this.viewMoreTimeEvent, this.searchText)
      }
    },
    onCalendarSearch(calendarSearchData) {
      this.searchText = calendarSearchData
      this.onViewMore(this.viewMoreTimeEvent, calendarSearchData)
    },
    fetchEvents: debounce(async function() {
      let { startTime, endTime } = this.getCurrentTimeStamp()

      if (isNaN(startTime) || isEmpty(this.viewname)) return

      eventBus.$emit('custom-event', true)

      this.isLoading = true

      let serializedData = await this.deserialize(startTime, endTime)
      let res = await API.get(
        `v3/modules/calendarData/${this.moduleName}/view/${this.viewname}/get`,
        serializedData
      )

      let { data, meta, error } = res

      if (!error) {
        let { supplements } = meta || {}

        if (supplements)
          this.moduleStateObj = supplements[this.moduleName]?.moduleState || {}

        this.calendarEvent = data[this.moduleName] || {}
      } else {
        console.warn(`${this.moduleName} calendar API failed or cancelled`)
        let { isCancelled } = error || {}
        if (!isCancelled) throw error
      }

      this.isLoading = false
      eventBus.$emit('custom-event', false)
    }, 200),
    eventClick(id) {
      let { viewname, moduleName } = this
      let query = this.$route.query || null

      if (isWebTabsEnabled()) {
        if (!isEmpty(this.webTabRouteName)) {
          let { href } = this.$router.resolve({
            name: this.webTabRouteName,
            params: { viewname, id },
            query,
          })
          window.open(href, '_blank')
        }
      } else {
        let currentModuleName = isEmpty(moduleRouteHash[moduleName])
          ? 'custom'
          : moduleName
        let routeObj =
          findRouterForModuleInApp(currentModuleName, pageTypes.OVERVIEW) || {}

        if (!isEmpty(routeObj)) {
          let { href } = this.$router.resolve({
            ...routeObj(id, viewname, moduleName),
            query,
          })
          window.open(href, '_blank')
        }
      }
    },
    async onViewMore(event, search) {
      let { startDate } = event
      let { outOfScope } = this
      let { query } = this.$route || {}

      startDate = moment
        .tz(startDate || this.referenceDate, this.$timezone)
        .valueOf()
      let endDate = moment
        .tz(startDate, this.$timezone)
        .endOf('day')
        .valueOf()

      query = { ...query, today: null, onChange: null, refDate: startDate }

      if (this.onChangeCal) outOfScope = false
      else outOfScope = true

      if (!this.fetchingMore) {
        this.viewMoreData = []
        this.page = 1
      }

      this.viewMoreLoading = true
      this.$router.replace({ query }).catch(() => {})
      this.outOfScope = outOfScope
      this.viewMoreTimeEvent = event
      this.selectedDate = moment.tz(startDate, this.$timezone).format('D dddd')

      let calendarViewRequest = {
        startTime: parseInt(startDate),
        endTime: parseInt(endDate),
      }

      let param = {
        calendarViewRequest: JSON.stringify(calendarViewRequest),
        page: this.fetchingMore ? this.page : 1,
        perPage: 20,
        filters: !isEmpty(this.filters) ? JSON.stringify(this.filters) : null,
        search: !isEmpty(search) ? search : null,
      }

      let { data, meta, error } = await API.get(
        `v3/modules/calendarData/${this.moduleName}/view/${this.viewname}/list`,
        param
      )

      if (!error && !isEmpty(data)) {
        if (this.fetchingMore)
          this.viewMoreData = [
            ...(this.viewMoreData || []),
            ...data[this.moduleName],
          ]
        else this.viewMoreData = data[this.moduleName]

        this.recordCount = data[this.moduleName]
          ? data[this.moduleName]?.length
          : 0
      }
      if (!isEmpty(meta)) {
        let { supplements } = meta || {}

        if (supplements) {
          let moduleState = supplements[this.moduleName]?.moduleState || {}
          this.moduleStateObj = { ...this.moduleStateObj, ...moduleState }
        }
      }

      if (isEmpty(search) && !isEmpty(this.$refs['toggle-search']))
        await this.$refs['toggle-search'].clearSearch()

      this.fetchingMore = this.viewMoreLoading = false
    },
  },
}
</script>
<style lang="scss">
.calendar-view-container {
  margin: 0px 9.5px 10px;

  .calendar-wrapper {
    height: calc(100vh - 160px);
    flex-grow: 1;
    background-color: #fff;
    border-bottom-left-radius: 5px;
    box-shadow: 0 3px 7px 0 rgb(233 233 226 / 50%);
  }
  .calendar-wrapper-container {
    width: 100%;
    height: 100%;
    display: flex;
    gap: 2px;

    .view-more-dialog {
      flex-shrink: 0;
      width: 360px;
      height: calc(100vh - 160px);
      background-color: #fff;
      border-bottom-right-radius: 4px;
      border: 0.5px solid rgba(196, 196, 196, 0.25);
      box-shadow: 0 3px 7px 0 rgb(233 233 226 / 50%);

      .dialog-header {
        height: 52px;
        display: flex;
        align-items: center;
        padding: 0px 12px;
        border-bottom: 1px solid rgba(196, 196, 196, 0.25);

        .cv-action-container {
          display: flex;
          gap: 24px;
          align-items: center;
        }
      }
      .infinte-scroll-view {
        padding-bottom: 50px;
        overflow-y: scroll;
        height: calc(100% - 65px);
      }

      .view-data-container {
        display: flex;
        padding: 12px 10px 12px 8px;
        gap: 12px;

        .cv-horiz-line {
          width: 6px;
          background-color: #cce8ff;
          border-radius: 3px;
        }

        .cal-text-ellipsis {
          max-width: 320px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .view-data-items {
          display: flex;
          flex-direction: column;
          flex-grow: 1;
          gap: 2px;

          .id-tag {
            color: #71818e;
          }
        }
      }
    }
  }
}
.calendar-loading-spinner {
  display: flex;
  align-items: center;
  position: absolute;
  z-index: 2000;
  background-color: rgba(255, 255, 255, 0.9);
  margin: 0;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  -webkit-transition: opacity 0.3s;
  transition: opacity 0.3s;
}
.gc-container {
  width: 100%;
  display: flex;
  gap: 10px;
  padding: 8px;

  .fields-container > * {
    flex: 1 0 50%;
  }
  .fields-container {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 4px;

    .fld-name {
      color: #1d384e !important;
      white-space: nowrap;
    }
    .fld-value {
      white-space: nowrap;
      color: #667480;
    }
  }
}
</style>
