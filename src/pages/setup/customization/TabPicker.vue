<template>
  <div>
    <el-dialog
      class="tab-picker"
      :visible="true"
      width="50%"
      max-width="750px"
      :append-to-body="true"
      :show-close="false"
      :before-close="handleClose"
      custom-class="max-width750"
    >
      <div class="height500">
        <div class="header-content">
          <div class="header-title">Associate Tabs</div>
          <i
            v-if="!showSearch"
            @click="toggleSearch"
            class="fa fa-search fa-search-asste-icon pointer"
            aria-hidden="true"
            style="margin: 7px;"
          ></i>
          <div v-else class="flex-middle">
            <div class="d-flex">
              <i class="el-icon-search pT7 pR10" style="color: #25243e;"></i>
              <el-input
                v-model="filterVal"
                placeholder="Search Tab"
                :autofocus="true"
              ></el-input>
            </div>
            <i class="el-icon-close pointer" @click="toggleSearch"></i>
          </div>
        </div>

        <el-row class="tab-list-header">
          <el-col :span="12">Tab Name</el-col>
          <el-col :span="12" class="pL30">Type</el-col>
        </el-row>

        <div v-if="$validation.isEmpty(filteredTabList)" class="empty-tab-list">
          <InlineSvg
            src="svgs/emptystate/readings-empty"
            iconClass="icon text-center icon-130 emptystate-icon-size"
          ></InlineSvg>
          <div class="empty-state-text">
            There are no tabs avaiable to associated. Click here to create tab
          </div>
          <button @click="createTab" class="add-tab-btn">
            Create Tab
          </button>
        </div>

        <template v-else>
          <el-checkbox-group v-model="tabList" class="tab-list">
            <el-checkbox
              v-for="tab in filteredTabList"
              :key="tab.id"
              :label="tab.id"
              :disabled="tab.disabled"
              class="checkbox"
            >
              <el-row>
                <el-col :span="12">{{ tab.name }} </el-col>
                <el-col :span="12" class="pL5"
                  >{{ tabTypes[tab.type] }}
                </el-col>
              </el-row>
            </el-checkbox>
          </el-checkbox-group>

          <div class="modal-dialog-footer">
            <el-button @click="handleClose()" class="modal-btn-cancel">
              CANCEL
            </el-button>
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="save()"
              :loading="saving"
            >
              {{ saving ? 'Associating...' : 'Associate' }}
            </el-button>
          </div>
        </template>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import sortBy from 'lodash/sortBy'

export default {
  props: ['webtabs', 'webTabGroups', 'activeGroupId', 'tabTypes'],
  data() {
    return {
      filterVal: null,
      showSearch: false,
      tabList: [],
      saving: false,
    }
  },

  created() {
    document.addEventListener('keydown', this.keyDownHandler)
    this.setSelectedTabs()
  },

  beforeDestroy() {
    document.removeEventListener('keydown', this.keyDownHandler)
  },

  computed: {
    filteredTabList() {
      let { avaiableTabs, filterVal } = this

      if (!isEmpty(filterVal)) {
        let filterObj = avaiableTabs.filter(tab => {
          let name = tab.name.toLowerCase()

          return name.includes(filterVal)
        })
        return sortBy(filterObj, ['id'])
      } else {
        return sortBy(avaiableTabs, ['id'])
      }
    },
    disabledTabs() {
      let { webTabGroups, webtabs, activeGroupId } = this
      let disabledTabIds = []
      let associatedTabs = []

      webTabGroups.forEach(group => {
        if (group.id !== activeGroupId) {
          let { tabs } = group
          disabledTabIds = [...disabledTabIds, ...tabs.map(tab => tab.id)]
        }
      })
      associatedTabs = webtabs.filter(tab => disabledTabIds.includes(tab.id))

      return associatedTabs.map(tab => {
        let { name, id, type } = tab
        return { name, id, type, disabled: true }
      })
    },
    avaiableTabs() {
      let { webtabs, disabledTabs } = this
      let avaiableTabs = []
      let disabledTabIds = disabledTabs.map(tab => tab.id)

      avaiableTabs = webtabs.filter(tab => !disabledTabIds.includes(tab.id))

      return avaiableTabs
    },
  },

  methods: {
    setSelectedTabs() {
      let { webtabs, webTabGroups, activeGroupId } = this
      let { tabs } = webTabGroups.find(group => group.id === activeGroupId)
      let tabIds = tabs.map(tab => tab.id)

      this.tabList = webtabs
        .filter(tab => tabIds.includes(tab.id))
        .map(tab => tab.id)
    },
    toggleSearch() {
      this.filterVal = null
      this.showSearch = !this.showSearch
    },
    keyDownHandler(e) {
      if (e.key === 'Escape' && this.showSearch) {
        this.toggleSearch()
      }
    },
    save() {
      let { webtabs, tabList, activeGroupId } = this
      let activeGroupTabs = webtabs
        .filter(tab => tabList.includes(tab.id))
        .map((tab, order) => {
          let { id, name, type } = tab
          return { id, name, type, order: order + 1 }
        })

      let params = {
        tabList: tabList.map(tab => ({ id: tab })),
        tabGroupId: activeGroupId,
      }

      this.saving = true
      API.post('/v2/tab/addTabsToGroup', params).then(({ error }) => {
        if (error) {
          this.saving = false
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success('Tabs associated to group')
          this.$emit('updateGroupTabs', activeGroupTabs)
          this.$emit('reload')
          this.saving = false
          this.handleClose()
        }
      })
    },
    createTab() {
      this.$emit('createTab')
      this.handleClose()
    },
    handleClose() {
      this.$emit('close')
    },
  },
}
</script>
<style lang="scss">
.tab-picker {
  .max-width750 {
    max-width: 750px;
  }
  .el-dialog__header {
    padding: 0px;
  }
  .el-dialog__body {
    padding: 0px;
  }
  .header-content {
    padding: 15px 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border: solid 1px #e6e6e6;
    text-transform: uppercase;

    .header-title {
      font-size: 13px;
      font-weight: bold;
      letter-spacing: 1px;
      color: #324056;
    }
    .el-input .el-input__inner {
      border-bottom: none;
    }
  }
  .tab-list-header {
    text-transform: uppercase;
    background-color: #f9f9f9;
    font-size: 12px;
    font-weight: bold;
    letter-spacing: 0.92px;
    color: #324056;
    padding: 15px;
  }
  .tab-list {
    display: flex;
    flex-direction: column;
    overflow-y: scroll;
    height: 70%;
    padding-bottom: 5px;

    .checkbox {
      padding: 20px;
      margin: 0px;
      border-bottom: solid 1px #f3f5f6;
      display: flex;

      .el-checkbox__label {
        width: 100%;
      }
    }
  }
  .empty-state-text {
    font-size: 14px;
    letter-spacing: 0.5px;
    text-align: center;
    color: #324056;
  }
  .add-tab-btn {
    border-radius: 2.8px;
    border: solid 1px #3ab2c2;
    background-color: #ffffff;
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 1px;
    color: #3ab2c2;
    margin-top: 20px;
    padding: 5px 25px;
    text-transform: uppercase;
    cursor: pointer;
  }
  .empty-tab-list {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    height: 75%;
  }
}
</style>
