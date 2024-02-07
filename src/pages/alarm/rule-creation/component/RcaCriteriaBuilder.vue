<script>
import { CriteriaBuilder } from '@facilio/criteria'
import { API } from '@facilio/api'
import { isLookupPopupField, isLookupTypeField } from '../field-display-util'
export default {
  name: 'RcaCriteriaBuilder',
  extends: CriteriaBuilder,
  methods: {
    async loadFields() {
      let { moduleName, getField } = this
      let url = `/v2/filter/advanced/fields/${moduleName}`
      let response = await API.get(url)
      let { data: advancedData, error: advancedFieldsError } = response
      let { fields: advancedFieldsList } = advancedData
      let advancedFields = advancedFieldsList.map(field => ({
        ...field,
        multiple: isLookupPopupField(field) || isLookupTypeField(field),
      }))
      if (advancedFieldsError) {
        let { message = 'Error occured' } = advancedFieldsError || {}
        this.$message.error(message)
      } else {
        let fields = advancedFieldsList.reduce((acc, field) => {
          return [
            ...acc,
            {
              ...field,
              name: `${field.name}#parent`,
              multiple: isLookupPopupField(field) || isLookupTypeField(field),
            },
          ]
        }, [])
        let computedFields = []
        computedFields.push(
          getField('NUMBER', 'Duration', 'NUMBER', 'duration#parent'),
          getField('NUMBER', 'Event Count', 'NUMBER', 'noOfEvents#parent'),
          getField(
            'NUMBER',
            'Occurrence Count',
            'NUMBER',
            'noOfOccurrences#parent'
          )
        )
        let defaultFields = [
          'severity#parent',
          'faultType#parent',
          'readingAlarmAssetCategory#parent',
          'costImpact#parent',
          'energyImpact#parent',
        ]
        fields = (fields || []).filter(field => {
          return defaultFields.includes(field.name)
        })
        advancedFields = (advancedFields || []).filter(field => {
          return defaultFields.includes(`${field.name}` + '#parent')
        })
        let computedAdvancedFields = computedFields.reduce((acc, field) => {
          return [
            ...acc,
            {
              ...field,
              name: field.name.split('#')[0],
            },
          ]
        }, [])
        this.$setProperty(this, 'fields', [...fields, ...computedFields])
        this.$setProperty(this, 'advancedFields', [
          ...advancedFields,
          ...computedAdvancedFields,
        ])
      }
    },
    getField(dataType, displayName, displayType, name) {
      return {
        dataType,
        default: true,
        displayName,
        displayType,
        mainField: false,
        name,
      }
    },
  },
}
</script>
