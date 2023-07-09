#!/usr/bin/env node

import {} from ''
const child = require('child_process')

child.exec(__dirname + '/ReactNativeUpdateCli.exe', (err, stdout, stderr) => {

    console.log(err, stdout, stderr)

})
