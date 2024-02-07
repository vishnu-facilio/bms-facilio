<template>
  <div>
    <div class="fc-tenant-wo-page pB0 p20 pR10 pL10 pT10">
      <div
        v-if="loading"
        class="flex-middle flex-center-hH justify-content-center flex-col white-bg-block"
        style="height: 150px;"
      >
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div
        v-else-if="$validation.isEmpty(deskData)"
        class="flex-middle flex-center-hH justify-content-center flex-col white-bg-block"
        style="height: 150px;"
      >
        <inline-svg
          src="svgs/list-empty"
          iconClass="icon text-center icon-80"
        ></inline-svg>
        <div class="label-txt-black bold">
          No Desk
        </div>
      </div>
      <div
        class="white-bg-block p30 bR10 width100 flex-middle"
        style="height: 150px;"
        v-else
      >
        <el-row class="width100">
          <el-col :span="24">
            <div class="flex-middle">
              <div>
                <inline-svg src="svgs/desk-outline" iconClass="icon icon-lg">
                </inline-svg>
              </div>
              <div class="pL15">
                <div class="fc-black-14 text-left bold line-height22">
                  {{ deskData.name }}
                </div>
                <div class="fc-black-14 text-left bold line-height22">
                  {{ deskData.department }}
                </div>
              </div>
            </div>
            <div class="flex-middle pT15">
              <div>
                <i
                  class="el-icon-office-building pR10 f20 fc-occupant-icon"
                ></i>
              </div>
              <div class="pL8">
                <div v-if="$getProperty(deskData, 'building.name')">
                  <div class="fc-black-14 text-left bold line-height22">
                    {{ $getProperty(deskData, 'building.name') }}
                  </div>
                </div>
                <div v-if="$getProperty(deskData, 'floor.name')">
                  <div class="fc-black-14 text-left bold line-height22">
                    {{ $getProperty(deskData, 'floor.name') }}
                  </div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['deskData', 'loading'],
}
</script>
