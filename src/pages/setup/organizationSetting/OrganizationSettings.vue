<template>
  <div class="fc-email-template-page">
    <SetupHeader>
      <template #heading>
        {{ $t('common.header.organization') }}
      </template>
      <template #description>
        {{ $t('setup.list.list_profile_currency_tax') }}
      </template>
      <template #actions>
        <portal-target
          name="organization-action-buttons"
          class="portal-summary flex-middle"
        ></portal-target>
      </template>
      <template #tabs>
        <el-tabs class="org-setting-action-tab" v-model="currentTab">
          <el-tab-pane label="Profile" name="profile" lazy>
            <CompanyProfile v-if="currentTab === 'profile'"></CompanyProfile>
          </el-tab-pane>
          <el-tab-pane label="Currency" name="currency" lazy>
            <Currency v-if="currentTab === 'currency'"></Currency>
          </el-tab-pane>
          <el-tab-pane label="Tax" name="tax" lazy>
            <TaxList v-if="currentTab === 'tax'"> </TaxList>
          </el-tab-pane>
        </el-tabs>
      </template>
    </SetupHeader>
    <portal-target
      name="pagination-search-currency-btn"
      class="portal-summary flex-middle"
    ></portal-target>
  </div>
</template>
<script>
import SetupHeader from 'pages/setup/components/SetupHeaderTabs'
import TaxList from 'src/pages/setup/organizationSetting/tax/TaxList.vue'
import CompanyProfile from 'src/pages/setup/organizationSetting/profile/CompanyProfile.vue'
import Currency from 'src/pages/setup/organizationSetting/currency/Currency.vue'

export default {
  components: {
    TaxList,
    SetupHeader,
    CompanyProfile,
    Currency,
  },
  data() {
    return {
      currentTab: 'profile',
    }
  },
}
</script>
<style lang="scss">
.org-setting-action-tab {
  .el-tabs__active-bar {
    width: 56px;
    margin-left: 30px;
  }
  .el-tabs__nav {
    padding: 5px 30px;
  }

  .el-tabs__content {
    .el-tab-pane {
      background-color: #f7f8f9;
    }
  }
  .el-tabs__header {
    margin: 0px;
  }
}
</style>
