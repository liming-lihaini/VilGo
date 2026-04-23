import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    // 处理文件下载（blob类型）或非JSON响应
    const contentType = response.headers['content-type']
    if (response.config.responseType === 'blob' || (contentType && contentType.includes('application/octet-stream'))) {
      return response.data
    }
    // 如果响应不是JSON格式，也直接返回
    if (typeof response.data !== 'object' || response.data === null) {
      return response.data
    }
    const res = response.data
    if (res.code !== 200) {
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res.data
  },
  error => {
    return Promise.reject(error)
  }
)

export default request