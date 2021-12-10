'use strict';
const settings = require('./lib/config.js')
const task = require('./lib/migratedb.js')

task(Object.assign(settings, { phase: settings.options.env}));
