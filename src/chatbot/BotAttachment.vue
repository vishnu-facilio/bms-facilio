<template>
  <div class="bot-attachment">
    <iframe
      v-if="downloadUrl"
      :src="downloadUrl"
      style="display: none;"
    ></iframe>
    <div
      v-if="attachment.contentType.indexOf('image') !== -1"
      class="preview-attachment"
      @click="showPreview"
    >
      <img :src="attachment.previewUrl" alt="" width="100%" height="100%" />
    </div>
    <div v-else class="no-preview-attachment">
      <div class="file-type">
        {{ attachment.contentType.split('/')[1] || '--' }}
      </div>
      <div class="file-info">
        <div class="bot-file-name" @native="triggerDownload">
          {{ attachment.fileName | trim }}
        </div>
        <div class="file-size-download-container">
          <span class="file-size"> {{ attachment.fileSize | fileSize }}</span>
          <inline-svg
            class="file-download-icon"
            iconStyle="width:10px;height:9px;color:white;"
            @click.native="triggerDownload"
            src="svgs/chatbot/download"
          ></inline-svg>
        </div>
      </div>
    </div>

    <preview-file
      :visibility.sync="previewVisiblity"
      v-if="previewVisiblity"
      :previewFile="attachment"
      :files="[attachment]"
    ></preview-file>
  </div>
</template>

<script>
import _ from 'lodash'
import PreviewFile from '@/PreviewFile'
export default {
  components: {
    PreviewFile,
  },
  filters: {
    trim(value) {
      return value ? value.trim() : '-'
    },
    fileSize(value) {
      if (!value) {
        return '-'
      } else {
        let size = value / 1000
        size = _.round(size, 2)

        if (size > 1000) {
          return size / 1000 + ' MB'
        } else {
          return size + ' KB'
        }
      }
    },
  },
  props: ['attachment'],
  data() {
    return {
      downloadUrl: null,
      previewVisiblity: false,
    }
  },
  methods: {
    showPreview() {
      this.previewVisiblity = true
    },
    triggerDownload() {
      this.downloadUrl = null
      this.$nextTick(() => {
        this.downloadUrl = this.attachment.downloadUrl
      })
    },
  },
}
</script>

<style lang="scss">
@import './style/_variables.scss';

.bot-attachment {
  align-self: flex-end;

  .preview-attachment {
    width: 160px;
    height: 180px;
    align-self: flex-end;
    padding: 5px;
    border-radius: 5px;
    background: #3e2a8c;
    cursor: pointer;
  }

  .no-preview-attachment {
    color: white;
    display: flex;
    background: #3e2a8c;
    border-radius: 5px;
    padding: 5px;
  }
  .file-type {
    border-radius: 5px;
    font-size: 14px;
    text-transform: uppercase;
    background: white;
    color: #3e2a8c;
    padding: 15px;
    margin-right: 15px;
  }
  .file-info {
    flex-grow: 1;
  }
  .bot-file-name {
    cursor: pointer;
    font-size: 12px;
    margin-right: 10px;
    max-width: 120px;
    text-overflow: ellipsis;
    margin-top: 5px;
    overflow: hidden;
    white-space: nowrap;
    &:hover {
      text-decoration: underline;
    }
  }
  .file-size-download-container {
    font-size: 10px;
    margin-top: 5px;
    display: flex;
    height: 16px;
    // line-height: 14px;
  }
  .file-size {
    margin-right: 10px;
  }

  .file-download-icon {
    cursor: pointer;
  }

  .preview-image {
    width: 220px;
    height: 250px;
  }
}
</style>
