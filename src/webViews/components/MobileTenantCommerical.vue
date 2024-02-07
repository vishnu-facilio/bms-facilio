<template>
  <div>
    <div class="text-center width100">
      <img
        v-if="$getProperty($account, 'org.logoUrl')"
        :src="getLogoUrl($account.org)"
        height="60"
        width="100%"
        class="object-scale-down"
      />
    </div>
    <div class="fc-black-14 f24 flex-middle flex-wrap fwBold pT20">
      Hello {{ $portaluser.name }}
      <inline-svg
        src="svgs/emojis/hi-emoji"
        iconClass="icon text-center mL10 vertical-middle icon-lg"
      ></inline-svg>
    </div>
    <el-card
      :body-style="{ padding: '0px' }"
      class="fc-mobile-tenant-card mT20"
      v-if="loading"
    >
      <spinner :show="loading" size="80"></spinner>
    </el-card>

    <el-card
      :body-style="{ padding: '0px' }"
      class="fc-mobile-tenant-card-commerical pT20"
      v-if="!$validation.isEmpty(spaces) && !loading"
    >
      <div class="fc-card-commercial-block">
        <el-row class="">
          <el-col :span="16" class="flex-middle flex-wrap">
            <div
              class="fc-white-14 flex-middle pB5"
              v-for="(space, index) in tenant.spaces"
              :key="index"
            >
              <div v-if="!$validation.isEmpty($getProperty(space, 'name'))">
                {{ space.name
                }}<span v-if="index < tenant.spaces.length - 1" class="pR5"
                  >,</span
                >
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="bold text-right fc-yellow2-txt14 pB5">
              {{ $util.formateSqft(sumofsqft) }}
            </div>
          </el-col>
        </el-row>
        <div
          class="fc-white-14 pT10"
          v-if="!$validation.isEmpty($getProperty(tenant, 'site.name'))"
        >
          {{ $getProperty(tenant, 'site.name') }}
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  props: ['tenant', 'loading'],
  computed: {
    ...mapState({
      sites: state => state.sites,
    }),
    spaces() {
      if (this.tenant && this.tenant.spaces) {
        return this.tenant.spaces
      } else {
        return []
      }
    },
    sumofsqft() {
      if (this.spaces && this.spaces.length) {
        let cost = 0
        for (let i = 0; i < this.spaces.length; i++) {
          if (this.spaces[i].area) {
            cost += this.spaces[i].area
          }
        }
        return cost
      } else {
        return 0
      }
    },
  },
  methods: {
    getLogoUrl(org = {}) {
      let { logoUrl } = org || {}
      if (logoUrl) {
        return this.$prependBaseUrl(logoUrl)
      }
      return null
    },
    getSiteName(siteId) {
      let site = (this.sites || []).find(({ id }) => id === siteId)
      return site ? site.name : ''
    },
  },
}
</script>
<style lang="scss" scoped>
.fc-white-14 {
  line-height: normal;
  letter-spacing: 0.39px;
}
</style>
