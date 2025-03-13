self.importScripts('/js/pyodide/pyodide.js')
const pyodide = loadPyodide().then((p) => {
  p.loadPackage(['micropip', 'numpy', 'scikit-learn', 'scipy', 'pandas'])
  return p
})
self.onmessage = async (event) => {
  const { pythonCode } = event.data
  runPythonCode(await pyodide, pythonCode)
}
const runPythonCode = (pyodide, pythonCode) => {
  if (!pythonCode) {
    self.postMessage({ error: 'Python 代码为空' })
    return
  }
  try {
    const result = pyodide.runPython(pythonCode)
    // 转换为可序列化的 JavaScript 数据
    self.postMessage({ result: result.toString() ? result.toString() : result })
  } catch (error) {
    self.postMessage({ error: error.message })
  }
}
