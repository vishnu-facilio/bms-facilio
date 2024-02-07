<template>
  <div class="f-search-container mR20">
    <div v-if="boolval != undefined">
      <el-input
        ref="quickSearchQuery"
        :autofocus="true"
        class="fc-input-full-border-h35 mT15 mR10 wo-assigned-search"
        v-model="quickSearchQuery"
        @keyup.native.enter="quickSearch"
        placeholder="Search"
        prefix-icon="el-icon-search"
      >
        <i
          v-if="quickSearchQuery"
          slot="suffix"
          class="el-icon-close pointer"
          @click="closeSearch"
        ></i>
      </el-input>
    </div>
    <div
      v-if="
        showQuickSearch == true && boolval == undefined && remote == undefined
      "
    >
      <el-input
        ref="quickSearchQuery"
        v-show="showQuickSearch"
        :autofocus="true"
        class="f-quick-search-input"
        v-model="quickSearchQuery"
        @keyup.native.enter="quickSearch"
        placeholder="Search"
        prefix-icon="el-icon-search"
      >
        <i slot="suffix" class="el-icon-close pointer" @click="closeSearch"></i>
      </el-input>
    </div>
    <div
      v-if="showQuickSearch == true && boolval == undefined && remote == true"
    >
      <el-input
        ref="quickSearchQuery"
        v-show="showQuickSearch"
        :autofocus="true"
        class="f-quick-search-input "
        v-model="quickSearchQuery"
        @change="remoteSearch"
        placeholder="Search"
        prefix-icon="el-icon-search"
      >
        <i slot="suffix" class="el-icon-close pointer" @click="closeSearch"></i>
      </el-input>
    </div>
    <div
      class="pointer flRight"
      v-else-if="boolval == undefined && searchHide"
      @click="toggleQuickSearch"
    >
      <i class="el-icon-search"></i>
    </div>
  </div>
</template>
<script>
export default {
  props: ['value', 'searchKey', '', 'boolval', 'remote'],
  data() {
    return {
      showQuickSearch: false,
      quickSearchQuery: null,
      initialList: null,
      searchHide: true,
    }
  },
  beforeDestroy() {
    this.closeSearch()
  },
  mounted() {
    if (this.boolval != undefined) {
      this.showQuickSearch = this.boolval
    }
  },
  methods: {
    quickSearch() {
      if (!this.value) {
        return
      }
      let list
      if (this.quickSearchQuery) {
        if (!this.initialList) {
          this.initialList = this.$helpers.cloneObject(this.value)
        }
        list = this.initialList.filter(item => {
          return (
            item[this.searchKey || 'name']
              .toLowerCase()
              .indexOf(this.quickSearchQuery.toLowerCase()) >= 0
          )
        })
      } else {
        list = this.initialList
        this.initialList = null
      }
      if (list) this.$emit('input', list)
    },
    remoteSearch() {
      this.$emit('search', this.quickSearchQuery)
    },
    toggleQuickSearch() {
      this.searchHide = false
      this.showQuickSearch = !this.showQuickSearch
      if (this.showQuickSearch) {
        this.$nextTick(() => {
          this.$refs.quickSearchQuery && this.$refs.quickSearchQuery.focus()
        })
      }
    },
    clearSearch() {
      this.showQuickSearch = false
      this.quickSearchQuery = null
      this.searchHide = true
    },
    closeSearch() {
      if (this.boolval === undefined) {
        this.toggleQuickSearch()
      }
      this.quickSearchQuery = null
      if (this.remote) {
        this.remoteSearch()
      } else {
        this.quickSearch()
      }
      this.searchHide = true
      this.showQuickSearch = false
      this.$emit('close')
    },
  },
}
</script>
<style lang="scss">
.f-search-container {
  z-index: 2;
  .el-icon-close {
    color: #4f4f4f;
    font-weight: bold;
    font-size: 14px;
    padding-top: 15px;
    padding-right: 5px;
  }
  .el-icon-search {
    color: #4f4f4f;
    font-weight: bold;
    font-size: 15px;
  }
}

.f-quick-search-input {
  transition: max-width 0.3s linear;
  border: none !important;
  outline: none;
  background: transparent;
  display: flex;
  align-items: center;
  /* border-bottom: 1px solid #6f7c87 !important; */
}

.f-search-container .el-input .el-input__inner {
  width: 200px;
  height: 40px;
  padding-top: 1px;
  line-height: 30px;
  padding-left: 30px;
  padding-right: 30px;
  background: #f6f6f6;
  transition: all ease-in-out 0.15s;
  -webkit-transition: all ease-in-out 0.15s;
  -moz-transition: all ease-in-out 0.15s;
  border-bottom: none;
  border-radius: 4px;
  &:active,
  &:hover {
    border-color: #39b2c2 !important;
    border: 1px solid #39b2c2;
    border-bottom: 1px solid #39b2c2 !important;
  }
}

.f-search-container .el-input__prefix {
  top: 1px;
  left: 5px;
}

/* .f-search-container .el-input__suffix {
  top: 6px;
} */
.wo-assigned-search .el-input__suffix {
  top: 11px;
}
.wo-assigned-search .el-input__icon {
  line-height: 48px;
}
.wo-assigned-search .el-input__inner {
  box-shadow: 0px 0px 3px #e2e2e2;
}
.wo-assigned-search .el-input__validateIcon {
  display: none;
}
.f-search-container {
  .el-icon-search {
    &:hover {
      color: #3ab2c2;
    }
  }
  .el-icon-close {
    &:hover {
      color: #e87171;
    }
  }
}
</style>
