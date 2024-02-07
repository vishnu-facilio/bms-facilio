<script>
import FormCreation from '@/base/FormCreation'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  extends: FormCreation,
  title() {
    return 'Audiences'
  },
  computed: {
    moduleName() {
      return 'audience'
    },
    moduleDisplayName() {
      return 'Audience'
    },
    isV3Api() {
      return true
    },
    isWidgetsSupported() {
      return true
    },
  },
  methods: {
    afterSaveHook(response) {
      let { error, [this.moduleName]: record } = response || {}

      if (!error) this.redirectToOverview(record)
    },
    redirectToOverview({ id }) {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({ name, params: { viewname: 'all', id } })
        }
      } else {
        this.$router.push({
          name: 'audienceSummary',
          params: { viewname: 'all', id },
        })
      }
    },
    redirectToList() {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

        if (name) {
          this.$router.push({ name })
        }
      } else {
        this.$router.push({
          name: 'audienceList',
        })
      }
    },
  },
}
</script>
