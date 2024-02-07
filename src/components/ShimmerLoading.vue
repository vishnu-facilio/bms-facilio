<template>
  <div class="shimmer">
    <section v-if="type === 'form'">
      <article class="sh-background form">
        <div class="g"></div>
        <div class="h"></div>
        <div class="i"></div>
        <div class="j"></div>
        <div class="k"></div>
        <div class="l"></div>
        <div class="m"></div>
        <div class="n"></div>
        <div class="o"></div>
        <div class="p"></div>
        <div class="q"></div>
        <div class="r"></div>
      </article>
    </section>
    <section v-else-if="type === 'profile'">
      <article class="sh-background profile ">
        <div class="g"></div>
        <div class="h"></div>
        <div class="i"></div>
        <div class="j"></div>
      </article>
    </section>
    <section v-else-if="type === 'text'">
      <article class="sh-background text">
        <div class="a"></div>
        <div class="b"></div>
        <div class="c"></div>
        <div class="d"></div>
        <div class="e"></div>
        <div class="f"></div>
        <div class="g"></div>
        <div class="h"></div>
        <div class="i"></div>
        <div class="j"></div>
        <div class="k"></div>
        <div class="l"></div>
      </article>
    </section>
    <section v-else-if="type === 'img'">
      <article class="sh-background img">
        <div class="g"></div>
      </article>
    </section>
    <section v-else-if="type === 'none'">
      <article class="sh-background img"></article>
    </section>
  </div>
</template>
<script>
export default {
  props: ['config'],
  computed: {
    type() {
      if (this.config && this.config.type) {
        return this.config.type
      } else {
        return 'none'
      }
    },
  },
}
</script>

<style>
.sh-background {
  -webkit-animation-duration: 1s;
  -webkit-animation-fill-mode: forwards;
  -webkit-animation-iteration-count: infinite;
  -webkit-animation-name: placeHolderShimmer;
  -webkit-animation-timing-function: linear;
  background: #f6f7f9;
  background-image: linear-gradient(
    to right,
    #f6f7f9 0%,
    #e9ebee 20%,
    #f6f7f9 40%,
    #f6f7f9 100%
  );
  background-repeat: no-repeat;
  background-size: 800px 104px;
  height: 104px;
  position: relative;
}
.sh-background.img {
  background-size: 800px 100%;
  height: 95%;
}
.sh-background.profile {
  background-size: 800px 100%;
  height: 95%;
}
.shimmer section {
  width: 100%;
  max-width: 100%;
  padding: 0;
  height: 449px;
  background-color: #fff;
  border: 1px solid transparent;
  border-radius: 3px;
}

.sh-background div {
  background: #fff;
  height: 6px;
  left: 0;
  position: absolute;
  right: 0;
}
div.a {
  height: 40px;
  left: 40px;
  right: auto;
  top: 0;
  width: 8px;
}

div.b {
  height: 8px;
  left: 48px;
  top: 0;
}
div.c {
  left: 136px;
  top: 8px;
}
div.d {
  height: 12px;
  left: 48px;
  top: 14px;
}
div.e {
  left: 100px;
  top: 26px;
}
div.f {
  height: 10px;
  left: 48px;
  top: 32px;
}
div.g {
  height: 20px;
  top: 40px;
}
div.h {
  left: 410px;
  top: 60px;
}
div.i {
  height: 13px;
  top: 66px;
}
div.j {
  left: 440px;
  top: 79px;
}
div.k {
  height: 13px;
  top: 85px;
}
div.l {
  left: 178px;
  top: 98px;
}
.sh-background.img div.g {
  height: 20px;
  top: 50%;
  width: 100%;
}
.sh-background.profile div.g {
  height: 10px;
  top: 80%;
  width: 100%;
}
.sh-background.profile div.h {
  height: 10px;
  top: 87%;
  width: 80%;
  left: 0px;
}
.sh-background.profile div.i {
  height: 20px;
  top: 83%;
  width: 20%;
  left: 80%;
}
.sh-background.profile div.j {
  height: 10px;
  top: 80%;
  width: 80%;
  left: 0px;
}
.sh-background.form div.g {
  height: 20px;
  top: 0px;
}
.sh-background.form div.h {
  left: 410px;
  top: 20px;
}
.sh-background.form div.i {
  height: 13px;
  top: 26px;
}
.sh-background.form div.j {
  left: 440px;
  top: 39px;
}
.sh-background.form div.k {
  height: 13px;
  top: 45px;
}
.sh-background.form div.l {
  left: 178px;
  top: 57px;
  height: 15px;
}
.sh-background.form div.m {
  height: 10px;
  top: 45px;
}
.sh-background.form div.n {
  left: 410px;
  top: 60px;
}
.sh-background.form div.o {
  height: 13px;
  top: 66px;
}
.sh-background.form div.p {
  left: 440px;
  top: 79px;
}
.sh-background.form div.q {
  height: 13px;
  top: 85px;
}
.sh-background.form div.r {
  left: 178px;
  top: 98px;
}
@keyframes placeHolderShimmer {
  0% {
    background-position: -468px 0;
  }
  100% {
    background-position: 468px 0;
  }
}
</style>
