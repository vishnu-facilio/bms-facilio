<template>
  <FieldLoader v-if="isFieldLoading" :isLoading="isFieldLoading"></FieldLoader>
  <!-- eslint-disable-next-line vue/valid-template-root -->
  <div v-else class="f-lookup-chooser f-lookup-chooser-container">
    <el-select
      ref="selectBox"
      v-model="modelObj"
      collapse-tags
      :remote="isRemote"
      filterable
      :clearable="canHideLookupIcon"
      :multiple="isMultiple"
      :popperAppendToBody="popperAppendToBody"
      :remote-method="searchText => remoteMethod(searchText)"
      :loading="field.isDataLoading"
      :loading-text="$t('common._common.searching')"
      :disabled="disabled || hideDropDown"
      :class="[
        'fc-input-full-border-select2 width100',
        (!canHideLookupIcon || canShowQuickCreateIcon) && 'resource-search',
        isMultiple && 'fc-tag',
        hideDropDown && !disabled && 'skip-disable',
      ]"
      :placeholder="field.placeHolderText || 'Select'"
      @change="recordSelected"
    >
      <div slot="prefix" :class="!disabled && 'pointer'" class="flRight d-flex">
        <div
          v-if="canShowSystemClear"
          class="flookup-remove-icon-alignment flookup-remove-icon"
          @click="clearData"
        >
          <i class="el-icon-circle-close pointer fc-lookup-icon f13"></i>
        </div>
        <div
          class="icon-class-alignment"
          v-if="!canHideLookupIcon"
          @click.stop="openLookupFieldWizard(field)"
        >
          <inline-svg
            src="lookup"
            iconClass="icon icon-sm-md fc-lookup-icon"
          ></inline-svg>
        </div>
        <div
          class="icon-class-alignment"
          v-if="canShowQuickCreateIcon"
          @click.stop="openQuickCreate()"
        >
          <el-tooltip
            effect="dark"
            :content="
              `Add
            ${moduleDisplayName}`
            "
            placement="top"
          >
            <inline-svg
              src="add-pink"
              iconClass="icon icon-sm-md fc-lookup-icon"
            ></inline-svg>
          </el-tooltip>
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
          class="select-float-right-text13 pL10"
          ><fc-icon
            group="alert"
            class="fR pT10"
            name="decommissioning"
            size="16"
          ></fc-icon
        ></span>
      </el-option>
    </el-select>
    <QuickCreateData
      v-if="showQuickCreateData"
      :canShowDialog.sync="showQuickCreateData"
      :quickCreateField="field"
      :setAddedRecord="setAddedRecord"
    ></QuickCreateData>
  </div>
</template>

<script>
import { isEmpty, isArray, isNull } from '@facilio/utils/validation'
import Constants from 'util/constant'
import { getFieldOptions, getIsSiteDecommissioned } from 'util/picklist'
import { isChooserTypeField } from 'util/field-utils'
import FieldLoader from './FieldLoader'
import isEqual from 'lodash/isEqual'

const LOOKUP_FIELD_PLACEHOLDER_MAP = {
  people: {
    value: '${LOGGED_PEOPLE}',
    label: 'Current User',
  },
  users: {
    value: '${LOGGED_USER}',
    label: 'Current User',
  },
}

export default {
  name: 'f-lookup-field',
  props: {
    model: {},
    field: {
      type: Object,
    },
    disabled: {
      type: Boolean,
    },
    siteId: {
      type: [Number, String],
    },
    categoryId: {
      type: Number,
    },
    hideLookupIcon: {
      type: Boolean,
      default: false,
    },
    popperAppendToBody: {
      required: false,
      type: Boolean,
      default: true,
    },
    fetchOptionsOnLoad: {
      type: Boolean,
      default: true,
    },
    fetchOptionsMethod: {
      type: Function,
    },
    preHookFilterConstruction: {
      type: Function,
      default: field => {
        return field.filters
      },
    },
    hideDropDown: {
      type: Boolean,
      default: false,
    },
    skipLoading: {
      type: Boolean,
      default: false,
    },
    isClearable: {
      type: Boolean,
      default: true,
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
  components: {
    FieldLoader,
    QuickCreateData: () => import('@/forms/QuickCreateData'),
  },
  data() {
    return {
      localSearch: false,
      isLoadedOnce: false,
      currentModelValue: false,
      showQuickCreateData: false,
    }
  },
  computed: {
    customOptions() {
      let { field } = this || {}
      let { lookupModule } = field || {}
      if (!isEmpty(lookupModule)) {
        let { name } = lookupModule || {}
        if (!isEmpty(LOOKUP_FIELD_PLACEHOLDER_MAP[name])) {
          return [LOOKUP_FIELD_PLACEHOLDER_MAP[name]]
        } else {
          return []
        }
      } else {
        return []
      }
    },
    modelObj: {
      get() {
        return this.model
      },
      set(value) {
        this.currentModelValue = value
        this.$emit('update:model', value)
      },
    },
    moduleDisplayName() {
      let { field } = this
      let { lookupModule } = field.field || {}
      let { displayName } = lookupModule || {}
      return displayName
    },
    isFieldLoading() {
      let { field, defaultIds, skipLoading, isLoadedOnce } = this
      return (
        !skipLoading &&
        !isLoadedOnce &&
        !isEmpty(defaultIds) &&
        field.isDataLoading
      )
    },
    isPickListTypeModule() {
      let { field } = this
      let { field: fieldObj } = field
      let { lookupModule } = fieldObj || field
      let { type } = lookupModule || {}
      // type 1 is for base entity modules
      if (type) {
        return type !== 1
      }
      return false
    },
    isRemote() {
      let { localSearch } = this
      return !localSearch
    },
    canHideLookupIcon() {
      let {
        hideLookupIcon,
        isPickListTypeModule,
        hideDropDown,
        field,
        userfilmoduleEnum,
      } = this
      let { config } = field || {}
      let canHide = false
      if (!isEmpty(config)) {
        let canShowLookupWizard = this.$getProperty(
          config,
          'canShowLookupWizard',
          null
        )
        if (!isNull(canShowLookupWizard)) {
          canHide = !canShowLookupWizard
        }
      }
      if (hideDropDown) {
        return false
      }
      if (userfilmoduleEnum === 'PICK_LIST') {
        return true
      }
      return canHide || hideLookupIcon || isPickListTypeModule
    },
    canShowQuickCreateIcon() {
      let { disabled, field } = this
      let { config } = field || {}
      let canShow = false
      if (!isEmpty(config)) {
        canShow = this.$getProperty(config, 'canShowQuickCreate')
      }
      if (disabled) {
        canShow = false
      }
      return canShow
    },
    isSiteEnabled() {
      let siteId = Number(this.$cookie.get('fc.currentSite'))
      let currentSiteId = siteId > 0 ? siteId : -1
      return !isEmpty(currentSiteId)
    },
    isMultiple() {
      let { field } = this
      return !!field.multiple
    },
    resourceModuleName() {
      let { field } = this
      let { config, lookupModuleName } = field
      let { isFiltersEnabled, filterValue } = config || {}
      if (
        isFiltersEnabled &&
        Constants.LOOKUP_FILTER_ENABLED_FIELDS.includes(lookupModuleName)
      ) {
        let moduleName = Constants.LOOKUP_FILTERS_MAP[filterValue]
        return moduleName
      }
      return null
    },
    isResourceField() {
      let { field } = this
      let { name } = field
      let isResourceField = isChooserTypeField(field) || name === 'resource'
      return isResourceField
    },
    // To prefill the existing values, we have to send default ids in picklist api
    defaultIds() {
      let { modelObj, selectedOptions } = this
      let isPlaceHoldersEnabled =
        Constants.FIELD_PLACEHOLDERS.includes(modelObj) || false

      let defaultIds = []
      let selectedOptionsArr = (selectedOptions || []).map(option =>
        parseInt(option)
      )
      if (!isEmpty(selectedOptions)) {
        defaultIds = [...selectedOptionsArr]
      } else if (!isEmpty(modelObj) && !isPlaceHoldersEnabled) {
        if (!isArray(modelObj)) {
          defaultIds = [modelObj]
        } else {
          defaultIds = [...modelObj]
        }
      }
      defaultIds = defaultIds.filter(
        val => !Constants.FIELD_PLACEHOLDERS.includes(val)
      )
      return defaultIds
    },
    operatorLookupModule() {
      let { field } = this
      let { operatorLookupModule } = field || {}
      return operatorLookupModule || {}
    },
    skipSiteFilter() {
      let { field } = this
      let { config } = field || {}
      let { skipSiteFilter = false } = config || {}
      return skipSiteFilter
    },
    canShowSystemClear() {
      let { canHideLookupIcon, isClearable, modelObj, disabled } = this
      if (!canHideLookupIcon && !disabled) {
        return isClearable && !isEmpty(modelObj)
      } else {
        return false
      }
    },
  },
  watch: {
    model(newVal) {
      let { currentModelValue, isRemote } = this
      if (!isEqual(newVal, currentModelValue)) {
        if (isRemote) {
          this.getOptions({ initialFetch: true }).then(options => {
            this.$set(this.field, 'options', options)
          })
        }

        this.$set(this, 'modelObj', newVal)
      }
    },
    siteId: {
      handler(newVal, oldVal) {
        let { isSiteEnabled, skipSiteFilter } = this
        if (!skipSiteFilter && !isSiteEnabled && newVal !== oldVal) {
          this.getOptions({ initialFetch: true }).then(options => {
            this.$set(this.field, 'options', options)
          })
        }
      },
    },

    parentModuleId: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.getOptions({ initialFetch: true }).then(options => {
            this.$set(this.field, 'options', options)
          })
        }
      },
    },

    operatorLookupModule: {
      handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          this.getOptions({ initialFetch: true }).then(options => {
            this.$set(this.field, 'options', options)
          })
        }
      },
    },
    'field.clientCriteria': {
      handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          this.getOptions({ initialFetch: true }).then(options => {
            this.$set(this.field, 'options', options)
          })
        }
      },
    },
    categoryId: {
      handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          this.getOptions({ initialFetch: true }).then(options => {
            this.$set(this.field, 'options', options)
          })
        }
      },
    },
    'field.options': {
      handler(value) {
        let { modelObj } = this
        if (!isEmpty(value)) {
          if (!isEmpty(modelObj)) {
            if (!isArray(modelObj)) {
              let selectedItem = value.find(option => option.value === modelObj)
              this.field.selectedItems = [selectedItem]
            } else {
              let selectedItems = value.filter(option =>
                modelObj.includes(option.value)
              )
              this.field.selectedItems = selectedItems
            }
          } else {
            this.field.selectedItems = []
          }
        }
      },
      immediate: true,
    },
    isLoadedOnce: {
      handler(value) {
        if (value) {
          this.$emit('optionsLoadedOnce')
        }
      },
    },
  },
  async created() {
    let { fetchOptionsOnLoad, field, modelObj, customOptions } = this

    this.currentModelValue = modelObj
    this.getIsSiteDecommissioned = getIsSiteDecommissioned

    // Have to skip fetching options initially, when module is 'resource' and filters is not enabled
    if (fetchOptionsOnLoad) {
      let options = (await this.getOptions({ initialFetch: true })) || []

      this.$set(this.field, 'options', [...customOptions, ...options])
      this.$set(this, 'isLoadedOnce', true)
    }
    this.remoteMethod = this.$helpers.debounce(async searchText => {
      let { isDataLoading } = field
      if (!isDataLoading) {
        let options = await this.getOptions({
          searchText,
        })
        this.$set(this.field, 'options', options)
      }
    }, 1000)
  },
  methods: {
    focus() {
      if (!isEmpty(this.$refs['selectBox'])) {
        this.$refs['selectBox'].focus()
      }
    },
    async getOptions(params) {
      let { searchText, initialFetch } = params
      let {
        fetchOptionsMethod,
        field,
        siteId,
        categoryId,
        isRemote,
        defaultIds,
        resourceModuleName,
        isResourceField,
        skipSiteFilter,
        selectedOptions,
      } = this
      let { filters: fieldFilters = {} } = field
      let options = []
      if (!isEmpty(resourceModuleName)) {
        field.resourceLookupModuleName = resourceModuleName
      }
      let filters = this.preHookFilterConstruction(field)
      if (!isEmpty(filters)) {
        field.filters = {
          ...fieldFilters,
          ...filters,
        }
      }

      if (!isEmpty(categoryId)) {
        field.filters = {
          ...field.filters,
          category: {
            operator: 'is',
            value: [`${categoryId}`],
          },
        }
      }

      let props = {
        field,

        searchText,
      }
      if (!skipSiteFilter) {
        props = {
          ...props,
          siteId,
        }
      }
      /*
        Have to return empty options if field is of resourcetype, if filters havent be enabled
        and if no values selected already
      */
      if (
        isResourceField &&
        isEmpty(resourceModuleName) &&
        isEmpty(defaultIds)
      ) {
        return options
      }
      if (initialFetch && isRemote) {
        let perPageCount = defaultIds.length > 50 ? defaultIds.length : 50
        props = {
          ...props,
          page: 1,
          perPage: perPageCount,
          defaultIds,
        }
        if (
          (isEmpty(resourceModuleName) && isResourceField) ||
          !isEmpty(selectedOptions)
        ) {
          // Have to fetch only selected values, which is same as the number of defaultids
          props = {
            ...props,
            perPage: defaultIds.length,
          }
        }
      }
      this.$set(field, 'isDataLoading', true)
      if (!isEmpty(fetchOptionsMethod)) {
        let { options: optionsArr = [], error } = await this.fetchOptionsMethod(
          props
        )
        if (error) {
          let { message } = error || {}
          this.$message.error(message || 'Error Occured')
        }
        options = optionsArr
      } else {
        let { options: optionsArr = [], error, meta } = await getFieldOptions(
          props
        )
        if (error) {
          let { message } = error || {}
          this.$message.error(message || 'Error Occured')
        }
        if (meta && isEmpty(searchText)) {
          let { localSearch = false } = meta
          this.$set(this, 'localSearch', localSearch)
        }
        options = optionsArr
      }
      this.$set(field, 'isDataLoading', false)
      return options
    },
    clearData() {
      this.$emit('update:model', '')
      this.recordSelected('')
    },
    recordSelected(value) {
      let {
        field,
        field: { options = [] },
        isMultiple,
      } = this
      let selectedValues = null

      if (isMultiple) {
        selectedValues = options.filter(e => (value || []).includes(e.value))
        field.selectedItems = selectedValues
      } else {
        selectedValues = options.find(e => e.value == value) || {}
        field.selectedItems = []
        if (!isEmpty(selectedValues)) {
          field.selectedItems = [selectedValues]
        }
      }
      this.$emit('recordSelected', selectedValues, field)
    },
    openLookupFieldWizard(field) {
      let { disabled } = this
      if (!disabled) {
        this.$emit('showLookupWizard', field, true)
      }
    },
    openQuickCreate() {
      let { disabled } = this
      if (!disabled) {
        this.showQuickCreateData = true
      }
    },
    setAddedRecord(props) {
      let { record } = props
      let { id } = record || {}
      let name = record.displayName || record.name || record.subject
      let { field } = this
      field.selectedItems = [{ value: id, label: name }]
      this.$emit('setLookupFieldValue', { field: { ...field } })
    },
  },
}
</script>

<style lang="scss">
.search-input-comp {
  .resource-search {
    .el-input__inner {
      padding-left: 15px !important;
    }
  }
}
.fc-lookup-icon {
  color: #000000;
}
.f-lookup-chooser {
  position: relative;
  &:hover {
    .flookup-remove-icon {
      display: flex;
    }
  }
  .resource-search {
    .el-input {
      .el-input__prefix {
        right: 5px;
        left: 95%;
        z-index: 10;
      }
      .el-input__suffix {
        .el-select__caret {
          &.el-icon-arrow-up {
            display: none;
          }
        }
        .el-icon-circle-close {
          padding-left: 30px;
        }
      }
    }
  }
  .skip-disable {
    .el-input__inner {
      &:disabled {
        cursor: auto !important;
        background-color: #fff;
        color: #324056;
      }
    }
  }
}
.f-lookup-chooser-container {
  .icon-sm {
    height: 10px;
    width: 10px;
  }

  .flookup-remove-icon {
    display: none;
  }

  .flookup-remove-icon-alignment {
    height: 100%;
    align-items: center;
    justify-content: center;
    margin: 1px 5px;
  }

  .icon-class-alignment {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 1px 5px;
  }

  .flRight {
    height: 100%;
  }
}
</style>
