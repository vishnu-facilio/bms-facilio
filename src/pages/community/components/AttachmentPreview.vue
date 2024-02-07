<template>
  <div class="pL30 pR30 d-flex flex-direction-column">
    <div class="widget-topbar">
      <div class="widget-title mL0">Attachments</div>
    </div>
    <div
      :class="[
        'd-flex mT10 mB30',
        $validation.isEmpty(attachments) && 'justify-content-center',
      ]"
      ref="attachment-container"
    >
      <template v-if="loading">
        <div class="text-center">
          <spinner :show="loading"></spinner>
        </div>
      </template>
      <template v-else-if="$validation.isEmpty(attachments)">
        <div
          class="d-flex flex-direction-column justify-content-container mB20"
          style="max-height: 80px"
        >
          <inline-svg
            src="svgs/community-empty-state/attachments"
            class="vertical-middle self-center"
            iconClass="icon empty-notes mR5"
          ></inline-svg>
          <div class="empty-notes-desc f13 pL10 self-center">
            {{ $t('common._common.no_attachments') }}
          </div>
        </div>
      </template>
      <template v-else>
        <template v-for="(attachment, index) in previewAttachments">
          <template v-if="attachment.contentType.includes('image')">
            <div
              :key="details.id + index + attachment.id"
              @click="openAttachmentsPreview(true, attachment)"
              :style="getOverlayStyle(attachment)"
              class="fc-avatar-square image-preview cursor-pointer"
            >
              <div v-if="canShowOverlay(index)" class="image-overlay">
                <span class="overlay-count f24"
                  >+{{ hiddenAttachmentCount }}</span
                >
              </div>
            </div>
          </template>
          <template v-else>
            <div
              :key="details.id + index + attachment.id"
              @click="openAttachmentsPreview(true, attachment)"
              class="position-relative cursor-pointer"
            >
              <div v-if="canShowOverlay(index)" class="overlay-count f24">
                +{{ hiddenAttachmentCount }}
              </div>
              <div
                :class="[
                  'file-icon flex flex-row',
                  canShowOverlay(index) && 'svg-overlay',
                ]"
              >
                <InlineSvg
                  :src="getFileIcon(attachment)"
                  class="d-flex "
                  iconClass="icon icon-40"
                >
                </InlineSvg>
              </div>
            </div>
          </template>
        </template>
      </template>
    </div>
    <preview-file
      :visibility.sync="visibility"
      v-if="visibility"
      :previewFile="previewFile"
      :files="attachments"
    ></preview-file>
  </div>
</template>
<script>
import PreviewFile from '@/PreviewFile'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  name: 'AttachmentPreview',
  props: ['moduleName', 'details', 'attachmentsModuleName'],
  components: { PreviewFile },
  data() {
    return {
      loading: false,
      attachments: [],
      visibility: false,
      previewFile: null,
      maxCount: 4,
    }
  },
  created() {
    this.loadAttachments()
  },
  computed: {
    previewAttachments() {
      let { attachments, maxCount } = this
      return attachments.slice(0, maxCount + 1)
    },
    hiddenAttachmentCount() {
      let { attachments: { length: length } = {}, maxCount } = this
      return Math.abs(length - maxCount)
    },
  },
  methods: {
    async loadAttachments() {
      this.loading = true

      let { error, data } = await API.get('/attachment', {
        module: this.attachmentsModuleName,
        recordId: this.details.parentId
          ? this.details.parentId
          : this.details.id,
      })

      if (error) {
        this.loading = false
        this.attachments = []
      } else {
        this.attachments = data.attachments ? data.attachments : []
      }

      this.loading = false
    },
    openAttachmentsPreview(fileAttached, image) {
      let { attachments } = this
      if (attachments.length > 0) {
        this.visibility = true
        if (fileAttached) {
          this.previewFile = image
        } else {
          this.previewFile = attachments[0]
        }
      }
    },
    getFileIcon(attachment) {
      let { contentType } = attachment
      let { FILE_TYPE_ICONS } = this.$constants

      let selectedIndex = FILE_TYPE_ICONS.findIndex(icons => {
        let { fileTypes } = icons
        return fileTypes.some(type => contentType.includes(type))
      })

      if (isEmpty(selectedIndex)) return FILE_TYPE_ICONS[0].path
      else return FILE_TYPE_ICONS[selectedIndex].path
    },
    getOverlayStyle(attachment) {
      let url = this.$prependBaseUrl(attachment.previewUrl)

      return `background-image: url(${url})`
    },
    canShowOverlay(index) {
      let { maxCount, hiddenAttachmentCount } = this
      return index === maxCount && hiddenAttachmentCount > 1
    },
  },
}
</script>
<style lang="scss" scoped>
.image-preview,
.file-icon {
  height: 70px;
  width: 70px;
  margin-right: 10px;
  border: solid 1px #e2e8ee;
  border-radius: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.svg-overlay {
  filter: brightness(0.5);
  background-color: rgba(0, 0, 0, 0.5);
  position: relative;
}
.image-overlay {
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  color: #fff;
}
.overlay-count {
  line-height: 0;
  color: #fff;
  position: absolute;
  top: 2.1rem;
  left: 1.1rem;
  z-index: 99;
}
.empty-notes-desc {
  font-size: 12px;
  letter-spacing: 0.34px;
  text-align: center;
  color: #b3afc9;
}
</style>
