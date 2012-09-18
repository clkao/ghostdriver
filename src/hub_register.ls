nodeconf = (port, hub) ->
  [,hubHost, hubPort] = hub.match /([\w\d\.]+):(\d+)/
  hubPort = +hubPort
  console.log hubHost, hubPort
  capabilities: [
    browserName: "phantomjs"
    maxInstances: 5
    seleniumProtocol: "WebDriver"
  ]
  configuration: { hub, hubHost, hubPort, port } <<< do
    proxy: "org.openqa.grid.selenium.proxy.DefaultRemoteProxy"
    maxSession: 5
    register: true
    registerCycle: 5000
    role: "wd"
    url: "http://127.0.0.1:" + port
    remoteHost:"http://127.0.0.1:" + port

module.exports = register: (port, hub) ->
  port = +port
  page = require('webpage').create!
  hub += '/' unless hub.match /\/$/
  console.log JSON.stringify nodeconf port, hub
  status <- page.open hub + 'grid/register',
    operation: 'post'
    data: JSON.stringify nodeconf port, hub
    headers: 'Content-Type': 'application/json'
  if status isnt 'success'
    console.log "Unable to register with grid #hub: #status"
  else
    console.log "registered with grid hub: #hub" + page.content
