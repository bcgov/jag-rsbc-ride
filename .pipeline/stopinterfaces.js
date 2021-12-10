'use strict';
const settings = require('./lib/config.js')
const task = require('./lib/stopinterfaces.js')

task(Object.assign(settings, { phase: settings.options.env}));
