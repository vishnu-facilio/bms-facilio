<template>
  <div class="db-user-filter-manager">
    <el-dialog
      :visible="visibility"
      class="db-user-filter-manager-dialog"
      :show-close="false"
      :append-to-body="true"
      title="Dashboard Filters"
      width="60%"
    >
      <div class="body">
        <spinner v-if="loading" :show="loading" :size="80"></spinner>
        <div
          v-else-if="filterGroups.length == 0"
          class="user-filter-module-empty"
        >
          <div
            class="col-12 flex-middle justify-content-center flex-direction-column height60vh pT30"
          >
            <inline-svg
              src="svgs/empty-dashboard"
              iconClass="text-center icon-xxxlg"
            ></inline-svg>
            <div class="fc-black-24 pT20">
              There are no filter supported widgets in dashboard
            </div>
            <div class="fc-grayish f18 pT10">
              Add supported widgets to configure dashboard filters here
            </div>
          </div>
        </div>

        <div v-else class="filter-config-container">
          <div class="filter-config-left">
            <div class="filter-config-accordian">
              <div
                class="accordian-item"
                v-for="(module, moduleIndex) in filterGroups"
                :key="moduleIndex"
              >
                <div
                  class="accordian-item-header pointer"
                  @click="accordianClick(moduleIndex)"
                >
                  {{ module.displayName }}
                  <i
                    class="accordian-arrow"
                    :class="[{ expanded: module.isExpanded }]"
                  ></i>
                </div>
                <div class="accordian-item-body" v-if="module.isExpanded">
                  <div class="module-fields-container">
                    <div
                      class="module-field"
                      v-for="(field, fieldIndex) in module.fields"
                      :key="fieldIndex"
                    >
                      <el-checkbox
                        style="border: none;"
                        v-model="field.isSelected"
                        :label="field.displayName"
                        @change="
                          toggleOptionSelection(moduleIndex, fieldIndex, $event)
                        "
                      ></el-checkbox>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="filter-config-right">
            <div class="selcted-filters-title">
              SELECTED FILTERS
            </div>
            <draggable :list="selectedOptions" v-bind="fieldSectionDragOptions">
              <div
                v-for="(selectedOption, index) in selectedOptions"
                :key="index"
                class="mB10 mT15"
              >
                <div class="field-row">
                  <div class="task-handle mR10 pointer">
                    <img src="~assets/drag-grey.svg" />
                  </div>

                  <div class="mT-auto mB-auto">
                    {{ selectedOption.displayName || '' }}
                  </div>

                  <div class="mL-auto mT-auto mB-auto">
                    <div class="mR10 inline">
                      <i
                        class="el-icon-delete pointer trash-icon"
                        @click="removeSelectedOption(selectedOption, index)"
                      ></i>
                    </div>
                  </div>
                </div>
                <div class="applies-to-container">
                  <div class="applies-to-title">Applies to</div>
                  <template
                    v-for="(applicableModule,
                    moduleIndex) in selectedOption.appliesToModules"
                  >
                    <div
                      class="applies-to-field"
                      v-for="(applicableField,
                      fieldIndex) in applicableModule.appliesToFields"
                      :key="moduleIndex + '-' + fieldIndex"
                    >
                      <span class="applies-to-field-field-name">
                        {{ applicableField.displayName }}</span
                      >
                      <span class="applies-to-field-in">IN</span>
                      <span class="applies-to-field-module-name">{{
                        applicableModule.displayName
                      }}</span>
                    </div>
                  </template>
                </div>
              </div>
            </draggable>
          </div>
        </div>
      </div>
      <div
        class="dialog-save-cancel"
        v-if="!loading && filterGroups.length == 0"
      >
        <el-button
          type="primary"
          class="modal-btn-save width100"
          @click="saveFilterConfig"
          >Done</el-button
        >
      </div>
      <div class="dialog-save-cancel" v-else>
        <el-button class="modal-btn-cancel" @click="closeDialog">
          Cancel</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="saveFilterConfig"
          >Done</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import draggable from 'vuedraggable'
import { deepCloneObject } from 'util/utility-methods'

const removeDuplicatesFromArrayOfObj = (sourceArray, fieldToCompare) => {
  const uniqueArray = Array.from(
    new Set(sourceArray.map(e => e[fieldToCompare]))
  ).map(valueToCompare => {
    return sourceArray.find(e => e[fieldToCompare] === valueToCompare)
  })

  return uniqueArray
}

export default {
  components: {
    draggable,
  },
  props: [
    'userFilterFields',
    'userFilterModules',
    'visibility',
    'dashboardId',
    'dashboardTabId',
  ],
  created() {
    this.getDbModules().then(() => {
      //for edit case , initialize the selection of fields
      this.loading = false
    })
  },
  data() {
    return {
      loading: true,
      filterGroups: [],
      selectedOptions: [], //list holding selected options[option ->Field object or module object]
      fieldSectionDragOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    saveFilterConfig() {
      //remove applies to mods from object, causing circular ref when cloning elsewhere
      this.selectedOptions.forEach(e => {
        e.appliesToModules = []
      })
      this.$emit('filterConfigChanged', this.selectedOptions)
    },

    // remove from selected Option list
    removeSelectedOption(option, selectedOptionIndex) {
      this.filterGroups.forEach(module => {
        module.fields.forEach(e => {
          //if selectd option is module ,check name eqiuality, if a field check fieldID equality
          if (option.filterType == 'FIELD' && e.fieldId == option.fieldId) {
            e.isSelected = false
          } else if (option.filterType == 'MODULE' && e.name == option.name) {
            e.isSelected = false
          }
        })
      })
      this.selectedOptions.splice(selectedOptionIndex, 1)
    },
    // add or remove from left pane checkbox
    toggleOptionSelection(optionGroupIndex, optionIndex, value) {
      let selectedOption = this.filterGroups[optionGroupIndex].fields[
        optionIndex
      ]

      if (value) {
        this.selectedOptions.push(selectedOption)
      } else {
        let optionIndex = this.selectedOptions.findIndex(e => {
          if (selectedOption.filterType == 'FIELD') {
            return e.fieldId == selectedOption.fieldId
          } else if (selectedOption.filterType == 'MODULE') {
            return e.name == selectedOption.name
          }
        })

        this.selectedOptions.splice(optionIndex, 1)
      }
    },
    accordianClick(index) {
      if (this.filterGroups[index].isExpanded) {
        this.filterGroups[index].isExpanded = false
        return
      }

      this.filterGroups.forEach(module => {
        module.isExpanded = false
      })
      this.filterGroups[index].isExpanded = true
    },
    async getDbModules() {
      let params = {}
      if (this.dashboardTabId && this.dashboardTabId != -1) {
        params.dashboardTabId = this.dashboardTabId
      } else {
        params.dashboardId = this.dashboardId
      }
      let {
        data: { modules: moduleNames = [] },
        error,
      } = await API.get('/v2/dashboardFilter/getModules', params)

      let promises = []

      moduleNames.forEach(moduleName => {
        promises.push(API.get(`v2/modules/meta/${moduleName}`))
      })
      let resps = await Promise.all(promises)

      let moduleResps = resps.map(resp => resp.data.meta)
      //collect lookup fields seperately as common module
      //collect enum fields in their own modules

      this.filterGroups = []

      // Collect  lookedup modules of  lookupfields FROM all module into an array
      let allCommonFilterModules = moduleResps.reduce(
        (accumulator, currentModule) => {
          let lookupFields = currentModule.fields.filter(
            field => field.dataTypeEnum == 'LOOKUP'
          )
          let lookupModules = lookupFields.map(
            lookupField => lookupField.lookupModule
          )

          accumulator.push(...lookupModules)
          return accumulator
        },
        []
      )
      //remove duplicates. Vendor may have been looked up by Workorder.Vendor and Asset.Vendor
      let uniqueCommonFilterModules = removeDuplicatesFromArrayOfObj(
        allCommonFilterModules,
        'name'
      )

      //remove Resource and Basespace  modules from the  list  and replace them with  'Asset and building 'for resource and 'building' for basespace
      let resourceModuleIndex = -1
      let basespaceModuleIndex = -1
      let buildingModule = await this.getModuleMeta('building')
      let floorModule = await this.getModuleMeta('floor')
      let spaceModule = await this.getModuleMeta('space')
      let assetModule = await this.getModuleMeta('asset')

      /* DONT change order of finding index and splicing */
      resourceModuleIndex = uniqueCommonFilterModules.findIndex(
        module => module.name == 'resource'
      )

      if (resourceModuleIndex != -1) {
        uniqueCommonFilterModules.splice(
          resourceModuleIndex,
          1,
          buildingModule,
          floorModule,
          spaceModule,
          assetModule
        )
      }
      basespaceModuleIndex = uniqueCommonFilterModules.findIndex(
        module => module.name == 'basespace'
      )

      if (basespaceModuleIndex != -1) {
        uniqueCommonFilterModules.splice(
          basespaceModuleIndex,
          1,
          buildingModule
        )
      }

      //again remove duplicates introduced by replacing resource and basespace
      uniqueCommonFilterModules = removeDuplicatesFromArrayOfObj(
        uniqueCommonFilterModules,
        'name'
      )

      uniqueCommonFilterModules.forEach(module => {
        //Add filterType and  sync selection State (previouly saved filter)

        module.isSelected = false
        module.filterType = 'MODULE'

        if (
          this.userFilterModules &&
          this.userFilterModules.find(e => e.name == module.name)
        ) {
          module.isSelected = true
          this.selectedOptions.push(module)
        }
      })

      // iterate the unique lookup modules and set ->appliesToModules:[modules]->{applicableFields:[fieldList]} into the unique module objects

      uniqueCommonFilterModules.forEach(filterModule => {
        filterModule.appliesToModules = []
        moduleResps.forEach(module => {
          let allLookupFields = module.fields.filter(
            field => field.dataTypeEnum == 'LOOKUP'
          )

          let applicableFields = allLookupFields.filter(
            field => field.lookupModule.name == filterModule.name
          )
          if (applicableFields.length > 0) {
            filterModule.appliesToModules.push({
              ...module,
              appliesToFields: applicableFields,
            })
          } else if (filterModule.moduleId != -1) {
            //special type modules cannot extend or be extended
            //no applicable fields found , find parent field -> building Filter -> no building lookup field in WOrkorder module, so see if any lookup is parent of building module
            let applicableParentFields = allLookupFields.filter(field =>
              filterModule.extendedModuleIds.includes(
                field.lookupModule.moduleId
              )
            )
            if (applicableParentFields.length > 0) {
              filterModule.appliesToModules.push({
                ...module,
                appliesToFields: applicableParentFields,
              })
            }
          }
        })
      })

      let commonFilterGroup = {
        displayName: 'Common',
        isExpanded: false,
        fields: uniqueCommonFilterModules,
      }
      // first tab in accordian , add to front after adding other tabs

      this.filterGroups = moduleResps.map(moduleMeta => {
        let meta = {}
        meta.displayName = moduleMeta.displayName
        meta.isExpanded = false
        meta.fields = moduleMeta.fields
          .filter(
            field =>
              field.dataTypeEnum == 'ENUM' || field.dataTypeEnum === 'DATE_TIME'
          )
          .map(field => {
            //even for enum type filters -> hold a reference to the module->[FieldItself again]
            return {
              ...field,
              isSelected: false,
              filterType: 'FIELD',
              appliesToModules: [{ ...moduleMeta, appliesToFields: [field] }],
            }
          }) //add a isSelected Property to all enum fields
        meta.fields.forEach(field => {
          if (
            this.userFilterFields &&
            this.userFilterFields.find(e => e.fieldId == field.fieldId)
          ) {
            field.isSelected = true
            this.selectedOptions.push(field)
          }
        })
        return meta
      })

      this.filterGroups.unshift(commonFilterGroup)
      // add common filter group of options as first accordian tab

      // filter out tabs without options
      this.filterGroups = this.filterGroups.filter(
        filterGroup => filterGroup.fields.length > 0
      )
      if (this.filterGroups.length > 0) {
        this.filterGroups[0].isExpanded = true
      }

      this.selectedOptions.sort((selectedOption1, selectedOption2) => {
        return selectedOption1.filterOrder - selectedOption2.filterOrder
      })

      // let { data, error } = await API.get('v2/modules/meta/${moduleName}')
    },
    async getModuleMeta(moduleName) {
      let { data, error } = await API.get(`/v2/modules/meta/${moduleName}`)
      return data.meta.module
    },
  },
}
</script>

<style lang="scss">
.db-user-filter-manager-dialog {
  .el-dialog__header {
    border-bottom: 1px solid #eff1f4;
  }
  .el-dialog__body {
    padding: 0px;
  }
  .body {
    height: 500px;
  }
  .filter-config-container {
    height: 100%;
    display: flex;
  }

  .filter-config-left {
    width: 40%;
    height: 100%;
    overflow-y: scroll;
    border-right: 1px solid #eff1f4;
  }
  .filter-config-right {
    width: 60%;
    height: 100%;
    overflow-y: scroll;
    padding: 15px 25px;
  }

  .accordian-item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    background-color: #f8fcff;
    font-size: 14px;

    font-weight: 500;
    padding: 15px 25px;
    letter-spacing: 0.5px;
    color: #324056;
    border-bottom: 1px solid #eff1f4;
    border-top: 1px solid #eff1f4;
  }
  .selcted-filters-title {
    font-size: 11px;
    font-weight: bold;
    letter-spacing: 1px;
    color: #324056;
  }
  .module-fields-container {
    padding: 10px;
    margin-left: 10px;
    // max-height: 300px;
    // overflow-y: scroll;
  }
  .module-field {
    font-size: 14px;
    padding: 10px;
    letter-spacing: 0.5px;
    color: #324056;
  }
  .accordian-arrow {
    border: solid #809bae;
    border-width: 1px 1px 0px 0px;
    display: inline-block;
    padding: 3px;
    transform: rotate(135deg);

    &.expanded {
      transform: rotate(-45deg);
    }
  }
  .field-row {
    padding: 10px 10px;
    border: 1px solid #f4f5f7;
    box-shadow: 0px 3px 5px #f4f5f7;
    display: flex;
    font-size: 13px;
  }

  .field-row:hover {
    background-color: #f1f8fa;
  }

  .modal-btn-save {
    margin-left: 0px !important;
  }
  .applies-to-container {
    border: 1px solid #f4f5f7;
    box-shadow: 0px 3px 5px #f4f5f7;
    padding: 15px 15px 10px 30px;
  }
  .applies-to-field {
    font-size: 14px;
    letter-spacing: 0.5px;
    padding-bottom: 7px;
  }
  .applies-to-field-in {
    font-weight: 500;
    font-style: italic;
    margin-left: 5px;
    margin-right: 5px;
  }
  .applies-to-field-module-name {
    text-transform: uppercase;
    letter-spacing: 0.2px;
  }
  .applies-to-title {
    letter-spacing: 0.5px;
    font-weight: 500;
    font-size: 14px;
    color: #324056;
    padding-bottom: 7px;
    padding-top: 5px;
  }
}
</style>
