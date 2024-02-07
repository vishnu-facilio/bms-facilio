<template>
  <div>
    <div class="dashboard-tab-editor">
      <el-dialog
        :visible.sync="visibility"
        :fullscreen="false"
        :before-close="closeDialog"
        :append-to-body="true"
        key="1"
        title="Dashboard Tab Editor"
        custom-class="fc-animated slideInRight fc-dialog-form contract-notification-form-heading fc-dialog-right width50 dashboard-tab-editor-dialog"
      >
        <div v-if="loading.initial">
          <spinner :show="loading.initial" class="mT20" size="80"></spinner>
        </div>
        <div v-else class="new-body-modal">
          <div class="mB30">
            <span class="heading-black18 fw4">{{
              $t('panel.share.list')
            }}</span>
            <span class="fR">
              <el-popover
                placement="bottom"
                width="280"
                trigger="click"
                :after-enter="nameChange"
                popper-class="inventory-list-popover"
              >
                <el-button
                  icon="el-icon-plus"
                  :loading="loading.newTab"
                  slot="reference"
                  class="el-button f12 el-button--text sh-button sh-button-add sp-sh-btn-sm"
                  :title="'Add Dashboard Tab'"
                  v-tippy
                  data-size="small"
                ></el-button>
                <div>
                  <el-row class="add-dashboard-popper">
                    <el-col :span="24" class="mB10">{{
                      $t('panel.share.name_')
                    }}</el-col>
                    <el-col :span="24">
                      <el-input
                        v-model="newDashboardTabName"
                        class="fc-input-full-border2"
                      ></el-input>
                    </el-col>
                  </el-row>
                  <el-row>
                    <el-button
                      :loading="loading.newTab"
                      @click="addNewTab(false)"
                      class="modal-btn-save width100"
                      >{{ $t('panel.share.ad') }}</el-button
                    >
                  </el-row>
                </div>
              </el-popover>
            </span>
          </div>
          <div v-if="dashboardTabList && dashboardTabList.length === 0">
            <div>
              <div class="attendance-transaction-no-data">
                <img src="~statics/noData-light.png" width="100" height="100" />
                <div class="mT10 label-txt-black f14">
                  {{ $t('panel.share.no_tabs') }}
                </div>
              </div>
            </div>
          </div>
          <div v-else-if="dashboardTabList && dashboardTabList.length > 0">
            <div>
              <draggable
                @change="updateOrder"
                v-model="dashboardTabList"
                v-bind="dashboardParentTabDragOptions"
                class="drag-folder dashboard-container-scroll pointer"
              >
                <div
                  v-for="(parentTab, pIndex) in dashboardTabList"
                  :key="pIndex"
                  class="asd"
                >
                  <div
                    class="flex-middle justify-content-space line-height40 fc-tab-editor-grey-bg1 dashboard-tab-container pL15"
                  >
                    <div
                      @click="toggleExpand(parentTab)"
                      class="inline-flex dashboard-tab-parent-name"
                    >
                      <div>
                        <i
                          v-if="
                            parentTab.expand &&
                              parentTab.childTabs &&
                              parentTab.childTabs.length > 0
                          "
                          class="el-icon-caret-bottom"
                        ></i>
                        <i
                          v-else-if="
                            parentTab.childTabs &&
                              parentTab.childTabs.length > 0
                          "
                          class="el-icon-caret-right"
                        ></i>
                        {{ parentTab.name }}
                      </div>
                    </div>
                    <div class="fR visibility-hide-actions">
                      <el-popover
                        placement="bottom"
                        width="280"
                        trigger="click"
                        :after-enter="nameChange"
                        popper-class="inventory-list-popover"
                      >
                        <i
                          slot="reference"
                          class="el-icon-plus pL10"
                          v-tippy="{
                            placement: 'top',
                            animation: 'shift-away',
                            arrow: true,
                          }"
                          title="Add Tab"
                        ></i>
                        <div>
                          <el-row class="add-dashboard-popper">
                            <el-col :span="24" class="mB10">{{
                              $t('panel.share.name_')
                            }}</el-col>
                            <el-col :span="24">
                              <el-input
                                v-model="newDashboardTabName"
                                class="fc-input-full-border2"
                              ></el-input>
                            </el-col>
                          </el-row>
                          <el-row>
                            <el-button
                              :loading="loading.newTab"
                              @click="addNewTab(parentTab)"
                              class="modal-btn-save width100"
                              >{{ $t('panel.share.ad') }}</el-button
                            >
                          </el-row>
                        </div>
                      </el-popover>
                      <i
                        class="el-icon-delete pL10 mR20"
                        v-tippy="{
                          placement: 'top',
                          animation: 'shift-away',
                          arrow: true,
                        }"
                        title="Delete Tab"
                        @click="deleteTab(parentTab, true)"
                      ></i>
                    </div>
                  </div>
                  <div v-if="parentTab.childTabs && parentTab.expand">
                    <draggable
                      @change="onDashBoardChildTabChange"
                      v-model="parentTab.childTabs"
                      v-bind="dashboardChildTabDragOptions"
                      class="drag-folder pointer"
                    >
                      <div
                        v-for="(childTab, pIndex) in parentTab.childTabs"
                        :key="pIndex"
                        class="asd"
                      >
                        <div
                          class="flex-middle justify-content-space fc-tab-editor-grey-bg2 line-height40 dashboard-tab-container pL40"
                        >
                          <div class="inline-flex dashboard-tab-child-name">
                            <div>{{ childTab.name }}</div>
                          </div>
                          <div class="fR visibility-hide-actions">
                            <i
                              class="el-icon-delete pL10 mR20"
                              v-tippy="{
                                placement: 'top',
                                animation: 'shift-away',
                                arrow: true,
                              }"
                              title="Delete Tab"
                              @click="deleteTab(childTab, false, parentTab)"
                            ></i>
                          </div>
                        </div>
                      </div>
                    </draggable>
                  </div>
                </div>
              </draggable>
            </div>
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel width100" @click="closeDialog()">{{
            $t('panel.share.close')
          }}</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import draggable from 'vuedraggable'
export default {
  props: ['dashboardObject', 'visibility'],
  components: {
    draggable,
  },
  data() {
    return {
      loading: {
        newTab: false,
        initial: false,
      },
      saving: false,
      dashboardTabList: [],
      dragOptions: {
        draggable: this.moveIt ? '.asd' : '',
        handle: '.icon-caret-right',
        ghostClass: 'drag-ghost',
        dragClass: 'custom-drag',
        animation: 150,
      },
      newDashboardTabName: null,
      moveIt: true,
    }
  },
  mounted() {
    this.constructDashboardTab()
  },
  computed: {
    dashboardChildTabDragOptions() {
      return {
        group: 'dashboardChildTab',
      }
    },
    dashboardParentTabDragOptions() {
      return {
        group: 'dashboardParentTab',
      }
    },
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    constructDashboardTab() {
      if (
        this.dashboardObject &&
        this.dashboardObject.tabEnabled &&
        this.dashboardObject.tabs
      ) {
        let order = 0
        for (let tab of this.dashboardObject.tabs) {
          this.$set(tab, 'expand', false)
          this.$set(tab, 'edit', false)
          this.$set(tab, 'sequence', ++order)
        }
        this.dashboardTabList = this.dashboardObject.tabs
      } else {
        this.dashboardTabList = []
      }
    },
    addNewTab(parentTab) {
      let param = {
        dashboardTabContext: {
          name: this.newDashboardTabName,
          sequence: this.dashboardTabList
            ? this.dashboardTabList.length + 1
            : 1,
          dashboardId: this.dashboardObject.id,
        },
      }
      if (parentTab && parentTab.id > 0) {
        param.dashboardTabContext['dashboardTabId'] = parentTab.id
        param.dashboardTabContext['sequence'] = parentTab.childTabs
          ? parentTab.childTabs.length + 1
          : 1
      }
      this.loading.newTab = true
      this.$http
        .post('dashboard/addDashboardTab', param)
        .then(response => {
          if (response.data) {
            this.$message.success('Dashboard Tab Added Successfully')
            let tabContext = response.data.dashboardTabContext
            if (parentTab) {
              if (parentTab.childTabs) {
                parentTab.childTabs.push(tabContext)
              } else {
                this.$set(parentTab, 'childTabs', [tabContext])
                parentTab.expand = true
              }
            } else {
              this.$set(tabContext, 'expand', false)
              this.$set(tabContext, 'edit', false)
              this.dashboardTabList.push(tabContext)
            }
          } else {
            this.$message.error('Error Occured')
          }
          this.loading.newTab = false
        })
        .catch(error => {
          this.$message.error('Error Occured')
          this.loading.newTab = false
        })
    },
    editTabName() {
      this.$message('Edit Tab name')
    },
    updateOrder() {
      let order = 0
      this.dashboardTabList.forEach(element => {
        element['sequence'] = ++order
      })
      this.updateTabList(this.dashboardTabList)
    },
    updateTabList(tabsContext) {
      let tempContextList = []
      if (tabsContext && tabsContext.length > 0) {
        for (let tab of tabsContext) {
          let tempContext = {}
          tempContext['dashboardId'] = tab.dashboardId
          tempContext['id'] = tab.id
          tempContext['name'] = tab.name
          tempContext['sequence'] = tab.sequence
          tempContext['dashboardTabId'] = tab.dashboardTabId
          tempContextList.push(tempContext)
        }
      }
      this.$http
        .post('dashboard/updateDashboardTabsList', {
          dashboardTabContexts: tempContextList,
        })
        .then(response => {
          if (response.data) {
            this.$message.success('Updated Successfully')
          } else {
            this.$message.error('Error Occured')
          }
        })
        .catch(error => {
          console.log(error)
          this.$message.error('Error Occured')
        })
    },
    deleteTab(tabContext, isParent, parentTab) {
      if (isParent && tabContext.childTabs && tabContext.childTabs.length > 0) {
        this.$message.error('Delete All Child Tabs Before Deleting Parent Tab')
      } else {
        let param = {
          dashboardTabId: tabContext.id,
        }
        this.$http
          .post('dashboard/deleteDashboardTab', param)
          .then(response => {
            if (response.data) {
              this.$message.success('Dashboard Tab Deleted Successfully')
              let index
              if (isParent) {
                index = this.dashboardTabList.indexOf(tabContext)
                this.dashboardTabList.splice(index, 1)
              } else {
                index = parentTab.childTabs.indexOf(tabContext)
                parentTab.childTabs.splice(index, 1)
              }
            } else {
              this.$message.error('Error Occured')
            }
            this.$router.replace('/')
          })
          .catch(error => {
            this.$message.error('Error Occured')
          })
      }
    },
    toggleExpand(val) {
      val.expand = !val.expand
    },
    nameChange() {
      this.newDashboardTabName = null
    },
    onDashBoardChildTabChange(data) {
      let dashboardTabList = this.dashboardTabList
      if (data['moved'] && data['moved'].element) {
        for (let i = 0; i < dashboardTabList.length; i++) {
          let list = dashboardTabList[i]
          if (
            list.childTabs &&
            list.childTabs.find(rl => rl.id === data['moved'].element.id)
          ) {
            let listdata = []
            let j = 0
            list.childTabs.forEach(tab => {
              listdata.push({
                id: tab.id,
                sequence: j,
                dashboardTabId: list.id,
              })
              j++
            })
            this.updateTabList(listdata)
          }
        }
      } else if (data['added'] && data['added'].element) {
        for (let i = 0; i < dashboardTabList.length; i++) {
          let list = dashboardTabList[i]
          if (
            list.childTabs &&
            list.childTabs.find(rl => rl.id === data['added'].element.id)
          ) {
            let listdata = []
            let j = 0
            list.childTabs.forEach(tab => {
              listdata.push({
                id: tab.id,
                displayOrder: j,
                dashboardTabId: list.id,
              })
              j++
            })
            this.updateTabList(listdata)
          }
        }
      }
    },
  },
}
</script>
<style scoped lang="scss">
.dashboard-tab-editor-dialog {
  .dashboard-tab-parent-name {
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 0.5px;
    color: #324056;
  }
  .dashboard-tab-child-name {
    font-size: 14px;
    letter-spacing: 0.5px;
    color: #324056;
  }
  .dashboard-container-scroll {
    height: calc(100vh - 260px);
    overflow-y: scroll;
    padding-bottom: 80px !important;
    padding-left: 0 !important;
    padding-right: 0 !important;
  }
  .dashboard-tab-container:hover .el-icon-delete,
  .dashboard-tab-container:hover .el-icon-edit,
  .dashboard-tab-container:hover .el-icon-plus {
    visibility: visible;
  }
  .fR .sh-button {
    border-radius: 3px;
  }
  .fc-tab-editor-grey-bg1 {
    background: #f2f2f2;
    border-bottom: 1px solid #dbe4ef;
  }
  .fc-tab-editor-grey-bg2 {
    background: #f8f9fa;
    border-bottom: 1px solid #ebedf4;
  }
}
.inventory-list-popover {
  .add-dashboard-popper.el-row {
    padding: 20px;
  }
}
</style>
