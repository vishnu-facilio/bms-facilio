<template>
  <div class="height-100">
    <slot name="title"></slot>

    <ul class="mT10 p0 side-bar-list">
      <v-infinite-scroll
        :loading="isLoading"
        @bottom="nextPage"
        :offset="20"
        class="overflow-y-scroll"
      >
        <div
          class="pointer list-item"
          v-for="(record, index) in lists"
          :key="index"
          v-bind:class="{ active: currentId === record.id }"
        >
          <slot :record="record"></slot>
        </div>
      </v-infinite-scroll>

      <spinner :show="isLoading" size="80"></spinner>
    </ul>
  </div>
</template>
<script>
import VInfiniteScroll from 'v-infinite-scroll'

export default {
  props: [
    'list',
    'isLoading',
    'activeRecordId',
    'total',
    'currentCount',
    'currentPage',
    'perPage',
  ],

  data() {
    return {
      currentId: -1,
      lists: [],
    }
  },

  components: {
    VInfiniteScroll,
  },

  computed: {
    page() {
      if (this.currentPage) return this.currentPage

      return parseInt(this.$route.query.page || 1)
    },
  },

  watch: {
    activeRecordId: {
      handler(newValue) {
        this.currentId = newValue
      },
      immediate: true,
    },

    list: {
      handler(newValue) {
        if (this.isLoading) this.lists = [...this.lists, ...newValue]
        else this.lists = [...newValue]

        this.$emit('update:isLoading', false)
      },
      immediate: true,
    },
  },

  methods: {
    nextPage() {
      let { page, total, currentCount, perPage } = this
      let { query } = this.$route
      let canTraverseNextPage =
        total > (page - 1) * (perPage || 50) + currentCount

      if (canTraverseNextPage) {
        this.$emit('nextPage')
        this.$router.push({ query: { ...query, page: page + 1 } })
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.side-bar-list {
  height: calc(100vh - 130px);
  display: flex;
  flex-direction: column;

  .list-item {
    border-bottom: 1px solid #f4f6f8;
  }
  .list-item:hover,
  .list-item.active {
    background: #eef5f7;
    cursor: pointer;
  }
}
</style>
