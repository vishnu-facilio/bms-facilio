<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Source - Resource Mapping</div>
        <div class="heading-description">
          List of all Source - Resource Mapping
        </div>
      </div>
    </div>
    <!-- <div class="action-btn setting-page-btn">
       <el-button v-if="addControllerlist.length > 0" class="plain" @click="downloadCertificate">Download Certificate</el-button>
        <el-button type="primary" @click="$refs.createNewModel.open()" class="setup-el-btn">Add Controller</el-button>
      </div> -->
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">SOURCE</th>
                <th class="setting-table-th setting-th-text">ASSET</th>
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td colspan="600px" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="!this.nodes.length">
              <tr>
                <td colspan="600px" class="text-center">
                  No nodes created yet.
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr class="tablerow" v-for="(node, index) in nodes" :key="index">
                <td>{{ node.source }}</td>
                <td v-if="node.resourceId">
                  {{ resources[node.resourceId].name }}
                </td>
                <td v-else style="width: 300px;">
                  <div class="link-text">Map Asset</div>
                  <q-popover ref="assetpopover">
                    <q-list link class="scroll" style="min-width: 150px">
                      <q-item
                        v-for="(asset, index) in assets"
                        @click="updatenode(node.id, index)"
                        :key="index"
                      >
                        <q-item-main :label="asset" />
                      </q-item>
                    </q-list>
                  </q-popover>
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
import { QList, QItem, QItemMain, QPopover } from 'quasar'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { getFieldOptions } from 'util/picklist'

export default {
  components: {
    QList,
    QItem,
    QItemMain,
    QPopover,
  },
  data() {
    return {
      assets: [],
      loading: true,
      nodes: [],
      assetId: null,
      resources: {},
    }
  },
  title() {
    return 'Node-Asset Mappings'
  },
  created() {
    this.loadAssetPickListData()
  },
  mounted() {
    this.loadNodes()
  },
  methods: {
    async loadAssetPickListData() {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'asset', skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.assets = options
      }
    },
    loadNodes() {
      this.loading = true
      API.get('/v2/event/sources').then(({ data, error }) => {
        if (isEmpty(error)) {
          let { sources, resources } = data
          this.nodes = sources
          this.resources = resources
        }
        this.loading = false
      })
    },
    updatenode(id, assetId) {
      API.post('/event/updatesource', { id, resourceId: assetId }).then(
        ({ error }) => {
          if (!isEmpty(error)) {
            this.$message.error(error)
          } else {
            this.loadNodes()
          }
        }
      )
    },
  },
}
</script>
