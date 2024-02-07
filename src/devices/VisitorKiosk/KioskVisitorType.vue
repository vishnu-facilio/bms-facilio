<template>
  <div class="fc-kiosk-form-con">
    <div
      class="fc-kiosk-form position-relative"
      style="align-items: initial;"
      v-if="!loading"
    >
      <div class="fc-check-in-f22 kiosk-areyou-txt pB25">I'm here as a</div>
      <div class="kiosk-visior-select-con">
        <div
          class="kiosk-visior-avatar fc-scale-in-center"
          v-for="(visitorType, index) in visitorTypes"
          :key="index"
          @click="handleTypeSelect(visitorType)"
        >
          <img
            :src="
              require('src/assets/' + visitorType.visitorLogoObj.value + '.svg')
            "
            style="width: 50px; height: 54px;"
          />
          <div class="fc-kiosk-16 text-uppercase font-bold">
            {{ visitorType.name }}
          </div>
        </div>
      </div>
    </div>
    <div class="kiosk-page-loading" v-else-if="loading">
      <facilio-new-loader> </facilio-new-loader>
    </div>
  </div>
</template>
<script>
import facilioNewLoader from 'src/components/facilio-loading'

import { mapGetters } from 'vuex'
export default {
  props: ['sharedData'],
  data() {
    return {
      loading: true,
      visitorTypes: [],
    }
  },
  components: {
    facilioNewLoader,
  },
  created() {
    this.loadVisitorTypes()
  },
  computed: {
    ...mapGetters(['currentAccount']),
  },
  methods: {
    handleTypeSelect(visitorType) {
      this.$emit('nextStep', { visitorTypeId: visitorType.id })
    },
    async loadVisitorTypes() {
      let resp = await this.$http.get(
        '/v2/module/data/list?moduleName=visitortype'
      )
      this.visitorTypes = resp.data.result.moduleDatas.filter(
        visitorType => visitorType.enabled
      )

      this.loading = false
      return
    },
  },
}
</script>
<style lang="scss">
// .kiosk-areyou-txt{
//   position: absolute;
//   top: 200px;
//   z-index: 300;
//   left: 0;
// }
</style>
