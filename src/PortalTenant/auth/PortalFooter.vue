<template>
  <el-footer v-if="canShowFooter">
    <div class="footer-power">
      <span class="powered-txt">{{
        $t('tenant.announcement.powered_by')
      }}</span>
      <span>
        <img
          class="fc-custom-logo fc-footer-moro-logo"
          v-if="this.brandName == 'Moro'"
          src="~assets/facilio-moro.png"
        />
        <img
          v-else
          class="sp-logo"
          src="~assets/facilio-blue-logo.svg"
          style="width:60px; height:25px;margin-right: 5px;"
        />
      </span>
    </div>
  </el-footer>
</template>
<script>
import getProperty from 'dlv'
import RebrandMixin from 'util/rebrandMixin'
export default {
  mixin: [RebrandMixin],
  mounted() {
    // if (window.rebrandInfo && window.rebrandInfo.brandName) {
    //   this.brandName = window.rebrandInfo.brandName
    // }
    if (window.brandConfig && window.brandConfig.name) {
      this.brandName = window.brandConfig.name
    }

  },
  data() {
    return {
      brandName: '',
    }
  },
  computed: {
    canShowFooter() {
      return (
        getProperty(this, '$portalOrg.logoUrl') &&
        getProperty(this, '$portalOrg.id') !== 75
      )
    },
  },
}
</script>
<style>
body {
  background-color: #f0f3f4;
  color: #58666e;
}
.footer {
  padding-top: 30px;
}
</style>
<style>
@import '../../assets/styles/portal.css';
@import '../../assets/styles/portalform.css';
@import '../../assets/styles/white-theme.css';
@import '../../assets/styles/black-theme.scss';
@import '../../assets/styles/helper.scss';
@import '../../assets/styles/c3.css';
</style>
