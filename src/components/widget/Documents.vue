<template>
  <div
    class="fc-attachments h100 position-relative"
    ref="docs-container"
    @dragover.prevent="dragOverHandler"
    @dragleave.prevent="dragLeaveHandler"
    @drop="dropLeaveHandler"
  >
    <div v-if="loading" class="text-center">
      <spinner :show="loading"></spinner>
    </div>
    <template v-else>
      <div style="max-width: 900px;">
        <div class="view-more" v-if="needsShowMore && attachments">
          <a @click="showAllAttachments" class="f12 mL10">{{
            canShowAllAttachments ? 'Show Less' : 'Show More'
          }}</a>
          <div class="inline-block bold f12" v-if="!canShowAllAttachments">
            {{ $getProperty(filteredAttachments, 'length') }} of
            {{ $getProperty(attachments, 'length') }}
          </div>
        </div>
        <div
          class="fc-attachment-row visibility-visible-actions"
          v-for="(attachment, index) in filteredAttachments"
          :key="`${attachment.fileId}_${index}`"
        >
          <div class="attachment-label pointer">
            <a
              v-if="attachment.previewUrl"
              @click="openFilePreview(attachment)"
              >{{ attachment.fileName }}</a
            >
            <span v-else>{{ attachment.fileName }}</span>
          </div>
          <div class="attachment-sublabel pT15 pB10">
            <span class="pR10">{{ attachment.fileSize | prettyBytes }}</span>
            <span class="attachment-sublabel" v-if="attachment.status === 1"
              >, Uploading...</span
            >
            <span
              class="attachment-sublabel"
              v-else-if="attachment.status === 2"
              >, Success</span
            >
            <span class="attachment-sublabel" v-else>{{
              attachment.error
            }}</span>
          </div>
          <div v-if="canShowDelete(attachment)" class="remove pull-right">
            <!-- <a
              style="cursor: wait;"
              class="visibility-hide-actions"
              onclick="javascript:void(0)"
              v-if="attachment.status === 1"
            >
              <img src="~assets/bin-1.svg" width="15" height="15" />
            </a> -->
            <a
              class="remove-attachment-link fc-delete visibility-hide-actions"
              @click="deleteAttachment(attachment, index)"
            >
              <img src="~assets/bin-1.svg" width="15" height="15" />
            </a>
          </div>
        </div>
      </div>
      <form
        enctype="multipart/form-data"
        novalidate
        :class="[showDragArea ? 'show-v' : 'hide-v', 'drop-container']"
      >
        <div class="dropbox p0">
          <input
            ref="file-input"
            type="file"
            multiple
            class="input-file"
            @change="filesChange($event.target.files)"
          />
          <img
            src="~assets/upload-icon.svg"
            width="20"
            height="20"
            class="mT10 mB20 opacity-05"
          />
          <p>
            {{ $t('common.attachment_form.drag_and_drop_files') }}
            <br />{{ $t('common.attachment_form.click_to_browse') }}
          </p>
        </div>
      </form>
    </template>
    <preview-file
      :visibility.sync="canShowPreview"
      v-if="canShowPreview && selectedAttachment"
      :previewFile="selectedAttachment"
      :files="attachments"
    ></preview-file>
    <portal v-if="isActive && groupKey" :to="groupKey + '-topbar'" slim>
      <img
        @click="openFilePicker"
        src="~statics/icons/clip-black.svg"
        class="mR15 pull-right pointer new-attachement"
      />
    </portal>
    <portal v-else-if="(widget || {}).key" :to="widget.key + '-topbar'" slim>
      <img
        @click="openFilePicker"
        src="~statics/icons/clip-black.svg"
        class="mR15 pull-right pointer new-attachement"
      />
    </portal>
  </div>
</template>
<script>
import BaseAttachment from '@/base/Attachments'
import PreviewFile from '@/PreviewFile'

export default {
  extends: BaseAttachment,
  components: {
    PreviewFile,
  },
  props: [
    'groupKey',
    'resizeWidget',
    'layoutParams',
    'isActive',
    'widget',
    'parentModule',
  ],
  data() {
    return {
      canShowAllAttachments: false,
      canShowPreview: false,
      selectedAttachment: null,
      showDragArea: false,
      visibleAttachmentCount: 3,
      needsShowMore: true,
      defaultWidgetHeight: this.layoutParams.h || null,
    }
  },
  computed: {
    filteredAttachments() {
      let attachments = this.attachments
      let count = this.$getProperty(attachments, 'length')

      return !this.canShowAllAttachments && count > this.visibleAttachmentCount
        ? attachments.slice(0, this.visibleAttachmentCount)
        : attachments
    },
  },
  watch: {
    attachments: function() {
      let count = this.$getProperty(this.attachments, 'length')
      this.record.noOfAttachments = count
      this.needsShowMore = count > this.visibleAttachmentCount
      this.showDragArea = count === 0
    },
    isActive(isActive) {
      if (!isActive && this.canShowAllAttachments) this.showAllAttachments()
    },
  },
  methods: {
    dragOverHandler() {
      this.showDragArea = true
    },
    dragLeaveHandler() {
      this.showDragArea = true
    },
    dropLeaveHandler() {
      this.showDragArea = false
    },
    openFilePreview(attachment) {
      this.selectedAttachment = attachment
      this.canShowPreview = true
    },
    showAllAttachments() {
      this.canShowAllAttachments = !this.canShowAllAttachments

      this.$nextTick(() => {
        if (this.canShowAllAttachments) {
          let height = this.$refs['docs-container'].scrollHeight + 70
          let width = this.$refs['docs-container'].scrollWidth
          this.resizeWidget({ height, width })
        } else {
          this.resizeWidget({ h: this.defaultWidgetHeight })
        }
      })
    },
    openFilePicker() {
      this.$refs['file-input'].click()
    },
  },
}
</script>
<style scoped>
.view-more {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding: 15px 0 16px;
  border-bottom: 1px solid #f1f1f1;
}
.drop-container {
  width: 100%;
  background-color: #fff;
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  padding-bottom: 80px;
}
.dropbox {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin: 30px 20px;
}

.fc-attachment-row {
  padding: 15px 10px;
}
.fc-attachment-row:hover {
  box-shadow: none;
  background-color: #f9fafa;
}

.fc-attachments .dropbox .input-file {
  position: absolute;
  bottom: 0;
  top: 0;
  height: auto;
}
.fc-attachments .dropbox {
  outline: none;
  border: 1px dashed #c0c9c8;
  border-radius: 5px;
}
.fc-attachments .dropbox:hover {
  outline: none;
  border-color: #39b2c2;
}

.attachment-sublabel,
.attachment-progress {
  color: #a3a2b1;
  font-size: 12px;
}
.new-attachement {
  width: 18px;
  height: 18px;
}
</style>
