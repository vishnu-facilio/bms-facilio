<template>
  <div
    class="flex-center-row-space fc-black-small-txt-12"
    v-if="canShowPagination"
  >
    <span
      class="el-icon-arrow-left pagination-arrow f16 pointer p5 fw-bold"
      @click="from > 1 && prev()"
      :class="{ disable: from <= 1 }"
    ></span>
    <el-popover placement="bottom" trigger="click" :disabled="disablePopup">
      <div
        slot="reference"
        class="p8"
        :class="[canShowPopup && 'button-hover pointer']"
        v-if="currentPageCount !== 0"
      >
        <div
          v-if="!canShowPaginationParts"
          class="page-shimmer-line loading-shimmer mT2"
        ></div>
        <div v-else class="flex-middle justify-between ">
          <span v-if="from !== to">{{ from }} - </span>
          <span class="mL5">{{ to }}</span
          ><span v-if="!skipTotalCount" class="d-flex lp-page">
            <span>{{ 'of' }}</span>
            <span
              v-if="shimmerLoading"
              class="count-shimmer-line loading-shimmer mT1 "
            ></span>
            <span v-else>{{ total }}</span>
          </span>
        </div>
      </div>
      <el-pagination
        v-if="!$validation.isEmpty(total)"
        layout="prev, pager, next"
        @current-change="handleCurrentChange"
        :total="total"
        :pager-count="5"
        :page-size.sync="perPage"
        :current-page.sync="page"
        class="pagination-without-count-popover"
      >
      </el-pagination>
    </el-popover>
    <span>
      <span
        class="el-icon-arrow-right pagination-arrow  f16 pointer p5 fw-bold"
        @click="canGoNextPage && next()"
        :class="{ disable: !canGoNextPage }"
      ></span>
    </span>
  </div>
</template>
<script>
import { isWebTabsEnabled } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: {
    total: [Number, String],
    currentPageCount: Number,
    perPage: { type: Number, default: 50 },
    currentPage: Number,
    hidePopover: {
      type: Boolean,
      default: false,
    },
    skipTotalCount: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      from: 0,
      to: 0,
      page: 1,
      shimmerLoading: true,
      navigatingToNextPage: true,
    }
  },
  created() {
    if (isWebTabsEnabled()) {
      document.addEventListener('keydown', this.keyDownHandler)
    }
  },
  beforeDestroy() {
    if (isWebTabsEnabled()) {
      document.removeEventListener('keydown', this.keyDownHandler)
    }
  },
  computed: {
    pageQuery() {
      let { query } = this.$route || {}
      let { page } = query || {}
      return parseInt(page) || 1
    },
    hasMorePage() {
      return this.total / this.perPage > 1
    },
    disablePopup() {
      return this.hidePopover || !this.hasMorePage
    },
    canShowPages() {
      return !isEmpty(this.total)
    },
    canGoNextPage() {
      return !isEmpty(this.total)
        ? this.to !== this.total
        : this.perPage == this.currentPageCount
    },
    canShowPaginationParts() {
      return !!(this.currentPageCount || this.total)
    },
    canShowPagination() {
      return this.page !== 1 || !this.navigatingToNextPage
        ? true
        : this.currentPageCount > 0 || !isEmpty(this.total)
    },
    canShowPopup() {
      return !this.disablePopup && !isEmpty(this.total)
    },
  },
  watch: {
    total: {
      handler(newVal) {
        this.shimmerLoading = true
        if (!isEmpty(newVal)) this.shimmerLoading = false
        this.init()
      },
      immediate: true,
    },
    currentPageCount(newVal) {
      if (!isEmpty(newVal) && isEmpty(this.total)) {
        this.init()
      }
    },
    pageQuery(val) {
      if (val !== this.page) {
        this.init()
      }
    },
  },
  methods: {
    init() {
      this.page = this.currentPage ? this.currentPage : this.pageQuery
      this.from = (this.page - 1) * this.perPage + 1
      let to = this.from + this.perPage - 1

      this.to = isEmpty(this.total)
        ? this.from + this.currentPageCount - 1
        : this.total > to
        ? to
        : this.total
    },
    handleCurrentChange(val) {
      this.page = val
      this.from = (this.page - 1) * this.perPage + 1
      this.to =
        this.total >= this.from + this.perPage
          ? this.from + this.perPage - 1
          : this.total
      this.setPage()
    },
    next() {
      this.from = this.to + 1
      if (!isEmpty(this.total)) {
        let to = this.from + this.perPage - 1

        if (to > this.total) to = this.total
        this.to = to
      }
      this.page++
      this.navigatingToNextPage = true
      this.setPage()
    },
    prev() {
      this.to = this.from - 1
      this.from -= this.perPage
      if (this.from <= 1) {
        this.from = 1
        this.page = 1
      } else {
        this.page--
      }
      this.navigatingToNextPage = false
      this.setPage()
    },
    setPage() {
      if (this.currentPage) {
        this.$emit('update:currentPage', this.page)
      } else {
        let query = { ...this.$route.query }
        if (!isEmpty(query)) {
          query.page = this.page
        } else {
          query = { page: this.page }
        }
        this.$router.push({ query })
      }
    },
    keyDownHandler(e) {
      if (e.shiftKey) {
        if (
          e.key === 'ArrowRight' &&
          (this.to !== this.total || this.to !== this.currentPageCount)
        ) {
          this.next()
        } else if (e.key === 'ArrowLeft' && this.from > 1) {
          this.prev()
        }
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.pagination-arrow.disable {
  cursor: not-allowed;
  color: #50506c;
  font-weight: bold;
  opacity: 0.4;
  background: none;
}
.button-hover {
  border-radius: 5px;

  &:hover {
    background: rgb(202 212 216 / 30%);
  }
}
.lp-page {
  span {
    margin-left: 4px;
  }
}
.count-shimmer-line {
  height: 12px;
  width: 25px;
}

.page-shimmer-line {
  height: 12px;
  width: 70px;
}
</style>

<style lang="scss">
.pagination-without-count-popover {
  .el-pager li.active,
  .el-pager li:hover {
    color: #39b2c2;
  }
}
</style>
