<template>
  <div class="height100 overflow-hidden">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('setup.setupLabel.modules') }}
        </div>
        <div class="heading-description">
          {{ $t('setup.list.list_modules') }}
        </div>
      </div>
      <div
        v-if="activeTabName === 'customModules'"
        class="action-btn setting-page-btn"
      >
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="openModuleCreation()"
          >{{ $t('setup.add.add_moduele') }}</el-button
        >
      </div>
    </div>
    <el-tabs v-model="activeTabName" @tab-click="handleClick" class="mL30">
      <el-tab-pane
        class=""
        :label="$t('setup.setup.systemModules')"
        name="systemModules"
      >
      </el-tab-pane>

      <el-tab-pane
        :label="$t('setup.setup.customModules')"
        name="customModules"
      >
      </el-tab-pane>
    </el-tabs>
    <div class="container-scroll">
      <ModulesListTemplate
        :isLoading="isLoading"
        :moduleList="moduleList"
        @redirect="redirectToModule"
        @openModuleCreation="openModuleCreation"
        :allowModulesEdit="allowModulesEdit"
      >
      </ModulesListTemplate>
    </div>
    <!-- Create New Module Dialogue Box -->
    <ModuleNew
      v-if="canShowModuleCreation"
      :canShowModuleCreation.sync="canShowModuleCreation"
      :moduleObj="selectedModule"
      :isEdit="isEdit"
      :updateModule="updateSelectedModule"
    ></ModuleNew>
  </div>
</template>

<script>
import ModulesListTemplate from 'pages/setup/modules/ModulesListTemplate'
import ModuleNew from 'pages/setup/modules/ModuleNew'
import { isEmpty } from '@facilio/utils/validation'
import { deepCloneObject } from 'util/utility-methods'
import { API } from '@facilio/api'

export default {
  components: {
    ModuleNew,
    ModulesListTemplate,
  },
  data() {
    return {
      moduleList: [],
      allowModulesEdit: false,
      activeTabName: 'systemModules',
      modulesObj: {},
      isLoading: false,
      canShowModuleCreation: false,
      selectedModule: {
        displayName: null,
        description: null,
        stateFlowEnabled: false,
      },
      isEdit: false,
    }
  },
  async created() {
    this.isLoading = true

    let url = '/v2/module/lists?defaultModules=true'
    let { error, data } = await API.get(url)

    if (error) {
      this.$message.error(error.message || 'Error Occurred')
    } else {
      let modulesObj = data.modules
      this.modulesObj = modulesObj
      this.moduleList = modulesObj[this.activeTabName]
    }
    this.isLoading = false
  },
  methods: {
    handleClick() {
      this.moduleList = this.modulesObj[this.activeTabName]
      if (this.activeTabName === 'customModules') {
        this.allowModulesEdit = true
      } else {
        this.allowModulesEdit = false
      }
    },
    openModuleCreation(module) {
      this.$set(this, 'canShowModuleCreation', true)
      if (!isEmpty(module)) {
        this.$set(this, 'selectedModule', deepCloneObject(module))
        this.$set(this, 'isEdit', true)
      } else {
        this.$set(this, 'selectedModule', {
          displayName: null,
          description: null,
        })
        this.$set(this, 'isEdit', false)
      }
    },
    redirectToModule(module) {
      let { name: moduleName } = module
      this.$router.push({
        name: 'modules-details',
        params: {
          moduleName,
        },
      })
    },
    updateSelectedModule(selectedModule) {
      let { moduleList } = this
      let selectedModuleIndex = (moduleList || []).findIndex(
        module => module.moduleId === selectedModule.moduleId
      )
      moduleList.splice(selectedModuleIndex, 1, selectedModule)
      this.$set(this, 'moduleList', moduleList)
    },
  },
}
</script>
