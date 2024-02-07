<template>
  <div class="item-summary-table">
    <div>
      <el-select
        filterable
        clearable
        :placeholder="$t('common.header.filter_by_storeroom')"
        v-model="selectedToolid"
        @change="changeTool(selectedToolid)"
        class="fc-input-full-border-select2 width200px mT20"
      >
        <el-option
          v-for="(tool, index) in tools"
          :key="index"
          :label="tool.storeRoom.name"
          :value="tool.id"
        ></el-option>
      </el-select>
      <el-table
        v-loading="loading"
        :data="toolTransactionsList"
        :empty-text="$t('common.products.no_transaction_yet')"
        :default-sort="{ prop: 'sysCreatedTime', order: 'descending' }"
        class="inventory-inner-table"
        height="200px"
      >
        <el-table-column
          prop="sysCreatedTime"
          sortable
          :formatter="getDateTime"
          :label="$t('common.products.transaction_time')"
          min-width="150"
        ></el-table-column>
        <el-table-column
          prop="toolTransactionsList"
          :formatter="getStoreRoomName"
          sortable
          :label="$t('common.products.storeroom_name')"
          min-width="150"
        ></el-table-column>
        <el-table-column
          prop="transactionStateEnum"
          :label="$t('common._common._type')"
          min-width="100"
        ></el-table-column>
        <el-table-column
          prop="quantity"
          :formatter="getQuantity"
          sortable
          :label="$t('common._common._quantity')"
          min-width="100"
          class-name="right-align-items"
        ></el-table-column>
        <el-table-column min-width="150" class-name="center-align-element">
          <template v-slot="toolTransactionVal">
            <el-popover placement="top" width="350" trigger="hover">
              <div class="pL20 pR20">
                <div class="transaction-info">
                  {{ $t('common._common.initiated_by') }}
                </div>
                <div class="fc-black-14 text-left pB15 pT5">
                  {{ getUserName(toolTransactionVal.row) }}
                </div>
                <div class="transaction-info">
                  {{ $t('common.wo_report._description') }}
                </div>
                <div class="ffc-black-14 text-left pT5">
                  <InventoryTransactionDescription
                    :transaction="toolTransactionVal.row"
                  >
                  </InventoryTransactionDescription>
                </div>
              </div>
              <InlineSvg
                src="svgs/info-2"
                iconClass="icon icon-sm mR10"
                slot="reference"
              ></InlineSvg>
            </el-popover>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import InventoryTransactionDescription from '../component/InventoryTransactionDescription'
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'
import { getFilteredToolList } from 'pages/Inventory/InventoryUtil'
export default {
  components: {
    InventoryTransactionDescription,
  },
  props: {
    toolTypeId: {
      type: Number,
    },
    refreshList: {
      type: Boolean,
    },
    toolType: {},
  },
  data() {
    return {
      toolTransactionsList: [],
      selectedTool: null,
      selectedToolid: null,
      loading: false,
      fetchingMore: false,
      tools: null,
    }
  },
  watch: {
    refreshList() {
      this.loadTool()
      this.$emit('update:refreshList', false)
    },
  },
  mounted() {
    this.loadTool()
  },
  computed: {
    ...mapGetters(['getUser']),
  },
  methods: {
    getStoreRoomName(val) {
      let storeRoomName = this.$getProperty(val, 'storeRoom.name', '---')
      return storeRoomName
    },
    getQuantity(val) {
      let quantity = this.$getProperty(val, 'quantity', '---')
      return quantity
    },
    async loadToolTransactions() {
      this.loading = true
      if (this.selectedToolid) {
        let params = {
          filters: JSON.stringify({
            tool: {
              operatorId: 5,
              value: [this.selectedToolid + ''],
            },
          }),
        }

        let { list, error } = await API.fetchAll('toolTransactions', params)
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.toolTransactionsList = list || {}
          this.loading = false
        }
      } else {
        let params = {
          filters: JSON.stringify({
            toolType: {
              operatorId: 5,
              value: [this.toolType.id + ''],
            },
          }),
        }

        let { list, error } = await API.fetchAll('toolTransactions', params)
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.toolTransactionsList = list || {}
          this.loading = false
        }
      }
    },
    getDateTime(val) {
      let value = val.sysCreatedTime
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value)
    },
    async loadTool() {
      let { toolType } = this
      let { id } = toolType || {}
      this.loading = true
      if (id) {
        let filters = {
          toolType: {
            operatorId: 36,
            value: [id + ''],
          },
        }
        let tools = await getFilteredToolList(filters)
        if (!isEmpty(tools)) {
          this.tools = tools
          this.loadToolTransactions()
        }
      }
      this.loading = false
    },
    changeTool(toolid) {
      if (toolid) {
        this.selectedTool = this.tools.find(it => it.id === toolid)
      }
      this.loadToolTransactions()
    },
    getUserName(userData) {
      return !isEmpty(userData.sysCreatedBy)
        ? this.getUser(userData.sysCreatedBy.id).name
        : '---'
    },
  },
}
</script>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
</style>
