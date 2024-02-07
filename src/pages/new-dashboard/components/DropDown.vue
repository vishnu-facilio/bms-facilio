<template>
  <el-popover
    :placement="placement"
    width="200"
    trigger="click"
    popper-class="widget-vertical-dropdown-body"
    v-model="showPopover"
    @show="$emit('update:isDropDownExpanded', true)"
    @hide="$emit('update:isDropDownExpanded', false)"
  >
    <template
      v-for="({ action, label, icon, nested, isLineBreak }, index) in options"
    >
      <div
        class="pointer options-label"
        v-if="!nested && !isLineBreak"
        :key="index"
        @click="
          () => {
            action(item)
            showPopover = false
          }
        "
      >
        <i v-if="icon" :class="icon" class="icons"></i>
        {{ label }}
      </div>
      <div class="horizontal-rule" v-else-if="isLineBreak" :key="index"></div>
      <drop-down
        v-else-if="nested"
        :item="item"
        :options="nested"
        placement="left"
        :key="index"
      >
        <template #icon>
          <div class="pointer options-label">
            <i v-if="icon" :class="icon" class="icons"></i>
            {{ label }}
          </div>
        </template>
      </drop-down>
      <div v-else :key="index">Error</div>
    </template>
    <slot name="icon" slot="reference"></slot>
  </el-popover>
</template>

<script>
import DropDown from './DropDown.vue'
export default {
  props: {
    options: {
      type: Array,
      default: () => [],
      required: true,
    },
    placement: {
      type: String,
      default: 'bottom',
    },
    item: {
      type: Object,
      required: true,
    },
    isDropDownExpanded: {
      // Syncs the current state of the dropdown, expanded / collapsed.
      type: Boolean,
    },
  },
  components: {
    DropDown,
  },
  name: 'DropDown',
  data() {
    return {
      showPopover: false,
    }
  },
}
</script>

<style lang="scss" scoped>
.horizontal-rule {
  width: 100%;
  border-top: 1px solid #eae8e8;
}
.icons {
  margin-right: 10px;
  font-size: 14px;
}
.options-label {
  padding: 12px;
  color: #25243e;
  font-size: 13px;
}
.options-label:hover {
  background-color: hsla(0, 0%, 96%, 0.5);
}
</style>
<style lang="scss">
.widget-vertical-dropdown-body {
  padding: 0px;
}
</style>
