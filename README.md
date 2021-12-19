Zip / UnZip plugin
===

Cordova plugin to compress and decompress (__zip__/__unzip__) files and folders in `*.zip` format.

__Contributors are welcome.__

Platforms supported
* __android__
* __iOS__ (On top of: [SSZipArchive](https://cocoapods.org/pods/SSZipArchive "In case you want to be curious").)

Installation
---

`cordova plugin add cordova-zip-plugin`

Easy Use  
---  
  
The object `JJzip` is exposed in the `window`

## Operations

### Zip Folder

`zipFolder(folder, destination, successCallback, errorCallback])`

Creates a ZIP file with all the files in the given folder (recursive). Example: 

```javascript
window.JJzip.zipFolder(
    cordova.file.dataDirectory + 'inputFolder',
    cordova.file.dataDirectory + 'out.zip',
    function() { console.log('success'); },
    function(err) { console.log('error: ' + err) });
```

### Zip Files

`zipFolder(files, destination, successCallback, errorCallback])`

Creates a flat ZIP file with all the given files. Example: 

```javascript
const files = [
    cordova.file.dataDirectory + 'file1.txt',
    cordova.file.dataDirectory + 'file2.txt',
];
window.JJzip.zipFiles(
    files,
    cordova.file.dataDirectory + 'out.zip',
    function() { console.log('success'); },
    function(err) { console.log('error: ' + err) });
```

### Unzip

`unzip(zipFile, destination, successCallback, errorCallback])`

Unzips the content of the given file in the destination folder. Example: 

```javascript
window.JJzip.unzip(
    cordova.file.dataDirectory + 'input.zip',
    cordova.file.dataDirectory + 'extracted',
    function() { console.log('success'); },
    function(err) { console.log('error: ' + err) });
```
