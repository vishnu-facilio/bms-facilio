<template>
  <div class="scrollable">
    <div class="fc-space-layout p25 ">
      <div class="sp-div-row"></div>
      <space-nav @new="openCreateDialog"></space-nav>

      <div class="fc-space-list">
        <div class="sp-div-row"></div>
        <div class="fc-space-header row">
          <div class="col-12 col-sm-12 col-lg-12 pull-left self-center f18 fw5">
            All Zones&nbsp;<span class="fc-identify-color-2"
              >({{ zones.length }})</span
            >
          </div>
        </div>
        <div class="sp-div-row"></div>
        <div class="sp-div-row"></div>

        <div
          v-if="zones.length"
          class="fc-space-content-header row uppercase f11 secondary-color"
          style="border-top:1px solid #f3eeee;height: 73px;"
        >
          <div class="col-1 p20">
            <div class="sp-h"></div>
          </div>
          <div class="col-3 p20">
            <div class="sp-h">zone name</div>
          </div>
          <div class="col-3 p20">
            <div class="sp-h">zone location</div>
          </div>
          <div class="col-2 p20">
            <div class="sp-h">Area</div>
          </div>
          <div class="col-2 p20">
            <div class="sp-h">
              {{ $t('space.sites.max_occup') }}
            </div>
          </div>
          <div class="col-2 p20"></div>
        </div>
        <div
          v-if="loading"
          class="fc-space-conetnt row bg-color-1 fc-border-radius"
        >
          <spinner :show="loading"></spinner>
        </div>
        <div v-else>
          <div
            v-if="!zones.length"
            class="fc-space-conetnt row bg-color-1 fc-border-radius p30 center justify-center"
          >
            No zones available.
          </div>
          <div
            v-else
            v-for="(zone, index) in zones"
            :key="index"
            v-loading="loading"
            class="fc-space-conetnt row bg-color-1 fc-border-radius fc-site-row"
          >
            <div class="col-1">
              <div class="sp-c text-center">
                <space-avatar
                  size="xxlg"
                  :name="false"
                  :space="zone"
                ></space-avatar>
              </div>
            </div>
            <div
              class="col-3 p20 pointer"
              @click="openZoneOverview(zone.id, zone.siteId)"
            >
              <div class="sp-c">
                <div class="fw5 ellipsis">{{ zone.name }}</div>
                <div class="fc-identify-color-1 f12 sp-div-row-2">
                  #{{ zone.id }}
                </div>
              </div>
            </div>
            <div class="col-3 p20 self-center">
              <div class="sp-c zone-color">
                <div class="ellipsis">
                  {{
                    zone.baseSpaceContext ? zone.baseSpaceContext.name : '--'
                  }}
                </div>
              </div>
            </div>
            <div class="col-2 p20 self-center">
              <div class="sp-c">
                <div class="ellipsis">{{ zone.area > -1 ? zone.area : 0 }}</div>
              </div>
            </div>
            <div class="col-2 p20 self-center">
              <div class="sp-c">
                <div class="ellipsis">
                  {{ zone.maxOccupancy > -1 ? zone.maxOccupancy : '---' }}
                </div>
              </div>
            </div>
            <div class="col-1 p20 self-center hover-action-zone">
              <i
                class="el-icon-edit edit-icon-color"
                title="Edit Space"
                data-arrow="true"
                v-tippy
                @click="editZone(zone)"
              ></i>
              <i
                class="el-icon-delete pointer edit-icon-color"
                style="padding-left: 18px"
                data-arrow="true"
                title="Delete Space"
                v-tippy
                @click="deleteZone(zone)"
              ></i>
            </div>
          </div>
        </div>
      </div>
      <div class="sp-div-row"></div>
      <div class="sp-div-row"></div>
      <div class="sp-div-row"></div>
      <div class="sp-div-row"></div>
    </div>
    <q-modal
      ref="createNewZoneModel"
      position="right"
      content-classes="fc-create-record"
    >
      <q-btn class="fc-model-close" flat @click="closeCreateDialog">
        <q-icon name="close" />
      </q-btn>
      <form-layout
        :layout="formlayout"
        @onsave="saveZone"
        @cancel="closeCreateDialog"
      >
        <el-col :md="24" :lg="24">
          <div class="bold pT15 pB15">Attach Spaces</div>
          <el-select v-model="attachedSpaces" filterable multiple>
            <el-option
              v-for="(space, index) in spaces"
              :key="index"
              :label="space.name + ' (' + space.spaceTypeVal + ')'"
              :value="space.id"
            ></el-option>
          </el-select>
        </el-col>
      </form-layout>
    </q-modal>
    <new-zone
      :showNewZone.sync="showCreateDialog"
      :zoneobj="zoneobj"
      @refreshlist="updateList"
      :isnew="isNew"
      v-if="showCreateDialog"
    ></new-zone>
  </div>
</template>
<script>
import NewZone from 'pages/spacemanagement/newzone'
import SpaceAvatar from '@/avatar/Space'
import spaceNav from 'pages/spacemanagement/SpaceNav'
import FormLayout from '@/Formlayout'
import { QModal, QBtn, QIcon, Alert } from 'quasar'
import { findRouteForTab, isWebTabsEnabled, tabTypes } from '@facilio/router'

export default {
  components: {
    NewZone,
    SpaceAvatar,
    spaceNav,
    FormLayout,
    QModal,
    QBtn,
    QIcon,
  },
  data() {
    return {
      isNew: true,
      zoneobj: {},
      showCreateDialog: false,
      loading: true,
      formlayout: {
        title: 'New Zone',
        metaurl: '/zone/layout',
      },
      attachedSpaces: [],
    }
  },
  mounted() {
    this.loadZones()
  },

  title() {
    return 'Zones'
  },
  computed: {
    zones() {
      return this.$store.state.space.zones
    },
    spaces() {
      return this.$store.state.spaces
    },
  },
  methods: {
    getParentName(zone) {
      if (zone.floorId > 0) {
        return 'Floor'
      } else if (zone.buildingId > 0) {
        return 'Building'
      } else if (zone.siteId > 0) {
        return 'Site'
      } else {
        return '--'
      }
    },
    deleteZone(zone) {
      let self = this
      let promptObj = {
        title: 'Delete zone',
        message: 'Are you sure you want to delete this zone?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      self.$dialog.confirm(promptObj).then(function(value) {
        if (value) {
          self.$http
            .post('/zone/delete', { zoneId: zone.id })
            .then(function(response) {
              if (
                response.data &&
                !response.data.error &&
                response.data.zoneId
              ) {
                let doZones = self.zones.find(
                  r => r.id === response.data.zoneId
                )
                if (doZones) {
                  self.zones.splice(self.zones.indexOf(doZones), 1)
                }
                self.$message.success('Deleted Successfully')
              } else if (response.data && response.data.error) {
                self.$message.error(response.data.error)
              }
            })
        }
      })
    },
    updateList() {
      this.loadZones()
    },
    loadZones() {
      let self = this
      self.loading = true
      self.$store
        .dispatch('space/fetchZones')
        .then(function() {
          self.loading = false
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
          }
        })
    },
    findRoute() {
      let tabType = tabTypes.CUSTOM
      let config = { type: 'portfolio' }
      let { name } = findRouteForTab(tabType, { config }) || {}

      return name ? this.$router.resolve({ name }).href : null
    },
    openZoneOverview(id) {
      let parentPath = isWebTabsEnabled()
        ? this.findRoute()
        : '/app/home/portfolio'

      if (parentPath) {
        this.$router.push({ path: `${parentPath}/zone/${id}/overview` })
      }
    },
    openCreateDialog() {
      this.isNew = true
      this.showCreateDialog = true
    },
    closeCreateDialog() {
      this.$refs.createNewZoneModel.close()
      this.loadZones()
      this.attachedSpaces = []
    },
    editZone(editZone) {
      this.isNew = false
      this.zoneobj = this.$helpers.cloneObject(editZone)
      this.showCreateDialog = true
    },
    saveZone(formdata) {
      let data = {
        zone: formdata.zone,
        spaceId: this.attachedSpaces,
      }
      let self = this
      self.$http
        .post('/zone/add', data)
        .then(function(response) {
          if (typeof response.data === 'object') {
            const alert1 = Alert.create({
              html: 'Zone created successfully!',
              color: 'positive',
              position: 'top-center',
            })
            setTimeout(function() {
              alert1.dismiss()
            }, 1500)

            self.closeCreateDialog()
          } else {
            const alert3 = Alert.create({
              html: 'Zone creation failed!',
              color: 'negative',
              position: 'top-center',
            })
            setTimeout(function() {
              alert3.dismiss()
            }, 1500)
          }
        })
        .catch(function(error) {
          const alert2 = Alert.create({
            html: 'Zone creation failed! [ ' + error + ']',
            color: 'negative',
            position: 'top-center',
          })
          setTimeout(function() {
            alert2.dismiss()
          }, 1500)
        })
    },
  },
}
</script>
<style>
.hover-action-zone {
  visibility: hidden;
}
.fc-space-conetnt:hover .hover-action-zone {
  visibility: visible;
}
.fc-space-conetnt {
  cursor: pointer;
}
</style>
