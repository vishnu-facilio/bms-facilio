<template>
  <div class="width100">
    <div v-if="!id" class="page-width-cal agent-commissioning-page">
      <el-header class="fc-agent-main-header" height="80">
        <div class="inline-flex justify-content-space">
          <div class="fc-agent-black-26">
            {{ $t('commissioning.list.commissioning') }}
          </div>
          <div class="flex-middle">
            <div v-if="showSearch">
             <AdvancedSearch
               :key="`${moduleName}-search`"
               :moduleName="moduleName"
               :moduleDisplayName="moduleDisplayName"
               >
             </AdvancedSearch>
            </div>
            <span
              class="separator"
              v-if="totalCount"
              >|</span
            >
            <pagination
              :total="totalCount"
              :currentPage.sync="page"
              :perPage="perPage"
              class="nowrap"
            ></pagination>
            <span
              class="separator"
              v-if="totalCount"
              >|</span
            >
            <div>
              <el-button
                class="fc-agent-add-btn"
                @click="openCommissioningCreation"
              >
                <i class="el-icon-plus pR5 fwBold"></i
                >{{ $t('commissioning.list.add_commissioning') }}
              </el-button>
            </div>
          </div>
        </div>
      </el-header>
      <div>
      <FTags :key="moduleName"></FTags>
    </div>
      <div class="white-bg mL20 mR20 mT10">
        <el-tabs
          v-model="commissioningTab"
          class="agent-tabs"
        >
          <el-tab-pane
            v-for="tab in tabs"
            :key="tab.key"
            :label="tab.label"
            :name="tab.key"
          >
            <div v-if="isLoading" class="mT40">
              <Spinner :show="isLoading"></Spinner>
            </div>
            <div v-else class="fc-agent-table p10 fc-list-table-container">
              <el-table
                ref="multipleSelection"
                :data="tableData"
                style="width: 100%"
                height="auto"
                class="fc-list-view p10 pT0 mT10 fc-table-td-height fc-table-viewchooser pB100"
                :class="commissioningTableHeight()"
              >
                <template slot="empty">
                  <img
                    class="mT20"
                    src="~statics/noData-light.png"
                    width="100"
                    height="100"
                  />
                  <div class="mT10 label-txt-black f14">
                    No Commissioining logs available.
                  </div>
                </template>
                <el-table-column label="agent" width="200" fixed="left">
                  <template v-slot="item">
                    <div @click="redirectToCommisisioning(item.row)">
                      {{ item.row.agentName }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="controllers" min-width="200">
                  <template v-slot="item">
                    {{ getControlltersName(item.row) }}
                  </template>
                </el-table-column>
                <el-table-column
                  label="Created Time"
                  width="200"
                  prop="sysCreatedTime"
                >
                  <template v-slot="item">
                    {{
                      $validation.isEmpty(item.row.sysCreatedTime)
                        ? '---'
                        : $options.filters.formatDate(item.row.sysCreatedTime)
                    }}
                  </template>
                </el-table-column>
                <el-table-column
                  label="Modified Time"
                  width="200"
                  prop="sysModifiedTime"
                >
                  <template v-slot="item">
                    {{
                      $validation.isEmpty(item.row.sysModifiedTime)
                        ? '---'
                        : $options.filters.formatDate(item.row.sysModifiedTime)
                    }}
                  </template>
                </el-table-column>
                <el-table-column
                  :label="commissioningTab === 'draft' ? '' : 'Published Time'"
                  width="250"
                  prop="publishedTime"
                >
                  <template v-slot="item">
                    {{
                      $validation.isEmpty(item.row.publishedTime)
                        ? ''
                        : $options.filters.formatDate(item.row.publishedTime)
                    }}
                    <div
                      class="d-flex"
                      v-if="$validation.isEmpty(item.row.publishedTime)"
                    >
                      <el-button
                        v-if="$validation.isEmpty(item.row.publishedTime)"
                        class="fc-agent-add-btn fc-agent-add-btn2"
                        :disabled="!item.row.isSaving && isPublishing"
                        :loading="item.row.isSaving"
                        @click="publishCommissioning(item.row)"
                      >
                        {{ $t('commissioning.list.publish') }}
                      </el-button>
                      <i
                        v-if="$validation.isEmpty(item.row.publishedTime)"
                        class="el-icon-edit pointer mL10 self-center label-txt-black"
                        @click="redirectToCommisisioning(item.row)"
                      ></i>
                      <span
                        v-if="$validation.isEmpty(item.row.publishedTime)"
                        class="self-center"
                        @click="showConfirmDelete(item.row.id)"
                      >
                        <inline-svg
                          src="svgs/delete"
                          class="pointer edit-icon-color mL10 vertical-text-top"
                          iconClass="icon icon-xs"
                        ></inline-svg>
                      </span>
                    </div>
                    <span v-else class="pointer">
                      <img
                        src="~assets/svgs/eye.svg"
                        title="view"
                        v-tippy
                        data-arrow="true"
                        class="mL10 vertical-text-top"
                        width="14"
                        height="14"
                        @click="redirectToCommisisioning(item.row)"
                      />
                    </span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      <router-view></router-view>
      <!-- Create new commissioning dialog box -->
      <CommissioningNew
        v-if="canShowCommissioningCreation"
        :canShowCommissioningCreation.sync="canShowCommissioningCreation"
      ></CommissioningNew>
    </div>
    <router-view v-if="id"></router-view>
  </div>
</template>
<script>
import CommissioningNew from 'pages/newcoms/CommissioningNew'
import Spinner from '@/Spinner'
import { isEmpty,isNull } from '@facilio/utils/validation'
import { findRouteForTab, tabTypes, getApp } from '@facilio/router'
import isEqual from 'lodash/isEqual'
import { API } from '@facilio/api'
import Pagination from 'src/components/list/FPagination'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import FTags from 'newapp/components/search/FTags'

export default {
  components: {
    CommissioningNew,
    Spinner,
    Pagination,
    AdvancedSearch,
    FTags,
  },
  props: ['id'],
  data() {
    return {
      tableData: [],
      canShowCommissioningCreation: false,
      isLoading: false,
      isPublishing: false,
      commissioningTab: 'all',
      tabs: [
        {
          key: 'all',
          label: 'All',
        },
        {
          key: 'draft',
          label: 'Draft',
        },
        {
          key: 'published',
          label: 'Published',
        },
      ],
       totalCount: null,
       perPage: 50,
       moduleName:'commissioninglog',
       moduleDisplayName:'Commissioning Log',
       showSearch:true
    }
  },
  created() {
    this.fetchCommissioningList()
  },
  computed: {
   page() {
      return this.$route.query.page || 1
    },
  filters() {
   if (this.$route.query.search) {
    return JSON.parse(this.$route.query.search)
  }
  return null
  }
},
  watch: {
    $route: {
      handler(newVal,oldVal) {
        if ((newVal!=oldVal)) {
          this.fetchCommissioningList()
        }
      },
    },
    commissioningTab(newVal, oldVal) {
      if (newVal != oldVal) {
        let query = this.$route.query
        this.$router.push({
          query: {...query,page:1,status: this.commissioningTab},
        })
      }
    },
  },
  methods: {
    getControlltersName(row) {
      let { controllers } = row
      let controllersLen = (controllers || []).length
      let controllerNames = ''
      if (!isEmpty(controllers)) {
        controllers.forEach((controller, index) => {
          if (index < 2) {
            let { name } = controller
            if (!isEmpty(controllerNames)) {
              controllerNames = `${controllerNames}, ${name}`
            } else {
              controllerNames = name
            }
          }
        })
        if (!isEmpty(controllerNames) && controllersLen > 2) {
          controllerNames = `${controllerNames}, +${controllersLen - 2} more`
        }
      }
      return controllerNames
    },
    openCommissioningCreation() {
      this.$set(this, 'canShowCommissioningCreation', true)
    },
    redirectToCommisisioning(row) {
      let { id } = row
      let { linkName: appName } = getApp()
      let { path } = findRouteForTab(tabTypes.CUSTOM, {
        config: {
          type: 'commissioning',
        },
      })
      this.$router.push({
        path: `/${appName}/${path}/${id}/edit`,
      })
    },
    publishCommissioning(row) {
      let { id } = row
      let url = 'v2/commissioning/publish'
      let promises = []
      let data = {
        id,
      }
      this.$set(row, 'isSaving', true)
      this.$set(this, 'isPublishing', true)
      promises.push(
        this.$http
          .post(url, data)
          .then(({ data: { message, responseCode } }) => {
            if (responseCode === 0) {
              this.$message.success('Published Successfully')
              this.fetchCommissioningList()
            } else {
              throw new Error(message)
            }
          })
          .catch(({ message }) => {
            this.$message.error(message)
          })
      )
      Promise.all(promises).finally(() => {
        this.$set(row, 'isSaving', false)
        this.$set(this, 'isPublishing', false)
      })
    },
    showConfirmDelete(id) {
      let dialogObj = {
        title: `${this.$t('commissioning.list.delete')}`,
        htmlMessage: `${this.$t('commissioning.list.confirm_delete_text')}`,
        rbDanger: true,
        rbLabel: 'Confirm',
      }
      this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          this.deleteCommissioning(id)
        }
      })
    },
    deleteCommissioning(id) {
      let url = `/v2/commissioning/delete?id=${id}`
      let { tableData } = this
      this.$http.get(url).then(({ data: { responseCode } }) => {
        if (responseCode === 0) {
          this.$message.success(
            `${this.$t('commissioning.list.delete_success_text')}`
          )
          if (!isEmpty(tableData)) {
            let deletedIndex = tableData.findIndex(data => data.id === id)
            tableData.splice(deletedIndex, 1)
            this.$set(this, 'tableData', tableData)
            this.loadCount()
          }
        }
      })
    },
    async fetchCommissioningList() {
      this.isLoading = true
        let params = {
        page: this.page,
        perPage: this.perPage,
     }
      let url = `v2/commissioning/list?status=${this.commissioningTab}`
      let { filters } = this
      let filterObj = { ...filters }
      let parsedFilters
      if (!isEmpty(filterObj)) {
        if (filterObj.resource) delete filterObj.resource
        parsedFilters = encodeURIComponent(JSON.stringify(filterObj))
      }
      if (!isEmpty(parsedFilters)) url = `${url}&filters=${parsedFilters}`

      let { error, data } = await API.get(url, params, { force: true })
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.tableData = data.logs || []
      }
      this.showSearch = isEmpty(this.tableData) ? false  : true
      this.isLoading = false
      this.loadCount()
    },
      async loadCount() {
      let url = `v2/commissioning/count?status=${this.commissioningTab}`
      let { filters } = this
      let filterObj = { ...filters }
      let parsedFilters
      if (!isEmpty(filterObj)) {
        if (filterObj.resource) delete filterObj.resource
        parsedFilters = encodeURIComponent(JSON.stringify(filterObj))
      }
      if (!isEmpty(parsedFilters)) url = `${url}&filters=${parsedFilters}`
      let { error, data } = await API.get(url, null,{force:true})
      if (error) {
        this.$message.error(error.message)
      } else {
        this.totalCount = this.$getProperty(data, 'count', null)
      }
    },
    commissioningTableHeight(){
      let { filters } = this
      return isNull(filters) ? "fc-commissioning-table" : "fc-commissioning-table-filter-applied"
    }  
  },
}
</script>
<style lang="scss">
.agent-commissioning-page {
  .handsontable .wtSpreader {
    padding-bottom: 215px;
  }
  .fc-commissioning-table {
    height: calc(100vh - 245px) !important;
  }
  .fc-commissioning-table-filter-applied {
    height: calc(100vh - 350px) !important;
  }  
}
</style>
