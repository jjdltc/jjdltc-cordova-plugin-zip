/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - https://github.com/jjdltc
 * See a full copy of license in the root folder of the project
 */

#import "JJzip.h"
#import <Cordova/CDV.h>
#import <SSZipArchive/SSZipArchive.h>

@implementation JJzip

- (void)zipFolder:(CDVInvokedUrlCommand*)command {
    NSString *sourceFolder = [NSURL URLWithString:[command argumentAtIndex:0]].path;
    NSString *targetFile = [NSURL URLWithString:[command argumentAtIndex:1]].path;

    [self.commandDelegate runInBackground:^{
        BOOL success = [SSZipArchive
                        createZipFileAtPath:targetFile
                        withContentsOfDirectory:sourceFolder];
        
        CDVPluginResult* pluginResult;
        if (success) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        }

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)zipFiles:(CDVInvokedUrlCommand*)command {
    NSArray *sourceFileUrls = [command argumentAtIndex:0];
    NSString *targetFile = [NSURL URLWithString:[command argumentAtIndex:1]].path;
    
    NSMutableArray* sourceFiles = [NSMutableArray new];
    for (NSString *sourceFileUrl in sourceFileUrls) {
        [sourceFiles addObject:[NSURL URLWithString:sourceFileUrl].path];
    }

    [self.commandDelegate runInBackground:^{
        BOOL success = [SSZipArchive
                        createZipFileAtPath:targetFile
                        withFilesAtPaths:sourceFiles];
        
        CDVPluginResult* pluginResult;
        if (success) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        }

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)unzip:(CDVInvokedUrlCommand*)command {
    NSString *sourceFile = [command argumentAtIndex:0];
    NSString *targetPath = [command argumentAtIndex:1];

    [self.commandDelegate runInBackground:^{
        BOOL success = [SSZipArchive
                        unzipFileAtPath: sourceFile
                        toDestination: targetPath];
        
        CDVPluginResult* pluginResult;
        if (success) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        }

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

@end
