<template>
  <div class="formbuilder-con" v-show="isFormLoading">
    <div class="formbuilder-main">
      <div class="formbuilder-outer-con">
        <div class="position-relative">
          <div class="height100">
            <div class="formbuilder-sidebar-left">
              <div class="formbuilder-left-tools-con">
                <div class="formbuilder-left-header">
                  <div class="fc-grey-text-input-label pointer mT40">
                    <el-row :gutter="20" class="mB20">
                      <el-col :span="12">
                        <div class="fc-animated-background"></div>
                      </el-col>
                      <el-col :span="12">
                        <div class="fc-animated-background"></div>
                      </el-col>
                    </el-row>
                    <el-row :gutter="20" class="mB20">
                      <el-col :span="12">
                        <div class="fc-animated-background"></div>
                      </el-col>
                      <el-col :span="12">
                        <div class="fc-animated-background"></div>
                      </el-col>
                    </el-row>
                  </div>
                </div>
              </div>
            </div>
            <div class="formbuilder-main-con">
              <div class="formbuilder-main-inner">
                <div class="height100">
                  <div class="formbuilder-main-bg">
                    <div class="formbuilder-main-comp-left p20">
                      <el-row :gutter="20" class="mB20">
                        <el-col :span="12">
                          <div class="fc-animated-background"></div>
                        </el-col>
                        <el-col :span="12">
                          <div class="fc-animated-background"></div>
                        </el-col>
                      </el-row>
                      <el-row :gutter="20" class="mB20">
                        <el-col :span="12">
                          <div class="fc-animated-background"></div>
                        </el-col>
                        <el-col :span="12">
                          <div class="fc-animated-background"></div>
                        </el-col>
                      </el-row>
                    </div>
                    <div class="formbuilder-main-comp-right p20">
                      <el-row :gutter="20" class="mB20">
                        <el-col :span="12">
                          <div class="fc-animated-background"></div>
                        </el-col>
                        <el-col :span="12">
                          <div class="fc-animated-background"></div>
                        </el-col>
                      </el-row>
                      <el-row :gutter="20" class="mB20">
                        <el-col :span="12">
                          <div class="fc-animated-background"></div>
                        </el-col>
                        <el-col :span="12">
                          <div class="fc-animated-background"></div>
                        </el-col>
                      </el-row>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    isFormLoading: {
      type: Boolean,
      required: true,
    },
  },
}
</script>

<style></style>
