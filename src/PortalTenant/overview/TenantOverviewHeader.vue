<template>
  <div class="fc-tenant-homeP-header-block p20 pB10">
    <div
      v-if="loading"
      class="pT20 pB20 flex-middle flex-center-hH height300 width100 white-bg-block shadow-none pB20"
    >
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div class="width100" v-else>
      <div class="position-relative white-bg-block shadow-none">
        <div class="fc-tenant-homeP-header-img-bg"></div>
        <img
          :src="$prependBaseUrl(tenant.avatarUrl)"
          class="portal-tenant-logo fc-tenant-header-img"
          v-if="!$validation.isEmpty($getProperty(tenant, 'avatarUrl'))"
        />
        <div class="fc-tenant-logo-block white-bg-block">
          <div class="fc-tenant-logo flex-middle">
            <avatar
              size="lg"
              :user="{ name: tenant.name }"
              :style="{
                color: '#fff',
              }"
              color="#473a9e"
              class="pointer"
            ></avatar>
            <div class="pL10">
              <div class="fc-white-16 text-left nowrap">
                {{ $getProperty(tenant, `name`, '---') }}
              </div>
              <div
                class="fc-white-16 text-left pT5"
                v-if="tenant.tenantType === 1"
              >
                Commerical
              </div>
              <div
                class="fc-white-16 text-left pT5"
                v-if="tenant.tenantType === 2"
              >
                Residential
              </div>
              <div v-else class="fc-white-16 text-left pT5">
                {{ '' }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="fc-blue-label pT40 pB10">ALL LOCATIONS</div>
      <div
        class="white-bg-block p30 "
        :class="expandHeader"
        id="tenantlist"
        :style="{ height: height }"
      >
        <el-row>
          <el-col :span="12">
            <el-col :span="12">
              <div class="fc-black-13 text-left bold">
                {{ $t('tenant.tenants.unit_name') }}
              </div>
            </el-col>
            <el-col :span="12" v-if="$validation.isEmpty(tenanUnit)"
              >---</el-col
            >
            <el-col :span="12" v-if="!$validation.isEmpty(tenanUnit)">
              <div
                class="fc-black-13 text-left flex-middle pB5"
                v-for="(tenant, index) in UnitsArray"
                :key="index"
              >
                <div v-if="!$validation.isEmpty($getProperty(tenant, 'name'))">
                  {{ tenant.name }}
                  <span v-if="index < tenant.length - 1" class="pR5">,</span>
                </div>
                <div v-else>---</div>
              </div>
              <div
                @click="showOrHideUnits"
                v-if="tenanUnit.length > 2"
                class="fc-blue-txt4-13 fc-text-underline pointer"
              >
                {{ expandText }}
              </div>
            </el-col>
          </el-col>
          <el-col :span="12">
            <el-col :span="12">
              <div class="fc-black-13 text-left bold">
                {{ $t('space.sites.area') }}
              </div>
            </el-col>
            <el-col :span="12">
              <div
                class="fc-black-13 text-left "
                v-if="!$validation.isEmpty(tenanUnit)"
              >
                {{ $util.formateSqft(sumofsqft) }}
              </div>
              <div class="fc-black-13 text-left " v-else>
                ---
              </div>
            </el-col>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import Avatar from '@/Avatar'
export default {
  data() {
    return {
      tenant: [],
      loading: true,
      spaceArea: null,
      spaceName: null,
      tenanUnit: [],
      showMoreUnits: true,
      fullHeight: null,
    }
  },
  mounted() {
    this.init()
  },
  components: {
    Avatar,
  },
  computed: {
    height() {
      if (this.fullHeight && this.tenanUnit.length > 2) {
        return this.showMoreUnits ? `${this.fullHeight}px` : '116px'
      }
      return 'auto'
    },
    sumofsqft() {
      if (this.tenanUnit && this.tenanUnit.length) {
        let cost = 0
        for (let i = 0; i < this.tenanUnit.length; i++) {
          if (this.tenanUnit[i].area) {
            cost += this.tenanUnit[i].area
          }
        }
        return cost
      } else {
        return 0
      }
    },
    expandHeader() {
      let { showMoreUnits } = this
      return showMoreUnits
        ? 'portal-all-locations-expanded'
        : 'portal-all-locations'
    },
    UnitsArray() {
      let { tenanUnit, showMoreUnits } = this || {}
      if (tenanUnit && tenanUnit.length) {
        if (!tenanUnit.length > 2) {
          return tenanUnit
        }
        return showMoreUnits ? tenanUnit : tenanUnit.slice(0, 2)
      }
      return null
    },
    expandText() {
      let { tenanUnit, showMoreUnits } = this || {}
      if (tenanUnit && tenanUnit.length) {
        if (!tenanUnit.length > 2) {
          return
        }
        if (showMoreUnits) {
          return 'Show Less'
        }
        let remainingCount = tenanUnit.length - 2
        let message = `+${remainingCount} More`
        return message
      }
      if (showMoreUnits) {
        return 'Show Less'
      }
      return null
    },
  },
  methods: {
    async init() {
      this.loading = true

      await this.loadTenant()
      await this.loadTenantUnit()
      this.loading = false
      this.getClientHeight()
    },
    getClientHeight() {
      this.$nextTick(() => {
        let el = document.getElementById('tenantlist')
        if (el) {
          this.fullHeight = el.clientHeight

          this.showMoreUnits = false
        }
      })
    },
    loadTenant() {
      API.get('v2/tenant/details', {
        tenantPortal: true,
      }).then(({ error, data }) => {
        if (!error) {
          this.tenant = data.tenant || []
        }
      })
    },
    showOrHideUnits() {
      let { showMoreUnits } = this
      this.showMoreUnits = !showMoreUnits
    },
    async loadTenantUnit() {
      let { error, data } = await API.get(
        'v3/modules/data/list?moduleName=tenantunit'
      )
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.tenanUnit = data.tenantunit || []
      }
    },
  },
}
</script>

<style lang="scss">
.fc-tenant-homeP-header-block {
  position: relative;

  .fc-tenant-homeP-header-img-bg {
    height: 159px;
    position: relative;
    background: #4e419f;

    &::after {
      content: '';
      z-index: 100;
      width: 100%;
      height: 158px;
      left: 0;
      position: absolute;
      background-image: linear-gradient(
        to bottom,
        rgba(0, 0, 0, 0) 56%,
        #000000 100%
      );
    }
  }

  .fc-tenant-homeP-header-row .el-col-12 {
    width: 33.3% !important;
  }
}

.fc-tenant-logo-block {
  position: absolute;
  bottom: -23px;
  z-index: 100;
  width: 132px;
  height: 132px;
  border-radius: 50%;
  background: #fff;
  left: 30px;
  box-shadow: 0 7px 4px 0 rgba(63, 66, 76, 0.09);

  .fc-tenant-logo {
    .fc-avatar {
      width: 130px;
      height: 130px;
      font-size: 22px;
      background: #fff !important;
      color: #324056;
    }
  }

  .fc-tenant-header-name {
    position: absolute;
    bottom: 50px;
    left: 150px;
    color: #fff;
    font-size: 18px;
    font-weight: normal;
    line-height: normal;
    letter-spacing: 0.5px;
    color: #ffffff;
  }
}

.fc-tenant-header-img {
  width: 100%;
  height: 158px;
  position: absolute;
  top: 0;
  object-fit: cover;
}
.portal-all-locations {
  transition: height 0.1s ease-in-out;
}
.portal-all-locations-expanded {
  transition: height 0.1s ease-in-out;
}
</style>
