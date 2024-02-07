<script>
import FormCreation from '@/base/FormCreation'
import { findRouteForModule, pageTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
export default {
  extends: FormCreation,
  mixins: [FetchViewsMixin],
  computed: {
    moduleName() {
      return this.$attrs.moduleName
    },
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.$getProperty(this.formObj, 'module.displayName')
      }
      return ''
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    afterSaveHook(response) {
      let { [this.moduleName]: data } = response
      if (!isEmpty(data)) {
        let { id } = data || {}
        this.redirectToSummary(id)
      }
    },
    redirectToList() {
      let route = findRouteForModule(this.moduleName, pageTypes.LIST)
      if (route) {
        this.$router.push({ name: route.name })
      } else {
        console.warn('Could not resolve route')
      }
    },
    async redirectToSummary(id) {
      let viewname = await this.fetchView(this.moduleName)
      let { name } = findRouteForModule(this.moduleName, pageTypes.OVERVIEW)
      if (name) {
        this.$router.push({ name, params: { viewname, id } })
      } else {
        console.warn(this.$t('common._common.could_not_resolve_route'))
      }
    },
  },
}
</script>
