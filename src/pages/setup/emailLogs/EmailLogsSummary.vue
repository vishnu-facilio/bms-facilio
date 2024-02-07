<template>
  <div class="emaillog-summary">
    <SetupHeader>
      <template #heading>
        <div class="flex-middle fc-setup-breadcrumb mB10">
          <div
            class="fc-setup-breadcrumb-inner pointer"
            @click="setupHomeRoute"
          >
            {{ $t('common.products.home') }}
          </div>
          <div class="fc-setup-breadcrumb-inner pL10 pR10">
            <i class="el-icon-arrow-right f14 fwBold"></i>
          </div>
          <div
            class="fc-setup-breadcrumb-inner pointer"
            @click="setupLogsListRoute"
          >
            {{ $t('emailLogs.email_logs') }}
          </div>
          <div class="fc-setup-breadcrumb-inner pL10 pR10">
            <i class="el-icon-arrow-right f14 fwBold"></i>
          </div>
          <div class="fc-breadcrumbBold-active">#{{ loggerId }}</div>
        </div>
        <p>{{ subject || '---' }}</p>
      </template>
      <template #tabs>
        <el-tabs class="fc-setup-list-tab" v-model="currentTab">
          <el-tab-pane label="Email Information" name="infoTab">
            <div v-if="currentTab === 'infoTab'" class="mT10 mB20">
              <EmailLogsInfoTab
                :loggerId="loggerId"
                @emailLogFetched="setSubject"
              />
            </div>
          </el-tab-pane>
          <el-tab-pane
            :label="$t('emailLogs.email_dialog.recipient_list')"
            name="recipientTab"
          >
            <div class="mT10 mB20">
              <EmailLogsTabList
                v-if="isRecipientTab"
                :loggerId="loggerId"
                :recordsPerPage="recordsPerPage"
                :currentPage="currentPage"
                :searchText="searchTextProp"
                status="all"
                @paginationFetch="count => (totalRecords = count)"
              />
            </div>
          </el-tab-pane>
          <el-tab-pane
            :label="$t('emailLogs.email_dialog.delivered_list')"
            name="deliveredTab"
          >
            <div class="mT10 mB20">
              <EmailLogsTabList
                v-if="isDeliveredTab"
                :loggerId="loggerId"
                :recordsPerPage="recordsPerPage"
                :currentPage="currentPage"
                :searchText="searchTextProp"
                status="delivered"
                @paginationFetch="count => (totalRecords = count)"
              />
            </div>
          </el-tab-pane>
          <el-tab-pane
            :label="$t('emailLogs.email_dialog.bounced_list')"
            name="bouncedTab"
          >
            <div class="mT10 mB20">
              <EmailLogsTabList
                v-if="isBouncedTab"
                :loggerId="loggerId"
                :recordsPerPage="recordsPerPage"
                :currentPage="currentPage"
                :searchText="searchTextProp"
                status="bounced"
                @paginationFetch="count => (totalRecords = count)"
              />
            </div>
          </el-tab-pane>
          <el-tab-pane v-if="canShowPagination">
            <span slot="label" class="pagination-container">
              <div
                class="self-center cursor-pointer"
                style="width:130px"
                @click="prventTabClick"
              >
                <Pagination
                  :total="totalRecords"
                  :perPage="recordsPerPage"
                  class="nowrap"
                  ref="f-page"
                  :pageNo="currentPage"
                  @onPageChanged="setPage"
                >
                </Pagination>
              </div>
            </span>
          </el-tab-pane>
          <el-tab-pane v-if="canShowPagination">
            <span slot="label" @click="prventTabClick">
              <div class="seperator">|</div>
            </span>
          </el-tab-pane>
          <el-tab-pane v-if="canShowSearch">
            <span slot="label" class="search-container" @click="prventTabClick">
              <div>
                <div
                  v-if="!showSearchInput"
                  class="fc-portal-filter-border"
                  v-tippy="{
                    arrow: true,
                    arrowType: 'round',
                    animation: 'fade',
                  }"
                  :content="$t('common._common.search')"
                  @click="handleSearchClick($event)"
                >
                  <i class="el-icon-search fc-black-14 fwBold"></i>
                </div>
                <div class="relative" v-if="showSearchInput">
                  <input
                    class="fc-log-search"
                    :placeholder="$t('emailLogs.email_dialog.search_email')"
                    v-model="searchText"
                    @keyup.enter="updateSearchText"
                  />
                  <i
                    class="el-icon-close fwBold fc-search-close"
                    @click="closeSearchInput($event)"
                  ></i>
                </div>
              </div>
            </span>
          </el-tab-pane>
        </el-tabs>
      </template>
    </SetupHeader>
  </div>
</template>

<script>
import SetupHeader from 'pages/setup/components/SetupHeaderTabs'
import EmailLogsTabList from 'pages/setup/emailLogs/EmailLogsTabList'
import EmailLogsInfoTab from 'pages/setup/emailLogs/EmailLogsInfoTab'
import Pagination from 'src/components/list/FPagination'
import { isEmpty } from '@facilio/utils/validation'
export default {
  name: 'EmailLogsSummary',
  data() {
    return {
      currentTab: 'infoTab',
      subject: '',
      totalRecords: 0,
      recordsPerPage: 50,
      currentPage: 1,
      showSearchInput: false,
      searchText: '',
      searchTextProp: '',
    }
  },
  components: { SetupHeader, Pagination, EmailLogsTabList, EmailLogsInfoTab },
  computed: {
    canShowSearch() {
      let { currentTab, totalRecords, searchText } = this
      return currentTab !== 'infoTab' && (totalRecords || searchText)
    },
    canShowPagination() {
      let { currentTab, totalRecords } = this
      return currentTab !== 'infoTab' && totalRecords
    },
    loggerId() {
      let { $route: route } = this
      let { params } = route
      let { loggerId } = params
      if (!isEmpty(loggerId)) {
        return loggerId
      }
      return -1
    },
    isRecipientTab() {
      let { currentTab } = this
      if (!isEmpty(currentTab)) {
        return currentTab === 'recipientTab'
      }
      return false
    },
    isDeliveredTab() {
      let { currentTab } = this
      if (!isEmpty(currentTab)) {
        return currentTab === 'deliveredTab'
      }
      return false
    },
    isBouncedTab() {
      let { currentTab } = this
      if (!isEmpty(currentTab)) {
        return currentTab === 'bouncedTab'
      }
      return false
    },
  },
  watch: {
    currentTab(newVal, oldVal) {
      if (!_.isEqual(newVal, oldVal)) {
        this.currentPage = 1
        this.showSearchInput = false
        this.totalRecords = 0
        this.searchText = ''
        this.searchTextProp = ''
      }
    },
  },
  methods: {
    setSubject(sub) {
      this.subject = sub
    },
    prventTabClick(e) {
      e.stopPropagation()
    },
    handleSearchClick(e) {
      e.stopPropagation()
      this.showSearchInput = true
    },
    closeSearchInput(e) {
      e.stopPropagation()
      this.showSearchInput = false
      this.searchText = ''
      this.searchTextProp = ''
    },
    setPage(page) {
      this.currentPage = page
    },
    updateSearchText() {
      this.searchTextProp = this.searchText
    },
    setupHomeRoute() {
      this.$router.replace({ name: 'setup' })
    },
    setupLogsListRoute() {
      this.$router.replace({ name: 'emailLogsList' })
    },
  },
}
</script>

<style lang="scss" scoped>
.emaillog-summary {
  height: 100%;
  .seperator {
    font-size: 18px;
    color: #b2b3b5;
  }
  .fc-log-search {
    height: 35px;
    width: 250px;
    line-height: 40px;
    padding-right: 30px;
    border-radius: 3px;
    background-color: #ffffff;
    border: solid 1px #d0d9e2;
    font-size: 16px;
    font-weight: 500;
    letter-spacing: 0.4px;
    color: #333333;
    font-size: 16px;
    padding-left: 15px !important;
    transition: 0.4s all;
  }
  .fc-search-close {
    position: absolute;
    right: 10px;
    top: 14px;
    background: #fff;
    cursor: pointer;
    font-size: 18px;
  }
  .pagination-container {
    display: inline-block;
    width: 130px;
  }
  .search-container {
    display: inline-block;
    height: 100%;
  }
}
</style>

<style lang="scss">
.emaillog-summary {
  .fc-v1-setup-header {
    border-bottom: 0;
  }
  .fc-header-border-bottom {
    padding-bottom: 0px;
  }
  .el-tabs__nav {
    width: 100%;
    padding-bottom: 5px;
    #tab-4,
    #tab-5,
    #tab-6 {
      padding: 0px;
      float: right;
      margin-right: 15px;
      &:hover {
        color: #324056;
      }
    }
    #tab-6 {
      margin-top: 2px;
    }
  }
  .el-icon-close:hover {
    background: #fff;
    color: #324056;
  }
}
</style>
