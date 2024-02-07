<template>
  <div>
    <div
      v-if="canShowFileDisplay"
      @click="openAttachment()"
      class="d-flex file-column"
    >
      <a class="truncate-text">
        {{ getColumnDisplayValue }}
      </a>
    </div>
    <div v-else>---</div>

    <preview-file
      :visibility.sync="showPreview"
      v-if="showPreview && selectedFile"
      :previewFile="selectedFile"
      :files="[selectedFile]"
    ></preview-file>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import PreviewFile from '@/PreviewFile'

export default {
  props: ['field', 'record', 'isV2'],
  components: { PreviewFile },
  data() {
    return {
      selectedFile: null,
      showPreview: false,
    }
  },
  computed: {
    getColumnDisplayValue() {
      let { field, record, isV2 } = this
      let data = this.getRecordData(field, record)
      let fileName

      if (isV2) {
        fileName = data[`${field.name}FileName`]
      } else {
        fileName = record[`${field.name}FileName`]
      }

      if (!isEmpty(fileName)) {
        return fileName
      } else {
        return '---'
      }
    },
    canShowFileDisplay() {
      let { field, record, isV2 } = this
      let data = this.getRecordData(field, record)

      if (isV2) {
        return !isEmpty(data[`${field.name}FileName`])
      } else {
        return !isEmpty(record[`${field.name}FileName`])
      }
    },
  },
  methods: {
    openAttachment() {
      let { field, record, isV2 } = this
      let data = this.getRecordData(field, record)

      if (isV2) {
        this.selectedFile = {
          contentType: data[`${field.name}ContentType`],
          fileName: data[`${field.name}FileName`],
          downloadUrl: data[`${field.name}DownloadUrl`],
          previewUrl: data[`${field.name}Url`],
        }
        this.showPreview = true
      } else {
        this.selectedFile = {
          contentType: record[`${field.name}ContentType`],
          fileName: record[`${field.name}FileName`],
          downloadUrl: record[`${field.name}DownloadUrl`],
          previewUrl: record[`${field.name}Url`],
        }
        this.showPreview = true
      }
    },
    getRecordData(field, record) {
      let data
      if (!field.default && !isEmpty(record.data)) {
        data = record.data
      } else {
        data = record
      }
      return data
    },
  },
}
</script>

<style></style>
