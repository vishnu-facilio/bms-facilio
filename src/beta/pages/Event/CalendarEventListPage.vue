<script>
import ModuleList from 'src/beta/list/ModuleList.vue'
export default {
  extends: ModuleList,
  methods: {
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

              <FText
                fontsize="body3"
                fontWeight="medium"
                overflow="hidden"
                textOverflow="ellipsis"
              >
                {record[mainFieldName]}
              </FText>
            </FTooltip>
          )
        },
        [slotList[2].name]: ({ record } = {}) => {
          return (
            <FContainer display="flex" gap="containerMedium">
              <FIcon
                group="edit"
                name="edit-line"
                size="16"
                vOn:click={() => this.editModule(record)}
              />
            </FContainer>
          )
        },
      }
    },
  },
}
</script>
