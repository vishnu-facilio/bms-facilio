<template>
  <div class="layout container">
    <subheader
      :menu="subheaderMenu"
      parent="/app/home/reservation"
      maxVisibleMenu="4"
      class="inventory_subheader"
    >
      <template slot="prefix">
        <div class="fL fc-subheader-left">
          <!-- DropDown -->
          <div class="pR15 pointer">
            <el-dropdown
              class="fc-dropdown-menu pL10"
              @command="openChild"
              v-if="moduleVsViews.preparingViews"
              trigger="click"
            >
              <span class="el-dropdown-link">
                {{ moduleVsViews.mainViewsList[selectedList] }}
                <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu
                slot="dropdown"
                v-if="moduleVsViews.preparingViews"
              >
                <el-dropdown-item
                  v-for="(view, key) in moduleVsViews.preparingViews"
                  :key="key"
                  :command="key"
                  >{{ moduleVsViews.mainViewsList[key] }}</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div
            v-if="moduleVsViews.preparingViews"
            class="fc-separator-lg mL10 mR10"
          ></div>
          <!-- Dialog for Edit View -->
        </div>
      </template>
      <div v-if="getCurrentViewType === 'table'" class="fR fc-subheader-right">
        <template>
          <div>
            <div v-if="getCurrentViewType === 'table'">
              <el-button
                class="fc-create-btn create-btn"
                @click="reservationFormVisibilityToggle"
              >
                <div>NEW RESERVATION</div>
              </el-button>
            </div>
          </div>
        </template>
      </div>
    </subheader>
    <div class="height100 row width100">
      <div class="height100 f-list-view">
        <router-view></router-view>
      </div>
    </div>
    <reservation-form
      v-if="reservationFormVisibility"
      :showCreateNewDialog.sync="reservationFormVisibility"
      @saved="loadReservations"
    ></reservation-form>
  </div>
</template>
<script>
import Subheader from '@/Subheader'
import ReservationForm from './ReservationForm'
import { mapState, mapActions } from 'vuex'

export default {
  data() {
    return {
      reservationFormVisibility: false,
      moduleVsViews: {
        mainViewsList: {},
        preparingViews: {},
        parentPathList: {},
      },
      selectedList: 'reservation',
    }
  },
  components: {
    Subheader,
    ReservationForm,
  },
  mounted() {
    this.loadAllModuleViews()
    this.loadGroupViews()
    this.$store.dispatch('view/loadModuleMeta', 'reservation')
    this.getViewDetail()
    if (!this.filterSelected && this.filters) {
      this.toggleViewFilter()
    }
    // this.loadSortConfigListForModule()
    // this.setFilterConfig()
  },
  computed: {
    ...mapActions({
      loadViewDetail: 'view/loadViewDetail',
    }),
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
    }),
    views() {
      return this.$store.state.view.groupViews
    },
    subheaderMenu() {
      return this.getSubHeaderMenu()
    },
    getCurrentViewType() {
      return this.$route.meta.type || 'table'
    },
    getCurrentModule() {
      return this.$route.meta.module
    },
    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
  },
  created() {
    this.$store.dispatch('view/clearViews')
  },
  watch: {},
  methods: {
    openChild(command) {
      let self = this
      self.selectedList = null
      self.selectedList = command
      let a2 = self.moduleVsViews.preparingViews[command]
      if (a2 && command !== 'planner') {
        self.$router.push(
          this.moduleVsViews.parentPathList[command] + a2[0].name
        )
      } else {
        self.$router.push(this.moduleVsViews.parentPathList[command])
      }
    },
    loadAllModuleViews() {
      let modules = [
        {
          moduleName: 'reservation',
          displayName: 'Reservations',
          parentPath: '/app/home/reservation/list/',
        },
        {
          moduleName: 'planner',
          displayName: 'Space Availability',
          parentPath: '/app/home/reservation/planner/',
        },
      ]
      let self = this
      for (let moduleKey of modules) {
        self.moduleVsViews.mainViewsList[moduleKey.moduleName] =
          moduleKey.displayName
        self.moduleVsViews.parentPathList[moduleKey.moduleName] =
          moduleKey.parentPath
        self.moduleVsViews.preparingViews[moduleKey.moduleName] = []
        if (moduleKey.moduleName === 'reservation') {
          self.$http
            .get(
              '/v2/views/viewList?moduleName=' +
                moduleKey.moduleName +
                '&groupStatus=' +
                'false'
            )
            .then(function(response) {
              if (response.data.result) {
                self.moduleVsViews.preparingViews[moduleKey.moduleName] =
                  response.data.result.views
              }
            })
            .catch(function(error) {
              console.log('######### error: ', error)
            })
        } else {
          self.moduleVsViews.preparingViews[moduleKey.moduleName] = [{}]
        }
      }
    },
    getSubHeaderMenu() {
      let list = []
      let self = this
      if (self.getCurrentViewType === 'table') {
        Object.keys(self.views).forEach(function(key) {
          let view = self.views[key]
          list.push({
            label: view.displayName,
            path: {
              path:
                self.moduleVsViews.parentPathList['reservation'] + view.name,
            },
            id: view.id,
            name: view.name,
          })
        })
      }
      return list
    },
    loadGroupViews() {
      let param = {
        moduleName: 'reservation',
        status: false,
      }
      this.$store.dispatch('view/loadGroupViews', param)
    },
    toggleViewFilter() {
      this.filterSelected = !this.filterSelected
    },
    getViewDetail() {
      this.$store.dispatch('view/loadViewDetail', {
        viewName: this.currentView,
        moduleName: 'reservation',
      })
    },
    reservationFormVisibilityToggle() {
      this.reservationFormVisibility = !this.reservationFormVisibility
    },
    loadReservations() {
      let queryObj = {
        viewname: this.currentView,
      }
      this.$store.dispatch('reservation/fetchReservations', queryObj)
    },
  },
}
</script>
<style>
.fc-subheader-left {
  display: flex;
  flex-direction: row;
  position: relative;
}

.fc-subheader-right-search-inventory {
  position: relative;
  bottom: 0px;
  right: -2px;
}

.create-btn {
  margin-top: -10px;
}

.wo-three-line {
  position: relative;
  top: 2px;
}

.fc-dropdown-menu {
  font-weight: 500;
  color: #2d2d52;
}

.el-icon-edit.pointer.edit-icon.visibility-hide-actions.fR.editview {
  position: absolute;
  right: 38px;
  top: 10px;
}

.subheader-saveas-btn:hover {
  background-color: rgba(125, 103, 184, 0.1);
  color: #372668;
}
.sort-icon {
  margin-top: -7px;
}

.pagination {
  padding-top: 13px;
}

.delete-icon {
  position: absolute;
  right: 6px;
  top: 10px;
}

.filter-search-close {
  font-size: 18px;
  position: absolute;
  right: 85px;
  top: 10px;
  color: #8ca1ad;
}

.layout-new-tag {
  width: 100%;
  padding-left: 20px;
  padding-right: 10px;
  margin-top: 24px;
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
}

.sort-icon-hover:hover {
  fill: #e7328a;
  transition: fill 0.5s ease;
}
.save-btn-section {
  width: 17%;
  position: absolute;
  top: 29px;
  right: 22px;
  text-align: right;
}
.subheader-saveas-btn {
  background: #39b2c2;
  border-radius: 3px;
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 0.3px;
  color: #ffffff;
  padding: 5px;
}
.subheader-saveas-btn:hover {
  background: #33a6b5;
  color: #fff;
}

.clear-filter {
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #748893;
  float: right;
  margin-left: 10px;
  margin-top: 5px;
  cursor: pointer;
}
#newworkordercategory .el-textarea .el-textarea__inner {
  min-height: 50px !important;
  width: 350px;
  resize: none;
}
.assetaddvaluedialog .inventory_subheader .subheader-section {
  background: #f8f9fa;
}
.fc-subheader-right-inventory {
  align-items: center;
  margin-top: 8px;
  margin-bottom: 8px;
}
</style>
