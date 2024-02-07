<script>
import ModuleList from 'src/beta/list/ModuleList.vue'

export default {
  extends: ModuleList,
  name: 'VirtualMeterTemplateList',
  computed: {
    createBtnText() {
      return 'New Template'
    },
  }
}
</script>
