<template>
  <div
    class="db-filter-container"
    :class="[{ 'edit-background': isEditBackground }]"
  >
    <div class="db-filter-empty-state" v-if="isUserFilterEmptyState">
      <div class="empty-state-button" @click="openUserFilterManager">
        Create filters
      </div>
      <div class="empty-state-button" @click="openTimeLineFilterManager">
        Timeline filter
      </div>
    </div>
    <!-- <dashboard-timeline-filter
      :dbTimelineFilterInitialState="dbTimelineFilter"
      v-if="dashboardFilterObj.isTimelineFilterEnabled"
      @timelineFilterChanged="handleTimelineFilterChanged"
    >
    </dashboard-timeline-filter> -->
    <!--in  edit mode just show dummy filters -->
    <div
      class="lookup-filter-section"
      v-else-if="isEditMode && editStateUserFilters"
    >
      <dashboard-lookup-filter
        v-for="(filter, index) in editStateUserFilters"
        :key="filter.id"
        :isEditMode="true"
        :userFilterObj="filter"
        @edit="customizeFilter(index)"
        @delete="deleteFilter(index)"
      >
      </dashboard-lookup-filter>
    </div>
    <!-- edit mode end -->
    <div
      class="lookup-filter-section"
      v-else-if="!isEditMode && dashboardUserFilters"
    >
      <dashboard-lookup-filter
        :isEditMode="false"
        v-for="filter in dashboardUserFilters"
        :key="filter.id"
        :userFilterObj="filter"
        :cascadingFilterJson="cascadingFilterModel[filter.id]"
        v-model="lookupFilterModel[filter.id]"
        @valueChanged="handleValueChange"
      >
      </dashboard-lookup-filter>
    </div>
    <div class="edit-mode-bar" v-if="isEditMode">
      <div class="blue-btn" @click="openUserFilterManager">
        Add/Edit filters
      </div>
      <div class="seperator"></div>
      <div class="blue-btn" @click="openTimeLineFilterManager">
        Timeline filter
      </div>
      <div class="seperator"></div>
      <div class="cancel-btn" @click="cancelClicked">
        Cancel
      </div>
      <div class="seperator"></div>

      <div class="blue-btn" @click="saveContainer">
        Save
      </div>
    </div>
    <user-filter-manager
      v-if="showUserFilterManager"
      :visibility.sync="showUserFilterManager"
      :userFilterFields="userFilterFields"
      :userFilterModules="userFilterModules"
      :dashboardId="dashboardId"
      :dashboardTabId="dashboardTabId"
      @filterConfigChanged="manageUserFilterSave"
    >
    </user-filter-manager>
    <timeline-filter-manager
      v-if="showTimelineFilterManager"
      :visibility.sync="showTimelineFilterManager"
      :timelineFilterState.sync="editStateTimelineFilter"
      @timelineFilterConfigSave="handleTimelineFilterConfigSave"
    >
    </timeline-filter-manager>
    <UserFilterCustomization
      v-if="showUserFilterCustomization"
      :userFilter="filterToCustomize"
      :visibility.sync="showUserFilterCustomization"
      @filterPropertiesChanged="saveFilterCustomization"
    >
    </UserFilterCustomization>
  </div>
</template>

<script>
import './styles/dbFilter.scss'
import cloneDeep from 'lodash/cloneDeep'
import { deepCloneObject } from 'util/utility-methods'
import DashboardLookupFilter from './DashboardLookupFilter'
import UserFilterManager from './UserFilterManager'
import TimelineFilterManager from './TimelineFilterManager'
import UserFilterCustomization from './UserFilterCustomization'
import { isEmpty } from '@facilio/utils/validation'

import {
  getUserFilterModel,
  generateFilterJSON,
  getUserFilterObj,
  serializeUserFilters,
  getCascadingFilterModel,
} from './DashboardFilterHelper'

export default {
  props: ['dashboardFilterObj', 'dashboardId', 'dashboardTabId'],

  components: {
    DashboardLookupFilter,
    UserFilterManager,
    TimelineFilterManager,
    UserFilterCustomization,
  },
  computed: {
    //while in empty state check the editState filter config.
    //while live dashboard check if filters present ,before showing 'no data' buttons'

    //applies for both types
    dashboardUserFilters() {
      const { dashboardFilterObj } = this ?? {}
      const { dashboardUserFilters } = dashboardFilterObj ?? {}
      return dashboardUserFilters ?? []
    },
    isUserFilterEmptyState() {
      if (this.isEditMode) {
        return !(
          this.editStateUserFilters && this.editStateUserFilters.length > 0
        )
      } else {
        return !(
          this.dashboardFilterObj &&
          this.dashboardUserFilters &&
          this.dashboardUserFilters.length > 0
        )
      }
    },
    isEditBackground() {
      if (this.isUserFilterEmptyState || this.isEditMode) {
        return true
      } else return false
    },
  },
  data() {
    return {
      lookupFilterModel: {}, //filter:value
      cascadingFilterModel: {}, //filterId :FilterJson
      isEditMode: false,
      showUserFilterManager: false,
      showUserFilterCustomization: false,
      filterToCustomize: null,
      showTimelineFilterManager: false,
      editStateUserFilters: null,
      /* user filter list contains userFilterObj->Field OR Module +Other properties
      for add/edit filter wizard ,send only fields or modules from selected user filters
      */

      userFilterFields: null,
      userFilterModules: null,
      /*  */
      editStateTimelineFilter: null,
    }
  },
  created() {
    if (this.dashboardUserFilters) {
      this.lookupFilterModel = getUserFilterModel(this.dashboardUserFilters)
      try {
        this.cascadingFilterModel = getCascadingFilterModel(
          this.dashboardUserFilters,
          this.lookupFilterModel
        )
      } catch (e) {
        console.error('error applying cascading filters on init')
      }
    }

    if (this.dashboardUserFilters) {
      this.dashboardUserFilters.filter(filter => {
        let { parentModuleName } = filter || {}
        if (parentModuleName) {
          filter.module.parentModule = parentModuleName
        }
      })
    }
  },
  mounted() {
    console.log('db filter container   mounted')
  },
  methods: {
    deleteFilter(index) {
      console.log('deleting ', index)
      this.editStateUserFilters.splice(index, 1)
    },
    customizeFilter(index) {
      this.filterToCustomizeIndex = index
      this.filterToCustomize = this.editStateUserFilters[
        this.filterToCustomizeIndex
      ]
      this.showUserFilterCustomization = true
    },
    saveFilterCustomization(changedFilter) {
      this.editStateUserFilters.splice(
        this.filterToCustomizeIndex,
        1,
        changedFilter
      )
      this.showUserFilterCustomization = false
    },
    handleTimelineFilterConfigSave(timelineFilterConfig) {
      this.showTimelineFilterManager = false
      this.editStateTimelineFilter = timelineFilterConfig
      this.isEditMode = true
      this.$emit('update:filterEditMode', this.isEditMode)
      this.$emit(
        'update:hideFilterInsideWidgets',
        timelineFilterConfig.isHideTimeLineFilterEnabled
      )
      //if empty state and timeline filter enabled  first time ,trigger edit mode
    },
    enterEditMode() {
      if (this.dashboardUserFilters) {
        this.editStateUserFilters = deepCloneObject(this.dashboardUserFilters)
      }
      this.isEditMode = true
      this.$emit('update:filterEditMode', this.isEditMode)
    },
    cancelClicked() {
      this.isEditMode = false
      this.$emit('update:filterEditMode', this.isEditMode)
      this.editStateUserFilters = null
      this.editStateTimelineFilter = null
    },
    createTimelineEditState() {
      if (!this.editStateTimelineFilter) {
        this.editStateTimelineFilter = {
          isTimelineFilterEnabled: this.$getProperty(
            this.dashboardFilterObj,
            'isTimelineFilterEnabled',
            false
          ),
          dateOperator: this.$getProperty(
            this.dashboardFilterObj,
            'dateOperator',
            22
          ),
          dateLabel: this.$getProperty(
            this.dashboardFilterObj,
            'dateLabel',
            'Today'
          ),
          hideFilterInsideWidgets: this.$getProperty(
            this.dashboardFilterObj,
            'hideFilterInsideWidgets',
            false
          ),
        }
      }
    },
    saveContainer() {
      // This is Sujitha's functionality, don't know what is the use case, so I added,
      // a ? to fix it...
      let availableFilter = this.editStateUserFilters?.find(
        filter => filter?.module?.name === 'ticketstatus'
      )
      if (!isEmpty(availableFilter)) {
        let { module } = availableFilter || {}
        let { parentModule, name } = module || {}
        if (name === 'ticketstatus') {
          if (isEmpty(parentModule)) {
            this.$message.error(
              this.$t(
                'common.products.please_select_parentmodule_in_ticket_status'
              )
            )
          } else {
            this.saveClicked()
          }
        }
      } else {
        this.saveClicked()
      }
    },
    saveClicked() {
      console.log('save db filters', this.editStateUserFilters)
      this.createTimelineEditState()

      let reqJson = {
        dashboardFilter: {
          dashboardUserFilters: this.editStateUserFilters
            ? serializeUserFilters(this.editStateUserFilters)
            : null,
          isTimelineFilterEnabled: this.editStateTimelineFilter
            .isTimelineFilterEnabled,
          dateOperator: this.editStateTimelineFilter.dateOperator,
          dateLabel: this.editStateTimelineFilter.dateLabel,
          hideFilterInsideWidgets: this.editStateTimelineFilter
            .hideFilterInsideWidgets,
        },
      }
      if (this.dashboardTabId && this.dashboardTabId != -1) {
        reqJson.dashboardFilter.dashboardTabId = this.dashboardTabId
      } else {
        reqJson.dashboardFilter.dashboardId = this.dashboardId
      }

      //update existing filter
      if (this.dashboardFilterObj && this.dashboardFilterObj.id) {
        reqJson.dashboardFilter.id = this.dashboardFilterObj.id
      }
      // this.editStateUserFilters = null
      this.$emit('dbFilterConfigSaved', reqJson)
    },
    handleValueChange(filterId) {
      this.$emit('dbUserFilters', {
        filter: cloneDeep(this.lookupFilterModel),
        filterId: filterId,
      })
      // let widgetFilterJson = generateFilterJSON(
      //   this.lookupFilterModel,
      //   this.dashboardFilterObj.dashboardUserFilters,
      //   this.dashboardFilterObj.widgetUserFiltersMap
      // )
      // console.log(
      //   'filter changed generated filterJSON is=',
      //   JSON.stringify(widgetFilterJson)
      // )
      // try {
      //   this.cascadingFilterModel = getCascadingFilterModel(
      //     this.dashboardUserFilters,
      //     this.lookupFilterModel
      //   )
      // } catch (e) {
      //   console.error('error applying cascading filters on value change')
      // }
      // this.$emit('dbUserFilters', {
      //   filterJson: widgetFilterJson,
      //   filterModel: this.lookupFilterModel,
      // })
    },
    openTimeLineFilterManager() {
      this.createTimelineEditState()
      this.showTimelineFilterManager = true
    },

    openUserFilterManager() {
      if (this.editStateUserFilters) {
        //intermediate saved state, Edit/add filters clicked , saved ,then  Edit/add filters clicked again
        this.userFilterFields = this.editStateUserFilters
          .filter(e => e.field)
          .map(e => {
            return { ...e.field, filterOrder: e.filterOrder }
          })
        this.userFilterModules = this.editStateUserFilters
          .filter(e => e.module)
          .map(e => {
            return { ...e.module, filterOrder: e.filterOrder }
          })
      } else if (this.dashboardUserFilters) {
        //first time edit mode openened

        this.userFilterFields = this.dashboardUserFilters
          .filter(e => e.field)
          .map(e => e.field)
        this.userFilterModules = this.dashboardUserFilters
          .filter(e => e.module)
          .map(e => e.module)
      }
      this.showUserFilterManager = true
    },
    manageUserFilterSave(selectedOptions) {
      // saved fields here are of two types
      // contains moduleObj for a module filter and fieldObject for a ENUM filter

      console.log('saving filters from manage fields are', selectedOptions)
      let userFilters = []
      //previous fields =, maintaing config and userFilter id of fields already prevent before edit

      let previousFilterFieldIds = []
      let previousFilterModuleNames = []
      if (this.userFilterFields) {
        previousFilterFieldIds = this.userFilterFields.map(e => e.fieldId)
      }
      if (this.userFilterModules) {
        previousFilterModuleNames = this.userFilterModules.map(e => e.name)
      }

      selectedOptions.forEach((option, index) => {
        let userFilter = null

        if (
          option.filterType == 'FIELD' &&
          previousFilterFieldIds.includes(option.fieldId)
        ) {
          //field already present as a user filter , send the same user filter  obj

          userFilter = this.editStateUserFilters.find(
            e => e.fieldId == option.fieldId
          )

          console.log('exisitng field,type ENUM ', userFilter.label)
        } else if (
          option.filterType == 'MODULE' &&
          previousFilterModuleNames.includes(option.name)
        ) {
          //module already present as a user filter , send the same user filter  obj

          userFilter = this.editStateUserFilters.find(
            e => e.moduleName == option.name
          )

          console.log('exisitng field , type LOOKUP', userFilter.label)
        } else {
          userFilter = getUserFilterObj(option) //new field/Module being added as a  user filter
          console.log('new filter ,type=' + option.filterType, userFilter.label)
        }

        userFilter.filterOrder = index + 1
        userFilter.selectedOptions = []
        userFilters.push(userFilter)
      })

      console.log('setting temp filter state to ', userFilters)
      this.editStateUserFilters = userFilters

      //if manage user filter dialog was opened from EMPTY STATE, and saved with a non empty value enter edit mode . ,else do nothing

      if (this.editStateUserFilters && this.editStateUserFilters.length > 0) {
        this.isEditMode = true
        this.$emit('update:filterEditMode', this.isEditMode)
      }

      this.showUserFilterManager = false
    },

    // handleTimelineFilterChanged(timelineObj) {
    //   console.log('timeline filter changed in dbfiltercontainer', timelineObj)
    //   this.$emit('update:dbTimelineFilter', timelineObj)
    // },
  },
}
</script>
