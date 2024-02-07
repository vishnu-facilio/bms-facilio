<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import ModuleSummary from 'src/beta/summary/ModuleSummary.vue'
import NewMeter from 'src/beta/pages/meter/NewMeter'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  name: 'MeterSummary',
  extends: ModuleSummary,
  components: {
    NewMeter,
  },
  data() {
    return {
      canShowMeterCreation: false,
      selectedCategory: null,
      meterReading: [],
      notesModuleName: 'meternotes',
      attachmentsModuleName: 'meterattachments',
    }
  },
  computed: {
    moduleName() {
      return 'meter'
    },
  },
  watch: {
    record() {
      this.getAllMeterReadings()
    },
  },
  methods: {
    systemButtons() {
      return this.isPhysicalType(this.record) ? (
        <FButton
          appearance="tertiary"
          iconGroup="default"
          iconName="edit"
          iconButton={true}
          vOn:click={this.editRecord}
        />
      ) : null
    },
    async editRecord() {
      let { record } = this
      let { utilityType } = record || {}
      let { id: categoryId } = utilityType
      if (!isEmpty(categoryId)) {
        let selectedCategory = await this.getUtilityTypeById(categoryId)
        if (!isEmpty(selectedCategory)) {
          this.$set(this, 'selectedCategory', selectedCategory)
          let categoryModuleName = selectedCategory.moduleName
          let utilityTypeId = (selectedCategory || {}).id
          if (isWebTabsEnabled()) {
            let { name } = findRouteForModule('meter', pageTypes.EDIT) || {}
            name &&
              this.$router.push({
                name,
                query: { categoryModuleName, utilityTypeId },
              })
          } else {
            this.$router.push({
              name: 'meter',
              query: { categoryModuleName, utilityTypeId },
            })
          }
        }
      }
    },
    async getUtilityTypeById(id) {
      let { utilitytype, error } = await API.fetchRecord('utilitytype', { id })
      if (!error) {
        return utilitytype || {}
      } else {
        this.$message.error(error?.message || 'Error Occured')
      }
      return {}
    },
    async getAllMeterReadings() {
      let { record } = this
      let utilityTypeId = this.$getProperty(record, 'utilityType.id')
      let readings = await this.$util.loadMeterReadingFields(
        null,
        utilityTypeId,
        false,
        null,
        true
      )
      this.meterReading = readings || []
    },
    isPhysicalType(meter) {
      let { isVirtual } = meter || {}
      return !isVirtual
    },
    back() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('meter', pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
          })
      } else {
        this.$router.push({
          name: 'meter',
        })
      }
    },
  },
}
</script>
