var nodeconf;
nodeconf = function(port, hub){
  var ref$, hubHost, hubPort;
  ref$ = hub.match(/([\w\d\.]+):(\d+)/), hubHost = ref$[1], hubPort = ref$[2];
  hubPort = +hubPort;
  console.log(hubHost, hubPort);
  return {
    capabilities: [{
      browserName: "phantomjs",
      maxInstances: 5,
      seleniumProtocol: "WebDriver"
    }],
    configuration: import$({
      hub: hub,
      hubHost: hubHost,
      hubPort: hubPort,
      port: port
    }, {
      proxy: "org.openqa.grid.selenium.proxy.DefaultRemoteProxy",
      maxSession: 5,
      register: true,
      registerCycle: 5000,
      role: "wd",
      url: "http://127.0.0.1:" + port,
      remoteHost: "http://127.0.0.1:" + port
    })
  };
};
module.exports = {
  register: function(port, hub){
    var page;
    port = +port;
    page = require('webpage').create();
    if (!hub.match(/\/$/)) {
      hub += '/';
    }
    console.log(JSON.stringify(nodeconf(port, hub)));
    return page.open(hub + 'grid/register', {
      operation: 'post',
      data: JSON.stringify(nodeconf(port, hub)),
      headers: {
        'Content-Type': 'application/json'
      }
    }, function(status){
      if (status !== 'success') {
        return console.log("Unable to register with grid " + hub + ": " + status);
      } else {
        return console.log(("registered with grid hub: " + hub) + page.content);
      }
    });
  }
};
function import$(obj, src){
  var own = {}.hasOwnProperty;
  for (var key in src) if (own.call(src, key)) obj[key] = src[key];
  return obj;
}