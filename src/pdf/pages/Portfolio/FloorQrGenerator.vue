<template>
  <div>
    <div v-if="loading" class="flex-middle justify-content-center">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div class="building-qr-page flex-middle" v-else>
      <div class="qr-code-dotted" v-if="floorData.qrVal">
        <qrcode-vue :value="floorData.qrVal || []" :size="size" level="H" />

        <div
          class="label-txt-black fwBold pT10 text-center"
          v-if="floorData.name"
        >
          {{ floorData.name }}
        </div>
        <div class="flex-middle flex-wrap justify-center pT10 gap7">
          <div class="fc-black2-label12" v-if="siteData.name">
            {{ siteData.name }} <span class="pL5 pR5">></span>
          </div>
          <div class="fc-black2-label12" v-if="buildingData.name">
            {{ buildingData.name }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import QrcodeVue from 'qrcode.vue'
import { API } from '@facilio/api'
import Spinner from '@/Spinner'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['moduleName'],
  title() {
    return 'Floor Qr'
  },
  data() {
    return {
      loading: true,
      size: 200,
      buildingData: {},
      siteData: {},
      floorData: {},
    }
  },
  components: {
    QrcodeVue,
    Spinner,
  },
  computed: {
    id() {
      return this.$route.params.floorId
        ? parseInt(this.$route.params.floorId)
        : ''
    },
  },
  mounted() {
    this.init()
  },
  methods: {
    async init() {
      this.loading = true
      await this.fetchFloorOverView()
      this.loading = false
      this.printPage()
    },
    async fetchFloorOverView() {
      let { id } = this
      if (!isEmpty(id)) {
        let { error, floor } = await API.fetchRecord('floor', { id })
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.floorData = floor || {}
          let floorValue = this.floorData
          await this.fetchBuildingOverView(floorValue)
          await this.fetchSiteOverView(floorValue)
        }
      }
    },
    async fetchBuildingOverView(floor) {
      let { building } = floor || {}
      let { id } = building || {}
      if (!isEmpty(id)) {
        let { error, building } = await API.fetchRecord('building', { id })
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.buildingData = building || {}
        }
      }
    },
    async fetchSiteOverView(floor) {
      let { site } = floor || {}
      let { id } = site || {}
      if (!isEmpty(id)) {
        let { error, site } = await API.fetchRecord('site', { id })
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.siteData = site || {}
        }
      }
    },
    printPage() {
      let { floorData } = this
      let { qrVal } = floorData || {}
      if (!isEmpty(qrVal)) {
        this.$nextTick(() => {
          window.print()
        }, 2500)
      }
    },
  },
}
</script>

<style>
.building-qr-page {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
@media print {
  .building-qr-page {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>
