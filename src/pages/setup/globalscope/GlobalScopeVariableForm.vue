<template>
  <div class="formbuilder-fullscreen-popup height100vh">
    <FormLayout
      class="view-creation-form"
      :subHeadings="subHeading"
      :title="title"
      :isLoading="loading"
      @onCancel="onCancel"
      :isSaving="isSaving"
      @onSave="saveScope"
    >
      <template #spinner-section>
        <spinner v-if="loading" :show="loading" size="80"></spinner>
      </template>
      <template v-if="!loading" #details-section>
        <ScopeDetails
          :appId="appId"
          ref="details-section"
          :modulesList="modulesList"
          :scopeVariableDetail="scopeVariableDetail"
          :detailsSection="detailsSection"
          @selectModule="data => changeApplicableModule(data)"
        ></ScopeDetails>
      </template>
      <template v-if="!loading" #valuegenerator-section>
        <ValueGenerator
          ref="valgen-section"
          :valueGenerators="valueGenerators"
          :loading="metaLoading"
          :scopeVariableDetail="scopeVariableDetail"
          :valGenSection="valGenSection"
        >
        </ValueGenerator>
      </template>
      <template v-if="!loading" #fieldmapping-section>
        <FieldMapping
          ref="fieldmap-section"
          :moduleFieldsMap="moduleFieldsMap"
          :scopeVariableDetail="scopeVariableDetail"
          :loading="metaLoading"
          :model="moduleFieldModel"
        >
        </FieldMapping>
      </template>
    </FormLayout>
  </div>
</template>

<script>
import { FormLayout } from '@facilio/ui/setup'
import { isEmpty } from '@facilio/utils/validation'
import ScopeDetails from 'src/pages/setup/globalscope/ScopeDetails.vue'
import ValueGenerator from 'src/pages/setup/globalscope/ValueGenerator.vue'
import FieldMapping from 'src/pages/setup/globalscope/FieldMapping.vue'
import { API } from '@facilio/api'
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'

export default {
  components: {
    FormLayout,
    ScopeDetails,
    ValueGenerator,
    FieldMapping,
  },
  data() {
    return {
      subHeading: [
        {
          displayName: 'Scoping Details',
          id: 'details',
        },
        {
          displayName: 'Variable',
          id: 'valuegenerator',
        },
        {
          displayName: 'Field Mapping',
          id: 'fieldmapping',
        },
      ],
      modulesList: [],
      moduleFieldsMap: {},
      valueGenerators: [],
      metaLoading: false,
      loading: true,
      isSaving: false,
      scopeVariableDetail: null,
      moduleFieldModel: {
        moduleField: [],
      },
      valGenSection: {
        valueGeneratorId: '',
      },
      detailsSection: {
        displayName: '',
        description: '',
        switchModule: '',
      },
    }
  },
  async created() {
    let { id } = this
    if (!isEmpty(id)) {
      this.getGlobalScopeVariable(id)
    }
    this.getModuleList()
  },
  mounted() {
    eventBus.$on('resetFieldMapping', this.resetFieldMapping)
  },
  computed: {
    title() {
      let { id } = this
      return isEmpty(id)
        ? this.$t('setup.globalscoping.addglobalscope')
        : this.$t('setup.globalscoping.editglobalscope')
    },
    appId() {
      if (this.$route.params.appId) {
        return parseInt(this.$route.params.appId)
      }
      return -1
    },
    id() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
  },
  methods: {
    async changeApplicableModule(module) {
      this.valGenSection = { valueGeneratorId: '' }
      await this.getValGenAndFields(module)
    },
    async getValGenAndFields(module) {
      this.metaLoading = true
      await Promise.all([
        this.getApplicableModuleFieldsMap(module),
        this.getValueGenerators(module),
      ]).then(() => {
        this.metaLoading = false
      })
    },
    onCancel() {
      this.$router.back()
    },
    async getModuleList() {
      this.loading = true
      let { appId } = this
      let { error, data } = await API.get(`/v3/scopeVariable/meta`, { appId })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let { modules } = data || {}
        this.modulesList = modules
      }
      this.loading = false
    },
    async getApplicableModuleFieldsMap(module) {
      this.moduleFieldsMap = []
      let { appId } = this
      let { error, data } = await API.get(
        `/v3/scopeVariable/applicableFieldsMap`,
        {
          appId,
          lookupModuleName: module,
        }
      )
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let { records } = data || {}
        this.moduleFieldsMap = records
      }
    },
    async getValueGenerators(module) {
      this.valueGenerators = []
      let { error, data } = await API.get(
        `/v3/scopeVariable/applicableValueGenerators`,
        {
          applicableModuleName: module,
        }
      )
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let valueGenerators = this.$getProperty(data, 'valueGenerators', [])
        valueGenerators.push({ displayName: 'All', id: -1 })
        this.valueGenerators = valueGenerators
      }
    },
    async getGlobalScopeVariable(id) {
      this.loading = true
      let { error, data } = await API.get(`/v3/scopeVariable/detail`, { id })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let { scopeVariable } = data || {}
        this.scopeVariableDetail = scopeVariable
        let { applicableModuleName } = scopeVariable || {}
        await this.getValGenAndFields(applicableModuleName)
        this.deSerialize()
      }
      this.loading = false
    },
    async saveScope() {
      this.isSaving = true
      let { id, appId } = this
      let scopeVariable = this.constructScopeVariableData()
      if (!isEmpty(scopeVariable)) {
        let { error } = await API.post(`/v3/scopeVariable/addOrUpdate`, {
          scopeVariable,
        })
        if (error) {
          this.$message.error(error.message || 'Error Occurred')
        } else {
          if (!isEmpty(id)) {
            this.$message.success(
              this.$t('setup.globalscoping.scopevariableupdated')
            )
          } else {
            this.$message.success(
              this.$t('setup.globalscoping.scopevariablecreated')
            )
          }
          this.$router.replace({
            name: 'globalscopelist',
            params: { appId },
          })
        }
      }
      this.isSaving = false
    },
    constructScopeVariableData() {
      let fieldsMap = this.$refs['fieldmap-section'].serializeData()
      let details = this.$refs['details-section'].serializeData()
      let valueGen = this.$refs['valgen-section'].serializeData()
      if (isEmpty(details) || isEmpty(valueGen)) {
        return null
      }
      //Need to be uncommented if fields mapping is mandatory
      // if (isEmpty(fieldsMap)) {
      //   this.$message.error('No fields selected')
      //   return null
      // }
      let { appId, id } = this
      let scopeVariable = {
        ...details,
        ...valueGen,
        status: true,
        appId,
        scopeVariableModulesFieldsList: fieldsMap,
      }
      if (!isEmpty(id)) {
        scopeVariable.id = id
      }
      return scopeVariable
    },
    deSerialize() {
      let { scopeVariableDetail, moduleFieldsMap } = this
      if (!isEmpty(scopeVariableDetail)) {
        let {
          displayName,
          description,
          applicableModuleName,
          valueGeneratorId,
          scopeVariableModulesFieldsList,
        } = scopeVariableDetail || {}

        this.detailsSection = {
          displayName,
          description,
          switchModule: applicableModuleName,
        }
        if (isEmpty(valueGeneratorId)) {
          valueGeneratorId = -1
        }
        this.valGenSection = {
          valueGeneratorId,
        }
        let moduleFieldModel = {}
        moduleFieldsMap.map(module => {
          let { moduleId } = module
          scopeVariableModulesFieldsList.map(scopeModuleField => {
            if (scopeModuleField.moduleId === moduleId) {
              moduleFieldModel[module.name] = scopeModuleField.fieldName
            }
          })
        })
        this.moduleFieldModel = { moduleField: moduleFieldModel }
      }
    },
    resetFieldMapping() {
      this.moduleFieldModel = {
        moduleField: [],
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.view-creation-form {
  padding-left: 0px !important;
}
</style>
