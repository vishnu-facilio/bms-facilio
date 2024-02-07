<template>
  <div class="d-flex flex-col news-comment-container">
    <div
      v-if="hasComments"
      class="pT10 pB10 pL25 pR25 border-top7 f13 comment-count"
      @click="showComments = true"
    >
      <span class="cursor-pointer">
        {{ comments.length }}&nbsp;
        {{ comments.length > 1 ? 'comments' : 'comment' }}
      </span>
    </div>
    <div class="pT20 pB15 pL25 pR25 border-top7 d-flex flex-row">
      <UserAvatar
        size="md"
        :user="$portaluser"
        :name="false"
        class="mR15 self-center"
      ></UserAvatar>
      <div
        ref="comment-input"
        contenteditable
        data-placeholder="Write a comment"
        class="comment-input"
        @input="onInput"
        @paste.prevent="onPaste"
        @keypress="onKeypress"
      ></div>
    </div>
    <div v-if="showComments" class="d-flex flex-col border-top7">
      <div
        v-for="comment in filteredComments"
        :key="comment.id"
        class="d-flex flex-row mR25 mL25 pT20 pB20 border-bottom7"
      >
        <div class="mR10">
          <UserAvatar
            size="md"
            :user="comment.createdBy"
            :name="false"
            class="mR15 self-center"
          ></UserAvatar>
        </div>
        <div class="d-flex flex-col">
          <div class="bold">
            {{ comment.createdBy.name || 'Unknown' }}
          </div>
          <div class="mT5 line-height20">
            {{ comment.body }}
          </div>
          <div class="mT10 f12 text-fc-grey">
            {{ comment.createdTime | fromNow }}
          </div>
        </div>
      </div>
      <div
        v-if="filteredComments.length < comments.length"
        class="m15 text-center text-fc-grey f13 cursor-pointer"
        @click="showAllComments"
      >
        View all comments
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import UserAvatar from '@/avatar/User'

export default {
  props: [
    'record',
    'comments',
    'moduleName',
    'notesModuleName',
    'canShowAllComments',
  ],
  components: { UserAvatar },
  data() {
    return {
      comment: {
        parentModuleLinkName: this.moduleName,
        parentId: this.record.id,
        body: null,
        notifyRequester: false,
      },
      showComments: !isEmpty(this.comments),
    }
  },
  computed: {
    value: {
      get() {
        return this.comment.body
      },
      set(value) {
        this.comment.body = value
        // Update div with value if it is diff from current value
        // TODO fix cursor position change
        let ref = this.$refs['comment-input']
        if (ref && ref.innerText !== value) {
          ref.innerText = value
        }
      },
    },
    hasComments() {
      return !isEmpty(this.comments)
    },
    filteredComments() {
      return this.canShowAllComments ? this.comments : this.comments.slice(0, 2)
    },
  },
  methods: {
    onInput(e) {
      let {
        target: { innerText },
      } = e

      this.value = isEmpty(innerText) ? '' : innerText
    },
    onKeypress(e) {
      if (e.key == 'Enter' && !e.shiftKey) {
        e.preventDefault()
        this.saveComment()
      }
    },
    onPaste(e) {
      let text = (e.originalEvent || e).clipboardData.getData('text/plain')
      window.document.execCommand('insertText', false, text)
    },

    saveComment() {
      let { comment, notesModuleName, moduleName } = this
      if (isEmpty(comment.body)) {
        return
      }

      let data = {
        note: comment,
        module: notesModuleName,
        parentModuleName: moduleName,
      }

      API.post('/note/add', data).then(({ data, error }) => {
        if (error || isEmpty(data.note)) {
          this.$message.error(error.message || 'Error while adding comment')
        } else {
          this.value = ''
          this.$emit('onCommentAdded', data.note)
          this.$nextTick(() => (this.showComments = true))
        }
      })
    },

    showAllComments() {
      this.$emit('onShowAllComments')
    },
  },
}
</script>
<style lang="scss" scoped>
.comment-count {
  color: #324056;
}
.comment-input {
  width: 100%;
  font-size: 13px;
  line-height: 20px;
  letter-spacing: 0.4px;
  color: rgb(51, 51, 51);
  background-color: #f5f6fa;
  padding: 8px 15px 6px;
  border-radius: 3px;
  border: 1px solid #d0d9e2 !important;

  &:empty:before {
    content: attr(data-placeholder);
  }
}
</style>
