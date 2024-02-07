<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right addtenant-container setup-dialog50"
    :before-close="handleclose"
  >
    <error-banner
      :error.sync="error"
      :errorMessage.sync="errorText"
    ></error-banner>
    <el-form>
      <!-- Header -->
      <div class="new-header-container">
        <div class="new-header-modal">
          <div class="setup-modal-title">
            ADD TENANT
          </div>
        </div>
      </div>
      <!-- body -->
      <div class="tenant-body">
        <el-row>
          <el-col :span="6">
            <div class="logo-upload">
              <span class="input-upload tenant-upload">
                <input
                  ref="tenantLogo"
                  type="file"
                  class="upload"
                  style="width: 200px;height:100px;"
                  @change="getimage($event.target.files[0])"
                />
                <span class="upload-icon-block">
                  <img src="~assets/image-upload.svg" class="upload-img" />
                  <img
                    v-if="logoImageUrl"
                    :src="logoImageUrl"
                    class="upload-img2"
                  />
                  <div class="upload-txt">Upload Logo</div>
                </span>
                <span class="upload-text">
                  choose photo
                </span>
              </span>
            </div>
          </el-col>
          <el-col :span="18">
            <el-col :span="12">
              <el-form-item prop="name">
                <p class="grey-text2 pB10">Tenant Name</p>
                <el-input
                  :autofocus="true"
                  v-model="model.name"
                  class="fc-input-full-border2"
                  placeholder="New Tenant Name"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" class="pL10">
              <el-form-item prop="name">
                <p class="grey-text2 pB10">Building/ Zone</p>
                <el-select
                  v-model="model.spaceId"
                  filterable
                  @change=";(energyUtilityAssets = []), loadEnergyMeters()"
                  placeholder="Select Space"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="space in buildings"
                    :key="space.id"
                    :label="space.name"
                    :value="space.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item prop="description">
                <p class="grey-text2 pB10">Description</p>
                <el-input
                  :autosize="{ minRows: 2, maxRows: 4 }"
                  resize="none"
                  v-model="model.description"
                  class="edittext fc-input-full-border-select2 width100"
                  placeholder="Enter the description"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-col>
        </el-row>
        <el-row class="mT30" v-if="sites.length !== 1">
          <el-col :span="8">
            <el-col :span="1">
              <img
                src="~assets/Shape.svg"
                style="height:18px;width:18px;position:relative;top:4px;"
                class="delete-icon"
              />
            </el-col>
            <el-col :span="20" class="mL15 utility-det-block">
              <p class="utility-list-H">Site</p>
              <p class="utility-list-D">
                Select Site to be associated with the tenant
              </p>
            </el-col>
          </el-col>

          <el-col style="padding-left:0px;" :span="11" class="mL40">
            <el-form-item prop="name">
              <p class="grey-text2 pB10">Site</p>
              <el-select
                filterable
                v-model="model.siteId"
                @change=";(energyUtilityAssets = []), (spacesAssociated = [])"
                style="width:100%"
                class="fc-input-full-border-select2"
                placeholder="Select Site"
              >
                <el-option
                  v-for="site in sites"
                  :key="site.id"
                  :label="site.name"
                  :value="site.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row class="mT30">
          <el-col :span="8">
            <el-col :span="1">
              <img
                src="~assets/Shape.svg"
                style="height:18px;width:18px;position:relative;top:4px;"
                class="delete-icon"
              />
            </el-col>
            <el-col :span="20" class="mL15 utility-det-block">
              <p class="utility-list-H">Space</p>
              <p class="utility-list-D">
                Select Spaces to be associated with the tenant zone
              </p>
            </el-col>
          </el-col>
          <el-col :span="11" class="mL40">
            <el-select
              v-model="spacesAssociated"
              multiple
              collapse-tags
              filterable
              placeholder="Select Space"
              @change="loadEnergyMeters()"
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="space in spaces"
                :key="space.id"
                :label="space.name"
                :value="space.id"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>

        <div class="mT30">
          <div class="fc-text-pink">Contact details</div>
          <p class="grey-text2 line-height10">
            Please enter name and contact details below
          </p>
        </div>

        <el-row class="mT30">
          <el-col :span="12">
            <el-form-item prop="contactname">
              <p class="grey-text2 pB10">Name</p>
              <el-input
                :autofocus="true"
                v-model="model.contact.name"
                class="fc-input-full-border-select2"
                placeholder="Name"
                style="width: 230px;"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item prop="email">
              <p class="grey-text2 pB10">Email</p>
              <el-input
                v-model="model.contact.email"
                placeholder="Enter Email"
                class="fc-input-txt fc-desc-input fc-input-full-border-select2"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="mT30">
          <div>
            <el-col :span="12">
              <el-switch
                v-model="model.contact.portal_verified"
                class="Notification-toggle"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#e5e5e5"
              ></el-switch>
              <span class="portal pL10">
                Allow Portal Access
              </span>
            </el-col>
          </div>
        </el-row>

        <div class="mT30">
          <div class="fc-text-pink">UTILITY DETAILS</div>
          <p class="grey-text2 line-height10">
            Map the utility consumed by the tenants
          </p>
        </div>
        <el-row class="mT30 mL10">
          <el-col :span="8">
            <el-col :span="1">
              <img
                src="~assets/Shape.svg"
                style="height:18px;width:18px;position:relative;top:4px;"
                class="delete-icon"
              />
            </el-col>
            <el-col :span="20" class="mL15 utility-det-block">
              <p class="utility-list-H">Energy</p>
              <p class="utility-list-D">
                Select Energy meter associated with the tenant
              </p>
            </el-col>
          </el-col>
          <el-col :span="14" class="mL20">
            <el-select
              v-model="energyUtilityAssets"
              multiple
              collapse-tags
              filterable
              placeholder="Select Energy Meter"
              class="utility-select fc-input-full-border-select2"
            >
              <el-option
                v-for="meter in energyMeters"
                :key="meter.id"
                :label="meter.name"
                :value="meter.id"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>

        <el-row class="mT30 mL10">
          <el-col :span="8">
            <el-col :span="1">
              <img
                src="~assets/drop.svg"
                style="height:18px;width:18px;position:relative;top:4px;"
                class="delete-icon"
              />
            </el-col>
            <el-col :span="20" class="mL15 utility-det-block">
              <p class="utility-list-H">Water</p>
              <p class="utility-list-D">
                Select Water service associated with the tenant
              </p>
            </el-col>
          </el-col>
          <el-col :span="14" class="mL20">
            <el-select
              multiple
              v-model="value"
              placeholder="Select Water Service"
              class="utility-select fc-input-full-border-select2"
            >
              <el-option
                v-for="item in waterAssets"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>

        <el-row class="mT30 mL10">
          <el-col :span="8">
            <el-col :span="1">
              <img
                src="~assets/pressure.svg"
                style="height:18px;width:18px;position:relative;top:4px;"
                class="delete-icon"
              />
            </el-col>
            <el-col :span="20" class="mL15 utility-det-block">
              <p class="utility-list-H">Natural Gas</p>
              <p class="utility-list-D">
                Select Natural Gas service associated with the tenant
              </p>
            </el-col>
          </el-col>
          <el-col :span="14" class="mL20">
            <el-select
              multiple
              v-model="value"
              placeholder="Select Natural Gas"
              class="utility-select fc-input-full-border-select2"
            >
              <el-option
                v-for="item in gasAssets"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>

        <el-row class="mT30 mL10">
          <el-col :span="8">
            <el-col :span="1">
              <img
                src="~assets/pressure.svg"
                style="height:18px;width:18px;position:relative;top:4px;"
                class="delete-icon"
              />
            </el-col>
            <el-col :span="20" class="mL15 utility-det-block">
              <p class="utility-list-H">BTU</p>
              <p class="utility-list-D">
                Select BTU associated with the tenant
              </p>
            </el-col>
          </el-col>
          <el-col :span="14" class="mL20">
            <el-select
              multiple
              v-model="value"
              placeholder="Select BTU"
              class="utility-select fc-input-full-border-select2"
            >
              <el-option
                v-for="item in btuAssets"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
        <el-row
          align="middle"
          style="margin:0px;padding-top:40px;"
          :gutter="50"
        >
          <el-col style="padding-left:0px;" :span="12">
            <div class="fc-input-label-txt">In Date</div>
            <div class="form-input">
              <el-date-picker
                v-model="model.inTime"
                class="form-item calendaer-input-paading fc-input-full-border-select2"
                type="date"
                style="width: 100%;"
                suffix-icon="el-icon-date"
              />
            </div>
          </el-col>
          <el-col style="padding-left:0px;" :span="12">
            <div class="fc-input-label-txt">Out Date</div>
            <div class="form-input">
              <el-date-picker
                v-model="model.outTime"
                class="form-item calendaer-input-paading fc-input-full-border-select2"
                type="date"
                style="width: 100%;"
              />
            </div>
          </el-col>
        </el-row>
      </div>
      <!-- footer -->
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >CANCEL</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="save()"
          :loading="saving"
          >{{ saving ? 'Saving...' : 'SAVE' }}</el-button
        >
      </div>
    </el-form>
  </el-dialog>
</template>

<script>
import ErrorBanner from '@/ErrorBanner'
import { mapState, mapGetters } from 'vuex'

export default {
  props: ['visibility', 'tenant', 'rule', 'userlist'],
  watch: {
    'model.siteId': {
      handler: function(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.$util
            .loadSpace(
              [2, 3, 4],
              null,
              [{ key: 'siteId', value: newVal }],
              null
            )
            .then(data => {
              this.spaces = data.basespaces
            })
        }
      },
      deep: true,
    },
  },
  data() {
    return {
      saving: false,
      savings: false,
      popoversavings: false,
      visible: false,
      error: false,
      errorText: '',
      user: {
        name: null,
        email: null,
        phone: null,
      },
      model: {
        name: '',
        description: '',
        inTime: null,
        outTime: null,
        contact: {
          name: '',
          email: null,
          portal_verified: null,
        },
        spaces: [],
        utilityAssets: [],
        siteId: null,
      },
      zone: {
        name: '',
        siteId: null,
        area: 0,
      },
      energyUtilityAssets: [],
      spacesAssociated: [],
      spaceObjectsMap: {},
      buildings: [],
      energyMeters: [],
      waterAssets: [],
      gasAssets: [],
      btuAssets: [],
      spaces: {},
      allChildren: [],
      logoImageUrl: null,
      zoneId: null,
      value: '',
    }
  },
  components: {
    ErrorBanner,
  },
  mounted() {
    if (this.sites.length === 1) {
      this.model.siteId = this.sites[0].id
    }
    this.$util.loadSpace(2).then(response => {
      this.buildings = response.basespaces
    })
    if (this.tenant) {
      this.$helpers.copy(this.model, this.tenant)
      this.loadEnergyMeters()
      this.energyUtilityAssets = this.tenant.utilityAssets
        .filter(uasset => uasset.utilityEnum === 'ENERGY')
        .map(uasset => uasset.assetId)
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadEnergyMeters')
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadBuildings')
  },

  computed: {
    ...mapState({
      sites: state => state.site,
    }),
    ...mapGetters(['getAssetCategoryByType']),
  },

  methods: {
    loadSpaces() {
      let self = this
      let spaceType = [2, 3, 4]
      self.$util.loadSpace(spaceType).then(function(response) {
        if (response.basespaces) {
          self.spaces = response.basespaces.filter(
            space =>
              space.spaceTypeEnum !== 'ZONE' && space.spaceTypeEnum !== 'SITE'
          )
        }
      })
    },
    loadEnergyMeters() {
      let spaceIds = this.spacesAssociated.join(',')
      let filter = {
        space: {
          operator: 'building_is',
          value: [spaceIds],
          orFilters: [{ field: 'purposeSpace' }],
        },
      }
      this.$util
        .loadAsset({
          categoryId: this.getAssetCategoryByType(
            this.$constants.AssetCategoryType.ENERGY
          ).id,
          getCategoryDetails: true,
          filters: filter,
          spaceId: this.model.siteId,
        })
        .then(response => {
          this.energyMeters = response.assets
        })
    },
    handleclose() {
      this.$emit('update:visibility', false)
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    getimage(file) {
      let self = this
      let picReader = new FileReader()
      picReader.addEventListener('load', function(event) {
        self.logoImageUrl = event.target.result
      })
      picReader.readAsDataURL(file)
    },
    requester() {
      let self = this
      if (this.user && this.user.name && this.user.email) {
        let data = this.userlist.find(d => d.email === this.user.email)
        if (data) {
          this.model.contact = data
        }
        this.model.contact.name = this.user.name
        this.model.contact.email = this.user.email
        this.userlist.push(this.user)
      }
      let userObj = this.$helpers.cloneObject(this.user)
      let phone = /^(\+\d{11,13})/
      if (phone.test(userObj.email)) {
        userObj.mobile = userObj.email
        userObj.email = null
      }
      self.popoversavings = true
      this.$http
        .post('/setup/inviterequester', { user: userObj })
        .then(function(response) {
          JSON.stringify(response)
          if (response.status === 200) {
            self.$message.success(' Saved ')
            self.popoversavings = false
            self.visible = false
            self.user.name = null
            self.user.email = null
            self.user.phone = null
          } else {
            alert(JSON.stringify(response))
          }
        })
        .catch(function(error) {
          self.popoversavings = false
          if (error.response.data.fieldErrors.email[0] != null) {
            self.$message.error(error.response.data.fieldErrors.email[0])
          }
        })
    },
    validation(rule) {
      this.error = false
      this.errorText = ''
      if (rule.name === null || rule.name === '') {
        this.errorText = 'Please enter the Tenant Name'
        this.error = true
      } else if (!rule.siteId || rule.siteId < 0) {
        this.errorText = 'Please select the Site'
        this.error = true
      } else if (!rule.contact.name || rule.contact.name === '') {
        this.errorText = 'Please enter the contact Name'
        this.error = true
      } else if (!rule.contact.email || rule.contact.email === '') {
        this.errorText = 'Please enter the contact MailId'
        this.error = true
      } else {
        this.spacesAssociated.forEach(spaceId => {
          let space = this.spaceObjectsMap[spaceId]
          if (
            space.id !== space.buildingId &&
            this.spacesAssociated.includes(space.buildingId)
          ) {
            this.errorText =
              'The Parent building of ' + space.name + ' is already chosen.'
            this.error = true
          } else if (
            space.id !== space.floorId &&
            this.spacesAssociated.includes(space.floorId)
          ) {
            this.errorText =
              'The Parent floor of ' + space.name + ' is already chosen.'
            this.error = true
          } else if (
            space.id !== space.spaceId &&
            this.spacesAssociated.includes(space.spaceId)
          ) {
            this.errorText =
              'The Parent space of ' + space.name + ' is already chosen.'
            this.error = true
          } else {
            this.errorText = ''
            this.error = false
          }
        })
      }
    },
    loadAllZoneChildren() {
      let self = this
      self.loading = true
      this.$http.get('/zone/allTenantZoneChildren').then(function(response) {
        self.allChildren = response.data.children
      })
    },
    checkZoneOccupancy() {
      this.allChildren.forEach(child => {
        if (this.spacesAssociated.includes(child.id)) {
          return child.name
        }
        return null
      })
    },
    save() {
      let self = this
      this.spaces.forEach(space => {
        this.spaceObjectsMap[space.id] = space
      })
      if (this.spacesAssociated) {
        this.spacesAssociated.forEach(spaceId => {
          let spaceObj = this.spaceObjectsMap[spaceId]
          this.zone.area += spaceObj.area
        })
      }
      this.validation(self.model)
      this.model.inTime = this.$helpers.getTimeInOrg(this.model.inTime)
      this.model.outTime = this.$helpers.getTimeInOrg(this.model.outTime)
      this.saving = true
      this.model.utilityAssets = []
      this.energyUtilityAssets.forEach(assetId => {
        this.model.utilityAssets.push({
          utility: this.$constants.TenantUtility.getUtilityFromType('ENERGY')
            .id,
          assetId: assetId,
        })
      })
      let url
      let data
      if (this.tenant) {
        url = '/tenant/update'
        data = this.$helpers.compareObject(this.model, this.tenant)
        data.id = this.tenant.id
      } else {
        url = '/tenant/add'
        data = this.model
      }
      url = '/tenant/add'
      this.zone.siteId = this.model.siteId
      this.zone.name = this.model.name + '_zone'
      data = this.model
      let formdata
      let logos = this.$refs.tenantLogo.files
      if (logos.length) {
        formdata = new FormData()
        this.$helpers.setFormData('tenant', data, formdata)
        formdata.append('tenant.tenantLogo', logos[0])
        this.$helpers.setFormData('zone', this.zone, formdata)
        this.$helpers.setFormData('spaceId', this.spacesAssociated, formdata)
        this.$helpers.setFormData('tenantZone', 'true', formdata)
      } else {
        formdata = {
          tenant: data,
          zone: this.zone,
          spaceId: this.spacesAssociated,
          tenantZone: true,
        }
      }
      this.$http.post(url, formdata).then(response => {
        self.saving = false
        if (response.data && typeof response.data === 'object') {
          this.$message.success(
            !this.tenant
              ? 'Tenant added successfully'
              : 'Tenant updated successfully'
          )
          this.$emit('saved', true)
          this.closeDialog()
        } else {
          this.$message.error('Tenant creation failed!')
        }
      })
    },
  },
}
</script>
<style>
.addtenant-container .el-dialog__header {
  display: none;
}
.tenant-body {
  height: calc(100vh - 160px);
  overflow-y: scroll;
  overflow-x: hidden;
  position: relative;
  margin: 30px 40px 30px 30px;
}
.utility-det-block p {
  margin: 0;
}
.utility-list-H {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.5px;
  color: #6b7e91;
}
.utility-list-D {
  font-size: 12px;
  line-height: 1.42;
  letter-spacing: 0.5px;
  color: #999999;
}
.utility-select {
  width: 250px;
}
.customlogo {
  background: white !important;
  border-radius: 0% !important;
}
.input-upload {
  background-color: #f6feff;
  border: none;
  border-radius: 100%;
  margin-top: 10px;
  color: #fff;
  cursor: pointer;
  overflow: hidden;
  white-space: nowrap;
  position: relative;
  display: inline-block;
  margin-top: 0;
  width: 130px;
  height: 130px;
  border-style: dotted;
  border: 2px dashed #8cd9e3;
}
.input-upload .upload {
  position: absolute;
  top: 0;
  right: 0;
  margin: 0;
  padding: 0;
  font-size: 20px;
  cursor: pointer;
  opacity: 0;
  filter: alpha(opacity=0);
  cursor: pointer;
  font-size: 10px;
  left: 0;
  z-index: 1;
}
.tenant-upload .upload-img {
  width: 24px;
  height: 24px;
  position: absolute;
  top: 40px;
  left: 40%;
  vertical-align: middle;
}
.input-upload .upload-icon-block {
  color: #fff;
}
.upload-img2 {
  width: 130px;
  height: 130px;
  vertical-align: middle;
  position: absolute;
  top: 0;
  left: 0;
  overflow: hidden;
}
.upload-txt {
  font-size: 12px;
  font-weight: 600;
  line-height: 1.17;
  letter-spacing: 0.5px;
  text-align: center;
  color: #6ec0ca;
  position: absolute;
  top: 75px;
  left: 26px;
}
</style>
