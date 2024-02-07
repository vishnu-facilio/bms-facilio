import { isNullOrUndefined } from '@facilio/utils/validation'
import { isEnumField } from '@facilio/utils/field'
import { deepCloneObject, makeObjectNonReactive } from 'util/utility-methods'

const viewStateMeta = makeObjectNonReactive({
  //start and end values for each view are maintained
  DAY: {
    operatorID: 62,
    pickerTabName: 'D',
    displayName: 'D',
    start: null,
    end: null,
    grouping: '4H',
    gridLines: '1H',
    showGridLineHeaders: true,
    displayType: 'dateTime',
  },
  WEEK: {
    operatorID: 63,
    pickerTabName: 'W',
    displayName: 'W',
    start: null,
    end: null,
    grouping: null,
    gridLines: 'W',
    showGridLineHeaders: true,
    displayType: 'subject',
  },
  MONTH: {
    operatorID: 64,
    pickerTabName: 'M',
    displayName: 'M',
    start: null,
    end: null,
    grouping: null,
    gridLines: 'DAY',
    showGridLineHeaders: true,
    displayType: 'dateTime',
  },
  YEAR: {
    operatorID: 65,
    pickerTabName: 'Y',
    displayName: 'Y',
    start: null,
    end: null,
    grouping: 'MONTH',
    gridLines: 'WEEK',
    showGridLineHeaders: true,
    displayType: 'frequency',
  },
})
import NewDateHelper from 'src/components/mixins/NewDateHelper'
import Cell from './Cell'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: {
    allowUserSettings: {
      default: true,
    },
    defaultSettings: {
      type: Object,
    },
    views: {
      type: Array,
      default: () => ['DAY', 'WEEK', 'MONTH', 'YEAR'],
    },
    defaultView: {
      type: String,
      default: function() {
        return this.views[0]
      },
    },
    startRange: {
      type: Number,
    },

    isDragDropAllowed: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      currentView: null, //DAY/WEEK/MONTH/YEAR,
      plannerSettings: null,
      viewState: {},
      timeZone: null,
      showPicker: false,
      pickerObj: null,
      isFullScreen: false,
      loading: true,
      taskList: [],
      showSettings: false,
      gridColumns: [],
      groupColumns: [],
      currentDate: this.$helpers.getOrgMoment().valueOf(),
      currentDateCol: null,
      daysInWeek: this.getDaysInWeek(),
    }
  },

  /* Planner/calendar flow
    On create copy set up whatever views passed in props to internal state
    On mount Load settings ,once settings loaded initialize time values etc for the views present
    Emit a initial change event to load data from consuming component
  */
  created() {
    this.timeZone = this.$timezone //needed for picker
    this.intiViewStateObj()

    //init default views in planner
  },
  mounted() {
    /* load settings from server and use that to initPlanner
    or if default setting given by user ,use them
    once settings obtained init the planner state and fire a initial change event to load data*/

    if (this.allowUserSettings) {
      this.loadSettings().then(settingData => {
        this.plannerSettings = settingData
        //load custom field list to display in legend setting
        this.$http.get('/module/meta?moduleName=workorder').then(resp => {
          this.customEnumFields = resp.data.meta.fields
            .filter(e => !e.default)
            .filter(e => isEnumField(e))

          this.legendOptions.push(
            ...this.customEnumFields.map(e => {
              return { name: e.name, displayName: e.displayName }
            })
          )
        })

        this.initPlanner()
      })
    } else if (this.defaultSettings) {
      this.plannerSettings = this.defaultSettings
      this.initPlanner()
    }
  },
  computed: {
    currentViewState() {
      return this.viewState[this.currentView]
    },
  },
  methods: {
    syncTabInQuery() {
      //sync start and end times of least supported view only ,
      this.$router.replace({
        query: {
          ...this.$route.query,
          calendarTab: this.currentView,
          startTime: this.viewState[this.views[0]].start,
          endTime: this.viewState[this.views[0]].end,
        },
      })
    },
    getDaysInWeek() {
      let weekDaysInLocale = []
      let dayOfWeek = this.$helpers.getOrgMoment()
      dayOfWeek.startOf('week')
      for (let i = 0; i < 7; i++) {
        weekDaysInLocale.push(dayOfWeek.format('ddd'))
        dayOfWeek.add(1, 'day')
      }
      return weekDaysInLocale
    },
    /* 4 views ,DAY ,WEEK ,MONTH ,YEAR */
    /* Initialize meta for only views chosen by user */
    intiViewStateObj() {
      this.views.forEach(view => {
        this.$set(this.viewState, view, deepCloneObject(viewStateMeta[view]))
      })
      this.currentView = this.defaultView
      //if user is refreshing persist the view,if view supported in current planner
      if (
        this.$route.query.calendarTab &&
        this.views.includes(this.$route.query.calendarTab)
      ) {
        this.currentView = this.$route.query.calendarTab
      }
    },
    initPlanner() {
      this.initStateFromSettings()

      if (this.$route.query.startTime) {
        this.setViews(Number.parseInt(this.$route.query.startTime))
      } else {
        //init planner also called when settings are changed , in that  case alone start time exists when init is done..ie maintain last tab /view range before settings change
        this.setViews(
          this.currentViewState.start
            ? this.currentViewState.start
            : this.$helpers.getOrgMoment().valueOf()
        ) //init all views to current date
      }

      this.setPicker()
      this.syncTabInQuery()
      this.emitViewChanged()
    },

    /* For each view that exists(props) copy  settings to internal view state object*/
    initStateFromSettings() {
      //Column meta DS different in planner , so copy values to local state and use
      if (!isEmpty(this.plannerSettings)) {
        this.views.forEach(view => {
          this.viewState[view].gridLines = this.plannerSettings.viewSettings[
            view
          ].gridLines
          this.viewState[view].grouping =
            this.plannerSettings.viewSettings[view].grouping == 'NONE'
              ? null
              : this.plannerSettings.viewSettings[view].grouping

          this.viewState[view].displayType = this.plannerSettings.viewSettings[
            view
          ].displayType

          this.viewState[
            view
          ].gridLinesFormat = this.plannerSettings.viewSettings[
            view
          ].gridLinesFormat

          if (
            !isNullOrUndefined(
              this.plannerSettings.viewSettings[view].showGridLineHeaders
            )
          ) {
            this.viewState[
              view
            ].showGridLineHeaders = this.plannerSettings.viewSettings[
              view
            ].showGridLineHeaders
          }
        })
      }
    },
    emitViewChanged() {
      this.loading = true
      this.$emit('viewChanged', {
        start: this.getStartTimeStamp(),
        end: this.getEndTimeStamp(),
        interval: this.currentViewState.gridLines,
        settings: this.plannerSettings,
      })
    },

    /*set all views to given date  */
    setViews(date) {
      this.viewState.DAY && this.setDay(date)
      this.viewState.WEEK && this.setWeek(date)
      this.viewState.MONTH && this.setMonth(date)
      this.viewState.YEAR && this.setYear(date)
    },

    setDay(date) {
      this.viewState.DAY.start = this.$helpers
        .getOrgMoment(date)
        .startOf('day')
        .valueOf()
      this.viewState.DAY.end = this.$helpers
        .getOrgMoment(date)
        .endOf('day')
        .valueOf()
    },
    setWeek(date) {
      this.viewState.WEEK.start = this.$helpers
        .getOrgMoment(date)
        .startOf('week')
        .valueOf()
      this.viewState.WEEK.end = this.$helpers
        .getOrgMoment(date)
        .endOf('week')
        .valueOf()
    },
    setMonth(date) {
      this.viewState.MONTH.start = this.$helpers
        .getOrgMoment(date)
        .startOf('month')
        .valueOf()
      this.viewState.MONTH.end = this.$helpers
        .getOrgMoment(date)
        .endOf('month')
        .valueOf()
    },
    setYear(date) {
      this.viewState.YEAR.start = this.$helpers
        .getOrgMoment(date)
        .startOf('year')
        .valueOf()
      this.viewState.YEAR.end = this.$helpers
        .getOrgMoment(date)
        .endOf('year')
        .valueOf()
    },
    /* ******* */
    datePickerTabs() {
      let temp = {}
      temp['enableByOperationOnId'] = true
      temp['disableDefaultLabels'] = true
      let enabledTabs = []
      this.views.forEach(view => {
        enabledTabs.push(viewStateMeta[view].pickerTabName)
      })

      temp['enabledTabs'] = enabledTabs
      temp['loadAdditional'] = {
        year: {
          period: 'start',
          label: 'year',
          operation: 'add',
          value: 4,
        },
      }
      return temp
    },

    /* need to keep state of all view tabs in sync with picker 2 WAY SYNC */

    /* user changes date range view colheader click or view tab click , eg month to week or drill down on a day  in month view*/
    setPicker() {
      this.showPicker = false

      this.pickerObj = NewDateHelper.getDatePickerObject(
        this.currentViewState.operatorID,
        '' + this.currentViewState.start
      )
      this.$nextTick(() => {
        this.showPicker = true
      })
    },

    /* User changes range via the pick , now sync the views to picker range */
    navigateFromPicker(event) {
      this.goToRange(event.value[0], event.operationOn.toUpperCase())
    },

    /* Today button , go to range that has today in whichever view is active */
    todayClicked() {
      if (this.currentDateCol) {
        //if current view has today return
        this.scrollColToView()
        return
      }
      this.setViews(this.$helpers.getOrgMoment()) //init all views to current date
      this.setPicker()
      this.emitViewChanged()
    },
    handleViewTabClick(view) {
      this.switchView(view)
      this.setPicker()
    },

    goToRange(date, view) {
      //reset data for all views in sync with specified date
      //change current view to desired view
      //if view exists drill down , EX , in day view grid interval can be 1 HR , but 1hr can't be shown as a view
      if (this.viewState[view]) {
        this.setViews(date)
        this.switchView(view)
      }
    },
    /*  While switching views  , tab state ie date is not reset ,
      Eg , in month currently viewing jan, week is set to jan 1st week (starting from first sun of week in 1st jan)
      state in both views is now , week 1-7 jan timestamp , month 1-30 timestamp. state is only reset when moving left/right via picker
    */
    switchView(view) {
      this.currentView = view
      this.syncTabInQuery()
      //can be done either by click the view name buttons straight or via column headers
      //if column headers go the exact time , else set defaults

      this.clearAllSelections()
      //TO DO , move this to the resoure planner hook with same method name

      this.emitViewChanged()
    },
    generateColumnData() {
      let perfStart = performance.now()
      //initialize a list of   gridLines and  grouplines and
      //if grouping is available for view group the gridLines into group lines
      this.gridColumns = []
      let gridLineTs = this.getStartTimeStamp()
      let gridLineEndTimeStamp = this.getEndTimeStamp()
      let gridIndex = 0
      this.currentDateCol = null

      //create and initialize gridline array
      while (gridLineTs <= gridLineEndTimeStamp) {
        let colStart = gridLineTs
        gridLineTs = this.getNextTimeStamp(
          gridLineTs,
          this.viewState[this.currentView].gridLines
        )
        let colEnd = gridLineTs
        let column = {
          start: colStart,
          end: colEnd,
          tasks: [],
          index: gridIndex,
          label: this.getHeader(
            colStart,
            this.viewState[this.currentView].gridLines,
            this.viewState[this.currentView].gridLinesFormat
          ),
        }
        this.gridColumns[gridIndex] = column
        //mark past dates ,
        if (this.currentDate <= colStart) {
          column.past = false
        } else {
          column.past = true
        }
        //find current date column if present within current daterange
        if (this.currentDate >= colStart && this.currentDate < colEnd) {
          this.currentDateCol = column
        }
        gridIndex++
      }

      //after gridLines generate grouplines if required , EX, Year view weekly grid and group by month
      if (this.viewState[this.currentView].grouping) {
        this.groupColumns = []
        //for weeks grouped by month alone do a bit of special handling but  going to start of first month
        let groupLineTs = this.$helpers
          .getOrgMoment(this.getStartTimeStamp())
          .startOf(this.viewState[this.currentView].grouping)
          .valueOf()
        let groupIndex = 0
        let groupEnd = this.getEndTimeStamp()
        //create and initialize group array with each entry having groupline start time and empty gridarray

        while (groupLineTs <= groupEnd) {
          let groupColStart = groupLineTs
          groupLineTs = this.getNextTimeStamp(
            groupLineTs,
            this.viewState[this.currentView].grouping
          )
          let groupColEnd = groupLineTs
          this.groupColumns[groupIndex] = {
            start: groupColStart,
            gridColumns: [],
            end: groupColEnd,
            label: this.getHeader(
              groupColStart,
              this.viewState[this.currentView].grouping
            ),
          }
          groupIndex++
        }

        groupIndex = 0
        //columns are put inside the groups based of their start time,for week and monthy grouping when a week spans across months it's put inside it's start month group
        this.gridColumns.forEach(e => {
          while (e.start >= this.groupColumns[groupIndex].end) {
            groupIndex++ //go to correct groupline and insert gridline into it
          }
          this.groupColumns[groupIndex].gridColumns.push(e)
        })

        //weekly grid with monthly grouping results in the last group being empty
        //check and remove this below
        if (
          this.groupColumns[this.groupColumns.length - 1].gridColumns.length ==
          0
        ) {
          this.groupColumns.pop()
        }
        //mark the last column in each group as last , //needed for darker css border on each group boundary
        this.groupColumns.forEach(group => {
          group.gridColumns.forEach((gridColumn, gridColIndex) => {
            if (gridColIndex == group.gridColumns.length - 1) {
              gridColumn.isGroupBoundary = true
            }
          })
        })
      }
      console.warn('generate columns took', performance.now() - perfStart)
    },
    spreadTasksToColumns() {
      //spread out  task data into multiple columns based on gridlines , put each task in correct columns,
      let gridIndex = 0

      this.taskList.some(task => {
        if (task.start >= this.gridColumns[this.gridColumns.length - 1].end) {
          return true
          //break out of loop when one of the tasks exceeds end column in grid
        }
        this.formatLabel(task)
        //iterate through tasklist list,
        //for each task go to the correct grid(column) and insert

        while (task.start >= this.gridColumns[gridIndex].end) {
          gridIndex++
        }
        this.$set(task, 'selection', false)
        this.gridColumns[gridIndex].tasks.push(task)
        return false
      })
    },

    //this is required to handle weekly split alone
    getStartTimeStamp() {
      let timeStamp = this.viewState[this.currentView].start

      if (
        this.viewState[this.currentView].gridLines == 'WEEK' ||
        this.viewState[this.currentView].grouping == 'WEEK'
      ) {
        timeStamp = this.$helpers
          .getOrgMoment(timeStamp)
          .startOf('week')
          .valueOf()
      }
      return timeStamp
    },

    getEndTimeStamp() {
      let timeStamp = this.viewState[this.currentView].end

      if (
        this.viewState[this.currentView].gridLines == 'WEEK' ||
        this.viewState[this.currentView].grouping == 'WEEK'
      ) {
        timeStamp = this.$helpers
          .getOrgMoment(timeStamp)
          .endOf('week')
          .valueOf()
      }
      return timeStamp
    },
    //methods is used for both column ts next and task drop
    //TO DO remove startof call
    getNextTimeStamp(timeStamp, interval, count = 1) {
      switch (interval) {
        case 'WEEK':
          return this.$helpers
            .getOrgMoment(timeStamp)
            .add(count, 'week')
            .valueOf()
        case 'DAY':
          return this.$helpers
            .getOrgMoment(timeStamp)
            .add(count, 'day')
            .valueOf()
        case 'MONTH':
          return (
            this.$helpers
              .getOrgMoment(timeStamp)
              //.startOf("month") //when grouping weeks by months use this
              .add(count, 'month')
              .valueOf()
          )

        case '4H': //todo make this more generic
          return this.$helpers
            .getOrgMoment(timeStamp)
            .add(count * 4, 'hours')
            .valueOf()
        case '1H':
          return this.$helpers
            .getOrgMoment(timeStamp)
            .add(count, 'hours')
            .valueOf()
        case '.5H':
          return this.$helpers
            .getOrgMoment(timeStamp)
            .add(count * 0.5, 'hours')
            .valueOf()
        case 'QUARTER':
          return this.$helpers
            .getOrgMoment(timeStamp)
            .startOf('quarter')
            .add(count, 'quarter')
            .valueOf()
      }
    },
    getHeader(timeStamp, interval, userFormat) {
      let formatMoment = this.$helpers.getOrgMoment(timeStamp)
      let formatString = ''
      if (userFormat) {
        return formatMoment.format(userFormat)
      }
      switch (interval) {
        case 'DAY':
          //return "DD";
          formatString = formatMoment.format('DD ddd')
          break

        case 'WEEK':
          formatString = formatMoment.format('ww') + ' W'
          break
        //return "D ddd";
        case 'MONTH':
          formatString = formatMoment.format('MMM')
          break

        case '4H':
          formatString =
            formatMoment.format('h a') +
            ' - ' +
            this.$helpers
              .getOrgMoment(this.getNextTimeStamp(formatMoment, interval))
              .format('h a')
          break
        // return "h a";

        case '1H':
          formatString = formatMoment.format('h a')
          break
        case '.5H':
          formatString = formatMoment.format('h a')
          break

        case 'QUARTER':
          formatString = formatMoment.format('Qo') + ' Quarter'
          break
      }

      return formatString
    },
    getLabelFormat(interval) {
      switch (interval) {
        case 'DAY':
          return 'hh a'

        case 'WEEK':
          // return "ww";
          return 'DD ddd'

        case 'MONTH':
          return 'DD ddd'

        case '1H':
          return 'hh:mm a'

        case '.5H':
          return 'hh:mm a'
      }
    },
  },
}
