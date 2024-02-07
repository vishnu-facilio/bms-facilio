<template>
  <div class="fc-comments">
    <!-- <portal-target name="gtranslate-icon" slim> </portal-target> -->
    <div class="row new-comment-area mT20 flex-shrink-0" id="commentBoxPar">
      <div
        class="d-flex flex-direction-column width100"
        style="border: 1px solid #e5e5ea; border-radius: 4px;"
      >
        <div class="d-flex flex-direction-column">
          <textarea
            v-if="rerender"
            ref="commentBoxRef"
            v-model="newComment.body"
            :placeholder="
              title
                ? $t('common._common.add_a_comment')
                : $t('common._common.add_note')
            "
            v-bind:class="{ height70: commentFocus, height38: !commentFocus }"
            class="comment-box"
            style="font-size:13px;"
            @focus="focusCommentBox"
            @input="handleInputEvent($event)"
          />
          <template v-if="this.markdownshow">
            <markdown-help-list :markdownshow.sync="markdownshow">
            </markdown-help-list>
          </template>

          <div
            class="fc-menu-row"
            style="border-top: 1px solid #e5e5ea;"
            v-if="commentFocus"
          >
            <div class="fc-menu-col">
              <div class="markdown-outside">
                <button
                  v-if="commentFocus"
                  @click="markdownhelplist"
                  style="top: 45px; background: white;"
                >
                  <img
                    src="~assets/markdown.svg"
                    class="pull-right pointer"
                    style="width: 21px;"
                  />
                </button>
              </div>
            </div>
            <div style="float:right;">
              <div
                class="fc-menu-col"
                style=" margin-top: 10px !important;"
                v-if="canShowSharing"
              >
                <SharingPopover
                  v-model="newComment.commentSharing"
                  :apps="portalApps"
                >
                  <button
                    class="comment-visibilty-btn visibilty-public"
                    v-if="newComment.commentSharing.length"
                  >
                    <inline-svg
                      src="svgs/comment-visibility-public"
                      iconClass="text-center icon-sm-md"
                      class="vertical-middle sharing-icon-choser"
                    ></inline-svg>
                    {{ $t('common._common.public') }}
                  </button>
                  <button v-else class="comment-visibilty-btn visibilty-public">
                    <inline-svg
                      src="svgs/comment-sharing-off"
                      iconClass="text-center icon-sm-md"
                      class="vertical-middle sharing-icon-choser"
                    ></inline-svg>
                    {{ $t('common._common.private') }}
                  </button>
                </SharingPopover>
              </div>
              <div class="fc-menu-col">
                <el-button
                  :class="[commentFocus ? 'show' : 'hide']"
                  class="markdown-comment-btn el-button--primary "
                  @click="addComment"
                  :loading="isAddingNotes"
                >
                  {{
                    forWO
                      ? $t('common._common.comment')
                      : $t('common._common.add_note_btn')
                  }}
                </el-button>
              </div>
              <div class="fc-menu-col" style="margin-right: 25px !important;">
                <el-button
                  :class="[commentFocus ? 'show' : 'hide']"
                  class="markdown-cancel-btn"
                  @click="closeComment"
                  :disabled="isAddingNotes"
                >
                  {{ $t('common._common.cancel') }}
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- <div v-if="!isServicePortal && showNotifyRequester" style="margin: 8px;">
        <input
          type="checkbox"
          v-model="newComment.notifyRequester"
          name="notifyRequester"
          class="notify-requester-checkbox"
        />
        <label class="notify-req" for="notifyRequester">{{
          $t('common._common.notify')
        }}</label>
      </div> -->
    </div>
    <div class="text-center" v-if="loading">
      <spinner :show="loading"></spinner>
    </div>
    <div
      v-else-if="$validation.isEmpty(comments)"
      class="mT20"
      style="color: #8ca1ad;"
    >
      <div class="d-flex flex-direction-column" style="max-height: 80px">
        <inline-svg
          src="svgs/community-empty-state/comments"
          class="vertical-middle self-center"
          iconClass="icon empty-note mR5 "
        ></inline-svg>
        <div class="empty-notes-desc f13 pL10 self-center">
          {{
            title
              ? $t('common.products.no_mod_available') +
                title +
                $t('common._common.added_yet')
              : $t('common._common.no_notes')
          }}
        </div>
      </div>
    </div>
    <div
      v-else
      class="comment-msg-container flex-grow"
      ref="notes-container"
      style=" padding-top: 0px !important;"
    >
      <div class="fc-visibility-gicon-hide-actions"></div>
      <div
        class="fc-comment-row d-flex flex-direction-row visibility-visible-actions"
        v-for="(comment, index) in filteredComments"
        :key="comment.id"
      >
        <div class="f13 secondary-color comment-by">
          <avatar
            size="md"
            :user="
              comment.createdBy && comment.createdBy.id
                ? comment.createdBy
                : { id: -1, name: 'Unknown' }
            "
          ></avatar>
        </div>
        <div class="comment-time f13 el-col-22">
          <span
            v-if="comment.createdBy && comment.createdBy.id"
            class="inline-block bold f14"
            style="margin-top: 5px;"
            >{{ comment.createdBy.name }}</span
          >
          <div style="flex-grow: 1;">
            <div
              class="markdown-style"
              v-if="comment.bodyHTML != null"
              v-html="comment.bodyHTML"
            ></div>
          </div>
          <div class="comment-options">
            <el-tooltip
              placement="top"
              trigger="hover"
              :content="comment.createdTime | formatDate"
              popper-class="comment-message-tooltip"
            >
              <span class="f12">
                <i class="fa el-icon-time"></i>
                {{ comment.createdTime | fromNow }}
              </span>
            </el-tooltip>
            <SharingPopover
              v-model="comment.commentSharing"
              class="comment-sharing-btn"
              style="display: inline-block;"
              :apps="portalApps"
              :disable="!comment.editAvailable"
              @change="() => updateCommentSharing(comment)"
              v-if="canShowSharing"
            >
              <el-tooltip
                placement="top"
                trigger="hover"
                popper-class="comment-message-tooltip"
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
                <button
                  v-bind:class="
                    comment.editAvailable
                      ? 'visibilty-public'
                      : 'visibilty-private'
                  "
                >
                  <inline-svg
                    :src="
                      comment.commentSharing && comment.commentSharing.length
                        ? 'svgs/comment-visibility-public'
                        : 'svgs/comment-sharing-off'
                    "
                    iconClass="text-center icon icon-sm"
                    v-bind:class="
                      comment.editAvailable
                        ? 'sharing-icon-show'
                        : 'sharing-icon-hide'
                    "
                  ></inline-svg>
                </button>
              </el-tooltip>
            </SharingPopover>
          </div>
          <InstantTranslate
            class="fc-wo-comments-translate"
            v-if="woDescriptiontranslate"
            :key="comment.id"
            :content="comment.bodyText"
          ></InstantTranslate>
        </div>
        <div class="el-col-2 comment-more-options">
          <div
            v-if="(comment.createdBy || {}).id === $account.user.id"
            class="pT10 text-right"
          >
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
              <el-dropdown-menu slot="dropdown" class="sharing-popover">
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
      <div class="pT10" v-if="comments && canShowViewMore">
        <a @click="showAllComments" class="url-field-display flex-shrink-0">{{
          canShowAllComments
            ? forWO
              ? $t('common._common.view_recent_comments')
              : $t('common._common.view_recent_notes')
            : forWO
            ? $t('common._common.view_new') +
              (comments.length - filteredComments.length) +
              $t('common._common.more_comments')
            : $t('common._common.view_new') +
              (comments.length - filteredComments.length) +
              $t('common._common.more_notes')
        }}</a>
      </div>
    </div>
  </div>
</template>

<script>
import { isEmpty, isFunction } from '@facilio/utils/validation'
import BaseComment from '@/base/Comments.vue'
import Avatar from '@/Avatar'
import { mapState } from 'vuex'
import MarkdownHelpList from '@/relatedlist/MarkdownHelpList'
import InstantTranslate from 'src/components/InstantTranslate'
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'
import { API } from '@facilio/api'
import SharingPopover from '@/comments/SharingPopover'
import { getApp } from '@facilio/router'

export default {
  name: 'OldNotes',
  extends: BaseComment,
  components: {
    Avatar,
    MarkdownHelpList,
    InstantTranslate,
    SharingPopover,
  },
  props: [
    'resizeWidget',
    'layoutParams',
    'isActive',
    'title',
    'module',
    'parentModule',
    'forWO',
    'listViewComments',
  ],

  created() {
    this.newComment.commentSharing = []
    eventBus.$on('woDescriptiontranslate', woDescriptiontranslate => {
      this.woDescriptiontranslate = woDescriptiontranslate
    })
    eventBus.$on('tictketnotes_reloadlist', this.loadComments)
  },
  destroyed() {
    eventBus.$off('woDescriptiontranslate', () => {
      this.woDescriptiontranslate = false
    })
  },
  data() {
    return {
      activeComment: false,
      woDescriptiontranslate: false,
      canShowAllComments: this.listViewComments || false,
      visibleCommentCount: 3,
      defaultWidgetHeight: this.$getProperty(this.layoutParams, 'h', null),
      markdownshow: false,
      defaultSharingApps: [],
      sharingPopover: false,
      rerender: true,
    }
  },
  mounted() {
    this.currentUserId = this.getCurrentUser().id
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
    filteredComments() {
      let comments = this.$helpers
        .cloneObject(this.comments || [])
        .sort((a, b) => b.createdTime - a.createdTime)

      let count = comments && comments.length

      return this.canShowAllComments && count > 0
        ? comments
        : comments.slice(0, this.visibleCommentCount)
    },
    canShowViewMore() {
      if (!this.comments) return false
      if (!isEmpty(this.listViewComments) && this.listViewComments) {
        return false
      }
      return this.comments && this.comments.length > this.visibleCommentCount
    },
    fliterTranslateComments() {
      return this.filteredComments.map(({ bodyText }) => bodyText)
    },
    canShowSharing() {
      let {
        appCategory: { PORTALS },
      } = this.$constants
      if (getApp() && getApp().appCategory === PORTALS) {
        return false
      } else {
        return true
      }
    },
  },
  watch: {
    isActive(isActive) {
      if (!isActive && this.canShowAllComments) this.showAllComments()
    },
    filteredComments(list) {
      if (isEmpty(list)) this.commentFocus = true
    },
  },
  methods: {
    EditOrDeleteNote(command, note, index) {
      if (command === 'delete') {
        this.deleteNote(note, index)
      }
    },
    handleInputEvent(e) {
      if (isEmpty(this.newComment.body)) {
        if (!this.canShowAllComments) {
          this.resetCommentBox()
        } else {
          this.resetShowAllCommentBox()
        }
        e.target.style.height = `70px`
      } else {
        this.resize(e)
      }
    },
    resize(e) {
      if (e.target.scrollHeight > 70) {
        // if more than default height
        e.target.style.height = 'auto'
        e.target.style.height = `${e.target.scrollHeight}px`
        this.increaseWidgetSize(e)
      }
    },

    markdownhelplist() {
      this.markdownshow = true
    },
    deleteNote(note) {
      let { module, parentModule } = this
      let obj = {
        noteId: note.id,
        module: module,
        parentModuleName: parentModule,
      }
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_note'),
          message: this.$t(
            'common.header.are_you_sure_you_want_to_delete_this_note'
          ),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          let url = '/v2/notes/delete'
          if (this.$helpers.isLicenseEnabled('THROW_403_WEBTAB')) {
            let { record } = this
            url = `v2/notes/${module}/${parentModule}/delete/${record.id}`
          }
          if (value) {
            this.$http
              .post(url, obj)
              .then(({ data: { message, responseCode = {} } }) => {
                if (responseCode === 0) {
                  this.$message.success(
                    this.$t('common.products.note_deleted_successfully')
                  )

                  let doNote = this.comments.find(r => r.id === note.id)
                  if (doNote) {
                    this.comments.splice(this.comments.indexOf(doNote), 1)
                  }
                } else {
                  throw new Error(message)
                }
              })
          }
        })
    },
    showAllComments() {
      this.canShowAllComments = !this.canShowAllComments
      this.$nextTick(() => {
        if (
          this.canShowAllComments &&
          !isEmpty(this.layoutParams) &&
          isFunction(this.resizeWidget)
        ) {
          let height = this.$refs['notes-container'].scrollHeight + 150
          let width = this.$refs['notes-container'].scrollWidth
          this.resizeWidget({ height, width })
        } else {
          if (isFunction(this.resizeWidget)) {
            this.resizeWidget({ h: this.defaultWidgetHeight })
          }
        }
      })
    },
    increaseWidgetSize(e) {
      this.$nextTick(() => {
        let height =
          this.$refs['notes-container'].scrollHeight +
          e.target.scrollHeight +
          150
        let width = this.$refs['notes-container'].scrollWidth
        if (isFunction(this.resizeWidget)) {
          this.resizeWidget({ height, width })
        }
      })
    },
    focusCommentBox() {
      this.$refs.commentBoxRef.focus()
      this.commentFocus = true
      this.$nextTick(() => {
        let height = this.$refs['notes-container'].scrollHeight + 150
        let width = this.$refs['notes-container'].scrollWidth
        if (isFunction(this.resizeWidget)) {
          this.resizeWidget({ height, width })
        }
      })
    },
    blurCommentBox(e) {
      let itTargetPar = e.path.filter(ele => ele.id === 'commentBoxPar')
      if (
        (!this.newComment.body || this.newComment.body.trim() === '') &&
        !isEmpty(this.comments) &&
        !itTargetPar.length
      ) {
        this.commentFocus = false
      }
    },
    closeComment() {
      if (!this.canShowAllComments) {
        this.resetCommentBox()
      } else {
        this.resetShowAllCommentBox()
      }
      this.commentFocus = false
      this.reset()
    },
    resetCommentBox() {
      this.rerender = false
      this.$nextTick(() => {
        this.rerender = true
        if (isFunction(this.resizeWidget)) {
          this.resizeWidget({ h: this.defaultWidgetHeight + 2 })
        }
      })
    },
    resetShowAllCommentBox() {
      if (isEmpty(this.listViewComments)) {
        this.$nextTick(() => {
          let height = this.$refs['notes-container'].scrollHeight
          let width = this.$refs['notes-container'].scrollWidth
          if (isFunction(this.resizeWidget)) {
            this.resizeWidget({ height, width })
          }
        })
      }
      this.rerender = false
      this.$nextTick(() => {
        this.rerender = true
      })
    },
    getAppName(appId) {
      if (this.portalApps) {
        let app = this.portalApps.find(a => a.id == appId)
        if (app) {
          return app.name
        }
      }
      return ''
    },
    async updateCommentSharing(comment) {
      let existComment = this.comments.find(c => c.id == comment.id)
      let commentIndex = this.comments.indexOf(existComment)
      this.$set(this.comments, commentIndex, comment)
      let param = {
        note: comment,
        module: this.module,
        parentModuleName: this.parentModule,
        noteId: comment.id,
      }
      let { error } = await API.post('v2/notes/updateSharing', param)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success('Comments have been updated')
      }
    },
  },
}
</script>
<style scoped lang="scss">
.view-more {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding: 10px 10px 15px;
  margin-bottom: 20px;
}
.comment-box {
  font-size: 13px;
  letter-spacing: 0.4px;
  color: rgb(51, 51, 51);
  border-color: rgb(208, 217, 226) !important;
  border-radius: 3px;
  margin-right: 30px;
  margin-top: 6px;
  font-size: 13px;
  border: 0px !important;
  margin-left: 16px !important;
  width: 98%;
  line-height: 16px !important;
}
.comment-btn {
  height: 35px;
  margin: 0;
  &:hover {
    background: #33a6b5;
    cursor: pointer;
  }
}
.comment-msg-container,
.new-comment-area {
  width: 100%;
  padding: 0px 0px 15px;
  margin-top: 0px !important;
}
.comment-time .fa-circle {
  opacity: 0.4;
  color: rgba(140, 161, 173, 0.3);
  font-size: 8px;
  padding-right: 5px;
}

.fc-comments {
  padding: 0px 30px 10px;
}
.fc-comments .comment-btn {
  max-width: 175px;
  margin: 18px 0 8px;
  font-size: 11px;
}
.fc-comments .fc-comment-row .comment-body {
  max-width: 100%;
}
.fc-comments .empty-notes-desc {
  font-size: 12px;
  letter-spacing: 0.34px;
  text-align: center;
  color: #b3afc9;
  position: relative;
  bottom: 17px;
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

.visibilty-private {
  border: 0px;
  background: #fff;
  font-size: 12px;
  padding: 0px;
  padding-bottom: 2px;
  padding-right: 2px;
  border-radius: 5px;
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
  display: inline-block;
  float: left;
  margin-top: 10px;
  margin-bottom: 10px;
  margin-right: 15px;
}
.url-field-display {
  color: #46a2bf;
  &:hover {
    text-decoration: underline;
    text-underline-offset: 3px;
    color: #46a2bf;
  }
}
.delete-dropdown {
  border-radius: 50%;
  width: 30px;
  height: 30px;
}
.delete-dropdown:hover {
  background: #edf2f8;
}
.delete-dropdown .el-dropdown-menu {
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
.comment-options {
  margin-top: 6px;
}
.comment-options span {
  color: #8ca1ad;
  cursor: default;
}
.comment-options span:hover {
  color: #0053cc;
}
.sharing-popover {
  padding: 0px;
}
.comment-visibilty-btn:hover {
  background: rgba(202, 212, 216, 0.3);
  opacity: 0.8;
}
.comment-more-options {
  opacity: 0;
}
.fc-comment-row:hover .comment-more-options {
  opacity: 1;
}
</style>
