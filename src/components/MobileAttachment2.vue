<template>
  <div>
    <div class="text-center" v-if="loading">
      <spinner :show="loading"></spinner>
    </div>
    <div v-else>
      <div class="row">
        <div class="col-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
          <div class="row">
            <div>
              <div class="header-label pB15">
                {{ $t('common.wo_report.before') }}
              </div>
              <div v-if="beforeImage">
                <div>
                  <img
                    class="custom-file-upload"
                    width="100"
                    height="auto"
                    style="padding: 0px"
                    :src="beforeImage"
                    @click="imgaeBefore(1)"
                  />
                </div>
              </div>
              <div v-else>
                <div class="inputover">
                  <label for="file-upload" class="custom-file-upload">
                    <i class="fa fa-camera" />
                  </label>
                  <input
                    v-if="!doNotSave"
                    id="file-upload"
                    type="file"
                    accept="image/*"
                    @change="filesChange($event.target.files, 1)"
                    :disabled="disabled"
                  />
                  <input
                    v-else
                    id="file-upload"
                    type="file"
                    accept="image/*"
                    :disabled="disabled"
                    @change="marshallFile($event.target.files, 1)"
                  />
                </div>
              </div>
            </div>
            <div class="mL30">
              <div class="header-label pB15" style="padding-left: 0px">
                {{ $t('common.wo_report.after') }}
              </div>
              <div v-if="afterImage">
                <div>
                  <img
                    class="custom-file-upload"
                    width="100"
                    height="auto"
                    style="padding: 0px"
                    :src="afterImage"
                    @click="imgaeBefore(2)"
                  />
                </div>
              </div>
              <div v-else>
                <div class="inputover">
                  <label for="file-upload" class="custom-file-upload">
                    <i class="fa fa-camera" />
                  </label>
                  <input
                    v-if="!doNotSave"
                    id="file-upload"
                    type="file"
                    accept="image/*"
                    @change="filesChange($event.target.files, 2)"
                    :disabled="disabled"
                  />
                  <input
                    v-else
                    id="file-upload"
                    type="file"
                    accept="image/*"
                    @change="marshallFile($event.target.files, 2)"
                    :disabled="disabled"
                  />
                </div>
              </div>
            </div>
          </div>
          <!-- <div class="row">
          <div>
            <div v-if = "!attachments.length" class="inputover">
              <label for="file-upload" class="custom-file-upload">
                <i class = "fa fa-camera text-center"/>
              </label>
              <input id="file-upload" type="file" accept="image/*" @change="filesChange($event.target.files, 1)"/>
            </div>
            <div v-else v-for="(attachment, index) in attachments" style="display:inline-flex">
              <span><img class="custom-file-upload" style="padding:0px;" :src= "attachment.previewUrl" v-if= "attachment.type === 1"/></span>
              <span><img class="custom-file-upload" style="padding:0px;" :src= "attachment.previewUrl" v-if= "attachment.type === 2"/></span>
            </div>
          </div>
        </div> -->
        </div>
      </div>
    </div>
    <preview-file
      :visibility.sync="visibility"
      v-if="visibility && selectedAttachment"
      showDelete="true"
      :previewFile="selectedAttachment"
      :files="attachments"
      @onDelete="taskPhotoDletion"
    ></preview-file>
  </div>
</template>
<script>
import PreviewFile from '@/PreviewFile'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['module', 'record', 'disabled', 'doNotSave'],
  data() {
    return {
      loading: true,
      before: false,
      after: null,
      status: {
        UPLOADING: 1,
        SUCCESS: 2,
        FAILED: 3,
      },
      beforeImage: null,
      afterImage: null,
      attachments: [],
      formFieldName: 'attachment',
      attachmentType: -1,
      selectedAttachment: null,
      visibility: false,
      afterImageDetail: {},
      beforeImageDetail: {},
    }
  },
  watch: {
    record: function(recordObj) {
      this.loadAttachments()
    },
    attachments: function(newVal) {
      this.record.noOfAttachments = this.attachments.length
    },
  },
  components: {
    PreviewFile,
  },
  mounted() {
    this.loadAttachments()
  },
  methods: {
    taskPhotoDletion(file) {
      let dialogObj = {
        title: 'Delete Task Attachment',
        message: 'Are you sure you want to delete this attachment?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          let url = `/v2/tasks/deleteTaskAttachment`
          let { id } = file
          let params = {
            recordId: this.record.id,
            attachmentId: [id],
            module: 'taskattachments',
          }

          API.post(url, params).then(({ data, error }) => {
            if (error) {
              this.$message.error(error.message || 'Error Occured')
            } else {
              if (file.type === 1) {
                this.beforeImage = null
              } else if (file.type === 2) {
                this.afterImage = null
              }
              this.$emit('taskattachmentactivity')
              this.visibility = false
            }
          })
        }
      })
    },
    marshallFile(fileList, attType) {
      let attachmentInfo = {
        module: this.module,
        recordId: this.record.id,
        attachmentType: attType,
        formFieldName: this.formFieldName,
        file: fileList[0],
        fileName: fileList[0].name,
      }

      let picReader = new FileReader()
      picReader.addEventListener('load', event => {
        let picFile = event.target
        if (attType === 1) {
          this.beforeImage = picFile.result
        } else if (attType === 2) {
          this.afterImage = picFile.result
        }
      })
      picReader.readAsDataURL(fileList[0])
      this.$emit('marshalled', attachmentInfo)
    },
    loadAttachments() {
      if (!this.record && !this.record.id) {
        return false
      }
      let self = this
      self.loading = true
      return this.$http
        .get(
          '/attachment?module=' + this.module + '&recordId=' + this.record.id
        )
        .then(function(response) {
          if (response.status === 200) {
            self.loading = false
            self.attachments = response.data.attachments
              ? response.data.attachments
              : []
            if (self.attachments.length > 0) {
              for (let attachment of self.attachments) {
                if (attachment.type === 1) {
                  self.beforeImage = attachment.previewUrl
                } else if (attachment.type === 2) {
                  self.afterImage = attachment.previewUrl
                }
                self.setAttachmentDetail(attachment)
              }
            } else {
              self.beforeImage = null
              self.afterImage = null
            }
          }
        })
    },
    imgaeBefore(type) {
      if (this.attachments.length > 0) {
        this.visibility = true
        for (let attachment of this.attachments) {
          if (type === attachment.type) {
            this.selectedAttachment = attachment
            this.setAttachmentDetail(attachment)
          }
        }
      }
    },
    filesChange(fileList, attType) {
      let self1 = this
      const formData = new FormData()
      formData.append('module', this.module)
      formData.append('recordId', this.record.id)
      formData.append('attachmentType', attType)
      formData.append(this.formFieldName, fileList[0], fileList[0].name)
      let picReader = new FileReader()
      picReader.addEventListener('load', function(event) {
        let picFile = event.target
        if (attType === 1) {
          self1.beforeImage = picFile.result
          self1.beforeImageDetail = {}
        } else if (attType === 2) {
          self1.afterImage = picFile.result
          self1.afterImageDetail = {}
        }
      })
      picReader.readAsDataURL(fileList[0])
      let self = this
      self.$http
        .post('/attachment/add', formData)
        .then(function(response) {
          self.$emit('taskattachmentactivity')
          if (response.status === 200) {
            self.$message.success('Attachment added successfully')
            let { data } = response || {}
            let { attachments } = data || []
            if (!isEmpty(attachments)) {
              attachments.forEach(attachment => {
                self.attachments.push(attachment)
                self.setAttachmentDetail(attachment)
              })
            }
            // window.location.reload()
          }
        })
        .catch(function() {
          self.$message.error('Error in adding attachment')
        })
    },
    setAttachmentDetail(attachment) {
      if (attachment.type == 1) {
        this.beforeImageDetail = attachment
      }
      if (attachment.type == 2) {
        this.afterImageDetail = attachment
      }
    },
  },
}
</script>
