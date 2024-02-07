<template>
  <el-popover
    :popper-class="'f-popover'"
    placement="left"
    v-model="visibility"
    :width="300"
  >
    <div class="fc-popover-content">
      <slot name="header">
        <div
          class="title uppercase f12 bold"
          style="letter-spacing: 1.1px; color:#000000"
        >
          {{ $t('panel.remote.connect') }}
        </div>
      </slot>
      <div>
        <div class="code-info-badge">
          {{ $t('panel.remote.access') }}
          <b> {{ $t('panel.remote.web_tv') }}</b> {{ $t('panel.remote.cont') }}
        </div>
        <el-form
          :model="remoteScreen"
          ref="addRemoteScreen"
          :label-position="'top'"
        >
          <el-form-item prop="name">
            <p class="grey-text2">{{ $t('panel.remote.name') }}</p>
            <el-input
              :autofocus="true"
              v-model="remoteScreen.name"
              placeholder="Name (Eg: Reception)"
              class="fc-input-full-border2"
            ></el-input>
          </el-form-item>
          <el-form-item prop="code">
            <p class="grey-text2">{{ $t('panel.remote.code') }}</p>
            <el-input
              :autofocus="true"
              v-model="remoteScreen.code"
              placeholder="Enter code (6 digit code)"
              class="fc-input-full-border2"
            ></el-input>
          </el-form-item>
          <el-form-item prop="screenId" v-if="screens && screens.length">
            <p class="grey-text2">{{ $t('panel.remote.cast') }}</p>
            <el-select
              v-model="remoteScreen.screenId"
              :filterable="true"
              placeholder="Select Screen"
              class="fc-input-full-border2 width100"
            >
              <el-option
                v-for="(screen, idx) in screens"
                :key="idx"
                :label="screen.name"
                :value="screen.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <div class="f-footer row" style="height:46px;">
      <slot name="footer">
        <button
          type="button"
          class="footer-btn footer-btn-secondary col-6"
          @click="closeNewDialog"
        >
          <span>{{ $t('panel.remote.cancel') }}</span>
        </button>
        <button
          type="button"
          class="footer-btn footer-btn-primary col-6"
          @click="connectRemoteScreen"
        >
          <span>{{ $t('panel.remote.connect') }}</span>
        </button>
      </slot>
    </div>
    <template slot="reference"><slot name="reference"></slot></template>
  </el-popover>
</template>

<script>
export default {
  props: ['screens'],
  data() {
    return {
      visibility: false,
      remoteScreen: {
        name: null,
        screenId: null,
        code: null,
      },
    }
  },
  methods: {
    connectRemoteScreen() {
      let self = this
      if (
        !this.remoteScreen.code ||
        this.remoteScreen.code.trim().length !== 6
      ) {
        alert('Invalid code!')
      } else {
        let data = {
          code: this.remoteScreen.code,
          remoteScreenContext: {
            name: this.remoteScreen.name ? this.remoteScreen.name : 'Screen 1',
            screenId: this.remoteScreen.screenId,
          },
        }
        self.$http
          .post('/screen/connectRemoteScreen', data)
          .then(function(response) {
            if (response.data.remoteScreenContext) {
              self.remoteScreen = {
                name: null,
                screenId: null,
                code: null,
              }
              self.$emit('save')
              self.visibility = false
            } else {
              alert('Invalid code!')
            }
          })
          .catch(function(error) {
            console.log(error)
            alert('Invalid code!')
          })
      }
    },
    closeNewDialog() {
      this.remoteScreen = {
        name: null,
        screenId: null,
        code: null,
      }
      this.visibility = false
      this.$emit('close')
    },
  },
}
</script>

<style>
.code-info-badge {
  padding: 8px;
  border: 1px solid #bbba;
  background: #fafafa;
  text-align: left;
  display: block;
  font-size: 11px;
  margin: 20px 0;
}
</style>
