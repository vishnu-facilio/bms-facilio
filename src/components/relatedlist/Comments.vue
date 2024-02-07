<template>
  <div class="fc-comments">
    <div class="text-center" v-if="loading">
      <spinner :show="loading"></spinner>
    </div>
    <div v-else class="comment-msg-container">
      <div class="fc-comment-row" v-for="comment in comments" :key="comment.id">
        <div class="flLeft f12 secondary-color comment-by">
          <user-avatar
            size="md"
            class="comments-avatar"
            :user="comment.createdBy"
            :id="comment.createdBy.id"
          ></user-avatar>
          <div class="pL45 comment-time">
            <i
              class="fa fa-circle"
              aria-hidden="true"
              style="opacity: 0.4px;color: rgba(140,161,173,0.30);font-size: 8px;padding-right: 5px;"
            ></i>
            {{ comment.createdTime | fromNow }}
          </div>
        </div>
        <div
          class="markdown-style"
          v-if="comment.bodyHTML != null"
          v-html="comment.bodyHTML"
        ></div>
        <div class="clearboth"></div>
      </div>
    </div>
    <div class="row new-comment-area width100 mT20" id="commentBoxPar">
      <form v-on:submit.prevent="addComment" class="col-12">
        <div class="markdown-inside">
          <textarea
            ref="commentBoxRef"
            style="font-size: 13px;letter-spacing: 0.3px;color: #333333;border-color:#d0d9e2 !important;border-radius: 3px;width: 100%;height: 40px;"
            v-model="newComment.body"
            :placeholder="
              placeholder ? placeholder : $t('common._common.write_comment')
            "
            v-bind:class="{ height75: commentFocus, height35: !commentFocus }"
            class="comment-box pT10"
            @focus="focusCommentBox"
          />
          <button
            :class="[commentFocus ? 'show' : 'hide']"
            @click="markdownhelplist"
          >
            <img src="~assets/markdown-mark.svg" class="pull-right pointer" />
          </button>
        </div>
        <template v-if="this.markdownshow">
          <markdown-help-list :markdownshow.sync="markdownshow">
          </markdown-help-list>
        </template>
        <div v-if="commentFocus">
          <div class="flRight">
            <button class="comment-btn">
              {{ btnLabel ? btnLabel : $t('common._common.comment') }}
            </button>
          </div>
          <div class="flLeft" v-if="!isServicePortal && showNotifyRequester">
            <input
              type="checkbox"
              v-model="newComment.notifyRequester"
              name="notifyRequester"
              class="notify-requester-checkbox"
            />
            <label class="notify-req" for="notifyRequester">{{
              $t('common._common.notify')
            }}</label>
          </div>
          <div class="clearboth"></div>
        </div>
      </form>
    </div>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import BaseComment from '@/base/Comments.vue'
import MarkdownHelpList from '@/relatedlist/MarkdownHelpList'

export default {
  extends: BaseComment,
  props: ['placeholder', 'btnLabel'],
  components: {
    UserAvatar,
    MarkdownHelpList,
  },
  data() {
    return {
      markdownshow: false,
    }
  },
  created() {
    window.addEventListener('click', this.blurCommentBox)
  },
  methods: {
    markdownhelplist() {
      this.markdownshow = true
    },
    focusCommentBox() {
      this.$refs.commentBoxRef.focus()
      this.commentFocus = true
    },
    blurCommentBox(e) {
      let itTargetPar = e.path.filter(ele => {
        if (ele.id === 'commentBoxPar') {
          return true
        }
      })
      if (
        (!this.newComment.body || this.newComment.body.trim() === '') &&
        !itTargetPar.length
      ) {
        this.commentFocus = false
      }
    },
  },
}
</script>
<style>
.fc-comments {
  margin-top: 10px;
}

.fc-comments .fc-comment-row {
  padding-bottom: 12px;
}

.fc-comments .fc-comment-row .comment-by {
  font-weight: 500;
  margin-right: 6px;
}

.fc-comments .fc-comment-row .comment-body {
  font-size: 13px;
  line-height: 20px;
  text-align: left;
  max-width: 100%;
  clear: both;
  margin-left: 0px;
  word-wrap: break-word;
  line-height: 1.5;
  letter-spacing: 0.6px;
  color: #324056;
}

.fc-comments .comment-box {
  border: 1px solid #ededed;
  resize: none;
  padding: 10px;
  outline: none;
  font-size: 12px;
  width: 100%;
  transition-timing-function: ease-in;
  transition: 0.4s;
  height: 65px;
}

.fc-comments .comment-btn {
  background-color: #fff;
  padding: 8px 12px;
  outline: none;
  cursor: pointer;
  border-radius: 3px;
  border: 1px solid #39b2c2;
  font-size: 10px;
  font-weight: 700;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.7px;
  text-align: center;
  color: #39b2c2;
  transition: all 0.3s ease 0s;
  margin-top: 10px;
}
.fc-comments .comment-btn:hover {
  background: #39b2c2;
  color: #fff;
}

.fc-comments .notify-req {
  padding-left: 4px;
  font-size: 11px;
  font-size: 12px;
  font-weight: normal;
  letter-spacing: 0.3px;
  color: #333333;
}
.fc-comments .comment-message-container {
  margin-top: 20px;
}
.comment-time {
  font-size: 10px;
  font-weight: normal;
  letter-spacing: 0.5px;
  color: #8ca1ad;
}
.comments-avatar .q-item-label {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.5px;
  color: #242326;
}
.comment-form {
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
  margin-top: 50px;
}
.comment-checkbox {
  font-size: 15px;
  color: #d3d3d3;
}
.notify-requester-checkbox {
  width: 18px;
  height: 18px;
  border: 1px solid #dcdfe6;
  border-radius: 2px;
  transition: border-color 0.25s cubic-bezier(0.71, -0.46, 0.29, 1.46),
    background-color 0.25s cubic-bezier(0.71, -0.46, 0.29, 1.46);
}
</style>
