<template>
  <div class="global-scope-variable-details-container">
    <div class="sub-title-desc mT10">
      {{ $t('setup.globalscoping.specifyscopevariabledetails') }}
    </div>
    <el-form ref="naming-form" :rules="rules" :model="detailsSection">
      <el-form-item
        :label="$t('setup.globalscoping.scopename')"
        prop="displayName"
      >
        <TextInput
          v-model="detailsSection.displayName"
          :placeholder="$t('setup.globalscoping.entername')"
        />
      </el-form-item>
      <el-form-item
        prop="description"
        :label="$t('common.wo_report.report_description')"
        class="mB10"
      >
        <el-input
          type="textarea"
          :autosize="{ minRows: 4, maxRows: 4 }"
          class="fc-input-full-border-textarea"
          :placeholder="'Enter Description'"
          v-model="detailsSection.description"
          resize="none"
        ></el-input>
      </el-form-item>
      <el-form-item
        prop="switchModule"
        :label="$t('setup.globalscoping.scopemodule')"
        class="mB10"
      >
        <el-select
          v-model="detailsSection.switchModule"
          :placeholder="'Select Switch Module'"
          class="fc-input-full-border-select2 width100"
          filterable
          @change="setApplicableModule"
        >
          <el-option-group
            v-for="(moduleGroupList, key) in getModulesGroupList"
            :key="key"
            :label="key"
          >
            <el-option
              v-for="mod in moduleGroupList"
              :key="'module-' + mod.name"
              :label="mod.displayName"
              :value="mod.name"
            >
            </el-option>
          </el-option-group>
        </el-select>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import { TextInput } from '@facilio/ui/forms'
import { isEmpty } from '@facilio/utils/validation'
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'

export default {
  props: ['modulesList', 'scopeVariableDetail', 'detailsSection'],
  components: { TextInput },
  data() {
    return {
      formData: {},
      rules: {
        displayName: [
          {
            required: true,
            message: 'Please input display name',
            trigger: 'blur',
          },
        ],
        switchModule: [
          {
            required: true,
            message: 'Please select scope module',
            trigger: 'change',
          },
        ],
      },
    }
  },
  methods: {
    setApplicableModule(module) {
      this.$emit('selectModule', module)
      eventBus.$emit('resetValueGen')
      eventBus.$emit('resetFieldMapping')
    },
    serializeData() {
      this.$refs['naming-form'].validate(valid => {
        if (valid) {
          let data = {}
          let { modulesList, detailsSection } = this
          data.displayName = detailsSection.displayName
          data.description = detailsSection.description
          if (!isEmpty(modulesList)) {
            let { switchModule } = detailsSection || {}
            modulesList.map(module => {
              if (module.name === switchModule) {
                if (module.moduleId <= -1) {
                  data.applicableModuleName = module.name
                } else {
                  data.applicableModuleId = module.moduleId
                }
              }
            })
          }
          this.formData = data
        } else {
          return null
        }
      })
      return this.formData
    },
  },
  computed: {
    getModulesGroupList() {
      let systemMod = []
      let customMod = []
      let { modulesList } = this || {}
      modulesList.map(module => {
        if (!isEmpty(module)) {
          if (module.custom) {
            customMod.push(module)
          } else {
            systemMod.push(module)
          }
        }
      })
      return { 'System Modules': systemMod, 'Custom Modules': customMod }
    },
  },
}
</script>
<style scoped lang="scss">
.global-scope-variable-details-container {
  .sub-title-desc {
    font-size: 12px;
    letter-spacing: 0.6px;
    color: #999999;
  }
}
</style>
