<template>
  <FContainer>
    <!-- <portal v-if="!hideTitleSection" :to="`header-${widget.id}-${widget.name}`">
      <FContainer padding="containerXLarge">
        <FText appearance="headingMed14">{{ widget.displayName }}</FText>
      </FContainer>
    </portal> -->
    <Attachment
      :module="attachmentsModuleName"
      :parentModule="moduleName"
      :record="details"
    />
  </FContainer>
</template>

<script>
import Attachment from './Attachment.vue'
export default {
  name: 'AttachmentWidget',
  components: { Attachment },
  props: [
    'hideTitleSection',
    'widget',
    'details',
    'moduleName',
    'attachmentsModuleName',
  ],
}
</script>
