<template>
  <div class="fc-switcher-container fc-site-switcher-container">
    <div class="switcher-header"></div>
    <div class="switcher-content pT10">
      <ul class="list-style-none">
        <li
          @click="selectItem(item)"
          v-for="(item, index) in list"
          :key="index"
          v-bind:class="{ active: item.id === currentValue }"
          class="el-dropdown-menu__item"
        >
          {{ item.name }}
        </li>
      </ul>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      searchInput: '',
      currentValue: '',
      list: [],
    }
  },
  methods: {
    load(options) {
      this.currentValue = options.currentValue
        ? parseInt(options.currentValue)
        : -1
      this.list = this.$store.state.site
    },
    selectItem(item) {
      this.$emit('onselect', item)
    },
  },
}
</script>
<style>
.el-dropdown-menu__item.active {
  background-color: #ecf5ff;
  color: #66b1ff;
}
.fc-site-switcher-container {
  max-height: 300px;
  overflow-x: hidden;
  overflow-y: scroll;
  padding-bottom: 20px;
}
.fc-site-switcher-container .el-dropdown-menu__item {
  line-height: 24px;
}
</style>
