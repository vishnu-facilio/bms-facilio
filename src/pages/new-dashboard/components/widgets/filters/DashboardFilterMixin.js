import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { deepCloneObject } from 'util/utility-methods'
import { getUserFilterModel, generateFilterJSON } from './DashboardFilterHelper'
import NewDateHelper from '@/mixins/NewDateHelper'
import { mapActions } from 'vuex'
import cloneDeep from 'lodash/cloneDeep'

export default {
  data() {
    return {
      showDashboardFilterWidgetConfigDialog: false,
      filterEditMode: false,
      hideFilterInsideWidgets: false,
      widgetForFilterConfig: null,
      //main state for db filters->used in desktop and mobile db viewer
      dashboardFilterObj: null,
      dbUserFilters: {}, //'widgetId':filterJson
      dbTimelineFilter: {}, //'widgetId':{startTime:123,endTime...}
      dbCustomScriptFilter: {}, //widgetId:{filterMeta,filterValues}
    }
  },
  methods: {
    ...mapActions({
      persistDbTimelineFilter: 'dashboard/persistDbTimelineFilter',
      persistDbUserFilters: 'dashboard/persistDbUserFilters',
    }),
    clearDashboardFilters() {
      this.dbUserFilters = {}
      this.dbTimelineFilter = {}
      this.dashboardFilterObj = null
      this.persistDbTimelineFilter(null)
      this.persistDbUserFilters(null)
    },
    initializeDashboardFilters(dashboardFilter) {
      if (dashboardFilter) {
        this.dashboardFilterObj = dashboardFilter

        let urlDbFilterString = this.$route.query.dbFilters
        let urlDbFilters = null

        if (urlDbFilterString) {
          urlDbFilters = JSON.parse(urlDbFilterString)
        }
        let urlUserFilters = null
        let urlTimelineFilter = null
        if (urlDbFilters) {
          urlUserFilters = urlDbFilters.userFilters
          urlTimelineFilter = urlDbFilters.timelineFilter
        }

        let {
          dashboardUserFilters,
          widgetUserFiltersMap,
          isTimelineFilterEnabled,
          hideFilterInsideWidgets,
          dateOperator,
          dateValue: dateValueString, //"12313",213123"// timestamp string
          dateLabel,
          startTime,
          endTime,
        } = this.dashboardFilterObj

        const self = this
        if (dashboardUserFilters) {
          //override defaults using values from url params
          if (urlUserFilters) {
            dashboardUserFilters.forEach(dbUserFilter => {
              let values = urlUserFilters[dbUserFilter.id]
              if (!isEmpty(values)) {
                self.$set(dbUserFilter, 'defaultValues', values)
              }
            })
          }

          //get filterModel in the form {userFilterId:[defaulValues]}
          let filterModel = getUserFilterModel(dashboardUserFilters)
          this.rawUserFilter = cloneDeep(filterModel)
          this.dbUserFilters = generateFilterJSON(
            filterModel,
            dashboardUserFilters,
            widgetUserFiltersMap
          )

          this.setCustomScriptFilters(filterModel)
          this.showDashboardFilterBar = true
        }

        if (isTimelineFilterEnabled) {
          if (!isEmpty(urlTimelineFilter)) {
            dateOperator = urlTimelineFilter.operatorId
            if (urlTimelineFilter.startTime && urlTimelineFilter.endTime) {
              dateValueString =
                urlTimelineFilter.startTime + ',' + urlTimelineFilter.endTime
            }
            if (urlTimelineFilter.dateLabel) {
              dateLabel = urlTimelineFilter.dateLabel
            }
          }
          let pickerObj = NewDateHelper.getDatePickerObject(
            dateOperator,
            dateValueString
          )
          //pickerObj always has start and end times,

          let { value, operatorId } = pickerObj
          // value is a `array-like` object not an
          // array, can only destructure iterables... Look into `HTMLCollections`.
          const [startTime, endTime] = Array.from(value)
          //set initial state of filters
          this.dbTimelineFilter = {
            startTime,
            endTime,
            dateLabel,
            dateValueString: startTime + ',' + endTime,
            operatorId,
          }
          //dateValueString,operatorId used to get picker instance wherever required
          //startTime,endTime,dateLabel sent in api calls
        }
      }

      /* ********* */
    },
    setCustomScriptFilters(filterModel) {
      let newdbCustomScriptFilter = {}
      for (const widgetId of this.dashboardFilterObj.customScriptWidgets) {
        newdbCustomScriptFilter[widgetId] = {
          filterValues: filterModel,
          filterMeta: this.dashboardFilterObj.dashboardUserFilters,
        }
      }
      this.$set(
        this,
        'dbCustomScriptFilter',
        deepCloneObject(newdbCustomScriptFilter)
      )
    },
    editWidgetFilterConfig(widget) {
      this.widgetForFilterConfig = widget
      this.showDashboardFilterWidgetConfigDialog = true
    },
    saveWidgetFilterSettings(widget) {
      console.log('saving widget settings', widget.id, widget.widgetSettings)
      this.showDashboardFilterWidgetConfigDialog = false
      let data = {
        widgets: [
          {
            id: widget.id,
            widgetSettingsJsonString: JSON.stringify(widget.widgetSettings),
          },
        ],
      }
      API.post('v2/dashboardFilter/updateWidgetSettings', data).then(resp => {
        let { data, error } = resp
        if (error) {
          this.$message('error updating widget-filter settings')
          console.error(error)
        } else {
          //saved widget settings, change state in dashboard viewer
          this.widgetForFilterConfig.widgetSettings = widget.widgetSettings
        }
      })
    },
  },
}
