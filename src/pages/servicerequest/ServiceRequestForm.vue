<script>
import FormCreation from '@/base/FormCreation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  extends: FormCreation,
  mixins: [FetchViewsMixin],
  computed: {
    moduleName() {
      return 'serviceRequest'
    },
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.formObj.module.displayName
      }
      return ''
    },
    isV3Api() {
      return true
    },
    isWidgetsSupported() {
      return true
    },
  },
  methods: {
    afterSerializeHook({ data }) {
      data = {
        ...data,
        sourceType: 1,
      }
      return data
    },
    afterSaveHook(data) {
      let { error } = data || {}
      if (!error) {
        this.redirectToSummary(data)
      }
    },
    async redirectToSummary(data) {
      let { moduleName } = this
      let { serviceRequest } = data || {}
      let { id } = serviceRequest || {}
      let viewname = await this.fetchView(moduleName)
      let params = { viewname, id }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        name && this.$router.push({ name, params })
      } else {
        this.$router.push({
          name: 'serviceRequestSummary',
          params,
        })
      }
    },
    async redirectToList() {
      let { moduleName } = this
      let viewname = await this.fetchView(moduleName)
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

        if (name) {
          this.$router.push({ name })
        }
      } else {
        this.$router.push({
          name: 'serviceRequestList',
          params: { viewname },
        })
      }
    },
  },
}
</script>
