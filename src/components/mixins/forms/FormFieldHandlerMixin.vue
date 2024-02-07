<script>
import { isEmpty, isObject, isNull } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  methods: {
    siteFieldHandler(props) {
      let { field, currentSiteId } = props
      // To set globally selected site scope
      if (!isEmpty(currentSiteId)) {
        field.value = currentSiteId
        this.onChangeHandler({ field })
      }
    },
    async lookupFieldHandler(props) {
      let {
        field,
        isEdit,
        preFillValueForCreationObj,
        isResourceField,
        isLookupMulti,
        isLookupField,
      } = props
      let { value, name, lookupModuleName } = field
      let label
      let dataValue

      field.multiple = isLookupMulti

      if (isLookupField) {
        // For old lookupfields, this `canShowLookupWizard` will not be available to skip migration, we're doing this
        let { config } = field || {}
        if (!isEmpty(config)) {
          let canShowLookupWizard = this.$getProperty(
            config,
            'canShowLookupWizard',
            null
          )
          if (isNull(canShowLookupWizard)) {
            field.config.canShowLookupWizard = true
          }
        } else {
          field.config = {}
          field.config.canShowLookupWizard = true
        }
      }

      if (isResourceField) {
        // To prefill value by extracting it from url
        if (!isEdit) {
          if (!isEmpty(preFillValueForCreationObj[`${name}`])) {
            dataValue = Number(preFillValueForCreationObj[`${name}`])
          }
        }
        if (isEmpty(dataValue) && !isEmpty(value)) {
          /*
          To prefill default value configured in form builder
          To prefill values in edit case
        */
          let { id } = value
          dataValue = id ? Number(id) : id
        }

        if (!isEmpty(dataValue)) {
          label = await this.getResourceLabel({
            lookupModuleName,
            selectedOptionId: dataValue,
          })

          this.$set(field, 'value', { id: dataValue })
          this.setupInitialResourceObj({
            label,
            dataValue,
          })
        }
      }
    },
    numberDecimalFieldHandler(props) {
      let { field } = props
      let { field: fieldObj } = field
      if (!isEmpty(fieldObj)) {
        let { metric, unitId } = fieldObj
        if (metric !== -1) {
          field.metric = metric
        }
        if (unitId !== -1) {
          field.unitId = unitId
        }
      }
    },
    durationFieldHandler(props) {
      let { field } = props
      let { value } = field
      if (isEmpty(value)) {
        this.$set(field, 'selectedUnit', 'days')
      }
    },
    async getResourceLabel(props) {
      let { lookupModuleName, selectedOptionId } = props
      let labelMeta = {}
      labelMeta[lookupModuleName] = [selectedOptionId]
      let { data, error } = await API.post('/v2/picklist/label', { labelMeta })
      if (error) {
        return { error }
      } else {
        let { label } = data || {}
        let [selectedOption] = label[lookupModuleName] || []
        return (selectedOption || {}).label
      }
    },
    setupInitialResourceObj(props) {
      let { spaceAssetResourceObj } = this
      let { label, dataValue } = props
      // spaceAssetResourceObj used in task list component to set initial resource value for new task created.
      if (isObject(spaceAssetResourceObj)) {
        this.spaceAssetResourceObj = {
          name: label,
          id: dataValue,
        }
      }
    },
  },
}
</script>
