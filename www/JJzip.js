/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - https://github.com/jjdltc
 * See a full copy of license in the root folder of the project
 */

var argscheck       = require('cordova/argscheck'),
    exec            = require('cordova/exec');

function JJzip() { }

/**
 *
 * @param {String} Path to the file or directory to zip
 * @param {String} Object with options of the action like
 * {
 *      target  : "Path/to/place/result/file"
 *      , name  : if set, put the result this name, otherwise put the source file name  
 * }
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
JJzip.prototype.zip = function(file, options, successCallback, errorCallback) {
    argscheck.checkArgs('sOFF', 'JJzip.zip', arguments);
    exec(successCallback, errorCallback, "JJzip", "zip", [file, options]);
};

/**
 *
 * @param {String} Path to the zip file
 * @param {String} Object with options of the action like
 * {
 *      target  : "Path/to/place/result/file"
 *      , name  : if set, put the result this name, otherwise put the source file name  
 * }
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
JJzip.prototype.unzip = function(file, options, successCallback, errorCallback) {
    argscheck.checkArgs('sOFF', 'JJzip.unzip', arguments);
    exec(successCallback, errorCallback, "JJzip", "unzip", [file, options]);
};

module.exports = new JJzip();