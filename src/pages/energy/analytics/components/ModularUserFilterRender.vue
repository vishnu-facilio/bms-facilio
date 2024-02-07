<template>
  <div class="report-userFilter">
    <div v-if="!compactFilter">
      <div
        style="display:inline-block;"
        class="mL20 pT10"
        v-for="(filter, filterIdx) in filterConfiguration"
        :key="filterIdx"
      >
        <!-- <div class="text-left">{{filter.name}}</div> -->
        <FLookupFieldWrapper
          v-model="currentFilterValue[filterIdx]"
          v-if="filter.component.componentType === 1 && filter.lookupModuleName"
          :field="{
            lookupModule: { name: filter.lookupModuleName },
            multiple: false,
            placeHolderText: 'All',
          }"
          :fetchOptionsOnLoad="true"
          @recordSelected="setLookupFilter($event, filter.fieldId)"
          @showingLookupWizard="showingLookupWizard = true"
          class="fc-department-icon fc-department-input rm-arrow"
        >
        </FLookupFieldWrapper>
        <el-select
          :placeholder="filter.name"
          class="fc-input-full-border-h35"
          :popper-append-to-body="false"
          v-model="dataStructures[filter.fieldId]"
          @change="setFilter($event, filter.fieldId)"
          v-if="
            filter.component.componentType === 1 && !filter.lookupModuleName
          "
          filterable
        >
          <el-option
            v-for="(option, optionIdx) in getDynamicFieldList(filter)"
            :key="optionIdx"
            :label="option.label"
            :value="option.name"
          ></el-option>
        </el-select>
        <el-select
          :placholder="filter.name"
          class="fc-input-full-border-h35"
          :popper-append-to-body="false"
          :collapse-tags="true"
          v-model="dataStructures[filter.fieldId]"
          @change="setFilter($event, filter.fieldId)"
          v-if="filter.component.componentType === 2"
          multiple
          filterable
        >
          <el-option
            v-for="(option, optionIdx) in getDynamicFieldList(filter)"
            :key="optionIdx"
            :label="option.label"
            :value="option.name"
          ></el-option>
        </el-select>
      </div>
    </div>

    <div v-else>
      <el-popover
        placement="bottom-start"
        trigger="click"
        width="200"
        v-model="masterToggler"
      >
        <el-popover
          v-for="(cFilter, cFilterIdx) in filterConfiguration"
          :key="cFilterIdx"
          placement="right"
          trigger="click"
          width="145"
          v-model="fieldPopoverToggles[cFilter.fieldId]"
          popper-class="filter-priority-popover"
        >
          <div
            class="pointer fc-black-12 text-left fw4 priority-filter-hover"
            v-for="(cOption, cOptionsIdx) in getDynamicFieldList(cFilter)"
            @click="
              ;(dataStructures[cFilter.fieldId] = cOption.name),
                (fieldPopoverToggles[cFilter.fieldId] = false),
                setFilter(cOption.name, cFilter.fieldId)
            "
            :key="cOptionsIdx"
          >
            {{ cOption.label }}
          </div>
          <div class="pointer filter-select-txt" slot="reference">
            {{ cFilter.name }}
            <i
              class="el-icon-arrow-right text-right fR f18"
              style="color: #d0d9e2; font-sizze: 14px;"
            ></i>
          </div>
        </el-popover>
        <div
          slot="reference"
          class="pT10 user-filter-txt clearboth flex-middle"
        >
          <img src="~assets/filter-box.svg" class="mR10" />Filters<i
            class="el-icon-arrow-down pointer"
            @click="masterToggler = true"
          ></i>
        </div>
      </el-popover>
    </div>
  </div>
</template>

<script>
import ModularUserFilterMixin from 'src/pages/report/mixins/modularUserFilter'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import { isEmpty } from '@facilio/utils/validation'
export default {
  components: {
    FLookupFieldWrapper,
  },
  watch: {
    filterConfiguration: {
      handler: function(newValue, oldValue) {
        if (newValue) {
          this.initAll()
          this.checkChangeinConfig(newValue, oldValue)
        }
      },
      deep: true,
    },
  },
  props: ['filterConfiguration', 'widgetConfig'],
  mixins: [ModularUserFilterMixin],
  created() {
    this.initAll()
  },
  computed: {
    compactFilter() {
      if (this.widgetConfig) {
        if (
          this.widgetConfig.actualWidth < 5.5 ||
          this.widgetConfig.widget.layout.width < 40
        ) {
          return true
        } else {
          return false
        }
      }
      return false
    },
  },
  methods: {
    getDynamicFieldList(filter) {
      let fields = []
      fields.push({
        label: 'All',
        name: 'all',
      })
      if (filter.chooseValue.values && filter.allValues) {
        for (let value of filter.chooseValue.values) {
          //TO Fix , Use find function rather than filter when searching for aa single item by id
          let labellist = filter.allValues.filter(
            val => Object.keys(val)[0] === value
          )
          if (labellist.length > 0) {
            fields.push({
              label: labellist[0][value],
              name: value,
            })
          }
        }
      }
      if (filter.chooseValue.otherEnabled === true) {
        fields.push({
          label: 'Others',
          name: 'others',
        })
      }
      return fields
    },
    setLookupFilter(val, fieldId) {
      if (this.compactFilter) {
        this.masterToggler = false
      }
      let filterObj = this.filterConfiguration.filter(
        filter => filter.fieldId === fieldId
      )[0]
      if (filterObj.component.componentType === 1) {
        if (val?.value === '') {
          this.$set(this.dataStructures, fieldId, 'all')
        } else {
          this.$set(this.dataStructures, fieldId, val)
        }
      }
      this.showingLookupWizard = false
      this.emitLookupFilterChangeValue()
    },
    setFilter(val, fieldId) {
      if (this.compactFilter) {
        this.masterToggler = false
      }
      let filterObj = this.filterConfiguration.filter(
        filter => filter.fieldId === fieldId
      )[0]
      if (filterObj.component.componentType === 2) {
        if (val.includes('all')) {
          if (this.allToggles[fieldId] === false) {
            this.allToggles[fieldId] = true
            let values = []
            for (let field of this.getDynamicFieldList(filterObj)) {
              values.push(field.name)
            }
            this.dataStructures[fieldId] = values
          } else {
            this.allToggles[fieldId] = false
            let indexofAll = val.indexOf('all')
            val.splice(indexofAll, 1)
            this.dataStructures[fieldId] = val
          }
        } else {
          //When all is unselected , if default value exists ,select it ,else select the first value, even when type=1(all) ,chooseValues will contain valueList but for all items
          if (this.allToggles[fieldId] === true) {
            if (filterObj.defaultValues.length !== 0) {
              this.dataStructures[fieldId] = filterObj.defaultValues
            } else {
              this.dataStructures[fieldId] = [filterObj.chooseValue.values[0]]
            }
            this.allToggles[fieldId] = false
          } else {
            //TO DO no change in 'all' toggle,only one value is selected , don't allow to unselect it  ,

            this.$set(this.dataStructures, fieldId, val)
          }
        }
      } else if (filterObj.component.componentType === 1) {
        this.$set(this.dataStructures, fieldId, val)
      }
      this.emitChangedValue()
    },
    isDisabled(filter) {
      if (filter.chooseValue.type === 1) {
        return true
      } else {
        return false
      }
    },
    checkChangeinConfig(newValue, oldValue) {
      // this function checks for change in component types and initializes them
      for (let filter of newValue) {
        let oldComp = oldValue.filter(
          userFilter => userFilter.fieldId === filter.fieldId
        )[0]
        if (
          oldComp.component.componentType !== filter.component.componentType
        ) {
          this.initSpecific(filter.fieldId, filter.component.componentType)
        } else if (
          oldComp.chooseValue.values.length !== filter.chooseValue.values.length
        ) {
          this.loadDefaultValues(filter.fieldId, filter.component.componentType)
        } else if (
          oldComp.defaultValues[oldComp.defaultValues.length - 1] !==
          filter.defaultValues[filter.defaultValues.length - 1]
        ) {
          this.loadDefaultValues(filter.fieldId, filter.component.componentType)
        }
      }
    },
    initSpecific(fieldId, componentType) {
      this.$set(
        this.dataStructures,
        fieldId,
        this.dataStructureForComponent[componentType]
      )
      this.loadDefaultValues(fieldId, componentType)
    },
    initAll() {
      if (typeof this.filterConfiguration !== 'undefined') {
        this.initToggles()
        for (let filter of this.filterConfiguration) {
          if (this.dataStructures[filter.fieldId]) {
            return
          } else {
            if (
              this.componentType[filter.component.componentType].label ===
              'singleselect'
            ) {
              this.$set(
                this.dataStructures,
                filter.fieldId,
                this.dataStructureForComponent[filter.component.componentType]
              )
              this.loadDefaultValues(
                filter.fieldId,
                filter.component.componentType
              )
            } else if (
              this.componentType[filter.component.componentType].label ===
              'multipleselect'
            ) {
              this.$set(
                this.dataStructures,
                filter.fieldId,
                this.dataStructureForComponent[filter.component.componentType]
              )
              this.loadDefaultValues(
                filter.fieldId,
                filter.component.componentType
              )
            }
          }
        }
      }
    },
    initToggles() {
      for (let config of this.filterConfiguration) {
        if (!this.allToggles.hasOwnProperty(config.fieldId)) {
          this.allToggles[config.fieldId] = false
        }
        if (!this.fieldPopoverToggles.hasOwnProperty(config.fieldId)) {
          this.$set(this.fieldPopoverToggles, config.fieldId, false)
        }
      }
    },
    loadDefaultValues(fieldId, componentType) {
      let filterObject = this.filterConfiguration.filter(
        filterObj => filterObj.fieldId === fieldId
      )

      if (filterObject && filterObject.length !== 0) {
        filterObject = filterObject[0]
      }
      if (componentType === 1) {
        this.$set(
          this.dataStructures,
          fieldId,
          filterObject?.defaultValues?.length > 0
            ? filterObject.defaultValues[0]
            : 'all'
        )
        this.allToggles[fieldId] = this.dataStructures[fieldId] === 'all'
      } else if (componentType === 2) {
        this.$set(
          this.dataStructures,
          fieldId,
          filterObject.defaultValues.length > 0
            ? filterObject.defaultValues
            : 'all'
        )
        this.allToggles[fieldId] = this.dataStructures[fieldId].includes('all')

        // special handling for multi select all cases
        if (this.allToggles[fieldId] === true) {
          this.dataStructures[fieldId] = []
          for (let id of this.getDynamicFieldList(filterObject)) {
            this.dataStructures[fieldId].push(id.name)
          }
        }
      }
    },
    emitLookupFilterChangeValue() {
      let temp = this.filterConfiguration
      for (let config of this.filterConfiguration) {
        let value = this.dataStructures[config.fieldId]
        if (Array.isArray(value)) {
          if (value.includes('all')) {
            config.values = ['all']
          } else {
            config.values = value
          }
        } else if (value?.value) {
          config.values = [value.value.toString()]
        } else if (isEmpty(value)) {
          config.values = ['all']
        }
      }
      this.$emit('emitChangedValue', this.filterConfiguration)
    },
    emitChangedValue() {
      let temp = this.filterConfiguration
      for (let config of this.filterConfiguration) {
        let value = this.dataStructures[config.fieldId]
        if (Array.isArray(value)) {
          if (value.includes('all')) {
            config.values = ['all']
          } else {
            config.values = value
          }
        } else {
          config.values = [value]
        }
      }
      this.$emit('emitChangedValue', this.filterConfiguration)
    },
  },
  data() {
    return {
      dataStructures: {},
      allToggles: {},
      masterToggler: false,
      fieldPopoverToggles: {},
      showingLookupWizard: false,
      currentFilterValue: [],
    }
  },
}
</script>

<style>
.user-filter-txt,
.user-filter-txt .el-icon-arrow-down {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1.5px;
  color: #ee518f;
  text-transform: uppercase;
  margin-left: 17px;
}
.filter-select-txt {
  padding: 10px;
  border: 1px solid #e2ebf4;
  border-radius: 3px;
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.5px;
  color: #324056;
  margin-bottom: 10px;
}
.priority-filter-hover {
  padding: 10px 20px;
  font-weight: 500;
}
.priority-filter-hover:hover {
  background-color: #f5f7f4;
  background: aliceblue;
  color: #39b2c2;
  font-weight: 600;
}
.filter-priority-popover {
  padding: 0;
}
.report-userFilter .el-select-dropdown__item {
  text-align: left;
}
</style>
