<script>
import FieldDetails from 'src/components/page/widget/common/field-details/HorizontalFieldDetails.vue'
import { isEmpty } from '@facilio/utils/validation'

const skipFieldsForModuleMap = {
  spacebooking: [
    'host',
    'description',
    'space',
    'internalAttendees',
    'externalAttendees',
    'bookingStartTime',
    'bookingEndTime',
    'noOfAttendees',
  ],
}
export default {
  extends: FieldDetails,
  methods: {
    deserialize(data) {
      let { moduleName } = this
      let promises = []
      let skipFields = [
        ...this.filterfields,
        ...this.primaryFields,
        ...(skipFieldsForModuleMap[moduleName] || []),
      ]

      // Fetch field values
      let fieldsArray = this.$getProperty(data, 'fields', [])

      let fields = []
      fieldsArray.forEach(field => {
        let dataObj =
          !this.isV3Api && field.field && field.field.default !== true
            ? this.details.data
            : this.details

        if (!skipFields.includes(this.$getProperty(field, 'field.name'))) {
          let promise = this.getFormattedValue(field, dataObj, this.sites).then(
            value => {
              this.$set(field, 'displayValue', value)
            }
          )
          promises.push(promise)
          fields.push(field)
        }

        if (field.name === 'childMeterExpression') {
          this.showChildMeterExp = !isEmpty(dataObj.childMeterExpression)
        }
      })

      this.fieldsList = fields

      let fieldCount =
        this.fieldsList.length -
        this.fieldsList.filter(field => field.displayTypeEnum === 'TEXTAREA')
          .length
      if (this.showChildMeterExp) {
        --fieldCount
      }

      if (fieldCount % 2 !== 0) this.additionalField = true

      Promise.all(promises).then(() => {
        this.loading = false
        this.autoResize()
      })
    },
  },
}
</script>
