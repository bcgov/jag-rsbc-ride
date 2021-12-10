'use strict';
const settings = require('./lib/config.js')
const task = require('./lib/idleapp.js')

task(Object.assign(settings, { phase: settings.options.env}));
