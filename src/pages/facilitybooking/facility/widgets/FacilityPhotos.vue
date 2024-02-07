<template>
  <div class="facility-photos-container">
    <div v-if="loading">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div v-else-if="$validation.isEmpty(imageAttachments)" class="height100">
      <div
        class="d-flex justify-content-center flex-direction-column align-center height100"
      >
        <div
          v-if="!$helpers.isPortalUser()"
          @click="openPhotosUpdater"
          class="add-image-box no-data"
        >
          <InlineSvg
            src="svgs/plus-icon"
            class="vertical-middle'"
            iconClass="icon icon-lg"
          ></InlineSvg>
        </div>
        <InlineSvg
          v-else
          src="svgs/cardNodata"
          class="vertical-middle'"
          iconClass="icon icon-xxllg"
        ></InlineSvg>
        <div class="fc-black3-16 self-center bold mT10">
          No photos added for the Facility.
        </div>
      </div>
    </div>
    <div v-else class="facility-photos-padding d-flex flex-row">
      <div
        @click="openAttachmentsPreview(index)"
        v-for="(image, index) in getImagesForPreview"
        :key="index"
      >
        <img
          :src="$prependBaseUrl(image.originalUrl)"
          class="fb-img-container pointer mR30"
        />
        <div
          v-if="index === 4 && imageAttachments.length > 5"
          class="overlay-box fc-white-13"
        >
          {{ `+ ${imageAttachments.length - 5}` }}
        </div>
      </div>
      <div
        v-if="!$helpers.isPortalUser()"
        @click="openPhotosUpdater"
        class="add-image-box list-addon"
      >
        <InlineSvg
          src="svgs/plus-icon"
          class="vertical-middle'"
          iconClass="icon icon-lg"
        ></InlineSvg>
      </div>
      <PreviewFile
        :visibility.sync="visibility"
        v-if="visibility"
        :previewFile="getFormattedFile[previewIndex]"
        :files="getFormattedFile"
      ></PreviewFile>
    </div>
    <PhotosUpdater
      ref="photos-updater"
      :record="details"
      :photosModuleName="'facilityphotos'"
      @photosList="data => setPhotos(data)"
    ></PhotosUpdater>
  </div>
</template>
<script>
import PreviewFile from '@/PreviewFile'
import PhotosUpdater from 'src/pages/spacemanagement/overview/components/PhotosUpdater'
export default {
  props: ['details'],
  components: {
    PreviewFile,
    PhotosUpdater,
  },
  data() {
    return {
      loading: true,
      imageAttachments: [],
      visibility: false,
      previewIndex: null,
    }
  },
  computed: {
    getImagesForPreview() {
      return this.imageAttachments.slice(0, 5)
    },
    getFormattedFile() {
      let { imageAttachments } = this
      let filePreviewList = []
      imageAttachments.forEach(photo => {
        filePreviewList.push({
          contentType: 'image',
          previewUrl: this.$prependBaseUrl(photo.originalUrl),
          downloadUrl: this.$helpers.getFileDownloadUrl(photo.photoId),
        })
      })
      return filePreviewList
    },
  },
  methods: {
    openAttachmentsPreview(index) {
      this.previewIndex = index
      this.visibility = true
    },
    setPhotos(data) {
      this.imageAttachments = data
      this.loading = false
    },
    openPhotosUpdater() {
      this.$refs['photos-updater'].open()
    },
  },
}
</script>
