<template>
  <ImpactWidget
    :moduleName="moduleName"
    :details="details"
    :layoutParams="layoutParams"
    :resizeWidget="resizeWidget"
    :primaryFields="primaryFields"
    :isCostImpact="false"
  />
</template>
<script>
import ImpactWidget from './ImpactWidget'
export default {
  components: {
    ImpactWidget,
  },
  props: [
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'primaryFields',
  ],
}
</script>
