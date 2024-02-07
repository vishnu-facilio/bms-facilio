<template>
  <div class="d-flex flex-col">
    <portal-target
      :name="`booking-policy-settings-header-${currentTab}`"
      :key="currentTab"
    ></portal-target>
    <el-tabs class="alarm-action-tab" v-model="currentTab">
      <el-tab-pane label="Booking Policies" name="booking-policies" lazy>
        <NewBookingPolicies />
      </el-tab-pane>
      <el-tab-pane label="Form Relation" name="form-relation" lazy>
        <FormRelation />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import FormRelation from 'src/pages/setup/WorkplaceSettings/FormRelation.vue'
import NewBookingPolicies from 'src/pages/setup/WorkplaceSettings/BookingPoliciesListView.vue'
export default {
  components: {
    NewBookingPolicies,
    FormRelation,
  },
  data() {
    return {
      currentTab: 'booking-policies',
    }
  },
}
</script>
<style>
.alarm-action-tab .el-tabs__nav-wrap {
  margin-left: 20px;
}
</style>
