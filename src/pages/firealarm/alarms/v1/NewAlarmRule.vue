<template>
  <div>
    <el-dialog
      :visible.sync="dialogVisible"
      width="50%"
      :before-close="handleClose"
      class="fc-dialog-center-container"
    >
      <div class="alarm-template-dialog-body">
        <div class="fc-black2-24">All chillers in running state together</div>
        <div class="fc-grey2-text14">All chillers are turned on</div>
        <el-row class="mT20">
          <el-col :span="6">
            <div class="fc-grey2-11">ASSET CATEGORY</div>
            <div class="label-txt-black mT5">Chiller</div>
          </el-col>
          <el-col :span="6">
            <div class="fc-grey2-11">THRESHOLD METRIC</div>
            <div class="label-txt-black mT5">Run State</div>
          </el-col>
        </el-row>
        <div class="fc-text-pink mT30">
          RULE CONDITION
        </div>
        <div class="fc-dark-blue2-12 mT10">
          PREREQUISITE <i class="el-icon-info mL10"></i>
        </div>
        <div class="label-txt-black mT10">
          Condition statement (Metric, Formula / expression)
        </div>
        <div class="fc-dark-blue2-12 mT20">
          Alarm triggered when
        </div>
        <div class="label-txt-black mT10">
          Variables and formula/expression; Variable (Attributes / Asset data
          points)
        </div>
        <el-row class="mT20">
          <el-col :span="2">
            <div class="fc-grey2-11 mT5">Severity</div>
          </el-col>
          <el-col :span="4">
            <div class="fc-badge-critical">CRITICAL</div>
          </el-col>
        </el-row>
        <div class="fc-dark-blue2-12 mT20">
          ALARM CLEAR WHEN
        </div>
        <div class="label-txt-black mT10">Autoclear (Default)</div>
        <div class="fc-text-pink mT30">
          ALARM DETAILS
        </div>
        <div class="mT10">
          <div class="fc-grey2-11">ALARM MESSAGE</div>
          <div class="label-txt-black mT5">
            Indicates templates users don't have to make any edits to
          </div>
        </div>
        <div class="mT20">
          <div class="fc-grey2-11">PROBLEMS</div>
          <div class="label-txt-black mT5">
            Indicates templates users don't have to make any edits to
          </div>
        </div>
        <div class="mT20">
          <div class="fc-grey2-11">POSSIBLE CAUSES</div>
          <div class="label-txt-black mT5">
            Chiller operation not sequentially programmed
          </div>
        </div>
        <div class="mT20">
          <div class="fc-grey2-11">SUGGESTIONS</div>
          <div class="label-txt-black mT5">
            Check the communication or check anybody running chiller manually
          </div>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel">CANCEL</el-button>
        <el-button type="primary" class="modal-btn-save"
          >USE TEMPLATE</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
