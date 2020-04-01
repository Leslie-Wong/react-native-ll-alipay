package com.leslie.rn.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import java.util.Map;


import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

// import com.leslie.rn.alipay.AuthTask;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.leslie.rn.alipay.util.OrderInfoUtil2_0;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONObject;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class RNAlipayModule extends ReactContextBaseJavaModule {
	// 商户PID
	//public static final String PARTNER = "";
	// 商户收款账号
	//public static final String SELLER = "";
	// 商户私钥，pkcs8格式
	//public static final String RSA_PRIVATE = "";
	// 支付宝公钥
	//public static final String RSA_PUBLIC = "";
	//private static final int SDK_PAY_FLAG = 1;
	//private static final int SDK_CHECK_FLAG = 2;

	/** 支付宝支付业务：入参app_id */
	public static final String APPID = "";

	/** 支付宝账户登录授权业务：入参pid值 */

	// public static final String PID = "2088821934190852"; //alipay online-test
	public static final String PID = "2088621894025216"; //alipay online-test
	/** 支付宝支付业务：商户收款账号 */

	public static final String SELLER = "hksandbox_2588@alitest.com";
	// public static final String SELLER = "cs@eascotel.com";
	/** 支付宝账户登录授权业务：入参target_id值 */
	public static final String TARGET_ID = "shuzhitest";

	/** 商户私钥，pkcs8格式 */
	/** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
	/** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
	/** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
	/** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
	/** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
	public static final String RSA2_PRIVATE = "";
	// public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIn7Q64wKPIeDefFSf9ziJJ46GAEsTZoedRmA9tzAov5ldsTKlBo5F8DxVtRURcjAD5uBvzpXzK3RExS+uCGduYXOLCioD0l5LXC5PYV8s9Zs04ASx9rlMnFY3ccDHkDfjviXcYHH42rI0jeZBBLqJ0PQbAjGc4qU9hKyhmfsozJAgMBAAECgYA4kNRvLXkrwNxsfrnWSpPbkEubSXEo3+3KAERmi6Cop5oipJm9OzMcLJyU0iJPPN8VuN+OgVGwpecO7xnAP/+vu6MPZ8srWSP6TD/5g1RHiDvZ93tU+XMBUSGTOAiOVDw9AtArm5S3JXfB3jm/zeY3QWSA9j4cS9yLa642zpoecQJBAMpNmjh1sL0jgp5d6Lg0pHsP7VyXcG1zvW9vH8aN+gqIj9jfqi0+TH8tGuGbJbE47uumW+moGJ2nHrjD340YsgUCQQCumwn2Y/Sx3PYe2wTV2bwHvXlTDO6RgV0Y6pv6/F9Zg+Z+UZ3Zg8YcR1464Cpce3clESAdqwvKntbbzmx9QNb1AkAoulrUBw2f5mhDjdBuVVrP07MLswlVX7nBl3OkO60dNcUP+md+WE56RBBnXx7FgRrIomNatRbPlnee5wA4ncIpAkEAph4M8WSRtfJiGmMHi6mWccq7Nd90zexEtWLnuXcIBDMwRHfD9OCgx+DCbsM8qjYw2YwcgP1tv301L3SJ1PSuTQJBAJtweM7soSH9DCl10nEnWOZM06Mp7Tlg2/fTUjFMFYewxgfQtVmtaddlgaWQLiq0GJe1Y9e3xzu14oWRZFSMN6s=" ;
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIckaLFMMqNXgVwrIxUJlhlZ7vvJrdIDy1pxRpmLfaCRz0oud0mErqW24uUYBNRk2HUpDJdt6C+aHhtAQMWwEzG0EtVu0INR6wHvukivVH9TEY+pMCikfGAIlpFsHKBJrHniIV6+A8CduoBd3Ku2jOmbM271cHDx4FmLXYRwrRBxAgMBAAECgYADpMip1xsaalaTjJoyjAKfBly6LzLfkbie5KT5qNgnRycmoeAXfFtVHnw5nw8qRtnLNuMfBhiXh/k91NqqOdgambQGuO7E7U9CvMJ8cBR+GXLdrPoGeNXYSbIblhKPzDCLfT3T0yUEMncr5A4FoVFE9ESxu8SBVIY8nG7OPAGk/QJBAOah/89KqoQ2ws8bZ0yYMt8yGgKigVk8/KOTaA111df1byjdJjSbd8mo6Jgp6kOYRonAuYvFFunZN1wLe9CoSZcCQQCWAaawQetWJvvBjE6v0iY2mClSkTXf2MvrHMOjl5jujO3Hay76lJFXtbA5UuRQx2VFp7vdiZSz6pKohfLGkuc3AkB1LDjbCAQ5x9iRgEqrD2hJHRs523X78JAqSunCbOuIHJmtGS4f7Byfx8DkDeeHEAZGVjPuXuOVS1Zk3TC6gcVhAkB0KJEoedM/4m4fQYQJOGVo70lIa7PZRCscRCjm/FRrnWvfeYtmdTdbQRFWQm8UcmNkne+nLIufCq1InHkT9dvfAkEAsSsUNeRNv6zeus3Q3V4lmX6sAb8iBMWUuLqLH9oYCz9vWQWheUK0+CYo+LcJmT4T12sPeryaV10rc+DaiVSNUA==";
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_AUTH_FLAG = 2;

	private Promise promise;


//	@SuppressLint("HandlerLeak")
	// private Handler mHandler = new Handler() {
	// 	@SuppressWarnings("unused")
	// 	public void handleMessage(Message msg) {
	// 		switch (msg.what) {
	// 		case SDK_PAY_FLAG: {
	// 			@SuppressWarnings("unchecked")
	// 			PayResult payResult = new PayResult((Map<String, String>) msg.obj);
	// 			/**
	// 			 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
	// 			 */
	// 			String resultInfo = payResult.getResult();// 同步返回需要验证的信息
	// 			String resultStatus = payResult.getResultStatus();
	// 			// 判断resultStatus 为9000则代表支付成功
	// 			if (TextUtils.equals(resultStatus, "9000")) {
	// 				// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
	// 				//Toast.makeText(PayDemoActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
	// 				promise.resolve(payResult.toString());
	// 			} else {
	// 				// 判断resultStatus 为非“9000”则代表可能支付失败
	// 				// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
	// 				if (TextUtils.equals(resultStatus, "8000")) {
	// 					//Toast.makeText(getCurrentActivity(), "支付结果确认中", Toast.LENGTH_SHORT).show();
	// 					promise.resolve(payResult.toString());
	// 				} else {
	// 					// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
	// 					//Toast.makeText(getCurrentActivity(), "支付失败", Toast.LENGTH_SHORT).show();
	// 					promise.resolve(payResult.toString());
	// 				}
	// 			}
	// 			break;
	// 		}
	// 		// case SDK_AUTH_FLAG: {
	// 		// 	@SuppressWarnings("unchecked")
	// 		// 	AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
	// 		// 	String resultStatus = authResult.getResultStatus();

	// 		// 	// 判断resultStatus 为“9000”且result_code
	// 		// 	// 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
	// 		// 	if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
	// 		// 		// 获取alipay_open_id，调支付时作为参数extern_token 的value
	// 		// 		// 传入，则支付账户为该授权账户
	// 		// 		Toast.makeText(getCurrentActivity(),
	// 		// 				"授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
	// 		// 				.show();
	// 		// 	} else {
	// 		// 		// 其他状态值则为授权失败
	// 		// 		Toast.makeText(getCurrentActivity(),
	// 		// 				"授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

	// 		// 	}
	// 		// 	break;
	// 		// }
	// 		default:
	// 			break;
	// 		}
	// 	};
	// };
//

	ReactApplicationContext mReactContext;

	public RNAlipayModule(ReactApplicationContext reactContext) {
		super(reactContext);
		mReactContext = reactContext;
  }
  	
	@Override
  	public String getName() {
    	return "RNAlipay";
  	}

//  	@ReactMethod
//  	public void pay(ReadableMap options, final Promise promise) {
//
//		//promise = _promise;
//
//		final Float Price = Float.valueOf(options.getString("totalFee"));
//		final String Subject = options.getString("subject");
//		final String Body = options.getString("body");
//
//
//		boolean rsa2 = (RSA2_PRIVATE.length() > 0);
//		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
//		final String orderInfo = OrderInfoUtil2_0.getSignOrder(PID, SELLER, Price, Subject, Body, privateKey, rsa2);
//
//		Runnable payRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//				PayTask alipay = new PayTask(getCurrentActivity());
//				//final String result = alipay.pay(orderInfo, true);
//				final Map<String, String> result = alipay.payV2(orderInfo, true);
//				final Activity activity = getCurrentActivity();
//
//				// mHandler.post(new Runnable() {
//				// 	@Override
//				// 	public void run() {
//				// 		 // Do Work
//				// 		 	//Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
//				// 			//mHandler.removeCallbacks(this);
//				// 			//Looper.myLooper().quit();
//				// 	}
//				// });
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						//Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
//						//promise.resolve(result);
//
//						PayResult payResult = new PayResult((Map<String, String>) result);
//						/**
//						 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
//						*/
//						String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//						String resultStatus = payResult.getResultStatus();
//						// 判断resultStatus 为9000则代表支付成功
//						if (TextUtils.equals(resultStatus, "9000")) {
//							// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//							//Toast.makeText(PayDemoActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//							promise.resolve(payResult.toString());
//						} else {
//							// 判断resultStatus 为非“9000”则代表可能支付失败
//							// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
//							if (TextUtils.equals(resultStatus, "8000")) {
//								//Toast.makeText(getCurrentActivity(), "支付结果确认中", Toast.LENGTH_SHORT).show();
//								//promise.resolve(payResult.toString());
//								promise.resolve(payResult.toString());
//							} else {
//								// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//								//Toast.makeText(getCurrentActivity(), "支付失败", Toast.LENGTH_SHORT).show();
//								promise.resolve(payResult.toString());
//							}
//						}
//					}
//				});
//			}
//		};
//
//		Thread payThread = new Thread(payRunnable);
//		payThread.start();
//
//		//        String privateKey = options.getString("privateKey");
//		//        String partner = options.getString("partner");
//		//        String seller = options.getString("seller");
//		//        String outTradeNO = options.getString("outTradeNO");
//		//        String subject = options.getString("subject");
//		//        String body = options.getString("body");
//		//        String notifyURL = options.getString("notifyURL");
//		//
//		//        String totalFee;
//		//        if (options.getType("totalFee") == ReadableType.Number) {
//		//             totalFee = Double.toString(options.getDouble("totalFee"));
//		//        } else {
//		//             totalFee = options.getString("totalFee");
//		//        }
//		//
//		//        String itBPay = options.getString("itBPay");
//		//        String showURL = options.getString("showURL");
//		//
//		//		if (TextUtils.isEmpty(partner) || TextUtils.isEmpty(privateKey) || TextUtils.isEmpty(seller)) {
//		//
//		//		    promise.reject("需要配置PARTNER | RSA_PRIVATE| SELLER");
//		//
//		//			return;
//		//		}
//		//
//		//		String orderInfo = getOrderInfo(partner, seller, outTradeNO, subject, body, totalFee, itBPay, showURL, notifyURL);
//		//
//		//		/**
//		//		 * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
//		//		 */
//		//		String sign = sign(orderInfo, privateKey);
//		//
//		//        try {
//		//            /**
//		//             * 仅需对sign 做URL编码
//		//             */
//		//            sign = URLEncoder.encode(sign, "UTF-8");
//		//        } catch (UnsupportedEncodingException e) {
//		//            e.printStackTrace();
//		//        }
//		//
//		//        /**
//		//         * 完整的符合支付宝参数规范的订单信息
//		//         */
//		//        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
//		//
//		//        System.out.println(payInfo);
//		//
//		//		PayTask alipay = new PayTask(getCurrentActivity());
//		//		String result = alipay.pay(payInfo);
//		//		//cb.invoke(result);
//		//		promise.resolve(result);
//
//    }

  	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(
	    String partner,
	    String seller,
	    String outTradeNO,
	    String subject,
	    String body,
	    String totalFee,
	    String itBPay,
	    String showURL,
	    String notifyURL
	) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + partner + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + seller + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + outTradeNO + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + totalFee + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"" + itBPay + "\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"" + showURL + "\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		orderInfo += "&notify_url=\"" + notifyURL + "\"";

		return orderInfo;
	}

//	/**
//	 * sign the order info. 对订单信息进行签名
//	 *
//	 * @param content
//	 *            待签名订单信息
//	 */
//	public String sign(String content, String rsaPrivate) {
//		return SignUtils.sign(content, rsaPrivate);
//	}


	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}



	@ReactMethod
	public void pay(ReadableMap options) {
		Activity currentActivity = getCurrentActivity();
		//		final String orderText = params.getString("orderText");
		final Float Price = Float.valueOf(options.getString("totalFee"));
		final String Subject = options.getString("subject");
		final String Body = options.getString("body");
		final String notifyURL = options.getString("notifyURL");
		final String outTradeNO = options.getString("outTradeNO");

		boolean rsa2 = (RSA2_PRIVATE.length() > 0);
		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
		final String orderInfo = OrderInfoUtil2_0.getSignOrder(PID, SELLER, Price, Subject, Body, notifyURL, outTradeNO, privateKey, rsa2);
		payV2(currentActivity, orderInfo);
	}

	private void payV2(final Activity activity, final String orderText) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				@SuppressLint("HandlerLeak")
				final Handler mHandler = new Handler() {
					@SuppressWarnings("unused")
					public void handleMessage(Message msg) {
						switch (msg.what) {
							case SDK_PAY_FLAG: {
								@SuppressWarnings("unchecked")
//                                PayResult payResult = new PayResult((Map<String, String>) msg.obj);
								PayResult payResult = new PayResult(msg.obj.toString());
								/**
								 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
								 */
								WritableMap resultMap = setResultMap(payResult);
//                                String resultInfo = payResult.getResult();// 同步返回需要验证的信息, // 该笔订单是否真实支付成功/失败，需要依赖服务端的异步通知。
//                                String resultStatus = payResult.getResultStatus();

								mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
										.emit("alipay.mobile.securitypay.pay.onPaymentResult", resultMap);
								break;
							}
							default:
								break;
						}
					};
				};

				EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
				Runnable payRunnable = new Runnable() {

					@Override
					public void run() {
						PayTask alipay = new PayTask(activity);
//                        Map<String, String> result = alipay.payV2(orderText, true); //支付成功却返回resultStatus=6001, 这是新版方法的bug?
						String result = alipay.pay(orderText, true);    //退回使用旧版的方法, 支付成功可以正常返回resultStatus=6001, 但需要自行解析数据
//                        Log.i("result = ", result.toString());
						Message msg = new Message();
						msg.what = SDK_PAY_FLAG;
						msg.obj = result;
						mHandler.sendMessage(msg);
					}
				};

				Thread payThread = new Thread(payRunnable);

				WritableMap resultMap = Arguments.createMap();
				resultMap.putString("resultStatus", "updateSign");
				resultMap.putString("sign", OrderInfoUtil2_0.sign);
				resultMap.putString("outTradeNO", OrderInfoUtil2_0.outTradeNO);
				mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
				.emit("alipay.mobile.securitypay.pay.onPaymentResult", resultMap);

				payThread.start();

			}
		});
	}

	private WritableMap setResultMap(PayResult payResult) {
		WritableMap resultMap = Arguments.createMap();

		if (null != payResult) {
			resultMap.putInt("resultStatus", Integer.parseInt(payResult.getResultStatus()));
			resultMap.putString("result", payResult.getResult());
			resultMap.putString("memo", payResult.getMemo());
		}

		return resultMap;
	}
}