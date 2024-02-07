<template>
  <div>
    <el-popover
      placement="bottom"
      width="250"
      trigger="manual"
      :value="canShow"
      popper-class="field-picker"
      v-click-outside="close"
    >
      <div v-if="$validation.isEmpty(moduleName)" class="m-12">
        <el-tabs v-model="activeTab" class="field-picker">
          <el-tab-pane label="Fields" name="Fields">
            <el-checkbox-group v-model="fieldList" class="checkbox-group">
              <el-checkbox
                v-for="(field, index) in availableFields"
                :key="index"
                :label="field.name"
                class="checkbox"
                >{{ field.displayName }}</el-checkbox
              >
            </el-checkbox-group>
          </el-tab-pane>
          <el-tab-pane
            v-if="!$validation.isEmpty(availableModules)"
            label="Module"
            name="Module"
          >
            <el-checkbox-group v-model="moduleList" class="checkbox-group">
              <el-checkbox
                v-for="(moduleObj, index) in availableModules"
                :key="index"
                :label="moduleObj.name"
                class="checkbox"
                >{{ moduleObj.displayName }}</el-checkbox
              >
            </el-checkbox-group>
          </el-tab-pane>
        </el-tabs>
        <el-button class="btn-save-fields" @click="save">Save</el-button>
      </div>

      <div v-else class="m-12">
        <el-checkbox-group v-model="moduleFields" class="checkbox-group">
          <el-checkbox
            v-for="(field, index) in availableModuleFields[moduleName]"
            :key="index"
            :label="field.name"
            class="checkbox"
            >{{ field.displayName }}</el-checkbox
          >
        </el-checkbox-group>
        <el-button class="btn-save-fields" @click="saveModuleFields"
          >Save</el-button
        >
      </div>

      <slot :slot="'reference'">
        <div class="text-fc-green pointer" @click.stop="canShow = true">
          <inline-svg
            src="add-icon"
            class="vertical-middle"
            iconClass="icon icon-md mR5"
          ></inline-svg
          >Add New
        </div>
      </slot>
    </el-popover>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: [
    'availableFields',
    'availableModules',
    'availableModuleFields',
    'list',
    'selectedModuleFields',
    'moduleName',
  ],
  data() {
    return {
      fieldList: [],
      moduleList: [],
      moduleFields: [],
      canShow: false,
      activeTab: 'Fields',
      fieldListRequired: {},
    }
  },
  methods: {
    save() {
      let selectedFields = this.fieldList
        .map(fieldName => {
          let field = this.availableFields.find(
            field => field.name === fieldName
          )
          if (!isEmpty(field) && this.fieldListRequired[fieldName]) {
            field = { ...field, required: this.fieldListRequired[fieldName] }
          }
          return !isEmpty(field) ? field : null
        })
        .filter(f => !isEmpty(f))

      let selectedModules = this.moduleList.map(subFormName => {
        let moduleObj = this.availableModules.find(
          moduleObj => moduleObj.name === subFormName
        )
        return {
          fields: [],
          sectionType: 2,
          subFormId: null,
          subForm: { displayName: moduleObj.displayName, name: subFormName },
        }
      })

      let list = [...selectedFields, ...selectedModules]

      this.$emit('update:list', list)
      this.close()
    },

    saveModuleFields() {
      let selectedModuleFields = this.moduleFields
        .map(fieldName => {
          let field = this.availableModuleFields[this.moduleName].find(
            field => field.name === fieldName
          )
          return !isEmpty(field) ? field : null
        })
        .filter(f => !isEmpty(f))

      this.$emit('update:selectedModuleFields', selectedModuleFields)
      this.close()
    },

    close() {
      this.canShow = false
    },
  },
  watch: {
    list: {
      handler: function() {
        if (!isEmpty(this.list)) {
          let fieldList = this.list.filter(field =>
            field.hasOwnProperty('fieldId')
          )

          this.fieldList = fieldList.map(field => field.name)
          this.fieldListRequired = fieldList.reduce(
            (requiredFieldObj, field) => {
              let { name, required } = field
              requiredFieldObj[name] = required
              return requiredFieldObj
            },
            {}
          )

          this.moduleList = this.list
            .filter(field => field.hasOwnProperty('subFormId'))
            .map(field =>
              this.$getProperty(
                field.subForm,
                'module.name',
                field.subForm.name
              )
            )
        }
      },
      immediate: true,
    },
  },
}
</script>
<style scoped>
.checkbox {
  display: block;
  padding-left: 10px;
  margin: 15px 0;
}
.btn-save-fields {
  width: 100%;
  padding-top: 10px;
  padding-bottom: 10px;
  cursor: pointer;
  background-color: #39b2c2;
  border: transparent;
  letter-spacing: 1.1px;
  text-align: center;
  color: #ffffff;
  text-transform: uppercase;
  font-weight: 500;
  border-radius: 0;
  float: right;
  line-height: 16px;
  margin-bottom: -2px;
}
.m-12 {
  margin-right: -12px;
  margin-left: -12px;
}
.checkbox-group {
  max-height: 200px;
  overflow-y: scroll;
}
</style>
<style>
.field-picker .el-tabs__header {
  margin-left: 10px;
}
.field-picker {
  padding-bottom: 2px;
}
</style>
