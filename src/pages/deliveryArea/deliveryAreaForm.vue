<script>
import FormCreation from '@/base/FormCreation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: FormCreation,
  data() {
    return {
      isSaving: false,
    }
  },
  computed: {
    formDisplayName() {
      return (this.formObj || {}).displayName
    },
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.formObj.module.displayName
      }
      return ''
    },
    moduleName() {
      return 'deliveryArea'
    },
  },
  methods: {
    afterSaveHook({ error }) {
      if (!error) this.redirectToList()
    },
    redirectToList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
          })
      } else {
        this.$router.push({
          name: 'deliveryAreaList',
        })
      }
    },
    redirectToSummary(data) {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id: data.id,
              viewname: 'all',
            },
          })
      } else {
        this.$router.push({
          name: 'deliveryAreaOverview',
          params: { id: data.id, viewname: 'all' },
        })
      }
    },
  },
}
</script>
