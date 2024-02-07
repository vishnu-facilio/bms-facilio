<template>
  <div>
    <div
      :class="{ 'fc-black-small-txt-12 text-right pB15': !hideToggle }"
      v-if="total > 0"
    >
      <span v-if="from !== to">{{ from }} -</span>
      <span>{{ to }}</span>
      <template v-if="!hideToggle">
        of {{ total }}
        <span
          class="el-icon-arrow-left fc-black-small-txt-12 text-right pB15 fw-bold f16 pointer mL10 mR5"
          @click="from > 1 ? prev() : null"
          v-bind:class="{ disable: from <= 1 }"
        ></span>
        <span
          class="el-icon-arrow-right fc-black-small-txt-12 text-right pB15 f16 pointer mR10 fw-bold"
          @click="to !== total ? next() : null"
          v-bind:class="{ disable: to === total }"
        ></span>
      </template>
    </div>
  </div>
</template>
<script>
export default {
  props: ['total', 'perPage', 'hideToggle'],
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
  computed: {
    pageQuery() {
      return this.$route.query.page
    },
  },
  watch: {
    total() {
      this.init()
    },
    pageQuery(val) {
      if (val !== this.page) {
        this.init()
      }
    },
  },
  methods: {
    init() {
      this.page = this.pageQuery || 1
      this.from = (this.page - 1) * this.perPage + 1
      let to = this.from + this.perPage - 1
      this.to = this.total > to ? to : this.total
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
      this.setPage()
      this.init()
    },
    setPage() {
      this.$emit('fromto', this.from, this.to)
    },
  },
}
</script>

<style>
.el-icon-arrow-right.disable {
  color: #50506c;
  font-weight: bold;
  opacity: 0.4;
}
.el-icon-arrow-left.disable {
  color: #50506c;
  font-weight: bold;
  opacity: 0.4;
}
</style>
