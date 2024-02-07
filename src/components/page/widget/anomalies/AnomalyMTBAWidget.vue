<template>
  <metrics-widget
    :details="details"
    :isMttc="false"
    :moduleName="moduleName"
    title="Mean Time Between Occurrences"
    cardId="3"
  ></metrics-widget>
</template>
<script>
import MetricsWidget from './AnomalyMeanMetricWidget'
export default {
  components: { MetricsWidget },
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'activeTab',
    'moduleName',
    'widget',
    'resizeWidget',
    'eventBus',
  ],
}
</script>
