<script>
import { API } from '@facilio/api'
import ModuleList from 'src/beta/list/ModuleList.vue'
import { isEmpty } from '@facilio/utils/validation'
import { findRouteForModule, pageTypes } from '@facilio/router'

export default {
  extends: ModuleList,
  name: 'UtilityIntegrationCustomerList',
  computed: {
    createBtnText() {
      return this.$t('common.utility.new_utility_customer')
    },
  },
  methods: {
    async redirectToFormCreation() {
      let { moduleName } = this
      let { error, data } = await API.get(
        `v3/utilityIntegration/createUtilityAccounts`,
        {}
      )
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        let secretState = this.$getProperty(data, 'state', {})
        if (!isEmpty(secretState)) {
          let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

          name &&
            this.$router.push({
              name,
              query: { secretState },
            })
        }
      }
    },
    slotsContent() {
      let { slotList, mainFieldName } = this || {}
      return {
        [slotList[0].name]: ({ record } = {}) => {
          return (
            <div>
              <FText color="textMain">{'#' + record.id}</FText>
            </div>
          )
        },
        [slotList[1].criteria]: ({ record } = {}) => {
          return (
            <FTooltip placement="bottomLeft" mouseEnterDelay={1}>
              <FText
                slot="title"
                fontsize="body3"
                fontWeight="medium"
                color="backgroundCanvas"
              >
                {record[mainFieldName]}
              </FText>
              <FButton
                appearance="link"
                vOn:click={() => this.redirectToOverview(record.id)}
              >
                <FText
                  fontsize="body3"
                  fontWeight="medium"
                  overflow="hidden"
                  textOverflow="ellipsis"
                >
                  {record[mainFieldName]}
                </FText>
              </FButton>
            </FTooltip>
          )
        },
        [slotList[2].name]: ({ record } = {}) => {
          return (
            <FContainer display="flex" gap="containerMedium">
              <FIcon
                group="default"
                name="trash-can"
                size="16"
                vOn:click={() => this.deleteRecords([record.id])}
              />
            </FContainer>
          )
        },
      }
    },
  },
}
</script>
