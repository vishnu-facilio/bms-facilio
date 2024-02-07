<template>
  <div>
    <f-dialog
      :append-to-body="true"
      title="Move Asset"
      v-if="visibility"
      :visible.sync="visibility"
      width="30%"
      :stayOnSave="true"
      @save="moveAsset(assetMovement)"
      @close="moveDialogClose"
      :confirmTitle="asset.moveApprovalNeeded ? 'Request' : 'Move'"
      customClass="qr-dialog"
    >
      <el-row align="middle" class="pB20">
        <el-col :span="24" class="pR5">
          <div class="fc-input-label-txt" style="color:#dc7171">To Site</div>
          <div class="form-input">
            <el-select
              filterable
              v-model="assetMovement.toSite"
              class="form-item width100 fc-input-full-border2"
              placeholder="Select Site"
            >
              <el-option
                v-for="site in sites"
                :key="site.id"
                :label="site.name"
                :value="site.id"
              ></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
      <el-row align="middle" class="pB20">
        <el-col :span="24">
          <div class="fc-input-label-txt" style="color:#dc7171">To Space</div>
          <div class="form-input">
            <el-input
              @change="
                quickSearchQuery = assetMovement.spaceDisplayName
                showSpaceChooser = true
              "
              v-model="assetMovement.spaceDisplayName"
              type="text"
              :placeholder="$t('maintenance.wr_list.to_search_type')"
              class="fc-input-full-border width100"
            >
              <i
                @click="showSpaceChooser = true"
                slot="suffix"
                class="el-input__icon el-icon-search pm-form-search-icon op5 pT7"
              ></i>
            </el-input>
          </div>
        </el-col>
      </el-row>
      <el-row align="middle" class="pB20">
        <el-col :span="24" class="pR5">
          <div class="fc-input-label-txt" style="color:#dc7171">
            Requested By
          </div>
          <div class="form-input">
            <el-select
              filterable
              v-model="currentUserId"
              class="form-item width100 fc-input-full-border2"
              placeholder="Select Site"
            >
              <el-option
                v-for="user in users"
                :key="user.id"
                :label="user.name"
                :value="user.id"
              ></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
      <el-row align="middle" class="pB20">
        <el-col :span="24" class="pR5">
          <div class="fc-input-label-txt" style="color:#dc7171">
            Description
          </div>
          <div class="form-input">
            <el-input
              type="textarea"
              v-model="assetMovement.description"
              :autosize="{ minRows: 6, maxRows: 4 }"
              :placeholder="$t('common._common.enter_desc')"
              class="fc-input-full-border-textarea"
              resize="none"
            ></el-input>
          </div>
        </el-col>
      </el-row>
    </f-dialog>
    <space-asset-chooser
      v-if="showSpaceChooser"
      :resourceType="'2, 3, 4'"
      picktype="space"
      @associate="associate($event)"
      :query="quickSearchQuery"
      :visibility.sync="showSpaceChooser"
      :filter="filter"
      :showAsset="false"
    ></space-asset-chooser>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import FDialog from '@/FDialogNew'

export default {
  components: {
    FDialog,
    SpaceAssetChooser,
  },
  props: ['visibility', 'asset'],
  data() {
    return {
      quickSearchQuery: '',
      currentUserId: null,
      showSpaceChooser: false,
      assetMovement: {},
    }
  },
  created() {
    this.$store.dispatch('loadSite')
  },
  mounted() {
    this.assetMovement = {}
    if (this.sites?.length === 1) {
      this.assetMovement.toSite = this.sites[0].id
    }
    this.currentUserId = this.getCurrentUser().id
  },
  computed: {
    ...mapState({
      users: state => state.users,
      sites: state => state.site,
    }),
    ...mapGetters(['getCurrentUser']),
    filter() {
      let filter = {}
      if (this.assetMovement.toSite) {
        filter.site = Number(this.assetMovement.toSite)
      }
      return filter
    },
  },
  methods: {
    moveDialogClose() {
      this.visibility = false
      this.$emit('update:visibility', false)
    },
    associate(selectedObj) {
      this.assetMovement.toSpace = selectedObj.id
      this.assetMovement.spaceDisplayName = selectedObj.name
      this.showSpaceChooser = false
      this.$forceUpdate()
    },
    moveAsset(assetMovement) {
      if (!assetMovement.toSite) {
        this.$message.error('Please choose the To Site')
        return
      } else if (!assetMovement.toSpace) {
        this.$message.error('Please choose the To Space')
        return
      }
      assetMovement.assetId = this.asset.id
      assetMovement.approvalNeeded = this.asset.moveApprovalNeeded
      if (this.asset.identifiedLocation) {
        assetMovement.fromSite = this.asset.identifiedLocation.id
      } else if (this.asset.siteId) {
        assetMovement.fromSite = this.asset.siteId
      }

      if (this.asset.currentSpaceId) {
        assetMovement.fromSpace = this.asset.currentSpaceId
      } else if (this.asset.space) {
        assetMovement.fromSpace = this.asset.space.id
      }

      if (
        assetMovement.fromSite == assetMovement.toSite &&
        assetMovement.fromSpace == assetMovement.toSpace
      ) {
        this.$message.error('Please choose a different site/space for movement')
        return
      }
      assetMovement.requestedBy = {
        id: this.currentUserId,
      }
      this.$http
        .post('/v2/assetMovement/create', { assetMovement })
        .then(response => {
          if (response.data.responseCode === 0) {
            if (assetMovement.approvalNeeded) {
              this.$message.success('Asset Move requested successfully.')
            } else {
              this.$message.success('Asset Moved successfully.')
            }
            this.moveDialogClose()
            this.$emit('refresh')
          } else {
            this.$message.error(response.data.message)
          }
        })
    },
  },
}
</script>
<style lang="scss"></style>
