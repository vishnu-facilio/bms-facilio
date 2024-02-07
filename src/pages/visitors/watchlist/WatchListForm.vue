<script>
import VisitorForm from 'pages/visitors/visitor/VisitorForm.vue'

export default {
  extends: VisitorForm,
  computed: {
    moduleKey() {
      return 'watchListRecords'
    },
  },
}
</script>
