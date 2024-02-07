<template>
  <div ref="comment-section" class="fc-comments-widget">
    <portal v-if="isActive && groupKey" :to="groupKey + '-topbar'" slim>
      <div class="ex-icon-container" @click="expandNotesWidget()">
        <InlineSvg
          :src="'svgs/expand-icon'"
          iconClass="expand-icon"
        ></InlineSvg>
      </div>
    </portal>
    <CommentInput
      ref="commentInput"
      class="comment-input pT10 pB10"
      :placeHolder="
        forWO
          ? $t('common._common.add_a_comment')
          : $t('common._common.add_note')
      "
      @focus="closeAllReplyInputs"
      @save="createHandler"
    >
    </CommentInput>
    <div>
      <div class="text-center" v-if="loading">
        <spinner :show="loading"></spinner>
      </div>
      <div
        v-else-if="$validation.isEmpty(comments)"
        class="d-flex flex-direction-column text-fc-grey mT20"
      >
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
      <div v-else ref="notes-container">
        <div v-for="(comment, index) in filteredComments" :key="comment.id">
          <CommentView
            class="parent-comments"
            :comment="comment"
            :currentLevel="1"
            :index="index"
            :woDescriptiontranslate="woDescriptiontranslate"
            @edit="editHandler"
            @delete="deleteHandler"
            @updateCommentSharing="updateCommentSharing"
            @refreshComments="loadComments()"
          ></CommentView>
        </div>
        <div v-if="comments && canShowViewMore">
          <a
            @click="showOrHideComments"
            class="show-all-comments fc-text-underline"
            >{{
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
            }}</a
          >
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty, areValuesEmpty, isFunction } from '@facilio/utils/validation'
import { getCommentService } from 'src/components/notes/CommentUtils.js'
import CommentInput from 'src/components/notes/CommentInput.vue'
import CommentView from 'src/components/notes/CommentView.vue'
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'

export default {
  props: [
    'module',
    'record',
    'notify',
    'parentModule',
    'isCustomModule',
    'isServicePortal',
    'setNoOfNotesLength',
    'resizeWidget',
    'layoutParams',
    'isActive',
    'title',
    'forWO',
    'listViewComments',
    'isPopoverView',
    'groupKey',
  ],
  components: {
    CommentInput,
    CommentView,
  },
  data() {
    return {
      CommentService: getCommentService(),
      comments: [],
      loading: true,
      woDescriptiontranslate: false,
      canShowAllComments: this.listViewComments || false,
      visibleCommentCount: 3,
      maxWidgetHeight: 650,
      currentWidgetHeight: 0,
      sharingPopover: false,
      defaultWidgetHeight: this.$getProperty(this.layoutParams, 'h', null),
    }
  },

  async created() {
    await this.init()
  },
  destroyed() {
    this.killActiveEvents()
    this.CommentService.$destroy()
  },

  provide() {
    return {
      CommentService: this.CommentService,
      fetchComments: this.fetchComments,
      resizeNotesWidget: this.resizeNotesWidget,
    }
  },

  computed: {
    filteredComments() {
      let { comments, canShowAllComments, visibleCommentCount } = this
      if (isEmpty(comments) || areValuesEmpty(comments)) {
        return []
      } else {
        let filteredArr = this.$helpers
          .cloneObject(comments)
          .sort((a, b) => b.createdTime - a.createdTime)

        let count = filteredArr && filteredArr.length

        return canShowAllComments && count > 0
          ? filteredArr
          : filteredArr.slice(0, visibleCommentCount)
      }
    },
    showNotifyRequester() {
      let { notify } = this
      if (typeof notify !== 'undefined') {
        return notify
      }
      return true
    },

    canShowViewMore() {
      let {
        comments,
        listViewComments,
        visibleCommentCount,
        isPopoverView,
      } = this
      if (!comments || isPopoverView) return false
      if (!isEmpty(listViewComments) && listViewComments) {
        return false
      }
      return comments && comments.length > visibleCommentCount
    },
    fliterTranslateComments() {
      let { filteredComments } = this
      return filteredComments.map(({ bodyText }) => bodyText)
    },
  },
  watch: {
    isActive(isActive) {
      if (!isActive && this.canShowAllComments) this.showOrHideComments()
    },
    filteredComments(list) {
      if (isEmpty(list)) this.$refs['commentInput'].expandTextBox = true
    },
    record: function() {
      this.loadComments()
    },
    comments: function() {
      if (
        this.record &&
        typeof this.record === 'object' &&
        this.setNoOfNotesLength
      ) {
        this.setNoOfNotesLength(this.comments.length)
      }
    },
  },
  methods: {
    async init() {
      await this.initCommentService()
      await this.initiateEventListeners()
      await this.loadComments()
      if (this.isPopoverView) {
        this.canShowAllComments = true
      }
    },
    async initCommentService() {
      this.prefillCommentSharing()
      this.setupCommentServiceData()
    },
    async prefillCommentSharing() {
      let { CommentService } = this

      await CommentService.getSharingApps()
      if (CommentService.canShowSharing) {
        if (this.$refs['commentInput']) {
          this.$refs['commentInput'].commentSharing =
            CommentService.defaultSharingApps
        }
      }
    },
    setupCommentServiceData() {
      this.CommentService.module = this.module
      this.CommentService.parentModule = this.parentModule
      this.CommentService.record = this.record
      this.CommentService.isServicePortal = this.isServicePortal
      this.CommentService.forWO = this.forWO
      this.CommentService.getPeopleFieldForRecord()
    },

    initiateEventListeners() {
      eventBus.$on('woDescriptiontranslate', woDescriptiontranslate => {
        this.woDescriptiontranslate = woDescriptiontranslate
      })
      eventBus.$on('tictketnotes_reloadlist', this.loadComments)
      eventBus.$on('shrink-comment-text-area', this.blurCommentBox)
    },

    killActiveEvents() {
      eventBus.$off('woDescriptiontranslate', () => {
        this.woDescriptiontranslate = false
      })
      eventBus.$on('tictketnotes_reloadlist')
      eventBus.$off('shrink-comment-text-area')
    },
    closeAllReplyInputs() {
      eventBus.$emit('close-reply-text-area')
    },
    blurCommentBox() {
      this.$refs['commentInput'].expandTextBox = false
    },
    expandNotesWidget() {
      eventBus.$emit('expandNotesWidget')
    },
    createHandler(note) {
      this.comments.push(note)
    },
    editHandler(note) {
      let { comments } = this
      let existingNote = comments.find(r => r.id === note.id)
      if (existingNote) {
        let index = comments.indexOf(existingNote)
        this.comments.splice(index, 1, note)
      }
    },
    deleteHandler(note) {
      let { comments } = this
      let existingNote = comments.find(r => r.id === note.id)
      if (existingNote) {
        this.comments.splice(comments.indexOf(existingNote), 1)
      }
    },

    updateCommentSharing(comment) {
      let { comments, CommentService } = this
      let existComment = comments.find(c => c.id == comment.id)
      let commentIndex = comments.indexOf(existComment)
      this.$set(this.comments, commentIndex, comment)
      let { error } = CommentService.updateCommentSharing(comment)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success('Comments have been updated')
      }
    },

    async loadComments() {
      let { CommentService, resizeNotesWidget } = this
      this.loading = true
      let { data, error } = await CommentService.fetchComments()
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.comments = !isEmpty(data) ? data : []
      }
      this.loading = false
      resizeNotesWidget()
    },

    showOrHideComments() {
      let { canShowAllComments } = this
      this.canShowAllComments = !canShowAllComments
      this.resizeNotesWidget()
    },
    resizeNotesWidget() {
      let { currentWidgetHeight, defaultWidgetHeight } = this
      this.$nextTick(() => {
        if (isEmpty(this.$refs['notes-container'])) {
          this.resizeWidget({ h: defaultWidgetHeight })
        } else {
          let height = this.$refs['comment-section'].scrollHeight + 80
          let width = this.$refs['notes-container'].scrollWidth
          if (height != currentWidgetHeight) {
            if (isFunction(this.resizeWidget)) {
              this.resizeWidget({ height, width })
              this.currentWidgetHeight = height
            }
          }
        }
      })
    },
  },
}
</script>
<style scoped lang="scss">
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

.fc-comments-widget {
  overflow: scroll;
  padding: 10px 30px;
}
.fc-comments-widget .comment-btn {
  max-width: 175px;
  margin: 18px 0 8px;
  font-size: 11px;
}

.fc-comments-widget .empty-notes-desc {
  font-size: 12px;
  letter-spacing: 0.34px;
  text-align: center;
  color: #b3afc9;
  position: relative;
  bottom: 17px;
}

.show-all-comments {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #428fc7;
}
</style>
