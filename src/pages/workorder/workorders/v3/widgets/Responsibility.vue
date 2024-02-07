<template>
  <div class="white-bg-block mT20 p20 mB20">
    <div class="fc-id11 text-uppercase text-left">
      {{ $t('maintenance.wr_list.responsibility') }}
    </div>
    <div
      class="fc-text-pink11 text-uppercase text-uppercase text-left pT10 pB10 f10"
    >
      {{ $t('maintenance.wr_list.staff/team') }}
    </div>
    <el-row class="flex-middle pB20">
      <el-col :span="24">
        <div class="label-txt-black visibility-visible-actions">
          <div
            class="flLeft width80"
            v-if="
              (workorder.assignedTo && workorder.assignedTo.id > 0) ||
                (workorder.assignmentGroup && workorder.assignmentGroup.id > 0)
            "
          >
            <user-avatar
              size="md"
              :user="workorder.assignedTo"
              :group="workorder.assignmentGroup"
              :showPopover="true"
              :showLabel="true"
              moduleName="workorder"
            ></user-avatar>
          </div>
          <div class="flLeft color-d" v-else>--- / ---</div>
        </div>
      </el-col>
    </el-row>

    <!-- vendor -->

    <div v-if="!canHideField('vendor')">
      <div v-if="vendorLicenseEnabled()">
        <div class="fc-text-pink11 text-uppercase text-left f10">
          {{ $t('maintenance.wr_list.vendor') }}
        </div>
        <el-row class="flex-middle pB20">
          <el-col :span="24">
            <div class="label-txt-black mT10">
              <div class="flLeft" :class="workorder.vendor ? '' : 'color-d'">
                {{
                  workorder.vendor && workorder.vendor.name
                    ? workorder.vendor.name
                    : '---'
                }}
              </div>
              <div class="clearboth"></div>
            </div>
          </el-col>
        </el-row>
      </div>

      <div>
        <div class="fc-text-pink11 text-uppercase text-left f10">
          {{ $t('common.products.requester') }}
        </div>
        <el-row class="flex-middle pB20">
          <el-col :span="24">
            <div class="label-txt-black visibility-visible-actions">
              <div
                v-if="workorder.requester && workorder.requester.name"
                class="pT10"
              >
                {{ workorder.requester.name }}
              </div>
              <div v-else class="pT10">---</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import helpers from './../../../../../util/helpers'

export default {
  name: 'Responsibility',
  props: ['moduleName', 'details'],
  components: {
    UserAvatar,
  },
  data() {
    return {
      excludeFields: [],
    }
  },
  computed: {
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Responsibility'
    },
    workorder() {
      return this.details.workorder
    },
  },
  methods: {
    canHideField(fieldName) {
      return this.excludeFields.includes(fieldName)
    },
    vendorLicenseEnabled() {
      const { isLicenseEnabled } = helpers
      const vendorLicense = isLicenseEnabled('VENDOR')
      return vendorLicense
    },
  },
}
</script>
