<template>
  <FieldLoader v-if="isFieldLoading" :isLoading="isFieldLoading"></FieldLoader>
  <!-- eslint-disable-next-line vue/valid-template-root -->
  <div v-else :class="hideDropDown ? 'f-lookup-chooser' : ''">
    <el-select
      ref="selectBox"
      v-model="modelObj"
      collapse-tags
      :remote="isRemote"
      filterable
      :clearable="isClearable"
      :multiple="isMultiple"
      :popperAppendToBody="popperAppendToBody"
      :remote-method="searchText => remoteMethod(searchText)"
      :loading="field.isDataLoading"
      :loading-text="$t('common._common.searching')"
      :disabled="disabled || hideDropDown"
      :class="[
        'fc-input-full-border-select2 width100 fp-picker-input',
        canShowIcons && 'resource-search',
        isMultiple && 'fc-tag',
      ]"
      :placeholder="field.placeHolderText || 'Select'"
      @change="recordSelected"
    >
      <div
        v-if="canShowIcons"
        slot="prefix"
        :class="!disabled && 'pointer'"
        class="flRight inline-flex"
      >
        <div
          v-if="!canHideLookupIcon"
          @click.stop="openLookupFieldWizard(field)"
          slot="prefix"
          :class="!disabled && 'pointer'"
          class=""
        >
          <inline-svg
            src="lookup"
            iconClass="icon icon-sm-md mT15 fc-lookup-icon"
            class="mR10"
          ></inline-svg>
        </div>
        <div
          v-if="!canHideFloorplanIcon"
          @click.stop="openFloorPlanWizard(field)"
          slot="prefix"
          :class="!disabled && 'pointer'"
          class="flRight"
        >
          <inline-svg
            src="floorpicker"
            iconClass="icon icon-sm-md mT15 fc-lookup-icon"
            class="mR10"
          ></inline-svg>
        </div>
      </div>
      <el-option
        class="fc-input-full-border-select2 width100"
        v-for="(option, index) in field.options"
        :key="`${option.value} ${index}`"
        :label="option.label"
        :value="option.value"
      >
        <span class="fL">{{ option.label }}</span>
        <span
          v-if="!$validation.isEmpty(option.secondaryLabel)"
          class="select-float-right-text13"
          >{{ option.secondaryLabel }}</span
        >
        <span
          v-if="getIsSiteDecommissioned(option)"
          v-tippy
          :title="$t('setup.decommission.decommissioned')"
          class="select-float-right-text13"
          ><fc-icon
            group="alert"
            class="fR pT10"
            name="decommissioning"
            size="16"
          ></fc-icon
        ></span>
      </el-option>
    </el-select>
    <div
      v-if="hideDropDown && !$validation.isEmpty(modelObj)"
      class="remove-icon"
    >
      <i class="el-icon-circle-close pointer f13" @click="clearData"></i>
    </div>
    <IndoorFloorPlanPicker
      v-if="openfloorplanWizard"
      :visibility.sync="openfloorplanWizard"
      :moduleName="lookupModuleName"
      :field="field"
      :inputValue="modelObj"
      :siteId="siteId"
      :moduleId="lookupModuleId"
      @onSelect="setValue"
    ></IndoorFloorPlanPicker>
  </div>
</template>
<script>
import FLookupField from '@/forms/FLookupField'
import IndoorFloorPlanPicker from 'src/pages/indoorFloorPlan/IndoorFloorPlanPicker.vue'
import { getIsSiteDecommissioned } from 'util/picklist'
export default {
  components: { IndoorFloorPlanPicker },
  extends: FLookupField,
  data() {
    return {
      recordId: null,
      canHideFloorplanIcon: false,
      openfloorplanWizard: false,
    }
  },
  created() {
    this.getIsSiteDecommissioned = getIsSiteDecommissioned
  },
  computed: {
    lookupModuleName() {
      if (this.field?.field?.lookupModule?.name) {
        return this.field.field.lookupModule.name
      }
      return null
    },
    lookupModuleId() {
      if (this.field?.field?.lookupModuleId) {
        return this.field.field.lookupModuleId
      }
      return null
    },
    canShowIcons() {
      let { canHideLookupIcon, canHideFloorplanIcon } = this
      return !canHideLookupIcon || !canHideFloorplanIcon
    },
  },
  methods: {
    setName() {},
    setValue(value, selectedObject) {
      let { options } = this.field
      if (options && options.length && value) {
        let option = options.find(rt => rt.value === value)
        if (!option && selectedObject?.label) {
          options.push({ label: selectedObject.label, value: value })
        }
      }
      this.$emit('update:model', value)
    },
    openFloorPlanWizard(field) {
      let { disabled } = this
      if (!disabled) {
        this.openfloorplanWizard = true
        this.$emit('showFloorPlanWizard', field, true)
      }
    },
  },
}
</script>
<style>
.fp-picker-input.el-select {
  background-color: #ffffff;
  border: solid 1px #d0d9e2;
  border-radius: 3px !important;
}
.fp-picker-input .el-input__inner {
  border: none !important;
  width: 70%;
  text-overflow: ellipsis;
}
.fp-picker-input .el-icon-circle-close {
  position: relative;
  right: 30px;
}
</style>
