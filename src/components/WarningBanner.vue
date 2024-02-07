<template>
  <div class="fc-warning-card">
    <div class="fc-warning-info" id="warning" v-if="hasWarning">
      <el-row>
        <el-col :span="1">
          <i class="el-icon-warning-outline rotate180"></i>
        </el-col>
        <el-col :span="23">
          <div class="warning-txt">
            {{ warningMessage ? warningMessage : 'Invalid Reading' }}
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
export default {
  props: ['warning', 'warningMessage'],
  computed: {
    hasWarning: {
      get() {
        return this.warning
      },
      set(value) {
        this.$emit('update:warning', value)
      },
    },
  },
}
</script>
<style lang="scss">
.fc-warning-card {
  .fc-warning-info {
    border: solid 1px #d0e8ed;
    background-color: #f8fdff;
    box-sizing: border-box;
    width: 100%;
    padding: 10px 10px;
    text-align: left;
    border: solid 1px #d0e8ed;
    border-radius: 3px;
    margin-bottom: 20px;
    .warning-txt {
      font-size: 13px;
      font-weight: 500;
      letter-spacing: 0.4px;
      text-align: left;
      color: #44929c;
      margin-bottom: 0;
      word-break: break-word;
      line-height: 20px;
      padding-left: 5px;
    }
  }
  .el-icon-warning-outline {
    color: #44929c;
    font-size: 14px;
    font-weight: bold;
    padding-left: 5px;
  }
}
</style>
