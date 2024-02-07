<template>
  <div>
    <el-popover
      placement="right-start"
      v-if="content"
      popper-class="break-normal text-left"
      width="300"
      trigger="hover"
    >
      <div slot="reference">
        <inline-svg
          iconClass="icon icon-sm"
          src="svgs/ic-info-outline"
          class=" flex-middle"
        ></inline-svg>
      </div>
      <div>{{ content }}</div>
    </el-popover>
  </div>
</template>
<script>
export default {
  props: {
    content: {
      type: String,
    },
  },
}
</script>
<style scoped>
.text-left {
  text-align: left;
}
</style>
