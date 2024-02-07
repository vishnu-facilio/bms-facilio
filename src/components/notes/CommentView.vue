<template>
  <div class="d-flex flex-direction-column comment-view">
    <CommentInput
      v-if="showEdit"
      :value="comment"
      :isEdit="showEdit"
      @save="emitUpdate"
      @close="showEdit = false"
    >
    </CommentInput>
    <div
      v-else
      class="fc-commentview-row d-flex flex-direction-row visibility-visible-actions position-relative"
    >
      <div class="f13 secondary-color comment-by mR5">
        <avatar
          size="md"
          :user="
            comment.createdBy && comment.createdBy.id
              ? comment.createdBy
              : { id: -1, name: 'Unknown' }
          "
        ></avatar>
      </div>
      <div class="comment-time width100">
        <div class="flex-middle flex-row mT8 mB8">
          <span
            v-if="comment.createdBy && comment.createdBy.id"
            class="fc-commentview-title-14"
            >{{ comment.createdBy.name }}</span
          >
          <div class="comments-dot comments-dot-grey-color"></div>
          <el-tooltip
            placement="top"
            trigger="hover"
            :open-delay="500"
            :content="comment.createdTime | formatDate"
            popper-class="comment-message-tooltip f12 p6"
          >
            <span class="fc-comment-subtext-12">
              {{ comment.createdTime | fromNow }}
            </span>
          </el-tooltip>
        </div>
        <div
          class="markdown-style fc-commentview-text-14 mB8"
          v-if="comment.bodyHTML != null"
          v-html="comment.bodyHTML"
        ></div>

        <CommentAttachments
          v-if="canShowAttachments"
          :attachments="comment.attachments"
          class="comment-attachments-preview mb5"
        ></CommentAttachments>
        <div
          class="comment-options d-flex"
          :class="{
            mB8: canShowCommentBar || canShowReplies,
          }"
        >
          <div
            class="comment-reply-button pL0"
            @click="showOrHideReply"
            @mouseover="replyBtnHover = true"
            @mouseleave="replyBtnHover = false"
          >
            <span class="flex-middle">
              <fc-icon
                group="communication"
                class="mR3"
                name="reply"
                :color="replyBtnColor"
                size="18"
              ></fc-icon>

              <span class="reply-btn">{{ 'Reply' }}</span></span
            >
          </div>

          <div
            v-if="CommentService.canShowSharing && !isReply"
            class="comments-dot comments-dot-light-grey-color"
          ></div>
          <el-button
            class="comment-reply-button flex-middle"
            v-if="CommentService.canShowSharing && !isReply"
          >
            <SharingPopover
              v-model="comment.commentSharing"
              class="comment-sharing-btn"
              style="display: inline-block;"
              :apps="CommentService.portalApps"
              :disable="!comment.editAvailable"
              @change="() => updateCommentSharing(comment)"
              v-if="CommentService.canShowSharing && !isReply"
            >
              <el-tooltip
                placement="top"
                :open-delay="500"
                popper-class="comment-message-tooltip f12 p6"
              >
                <div slot="content">
                  <div
                    v-if="
                      !comment.commentSharing || !comment.commentSharing.length
                    "
                  >
                    <span>{{ 'Private' }}</span>
                  </div>
                  <div v-else>
                    <span>{{ 'Public:' }}</span>
                    <br />
                    <div
                      v-for="sharing in comment.commentSharing"
                      :key="sharing.appId"
                    >
                      <span>{{ getAppName(sharing.appId) }}<br /></span>
                    </div>
                  </div>
                </div>

                <fc-icon
                  v-if="canShowSharingIcon && isPublicComment"
                  group="default"
                  :color="sharingIconColor"
                  class="mT2"
                  size="17.5"
                  name="eye-open"
                  @mouseover="sharingIconHover = true"
                  @mouseleave="sharingIconHover = false"
                ></fc-icon>
                <fc-icon
                  v-else-if="canShowSharingIcon && !isPublicComment"
                  group="default"
                  :color="sharingIconColor"
                  class="mT2"
                  size="17.5"
                  name="eye-slashed"
                  @mouseover="sharingIconHover = true"
                  @mouseleave="sharingIconHover = false"
                ></fc-icon>
              </el-tooltip> </SharingPopover
          ></el-button>
        </div>
        <div class="reply-thread-container">
          <div v-if="canShowCommentBar || canShowReplies" class="reply-thread">
            <div v-if="canShowCommentBar" class="pL10 pT10 pB10">
              <CommentInput
                ref="reply-input"
                :parentNote="comment"
                :placeHolder="$t('common._common.add_a_reply')"
                :referer="refererForReply"
                @save="createHandler"
                @close="showOrHideReply"
              >
              </CommentInput>
            </div>

            <div class="text-center" v-if="loading">
              <spinner :show="loading"></spinner>
            </div>
            <div v-else-if="canShowReplies">
              <RecursiveComment
                v-for="(reply, replyIndex) in filteredReplies"
                :key="reply.id"
                :comment="reply"
                :currentLevel="currentLevel + 1"
                :index="replyIndex"
                :woDescriptiontranslate="woDescriptiontranslate"
                @edit="editHandler"
                @delete="deleteHandler"
                @replyParent="showOrHideReply"
                class="pL10"
              ></RecursiveComment>
            </div>
          </div>
          <div>
            <span
              v-if="showRepliesButton"
              @click="viewMoreReplies"
              class="view-more-replies fc-text-underline pointer pT5 pB5"
            >
              {{ repliesExpandText }}
            </span>
          </div>
        </div>
        <InstantTranslate
          class="fc-wo-comments-translate"
          v-if="woDescriptiontranslate"
          :key="comment.id"
          :content="comment.bodyText"
        ></InstantTranslate>
      </div>
      <div class="comment-more-options position-absolute top-0 right-0">
        <div v-if="(comment.createdBy || {}).id === $account.user.id" class="">
          <el-dropdown
            class="delete-dropdown"
            trigger="click"
            @command="EditOrDeleteNote($event, comment, index)"
          >
            <span class="el-dropdown-link">
              <i
                class="el-icon-more pointer fc-grey6"
                style="padding: 8px;"
              ></i>
            </span>
            <el-dropdown-menu slot="dropdown" class="p0">
              <el-dropdown-item
                command="edit"
                class="el-dropdown-comments-delete"
                ><div>
                  {{ $t('common._common.edit') }}
                </div></el-dropdown-item
              >
              <el-dropdown-item
                command="delete"
                class="el-dropdown-comments-delete"
                ><div>
                  {{ $t('common._common.delete') }}
                </div></el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Avatar from '@/Avatar'
import CommentInput from 'src/components/notes/CommentInput.vue'
import { eventBus } from '@/page/widget/utils/eventBus'
import InstantTranslate from 'src/components/InstantTranslate'
import { isEmpty, areValuesEmpty } from '@facilio/utils/validation'
import SharingPopover from '@/comments/SharingPopover'
import PeoplePopover from 'src/components/notes/PeoplePopover.vue'
import _ from 'lodash'
import Vue from 'vue'
import CommentAttachments from 'src/components/notes/CommentAttachments.vue'
import { mapState, mapGetters } from 'vuex'
import tippy from 'tippy.js'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  name: 'RecursiveComment',
  props: ['comment', 'index', 'currentLevel', 'woDescriptiontranslate'],
  components: {
    Avatar,
    InstantTranslate,
    SharingPopover,
    CommentInput,
    CommentAttachments,
  },
  inject: ['CommentService', 'resizeNotesWidget'],
  mounted() {
    this.init()
  },

  destroyed() {
    this.removeEventBusListeners()
    this.removeClickEventForMentions()
  },
  data() {
    return {
      canShowReplyInput: false,
      peoplePopoverVisible: false,
      canShowReplies: false,
      showEdit: false,
      repliesCount: 0,
      replies: [],
      loading: false,
      refererForReply: null,
      replyBtnHover: false,
      sharingIconHover: false,
      peoplePopoverReference: null,
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
    ...mapGetters(['getCurrentUser']),

    isCustomModule() {
      //temp evaluation
      let { CommentService } = this
      let { parentModule } = CommentService
      if (
        (this.$route.meta.moduleType &&
          this.$route.meta.moduleType === 'custom') ||
        parentModule.startsWith('custom_')
      ) {
        return true
      }
      return false
    },
    canShowCommentBar() {
      let { currentLevel, canShowReplyInput } = this
      return currentLevel === 1 && canShowReplyInput
    },
    showRepliesButton() {
      let { repliesCount, currentLevel, canShowReplies } = this
      return repliesCount > 0 && currentLevel === 1 && !canShowReplies
    },
    isReply() {
      let { currentLevel } = this
      return currentLevel > 1
    },
    isPublicComment() {
      let { comment } = this
      return comment.commentSharing && comment.commentSharing.length
    },
    canShowAttachments() {
      return isEmpty(this.comment?.attachments) ||
        !this.comment?.attachments.length > 0
        ? false
        : true
    },
    canShowSharingIcon() {
      let { comment, isReply } = this
      return comment.editAvailable && !isReply
    },
    replyBtnColor() {
      let { replyBtnHover } = this
      return replyBtnHover ? '#324056' : '#90959c'
    },
    sharingIconColor() {
      let { sharingIconHover } = this
      return sharingIconHover ? '#324056' : '#90959c'
    },
    filteredReplies() {
      let { replies } = this
      if (isEmpty(replies) || areValuesEmpty(replies)) {
        return []
      } else {
        let filteredComments = this.$helpers
          .cloneObject(replies || [])
          .sort((a, b) => b.createdTime - a.createdTime)
        return filteredComments
      }
    },

    repliesExpandText() {
      let { canShowReplies, repliesCount } = this
      if (canShowReplies) {
        return 'Hide replies'
      } else {
        return repliesCount > 0
          ? repliesCount === 1
            ? `View 1 reply`
            : `View ${repliesCount} Replies`
          : 'View replies'
      }
    },
  },

  methods: {
    init() {
      this.setEventBusListeners()
      this.repliesCount = this.comment?.replyCount
      this.addClickEventForMentions()
      this.initiateTippyrForMentions()
    },
    setEventBusListeners() {
      eventBus.$on('close-reply-text-area', id => {
        if (id != this.commentViewId) {
          this.canShowReplyInput = false
        }
      })
    },
    removeEventBusListeners() {
      eventBus.$off('close-reply-text-area')
    },
    initiateTippyrForMentions() {
      tippy('.' + this.$constants.PEOPLE_MENTION_CLASS, {
        arrow: true,
        allowHTML: true,
        placement: 'bottom',
        // trigger: 'click',
        trigger: 'mouseenter',
        onShow: instance => {
          const attributeValue = instance.reference.getAttribute('data-id')
          let people = this.getPeopleForAttribute(attributeValue)
          if (!people) {
            return
          }
          const ToastComponent = Vue.extend(PeoplePopover)
          const PopoverInstance = new ToastComponent({
            propsData: {
              people,
              users: this.users,
            },
          })
          PopoverInstance.$mount()
          instance.setContent(PopoverInstance.$el)
        },
      })
    },
    getPeopleForAttribute(attributeValue) {
      let MentionDetails = attributeValue.split('~')
      let recordId, ppl
      if (!isNaN(MentionDetails[1])) {
        recordId = parseInt(MentionDetails[1])
      }
      if (recordId) {
        ppl = this.getPeopleForId(recordId)
      }
      return ppl
    },
    getPeopleForId(peopleId) {
      let { comment } = this
      let { mentions } = comment || []
      if (!isEmpty(mentions)) {
        let mention = mentions.find(
          mention => mention?.recordObj?.id === peopleId
        )
        if (!isEmpty(mention?.recordObj)) {
          return mention.recordObj
        }
      }
      return null
    },
    async showOrHideReply(command) {
      let {
        currentLevel,
        canShowReplyInput,
        comment,

        canShowReplies,
        showRepliesButton,
      } = this
      if (currentLevel != 1) {
        command = {
          action: 'open',
          referer: comment.createdBy,
        }
        this.$emit('replyParent', command)
      } else {
        let showReply = !canShowReplyInput
        if (command?.action === 'open') {
          showReply = true
        }
        if (showReply) {
          let referer = !isEmpty(command.referer)
            ? command.referer
            : comment.createdBy

          this.refererForReply =
            referer.peopleId == this.getCurrentUser().peopleId ? null : referer
          await this.$nextTick()
          this.$refs['reply-input']?.resetComment()
          this.closeOtherCommentInputs()
        }
        this.canShowReplyInput = showReply
        if (showRepliesButton && !canShowReplies) {
          this.viewMoreReplies()
        }
        this.resizeNotesWidget()
      }
    },
    closeOtherCommentInputs() {
      let { commentViewId } = this
      eventBus.$emit('shrink-comment-text-area')
      eventBus.$emit('close-reply-text-area', commentViewId)
    },
    createHandler(note) {
      this.replies.push(note)
      this.repliesCount = this.replies.length
    },
    emitUpdate(note) {
      this.$emit('edit', note)
    },
    editHandler(note) {
      let { replies } = this
      let existingNote = replies.find(r => r.id === note.id)
      if (existingNote) {
        this.replies.splice(replies.indexOf(existingNote), 1, note)
      }
    },
    deleteHandler(note) {
      let { replies } = this
      let doNote = replies.find(r => r.id === note.id)
      if (doNote) {
        this.replies.splice(replies.indexOf(doNote), 1)
        this.repliesCount = replies.length
      }
    },
    updateCommentSharing(comment) {
      this.$emit('updateCommentSharing', comment)
    },
    commentViewId() {
      return _.uniqueId('comment_view')
    },
    getAppName(appId) {
      let { CommentService } = this
      if (CommentService.portalApps) {
        let app = CommentService.portalApps.find(a => a.id == appId)
        if (app) {
          return app.name
        }
      }
      return ''
    },

    async loadReplies() {
      let { CommentService, comment } = this
      this.loading = true
      let { data, error } = await CommentService.fetchComments(comment)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.replies = data
        this.repliesCount = this.replies.length
      }
      this.loading = false
      this.resizeNotesWidget()
    },
    async EditOrDeleteNote(command, note) {
      if (command === 'delete') {
        let success = await this.deleteNote(note)
        if (success) {
          this.$emit('delete', note)
        }
      }
      if (command === 'edit') {
        this.closeOtherCommentInputs()
        this.showEdit = true
      }
    },
    async deleteNote(note) {
      let { promptDelete } = this
      let isDelete = await promptDelete()
      if (!isDelete) {
        return false
      } else {
        let { error } = this.CommentService.delete(note)
        if (error) {
          let { message } = error
          this.$message.error(message)
          return false
        } else {
          this.$message.success(
            this.$t('common.products.note_deleted_successfully')
          )
          return true
        }
      }
    },
    async promptDelete() {
      let value = await this.$dialog.confirm({
        title: this.$t('common.header.delete_note'),
        message: this.$t(
          'common.header.are_you_sure_you_want_to_delete_this_note'
        ),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })
      return value ? true : false
    },

    viewMoreReplies() {
      let { replies } = this
      this.canShowReplies = true
      if (isEmpty(replies)) {
        this.loadReplies()
      }
      this.resizeNotesWidget()
    },

    addClickEventForMentions() {
      this.$nextTick(() => {
        let recordmentionelements = this.$el.getElementsByClassName(
          this.$constants.RECORD_MENTION_CLASS
        )

        if (recordmentionelements && recordmentionelements.length) {
          let lengthofEl = recordmentionelements.length
          for (let i = 0; i < lengthofEl; i++) {
            recordmentionelements[i].addEventListener('click', () => {
              this.redirectToOverview(recordmentionelements[i])
            })
          }
        }
      })
    },
    removeClickEventForMentions() {
      this.$nextTick(() => {
        let recordmentionelements = this.$el.getElementsByClassName(
          this.$constants.RECORD_MENTION_CLASS
        )

        if (recordmentionelements && recordmentionelements.length) {
          let lengthofEl = recordmentionelements.length
          for (let i = 0; i < lengthofEl; i++) {
            recordmentionelements[i].removeEventListener('click', () => {
              this.redirectToOverview(recordmentionelements[i])
            })
          }
        }
      })
    },
    redirectToOverview(el) {
      let { isCustomModule } = this
      let route
      let mentionId = el.getAttribute('data-id')
      if (isEmpty(mentionId) || !mentionId.includes('~')) {
        return
      }
      let mentionDetails = mentionId.split('~')
      let moduleName = mentionDetails[0]
      let recordId = mentionDetails[1]
      if (isNaN(recordId)) {
        return
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          route = this.$router.resolve({
            name,
            params: {
              viewname: 'all',
              id: recordId,
            },
          }).href
        }
      } else {
        let hasPermission = this.$hasPermission(`${moduleName}: READ`)
        if (!hasPermission) {
          return
        }
        let routerMap = {
          workorder: {
            name: 'wosummarynew',
            params: { id: recordId },
          },
          workpermit: {
            name: 'workPermitSummary',
            params: { id: recordId, viewname: 'all' },
          },
          purchaserequest: {
            name: 'prSummary',
            params: { id: recordId, viewname: 'all' },
          },
          purchaseorder: {
            name: 'poSummary',
            params: { id: recordId, viewname: 'all' },
          },
          tenantcontact: {
            name: 'tenantcontact',
            params: { id: recordId, viewname: 'all' },
          },
          tenantunit: {
            name: 'tenantUnitSummary',
            params: { id: recordId, viewname: 'all' },
          },
          vendors: {
            name: 'vendorsSummary',
            params: { id: recordId, viewname: 'all' },
          },
          asset: {
            name: 'assetsummary',
            params: { assetid: recordId, viewname: 'all' },
          },
          tenantspaces: {
            name: 'tenant',
            params: { id: (recordId || {}).id, viewname: 'all' },
          },
          serviceRequest: {
            name: 'serviceRequestSummary',
            params: { id: recordId, viewname: 'all' },
          },
          client: {
            name: 'clientSummary',
            params: { id: recordId, viewname: 'all' },
          },
          vendorcontact: {
            name: 'vendorContactsSummary',
            params: { id: recordId, viewname: 'all' },
          },
          insurance: {
            name: 'insurancesSummary',
            params: { id: recordId, viewname: 'all' },
          },
          quote: {
            path: `/app/tm/quotation/all/${recordId}/overview`,
          },
          item: {
            path: `/app/inventory/item/all/${recordId}/summary`,
          },
          tool: {
            path: `/app/inventory/tool/all/${recordId}/summary`,
          },
          inspectionResponse: {
            path: `/app/inspection/individual/all/summary/${recordId}`,
          },
        }

        if (isCustomModule) {
          route = this.$router.resolve({
            path: `/app/ca/modules/${moduleName}/all/${recordId}/summary`,
          }).href
        } else {
          if (isEmpty(routerMap[moduleName])) {
            return
          }
          route = this.$router.resolve(routerMap[moduleName]).href
        }
      }
      if (route) route && window.open(route, '_blank')
    },
  },
}
</script>

<style scoped lang="scss">
.comment-view {
  padding: 10px 0;

  .pL15 {
    padding-left: 15px;
  }
  .mB8 {
    margin-bottom: 8px;
  }
  .fc-commentview-title-14 {
    font-size: 14px;
    font-weight: 500;
    font-stretch: normal;
    font-style: normal;
    line-height: 1;
    letter-spacing: 0.5px;
    color: #324056;
    font-size: 13px;
  }
  .fc-commentview-text-14 {
    font-size: 14px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: normal;
    letter-spacing: 0.5px;
    color: #324056;
    margin-top: 0;
  }
  .fc-comment-subtext-12 {
    font-size: 12px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: normal;
    letter-spacing: 0.43px;
    color: #90959c;
  }
  .fc-commentview-row {
    .comment-time {
      .comment-options {
        align-items: center;
        .comment-reply-button {
          cursor: pointer;
          display: flex;
          height: 28px;
          padding: 4px 4px;
          font-size: 13px;
          border-radius: 3px;
          border: none;
          color: #90959c;

          .reply-btn {
            display: flex;
            align-items: center;
            line-height: normal;
          }
          &:hover {
            background-color: #cad4d84d;
            color: #324056;
          }
          &:focus {
            background-color: #cad4d84d;
            color: #324056;
          }
        }
      }
    }
    .comment-more-options {
      display: none;
      .delete-dropdown {
        border-radius: 50%;
        width: 30px;
        height: 30px;
        .el-dropdown-menu {
          border-radius: 0;
          border: 1px solid #d0d9e2;
        }
        .el-dropdown-comments-delete {
          font-size: 13px;
          line-height: 3.08;
          letter-spacing: 0.2px;
          border: 1px solid transparent;
          color: #333333;
          width: 144px;
        }
        &:hover {
          background: #edf2f8;
        }
      }
    }
  }
  .reply-thread-container {
    .reply-thread {
      border-left: 0.12rem solid #e5e5ea;
    }
    .view-more-replies {
      font-size: 13px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: normal;
      letter-spacing: normal;
      text-align: center;
      color: #428fc7;
    }
  }
  &:hover > .fc-commentview-row .comment-more-options {
    display: block;
  }

  .comment-time .fa-circle {
    opacity: 0.4;
    color: rgba(140, 161, 173, 0.3);
    font-size: 8px;
    padding-right: 5px;
  }

  .delete-dropdown .el-dropdown-menu {
    border-radius: 0;
    border: 1px solid #d0d9e2;
  }

  .commentborder {
    border-bottom: 1px solid #fafafa;
  }

  .comments-dot {
    margin: 0px 5px 0 10px;
    width: 4px;
    height: 4px;
    border-radius: 100%;

    &.comments-dot-grey-color {
      background-color: #90959c;
    }
    &.comments-dot-light-grey-color {
      opacity: 0.35;
      background-color: #90959c;
    }
  }
  .attachments-dot {
    margin: 0px 3px;
    width: 17px;
    height: 17px;
    border-radius: 100%;
    color: white;
    &.comments-dot-blue-color {
      background-color: #28badc;
    }
  }
  .comment-attachments-preview {
    margin-bottom: 2px;
    width: 96%;
  }
}
</style>
<style lang="scss">
.comment-view {
  .comment-record-mention {
    cursor: pointer;
    &:hover {
      text-decoration: underline;
    }
  }
  .comment-people-mention {
    cursor: pointer;
  }
}
</style>
