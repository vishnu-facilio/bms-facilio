<template>
  <el-popover v-model="showNewScreen" :width="600">
    <div class="remote-screen-caster">
      <div class="row">
        <div class="col-7 left-panel">
          <div class="panel-header pT10">
            {{ $t('panel.remote.remote_screens') }}
          </div>
          <div class="panel-content remote-screen-list">
            <spinner v-if="loading" :show="loading"></spinner>
            <div v-else-if="failed">
              {{ $t('panel.remote.failed') }}
            </div>
            <div
              v-else-if="!remoteScreens || !remoteScreens.length"
              style="padding-top: 30px; text-align: left;"
            >
              {{ $t('panel.remote.connected') }}
            </div>
            <ul v-else>
              <li v-for="(remoteScreen, index) in remoteScreens" :key="index">
                <div style="float: left; max-width: 66%; text-align: left;">
                  <div class="remote-screen-name">{{ remoteScreen.name }}</div>
                  <div
                    class="remote-screen-subname"
                    v-if="remoteScreen.screenContext"
                  >
                    {{
                      remoteScreen.screenContext.id === screen.id
                        ? 'Already casting this screen.'
                        : 'Currently casting ' +
                          remoteScreen.screenContext.name +
                          ' screen.'
                    }}
                  </div>
                  <div class="remote-screen-subname" v-else>
                    {{ $t('portal.remote.no') }}
                  </div>
                </div>
                <div style="float: right;">
                  <el-button
                    @click="castScreen(remoteScreen, -1)"
                    @mouseover.native="remoteScreen.hover = true"
                    @mouseleave.native="remoteScreen.hover = false"
                    v-if="
                      remoteScreen.screenContext &&
                        remoteScreen.screenContext.id === screen.id
                    "
                    :type="remoteScreen.hover ? 'danger' : 'info'"
                    size="mini"
                    >{{ remoteScreen.hover ? 'Uncast' : 'Casting' }}</el-button
                  >
                  <el-button
                    @click="castScreen(remoteScreen, screen.id)"
                    v-else
                    type="success"
                    size="mini"
                    >{{ $t('panel.remote.cst') }}</el-button
                  >
                </div>
                <div style="clear: both;"></div>
              </li>
            </ul>
          </div>
        </div>
        <div class="col-5 right-panel">
          <div class="panel-header">{{ $t('panel.remote.connect') }}</div>
          <div class="panel-content connect-remote-screen">
            <div class="code-info-badge">
              {{ $t('panel.remote.access') }}
              <b>{{ $t('panel.remote.web_tv') }}</b
              >{{ $t('panel.remote.cont') }}
            </div>
            <el-input
              v-model="newRemoteScreen.name"
              placeholder="Name (Eg: Reception)"
            ></el-input>
            <el-input
              v-model="newRemoteScreen.code"
              placeholder="Enter code (6 digit code)"
            ></el-input>
            <el-button
              type="primary"
              @click="connectRemoteScreen"
              class="setup-el-btn"
              size="mini"
              >{{ $t('panel.remote.conect') }}</el-button
            >
          </div>
        </div>
      </div>
    </div>
    <template slot="reference"><slot name="reference"></slot></template>
  </el-popover>
</template>

<script>
export default {
  props: ['screen', 'screens'],
  data() {
    return {
      loading: true,
      failed: false,
      remoteScreens: [],
      showNewScreen: false,
      hover: false,
      newRemoteScreen: {
        name: '',
        code: '',
      },
    }
  },
  mounted() {
    this.loadRemoteScreens()
  },
  methods: {
    loadRemoteScreens() {
      let self = this
      self.loading = true
      self.failed = false
      self.$http
        .get('/screen/getAllRemoteScreens')
        .then(function(response) {
          let list = response.data.remoteScreenContexts
          let remoteScrnList = []
          let alreadyCastingScreens = []
          if (list && list.length) {
            for (let l of list) {
              l.hover = false
              remoteScrnList.push(l)

              if (l.screenId === self.screen.id) {
                alreadyCastingScreens.push(l)
              }
            }
          }
          self.screen.remoteScreens = alreadyCastingScreens
          self.remoteScreens = remoteScrnList
          self.loading = false
        })
        .catch(function(error) {
          self.loading = false
          if (error) {
            self.failed = true
          }
        })
    },
    connectRemoteScreen() {
      let self = this
      if (
        !this.newRemoteScreen.code ||
        this.newRemoteScreen.code.trim().length !== 6
      ) {
        alert('Invalid code!')
      } else {
        let data = {
          code: this.newRemoteScreen.code,
          remoteScreenContext: {
            name: this.newRemoteScreen.name
              ? this.newRemoteScreen.name
              : 'Screen 1',
            screenId: this.screen.id,
          },
        }
        self.$http
          .post('/screen/connectRemoteScreen', data)
          .then(function(response) {
            if (response.data.remoteScreenContext) {
              self.newRemoteScreen = {
                name: '',
                code: '',
              }
              self.loadRemoteScreens()
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

    castScreen(remoteScreen, screenId) {
      let self = this
      let updateData = {
        remoteScreenContext: {
          id: remoteScreen.id,
          screenId: screenId,
        },
      }
      self.$http
        .post('/screen/updateRemoteScreen', updateData)
        .then(function(response) {
          self.loadRemoteScreens()

          if (self.screens) {
            for (let scrn of self.screens) {
              if (scrn.id !== screenId && scrn.remoteScreens) {
                scrn.remoteScreens = scrn.remoteScreens.filter(
                  rs => rs.id !== remoteScreen.id
                )
              }
            }
          }
        })
    },
  },
}
</script>

<style>
.remote-screen-caster .panel-header {
  font-weight: 500;
  text-transform: uppercase;
  font-size: 12px;
}

.remote-screen-caster .left-panel,
.remote-screen-caster .right-panel {
  padding: 10px 15px;
}

.remote-screen-caster .left-panel {
  border-right: 1px solid #f4f4f4;
  padding-right: 20px;
}

.remote-screen-caster .right-panel {
  padding-left: 20px;
}

.code-info-badge {
  padding: 8px;
  border: 1px solid #bbba;
  background: #fafafa;
  text-align: left;
  display: block;
  font-size: 11px;
  margin: 20px 0;
}

.panel-content .el-input,
.panel-content .el-button {
  margin: 10px 0px;
}

.remote-screen-list {
  max-height: 315px;
  overflow: scroll;
}

.remote-screen-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
  padding-top: 15px;
}

.remote-screen-list ul li {
  padding: 10px;
  background: #f4f4f4;
  margin: 6px 0;
}

.remote-screen-subname {
  font-size: 10px;
  padding-top: 4px;
  opacity: 0.8;
}
</style>
