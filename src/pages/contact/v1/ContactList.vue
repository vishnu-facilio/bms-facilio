<template>
  <CommonListLayout
    :moduleName="getModuleName"
    :showViewRearrange="false"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => viewName"
    pathPrefix="/app/home/contact/"
  >
    <template #header>
      <template v-if="!showSearch">
        <pagination
          :total="listCount"
          :perPage="50"
          class="pL15 fc-black-small-txt-12"
        ></pagination>
        <span class="separator" v-if="listCount > 0">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.sort')"
          placement="right"
        >
          <sort
            :key="getModuleName + '-sort'"
            :config="sortConfig"
            :sortList="sortConfigList"
            @onchange="updateSort"
          ></sort>
        </el-tooltip>
        <span class="separator">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.export')"
          placement="left"
        >
          <f-export-settings
            :module="getModuleName"
            :viewDetail="viewDetail"
            :showViewScheduler="false"
            :showMail="false"
            :filters="filters"
          ></f-export-settings>
        </el-tooltip>
        <span class="separator">|</span>
      </template>

      <el-tooltip
        effect="dark"
        :content="$t('common._common.search')"
        placement="left"
      >
        <search
          :key="getModuleName + '-search'"
          :config="filterConfig"
          :moduleName="getModuleName"
          :defaultFilter="defaultFilter"
          class="fc-black-small-txt-12"
        ></search>
      </el-tooltip>

      <template v-if="!showSearch">
        <span class="separator pL10">|</span>
        <div>
          <button
            class="fc-create-btn create-btn"
            style="margin-top: -10px;"
            @click="addRecord()"
          >
            <i class="el-icon-plus"></i>
          </button>
        </div>
      </template>
    </template>
    <div class="height100">
      <tags></tags>
      <div
        class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
      >
        <div v-if="showLoading" class="flex-middle fc-empty-white">
          <spinner :show="showLoading" size="80"></spinner>
        </div>
        <div
          v-else-if="$validation.isEmpty(recordList)"
          class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column"
        >
          <inline-svg
            src="svgs/emptystate/contracts"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="nowo-label">
            {{ $t('home.contact.contact_no_data') }}
          </div>
        </div>
        <div v-else>
          <div class="view-column-chooser" @click="showColumnSettings = true">
            <img
              src="~assets/column-setting.svg"
              style="text-align: center; position: absolute; top: 35%;right: 29%;"
            />
          </div>
          <el-table
            :data="recordList"
            ref="tableList"
            class="width100"
            height="auto"
            :fit="true"
          >
            <el-table-column fixed prop label="ID" min-width="90">
              <template v-slot="data">
                <div class="fc-id">{{ '#' + data.row.id }}</div>
              </template>
            </el-table-column>
            <el-table-column fixed prop label="NAME" width="300">
              <template v-slot="data">
                <div
                  v-tippy
                  small
                  @click="openRecordSummary(data.row.id)"
                  :title="$getProperty(data, 'row.name', '---')"
                  class="flex-middle"
                >
                  <div class="mL10">
                    <div class="fw5 ellipsis textoverflow-ellipsis width200px">
                      {{ $getProperty(data, 'row.name', '---') }}
                    </div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              :align="field.field.dataTypeEnum === 'DECIMAL' ? 'right' : 'left'"
              v-for="(field, index) in viewColumns"
              :key="index"
              :prop="field.name"
              :label="field.displayName"
              min-width="200"
              v-if="!isFixedColumn(field.name) || field.parentField"
            >
              <template v-slot="data">
                <div v-if="!isFixedColumn(field.name) || field.parentField">
                  <div
                    class="table-subheading"
                    :class="{
                      'text-right': field.field.dataTypeEnum === 'DECIMAL',
                    }"
                  >
                    {{ getColumnDisplayValue(field, data.row) || '---' }}
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop
              label
              width="180"
              class="visibility-visible-actions"
              fixed="right"
            >
              <template v-slot="data">
                <div class="text-center">
                  <i
                    class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                    data-arrow="true"
                    :title="$t('home.contact.edit_contact')"
                    v-tippy
                    @click="editRecord(data.row)"
                  ></i>
                  <i
                    class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                    data-arrow="true"
                    :title="$t('home.contact.delete_contact')"
                    v-tippy
                    @click="deleteRecord(data.row.id)"
                  ></i>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <column-customization
        :visible.sync="showColumnSettings"
        :moduleName="getModuleName"
        :viewName="currentView"
      ></column-customization>
      <ContactForm
        :visibility.sync="editFormVisibility"
        :moduleName="getModuleName"
        :editData="editData"
        @saved="refreshList"
      ></ContactForm>
    </div>
    <portal to="view-manager-link">
      <div @click="getViewManagerRoute" class="view-manager-btn">
        <inline-svg
          src="svgs/hamburger-menu"
          class="d-flex"
          iconClass="icon icon-sm"
        ></inline-svg>
        <span class="label mL10 text-uppercase">
          {{ $t('viewsmanager.list.views_manager') }}
        </span>
      </div>
    </portal>
  </CommonListLayout>
</template>
<script>
import Spinner from '@/Spinner'
import ColumnCustomization from '@/ColumnCustomization'
import ViewMixinHelper from '@/mixins/ViewMixin'
import ContactForm from './ContactForm'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import CommonListLayout from 'newapp/list/DeprecatedCommonLayout'
import Pagination from '@/list/FPagination'
import Search from 'newapp/components/Search'
import Tags from 'newapp/components/Tags'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import { API } from '@facilio/api'
import { pageTypes } from '@facilio/router'
import { findRouterForModuleInApp } from 'newapp/viewmanager/routeUtil'

export default {
  mixins: [ViewMixinHelper],

  components: {
    Spinner,
    ColumnCustomization,
    ContactForm,
    CommonListLayout,
    Pagination,
    Search,
    Tags,
    FExportSettings,
    Sort,
  },

  data() {
    return {
      loading: true,
      recordList: null,
      showColumnSettings: false,
      editFormVisibility: false,
      editData: null,
      tableLoading: false,
      listCount: null,
      sortConfig: {
        orderBy: {
          label: 'Name',
          value: 'name',
        },
        orderType: 'asc',
      },
      sortConfigList: ['name', 'phone', 'email'],
      defaultFilter: 'name',
      filterConfig: {
        moduleName: 'contact',
        includeParentCriteria: true,
        path: '/app/home/contact/',
        data: {
          name: {
            label: 'Name',
            displayType: 'string',
            value: '',
          },
          phone: {
            label: 'Phone',
            displayType: 'string',
            value: '',
          },
          email: {
            label: 'Email',
            displayType: 'string',
            value: '',
          },
        },
        availableColumns: ['name', 'email', 'phone'],
        fixedCols: ['name'],
        saveView: true,
      },
    }
  },

  mounted() {
    this.loadRecords()
    this.loadRecordsCount()
  },

  computed: {
    ...mapState({
      showSearch: state => state.search.active,
      viewDetail: state => state.view.currentViewDetail,
      viewLoading: state => state.view.isLoading,
      currentViewFields: state => state.view.currentViewDetail['fields'],
    }),
    showLoading() {
      return this.loading || this.viewLoading || this.tableLoading
    },
    getModuleName() {
      return this.$route.meta.module || 'contact'
    },
    currentView() {
      return this.$route.params.viewname || null
    },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    page() {
      return this.$route.query.page || 1
    },
    viewName() {
      let viewList = {
        all: 'All Contact',
        tenant: 'Tenant Contact',
        vendor: 'Vendor Contact',
        employee: 'Employee Contact',
      }
      let title = this.currentView
        ? this.$getProperty(viewList, this.currentView, null)
        : null

      if (title) return title
      return 'Contact'
    },
  },

  watch: {
    currentViewFields() {
      this.tableLoading = true
      this.$nextTick(() => {
        this.$refs.tableList ? this.$refs.tableList.doLayout() : null
        this.tableLoading = false
      })
    },
    currentView: {
      handler: function(newVal, oldVal) {
        if (oldVal !== newVal && !isEmpty(newVal)) {
          this.loadRecords()
          this.loadRecordsCount()
        }
      },
      immediate: true,
    },
    filters() {
      this.loadRecords()
      this.loadRecordsCount()
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadRecords()
        this.loadRecordsCount()
      }
    },
    viewDetail() {
      let { sortFields = [] } = this.viewDetail || {}

      if (!isEmpty(sortFields)) {
        let { name } = this.$getProperty(sortFields[0], 'sortField', {})

        this.sortConfig = {
          orderType: sortFields[0].isAscending ? 'asc' : 'desc',
          orderBy: name ? name : '',
        }
      }
    },
  },

  methods: {
    refreshList() {
      this.loadRecords()
      this.loadRecordsCount()
    },
    loadRecords() {
      let params = {
        page: this.page,
        perPage: 50,
        filters: !isEmpty(this.filters)
          ? encodeURIComponent(this.filters)
          : null,
      }
      let url = `/v2/contacts/${this.currentView}`

      this.loading = true
      API.get(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error occured')
        } else {
          this.recordList = data.contacts
        }

        this.loading = false
      })
    },
    getViewManagerRoute() {
      let { moduleName, $route } = this
      let { query } = $route

      let route = findRouterForModuleInApp(moduleName, pageTypes.VIEW_MANAGER)
      if (route)
        this.$router.push({
          ...route,
          params: { moduleName },
          query: { ...query },
        })
    },
    loadRecordsCount() {
      let params = {
        filters: !isEmpty(this.filters)
          ? encodeURIComponent(this.filters)
          : null,
        criteriaIds: this.$route.query.criteriaIds,
        fetchCount: true,
      }
      let url = `/v2/contacts/${this.currentView}`

      API.get(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error occured')
        } else {
          this.listCount = data.recordCount
        }
      })
    },
    deleteRecord(id) {
      this.$dialog
        .confirm({
          title: this.$t('home.contact.delete_contact'),
          message: this.$t('home.contact.delete_contact_confirmation'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            let url = '/v2/contacts/delete'
            let param = { contactIds: [id] }

            API.post(url, param).then(({ error }) => {
              if (error) {
                this.$message.error(error.message || 'Error occured')
              } else {
                this.$message.success(
                  this.$t('home.contact.contact_delete_success')
                )
                this.refreshList()
              }
            })
          }
        })
    },
    addRecord() {
      this.editData = null
      this.editFormVisibility = true
    },
    editRecord(record) {
      this.editData = record
      this.editData.contactType =
        record.contactType > 0 ? String(record.contactType) : ''
      this.editFormVisibility = true
    },
    openRecordSummary(id) {
      this.$router.push({
        path: '/app/home/contact/' + this.currentView + '/summary/' + id,
        query: this.$route.query,
      })
    },
    updateSort(sorting) {
      this.$store
        .dispatch('view/savesorting', {
          viewName: this.currentView,
          orderBy: sorting.orderBy,
          orderType: sorting.orderType,
          moduleName: this.getModuleName,
        })
        .then(() => this.loadRecords())
    },
  },
}
</script>
