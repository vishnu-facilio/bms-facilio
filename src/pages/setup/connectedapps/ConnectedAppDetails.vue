<template>
  <div v-if="isLoading">
    <Spinner :show="isLoading"></Spinner>
  </div>
  <div v-else class="d-flex height100 overflow-hidden">
    <div class="modules-left-pane">
      <div class="height100 overflow-hidden">
        <div class="modules-details">
          <div
            class="d-flex text-capitalize cursor-pointer"
            @click="redirectToConnectedAppsList()"
          >
            <inline-svg
              src="svgs/arrow"
              class="vertical-middle"
              iconClass="icon icon-sm mR10 arrow rotate-left mT2"
            ></inline-svg>
            <div
              class="f16 bold letter-spacing0_5 fc-black-color text-capitalize"
            >
              {{ connectedApp.name }}
            </div>
          </div>

          <div class="capp-logo d-flex mT30" v-if="connectedApp.logoUrl">
            <img :src="connectedApp.logoUrl" width="50px" />
          </div>

          <div class="fields-count d-flex mT30">
            <div
              class="text-capitalize f14 bold letter-spacing0_5 fc-black-color"
            >
              Widgets
            </div>
            <div class="f14 letter-spacing0_5 fc-black-color">
              {{
                connectedApp.connectedAppWidgetsList
                  ? connectedApp.connectedAppWidgetsList.length
                  : '0'
              }}
            </div>
          </div>

          <div class="state-flow d-flex mT15">
            <div
              class="text-capitalize f14 bold letter-spacing0_5 fc-black-color"
            >
              SAML
            </div>
            <div
              class="f14 letter-spacing0_5 status"
              :class="connectedApp.connectedAppSAML ? '' : 'disabled'"
            >
              {{
                connectedApp.connectedAppSAML ? 'Configured' : 'Not Configured'
              }}
            </div>
          </div>

          <div class="description mT15">
            <div class="title text-uppercase f11 bold letter-spacing0_5">
              {{ $t('custommodules.list.description') }}
            </div>
            <div class="f13 letter-spacing0_5 fc-black-color mT7 line-height20">
              {{ connectedApp.description ? connectedApp.description : '---' }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="modules-right-pane">
      <router-view class="border-separator"></router-view>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'

import Spinner from '@/Spinner'

export default {
  computed: {
    connectedAppId() {
      return this.$route.params.connectedAppId
    },
  },
  components: {
    Spinner,
  },
  beforeRouteLeave(to, from, next) {
    this.$store.dispatch('updateSetupSideBar', true).then(() => {
      next()
    })
  },
  created() {
    this.loadConnectedApp()
    this.$store.dispatch('updateSetupSideBar', false)
  },
  data() {
    return {
      isLoading: false,
      connectedApp: null,
    }
  },
  methods: {
    loadConnectedApp() {
      this.isLoading = true
      this.$http
        .get('/v2/connectedApps/get?connectedAppId=' + this.connectedAppId)
        .then(response => {
          this.connectedApp = response.data.result.connectedApp
          this.isLoading = false
        })
        .catch(function(error) {
          this.isLoading = false
          console.log(error)
        })
    },
    redirectToConnectedAppsList() {
      this.$router.push({
        name: 'connectedapp-list',
      })
    },
  },
}
</script>

<style lang="scss">
.modules-left-pane {
  flex: 0 0 21%;
  border-right: 1px solid #e6e6e6;
  border-left: 1px solid #e8e8e8;
  background: #fff;
}
.modules-right-pane {
  overflow: scroll;
  flex: 0 0 79%;
}
.modules-details {
  bottom: 0;
  padding: 25px 30px 30px 30px;
  .calender {
    width: 36px;
    height: 36px;
    border-radius: 50px;
    background-color: #efa25e;
  }
  .fields-count,
  .state-flow {
    div {
      flex: 0 0 50%;
    }
  }
  .fields-count,
  .state-flow,
  .description {
    border-top: solid 1px #f3f5f9;
    padding-top: 15px;
  }
  .description {
    .title {
      letter-spacing: 0.92px;
      color: #8ca1ad;
    }
  }
  .state-flow {
    .status {
      color: #46ca87;
      &.disabled {
        color: #eb696a;
      }
    }
  }
}
</style>
