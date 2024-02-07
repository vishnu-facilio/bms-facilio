<template>
  <div class="portal-layout employee-portal-header">
    <slot name="views-list">
      <ViewSideBar
        v-bind:class="{ showSidebar: canShowViewsSidePanel }"
        :canShowViewsSidePanel.sync="canShowViewsSidePanel"
        :groupViews="groupViews"
        :moduleName="moduleName"
        @onChange="goToView"
      ></ViewSideBar>
    </slot>
    <div class="flex-grow overflow-hidden flex-direction-column d-flex">
      <div class="header employee-portal-header-container">
        <slot name="views">
          <EmployeePortalViewHeader
            class="employeePortalViewHeader pT20"
            :moduleName="moduleName"
            :canShowViewsSidePanel.sync="canShowViewsSidePanel"
            :hideleftViewHeader="true"
            :groupViews="groupViews"
            :getRoute="getRoute"
            @onChange="goToView"
          ></EmployeePortalViewHeader>
        </slot>
        <div class="fR fc-subheader-right inline-flex">
          <slot name="header"></slot>
        </div>
      </div>
      <div
        class="height100 common-content-container"
        style="background-color: #fff;"
      >
        <slot name="content"></slot>
        <slot></slot>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { findRouteForTab, pageTypes } from '@facilio/router'
import ViewSideBar from 'PortalTenant/components/ViewSideBar'
import EmployeePortalViewHeader from 'src/newapp/components/EmployeePortalViewHeader.vue'

const { LIST } = pageTypes

export default {
  props: ['moduleName'],
  components: { ViewSideBar, EmployeePortalViewHeader },
  data() {
    return {
      canShowViewsSidePanel: false,
    }
  },
  computed: {
    ...mapState('view', {
      groupViews: state => {
        return !isEmpty(state.groupViews) ? state.groupViews : []
      },
    }),
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    currentView() {
      return this.$route.params.viewname
    },
  },
  watch: {
    moduleName: {
      async handler(newVal, oldVal) {
        if (newVal !== oldVal && !isEmpty(newVal)) {
          await this.$store.dispatch('view/clearViews')
          this.loadViews()
        }
      },
      immediate: true,
    },
    currentView(value) {
      // If current view is removed from URL, loadViews again and navigate to first group
      if (isEmpty(value)) this.loadViews()
    },
  },
  methods: {
    loadViews() {
      return this.$store
        .dispatch('view/loadGroupViews', {
          moduleName: this.moduleName,
        })
        .then(this.initViews)
    },
    initViews() {
      if (!this.currentView) {
        let { views } =
          this.groupViews.find(group => !isEmpty(group.views)) || {}

        if (!isEmpty(views)) {
          this.goToView(views[0])
        }
      }
    },
    goToView(view) {
      let route = this.getRoute(view)
      this.$router.replace(route)
    },
    getRoute(view) {
      if (isEmpty(view && view.name)) {
        return
      }

      let { id } = this.currentTab
      let { name = null } = findRouteForTab(id, { pageType: LIST }) || {}

      return name
        ? {
            name,
            params: { viewname: view.name },
          }
        : null
    },
  },
}
</script>
<style lang="scss" scope>
.employee-portal-homepage {
  .el-table--striped .el-table__body tr.el-table__row--striped.current-row td,
  .el-table__body tr.current-row > td,
  .el-table__body tr.hover-row.current-row > td,
  .el-table__body tr.hover-row.el-table__row--striped.current-row > td,
  .el-table__body tr.hover-row.el-table__row--striped > td,
  .el-table__body tr.hover-row > td {
    background: #fbfaff !important;
  }
}
.employee-portal-homepage .tags-container {
  border-top: 0;
  margin: 0;
  border-left: 0;
  border-right: 0;
  padding-top: 15px;
}
.employee-portal-header-container {
  .el-table th.el-table__cell {
    background: #f7f8f9;
    padding: 10px 20px;
  }
  .fc-list-table-container {
    padding: 0px;
    margin-top: 0px !important;
  }
  .subheader-tabs li.active a {
    font-weight: 600;
  }
  .subheader-tabs li.active .emp-selection-bar {
    border: 1.5px solid #0053cc;
    width: 25px;
    margin-top: 9.5px;
    position: absolute;
    border-radius: 2px;
  }
}
.employee-portal-header {
  // width: calc(100% - 22px) !important;
  // margin: 10px 10px 10px;
}
.portal-layout.employee-portal-header {
  display: flex;
  flex-direction: row;
  left: -1px;
  position: relative;
}
.portal-layout.employee-portal-header .header {
  border-left: none;
}
.employee-portal-header .portal-view-sidebar-fixed {
  border: none;
  border-right: 1px solid #f4f4f4;
  width: 0px;
  transition: width 0.3s ease 0s;
  position: relative;
  right: 200px;
}
.employee-portal-header .portal-view-sidebar-fixed.showSidebar {
  width: 240px;
  right: 0px;
}
.employee-portal-header {
  .el-table .el-table__cell {
    padding: 10px 20px !important;
  }
  .table-border {
    border: none;
  }
}

.portal-layout.employee-portal-header {
  display: flex;
  width: 100%;
  height: 100%;
  overflow: hidden;

  .subheader-tabs .hamburger-icon .icon.icon-sm {
    height: 12px;
    width: 12px;
  }
  .el-table th.is-leaf {
    background: #f5f7fa;
  }
  .portal-view-sidebar-fixed .portal-view-sidebar .view-item.active {
    background-color: #f5f7fa;
  }
  .header {
    background: #ffffff;
    position: relative;
    padding: 13px 30px;
    border-left: none;
    padding-bottom: 0px;
    padding-right: 15px;
    height: 45px;
    .header-details {
      display: flex;
      flex-direction: column;

      .center-title {
        height: 70px;
        justify-content: center;
      }
      .tab-header {
        display: flex;
        justify-content: space-between;

        .tab-name {
          font-size: 24px;
          font-weight: 400;
          letter-spacing: 0.67px;
          color: #000;
          line-height: 40px;
        }
      }
      .header-section {
        display: flex;
        align-items: center;
      }
    }
    .router-link-exact-active {
      .portal-active-tab {
        height: 2px;
        width: 25px;
        background: #ff3184;
        bottom: 0;
      }
    }
    .grp-view-name {
      font-weight: 500;
      color: #324056;
    }
  }
  .list-loading,
  .list-empty-state {
    display: flex;
    height: 100vh;
    background-color: #fff;
  }
  .list-empty-state {
    justify-content: center;
    align-items: center;
    flex-direction: column;
  }
  .list-container {
    margin: 0px;
    flex-grow: 1;
    overflow: hidden;
    .el-table {
      th > .cell {
        padding-left: 0px;
        padding-right: 0px;
      }
      th,
      th.is-leaf,
      td {
        padding: 10px 20px !important;
      }
      td:nth-child(2) {
        cursor: pointer;
      }
    }
  }
}
.employee-portal-header-container {
  padding: 0 20px !important;
  align-items: center;
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #eceef1;
  .fc-subheader-right {
    align-items: center;
  }
  .employeePortalViewHeader {
    li.active a {
      font-weight: bold;
    }
  }
}
.employee-portal-header-container .subheader-tabs li .sh-selection-bar {
  margin-top: 12px;
}
.employee-portal-header .fc-table-td-height .el-table td {
  padding: 10px 15px !important;
}
.portal-layout .list-container {
  height: 100%;
  margin: 0px !important;
}
.employee-portal-header .portal-view-sidebar .view-grp-name {
  padding: 15px 30px;
  font-weight: 500;
  color: #532fb8;
}
.portal-view-sidebar-fixed .portal-view-sidebar .view-item:hover {
  background-color: #f5f7fa;
}
.employee-portal-header .portal-view-sidebar {
  // position: absolute;
  // left: 0;
  // z-index: 100;
  // top: 0px;
  // box-shadow: 0 0 10px rgba(0, 0, 0, 0.12);
}
.employee-portal-header .portal-view-sidebar .back-icon:hover {
  background: rgb(178 186 189 / 30%);
}
</style>
