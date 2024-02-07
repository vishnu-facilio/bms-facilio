<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'

export default {
  computed: {
    ...mapState('search', {
      fields: state => state.searchableFields,
    }),
    appliedFilters() {
      let { $route, hideQuery, filterList } = this
      let {
        query: { search },
      } = $route || {}
      if (hideQuery) {
        return filterList
      } else {
        if (!isEmpty(search)) {
          return JSON.parse(this.$route.query.search)
        }
      }
      return null
    },
  },
  methods: {
    isLookupField(field) {
      let { displayType } = field || {}
      return ['LOOKUP_SIMPLE'].includes(displayType)
    },
    isLookupPopupField(field) {
      let { displayType } = field || {}
      return ['LOOKUP_POPUP'].includes(displayType)
    },
    isMultiLookupField(field) {
      let { displayType } = field || {}
      return ['MULTI_LOOKUP_SIMPLE'].includes(displayType)
    },
  },
}
</script>
