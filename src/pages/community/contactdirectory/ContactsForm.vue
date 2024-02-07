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
    return 'Contact Directory'
  },
  computed: {
    moduleName() {
      return 'contactdirectory'
    },
    moduleDisplayName() {
      return 'Contact Directory'
    },
    isV3Api() {
      return true
    },
    isWidgetsSupported() {
      return true
    },
  },
  methods: {
    afterSaveHook({ error }) {
      if (!error) this.redirectToList()
    },
    redirectToList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name && this.$router.push({ name })
      } else {
        this.$router.push({
          name: 'list-contactdir',
        })
      }
    },
  },
}
</script>
