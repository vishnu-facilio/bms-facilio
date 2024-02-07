<script>
export default {
  computed: {
    getFormattedFile() {
      let { photos } = this
      let filePreviewList = []

      ;(photos || []).forEach(photo => {
        filePreviewList.push({
          contentType: 'image',
          previewUrl: photo.originalUrl,
          downloadUrl: this.$helpers.getFileDownloadUrl(photo.photoId),
        })
      })
      return filePreviewList
    },
    newSiteSummary() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers || {}

      return (
        isLicenseEnabled('NEW_SITE_SUMMARY') &&
        isLicenseEnabled('WEATHER_INTEGRATION')
      )
    },
  },
}
</script>
