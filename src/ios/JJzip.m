/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - https://github.com/jjdltc
 * See a full copy of license in the root folder of the project
 */

#import "JJzip.h"
#import <Cordova/CDV.h>
#import <ZipArchive/SSZipArchive.h>

@implementation JJzip

- (void)zip:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Hi compress"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];    
}

- (void)unzip:(CDVInvokedUrlCommand*)command {
    NSDictionary *sourceDictionary = [self getSourceDictionary:[command argumentAtIndex:0]];
    NSData *targetOptions = [command argumentAtIndex:1];
    NSString *targetPath = [[targetOptions valueForKey:@"target"] stringByReplacingOccurrencesOfString:@"file://" withString:@""];
    NSString *sourcePath = [sourceDictionary objectForKey:@"path"];
    NSString *sourceName = [sourceDictionary objectForKey:@"name"];

    BOOL success = [SSZipArchive
                    unzipFileAtPath: [sourcePath stringByAppendingString:sourceName]
                    toDestination: targetPath];
    
    
    NSDictionary *responseObj = @{
                                  @"success" : [NSNumber numberWithBool:success],
                                  @"message" : @"-"
                                  };
    
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:responseObj];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (NSDictionary*)getSourceDictionary:(NSString*)sourceString{
    NSInteger lastIndexSlash = [sourceString rangeOfString:@"/" options:NSBackwardsSearch].location;
    NSString *path = [sourceString substringWithRange:NSMakeRange(0, lastIndexSlash+1)];
    NSString *name = [sourceString substringFromIndex:lastIndexSlash+1];
    NSDictionary *sourceDictionary = @{
                                      @"path": [path stringByReplacingOccurrencesOfString:@"file://" withString:@""],
                                      @"name": name
                                      };
    return sourceDictionary;
}

- (void)jsEvent:(NSString*)event:(NSString*)data{
    NSString *eventStrig = [NSString stringWithFormat:@"cordova.fireDocumentEvent('%@'", event];
    // NSString *eventStrig = [NSString stringWithFormat:@"console.log('%@'", event];
    
    if(data != nil){
        eventStrig = [NSString stringWithFormat:@"%@,%@", eventStrig, data];
    }
    
    eventStrig = [eventStrig stringByAppendingString:@");"];
    
    [self.commandDelegate evalJs:eventStrig];
}

- (NSString*) dictionaryToJSONString:(NSDictionary*)toCast{
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:toCast options:NSJSONWritingPrettyPrinted error:&error];
    if(!jsonData){
        return nil;
    }
    else{
        return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
}

@end
