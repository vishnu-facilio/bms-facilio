<script>
import FormCreation from 'src/components/base/FormCreation.vue'

export default {
  extends: FormCreation,
  title() {
    return 'Quotation'
  },
  computed: {
    customClassForContainer() {
      return 'quote-container'
    },
  },
}
</script>
