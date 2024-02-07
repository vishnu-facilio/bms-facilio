<template>
  <div class="command-list">
    <el-tabs v-model="activeTab">
      <el-tab-pane
        v-for="tab in tabs"
        :key="tab.name"
        :name="tab.name"
        :label="tab.displayName"
        lazy
      >
        <div class="command-tab-content">
          <div v-if="showLoading" class="text-center width100 pT50 mT50">
            <spinner :show="showLoading" size="80"></spinner>
          </div>
          <el-table
            v-else-if="!$validation.isEmpty(commands)"
            :data="commands"
            :max-height="maxHeight"
          >
            <el-table-column
              label="ID"
              header-align="center"
              align="center"
              width="100px"
            >
              <template v-slot="command">
                <div class="fc-id">
                  {{ '#' + command.row.id }}
                </div>
              </template>
            </el-table-column>

            <el-table-column
              label="EXECUTED TIME"
              header-align="center"
              align="center"
            >
              <template v-slot="command">
                {{ command.row.executedTime | formatDate() }}
              </template>
            </el-table-column>
            <el-table-column
              label="ASSET"
              header-align="center"
              align="center"
              min-width="100px"
            >
              <template v-slot="command">
                {{ command.row.resource.name }}
              </template>
            </el-table-column>

            <el-table-column
              label="READING"
              header-align="center"
              align="center"
              min-width="100px"
            >
              <template v-slot="command">
                {{ command.row.field.displayName }}
              </template>
            </el-table-column>

            <el-table-column
              label="SET VALUE"
              header-align="center"
              align="center"
              min-width="100px"
            >
              <template v-slot="command">
                {{ getSetValue(command.row) }}
              </template>
            </el-table-column>
            <el-table-column
              label="EXECUTED BY"
              header-align="center"
              align="center"
            >
              <template v-slot="command">
                {{ command.row.executedBy.name }}
              </template>
            </el-table-column>
          </el-table>
          <div v-else class="no-commands-data">
            <img src="~statics/noData-light.png" width="100" height="100" />
            <div class="mT10 label-txt-black f14 op6">
              No {{ tab.displayName }} Available.
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    <div class="widget-topbar-actions">
      <pagination
        v-if="listCount"
        :total="listCount"
        :perPage="perPage"
        :currentPage.sync="page"
      ></pagination>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import { isBooleanField } from '@facilio/utils/field'
export default {
  components: { Pagination },
  props: ['group', 'isLoading', 'offset'],
  data() {
    return {
      commands: [],
      loading: false,
      activeTab: 'upcomingCommands',
      moduleName: 'controlActionCommand',
      tabs: [
        {
          name: 'upcomingCommands',
          displayName: 'Upcoming Commands',
          viewName: 'upcoming',
          filter: {
            status: { operatorId: 9, value: ['4', '5'] },
          },
        },
        {
          name: 'commandHistory',
          displayName: 'Commands History',
          viewName: 'history',
          filter: {
            status: { operatorId: 9, value: ['1', '2', '3'] },
          },
        },
      ],
      perPage: 30,
      listCount: null,
      page: 1,
      maxHeight: null,
    }
  },
  created() {
    this.loadRecords()
  },
  mounted() {
    this.maxHeight = this.$el.offsetHeight - 55 // height of this component - height of el-tabs header
  },
  computed: {
    showLoading() {
      return this.isLoading || this.loading
    },
  },
  watch: {
    group: {
      handler(value) {
        if (!isEmpty(value)) this.loadRecords()
      },
      immediate: true,
    },
    page: 'loadRecords',
    activeTab: {
      handler() {
        this.page = 1
        this.loadRecords()
      },
    },
  },
  methods: {
    async loadRecords() {
      this.loading = true
      let { id } = this.group
      let currTab = this.tabs.find(tab => tab.name === this.activeTab)
      let { filter, viewName } = currTab
      filter = {
        ...filter,
        group: { operatorId: 36, value: [id + ''] },
      }
      let params = {
        page: this.page || 1,
        perPage: this.perPage,
        withCount: true,
        filters: JSON.stringify(filter),
        viewName,
      }
      let { moduleName } = this
      let { list, error, meta } = await API.fetchAll(moduleName, params)
      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error Occured')
        this.commands = []
      } else {
        let {
          pagination: { totalCount },
        } = meta
        this.listCount = totalCount
        this.commands = list
      }

      this.loading = false
    },
    getSetValue(command) {
      let { field, value } = command
      if (isBooleanField(field)) {
        let { trueVal, falseVal } = field
        if (value) return trueVal
        else falseVal
      } else {
        let { unit } = field
        return `${value} ${unit}`
      }
    },
  },
}
</script>
<style scoped>
.widget-header {
  font-size: 14px;
  letter-spacing: 1px;
  font-weight: 500;
  color: #385571;
  padding: 10px;
  border-bottom: 1px solid #f7f8f9;
  background-color: #ffffff;
  padding-left: 20px;
}
.command-list {
  background-color: #ffffff;
  padding: 10px 20px;
  overflow: hidden;
  position: relative;
}
.no-commands-data {
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: center;
  margin-top: 200px;
}
.widget-topbar-actions {
  position: absolute;
  right: 15px;
  top: 15px;
  display: flex;
  flex-direction: row;
  align-items: center;
}
</style>
<style lang="scss">
.command-list .el-tabs__header {
  margin-bottom: 0;
}
</style>

<style lang="scss">
.command-list {
  .mT200 {
    margin-top: 200px;
  }
}
</style>
