<template>
  <FieldDetails
    :details="visitorDetails"
    :moduleName="moduleName"
    :primaryFields="primaryFields"
    :widget="widgetData"
    :layoutParams="layout"
    :isV3Api="isV3Api"
    :calculateDimensions="null"
  >
  </FieldDetails>
</template>
<script>
import FieldDetails from 'src/pages/visitors/visitor/VisitorFieldDetails.vue'

export default {
  props: ['visitorDetails'],
  name: 'VisitorSecondaryDetails',
  components: { FieldDetails },
  data() {
    return {
      dimensions: { h: 10 },
      widgetData: {
        widgetParams: {
          orderType: 1,
        },
        title: '',
      },
      layout: {
        h: 7,
        w: 24,
        x: 0,
        y: 0,
      },
    }
  },
  computed: {
    moduleName() {
      return 'visitor'
    },
    primaryFields() {
      return []
    },
    isV3Api() {
      return true
    },
  },
}
</script>
