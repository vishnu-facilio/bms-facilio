<template>
  <div class="dragabale-card fahucard">
    <div class="ahu-card-conatiner" v-if="data" slot="reference">
      <div>
        <div
          class="ahuheader p10"
          v-if="data.name"
          :style="getFontSize(data.name)"
        >
          {{ data.name }}
        </div>
        <div class="ahustatus row p10">
          <img
            class="svg-icon col-6 status-spin"
            v-if="data.runStatus"
            src="~assets/product-icons/on-off-icon.svg"
          />
          <img
            class="svg-icon col-6"
            v-else
            src="~assets/product-icons/off-on-icon.svg"
          />
          <div class="col-6 data" v-if="data.runStatus">
            {{ $t('panel.tyre.FonCU') }}
          </div>
          <div class="col-6 dataoff" v-else>{{ $t('panel.tyre.off') }}</div>
        </div>
        <div class="tripstatus row p10">
          <div class="col-6 data" v-if="data.autoStatus">
            {{ $t('panel.tyre.auto') }}
          </div>
          <div v-else class="col-6 data">{{ $t('panel.tyre.manual') }}</div>
          <img
            class="svg-icon col-6"
            v-if="data.tripStatus"
            src="~assets/product-icons/bell-dot-icon.svg"
            :title="'TRIP STATUS: TRIPPED'"
            v-tippy="{
              distance: 0,
              interactive: true,
              theme: 'light',
              animation: 'scale',
              arrow: true,
            }"
          />
          <img
            class="svg-icon col-6"
            v-else
            src="~assets/product-icons/bell-icon.svg"
            :title="'TRIP STATUS: NORMAL'"
            v-tippy="{
              distance: 0,
              interactive: true,
              theme: 'light',
              animation: 'scale',
              arrow: true,
            }"
          />
        </div>
        <div class="valuestatus row" v-if="data.valveFeedback">
          <img
            class="svg-icon col-6"
            src="~assets/product-icons/volve-icon.svg"
          />
          <div class="col-6 data">{{ data.valveFeedback }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['widget'],
  data() {
    return {
      data: null,
    }
  },
  mounted() {
    this.loadCardData()
  },
  methods: {
    refresh() {
      this.loadCardData()
    },
    loadCardData() {
      if (this.widget) {
        let self = this
        let params = null
        if (
          this.widget &&
          this.widget.dataOptions &&
          this.widget.dataOptions.AHUcard &&
          this.widget.dataOptions.AHUcard.id
        ) {
          params = {
            paramsJson: {
              parentId: this.widget.dataOptions.AHUcard.id,
            },
            staticKey: this.widget.dataOptions.staticKey
              ? this.widget.dataOptions.staticKey
              : 'fahuStatusCard',
          }
        } else {
          params = {
            widgetId: this.widget.id,
          }
        }
        self.loading = true
        self.$http
          .post('dashboard/getCardData', params)
          .then(function(response) {
            self.data = response.data.cardResult.result
            if (self.data.valveFeedback > -1) {
              self.data.valveFeedback =
                Math.round(self.data.valveFeedback) + ' %'
            }
            self.loading = false
          })
          .catch(() => {})
      }
    },
    getFontSize(name) {
      if (name) {
        if (name.length < 40) {
          let size = 12
          if (name.length < 16) {
            size = 12
          } else if (name.length < 35) {
            size = 11
          } else if (name.length < 40) {
            size = 10
          }
          return 'font-size:' + size + 'px;'
        } else {
          return 'font-size: 10px;text-overflow: ellipsis;white-space: nowrap;'
        }
      }
    },
  },
}
</script>
<style>
.valuestatus {
  display: inline-flex;
  width: 100%;
  align-items: center;
  position: absolute;
  bottom: 0px;
  padding: 10px;
  left: 0;
  padding-left: 25px;
  padding-right: 25px;
  white-space: nowrap;
}
.tripstatus {
  position: absolute;
  top: 50%;
  width: 100%;
}
.ahustatus {
  position: absolute;
  top: 25%;
  width: 100%;
  padding-left: 30px;
  padding-right: 30px;
}
.ahuheader {
  background-color: #816bc5;
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.4px;
  text-align: center;
  color: #ffffff;
}
.ahustatus .data {
  font-size: 18px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.9px;
  text-align: center;
  color: #15a7a6;
}
.ahustatus .dataoff {
  font-size: 18px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.9px;
  text-align: center;
  color: #8a8d8d;
}
.tripstatus {
  background-color: #f7f7f7;
}
.tripstatus .data {
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #7c7a7f;
}
.valuestatus .data {
  font-size: 15px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #000000;
}
.status-spin {
  animation-name: spin;
  animation-duration: 1000ms;
  animation-iteration-count: infinite;
  animation-timing-function: linear;
}
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
.ahu-tool-tip-content {
  font-size: 11px;
  padding: 8px;
  white-space: nowrap;
  text-transform: uppercase;
}
.ahu-tool-tip-body {
  padding: 10px;
  border-bottom: 2px solid #816bc5;
}
.fahucard,
.ahu-card-conatiner {
  height: 100%;
}
</style>
