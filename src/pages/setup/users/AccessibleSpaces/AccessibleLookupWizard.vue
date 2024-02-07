<script>
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { API } from '@facilio/api'
import { isEmpty, isNullOrUndefined } from '@facilio/utils/validation'
export default {
  name: 'AccessibleLookupWizard',
  extends: FLookupFieldWizard,
  props: ['summaryId'],
  computed: {
    title() {
      let { moduleDisplayName } = this
      return `${this.$t('common.header.add_new')} ${moduleDisplayName}`
    },
  },
  methods: {
    async fetchModulesData(skipResetPage = false) {
      let {
        moduleName,
        filters = {},
        perPage,
        hidePaginationSearch,
        summaryId,
        page,
        withReadings = null,
        selectedLookupField,
      } = this
      let { clientCriteria, additionalParams } = selectedLookupField || {}
      let { list, meta, error } = await API.fetchAll(moduleName, {
        excludeAccessibleSpace: true,
        ouid: summaryId,
        withCount: true,
        page: !isEmpty(filters) && !skipResetPage ? 1 : page,
        filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
        perPage,
        hidePaginationSearch,
        viewName: 'hidden-all',
        withReadings,
        clientCriteria: !isEmpty(clientCriteria)
          ? JSON.stringify(clientCriteria)
          : null,
        ...additionalParams,
      })
      let totalCount = this.$getProperty(meta, 'pagination.totalCount', null)

      if (!error) {
        if (!isNullOrUndefined(list)) {
          this.$set(this, 'modulesList', list)
        }
        if (!isNullOrUndefined(totalCount)) {
          this.$set(this, 'totalCount', totalCount)
        }
      } else {
        this.$message.error(error.message)
      }
      this.isSearchDataLoading = false
    },
    loadDataCount() {},
  },
}
</script>
