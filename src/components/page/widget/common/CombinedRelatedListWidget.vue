<script>
import RelatedListWidget from '@/page/widget/common/RelatedListWidget'
import { isEmpty } from '@facilio/utils/validation'
import Constants from 'util/constant'

const skipSiteIdModules = [
  'vendors',
  'safetyPlan',
  'people',
  'vendorcontact',
  'service',
  'client',
]

export default {
  name: 'CombinedRelatedListWidget',
  extends: RelatedListWidget,
  props: [
    'details',
    'widget',
    'resizeWidget',
    'calculateDimensions',
    'layoutParams',
  ],
  computed: {
    filters() {
      let { mainField, searchText, details, widget, siteId, moduleName } = this

      let { id } = details
      let { relatedList } = widget || {}
      let { fields } = relatedList || {}
      let filterObj = {}
      let orFilters = []
      let filterFieldName = null
      for (let field of fields) {
        let fieldName = (field || {}).name
        if (!isEmpty(fieldName) && id) {
          if (isEmpty(filterObj)) {
            filterFieldName = fieldName
            filterObj[fieldName] = {
              operatorId: 36,
              value: [`${id}`],
            }
          } else {
            orFilters.push({ field: `${fieldName}` })
          }
        }
      }
      if (filterFieldName && !isEmpty(filterObj) && !isEmpty(orFilters)) {
        filterObj[filterFieldName]['orFilters'] = orFilters
      }
      if (!isEmpty(siteId) && !skipSiteIdModules.includes(moduleName)) {
        filterObj.siteId = {
          operatorId: 36,
          value: [`${siteId}`],
        }
      }

      if (
        !isEmpty(mainField) &&
        !isEmpty(searchText) &&
        searchText.length > 0
      ) {
        let { name, field } = mainField
        let { dataTypeEnum } = field
        let value = [searchText]
        let operatorId = Constants.FILTER_OPERATORID_HASH[dataTypeEnum]
        filterObj[name] = {
          operatorId,
          value,
        }
      }

      return filterObj
    },
  },
}
</script>
