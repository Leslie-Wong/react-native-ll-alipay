#import "RNAlipay.h"
#import "Order.h"
#import "DataSigner.h"
#import "RSADataSigner.h"
#import <AlipaySDK/AlipaySDK.h>

@implementation RNAlipay

RCT_EXPORT_MODULE();

RCT_REMAP_METHOD(pay, options:(NSDictionary *)options
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
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
        reject(@"错误", @"缺少partner或者seller或者私钥。", error);
        
        return;
    }
    
    /*
     *生成订单信息及签名
     */
    //将商品信息赋予AlixPayOrder的成员变量
    Order *order = [[Order alloc] init];
    order.partner = partner;
    order.sellerID = seller;
    order.outTradeNO = [options objectForKey:@"outTradeNO"]; //订单ID（由商家自行制定）
    order.subject = [options objectForKey:@"subject"]; //商品标题
    order.body = [options objectForKey:@"body"]; //商品描述
    
    float totalFee = [[options objectForKey:@"totalFee"] floatValue];
    order.totalFee = [NSString stringWithFormat:@"%0.2f", totalFee]; //商品价格
//    order.totalFee = [NSString stringWithFormat:@"%.2f", 0.01]; //商品价格
    order.notifyURL =  @"http://www.google.com";//回调URL
    
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
    NSString *signedString = nil;
    RSADataSigner* signer = [[RSADataSigner alloc] initWithPrivateKey:((rsa2PrivateKey.length > 1)?rsa2PrivateKey:rsaPrivateKey)];
    if ((rsa2PrivateKey.length > 1)) {
        signedString = [signer signString:orderSpec withRSA2:YES];
    } else {
        signedString = [signer signString:orderSpec withRSA2:NO];
    }
    
    //将签名成功字符串格式化为订单字符串,请严格按照该格式
    NSString *orderString = nil;
    if (signedString != nil) {
        //应用注册scheme,在AliSDKDemo-Info.plist定义URL types
        NSString *appScheme = @"Veeroam";
        
        orderString = [NSString stringWithFormat:@"%@&sign=\"%@\"&sign_type=\"%@\"",
                       orderSpec, signedString, @"RSA"];
//        orderString = [NSString stringWithFormat:@"%@&sign=\"%@\"&sign_type=\"%@\"",
//                       orderSpec, signedString, ((rsa2PrivateKey.length > 1)?@"RSA2":@"RSA")];
        
        NSLog(@"orderString = %@", orderString);
        
        //resolve(@"支付成功!");
        
//        [[AlipaySDK defaultService] payOrder:orderString fromScheme:appScheme callback:^(NSDictionary *resultDic) {
        [[AlipaySDK defaultService] payOrder:orderString fromScheme:appScheme callback:^(NSDictionary *resultDic) {
            NSLog(@"reslut = %@",resultDic);
            
            NSLog(@"orderString = %@", @"支付成功啦啦啦啦！");
            resolve(resultDic);
        }];
        return;
    }
    
    NSError *error = nil;
    reject(@"支付失败", @"参数错误", error);
    
//    if (str) {
//        resolve(str);
//    } else {
//        NSError *error = nil;
//        reject(@"no_events", @"There were no events", error);
//    }
}

@end
