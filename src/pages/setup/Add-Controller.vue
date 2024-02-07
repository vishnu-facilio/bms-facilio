<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Controllers</div>
        <div class="heading-description">List of all controllers</div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          v-if="addControllerlist.length > 0"
          class="plain"
          @click="downloadCertificate"
          >Download Certificate</el-button
        >
        <el-button
          type="primary"
          @click="
            showDialog = true
            isNew = true
          "
          class="setup-el-btn"
          >Add Controller</el-button
        >
        <new-addController
          v-if="showDialog"
          :isNew="isNew"
          :model="model"
          :visibility.sync="showDialog"
          @saved="addControllerSaved"
        ></new-addController>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">
                  CONTROLLER NAME
                </th>
                <th class="setting-table-th setting-th-text">MAC ADDRESS</th>
                <th class="setting-table-th setting-th-text">BUILDINGS</th>
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
            <tbody v-else-if="addControllerlist.length === 0">
              <tr>
                <td colspan="100%" class="text-center">
                  No Controller available.
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="addController in addControllerlist"
                :key="addController.id"
                v-loading="loading"
              >
                <td>{{ addController.name }}</td>
                <td>{{ addController.macAddr }}</td>
                <td>{{ prettyPrint(addController.buildingIds) }}</td>
                <td>
                  <div
                    class="text-left actions"
                    style="margin-top:-3px;margin-right: 15px;text-align:center;width: 120px;"
                  >
                    <i
                      class="el-icon-edit pointer"
                      title="Edit Controller"
                      v-tippy
                      @click="editController(addController)"
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
import NewAddController from 'pages/setup/agents/NewController'
export default {
  title() {
    return 'AddController'
  },
  components: {
    NewAddController,
  },
  data() {
    return {
      isNew: true,
      loading: false,
      addControllerlist: [],
      showDialog: false,
      buildings: {},
    }
  },
  mounted() {
    this.$store.dispatch('loadBuildings')
    this.loading = true
    this.$util
      .loadSpace(2)
      .then(response => {
        if (response.basespaces) {
          response.basespaces.forEach(i => {
            this.buildings[i.id] = i.name
          })
        }
        this.loadaddController()
        this.loading = false
      })
      .catch(_ => {
        this.loading = false
      })
  },
  methods: {
    prettyPrint(buildingIds) {
      if (!buildingIds || buildingIds.length === 0) {
        return '---'
      }
      let b = []
      buildingIds.forEach(i => b.push(this.buildings[i]))
      return b.join(', ')
    },
    loadaddController() {
      this.loading = true
      this.$http
        .get('/setup/controllersettings')
        .then(response => {
          this.loading = false
          this.addControllerlist = response.data.controllerSettings
            ? response.data.controllerSettings
            : []
        })
        .catch(_ => {
          this.loading = false
        })
    },
    downloadCertificate() {
      this.loading = true
      this.$http
        .get('/setup/downloadCertificate')
        .then(response => {
          this.loading = false
          window.location.href = response.data.url
        })
        .catch(_ => {
          this.loading = false
        })
    },
    addControllerSaved() {
      this.loadaddController()
    },
    editController(controller) {
      this.isNew = false
      this.model = controller
      this.showDialog = true
    },
  },
}
</script>
