<template>
  <FContainer>
    <!-- <portal :to="`header-${widget.id}-${widget.name}`">
      <FContainer padding="containerXLarge">
        <FText appearance="headingMed14">{{ widget.displayName }}</FText>
      </FContainer>
    </portal> -->
    <Notes
      :module="notesModuleName"
      :parentModule="moduleName"
      :isCustomModule="isCustomModule"
      class="assetsnotes flex-grow overflow-y-scroll"
      :record="details"
      :notify="canShowNotifyRequester"
      :title="widget ? widget.title : $t('asset.assets.notes')"
      :layoutParams="layoutParams"
      :resizeWidget="resizeWidget"
      :isActive="isActive"
      :groupKey="groupKey"
    />
  </FContainer>
</template>

<script>
import { FContainer } from '@facilio/design-system'
import NotesWidget from '@/page/widget/common/NotesWidget.vue'
import Notes from './Notes.vue'
export default {
  extends: NotesWidget,
  name: 'NotesWidget',
  props: ['widget'],
  components: { FContainer, Notes },
}
</script>
