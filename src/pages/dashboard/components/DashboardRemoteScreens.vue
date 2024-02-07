<template>
  <div class="height100">
    <div class="setting-header2 dashboard-screen-header">
      <div class="setting-title-block">
        <div class="setting-form-title pT10">
          {{ $t('panel.remote.remote_screens') }}
        </div>
      </div>
      <template v-if="activeName && activeName === 'remote-screens'">
        <portal to="remote-screens">
          <div class="action-btn setting-page-btn">
            <add-remote-screen :screens="screens" @save="loadRemoteScreens">
              <el-button slot="reference" type="primary" class="pink-el-btn">{{
                $t('panel.remote.connect')
              }}</el-button>
            </add-remote-screen>
          </div>
        </portal>
      </template>
      <template v-else>
        <div class="action-btn setting-page-btn">
          <add-remote-screen :screens="screens" @save="loadRemoteScreens">
            <el-button slot="reference" type="primary" class="setup-el-btn">{{
              $t('panel.remote.connect')
            }}</el-button>
          </add-remote-screen>
        </div>
      </template>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout dashboard-container-scroll">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">
                  {{ $t('panel.remote.screen_name') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('panel.remote.casting') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="failed">
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('panel.remote.failed') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="!remoteScreens.length">
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('panel.remote.connected') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(remoteScreen, index) in remoteScreens"
                :key="index"
              >
                <td>
                  <div class="screen-name" v-if="!remoteScreen.editName">
                    {{ remoteScreen.name }}
                    <i
                      class="el-icon-edit pointer"
                      @click="remoteScreen.editName = true"
                      style="padding: 0 5px;"
                    ></i>
                  </div>
                  <div class="screen-name" v-else>
                    <textarea
                      @change="updateRemoteScreen(remoteScreen)"
                      @blur="remoteScreen.editName = false"
                      @keydown.enter.exact.prevent
                      @keyup.enter.exact="remoteScreen.editName = false"
                      autofocus="true"
                      autocomplete="off"
                      v-model="remoteScreen.name"
                      class="screen-editable-name"
                    ></textarea>
                  </div>
                </td>
                <td>
                  <el-select
                    @change="updateRemoteScreen(remoteScreen)"
                    v-model="remoteScreen.screenId"
                    :filterable="true"
                    placeholder="Select Screen"
                    v-if="screens && screens.length"
                    class="el-select-border"
                  >
                    <el-option
                      v-for="(screen, idx) in screens"
                      :key="idx"
                      :label="screen.name"
                      :value="screen.id"
                    ></el-option>
                  </el-select>
                  <span v-else>{{ $t('panel.remote.no_screens') }}</span>
                </td>
                <td style="width: 15%;">
                  <div
                    class="text-left actions"
                    style="margin-top:0px;margin-right: 15px;text-align:center; cursor: pointer;"
                  >
                    <i
                      class="el-icon-refresh pointer f16 fw-bold"
                      title="Refresh"
                      data-arrow="true"
                      v-tippy
                      @click="refreshRemoteScreen(remoteScreen)"
                    ></i>
                    &nbsp;&nbsp;&nbsp;&nbsp;<i
                      class="el-icon-delete pointer"
                      title="Delete"
                      data-arrow="true"
                      v-tippy
                      @click="deleteRemoteScreen(index, remoteScreen)"
                    ></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import http from 'util/http'
import AddRemoteScreen from './AddRemoteScreen'
export default {
  props: ['activeName'],
  components: {
    AddRemoteScreen,
  },
  data() {
    return {
      loading: true,
      failed: false,
      remoteScreens: [],
      screens: [],
    }
  },
  mounted() {
    this.loadRemoteScreens()
    this.loadScreens()
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
          if (list && list.length) {
            for (let l of list) {
              l.editName = false
              remoteScrnList.push(l)
            }
          }
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
    loadScreens() {
      let self = this
      self.$http.get('/screen/getAllScreens').then(function(response) {
        self.screens = response.data.screenContexts
      })
    },
    connectRemoteScreen() {
      let self = this
      if (!this.connectPasscode || this.connectPasscode.trim().length !== 6) {
        alert('Invalid code!')
      } else {
        http
          .post('/screen/connectRemoteScreen', { code: this.connectPasscode })
          .then(function(response) {
            if (response.data.remoteScreenContext) {
              self.showNewScreen = false
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
    updateRemoteScreen(remoteScreen) {
      let self = this
      let updateData = {
        remoteScreenContext: {
          id: remoteScreen.id,
          name: remoteScreen.name,
          screenId: remoteScreen.screenId ? remoteScreen.screenId : -1,
        },
      }
      self.$http.post('/screen/updateRemoteScreen', updateData)
    },
    refreshRemoteScreen(remoteScreen) {
      let self = this
      self.$http.post('/screen/refreshRemoteScreen', {
        remoteScreenContext: { id: remoteScreen.id },
      })
    },
    closeNewDialog() {
      this.connectPasscode = null
    },
    deleteRemoteScreen(index, remoteScreen) {
      let self = this
      let cstatus = confirm(
        'Are you sure want to delete "' + remoteScreen.name + '" remote screen?'
      )
      if (cstatus) {
        self.$http
          .post('/screen/deleteRemoteScreen', {
            remoteScreenContext: { id: remoteScreen.id },
          })
          .then(function(response) {
            self.remoteScreens.splice(index, 1)
          })
      }
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
.dashboard-container-scroll {
  height: calc(100vh - 260px);
  overflow-y: scroll;
  padding-bottom: 80px !important;
  padding-left: 0 !important;
  padding-right: 0 !important;
}
.pink-el-btn {
  background: #ef508f;
  border-color: #ef508f;
  font-size: 12px;
  text-transform: uppercase;
  font-weight: bold;
  letter-spacing: 0.4px;
}
</style>
