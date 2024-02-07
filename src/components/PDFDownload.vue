<template>
  <iframe v-if="downloadUrl" :src="downloadUrl" style="display: none;"></iframe>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['url', 'additionalInfo', 'isDownload'],
  data() {
    return {
      downloadUrl: null,
    }
  },
  watch: {
    isDownload: {
      handler(val) {
        let { url } = this
        if (!isEmpty(url) && val) {
          this.downloadPdf()
        }
      },
      immediate: true,
    },
  },
  methods: {
    downloadPdf() {
      let { url, additionalInfo } = this
      this.$message({
        message: 'Downloading...',
        showClose: true,
        duration: 0,
      })
      API.post(`/v2/integ/pdf/create`, {
        url,
        additionalInfo,
      }).then(({ data, error }) => {
        this.$message.closeAll()
        if (error) {
          let { message = 'Unable to fetch download link' } = error
          this.$message.error(message)
        } else {
          this.downloadUrl = (data || {}).fileUrl
        }
        this.$emit('update:isDownload', false)
      })
    },
  },
}
</script>
