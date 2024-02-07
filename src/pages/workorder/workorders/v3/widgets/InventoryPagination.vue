<script>
import Pagination from 'src/components/list/FPagination'
export default {
  name: 'InventoryPagination',
  extends: Pagination,
  methods: {
    setPage(isReset) {
      if (this.pageNo) {
        this.$emit('onPageChanged', this.page)
      } else {
        if (!isReset) {
          this.$emit('onPageChanged', this.page)
        }
      }
    },
  },
}
</script>
