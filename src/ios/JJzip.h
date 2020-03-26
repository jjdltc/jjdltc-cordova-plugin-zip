/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - https://github.com/jjdltc
 * See a full copy of license in the root folder of the project
 */

#import <Cordova/CDVPlugin.h>
#import "SSZipArchive.h"

@interface JJzip : CDVPlugin

- (void)zip:(CDVInvokedUrlCommand*)command;

- (void)unzip:(CDVInvokedUrlCommand*)command;

@end
