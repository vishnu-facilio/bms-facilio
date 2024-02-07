<template>
  <el-popover
    placement="bottom"
    v-model="visible"
    @hide="$emit('rowsCountUpdated')"
  >
    <div class="hide-column-customize">
      <div
        style="display:flex;justify-content:space-between;align-items:center;"
      >
        <span style="margin-bottom:0; margin-right:5px;">No. of Rows</span>
        <el-input
          type="number"
          :min="1"
          :max="2000"
          style="width:100px"
          class="fc-input-full-border-select2 name-field"
          v-model.number="config.sortBy.limit"
        >
        </el-input>
      </div>
      <div style="color:grey;opacity:0.7;" v-if="showLimitBubble">
        <span style="color:red;">*</span
        ><i>Modify row limit to view more data</i>
      </div>
    </div>
    <div
      slot="reference"
      class="row-count-label-container pivot-tab-hover"
      style="margin-left:16px;"
    >
      <img
        src="~statics/pivot/Row.svg"
        height="15px"
        width="15px"
        style="margin-top:1px;scale:1.2"
        v-if="!showLimitBubble"
      />
      <img
        src="~statics/pivot/Row-bubble.svg"
        iconClass="icon icon-sm-md "
        style="margin-top:1px;scale:1.3;"
        height="15px"
        width="16px"
        v-else
      />
      <span style="margin-left:8px;">{{ $t('pivot.rowLimit') }}</span>
    </div>
  </el-popover>
</template>
<script>
export default {
  props: ['config', 'recordCount'],
  data() {
    return {
      visible: false,
      showLimitBubble: false,
    }
  },
  watch: {
    'config.showLimitBubble': function(newval) {
      this.showLimitBubble = newval
    },
  },
}
</script>
<style lang="scss">
.row-count-label-container {
  padding: 5px 10px;
  cursor: pointer;
  font-size: 13px;
  display: flex;
  margin-top: 1px;
  .inline {
    margin-right: 3px;
  }
}
</style>
