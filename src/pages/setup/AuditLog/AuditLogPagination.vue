<template>
  <div class="flex-middle fc-setup-pagination">
    <el-button
      type="button"
      class="fc-pagination-arrow"
      @click="currentPage > 1 ? onClickPreviousPage() : null"
      :disabled="isInFirstPage"
    >
      <i class="el-icon-arrow-left"></i>
    </el-button>
    <div class="mL10 fc-black-14 text-left bold">
      {{ currentPage }}
    </div>
    <el-button
      type="button"
      class="fc-pagination-arrow mL10"
      @click="startPage !== total ? onClickNextPage() : null"
      :disabled="total === 0"
    >
      <i class="el-icon-arrow-right"></i>
    </el-button>
  </div>
</template>
<script>
export default {
  props: {
    totalPages: {
      type: Number,
    },
    total: {
      type: Number,
      required: true,
    },
    currentPage: {
      type: Number,
      required: true,
    },
  },
  computed: {
    isInFirstPage() {
      return this.currentPage === 1
    },
    isInLastPage() {
      return this.currentPage === this.totalPages
    },
    startPage() {
      if (this.currentPage === 1) {
        return 1
      }
      return this.currentPage - 1
    },
    endPage() {
      return Math.min(this.totalPages)
    },
    pages() {
      const range = []
      for (let i = this.startPage; i <= this.endPage; i += 1) {
        range.push({
          name: i,
          isDisabled: i === this.currentPage,
        })
      }
      return range
    },
  },
  methods: {
    onPageChange(page) {
      this.currentPage = page
    },
    onClickPreviousPage() {
      this.$emit('pagechanged', this.currentPage - 1)
    },
    onClickPage(page) {
      this.$emit('pagechanged', page)
    },
    onClickNextPage() {
      if (this.total) {
        this.$emit('pagechanged', this.currentPage + 1)
      }
    },
    isPageActive(page) {
      return this.currentPage === page
    },
  },
}
</script>
<style lang="scss">
.fc-setup-pagination {
  .fc-pagination-arrow {
    padding: 0;
    border: none;
    font-size: 16px;
    color: #324056;
    .el-icon-arrow-left,
    .el-icon-arrow-right {
      font-weight: bolder;
      &:hover {
        color: #38b2c2;
      }
    }
    &:hover {
      color: #324056;
      background: none;
      border: none;
    }
    &:focus {
      background: none;
    }
  }
  .el-icon-arrow-right,
  .el-icon-arrow-left {
    width: 30px;
    height: 30px;
    border: 1px solid transparent;
    padding-top: 7px;
    border-radius: 3px;
    &:hover {
      background: #f5f6f8;
      border: 1px solid #dae0e8;
    }
  }
}
</style>
