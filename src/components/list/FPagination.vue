<template>
  <div>
    <div
      :class="{ 'fc-black-small-txt-12': !hideToggle }"
      v-if="total > 0"
      class="flex-center-row-space mT-5"
    >
      <el-popover placement="bottom" trigger="click" :disabled="disablePopup">
        <span
          slot="reference"
          class="p8 pointer"
          :class="[!disablePopup && 'button-hover']"
        >
          <span v-if="from !== to">{{ from }} - </span>
          <span>{{ to }}</span> of {{ total }}
        </span>
        <el-pagination
          layout="prev, pager, next"
          @current-change="handleCurrentChange"
          :total="total"
          :pager-count="5"
          :page-size.sync="perPage"
          :current-page.sync="page"
        >
        </el-pagination>
      </el-popover>
      <span v-if="!hideToggle">
        <span
          class="el-icon-arrow-left pagination-arrow fc-black-small-txt-12 f16 fw-bold pointer p5"
          @click="from > 1 ? prev() : null"
          :class="{ disable: from <= 1 }"
        ></span>
        <span
          class="el-icon-arrow-right pagination-arrow fc-black-small-txt-12 f16 fw-bold pointer p5"
          @click="to !== total ? next() : null"
          :class="{ disable: to === total }"
        ></span>
      </span>
    </div>
  </div>
</template>
<script>
import { isWebTabsEnabled } from '@facilio/router'

export default {
  props: {
    total: [Number, String],
    perPage: Number,
    hideToggle: {
      type: Boolean,
      default: false,
    },
    pageNo: Number,
    hidePopover: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      from: 0,
      to: 0,
      page: 1,
    }
  },
  created() {
    if (isWebTabsEnabled()) {
      document.addEventListener('keydown', this.keyDownHandler)
    }
  },
  mounted() {
    this.init()
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
  },
  watch: {
    total() {
      this.init()
    },
    pageQuery() {
      this.init()
    },
  },
  methods: {
    init() {
      this.page = this.pageQuery
      this.from = (this.page - 1) * this.perPage + 1
      if (
        this.from > this.total &&
        this.from &&
        this.from > 0 &&
        this.total &&
        this.total > 0
      ) {
        this.reset()
        return
      }
      let to = this.from + this.perPage - 1
      this.to = this.total > to ? to : this.total
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
      this.to += this.perPage
      if (this.to > this.total) {
        this.to = this.total
      }
      this.page++
      this.setPage()
    },
    prev() {
      this.to = this.from - 1
      this.from -= this.perPage
      if (this.from <= 1) {
        this.from = this.page = 1
      } else {
        this.page--
      }
      this.setPage()
    },
    reset() {
      this.page = 1
      this.setPage(true)
    },
    setPage(isReset) {
      if (this.pageNo) {
        this.$emit('onPageChanged', this.page)
      } else {
        let query = {}
        Object.assign(query, this.$route.query)
        if (query) {
          query.page = this.page
        } else {
          query = { page: this.page }
        }
        this.$router.push({ query })
        if (!isReset) {
          this.$emit('onPageChanged', this.page)
        }
      }
    },
    keyDownHandler(e) {
      if (e.shiftKey) {
        if (e.key === 'ArrowRight' && this.to !== this.total) {
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
  color: #50506c;
  font-weight: bold;
  opacity: 0.4;
  background: none;
}
.mT-5 {
  margin-top: -5px;
}
.button-hover {
  border-radius: 5px;

  &:hover {
    background: rgb(202 212 216 / 30%);
  }
}
</style>

<style lang="scss">
.el-pager li.active,
.el-pager li:hover {
  color: #39b2c2;
}
</style>
