<script>
import ModuleList from 'PortalTenant/custom-module/ModuleList'

export default {
  extends: ModuleList,
  data() {
    return {
      columnConfig: {
        fixedColumns: ['facility'],
        availableColumns: [],
      },
    }
  },
  computed: {
    slotList() {
      return [
        {
          name: 'localId',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 110,
            label: 'ID',
            fixed: 'left',
          },
        },
        {
          criteria: JSON.stringify({ name: 'facility' }),
        },
        {
          name: 'editDelete',
          isActionColumn: true,
          columnAttrs: {
            width: 130,
            class: 'visibility-visible-actions',
            fixed: 'right',
          },
        },
      ]
    },
    mainFieldName() {
      return 'facility.name'
    },
  },
}
</script>
