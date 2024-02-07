<template>
  <div>
    <div v-if="loading" class="flex-middle justify-content-center">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div class="building-qr-page flex-middle flex-wrap justify-center" v-else>
      <div class="qr-code-dotted" v-if="buildingData.qrVal">
        <qrcode-vue :value="buildingData.qrVal || []" :size="size" level="H" />

        <div class="label-txt-black fwBold pT10" v-if="buildingData.name">
          {{ buildingData.name }}
        </div>
        <div class="fc-black2-label12 pT10 text-center" v-if="siteData.name">
          {{ siteData.name }}
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
    return 'Building Qr'
  },
  data() {
    return {
      loading: true,
      size: 200,
      buildingData: {},
      siteData: {},
    }
  },
  components: {
    QrcodeVue,
    Spinner,
  },
  computed: {
    id() {
      return this.$route.params.buildingId
        ? parseInt(this.$route.params.buildingId)
        : ''
    },
  },
  mounted() {
    this.init()
  },
  methods: {
    async init() {
      this.loading = true
      await this.fetchBuildingOverView()
      this.loading = false
      this.printPage()
    },
    async fetchBuildingOverView() {
      let { id } = this
      let { error, building } = await API.fetchRecord('building', { id })
      if (!isEmpty(id)) {
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.buildingData = building || {}
          await this.fetchSiteOverView(this.buildingData)
        }
      }
    },
    async fetchSiteOverView(building) {
      let { site } = building || {}
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
      let { buildingData } = this
      let { qrVal } = buildingData || {}
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
