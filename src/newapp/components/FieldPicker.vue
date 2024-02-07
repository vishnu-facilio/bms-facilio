<script>
import { isEmpty } from '@facilio/utils/validation'
import FieldPicker from '@/fields/FieldPicker'

export default {
  extends: FieldPicker,
  methods: {
    save() {
      this.$emit('save', this.list)
      if (!isEmpty(this.list)) {
        this.handleclose()
      }
    },
  },
}
</script>
