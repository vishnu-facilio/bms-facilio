<template>
  <div class="d-flex ">
    <div v-if="canShowAvatar" class="f13 secondary-color comment-by mR5">
      <avatar class="pT8" size="md" :user="$account.user"></avatar>
    </div>
    <div class="notes-input-container d-flex flex-direction-column flex-grow">
      <NotesRichText
        ref="notesEditor"
        v-model="comment.bodyHTML"
        :placeholder="placeHolder"
        :parentModule="CommentService.parentModule"
        :peopleFromRecordFields="CommentService.peopleFromRecordFields"
        :charLimit="charLimit"
        :expandTextBox="expandTextBox"
        :showAttachmentsPreview="showAttachmentsPreview"
        @focus="triggerFocus"
        @input="inputHandler"
        class="comment-box-input"
        :style="{ minHeight }"
      >
        <template v-slot:attachments>
          <div
            v-if="showAttachmentsPreview"
            class="comment-attachment-preview flex-middle flex-wrap flex-shrink"
          >
            <el-tag
              v-for="(attachment, index) in filteredAttachments"
              :key="`uploaded-${index}`"
              size="medium"
              color="#efeff5"
              type="info"
              :disable-transitions="true"
              @close="removeAttachment(index)"
              closable
            >
              {{ attachment.fileName }}
            </el-tag>

            <el-tag
              v-for="(attachment, index) in uploadingFileList"
              :key="`uploading-${index}`"
              :disable-transitions="true"
              size="medium"
              color="#efeff5"
              type="info"
            >
              {{ attachment.name }} <i class="el-icon-loading"></i>
            </el-tag>

            <el-tag
              v-for="(attachment, index) in failedFileList"
              :key="`failed-${index}`"
              :disable-transitions="true"
              size="medium"
              type="danger"
              @close="removeFailedAttachments(index)"
              closable
            >
              {{ attachment.name + ` failed` }}
            </el-tag>

            <el-tag
              v-if="canShowViewMoreAttachments"
              @click="showMoreAttachments = true"
              :disable-transitions="true"
              size="medium"
              color="#efeff5"
              class="clickable"
              type="info"
            >
              {{ `+${hiddenAttachmentsCount}` }}
            </el-tag>
          </div>
        </template>
      </NotesRichText>

      <template v-if="this.markdownshow">
        <markdown-help-list :markdownshow.sync="markdownshow">
        </markdown-help-list>
      </template>
      <div
        class="fc-menu-row flex-center-row-space comment-area-footer"
        v-if="expandTextBox"
      >
        <div class="fc-menu-col">
          <div class="mention-buttons fc-align-center-column">
            <el-tooltip
              placement="top"
              trigger="hover"
              :open-delay="500"
              popper-class="comment-message-tooltip f12 p6"
              content="Mention People"
            >
              <fc-icon
                @click="peopleMentionButton"
                color="#ababb4"
                group="text-edit"
                name="at"
              ></fc-icon>
            </el-tooltip>
          </div>

          <div class="mention-buttons fc-align-center-column">
            <el-tooltip
              placement="top"
              trigger="hover"
              :open-delay="500"
              popper-class="comment-message-tooltip f12 p6"
              content="Mention Record"
            >
              <fc-icon
                @click="recordMentionButton"
                group="text-edit"
                color="#ababb4"
                name="record-mention"
              ></fc-icon>
            </el-tooltip>
          </div>
          <label>
            <input
              class="hide"
              type="file"
              multiple
              @change="filesChange($event.target.files)"
            />
            <div class="mention-buttons fc-align-center-column">
              <el-tooltip
                placement="top"
                trigger="hover"
                :open-delay="500"
                popper-class="comment-message-tooltip f12 p6"
                content="Attach Files"
              >
                <fc-icon
                  color="#ababb4"
                  group="text-edit"
                  name="attachment"
                ></fc-icon>
              </el-tooltip>
            </div>
          </label>

          <div
            @click="markdownhelplist"
            class="mention-buttons fc-align-center-column"
          >
            <el-tooltip
              placement="top"
              trigger="hover"
              :open-delay="500"
              popper-class="comment-message-tooltip f12 p6"
              content="Markdowns"
            >
              <fc-icon
                color="#ababb4"
                size="22"
                group="text-edit"
                name="markup"
              ></fc-icon>
            </el-tooltip>
          </div>
        </div>
        <div class="flex-middle pT10 pB10">
          <div
            class="fc-menu-col"
            v-if="CommentService.canShowSharing && !isReply"
          >
            <el-tooltip
              placement="top"
              trigger="hover"
              :open-delay="500"
              popper-class="comment-message-tooltip f12 p6"
              content="Comment Sharing"
            >
              <SharingPopover
                v-model="commentSharing"
                :apps="CommentService.portalApps"
              >
                <div
                  class="comment-visibilty-btn visibilty-public flex-middle"
                  v-if="commentSharing.length"
                >
                  <fc-icon
                    group="default"
                    color="#0053cc"
                    name="eye-open"
                  ></fc-icon>
                  {{ $t('common._common.public') }}
                </div>
                <div
                  v-else
                  class="comment-visibilty-btn visibilty-public flex-middle"
                >
                  <fc-icon
                    group="default"
                    color="#0053cc"
                    name="eye-slashed"
                  ></fc-icon>
                  {{ $t('common._common.private') }}
                </div>
              </SharingPopover>
            </el-tooltip>
          </div>
          <div v-if="!isFormComments" class="fc-menu-col">
            <el-button
              :class="[expandTextBox ? 'show' : 'hide']"
              class="markdown-comment-btn el-button--primary "
              @click="saveNote"
              :loading="isAddingNotes"
            >
              {{ saveNoteBtn }}
            </el-button>
          </div>
          <div
            v-if="!isFormComments"
            class="fc-menu-col"
            style="margin-right: 25px !important;"
          >
            <el-button
              :class="[expandTextBox ? 'show' : 'hide']"
              class="markdown-cancel-btn"
              @click="close"
              :disabled="isAddingNotes"
            >
              {{ $t('common._common.cancel') }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Avatar from '@/Avatar'
import { API } from '@facilio/api'
import MarkdownHelpList from '@/relatedlist/MarkdownHelpList'
import NotesRichText from 'src/components/notes/NotesRichText.vue'
import SharingPopover from '@/comments/SharingPopover'
import { isEmpty } from '@facilio/utils/validation'
import Constants from 'util/constant'
import { sanitize } from '@facilio/utils/sanitize'
import { deepCloneObject } from 'util/utility-methods'
import { getCommentService } from 'src/components/notes/CommentUtils.js'

export default {
  components: {
    Avatar,
    MarkdownHelpList,
    NotesRichText,
    SharingPopover,
  },
  inject: {
    CommentService: {
      default: getCommentService(),
    },
    resizeNotesWidget: {
      default: () => {},
    },
  },
  props: {
    value: {
      type: Object,
      default: () => {
        return { bodyHTML: '' }
      },
    },

    referer: {
      type: Object, //user
      default: () => {
        return {}
      },
    },

    parentNote: {
      type: Object,
    },

    placeHolder: {
      type: String,
    },
  },
  created() {
    this.init()
  },
  mounted() {
    if (this.isReply && !isEmpty(this.referer)) {
      this.$refs['notesEditor'].prefillMentionForReply(this.referer)
    }
    if (this.isEdit) {
      this.$refs['notesEditor'].editor?.commands.focus('end')
    }
  },
  data() {
    return {
      markdownshow: false,
      expandTextBox: false,
      isAddingNotes: false,
      canShowAvatar: true,
      showMoreAttachments: false,
      maxAttachmentsToDisplay: 3,
      uploadingFileList: [],
      failedFileList: [],
      commentSharing: [],
      attachments: [],
      comment: { bodyHTML: '' },
    }
  },
  computed: {
    minHeight() {
      let { expandTextBox } = this
      return expandTextBox ? `70px` : `40px`
    },
    isFormComments() {
      return false
    },
    isReply() {
      let { parentNote } = this
      return !isEmpty(parentNote)
    },
    isEdit() {
      let { value } = this
      return !isEmpty(value?.id)
    },
    canShowViewMoreAttachments() {
      let { showMoreAttachments, attachments } = this
      return !showMoreAttachments && attachments?.length > 3
    },
    charLimit() {
      let { CommentService } = this
      return CommentService.forWO || CommentService?.module == 'ticketnotes'
        ? 1800
        : 900
    },
    showAttachmentsPreview() {
      return (
        !isEmpty(this.attachments) ||
        !isEmpty(this.uploadingFileList) ||
        !isEmpty(this.failedFileList)
      )
    },
    filteredAttachments() {
      let attachments = this.$helpers.cloneObject(this.attachments || [])
      return this.showMoreAttachments
        ? attachments
        : attachments.slice(0, this.maxAttachmentsToDisplay)
    },
    hiddenAttachmentsCount() {
      if (this.attachments?.length > this.maxAttachmentsToDisplay) {
        return this.attachments?.length - this.maxAttachmentsToDisplay
      } else return 0
    },
    saveNoteBtn() {
      let { isEdit, CommentService } = this
      if (!isEdit) {
        return CommentService.forWO
          ? this.$t('common._common.comment')
          : this.$t('common._common.add_note_btn')
      } else {
        return CommentService.forWO
          ? this.$t('common._common.edit_comment_btn')
          : this.$t('common._common.edit_note_btn')
      }
    },
  },

  methods: {
    init() {
      let { isReply, isEdit, value } = this
      if (isReply || isEdit) {
        this.expandTextBox = true
        if (isEdit) {
          this.cloneExistingComment(value)
        }
      }
    },
    cloneExistingComment(existingComment) {
      this.comment = deepCloneObject(existingComment)
      this.attachments = deepCloneObject(existingComment?.attachments || [])
      this.commentSharing = deepCloneObject(
        existingComment?.commentSharing || []
      )
    },
    async saveNote() {
      let comment = this.setupNewComment()
      let { CommentService, isEdit } = this
      if (!comment || !comment.bodyHTML || !comment.body) return
      this.isAddingNotes = true
      let { error, data } = await CommentService.save(comment, isEdit)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$emit('save', data)
        this.close()
      }
      this.isAddingNotes = false
    },
    inputHandler() {
      this.resizeNotesWidget()
    },
    markdownhelplist() {
      this.markdownshow = true
    },
    peopleMentionButton() {
      this.$refs['notesEditor'].triggerPeopleMention()
      return null
    },
    recordMentionButton() {
      this.$refs['notesEditor'].triggerSlashMention()
      return null
    },
    async triggerFocus() {
      let { isFormComments } = this
      this.$emit('focus')
      this.expandTextBox = true
      await this.$nextTick()
      if (!isFormComments) this.resizeNotesWidget()
    },

    async filesChange(file) {
      this.isAddingNotes = true
      this.uploadingFileList = Array.from(file)

      if (!this.uploadingFileList.length) return

      let fileAddlist = []
      this.uploadingFileList.forEach(async rt => {
        fileAddlist.push(this.addFile(rt))
      })

      await Promise.all(fileAddlist).then(() => (this.isAddingNotes = false))
    },
    async addFile(file) {
      let { CommentService } = this
      let { parentModule, record } = CommentService
      const formData = new FormData()
      formData.append('fileContent', file)
      await API.post(
        `/v2/files/${parentModule}/addCommentAttachments/${record.id}`,
        formData
      ).then(({ data, error }) => {
        let { name, size } = file || {}
        let attachmentIdx = this.uploadingFileList.findIndex(
          a => a.name === name && a.size === size
        )

        if (!isEmpty(attachmentIdx)) {
          this.uploadingFileList.splice(attachmentIdx, 1)
        }

        if (error) {
          this.failedFileList.push(file)
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.uploadingfile = null
          let attachmentIndexMap = {}
          let fileEntry = {
            attachmentId: -1,
            fileId: data.fileInfo.fileId,
            fileName: file.name,
            fileSize: file.size,
            contentType: file.type,
            error: null,
            previewUrl: null,
            type: 1,
          }
          this.attachments.push(fileEntry)
          attachmentIndexMap[0 + ''] = this.attachments.length - 1
        }
      })
    },
    extractMentions() {
      let editor = this.$refs['notesEditor']?.editor || {}
      let mentions = []
      editor.getJSON()?.content?.forEach(element => {
        element?.content?.forEach(item => {
          if (item?.type === 'atMention' || item?.type === 'slashMention') {
            let MentionDetails = item?.attrs?.id.split('~')
            let MentionSource = MentionDetails[0]
            let recordId = MentionDetails[1]
            if (isNaN(MentionDetails[1])) {
              return
            }
            let mention = {
              mentionedRecordId: parseInt(recordId),
            }

            if (MentionSource in Constants.MENTIONS_TYPE_HASH) {
              this.$set(
                mention,
                'mentionType',
                Constants.MENTIONS_TYPE_HASH[MentionSource]
              )
            } else {
              this.$set(
                mention,
                'mentionType',
                Constants.MENTIONS_TYPE_HASH[Constants.SUGGESTIONS_TYPE.RECORD]
              )
              this.$set(mention, 'mentionedModuleName', MentionSource)
            }
            mentions.push(mention)
          }
        })
      })
      return mentions
    },

    removeAttachment(index) {
      this.attachments.splice(index, 1)
    },
    removeFailedAttachments(index) {
      this.failedFileList.splice(index, 1)
    },
    close() {
      let { isEdit, isReply } = this
      if (isEdit || isReply) {
        this.$emit('close')
      } else {
        this.expandTextBox = false
        this.resetComment()
        this.resizeNotesWidget()
      }
    },
    resetComment() {
      let { CommentService, referer, isEdit, isReply } = this
      let editor = this.$refs['notesEditor']?.editor
      if (editor) {
        editor.commands.clearContent()
      }
      this.comment = {}
      this.attachments = []
      this.failedFileList = []
      this.uploadingFileList = []
      this.init()
      if (isReply && !isEmpty(referer)) {
        this.$refs['notesEditor'].prefillMentionForReply(referer)
      } else if (!isEdit && CommentService.canShowSharing) {
        this.commentSharing = CommentService.defaultSharingApps
      }
    },
    setupNewComment() {
      let {
        comment,
        parentNote,
        attachments,
        commentSharing,
        extractMentions,
      } = this

      let editor = this.$refs['notesEditor']?.editor || {}

      if (!isEmpty(editor)) {
        if (!isEmpty(parentNote)) {
          comment.parentNote = parentNote
        }
        comment.body = editor.getText()
        comment.bodyHTML = sanitize(editor.getHTML())
        comment.mentions = extractMentions()
        comment.attachments = attachments
        comment.commentSharing = commentSharing
      }
      return comment
    },
  },
}
</script>
<style scoped lang="scss">
.notes-input-container {
  border: 1px solid #e5e5ea;
  border-radius: 4px;
}
.comment-box-input {
  resize: none;
  outline: none;
  width: 100%;
  transition-timing-function: ease-in;
  transition: 0.3s;
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  margin-top: 0;
  height: auto;
  letter-spacing: 0.5px;
  width: 100% !important;
  border: none;
}

.visibilty-public {
  border: 0px;
  background: #fff;
  color: #0053cc;
  font-size: 13px;
  font-style: italic;
  cursor: pointer;
  border-radius: 3px;
  padding: 5px 7px 4px 7px;
}
.visibilty-public:hover {
  opacity: 1;
}

.sharing-icon-show,
.sharing-icon-hide {
  top: 4px;
  margin-left: 5px;
  color: #8ca1ad;
}
.sharing-icon-show:hover,
.sharing-icon-hide svg:hover {
  color: #0053cc;
}
.sharing-icon-choser {
  fill: #0053cc;
  width: 15px;
  padding-right: 20px;
}
.fc-menu-row .fc-menu-col {
  -moz-box-sizing: border-box;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}
.fc-menu-row {
  width: 100%;
  overflow: hidden;
}
.fc-menu-col {
  display: flex;
  flex-direction: row;
  float: left;
  margin-right: 15px;
}

.comment-visibilty-btn:hover {
  background: rgba(202, 212, 216, 0.3);
  opacity: 0.8;
}

.fc-comment-row:hover {
  opacity: 1;
}
</style>
<style lang="scss">
.comment-attachment-preview {
  width: initial;

  .el-tag {
    margin: 3px 5px;
    color: #324056;
    border: none;
    border-radius: 4px;
  }
  .el-tag.el-tag--info .el-tag__close {
    color: #324056;
  }
  .el-tag.el-tag--danger .el-tag__close {
    color: #324056;
  }
}
.mention-buttons {
  margin-left: 15px;
  background: white;
  height: 30px;
  width: 30px;
  border-radius: 50%;
  cursor: pointer;
  &:hover {
    opacity: 1;
    background: rgba(202, 212, 216, 0.3) !important;
  }
}

.comment-area-footer {
  border-top: 1px solid #e5e5ea;
}
</style>
