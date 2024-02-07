<template>
  <div class="fp-tyoe-switch">
    <el-select
      v-if="floorplanTypes.size > 1"
      v-model="floorplanId"
      filterable
      default-first-option
      placeholder="Type"
      size="mini"
      @change="setType"
      class="mapbox-select  fc-floorplan-room mR10"
    >
      <el-option
        v-for="(value, key) in floorplanTypes"
        :key="key"
        :label="types[key]"
        :value="value.id"
      ></el-option>
    </el-select>
    <!-- <el-select
      v-model="viewType"
      filterable
      default-first-option
      placeholder="Type"
      size="mini"
      @change="handleFloorplanSwitch"
      class="mapbox-select  fc-floorplan-room"
    >
      <el-option
        v-for="(value, key) in actionTypes"
        :key="key"
        :label="value.name"
        :value="value.viewerMode"
      ></el-option>
    </el-select> -->
    <template v-if="showDropdown">
      <el-dropdown trigger="click" @command="handleFloorplanSwitch">
        <el-button class="floorplan-mode-swicth">
          Edit Layout<i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="(value, index) in actionTypes"
            :key="index"
            :command="value.viewerMode"
            v-if="value.permission"
            >{{ value.name }}</el-dropdown-item
          >
          <el-dropdown-item></el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </template>
  </div>
</template>
<script>
import { eventBus } from '@/page/widget/utils/eventBus'
import { mapState, mapGetters } from 'vuex'

export default {
  props: ['floorplanTypes', 'indoorFloorplanFields', 'viewerMode'],
  data() {
    return {
      types: {
        1: 'Workstation',
        2: 'Lockers',
        3: 'Parkings',
      },
      floorplanId: null,
      viewType: null,
    }
  },
  mounted() {
    this.setFloorplanTypes()
    this.setFloorplanType()
    this.setVieMode()
  },
  computed: {
    ...mapGetters(['getCurrentUser']),
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),

    hasEditPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('EDIT', currentTab)
    },

    hasCreatePermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('CREATE', currentTab)
    },
    hasViewPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW', currentTab)
    },
    hasViewAssignmentPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_ASSIGNMENT', currentTab)
    },

    hasAssignmentDepartmentPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_ASSIGNMENT_DEPARTMENT', currentTab)
    },
    hasAssignmentOwnPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_ASSIGNMENT_OWN', currentTab)
    },
    hasViewBookingPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_BOOKING', currentTab)
    },
    hasBookingDepartmentPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_BOOKING_DEPARTMENT', currentTab)
    },
    hasBookingOwnPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_BOOKING_OWN', currentTab)
    },
    showEditOption() {
      if (this.hasViewPermission) {
        if (this.hasEditPermission) {
          return true
        }
      }
      return false
    },
    showCreateOption() {
      if (this.hasViewPermission) {
        if (this.hasCreatePermission) {
          return true
        }
      }
      return false
    },
    showViewOption() {
      if (this.hasViewPermission) {
        return true
      }
      return false
    },
    showViewAssignmentOption() {
      if (this.hasViewPermission) {
        if (this.hasViewAssignmentPermission) {
          return true
        }
      }
      return false
    },
    showAssignmentDepartmentOption() {
      if (this.hasViewPermission) {
        if (this.hasAssignmentDepartmentPermission) {
          return true
        }
      }
      return false
    },
    showAssignmentOwnOption() {
      if (this.hasViewPermission) {
        if (this.hasAssignmentOwnPermission) {
          return true
        }
      }
      return false
    },
    showViewBookingOption() {
      if (this.hasViewPermission) {
        if (this.hasViewBookingPermission) {
          return true
        }
      }
      return false
    },
    showBookingDepartmentOption() {
      if (this.hasViewPermission) {
        if (this.hasBookingDepartmentPermission) {
          return true
        }
      }
      return false
    },
    showBookingOwnOption() {
      if (this.hasViewPermission) {
        if (this.hasBookingOwnPermission) {
          return true
        }
      }
      return false
    },
    showDropdown() {
      return (
        this.showViewOption &&
        (this.showEditOption || this.showCreateOption) &&
        this.viewerMode === 'BOOKING' &&
        this.$helpers.isLicenseEnabled('NEW_FLOORPLAN')
      )
    },
    actionTypes() {
      return [
        {
          name: 'Edit Assignment',
          viewerMode: 'ASSIGNMENT',
          permission:
            this.showViewAssignmentOption ||
            this.showAssignmentDepartmentOption ||
            this.showAssignmentOwnOption,
        },
        {
          name: 'Edit Floorplan',
          viewerMode: 'edit',
          permission: this.showEditOption,
        },
      ]
    },
    floorplanFieldTypes() {
      if (this.indoorFloorplanFields && this.indoorFloorplanFields.length) {
        let field = this.indoorFloorplanFields.forEach(
          field => field.name === 'floorPlanType'
        )
        if (field?.enumMap) {
          return field.enumMap
        }
        return null
      }
      return null
    },
    floorplanquery() {
      if (this.$route.query) {
        return { ...this.$route.query, type: this.floorplanType }
      }
      return { type: this.floorplanType }
    },
    indoorFloorplanId() {
      if (this.$route && this.$route.params.floorplanid) {
        return parseInt(this.$route.params.floorplanid)
      }
      return null
    },
  },
  methods: {
    setVieMode() {
      this.viewType = this.viewerMode
    },
    handleFloorplanSwitch(data) {
      if (data === 'ASSIGNMENT') {
        eventBus.$emit('OPEN_ASSIGNMENT_IN_DIALOG', data)
      }

      eventBus.$emit('SWITCH_FLOORPLAN_MODE', data)
    },
    setFloorplanTypes() {
      if (this.floorplanFieldTypes) {
        this.types = this.floorplanFieldTypes
      }
    },
    setFloorplanType() {
      if (this.indoorFloorplanId) {
        this.floorplanId = Number(this.indoorFloorplanId)
      }
    },
    setType() {
      if (this.floorplanId) {
        this.$router.push({
          params: { floorplanid: this.floorplanId },
        })
      }
    },
  },
}
</script>
<style lang="scss">
.floorplan-mode-swicth .el-button:focus,
.floorplan-mode-swicth .el-button:hover {
  background: #fff;
  border-color: #dedede;
  color: #5e6265;
}
.floorplan-mode-swicth {
  position: relative;
  top: 1px;
  border-radius: 5px !important;
  height: 32px !important;
  font-size: 13px !important;
  max-width: 150px;
  font-weight: 500;
  -webkit-box-shadow: 0px 0px 0 2px rgb(0 0 0 / 10%);
  box-shadow: 0px 0px 0 2px rgb(0 0 0 / 10%);
  border-color: transparent !important;
  padding: 0px 10px;
  background: #fff;
  color: #5e6265;
}
.fp-tyoe-switch {
  top: 10px;
  position: absolute;
  right: 60px;
}
.mapbox-select {
  .el-input__inner:focus {
    border-color: transparent !important;
  }
  .el-input__inner {
    border-radius: 5px !important;
    height: 32px !important;
    line-height: 29px !important;
    font-size: 13px !important;
    max-width: 150px;
    font-weight: 500;
    box-shadow: 0px 0px 0 2px rgb(0 0 0 / 10%);
    border-color: transparent !important;
  }
  .el-input__inner::placeholder {
    font-size: 12px !important;
  }
  .el-select-dropdown__item {
    font-size: 12px !important;
  }
}
</style>
<style scoped>
.el-select .el-input__inner:focus {
  border-color: transparent !important;
}
</style>
