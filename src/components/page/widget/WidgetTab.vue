<template>
  <widget-card
    v-if="!$validation.isEmpty(getTabComponent())"
    class="widget-card"
  >
    <component :is="getTabComponent()" :tab="tab" v-bind="$attrs"></component>
  </widget-card>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import WidgetCard from 'pageWidgets/utils/WidgetCard'

export default {
  props: ['tab'],
  components: {
    WidgetCard,
    History: () => import('pageWidgets/common/History'),
    RuleRCA: () => import('pageWidgets/rule/RuleRCA'),
    HistoryLog: () => import('pageWidgets/rule/HistoryLog'),
    Graphics: () => import('pageWidgets/energy/Graphics'),
    AnomalyRCA: () => import('pageWidgets/anomalies/AnomalyRCA'),
    OccurrenceHistory: () => import('pageWidgets/anomalies/OccurrenceHistory'),
    ActivityWidget: () => import('pageWidgets/common/ActivityWidget'),
    RuleActionWidget: () => import('pageWidgets/rule/RuleActionWidget'),
    AnomalyMetrics: () => import('pageWidgets/anomalies/AnomalyMetrics'),
    AssetContracts: () => import('pageWidgets/common/AssetContracts'),
    kpiViolations: () => import('pages/energy/kpi/kpiViolations'),
    kpiViewer: () => import('pages/energy/kpi/ReadingKpiList'),
    kpiLog: () => import('pages/energy/kpi/kpiLogs'),
    srSummary: () =>
      import('src/pages/servicerequest/widgets/SRSummaryTabContainer'),
    AlarmRca: () =>
      import('pages/alarm/rule-creation/component/ReadingRuleRcaReadings.vue'),
    ConnectedAppWidget: () => import('pageWidgets/common/ConnectedAppWidget'),
  },
  methods: {
    getTabComponent() {
      let { tab } = this

      let componentHash = {
        history: 'History',
        commissioningLog: 'CommissioningLog',
        root_cause_impact: 'RuleRCA',
        history_log: 'HistoryLog',
        graphics: 'Graphics',
        anomalyRCA: 'AnomalyRCA',
        occurrenceHistory: 'OccurrenceHistory',
        activity: 'ActivityWidget',
        actions: 'RuleActionWidget',
        anomalyMetrics: 'AnomalyMetrics',
        contracts: 'AssetContracts',
        violationsList: 'kpiViolations',
        kpiViewer: 'kpiViewer',
        formulaLog: 'kpiLog',
        alarmRca: 'AlarmRca',
        connectedapp_widget: 'ConnectedAppWidget',
        serviceRequestDetails: 'srSummary',
      }
      return !isEmpty(tab.component) ? componentHash[tab.component] : null
    },
  },
}
</script>
