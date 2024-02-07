<template>
  <div>
    <div v-if="loading" class="flex-middle justify-content-center">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div class="site-qr-page flex-middle" v-else>
      <div class="qr-code-dotted" v-if="siteData.qrVal">
        <qrcode-vue :value="siteData.qrVal || []" :size="size" level="H" />
        <div
          class="label-txt-black fwBold pT10 text-center"
          v-if="siteData.name"
        >
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
    return 'Site Qr download'
  },
  data() {
    return {
      loading: true,
      size: 200,
      siteData: {},
    }
  },
  components: {
    QrcodeVue,
    Spinner,
  },
  computed: {
    id() {
      return this.$route.params.siteId
        ? parseInt(this.$route.params.siteId)
        : ''
    },
  },
  mounted() {
    this.init()
  },
  methods: {
    async init() {
      this.loading = true
      await this.fetchSiteOverView()
      this.loading = false
      this.printPage()
    },
    async fetchSiteOverView() {
      let { error, data } = await API.get(
        `v3/modules/site/${this.id}?id=${this.id}&moduleName=site`
      )
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.siteData = data.site || {}
      }
    },
    printPage() {
      let { siteData } = this
      let { qrVal } = siteData || {}
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
.site-qr-page {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
@media print {
  .site-qr-page {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>
