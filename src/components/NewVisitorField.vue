<template>
  <el-dialog
    :visible.sync="visibility"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <!-- <error-banner :error.sync="error" :errorMessage.sync="errorText"></error-banner> -->
    <el-form ref="ruleForm" :label-position="'top'">
      <div id="newcustomfield">
        <!-- header -->
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">New Custom Field</div>
          </div>
        </div>

        <div class="new-body-modal">
          <el-row :gutter="20">
            <el-col :span="24">
              <p class="fc-input-label-txt pB10">Field name</p>
              <el-form-item prop="name">
                <el-input
                  class="fc-input-full-border2 width100"
                  :autofocus="true"
                  v-model="newCustomField.displayName"
                  type="text"
                  placeholder="Enter Field Name"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt pB10">Field Type</p>
              <el-form-item prop="fieldType">
                <el-select
                  v-model="newFieldTypeEnum"
                  class="fc-input-full-border-select2 width100"
                  placeholder="Choose Fieldtype"
                  @change="fieldTypeChanged"
                >
                  <el-option
                    v-for="fieldType in supportedFieldTypes"
                    :key="fieldType.displayTypeEnum"
                    :label="fieldType.displayName"
                    :value="fieldType.displayTypeEnum"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button
          type="primary"
          @click="saveCustomField(newCustomField)"
          :loading="saving"
          class="modal-btn-save"
          >{{ saving ? 'Saving...' : 'Save' }}</el-button
        >
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
// import { QInput, QSelect, QCheckbox, QRadio } from 'quasar'
import FormBuilderMixin from '@/mixins/forms/FormBuilderMixin'
import { deepCloneObject } from 'util/utility-methods'

import Constants from 'util/constant'
// import ErrorBanner from '@/ErrorBanner'
// import VSelect from 'vue-select'

export default {
  mixins: [FormBuilderMixin],
  props: ['module', 'field', 'isNew', 'visibility'],
  components: {
    // ErrorBanner,
  },
  data() {
    return {
      saving: false,
      showPicklistItems: false,
      newFieldTypeEnum: null,
      newCustomField: null,
    }
  },
  computed: {
    supportedFieldTypes() {
      return Constants.FORMS_DEFAULT_FIELDS.filter(fieldType =>
        ['NUMBER', 'TEXTBOX', 'DECISION_BOX'].includes(
          fieldType.displayTypeEnum
        )
      )
    },
    // selectedFieldType()
    // {
    //   return supportedFieldTypes.find(fieldType.displayTypeEnum==selectedFieldType)
    // }
  },
  mounted() {},
  created() {
    // this.fieldTypeChanged()

    this.newFieldTypeEnum = this.supportedFieldTypes[0].displayTypeEnum
    this.newCustomField = deepCloneObject(this.supportedFieldTypes[0])
  },
  methods: {
    // reset() {

    // },
    fieldTypeChanged() {
      this.newCustomField = deepCloneObject(
        this.supportedFieldTypes.find(
          field => field.displayTypeEnum == this.newFieldTypeEnum
        )
      )
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },

    saveCustomField(customField) {
      let fieldJson = this.serializeFields(
        customField,
        Constants.ADD_FIELDS_RESOURCE_PROPS
      )
      let data = {
        moduleName: this.module,
        fieldJson,
      }
      this.$http.post('v2/modules/fields/add', data).then(resp => {
        this.$emit('update:visibility', false)
        let moduleField = resp.data.result.field

        //fill field ID and name from module Field object to form field object and emit
        customField.name = moduleField.name
        customField.fieldId = moduleField.fieldId
        customField.displayName = moduleField.displayName
        this.$emit('onsave', customField)

        //self.$dialog.notify('Field created successfully!')
      })
    },

    cancel() {
      this.$emit('canceled')
    },
  },
}
</script>
<style>
.field-hint {
  padding: 8px 0;
  font-size: 12px;
  border-radius: 2px;
}
.custom-field-modal .fc-dialog-form .el-dialog__header {
  padding: 0 !important;
}
.custom-field-modal .el-dialog__header {
  padding: 0 !important;
}
/* .new-body-modal .fc-form input:not(.q-input-target):not(.el-input__inner):not(.el-select__input):not(.btn), .fc-form textarea:not(.q-input-target):not(.el-textarea__inner):not(.el-input__inner):not(.el-select__input), .fc-form .fselect{
    width: 300px;
  } */
.new-body-modal .check-required {
  display: block;
}
.new-body-modal .select-height {
  height: 40px;
}
.v-modal {
  z-index: 101 !important;
}
</style>
