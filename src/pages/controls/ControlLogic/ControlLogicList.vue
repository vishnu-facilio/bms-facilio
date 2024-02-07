<template>
  <div class="height100">
    <div class="position-relative">
      <portal to="controllogicpagenation" key="controllogiclistpage" slim>
        <el-tooltip
          effect="dark"
          :content="$t('common._common.search')"
          placement="left"
        >
          <div>
            <i
              class="el-icon-search fc-black-2 f16 pointer fw-bold mR10 control-logic-search"
              @click.stop="toggleQuickSearch()"
            ></i>
          </div>
        </el-tooltip>
      </portal>
      <div
        class="fc-black-small-txt-12 fc-subheader-right-search control-logic-search-filter"
        v-show="showQuickSearch"
      >
        <new-search
          :config="filterConfig"
          @hideSearch="showQuickSearch = false"
          :showSearch="showQuickSearch"
          :defaultFilter="defaultFilter"
        >
        </new-search>
        <div class="filter-search-close"></div>
      </div>
      <div v-if="appliedFilters !== null">
        <!-- <div class="fL" style="width: 84%;"> -->
        <new-tag
          :config="filterConfig"
          :filters="appliedFilters"
          :showFilterAdd="showAddFilter"
          :showCloseIcon="true"
          class="layout-new-tag"
        ></new-tag>
        <div
          class="clear-filter pointer control-logic-clear-filter"
          v-if="Object.keys(appliedFilters).length > '0'"
          @click="resetFilters()"
        >
          {{ $t('common._common.clear_all_filters') }}
        </div>

        <!-- </div> -->
      </div>
    </div>
    <div
      class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
      v-if="openControlId === -1"
      :class="$route.query.search ? 'fc-list-table-search-scroll' : ''"
    >
      <div v-if="loading" class="flex-middle fc-empty-white">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div v-else>
        <el-table
          :data="controllogicload"
          style="width: auto;"
          height="auto"
          type="index"
          :index="indexMethod"
          :fit="true"
          class="control-points-table"
        >
          <template slot="empty">
            <img
              class="mT50"
              src="~statics/noData-light.png"
              width="100"
              height="100"
            />
            <div class="mT10 label-txt-black f14 op6">
              No Control Points Available.
            </div>
          </template>
          <el-table-column label="ID" fixed width="150" prop="id">
            <template v-slot="scope">
              <div class="fc-id">{{ '#' + scope.row.id }}</div>
            </template>
          </el-table-column>
          <el-table-column label="NAME" fixed width="300" prop="id">
            <template v-slot="scope">
              <div class="max-width300px textoverflow-ellipsis">
                {{ scope.row.name }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="CREATED TIME" width="150" prop="id">
            <template v-slot="scope">
              {{ scope.row.createdTime | formatDate(true) }}
            </template>
          </el-table-column>
          <el-table-column label="MODIFIED TIME" width="150" prop="id">
            <template v-slot="scope">
              {{ scope.row.modifiedTime | formatDate(true) }}
            </template>
          </el-table-column>
          <el-table-column label="TYPE" width="150" prop="id">
            <template v-slot="scope">
              <div v-if="scope.row.ruleType === 34">
                <div>Reservation</div>
              </div>
              <div v-if="scope.row.ruleType === 35">
                <div>Rule Based</div>
              </div>
              <div v-if="scope.row.ruleType === 36">
                <div>Scheduled</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="STATUS" width="250" prop="id">
            <template v-slot="scope">
              <el-switch
                v-model="scope.row.status"
                @change="changestatus(scope.row, scope.row.status)"
                :active-value="true"
                :inactive-value="false"
                class="Notification-toggle"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#e5e5e5"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column
            prop
            label
            width="100"
            class="visibility-visible-actions"
          >
            <template v-slot="scope">
              <div class="text-center">
                <i
                  class="el-icon-edit pointer visibility-hide-actions"
                  @click="editControlRule(scope.row)"
                ></i>
                <i
                  class="el-icon-delete pointer visibility-hide-actions mL10"
                  @click="deleteControlRule(scope.row)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <new-logic
      v-if="controlLogicVisible"
      :controlLogic="selectedControlLogic"
      :visibility.sync="controlLogicVisible"
      @saved="onControlLogicCreated"
      :model="controllogicload"
    ></new-logic>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import NewSearch from '@/NewSearch'
import NewTag from '@/NewTag'
import NewLogic from 'pages/controls/ControlLogic/NewControlLogicForm'
import { mapState } from 'vuex'

export default {
  data() {
    return {
      showAddFilter: false,
      showQuickSearch: false,
      defaultFilter: 'name',
      filterConfig: {
        includeParentCriteria: true,
        path: '/app/co/cl/',
        data: {
          // fieldId: {
          //   label: 'Reading',
          //   displayType: 'select',
          //   options: {},
          //   value: []
          // },
          // resourceId: {
          //   label: 'Asset',
          //   displayType: 'select',
          //   options: {},
          //   value: []
          // },
          name: {
            label: 'Name',
            displayType: 'string',
            value: [],
          },
          ruleType: {
            label: 'Trigger',
            displayType: 'select',
            options: { 34: 'Reservation', 35: 'Alarms', 36: 'Schedule' },
            operatorId: 9,
            value: '',
          },

          // ttime: {
          //   label: 'Last recorded time',
          //   displayType: 'select',
          //   single: true,
          //   type: 'date',
          //   customdate: true,
          //   options: {
          //     '1_40': 'Within 1 Hour',
          //     '12_40': 'Within 12 Hours',
          //     '24_40': 'Within 24 Hours',
          //     22: 'Today',
          //     25: 'Yesterday',
          //     31: 'This Week',
          //     28: 'This Month',
          //     27: 'Last Month',
          //     '2_39': 'Last 2 Months',
          //     '6_39': 'Last 6 Months'
          //   },
          //   value: ''
          // },
        },
      },
      controllogicload: [],
      selectedControlLogic: null,
      loading: true,
      updatelogiclist: [],
      tableData: [],
      controlLogicVisible: false,
    }
  },
  computed: {
    ...mapState({
      controls: state => state.control.controls,
    }),
    openControlId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    filters() {
      let { $route } = this
      let {
        query: { search },
      } = $route || {}
      if (!isEmpty(search)) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    appliedFilters() {
      let {
        $route: { query },
      } = this
      let { search } = query || {}
      return search ? JSON.parse(search) : null
    },
  },
  watch: {
    filters: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.loadControlPoints()
        }
      },
    },
    appliedFilters: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          let {
            $route: { query },
          } = this
          let { search } = query || {}
          this.$router.push({
            query: {
              search: search,
              includeParentFilter: 'true',
            },
          })
        }
      },
    },
  },
  mounted: function() {
    this.loadControlPoints()
  },
  components: {
    NewLogic,
    NewSearch,
    NewTag,
  },
  methods: {
    setOptionsForAssets() {
      let url = `v2/controlAction/getControllableAssets`
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          if (
            response.data &&
            response.data.result &&
            response.data.result.controllableResources
          ) {
            response.data.result.controllableResources.forEach(asset => {
              this.filterConfig.data.resourceId.options[asset.id] = asset.name
            })
          }
          this.setOptionsForResources()
        }
      })
    },
    onControlLogicCreated() {
      this.loadControlPoints()
    },
    setOptionsForResources() {
      // data.siteId.options[sit.id] = sit.name
      let url = `v2/controlAction/getControllableAssets`
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          if (
            response.data &&
            response.data.result &&
            response.data.result.controllableFields
          ) {
            response.data.result.controllableFields.forEach(field => {
              this.filterConfig.data.fieldId.options[field.id] =
                field.displayName
            })
          }
        }
      })
    },
    toggleQuickSearch() {
      this.showQuickSearch = !this.showQuickSearch
    },
    resetFilters() {
      this.$router.push({
        path: '/app/co/cl/controlpoints',
        query: '',
      })
    },
    loadControlPoints() {
      let { filters } = this
      let self = this
      self.loading = true
      let url = '/v2/controlAction/getControlActionRules'
      if (filters) {
        url += `?filters=${encodeURIComponent(JSON.stringify(filters))}`
      }
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          this.controllogicload = response.data.result.workflowRules
          self.loading = false
        }
      })
    },
    indexMethod(index) {
      return index * 2
    },
    editControlRule(controlData) {
      this.controlLogicVisible = true
      this.selectedControlLogic = controlData
    },
    setdialogShow(scopeRow) {
      this.selectedScope = scopeRow
    },
    changestatus(scopeRow, status) {
      let self = this
      let queryParams = '&status=' + scopeRow.status + '&ruleId=' + scopeRow.id

      self.$http
        .post(`/v2/setup/alarm/rules/status`, queryParams)
        .then(response => {
          let res = response.data.result
          if (res.result === 'success') {
            self.$message.success(
              'Switched to ' +
                (res.result === true ? 'Sand Box' : 'Live') +
                ' status successfully.'
            )
          }
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    deleteControlRule(scopeRow) {
      let self = this
      self.$dialog
        .confirm({
          title: 'Delete Control Logic',
          message: 'Are you sure you want to delete this control logic?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(function(value) {
          if (value) {
            self.$http
              .get(
                `/v2/controlAction/deleteControlActionRule?ruleId=${scopeRow.id}`
              )
              .then(function(response) {
                if (response.data.result.ruleId) {
                  response.data.ruleId.forEach(element => {
                    let doControls = self.workflowRules.find(
                      r => r.id === element
                    )
                    if (doControls) {
                      self.workflowRules.splice(
                        self.workflowRules.indexOf(doControls),
                        1
                      )
                      self.$message.success(
                        self.$t('Control Logic deleted successfully')
                      )
                    }
                  })
                } else {
                  self.$message.success(
                    self.$t('Control Logic deleted successfully')
                  )
                }
              })
          }
        })
    },
  },
}
</script>
<style>
.fc-subheader-right-search {
  position: relative;
  bottom: 11px;
  right: -2px;
}
.control-logic-search {
  position: relative !important;
  top: 0px !important;
  margin-right: 0;
  right: 10px;
}
.control-logic-search-filter {
  position: absolute !important;
  z-index: 500;
  top: -58px !important;
  right: 15px !important;
  background: #fff;
}
.control-logic-clear-filter {
  position: absolute !important;
  right: 17px !important;
  top: 0 !important;
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #748893;
  float: right;
  margin-left: 10px;
  margin-top: 5px;
  cursor: pointer;
}
</style>
