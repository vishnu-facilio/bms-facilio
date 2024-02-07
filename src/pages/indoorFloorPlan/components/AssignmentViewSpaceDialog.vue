<template>
  <div class="properties-section">
    <spinner :show="loading" size="80" v-if="loading"></spinner>
    <template v-else>
      <div class="new-header-container pL30">
        <div class="new-header-text relative">
          <div class="fc-setup-modal-title">Properties</div>
          <div class="flex-middle">
            <div
              content="View Summary"
              v-if="!isPortal && moduleName === 'desks'"
              v-tippy="{ arrow: true }"
              @click="openRecordSummary(record, moduleName)"
            >
              <inline-svg
                src="svgs/external-link2"
                iconClass="icon icon-sm-md fill-blue mR10 pointer"
              ></inline-svg>
            </div>
            <div class="fc-setup-modal-close f18 pointer" @click="handleClose">
              <i class="el-icon-close fwBold"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="new-body-modal infp-left-section height100 pL20 pR20 mT10">
        <!-- start -->
        <el-form ref="form" :model="record" label-width="120px">
          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item prop="name" class="mB10 m0">
                <p class="fc-input-label-txt pB5">Name</p>
                <el-input
                  :disabled="true"
                  v-model="record.name"
                  class="width100 fc-input-full-border2"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row class="mB10 pT10" v-if="record.employee">
            <el-col :span="24">
              <div class="fc-black-14 text-left text-capitalize mB10">
                {{ employee }}
              </div>
              <FLookupFieldWrapper
                v-if="rerenderEmplyWizard"
                class="rm-arrow"
                v-model="record.employee.id"
                :label="employee"
                @recordSelected="value => setValue(value)"
                :field="{
                  lookupModule: { name: 'employee' },
                  multiple: false,
                }"
                :filterConstruction="constructEmployeeFilter"
              ></FLookupFieldWrapper>
            </el-col>
          </el-row>
        </el-form>

        <!-- end -->
        <div class="text-center pT20"></div>

        <el-row class="text-center pT20">
          <el-col :span="20" v-if="showbtn">
            <el-button class="fc-pink-btn-small" size="small" @click="update">{{
              doneChanges
            }}</el-button>
          </el-col>
          <el-col :span="20" v-else>
            <el-button
              class="fc-pink-btn-small inactive"
              size="small"
              :disabled="true"
              >{{ doneChanges }}</el-button
            >
          </el-col>
          <el-col :span="4" v-if="showbtn">
            <el-tooltip
              class="item"
              effect="dark"
              content="Reset"
              placement="bottom"
            >
              <el-button
                size="small"
                @click="reset"
                class="fp-reset-icon"
                icon="el-icon-refresh-left"
              ></el-button>
            </el-tooltip>
          </el-col>
          <el-col :span="4" v-else>
            <el-tooltip
              class="item"
              effect="dark"
              content="Reset"
              placement="bottom"
            >
              <el-button
                size="small"
                :disabled="true"
                class="fp-reset-icon"
                icon="el-icon-refresh-left"
              ></el-button>
            </el-tooltip>
          </el-col>
        </el-row>
      </div>
    </template>
  </div>
</template>
<script>
import properties from 'pages/indoorFloorPlan/components/ViewerPropertyDialogWrapper'
export default {
  extends: properties,
}
</script>
