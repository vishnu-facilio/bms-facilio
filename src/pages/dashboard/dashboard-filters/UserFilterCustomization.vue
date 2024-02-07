<template>
  <div class="db-user-filter-customization">
    <el-dialog
      v-if="
        userFilterConfig.module ||
          (userFilterConfig.field &&
            userFilterConfig.field.dataTypeEnum !== 'DATE_TIME')
      "
      :visible="visibility"
      class="db-user-filter-customization-dialog"
      :show-close="false"
      :append-to-body="true"
      :title="
        (userFilterConfig.field && userFilterConfig.field.displayName) ||
          (userFilterConfig.module && userFilterConfig.module.displayName) +
            '- FILTER CUSTOMIZATION'
      "
      width="50%"
    >
      <div class="body p25">
        <div class="filter-customization-container">
          <div class="filter-label title mB10">
            Filter label
          </div>
          <el-input
            class="fc-input-full-border2 mB20 width60"
            v-model="userFilterConfig.label"
          ></el-input>

          <div class="filter-option-type title mB10">
            Selection type
          </div>
          <el-select
            class="fc-input-full-border-h35 mB20 width60"
            v-model="userFilterConfig.componentType"
          >
            <el-option v-bind:value="1" label="Single select"> </el-option>
            <el-option v-bind:value="2" label="Multi select"> </el-option>
          </el-select>

          <div v-if="userFilterConfig.moduleName === 'ticketstatus'">
            <div class="filter-option-type title mB10">
              Select Parent Module
            </div>
            <el-select
              class="fc-input-full-border-h35 mB20 width60"
              v-model="userFilterConfig.module.parentModule"
              @change="parentModuleSelected"
              filterable
            >
              <el-option
                v-for="(modules, index) in moduleList"
                :key="index"
                :label="modules.displayName"
                :value="modules.name"
              ></el-option>
            </el-select>
          </div>

          <div class="filter-option-type title mB10">
            Default option
          </div>
          <el-select
            placeholder="Type to search"
            :loading="remoteOptionsLoading"
            loading-text="loading ... "
            class="fc-input-full-border-h35 mB20 width60"
            :multiple="userFilterConfig.componentType == 2"
            collapse-tags
            filterable
            v-model="elSelectModel"
            :remote="isLazyLoad"
            :remote-method="handleRemoteSearch"
          >
            <el-option
              v-for="option in elSelectOptions"
              :label="option.label"
              :value="option.value"
              :key="option.value"
            >
            </el-option>
          </el-select>
          <!-- relevant toggle only available for LOOKUP filters -->
          <!-- <div class="mB20" v-if="this.userFilterConfig.module">
            <el-checkbox
              style="border: none;"
              v-model="userFilterConfig.showOnlyRelevantValues"
            >
              Show only relevant values
            </el-checkbox>
          </div> -->
          <!-- <div class="mB20">
            <el-checkbox
              style="border: none;"
              v-model="userFilterConfig.isAllOptionEnabled"
            >
              Show ALL as an option
            </el-checkbox>
          </div> -->

          <div class="mB15">
            <el-checkbox
              style="border: none;"
              v-model="userFilterConfig.optionType"
              v-bind:true-label="2"
              v-bind:false-label="1"
              @change="checkBoxChanged"
            >
              Limit options
            </el-checkbox>
          </div>

          <template v-if="userFilterConfig.optionType == 2 && !loading">
            <div class="mB10 mT10">
              <!-- <el-checkbox
                style="border: none;"
                v-model="userFilterConfig.isOthersOptionEnabled"
              >
                Shows 'Others' option
              </el-checkbox> -->
            </div>
            <div class="checkbox-list " v-if="showCheckboxList">
              <el-checkbox-group
                v-model="userFilterConfig.selectedOptions"
                class="flex flex-wrap overflow-y-scroll flex-align-start content-start"
              >
                <el-checkbox
                  class="p5"
                  v-for="(option, index) in checkboxList"
                  :key="index"
                  :label="option.value"
                  >{{ option.label }}</el-checkbox
                >
              </el-checkbox-group>
            </div>
            <div class="option-criteria" v-else-if="showCriteriaBuilder">
              <new-criteria-builder
                title="Show options that match"
                class="stateflow-criteria"
                ref="criteriaBuilder"
                v-model="userFilterConfig.criteria"
                :exrule="userFilterConfig.criteria"
                :module="userFilterConfig.module.name"
                @condition="updateCriteria"
              ></new-criteria-builder>
            </div>
          </template>
        </div>
      </div>

      <div class="dialog-save-cancel">
        <el-button class="modal-btn-cancel" @click="closeDialog">
          Cancel</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="saveFilterDialog"
          >Done</el-button
        >
      </div>
    </el-dialog>
    <div
      v-if="
        userFilterConfig.field &&
          userFilterConfig.field.dataTypeEnum === 'DATE_TIME'
      "
    >
      <DashboardDateTimeFilter
        :userDTFilterConfig="userFilterConfig"
        :visibility="visibility"
        @updatevisibility="closeDialog"
        @filterPropertiesChanged="saveFilterConfig"
      >
      </DashboardDateTimeFilter>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import { deepCloneObject } from 'util/utility-methods'
import { constructFieldOptions } from '@facilio/utils/utility-methods'
import { isEmpty } from '@facilio/utils/validation'
import DashboardDateTimeFilter from './DashboardDateTimeFilter'
export default {
  components: {
    NewCriteriaBuilder,
    DashboardDateTimeFilter,
  },
  props: ['userFilter', 'visibility'],
  created() {
    this.userFilterConfig = deepCloneObject(this.userFilter)
    this.getModuleList().then(() => {
      this.loadOptionLimitConfig()
    })
  },
  data() {
    return {
      userFilterConfig: null,
      loading: false,
      defaultOptions: [],
      showCheckboxList: false,
      showCriteriaBuilder: false,
      checkboxList: [],
      remoteOptionsLoading: false,
      moduleList: [],
    }
  },
  computed: {
    // these properties are required for default value chooser
    isLazyLoad() {
      //for non picklist lookup module ,need to lazy load the options
      return (
        this.userFilterConfig.module &&
        this.userFilterConfig.module.typeEnum != 'PICK_LIST'
      )
    },
    elSelectModel: {
      get() {
        if (this.userFilterConfig.componentType == 1) {
          //single select
          //v -model passed to LookupFilter component is always  of form filterId:[value] , for single select return value as a single element
          return this.userFilterConfig.defaultValues[0]
        } else if (this.userFilterConfig.componentType == 2) {
          //multi select
          return this.userFilterConfig.defaultValues
        } else {
          return null
        }
      },
      set(value) {
        //add toggle logic for all,
        if (this.userFilterConfig.componentType == 1) {
          this.userFilterConfig.defaultValues = [value]
        } else if (this.userFilterConfig.componentType == 2) {
          this.userFilterConfig.defaultValues = value
        }
      },
    },
    elSelectOptions() {
      console.log('elSelectOptions computed run')
      let options = deepCloneObject(this.defaultOptions)
      //for enum and lookup with module=picklist filter options if only certain options are selected
      if (this.userFilterConfig.optionType == 2 && !this.isLazyLoad) {
        options = options.filter(e => {
          return this.userFilterConfig.selectedOptions.includes(e.value)
        })
      }

      if (this.userFilterConfig.isAllOptionEnabled) {
        options.unshift({ label: 'All', value: 'all' })
      }

      if (
        this.userFilterConfig.optionType == 2 &&
        this.userFilterConfig.isOthersOptionEnabled
      ) {
        options.push({ label: 'Others', value: 'others' })
      }
      return options
    },
  },
  watch: {
    'userFilterConfig.module.parentModule': {
      handler(newVal, oldVal) {
        if (newVal) {
          this.getModuleList().then(() => {
            this.loadOptionLimitConfig()
          })
        }
      },
    },
  },
  methods: {
    checkBoxChanged() {
      if (this.userFilterConfig.optionType == 1) {
        this.userFilterConfig.criteria = null
        this.userFilterConfig.selectedOptions = []
      }
    },
    async handleRemoteSearch(search) {
      if (this.remoteOptionsLoading) {
        return
      }
      this.remoteOptionsLoading = true
      console.log('remote search', search)
      this.defaultOptions = await this.getOptions(search)
      this.remoteOptionsLoading = false
    },
    updateCriteria(newValue) {
      this.userFilterConfig.criteria = newValue
      if (this.isValidCriteria(newValue)) {
        console.log('criteria valid, refresh default options', newValue)
        this.getOptions(null, true).then(options => {
          this.defaultOptions = options
        })
      }
    },
    isValidCriteria(criteria) {
      console.log('validating criteria')
      if (!criteria.conditions) {
        return false
      }
      let conditions = criteria.conditions
      for (const key in conditions) {
        if (Number.isInteger(Number.parseInt(key))) {
          let condition = conditions[key]
          if (isEmpty(condition) || isEmpty(condition.operatorLabel)) {
            return false
          }
        }
      }
      return true
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    parentModuleSelected() {
      this.userFilterConfig.defaultValues = []
    },
    saveFilterDialog() {
      let { userFilterConfig } = this
      let { module } = userFilterConfig || {}
      let { parentModule, name } = module || {}
      if (name === 'ticketstatus') {
        if (isEmpty(parentModule)) {
          this.$message.error(
            this.$t(
              'common.products.please_select_parentmodule_in_ticket_status'
            )
          )
        } else {
          this.saveFilterConfig()
        }
      } else {
        this.saveFilterConfig()
      }
    },
    saveFilterConfig() {
      if (this.userFilterConfig.optionType == 2 && this.showCriteriaBuilder) {
        if (this.isValidCriteria(this.userFilterConfig.criteria)) {
          this.$emit('filterPropertiesChanged', this.userFilterConfig)
        } else {
          this.$message.error('Please enter valid criteria')
          return
        }
      }

      this.$emit('filterPropertiesChanged', this.userFilterConfig)
    },
    async getModuleList() {
      let { data, error } = await API.get('v3/modules/list/all')
      if (!error) {
        let { customModules, systemModules } = data
        this.moduleList = [...customModules, ...systemModules]
      }
    },
    loadOptionLimitConfig() {
      if (
        this.userFilterConfig.field &&
        this.userFilterConfig?.field?.dataTypeEnum !== 'DATE_TIME'
      ) {
        //field obj filled indicates ->FILTER TYPE=ENUM
        this.showCheckboxList = true
        this.checkboxList = constructFieldOptions(
          this.userFilterConfig.field.enumMap
        ).map(e => {
          return {
            value: String(e.value),
            label: e.label,
          }
        })
        this.defaultOptions = this.checkboxList
      } else if (this.userFilterConfig.module) {
        //MODULE ibject field -> Filter TYPE= Lookup filter
        if (this.userFilterConfig.module.typeEnum == 'PICK_LIST') {
          //PICK_LIST is a special type lookup with limited values,hence load options upfront
          this.loading = true
          this.getOptions().then(options => {
            this.checkboxList = options
            this.loading = false
            this.showCheckboxList = true
            this.defaultOptions = this.checkboxList
          })
        } else {
          //BASE_ENTITY Module
          // this.loading = true
          this.showCriteriaBuilder = true
          this.getOptions(null, true).then(options => {
            this.defaultOptions = options
          })
        }
      }
    },

    async getOptions(search, isInitialFetch) {
      //,called for PICKLIST where data_type=lookup
      //for baseentity module type lookup , lazy load done,remote search ,
      // for picklist module lookup load all values upfront,this method called only on init

      let params = {}

      if (this.isLazyLoad) {
        if (!isInitialFetch && isEmpty(search)) {
          return
        }
        params.search = search
        params.page = 1
        params.perPage = 10

        //make sure currently selected lookup record always resolves  its  id ->label,
        let currentySelectedOptions = this.userFilterConfig.defaultValues.filter(
          e => {
            return e != 'all' && e != 'others'
          }
        )
        currentySelectedOptions = currentySelectedOptions.map(e => Number(e))

        if (this.userFilterConfig.optionType == 2) {
          let criteria = this.userFilterConfig.criteria
          if (criteria && criteria.conditions) {
            //dont send operator as there is no enum setter for operator,causes struts error,operatorId is sent which will suffice

            for (const conditionId in criteria.conditions) {
              delete criteria.conditions[conditionId].operator
            }
          }
          params.criteria = criteria
        }
      }

      let { userFilterConfig } = this
      let { moduleName, module: moduleObj } = userFilterConfig || {}
      let { parentModule } = moduleObj || {}
      if (moduleName === 'ticketstatus') {
        if (!isEmpty(parentModule)) {
          let modules = this.moduleList.find(m => m.name === parentModule)
          let parentModuleId = modules ? modules.moduleId : null
          if (parentModuleId) {
            params.filters = JSON.stringify({
              parentModuleId: { operatorId: 9, value: [`${parentModuleId}`] },
            })
          }
        }
      }

      //else for picklist type lookups can load all records as there will be few in number, no params

      // let isBaseSpaceModule =
      //   this.userFilterConfig.field.lookupModule.name == 'basespace'
      //basespace response returing an array instead of map , must fix api
      let { data, error } = await API.post(
        `v2/picklist/${this.userFilterConfig.module.name}`,
        params
      )

      if (error) {
        this.$message.error(error.message)
      } else {
        let options = []
        let { pickList, basespaces } = data

        if (pickList) {
          Object.keys(pickList).forEach(key => {
            options.push({
              label: pickList[key],
              value: String(key),
            })
          })
        } else if (basespaces) {
          if (basespaces != null) {
            options = basespaces.map(e => {
              return { value: String(e.id), label: e.name }
            })
          }
        }

        return options
      }
    },
  },
}
</script>

<style lang="scss">
.db-user-filter-customization-dialog {
  .spinner {
    margin-top: 200px;
  }

  .checkbox-list {
    .el-checkbox-group {
      height: 180px;
    }

    .el-checkbox {
      flex: 0 0 40%;
    }
  }

  .el-dialog__header {
    border-bottom: 1px solid #eff1f4;
  }
  .el-dialog__body {
    padding: 0px;
  }
  .body {
    height: 500px;
    overflow-y: scroll;
  }
  .filter-customization-container {
    height: 100%;
  }

  .modal-btn-save {
    margin-left: 0px !important;
  }

  .title {
    height: 14px;
    font-size: 14px;
    letter-spacing: 0.5px;
    color: #6b7e91;
  }
  .option-config-separator {
    height: 1px;
    border-bottom: solid 1px #eff1f4;
  }
}
</style>
