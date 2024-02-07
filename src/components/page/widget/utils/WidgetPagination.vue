<template>
  <div class="fc-widget-pagination">
    <div :class="{ 'fc-black-small-txt-12': !hideToggle }" v-if="total > 0">
      <span v-if="from !== to">{{ from }} -</span>
      <span>{{ to }}</span>
      <template v-if="!hideToggle">
        of {{ total }}
        <span
          class="el-icon-arrow-left fc-black-small-txt-12 fw-bold f16 pointer mL10 mR5"
          @click="from > 1 ? prev() : null"
          v-bind:class="{ disable: from <= 1 }"
        ></span>
        <span
          class="el-icon-arrow-right fc-black-small-txt-12 f16 pointer mR10 fw-bold"
          @click="to !== total ? next() : null"
          v-bind:class="{ disable: to === total }"
        ></span>
      </template>
    </div>
  </div>
</template>
<script>
export default {
  props: ['currentPage', 'perPage', 'total', 'hideToggle'],
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
    total() {
      this.init()
    },
    currentPage(val) {
      if (val !== this.page) {
        this.init()
      }
    },
  },
  methods: {
    init() {
      this.page = this.currentPage || 1
      this.from = (this.page - 1) * this.perPage + 1
      let to = this.from + this.perPage - 1
      this.to = this.total > to ? to : this.total
    },
    next() {
      this.from = this.to + 1
      this.to += this.perPage
      if (this.to > this.total) {
        this.to = this.total
      }
      this.page++
      this.updateCurrentPage()
    },
    prev() {
      this.to = this.from - 1
      this.from -= this.perPage
      if (this.from <= 1) {
        this.from = this.page = 1
      } else {
        this.page--
      }
      this.updateCurrentPage()
    },
    updateCurrentPage() {
      // Update currentPage value
      this.$emit('update:currentPage', this.page)
      this.$emit('onChange')
    },
  },
}
</script>
<style lang="scss">
.fc-widget-pagination {
  .disable {
    opacity: 0.5;
    cursor: not-allowed;
  }
  .el-icon-arrow-left {
    padding: 4px 3px;
    border-radius: 4px;
    border: 1px solid transparent;
    &:hover {
      color: #615e88;
      background: #f5f6f8;
      border: 1px solid #dae0e8;
    }
  }
  .el-icon-arrow-right {
    padding: 4px 3px;
    border-radius: 4px;
    border: 1px solid transparent;
    &:hover {
      color: #615e88;
      background: #f5f6f8;
      border: 1px solid #dae0e8;
    }
  }
}
</style>
