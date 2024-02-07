<template>
  <div class="p0">
    <el-scrollbar class="fc-placeholder-picker">
      <div
        v-for="(option, index) in optionsList"
        :key="`${option.name}-${index}`"
        :class="[
          'el-dropdown-menu__item fc-field-item',
          selectedIndex === index && 'selected-list-item',
        ]"
        @click="emitOptions(option, options)"
        @mouseover="openPopover(option, options, index)"
      >
        <div class="font-normal">{{ option.displayName }}</div>
        <div v-if="option.children">
          <i class="el-icon-arrow-right"></i>
        </div>
      </div>
    </el-scrollbar>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['options'],
  data() {
    return {
      selectedIndex: null,
    }
  },
  computed: {
    optionsList() {
      let { children } = this.options
      if (children) {
        return children
      } else {
        return this.options
      }
    },
  },
  methods: {
    emitOptions(option, options) {
      let { children } = option
      if (isEmpty(children)) {
        this.$emit('itemClick', { option, options })
      }
    },
    openPopover(option, options, index) {
      let { children } = option

      if (!isEmpty(children)) this.selectedIndex = index
      else this.selectedIndex = null

      this.$emit('openPopover', { option, options })
    },
  },
}
</script>

<style lang="scss" scoped>
.fc-placeholder-picker {
  max-height: 45vh;
  overflow-y: scroll;
  top: 0;
  left: 0;
  padding: 2px 0;
  margin: 5px 0;
  border-radius: 4px;
  color: #606266;
  line-height: 1.4;
  text-align: justify;
  font-size: 14px;
}
.popover-border-left {
  border-left: solid 0.5px #f8f9fa;
}
.fc-field-item {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding: 0px 15px;
  &:hover {
    color: #f94f83;
    background-color: #f8f9fac7;
  }
}
.selected-list-item {
  color: #f94f83;
  background-color: #f8f9fac7;
}
</style>
