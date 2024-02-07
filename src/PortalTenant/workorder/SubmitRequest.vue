<script>
import { isEmpty } from '@facilio/utils/validation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import ModuleForm from '../custom-module/ModuleForm'
export default {
  extends: ModuleForm,
  mixins: [FetchViewsMixin],
  computed: {
    moduleDisplayName() {
      return 'Request'
    },
    title() {
      let { moduleDataId } = this
      return isEmpty(moduleDataId)
        ? this.$t('common._common.create_a_request')
        : this.$t('common.header.edit_request')
    },
  },
  methods: {
    getSelectedFormId() {
      let defaultForm = this.forms.find(form =>
        form.name.startsWith('default_')
      )
      return defaultForm ? defaultForm.id : null
    },
  },
}
</script>
