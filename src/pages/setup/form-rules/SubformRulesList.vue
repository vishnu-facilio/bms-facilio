<script>
import FormRulesList from './FormRulesList'
export default {
  name: 'SubformRulesList',
  extends: FormRulesList,
  computed: {
    isSubForm() {
      return true
    },
    addRuleText() {
      return this.$t('forms.rules.add_new_subform_rule')
    },
  },
}
</script>
