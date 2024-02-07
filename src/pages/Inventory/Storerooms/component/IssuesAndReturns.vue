<template>
  <el-tabs v-model="moduleName">
    <el-tab-pane :label="$t('common.header.items')" name="workorderItem">
      <IssueItems
        v-if="moduleName == 'workorderItem'"
        v-bind="$attrs"
        class="height-100"
      >
      </IssueItems>
    </el-tab-pane>
    <el-tab-pane :label="$t('common.header.tools')" name="workorderTools">
      <IssueTools
        v-if="moduleName == 'workorderTools'"
        v-bind="$attrs"
        class="height-100"
      >
      </IssueTools>
    </el-tab-pane>
  </el-tabs>
</template>
<script>
import IssueItems from 'src/pages/Inventory/Storerooms/component/IssueItems'
import IssueTools from 'src/pages/Inventory/Storerooms/component/IssueTools'

export default {
  components: {
    IssueItems,
    IssueTools,
  },
  data() {
    return {
      moduleName: 'workorderItem',
    }
  },
  computed: {},
}
</script>
