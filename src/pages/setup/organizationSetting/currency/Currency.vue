<template>
  <div>
    <div
      v-if="isLoading"
      class="flex-middle fc-empty-white fc-agent-empty-state fc-agent-empty-state-points"
    >
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div v-else class="height500 d-flex flex-direction-column overflow-hidden ">
      <div v-if="isDefault" class="margin-auto currency-empty-state m15">
        <inline-svg
          src="svgs/spacemanagement/ic-nodata"
          iconClass="text_center icon icon-xxxlg"
          class="svg_center"
        ></inline-svg>
        <span class="f16 fw6 letter-spacing0_3 mtb10">{{
          $t('setup.currency.no_currency')
        }}</span>
        <span class="f14 letter-spacing0_5">{{
          $t('setup.currency.description')
        }}</span>
        <div class="action-btn setting-page-btn mL20 mT20">
          <el-button
            type="primary"
            class="setup-el-btn"
            @click="addOrUpdateCurrency"
          >
            {{ $t('setup.currency.configure') }}
          </el-button>
        </div>
      </div>
      <CurrencyList
        v-else
        :currencyData="currencyData"
        :currencyCode="baseCode"
        @edit="addOrUpdateCurrency"
      ></CurrencyList>
      <CurrencyForm
        v-if="openCurrencyForm"
        :baseCode="baseCode"
        :record="selectedRecord"
        :isDefault="isDefault"
        @saved="refreshRecordsList"
        @close="openCurrencyForm = false"
      ></CurrencyForm>
    </div>
    <template>
      <portal to="organization-action-buttons">
        <div class="action-btn setting-page-btn mL20">
          <el-button
            type="primary"
            class="setup-el-btn"
            @click="addOrUpdateCurrency"
          >
            {{ $t('setup.currency.add_currency') }}
          </el-button>
        </div>
      </portal>
      <portal to="pagination-search-currency-btn">
        <div class="paginationAndSearch-currency">
          <f-search
            class="mR10"
            key="name"
            :remote="true"
            @search="quickSearch"
            @close="closeSearch"
          ></f-search>
          <template v-if="showPagination">
            <span class="separator mT-5">|</span>
            <Pagination
              :key="`pagination-${moduleName}`"
              :total="recordCount"
              :currentPage.sync="page"
              :currentPageCount="(currencyData || []).length"
              :perPage="perPage"
            ></Pagination>
          </template>
        </div>
      </portal>
    </template>
  </div>
</template>

<script>
import CurrencyForm from 'src/pages/setup/organizationSetting/currency/CurrencyForm.vue'
import CurrencyList from 'src/pages/setup/organizationSetting/currency/CurrencyList.vue'
import FSearch from '@/FSearch'
import Pagination from 'src/newapp/components/ListPagination'
import { isEmpty } from '@facilio/utils/validation'
import { CurrencyListModel } from 'src/pages/setup/organizationSetting/currency/CurrencyModel'
import { mapState } from 'vuex'

export default {
  components: { CurrencyForm, CurrencyList, Pagination, FSearch },
  data() {
    return {
      currencyData: [],
      isLoading: false,
      openCurrencyForm: false,
      selectedRecord: null,
      recordCount: null,
      searchText: null,
      page: 1,
      perPage: 10,
      supplements: null,
      error: null,
    }
  },
  computed: {
    ...mapState({
      account: state => state.account,
    }),
    moduleName() {
      return 'multicurrency'
    },
    isDefault() {
      let { error, currencyData, searchText } = this
      return isEmpty(error) && isEmpty(currencyData) && isEmpty(searchText)
    },
    showPagination() {
      return !isEmpty(this.currencyData)
    },
    baseCode() {
      let { currencyInfo } = this.account.data || {}
      return currencyInfo?.currencyCode
    },
  },
  watch: {
    page() {
      this.loadRecords()
    },
  },
  async created() {
    await this.refreshRecordsList()
  },
  methods: {
    quickSearch(searchText) {
      if (!isEmpty(searchText)) {
        this.page = 1
        this.searchText = searchText
        this.refreshRecordsList()
      }
    },
    closeSearch() {
      if (!isEmpty(this.searchText)) {
        this.searchText = null
        this.refreshRecordsList()
      }
    },
    async refreshRecordsList() {
      await this.loadRecords()
      await this.loadCount()
    },
    addOrUpdateCurrency(record) {
      this.selectedRecord = record?.id ? record : null
      this.openCurrencyForm = true
    },
    async loadRecords(force = true) {
      this.isLoading = true
      let { page, perPage, searchText } = this
      let params = {
        force,
        page,
        perPage,
        search: searchText,
      }
      try {
        this.currencyData = await CurrencyListModel.fetchAll(params)
      } catch (error) {
        this.error = this.$t('common._common.error_occured')
      }
      this.isLoading = false
    },
    async loadCount(force = true) {
      let { page, perPage, searchText } = this
      let params = {
        force,
        page,
        perPage,
        search: searchText,
      }
      this.recordCount = await CurrencyListModel.fetchCount(params)
    },
  },
}
</script>
<style scoped>
.currency-empty-state {
  display: flex;
  flex-direction: column;
  text-align: center;
  justify-content: center;
  background: #fff;
  width: 97%;
  height: 92%;
}
.paginationAndSearch-currency {
  position: absolute;
  top: 88px;
  right: 20px;
  z-index: 999;
  align-items: center;
  display: flex;
  height: 50px;
}
</style>
