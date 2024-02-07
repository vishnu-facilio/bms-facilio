<script>
import { isEmpty } from '@facilio/utils/validation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import ModuleForm from '../custom-module/ModuleForm'
export default {
  extends: ModuleForm,
  mixins: [FetchViewsMixin],
  computed: {
    title() {
      return isEmpty(this.$route.params.id)
        ? this.$t('common.products.add_insurance')
        : this.$t('common.products.edit_insurance')
    },
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.formObj.module.displayName
      }
      return 'Insurance'
    },
    moduleDataId() {
      return this.$route.params.id
    },
  },
  methods: {
    afterSerializeHook({ data }) {
      if (isEmpty(this.moduleDataId)) {
        data['addedBy'] = { id: this.$portaluser.ouid }
      }
      return data
    },
  },
}
</script>
