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
  name: 'BookingRelatedListWidget',
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
      let { field, values } = relatedList || {}
      let fieldName = (field || {}).name
      let filterObj = {}

      if (!isEmpty(fieldName) && id) {
        filterObj[fieldName] = {
          operatorId: 36,
          value: [`${values}`],
        }
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
