<script>
import { isEmpty } from '@facilio/utils/validation'
import ModuleList from 'src/beta/list/ModuleList.vue'

export default {
  extends: ModuleList,

  computed: {
    mainFieldName() {
      return 'subject'
    },
    slotList() {
      return [
        {
          name: 'id',
          isHardcodedColumn: true,
          columnAttrs: {
            width: 50,
            displayName: 'ID',
            fixed: true,
          },
        },

        {
          criteria: JSON.stringify({ name: 'subject' }),
          columnAttrs: {
            width: 350,
          },
        },

        {
          name: 'action',
          isActionColumn: true,
          columnAttrs: {
            width: 200,
            class: 'visibility-visible-actions',
            fixed: 'right',
          },
        },
      ]
    },
    recordsList() {
      let { records } = this
      records = records.map(record => {
        let { sysCreatedBy, sysModifiedBy } = record || {}
        if (!isEmpty(sysCreatedBy)) {
          let { avatarUrl: sysCreatedByAvatar, name } = sysCreatedBy
          sysCreatedBy = { name, url: sysCreatedByAvatar || '' }
        }
        if (!isEmpty(sysModifiedBy)) {
          let { avatarUrl: sysModifiedByAvatar, name } = sysModifiedBy
          sysModifiedBy = { name, url: sysModifiedByAvatar || '' }
        }
        return { ...record, sysCreatedBy, sysModifiedBy }
      })
      return records
    },
    modifiedViewDetails() {
      let { viewDetail } = this
      let { fields } = viewDetail || {}
      fields = fields.map(field => {
        if (
          ['sysCreatedBy', 'sysModifiedBy', 'resolvedBy'].includes(field.name)
        ) {
          return { ...field, type: 'avatar' }
        } else {
          return field
        }
      })
      return { ...viewDetail, fields }
    },
  },
  methods: {
    header() {
      let { canHideFilter } = this || {}
      return (
        <div class="flex items-center">
          {!canHideFilter && (
            <AdvancedSearchWrapper
              filters={this.filters}
              moduleName={this.moduleName}
              moduleDisplayName={this.moduleDisplayName}
            ></AdvancedSearchWrapper>
          )}
          {!canHideFilter && <span class="separator">|</span>}
        </div>
      )
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
