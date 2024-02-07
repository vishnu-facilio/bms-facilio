<template>
  <div>
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Tenants</div>
        <div class="heading-description">
          List of all the tenants occupying this building
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button type="primary" class="setup-el-btn" @click="addTenant"
          >Add Tenant</el-button
        >
        <addtenant
          v-if="newtemp"
          :visibility.sync="newtemp"
          :tenant="selectedTenant"
          @saved="loadTenants"
          :userlist="userlist"
        ></addtenant>
      </div>
    </div>
    <div class="row NewSetting-Rlayout">
      <div
        class="col-lg-4 col-md-4"
        v-for="(tenant, index) in tenants"
        :key="index"
      >
        <div class="tenant-card">
          <div class="tenant-logo-img-block" v-if="tenant.logoUrl">
            <img v-bind:src="tenant.logoUrl" class="" />
          </div>
          <div class="tenant-name">
            {{ tenant.name }}
          </div>
          <div class="tenant-location-block">
            <span
              ><img src="~assets/maps-blue.svg" style="height: 10px;"
            /></span>
            <span
              class="tenant-address"
              v-if="
                tenant.space &&
                  tenant.space.name &&
                  tenant.space.spaceTypeVal === 'Building'
              "
              >{{ tenant.space.name }}</span
            >
            <!-- <span class="tenant-address" v-else-if="tenant.spaceTypeVal === 'Site'">Block A, Floor 1</span>
                 <span class="tenant-address" v-else-if="tenant.spaceTypeVal === 'Floor'">Block A, Floor 1</span> -->
          </div>
          <div class="contactinfo">
            <div v-if="tenant.contactInfo">
              <!-- <div class="fc-avatar fc-avatar-md">
                   {{tenant.contactInfo.avatarUrl}}
                  </div> -->
              <user-avatar
                size="lg"
                :user="tenant.contactInfo"
                :showPopover="false"
                :name="false"
              ></user-avatar>
              <div
                class="contactinfo-name"
                v-if="tenant.contactInfo && tenant.contactInfo.name"
              >
                {{ tenant.contactInfo.name }}
              </div>

              <div
                class="contactinfo-email"
                v-if="tenant.contactInfo && tenant.contactInfo.email"
              >
                {{ tenant.contactInfo.email }}
              </div>
            </div>
          </div>
          <div>
            <td>
              <div class="text-left actions flRight">
                <i
                  class="el-icon-edit pointer tenant-editicon"
                  style="position: absolute; top: 20px; right:60px; display:none;color: #319aa8;"
                  @click="editTenant(tenant)"
                ></i>
                <edittenant
                  v-if="isEdit"
                  :visibility.sync="isEdit"
                  :tenant="selectedTenant"
                  @saved="loadTenants"
                  :userlist="userlist"
                ></edittenant>
                &nbsp;&nbsp;
                <i
                  class="el-icon-delete pointer tenant-icon"
                  style="position: absolute; top: 20px;right: 40px; display:none;color: #de7272;"
                  @click="deleteTenant(tenant.id, index)"
                ></i>
                <i
                  v-if="showsummary"
                  class="fa fa-external-link pointer tenant-icon"
                  style="position: absolute; top: 20px;right: 15px; display:none;color: #2f2e49;"
                  @click="openTenantSummary(tenant.id, index)"
                ></i>
              </div>
            </td>
            <td>
              <!-- <div>
                <i class="fa fa-external-link" aria-hidden="true"  @click="tenantSummary(tenant)"></i>
             </div> -->
            </td>
          </div>
        </div>
      </div>
    </div>
    <div class="row NewSetting-Rlayout" v-if="loading">
      <div class="col-lg-12 col-md-12 text-center">
        <spinner :show="loading" size="80"></spinner>
      </div>
    </div>
    <div class="row NewSetting-Rlayout" v-else-if="!tenants.length">
      <div class="col-lg-12 col-md-12 text-center">
        No tenants created yet.
      </div>
    </div>
    <!-- <tbody v-if="loading">
          <tr class="tablerow">
            <td colspan="100%" class="text-center">

            </td>
          </tr>
        </tbody>
        <tbody v-else-if="!tenants.length">
          <tr class="tablerow">
            <td colspan="100%" class="text-center">
              No tenants created yet.
            </td>
          </tr>
        </tbody> -->
    <f-dialog
      :visible.sync="showBillDialog"
      :width="'38%'"
      title="Generate Bill"
      @save="generateBill"
      confirmTitle="OK"
    >
      <div slot="content">
        <div>Rate Card</div>
        <el-select v-model="model.rateCardId">
          <el-option
            v-for="ratecard in ratecards"
            :key="ratecard.id"
            :label="ratecard.name"
            :value="ratecard.id"
          ></el-option>
        </el-select>
        <div class="pT20 pB10">Select Period</div>
        <!-- <date-picker></date-picker> -->
        <el-date-picker
          class="pB10"
          style="width: 85%; margin-bottom: 15px;"
          :picker-options="dateOptions"
          v-model="date"
          type="daterange"
          range-separator="To"
          start-placeholder="Start date"
          end-placeholder="End date"
        >
        </el-date-picker>
      </div>
    </f-dialog>
    <iframe v-if="billUrl" :src="billUrl" style="display: none;"></iframe>
  </div>
</template>
<script>
import Addtenant from 'pages/setup/bill/Addtenant'
import Edittenant from 'pages/setup/bill/Edittenant'
import FDialog from '@/FDialogNew'
import UserAvatar from '@/avatar/User'

export default {
  data() {
    return {
      tenants: [],
      userlist: [],
      loading: true,
      isEdit: false,
      newtemp: false,
      selectedTenant: null,
      showBillDialog: false,
      showsummary: false,
      ratecards: [],
      date: [
        new Date(this.$helpers.getDateRange('month', 1)[0]),
        new Date(this.$helpers.getDateRange('month', 1)[1]),
      ],
      model: {
        rateCardId: null,
        tenantId: null,
        startTime: null,
        endTime: null,
        convertToPdf: true,
      },
      billUrl: null,
      dateOptions: {
        disabledDate(time) {
          return time.getTime() > new Date().getTime()
        },
      },
    }
  },
  components: {
    Addtenant,
    Edittenant,
    FDialog,
    UserAvatar,
  },
  computed: {},
  mounted() {
    this.loadRatecards()
    this.loadData()
  },
  methods: {
    addTenant() {
      this.newtemp = true
      this.selectedTenant = null
    },
    loadData() {
      let that = this
      that.$http
        .get('/setup/portalusers')
        .then(function(response) {
          that.userlist = response['data'].users
          that.loadTenants()
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    openTenantSummary(id) {
      if (id) {
        this.$router.push('/app/setup/tenantbilling/tenantsummary/' + id)
      }
    },
    editTenant(tenant) {
      this.isEdit = true
      this.selectedTenant = this.$helpers.cloneObject(tenant)
    },
    deleteTenant(id, idx) {
      this.$dialog
        .confirm({
          title: 'Delete Tenant',
          message: 'Are you sure you want to delete this tenant?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            this.$http.post('/tenant/delete', { id: id }).then(response => {
              if (typeof response.data === 'object') {
                this.tenants.splice(idx, 1)
                this.$message.success('Tenant deleted successfully')
              } else {
                this.$message.error('Rate card cannot be deleted')
              }
            })
          }
        })
    },
    loadTenants() {
      let self = this
      self.loading = true
      self.$http.get('/tenant/all?page=1&perPage=50').then(function(response) {
        if (self.$account.user.email.includes('@facilio.com')) {
          self.showsummary = true
        }
        self.tenants = response.data ? response.data : []
        self.loading = false
      })
    },
    loadRatecards() {
      this.$http.get('/ratecards').then(response => {
        this.ratecards = response.data ? response.data : []
      })
    },
    onBillClicked(tenantId) {
      this.showBillDialog = true
      this.model.tenantId = tenantId
    },
    // tenantSummary () {

    // },
    generateBill() {
      this.$message({ message: 'Generating bill...', duration: 0 })
      this.model.startTime = this.$helpers.getTimeInOrg(this.date[0])
      this.model.endTime = this.$helpers.getTimeInOrg(this.date[1])
      this.$http.post('/tenant/generatebill', this.model).then(response => {
        this.$message.closeAll()
        if (typeof response.data === 'object') {
          this.billUrl = response.data.fileUrl
        } else {
          this.$message.error('Bill cannot be generated')
        }
      })
    },
  },
}
</script>
<style>
.fc-list-view-table tbody tr.tablerow:hover {
  background: #fafbfc;
}
.tablerow {
  cursor: pointer;
}
.tablerow.active {
  background: #f3f4f7 !important;
  border-left: 3px solid #ff2d81 !important;
  border-right: 1px solid rgba(232, 232, 232, 0.35) !important;
  border-top: 1px solid rgba(232, 232, 232, 0.35) !important;
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}

.fc-white-theme .tablerow.selected {
  background: #e2f1ef;
  border-left: 3px solid #28b2a4;
  border-right: 1px solid rgba(232, 232, 232, 0.35);
  border-top: 1px solid rgba(232, 232, 232, 0.35);
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
.fc-black-theme .tablerow.selected {
  background: hsla(0, 0%, 100%, 0.1) !important;
  border-left: 3px solid #28b2a4;
  border-right: 1px solid rgba(232, 232, 232, 0.35);
  border-top: 1px solid rgba(232, 232, 232, 0.35);
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
.NewSetting-Rlayout {
  padding: 2.8rem 1.7rem !important;
  height: auto;
  overflow: scroll;
}
.contactinfo-name {
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #666666;
  letter-spacing: 0.6px;
  padding: 6px;
  text-transform: capitalize;
}
.contactinfo-email {
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #41a1d0;
  /* color: var(--windows-blue); */
  letter-spacing: 1.6px;
  /* padding: 6px; */
  text-transform: lowercase;
}
.contactinfo {
  width: 100%;
  position: relative;
  bottom: 20px;
  text-align: center;
  margin-top: 55px;
  margin-left: auto;
  margin-right: auto;
}
.text-left actions flRight {
  margin-top: 110px;
  margin-right: 15px;
  text-align: center;
  width: 281px;
  letter-spacing: 38px;
}
.tenant-card:hover .tenant-icon {
  display: block !important;
}
.tenant-card:hover .tenant-editicon {
  display: block !important;
}
</style>
