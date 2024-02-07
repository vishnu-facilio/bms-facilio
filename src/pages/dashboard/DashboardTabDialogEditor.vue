<template>
  <div class="db-tab-dialog-editor">
    <el-dialog
      :visible="visibility"
      class="db-user-filter-manager-dialog"
      :show-close="false"
      :append-to-body="true"
      :title="$t('home.dashboard.tab_manager')"
      width="40%"
      :before-close="closeDialog"
    >
      <div>
        <div class="p20 tab-section">
          <draggable
            class="tab-container"
            @change="updateOrder"
            v-model="dashboardTabList"
            v-bind="dashboardParentTabDragOptions"
          >
            <div
              v-for="(parentTab, pIndex) in dashboardTabList"
              :key="pIndex"
              class="mB10 mT15 box-border"
            >
              <div class="tab-drag-row pL15" @click="toggleExpand(parentTab)">
                <div class="task-handle mR10 pointer">
                  <img src="~assets/drag-grey.svg" />
                </div>

                <div v-if="editTabId && editTabId === parentTab.id">
                  <el-input
                    placeholder="Tab name"
                    v-model="parentTab.name"
                    @blur="editTabId = null"
                  ></el-input>
                </div>
                <div class="mT-auto mB-auto" v-else>
                  {{ parentTab.name }}
                </div>

                <div class="mL-auto mT-auto mB-auto inline-flex">
                  <div class="mR10 inline">
                    <i
                      class="el-icon-edit pointer trash-icon hoverIcon"
                      @click="editTabId = parentTab.id"
                    ></i>
                  </div>
                  <div class="mR10 inline">
                    <el-popover
                      :ref="`dynamic${pIndex}`"
                      placement="bottom"
                      width="280"
                      trigger="click"
                      :after-enter="nameChange"
                      popper-class="inventory-list-popover-1"
                    >
                      <div>
                        <el-row class="add-dashboard-popper p20">
                          <el-col :span="24" class="mB10">{{
                            'Subtab Name :'
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
                            @click="addNewTab(parentTab, pIndex)"
                            class="modal-btn-save width100"
                            >{{ $t('panel.share.ad') }}</el-button
                          >
                        </el-row>
                      </div>
                    </el-popover>
                    <i
                      v-popover="`dynamic${pIndex}`"
                      class="el-icon-plus pL10 hoverIcon"
                      v-tippy="{
                        placement: 'top',
                        animation: 'shift-away',
                        arrow: true,
                      }"
                      title="Add Sub Tab"
                    ></i>
                  </div>
                  <div class="mR10 inline">
                    <i
                      class="el-icon-delete pointer trash-icon hoverIcon"
                      @click="deleteTab(parentTab, true)"
                    ></i>
                  </div>
                  <i
                    v-if="
                      parentTab.expand &&
                        parentTab.childTabs &&
                        parentTab.childTabs.length > 0
                    "
                    class="el-icon-arrow-down"
                  ></i>
                  <i
                    v-else-if="
                      parentTab.childTabs && parentTab.childTabs.length > 0
                    "
                    class="el-icon-arrow-right"
                  ></i>
                  <i v-else class="el-icon-arrow-right opacity-0"></i>
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
                    class="pL15 pL25 tab-drag-row-child"
                    v-for="(childTab, chIndex) in parentTab.childTabs"
                    :key="chIndex"
                  >
                    <div class="task-handle mR10 pointer ">
                      <img src="~assets/drag-grey.svg" />
                    </div>

                    <div v-if="editTabId && editTabId === childTab.id">
                      <el-input
                        placeholder="Tab name"
                        v-model="childTab.name"
                        @blur="editTabId = null"
                      ></el-input>
                    </div>
                    <div class="mT-auto mB-auto" v-else>
                      {{ childTab.name }}
                    </div>

                    <div class="mL-auto mT-auto mB-auto">
                      <div class="mR10 inline-flex">
                        <i
                          class="el-icon-edit pointer trash-icon hoverIcon mR10"
                          @click="editTabId = childTab.id"
                        ></i>
                        <i
                          class="el-icon-delete pointer trash-icon hoverIcon"
                          @click="deleteTab(childTab, false, parentTab)"
                        ></i>
                      </div>
                    </div>
                  </div>
                </draggable>
              </div>
            </div>
          </draggable>
          <el-popover
            placement="bottom"
            width="280"
            trigger="click"
            v-model="showdialog"
            :after-enter="nameChange"
            popper-class="inventory-list-popover-1"
          >
            <!-- <el-button
              slot="reference"
              style="height: 35px !important;"
              type="primary"
              size="small"
              class="setup-el-btn mR10"
              ><i class="el-icon-plus"></i> Add Tab</el-button
            > -->
            <div slot="reference" class="f12 add-text">
              <i class="el-icon-plus"></i> Add Tab
            </div>
            <div>
              <el-row class="add-dashboard-popper p20">
                <el-col :span="24" class="mB10">{{ 'Tab Name :' }}</el-col>
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
        </div>
        <div class="dialog-save-cancel">
          <el-button class="modal-btn-cancel" @click="closeDialog">
            Cancel</el-button
          >
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="updateTabList()"
            >Save</el-button
          >
        </div>
      </div>
    </el-dialog>
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
      showdialog: false,
      showdialog1: false,
      editTabId: null,
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
    addTabDialog() {
      this.showdialog = true
    },
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
    addNewTab(parentTab, index) {
      this.showdialog = false
      this.showdialog1 = false
      if (index) {
        this.$refs[`dynamic${index}`][0].showPopper = false
      }
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
          this.newDashboardTabName = null
          this.loading.newTab = false
        })
        .catch(error => {
          this.$message.error('Error Occured')
          this.newDashboardTabName = null
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
    },
    updateTabList() {
      let tabsContext = this.dashboardTabList
      this.closeDialog()
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
    async deleteTab(tabContext, isParent, parentTab) {
      if (isParent && tabContext.childTabs && tabContext.childTabs.length > 0) {
        this.$message.error('Delete All Child Tabs Before Deleting Parent Tab')
      } else {
        let param = {
          dashboardTabId: tabContext.id,
        }
        let confirmObj = {
          title: 'Delete Tab',
          message: 'Are you sure you want to delete this ?',
          rbLabel: 'Yes, delete',
          lbLabel: 'No, cancel',
        }
        let deleteTabConfirmation = await this.$dialog.confirm(confirmObj)
        if (deleteTabConfirmation) {
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
            this.dashboardTabList = listdata
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
            this.dashboardTabList = listdata
          }
        }
      }
    },
  },
}
</script>
<style>
.tab-drag-row {
  padding: 15px;
  border: 1px solid #eef0f3;
  /* -webkit-box-shadow: 0px 3px 5px #f4f5f7;
  box-shadow: 0px 3px 5px #f4f5f7; */
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  font-size: 13px;
  cursor: pointer;
  background: #fbfbfd;
}
.hoverIcon {
  display: none;
}
.tab-drag-row:hover .hoverIcon {
  display: block;
}
.tab-drag-row-child {
  padding: 15px;
  /* -webkit-box-shadow: 0px 3px 5px #f4f5f7;
  box-shadow: 0px 3px 5px #f4f5f7; */
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  font-size: 13px;
  cursor: pointer;
  border: 1px solid #eef0f3;
  border-top: 0px;
}
.tab-drag-row-child:hover .hoverIcon {
  display: block;
}
.box-border {
  /* border: 1px solid #e0e0e0; */
}
.inventory-list-popover-1 {
  padding: 0px;
}
.tab-container {
  height: 380px;
  overflow: auto;
  margin-bottom: 20px;
}
.tab-section {
  height: 450px;
}
.add-text {
  text-decoration: underline;
  cursor: pointer;
  color: #39b2c2;
  width: 100px;
  font-weight: 500;
}
</style>
