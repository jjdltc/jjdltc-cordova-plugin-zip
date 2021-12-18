/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - https://github.com/jjdltc
 * See a full copy of license in the root folder of the project
 */

var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec');

function JJzip() { }

JJzip.prototype.zipFiles = function (file, options, successCallback, errorCallback) {
    argscheck.checkArgs('ASFF', 'JJzip.zip', arguments);
    exec(successCallback, errorCallback, "JJzip", "zipFiles", [file, options]);
};

JJzip.prototype.zipFolder = function (file, options, successCallback, errorCallback) {
    argscheck.checkArgs('SSFF', 'JJzip.zip', arguments);
    exec(successCallback, errorCallback, "JJzip", "zipFolder", [file, options]);
};

JJzip.prototype.unzip = function (file, options, successCallback, errorCallback) {
    argscheck.checkArgs('SSFF', 'JJzip.unzip', arguments);
    exec(successCallback, errorCallback, "JJzip", "unzip", [file, options]);
};

module.exports = new JJzip();
