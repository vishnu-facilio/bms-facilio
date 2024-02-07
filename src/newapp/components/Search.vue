<template>
  <div class="fc-black-small-txt-12">
    <div v-if="!showSearch" @click.stop="search()">
      <i
        class="el-icon-search fc-black-2 f16 pointer fw-bold"
        @click.stop="search()"
      ></i>
    </div>

    <outside-click
      v-if="showSearch"
      :visibility="showSearch"
      @onOutsideClick="close()"
    >
      <div class="inline-flex hideSearch fc-subheader-right-search">
        <el-select
          v-model="fieldName"
          filterable
          placeholder="Select"
          @change="onFilterChange()"
          class="search-select-comp"
        >
          <el-option
            v-for="(label, value) in filterObjectsData"
            :key="value"
            :label="label.label"
            :value="value"
          ></el-option>
        </el-select>

        <div
          v-if="
            !$validation.isEmpty($getProperty(filterObjectsData, fieldName))
          "
        >
          <div v-if="currentFilterProxy.displayType === 'select'">
            <template v-if="currentFilterProxy.type === 'date'">
              <el-select
                v-if="currentFilterValueProxy !== '20'"
                v-model="currentFilterValueProxy"
                filterable
                placeholder="Select"
                class="search-select-comp2 search-border-left fc-tag fc-search-bar-select"
                popper-class="f-search-popover"
                @change="applyDateFilter()"
              >
                <el-option
                  v-for="(label, value) in currentFilterProxy.options"
                  :key="value"
                  :label="label"
                  :value="value"
                ></el-option>

                <el-option
                  v-if="!currentFilterProxy.hideDateRange"
                  label="Custom"
                  value="20"
                ></el-option>
              </el-select>

              <f-date-picker
                v-show="currentFilterValueProxy === '20'"
                ref="fdate"
                class="mT10 new-search-date-filter search-date-picker"
                v-model="currentFilterProxy.dateRange"
                :type="'datetimerange'"
                :value-format="'timestamp'"
                :format="'dd-MM-yyyy HH:mm'"
                @change="applyFilter()"
              ></f-date-picker>
            </template>
            <el-select
              v-else
              ref="selectDropdown"
              v-model="currentFilterValueProxy"
              multiple
              collapse-tags
              filterable
              :disabled="canDisableField"
              :placeholder="conditionsPlaceHolder"
              class="search-select-comp2 search-border-left fc-tag"
              popper-class="f-search-popover"
            >
              <el-option
                v-for="(label, value) in currentFilterProxy.options"
                :key="`option-${value}`"
                :label="label"
                :value="value"
              ></el-option>

              <div class="search-select-filter-btn pT5">
                <el-button
                  class="btn-green-full filter-footer-btn fw-bold"
                  @click="applyFilter()"
                  >DONE</el-button
                >
              </div>
            </el-select>
          </div>

          <template
            v-if="
              ['resourceType', 'lookup'].includes(
                currentFilterProxy.displayType
              ) && !currentFilterProxy.specialType
            "
          >
            <FLookupFieldWrapper
              class="search-input-comp"
              :key="`lookup-${fieldName}`"
              v-model="currentFilterValueProxy"
              :field="getFieldObj(currentFilterProxy, fieldName)"
              :label="currentFilterProxy.selectedLabel"
              :hideWizard="hideLookupWizard"
              :hideDropDown="hideDropDown"
              @recordSelected="setLookUpFilter"
              @showingLookupWizard="value => (showingLookupWizard = value)"
            ></FLookupFieldWrapper>
          </template>

          <template v-if="currentFilterProxy.displayType === 'string'">
            <el-input
              @click.native.stop.prevent="() => {}"
              v-model="currentFilterValueProxy"
              :disabled="canDisableField"
              :placeholder="conditionsPlaceHolder"
              clearable
              class="input-with-select search-input-comp search-icon-set"
              @keyup.native.enter="applyFilter()"
              :autofocus="true"
              type="search"
              :prefix-icon="canDisableField ? '' : 'el-icon-search'"
              ref="f-search"
            ></el-input>
          </template>

          <div
            v-if="
              ['number', 'decimal'].includes(currentFilterProxy.displayType)
            "
          >
            <el-input
              @click.native.stop.prevent="() => {}"
              type="number"
              v-model="currentFilterValueProxy"
              :disabled="canDisableField"
              :placeholder="conditionsPlaceHolder"
              clearable
              class="input-with-select search-input-comp search-icon-set"
              @keyup.native.enter="applyFilter()"
              :autofocus="true"
              :prefix-icon="canDisableField ? '' : 'el-icon-search'"
              ref="f-search"
            ></el-input>
          </div>

          <div
            v-if="['asset', 'space'].includes(currentFilterProxy.displayType)"
            class="resource-list"
          >
            <el-select
              filterable
              class="multi resource-list el-input-textbox-full-border search-select-comp2 down-arrow-remove search-border-left fc-tag width100"
              multiple
              collapse-tags
              :value="!$validation.isEmpty(currentFilterValueProxy) ? [1] : []"
              disabled
            >
              <el-option :label="spaceAssetFilter" :value="1"></el-option>
            </el-select>
            <i
              v-if="!canDisableField"
              @click="spaceAssetProps(currentFilterProxy)"
              class="el-input__icon el-icon-search filter-space-search-icon3"
            ></i>
          </div>
        </div>

        <el-button
          v-if="!hideOperatorDialog"
          class="pointer user-select-none filter-btn"
          @click.stop="openFilterConditionPopup()"
          :disabled="
            currentFilterProxy.type === 'date' &&
              currentFilterValueProxy !== '20'
          "
        >
          <img src="~assets/filter.svg" style="width: 18px;" />
        </el-button>

        <filter-condition
          v-if="showFilterCondition"
          :fieldName="fieldName"
          :hideDropDownForLookup="hideDropDown"
          @close="showFilterCondition = false"
        ></filter-condition>
      </div>
    </outside-click>

    <space-asset-multi-chooser
      v-if="chooserVisibility"
      :showAsset="currentFilterProxy.displayType === 'asset'"
      @associate="associateResource($event, currentFilterProxy)"
      :initialValues="initialValues"
      :visibility.sync="chooserVisibility"
      :hideBanner="true"
      class="fc-input-full-border-select2"
    ></space-asset-multi-chooser>
  </div>
</template>
<script>
import OutsideClick from '@/OutsideClick'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import { isEmpty } from '@facilio/utils/validation'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import FilterCondition from './FilterCondition'
import SearchTagMixin from './SearchTagMixin'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'

export default {
  name: 'Search',
  props: [
    'config',
    'moduleName',
    'defaultFilter',
    'includeAllMetaFields',
    'hideOperatorDialog',
  ],
  components: {
    OutsideClick,
    FDatePicker,
    SpaceAssetMultiChooser,
    FilterCondition,
    FLookupFieldWrapper,
  },

  mixins: [SearchTagMixin],

  data() {
    return {
      fieldName: null,
      showFilterCondition: false,
      chooserVisibility: false,
      initialValues: null,
      showingLookupWizard: false,
    }
  },

  computed: {
    currentFilterProxy: {
      get() {
        let { fieldName, filterObjectsData } = this
        return filterObjectsData[fieldName] || {}
      },
      set(value) {
        let { filterObjectsData, fieldName } = this
        this.$set(filterObjectsData, fieldName, value)
      },
    },

    currentFilterValueProxy: {
      get() {
        let { currentFilterProxy } = this
        return currentFilterProxy.value || []
      },
      set(value) {
        this.currentFilterProxy = { ...this.currentFilterProxy, value }
      },
    },

    spaceAssetFilter() {
      let { currentFilterProxy } = this
      let filter = ''

      if (!isEmpty(currentFilterProxy.value)) {
        filter = currentFilterProxy.value.length

        if (currentFilterProxy.displayType === 'asset') filter += 'Asset'
        else filter += 'Space'

        if (currentFilterProxy.value.length > 1) filter += 's'
      }

      return filter
    },

    conditionsPlaceHolder() {
      let { currentFilterProxy } = this
      let { operatorId } = currentFilterProxy || {}

      if (operatorId === 1) return 'is Empty'
      else if (operatorId === 2) return 'is Not Empty'
      else return 'Search...'
    },

    canDisableField() {
      let { currentFilterProxy } = this
      let { operatorId } = currentFilterProxy
      let OPERATOR_CHECK = {
        isEmpty: 1,
        isNotEmpty: 2,
      }

      return [OPERATOR_CHECK.isEmpty, OPERATOR_CHECK.isNotEmpty].includes(
        operatorId
      )
    },
    hideLookupWizard() {
      return false
    },
    hideDropDown() {
      let { currentFilterProxy, fieldName } = this
      let { name } = this.getFieldObj(currentFilterProxy, fieldName) || {}

      return name === 'resource'
    },
  },

  watch: {
    config: {
      handler(value, oldValue) {
        if (JSON.stringify(value) !== JSON.stringify(oldValue)) {
          this.initSearchFields()
        }
      },
    },

    defaultFilter: {
      handler(value) {
        this.fieldName = value
      },
      immediate: true,
    },

    appliedFilters(value, oldValue) {
      if (JSON.stringify(value) !== JSON.stringify(oldValue)) {
        this.setFilterApplied()
        this.resetFilterData()
      }
    },
  },

  methods: {
    onFilterChange() {
      if (this.showFilterCondition) {
        let filterObj = this.currentFilterProxy
        this.setOperatorId(filterObj, this.fieldName)
      }
    },

    openFilterConditionPopup() {
      this.showFilterCondition = true
      this.setOperatorId()
    },

    applyDateFilter() {
      let filterData = this.currentFilterProxy

      if (filterData && filterData.value !== '20') {
        this.applyFilter()
      } else {
        this.$refs['fdate'].focus()
      }
    },

    associateResource(resource, field) {
      this.chooserVisibility = false

      field.value = resource.resourceList
        ? resource.resourceList.map(res => res.id)
        : []

      this.applyFilter()
    },

    search() {
      this.$store.dispatch('search/toggleActiveStatus', true)
    },

    close() {
      if (!this.showingLookupWizard && !this.showFilterCondition) {
        this.$store.dispatch('search/toggleActiveStatus', false)
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.filter-wizard {
  display: flex;
  top: 0px;
}

.search-icon-set {
  height: 80%;
}

.new-search-date-filter {
  border-radius: 0;
  margin-top: 0;
}

.filter-space-search-icon3 {
  position: absolute;
  top: 0;
  right: 47px;
  font-size: 16px;
  color: #333;
}

.fc-search-bar-select {
  border-left: none !important;
}

.fc-subheader-right-search {
  position: relative;
  bottom: 11px;
  right: -2px;
}
</style>
<style lang="scss">
.f-search-popover .el-select-dropdown__list {
  padding-bottom: 70px;
}
.down-arrow-remove .el-icon-arrow-up:before,
.el-icon-arrow-up:after {
  display: none;
}
.down-arrow-remove .el-input.is-disabled .el-input__inner {
  cursor: pointer !important;
  opacity: 1 !important;
}
.f-search-popover .el-select-dropdown__item {
  font-weight: 400;
}
.search-border-left .el-input__inner {
  border-left: 1px solid #d8dce5 !important;
}
.resource-list .multi .el-select .el-tag__close.el-icon-close,
.resource-list .multi .el-input__suffix {
  display: none;
}
</style>
