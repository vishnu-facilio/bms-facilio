<template>
  <div class="h100">
    <SpaceBookingOverview
      :widget="widget"
      :moduleName="moduleName"
      :details="details"
      :resizeWidget="resizeWidget"
      :primaryFields="primaryFields"
      :approvalMeta="approvalMeta"
      :hideTitleSection="hideTitleSection"
    ></SpaceBookingOverview>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import SpaceBookingOverview from '@/SpaceBookingOverview'

export default {
  props: [
    'widget',
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'primaryFields',
    'approvalMeta',
    'hideTitleSection',
  ],
  components: { SpaceBookingOverview },

  data() {
    return {
      activeName: 'first',
    }
  },
  computed: {
    ...mapGetters(['getUser']),
    list() {
      let { approvalMeta = {} } = this
      let approvers = this.$getProperty(approvalMeta, 'approvalList', []) || []

      return approvers.filter(approver => {
        if (approver.type === 'FIELD' && isEmpty(approver.value)) return false
        else return true
      })
    },
  },
  watch: {
    details() {
      this.$nextTick(this.autoResize)
    },
  },
  methods: {
    getStatus(approver) {
      return approver.actionBy
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['content-container']
        if (container) {
          let height = container.scrollHeight + 2000
          let width = container.scrollWidth
          this.resizeWidget({ height, width })
        }
      })
    },
  },
}
</script>
