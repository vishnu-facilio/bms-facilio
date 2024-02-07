<template>
  <div class="facility-photos-container">
    <div v-if="loading">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div v-else>
      <div class="flex-middle justify-content-space pL20 pT20">
        <div class="f13 bold text-uppercase fc-black-13 text-left">
          {{ $t('common._common.photos') }}
        </div>
      </div>
      <div v-if="$validation.isEmpty(imageAttachments)">
        <div
          class="d-flex justify-content-center flex-direction-column align-center height100 mT20"
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
            {{ 'No photos added for the Unit' }}
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
    </div>
    <PhotosUpdater
      ref="photos-updater"
      :record="details"
      :photosModuleName="'basespacephotos'"
      @photosList="data => setPhotos(data)"
    ></PhotosUpdater>
  </div>
</template>
<script>
import FacilityPhotos from 'src/pages/facilitybooking/facility/widgets/FacilityPhotos.vue'
export default {
  extends: FacilityPhotos,
}
</script>
<style lang="scss" scoped>
.facility-photos-container {
  .facility-photos-padding {
    padding: 20px 30px 20px 30px;
  }
  .fb-img-container {
    width: 70px;
    height: 70px;
    border-radius: 6px;
    cursor: pointer;
    object-fit: cover;
  }
}
</style>
