<template>
  <div>
    <el-image
      v-on:click.self.prevent="openAttachmentsPreview(true, imageAttachment)"
      :src="imageAttachment ? $prependBaseUrl(imageAttachment.previewUrl) : ''"
      fit="cover"
      :class="[
        'fc-image-list-preview fc-avatar-square',
        customClass ? customClass : null,
      ]"
    >
      <div slot="error">
        <div v-on:click.prevent="openAttachmentsPreview(false)">
          <slot v-if="attachments.length > 0" name="no-image">
            <InlineSvg
              src="clip"
              iconClass="icon fill-grey icon-xxlll op5 vertical-middle object-contain"
            ></InlineSvg>
          </slot>
          <slot v-else name="no-data">
            <InlineSvg
              src="svgs/photo"
              iconClass="icon fill-grey icon-xxlll op5 vertical-middle object-contain"
            ></InlineSvg>
          </slot>
        </div>
      </div>
    </el-image>
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

export default {
  components: {
    PreviewFile,
  },
  props: ['module', 'record', 'customClass', 'id'],
  data() {
    return {
      loading: false,
      attachments: [],
      visibility: false,
      imageAttachment: null,
      previewFile: null,
      imageAttachments: [],
    }
  },
  created() {
    this.loadAttachments()
  },
  computed: {
    recordId() {
      return this.id ? this.id : this.record.id
    },
  },
  methods: {
    openAttachmentsPreview(fileAttached, image) {
      if (this.attachments.length > 0) {
        this.visibility = true
        if (fileAttached) {
          this.previewFile = image
        } else {
          this.previewFile = this.attachments[0]
        }
      }
    },
    setImageUrl() {
      for (let attachment of this.attachments) {
        if (attachment.contentType.indexOf('image') !== -1) {
          if (!this.imageAttachment) {
            this.imageAttachment = attachment
          }
          this.imageAttachments.push(attachment)
        }
      }
    },
    loadAttachments() {
      this.loading = true
      return API.get('/attachment', {
        module: this.module,
        recordId: this.recordId,
      }).then(({ error, data }) => {
        if (error) {
          this.attachments = []
        } else {
          this.attachments = data.attachments ? data.attachments : []
          this.setImageUrl()
        }
        this.loading = false
      })
    },
  },
}
</script>
