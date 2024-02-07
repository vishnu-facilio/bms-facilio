<template>
  <div>
    <SetReadingPopup
      v-if="showControlActionPopup && control.assetId"
      :saveAction="closePopup"
      :closeAction="closePopup"
      :recordId="control.assetId"
      :fieldId="control.fieldId"
      :groupId="control.controlGroupId"
      :recordName="''"
    ></SetReadingPopup>
    <SetReadingPopup
      v-else-if="showControlActionPopup && control.controlPointId"
      :saveAction="closePopup"
      :closeAction="closePopup"
      :pointId="control.controlPointId"
      :groupId="control.controlGroupId"
      :recordName="''"
    ></SetReadingPopup>
    <CardTrend
      :visible.sync="showTrendPopup"
      :closeAction="closeTrendPop"
      :cardComponent="trend"
      v-if="showTrendPopup"
    ></CardTrend>
    <UrlTarget
      v-if="showUrl"
      :visible.sync="showUrl"
      :closeAction="close"
      :urlAction="urlAction"
    ></UrlTarget>
    <CardListView
      v-if="showListView"
      :visible.sync="showListView"
      :closeAction="closeListView"
      :data="listView"
    ></CardListView>
  </div>
</template>
<script>
import { cloneDeep } from 'lodash'
import { isEmpty } from '@facilio/utils/validation'
import SetReadingPopup from '@/readings/SetReadingValue'
import CardTrend from 'pages/card-builder/components/CardTrend'
import UrlTarget from 'pages/card-builder/components/URLActionTarget'
import CardListView from 'pages/card-builder/components/CardListView'
import { dateOperators } from 'pages/card-builder/card-constants'
import CardHelpers from 'pages/card-builder/card-helpers'
import { deepCloneObject } from 'util/utility-methods'
export default {
  data() {
    return {
      useDashboardFilters: false,
      allFilters: {},
      showControlActionPopup: false,
      showTrendPopup: false,
      showListView: false,
      showUrl: false,
      control: null,
      trend: null,
      urlAction: null,
      listView: null,
      dateOperators,
      reportActionData: null,
      showReport: false,
    }
  },
  mixins: [CardHelpers],
  components: { SetReadingPopup, CardTrend, UrlTarget, CardListView },
  methods: {
    executeAction(action, cardComponent, allFilters, useDashboardFilters) {
      if (isEmpty(action)) return
      else {
        let { actionType, data } = action

        if (actionType === 'controlAction') {
          this.control = data
          this.showControlActionPopup = true
        } else if (actionType === 'hyperLink') {
          // Handling for opening popup or redirecting page
          if (this.$mobile) {
            this.executeMobileAction(action, cardComponent)
          } else {
            if (this.showUrl) {
              this.showUrl = false
              setTimeout(() => {
                this.showUrl = true
              }, 250)
            } else {
              this.showUrl = true
            }
            this.urlAction = data
          }
        } else if (actionType === 'showTrend') {
          if (this.$mobile) {
            this.executeMobileAction(action, cardComponent)
          } else {
            this.trend = cardComponent
            this.showTrendPopup = true
          }
          // Handling for opening reports
        } else if (actionType === 'showListView') {
          if (this.$mobile) {
            this.executeMobileAction(action, cardComponent)
          } else {
            // Handling for opening reports
            this.listView = this.getListViewdata(cardComponent, action)
            this.showListView = true
          }
        } else if (actionType === 'showReport') {
          if (this.$mobile) {
            // mobile handling
          } else {
            // Handling for opening reports
            this.reportActionData = this.getReportActionData(
              cardComponent,
              action
            )
            this.allFilters = allFilters
            this.useDashboardFilters = useDashboardFilters
            this.showReport = true
            this.popUpReport()
          }
        } else if (actionType === 'function') {
          if (this.$mobile) {
            this.executeMobileAction(action, cardComponent)
          }
          // API call for executing function
        } else if (actionType === 'none') {
          if (this.$mobile) {
            this.executeMobileAction(action, cardComponent)
          }
          // API call for executing function
        }
      }
    },
    popUpReport() {
      let { reportId, report, qs } = this.reportActionData.actionData
      let params = {}
      if (this.useDashboardFilters) {
        const { dbFilterJson, dbTimelineFilter } = this.allFilters ?? {}
        if (!isEmpty(dbFilterJson)) {
          params['dbFilterJson'] = dbFilterJson
        }
        if (!isEmpty(dbTimelineFilter)) {
          const {
            startTime,
            endTime,
            dateOperator,
            operatorId,
            dateLabel,
            dateField,
          } = dbTimelineFilter ?? {}
          params['dbTimelineFilter'] = {
            startTime: startTime,
            endTime: endTime,
            operatorId: operatorId ?? dateOperator,
            dateLabel: dateLabel,
            dateValueString: `${startTime},${endTime}`,
            dateField: dateField,
          }
        }
      }
      params['type'] = 'report'
      params['url'] = ''
      params['alt'] = ''
      params['dashboardId'] = ''
      params['reportId'] = reportId
      params['newReport'] = report
      params['qs'] = qs
      params['target'] = ''
      this.$popupView.openPopup(params)
    },
    getReportActionData(cardComponent, action) {
      let { data } = action
      let tempData = deepCloneObject(data)
      if (cardComponent.report) {
        this.$set(tempData, 'report', cardComponent.report)
      }
      if (cardComponent.cardData.variables && action.data.parameter) {
        this.$set(
          tempData,
          'qs',
          this.extractPlaceHolder(
            action.data.parameter,
            cardComponent.cardData.variables
          )
        )
      }
      return {
        actionData: tempData,
      }
    },
    extractPlaceHolder(string, filedlist) {
      if (string && filedlist) {
        filedlist.push({ name: 'currentTime', value: new Date().valueOf() })
        if (string && string.trim() !== '' && filedlist) {
          filedlist.forEach(field => {
            string = string.replace('${' + field.name + '}', field.value)
          })
        }
      }
      return string
    },
    executeMobileAction(action, cardComponent) {
      let { actionType, data } = action
      let { cardParams } = cardComponent
      if (actionType === 'controlAction') {
        // ..
      } else if (actionType === 'hyperLink') {
        let drillDowndata = {
          path: data.url ? data.url : null,
          type: 'url',
        }
        this.$helpers.sendToMobile(drillDowndata)
      } else if (actionType === 'showTrend') {
        // ..
      } else if (actionType === 'showListView') {
        // Handling for opening reports
        let listView = this.getListViewdata(cardComponent, action)
        let { kpi } = listView
        let { criteria } = kpi
        let drillDowndata = {
          moduleName: kpi.moduleName,
          viewName: 'all',
          viewDisplayName: 'All',
          type: 'view',
        }
        if (criteria) {
          drillDowndata.query = {
            filter: this.$helpers.criteriaToFilters(criteria),
          }
          if (cardParams && cardParams.dateRange) {
            let operatorId = this.dateOperators.find(
              rt => rt.value === cardParams.dateRange
            ).enumValue
            if (!isEmpty(kpi.dateFieldId) && kpi.dateFieldId > 0) {
              this.$set(drillDowndata.query.filter, kpi.dateFieldName, {
                operatorId: operatorId,
              })
            }
          }
        }
        this.$helpers.sendToMobile(drillDowndata)
      } else if (actionType === 'function') {
        // API call for executing function
      } else if (actionType === 'none') {
        // API call for executing function
      }
      return
    },
    getListViewdata(cardComponent, action) {
      let actiondata = {
        kpiId: null,
        criteria: null,
      }
      let { data } = action
      let { kpi } = cardComponent.cardParams
      let { cardData, cardParams } = cardComponent
      let kpiObj = null
      if (cardData && cardData.value && cardData.value.kpi) {
        kpiObj = cardData.value.kpi
      }
      this.generateProperFieldNameInCriteria(kpiObj)
      actiondata.kpiId = kpi.kpiId
      if (!isEmpty(data)) {
        this.$set(actiondata, 'kpi', kpiObj)
        let { target, view } = data
        if (view) {
          this.$set(actiondata, 'view', view)
        } else {
          this.$set(actiondata, 'view', 'all')
        }
        if (target) {
          this.$set(actiondata, 'target', target)
        } else {
          this.$set(actiondata, 'target', 'popup')
        }
      } else {
        this.$set(actiondata, 'kpi', kpiObj)
        this.$set(actiondata, 'target', 'popup')
      }
      this.$set(actiondata, 'title', kpiObj.name)
      this.$set(actiondata, 'cardParams', cardComponent.cardParams)

      this.$set(actiondata, 'cardUserFilters', cardComponent.cardUserFilters)

      this.$set(actiondata, 'cardFilters', cardComponent.cardFilters)

      return cloneDeep(actiondata)
    },
    generateProperFieldNameInCriteria(kpiObj) {
      if (
        kpiObj?.criteria?.conditions &&
        Object.keys(kpiObj.criteria.conditions).length > 0
      ) {
        let conditions = kpiObj.criteria.conditions
        for (let key in conditions) {
          let operatorIds = [90, 35]
          let condition = conditions[key] || {}
          let { operatorId, fieldName } = condition
          if (operatorIds.includes(operatorId)) {
            if (fieldName) {
              let field_name_arr = fieldName.split('.')
              if (
                field_name_arr &&
                field_name_arr.length == 2 &&
                field_name_arr[1]
              ) {
                condition.fieldName = field_name_arr[1]
              }
            }
          }
        }
      }
    },
    closeListView() {
      this.listView = null
      this.showListView = false
    },
    closePopup() {
      this.showControlActionPopup = false
    },
    closeTrendPop() {
      this.showTrendPopup = false
    },
    close() {
      this.showUrl = false
    },
    closeReport() {
      this.showReport = false
    },
  },
}
</script>
