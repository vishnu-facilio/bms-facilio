<template>
  <div v-if="details">
    <div class="fc__white__bg__info height100">
      <el-row class="border-bottom6 pB20">
        <el-col :span="6" class="field-label">Description</el-col>
        <el-col :span="16" class="field-value line-height22">{{
          details.description ? details.description : '---'
        }}</el-col>
      </el-row>
      <el-row class="border-bottom6 pB20 pT20">
        <el-col :span="12">
          <el-col :span="12">
            <div class="field-label">Site</div>
          </el-col>
          <el-col :span="12" class="field-value">
            {{
              details.siteId > 0
                ? $store.getters.getSite(details.siteId).name
                : '---'
            }}
          </el-col>
        </el-col>
        <el-col :span="12">
          <el-col :span="12">
            <div class="field-label">
              {{
                details.resource != null
                  ? details.resource.resourceType === 1
                    ? 'Space'
                    : 'Asset'
                  : 'Space/Asset'
              }}
            </div>
          </el-col>
          <el-col :span="12" class="field-value">
            {{ details.resource != null ? details.resource.name : '---' }}
          </el-col>
        </el-col>
      </el-row>
      <el-row class="border-bottom6 pB20 pT20">
        <el-col :span="12">
          <el-col :span="12">
            <div class="field-label">Urgency</div>
          </el-col>
          <el-col :span="12" class="field-value">
            {{ details.urgency > -1 ? WOUrgency[details.urgency] : '---' }}
          </el-col>
        </el-col>
        <el-col :span="12">
          <el-col :span="12">
            <div class="field-label">Assigned To</div>
          </el-col>
          <el-col :span="12" class="field-value">
            <user-avatar
              size="md"
              :user="details.assignedTo"
              :group="details.assignmentGroup"
              :showPopover="true"
              :showLabel="true"
              moduleName="workorder"
            ></user-avatar>
          </el-col>
        </el-col>
        <el-col :span="12"> </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'

export default {
  props: ['details'],
  components: {
    UserAvatar,
  },
  data() {
    return {
      WOUrgency: {
        1: 'Not Urgent',
        2: 'Urgent',
        3: 'Emergency',
      },
    }
  },
  created() {
    this.$store.dispatch('loadSite')
  },
}
</script>
<style scoped>
.field-label {
  color: #324056;
  font-weight: 500;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.5px;
}
.field-value {
  padding-left: 10px;
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.5px;
  color: #324056;
}
</style>
