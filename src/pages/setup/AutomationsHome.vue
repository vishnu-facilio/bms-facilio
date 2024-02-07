<template>
  <div class="overflow height100">
    <portal to="automation-modules" slim>
      <div v-if="!$validation.isEmpty(automationModulesList)">
        <el-select
          placeholder="Select"
          class="fc-input-full-border-select2"
          v-model="selectedModuleId"
          @change="setCurrentModule(...arguments)"
          filterable
        >
          <el-option
            v-for="module in automationModulesList"
            :key="module.name"
            :label="module.displayName"
            :value="module.moduleId"
          ></el-option>
        </el-select>
      </div>
    </portal>
    <router-view
      :key="routeName"
      :moduleName="moduleName"
      :moduleDisplayName="moduleDisplayName"
      :isCustomModule="isCustomModule"
      :modules="currentModule"
    ></router-view>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'

export default {
  name: 'AutomationsHome',
  data() {
    return {}
  },
  computed: {
    ...mapGetters('automationSetup', ['getAutomationModulesList']),
    automationModulesList() {
      return this.getAutomationModulesList()
    },
    currentModule: {
      get() {
        let automationModulesList = this.getAutomationModulesList()
        let { $route } = this
        let { params } = $route || {}
        let { moduleName } = params || {}
        if (!isEmpty(moduleName)) {
          let selectedModule = automationModulesList.find(
            module => module.name === moduleName
          )
          return selectedModule || { name: moduleName, displayName: moduleName }
        }
        return isEmpty(automationModulesList) ? [] : automationModulesList[0]
      },
      set() {},
    },
    routeName() {
      let { $route } = this
      let { path } = $route
      if (!isEmpty(path)) {
        return path
      }
      return ''
    },
    moduleName() {
      let { currentModule } = this
      if (!isEmpty(currentModule)) {
        let { name } = currentModule
        return name
      }
      return ''
    },
    moduleDisplayName() {
      let { currentModule } = this
      if (!isEmpty(currentModule)) {
        let { displayName } = currentModule
        return displayName
      }
      return ''
    },
    selectedModuleId: {
      get() {
        let { currentModule } = this
        if (!isEmpty(currentModule)) {
          let { moduleId } = currentModule
          return moduleId
        }
        return null
      },
      set() {},
    },
    isCustomModule() {
      let { currentModule } = this
      if (!isEmpty(currentModule)) {
        let { custom } = currentModule
        return custom
      }
      return false
    },
  },
  methods: {
    setCurrentModule(moduleId) {
      let { automationModulesList, $route } = this
      let { name } = $route
      let selectedModule = automationModulesList.find(
        module => module.moduleId === moduleId
      )
      this.$set(this, 'currentModule', selectedModule)
      this.$router.replace({
        name,
        params: {
          moduleName: selectedModule.name,
        },
      })
    },
  },
}
</script>
