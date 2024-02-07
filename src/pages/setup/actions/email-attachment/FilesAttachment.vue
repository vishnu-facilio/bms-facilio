<template>
  <el-dialog
    :visible.sync="visibility"
    width="60%"
    class="fieldchange-Dialog pB15 fc-dialog-center-container fc-dialog-center-body-p0"
    :title="$t('setup.notification.mail_attachment')"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="setup-dialog-lay new-body-modal mT0 pB100 height400">
      <el-form>
        <el-row>
          <el-col :span="24" class="el-select-block">
            <div class="email-attachment">
              {{ $t('setup.notification.attach_file') }}:
            </div>
          </el-col>
        </el-row>
        <el-row class="mT30">
          <DirectFileAttachment
            module="templatefileattachment"
            record=""
            :templateId="templateId"
            :attachmentsList="attachmentsList"
            @removeFileId="removeFileId"
            @addAttachment="addAttachment"
          ></DirectFileAttachment>
        </el-row>
        <el-row class="mT10">
          <el-col :span="24" class="el-select-block">
            <div class="email-attachment">
              {{ $t('setup.notification.add_urls') }}:
            </div>
          </el-col>
        </el-row>
        <el-row>
          <UrlAttachment
            module="templateurlattachment"
            :urlArray.sync="urlArray"
          ></UrlAttachment>
        </el-row>
        <el-row v-if="!$validation.isEmpty(fileFieldsList)" class="mT30">
          <el-col :span="24" class="el-select-block">
            <div class="email-attachment">
              {{ $t('setup.notification.select_file_fields') }}:
            </div>
          </el-col>
        </el-row>
        <el-row v-if="!$validation.isEmpty(fileFieldsList)" class="mT20">
          <FileFieldAttachment
            module="templatefilefieldattachment"
            :fileFieldsList="fileFieldsList"
            :fileFieldIds.sync="fileFieldIds"
          ></FileFieldAttachment>
        </el-row>
      </el-form>
    </div>

    <div class="modal-dialog-footer" style="z-index: 900;">
      <el-button @click="closeDialog()" class="modal-btn-cancel">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button type="primary" class="modal-btn-save" @click="actionSave()">{{
        $t('common._common._save')
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import DirectFileAttachment from 'pages/setup/actions/email-attachment/DirectFileAttachment'
import UrlAttachment from 'pages/setup/actions/email-attachment/UrlAttachment'
import FileFieldAttachment from 'pages/setup/actions/email-attachment/FileFieldAttachment'
import { mapState } from 'vuex'
import { isEmpty } from 'util/validation'

export default {
  props: [
    'visibility',
    'templateId',
    'urlStringList',
    'fileIdsList',
    'attachmentsList',
    'fileFieldIdsList',
  ],
  components: {
    DirectFileAttachment,
    UrlAttachment,
    FileFieldAttachment,
  },
  data() {
    return {
      canShowPreview: true,
      selectedAttachment: null,
      fileFieldsList: [],
      attachments: [],
      urlArray: [],
      fileIds: [],
      fileFieldIds: [],
    }
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      let { urlStringList, fileIdsList, fileFieldIdsList } = this
      this.urlArray = urlStringList || []
      this.fileIds = fileIdsList || []
      this.fileFieldIds = fileFieldIdsList || []
      this.attachments = this.attachmentsList
      let { moduleMeta } = this
      if (!isEmpty(moduleMeta)) {
        let { fields } = moduleMeta
        let fileFields = []
        if (!isEmpty(fields)) {
          fields.forEach(field => {
            let { dataTypeEnum, displayType } = field || {}
            if (
              !isEmpty(displayType) &&
              displayType._name === 'FILE' &&
              !isEmpty(dataTypeEnum) &&
              dataTypeEnum._name === 'FILE'
            ) {
              fileFields.push(field)
            }
          })
          this.fileFieldsList = fileFields
        }
      }
    },
    addAttachment(file) {
      this.addFileId(file.fileId)
    },
    addFileId(fileId) {
      this.fileIds.push(fileId)
    },
    removeFileId(fileId, index) {
      this.fileIds.splice(index, 1)
    },
    handleAttachment() {},
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    actionSave() {
      let { urlArray, fileIds, attachments, fileFieldIds } = this
      this.closeDialog()
      this.$emit('onSave', urlArray, fileIds, attachments, fileFieldIds)
    },
  },
}
</script>
