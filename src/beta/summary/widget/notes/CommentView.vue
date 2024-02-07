<template>
  <FContainer display="flex" flexDirection="column">
    <CommentInput
      v-if="showEdit"
      :value="comment"
      :isEdit="showEdit"
      @save="emitUpdate"
      @close="showEdit = false"
    />
    <FContainer v-else display="flex" :padding="commentViewPadding">
      <FAvatar :userName="userName" />
      <FContainer width="100%">
        <FContainer
          display="flex"
          alignItems="center"
          justifyContent="space-between"
          marginLeft="containerXLarge"
        >
          <FContainer display="flex" gap="containerLarge" alignItems="center">
            <FText
              appearance="bodyReg14"
              v-if="comment.createdBy && comment.createdBy.id"
            >
              {{ comment.createdBy.name }}
            </FText>
            <FDivider height="12px" width="0px" />
            <FText appearance="captionReg12" color="textCaption">
              {{ comment.createdTime | fromNow }}
            </FText>
          </FContainer>
          <FDropdown
            :options="dropDownOptions"
            :split="false"
            :buttonProps="{ appearance: 'tertiary' }"
            contentWidth="150px"
            @dropdown="option => editDeleteHandler(option, comment)"
          >
            <FIcon
              group="action"
              name="options-horizontal"
              size="16"
              color="textCaption"
            ></FIcon>
          </FDropdown>
        </FContainer>
        <FContainer
          class="markdown-style"
          marginLeft="containerXLarge"
          v-if="comment.bodyHTML != null"
          v-html="comment.bodyHTML"
        ></FContainer>
        <FContainer marginTop="containerLarge" marginLeft="containerXLarge">
          <CommentAttachments
            v-if="canShowAttachments"
            :attachments="comment.attachments"
            class="comment-attachments-preview mb5"
          ></CommentAttachments>
        </FContainer>
        <FContainer
          display="flex"
          alignItems="center"
          gap="containerLarge"
          margin="containerMedium containerMedium"
        >
          <FButton
            appearance="tertiary"
            iconGroup="communication"
            iconName="reply"
            size="small"
            @click="showOrHideReply({})"
          >
            {{ 'Reply' }}
          </FButton>
          <FDivider
            v-if="CommentService.canShowSharing && !isReply"
            height="12px"
            width="0px"
          />
          <SharingPopover
            v-if="CommentService.canShowSharing && !isReply"
            v-model="comment.commentSharing"
            :apps="CommentService.portalApps"
            :disable="!comment.editAvailable"
            @change="() => updateCommentSharing(comment)"
          >
            <FButton
              appearance="tertiary"
              iconGroup="default"
              :iconName="isPublicComment ? 'eye-open' : 'eye-slashed'"
              size="small"
            >
              {{ isPublicComment ? 'Public' : 'Private' }}
            </FButton>
          </SharingPopover>
        </FContainer>
        <FContainer marginTop="containerLarge" marginLeft="containerXLarge">
          <FContainer v-if="canShowCommentBar || canShowReplies">
            <FContainer v-if="canShowCommentBar">
              <CommentInput
                ref="reply-input"
                :parentNote="comment"
                :placeHolder="$t('common._common.add_a_reply')"
                :referer="refererForReply"
                @save="createHandler"
                @close="showOrHideReply"
              >
              </CommentInput>
            </FContainer>

            <FContainer v-if="loading">
              <FSpinner :size="20"></FSpinner>
            </FContainer>
            <FContainer v-else-if="canShowReplies">
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
            </FContainer>
          </FContainer>
          <FButton
            v-if="showRepliesButton"
            appearance="link"
            @click="viewMoreReplies"
          >
            {{ repliesExpandText }}</FButton
          >
        </FContainer>
      </FContainer>
    </FContainer>
  </FContainer>
</template>

<script>
import {
  FContainer,
  FAvatar,
  FDivider,
  FText,
  FButton,
  FDropdown,
  FIcon,
} from '@facilio/design-system'
import CommentInput from './CommentInput.vue'
import CommentView from '@/notes/CommentView.vue'
import SharingPopover from './SharingPopover.vue'
import CommentAttachments from 'src/components/notes/CommentAttachments.vue'
export default {
  extends: CommentView,
  name: 'RecursiveComment',
  components: {
    FContainer,
    CommentInput,
    FAvatar,
    FDivider,
    FText,
    FButton,
    SharingPopover,
    FDropdown,
    CommentAttachments,
    FIcon,
  },
  computed: {
    userName() {
      let { $account } = this || {}
      let { user } = $account || {}
      let { name } = user || {}
      return name
    },
    dropDownOptions() {
      return [
        { label: this.$t('common._common.edit'), value: 'edit' },
        { label: this.$t('common._common.delete'), value: 'delete' },
      ]
    },
    commentViewPadding() {
      let { currentLevel } = this || {}
      if (currentLevel > 1)
        return 'containerLarge containerNone containerLarge containerXxLarge'
      else return 'containerLarge containerXxLarge'
    },
  },
  methods: {
    async editDeleteHandler(option, note) {
      let { value: command } = option || {}
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
  },
}
</script>
