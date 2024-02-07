<template>
  <div class="d-flex">
    <div class="fc-blue-text-14">{{ inputText || '---' }}</div>
    <div class="copy-img" v-if="inputText">
      <div style="margin:9px 0 0 8px" @click="copyLinkName(inputText)">
        <InlineSvg
          src="svgs/employeePortal/ic-content-copy"
          icon-class="xxxs"
        ></InlineSvg>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['inputText'],
  methods: {
    async copyLinkName(copy) {
      await navigator.clipboard.writeText(copy)
      this.$message({
        message: 'Copied to clipboard!',
        type: 'success',
      })
    },
  },
}
</script>
<style scoped>
.fc-blue-text-14 {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 32px;
  letter-spacing: 0.34px;
  color: #0053cc;
}
.fc-blue-text-14:hover {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 32px;
  letter-spacing: 0.34px;
  color: #0053cc;
  text-decoration: underline;
}
.copy-img {
  margin-left: 10px;
  height: 32px;
  width: 32px;
  cursor: pointer;
}
.copy-img:hover {
  background-color: #f0f0f0;
  border-radius: 15px;
  cursor: pointer;
}
</style>
