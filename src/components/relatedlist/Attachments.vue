<template>
  <div class="fc-attachments">
    <div class="text-center" v-if="loading">
      <spinner :show="loading"></spinner>
    </div>
    <div v-else>
      <div
        class="fc-attachment-row visibility-visible-actions"
        v-for="(attachment, index) in attachments"
        :key="attachment.attachmentId"
      >
        <div class="attachment-label pointer">
          <a v-if="attachment.previewUrl" @click="selectedFile(attachment)">{{
            attachment.fileName
          }}</a>
          <span v-else>{{ attachment.fileName }}</span>
        </div>
        <div class="attachment-sublabel pT15 pB10">
          <span>{{ attachment.fileSize | prettyBytes }}</span>
          <span class="attachment-sublabel" v-if="attachment.status === 1"
            >, Uploading...</span
          >
          <span class="attachment-sublabel" v-else-if="attachment.status === 2"
            >, Success</span
          >
          <span class="attachment-sublabel" v-else>
            {{ attachment.error }}</span
          >
        </div>
        <div class="remove pull-right">
          <a
            style="cursor: wait;"
            class="visibility-hide-actions"
            onclick="javascript:void(0)"
            v-if="attachment.status === 1"
            ><img src="~assets/bin-1.svg" width="15" height="15"
          /></a>
          <a
            class="remove-attachment-link fc-delete visibility-hide-actions"
            @click="deleteAttachment(attachment, index)"
            v-else-if="canShowDelete(attachment)"
            ><img src="~assets/bin-1.svg" width="15" height="15"
          /></a>
        </div>
      </div>
      <form enctype="multipart/form-data" novalidate class="pT20 pB20">
        <div class="dropbox">
          <input
            type="file"
            multiple
            class="input-file"
            @change="filesChange($event.target.files)"
          />
          <img
            src="~assets/upload-icon.svg"
            width="20"
            height="20"
            class="mT20 opacity-05 mB10"
          />
          <p>
            {{ $t('common.attachment_form.drag_and_drop_files') }}<br />
            {{ $t('common.attachment_form.click_to_browse') }}
          </p>
        </div>
      </form>
    </div>
    <preview-file
      :visibility.sync="visibility"
      v-if="visibility && selectedAttachedFile"
      :previewFile="selectedAttachedFile"
      :files="attachments"
    ></preview-file>
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
  methods: {
    selectedFile(inList) {
      this.selectedAttachedFile = inList
      this.visibility = true
    },
  },
}
</script>
<style>
.fc-attachments .dropbox {
  outline: 1px dashed #d3d3d3;
  outline-offset: -10px;
  color: #696969;
  padding: 10px 10px;
  position: relative;
  cursor: pointer;
}
.fc-attachments .dropbox .input-file {
  opacity: 0;
  width: 100%;
  height: 84px;
  position: absolute;
  cursor: pointer;
}
.fc-attachments .dropbox:hover {
  outline: 1px dashed #808080;
}
.fc-attachments .dropbox p {
  font-size: 0.8em;
  text-align: center;
}
.fc-attachments .fc-attachment-row {
  border-bottom: 1px solid #f7f7f7;
  padding: 10px 15px;
}
.fc-attachments {
  font-size: 13px;
}
.fc-attachments .fc-attachment-row .attachment-sublabel,
.fc-attachments .fc-attachment-row .attachment-progress {
  color: #a3a2b1;
  font-size: 12px;
}
.fc-attachments .fc-attachment-row div {
  color: #000;
}
.fc-attachments .fc-attachment-row .remove {
  margin-top: -30px;
}
.fc-attachments .fc-attachment-row:last-child {
  border: none;
}
.attachment-label a {
  color: #25243e;
}
.remove-attachment-link:hover {
  color: #333;
}
</style>
