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
      :userfilmoduleEnum="userfilmoduleEnum"
      :preHookFilterConstruction="filterConstruction"
      :parentModuleId="parentModuleId"
      :selectedOptions="selectedOptions"
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
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import FLookupField from '@/forms/FLookupFieldWithDefaultList'
import { isEmpty, isArray } from '@facilio/utils/validation'

export default {
  props: {
    defaultList: {
      type: Array,
      default: () => [],
    },
    criteria: {
      type: Object,
    },
    userfilmoduleEnum: {
      type: String,
    },
    parentModuleId: {
      type: Number,
    },
    selectedOptions: {
      type: Array,
    },
  },
  extends: FLookupFieldWrapper,
  components: { FLookupField },
  computed: {
    // hideWizard(){
    //   if(this.userfilmoduleEnum == 'PICK_LIST'){
    //     return true
    //   }
    //   return false
    // },
    modelObj: {
      // default list value handled
      get() {
        if (!isEmpty(this.value)) {
          if (!isArray(this.value)) {
            if (this.defaultListValues.includes(this.value)) {
              return this.value
            }
            return parseInt(this.value)
          } else {
            return this.value.map(val => {
              if (this.defaultListValues.includes(val)) {
                return val
              }
              return parseInt(val)
            })
          }
        }
        return this.value
      },
      set(value) {
        this.$emit('input', value)
      },
    },
    defaultListValues() {
      return this.defaultList.map(rt => rt.value)
    },
  },
  methods: {
    setField(field) {
      let { fieldObj } = this
      fieldObj.field = field
      fieldObj.multiple = field.multiple
      fieldObj.lookupModuleName = field.lookupModule.name
      fieldObj.clientCriteria = this.criteria
      if (!isArray(this.modelObj)) {
        if (!this.defaultListValues.includes(this.modelObj)) {
          fieldObj.selectedItems = { value: this.modelObj }
        }
      } else {
        fieldObj.selectedItems = this.modelObj.map(id => {
          if (!this.defaultListValues.includes(id)) {
            return { value: id }
          }
        })
      }
    },
  },
}
</script>
