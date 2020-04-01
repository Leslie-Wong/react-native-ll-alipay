#if __has_include(<React/RCTBridgeModule.h>)
#import <React/RCTBridgeModule.h>
#import <React/RCTLog.h>
#import <React/RCTConvert.h>
#import <React/RCTBridge.h>
#import <React/RCTUtils.h>
#import <React/RCTEventDispatcher.h>
#import <React/RCTEventEmitter.h>
#else
#import "RCTBridgeModule.h"
#import "RCTLog.h"
#import "RCTConvert.h"
#import "RCTBridge.h"
#import "RCTUtils.h"
#import "RCTEventDispatcher.h"
#import "RCTEventEmitter.h"
#endif

#import <UIKit/UIKit.h>

@interface RNAlipay : NSObject <RCTBridgeModule>

@property(nonatomic,strong)  RNAlipay *payService;

- (void) processNotification:(NSDictionary *)resultDic;

@end

