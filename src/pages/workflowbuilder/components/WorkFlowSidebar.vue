<template>
  <div class="workflow-tool">
    <div
      class="workflow-block"
      v-for="(type, index) in availableTypes"
      :key="index"
    >
      <div
        class="self-center"
        draggable="true"
        @dragstart="setDraggedType(type)"
      >
        <InlineSvg
          :src="`svgs/workflowbuilder/${type.svgName}`"
          iconClass="icon icon-40"
        ></InlineSvg>
      </div>
      <div class="self-center">{{ type.label }}</div>
    </div>
  </div>
</template>
<script>
export default {
  props: ['selectedBlockType', 'isNewWorkFlowBlockDragged'],
  data() {
    return {
      availableTypes: [
        {
          label: 'Event',
          type: 'event',
          svgName: 'event',
        },
        {
          label: 'Condition',
          type: 'condition',
          svgName: 'condition',
        },
        {
          label: 'Action',
          type: 'action',
          svgName: 'action',
        },
      ],
    }
  },
  methods: {
    setDraggedType(type) {
      this.$emit('update:selectedBlockType', type)
      this.$emit('update:isNewWorkFlowBlockDragged', true)
    },
  },
}
</script>
<style lang="scss">
.workflow-tool {
  .workflow-block {
    border-bottom: 1px solid;
    padding: 15px;
    display: flex;
    flex-direction: column;
  }
}
</style>
