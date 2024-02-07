<template>
  <FieldDetails
    :details="details"
    :moduleName="moduleName"
    :primaryFields="primaryFields"
    :widget="widget"
    :layoutParams="layoutParams"
    :isV3Api="isV3Api"
    :resizeWidget="resizeWidget"
    :calculateDimensions="calculateDimensions"
  >
  </FieldDetails>
</template>
<script>
import FieldDetails from 'src/components/SpaceBookingFieldDetails.vue'

export default {
  name: 'SpaceBookingSecondaryDetails',
  components: { FieldDetails },
  computed: {
    layoutParams() {
      let { $attrs } = this
      let { layoutParams } = $attrs || {}

      return layoutParams
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
