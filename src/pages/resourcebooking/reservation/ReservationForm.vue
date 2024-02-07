<template>
  <div>
    <el-dialog
      :visible.sync="showCreateNewDialog"
      v-if="showCreateNewDialog"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form setup-dialog-right setup-dialog45 setup-dialog fc-web-form-dialog f-webform-right-dialog"
      :before-close="closeDialog"
    >
      <div v-if="loading" class="fc-pm-main-bg">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <facilio-web-form
        v-else
        :emitForm="emitForm"
        :name="'reservationForm'"
        :editObj="reservationObj"
        @failed=";(isSaving = false), (emitForm = false)"
        @validated="data => save(data)"
        :reset.sync="resetForm"
        class="facilio-inventory-web-form-body reservation-form-body"
      ></facilio-web-form>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialog()"
          >CANCEL</el-button
        >
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="saveForm()"
          :loading="isSaving"
          >{{ isSaving ? 'Submitting...' : 'SAVE' }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import FacilioWebForm from '@/FacilioWebForm'
export default {
  props: ['showCreateNewDialog', 'editObjId', 'preFillObj'],
  components: {
    FacilioWebForm,
  },
  data() {
    return {
      isSaving: false,
      emitForm: false,
      saving: false,
      resetForm: false,
      reservationObj: null,
      loading: false,
    }
  },
  mounted() {
    if (this.preFillObj) {
      this.reservationObj = this.preFillObj
    } else if (this.editObjId) {
      this.fetchEditObj()
    }
  },
  methods: {
    fetchEditObj() {
      this.loading = true
      this.$http
        .get('/v2/reservations/' + this.editObjId)
        .then(response => {
          if (response.data.responseCode === 0) {
            let tempResObj = response.data.result.reservation
            this.constructEditObject(tempResObj)
          }
          this.loading = false
        })
        .catch(error => {
          this.$message.error(error)
          this.loading = false
        })
    },
    constructEditObject(tempResObj) {
      this.reservationObj = this.$helpers.cloneObject(tempResObj)
      this.reservationObj.internalAttendees = []
      for (let obj of tempResObj.internalAttendees) {
        this.reservationObj.internalAttendees.push(String(obj.id))
      }
      this.reservationObj.durationType = String(
        this.reservationObj.durationType
      )
      this.reservationObj.siteId = String(this.reservationObj.siteId)
    },
    saveForm() {
      this.emitForm = true
    },
    save(data) {
      this.emitForm = false
      let reservationObj = this.$helpers.cloneObject(data)
      this.validateReservation(reservationObj)
    },
    validateReservation(reservationObj) {
      for (let i in reservationObj.internalAttendees) {
        reservationObj.internalAttendees[i] = {
          id: reservationObj.internalAttendees[i],
        }
      }
      if (reservationObj.siteId === null) {
        delete reservationObj.siteId
      }
      if (reservationObj.noOfAttendees > 0) {
        if (
          parseInt(reservationObj.noOfAttendees) <
          reservationObj.internalAttendees.length +
            reservationObj.externalAttendees.length
        ) {
          this.$message.error(
            'Attendees Count is greater and number of attendees'
          )
          return
        }
      }
      if (
        reservationObj.durationType > 0 &&
        reservationObj.durationType !== 6
      ) {
        reservationObj.scheduledEndTime = null
      }
      this.submitForm(reservationObj)
    },
    submitForm(reservationObj) {
      this.isSaving = true
      let url
      let message
      if (this.editObjId > 0) {
        url = '/v2/reservations/update'
        message = 'Reservation Updated Successfully'
        reservationObj['id'] = this.editObjId
      } else {
        url = '/v2/reservations/add'
        message = 'Reservation Added Successfully'
      }
      this.$http
        .post(url, { reservation: reservationObj })
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success(message)
            this.$emit('saved')
            this.closeDialog()
          } else {
            this.$message.error(response.data.message)
          }
          this.isSaving = false
        })
        .catch(error => {
          this.$message.error(error)
          this.isSaving = false
        })
    },

    closeDialog() {
      this.$emit('update:showCreateNewDialog', false)
    },
  },
}
</script>
<style></style>
