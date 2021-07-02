//
//  ZipPlugin.swift
//  
//
//  Created by Pace Wisdom on 01/07/21.
//

import Foundation
import SSZipArchive


@objc(ZipPlugin) class ZipPlugin : CDVPlugin {
  
    //zip Method.
    func zip(_ command: CDVInvokedUrlCommand?) {
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAsString: "Hi compress")
        commandDelegate.send(pluginResult, callbackId: command?.callbackId)
    }

//unzip Method.
  func unzip(_ command: CDVInvokedUrlCommand?) {
    let sourceDictionary = getSourceDictionary(command?.argument(atIndex: 0))
    let targetOptions = command?.argument(atIndex: 1)
    let targetPath = targetOptions?.value(forKey: "target")?.replacingOccurrences(of: "file://", with: "")
    let sourcePath = sourceDictionary["path"] as? String
    let sourceName = sourceDictionary["name"] as? String

    let success = SSZipArchive.unzipFile(
        atPath: (sourcePath ?? "") + (sourceName ?? ""),
        toDestination: targetPath)


    let responseObj = [
        "success": NSNumber(value: success),
        "message": "-"
    ]

    let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAsDictionary: responseObj)
    commandDelegate.send(pluginResult, callbackId: command?.callbackId)
}


// getsource method.
func getSourceDictionary(_ sourceString: String?) -> [AnyHashable : Any]? {
    let lastIndexSlash = (sourceString as NSString?)?.range(of: "/", options: .backwards).location ?? 0
    let path = (sourceString as NSString?)?.substring(with: NSRange(location: 0, length: lastIndexSlash + 1))
    let name = (sourceString as NSString?)?.substring(from: lastIndexSlash + 1)
    let sourceDictionary = [
        "path": path?.replacingOccurrences(of: "file://", with: "") ?? "",
        "name": name ?? ""
    ]
    return sourceDictionary
}


// jsevent method.
func jsEvent(_ event: String?, _ data: String?) {
    var eventStrig = "cordova.fireDocumentEvent('\(event ?? "")'"
    // NSString *eventStrig = [NSString stringWithFormat:@"console.log('%@'", event];

    if let data = data {
        eventStrig = "\(eventStrig),\(data)"
    }

    eventStrig = eventStrig + ");"

    commandDelegate.evalJs(eventStrig)
}

// dic method.
func dictionary(toJSONString toCast: [AnyHashable : Any]?) -> String? {
    var error: Error?
    var jsonData: Data? = nil
    do {
        if let toCast = toCast {
            jsonData = try JSONSerialization.data(withJSONObject: toCast, options: .prettyPrinted)
        }
    } catch {
    }
    if jsonData == nil {
        return nil
    } else {
        if let jsonData = jsonData {
            return String(data: jsonData, encoding: .utf8)
        }
        return nil
    }
}


}
