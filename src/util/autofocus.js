const AutofocusMixin = {
  methods: {
    setAutofocus(refElement) {
      this.$nextTick(() => {
        if (this.$refs[refElement]) {
          this.$refs[refElement].focus()
        }
      })
    },
  },
}

export default AutofocusMixin
