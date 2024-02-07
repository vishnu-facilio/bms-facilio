<script>
import FormCreation from '@/base/FormCreation'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: FormCreation,

  computed: {
    moduleName() {
      return 'routes'
    },
    moduleDisplayName() {
      return 'Routes'
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    saveRecord(formModel) {
      let { moduleName, moduleDataId, formObj } = this
      let { formId } = formModel
      let serializeData = this.serializedData(formObj, formModel)
      let data = {
        ...serializeData,
      }

      if (formId) {
        data.formId = formId
      }

      this.isSaving = true

      if (isEmpty(moduleDataId)) {
        API.createRecord(moduleName, {
          data,
        }).then(({ error }) => {
          if (!error) {
            this.$message.success(
              `${this.moduleDisplayName} created successfully`
            )
            this.redirectToList()
          } else {
            this.$message.error(error.message)
          }
          this.isSaving = false
        })
      } else {
        data.id = moduleDataId

        API.updateRecord(moduleName, {
          id: moduleDataId,
          data,
        }).then(({ error }) => {
          if (!error) {
            this.$message.success(
              `${this.moduleDisplayName} updated successfully`
            )
            this.redirectToList()
          } else {
            this.$message.error(error.message)
          }
          this.isSaving = false
        })
      }
    },
    redirectToList() {
      let { moduleName, $router, $route } = this
      let { query } = $route || {}
      let listRoute = { name: 'routeList', query }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        if (!isEmpty(name)) {
          listRoute.name = name
        }
      }
      $router.push(listRoute)
    },
  },
}
</script>
