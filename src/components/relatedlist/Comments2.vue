<template>
  <div class="fc-new-comments fc-comments row">
    <div class="col-12 scroll-new">
      <notes
        :module="'ticketnotes'"
        parentModule="workorder"
        class="assetsnotes new-summary-block fc-visibility-gicon-visible flex-grow overflow-y-scroll"
        :record="record"
        :notify="true"
        :layoutParams="500"
        :resizeWidget="this.resizeWidget"
        :isActive="false"
        :forWO="true"
        :listViewComments="true"
      ></notes>
    </div>
  </div>
</template>
<script>
import Notes from '@/widget/Notes'

export default {
  extends: Notes,
  props: [
    'record',
    'moduleName',
    'details',
    'layoutParams',
    'widget',
    'resizeWidget',
  ],
  components: {
    Notes,
  },
  data() {
    return {
      markdownshow: false,
    }
  },
  methods: {
    markdownhelplist() {
      this.markdownshow = true
    },
    focusCommentBox() {
      this.commentFocus = true
    },
    blurCommentBox() {
      if (!this.newComment.body || this.newComment.body.trim() === '') {
        // this.commentFocus = false
      }
    },
  },
}
</script>
<style>
.fc-new-comments .fc-comments .comment-msg-container {
  overflow-y: scroll;
  max-height: 77vh;
}
.fc-comments {
  margin-top: 10px;
}

.fc-comments .fc-comment-row {
  padding: 12px 0;
  border-bottom: 1px solid #fafafa;
}

.fc-comments .fc-comment-row .comment-by {
  font-weight: 500;
  margin-right: 5px;
}
.comment-by2 {
  margin-right: 0px !important;
}

.fc-comments .fc-comment-row .comment-body {
  line-height: 20px;
  text-align: left;
  max-width: 70%;
  word-wrap: break-word;
  white-space: normal;
}

.fc-comments .comment-box {
  border: 1px solid #ededed;
  resize: none;
  padding: 10px;
  outline: none;
  font-size: 12px;
  width: 95% !important;
  transition-timing-function: ease-in;
  transition: 0.4s;
  white-space: pre-wrap;
}

.fc-comments .comment-btn {
  padding: 8px 22px;
  outline: none;
  cursor: pointer;
  border-radius: 3px;
  border: solid 1px transparent;
  font-size: 10px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.7px;
  text-align: center;
  color: #fff;
  font-size: 12px;
  font-weight: bold;
  background: #39b2c2;
}

.fc-comments .notify-req {
  padding-left: 4px;
  font-size: 11px;
}
.comment-area {
  position: absolute;
  width: 100% !important;
  bottom: 30px !important;
  margin-top: 20px;
  /* border-top: 1px solid rgb(221, 221, 221); */
  padding-top: 30px;
  background-color: #fff;
  padding-left: 20px;
  padding-bottom: 60px;
  padding-right: 20px;
}
.comments-row {
  overflow-y: auto;
  padding-left: 20px;
  padding-right: 20px;
  padding-bottom: 80px;
}
.comment-box:focus,
.comment-box:active {
  border-color: #d3dbda;
}
.comment-by2 span.q-item-label {
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.2px;
  text-align: left;
  color: #333333;
  margin-left: 10px;
}
.comment-by2 div.flRight.pL15.secondary-color2.f12.comment-time.col-4 {
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.2px;
  text-align: left;
  color: #8c8c8c;
}
.comment-body2 {
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.46;
  letter-spacing: 0.2px;
  padding-top: 5px;
  text-align: justify !important;
  color: #333333;
  max-width: 85% !important;
}
.scroll-new {
  height: 100vh;
  overflow-y: auto;
}
.comment-circle {
  vertical-align: middle;
  color: #d8d8d8;
  font-size: 6px;
  margin-left: 6px;
}
.fc-comments .notify-req {
  padding-left: 4px;
  font-size: 12px;
  letter-spacing: 0.3px;
  text-align: left;
  color: #666666;
}
.comment-checkbox {
  width: 16px;
  height: 16px;
  border-radius: 3px;
  background-color: #ffffff;
  border: solid 1px #dcdcdc;
}
.temp-command-btn {
  bottom: 20px;
  position: absolute;
  right: 20px;
  border-radius: 1px;
  font-size: 10px;
  letter-spacing: 0.8px;
  background: #39b2c2;
  color: #fff;
  font-weight: 500;
  text-transform: uppercase;
  border: none;
}
.temp-command-btn:hover {
  background: #33a6b5;
}
</style>
