package com.leslie.rn.alipay;

import java.util.Map;

import android.text.TextUtils;

public class PayResult {
	private String resultStatus;
	private String result;
	private String memo;

//	public PayResult(Map<String, String> rawResult) {
	//		if (rawResult == null) {
	//			return;
	//		}
	//
	//		for (String key : rawResult.keySet()) {
	//			if (TextUtils.equals(key, "resultStatus")) {
	//				resultStatus = rawResult.get(key);
	//			} else if (TextUtils.equals(key, "result")) {
	//				result = rawResult.get(key);
	//			} else if (TextUtils.equals(key, "memo")) {
	//				memo = rawResult.get(key);
	//			}
	//		}
//	}

	public PayResult(String rawResult) {

		if (TextUtils.isEmpty(rawResult))
			return;

		String[] resultParams = rawResult.split("\\};");	//由于result中的返回数据有大机率包含";", 故使用"};"来代替
		for (String resultParam : resultParams) {
//			Log.i("resultParam = ", resultParam);
			if (resultParam.startsWith("resultStatus")) {
				resultStatus = getValue(resultParam + "}", "resultStatus");
			}
			else if (resultParam.startsWith("memo")) {
				memo = getValue(resultParam + "}", "memo");
			}
			else if (resultParam.startsWith("result")) {
				result = getValue(resultParam, "result");
			}
		}
	}

	private String getValue(String content, String key) {
		String prefix = key + "={";
		return content.substring(content.indexOf(prefix) + prefix.length(),
				content.lastIndexOf("}"));
	}

	@Override
	public String toString() {
		return "resultStatus={" + resultStatus + "};memo={" + memo
				+ "};result={" + result + "}";
	}

//	@Override
	//	public String toString() {
	//		String r = "";
	//		//result = "partner=\"2088821934190852\"&seller_id=\"cs@eascotel.com\"&out_trade_no=\"0503052907-9518\"&subject=\"US Data Package (500M)\"&body=\"0.1\"&total_fee=\"0.1\"&notify_url=\"http://www.xxx.com\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&return_url=\"m.alipay.com\"&payment_inst=\"ALIPAYHK\"&currency=\"HKD\"&product_code=\"NEW_WAP_OVERSEAS_SELLER\"&forex_biz=\"FP\"&secondary_merchant_id=\"A80001\"&secondary_merchant_name=\"Muku\"&secondary_merchant_industry=\"7011\"&success=\"true\"&sign_type=\"RSA\"&sign=\"Ho6M9c9UBv/OGu4N/TEst64mVAffl8Cz2mB8a5SLEF74LLTrBczNzMqVp0bFHVl7xKHkfVlstnw3V/gCF8AUpfNfonQik7j0rOPfizFXzLIdyvfpI93QOY+3iTw+CaJOSFQRolaAgKBQjsRY91LtXDV8zdoIztax8aCTyovw0Fw=\"";
	//
	//		if(result.equals(null) || result.equals("")){
	//			r = "";
	//		}else{
	//			result = result.substring (0, result.indexOf("sign="));
	//			r = result.replaceAll("=\"", "\":\"").replaceAll("\"&", "\",\"");
	//		}
	//		return "{\"resultStatus\":\"" + resultStatus + "\",\"memo\":\"" + memo
	//				+ "\",\"result\":{\"" + r + "\"}}";
//	}

	/**
	 * @return the resultStatus
	 */
	public String getResultStatus() {
		return resultStatus;
	}

	/**
	 * @return the memo
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}
}
