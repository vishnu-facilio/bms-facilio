<template>
  <div class="viewlist-page">
    <div>
      <transition name="fade">
        <outside-click
          :visibility.sync="canShowViewsList"
          class="workorder-list-container"
        >
          <div class="position-relative">
            <div class="fc-viewlist-header">
              <div class="label-txt-black fw-bold mB20 mT20">
                {{ $t('common._common.views') }}
                <i
                  class="el-icon-close fR label-txt-black fw-bold pointer"
                  v-on:click="canShowViewsList = false"
                ></i>
              </div>
              <input
                type="text"
                v-model="search"
                class="list-search-input"
                autofocus
                :placeholder="$t('common._common.search')"
              />
            </div>
            <div
              class="height100 overflow-y-scroll pB50"
              style="height: calc(100vh - 200px);"
            >
              <div v-if="search !== '' && !newGroup" class="mT20">
                <div class="wo-list-block">
                  <div v-for="(views, index) in filteredViews" :key="index">
                    <div
                      class="individual-view label-txt-black pT10 pB10 visibility-visible-actions pointer"
                      @click="changeView(views, index, filteredViews)"
                    >
                      <div class="width80 ellipsis">
                        {{ views.displayName }}
                      </div>
                      <i
                        v-if="showEditIcon"
                        v-bind:class="[
                          views.isDefault === false
                            ? 'el-icon-edit pointer edit-icon visibility-hide-actions fR customEdit'
                            : 'el-icon-edit pointer edit-icon visibility-hide-actions fR',
                        ]"
                        @click.stop="edit(views)"
                      ></i>
                      &nbsp;&nbsp;
                      <i
                        v-if="views.isDefault === false"
                        class="el-icon-delete pointer edit-icon visibility-hide-actions fR delete-icon"
                        @click.stop="deleteView(views)"
                      ></i>
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="newGroup">
                <div v-if="search !== ''" class="mT20">
                  <div class="wo-list-block">
                    <div v-for="(views, index) in filteredViews" :key="index">
                      <div
                        class="individual-view label-txt-black pT10 pB10 visibility-visible-actions pointer"
                        @click="changeView(views, index, filteredViews)"
                      >
                        <div class="width80 ellipsis">
                          {{ views.displayName }}
                        </div>
                        <i
                          v-if="showEditIcon"
                          v-bind:class="[
                            views.isDefault === false
                              ? 'el-icon-edit pointer edit-icon visibility-hide-actions fR customEdit'
                              : 'el-icon-edit pointer edit-icon visibility-hide-actions fR',
                          ]"
                          @click.stop="edit(views, true)"
                        ></i>
                        &nbsp;&nbsp;
                        <i
                          v-if="views.isDefault === false"
                          class="el-icon-delete pointer edit-icon visibility-hide-actions fR delete-icon"
                          @click.stop="deleteView(views, view.displayName)"
                        ></i>
                      </div>
                    </div>
                  </div>
                </div>
                <div
                  v-for="(view, key) in preparingViews"
                  :key="key"
                  v-if="search === '' && view.name !== 'sensorAlarmViews'"
                >
                  <div
                    v-if="
                      !$validation.isEmpty(preparingViews) &&
                        preparingViews.length > 1
                    "
                    class="fc-text-pink13 bold pB10 mT20"
                  >
                    {{ view.displayName }}
                  </div>
                  <div class="wo-list-block">
                    <div v-for="(views, index) in view.views" :key="index">
                      <div
                        class="individual-view label-txt-black pT10 pB10 visibility-visible-actions pointer"
                        @click="changeView(views, key)"
                      >
                        <div class="width80 ellipsis">
                          {{ views.displayName }}
                        </div>
                        <i
                          v-if="showEditIcon"
                          v-bind:class="[
                            views.isDefault === false
                              ? 'el-icon-edit pointer edit-icon visibility-hide-actions fR customEdit'
                              : 'el-icon-edit pointer edit-icon visibility-hide-actions fR',
                          ]"
                          @click.stop="edit(views, true)"
                        ></i>
                        &nbsp;&nbsp;
                        <i
                          v-if="views.isDefault === false"
                          class="el-icon-delete pointer edit-icon visibility-hide-actions fR delete-icon"
                          @click.stop="
                            deleteView(views, view.views.indexOf(views))
                          "
                        ></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div
                v-if="search === '' && !newGroup"
                v-for="(view, key) in preparingViews"
                :key="key"
              >
                <div class="fc-text-pink13 bold pB10 mT20">
                  {{ mainViewsList[key] }}
                </div>
                <div class="wo-list-block">
                  <div
                    v-for="(views, index) in preparingViews[key]"
                    :key="index"
                  >
                    <div
                      class="individual-view label-txt-black pT10 pB10 visibility-visible-actions pointer"
                      @click="changeView(views, key)"
                    >
                      <div class="width80 ellipsis">
                        {{ views.displayName }}
                      </div>
                      <i
                        v-if="showEditIcon"
                        v-bind:class="[
                          views.isDefault === false
                            ? 'el-icon-edit pointer edit-icon visibility-hide-actions fR customEdit'
                            : 'el-icon-edit pointer edit-icon visibility-hide-actions fR',
                        ]"
                        @click.stop="edit(views)"
                      ></i>
                      &nbsp;&nbsp;
                      <i
                        v-if="views.isDefault === false"
                        class="el-icon-delete pointer edit-icon visibility-hide-actions fR delete-icon"
                        @click.stop="deleteView(views)"
                      ></i>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div
              v-if="showViewSchedulerListTab"
              class="position-relative fc-viewlist-fixed"
            >
              <div class="scheduled-viewall" @click="loadViewReports()">
                View All Scheduled List
              </div>
            </div>
          </div>
        </outside-click>
      </transition>
    </div>
    <div v-if="showSaveDialog">
      <save-as-new-view
        :newViewName="newViewName"
        :share="shareTo"
        :modName="metaInfo.module.name"
        :selectedColumn="selectedColumn"
        :config="config"
        :showSaveDialog.sync="showSaveDialog"
      ></save-as-new-view>
    </div>
  </div>
</template>
<script>
import { mapActions, mapGetters, mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import FilterMixinHelper from '@/mixins/FilterMixin'
import OutsideClick from '@/OutsideClick'
import SaveAsNewView from '@/SaveAsNewView'
export default {
  mixins: [FilterMixinHelper],
  props: [
    'views',
    'preparingViews',
    'mainViewsList',
    'parentPathList',
    'showListWorkorder',
    'config',
    'showEditIcon',
    'moduleName',
    'newGroup',
    'showViewSchedulerListTab',
  ],
  data() {
    return {
      search: '',
      showSaveDialog: false,
      newViewName: '',
      shareTo: '',
      selectedColumn: {
        id: null,
        label: null,
      },
    }
  },
  watch: {
    preparingViews(val, oldVal) {
      this.$emit('update:preparingViews', val)
    },
  },
  mounted() {},
  computed: {
    ...mapActions({
      loadModuleMeta: 'view/loadModuleMeta',
      loadViews: 'view/loadViews',
      loadGroupViews: 'view/loadGroupViews',
      loadViewDetail: 'view/loadViewDetail',
      savesorting: 'view/savesorting',
    }),
    ...mapGetters(['getTicketTypePickList']),
    canShowViewsList: {
      get() {
        return this.showListWorkorder
      },
      set(value) {
        this.$emit('update:showListWorkorder', value)
      },
    },
    tickettype() {
      return this.getTicketTypePickList()
    },
    filteredViews: function() {
      let self = this
      if (this.parentPathList) {
        let allModuleViews = []
        let allModuleKeys = Object.keys(this.mainViewsList)
        for (let moduleKey in allModuleKeys) {
          for (let prepView in this.preparingViews[allModuleKeys[moduleKey]]) {
            this.preparingViews[allModuleKeys[moduleKey]][prepView][
              'moduleName'
            ] = allModuleKeys[moduleKey]
            let temp = this.preparingViews[allModuleKeys[moduleKey]][prepView]
            allModuleViews.push(temp)
          }
        }
        return allModuleViews.filter(function(view) {
          return (
            view.displayName.toLowerCase().indexOf(self.search.toLowerCase()) >=
            0
          )
        })
      }
      if (this.newGroup) {
        let lists = []
        for (let i = 0; i < self.preparingViews.length; i++) {
          for (let j = 0; j < self.preparingViews[i].views.length; j++) {
            lists.push(self.preparingViews[i].views[j])
          }
        }
        return lists.filter(function(b) {
          return (
            b.displayName.toLowerCase().indexOf(self.search.toLowerCase()) >= 0
          )
        })
      } else {
        return this.views.filter(function(view) {
          return (
            view.displayName.toLowerCase().indexOf(self.search.toLowerCase()) >=
            0
          )
        })
      }
    },
  },
  components: {
    OutsideClick,
    SaveAsNewView,
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAssetDepartment')
    this.$store.dispatch('loadAssetType')
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('loadGroups')
  },
  methods: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketstatus: state => state.ticketStatus.workorder,
      ticketpriority: state => state.ticketPriority,
      ticketcategory: state => state.ticketCategory,
      alarmseverity: state => state.alarmSeverity,
      assetcategory: state => state.assetCategory,
      assetdepartment: state => state.assetDepartment,
      assettype: state => state.assetType,
      moduleMeta: state => state.view.metaInfo,
      viewDetail: state => state.view.currentViewDetail,
    }),
    changeView(views, mainListName, filteredList) {
      // this.$router.push(views.name)
      if (views) {
        if (views.path) {
          this.$router.push(views.path)
        } else if (
          this.parentPathList &&
          filteredList &&
          filteredList[mainListName]
        ) {
          this.$router.push(
            this.parentPathList[filteredList[mainListName].moduleName] +
              views.name
          )
        } else if (
          this.parentPathList &&
          mainListName &&
          this.parentPathList[mainListName]
        ) {
          this.$router.push(this.parentPathList[mainListName] + views.name)
        } else if (
          this.config.moduleName === 'asset' ||
          this.config.moduleName === 'newreadingalarm'
        ) {
          if (views.moduleName === '') {
            views.moduleName = 'asset'
          }
          this.$store.dispatch('view/loadModuleMeta', views.moduleName)
          this.$router.push(views.name)
        } else {
          this.$router.push(views.name)
        }
      }
      this.canShowViewsList = false
      this.showListDialog = false
    },
    deleteView(a) {
      this.$emit('delete', a, 'index')
    },
    loadViewReports() {
      let { moduleName } = this
      if (moduleName === 'workorder') {
        this.$router.push('/app/wo/schedule')
      } else if (moduleName === 'newreadingalarm') {
        this.$router.push('/app/fa/schedule')
      } else {
        this.$router.push('/app/at/schedule')
      }
    },
    edit(view) {
      let self = this
      let a
      if (self.newGroup) {
        if (view.moduleName === '' || view.moduleName === 'newreadingalarm') {
          view.moduleName = this.moduleName
          a = this.moduleName
        } else {
          a = view.moduleName
        }
        this.$store.dispatch('view/loadModuleMeta', a).then(() => {
          this.$router.push(view.name)
          self.selectedColumn = []
          let url = '/view/' + view.name + '?moduleName=' + a
          self.$http.get(url).then(response => {
            self.newViewName = response.data.displayName
            if (response && response.data) {
              let { viewSharing } = response.data
              if (!isEmpty(viewSharing)) {
                viewSharing = viewSharing.filter(
                  viewSharingContext => viewSharingContext.typeEnum != 'APP'
                )
                let sharingTypeLength = viewSharing.length
                if (sharingTypeLength > 1) {
                  this.shareTo = 3
                } else if (
                  sharingTypeLength === 1 &&
                  viewSharing[0].userId === this.$account.user.ouid
                ) {
                  this.shareTo = 1
                } else {
                  this.shareTo = 2
                }
              } else {
                this.shareTo = 2
              }
            }
            if (response.data.fields) {
              for (let t of response.data.fields) {
                if (!isEmpty(t.field)) {
                  self.selectedColumn.push({
                    id: t.field && t.field.fieldId ? t.field.fieldId : -1,
                    label:
                      t.field && t.field.displayName
                        ? t.field.displayName
                        : t.columnDisplayName,
                  })
                } else {
                  self.selectedColumn.push({
                    id: t.field && t.field.fieldId ? t.field.fieldId : -1,
                    label:
                      t.field && t.field.displayName
                        ? t.field.displayName
                        : t.columnDisplayName,
                    fieldName: t.fieldName,
                  })
                }
              }
            }
            self.$router.push(response.data.name)
            self.canShowViewsList = false
            self.showSaveDialog = true
          })
        })
      } else {
        self.selectedColumn = []
        let url = '/view/' + view.name + '?moduleName=' + self.config.moduleName
        self.$http.get(url).then(response => {
          self.newViewName = response.data.displayName
          if (response && response.data) {
            let { viewSharing } = response.data
            if (!isEmpty(viewSharing)) {
              viewSharing = viewSharing.filter(
                viewSharingContext => viewSharingContext.typeEnum != 'APP'
              )
              let sharingTypeLength = viewSharing.length
              if (sharingTypeLength > 1) {
                this.shareTo = 3
              } else if (
                sharingTypeLength === 1 &&
                viewSharing[0].userId === this.$account.user.ouid
              ) {
                this.shareTo = 1
              } else {
                this.shareTo = 2
              }
            } else {
              this.shareTo = 2
            }
          }
          if (response.data.fields) {
            for (let t of response.data.fields) {
              self.selectedColumn.push({
                id: t.field.fieldId,
                label: t.field.displayName,
              })
            }
          }
          self.$router.push(response.data.name)
        })
        self.canShowViewsList = false
        self.showSaveDialog = true
      }
    },
  },
}
</script>
<style>
.fc-subheader-container {
  width: 100%;
  height: 60px;
  background: #ffffff;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-box-sizing: border-box;
  padding: 20px 30px;
}

.fc-subheader-left {
  display: flex;
  flex-direction: row;
  position: relative;
}

.fc-subheader-right {
  display: flex;
  flex-direction: row;
  height: 0;
  margin-right: 12px;
}

.fc-subheader-right-search {
  position: relative;
  bottom: 11px;
  right: -2px;
}

.create-btn {
  margin-top: -10px;
}

.subheader-tab {
  background: #fff;
  height: 2px;
}

.subheader-tab.active {
  background: #ef508f;
  margin-top: 8px;
  width: 20px;
}

.wo-three-line {
  position: relative;
  top: 2px;
}

.individual-view {
  position: relative;
  text-transform: capitalize;
  padding-left: 10px;
  padding-right: 10px;
  font-size: 14px;
  color: #333333;
  height: 40px;
}

.individual-view:hover {
  background-color: #f1f8fa;
  border-radius: 3px;
}

.individual-view .el-icon-edit {
  position: absolute;
  right: 8px;
  top: 10px;
}

.individual-view .customEdit {
  position: absolute;
  right: 30px;
  top: 10px;
}

.wo-list-block {
  margin-bottom: 20px;
}

.view-dialog-body .el-dialog__body {
  padding-top: 10px;
}

.view-dialog-body .el-dialog__title {
  padding-left: 10px;
}

.border-right-remove:last-child {
  border-right: none;
}

.fc-dropdown-menu {
  font-weight: 500;
  color: #2d2d52;
}

.comment-close {
  position: relative;
  top: 0;
  right: 10px;
  opacity: 0.3;
  color: #000000;
}

.el-icon-edit.pointer.edit-icon.visibility-hide-actions.fR.editview {
  position: absolute;
  right: 38px;
  top: 10px;
}

.save-btn-separator {
  font-size: 0;
  color: TRANSPARENT;
  padding-right: 0;
  padding-left: 0;
  border-left: 1px SOLID #efefef;
  margin-left: 10px;
  margin-right: 10px;
}

.subheader-saveas-btn:hover {
  background-color: rgba(125, 103, 184, 0.1);
  color: #372668;
}

/* .sort-icon {
 width: 17px;
 background-color: #ffffff;
 margin-top: 6px;
 }
 .sort {
 margin-top: -16px;
 } */
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

.sh-selection-bar {
  border: 1px solid #ee518f;
  width: 25px;
  margin-top: 5px;
  position: absolute;
}

.filter-search-close {
  font-size: 18px;
  position: absolute;
  right: 85px;
  top: 10px;
  color: #8ca1ad;
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
.dp-saveas-btn {
  background-color: #39b2c2;
  border-color: #39b2c2;
  padding: 6px 4px;
  font-size: 10px;
  font-weight: 500;
  border-radius: 4px;
  letter-spacing: 0.3px;
  color: #ffffff;
}
.dp-saveas-btn:hover {
  background: #33a6b5;
  color: #fff;
}
.fc-viewlist-header {
  position: sticky;
  top: 0px;
  z-index: 200;
  background: #fff;
}
.fc-viewlist-fixed .scheduled-viewall {
}
</style>
