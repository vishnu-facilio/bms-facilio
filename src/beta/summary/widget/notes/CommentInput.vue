<template>
  <FContainer display="flex" padding="containerXxLarge">
    <FAvatar :userName="userName" />
    <FContainer
      border="solid 1px"
      borderColor="borderNeutralBaseSubtler"
      borderRadius="medium"
      marginLeft="containerXLarge"
      width="100%"
    >
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
        <template #attachments>
          <FContainer
            v-if="showAttachmentsPreview"
            display="flex"
            gap="containerMedium"
            padding="containerLarge containerNone"
          >
            <FTags
              v-for="(attachment, index) in filteredAttachments"
              :key="`uploaded-${index}`"
              :text="attachment.fileName"
            >
              <template #suffix>
                <FIcon
                  group="default"
                  name="close"
                  size="11"
                  @click="removeAttachment(index)"
                ></FIcon>
              </template>
            </FTags>
            <FTags
              v-for="(attachment, index) in failedFileList"
              :key="`failed-${index}`"
              :text="attachment.name + ` failed`"
            >
              <template #suffix>
                <FIcon
                  group="default"
                  name="close"
                  size="11"
                  @click="removeFailedAttachments(index)"
                ></FIcon>
              </template>
            </FTags>
            <FTags
              v-if="canShowViewMoreAttachments"
              :text="`+${hiddenAttachmentsCount}`"
            />
          </FContainer>
        </template>
      </NotesRichText>
      <FContainer
        v-if="expandTextBox"
        display="flex"
        borderTop="solid 1px"
        borderColor="borderNeutralBaseSubtler"
        padding="containerLarge"
        justifyContent="space-between"
      >
        <FContainer display="flex" gap="containerMedium">
          <FIcon
            @click="peopleMentionButton"
            size="16"
            group="text-edit"
            name="at"
            color="iconNeutralDefault"
          ></FIcon>
          <FIcon
            @click="recordMentionButton"
            size="16"
            group="text-edit"
            name="record-mention"
            color="iconNeutralDefault"
          ></FIcon>
          <label>
            <input
              class="hide"
              type="file"
              multiple
              @change="filesChange($event.target.files)"
            />
            <FIcon
              size="16"
              group="text-edit"
              name="attachment"
              color="iconNeutralDefault"
            ></FIcon>
          </label>
          <FIcon
            size="16"
            group="text-edit"
            name="markup"
            color="iconNeutralDefault"
            @click="markdownshow = true"
          ></FIcon>
        </FContainer>
        <FContainer display="flex" gap="containerLarge">
          <SharingPopover
            v-model="commentSharing"
            :apps="CommentService.portalApps"
          >
            <FButton
              appearance="tertiary"
              size="small"
              iconGroup="default"
              iconName="eye-open"
            >
              {{ $t('common._common.private') }}
            </FButton>
          </SharingPopover>
          <FButton
            appearance="secondary"
            size="small"
            @click="close"
            :disabled="isAddingNotes"
          >
            {{ $t('common._common.cancel') }}
          </FButton>
          <FButton size="small" @click="saveNote" :loading="isAddingNotes">
            {{ saveNoteBtn }}
          </FButton>
        </FContainer>
      </FContainer>
    </FContainer>
    <MarkdownList :markdownshow.sync="markdownshow" />
  </FContainer>
</template>

<script>
import {
  FContainer,
  FAvatar,
  FIcon,
  FButton,
  FTags,
} from '@facilio/design-system'
import CommentInput from '@/notes/CommentInput.vue'
import NotesRichText from '@/notes/NotesRichText.vue'
import MarkdownList from './MarkdownList.vue'
import SharingPopover from './SharingPopover.vue'
export default {
  extends: CommentInput,
  name: 'CommentsInput',
  components: {
    FContainer,
    FAvatar,
    NotesRichText,
    FIcon,
    FButton,
    FTags,
    MarkdownList,
    SharingPopover,
  },
  computed: {
    userName() {
      let { $account } = this || {}
      let { user } = $account || {}
      let { name } = user || {}
      return name
    },
  },
}
</script>
