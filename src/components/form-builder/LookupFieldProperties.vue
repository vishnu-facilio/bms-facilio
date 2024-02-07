<template>
  <el-dialog
    :title="titleCard"
    :visible.sync="canShowLookupDialog"
    width="40%"
    class="fc-dialog-center-container lookup-field-prop-container"
    :before-close="closeDialogBox"
    :append-to-body="true"
  >
    <ErrorBanner
      :error.sync="error"
      :errorMessage.sync="errorMessage"
    ></ErrorBanner>
    <div class="pB60">
      <el-form ref="lookupFieldProps" :rules="rules" :model="field">
        <el-form-item
          label="Field Label"
          prop="displayName"
          class="mB20"
          :required="true"
        >
          <el-input
            :placeholder="$t('common._common.enter_name')"
            v-model="field.displayName"
            class="fc-input-full-border-select2"
            @input="changeFieldName"
          ></el-input>
        </el-form-item>
        <el-form-item prop="name" class="mB10" :required="true">
          <div class="d-flex align-center pB6">
            <p class="fc-input-label-txt pB0">
              {{ $t('fields.properties.link_name') }}
            </p>
            <span
              v-tippy="{
                arrow: true,
                arrowType: 'round',
                animation: 'fade',
              }"
              :content="$t('fields.properties.link_name_desc')"
              class="el-icon-info mL5"
            >
            </span>
          </div>
          <el-input
            :placeholder="$t('common._common.enter_name')"
            v-model="field.name"
            class="fc-input-full-border-select2"
            @change="changeNameInteractedBoolean"
          ></el-input>
        </el-form-item>
        <el-form-item
          v-if="!isLocationField"
          label="Type"
          prop="lookupModuleName"
          class="mB20"
          :required="!isLocationField"
        >
          <el-select
            placeholder="Select"
            v-model="field.lookupModuleName"
            filterable
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="module in moduleList"
              :key="module.name"
              :label="module.displayName"
              :value="module.name"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="!isLocationField"
          :label="$t('fields.properties.display_type')"
          prop="config"
          class="mB20"
        >
          <el-select
            :placeholder="$t('fields.properties.display_type_placeholders')"
            v-model="field.displayType"
            class="fc-input-full-border-select2 width100"
            @change="changeDisplayType"
          >
            <el-option
              :label="$t('fields.properties.single_select')"
              :value="10"
            ></el-option>
            <el-option
              :label="$t('fields.properties.multiple_select')"
              :value="66"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="canShowRelatedListTitle(field)"
          :label="$t('common._common.enter_related_list_label')"
          prop="relatedListDisplayName"
          class="mB20"
        >
          <el-input
            v-model="field.relatedListDisplayName"
            class="fc-input-full-border-select2"
            @input="changeRelatedListDisplayName"
          ></el-input>
        </el-form-item>
      </el-form>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialogBox" class="modal-btn-cancel"
        >Cancel</el-button
      >
      <el-button type="primary" class="modal-btn-save" @click="addLookup()"
        >Confirm</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import ErrorBanner from '@/ErrorBanner'
import { isEmpty } from '@facilio/utils/validation'
import { isMultiLookup } from '@facilio/utils/field'
import { API } from '@facilio/api'
export default {
  components: {
    ErrorBanner,
  },
  props: {
    openLookupDialog: {
      type: Boolean,
    },
    moduleList: {
      type: Array,
    },
    lookupField: {
      type: Object,
    },
    moduleName: {
      type: String,
    },
    isCustomModule: {
      type: Boolean,
    },
    isLocationField: {
      type: Boolean,
    },
  },
  computed: {
    titleCard() {
      let { isLocationField } = this || {}
      let placeholder = `${isLocationField ? 'Location' : 'Lookup'}`
      return `${placeholder} Field Properties`
    },
    canShowLookupDialog: {
      get() {
        return this.openLookupDialog
      },
      set(value) {
        this.$emit('update:openLookupDialog', value)
      },
    },
    field: {
      get() {
        return this.lookupField
      },
      set(value) {
        this.$emit('update:lookupField', value)
      },
    },
  },
  created() {
    let { field } = this
    let { displayName } = field || {}

    if (!isEmpty(displayName)) {
      let fieldName = this.getSnakeCase(displayName)
      this.$set(field, 'name', fieldName)
    }
  },
  data() {
    return {
      fieldProperties: {},
      canRemoveField: true,
      error: false,
      errorMessage: null,
      isNameFieldInteracted: false,
      rules: {
        displayName: [
          {
            required: true,
            message: 'Please input module name',
            trigger: 'change',
          },
        ],
        lookupModuleName: [
          {
            required: true,
            message: 'Please select module',
            trigger: 'change',
          },
        ],
      },
    }
  },
  methods: {
    canShowRelatedListTitle(field) {
      let { isLocationField } = this || {}
      return !isMultiLookup(field) && !isLocationField
    },
    closeDialogBox() {
      let { field } = this
      let { rowIndex, columnIndex } = field
      this.canShowLookupDialog = false
      if (this.canRemoveField) {
        this.$emit('removeField', field, rowIndex, columnIndex)
      }
    },
    addLookup() {
      this.$refs['lookupFieldProps'].validate(async valid => {
        if (valid) {
          let { field } = this
          let { displayTypeEnum } = field || {}
          let moduleObj =
            this.moduleList.find(
              module => module.name === field.lookupModuleName
            ) || {}
          if (isEmpty(moduleObj) && displayTypeEnum === 'GEO_LOCATION') {
            try {
              let { data } = await API.get('v2/module/location')
              let moduleId = this.$getProperty(data, 'module.moduleId') || null
              if (!isEmpty(moduleId)) {
                this.$set(field, 'lookupModuleId', moduleId)
              } else {
                this.$set(field, 'specialType', 'location')
              }
            } catch (error) {
              this.$message.error(error.message || 'Error Occurred')
            }
          }
          if (!isEmpty(moduleObj?.moduleId)) {
            this.$set(field, 'lookupModuleId', moduleObj.moduleId)
          } else {
            this.$set(field, 'specialType', moduleObj.name)
          }
          this.canRemoveField = false
          this.$emit('addNewDefaultField', field)
        }
      })
    },
    changeDisplayType(displayType) {
      let { field } = this
      if (displayType === 10) {
        this.$set(field, 'displayTypeEnum', 'LOOKUP')
        this.$set(field, 'displayTypeInt', 10)
        this.$set(field, 'dataType', 7)
      } else if (displayType === 66) {
        this.$set(field, 'displayTypeEnum', 'MULTI_LOOKUP_SIMPLE')
        this.$set(field, 'displayTypeInt', 66)
        this.$set(field, 'dataType', 13)
      }
    },
    getSnakeCase(string) {
      return string.replace(/[\W_]/g, '_').toLowerCase()
    },
    changeRelatedListDisplayName(inputVal) {
      this.$set(this.field, 'relatedListDisplayName', inputVal)
    },
    changeFieldName(val) {
      let { field, isNameFieldInteracted } = this
      if (!isNameFieldInteracted) {
        let fieldName = this.getSnakeCase(val)
        this.$set(field, 'name', fieldName)
      }
    },
    changeNameInteractedBoolean() {
      this.isNameFieldInteracted = true
    },
  },
}
</script>

<style lang="scss">
.lookup-field-prop-container {
  .lookup-field-prop-form {
    padding-bottom: 52px;
  }
  .fc-required-label {
    &::after {
      content: '*';
      color: red;
      font-size: 15px;
    }
  }
}
</style>
