<template>
  <div v-if="isLoading">
    <Spinner :show="isLoading"></Spinner>
  </div>
  <div v-else-if="!connectedAppsList || !connectedAppsList.length">
    <div class="no-connected-apps">
      <div class="inline">
        <img src="~assets/product-icons/app.svg" width="100px" />
      </div>
      <div class="message f14 mT10">
        {{ $t('common.products.no_connected_apps_available') }}
      </div>
    </div>
  </div>
  <div v-else class="container-scroll">
    <div class="d-flex flex-direction-row mL20 mR20 flex-wrap mB10">
      <div
        class="connectedapps-card mT25 mR25 visibility-visible-actions"
        v-for="(ca, index) in connectedAppsList"
        :key="index"
        @click="redirectToConnectedApp(ca)"
      >
        <div class="d-flex">
          <div class="flex-middle justify-content-center">
            <img v-if="ca.logoUrl" :src="ca.logoUrl" width="42" height="42" />
            <img v-else src="~assets/product-icons/app.svg" width="42px" />
          </div>
          <div class="flex-grow mL15">
            <div class="flex-middle justify-content-space">
              <div
                class="fc-black-color f16 bold letter-spacing0_5 text-capitalize"
              >
                {{ ca.name }}
              </div>
              <div>
                <i
                  v-on:click.stop
                  class="el-icon-delete pointer delete-icon-danger f15 visibility-hide-actions bold"
                  @click="deleteConnectedApp(ca)"
                ></i>
              </div>
            </div>
            <div class="fc-grey-text12 pT5">
              {{ ca.description }}
            </div>
          </div>
        </div>
        <div class="d-flex mT30">
          <div>
            <div class="fc-black-color f14 bold letter-spacing0_5">
              {{ $t('common._common.saml') }}
            </div>
            <div
              class="saml-status f13 mT5"
              :class="ca.connectedAppSAML ? '' : 'disabled'"
            >
              {{
                ca.connectedAppSAML
                  ? $t('common._common.configured')
                  : $t('common._common.not_configured')
              }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Spinner from '@/Spinner'

export default {
  components: {
    Spinner,
  },
  props: {
    isLoading: {
      type: Boolean,
    },
    connectedAppsList: {
      type: Array,
    },
  },
  methods: {
    redirectToConnectedApp(connectedApp) {
      this.$emit('redirect', connectedApp)
    },
    deleteConnectedApp(connectedApp) {
      this.$emit('delete', connectedApp)
    },
  },
}
</script>

<style lang="scss">
.connectedapps-card {
  padding: 20px 20px 20px 30px;
  border-radius: 2px;
  box-shadow: 0 4px 8px 2px rgba(0, 0, 0, 0.05);
  background-color: #ffffff;
  flex: 0 0 30%;
  cursor: pointer;
  transition: all 0.6s cubic-bezier(0.165, 0.84, 0.44, 1);
  .saml-status {
    line-height: 1.57;
    letter-spacing: 0.54px;
  }
  .saml-status {
    color: #46ca87;
    &.disabled {
      color: #eb696a;
    }
  }
}
.no-connected-apps {
  text-align: center;
  margin-top: 100px;
  .message {
    font-weight: 500;
    color: #324056;
    letter-spacing: 0.5px;
  }
}
</style>
