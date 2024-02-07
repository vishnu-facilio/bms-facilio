<script>
import { isEmpty } from '@facilio/utils/validation'
import FormCreation from '@/base/FormCreation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: FormCreation,
  mixins: [FetchViewsMixin],
  computed: {
    moduleName() {
      return 'employee'
    },
    moduleDisplayName() {
      return 'Employee'
    },
    isWidgetsSupported() {
      return true
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    afterSaveHook(response) {
      let { moduleName } = this
      let { [moduleName]: data } = response
      let { id } = data
      this.redirectToSummary(id)
    },
    async redirectToSummary(id) {
      this.isSaving = true

      let { moduleName } = this
      let viewname = await this.fetchView(moduleName)

      this.isSaving = false

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        if (name) {
          this.$router.push({ name, params: { viewname, id } })
        }
      } else {
        this.$router
          .push({
            path: `/app/pl/employee/all-employees/${id}/summary`,
          })
          .catch(() => {})
      }
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
          path: `/app/pl/employee/all-employees`,
        })
      }
    },
    modifyFieldPropsHook(field) {
      let emailValue = this.$getProperty(this, 'moduleData.email')
      let { name } = field || {}
      if (!isEmpty(emailValue) && name === 'email') {
        return { ...field, isDisabled: true }
      }
    },
  },
}
</script>
