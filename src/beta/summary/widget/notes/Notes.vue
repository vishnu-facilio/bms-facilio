<template>
  <FContainer>
    <CommentInput
      ref="commentInput"
      :placeHolder="
        forWO
          ? $t('common._common.add_a_comment')
          : $t('common._common.add_note')
      "
      @focus="closeAllReplyInputs"
      @save="createHandler"
    />
    <FContainer>
      <FContainer
        v-if="loading"
        display="flex"
        width="100%"
        justifyContent="center"
        alignItems="center"
        marginTop="containerXxLarge"
      >
        <FSpinner :size="20"></FSpinner>
      </FContainer>
      <FContainer
        v-else-if="$validation.isEmpty(comments)"
        display="flex"
        width="100%"
        justifyContent="center"
        alignItems="center"
        flexDirection="column"
      >
        <inline-svg
          src="svgs/community-empty-state/comments"
          class="vertical-middle self-center"
          iconClass="icon empty-note mR5 "
        ></inline-svg>
        <FText>{{
          title
            ? $t('common.products.no_mod_available') +
              title +
              $t('common._common.added_yet')
            : $t('common._common.no_notes')
        }}</FText>
      </FContainer>
      <template v-else>
        <CommentView
          v-for="(comment, index) in filteredComments"
          :key="comment.id"
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
      </template>
    </FContainer>
  </FContainer>
</template>

<script>
import CommentView from './CommentView'
import CommentInput from './CommentInput'
import { FContainer, FSpinner, FText } from '@facilio/design-system'
import Notes from '@/widget/NewNotes.vue'
export default {
  extends: Notes,
  name: 'Notes',
  components: { FContainer, CommentInput, FSpinner, FText, CommentView },
}
</script>
