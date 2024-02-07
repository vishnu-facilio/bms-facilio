<template>
  <div class="config-options">
    <el-radio-group v-model="config.tab" @change="resetModule" class="mB15">
      <el-radio label="service_catelog" class="fc-radio-btn">
        {{ $t('setup.users_management.service_catelog') }}
      </el-radio>
      <el-radio label="module" class="fc-radio-btn">
        {{ $t('setup.users_management.module') }}
      </el-radio>
    </el-radio-group>

    <el-select
      v-if="config.tab === 'module'"
      v-model="config.moduleName"
      placeholder="Please select the Module"
      class="fc-input-full-border2 width300px mB15"
      filterable
    >
      <el-option-group
        v-for="(moduleObj, moduleName) in modulesList"
        :key="moduleName"
        :label="moduleName.toUpperCase()"
      >
        <el-option
          v-for="list in moduleObj"
          :key="list.moduleId"
          :label="list.displayName"
          :value="list.name"
        >
        </el-option>
      </el-option-group>
    </el-select>

    <el-button
      type="primary"
      @click="save()"
      class="save-options width-fit"
      :loading="saving"
    >
      {{ saving ? $t('common._common._saving') : $t('common._common._save') }}
    </el-button>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['configJSON'],

  data() {
    return {
      config: {
        tab: 'service_catelog',
        moduleName: null,
      },
      modulesList: [],
      saving: false,
    }
  },

  created() {
    if (!isEmpty(this.configJSON)) {
      let { tab, moduleName } = this.configJSON
      this.config = { tab, moduleName }
    }
    this.loadModules()
  },

  methods: {
    async loadModules() {
      let { error, data } = await API.get('/v3/modules/list/all')

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let modulesList = Object.assign(data, {
          'System Modules': data['systemModules'],
          'Custom Modules': data['customModules'],
        })
        delete modulesList['systemModules']
        delete modulesList['customModules']

        this.modulesList = modulesList || {}
      }
    },
    resetModule() {
      this.config.moduleName = null
    },
    validate() {
      let { tab, moduleName } = this.config

      if (tab === 'module' && isEmpty(moduleName)) {
        this.$message.error('Please select module for Submit Request')
        return false
      }
      return true
    },
    save() {
      let { config } = this
      let valid = this.validate()

      if (!valid) return
      this.$emit('setOption', config)
    },
  },
}
</script>
