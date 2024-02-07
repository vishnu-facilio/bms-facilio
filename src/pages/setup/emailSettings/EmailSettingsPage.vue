<template>
  <div class="d-flex flex-col">
    <portal-target
      :name="`wo-email-settings-header-${currentTab}`"
      :key="currentTab"
    ></portal-target>
    <el-tabs class="alarm-action-tab" v-model="currentTab">
      <el-tab-pane label="Email" name="email" lazy>
        <EmailSettings />
      </el-tab-pane>
      <el-tab-pane label="From Address" name="from-address" lazy>
        <FromAddressList />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import EmailSettings from './components/EmailSettings'
import FromAddressList from './fromAddress/FromAddressList'
export default {
  components: {
    EmailSettings,
    FromAddressList,
  },
  data() {
    return {
      currentTab: 'email',
    }
  },
}
</script>
