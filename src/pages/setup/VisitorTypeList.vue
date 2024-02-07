<template>
  <div>
    <div class="d-flex flex-direction-row flex-wrap mT20">
      <spinner v-if="loading" :show="loading" size="80"></spinner>
      <div
        v-else
        class="visitor-type-card scale-up-left"
        v-for="(visitorType, index) in visitorTypes"
        :key="index"
      >
        <router-link
          :to="{
            name: 'visitorTypeSettings',
            params: { visitorTypeId: visitorType.id },
          }"
        >
          <div class="visitor-type-inner-pad">
            <el-row>
              <el-col :span="6">
                <div
                  class="visitor-type-avatar flex-middle justify-content-center"
                >
                  <img
                    :src="getIconUrl(visitorType.visitorLogoObj.value)"
                    width="42"
                    height="40"
                  />
                </div>
              </el-col>
              <el-col :span="18" class="pL15">
                <el-row>
                  <el-col :span="20">
                    <div class="fc-black-15 bold">{{ visitorType.name }}</div>
                  </el-col>
                  <el-col :span="4" @click.native.stop.prevent="">
                    <el-dropdown
                      class="pR15 pL15"
                      trigger="click"
                      @command="visitorTypeCardOptions($event, visitorType)"
                    >
                      <span class="el-dropdown-link">
                        <i class="el-icon-more visitor-type-card-more"></i>
                      </span>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item command="edit">{{
                          $t('common._common.edit')
                        }}</el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </el-col>
                </el-row>
                <div class="fc-grey4-13 pT10">
                  {{ visitorType.description }}
                </div>
              </el-col>
            </el-row>
          </div>
          <div class="visitor-type-card-footer">
            <div class="flex-middle">
              <div class="p5">
                <el-switch
                  v-model="visitorType.enabled"
                  @change="toggleVisitorEnable(visitorType)"
                ></el-switch>
              </div>
              <div class="label-txt-black pL10 fw6">
                {{
                  visitorType.enabled
                    ? $t('common._common.enabled')
                    : $t('common._common.disabled')
                }}
              </div>
            </div>
          </div>
        </router-link>
      </div>
    </div>
    <el-drawer
      :visible.sync="showAddVisitorDialog"
      :direction="direction"
      size="40%"
      class="fc-drawer-hide-header"
      @close="handleBeforeClose"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{ $t('common.visitor_forms.new_visitor_types') }}
          </div>
        </div>
      </div>
      <div class="new-body-modal">
        <div class="">
          <p class="fc-input-label-txt pB10">{{ $t('tenant.profile.name') }}</p>
          <el-input
            v-model="newVisitor.name"
            :autofocus="true"
            :placeholder="$t('common.visitor_forms.new_visitor_types')"
            class="fc-input-full-border2 width100"
          >
          </el-input>
        </div>
        <div class="pT20">
          <p class="fc-input-label-txt pB10">
            {{ $t('common.wo_report.report_description') }}
          </p>
          <el-input
            type="textarea"
            v-model="newVisitor.description"
            :autosize="{ minRows: 3, maxRows: 4 }"
            resize="none"
            :placeholder="$t('common._common.enter_desc')"
            class="fc-input-full-border-select2"
          ></el-input>
        </div>
        <div class="pT20">
          <p class="fc-input-label-txt pB10">
            {{ $t('common.visitor_forms.choose_icon') }}
          </p>
          <div class="flex-middle flex-wrap">
            <div
              class="mR20 border-11 p10 pointer bR3 mB10"
              :class="{
                iconActive: visitorLogo.index == newVisitor.visitorLogo,
              }"
              @click="newVisitor.visitorLogo = visitorLogo.index"
              v-for="(visitorLogo, index) in visitorLogos"
              :key="index"
            >
              <img
                :src="getIconUrl(visitorLogo.value)"
                width="42"
                height="40"
              />
            </div>
          </div>
        </div>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="closeVisitorTypeDialog" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <async-button
          buttonType="primary"
          buttonClass="modal-btn-save"
          :clickAction="saveBtnAction"
          >{{ $t('common._common._save') }}</async-button
        >
      </div>
    </el-drawer>
  </div>
</template>
<script>
import AsyncButton from '@/AsyncButton'
export default {
  props: ['settings'],
  components: {
    AsyncButton,
  },
  data() {
    return {
      visitorTypes: [],
      direction: 'rtl',
      showAddVisitorDialog: false,
      newVisitor: {
        id: null,
        name: null,
        description: null,
        visitorLogo: null,
      },
      saveBtnAction: null,
      visitorLogos: [],
      loading: true,
    }
  },
  created() {
    this.loadVisitorTypes()
    this.loadVisitorLogos()
  },
  methods: {
    handleBeforeClose() {
      this.newVisitor = {
        id: null,
        name: null,
        description: null,
        visitorLogo: null,
      }
      this.newVisitor.visitorLogo = this.visitorLogos[0].index
    },
    openAddVisitorDialog() {
      this.saveBtnAction = this.addVisitorType
      this.showAddVisitorDialog = true
    },
    getIconUrl(enumName) {
      return require('src/assets/' + enumName + '.svg')
    },
    async loadVisitorTypes() {
      let resp = await this.$http.get(
        '/v2/module/data/list?moduleName=visitortype'
      )
      this.visitorTypes = resp.data.result.moduleDatas
      this.loading = false
    },

    async updateVisitorType(visitorContext) {
      let resp = await this.$http.post('/v2/picklist/updateVisitorType', {
        visitorType: visitorContext,
      })

      return
    },
    async addVisitorType() {
      let resp = await this.$http.post('v2/visitorSettings/add', {
        visitorType: this.newVisitor,
      })
      this.closeVisitorTypeDialog()
      this.loadVisitorTypes()

      return
    },

    closeVisitorTypeDialog() {
      this.showAddVisitorDialog = false
    },

    toggleVisitorEnable(visitorType) {
      this.updateVisitorType({
        id: visitorType.id,
        enabled: visitorType.enabled,
      })
    },
    async loadVisitorLogos() {
      let resp = await this.$util.loadModuleMeta('visitortype')

      this.visitorLogos = resp.fields.find(
        field => field.name == 'visitorLogo'
      ).values
      this.newVisitor.visitorLogo = this.visitorLogos[0].index
    },
    async editVisitorType() {
      await this.updateVisitorType(this.newVisitor)
      this.closeVisitorTypeDialog()
      this.loadVisitorTypes()
      return
    },
    visitorTypeCardOptions(command, visitorTypeObj) {
      if (command == 'edit') {
        this.newVisitor = visitorTypeObj
        this.saveBtnAction = this.editVisitorType
        this.showAddVisitorDialog = true
      }
    },
  },
}
</script>

<style>
.iconActive {
  border: 1px solid #ff3989;
}
</style>
