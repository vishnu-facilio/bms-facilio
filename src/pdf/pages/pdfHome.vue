<template>
  <div class="pdf-generic-component" v-if="appName">
    <el-container class="fc-pdf-block">
      <el-main class="fc-pdf-main-component">
        <div>
          <WorkorderPdf
            :moduleName="moduleName"
            :id="id"
            v-if="moduleName === 'workorder'"
          ></WorkorderPdf>
          <AssetPdf v-if="moduleName === 'asset'"></AssetPdf>
        </div>
      </el-main>
    </el-container>
  </div>
</template>
<script>
import WorkorderPdf from '../pages/workorderPdf'
import AssetPdf from '../pages/assetPdf'
import Util from 'util/index'
import Vue from 'vue'
import { getApp } from '@facilio/router'
Vue.use(Util)
export default {
  data() {
    return {
      appName: getApp().linkName,
    }
  },
  components: {
    WorkorderPdf,
    AssetPdf,
  },
  computed: {
    moduleName() {
      return this.$route?.params?.moduleName || ''
    },
    linkName() {
      return this.$route?.params?.linkName === this.appName
        ? this.$route?.params?.linkName
        : ''
    },
    id() {
      return this.$route?.params?.id || ''
    },
  },
  methods: {},
}
</script>
<style lang="scss">
.fc-pdf-main-component {
  width: 100%;
  height: 100%;
  padding: 0;
  margin-left: auto;
  margin-right: auto;
  max-width: 1200px;
  margin-top: 20px;
}
</style>
