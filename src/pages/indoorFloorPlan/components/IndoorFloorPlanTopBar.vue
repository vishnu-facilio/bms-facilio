<template>
  <div
    class="setting-header2 position-relative fp-header indoor-fp-topbar flex-middle"
  >
    <div class="setting-title-block">
      <div class="fc-black3-16 fw4 flex-middle cursor-default">
        <el-input
          placeholder=""
          class="fc-input-border-remove width-auto fc-input-floor"
          v-if="indoorFloorPlan"
          v-model="indoorFloorPlan.name"
        ></el-input>
        <span class="fc-grey3-text14 bold f14" v-if="indoorFloorPlan">for</span>
        <div class="display-flex btn-effect-1">
          <i class="el-icon-office-building f18 pR5 primary-font"></i>
          <span class="primary-font f14 ellipsis bold"
            >{{ `${floor.name}` }}, {{ `${building.name}` }}</span
          >
        </div>
      </div>
    </div>
    <div class="action-btn setting-page-btn">
      <el-button
        @click="$emit('close')"
        type="secondary"
        class="fc-btn-green-medium-border shadow-none"
        >Close</el-button
      >
      <el-button
        v-if="saveButn"
        type="primary"
        class="fc-btn-green-medium-fill shadow-none"
        @click="save"
        >Save</el-button
      >
    </div>
  </div>
</template>
<script>
export default {
  props: ['indoorFloorPlan', 'saveButn', 'floorplan', 'floor', 'building'],
  methods: {
    save() {
      this.$confirm('Once saved, changes go live immediately', {
        title: 'Save changes',
        confirmButtonText: 'Save',
        cancelButtonText: 'Cancel',
        cancelButtonClass: 'msg-cancel-btn',
        confirmButtonClass: 'msg-confirm-btn',
        customClass: 'fc-el-msgBox',
        type: 'warning',
      })
        .then(() => {
          this.$emit('save')
        })
        .catch(() => {
          this.$emit('close')
        })
    },
  },
}
</script>

<style lang="scss">
.fp-chooser {
  padding: 5px;
  border: 1px solid #efefef;
  border-radius: 5px;
  cursor: pointer;
  position: relative;
  right: 2px;
}
.fp-chooser:hover {
  box-shadow: 0 0 3px rgb(33 33 33 / 20%);
}
.indoor-fp-topbar {
  padding: 10px;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.fc-input-floor {
  .el-input__inner {
    width: 200px;
    padding-right: 10px !important;
    padding-left: 5px;
    border-bottom: 1px solid #d8dce5 !important;
    border-radius: 0 !important;
    color: #324056;
    font-size: 18px;
    font-weight: 500;
    margin-right: 10px;
    &:hover,
    &:active,
    &:focus {
      border-color: #39b2c2 !important;
    }
  }
}
</style>
