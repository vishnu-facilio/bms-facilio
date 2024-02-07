<template>
  <component
    :is="detailsComponent"
    :details="details"
    :moduleName="moduleName"
    :primaryFields="primaryFields"
    :widget="widget"
    :layoutParams="layoutParams"
    :isV3Api="isV3Api"
    :resizeWidget="resizeWidget"
    :calculateDimensions="calculateDimensions"
  />
</template>
<script>
import HorizontalFieldDetails from './HorizontalFieldDetails.vue'
import VerticalFieldDetails from './VerticalFieldDetails.vue'

export default {
  name: 'FieldDetailsLayout',
  components: { HorizontalFieldDetails, VerticalFieldDetails },
  computed: {
    layoutParams() {
      let { $attrs } = this
      let { layoutParams } = $attrs || {}

      return layoutParams
    },
    //left-right view or top-down view
    detailsComponent() {
      let { widget } = this
      let { widgetParams } = widget || {}
      let { labelPosition } = widgetParams || {}

      if (labelPosition === 'top') {
        return 'VerticalFieldDetails'
      }
      return 'HorizontalFieldDetails'
    },
    details() {
      let { $attrs } = this
      let { details } = $attrs || {}

      return details
    },
    moduleName() {
      let { $attrs } = this
      let { moduleName } = $attrs || {}

      return moduleName
    },
    primaryFields() {
      let { $attrs } = this
      let { primaryFields = [] } = $attrs || {}

      return primaryFields
    },
    widget() {
      let { $attrs } = this
      let { widget } = $attrs || {}

      return widget
    },

    isV3Api() {
      let { $attrs } = this
      let { isV3Api } = $attrs || {}

      return isV3Api
    },
  },
  methods: {
    resizeWidget(params) {
      let { $attrs } = this
      return $attrs.resizeWidget(params)
    },
    calculateDimensions(params) {
      let { $attrs } = this
      return $attrs.calculateDimensions(params)
    },
  },
}
</script>
