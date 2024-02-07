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
    return 'Document'
  },
  computed: {
    moduleName() {
      return 'admindocuments'
    },
    moduleDisplayName() {
      return 'Document'
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
          name: 'list-admindocs',
        })
      }
    },
  },
}
</script>
