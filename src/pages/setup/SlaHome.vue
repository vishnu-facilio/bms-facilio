<template>
  <div class="overflow height100">
    <portal to="sla-modules" slim>
      <el-select
        v-if="!$validation.isEmpty(slaModulesList)"
        v-model="selectedModuleName"
        :placeholder="$t('common._common.select')"
        filterable
        @change="setSelectedModuleName"
        class="fc-input-full-border-select2"
      >
        <el-option
          v-for="module in slaModulesList"
          :key="module.name"
          :label="module.displayName"
          :value="module.name"
        ></el-option>
      </el-select>
    </portal>
    <router-view :moduleName="selectedModuleName"></router-view>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  name: 'SlaHome',
  data() {
    return {
      slaModulesList: [],
      selectedModuleName: null,
    }
  },
  created() {
    let moduleName = this.$getProperty(this.$route, 'params.moduleName') || null

    if (!isEmpty(moduleName)) this.selectedModuleName = moduleName
    this.getSlaModulesList()
  },
  methods: {
    async getSlaModulesList() {
      let { data, error } = await API.get('v2/automation/slaModule')

      if (!error) {
        let { modules } = data || {}
        this.slaModulesList = modules || []
      } else {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      }
    },
    setSelectedModuleName(moduleName) {
      if (!isEmpty(moduleName)) this.selectedModuleName = moduleName
      this.$router.replace({ params: { moduleName } })
    },
  },
}
</script>
