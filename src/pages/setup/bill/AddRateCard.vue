<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog55 ratecard-container"
    :before-close="closeDialog"
  >
    <el-form>
      <!-- Header -->
      <div class="new-header-container">
        <div class="new-header-modal">
          <div class="new-header-text">
            <div class="setup-modal-title">
              {{ rateCard ? 'Edit' : 'Add' }} Rate Card
            </div>
          </div>
        </div>
      </div>
      <!-- body -->
      <div class="ratecard-body">
        <div class="ratecared-block1">
          <el-row>
            <el-col :span="24">
              <el-form-item prop="name">
                <p class="grey-text2 pB10">Rate Card Name</p>
                <el-input
                  v-model="model.name"
                  :autofocus="true"
                  placeholder="New Rate Card Name"
                  class="fc-input-full-border2"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item prop="description">
                <p class="grey-text2 pB10">Description</p>
                <el-input
                  v-model="model.description"
                  :autosize="{ minRows: 2, maxRows: 4 }"
                  resize="none"
                  class="edittext fc-input-full-border2"
                  placeholder="Enter the description"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <div class="fc-text-pink">BILLING</div>
          <p class="grey-text2 line-height10">
            Add service utility for this rate card
          </p>
        </div>

        <div class="ratecard-component-block">
          <el-row
            class="mT15 billing-block"
            v-for="(service, index) in utilityServices"
            :key="index"
          >
            <el-col :span="7">
              <p class="grey-text2 pB10">Item</p>
              <el-input
                :autofocus="true"
                v-model="service.name"
                placeholder="New Item"
                class="fc-input-full-border2"
              ></el-input>
            </el-col>
            <el-col :span="5" class="utility-block">
              <p class="grey-text2 pB10">Utility</p>
              <el-select
                placeholder="Select"
                v-model="service.utility"
                class="fc-input-full-border2"
              >
                <el-option
                  v-for="(utility, index) in $constants.TenantUtility.values"
                  :key="index"
                  :label="utility.name"
                  :value="utility.id"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="5" class="quantity-block mL30">
              <p class="grey-text2 pB10">Unit</p>
              <el-input
                placeholder="quantity"
                disabled
                v-model="service.quantity"
                style="width: 60px;"
                class="fc-input-full-border2"
              >
                <div slot="append">
                  {{
                    service.utility
                      ? $constants.TenantUtility.getUtility(service.utility)
                          .unit
                      : ''
                  }}
                </div>
              </el-input>
            </el-col>
            <el-col :span="4" class="price-block2">
              <p class="grey-text2 pB10">Price</p>
              <el-input
                placeholder="price"
                v-model="service.price"
                style="width: 100px;"
                class="fc-input-full-border2"
                ><span slot="append">{{ $currency }}</span></el-input
              >
            </el-col>
            <el-col :span="1">
              <img
                src="~assets/bin-1.svg"
                style="height:15px;width:15px;"
                @click="deleteUtility(index)"
                class="delete-icon"
              />
            </el-col>
          </el-row>
          <div class="mT10 pL20">
            <el-button class="add-border-btn" @click="addUtility"
              ><i class="el-icon-plus"></i> Add Item</el-button
            >
          </div>
          <div class="billing-sec mT20">
            <div class="fc-text-pink">COMMON AREA BILLING</div>
            <p class="grey-text2 line-height10">
              Add common area service utility for this rate card
            </p>
            <el-row
              class="mT15 billing-block"
              v-for="(service, index) in formulaServices"
              :key="index"
            >
              <el-col :span="7">
                <p class="grey-text2 pB10">Item</p>
                <el-input
                  v-model="service.name"
                  :autofocus="true"
                  placeholder="New Item"
                  class="fc-input-full-border2"
                ></el-input>
              </el-col>
              <!-- <el-col :span="4" class="quantity-block mL30">
                   <p class="grey-text2">Quantity</p>
                   <el-input placeholder="quantity" disabled v-model="service.quantity"></el-input>
                 </el-col> -->
              <!-- <el-col :span="4" class="price-block2 mL20">
                   <p class="grey-text2">Price</p>
                  <el-input placeholder="price" v-model="service.price" style="width: 100px;">
                    <span slot="append">{{$currency}}</span>
                  </el-input>
                 </el-col> -->
              <el-col :span="4" class="price-block mL30">
                <p class="grey-text2 pB10">Formula</p>
                <div
                  v-if="service.workflow"
                  class="pointer"
                  @click="showWorkflowDialog(service, true)"
                >
                  <p class="add-green-txt flLeft">Added</p>
                  <i
                    class="el-icon-edit flLeft"
                    style="padding: 4px 0 0 15px;"
                  ></i>
                </div>
                <p
                  class="add-blue-txt pointer pT10"
                  v-else
                  @click="showWorkflowDialog(service)"
                >
                  Add
                </p>
              </el-col>
              <el-col :span="1">
                <img
                  src="~assets/bin-1.svg"
                  style="height:15px;width:15px;margin-top: 23px;"
                  @click="deleteFormulaService(index)"
                  class="delete-icon"
                />
              </el-col>
            </el-row>
            <div class="mT10">
              <el-button class="add-border-btn" @click="addFormulaService"
                ><i class="el-icon-plus"></i> Add Item</el-button
              >
            </div>
          </div>
          <div class="billing-sec mT20">
            <div class="fc-text-pink pB10">TAX</div>
            <p class="grey-text2 line-height10">Add Tax for the rate card</p>
            <el-row>
              <el-col :span="7" class="mT20">
                <p class="grey-text2 flLeft mR20 pB10">Tax</p>
                <el-input
                  v-model="tax.percentage"
                  placeholder="Enter tax"
                  class="tax-percentage-input fc-input-full-border2"
                >
                  <div slot="append">%</div>
                </el-input>
                <!-- <div v-if="tax.workflow" class="flLeft pointer">
                    <p class="add-green-txt flLeft">Added</p>
                    <i class="el-icon-edit flLeft" style="padding: 4px 0 0 15px;" @click="showWorkflowDialog(tax, true)"></i>
                    <span class="mL20 pT5 tax-reset pointer flLeft" @click="tax.workflow = null">Reset</span>
                  </div>
                  <p class="flLeft add-blue-txt pointer" v-else @click="showWorkflowDialog(tax)">Add</p> -->
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
      <!-- footer -->
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >CANCEL</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="save"
          :loading="saving"
          >{{ saving ? 'Saving...' : 'SAVE' }}</el-button
        >
      </div>

      <f-dialog
        v-if="showWorkflow"
        :visible.sync="showWorkflow"
        :width="'38%'"
        :title="(isAddWorkflow ? 'Add' : 'Edit') + ' Formula'"
        @save="associateWorkflow"
        @cancel="reset"
        confirmTitle="Ok"
      >
        <div slot="content">
          <formula
            v-model="tempWorkflow"
            module="formulaField"
            :hideModeChange="true"
          ></formula>
        </div>
      </f-dialog>
    </el-form>
  </el-dialog>
</template>

<script>
import Formula from '@/workflow/FFormulaBuilder'
import FDialog from '@/FDialogNew'
export default {
  props: ['visibility', 'rateCard'],
  components: { Formula, FDialog },
  data() {
    return {
      model: {
        name: '',
        description: '',
        services: [],
      },
      utilityServices: [],
      utilityServiceModel: {
        name: '',
        serviceType: ServiceType.UTILITY,
        utility: '',
        price: '',
        quantity: 1,
      },
      formulaServices: [],
      formulaServiceModel: {
        name: '',
        serviceType: ServiceType.FORMULA,
        workflow: '',
        quantity: 1,
      },
      tax: {
        name: 'tax',
        serviceType: ServiceType.TAX_FORMULA,
        percentage: '',
        workflow: '',
      },
      showWorkflow: false,
      selectedService: null,
      tempWorkflow: {},
      isAddWorkflow: true,
      saving: false,
    }
  },
  mounted() {
    if (this.rateCard) {
      this.$helpers.copy(this.model, this.rateCard)
      this.rateCard.services.forEach(service => {
        if (service.utility !== -1) {
          this.utilityServices.push(
            this.$helpers.copy(
              this.$helpers.cloneObject(this.utilityServiceModel),
              service
            )
          )
        } else if (service.serviceTypeEnum === 'FORMULA') {
          this.formulaServices.push(
            this.$helpers.copy(
              this.$helpers.cloneObject(this.formulaServiceModel),
              service
            )
          )
        } else if (service.serviceTypeEnum === 'TAX_FORMULA') {
          this.tax.percentage = service.workflow.expressions[0].constant
          this.tax.workflow = service.workflow
        }
      })
      if (!this.utilityServices.length) {
        this.addUtility()
      }
      if (!this.formulaServices.length) {
        this.addFormulaService()
      }
    } else {
      this.addUtility()
      this.addFormulaService()
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    addUtility() {
      this.utilityServices.push(
        this.$helpers.cloneObject(this.utilityServiceModel)
      )
    },
    deleteUtility(index) {
      this.utilityServices.splice(index, 1)
    },
    addFormulaService() {
      this.formulaServices.push(
        this.$helpers.cloneObject(this.formulaServiceModel)
      )
    },
    deleteFormulaService(index) {
      this.formulaServices.splice(index, 1)
    },
    showWorkflowDialog(service, isEdit) {
      this.selectedService = service
      this.isAddWorkflow = !isEdit
      this.showWorkflow = true
      this.tempWorkflow = service.workflow
    },
    associateWorkflow() {
      this.selectedService.workflow = this.tempWorkflow
      this.reset()
    },
    reset() {
      this.tempWorkflow = null
    },
    save() {
      this.saving = true
      this.model.services = [
        ...this.utilityServices.filter(
          service => service.name || service.service
        ),
        ...this.formulaServices.filter(
          service => service.name || service.workflow
        ),
      ]
      if (this.tax.percentage) {
        this.setTaxWorkflow(this.tax)
        this.tax.price = this.tax.percentage
        this.model.services.push(this.tax)
      }
      let data = this.model
      let url = '/ratecard/add'
      if (this.rateCard) {
        url = '/ratecard/update'
        data = this.$helpers.compareObject(this.model, this.rateCard)
        data.id = this.rateCard.id
      }

      this.$http.post(url, { rateCard: data }).then(response => {
        this.saving = false
        if (response.data && typeof response.data === 'object') {
          this.$message.success(
            !this.rateCard
              ? 'Rate card updated successfully'
              : 'Rate card added successfully'
          )
          this.$emit('saved', true)
          this.closeDialog()
        } else {
          this.$message.error('Rate card creation failed!')
        }
      })
    },
    setTaxWorkflow(tax) {
      tax.workflow = {
        expressions: [
          {
            constant: tax.percentage,
            name: 'a',
          },
        ],
        resultEvaluator: '(a)',
      }
    },
  },
}
const ServiceType = {
  UTILITY: 1,
  FORMULA: 2,
  TAX_FORMULA: 3,
}
</script>
<style>
.ratecard-container .el-dialog__header {
  display: none;
}
.utility-block {
  margin-left: 30px;
}
.utility-block .el-input .el-input__inner,
.utility-block .el-textarea .el-textarea__inner {
  width: 132px;
  position: relative;
  border-bottom: solid 1px #e2e8ee;
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #333333;
  cursor: pointer;
}
.utility-block .el-dropdown .el-icon-arrow-down,
.utility-block .quantity-block .el-dropdown .el-icon-arrow-down {
  float: right;
  color: #b9c6d4;
  top: 5px;
  position: relative;
}
.utility-block .el-dropdown-menu .el-popper {
  width: 132px !important;
}
.quantity-block .el-input .el-input__inner,
.quantity-block .el-textarea .el-textarea__inner,
.price-block .el-input .el-input__inner,
.price-block .el-textarea .el-textarea__inner {
  width: 101px;
  position: relative;
  border-bottom: solid 1px #e2e8ee;
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #333333;
  cursor: pointer;
}
.ratecard-component-block {
  margin-left: 17px;
  margin-right: 17px;
}
.billing-block2 {
  padding-top: 10px;
  width: 100%;
  border-left: 4px solid transparent;
  background: transparent;
}
/* .billing-block2:hover{
    background-color: #f7fafb;
    padding-top: 10px;
    padding-bottom: 19px;
    width: 100%;
    border-left: 4px solid #39b2c2;
    cursor: pointer;
  } */
.billing-block {
  padding-top: 10px;
  padding-bottom: 19px;
  padding-left: 20px;
  width: 100%;
  border-left: 4px solid transparent;
}
.billing-block:hover,
.billing-block2:hover {
  background-color: #f7fafb;
  padding-top: 10px;
  padding-bottom: 19px;
  width: 100%;
  border-left: 4px solid #39b2c2;
  padding-left: 20px;
  cursor: pointer;
}
.ratecared-block1 {
  padding: 20px 40px 0;
}
.billing-block .el-input__inner {
  background: transparent;
}
.billing-block .el-input__inner:hover {
  background: transparent;
}
.billing-block .delete-icon {
  position: relative;
  top: 43px;
  visibility: hidden;
}
.billing-block:hover .delete-icon {
  cursor: pointer;
  position: relative;
  top: 20px;
  visibility: visible;
}
.ratecard-body {
  position: relative;
  height: calc(100vh - 110px);
  overflow-y: scroll;
  overflow-x: hidden;
}
.add-border-btn {
  height: 30px;
  border-radius: 15px;
  background-color: #ffffff;
  border: solid 1px #39b2c2;
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.6px;
  color: #30a0af;
  padding: 8px 10px;
  text-align: center;
  text-transform: uppercase;
  transition: 0.3s all;
  -webkit-transition: 0.3s all;
}
.add-border-btn:hover {
  color: #fff;
  background: #39b2c2;
  border: solid 1px #39b2c2;
  transition: 0.3s all;
  -webkit-transition: 0.3s all;
}
.billing-sec {
  padding-left: 20px;
}
.add-blue-txt {
  font-size: 12px;
  letter-spacing: 0.6px;
  color: #3080af;
}
.add-green-txt {
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.6px;
  color: #24b581;
}
.quantity-block .el-input-group__append,
.price-block2 .el-input-group__append {
  background: no-repeat;
  border-right: none;
  border-radius: 0;
  border-top: 0;
  border-bottom: 1px solid #e2e8ee;
  right: 47px;
}
.price-block2 .el-input-group__append {
  background: no-repeat;
  border-right: none;
  border-radius: 0;
  border-top: 0;
  border: 1px solid #d0d9e2;
  right: 1px;
  border-left: none;
  border-radius: 3px;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
.price-block2 .el-input-group__append .el-icon-arrow-up {
  padding-right: 14px;
}
.quantiy-select-input .el-input-group__append button.el-button,
.quantiy-select-input .el-input-group__append div.el-select .el-input__inner,
.quantiy-select-input
  .el-input-group__append
  div.el-select:hover
  .el-input__inner,
.quantiy-select-input .el-input-group__prepend button.el-button,
.quantiy-select-input .el-input-group__prepend div.el-select .el-input__inner,
.quantiy-select-input
  .el-input-group__prepend
  div.el-select:hover
  .el-input__inner,
.price-block2 .el-input-group__append button.el-button,
.price-block2 .el-input-group__append div.el-select .el-input__inner,
.price-block2 .el-input-group__append div.el-select:hover .el-input__inner,
.price-block2 .el-input-group__prepend button.el-button,
.price-block2 .el-input-group__prepend div.el-select .el-input__inner,
.price-block2 .el-input-group__prepend div.el-select:hover .el-input__inner {
  width: 45px;
  font-size: 13px;
  letter-spacing: 0.6px;
  color: #666666;
}
.quantity-block .el-input-group__append .el-icon-arrow-up {
  padding-right: 14px;
}
.tax-percentage-input .el-input-group__append,
.tax-percentage-input .el-input-group__prepend {
  background-color: transparent;
  letter-spacing: 0.5px;
  color: #333333;
  border: 1px solid #e2e8ee;
  border-right: none;
  border-radius: 0;
  border-top: 0;
  padding: 0 10px;

  border: 1px solid #d0d9e2;
  right: 1px;
  border-left: none;
  border-radius: 3px;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

.quantity-block .el-input.is-disabled .el-input__inner {
  background-color: transparent;
}

.tax-reset {
  font-size: 12px;
  letter-spacing: 0.5px;
  color: #30a0af;
}
</style>
