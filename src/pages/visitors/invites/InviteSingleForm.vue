<script>
import FormCreation from '@/base/FormCreation'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  extends: FormCreation,
  computed: {
    moduleName() {
      return 'invitevisitor'
    },
    moduleDisplayName() {
      let { module: formModule } = this.formObj || {}
      let { displayName } = formModule || {}

      return displayName || ''
    },
    visitorTypeId() {
      let { query } = this.$route
      let { visitorTypeId } = query || {}
      return visitorTypeId
    },
    formId() {
      let { query } = this.$route
      let { formId } = query || {}
      return formId
    },
    isWidgetsSupported() {
      return true
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    async init() {
      let { moduleDataId, moduleName } = this

      if (!isEmpty(moduleDataId)) {
        await this.loadModuleData({
          moduleName,
          moduleDataId,
        })
      }
      await this.loadform()
    },
    async loadFormForType() {
      this.isLoading = true

      let { visitorTypeId } = this
      let { data, error } = await API.post('v2/visitorSettings/get', {
        visitorType: { id: visitorTypeId },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.selectedForm = this.$getProperty(
          data,
          'visitorSettings.visitorInviteForm',
          {}
        )
      }
      this.isLoading = false
    },
    async loadform() {
      let params = {
        fetchFormRuleFields: true,
        formId: this.formId,
      }
      this.isLoading = true
      let { data, error } = await API.get('/v2/forms/invitevisitor', params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.selectedForm = data.form
        this.forms.push(this.selectedForm)
      }
      this.isLoading = false
    },
    async onBlurHook({ field, value: phoneNumber, formModel }) {
      if (
        field.name === 'visitorPhone' &&
        isEmpty(formModel['visitorName']) &&
        isEmpty(formModel['visitorEmail'])
      ) {
        let filters = {
          phone: {
            operatorId: 3,
            value: [phoneNumber + ''],
          },
        }
        let params = {
          viewname: 'all',
          filters: JSON.stringify(filters),
        }
        let { list, error } = await API.fetchAll('visitor', params)
        if (!error) {
          if (!isEmpty(list) && isArray(list)) {
            let [visitor] = list
            if (!isEmpty(visitor)) {
              this.$set(formModel, 'visitorName', visitor.name)
              this.$set(formModel, 'visitorEmail', visitor.email)
            }
          }
        } else {
          if (error.message) this.$message.error(error.message)
        }
      }
    },
    afterSerializeHook({ data }) {
      let { visitorTypeId: id } = this
      data = {
        ...data,
        visitorType: { id },
      }
      return data
    },
    afterSaveHook({ error }) {
      if (!error) {
        this.redirectToList()
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
        this.$router.push({ name: 'invites-list' })
      }
    },
  },
}
</script>
