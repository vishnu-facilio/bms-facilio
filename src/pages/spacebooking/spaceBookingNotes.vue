<script>
import Notes from 'src/components/page/widget/common/NotesWidget.vue'

export default {
  extends: Notes,
  computed: {
    widgetTitle() {
      let { widget } = this
      return (widget || {}).title
        ? widget.title
        : this.$t('common._common.notes')
    },
  },
}
</script>
