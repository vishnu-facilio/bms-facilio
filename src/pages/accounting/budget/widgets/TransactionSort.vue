<script>
import Sort from 'newapp/components/Sort'
export default {
  extends: Sort,
  props: ['currentViewDetail', 'currentMetaInfo'],
  computed: {
    viewDetail() {
      return this.currentViewDetail
    },
    moduleMeta: {
      get() {
        return this.currentMetaInfo
      },
    },
  },
}
</script>
