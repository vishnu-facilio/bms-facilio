<template>
  <el-dropdown @command="handleCommand" trigger="hover">
    <div class="pointer visibility-hide-actions mL15">
      <i class="el-icon-more rotate-90"></i>
    </div>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item>{{ getDropdownLabel }}</el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>
<script>
import { API } from '@facilio/api'
export default {
  props: ['data'],
  data() {
    return {
      visible: false,
    }
  },
  computed: {
    getDropdownLabel() {
      let { data } = this
      let { showSwitch } = data || {}
      return showSwitch ? 'Disable switch' : 'Enable as switch'
    },
  },
  methods: {
    handleCommand() {
      let { data } = this
      let { showSwitch } = data || {}
      this.setSwitchStatus(!showSwitch)
    },
    async setSwitchStatus(status) {
      this.visible = false
      let { data } = this
      let { id } = data || {}
      let { error } = await API.post(`/v3/scopeVariable/setSwitchStatus`, {
        id,
        status,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        if (status) {
          this.$message.success('Switch Enabled')
        } else {
          this.$message.success('Switch Disabled')
        }
        this.$emit('refresh')
      }
    },
  },
}
</script>
