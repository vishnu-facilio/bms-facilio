<template>
  <el-dropdown
    v-if="selectedId"
    placement="bottom-start"
    trigger="click"
    @command="handleFilterSelection"
  >
    <span class="el-dropdown-link filter-text" v-if="displayName">
      {{ displayName }} <i class="el-icon-arrow-down el-icon--right"></i>
    </span>
    <el-dropdown-menu slot="dropdown">
      <el-input
        v-model="searchText"
        class="modules-header-input"
        placeholder="Search"
      ></el-input>
      <el-dropdown-item
        v-for="(option, index) in filteredOptions"
        :key="index"
        :command="option"
      >
        {{ option.label }}
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
export default {
  props: ['options', 'value'],
  data() {
    return {
      selectedId: this.value,
      searchText: '',
    }
  },
  computed: {
    displayName() {
      const selectedOption = this.options.find(option => {
        return option.id == this.selectedId
      })
      const { label } = selectedOption || {}
      return label
    },
    filteredOptions() {
      const query = this.searchText.replace(/\s/g, '').toLowerCase()
      return this.options.filter(option => {
        return option.label
          .replace(/\s/g, '')
          .toLowerCase()
          .includes(query)
      })
    },
  },
  watch: {
    value: function() {
      if (this.selectedId != this.value) {
        this.selectedId = this.value
      }
    },
  },
  methods: {
    handleFilterSelection(option) {
      this.selectedId = option.id
      this.$emit('input', option.id)
    },
  },
}
</script>

<style scoped lang="scss">
.modules-header-input .el-input {
  padding: 0px 20px;
  border-bottom: 1px solid #d8dce5;

  .el-input__inner {
    border-bottom: 0px;
    margin-bottom: 5px;
  }
}

.filter {
  margin: 0 20px;
  padding: 10px 0;
}

.filter-text {
  font-size: 14px;
  cursor: pointer;
  padding: 10px;
  background-color: #f3f4f7;
  border-radius: 3px;
  margin-top: 10px;
}
</style>
