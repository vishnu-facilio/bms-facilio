<template>
  <div class="stateflow-builder-toolbar d-flex justify-content-end">
    <el-button-group class="d-flex" style="margin-right: auto;">
      <el-button type="plain" @click="goBack">
        <inline-svg
          src="left-arrow"
          iconClass="icon vertical-bottom mR10"
        ></inline-svg>
        {{ $t('setup.setupLabel.go_back') }}
      </el-button>
    </el-button-group>

    <el-button-group class="d-flex">
      <el-button
        type="plain"
        icon="fa fa-arrow-circle-o-right mR10"
        @click="addTransition"
      >
        Add Transition
      </el-button>
    </el-button-group>
    <el-button-group v-if="canShowAutoAlign">
      <el-tooltip
        class="item"
        effect="dark"
        content="Auto-Align"
        placement="bottom"
        transition="el-zoom-in-top"
      >
        <el-button
          type="plain"
          icon="fa fa-magic"
          @click="autoLayout"
        ></el-button>
      </el-tooltip>
    </el-button-group>
    <el-button-group>
      <el-tooltip
        class="item"
        effect="dark"
        content="Undo"
        placement="bottom"
        transition="el-zoom-in-top"
      >
        <el-button
          :disabled="!canShowUndo"
          type="plain"
          icon="fa fa-undo"
          @click="undo"
        ></el-button>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="Redo"
        placement="bottom"
        transition="el-zoom-in-top"
      >
        <el-button
          :disabled="!canShowRedo"
          type="plain"
          icon="fa fa-repeat"
          @click="redo"
        ></el-button>
      </el-tooltip>
    </el-button-group>
    <el-button-group>
      <el-tooltip
        class="item"
        effect="dark"
        content="Fit To Screen"
        placement="bottom"
        transition="el-zoom-in-top"
      >
        <el-button
          type="plain"
          icon="fa fa-compress"
          @click="fitToScreen"
        ></el-button>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="Zoom In"
        placement="bottom"
        transition="el-zoom-in-top"
      >
        <el-button
          type="plain"
          icon="fa fa-search-plus"
          @click="adjustZoom(true)"
        ></el-button>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="Zoom Out"
        placement="bottom"
        transition="el-zoom-in-top"
      >
        <el-button
          type="plain"
          icon="fa fa-search-minus"
          @click="adjustZoom(false)"
        ></el-button>
      </el-tooltip>
    </el-button-group>
  </div>
</template>
<script>
export default {
  props: [
    'autoLayout',
    'adjustZoom',
    'undo',
    'redo',
    'canShowUndo',
    'canShowRedo',
    'fitToScreen',
    'addState',
    'addTransition',
    'goBack',
  ],
  computed: {
    canShowAutoAlign() {
      let { align = false } = this.$route.query || {}
      return align
    },
  },
}
</script>
<style lang="scss" scoped>
.stateflow-builder-toolbar {
  height: 50px;
  background: #fafafa;
  border-bottom: 1px solid #ededed;
  box-shadow: 0 3px 4px 0 hsla(0, 0%, 85.5%, 0.32);
  padding: 1px;
  ul {
    padding: 0;
    margin: 0;
  }
  ul li {
    list-style: none;
    display: inline;
    cursor: pointer;
  }
  ul li i {
    font-size: 20px;
    padding: 12px 12px;
  }
  .el-button-group {
    padding: 0 6px;
  }
  .el-button-group:not(:last-of-type):not(:first-of-type) {
    border-right: 1px solid #ededed;
  }
  .el-button {
    border: none;
    background: transparent;
  }
}
</style>
