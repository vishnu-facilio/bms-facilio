<script>
import ImportModule from './ImportModule'
import ImportSetting from 'src/pages/assets/import/v2/ImportSetting.vue'
import FieldMapping from 'src/pages/assets/import/v2/ColumnMapping'
import DataValidation from 'src/pages/assets/import/v2/DataValidation'

const MODULES_SUPPORTED = [
  'plannedmaintenance',
  'pmPlanner',
  'pmTrigger',
  'pmResourcePlanner',
  'resourceplanner',
  'pmTasksImport',
  'failureclass',
  'failurecode',
  'failurecodecauses',
  'failurecoderemedies',
  'labourCraftSkill',
]

export default {
  extends: ImportModule,
  components: {
    ImportSetting,
    FieldMapping,
    DataValidation,
  },
  created() {
    let { moduleName } = this.$route.params || {}
    if (!MODULES_SUPPORTED.includes(moduleName)) {
      this.$router.push({ name: 'notsupported' })
    }
  },
}
</script>
