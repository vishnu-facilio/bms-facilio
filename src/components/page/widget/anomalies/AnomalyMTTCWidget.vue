<template>
  <metrics-widget
    :details="details"
    :isMttc="true"
    :moduleName="moduleName"
    title="Mean Time To Clear"
    cardId="2"
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
    'widget',
    'moduleName',
    'resizeWidget',
    'eventBus',
  ],
}
</script>
