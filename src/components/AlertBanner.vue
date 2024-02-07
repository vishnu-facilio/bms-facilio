<template>
  <div class="error-block-page">
    <div class="fc-error-block" id="alert" v-if="hasAlert">
      <el-row>
        <el-col :span="1">
          <InlineSvg
            src="svgs/info-warning"
            iconClass="icon icon-sm fill-red vertical-text-super"
          ></InlineSvg>
        </el-col>
        <el-col :span="23">
          <div class="error-txt">
            {{ alertMessage ? alertMessage : 'Invalid Reading' }}
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
export default {
  props: ['alert', 'alertMessage'],
  computed: {
    hasAlert: {
      get() {
        return this.alert
      },
      set(value) {
        this.$emit('update:alert', value)
      },
    },
  },
}
</script>
<style lang="scss">
.error-block-page {
  .fc-error-block {
    width: 100%;
    border: solid 1px #f3bebe;
    background-color: #fff2f2;
    padding: 10px;
    position: relative;
  }
  .fc-error-block .error-txt {
    font-size: 13px;
    font-weight: 500;
    text-align: left;
    margin-bottom: 0;
    letter-spacing: 0.4px;
    color: #ec7c7c;
    word-break: break-word;
    line-height: 20px;
    padding-left: 5px;
  }
  .fc-error-block .error-close-icon {
    color: #de5656;
    font-size: 18px;
    font-weight: 300;
    cursor: pointer;
    color: #ec7c7c;
  }
}
</style>
