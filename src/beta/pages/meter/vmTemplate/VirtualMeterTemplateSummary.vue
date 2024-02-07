<script>
import ModuleSummary from 'src/beta/summary/ModuleSummary.vue'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  name: 'VirtualMeterTemplateSummary',
  extends: ModuleSummary,
  components: {},
  data() {
    return {}
  },
  computed: {},
  methods: {
    systemButtons() {
      return this.canPublishVMTemplate(this.record) ? (
        <FContainer style="display:flex;">
          <FButton
            appearance="tertiary"
            iconGroup="default"
            iconName="edit"
            iconButton={true}
            vOn:click={this.editRecord}
          />
          <FButton
            appearance="primary"
            size="small"
            vOn:click={() => this.publishVMTemplate(this.record.id)}
          >
            Publish
          </FButton>
        </FContainer>
      ) : (
        <FButton
          appearance="tertiary"
          iconGroup="default"
          iconName="edit"
          iconButton={true}
          vOn:click={this.editRecord}
        />
      )
    },
    canPublishVMTemplate(vmt) {
      if (!isEmpty(vmt)) {
        let { vmTemplateStatusEnum } = vmt || {}
        return vmTemplateStatusEnum === 'UN_PUBLISHED'
      }
      return false
    },
    async publishVMTemplate(id) {
      let { error } = await API.post('v3/virtualMeterTemplate/publish', {
        vmTemplateId: id,
      })

      if (!error) {
        this.$message.success(this.$t('asset.virtual_meters.vmt_published'))
        this.refreshData()
      } else {
        this.$message.error(error.message || 'Error Occurred')
      }
    },
    back() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('virtualMeterTemplate', pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
          })
      } else {
        this.$router.push({
          name: 'virtualMeterTemplate',
        })
      }
    },
  },
}
</script>
