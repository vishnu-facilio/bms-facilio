<template>
  <div class="h100 height100 overflow-hidden list-layout-container">
    <div
      class="setting-header2 position-relative fp-header indoor-fp-topbar p10 new-fp-header z-index1"
    >
      <portal to="employee-portal-top-header">
        <span slot-scope="props">
          <FloorSwitcher
            :floorplanId="floorplanIdFromRoute"
            :floorId="floorId"
            :buildingId="buildingId"
            :building="building"
            :floor="floor"
          ></FloorSwitcher>
          <span v-if="props && !props.targetRecived">
            <portal to="floorplan-building-swicth">
              <div
                class="setting-title-block fp-chooser-new"
                @click="showBuildingFilter(props)"
              >
                <div class="fc-black3-16 fw4 f15 line-height20">
                  <i class="el-icon-office-building f18 mR10"></i>
                  <span v-if="building">{{ `${building.name}` }}</span>
                  <span v-else>{{ `Building` }}</span>
                  <span>
                    <i
                      class="el-icon-arrow-right fc-grey-text12-light f18 pR5 pL5 vertical-bottom bold"
                    ></i>
                  </span>
                  <span v-if="floor">{{ `${floor.name}` }}</span>
                  <span v-else>{{ `Floor` }}</span>
                </div>
              </div>
            </portal>
          </span>
        </span>
      </portal>
      <portal to="employee-portal-top-header2">
        <span slot-scope="props">
          <FloorSwitcher
            :floorplanId="floorplanIdFromRoute"
            :floorId="floorId"
            :buildingId="buildingId"
            :building="building"
            :floor="floor"
            :diableSwitcherDialog="true"
          ></FloorSwitcher>
          <span v-if="props && !props.targetRecived">
            <portal to="floorplan-building-swicth">
              <div
                class="setting-title-block fp-chooser-new"
                @click="showBuildingFilter"
              >
                <div class="fc-black3-16 fw4 f15 line-height20">
                  <i class="el-icon-office-building f18 mR10"></i>
                  <span v-if="building">{{ `${building.name}` }}</span>
                  <span v-else>{{ `Building` }}</span>
                  <span>
                    <i
                      class="el-icon-arrow-right fc-grey-text12-light f18 pR5 pL5 vertical-bottom bold"
                    ></i>
                  </span>
                  <span v-if="floor">{{ `${floor.name}` }}</span>
                  <span v-else>{{ `Floor` }}</span>
                </div>
              </div>
            </portal>
          </span>
        </span>
      </portal>
      <portal-target name="floorplan-building-swicth"> </portal-target>
      <portal-target
        name="indoorFloorPlanLayout"
        class="indoor-fp-layout-portal"
      >
      </portal-target>
      <div class="action-btn setting-page-btn flex" v-if="!portalLayout">
        <el-radio-group
          v-model="viewerMode"
          class="fp-mode-switch"
          v-if="
            showViewOption &&
              (showViewAssignmentOption ||
                showAssignmentDepartmentOption ||
                showAssignmentOwnOption) &&
              (showViewBookingOption ||
                showBookingDepartmentOption ||
                showBookingOwnOption)
          "
        >
          <el-radio-button label="ASSIGNMENT">
            <div class="flex-middle">
              <inline-svg
                src="svgs/user-check"
                iconClass="icon text-center icon-sm-md"
              ></inline-svg>
              <div class="pL5">
                Assignment
              </div>
            </div>
          </el-radio-button>
          <el-radio-button label="BOOKING">
            <div class="flex-middle">
              <inline-svg
                src="svgs/calendar2"
                iconClass="icon text-center icon-sm-md"
              ></inline-svg>
              <div class="pL5">Booking</div>
            </div>
          </el-radio-button>
        </el-radio-group>
        <!-- <div class="f20">
          <inline-svg
            src="svgs/filter3"
            class="vertical-middle fc-filter-icon"
            iconClass="icon icon-md"
          ></inline-svg>
        </div> -->
        <template v-if="!isPortalLayout">
          <el-dropdown
            v-if="showViewOption && (showEditOption || showCreateOption)"
            class="pointer fc-actions-floor"
            @command="actions"
            trigger="click"
          >
            <span class="el-dropdown-link">
              <i class="el-icon-more rotate-90 pointer"></i>
            </span>
            <el-dropdown-menu slot="dropdown" class="dashboard-subheader-dp">
              <el-dropdown-item command="edit" v-if="showEditOption">
                <div>
                  {{ $t('common._common.edit') }}
                </div>
              </el-dropdown-item>
              <el-dropdown-item command="new" v-if="showCreateOption">
                <div>
                  {{ 'Add Floor Plan' }}
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </div>
      <div class="new-search-conatiner">
        <IndoorFloorPlanSearchList
          :floorId="floorId"
        ></IndoorFloorPlanSearchList>
      </div>
      <div v-if="!loading && viewerMode === 'BOOKING'" class="d-flex">
        <!-- <div class="mR20">
          <el-input
            clearable
            class="search-input"
            prefix-icon="el-icon-search"
            v-model="searchText"
            placeholder="Search All"
          >
          </el-input>
        </div> -->
        <el-popover
          placement="bottom"
          v-model="showPopover"
          popper-class="p0 top filter-popover"
          width="370"
          trigger="click"
          class="filter-popover"
        >
          <div class="popover-slot pointer relative" slot="reference">
            <inline-svg
              src="svgs/employeePortal/filter-icon"
              iconClass="icon"
              class=" m-auto d-flex"
            ></inline-svg>
            <div class="active-filter-indicator" v-if="showFilterTab"></div>
          </div>
          <div class="filter-dropdown width100">
            <div class="sticky">
              <div class="header-container">
                <div class="title">{{ 'Filters' }}</div>
                <div
                  @click="
                    () => {
                      this.showPopover = false
                    }
                  "
                  style="line-height:14px"
                  class="pointer"
                >
                  <inline-svg
                    src="svgs/ic-close"
                    iconClass="icon icon-md"
                  ></inline-svg>
                </div>
              </div>
              <div class="search-outer">
                <el-input
                  clearable
                  class="amenity-search"
                  prefix-icon="el-icon-search"
                  v-model="amenitySearch"
                  placeholder="Search amenities"
                >
                </el-input>
              </div>
            </div>
            <div class="amenity-container" v-if="filteredAmenities">
              <div
                v-for="(value, key) in Object.keys(filteredAmenities)"
                :key="key"
                class="mB20"
              >
                <div class="amenity-category">{{ value }}</div>
                <div class="d-flex-wrap">
                  <div
                    v-for="(amenity, index) in filteredAmenities[value]"
                    :key="index"
                    class="mR10 p0 mB10"
                    :command="amenity"
                  >
                    <el-tag
                      class="amenity-name pointer"
                      :class="{ 'active-class': amenity.active }"
                      @click="handleClick(amenity)"
                      >{{ amenity.name }}</el-tag
                    >
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-popover>
      </div>
    </div>
    <div v-if="showFilterTab" class="filter-tab d-flex">
      <div v-for="(value, index) in amenities" :key="index">
        <el-tag
          v-if="value.active"
          closable
          @close="handleClick(value)"
          class="filter-amenity-name mR10"
        >
          {{ value.name }}
        </el-tag>
      </div>
      <div v-if="clearAll" class="divider-div">
        <el-tag @click="handleCloseAll()" class="filter-amenity-name pointer">{{
          'Clear All'
        }}</el-tag>
      </div>
    </div>
    <IndoorFloorPlanBookingsListView
      v-if="switchView"
      @changed="
        () => {
          this.switchView = !this.switchView
        }
      "
      :searchText="searchText"
    >
    </IndoorFloorPlanBookingsListView>

    <template
      v-if="
        !loading &&
          floorplanIdFromRoute &&
          viewerMode == 'ASSIGNMENT' &&
          !switchView
      "
    >
      <IndoorFloorplandialog
        v-if="openAssignmentDialog"
        @close="handleCloseAssignment"
      >
        <template v-slot:main>
          <IndoorFloorPlanAssignmentView
            ref="assignmentView"
            :id="floorplanIdFromRoute"
            :building="building"
            :floor="floor"
            @floorPlanLoaded="getFloorDetails"
          ></IndoorFloorPlanAssignmentView>
        </template>
      </IndoorFloorplandialog>
      <IndoorFloorPlanAssignmentView
        v-else
        ref="assignmentView"
        :id="floorplanIdFromRoute"
        :building="building"
        :floor="floor"
        @floorPlanLoaded="getFloorDetails"
      ></IndoorFloorPlanAssignmentView>
    </template>

    <IndoorFloorPlanBookingsView
      :class="switchView ? 'hide' : ''"
      ref="bookingView"
      v-else-if="
        !loading &&
          floorplanIdFromRoute &&
          viewerMode == 'BOOKING' &&
          !switchView
      "
      :id="floorplanIdFromRoute"
      :customTime="customTime"
      @floorPlanLoaded="getFloorDetails"
    ></IndoorFloorPlanBookingsView>

    <div
      class="formbuilder-fullscreen-popup floor-plan-builder height100"
      v-if="fpEditor"
    >
      <FloorPlanEditor
        @close="closeEditor"
        :id="floorplanIdFromRoute"
        :visibile.sync="fpEditor"
      ></FloorPlanEditor>
    </div>

    <!-- filter dialog -->
    <IndoorFloorPlanSwitcher
      v-if="filterDialogOpen"
      :visibility.sync="filterDialogOpen"
      :floorplanId="floorplanIdFromRoute"
      :floorId="floorId"
      :buildingId="buildingId"
    ></IndoorFloorPlanSwitcher>

    <floorplanUploader
      v-if="newFp"
      :visibility.sync="newFp"
      @saved="floorplanSaved"
      :moduleName="'indoorfloorplan'"
    ></floorplanUploader>

    <portal-target name="employee-portal-floor-swicther"> </portal-target>
  </div>
</template>
<script>
import Layout from 'src/pages/indoorFloorPlan/Layout.vue'
import IndoorFloorPlanBookingsListView from 'src/pages/indoorFloorPlan/IndoorFloorPlanBookingsListView.vue'
import { API } from '@facilio/api'
import { eventBus } from '@/page/widget/utils/eventBus'
import throttle from 'lodash/throttle'
import { getApp } from '@facilio/router'
import FloorSwitcher from 'src/pages/indoorFloorPlan/EmployeePortalFloorSwither.vue'
import IndoorFloorPlanSearchList from 'src/pages/indoorFloorPlan/components/IndoorFloorPlanSearchList.vue'
import IndoorFloorplandialog from 'src/pages/indoorFloorPlan/IndoorFloorplandialog.vue'
export default {
  extends: Layout,
  components: {
    IndoorFloorPlanBookingsListView,
    FloorSwitcher,
    IndoorFloorPlanSearchList,
    IndoorFloorplandialog,
  },
  data() {
    return {
      switchView: false,
      searchText: null,
      amenities: [],
      amenitySearch: null,
      amenityFields: [],
      amenitiesList: [],
      showPopover: false,
      openAssignmentDialog: false,
    }
  },
  async mounted() {
    this.openAssignmentDialog = false
    this.defaultMode()
    await this.getAmenityMeta()
    await this.getAmenities()
    this.initDefaultEvents()
  },
  computed: {
    isPortalLayout() {
      let {
        appCategory: { PORTALS },
      } = this.$constants
      if (getApp() && getApp().appCategory === PORTALS) {
        return true
      }
      return false
    },
    formatedAmenityObj() {
      let obj = {}
      let { amenities, amenityEnumMap } = this
      amenities.forEach(element => {
        this.$set(element, 'active', false)
        if (amenityEnumMap) {
          let key = amenityEnumMap[element.category] || ''
          if (obj[`${key}`]) {
            obj[`${key}`].push(element)
          } else {
            obj[`${key}`] = []
            obj[`${key}`].push(element)
          }
        }
      })
      return obj
    },
    filteredAmenities() {
      let { formatedAmenityObj, amenitySearch } = this
      if (amenitySearch) {
        let obj = {}
        Object.keys(formatedAmenityObj).forEach(element => {
          formatedAmenityObj[element].forEach(value => {
            if (
              value.name.toLowerCase().includes(amenitySearch.toLowerCase())
            ) {
              if (obj[`${element}`]) {
                obj[`${element}`].push(value)
              } else {
                obj[`${element}`] = []
                obj[`${element}`].push(value)
              }
            }
          })
        })
        return obj
      } else {
        return formatedAmenityObj
      }
    },
    clearAll() {
      let count = 0
      Object.keys(this.filteredAmenities).forEach(element => {
        this.filteredAmenities[element].forEach(value => {
          if (value.active) {
            count += 1
          }
        })
      })
      return count >= 2 ? true : false
    },
    showFilterTab() {
      let count = 0
      Object.keys(this.filteredAmenities).forEach(element => {
        this.filteredAmenities[element].forEach(value => {
          if (value.active) {
            count += 1
          }
        })
      })
      return count >= 1 ? true : false
    },
  },
  beforeDestroy() {
    eventBus.$off('OPEN_ASSIGNMENT_IN_DIALOG', () => {})
  },
  methods: {
    defaultMode() {
      this.viewerMode = 'BOOKING'
    },
    initDefaultEvents() {
      eventBus.$on('OPEN_ASSIGNMENT_IN_DIALOG', () => {
        this.openAssignmentDialog = true
      })
    },
    handleCloseAssignment() {
      this.openAssignmentDialog = false
      eventBus.$emit('SWITCH_FLOORPLAN_MODE', 'BOOKING')
    },
    handleCloseAll() {
      Object.keys(this.filteredAmenities).forEach(element => {
        this.filteredAmenities[element].forEach(value => {
          value.active = false
        })
      })
      this.ResetFilter()
    },
    handleClick: throttle(function(amenityObj) {
      amenityObj.active = !amenityObj.active
      this.applyFilter()
    }, 1500),
    switcher() {
      if (this.$route?.query?.featureId) {
        this.$router.replace({ query: {} })
      }
      this.switchView = !this.switchView
    },
    async getAmenityMeta() {
      let { error, data } = await API.get('/module/meta?moduleName=amenity')
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let amenityFields = data.meta.fields || []
        let obj = amenityFields.find(f => f.name == 'category')
        this.amenityEnumMap = obj?.enumMap ? obj.enumMap : {}
      }
    },
    async getAmenities() {
      let { error, data } = await API.get(
        '/v3/modules/data/list?viewName=all&page=1&includeParentFilter=false&withCount=true&perPage=50&moduleName=amenity'
      )
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.amenities = data.amenity || []
      }
    },
    applyFilter() {
      let array = []
      Object.keys(this.filteredAmenities).forEach(element => {
        this.filteredAmenities[element].forEach(value => {
          if (value.active) {
            array.push(value.id)
          }
        })
      })
      eventBus.$emit('amenity-filter', array)
    },
    applySelectFilter() {
      this.applyFilter()
      this.showPopover = false
    },
    ResetFilter() {
      eventBus.$emit('reset-amenity')
      this.showPopover = false
    },
  },
}
</script>
<style scoped>
.new-search-conatiner {
  position: relative;
}
.new-fp-header {
  display: inline-flex;
  justify-content: unset;
}
.new-fp-header .indoor-fp-layout-portal {
  width: 100%;
}
.active-filter-indicator {
  width: 7px;
  height: 7px;
  background: #f56c6c;
  border-radius: 7px;
  position: absolute;
  top: 3px;
  right: 3px;
}
.d-flex-wrap {
  display: flex;
  flex-wrap: wrap;
}
.popover-slot {
  height: 24px;
  width: 24px;
  border-radius: 2px;
  margin-top: 8px;
  display: flex;
  /* margin-right: 20px; */
}
.popover-slot:hover {
  background-color: #f5f5f5;
}
.switch-icon {
  margin: 15px 5px 0 0;
  left: 4px;
  padding: 5px;
  margin: 0;
  height: 26px;
  height: 24px;
  width: 24px;
  top: 8px;
  position: relative;
  border-radius: 2px;
}
.switch-icon:hover {
  background-color: #f5f5f5;
}
.m-auto {
  margin: auto;
}
.header-container {
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #d6dbe3;
  padding: 15px 20px;
  line-height: 18px;
}
.active-class {
  background-color: #0053cc !important;
  color: #fff !important;
}
.sticky {
  top: 0;
  position: sticky;
  z-index: 5;
  background-color: #fff;
}
.sticky-bottom {
  bottom: 0;
  position: sticky;
  z-index: 5;
  background-color: #fff;
}
.filter-tab {
  padding: 10px 20px;
  border-bottom: 1px solid #ebeff3;
  background-color: #fff;
}
.divider-div {
  padding: 0 15px;
  border-left: 1px solid #dadada;
  margin-left: 5px;
}
.amenity-category {
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: justify;
  color: #324056;
  margin-bottom: 10px;
}
.title {
  font-size: 16px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: 28px;
  letter-spacing: 0.5px;
  color: #324056;
  height: 24px;
}

.amenity-container {
  min-width: 350px;
  padding: 20px 20px;
  height: 400px;
  overflow: hidden;
  overflow-y: scroll;
}
.setting-title-block.fp-chooser-new:hover {
  border-color: #39b2c2;
}
.list-layout-container {
  background-color: #fafbfc;
}
.hide {
  display: none;
}
.search-outer {
  border-bottom: 1px solid #d6dbe3;
  padding: 0 10px;
}
</style>
<style>
.amenity-search input.el-input__inner,
.amenity-search input.el-input__inner:hover,
.amenity-search input.el-input__inner:focus {
  border-bottom: 0 !important;
  padding: 0px 35px;
  height: 40px;
  border-color: #fff !important;
}
.amenity-name.el-tag {
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #0053cc;
  border-radius: 17px;
  border: solid 1px #0053cc;
  background-color: #fff;
  letter-spacing: 0.6px;
  font-size: 13px;
  height: 30px;
  display: flex;
  align-items: center;
}
.amenity-name.el-tag:hover {
  color: #fff;
  background-color: #0053cc;
}
i.el-dropdown-menu__item.mR10.p0 {
  background-color: #fff;
}
.filter-amenity-name.el-tag {
  font-size: 11px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 21px;
  letter-spacing: normal;
  text-align: justify;
  color: #483db6;
  border-radius: 14px;
  border: solid 1px #483db6;
  background-color: #fff;
  height: 24px;
}
.filter-amenity-name i.el-tag__close.el-icon-close {
  color: #483db6;
  /* line-height: 20px; */
}
.filter-amenity-name i.el-tag__close.el-icon-close:hover {
  background-color: #fff;
}
.search-input input.el-input__inner {
  height: 36px;
  width: 250px;
  padding: 0 10px;
  border-radius: 4px;
  border-bottom: 0;
}
.search-input input.el-input__inner {
  padding: 0 30px;
  border-bottom: 0;
}
.search-input.el-input.el-input--prefix.el-input--suffix {
  width: 250px;
}
.filter-popover {
  display: flex;
  align-items: center;
}
.filter-popover .el-popover__reference {
  margin-top: 0;
}
.filter-popover .el-popover {
  padding: 0 !important;
}
button.el-button.primary-button.el-button--primary,
button.el-button.primary-button.el-button--primary:hover {
  width: 50%;
  border-radius: 0;
  background-color: #0053cc;
  border-color: #0053cc;
  font-weight: 500;
  font-size: 14px;
}
button.el-button.secondary-button.el-button--default,
button.el-button.secondary-button.el-button--default:hover {
  width: 50%;
  border-radius: 0;
  border-color: #f4f4f4;
  font-size: 14px;
  font-weight: 500;
  background-color: #f4f4f4;
  color: #8f8f8f;
}
button.el-button.secondary-button.el-button--default:hover {
  z-index: 0;
}
button.el-button.primary-button.el-button--primary:hover {
  z-index: 0;
}
</style>
