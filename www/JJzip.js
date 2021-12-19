/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - https://github.com/jjdltc
 * See a full copy of license in the root folder of the project
 */

var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec');

function JJzip() { }

JJzip.prototype.zipFiles = function (files, destination, successCallback, errorCallback) {
    argscheck.checkArgs('ASFF', 'JJzip.zip', arguments);
    exec(successCallback, errorCallback, "JJzip", "zipFiles", [files, destination]);
};

JJzip.prototype.zipFolder = function (folder, destination, successCallback, errorCallback) {
    argscheck.checkArgs('SSFF', 'JJzip.zip', arguments);
    exec(successCallback, errorCallback, "JJzip", "zipFolder", [folder, destination]);
};

JJzip.prototype.unzip = function (file, destination, successCallback, errorCallback) {
    argscheck.checkArgs('SSFF', 'JJzip.unzip', arguments);
    exec(successCallback, errorCallback, "JJzip", "unzip", [file, destination]);
};

module.exports = new JJzip();
