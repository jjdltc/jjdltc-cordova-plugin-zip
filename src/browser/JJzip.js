const cacheDirectory = (require('./isChrome')()) ? 'filesystem:' + window.location.origin + '/temporary/' : 'file:///temporary/';
const dataDirectory = (require('./isChrome')()) ? 'filesystem:' + window.location.origin + '/persistent/' : 'file:///persistent/';


function zip(successCallback, errorCallback, data) {
  var pathToZipLocalPath = getLocalPathAndFileSystem(data[0]);
  var pathToTargetZipLocalPath = getLocalPathAndFileSystem(data[1].target);

  if (!pathToZipLocalPath.error && !pathToTargetZipLocalPath.error) {
    pathToTargetZipLocalPath.localPath += data[1].name + '.zip';

    window.requestFileSystem(pathToZipLocalPath.fileSystem, 0, (fs) => {
      readDirectory(fs.root, pathToZipLocalPath.localPath).then(async (files) => {
        var zip = new JSZip();

        for (let file of files) {
          zip.file(file.filePath, file.arrayBuffer);
        }

        zip.generateAsync({type:'arraybuffer'}).then((arrayBuffer) => {
          window.requestFileSystem(pathToTargetZipLocalPath.fileSystem, arrayBuffer.byteLength, (tfs) => {
            writeFile(tfs.root, pathToTargetZipLocalPath.localPath, arrayBuffer).then(() => {
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
  var zipFileLocalPath = getLocalPathAndFileSystem(data[0]);
  var targetDirLocalPath = getLocalPathAndFileSystem(data[1].target);

  if (!zipFileLocalPath.error && !targetDirLocalPath.error) {
    window.requestFileSystem(zipFileLocalPath.fileSystem, 0, (fs) => {
      readFile(fs.root, zipFileLocalPath.localPath).then((data) => {
        var zip = new JSZip();

        zip.loadAsync(data.arrayBuffer, {createFolders: true}).then((zip) => {
          window.requestFileSystem(targetDirLocalPath.fileSystem, data.arrayBuffer.byteLength * 1.1, (tfs) => {
            const entries = Object.keys(zip.files).map(key => zip.files[key]);
            var promises = [];

            for (let directory of entries.filter((e) => e.dir == true)) {
              promises.push(createDirectory(tfs.root, (targetDirLocalPath.localPath + directory.name).split('/')));
            }

            Promise.all(promises).then(() => {
              var filePromises = [];

              for (let file of entries.filter((e) => e.dir == false)) {
                filePromises.push(writeZipedFileToFileSystem(tfs.root, targetDirLocalPath.localPath, file));
              }

              Promise.all(filePromises).then(() => {
                successCallback({success: true});
              }).catch((error) => {
                errorCallback(error);
              });
            }).catch((error) => {
              errorCallback(error);
            });
          }, (error) => {
            errorCallback(error);
          });
        }).catch((error) => {
          errorCallback(error);
        });
      }).catch((error) => {
        errorCallback(error);
      });
    }, (error) => {
      errorCallback(error);
    });
  } else {
    errorCallback('INVALID_PATH');
  }
}

/**
 *
 * Gets the localPath according to the fileSystem (TEMPORARY or PERSISTENT).
 *
 * @param {String} Path to the file or directory to check
 * @returns {Object} value with informations to requestFileSystem later
 * @returns {string} value.localPath The localPath in relation with fileSystem.
 * @returns {number} value.fileSystem the fileSystem (TEMPORARY or PERSISTENT).
 * @returns {error} value.error if the path is not valid.
 * @returns {message} value.message error message.
 */
function getLocalPathAndFileSystem(pathToCheck) {
  let ret = {
    localPath: '',
    fileSystem: window.TEMPORARY
  };

  if (pathToCheck.startsWith(cacheDirectory)) {
    ret.localPath = pathToCheck.replace(cacheDirectory, '');
    ret.fileSystem = window.TEMPORARY;

  } else if (pathToCheck.startsWith(dataDirectory)) {
    ret.localPath = pathToCheck.replace(dataDirectory, '');
    ret.fileSystem = window.PERSISTENT;

  } else {
    return {error: true, message: 'INVALID_PATH'};
  }

  if (!ret.localPath.endsWith('/')) ret.localPath += '/';

  return ret;
}

/**
 *
 * Create directory and subdirectories in the root folder of the fileSystem
 *
 * @param {String} Root is the root folder of the fileSystem.
 * @param {Array} folders is an array containing all the folders and subfolders structure.
 * @returns {Promise} which resolves if full directory has been created and rejects if not.
 */
function createDirectory(root, folders) {
  return new Promise((resolve, reject) => {
    //Code from https://www.html5rocks.com/en/tutorials/file/filesystem/
    // Throw out './' or '/' and move on to prevent something like '/foo/.//bar'.
    if (folders[0] == '.' || folders[0] == '') {
      folders = folders.slice(1);
    }

    root.getDirectory(folders[0], {create: true}, (dirEntry) => {
      // Recursively add the new subfolder (if we still have another to create).
      if (folders.length && folders[1] != '') {
        createDirectory(dirEntry, folders.slice(1)).then(() => {
          resolve();
        }).catch((error) => {
          reject(error);
        });
      } else {
        resolve();
      }
    }, (error) => {
      reject(error);
    });
  });
};

/**
 *
 * Reads directory and subdirectories in the root folder of the fileSystem
 *
 * @param {String} Root is the root folder of the fileSystem.
 * @param {String} Path is the directory to be red.
 * @returns {Promise} which resolves with all the files as an array of arrayBuffer and rejects if something failed.
 */
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
              files.push(await readFile(root, entry.fullPath));
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

/**
 *
 * Reads a file in the fileSystem as and arrayBuffer.
 *
 * @param {String} Root is the root folder of the fileSystem.
 * @param {String} Path is the file to be red.
 * @returns {Promise} which resolves with an Object containing filePath and arrayBuffer, rejects if something went wrong.
 */
function readFile(root, filePath) {
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

/**
 *
 * Write a file in fileSystem.
 *
 * @param {String} Root is the root folder of the fileSystem.
 * @param {String} FilePath is where the file should be written.
  * @param {ArrayBuffer} arrayBuffer is the content of the file as an arrayBuffer.
 * @returns {Promise} which resolves if file has been written, rejects if something went wrong.
 */
function writeFile(root, filePath, arrayBuffer) {
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

/**
 *
 * Write an unzipped file to the fileSystem.
 *
 * @param {String} Root is the root folder of the fileSystem.
 * @param {String} FilePath is where the file should be written.
  * @param {Object} zipFile is zipped file.
 * @returns {Promise} which resolves if file has been written, rejects if something went wrong.
 */
function writeZipedFileToFileSystem(root, targetDirPath, zipFile) {
  return new Promise((resolve, reject) => {
      zipFile.async('arrayBuffer').then((arrayBuffer) => {
        root.getFile(targetDirPath + zipFile.name, {create: true}, (fileEntry) => {
          writeFile(root, targetDirPath + zipFile.name, arrayBuffer).then(() => {
            resolve();
          }).catch((error) => {
            reject(error);
          });
        }, (error) => {
          reject(error);
        });
      }).catch((error) => {
        reject(error);
      });
  });
}

module.exports = {
  zip: zip,
  unzip: unzip
};

require( "cordova/exec/proxy" ).add( "JJzip", module.exports );
