<template>
  <el-dropdown @command="handleCommand" trigger="hover">
    <div class="pointer visibility-hide-actions mL5">
      <i class="el-icon-more rotate-90"></i>
    </div>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item command="isDefaultWebApp" :disabled="defaultWebApp" >{{ "Set as the default for web"}}</el-dropdown-item>
      <el-dropdown-item command="isDefaultMobileApp" :disabled="defaultMobileApp" >{{ "Set as the default for mobile" }}</el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['data'],
  data() {
    return {
      visible: false,
    }
  },
  computed: {
    ouId() {
      let id = this.$getProperty(this, '$route.params.id', -1)
      return parseInt(id)
    },
    defaultWebApp() {
        let { data } = this
        return this.$getProperty(data, 'row.isDefaultApp')
    },
    defaultMobileApp() {
        let { data } = this
        return this.$getProperty(data, 'row.isDefaultMobileApp')
    }
  },
  methods: {
    async setDefaultApp(command) {
      let { data } = this
      let { row } = data || {}
      console.log(row)
      let { application,ouid } = row || {}
      let { id } = application || {}
      let isMobile = command === "isDefaultMobileApp"
      let isWeb = command === "isDefaultWebApp"
      let { error } = await API.get(`v3/defaultApp/setUserDefaultApp`, {
        ouIds: [ouid],
        appId: id,
        isMobile,
        isWeb
      })
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.$message.success(
          this.$t('common._common.default_app_updated_successfully')
        )
        this.$emit('refresh')
      }
    },
    handleCommand(command) {
        if(!isEmpty(command)) {
            this.setDefaultApp(command)
        }
    },
  },
}
</script>
