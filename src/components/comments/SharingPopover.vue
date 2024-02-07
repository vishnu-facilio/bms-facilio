<template>
  <el-popover
    placement="top"
    width="220"
    height="200"
    trigger="click"
    popper-class="sharing-popover"
    :disabled="disable"
    @hide="onHide"
  >
    <div>
      <div style="background: #f7f8f9" class="p15">
        <span class="popover-public-text">{{
          $t('common._common.public')
        }}</span>
        <el-switch
          v-model="allowPublic"
          @change="onTogglePublic"
          style="float: right"
          class="comment-sharing-switch"
        ></el-switch>
        <div style="margin-top: 2px">
          <span
            class="sharing-message"
            :class="{ 'sharing-message-active': allowPublic }"
            >{{ $t('common._common.edit_sharing_message') }}</span
          >
        </div>
      </div>
      <div class="p15">
        <el-checkbox-group
          v-model="selectedApps"
          :disabled="!allowPublic"
          @change="onChange"
        >
          <el-checkbox
            v-for="app in apps"
            :key="app.appId"
            class="pB10"
            :label="app.id"
            >{{ app.name }}</el-checkbox
          >
        </el-checkbox-group>
      </div>
    </div>
    <div slot="reference">
      <slot></slot>
    </div>
  </el-popover>
</template>
<script>
export default {
  props: ['value', 'apps', 'disable'],
  data() {
    return {
      allowPublic: false,
      selectedApps: [],
    }
  },
  mounted() {
    this.init()
  },
  watch: {
    value() {
      this.init()
    },
  },
  methods: {
    init() {
      if (this.value) {
        this.selectedApps = this.value.map(s => {
          return s.appId
        })
        if (this.selectedApps.length) {
          this.allowPublic = true
        }
      }
    },
    onTogglePublic() {
      if (!this.allowPublic) {
        this.selectedApps = []
        this.onChange()
      }
    },
    onChange() {
      let commentSharing = this.selectedApps.map(appId => {
        return { appId: appId }
      })
      this.$emit('input', commentSharing)
      this.$emit('change', commentSharing)
    },
    onHide() {
      this.$emit('hide')
    },
  },
}
</script>
<style scoped lang="scss">
.visibilty-public {
  border: 0px;
  background: #fff;
  font-size: 12px;
  border-radius: 30px;
  padding: 0px;
  padding-right: 2px;
  padding-bottom: 2px;
  border-radius: 5px;
  cursor: pointer;
}

.visibilty-private {
  border: 0px;
  background: #fff;
  font-size: 12px;
  padding: 0px;
  padding-bottom: 2px;
  padding-right: 2px;
  border-radius: 5px;
}
.sharing-popover {
  padding: 0px;
}
</style>
