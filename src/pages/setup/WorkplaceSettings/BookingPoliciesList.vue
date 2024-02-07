<template>
  <div>
    <div
      v-for="(policie, key) in policiesList"
      :key="key"
      class="app-list visibility-visible-actions"
    >
      <el-row :gutter="20" class="mB10">
        <el-row :key="key" class="visitor-hor-card scale-up-left flex-middle">
          <el-col :span="19">
            <div class="pull-left">
              <div class="app-name">
                {{ policie.name }}
              </div>
              <div class="fc-grey4-13 pT5">
                {{ policie.description }}
              </div>
            </div>
          </el-col>
          <el-col :span="7" class="text-right">
            <div
              v-if="
                [
                  'maximumAttendees',
                  'IsChargeable',
                  'securityDeposit',
                  'bookingAdvanceDays',
                  'bookingAdvanceHours',
                  'cancellationPeriodInDays',
                  'cancellationPeriodInHours',
                  'cancellationCharges',
                ].includes(key)
              "
            >
              <el-input
                v-if="policie.enable === true"
                type="number"
                v-model="policie.value"
                class="fc-input-full-border2 width80"
                :min="5"
              ></el-input>
            </div>
          </el-col>
          <el-col :span="3" class="text-right">
            <div class="pull-right">
              <el-switch
                v-model="policie.enable"
                @change="toggleGroup(policie)"
              ></el-switch>
            </div>
          </el-col>
        </el-row>
      </el-row>
    </div>
  </div>
</template>
<script>
export default {
  props: ['value'],
  computed: {
    policiesList: {
      get() {
        return this.value
      },
      set(value) {
        this.$emit('update:value', value)
      },
    },
  },
}
</script>
<style>
.visitor-hor-card {
  border: solid 0px;
}
</style>
