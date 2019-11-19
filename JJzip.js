const cacheDirectory = (require('./isChrome')()) ? 'filesystem:' + window.location.origin + '/temporary/' : 'file:///temporary/';
const dataDirectory = (require('./isChrome')()) ? 'filesystem:' + window.location.origin + '/persistent/' : 'file:///persistent/';

function zip(successCallback, errorCallback, data) {
  var pathToZip = data[0];
  var pathToTargetZip = data[1].target + data[1].name + '.zip';
  var pathErrors = false;

  var fileSystemChoice;
  var targetFileSystemChoice;

  if (pathToZip.startsWith(cacheDirectory)) {
    fileSystemChoice = window.TEMPORARY;
    pathToZip = pathToZip.replace(cacheDirectory, '');

  } else if (pathToZip.startsWith(dataDirectory)) {
    fileSystemChoice = window.PERSISTENT;
    pathToZip = pathToZip.replace(dataDirectory, '');

  } else {
    pathErrors = true;
  }

  if (pathToTargetZip.startsWith(cacheDirectory)) {
    targetFileSystemChoice = window.TEMPORARY;
    pathToTargetZip = pathToTargetZip.replace(cacheDirectory, '');

  } else if (pathToTargetZip.startsWith(dataDirectory)) {
    targetFileSystemChoice = window.PERSISTENT;
    pathToTargetZip = pathToTargetZip.replace(dataDirectory, '');

  } else {
    pathErrors = true;
  }

  if (!pathErrors) {
    window.requestFileSystem(fileSystemChoice, 0, (fs) => {
      readDirectory(fs.root, pathToZip).then(async (files) => {
        var zip = new JSZip();

        for (let file of files) {
          zip.file(file.filePath, file.arrayBuffer);
        }

        zip.generateAsync({type:'arraybuffer'}).then((arrayBuffer) => {
          window.requestFileSystem(targetFileSystemChoice, arrayBuffer.byteLength, (tfs) => {
            saveZip(tfs.root, pathToTargetZip, arrayBuffer).then(() => {
              successCallback({success: true});
            }).catch((error) => {
              errorCallback(error);
            });
          }, (error) => {
            errorCallback(error);
          });
        }, (error) => {
          errorCallback(error);
        });
      }).catch((error) => {
        errorCallback(error);
      });
    }, (error) => {
      errorCallback(error);
    });
  } else {
    errorCallback('PATH_NOT_VALID');
  }
}

function unzip(successCallback, errorCallback, data) {
  //successCallback();
  errorCallback();
}

//Reads directory and subdirectories and returns an array with filePath and file data as ArrayBuffer
function readDirectory(root, path) {
  return new Promise((resolve, reject) => {
    var files = [];

    root.getDirectory(path, {create: false}, (directoryEntry) => {
      var dirReader = directoryEntry.createReader();

      dirReader.readEntries(async (entries) => {
        for (let entry of entries) {
          try {
            if (entry.isDirectory) {
              let subDirFiles = await readDirectory(root, entry.fullPath);

              for (let subFile of subDirFiles) {
                files.push(subFile);
              }
            } else {
              files.push(await getFile(root, entry.fullPath));
            }
          } catch (error) {
            reject(error);
          }
        }
        resolve(files);
      }, (error) => {
        reject(error);
      });
    }, (error) => {
      reject(error);
    });
  });
}

function getFile(root, filePath) {
  return new Promise((resolve, reject) => {
    if (filePath.startsWith('/')) filePath = filePath.substring(1);

    root.getFile(filePath, {}, (fileEntry) => {
      fileEntry.file((file) => {
        let reader = new FileReader();

        reader.onload = function() {
          resolve({
            filePath: filePath,
            arrayBuffer: reader.result
          });
        };

        reader.onerror = function() {
          reject(reader.error);
        }

        reader.readAsArrayBuffer(file);

      }, (error) => {
        reject(error);
      });
    }, (error) => {
      reject(error);
    });
  });
}

function saveZip(root, filePath, arrayBuffer) {
  return new Promise((resolve, reject) => {
    root.getFile(filePath, {create: true}, (fileEntry) => {
      fileEntry.createWriter((fileWriter) => {
        fileWriter.onwriteend = (e) => {
          resolve();
        };

        fileWriter.onerror = (error) => {
          reject(error);
        }

        fileWriter.write(arrayBuffer);

      }, (error) => {
        reject(error);
      });
    }, (error) => {
      reject(error);
    });
  });
}

module.exports = {
  zip: zip,
  unzip: unzip
};

require( "cordova/exec/proxy" ).add( "JJzip", module.exports );
