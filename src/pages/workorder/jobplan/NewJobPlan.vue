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
  name: 'NewJobPlan',
  data: () => ({ dataId: null }),
  extends: FormCreation,
  computed: {
    moduleName() {
      return 'jobplan'
    },
    moduleDisplayName() {
      return 'Job Plan'
    },
    isV3Api() {
      return true
    },
    groupId() {
      let { $route } = this
      let { params } = $route || {}
      let { id } = params || {}

      return !isEmpty(id) ? parseInt(id) : null
    },
    version() {
      let { $route } = this
      let { query } = $route || {}
      let version = this.$getProperty(query, 'version', '')

      version = version.slice(1)
      return !isEmpty(version) ? parseInt(version) : null
    },
  },
  created() {
    this.initJP()
  },
  methods: {
    async init() {
      let { moduleName } = this
      this.isLoading = true
      await this.initJP()
      await this.loadFormsList(moduleName)
      if (!isEmpty(this.dataId)) {
        await this.loadModuleData({
          moduleName,
          moduleDataId: this.dataId,
        })
      }
      this.setInitialForm()

      this.isLoading = false
    },
    async initJP() {
      let { groupId, version } = this
      let params = { groupId, jobPlanVersion: version }
      if (!isEmpty(groupId) && !isEmpty(version)) {
        let { error, data } = await API.get('v3/jobPlan/getJobPlanId', params)

        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let { result } = data || {}
          this.dataId = result
        }
      }
    },
    modifyFieldPropsHook(field) {
      // Overriding field object in consuming component

      //For Job plan tasks
      let { displayTypeEnum } = field || {}
      if (displayTypeEnum === 'JP_TASK') {
        return { ...field, displayName: '' }
      }
    },
    async saveRecord(formModel) {
      let { moduleName, moduleDataId: id, formObj, isEdit } = this
      let { formId } = formModel
      let data = {
        ...this.serializedData(formObj, formModel),
      }
      if (formId) {
        data.formId = formId
      }

      this.isSaving = true
      let params = { data }
      let promise
      let successMsg = ''

      if (!isEdit) {
        promise = await API.createRecord(moduleName, params)
        successMsg = this.$t('jobplan.jp_created')
      } else {
        params = { id, data: { ...data, id } }
        promise = await API.updateRecord(moduleName, params)
        successMsg = this.$t('jobplan.jp_updated')
      }

      let { jobplan, error } = await promise
      if (!error) {
        this.$message.success(successMsg)
        this.redirectToSummary(jobplan)
      } else {
        this.$message.error(error.message)
      }
      this.isSaving = false
    },
    redirectToList() {
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
          })
      } else {
        this.$router.push({ name: 'jobPlanList', params: { viewname: 'all' } })
      }
    },
    redirectToSummary(jobplan) {
      let { moduleName, viewname } = this
      let { group, jobPlanVersion: version } = jobplan || {}
      let groupId = this.$getProperty(group, 'id', null)

      version = `v${version}`
      let params = {
        viewname: !isEmpty(viewname) ? viewname : 'all',
        id: groupId,
      }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params,
            query: { version },
          })
      } else {
        this.$router.push({
          name: 'jobPlanSummary',
          params,
          query: { version },
        })
      }
    },
  },
}
</script>
