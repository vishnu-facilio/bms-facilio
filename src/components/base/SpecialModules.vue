<script>
import UserAvatar from '@/avatar/User'

export default {
  props: ['modulesList', 'moduleDisplayName', 'moduleName', 'selectedItemId'],
  components: {
    UserAvatar,
  },
  computed: {
    selectedId: {
      get() {
        return this.selectedItemId
      },
      set(value) {
        this.$emit('update:selectedItemId', value)
      },
    },
  },
  methods: {
    setSelectedItem() {
      this.$emit('setSelectedItem')
    },
  },
}
</script>
