<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <error-banner
      :error.sync="error"
      :errorMessage.sync="errorText"
    ></error-banner>
    <el-form ref="ruleForm" :label-position="'top'">
      <div id="newcustomfield">
        <!-- header -->
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ !isNew ? 'Edit Custom Field' : 'New Custom Field' }}
            </div>
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
                  v-model="fieldLabel"
                  type="text"
                  placeholder="Enter Field Name"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20" v-if="isNew">
            <el-col :span="12">
              <p class="fc-input-label-txt pB10">Field Type</p>
              <el-form-item prop="fieldType">
                <el-select
                  v-model="fieldType"
                  class="fc-input-full-border-select2 width100"
                  placeholder="Choose Fieldtype"
                >
                  <el-option
                    v-if="(isNew && fieldType.type !== 'SELECTBOX') || !isNew"
                    v-for="fieldType in newfield"
                    :key="fieldType.type"
                    :label="fieldType.label"
                    :value="fieldType.type"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20" v-else>
            <el-col :span="12">
              <p class="fc-input-label-txt pB10">Field Type</p>
              <el-form-item>
                <el-input
                  class="fc-input-full-border2 width100"
                  v-model="fieldTypeLabel"
                  :disabled="true"
                  type="text"
                  placeholder="Enter Field Name"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <!-- <el-row :gutter="20" v-if="module === 'asset'">
            <el-col :span="12">
              <p class="fc-input-label-txt pB10">Category (optional)</p>
              <el-form-item prop="moduleName">
                <el-select
                  v-model="moduleName"
                  class="fc-input-full-border-select2 width100"
                  placeholder="Choose Category"
                >
                  <el-option
                    v-if="assetCategory"
                    v-for="category in assetCategory"
                    :key="category.moduleName"
                    :label="category.displayName"
                    :value="category.moduleName"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>-->
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button
          type="primary"
          @click="saveCustomField('ruleForm')"
          :loading="saving"
          class="modal-btn-save"
          >{{ saving ? 'Saving...' : 'Save' }}</el-button
        >
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'
export default {
  props: ['module', 'field', 'isNew', 'visibility'],
  components: {
    ErrorBanner,
  },
  data() {
    return {
      showPicklistItems: false,
      moduleName: null,
      showAssetDapartments: false,
      fieldId: -1,
      name: '',
      fieldType: null,
      fieldLabel: null,
      fieldTypeLabel: null,
      fieldRequired: false,
      fieldOptions: [],
      error: false,
      errorText: '',
      saving: false,
      fieldTypes: [
        {
          type: 'TEXTBOX',
          label: 'Single Line Text',
          displayType: 1,
          dataType: 1,
          hint: 'Capture small text.',
        },
        {
          type: 'TEXTAREA',
          label: 'Multi Line Text',
          displayType: 2,
          dataType: 18,
          hint:
            'Capture larger amounts of text, typically spanning multiple lines.',
        },
        {
          type: 'EMAIL',
          label: 'Email',
          displayType: 8,
          dataType: 1,
          hint: 'Capture a email address',
        },
        {
          type: 'NUMBER',
          label: 'Number',
          displayType: 9,
          dataType: 2,
          hint: 'Capture a numeric value. Only integers allowed.',
        },
        {
          type: 'DECIMAL',
          label: 'Decimal',
          displayType: 13,
          dataType: 3,
          hint: 'Capture a decimal value.',
        },
        {
          type: 'SELECTBOX',
          label: 'Pick List',
          displayType: 3,
          dataType: 1,
          hint:
            'Provide a picklist with options you define. The record will be tagged accordingly.',
        },
        {
          type: 'DATE',
          label: 'Date',
          displayType: 6,
          dataType: 5,
          hint: 'Capture a date in the future or past.',
        },
        {
          type: 'DATETIME',
          label: 'Datetime',
          displayType: 7,
          dataType: 6,
          hint: 'Capture a datetime in the future or past.',
        },
        {
          type: 'RADIO',
          label: 'Radio',
          displayType: 4,
          dataType: 4,
          hint: 'Capture one value from the listed options.',
        },
        {
          type: 'DECISION_BOX',
          label: 'Checkbox',
          displayType: 5,
          dataType: 4,
          hint: 'Capture a yes/no value.',
        },
      ],
      newfield: [
        {
          type: 'TEXTBOX',
          label: 'Single Line Text',
          displayType: 1,
          dataType: 1,
          hint: 'Capture small text.',
        },
        {
          type: 'TEXTAREA',
          label: 'Multi Line Text',
          displayType: 2,
          dataType: 18,
          hint:
            'Capture larger amounts of text, typically spanning multiple lines.',
        },
        {
          type: 'NUMBER',
          label: 'Number',
          displayType: 9,
          dataType: 2,
          hint: 'Capture a numeric value. Only integers allowed.',
        },
        {
          type: 'DATE',
          label: 'Date',
          displayType: 6,
          dataType: 5,
          hint: 'Capture a date in the future or past.',
        },
        {
          type: 'DATETIME',
          label: 'Datetime',
          displayType: 7,
          dataType: 6,
          hint: 'Capture a date in the future or past.',
        },
        {
          type: 'SELECTBOX',
          label: 'Pick List',
          displayType: 3,
          dataType: 1,
          hint:
            'Provide a picklist with options you define. The record will be tagged accordingly.',
        },
      ],
    }
  },
  computed: {
    selectedFieldType() {
      if (this.fieldType) {
        let ftype = this.fieldTypes.filter(
          fieldType => fieldType.type === this.fieldType
        )
        if (ftype && ftype.length) {
          return ftype[0]
        }
      }
      return null
    },
    assetCategory() {
      return this.$store.state.assetCategory
    },
  },
  mounted() {
    this.initField()
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAssetDepartment')
  },
  methods: {
    reset() {
      this.fieldId = -1
      this.fieldType = null
      this.fieldLabel = null
      this.fieldTypeLabel = null
      this.fieldRequired = false
      this.fieldOptions = []
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    handleclose() {
      this.reset()
    },
    removeFieldOption(index) {
      this.fieldOptions.splice(index, 1)
    },
    addFieldOption() {
      this.fieldOptions.push({
        value: '',
      })
    },
    initField() {
      if (this.isNew) {
        this.fieldId = -1
        this.fieldLabel = null
        this.fieldType = null
        this.fieldRequired = false
      } else {
        this.fieldId = this.field.fieldId
        this.fieldLabel = this.field.displayName
        if (
          (this.field.displayType &&
            this.field.displayType === 'LOOKUP_SIMPLE') ||
          this.field.displayType === 'LOOKUP_POPUP'
        ) {
          this.fieldTypeLabel = 'SELECTBOX'
        } else if (
          this.field.displayType &&
          this.field.displayType === 'TEXTBOX'
        ) {
          this.fieldTypeLabel = 'Single Line Text'
        } else if (
          this.field.displayType &&
          this.field.displayType === 'TEXTAREA'
        ) {
          this.fieldTypeLabel = 'Multi Line Text'
        } else {
          this.fieldTypeLabel = this.field.dataTypeEnum
        }
        this.fieldRequired = this.field.required
        if (this.fieldType === 'SELECTBOX') {
          this.loadFieldOptions(this.field)
        }
      }
    },
    loadFieldOptions: function(field) {
      if (this.module === 'asset' && field.name === 'department') {
        this.showPicklistItems = true
        for (let dept in this.$store.state.assetDepartment) {
          this.fieldOptions({
            name: dept.name,
          })
        }
        this.showAssetDapartments = true
      }
      if (this.fieldOptions.length === 0) {
        this.addFieldOption()
      }
    },
    validation(ruleForm) {
      this.error = false
      this.errorText = ''
      if (this.fieldLabel === null || this.fieldLabel === '') {
        this.errorText = 'Please enter the Field Name'
        this.error = true
        return false
      } else if (this.fieldType === null && this.isNew) {
        this.errorText = 'Please select Field Type'
        this.error = true
      } else {
        this.errorText = ''
        this.error = false
      }
    },
    saveCustomField(ruleForm) {
      let self = this
      this.validation()
      if (this.error) {
        return
      }
      self.saving = true
      let newCF = {}
      if (!this.isNew) {
        newCF = {
          dataType: this.field.dataType,
          displayName: this.fieldLabel,
          displayType: this.field.displayType,
          displayTypeInt: this.field.displayTypeInt,
          required: this.field.required,
          fieldId: this.field.id,
        }
      } else {
        newCF = {
          dataType: this.selectedFieldType.dataType,
          displayName: this.fieldLabel,
          displayType: this.selectedFieldType.type,
          displayTypeInt: this.selectedFieldType.displayType,
          required: this.fieldRequired,
        }
      }

      if (this.fieldId > 0) {
        // update field
        newCF.fieldId = this.fieldId
        let updateObj = {
          fieldId: this.fieldId,
          field: newCF,
        }
        self.$http
          .post('/module/updatefield', updateObj)
          .then(function(response) {
            self.$emit('onsave', newCF)
            self.$emit('update:visibility', false)
            self.$dialog.notify('Field updated successfully!')
          })
      } else {
        let addObj = {
          moduleName: this.moduleName ? this.moduleName : this.module,
          fields: [newCF],
        }
        self.$http.post('/module/addfields', addObj).then(function(response) {
          if (typeof response.data === 'object') {
            newCF.fieldId = response.data[0]
            newCF.displayType = { _name: self.selectedFieldType.label }
            self.$emit('update:visibility', false)
            newCF.id = response.data[0]
            newCF.fieldId = response.data[0]
            newCF.displayType._name = newCF.displayType._name.toUpperCase()
            self.$emit('onsave', newCF)
            self.$dialog.notify('Field created successfully!')
            self.fields.splice(self.fields.indexOf(self.field), 1)
          }
        })
      }
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
