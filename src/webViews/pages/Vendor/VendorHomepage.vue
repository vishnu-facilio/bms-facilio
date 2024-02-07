<template>
  <div class="fc-vendor-homepage">
    <div class="fc-black-14 text-left">
      {{ $t('tenant.vendor.profile_info') }}
    </div>
    <el-card shadow="never" class="fc-profile-card mT10">
      <div class="flex-middle justify-content-center">
        <div>
          <i class="el-icon-user-solid pR3"></i>
          {{ $t('portal.webview.hi') }},
          {{ $getProperty($account, 'org.name') }}
        </div>
        <div
          class="fc-vendor-status"
          v-if="$getProperty($account, 'org.state')"
        >
          {{ $getProperty($account, 'org.state') }}
        </div>
      </div>
    </el-card>

    <div class="fc-black-14 text-left pT20">
      {{ $t('tenant.vendor.company_info') }}
    </div>

    <el-card shadow="never" class="fc-profile-card mT10">
      <div class="text-center">
        <avatar size="lg" :user="$portaluser" color="#3e2a8c"></avatar>
      </div>
      <div class="text-center fc-black3-18">
        {{ $getProperty(vendorDetailsData, 'vendor.name') }}
      </div>
      <div class="flex-middle justify-content-center mT10">
        <div
          class="fc-vendor-status"
          v-if="
            $getProperty(vendorDetailsData.vendor, 'moduleState.displayName')
          "
        >
          {{
            $getProperty(vendorDetailsData.vendor, 'moduleState.displayName')
          }}
        </div>
      </div>
      <div v-if="vendorDetailsData">
        <div class="fc-grey4-12">
          {{ $t('portal.webview.scope') }}
        </div>
        <div class="mT10">
          <div class="fc-black2-14 fw6">
            {{ $t('portal.webview.services') }}
          </div>
          <div v-for="(service, iex) in vendorDetailsData" :key="iex">
            <div
              class="fc-black-13 pT10"
              v-for="(serve, inx) in vendorDetailsData.services"
              :key="inx"
            >
              {{ serve.name }}
            </div>
          </div>
        </div>
        <div class="fc-grey8-11 pT20 f14 text-center bold">
          {{ $t('portal.webview.show_more') }}
        </div>
      </div>
    </el-card>
    <el-row :gutter="20" class="mT20">
      <el-col :span="12" @click="goToListInsurance">
        <el-card shadow="never" class="fc-profile-blue-card mT10">
          <div class="fc-white-28 text-center pB10">
            {{ totalInsuranceCount ? totalInsuranceCount : '0' }}
          </div>
          <div class="fc-white-14 text-center">
            {{ $t('portal.webview.insurance_pending') }}
          </div>
          <div class="pT20">
            <i class="el-icon-right fc-white-22"></i>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12" @click="goToListContact">
        <el-card shadow="never" class="fc-profile-blue-card mT10">
          <div class="fc-white-28 text-center pB10 bold">
            {{ contactTotal ? contactTotal : '0' }}
          </div>
          <div class="fc-white-14 text-center">
            {{ $t('portal.webview.admin_workers') }}
          </div>
          <div class="pT20">
            <i class="el-icon-right fc-white-22"></i>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" class="mT10">
      <el-col :span="12" @click="goToListInduction">
        <el-card shadow="never" class="fc-profile-blue-card mT10">
          <div class="fc-white-28 text-center pB10">
            {{ inductionTotal ? inductionTotal : '0' }}
          </div>
          <div class="fc-white-14 text-center">
            {{ $t('portal.webview.induction_pending') }}
          </div>
          <div class="pT20">
            <i class="el-icon-right fc-white-22"></i>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12" @click="goToListLicense">
        <el-card shadow="never" class="fc-profile-blue-card mT10">
          <div class="fc-white-28 text-center pB10 bold">
            {{ licenseTotal ? licenseTotal : '0' }}
          </div>
          <div class="fc-white-14 text-center">
            {{ $t('portal.webview.worker_license') }}
          </div>
          <div class="pT20">
            <i class="el-icon-right fc-white-22"></i>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" class="mT10" @click="goToListSwms">
      <el-col :span="12">
        <el-card shadow="never" class="fc-profile-blue-card mT10">
          <div class="fc-white-28 text-center pB10">
            {{ swmsTotal ? swmsTotal : '0' }}
          </div>
          <div class="fc-white-14 text-center">
            {{ $t('portal.webview.swms_uploaded') }}
          </div>
          <div class="pT20">
            <i class="el-icon-right fc-white-22"></i>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import Avatar from '@/Avatar'
import { pageTypes } from '@facilio/router'
import { emitEvent } from 'src/webViews/utils/mobileapps'
export default {
  data() {
    return {
      loading: true,
      inusranceList: [],
      totalInsuranceCount: null,
      swmsData: [],
      swmsTotal: null,
      inductionTotal: null,
      inductionData: [],
      licenseTotal: null,
      vendorContactLicenseData: [],
      vendorContactDataListData: [],
      contactTotal: null,
      vendorDetailsData: [],
    }
  },
  components: {
    Avatar,
  },
  created() {
    this.loadInsurance()
    this.loadWorkorderCretificated()
    this.loadSwms()
    this.loadInduction()
    this.loadContacts()
    this.loadProperityService()
  },
  methods: {
    async loadProperityService() {
      this.loading = true
      let params = {
        viewName: 'allservices',
        perPage: 10,
        withCount: true,
        includeParentFilter: this.includeParentFilter,
        filters: `{"vendor":{"operatorId":36,"value":["${this.$portaluser.peopleId}"]}}`,
      }
      let { list, error } = await API.fetchAll('custom_vendorservices', params)
      if (error) {
        let { message } = error
        this.$message.error(message || 'Error occured while Vendor data')
      } else {
        this.vendorDetailsData = list || []
      }
      this.loading = false
    },
    async loadInsurance() {
      this.loading = true
      let params = {
        viewName: 'allinsurance',
        perPage: 10,
        withCount: true,
        includeParentFilter: this.includeParentFilter,
      }
      let { list, error, meta } = await API.fetchAll('insurance', params)
      if (error) {
        let { message } = error
        this.$message.error(message || 'Error occured while fetching insurance')
      } else {
        this.inusranceList = list || []
        this.totalInsuranceCount = this.$getProperty(
          meta,
          'pagination.totalCount',
          null
        )
      }
      this.loading = false
    },
    async loadSwms() {
      this.loading = true
      let params = {
        viewName: 'all',
        perPage: 10,
        withCount: true,
        includeParentFilter: this.includeParentFilter,
      }

      let { list, error, meta } = await API.fetchAll('custom_swms', params)

      if (error) {
        let { message } = error
        this.$message.error(message || 'Error occured while SWMS data')
      } else {
        this.swmsData = list || []
        this.swmsTotal = this.$getProperty(meta, 'pagination.totalCount', null)
      }
      this.loading = false
    },
    async loadInduction() {
      this.loading = true
      let params = {
        viewName: 'allinductions',
        perPage: 10,
        withCount: true,
        includeParentFilter: this.includeParentFilter,
      }

      let { list, error, meta } = await API.fetchAll(
        'inductionResponse',
        params
      )

      if (error) {
        let { message } = error
        this.$message.error(message || 'Error occured while Induction data')
      } else {
        this.inductionData = list || []
        this.inductionTotal = this.$getProperty(
          meta,
          'pagination.totalCount',
          null
        )
      }
      this.loading = false
    },
    async loadWorkorderCretificated() {
      this.loading = true
      let params = {
        viewName: 'alllicensecertificates',
        perPage: 10,
        withCount: true,
        includeParentFilter: this.includeParentFilter,
      }
      let { list, error, meta } = await API.fetchAll(
        'custom_vendorcontactlicensecertificates',
        params
      )
      if (error) {
        let { message } = error
        this.$message.error(message || 'Error occured while Contact data')
      } else {
        this.vendorContactLicenseData = list || []
        this.licenseTotal = this.$getProperty(
          meta,
          'pagination.totalCount',
          null
        )
      }
      this.loading = false
    },
    async loadContacts() {
      this.loading = true
      let params = {
        viewName: 'active',
        perPage: 10,
        withCount: true,
        includeParentFilter: this.includeParentFilter,
      }
      let { list, error, meta } = await API.fetchAll('vendorcontact', params)
      if (error) {
        let { message } = error
        this.$message.error(message || 'Error occured while Contact data')
      } else {
        this.vendorContactDataListData = list || []
        this.contactTotal = this.$getProperty(
          meta,
          'pagination.totalCount',
          null
        )
      }
      this.loading = false
    },
    goToListInsurance() {
      let routeData = {
        pageType: pageTypes.LIST,
        moduleName: 'insurance',
        viewName: 'allinsurance',
      }
      emitEvent('navigate', routeData)
    },
    goToListContact() {
      let routeData = {
        pageType: pageTypes.LIST,
        moduleName: 'vendorcontact',
        viewName: 'active',
      }
      emitEvent('navigate', routeData)
    },
    goToListInduction() {
      let routeData = {
        pageType: pageTypes.LIST,
        moduleName: 'inductionResponse',
        viewName: 'allinductions',
      }
      emitEvent('navigate', routeData)
    },
    goToListLicense() {
      let routeData = {
        pageType: pageTypes.LIST,
        moduleName: 'custom_vendorcontactlicensecertificates',
        viewName: 'alllicensecertificates',
      }
      emitEvent('navigate', routeData)
    },
    goToListSwms() {
      let routeData = {
        pageType: pageTypes.LIST,
        moduleName: 'custom_swms',
        viewName: 'all',
      }
      emitEvent('navigate', routeData)
    },
  },
}
</script>
<style lang="scss" scoped>
.fc-vendor-homepage {
  width: 100%;
  height: 100%;
  padding: 20px;
  background: #f7f8f9;

  .fc-profile-card {
    border-radius: 10px;
    border: none;
    .fc-vendor-status {
      font-size: 9px;
      font-weight: normal;
      line-height: normal;
      letter-spacing: 0.8px;
      text-align: center;
      color: #fff;
      padding: 4px 15px 3px;
      border-radius: 75px;
      border: solid 1px #23b096;
      background-color: #23b096;
      margin-left: 10px;
    }
    .fc-profile-card {
      box-shadow: 0 2px 13px 0 rgba(230, 233, 236, 0.26);
      -webkit-box-shadow: 0 2px 13px 0 rgba(230, 233, 236, 0.26);
      -moz-box-shadow: 0 2px 13px 0 rgba(230, 233, 236, 0.26);
      border-radius: 10px;
    }
  }
  .el-avatar {
    background-color: #3e2a8c;
    font-size: 18px;
    font-weight: normal;
    line-height: normal;
    letter-spacing: normal;
    color: #fff;
  }
  .fc-profile-blue-card {
    height: 193px;
    border-radius: 10px;
    position: relative;
    background-color: #3e2a8c;
    .el-icon-right {
      position: absolute;
      bottom: 20px;
    }
  }
}
</style>
