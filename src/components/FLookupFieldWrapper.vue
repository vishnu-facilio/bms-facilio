<template>
  <div class="lookup-wrapper">
    <FLookupField
      :class="{ 'handle-wizard-icon': handleWizardIcon }"
      v-if="fieldObj.field"
      :model.sync="modelObj"
      :field="fieldObj"
      :disabled="disabled"
      :siteId="siteId"
      :hideLookupIcon="hideWizard"
      :hideDropDown="hideDropDown"
      :preHookFilterConstruction="filterConstruction"
      :fetchOptionsOnLoad="fetchOptionsOnLoad"
      :fetchOptionsMethod="fetchOptionsMethod"
      @showLookupWizard="showLookupWizard"
      @recordSelected="onRecordSelected"
    ></FLookupField>
    <el-input v-else></el-input>
    <div v-if="!hideWizard && canShowLookupWizard">
      <FLookupFieldWizard
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="fieldObj"
        :siteId="siteId"
        :withReadings="withReadings"
        @setLookupFieldValue="setLookupFieldValue"
        @showMainFieldSearch="onWizardShow(false)"
      ></FLookupFieldWizard>
    </div>
  </div>
</template>

<script>
import FLookupField from '@/forms/FLookupField'
import { isEmpty, isArray } from '@facilio/utils/validation'
export default {
  name: 'f-lookup-field-wrapper',
  props: {
    value: [Number, String, Array],
    label: String, // Label of selected value if any
    formField: Object,
    field: Object, // Pass field object if form field not available and field avaialble
    moduleName: String, // Pass module name and field name if field object not avaialble
    fieldName: String,
    disabled: {
      type: Boolean,
      default: false,
    },
    siteId: Number,
    hideWizard: {
      type: Boolean,
      default: false,
    },
    hideDropDown: {
      type: Boolean,
      default: false,
    },
    fetchOptionsOnLoad: {
      type: Boolean,
      default: true,
    },
    fetchOptionsMethod: {
      type: Function,
    },
    withReadings: Boolean,
    handleWizardIcon: {
      // To handle wizard icon css
      type: Boolean,
      default: true,
    },
    filterConstruction: {
      type: Function,
      default: field => {
        return field.filters
      },
    },
  },
  components: {
    FLookupField,
    FLookupFieldWizard: () => import('@/FLookupFieldWizard'),
  },
  data() {
    return {
      fieldObj: {
        isDataLoading: false,
        options: [],
        lookupModuleName: '',
        field: null,
        filters: {},
        isDisabled: false,
      },
      canShowLookupWizard: false,
    }
  },
  computed: {
    modelObj: {
      get() {
        if (!isEmpty(this.value)) {
          if (!isArray(this.value)) return parseInt(this.value)
          else {
            return this.value.map(val => parseInt(val))
          }
        }
        return this.value
      },
      set(value) {
        this.$emit('input', value)
      },
    },
  },
  created() {
    this.handleDefaultOption()
  },
  mounted() {
    let { formField, field } = this
    if (isEmpty(formField)) {
      if (!isEmpty(field)) {
        this.setField(field)
      } else {
        this.fetchField()
      }
    } else {
      this.fieldObj = formField
    }
  },
  watch: {
    canShowLookupWizard(value) {
      if (!value) {
        this.onWizardShow(value)
      }
    },
  },
  methods: {
    showLookupWizard(field, canShow) {
      this.fieldObj = field
      this.canShowLookupWizard = canShow
      this.onWizardShow(true)
    },
    setLookupFieldValue(props) {
      let { field } = props
      let { multiple, selectedItems } = field || {}

      if (multiple) {
        let valueArr = []
        selectedItems.forEach(item => {
          let { value, label } = item
          this.addOption(value, label)
          valueArr.push(value)
        })
        this.modelObj = valueArr
        this.onRecordSelected(selectedItems, valueArr)
      } else {
        let [selectedItem] = selectedItems || []
        let { value, label } = selectedItem
        this.addOption(value, label)
        this.modelObj = value
        this.onRecordSelected(selectedItem)
      }
      this.onWizardShow(false)
    },
    onWizardShow(show) {
      this.$emit('showingLookupWizard', show)
    },
    fetchField() {
      let { moduleName, fieldName } = this
      this.$util.loadModuleMeta(moduleName).then(meta => {
        let field = meta.fields.find(field => field.name === fieldName)
        this.setField(field)
      })
    },

    onRecordSelected(...args) {
      this.$emit('recordSelected', ...args)
    },
    handleDefaultOption() {
      let { label, value } = this
      if (!isEmpty(label) && !isEmpty(value)) {
        this.addOption(value, label)
      }
    },
    setField(field) {
      let { fieldObj } = this
      fieldObj.field = field
      fieldObj.multiple = field.multiple
      fieldObj.lookupModuleName = field.lookupModule.name
      if (!isArray(this.modelObj)) {
        fieldObj.selectedItems = { value: this.modelObj }
      } else {
        fieldObj.selectedItems = this.modelObj.map(id => {
          return { value: id }
        })
      }
    },

    addOption(id, label) {
      let { fieldObj } = this
      let { options } = fieldObj
      let newOptionObj = {
        label,
        value: id,
      }
      if (!isEmpty(options)) {
        if (!options.some(option => option.value === id)) {
          options.unshift(newOptionObj)
        }
      } else {
        options.push(newOptionObj)
      }
      this.$set(this.fieldObj, 'options', options)
    },
  },
}
</script>
<style lang="scss">
.lookup-wrapper {
  .handle-wizard-icon {
    .el-select {
      &.resource-search {
        .el-input {
          .el-input__prefix {
            right: 5px;
            left: 95%;
            z-index: 10;
          }
          .el-input__suffix {
            .el-icon-circle-close {
              position: absolute;
              margin-left: -1.3cm;
              z-index: 10;
            }
          }
        }
      }
    }
  }
}
</style>
