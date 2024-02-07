<script>
import ModuleList from 'PortalTenant/custom-module/ModuleList'
export default {
  extends: ModuleList,
  data() {
    return {
      columnConfig: {
        fixedColumns: ['subject'],
        availableColumns: [],
      },
    }
  },
  computed: {
    slotList() {
      return [
        {
          name: 'id',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 110,
            label: 'ID',
            fixed: 'left',
          },
        },
        {
          criteria: JSON.stringify({ name: 'subject' }),
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
      return 'subject'
    },
  },
}
</script>
