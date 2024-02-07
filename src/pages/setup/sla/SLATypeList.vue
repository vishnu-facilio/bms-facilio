<template>
  <div class="height100 overflow-hidden">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title text-capitalize">
          {{ $t('setup.setup.slapolicies') }}
        </div>
        <div class="heading-description">
          {{ $t('setup.setupLabel.list_sla_policie') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          v-if="canShowAdd"
          type="primary"
          class="setup-el-btn"
          @click="addEntity"
        >
          {{ $t('setup.add.add_breach_type') }}
        </el-button>
      </div>
    </div>
    <div class="pL30 pB20">
      <portal-target name="sla-modules"></portal-target>
    </div>
    <slot name="subHeaderMenu"></slot>
    <el-tabs
      class="alarm-action-tab"
      v-model="currentTab"
      @tab-click="switchTab"
      :before-leave="() => false"
    >
      <el-tab-pane label="SLA Policies" name="sla.list"></el-tab-pane>
      <el-tab-pane
        :label="$t('setup.setupLabel.sla_tab_switch_title')"
        name="sla.types"
      ></el-tab-pane>
    </el-tabs>
    <div class="container-scroll mT15">
      <div class="row setting-Rlayout">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">
                  {{ $t('common.roles.name') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('setup.setupLabel.sla_field') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('setup.setupLabel.reference_field') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="isLoading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="isLoading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="!entities.length">
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('setup.empty.empty_sla') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(entity, index) in entities"
                :key="index"
              >
                <td>{{ entity.name }}</td>
                <td>{{ getFieldName(entity.dueFieldId) }}</td>
                <td>{{ getFieldName(entity.baseFieldId) }}</td>
                <td v-if="canShowAdd" @click="editEntity(entity)">
                  <div class="text-left actions mT0 mR15 text-center">
                    <i class="el-icon-edit pointer"></i>
                  </div>
                </td>
                <td v-else></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <NewSLAEntity
      v-if="showEntityDialog"
      :isNew="isNew"
      :entity="activeEntityObj"
      :moduleName="moduleName"
      @onClose="closeDialog"
      @onSave="getEntityList"
    ></NewSLAEntity>
  </div>
</template>
<script>
import NewSLAEntity from './components/NewSLAEntity'
import isEqual from 'lodash/isEqual'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['moduleName'],
  components: { NewSLAEntity },
  data() {
    return {
      currentTab: 'sla.types',
      module: null,
      isLoading: true,
      entities: [],
      fields: [],
      isNew: false,
      showEntityDialog: false,
      activeEntityObj: {},
    }
  },
  created() {
    this.loadFields()
  },
  computed: {
    canShowAdd() {
      let { query } = this.$route
      let showAdd = query.hasOwnProperty('add') && query.add
      return process.env.NODE_ENV === 'development' || showAdd
    },
  },
  watch: {
    moduleName: {
      handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal) && !isEmpty(newVal)) {
          this.getEntityList()
        }
      },
      immediate: true,
    },
  },
  methods: {
    switchTab(tab) {
      let params = {
        moduleName: this.moduleName,
      }
      this.$router.push({ name: tab.name, params })
    },
    loadFields() {
      return this.$http
        .get('/module/metafields?moduleName=' + this.moduleName)
        .then(({ data }) => {
          this.fields = this.$getProperty(data, 'meta.fields', null) || []
        })
    },
    getFieldName(id) {
      let field = this.fields.find(field => field.id === id) || {}
      return field.displayName || ''
    },

    getEntityList() {
      this.isLoading = true

      return this.$http
        .post(`v2/slaEntity/list`, {
          moduleName: this.moduleName,
        })
        .then(({ data }) => {
          this.entities = data.result.slaEntityList || []
        })
        .catch(({ message = 'Error loading SLA Types' }) => {
          this.$message.error(message)
        })
        .finally(() => (this.isLoading = false))
    },

    addEntity() {
      this.isNew = true
      this.activeEntityObj = {}
      this.showEntityDialog = true
    },
    editEntity(entity) {
      this.activeEntityObj = this.$helpers.cloneObject(entity)
      this.isNew = false
      this.showEntityDialog = true
    },
    closeDialog() {
      this.showEntityDialog = false
      this.isNew = false
      this.activeEntityObj = {}
    },
  },
}
</script>
<style scoped>
.alarm-action-tab {
  position: sticky;
  top: 82px;
  z-index: 5;
  background-color: #f8f9fa;
}
</style>
