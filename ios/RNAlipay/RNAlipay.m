#import "RNAlipay.h"
#import "Order.h"
#import "DataSigner.h"
#import "RSADataSigner.h"
#import <AlipaySDK/AlipaySDK.h>

#if __has_include(<React/RCTBridge.h>)
#import <React/RCTBridge.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTEventDispatcher.h>
#else
#import "RCTBridge.h"
#import "RCTEventEmitter.h"
#import "RCTEventDispatcher.h"
#endif

@implementation RNAlipay

@synthesize bridge = _bridge;

NSString *signedString;
NSString *outTradeNO;

RCT_EXPORT_MODULE();

- (id)init
{
    if (self = [super init]) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(processNotification:) name:@"RCTAliPay_Notification_processOrderWithPaymentResult" object:nil];
    }
    return self;
    
}

RCT_EXPORT_METHOD(payOrder:(NSDictionary *)params){
    
    NSString *orderText = [params objectForKey:@"orderText"];
    NSString *appScheme = [params objectForKey:@"appScheme"];   //应用注册scheme, 对应需要在Info.plist定义URL types
    
    // NOTE: 调用支付结果开始支付, 如没有安装支付宝app，则会走h5页面，支付回调触发这里的callback
    [[AlipaySDK defaultService] payOrder:orderText fromScheme:appScheme callback:^(NSDictionary *resultDic) {
        //        NSLog(@"payOrder reslut = %@",resultDic);
        [self.bridge.eventDispatcher sendAppEventWithName:@"alipay.mobile.securitypay.pay.onPaymentResult"
                                                     body:resultDic];
    }];
}


RCT_EXPORT_METHOD(pay:(NSDictionary *)options)
{
    /*
     *商户的唯一的parnter和seller。
     *签约后，支付宝会为每个商户分配一个唯一的 parnter 和 seller。
     */
    
    /*============================================================================*/
    /*=======================需要填写商户app申请的===================================*/
    /*============================================================================*/
    NSString *partner = @"2088821934190852";
    NSString *seller = @"cs@eascotel.com";
    NSString *rsa2PrivateKey = @"";
    NSString *rsaPrivateKey = @"MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIn7Q64wKPIeDefFSf9ziJJ46GAEsTZoedRmA9tzAov5ldsTKlBo5F8DxVtRURcjAD5uBvzpXzK3RExS+uCGduYXOLCioD0l5LXC5PYV8s9Zs04ASx9rlMnFY3ccDHkDfjviXcYHH42rI0jeZBBLqJ0PQbAjGc4qU9hKyhmfsozJAgMBAAECgYA4kNRvLXkrwNxsfrnWSpPbkEubSXEo3+3KAERmi6Cop5oipJm9OzMcLJyU0iJPPN8VuN+OgVGwpecO7xnAP/+vu6MPZ8srWSP6TD/5g1RHiDvZ93tU+XMBUSGTOAiOVDw9AtArm5S3JXfB3jm/zeY3QWSA9j4cS9yLa642zpoecQJBAMpNmjh1sL0jgp5d6Lg0pHsP7VyXcG1zvW9vH8aN+gqIj9jfqi0+TH8tGuGbJbE47uumW+moGJ2nHrjD340YsgUCQQCumwn2Y/Sx3PYe2wTV2bwHvXlTDO6RgV0Y6pv6/F9Zg+Z+UZ3Zg8YcR1464Cpce3clESAdqwvKntbbzmx9QNb1AkAoulrUBw2f5mhDjdBuVVrP07MLswlVX7nBl3OkO60dNcUP+md+WE56RBBnXx7FgRrIomNatRbPlnee5wA4ncIpAkEAph4M8WSRtfJiGmMHi6mWccq7Nd90zexEtWLnuXcIBDMwRHfD9OCgx+DCbsM8qjYw2YwcgP1tv301L3SJ1PSuTQJBAJtweM7soSH9DCl10nEnWOZM06Mp7Tlg2/fTUjFMFYewxgfQtVmtaddlgaWQLiq0GJe1Y9e3xzu14oWRZFSMN6s=";
    /*============================================================================*/
    /*============================================================================*/
    /*============================================================================*/
    
    //partner和seller获取失败,提示
    if ([partner length] == 0 ||
        [seller length] == 0 ||
        [rsaPrivateKey length] == 0)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示"
                                                        message:@"缺少partner或者seller或者私钥。"
                                                       delegate:self
                                              cancelButtonTitle:@"确定"
                                              otherButtonTitles:nil];
        [alert show];
        
        NSError *error = nil;
//        reject(@"错误", @"缺少partner或者seller或者私钥。", error);
        [self.bridge.eventDispatcher sendAppEventWithName:@"alipay.mobile.securitypay.pay.onPaymentResult"
                                                     body:@"缺少partner或者seller或者私钥。"];
        
        return;
    }
    
    
    /*
     *生成订单信息及签名
     */
    Order *order = [[Order alloc] init];
    //将商品信息赋予AlixPayOrder的成员变量
    order.partner = partner;
    order.sellerID = seller;
    order.outTradeNO = [options objectForKey:@"outTradeNO"]; //订单ID（由商家自行制定）
    outTradeNO = [options objectForKey:@"outTradeNO"];
    order.subject = [options objectForKey:@"subject"]; //商品标题
    order.body = [options objectForKey:@"body"]; //商品描述
    
    float totalFee = [[options objectForKey:@"totalFee"] floatValue];
    order.totalFee = [NSString stringWithFormat:@"%0.2f", totalFee]; //商品价格
//    order.amount = [NSString stringWithFormat:@"%0.2f", totalFee]; //商品价格
//    order.totalFee = [NSString stringWithFormat:@"%.2f", 0.01]; //商品价格
    //order.notifyURL =  @"http://hkwalker.hk/appapi/alipay.php";//回调URL
    if([options objectForKey:@"notifyURL"]){
        order.notifyURL =[options objectForKey:@"notifyURL"];
    }else{
        order.notifyURL =  @"http://www.veeroam.com/catalog/controller/customer/alipayNotify";
    }
    order.service = @"mobile.securitypay.pay";
    order.paymentType = @"1";
    order.inputCharset = @"utf-8";
    order.itBPay = @"1m";
    order.showURL = @"m.alipay.com";
    
    //海外版参数
    order.paymentInst = @"ALIPAYHK";
    order.currency = @"HKD";
    order.productCode = @"NEW_WAP_OVERSEAS_SELLER";
    order.forexBiz = @"FP";
 //   order.extend_params = [options objectForKey:@"extend_params"];
//    order.itBPay = [options objectForKey:@"itBPay"];
//    order.showURL = [options objectForKey:@"showURL"];
    
    //应用注册scheme,在AlixPayDemo-Info.plist定义URL types
    //NSString *appScheme = [options objectForKey:@"Veeroam"];
    
    //将商品信息拼接成字符串
    NSString *orderSpec = [order description];
    //NSLog(@"orderSpec = %@", orderSpec);
    
    //获取私钥并将商户信息签名,外部商户可以根据情况存放私钥和签名,只需要遵循RSA签名规范,并将签名字符串base64编码和UrlEncode
//    id<DataSigner> signer = CreateRSADataSigner(privateKey);
//    NSString *signedString = [signer signString:orderSpec];
    signedString = nil;
    RSADataSigner* signer = [[RSADataSigner alloc] initWithPrivateKey:((rsa2PrivateKey.length > 1)?rsa2PrivateKey:rsaPrivateKey)];
    if ((rsa2PrivateKey.length > 1)) {
        signedString = [signer signString:orderSpec withRSA2:YES];
    } else {
        signedString = [signer signString:orderSpec withRSA2:NO];
    }
    NSDictionary *resultDic = @{@"resultStatus": @"updateSign", @"sign": signedString, @"outTradeNO": outTradeNO};
    [self.bridge.eventDispatcher sendAppEventWithName:@"alipay.mobile.securitypay.pay.onPaymentResult"
                                                            body:resultDic];
    
    //将签名成功字符串格式化为订单字符串,请严格按照该格式
    NSString *orderString = nil;
    if (signedString != nil) {
        //应用注册scheme,在AliSDKDemo-Info.plist定义URL types
        NSString *appScheme = @"veeroam";
        
        orderString = [NSString stringWithFormat:@"%@&sign=\"%@\"&sign_type=\"%@\"",
                       orderSpec, signedString, @"RSA"];
//        orderString = [NSString stringWithFormat:@"%@&sign=\"%@\"&sign_type=\"%@\"",
//                       orderSpec, signedString, ((rsa2PrivateKey.length > 1)?@"RSA2":@"RSA")];
        
//        NSLog(@"orderString = %@", orderString);
//        [[AlipaySDK defaultService] payOrder:orderString fromScheme:appScheme callback:^(NSDictionary *resultDic) {
//            NSLog(@"reslut = %@",resultDic);
//        }];
        
        [[NSUserDefaults standardUserDefaults] setObject:@"isActived" forKey:@"alipay"];
        [[AlipaySDK defaultService] payOrder:orderString fromScheme:appScheme callback:^(NSDictionary *resultDic) {
            //        NSLog(@"payOrder reslut = %@",resultDic);
            [self.bridge.eventDispatcher sendAppEventWithName:@"alipay.mobile.securitypay.pay.onPaymentResult"
                                                         body:resultDic];
        }];
        
//        [[AlipaySDK defaultService] payOrder:orderString fromScheme:appScheme callback:^(NSDictionary *resultDic) {
//            //        NSLog(@"payOrder reslut = %@",resultDic);
//            [self.bridge.eventDispatcher sendAppEventWithName:@"alipay.mobile.securitypay.pay.onPaymentResult"
//                                                         body:resultDic];
//        }];
        
        //resolve(@"支付成功!");
        
//        [[AlipaySDK defaultService] payOrder:orderString fromScheme:appScheme callback:^(NSDictionary *resultDic) {
//        [[AlipaySDK defaultService] payOrder:orderString fromScheme:appScheme callback:^(NSDictionary *resultDic) {
//            NSLog(@"reslut = %@",resultDic);
//
//            NSLog(@"orderString = %@", @"支付成功啦啦啦啦！");
//            resolve(resultDic);
//        }];
        
//        return;
    }
    
//    NSError *error = nil;
//    reject(@"支付失败", @"参数错误", error);
//    [self.bridge.eventDispatcher sendAppEventWithName:@"alipay.mobile.securitypay.pay.onPaymentResult"
//                                                 body:@"参数错误。"];
    
//    if (str) {
//        resolve(str);
//    } else {
//        NSError *error = nil;
//        reject(@"no_events", @"There were no events", error);
//    }
}

- (void)processNotification:(NSNotification *)notification {
    NSDictionary *resultDic = notification.userInfo;
    //    NSLog(@"RCTAliPay -> processOrderWithPaymentResult resultDic = %@", resultDic);
    if(resultDic == nil ){
        NSString *fetchTradeToken  = AlipaySDK.defaultService.currentVersion;
        BOOL isLogined  = AlipaySDK.defaultService.isLogined;
        
        resultDic = @{@"resultStatus": @"nil", @"sign": signedString, @"outTradeNO": outTradeNO};
    }
    [self.bridge.eventDispatcher sendAppEventWithName:@"alipay.mobile.securitypay.pay.onPaymentResult"
                                                 body:resultDic];
}


- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
