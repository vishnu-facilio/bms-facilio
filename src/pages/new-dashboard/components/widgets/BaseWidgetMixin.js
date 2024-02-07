import { eventBus } from 'src/pages/new-dashboard/utils/eventBus.js'
import { isEmpty } from '@facilio/utils/validation'
import { getApp } from '@facilio/router'
import { cloneDeep, isObject } from 'lodash'
export default {
  data() {
    return {
      dbCustomScriptFilter: {},
    }
  },
  computed: {
    isPortalApp() {
      let {
        appCategory: { PORTALS },
      } = this.$constants
      if (getApp() && getApp().appCategory === PORTALS) {
        return true
      }
      return false
    },
  },
  mounted() {
    this.subscribeEventBus()
  },
  beforeDestory() {
    this.unsubscribeEventBus()
  },
  methods: {
    unsubscribeEventBus() {
      const id = String(this?.item?.id ?? this?.id) // Both user filters and dashboard widgets are widgets.
      eventBus.$off(`applyAction_${id}`)
    },
    subscribeEventBus() {
      this.unsubscribeEventBus()
      const id = String(this?.item?.id ?? this?.id)
      eventBus.$on(`applyAction_${id}`, this.applyAction)
    },
    updateEditDropDown(items) {
      eventBus.$emit('updateEditDropDown', { id: this.item.id, items })
    },
    updateViewDropDown(items) {
      eventBus.$emit('updateViewDropDown', { id: this.item.id, items })
    },
    initWidget(widgetConfig) {
      eventBus.$emit('initWidget', widgetConfig)
    },
    onAction(action) {
      eventBus.$emit('onAction', action)
    },
    applyAction(data) {
      const { widget_id: actionWidgetId } = data ?? {}
      const { actionMeta: actions } = data ?? {}
      const currentWidgetId = this?.item?.id ?? this?.id
      const self = this
      if (currentWidgetId == actionWidgetId) {
        if (!isEmpty(actions)) {
          Object.keys(actions).forEach(key => {
            if (key == 'TIMELINE_FILTER') {
              const { TIMELINE_FILTER: timelineFilter } = actions
              if (!isEmpty(timelineFilter)) {
                self.dbTimelineFilter = cloneDeep(timelineFilter)
              }
            } else if (key == 'USER_FILTER') {
              const { USER_FILTER: userFilter } = actions
              if (isObject(userFilter)) {
                self.dbFilterJson = cloneDeep(userFilter)
              }
            } else if (key == 'FILTER') {
              const {
                FILTER: { criteria, ruleInfo, datapoint },
              } = actions
              const { trigger_widget_criteria: triggerWidgetCriteria } =
                ruleInfo ?? {}
              self.ruleInfo = {
                criteria: criteria,
                trigger_widget_criteria: triggerWidgetCriteria,
                datapointList: datapoint,
              }
            } else if (key == 'card_parent_id') {
              self.cardParentId = actions[key]
            } else if (key == 'CUSTOM_SCRIPT_FILTER') {
              self.dbCustomScriptFilter = actions[key]
            }
          })
        } else {
          self.dbFilterJson = {}
          self.dbTimelineFilter = {}
          self.dbCustomScriptFilter = {}
        }
      }
    },
  },
}
