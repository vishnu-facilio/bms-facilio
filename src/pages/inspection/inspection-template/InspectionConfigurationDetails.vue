<script>
import InspectionFieldDetails from './InspectionFieldDetails'
export default {
  extends: InspectionFieldDetails,
  name: 'InspectionConfigurationDetails',
  computed: {
    title() {
      return 'Configuration Details'
    },
    requiredFields() {
      let fields = this.$getProperty(this, 'widget.widgetParams.fields')
      return fields
    },
  },
}
</script>
