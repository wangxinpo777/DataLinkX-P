<template>
  <a-card title="可视化图表">
    <div style="display: flex;">
      <div class="main-content">
        <div ref="chart" class="chart-container"></div>
      </div>
      <div class="sidebar">
        <h3>图表设置</h3>

        <!-- 基础设置 -->
        <a-card title="基础设置" style="margin-bottom: 10px;">
          <a-input v-model="chartTitle" placeholder="图表标题" style="margin-bottom: 10px;"/>
          <a-select v-model="chartType" style="width: 100%; margin-bottom: 10px;">
            <a-select-option value="line">折线图</a-select-option>
            <a-select-option value="bar">柱状图</a-select-option>
            <a-select-option value="scatter">散点图</a-select-option>
          </a-select>
          <a-checkbox v-model="showToolbox">显示工具栏</a-checkbox>
        </a-card>

        <!-- 坐标轴设置 -->
        <a-card
          title="坐标轴设置"
          style="margin-bottom: 10px;"
          :tab-list="[{ key: 'Y-Index', tab: 'Y轴' }, { key: 'X-Index', tab: 'X轴' }]"
          @tabChange="(key) => (activeKey = key)"
          :active-tab-key="activeKey"
        >
          <div v-show="activeKey === 'Y-Index'">
            <a-input v-model="yAxisTitle" placeholder="Y轴标题" style="margin-bottom: 10px;"/>
            <a-checkbox v-model="yAxisSplitLine">显示网格线</a-checkbox>
            <div style="display: flex; gap: 10px; margin: 10px 0;">
              <a-input-number v-model="yMin" placeholder="最小值" style="width: 100%;"/>
              <a-input-number v-model="yMax" placeholder="最大值" style="width: 100%;"/>
            </div>
            <div class="checkbox-group-container">
              <a-checkbox-group v-model="selectedBrands">
                <div class="checkbox-item" v-for="option in options" :key="option.value">
                  <a-checkbox :value="option.value">{{ option.label }}</a-checkbox>
                </div>
              </a-checkbox-group>
            </div>
          </div>
          <div v-show="activeKey === 'X-Index'">
            <a-input v-model="xAxisTitle" placeholder="X轴标题" style="margin-bottom: 10px;"/>
            <a-input-number
              v-model="xAxisRotate"
              :min="0"
              :max="90"
              style="width: 100%;"
              placeholder="标签旋转角度"
            />
          </div>
        </a-card>

        <!-- 系列样式 -->
        <a-card title="系列样式" style="margin-bottom: 10px;">
          <p>选择Y轴</p>
          <a-select
            style="width: 100%; margin-bottom: 10px;"
            :options="options"
            :defaultValue="chooseLine"
            @change="(value) => (chooseLine = value)"
          />
          <div class="section-title">实例设置</div>
          <compact
            v-model="brandStyles[chooseLine].lineColor"
            style="width: 100%; margin-bottom: 10px;"/>
          <a-select v-model="brandStyles[chooseLine].lineStyle" style="width: 100%; margin-bottom: 10px;">
            <a-select-option value="solid">实线</a-select-option>
            <a-select-option value="dashed">虚线</a-select-option>
            <a-select-option value="dotted">点线</a-select-option>
          </a-select>
          <div style="margin-top: 10px;">
            <div class="checkbox-item">
              <a-checkbox v-model="brandStyles[chooseLine].showDataLabel">显示数据标签</a-checkbox>
            </div>
            <template v-if="brandStyles[chooseLine].showDataLabel">
              <div class="section-title">数据标签设置</div>
              <div class="checkbox-item">
                <p>位置</p>
                <a-select v-model="brandStyles[chooseLine].position" style="width: 100%;" placeholder="位置" :options="positions">
                </a-select>
              </div>
              <p>颜色</p>
              <compact
                v-model="brandStyles[chooseLine].labelColor"
                style="width: 100%; margin-bottom: 10px;"/>
              <p>格式化</p>
              <a-select
                v-model="brandStyles[chooseLine].formatter"
                style="width: 100%; margin-bottom: 10px;"
                placeholder="格式化"
                :options="formatterOptions">
              </a-select>
              <p>字体大小</p>
              <a-input-number
                v-model="brandStyles[chooseLine].fontSize"
                :min="12"
                :max="24"
                style="width: 100%; margin-bottom: 10px;"
                placeholder="字体大小"
              />
              <p>字体粗细</p>
              <a-select
                v-model="brandStyles[chooseLine].fontWeight"
                style="width: 100%; margin-bottom: 10px;"
                placeholder="字体粗细">
                <a-select-option value="normal">正常</a-select-option>
                <a-select-option value="bold">加粗</a-select-option>
              </a-select>
            </template>
            <div class="checkbox-item">
              <a-checkbox v-model="brandStyles[chooseLine].smooth">平滑曲线</a-checkbox>
            </div>
          </div>
        </a-card>

        <!-- 图例设置 -->
        <a-card title="图例设置" style="margin-bottom: 10px;">
          <a-select v-model="legendPosition" style="width: 100%;" placeholder="图例位置" :options="legendPositions">
          </a-select>
        </a-card>
      </div>
    </div>
  </a-card>
</template>

<script>
import * as echarts from 'echarts'
import { baseMixin } from '@/store/app-mixin'
import { Compact } from 'vue-color'

export default {
  name: 'Index',
  mixins: [baseMixin],
  components: {
    Compact
  },
  data () {
    return {
      // 基础设置
      chartType: 'line',
      showToolbox: true,

      // 坐标轴设置
      activeKey: 'Y-Index',
      yAxisTitle: '',
      xAxisTitle: '',
      yMin: null,
      yMax: null,
      yAxisSplitLine: true,
      xAxisRotate: null,

      // 系列样式
      chooseLine: '',
      selectedBrands: ['Volvo', 'Ford'],
      chartTitle: '品牌对比图',
      lineColor: { hex: '#00bfae' },
      lineStyle: 'solid',
      lineStyles: ['solid', 'dashed', 'dotted'],
      showDataLabel: false,
      smooth: false,
      position: 'top',
      positions: [{ label: '顶部', value: 'top' }, { label: '底部', value: 'bottom' }, { label: '中间', value: 'middle' }, { label: '内部', value: 'inside' }, { label: '外部', value: 'outside' }, { label: '左侧', value: 'left' }, { label: '右侧', value: 'right' }],
      labelColor: { hex: '#00bfae' },
      formatter: '{c}',
      formatterOptions: [{ label: '数值', value: '{c}' }, { label: 'Y轴', value: '{a}' }, { label: 'X轴', value: '{b}' }],
      fontSize: 12,
      fontWeight: 'bold',
      brandStyles: {},

      // 图例设置
      legendPosition: 'top',
      legendPositions: [{ label: '顶部', value: 'top' }, { label: '底部', value: 'bottom' }, { label: '左侧', value: 'left' }, { label: '右侧', value: 'right' }],

      // 其他数据
      options: [
        { label: 'Volvo', value: 'Volvo' },
        { label: 'Ford', value: 'Ford' }
      ],
      chart: null,
      chartData: {
        2015: { Volvo: 50, Ford: 100 },
        2016: { Volvo: 60, Ford: 110 },
        2017: { Volvo: 80, Ford: 150 },
        2021: { Volvo: 250, Ford: 400 }
      }
    }
  },
  methods: {
    initBrandStyles () {
      this.chooseLine = this.selectedBrands[0]
      // 在组件挂载后获取动态的属性值
      this.brandStyles = {
        Volvo: {
          lineColor: { hex: '#00bfae' },
          labelColor: { hex: '#00bfae' },
          lineStyle: 'solid',
          showDataLabel: true,
          position: this.position,
          formatter: this.formatter,
          fontSize: this.fontSize,
          fontWeight: this.fontWeight,
          smooth: false
        },
        Ford: {
          lineColor: { hex: '#ff6f00' },
          labelColor: { hex: '#ff6f00' },
          lineStyle: 'dashed',
          showDataLabel: true,
          position: this.position,
          formatter: this.formatter,
          fontSize: this.fontSize,
          fontWeight: this.fontWeight,
          smooth: false
        }
      }
    },
    renderChart () {
      if (!this.chart) {
        this.chart = echarts.init(this.$refs.chart)
      }

      const seriesData = []
      const years = Object.keys(this.chartData)

      this.selectedBrands.forEach(brand => {
        const brandStyle = this.brandStyles[brand] || {}
        // 基础配置项
        const labelConfig = {
          show: brandStyle.showDataLabel !== undefined ? brandStyle.showDataLabel : this.showDataLabel,
          position: brandStyle.position || this.position,
          color: brandStyle.labelColor?.hex || this.labelColor,
          formatter: brandStyle.formatter || this.formatter,
          fontSize: brandStyle.fontSize || this.fontSize,
          fontWeight: brandStyle.fontWeight || this.fontWeight
        }

        switch (this.chartType) {
          case 'scatter':
            // Scatter 不支持 lineStyle 和 smooth
            const scatterConfig = {
              name: brand,
              type: 'scatter',
              data: years.map(year => [year, this.chartData[year][brand]]),
              itemStyle: {
                color: brandStyle.lineColor?.hex || this.lineColor
              },
              label: labelConfig
            }
            seriesData.push(scatterConfig)
            break

          case 'bar':
            // Bar 不支持 lineStyle 和 smooth
            const barConfig = {
              name: brand,
              type: 'bar',
              data: years.map(year => this.chartData[year][brand]),
              itemStyle: {
                color: brandStyle.lineColor?.hex || this.lineColor
              },
              label: labelConfig
            }
            seriesData.push(barConfig)
            break

          case 'line':
            // Line 支持 lineStyle 和 smooth，但不支持 itemStyle
            const lineConfig = {
              name: brand,
              type: 'line',
              data: years.map(year => this.chartData[year][brand]),
              lineStyle: {
                color: brandStyle.lineColor?.hex || this.lineColor,
                type: brandStyle.lineStyle || this.lineStyle
              },
              label: labelConfig,
              smooth: brandStyle.smooth !== undefined ? brandStyle.smooth : this.smooth
            }
            seriesData.push(lineConfig)
            break

          default:
            console.warn(`Unsupported chart type: ${this.chartType}`)
            break
        }
      })

      const option = {
        title: { text: this.chartTitle },
        tooltip: { trigger: 'axis' },
        legend: {
          data: this.selectedBrands,
          [this.legendPosition]: this.legendPosition
        },
        xAxis: {
          type: 'category',
          data: years,
          name: this.xAxisTitle,
          axisLabel: { rotate: this.xAxisRotate }
        },
        yAxis: {
          type: 'value',
          name: this.yAxisTitle,
          min: this.yMin,
          max: this.yMax,
          splitLine: { show: this.yAxisSplitLine }
        },
        series: seriesData,
        toolbox: this.showToolbox ? {
          feature: {
            saveAsImage: {},
            dataView: {}
          }
        } : null
      }
      this.chart.setOption(option, true)
    }
  },
  watch: {
    selectedBrands: 'renderChart',
    chartTitle: 'renderChart',
    lineColor: 'renderChart',
    lineStyle: 'renderChart',
    chartType: 'renderChart',
    showDataLabel: 'renderChart',
    smooth: 'renderChart',
    legendPosition: 'renderChart',
    yAxisTitle: 'renderChart',
    xAxisTitle: 'renderChart',
    yMin: 'renderChart',
    yMax: 'renderChart',
    yAxisSplitLine: 'renderChart',
    xAxisRotate: 'renderChart',
    showToolbox: 'renderChart',
    brandStyles: {
      handler: 'renderChart',
      deep: true
    },
    sideCollapsed () {
      setTimeout(() => this.chart.resize(), 100)
    }
  },
  created () {
  },
  mounted () {
    this.initBrandStyles()
    setTimeout(this.renderChart, 100)
  }
}
</script>

<style scoped>
.ant-input-number {
  width: 100%;
}

.sidebar {
  width: 250px;
  margin-left: 20px;
  padding: 20px;
  background-color: #f7f7f7;
}

.main-content {
  flex: 1;
  overflow: hidden;
  padding: 20px;
}

.chart-container {
  height: 600px;
  width: 100%;
}

.checkbox-group-container {
  width: 100%; /* 宽度自适应父容器 */
  max-height: 200px;
  overflow-y: auto; /* 启用垂直滚动 */
  background-color: #f7f7f7;
  border-radius: 5px;
  margin-bottom: 10px;
  padding: 10px;
}

.checkbox-item {
  margin-bottom: 8px; /* 每个选项之间的间距 */
}

/* 确保每个复选框占据整行 */
.checkbox-item .ant-checkbox-wrapper {
  width: 100%;
}

::v-deep .ant-card-body {
  padding: 10px;
}

.vc-compact {
  box-shadow: unset;
}
p{
  margin-bottom: 10px;
}
.section-title {
  font-size: 14px;
  font-weight: 1000;
  margin-bottom: 10px;
  border-bottom: 1px solid #ccc;
  height: 30px;
}
</style>
