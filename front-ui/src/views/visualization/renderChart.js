export function renderChart (VueIns) {
  const seriesData = []
  const xAxisData = VueIns.xAxisData

  VueIns.selectedYIndex.forEach(YIndex => {
    const ChartStyle = VueIns.chartStyles[YIndex] || {}
    // 基础配置项
    const labelConfig = {
      show: ChartStyle.showDataLabel,
      position: ChartStyle.position,
      color: ChartStyle.labelColor?.hex,
      formatter: ChartStyle.formatter,
      fontSize: ChartStyle.fontSize,
      fontWeight: ChartStyle.fontWeight
    }

    switch (VueIns.chartType) {
      case 'scatter':
        // Scatter 不支持 lineStyle 和 smooth
        const scatterConfig = {
          name: YIndex,
          type: 'scatter',
          data: xAxisData.map(XIndex => [XIndex, VueIns.chartData[XIndex][YIndex]]),
          itemStyle: {
            color: ChartStyle.lineColor?.hex
          },
          label: labelConfig,
          markPoint: {
            data: VueIns.showMarkPoint ? VueIns.markPointData : []
          },
          markLine: {
            data: VueIns.showMarkLine ? VueIns.markLineData : []
          }
        }
        seriesData.push(scatterConfig)
        break

      case 'bar':
        // Bar 不支持 lineStyle 和 smooth
        const barConfig = {
          name: YIndex,
          type: 'bar',
          data: xAxisData.map(XIndex => VueIns.chartData[XIndex][YIndex]),
          itemStyle: {
            color: ChartStyle.lineColor?.hex
          },
          label: labelConfig,
          markPoint: {
            data: VueIns.showMarkPoint ? VueIns.markPointData : []
          },
          markLine: {
            data: VueIns.showMarkLine ? VueIns.markLineData : []
          }
        }
        seriesData.push(barConfig)
        break

      case 'line':
        // Line 支持 lineStyle 和 smooth，但不支持 itemStyle
        const lineConfig = {
          name: YIndex,
          type: 'line',
          data: xAxisData.map(XIndex => VueIns.chartData[XIndex][YIndex]),
          lineStyle: {
            color: ChartStyle.lineColor?.hex,
            type: ChartStyle.lineStyle
          },
          label: labelConfig,
          smooth: ChartStyle.smooth,
          markPoint: {
            data: VueIns.showMarkPoint ? VueIns.markPointData : []
          },
          markLine: {
            data: VueIns.showMarkLine ? VueIns.markLineData : []
          }
        }
        seriesData.push(lineConfig)
        break

      default:
        console.warn(`Unsupported chart type: ${VueIns.chartType}`)
        break
    }
  })

  const option = {
    title: { text: VueIns.chartTitle },
    tooltip: { trigger: 'axis' },
    legend: {
      data: VueIns.selectedYIndex,
      [VueIns.legendPosition]: VueIns.legendPosition
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      name: VueIns.xAxisTitle,
      axisLabel: { rotate: VueIns.xAxisRotate }
    },
    yAxis: {
      type: 'value',
      name: VueIns.yAxisTitle,
      min: VueIns.yMin,
      max: VueIns.yMax,
      splitLine: { show: VueIns.yAxisSplitLine }
    },
    series: seriesData,
    toolbox: VueIns.showToolbox ? {
      feature: {
        saveAsImage: {},
        dataView: {},
        dataZoom: { show: true },
        restore: {}
      }
    } : null
  }
  VueIns.chart.setOption(option, true)
  setTimeout(() => { VueIns.chart.resize() }, 300)
}
