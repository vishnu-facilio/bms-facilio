<template>
  <div class="properties-section fc-properties-section-editor">
    <div class="new-header-container">
      <div class="new-header-text relative">
        <div class="fc-setup-modal-title">Properties</div>
        <div class="flex-middle">
          <div class="fc-setup-modal-close f18 pointer" @click="handleClose">
            <i class="el-icon-close fwBold"></i>
          </div>
        </div>
      </div>
    </div>
    <div class="new-body-modal infp-left-section height100 pL20 pR20 mT10">
      <el-form
        ref="form"
        :model="selectedZone"
        label-width="120px"
        v-if="spaceCategory === 'Parking Stall'"
      >
        <el-row class="mB10">
          <el-col :span="24">
            <el-form-item prop="siteId" class="mB10 m0">
              <p class="fc-input-label-txt pB5">{{ spaceCategory }}</p>

              <FLookupFieldWrapper
                v-model="selectedZone.properties.spaceId"
                :label="'Space'"
                :disabled="true"
                @recordSelected="value => setValue(value)"
                :field="{
                  lookupModule: { name: 'space' },
                  multiple: false,
                }"
              ></FLookupFieldWrapper>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row class="mB10 mT20">
          <el-col :span="24">
            <el-form-item prop="name" class="mB10 m0">
              <el-checkbox
                @change="update()"
                v-model="selectedZone.properties.isReservable"
                >Reservable</el-checkbox
              >
            </el-form-item>
          </el-col>
        </el-row>

        <el-row class="mT30">
          <el-col :span="24">
            <el-button @click="deleteZone()" class="fc-floorplan-delete"
              >DELETE</el-button
            >
          </el-col>
        </el-row>
      </el-form>
      <el-form
        ref="form"
        :model="selectedZone"
        label-width="120px"
        v-else-if="spaceCategory === 'Lockers'"
      >
        <el-row class="mB10">
          <el-col :span="24">
            <el-form-item prop="siteId" class="mB10 m0">
              <p class="fc-input-label-txt pB5">{{ spaceCategory }}</p>

              <FLookupFieldWrapper
                v-model="selectedZone.properties.spaceId"
                :label="'Space'"
                :disabled="true"
                @recordSelected="value => setValue(value)"
                :field="{
                  lookupModule: { name: 'space' },
                  multiple: false,
                }"
              ></FLookupFieldWrapper>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row class="mT30">
          <el-col :span="24">
            <el-button @click="deleteZone()" class="fc-floorplan-delete"
              >DELETE</el-button
            >
          </el-col>
        </el-row>
      </el-form>
      <el-form ref="form" :model="selectedZone" label-width="120px" v-else>
        <el-row class="mB10">
          <el-col :span="24">
            <el-form-item prop="siteId" class="mB10 m0">
              <p class="fc-input-label-txt pB5">Space</p>

              <FLookupFieldWrapper
                v-model="selectedZone.properties.spaceId"
                :label="'Space'"
                :disabled="true"
                @recordSelected="value => setValue(value)"
                :field="{
                  lookupModule: { name: 'space' },
                  multiple: false,
                }"
              ></FLookupFieldWrapper>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row class="mB10 mT20">
          <el-col :span="24">
            <el-form-item prop="name" class="mB10 m0">
              <el-checkbox
                @change="update()"
                v-model="selectedZone.properties.isReservable"
                >Reservable</el-checkbox
              >
            </el-form-item>
          </el-col>
        </el-row>

        <el-row class="mT30">
          <el-col :span="24">
            <el-button @click="deleteZone()" class="fc-floorplan-delete"
              >DELETE</el-button
            >
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>
<script>
import FLookupFieldWrapper from '@/FLookupFieldWrapper'

export default {
  components: { FLookupFieldWrapper },
  props: ['selectedZone', 'visible'],
  data() {
    return {
      departments: [],
    }
  },
  computed: {
    spaceCategory() {
      if (
        this.selectedZone &&
        this.selectedZone.properties &&
        this.selectedZone.properties.spaceCategory
      ) {
        return this.selectedZone.properties.spaceCategory
      }
      return ''
    },
  },
  methods: {
    deleteZone() {
      this.$emit('delete', this.selectedZone)
    },
    handleClose() {
      this.$emit('update:visible', false)
      this.$emit('close')
    },
    // changeSpace() {
    //   let selectedSpace = this.spaces.find(
    //     rt => rt.id === this.selectedZone.properties.spaceId
    //   )
    //   this.selectedZone.properties.label = selectedSpace.name
    //   this.update()
    // },
    setValue(value) {
      this.selectedZone.properties.label = value.name
      this.selectedZone.properties.spaceId = value.value
      this.update()
    },
    update() {
      this.$emit('update', this.selectedZone)
    },
    loadSpaces() {},
  },
}
</script>
