<template>
  <div>
    <spinner :show="loading" size="80" v-if="loading"></spinner>
    <div v-else>
      <el-row>
        <el-col :span="7">
          <div class="tenant-summary-detail-container">
            <div class="tenant-summary-scroll-layout">
              <div class="mL30 mR30 mT20">
                <div class="pointer" @click="back">
                  <div class="col-6 label-txt-blue f10 text-uppercase">
                    <i class="el-icon-back fw6 mR10"></i>
                    {{ $t('common._common.back_list') }}
                  </div>
                </div>

                <div
                  class="tenant-logo-img-block mT10 mB30"
                  v-if="tenant.logoUrl"
                >
                  <img v-bind:src="tenant.logoUrl" class="" />
                </div>

                <div class="tenant-name fwBold mT10">
                  {{ tenant.name }}
                </div>
                <div class="tenant-location-block mT10">
                  <span
                    ><img src="~assets/maps-blue.svg" style="height: 12px;"
                  /></span>
                  <span
                    class="tenant-address"
                    v-if="
                      tenant.space.spaceTypeVal === 'Building' &&
                        tenant.space &&
                        tenant.space.name
                    "
                    >{{ tenant.space.name }}</span
                  >
                </div>
                <ul class="tenant-count-list-block">
                  <li
                    class="tenant-count-list"
                    v-if="userLists && userLists.length"
                  >
                    <!-- <li class="tenant-count-list"> -->
                    <span style="padding-right: 10px;">
                      <img
                        src="~assets/user.svg"
                        style="height: 16px;width: 14px;"
                      />
                    </span>
                    <span class="tenant-count-txt">{{ userLists.length }}</span>
                    <span class="tenant-meter-count">
                      {{ userLists.length === 1 ? 'USER' : 'USERS' }}</span
                    >
                  </li>

                  <li
                    class="tenant-count-list"
                    v-if="enrgyMeters && enrgyMeters.length"
                  >
                    <span style="padding-right: 10px;">
                      <img
                        src="~assets/Shape.svg"
                        style="height: 20px;position: relative;top: 5px;"
                      />
                    </span>
                    <span class="tenant-count-txt">{{
                      enrgyMeters.length
                    }}</span>
                    <span class="tenant-meter-count"
                      >ENERGY
                      {{ enrgyMeters.length === 1 ? 'METER' : 'METERS' }}</span
                    >
                  </li>

                  <li
                    class="tenant-count-list"
                    v-if="waterMeters && waterMeters.length"
                  >
                    <span style="padding-right: 10px;">
                      <img
                        src="~assets/Shape.svg"
                        style="height: 20px;position: relative;top: 5px;"
                      />
                    </span>
                    <span class="tenant-count-txt">{{
                      waterMeters.length
                    }}</span>
                    <span class="tenant-meter-count"
                      >WATER
                      {{ waterMeters.length === 1 ? 'METER' : 'METERS' }}</span
                    >
                  </li>

                  <li
                    class="tenant-count-list"
                    v-if="naturalGas && naturalGas.length"
                  >
                    <span style="padding-right: 10px;">
                      <img
                        src="~assets/Shape.svg"
                        style="height: 20px;position: relative;top: 5px;"
                      />
                    </span>
                    <span class="tenant-count-txt">{{
                      naturalGas.length
                    }}</span>
                    <span class="tenant-meter-count">
                      {{
                        naturalGas.length === 1 ? 'NATURAL GAS' : 'NATURAL GAS'
                      }}</span
                    >
                  </li>

                  <li class="tenant-count-list" v-if="btu && btu.length">
                    <span style="padding-right: 10px;">
                      <img
                        src="~assets/Shape.svg"
                        style="height: 20px;position: relative;top: 5px;"
                      />
                    </span>
                    <span class="tenant-count-txt">{{ btu.length }}</span>
                    <span class="tenant-meter-count"
                      >BTU {{ btu.length === 1 ? 'BTU' : 'BTU' }}</span
                    >
                  </li>
                </ul>
                <p class="tenant-description mT40">
                  {{ tenant.description }}
                </p>
              </div>
            </div>
          </div>
        </el-col>

        <!-- <spinner :show="loading" size="80" v-if="userloading"></spinner>
     <el-col :span="17" v-else-if="userLists.length"> -->
        <spinner :show="loading" size="80"></spinner>
        <!-- <el-col :span="17" > -->
        <el-col>
          <div class="tenant-summary-card-container">
            <div class="label-txt-black3">
              USERS
            </div>
            <div class="tenant-user-card-container">
              <div class="tenant-user-wrap">
                <el-row>
                  <el-col
                    :span="4"
                    class="tenant-user-card text-center"
                    v-for="(user, i) in userLists"
                    :key="i"
                  >
                    <div class="mT30">
                      <user-avatar
                        size="xlg"
                        :user="user.orgUser"
                        :showPopover="false"
                        :name="false"
                      ></user-avatar>
                    </div>
                    <div
                      class="contactinfo-name summary-name"
                      v-if="user.orgUser && user.orgUser.name"
                    >
                      {{ user.orgUser.name }}
                    </div>
                    <div
                      class="contactinfo-email summary-email"
                      v-if="user.orgUser && user.orgUser.email"
                    >
                      {{ user.orgUser.email }}
                    </div>
                    <div
                      class="fc-black-13 mT10"
                      v-if="user.orgUser && user.orgUser.mobile"
                    >
                      {{ user.orgUser.mobile }}
                    </div>
                    <div class="portal-enable-section">
                      Portal Access
                      <el-switch
                        v-model="user.orgUser.portal_verified"
                        @change="toggleAttribute(user)"
                        class="Notification-toggle mL40"
                        active-color="#ef4f8f"
                        inactive-color="#e5e5e5"
                      ></el-switch>
                    </div>
                  </el-col>

                  <el-col :span="4" class="tenant-user-card">
                    <div class="tenant-addUser-block">
                      <i
                        class="el-icon-circle-plus-outline"
                        @click="showDialog = true"
                      ></i>
                      <div class="add-us">ADD USER</div>
                      <new-requester
                        v-if="showDialog"
                        :visibility.sync="showDialog"
                        @saved="addusers"
                        :source="'tenant'"
                      ></new-requester>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>

            <div class="label-txt-black3 mT60">
              ENERGY METERS
            </div>
            <div class="tenant-user-wrap">
              <div class="tenant-user-card-container">
                <el-row v-if="tenant.utilityAssets.length">
                  <el-col
                    :span="5"
                    class="tenant-user-card"
                    v-for="(utility, inx) in tenant.utilityAssets"
                    :key="inx"
                    v-if="utility.utilityEnum === 'ENERGY'"
                  >
                    <div class="tenant-text-center-asset">
                      {{ utility.asset.name }}
                    </div>
                    <div class="mL20 mT40">
                      <div class="reading">
                        {{ meterLists[utility.assetId] }}kwh
                      </div>
                      <div class="tenant-timeperdiod">
                        {{ $t('common.date_picker.this_month') }}
                      </div>
                    </div>
                    <div class="portal-enable-section">
                      Show In Portal
                      <el-switch
                        v-model="utility.showInPortal"
                        class="mL40"
                        @change="showInPortal(utility)"
                        active-color="#ef4f8f"
                        inactive-color="#e5e5e5"
                      ></el-switch>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>

            <div
              class="label-txt-black3 mT60"
              v-for="(utility, inx) in tenant.utilityAssets"
              :key="inx"
              v-if="utility.utilityEnum === 'WATER'"
            >
              WATER METERS
            </div>
            <div class="tenant-user-wrap">
              <div class="tenant-user-card-container">
                <el-row v-if="tenant.utilityAssets.length">
                  <el-col
                    :span="5"
                    class="tenant-user-card"
                    v-for="(utility, inx) in tenant.utilityAssets"
                    :key="inx"
                    v-if="utility.utilityEnum === 'WATER'"
                  >
                    <div class="tenant-text-center-asset">
                      {{ utility.asset.name }}
                    </div>
                    <div class="mL20 mT40">
                      <div class="reading">
                        {{ meterLists[utility.assetId] }}kwh
                      </div>
                      <div class="tenant-timeperdiod">
                        {{ $t('common.date_picker.this_month') }}
                      </div>
                    </div>
                    <div class="portal-enable-section">
                      Show In Portal
                      <el-switch
                        class="mL40"
                        active-color="#ef4f8f"
                        inactive-color="#e5e5e5"
                      ></el-switch>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>

            <div
              class="label-txt-black3 mT60"
              v-for="(utility, inx) in tenant.utilityAssets"
              :key="inx"
              v-if="utility.utilityEnum === 'NATURAL GAS'"
            >
              Natural Gas
            </div>
            <div class="tenant-user-wrap">
              <div class="tenant-user-card-container">
                <el-row v-if="tenant.utilityAssets.length">
                  <el-col
                    :span="5"
                    class="tenant-user-card"
                    v-for="(utility, inx) in tenant.utilityAssets"
                    :key="inx"
                    v-if="utility.utilityEnum === 'NATURAL GAS'"
                  >
                    <div class="tenant-text-center-asset">
                      {{ utility.asset.name }}
                    </div>
                    <div class="mL20 mT40">
                      <div class="reading">
                        {{ meterLists[utility.assetId] }}kwh
                      </div>
                      <div class="tenant-timeperdiod">
                        {{ $t('common.date_picker.this_month') }}
                      </div>
                    </div>
                    <div class="portal-enable-section">
                      Show In Portal
                      <el-switch
                        class="mL40"
                        active-color="#ef4f8f"
                        inactive-color="#e5e5e5"
                      ></el-switch>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>

            <div
              class="label-txt-black3 mT60"
              v-for="(utility, inx) in tenant.utilityAssets"
              :key="inx"
              v-if="utility.utilityEnum === 'BTU'"
            >
              BTU
            </div>
            <div class="tenant-user-wrap">
              <div class="tenant-user-card-container">
                <el-row v-if="tenant.utilityAssets.length">
                  <el-col
                    :span="5"
                    class="tenant-user-card"
                    v-for="(utility, inx) in tenant.utilityAssets"
                    :key="inx"
                    v-if="utility.utilityEnum === 'BTU'"
                  >
                    <div class="tenant-text-center-asset">
                      {{ utility.asset.name }}
                    </div>
                    <div class="mL20 mT40">
                      <div class="reading">
                        {{ meterLists[utility.assetId] }}kwh
                      </div>
                      <div class="tenant-timeperdiod">
                        {{ $t('common.date_picker.this_month') }}
                      </div>
                    </div>
                    <div class="portal-enable-section">
                      Portal Access
                      <el-switch
                        class="Notification-toggle tenant-show-in-portal"
                        active-color="#ef4f8f"
                        inactive-color="#e5e5e5"
                      ></el-switch>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import NewRequester from 'pages/setup/new/NewRequester'

export default {
  data() {
    return {
      tenant: null,
      loading: true,
      userloading: true,
      showDialog: false,
      newtemp: false,
      enrgyMeters: [],
      waterMeters: [],
      btu: [],
      naturalGas: [],
      user: {
        name: null,
        email: null,
        phone: null,
      },
      userList: [],
      userLists: [],
      meterLists: [],
      portal_verified: null,
    }
  },
  components: {
    UserAvatar,
    NewRequester,
  },
  watch: {
    $route: function() {
      this.loadTenantsDetails()
    },
  },
  computed: {},
  mounted() {
    this.loadTenantsDetails()
    this.loadMeterReadings()
  },
  methods: {
    prepareTenantInfo(data) {
      this.enrgyMeters = data.utilityAssets.filter(
        rw => rw.utilityEnum === 'ENERGY'
      )
      this.waterMeters = data.utilityAssets.filter(
        rw => rw.utilityEnum === 'WATER'
      )
      this.naturalgas = data.utilityAssets.filter(
        rw => rw.utilityEnum === 'NATURAL GAS'
      )
      this.btu = data.utilityAssets.filter(rw => rw.utilityEnum === 'BTU')
    },
    loadTenantsDetails(id) {
      let self = this
      self.loading = true
      self.$http
        .get('/tenant/' + this.$route.params.id)
        .then(function(response) {
          self.tenant = response.data ? response.data : null
          self.prepareTenantInfo(self.tenant)
          self.loading = false
          self.loadUsers()
        })
    },
    addusers(data) {
      let that = this
      let params = {
        id: parseInt(this.$route.params.id),
        user: { name: data.name, email: data.email, phone: data.phone },
      }
      if (data.portal_verified) {
        params.user.portal_verified = data.portal_verified
      }
      that.$http
        .post('/tenant/addTenantUser', params)
        .then(function(response) {
          that.userlist = response['data'].users
          that.loading = false
          that.loadUsers()
        })
        .catch(function(error) {
          that.loading = false
          console.log(error)
        })
    },
    loadUsers() {
      let that = this
      // that.$http.get('/tenant/getTenantUsers', params).then(function (response) {
      this.userloading = true
      that.$http
        .post('/tenant/getTenantUsers', { tenantId: this.$route.params.id })
        .then(function(response) {
          that.userLists = response['data'].tenantsUsers
          that.userloading = false
          that.loadMeterReadings()
        })
        .catch(function(error) {
          that.loading = false
          console.log(error)
        })
    },
    loadMeterReadings() {
      let that = this
      let assetId = []
      // assetId = tenant.utilityAssets.map(fn => fn.assetId)
      this.tenant.utilityAssets.forEach(element => {
        assetId.push(element.assetId)
      })
      that.$http
        .post('/tenant/getmeterreadings', { meterId: assetId })
        .then(function(response) {
          that.meterLists = response.data
          // that.meterLists.forEach(d => {
          //   console.log('__________', d)
          // })
          that.userloading = false
        })
    },
    toggleAttribute(user) {
      console.log('&&&&&&&&&', user)
      console.log('&&&&&&&&&', user.orgUser.ouid)
      let self = this
      let params = {
        user: {
          ouid: parseInt(user.orgUser.ouid),
          portal_verified: user.orgUser.portal_verified,
        },
      }
      self.$http
        .post('/tenant/updateportalaccess', params)
        .then(function(response) {})
        .then(function(response) {
          self.$message.success(' Portal access updated successfully')
          console.log('value updated ')
        })
    },
    showInPortal(utility) {
      let self = this
      let params = {
        tenantId: parseInt(this.$route.params.id),
        tenantMeterId: utility.assetId,
        show_In_Portal: utility.showInPortal,
      }
      console.log('*************', params)
      self.$http
        .post('/tenant/showinportal', params)
        .then(function(response) {})
        .then(function(response) {
          self.$message.success(' Portal access updated successfully')
          console.log('value updated ')
        })
    },
    back() {
      this.$router.push('/app/setup/tenantbilling/tenant')
    },
  },
}
</script>
<style>
/* .el-icon-circle-plus-outline {
    width: 24px !important;
    height: 24px !important;
} */
.summary-name {
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.2px;
  text-align: center;
  color: #333333;
  padding-top: 15px;
  padding-bottom: 5px;
}
.summary-email {
  font-size: 13px;
  color: #41a1d0;
  letter-spacing: 1.6px;
  text-transform: lowercase;
}
.add-us {
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.6px;
  color: #3ca875;
  position: relative;
  text-align: center;
  margin-top: 10px;
}
.tenant-text-center-asset {
  font-size: 16px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.25;
  letter-spacing: 0.6px;
  color: #000000;
  padding: 20px;
}
.tenant-portal {
  padding-top: 10px;
  padding-left: 20px;
  border-top: 1px solid #f3f5f9;
}
.tenant-show-in-portal .el-switch__core {
  left: 110px;
  position: relative;
  bottom: 15px;
}
.tenant-timeperdiod {
  font-size: 10px;
  letter-spacing: 0.8px;
  color: #999999;
}
.reading {
  line-height: 24px;
  font-size: 16px;
  letter-spacing: 0.6px;
  color: #000000;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  padding-right: 20px;
}
.portal-enable-section {
  position: absolute;
  bottom: 15px;
  right: 0;
  left: 0;
  border-top: solid 1px #f3f5f9;
  padding-top: 15px;
  font-size: 13px;
  letter-spacing: 0.4px;
  color: #666666;
  text-align: center;
}
.tenant-addUser-block .el-icon-circle-plus-outline {
  color: #58ca94;
  font-size: 32px;
  font-weight: 500;
}
.tenant-addUser-block {
  text-align: center;
  position: absolute;
  right: 0;
  left: 0;
  top: 40%;
  bottom: 50%;
}
</style>
