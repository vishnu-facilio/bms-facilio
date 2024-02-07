<template>
  <FPagination
    :pageSize="pageSize"
    :small="small"
    :total="total"
    :pageCount="pageCount"
    :pagerCount="pagerCount"
    :currentPage="currentPage"
    @currentChange="currentChange"
  ></FPagination>
</template>

<script>
import { FPagination } from '@facilio/design-system'
export default {
  name: 'Pagination',
  components: { FPagination },
  props: ['totalCount', 'currentPageCount', 'perPage', 'currentPageNo'],
  data() {
    return {
      pageSize: 10,
      small: false,
      total: 1000,
      pageCount: 10,
      pagerCount: 5,
      currentPage: 1,
    }
  },
  computed: {
    pageQuery() {
      let { query } = this.$route || {}
      let { page } = query || {}
      return parseInt(page) || 1
    },
  },
  watch: {
    totalCount() {
      this.init()
    },
    pageQuery: {
      handler() {
        this.init()
      },
      immediate: true,
    },
  },
  methods: {
    init() {
      this.pageSize = this.perPage
      this.pageCount = this.currentPageCount
      this.total = this.totalCount

      if (this.currentPageNo) this.currentPage = this.currentPageNo
      else this.currentPage = this.pageQuery
    },
    currentChange(pageNo) {
      this.currentPage = pageNo

      if (this.currentPageNo) {
        this.$emit('onPageChanged', pageNo)
        this.$emit('update:currentPageNo', pageNo)
      } else {
        let query = {}
        Object.assign(query, this.$route.query)
        if (query) {
          query.page = pageNo
        } else {
          query = { page: pageNo }
        }
        this.$router.push({ query })
      }
    },
  },
}
</script>
