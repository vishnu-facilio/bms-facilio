<template>
  <el-dialog
    :visible.sync="showDialogBox"
    width="90%"
    class="fc-dialog-center-containers f-lookup-wizard scale-up-center fc-wizard-table-width100"
    :append-to-body="true"
    :show-close="false"
    :before-close="handleClose"
  >
    <template slot="title" class="height70">
      <div class="header d-flex justify-between width100">
        <div class="el-dialog__title self-center f14">
          {{ isRead ? 'Viewers List' : 'Recipient List' }}
        </div>
        <div>
          <div class="mT10">
            <span
              class="close-btn self-center cursor-pointer"
              @click="handleClose"
            >
              <i class="el-dialog__close el-icon el-icon-close"></i>
            </span>
          </div>
        </div>
      </div>
    </template>
    <div class="horizontal-line"></div>
    <div class="header d-flex justify-between width100">
      <div
        class="pull-right table-btn display-flex-between-space mT5"
        style="z-index:10"
      >
        <SearchComponent
          v-if="showSearchComponent"
          :key="moduleName + '-search'"
          class="self-center mL-auto lookup-wizard-search mR10 mT10"
          :moduleName="moduleName"
          :searchMetaInfo="moduleMetaInfo"
          :hideOperatorDialog="true"
          :singleFieldSearch="true"
          :clearFilters="clearAllFilters"
          @onChange="applyFilter"
          @clearedFilters="clearAllFilters = true"
        >
        </SearchComponent>
        <el-tooltip
          effect="dark"
          content="Clear Filters"
          placement="right"
          v-if="!$validation.isEmpty(filterObjectsData)"
        >
          <span @click="clearFilter()" class="self-center"
            ><img
              src="~assets/filter-remove.svg"
              width="20px"
              height="20px"
              style="position: relative;top: 8px;left: 10px;"
          /></span>
        </el-tooltip>
        <span v-if="showSearchComponent" class="separator self-center mT5"
          >|</span
        >
        <pagination
          :currentPage.sync="page"
          :total="listCount"
          :perPage="perPage"
          class="self-center mT5"
        ></pagination>
      </div>
      <el-tabs
        v-model="moduleName"
        @tab-click="handleClick()"
        class="width100 people-tabs-container pL20 pR20 mT10"
      >
        <el-tab-pane
          v-for="(moduleObj, index) in modulesList"
          :key="'tab' + index"
          :name="moduleObj.modulename"
        >
          <span slot="label">{{ moduleObj.displayName }}</span>
          <div v-if="isLoading" class="mT40">
            <Spinner :show="isLoading"></Spinner>
          </div>
          <template v-else-if="$validation.isEmpty(items)">
            <div class="width100 fc-empty-center mB100">
              <img
                class="mT100"
                src="~statics/noData-light.png"
                width="100"
                height="100"
              />
              <div class="mT10">{{ 'No ' + moduleObj.displayName }}</div>
            </div>
          </template>
          <el-table
            v-else
            :data="items"
            :fit="true"
            height="500"
            style="width:100%;height:80%"
            class="fc-table-widget-scroll"
          >
            <el-table-column label="ID" width="70">
              <template v-slot="item">
                <div class="fc-id mL20">#{{ item.row.id }}</div>
              </template>
            </el-table-column>
            <el-table-column
              v-for="(field, index_field) in getModuleFieldsList"
              :key="'data' + index_field"
              :label="field.displayName"
              width="150"
            >
              <template v-slot="item">
                <div v-if="field.type && field.type === 'lookup'" class="mL20">
                  {{ item.row[field.fieldname].name || '---' }}
                </div>
                <div v-else class="mL20">
                  {{ item.row[field.fieldname] || '---' }}
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-dialog>
</template>

<script>
import { API } from '@facilio/api'
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import isEqual from 'lodash/isEqual'
import SearchComponent from 'newapp/components/WizardSearch'
import WizardFilterMixin from 'newapp/components/WizardQuickFilterAndSearchMixin'
import SearchTagMixin from 'newapp/components/SearchTagMixin'
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'

export default {
  mixins: [WizardFilterMixin, SearchTagMixin],
  props: ['details', 'sharingModuleName', 'showDialog', 'isRead'],
  components: {
    Pagination,
    SearchComponent,
    Spinner,
  },
  data() {
    return {
      items: [],
      page: 1,
      perPage: 20,
      showDialogBox: false,
      moduleName: 'employee',
      listCount: null,
      listFilters: null,
      isLoading: true,
      modulesList: [
        {
          displayName: 'Employee',
          modulename: 'employee',
        },
        {
          displayName: 'Vendor Contact',
          modulename: 'vendorcontact',
        },
        {
          displayName: 'Tenant Contact',
          modulename: 'tenantcontact',
        },
      ],
      commonFields: [
        {
          displayName: 'Name',
          fieldname: 'name',
        },
        {
          displayName: 'Email ID',
          fieldname: 'email',
        },
      ],
      moduleFieldsList: {
        tenantcontact: [
          {
            displayName: 'Tenant',
            fieldname: 'tenant',
            type: 'lookup',
          },
          {
            displayName: 'Tenant Unit',
            fieldname: 'tenantunit',
          },
          {
            displayName: 'Building',
            fieldname: 'building',
          },
          {
            displayName: 'Site',
            fieldname: 'site',
          },
        ],
        vendorcontact: [
          {
            displayName: 'Vendor',
            fieldname: 'vendor',
            type: 'lookup',
          },
        ],
        employee: [],
      },
      showSearchComponent: false,
    }
  },
  created() {
    this.initial(), (this.showDialogBox = this.showDialog)
  },
  watch: {
    page(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.setItemsList()
      }
    },
  },
  computed: {
    getModuleFieldsList() {
      let { moduleName, moduleFieldsList, commonFields } = this
      return commonFields.concat(moduleFieldsList[moduleName]) || []
    },
    ...mapGetters({
      getFilterConfig: 'search/getFilterConfig',
    }),
  },
  methods: {
    clearFilter() {
      this.clearAllFilters = true
      this.filterObjectsData = null
      this.listFilters = null
      this.page = 0
      this.setItemsList()
    },
    applyFilter(filterData) {
      if (isEmpty(filterData)) filterData = this.filterObjectsData
      else this.filterObjectsData = filterData

      this.page = 0
      let filters = this.formatDataForFilter(filterData)
      this.listFilters = filters
      this.setItemsList()
      this.showFilterCondition = false
    },
    async initial() {
      //employee people list
      await this.fetchModuleMetaInfo()
      await this.setItemsList()
    },
    async setItemsList() {
      this.isLoading = true
      let { details, listFilters, isRead } = this
      let params = {
        viewName: 'hidden-all',
        page: this.page,
        perPage: this.perPage,
        withCount: true,
        fetchAnnouncementPeople: true,
        announcementId: details.id,
      }
      if (!isEmpty(isRead)) {
        params.isRead = isRead
      }
      if (!isEmpty(listFilters)) {
        params.filters = JSON.stringify(listFilters)
      }
      this.loading = true

      let { moduleName } = this
      let { list, meta } = await API.fetchAll(moduleName, params)
      this.items = list
      let { pagination } = meta || {}
      let { totalCount } = pagination || {}
      this.listCount = totalCount
      if (totalCount > 0) {
        this.showSearchComponent = true
      } else {
        this.showSearchComponent = false
      }
      this.isLoading = false
    },
    async handleClick() {
      this.page = 1
      this.showSearchComponent = false
      this.isLoading = true
      await this.fetchModuleMetaInfo()
      await this.setItemsList()
    },
    handleClose() {
      this.showDialogBox = false
      eventBus.$emit('listDialogClose')
    },
    async fetchModuleMetaInfo() {
      let { moduleName } = this
      if (!isEmpty(moduleName)) {
        await API.get('/module/metafields?moduleName=' + moduleName)
          .then(response => {
            if (!isEmpty(response.data.meta))
              this.moduleMetaInfo = response.data.meta
            this.showSearch = true
          })
          .catch(() => {})
      }
    },
  },
}
</script>
<style lang="scss">
.fc-dialog-center-containers {
  .el-dialog {
    min-width: 1100px;
  }
  &.f-lookup-wizard {
    height: 650px;
    overflow: hidden;

    > .el-dialog {
      max-width: 998px;
    }
    .header {
      min-height: 35px;
    }
    .el-dialog__header {
      padding: 15px 20px;
    }
    .el-dialog__body {
      padding: 0;
      .fc-table-td-height {
        .el-table {
          tr {
            td:first-child + td,
            th:first-child + th {
              padding-left: 0px !important;
            }
          }
        }
      }
      .search-filter-tag {
        font-size: 13px;
        display: flex;
        letter-spacing: 0.5px;
        color: #748893;
        border-radius: 3px;
        background-color: #eaeef0;
        border: none;
        padding-top: 2px;
        margin-right: 10px;
        margin-bottom: 10px;
      }

      .search-filter-tag:hover {
        border-radius: 3px;
        background-color: #ddf1f4;
        font-size: 13px;
        letter-spacing: 0.5px;
        color: #38a1ae;
        border: none;
        padding-top: 2px;
        cursor: pointer;
      }
    }
    .fc-input-full-border2 {
      .el-input__inner {
        height: 35px !important;
      }
    }
    .separator {
      font-size: 18px;
    }
    .cell {
      .name {
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
      }
    }
    .search-icon {
      fill: #91969d;
    }

    .lookup-wizard-search {
      .fc-subheader-right-search {
        bottom: 0px;
      }
      .el-input__suffix {
        display: none;
      }
      .el-input__icon.el-icon-search.filter-space-search-icon3 {
        right: 5px;
        cursor: pointer;
      }
    }
  }
  .task-icon {
    fill: #dddddd;
  }
  .fc-create-btn {
    padding: 5px 7px;
  }

  .quick-filter {
    margin-left: 20px;
    &:last-of-type {
      margin-right: 20px;
    }
  }
}
.height40px {
  height: 40px;
}
.height30px {
  height: 30px;
}
.filter-key {
  background: rgba(234, 236, 238, 0.5) !important;
  color: rgb(107, 107, 107);
  font-weight: 400;
  border: 1px solid #d0d9e2 !important;
  font-size: 14px;
}
.close-icon {
  display: none;
  color: rgba(133, 153, 172, 1);
}

.name-input:hover .close-icon {
  display: block;
}
.name-input {
  border-radius: 100px;
}
.el-icon-close {
  font-weight: 500;
  font-size: 24px;
  color: #000000;
  text-align: right;
  cursor: pointer;
}
.horizontal-line {
  height: 0.5px;
  width: 100%;
  background-color: #e6e6e6;
  padding: 0px;
}
.people-tabs-container {
  .el-tabs__header {
    background: #fff;
    margin: 0px;
  }
  .el-tabs__nav-wrap {
    padding-left: 20px;
  }
}
.people-tabs-container .el-tabs__nav-wrap {
  padding-left: 20px;
  margin-top: 15px;
}
</style>
