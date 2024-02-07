<template>
  <div class="grp-pagination-text">
    <el-tooltip
      :disabled="from <= 1"
      effect="dark"
      :content="prevContent()"
      placement="top-start"
    >
      <span
        @click="prev"
        class="el-icon-arrow-left arrow-16"
        :class="{ disable: from <= 1 }"
      ></span>
    </el-tooltip>
    <el-tooltip
      :disabled="!hasMoreList"
      effect="dark"
      :content="`${to + 1} - ${perPage + to}`"
      placement="top-end"
    >
      <span
        @click="next"
        class="el-icon-arrow-right arrow-16"
        :class="{ disable: !hasMoreList }"
      ></span>
    </el-tooltip>
  </div>
</template>
<script>
export default {
  props: ['hasMoreList', 'currentPage', 'perPage', 'currentCount'],
  data() {
    return {
      from: 0,
      to: 0,
      page: 1,
    }
  },
  mounted() {
    this.init()
  },
  watch: {
    currentCount() {
      this.init()
    },
    currentPage(val) {
      if (val !== this.page) {
        this.init()
      }
    },
  },
  methods: {
    prevContent() {
      let { from, perPage } = this
      return from != 1
        ? `${from - perPage} - ${from - 1}`
        : `${from} - ${perPage}`
    },
    init() {
      let { currentPage, currentCount, perPage } = this

      this.page = currentPage || 1
      this.from = (this.page - 1) * perPage + 1
      this.to = (this.page - 1) * perPage + currentCount
    },
    next() {
      if (!this.hasMoreList) return
      this.from = this.to + 1
      this.to += this.perPage
      this.page++
      this.$emit('update:currentPage', this.page) // Update currentPage value
    },
    prev() {
      if (this.from <= 1) return

      this.to = this.from - 1
      this.from -= this.perPage
      if (this.from <= 1) {
        this.from = this.page = 1
      } else {
        this.page--
      }
      this.$emit('update:currentPage', this.page) // Update currentPage value
    },
  },
}
</script>
<style lang="scss" scoped>
.grp-pagination-text {
  display: flex;

  .arrow-16 {
    cursor: pointer;
    letter-spacing: 0.5px;
    font-size: 14px;
    margin-top: auto;
    font-weight: bold;
    padding: 5px;
    border-radius: 4px;

    &:hover {
      background: #f3f4f7;
    }
    &.disable {
      cursor: not-allowed;
      color: #a9aacb;
    }
  }
}
</style>
