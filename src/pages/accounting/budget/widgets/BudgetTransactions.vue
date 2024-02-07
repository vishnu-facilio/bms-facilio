<!-- eslint-disable-next-line vue/valid-template-root -->

<template>
  <div>
    <portal :to="sectionKey + '-title-section'" slim>
      <el-row
        :gutter="20"
        class="Rectangle-Copy-4"
        style="margin-left: 0px; margin-right: 0px;margin-top: 15px;"
      >
        <el-col :span="6" class="width40">
          <Lookup
            ref="cA"
            v-model="chartOfAccountIds"
            :field="chartOfAccountField"
            :hideLookupIcon="true"
          ></Lookup>
        </el-col>
        <el-col :span="6">
          <FDatePicker
            v-model="transactionDate"
            type="month"
            value-format="timestamp"
            placeholder="Select Month"
            class=" date-editor fc-input-full-border-select2 width100 "
          >
          </FDatePicker>
        </el-col>
        <el-col
          :span="12"
          class="section-container flex-container justify-content-end flex-direction-row "
        >
          <pagination
            :currentPage.sync="page"
            :total="listCount"
            :perPage="20"
            class="pL15 fc-black-small-txt-12"
          ></pagination>
          <el-tooltip
            effect="dark"
            :content="$t('common._common.sort')"
            placement="right"
          >
            <transaction-sort
              v-if="!$validation.isEmpty(sortConfigLists)"
              :key="moduleName + '-sort'"
              :config="sortConfig"
              :sortList="sortConfigLists"
              @onchange="updateSort"
              :currentViewDetail="viewDetail"
              :currentMetaInfo="metaInfo"
            ></transaction-sort>
          </el-tooltip>
        </el-col>
      </el-row>
    </portal>

    <div class=" common-content-container ">
      <div class="cm-list-container">
        <!-- table start -->
        <div v-if="showLoading" class="flex-middle cm-empty-bg-white">
          <spinner :show="showLoading" size="80"></spinner>
        </div>
        <div
          v-else-if="$validation.isEmpty(transactions)"
          class="height100vh cm-empty-bg-white flex-middle justify-content-center flex-direction-column"
        >
          <inline-svg
            src="svgs/emptystate/purchaseOrder"
            iconClass="icon text-top icon-xxxxlg"
          ></inline-svg>
          <div class="nowo-label">
            {{ $t('common._common.no_transaction_available') }}
          </div>
        </div>

        <CommonList
          v-else
          :viewDetail="viewDetail"
          :records="transactions"
          :moduleName="moduleName"
          :columnConfig="columnConfig"
          :slotList="slotList"
          :hideListSelect="true"
        >
          <template #[slotList[0].name]="{record}">
            <div class="d-flex">
              <div class="fc-id">
                {{ '#' + record.id }}
              </div>
            </div>
          </template>
          <template #[slotList[1].criteria]="{record}">
            <div class="d-flex  fc-id">
              {{ `#${record.transactionSourceRecordId || '---'}` }}
            </div>
          </template>
          <template #[slotList[2].name]="{record}">
            <div class="d-flex  fc-id">
              {{ `#${record.ruleId || '---'}` }}
            </div>
          </template>
        </CommonList>
      </div>
    </div>
    <!-- table end -->
  </div>
</template>

<script>
import Spinner from '@/Spinner'
import { isEmpty, isArray } from '@facilio/utils/validation'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import { Lookup } from '@facilio/ui/forms'
import { API } from '@facilio/api'
import CommonList from 'newapp/list/CommonList'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import TransactionSort from 'src/pages/accounting/budget/widgets/TransactionSort.vue'

const chartOfAccountField = {
  isDataLoading: true,
  options: [],
  lookupModuleName: 'chartofaccount',
  field: {
    lookupModule: {
      name: 'chartofaccount',
      displayName: 'Chartofaccount',
    },
  },
  placeHolderText: 'Chart of Account',
  multiple: true,
}

export default {
  components: {
    Spinner,
    Pagination,
    CommonList,
    Lookup,
    FDatePicker,
    TransactionSort,
  },
  props: ['details', 'sectionKey', 'widget'],
  data() {
    return {
      chartOfAccountIds: [],
      chartOfAccountField,
      transactionDate: [],
      loading: false,
      viewLoading: false,
      sortConfig: {
        orderBy: 'id',
        orderType: 'desc',
      },
      sortConfigLists: ['account'],
      columnConfig: {
        fixedColumns: ['id'],
        availableColumns: [],
        showLookupColumns: true,
        lookupToShow: [],
      },
      listCount: null,
      transactions: [],
      viewDetail: null,
      metaInfo: {},
      perPage: 20,
      page: 1,
    }
  },
  computed: {
    moduleName() {
      return 'transaction'
    },
    moduleDisplayName() {
      return 'Transaction'
    },
    currentView() {
      return 'all'
    },
    showLoading() {
      return this.loading || this.viewLoading
    },

    slotList() {
      return [
        {
          name: 'id',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 230,
            label: 'TRANSACTION ID',
            fixed: 'left',
          },
        },
        {
          criteria: JSON.stringify({ name: 'transactionSourceRecordId' }),
        },
        {
          name: 'ruleid',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 200,
            label: 'RULE ID',
          },
        },
      ]
    },
    hasTransactionResource() {
      let { details } = this
      let { focalPointType } = details || {}

      return [2, 3].includes(focalPointType)
    },
    transactionResourceId() {
      let { details } = this
      let { focalPointResource } = details || {}
      let { id } = focalPointResource || {}

      return id
    },
    cAIdsInBudget() {
      let { details } = this
      let { budgetAmountList } = details || {}

      return (budgetAmountList || [])
        .map(budgetAmt => {
          let { account } = budgetAmt || {}
          let { id } = account || {}
          return !isEmpty(id) ? id : null
        })
        .filter(ba => !isEmpty(ba))
    },
  },
  watch: {
    page(newVal) {
      if (newVal) this.loadTransactions()
    },
    chartOfAccountIds() {
      this.page = 1
      this.loadTransactions(true)
    },
    transactionDate() {
      this.page = 1
      this.loadTransactions(true)
    },
  },
  async created() {
    this.loading = true
    this.constructCAFilters()
    this.getModuleMeta()
    await this.loadViewDetail()
    await this.loadTransactions()
  },
  methods: {
    updateSortConfig() {
      let { viewDetail } = this
      if (!isEmpty(viewDetail.sortFields) && isArray(viewDetail.sortFields)) {
        this.sortConfig = {
          orderType: this.$getProperty(
            viewDetail,
            'sortFields.0.isAscending',
            false
          )
            ? 'asc'
            : 'desc',
          orderBy: this.$getProperty(
            viewDetail,
            'sortFields.0.sortField.name',
            'id'
          ),
        }
      }
    },
    updateSort(sorting) {
      let { moduleName } = this
      let sortObj = {
        moduleName,
        viewName: 'all',
        orderBy: sorting.orderBy,
        orderType: sorting.orderType,
        skipDispatch: true,
      }

      this.$store
        .dispatch('view/savesorting', sortObj)
        .then(() => this.loadTransactions(true))
    },

    constructCAFilters() {
      let { details } = this
      let { budgetAmountList } = details || {}
      let options = (budgetAmountList || [])
        .map(budgetAmt => {
          let { account } = budgetAmt || {}
          let { id, name } = account || {}
          return !isEmpty(id) ? { label: name, value: id } : null
        })
        .filter(ba => !isEmpty(ba))

      this.$set(this.chartOfAccountField, 'options', options)
      this.$set(this.chartOfAccountField, 'isDataLoading', false)
      this.$nextTick(() => {
        this.$refs['cA'].localSearch = true
      })
    },
    async loadViewDetail() {
      this.viewLoading = true

      let moduleName = 'transaction'
      let url = `/v2/views/all?moduleName=${moduleName}`
      let { error, data } = await API.get(url)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.viewDetail = data.viewDetail || {}
      }
      this.viewLoading = false
      this.updateSortConfig()
    },

    async loadTransactions(force = false) {
      let config = force ? { force } : {}
      let filters = {}

      if (!isEmpty(this.chartOfAccountIds)) {
        let value = this.chartOfAccountIds.map(id => `${id}`)
        filters.account = { operatorId: 36, value }
      } else if (!isEmpty(this.cAIdsInBudget)) {
        let value = this.cAIdsInBudget.map(id => `${id}`)
        filters.account = { operatorId: 36, value }
      }
      if (this.hasTransactionResource) {
        if (this.transactionResourceId) {
          filters.transactionResource = {
            operatorId: 36,
            value: [`${this.transactionResourceId}`],
          }
        }
      } else {
        filters.transactionResource = { operatorId: 1 }
      }
      if (!isEmpty(this.transactionDate) && !isNaN(this.transactionDate)) {
        let start = this.$helpers
          .getOrgMoment(this.transactionDate)
          .startOf('month')
          .valueOf()
        let end =
          this.$helpers
            .getOrgMoment(this.transactionDate)
            .endOf('month')
            .valueOf() - 1

        filters.transactionDate = {
          operatorId: 20,
          value: [`${start}`, `${end}`],
        }
      }

      let queryParam = {
        viewName: 'all',
        page: this.page,
        perPage: this.perPage,
        withCount: true,
        filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
        includeParentFilter: !isEmpty(filters),
        moduleName: 'transaction',
      }

      this.loading = true

      let { list, meta, error } = await API.fetchAll(
        this.moduleName,
        queryParam,
        config
      )

      if (error) {
        let { message } = error
        this.$message.error(message || this.$t('common._common.error_occured'))
      } else {
        this.listCount = this.$getProperty(meta, 'pagination.totalCount', null)
        this.transactions = list || []
      }
      this.loading = false
    },
    async getModuleMeta() {
      let moduleName = 'transaction'
      let { data, error } = await API.get(
        `/module/meta?moduleName=${moduleName}`
      )

      if (!error) {
        let { meta } = data || {}
        let { fields } = meta || {}

        this.sortConfigLists = (fields || []).map(f => f.name)
        this.metaInfo = { fields }
      }
    },
  },
}
</script>

<style scoped>
.Rectangle-Copy-4 {
  width: 95vmax;
  height: 96px;
  padding: 24px 64px 24px 24px;
  background-color: #fff;
  display: flex;
  align-items: center;
}
</style>

<style lang="scss">
.common-content-container {
  .scrollable-y {
    overflow-y: hidden;
  }
}
.cm-list-container {
  margin-top: 10px;
  border-width: 0px !important;
  border-style: solid;
  padding: 0px 10px 70px 10px;
}
.cm-list-container {
  .el-table td {
    padding: 10px 20px;
  }
  .el-table th.is-leaf {
    padding: 15px 20px;
  }
  .el-table th > .cell {
    font-size: 11px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    color: #333;
    white-space: nowrap;
    padding-left: 0;
    padding-right: 0;
  }
  .hover-actions {
    visibility: hidden;
  }
  .el-table__body tr.hover-row > td .hover-actions {
    visibility: visible;
  }
  .el-table .el-table__cell {
    text-align: left;
  }
  .el-table {
    overflow-y: hidden;
  }
  .table-border {
    border: none;
  }
  .scrollable-y {
    overflow-y: hidden;
  }
}
</style>
