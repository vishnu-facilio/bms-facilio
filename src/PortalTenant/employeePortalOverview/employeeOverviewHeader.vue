<template>
  <div>
    <div class="fc-tenant-wo-page pB0 pT10 p20 pR10">
      <div class="fc-occupant-header p30">
        <el-row class="flex-middle">
          <el-col :span="4" class="flex-middle justify-content-center">
            <avatar size="xxlg" :user="$portaluser"></avatar>
          </el-col>
          <el-col :span="20" class="pL20">
            <div class="fc-black-18 text-left fwBold line-height22">
              {{ employeeData.name }}
            </div>
            <div
              class="fc-black-14 text-left bold line-height22 pT10"
              v-if="employeeData.email"
            >
              <i class="el-icon-message pR10 fc-occupant-icon"></i>
              {{ employeeData.email }}
            </div>
            <div
              class="fc-black-14 text-left bold line-height22 pT5"
              v-if="employeeData.phone"
            >
              <i class="el-icon-mobile-phone pR10 fc-occupant-icon"></i>
              {{ employeeData.phone }}
            </div>
            <div class="fc-black-14 text-left bold line-height22 pT5">
              <i class="el-icon-office-building pR10 fc-occupant-icon"></i>
              {{ $getProperty(employeeData.data, 'department.name') }}
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
import Avatar from '@/Avatar'
export default {
  props: ['loading', 'employeeData'],
  components: {
    Avatar,
  },
}
</script>
