<template>
  <div class="container" :style="containerStyle">
    <span></span>
    <span></span>
    <span></span>
    <span></span>
  </div>
</template>

<script>
export default {
  props: {
    size: {
      type: Number,
      default: 60
    }
  },
  computed: {
    containerStyle () {
      return {
        '--size': `${this.size}px` // 定义 CSS 变量 --size
      }
    }
  }
}
</script>

<style scoped>
.container {
  position: absolute;
  top: 50%;
  left: 50%;
  border-radius: 50%;
  height: var(--size); /* 使用 CSS 变量 */
  width: var(--size); /* 使用 CSS 变量 */
  animation: rotate_3922 1.2s linear infinite;
  background-color: #9b59b6;
  background-image: linear-gradient(#9b59b6, #84cdfa, #5ad1cd);
}

.container span {
  position: absolute;
  border-radius: 50%;
  height: 100%;
  width: 100%;
  background-color: #9b59b6;
  background-image: linear-gradient(#9b59b6, #84cdfa, #5ad1cd);
}

.container span:nth-of-type(1) {
  filter: blur(5px);
}

.container span:nth-of-type(2) {
  filter: blur(10px);
}

.container span:nth-of-type(3) {
  filter: blur(25px);
}

.container span:nth-of-type(4) {
  filter: blur(50px);
}

.container::after {
  content: "";
  position: absolute;
  top: calc(var(--size) * 0.1); /* 动态计算顶部位置 */
  left: calc(var(--size) * 0.1); /* 动态计算左侧位置 */
  right: calc(var(--size) * 0.1); /* 动态计算右侧位置 */
  bottom: calc(var(--size) * 0.1); /* 动态计算底部位置 */
  background-color: #fff;
  border: solid 5px #ffffff;
  border-radius: 50%;
}

@keyframes rotate_3922 {
  from {
    transform: translate(-50%, -50%) rotate(0deg);
  }

  to {
    transform: translate(-50%, -50%) rotate(360deg);
  }
}
</style>
