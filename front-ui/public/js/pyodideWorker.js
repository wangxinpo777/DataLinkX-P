self.importScripts('/js/pyodide/pyodide.js')
let pyodide
self.onmessage = async (event) => {
  const { pythonCode } = event.data
  if (pythonCode === 'Test') {
    pyodide = await loadPyodide()
    await pyodide.loadPackage(['micropip', 'numpy', 'scikit-learn', 'scipy', 'pandas'])
    self.postMessage({ result: 'True' })
    return
  }
  runPythonCode(pyodide, pythonCode)
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
