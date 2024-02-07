<template>
  <el-drawer
    :visible="visibility"
    direction="btt"
    :size="drawerSize"
    custom-class="db-filter-mobile-popover"
    v-bind:show-close="true"
    v-bind:wrapperClosable="true"
    v-bind:destroy-on-close="true"
    @open="handleOpen"
    @close="handleCloseEvent"
  >
    <template v-slot:title>
      <div class="filter-title">
        Filters
      </div>
    </template>
    <template v-slot:default>
      <div class="db-filter-container-mobile">
        <div class="seperator mB10"></div>
        <div class="filter-container-body" v-if="!isUserFilterEmptyState">
          <div
            class="lookup-filter-section-scroll-container"
            v-if="dashboardUserFilters"
          >
            <div class="lookup-filter-section">
              <dashboard-lookup-filter-mobile
                class="pL15 pR15 mB20"
                v-for="filter in dashboardUserFilters"
                :key="reRenderIndex + `` + filter.id"
                :userFilterObj="filter"
                v-model="lookupFilterModel[filter.id]"
                @valueChanged="handleValueChange"
              >
              </dashboard-lookup-filter-mobile>
            </div>
          </div>
          <div class="save-cancel-mobile flex pT10 pL15 pR15 pB20">
            <div class="m-cancel-btn" @click="handleClose">
              Cancel
            </div>
            <div class="m-save-btn" @click="applyFilters">
              Apply
            </div>
          </div>
        </div>
        <div class="db-filter-empty-state" v-else>
          <inline-svg
            class="db-filter-empty-state-icon pointer"
            iconStyle="width:60%;height:60%;margin-left: auto;margin-right: auto;"
            src="svgs/dashboard/no-data"
          ></inline-svg>
          <div class="empty-state-txt">
            No dashboard filters ,create filters from web to view here
          </div>
        </div>
      </div>
    </template>
  </el-drawer>
</template>

<script>
import DashboardFilterContainer from 'pages/dashboard/dashboard-filters/DashboardFilterContainer'
import DashboardLookupFilterMobile from 'pages/dashboard/dashboard-filters/mobile/DashboardLookupFilterMobile'
import { deepCloneObject } from 'util/utility-methods'
import {
  getUserFilterModel,
  generateFilterJSON,
  getUserFilterObj,
  serializeUserFilters,
} from 'pages/dashboard/dashboard-filters/DashboardFilterHelper'

export default {
  components: {
    DashboardLookupFilterMobile,
  },
  extends: DashboardFilterContainer,
  props: ['visibility'],
  data() {
    return {
      reRenderIndex: 0,
      tempLookupFilterModel: {},
    }
  },
  computed: {
    drawerSize() {
      if (this.isUserFilterEmptyState) {
        return '50%'
      } else {
        return '90%'
      }
    },
  },
  methods: {
    handleCloseEvent() {
      console.log(
        'db filter ,close event fired from drawer , visibility',
        this.visibility
      )
      if (this.visibility) {
        this.handleClose()
      }
    },
    handleValueChange(value) {
      //one of the filter values is changed
    },
    handleOpen() {
      //copy current state to temp model , set lookup model to temp state if cancelled or closed without saving
      console.log(
        'handling db filter popup open ,lookupfilter model is',
        JSON.stringify(this.lookupFilterModel)
      )
      this.tempLookupFilterModel = deepCloneObject(this.lookupFilterModel)
    },
    handleClose() {
      console.log('handling db filter popup close')
      console.log(
        'setting db filter container model to ',
        this.tempLookupFilterModel
      )
      this.lookupFilterModel = this.tempLookupFilterModel
      this.$emit('update:visibility', false)
      this.reRenderIndex++
    },
    applyFilters() {
      let widgetFilterJson = generateFilterJSON(
        this.lookupFilterModel,
        this.dashboardFilterObj.dashboardUserFilters,
        this.dashboardFilterObj.widgetUserFiltersMap
      )
      console.log('mobile db filters applied', widgetFilterJson)
      this.$emit('dbUserFilters', {
        filterJson: widgetFilterJson,
        filterModel: this.lookupFilterModel,
      })
      this.$emit('update:visibility', false)
    },
  },
}
</script>

<style lang="scss">
.db-filter-mobile-popover {
  font: 'ProximaNova' !important;

  // margin-left:auto;
  // margin-right:auto;
  // width:90% !important;

  border-top-left-radius: 15px;
  border-top-right-radius: 15px;
  .el-drawer__header {
    margin-bottom: 10px !important;
  }
  .seperator {
    height: 1px;
    width: 100%;
    border-bottom: solid 1px #eff1f4;
  }

  .filter-title {
    font-size: 14px;
    font-weight: 500;

    color: #191a45;
    text-transform: uppercase;
  }
}

.db-filter-container-mobile {
  font-family: 'ProximaNova' !important;
  background: #ffffff;
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;

  .filter-container-body {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
  }

  .db-filter-empty-state {
    padding: 25px;
    display: flex;
    flex-direction: column;
    flex: 1 0 100%;
  }
  .db-filter-empty-state-icon {
    flex: 0 1 50%;
    display: flex;
    margin-top: 30px;
  }
  .empty-state-txt {
    flex: 0 1 50%;
    font-size: 14px;
    margin-bottom: 20px;
    font-weight: 500;
    margin-top: 20px;
    padding-left: 20px;
    padding-right: 20px;
  }

  .lookup-filter-section-scroll-container {
    flex-grow: 1;
    flex-shrink: 0;
    flex-basis: 0;
    overflow-y: scroll;
  }
  .save-cancel-mobile {
    flex-grow: 0;
    flex-shrink: 0;
    flex-basis: auto;
    justify-content: space-around;
    background: #ffffff;
    box-shadow: 0 -2px 13px 0 #e6e9ec;
  }

  .m-save-btn,
  .m-cancel-btn {
    font-size: 14px;
    font-weight: 600;
    text-align: center;
    vertical-align: middle;
    padding-top: 15px;
    padding-bottom: 15px;
    text-transform: uppercase;
    flex: 0 0 48%;
    border-radius: 5px;
    letter-spacing: 0.3px;
  }
  .m-save-btn {
    background-color: #ea4783;
    color: #ffffff;
  }
  .m-cancel-btn {
    color: #858585;
    background-color: #f8f7f7;
  }
}
</style>
