import merge from 'lodash/merge'
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty } from '@facilio/utils/validation'
import { eventBus } from 'src/pages/new-dashboard/utils/eventBus.js'
import { API } from '@facilio/api'

export default {
  data() {
    return {
      hiddenWidgetsHashMap: {},
      recentTimelineFilterPayload: {},
      recentUserFilterPayload: {},
      recentActionsPayload: {},
      rawUserFilter: {},
    }
  },
  methods: {
    getDashboardTabId() {
      return !isEmpty(this.dashboardTabId)
        ? { dashboardTabId: this.dashboardTabId }
        : {}
    },
    removeTriggerWidgetId() {
      const {
        recentTimelineFilterPayload,
        recentUserFilterPayload,
        recentActionsPayload,
      } = this
      delete recentTimelineFilterPayload?.action_meta?.trigger_widget_id
      delete recentUserFilterPayload?.action_meta?.trigger_widget_id
      delete recentActionsPayload?.action_meta?.trigger_widget_id
    },
    getUserFilterReq({ filter, filterId }) {
      const { dashboardId, userFilterList } = this
      const placeHolderMeta = () => {
        return Object.keys(filter).reduce((placeHolderMeta, key) => {
          const {
            link_name: linkName,
            field,
            moduleName,
          } = userFilterList.find(userFilter => userFilter.id == key)
          placeHolderMeta[linkName] = {
            fieldName: field?.name ?? moduleName,
            moduleName: field?.module?.name ?? moduleName,
          }
          return placeHolderMeta
        }, {})
      }
      const placeHolders = () => {
        return Object.entries(filter).reduce((placeHolders, [id, filter]) => {
          const {
            link_name: linkName,
            dateTimeValues,
            selectedSliderRangeValues,
          } = userFilterList.find(userFilter => userFilter.id == id)
          if (!isEmpty(dateTimeValues) || !isEmpty(selectedSliderRangeValues)) {
            // This is done to handle `slider filter`.
            const getDateTimeValues = () => {
              // This is stupid, in the `first load` the object dateTimeValue
              // returns `undefined` for some reason, so we construct the range by ourselves.
              if (!isEmpty(dateTimeValues)) {
                return cloneDeep(dateTimeValues)
              } else {
                const [start, end] = selectedSliderRangeValues
                const preparedDateTimeValues = []
                for (let i = start; i <= end; i++) {
                  preparedDateTimeValues.push(String(i))
                }
                return preparedDateTimeValues
              }
            }
            placeHolders[linkName] = {
              value: getDateTimeValues(),
              operatorId: filter,
            }
          } else {
            // This is done to handle `multi-select` and `dropdowns`.
            placeHolders[linkName] = { value: cloneDeep(filter) }
          }
          return placeHolders
        }, {})
      }
      return {
        action_meta: {
          dashboardId: dashboardId,
          placeHolders: placeHolders(),
          placeHoldersMeta: placeHolderMeta(),
          trigger_widget_id: filterId,
          ...this.getDashboardTabId(),
        },
      }
    },
    getTimelineFilterReq(timelineFilter) {
      const { startTime, endTime, operatorId, dateValueString, dateLabel } =
        timelineFilter ?? {}
      const { dashboardId } = this ?? {}
      return {
        action_meta: {
          placeHolders: {
            startTime: startTime,
            endTime: endTime,
            dateOperator: operatorId,
            dateValueString: dateValueString,
            dateLabel: dateLabel,
          },
          dashboardId: dashboardId,
          ...this.getDashboardTabId(),
        },
      }
    },
    timelineFilterChanged(filterData) {
      this.persistDbTimelineFilter(filterData) // This is used when download the dashboard as .pdf.
      this.removeTriggerWidgetId()
      const { recentUserFilterPayload, recentActionsPayload } = this
      const timelineFilterPayload = this.getTimelineFilterReq(filterData)
      this.recentTimelineFilterPayload = cloneDeep(timelineFilterPayload)
      const req = merge(
        cloneDeep(recentActionsPayload),
        cloneDeep(recentUserFilterPayload),
        cloneDeep(timelineFilterPayload)
      )
      this.executeActions(req)
    },
    userFilterChanged(filterData) {
      const { filter } = filterData
      this.rawUserFilter = cloneDeep(filter)
      this.persistDbUserFilters(filter)
      this.removeTriggerWidgetId()
      const { recentTimelineFilterPayload, recentActionsPayload } = this
      const userFilterPayload = this.getUserFilterReq(filterData)
      this.recentUserFilterPayload = cloneDeep(userFilterPayload)
      const req = merge(
        cloneDeep(recentActionsPayload),
        cloneDeep(recentTimelineFilterPayload),
        cloneDeep(userFilterPayload)
      )
      this.executeActions(req)
    },
    async executeActions(req) {
      const self = this
      if (!isEmpty(req)) {
        const {
          data: { result: widgets },
        } = await API.post('/v3/dashboard/rule/execute', req)

        // if (isEmpty(this.hiddenWidgetsHashMap)) {
        //   widgets.push({
        //     actionType: 'hide_sections',
        //     actionMeta: {
        //       HIDE_SECTIONS: {
        //         section_ids: ['23604'],
        //       },
        //     },
        //   })
        // } else {
        //   widgets.push({
        //     actionType: 'show_sections',
        //     actionMeta: {
        //       SHOW_SECTIONS: {
        //         section_ids: ['23604'],
        //       },
        //     },
        //   })
        // }

        const { localActions, globalActions } = widgets.reduce(
          (actions, widget) => {
            const { globalActions, localActions } = actions
            const { widget_id: actionWidgetId } = widget ?? {}
            if (!isEmpty(actionWidgetId)) {
              localActions.push(widget)
            } else {
              globalActions.push(widget)
            }
            return actions
          },
          { globalActions: [], localActions: [] }
        )
        // We must first `show` the widgets, and then apply other actions on them.
        // If we don't do this the widgets will show (Mount on DOM) but they won't load because
        // the order to load the widget has already been given to the widget (THe widget doesn't exist in the DOM)...
        globalActions.forEach(widget => {
          self.executeGlobalActions(widget)
        })
        localActions.forEach(widget => {
          self.executeLocalActions(widget)
        })
      }
    },
    executeLocalActions(widget) {
      const self = this
      this.$nextTick(() => {
        // Used nextTick() to be safe, to make sure all the widgets are mounted and is present in the DOM, before sending them
        // orders via eventBus.
        // We can animate the widgets which are triggered by the actions API.
        // For example, we can make a widget vibrate when the global timeline filter
        // updates a specific widget.
        // const { widget_id: widgetId } = widget
        // self.animateWidget(widgetId)
        const id = String(widget.widget_id)
        const {
          customScriptWidgets,
          dashboardUserFilters,
        } = self.dashboardFilterObj ?? {}
        if ((customScriptWidgets ?? []).includes(widget.widget_id)) {
          Object.assign(widget.actionMeta, {
            CUSTOM_SCRIPT_FILTER: cloneDeep({
              filterValues: self.rawUserFilter,
              filterMeta: dashboardUserFilters,
            }),
          })
        }
        eventBus.$emit(`applyAction_${id}`, widget)
      })
    },
    hideWidget(widgetId) {
      const hiddenWidget = this.dashboardLayout.find(
        widget => widget.id == widgetId
      )
      if (!isEmpty(hiddenWidget)) {
        delete this.hiddenWidgetsHashMap[widgetId]
        this.hiddenWidgetsHashMap[widgetId] = cloneDeep(hiddenWidget)
        this.deleteWidget(widgetId)
      }
    },
    showWidget(widgetId) {
      const { hiddenWidgetsHashMap, dashboardLayout } = this
      const widget = cloneDeep(hiddenWidgetsHashMap[widgetId])
      delete hiddenWidgetsHashMap[widgetId]
      if (!isEmpty(widget)) {
        dashboardLayout.push(widget)
      }
    },
    executeGlobalActions(widget) {
      const self = this
      this.$nextTick(() => {
        // Used nextTick() to be safe, to make sure all the widgets are mounted and is present in the DOM, before sending them
        // orders via eventBus.
        const { actionType, actionMeta } = widget
        const { showWidget, hideWidget } = self
        const showHideSections = (sections, action) => {
          for (const section of sections) {
            action(section)
          }
        }
        if (actionType == 'hide_sections') {
          const {
            HIDE_SECTIONS: { section_ids: sections },
          } = actionMeta
          showHideSections(sections, hideWidget)
        } else if (actionType == 'show_sections') {
          const {
            SHOW_SECTIONS: { section_ids: sections },
          } = actionMeta ?? {}
          showHideSections(sections, showWidget)
        } else if (actionType == 'url') {
          const {
            URL: { url },
          } = actionMeta ?? {}
          window.open(url, '_blank')
        }
      })
    },
    onAction(action) {
      this.removeTriggerWidgetId()
      // This method handles `clicking on bar`, etc. types of actions, other than `timeline filter` and `user filter`.
      const {
        widgetLinkName,
        criteria,
        dimension,
        value,
        fieldName,
        moduleName,
        groupBy,
        groupByFieldName,
        groupByModuleName,
        id,
      } = action ?? {}
      const { dashboardId } = this ?? {}
      const placeHoldersMeta = () => {
        const placeHolderMeta = {}
        placeHolderMeta[widgetLinkName] = {
          fieldName: fieldName,
          moduleName: moduleName,
        }
        return placeHolderMeta
      }
      const placeHolders = () => {
        const placeHolders = {}
        placeHolders[widgetLinkName] = {
          dimension: [String(dimension)],
          criteria: criteria,
          group_by: groupBy,
          metrics: '',
        }
        return placeHolders
      }
      const groupByMeta = () => {
        const groupByMeta = {}
        groupByMeta[widgetLinkName] = {
          groupByFieldName,
          groupByModuleName,
        }
        return groupByMeta
      }
      // While sending the POST payload make sure that you send the applied filters such as timelineFilter and userFilter
      // as well... This data can be found in recentActionsPayload variable.
      const actionsPayload = {
        action_meta: {
          trigger_widget_id: Number(id),
          dashboardId: dashboardId,
          placeHolders: placeHolders(),
          placeHoldersMeta: placeHoldersMeta(),
          ...this.getDashboardTabId(),
        },
      }
      if (!isEmpty(groupByFieldName) && !isEmpty(groupByModuleName)) {
        actionsPayload.action_meta['groupByMeta'] = groupByMeta()
      }
      const { recentTimelineFilterPayload, recentUserFilterPayload } =
        this ?? {}
      this.recentActionsPayload = cloneDeep(actionsPayload)
      const req = merge(
        cloneDeep(recentUserFilterPayload),
        cloneDeep(recentTimelineFilterPayload),
        cloneDeep(actionsPayload)
      )
      this.executeActions(req)
    },
  },
}
