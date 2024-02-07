<template>
  <div>
    <div id="impact-header" class="section-header">
      {{ $t('rule.create.impact') }}
    </div>
    <div class="p50 pT10 pB30" style="min-height: 450px">
      <div
        v-if="$validation.isEmpty(selectedImpact)"
        class="rule-basic-info-content"
      >
        <div class="d-flex flex-direction-column text-center">
          <inline-svg
            src="svgs/emptystate/data-empty"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="mT10 empty-text f15 bold">
            {{ $t('rule.create.impact_not_available') }}
          </div>
          <div class="mT5 empty-text-desc f13">
            {{ $t('rule.create.no_impact_associated') }}
          </div>
          <div class="inline-block mT20">
            <el-button
              :class="{
                'v2-rules-btn': isV2,
              }"
              class="pT10 pB10 small-border-btn text-uppercase pL15 pR15"
              @click="showAddImpact = true"
              >{{ $t('rule.create.add_impact') }}</el-button
            >
          </div>
        </div>
      </div>
      <div class="root-cause-container" v-else>
        <div class="flex-center-row-space mB30 mT20">
          <div class="desc">{{ $t('rule.create.associated_impact') }}</div>
        </div>

        <el-table
          :data="selectedImpactList"
          class="root-cause-table"
          :fit="true"
          style="width: 100%;"
          border
        >
          <el-table-column label="S.NO." width="100"
            >{{ `1` }}
          </el-table-column>
          <el-table-column prop="name" :label="$t('common.products._name')">
            <template v-slot="impact">
              <div class="label-txt-black">{{ impact.row.name }}</div>
            </template>
          </el-table-column>
          <el-table-column
            prop="description"
            :label="$t('common._common.description')"
          >
            <template v-slot="impact">
              <div class="label-txt-black">
                {{ $getProperty(impact, 'row.description') || '---' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop
            label
            width="130"
            class="visibility-visible-actions"
          >
            <template v-slot="impact">
              <div class="text-center">
                <i
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  @click="deleteItem(impact.row)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <NewImpact
      v-if="showAddImpact"
      :closeDialog="() => (showAddImpact = false)"
      @addImpact="associateImpact"
      :impactList="impactList"
      :selectedImpact="selectedImpact"
      :isV2="isV2"
    />
  </div>
</template>
<script>
import NewImpact from './NewImpact.vue'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  name: 'Impact',
  data() {
    return {
      selectedImpact: -1,
      selectedImpactList: [],
      impactList: [],
      impactTemplates: [],
      showAddImpact: false,
    }
  },
  props: ['alarmImpactObj', 'ruleDetail', 'isV2'],
  components: { NewImpact },
  created() {
    this.initImpact()
  },
  watch: {
    selectedImpact: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          let { selectedImpact } = this
          let impact = { id: selectedImpact }

          this.$emit('ruleDetailsChange', { impact })
        }
      },
      immediate: true,
    },
    ruleDetail: {
      handler(newVal, oldVal) {
        let { assetCategory: newAssetCategory } = newVal || {}
        let { assetCategory: oldAssetCategory } = oldVal || {}
        if (
          newAssetCategory !== oldAssetCategory &&
          !isEmpty(newAssetCategory)
        ) {
          this.loadImpact()
        }
      },
      deep: true,
    },
  },
  methods: {
    async initImpact() {
      await this.loadImpact()
      let { alarmImpactObj } = this
      if (!isEmpty(alarmImpactObj)) {
        let { impact } = alarmImpactObj || {}
        let { id: impactId } = impact || {}
        if (!isEmpty(impactId)) {
          this.$set(this, 'selectedImpact', impactId)
          this.associateImpact(impactId)
        }
      }
    },
    async loadImpact() {
      this.loading = true
      let { ruleDetail } = this
      let { assetCategory } = ruleDetail || {}
      let { id } = assetCategory || {}
      let url = 'v3/modules/data/list'
      let params = {
        page: 1,
        perPage: 50,
        withCount: true,
        viewName: 'all',
        force: false,
        moduleName: 'faultImpact',
      }
      if (!isEmpty(id)) {
        params = {
          ...params,
          filters: JSON.stringify({
            assetCategory: {
              operatorId: 36,
              value: [id.toString()],
            },
          }),
        }
      }

      let { error, data } = await API.get(url, params)

      if (error) {
        this.$message.error('Error Occured')
      } else {
        let { faultImpact } = data || {}
        this.impactTemplates = faultImpact
        if (!isEmpty(this.impactTemplates)) {
          this.impactList = this.impactTemplates.map(impact => {
            return { label: impact.name, value: impact.id }
          })
        }
      }
      this.loading = false
    },
    associateImpact(selectedValue) {
      let { impactTemplates } = this
      let selectedImpactObj = impactTemplates.find(
        impact => impact.id === selectedValue
      )

      this.selectedImpactList.push(selectedImpactObj)
      this.selectedImpact = selectedValue
    },
    deleteItem(rule) {
      let { selectedImpactList } = this
      let index = selectedImpactList.findIndex(impact => impact.id === rule.id)

      this.selectedImpactList.splice(index, 1)
      this.selectedImpact = -1
    },
  },
}
</script>
<style lang="scss">
.v2-rules-btn {
  width: 135px;
  height: 35px;
  border-radius: 4px;
  border-color: transparent;
  background-color: #0074d1;
  color: #fff;
  font-weight: 500;
  font-size: 12px;
  text-transform: capitalize;
  &:hover {
    background-color: #ffffff;
    color: #000000;
  }
  &:active {
    color: #fff;
    background-color: #0074d1;
    border: transparent;
  }
}
</style>
