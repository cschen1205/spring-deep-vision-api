import * as axios from 'axios'

function detectObjects (formData) {
  const url = `./api/detect-objects/in-image`
  return axios.post(url, formData)
  // get data
    .then(x => x.data)
}

export { detectObjects }
