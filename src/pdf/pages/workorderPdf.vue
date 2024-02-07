<template>
  <div>
    <pdfPage>
      <template #content>
        <!-- header -->
        <div class="flex-middle justify-content-space">
          <div>
            Customer logo
          </div>
          <div>
            <div>
              {{ wosummaryData.subject ? wosummaryData.subject : '---' }}
            </div>
            <div class="flex-middle pT10" style="justify-content: center;">
              <div
                v-if="
                  wosummaryData.resource &&
                    wosummaryData.resource.resourceType === 2
                "
                class="flex-middle"
                style="justify-content: center;"
              >
                <div class="fc-black f14 pL10">
                  {{ siteNameCollection[wosummaryData.siteId] }}
                </div>
              </div>
            </div>
          </div>
          <div>
            <img src="~assets/facilio-logo-black.svg" />
          </div>
        </div>
        <div>
          main section
        </div>
      </template>
    </pdfPage>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import pdfPage from 'src/pdf/component/PdfPageComponent'
import WorkorderMixin from 'pages/pdf/mixins/WorkorderPdfMixin'
export default {
  mixins: [WorkorderMixin],
  data() {
    return {
      loading: true,
      wosummaryData: [],
    }
  },
  props: ['moduleName', 'id'],
  computed: {},
  components: {
    pdfPage,
  },
  created() {
    this.woSumary()
  },
  methods: {
    async woSumary() {
      this.loading = true
      let { error, data } = await API.get(
        `v3/modules/${this.moduleName}?id=${this.id}`
      )
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.wosummaryData = data.workorder
      }
      this.loading = false
    },
  },
}
</script>
