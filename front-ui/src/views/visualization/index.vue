<template>
  <a-card title="可视化图表" style="overflow: hidden;">
    <template v-slot:extra>
      <!--excel或者csv-->
      <a-upload
        :showUploadList="false"
        :accept="'.csv,.xls,.xlsx'"
        :beforeUpload="beforeUpload"
      >
        <a-button> <a-icon type="upload" /> 上传数据 </a-button>
      </a-upload>
      <a-button @click="() => { showDataSource = !showDataSource;fetchDsList() }">{{ '从数据源获取数据' }}</a-button>
      <a-button @click="() => { showTable = !showTable;initializeHandsontable() }">{{ '显示数据表格' }}
      </a-button>
    </template>
    <div style="display: flex;" class="chart-div">
      <div class="left-sidebar">
        <div v-for="type in chartTypes" :key="type.type" class="icon-div" :class="{ activeChartType: chartType === type.type }" @click="chartType = type.type">
          <a-icon :type="type['icon-type']" class="sidebar-icon"/>
          <span>{{ type.label }}</span>
        </div>
      </div>
      <div class="main-content" >
        <div class="chart-container" ref="chart"/>
      </div>
      <div class="right-sidebar">
        <!-- 图例设置 -->
        <a-card title="图例设置" style="margin-bottom: 10px;">
          <a-select v-model="legendPosition" style="width: 100%;" placeholder="图例位置" :options="legendPositions">
          </a-select>
        </a-card>

        <!-- 基础设置 -->
        <a-card title="基础设置" style="margin-bottom: 10px;">
          <a-input v-model="chartTitle" placeholder="图表标题" style="margin-bottom: 10px;"/>
          <a-checkbox v-model="showToolbox">显示工具栏</a-checkbox>
        </a-card>

        <!-- 坐标轴设置 -->
        <a-card
          title="坐标轴设置"
          style="margin-bottom: 10px;"
        >
          <a-tabs v-model="activeKey">
            <a-tab-pane key="Y-Index" tab="Y轴设置"/>
            <a-tab-pane key="X-Index" tab="X轴设置"/>
          </a-tabs>
          <div v-show="activeKey === 'Y-Index'">
            <a-input v-model="yAxisTitle" placeholder="Y轴标题" style="margin-bottom: 10px;"/>
            <div style="display: flex; gap: 10px;margin-bottom: 10px;">
              <a-input-number v-model="yMin" placeholder="最小值" style="width: 100%;"/>
              <a-input-number v-model="yMax" placeholder="最大值" style="width: 100%;"/>
            </div>
            <div class="checkbox-item">
              <a-checkbox v-model="yAxisSplitLine">显示网格线</a-checkbox>
            </div>
            <div class="checkbox-item">
              <a-checkbox v-model="showMarkPoint">显示标记点</a-checkbox>
            </div>
            <div class="checkbox-item">
              <a-checkbox v-model="showMarkLine">显示标记线</a-checkbox>
            </div>
            <div class="checkbox-group-container" v-if="YIndex.length > 0">
              <a-checkbox-group v-model="selectedYIndex">
                <div class="checkbox-item" v-for="option in YIndex" :key="option" @click="chooseLine = option" :class="{ activeLine: chooseLine === option }">
                  <a-checkbox :value="option">{{ option }}</a-checkbox>
                </div>
              </a-checkbox-group>
            </div>
            <div style="margin-top: 5px;text-align: right" class="checkbox-item" v-if="YIndex.length > 0">
              <a-checkbox :indeterminate="indeterminate" :checked="checkAll" @change="onCheckAllChange">
                全选
              </a-checkbox>
            </div>
          </div>
          <div v-show="activeKey === 'X-Index'">
            <a-select style="width: 100%;margin-bottom: 10px" placeholder="选择X轴" @change="changeXIndex" v-model="selectedXIndex">
              <a-select-option v-for="option in XIndex" :key="option" :value="option">{{ option }}</a-select-option>
            </a-select>
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
        <a-card :title="`系列样式 - ${chooseLine}`" style="margin-bottom: 10px;" v-if="chartStyles[chooseLine]">
          <div class="table-item">
            <div class="section-title">实例设置</div>
            <compact
              v-model="chartStyles[chooseLine].lineColor"
              style="width: 100%; margin-bottom: 10px;"/>
            <a-select v-model="chartStyles[chooseLine].lineStyle" style="width: 100%; margin-bottom: 10px;" placeholder="线条样式" :options="lineStyles">
            </a-select>
            <div class="checkbox-item">
              <a-checkbox v-model="chartStyles[chooseLine].smooth">平滑曲线</a-checkbox>
            </div>
            <div class="checkbox-item">
              <a-checkbox v-model="chartStyles[chooseLine].showDataLabel">显示数据标签</a-checkbox>
            </div>
          </div>
          <div class="table-item" v-if="chartStyles[chooseLine].showDataLabel">
            <div class="section-title">数据标签设置</div>
            <p>位置</p>
            <a-select v-model="chartStyles[chooseLine].position" style="width: 100%;margin-bottom: 10px;" placeholder="位置" :options="positions">
            </a-select>
            <p>颜色</p>
            <compact
              v-model="chartStyles[chooseLine].labelColor"
              style="width: 100%; margin-bottom: 10px;"/>
            <p>格式化</p>
            <a-select
              v-model="chartStyles[chooseLine].formatter"
              style="width: 100%; margin-bottom: 10px;"
              placeholder="格式化"
              :options="formatterOptions">
            </a-select>
            <p>字体大小</p>
            <a-input-number
              v-model="chartStyles[chooseLine].fontSize"
              :min="12"
              :max="24"
              style="width: 100%; margin-bottom: 10px;"
              placeholder="字体大小"
            />
            <p>字体粗细</p>
            <a-select
              v-model="chartStyles[chooseLine].fontWeight"
              style="width: 100%; margin-bottom: 10px;"
              placeholder="字体粗细">
              <a-select-option value="normal">正常</a-select-option>
              <a-select-option value="bold">加粗</a-select-option>
            </a-select>
          </div>
        </a-card>
      </div>
    </div>
    <div class="dataTable">
      <a-drawer
        title="数据表格"
        placement="right"
        :closable="false"
        :visible="showTable"
        :bodyStyle="{overflow: 'hidden', padding: 0}"
        :maskStyle="{ background: 'rgba(0, 0, 0, 0)' }"
        :width="'30vw'"
        @close="showTable = false"
      >
        <div style="margin: 30px 20px" class="chart-table" ref="chartTable"></div>
      </a-drawer>
    </div>
    <div class="dataSource">
      <a-drawer
        title="数据源设置"
        placement="right"
        :closable="false"
        :visible="showDataSource"
        :bodyStyle="{overflow: 'hidden', padding: 0}"
        :maskStyle="{ background: 'rgba(0, 0, 0, 0)' }"
        :width="'30vw'"
        @close="showDataSource = false"
      >
        <div class="data-source">
          <div class="data-source-header" v-show="showDataSource">
            <a-form :form="form" layout="vertical">
              <a-row type="flex" justify="space-around" style="margin-top: 24px;">
                <!-- 第一列：来源数据源 -->
                <a-col :span="10">
                  <a-form-item label="来源数据源" class="data-source-item">
                    <a-select
                      @change="handleFromChange"
                      v-decorator="['selectedDataSource', { rules: [{ required: true, message: '请选择来源数据源' }] }]">
                      <a-select-option v-for="table in fromDsList" :value="table.dsId" :key="table.name">
                        <div style="display: flex; align-items: center;">
                          <span class="ds-icon" style="margin-right: 8px;">
                            <img :src="dsImgObj[table.type]" alt="" style="width: 16px; height: 16px;" />
                          </span>
                          <span>{{ table.name }}</span>
                        </div>
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>

                <!-- 第二列：来源数据源表 -->
                <a-col :span="10">
                  <a-form-item label="来源数据源表" class="data-source-item">
                    <a-select
                      @change="handleFromTbChange"
                      v-decorator="['selectedSourceTable', { rules: [{ required: true, message: '请选择来源数据源表' }] }]">
                      <a-select-option v-for="table in sourceTables" :value="table" :key="table">
                        {{ table }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
              </a-row>
            </a-form>
          </div>
        </div>
      </a-drawer>
    </div>
  </a-card>
</template>

<script>
import * as echarts from 'echarts'
import { baseMixin } from '@/store/app-mixin'
import { Compact } from 'vue-color'
import * as XLSX from 'xlsx'
import Handsontable from 'handsontable'
import { fetchTables, getTableData, listQuery } from '@/api/datasource/datasource'
import { renderChart } from '@/views/visualization/renderChart'
import { dsImgObj } from '@/views/datasource/const'

const { registerLanguageDictionary, zhCN } = require('handsontable/i18n')
registerLanguageDictionary(zhCN)
export default {
  name: 'Index',
  computed: {
    dsImgObj () {
      return dsImgObj
    }
  },
  mixins: [baseMixin],
  components: {
    Compact
  },
  data () {
    return {
      fromDsList: [],
      sourceTables: [],
      form: this.$form.createForm(this),
      showTable: false,
      showDataSource: false,
      indeterminate: true,
      checkAll: false,
      // 基础设置
      chartType: 'line',
      chartTypes: [{ 'icon-type': 'line-chart', label: '折线图', type: 'line' }, { 'icon-type': 'bar-chart', label: '柱状图', type: 'bar' }, { 'icon-type': 'dot-chart', label: '散点图', type: 'scatter' }, { 'icon-type': 'pie-chart', label: '饼图', type: 'pie' }],
      showToolbox: true,

      // 坐标轴设置
      activeKey: 'Y-Index',
      yAxisTitle: '',
      xAxisTitle: '',
      yMin: null,
      yMax: null,
      yAxisSplitLine: true,
      xAxisRotate: null,
      showMarkPoint: false,
      showMarkLine: false,
      markPointData: [
        { type: 'max', name: '最大值' },
        { type: 'min', name: '最小值' }
      ],
      markLineData: [
        { type: 'average', name: '平均值' }
      ],

      // 系列样式
      chooseLine: '',
      selectedYIndex: [],
      YIndex: [],
      selectedXIndex: [],
      XIndex: [],
      xAxisData: [],
      chartTitle: '',
      lineStyles: [{ label: '实线', value: 'solid' }, { label: '虚线', value: 'dashed' }, { label: '点线', value: 'dotted' }],
      positions: [{ label: '顶部', value: 'top' }, { label: '底部', value: 'bottom' }, { label: '中间', value: 'middle' }, { label: '内部', value: 'inside' }, { label: '外部', value: 'outside' }, { label: '左侧', value: 'left' }, { label: '右侧', value: 'right' }],
      formatterOptions: [
        { label: '数值', value: '{c}' },
        { label: 'Y轴', value: '{a}' },
        { label: 'X轴', value: '{b}' },
        { label: '数值 (保留1位小数)', value: '{c|toFixed(1)}' },
        { label: '数值 (保留2位小数)', value: '{c|toFixed(2)}' },
        { label: '数值 (保留3位小数)', value: '{c|toFixed(3)}' }
      ],
      chartStyles: {},

      // 图例设置
      legendPosition: 'top',
      legendPositions: [{ label: '顶部', value: 'top' }, { label: '底部', value: 'bottom' }, { label: '左侧', value: 'left' }, { label: '右侧', value: 'right' }],

      // 其他数据
      chart: null,
      hot: null,
      chartData: [],
      chartJsonData: []
    }
  },
  methods: {
    beforeUpload (file) {
      // 判断文件类型
      const fileType = file.type
      // 限制文件大小
      if (file.size > 1024 * 1024 * 5) {
        this.$message.error('文件大小不能超过5M')
        return false
      }
      if (fileType === 'application/vnd.ms-excel' || fileType === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet') {
        this.analyzeExcelFile(file)
      } else if (fileType === 'text/csv') {
        this.analyzeCsvFile(file)
      } else {
        this.$message.error('请上传Excel或者CSV文件')
      }
      return false
    },
    // 处理上传是Excel文件
    analyzeExcelFile (file) {
      const fileReader = new FileReader()
      fileReader.readAsBinaryString(file)
      fileReader.onload = (ev) => {
        try {
          const workbook = XLSX.read(ev.target.result, {
            type: 'binary'
          })
          const workSheets = workbook.Sheets[workbook.SheetNames[0]]
          this.chartJsonData = XLSX.utils.sheet_to_json(workSheets, { header: 1 })
          console.log(this.chartJsonData)
          this.initChartData()
        } catch (e) {
          console.log(e)
        }
      }
    },
    // 处理是csv格式
    analyzeCsvFile (file) {
      const fileReader = new FileReader()
      fileReader.readAsText(file)
      fileReader.onload = (ev) => {
        try {
          // 处理解析之后的csv格式
          const result = ev.target.result.split('\n')
          this.chartJsonData = result.map((item) => {
            return item.split(',')
          })
          console.log(this.chartJsonData)
          this.initChartData()
        } catch (e) {
          console.log(e)
        }
      }
    },
    initializeHandsontable () {
      setTimeout(() => {
        if (this.hot) {
          this.hot.destroy()
        }
        const container = this.$refs.chartTable
        this.hot = new Handsontable(container, {
          data: this.chartJsonData.length > 0 ? this.chartJsonData : [[]],
          contextMenu: true,
          language: 'zh-CN',
          rowHeaders: true,
          colHeaders: [],
          autoWrapRow: false,
          autoWrapCol: false,
          minRows: 10, // 设置最少行数，确保空表格可编辑
          minCols: 10, // 保持列数
          height: '87vh',
          afterChange: (changes) => {
            if (changes) {
              this.chartJsonData = this.hot.getData()
                .map((row) => row.filter((value) => value !== '' && value !== null)) // 过滤掉空元素
                .filter((row) => row.length > 0) // 跳过完全为空的行
              if (this.chartJsonData.length > 0) {
                const YIndex = this.chartJsonData[0]
                if (JSON.stringify(YIndex) !== JSON.stringify(this.YIndex)) {
                  this.initChartData()
                } else {
                  this.chartData = this.convertJsonToObject()
                  this.xAxisData = Object.keys(this.chartData)
                }
              } else {
                this.chartJsonData = [[]]
                this.initChartData()
              }
            }
          },
          licenseKey: 'non-commercial-and-evaluation'
        })
      }, 300)
    },
    convertJsonToObject () {
      const headers = this.chartJsonData[0] // 假设第一行是列名
      const data = this.chartJsonData.slice(1) // 从第二行开始是数据
      const result = {}
      data.forEach((item) => {
        const obj = {}
        item.forEach((value, index) => {
          obj[headers[index]] = value
        })
        result[item[this.XIndex.findIndex((x) => x === this.selectedXIndex)]] = obj
      })
      return result
    },
    initChartData () {
      this.YIndex = this.chartJsonData[0].filter((item) => item !== '' && item !== null)
      this.XIndex = this.chartJsonData[0].filter((item) => item !== '' && item !== null)
      this.selectedYIndex = this.YIndex.slice(1)
      this.selectedXIndex = this.XIndex[0]
      this.chooseLine = this.selectedYIndex[0]
      this.chartData = this.convertJsonToObject()
      this.xAxisData = Object.keys(this.chartData)
      this.initChartStyles()
    },
    initChartStyles () {
      // 在组件挂载后获取动态的属性值
      this.selectedYIndex.forEach(YIndex => {
        this.$set(this.chartStyles, YIndex, {
          // 随机颜色
          lineColor: { hex: `#${Math.floor(Math.random() * 16777215).toString(16)}` },
          // 随机线条样式
          lineStyle: 'solid',
          showDataLabel: false,
          position: 'top',
          labelColor: { hex: `#${Math.floor(Math.random() * 16777215).toString(16)}` },
          formatter: '{c}',
          fontSize: 12,
          fontWeight: 'bold',
          smooth: false
        })
      })
    },
    changeXIndex () {
      this.chartData = this.convertJsonToObject()
    },
    renderChart () {
      renderChart(this)
    },
    handleFromChange (value) {
      // 切换数据源同步表单数据
      this.form.setFieldsValue({ 'selectedDataSource': value })
      // 清空表单中来源表
      this.form.setFieldsValue({ 'selectedSourceTable': '' })
      fetchTables(value).then(res => {
        this.sourceTables = res.result
      })
    },
    handleFromTbChange (value) {
      getTableData({
        dsId: this.form.getFieldValue('selectedDataSource'),
        tableName: value
      }).then(res => {
        if (res.result.length === 0) {
          this.$message.error('数据源表无数据')
        } else {
          const data = []
          data.push(Object.keys(res.result[0]))
          res.result.forEach(item => {
            data.push(Object.values(item))
          })
          this.chartJsonData = data
          this.initChartData()
        }
      })
    },
    onCheckAllChange (e) {
      Object.assign(this, {
        selectedYIndex: e.target.checked ? this.YIndex : [],
        indeterminate: false,
        checkAll: e.target.checked
      })
    },
    fetchDsList () {
      listQuery().then(res => {
        const record = res.result
        this.fromDsList = record.length > 0 && record.reduce((acc, item) => {
          if (item.type === 1) {
            acc.push({
              dsId: item.dsId,
              name: item.name,
              type: item.type
            })
          }
          return acc
        }, [])
      })
    }
  },
  watch: {
    chartData: 'renderChart',
    showTable: 'renderChart',
    selectedYIndex: 'renderChart',
    chartTitle: 'renderChart',
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
    chartStyles: {
      handler: 'renderChart',
      deep: true
    },
    sideCollapsed: 'renderChart',
    showMarkPoint: 'renderChart',
    showMarkLine: 'renderChart'
  },
  mounted () {
    this.chart = echarts.init(this.$refs.chart)
    this.renderChart()
  }
}
</script>

<style lang="less" scoped>
@import 'handsontable/styles/handsontable.min.css';
@import 'handsontable/styles/ht-theme-main.min.css';
.ant-input-number {
  width: 100%;
}

.right-sidebar {
  width: 250px;
  overflow-y: auto;
  padding: 10px;
  transition: all 0.3s;
  background-color: #f7f7f7;
  ::v-deep .ant-card-head {
    font-size: 14px;
    padding-left: 10px;
    border-left: 3px solid #1890ff;
    .ant-card-head-title {
      padding: 14px 0;
    }
  }
}
.chart-div{
  max-height: 84vh;
  min-height: 84vh;
  border: 1px solid #e8e8e8;
  border-radius: 5px;
  .main-content {
    flex: 1;
    overflow: hidden;
    padding: 20px;
    .chart-container {
      height: 100%;
      width: 100%;
    }
  }
}

.checkbox-group-container {
  width: 100%; /* 宽度自适应父容器 */
  max-height: 200px;
  overflow-y: auto; /* 启用垂直滚动 */
  background-color: #f7f7f7;
  border-radius: 5px;
}
.ant-checkbox-group {
  width: 100%;
  cursor: pointer;
}
.checkbox-item {
  padding: 5px;
  width: 100%;
  white-space: nowrap; /* 防止文本换行 */
  overflow: hidden; /* 超出部分隐藏 */
  text-overflow: ellipsis; /* 显示省略号 */
  display: inline-block;
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
  border-bottom: 1px solid #1890ff;
  height: 30px;
  position: relative;
  //上下居中文字
  line-height: 30px;
  padding-left: 10px;
}
.icon-div {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 10px;
  border-bottom: 1px solid #ccc;
}
.sidebar-icon {
  font-size: 30px;
}
.activeChartType {
  color: #1890ff;
  background-color: #f0f0f0;
}
.activeLine {
  background-color: #f0f0f0;
}
.table-item {
  margin-bottom: 10px;
  background-color: #fafafa;
  padding: 10px;
  border-radius: 5px;
}
.ant-btn{
  margin-right: 10px;
}
::v-deep .ds-icon {
  float: left;
  width: 24px;
  height: 24px;
  border-radius: 6px;
  overflow: hidden;
  margin-right: 4px;
  img {
    width: 24px;
    height: 24px;
    margin: 0;
    padding: 0;
    border: 0;
  }
}
</style>
