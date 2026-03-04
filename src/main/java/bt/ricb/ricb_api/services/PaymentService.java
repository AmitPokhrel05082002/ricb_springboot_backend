package bt.ricb.ricb_api.services;

import bt.ricb.ricb_api.util.BFSPKIImplementation;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
	public String paymentrequest(String messagetype, String amount, String email) throws Exception {
		System.out.println("Msg Type: " + messagetype);
		System.out.println("Amount: " + amount);
		System.out.println("Email: " + email);

		StringBuffer result = new StringBuffer();
		String resultRc = "";
		JSONArray json = new JSONArray();
		JSONObject jsonObject = new JSONObject();

		String bfs_msgType = messagetype;
		String bfs_txnAmount = amount;
		String bfs_remitterEmail = email;

		try {
			String bfs_benfTxnTime = (new SimpleDateFormat("yyyyMMddhhmmss")).format(new Date());
			String bfs_orderNo = genOrderNo();
			String bfs_benfId = "BE10000071";
			String bfs_benfBankCode = "01";
			String bfs_txnCurrency = "BTN";
			String bfs_paymentDesc = "Insurance Fee";
			String bfs_version = "1.0";
			String bfs_checkSum = bfs_benfBankCode + "|" + bfs_benfId + "|" + bfs_benfTxnTime + "|" + bfs_msgType + "|"
					+ bfs_orderNo + "|" + bfs_paymentDesc + "|" + bfs_remitterEmail + "|" + bfs_txnAmount + "|"
					+ bfs_txnCurrency + "|" + bfs_version;

			String finalCheckSum = BFSPKIImplementation.signData("pki/BE10000071.key", bfs_checkSum, "SHA1withRSA");
			System.out.println("final check sum: " + finalCheckSum);

			HttpPost httppost = new HttpPost("https://bfssecure.rma.org.bt/BFSSecure/nvpapi?");
			httppost.setHeader("Content-type", "application/x-www-form-urlencoded; charset-utf-8");

			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("bfs_msgType", bfs_msgType));
			params.add(new BasicNameValuePair("bfs_benfTxnTime", bfs_benfTxnTime));
			params.add(new BasicNameValuePair("bfs_orderNo", bfs_orderNo));
			params.add(new BasicNameValuePair("bfs_benfId", bfs_benfId));
			params.add(new BasicNameValuePair("bfs_benfBankCode", bfs_benfBankCode));
			params.add(new BasicNameValuePair("bfs_txnCurrency", bfs_txnCurrency));
			params.add(new BasicNameValuePair("bfs_txnAmount", bfs_txnAmount));
			params.add(new BasicNameValuePair("bfs_remitterEmail", bfs_remitterEmail));
			params.add(new BasicNameValuePair("bfs_paymentDesc", bfs_paymentDesc));
			params.add(new BasicNameValuePair("bfs_version", bfs_version));
			params.add(new BasicNameValuePair("bfs_checkSum", finalCheckSum));
			httppost.setEntity((HttpEntity) new UrlEncodedFormEntity(params));

			CloseableHttpClient client = HttpClients.createDefault();
			CloseableHttpResponse closeableHttpResponse = client.execute((HttpUriRequest) httppost);

			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			System.out.println("Status Code: " + statusCode);

			if (statusCode == 200) {
				System.out.println("Response: " + closeableHttpResponse.getEntity().getContent());

				BufferedReader rd = new BufferedReader(
						new InputStreamReader(closeableHttpResponse.getEntity().getContent()));
				String line = "";

				while ((line = rd.readLine()) != null) {
					result.append(line);
				}

				resultRc = URLDecoder.decode(result.toString(), "UTF-8");
				jsonObject.put("arresponse", resultRc);
				json.put(jsonObject);
				resultRc = json.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultRc;
	}

	private static String genOrderNo() {
		String timeStamp = (new SimpleDateFormat("yyyyMMddhhmmss")).format(new Date());
		Random rand = new Random();
		int num = rand.nextInt(999999999);
		String finalStr = null;
		StringBuffer sbf = new StringBuffer();
		sbf.append(num);
		sbf.append(timeStamp);
		finalStr = sbf.toString();
		return finalStr;
	}

	public String returnVerificationResponse(String messagetype, String benf_TxnId, String bfs_remitterBankId,
			String bfs_remitterAccNo) throws Exception {
		String returnString = null;

		StringBuffer result = new StringBuffer();
		String resultRc = "";
		JSONArray json = new JSONArray();
		JSONObject jsonObject = new JSONObject();

		String bfs_msgType = messagetype;
		String bfs_benfTxnId = benf_TxnId;

		try {
			String bfs_benfId = "BE10000071";
			String bfs_checkSum = bfs_benfId + "|" + bfs_benfTxnId + "|" + bfs_msgType + "|" + bfs_remitterAccNo + "|"
					+ bfs_remitterBankId;

			System.out.println("checksum=" + bfs_checkSum);
			String finalCheckSum = BFSPKIImplementation.signData("pki/BE10000071.key", bfs_checkSum, "SHA1withRSA");

			HttpPost httppost = new HttpPost("https://bfssecure.rma.org.bt/BFSSecure/nvpapi?");
			httppost.setHeader("Content-type", "application/x-www-form-urlencoded; charset-utf-8");

			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("bfs_msgType", bfs_msgType));
			params.add(new BasicNameValuePair("bfs_bfsTxnId", bfs_benfTxnId));
			params.add(new BasicNameValuePair("bfs_benfId", bfs_benfId));
			params.add(new BasicNameValuePair("bfs_remitterBankId", bfs_remitterBankId));
			params.add(new BasicNameValuePair("bfs_remitterAccNo", bfs_remitterAccNo));
			params.add(new BasicNameValuePair("bfs_checkSum", finalCheckSum));
			httppost.setEntity((HttpEntity) new UrlEncodedFormEntity(params));

			CloseableHttpClient client = HttpClients.createDefault();
			CloseableHttpResponse closeableHttpResponse = client.execute((HttpUriRequest) httppost);

			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			System.out.println("statusCode =========> " + statusCode);

			if (statusCode == 200) {
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(closeableHttpResponse.getEntity().getContent()));
				String line = "";

				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				System.out.println("result is ---->>>>>>>>>>>>" + result);

				resultRc = URLDecoder.decode(result.toString(), "UTF-8");
				jsonObject.put("aeresponse", resultRc);
				json.put(jsonObject);

				resultRc = json.toString();
			}
		} catch (Exception e) {
			returnString = "###Error at PaymentAction[openPayment]: " + e;
			return returnString;
		}
		return resultRc;
	}

	public String returnPaymentResponse(String messagetype, String benf_TxnId, String bfs_remitterOtp)
			throws Exception {
		System.out.println("Message type: " + messagetype);
		System.out.println("Transaction Id: " + benf_TxnId);
		System.out.println("OTP: " + bfs_remitterOtp);

		String returnString = null;

		StringBuffer result = new StringBuffer();
		String resultRc = "";

		JSONArray jsonArray = new JSONArray();

		try {
			String bfs_msgType = messagetype;
			String bfs_benfTxnId = benf_TxnId;

			String bfs_benfId = "BE10000071";
			String bfs_checkSum = bfs_benfId + "|" + bfs_benfTxnId + "|" + bfs_msgType + "|" + bfs_remitterOtp;
			System.out.println("checksum=" + bfs_checkSum);
			String finalCheckSum = BFSPKIImplementation.signData("pki/BE10000071.key", bfs_checkSum, "SHA1withRSA");

			HttpPost httppost = new HttpPost("https://bfssecure.rma.org.bt/BFSSecure/nvpapi?");
			httppost.setHeader("Content-type", "application/x-www-form-urlencoded; charset-utf-8");

			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("bfs_msgType", bfs_msgType));
			params.add(new BasicNameValuePair("bfs_bfsTxnId", bfs_benfTxnId));
			params.add(new BasicNameValuePair("bfs_benfId", bfs_benfId));
			params.add(new BasicNameValuePair("bfs_remitterOtp", bfs_remitterOtp));

			params.add(new BasicNameValuePair("bfs_checkSum", finalCheckSum));
			httppost.setEntity((HttpEntity) new UrlEncodedFormEntity(params));

			CloseableHttpClient client = HttpClients.createDefault();
			CloseableHttpResponse closeableHttpResponse = client.execute((HttpUriRequest) httppost);

			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			System.out.println("Status Code: " + statusCode);

			if (statusCode == 200) {
				System.out.println("Response: " + closeableHttpResponse.getEntity().getContent());
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(closeableHttpResponse.getEntity().getContent()));
				String line = "";

				while ((line = rd.readLine()) != null) {
					result.append(line);
				}

				resultRc = URLDecoder.decode(result.toString(), "UTF-8");
				jsonArray.put(resultRc);
				resultRc = jsonArray.toString();
				System.out.println("UABT respomse=" + resultRc);
			}

		} catch (Exception e) {

			returnString = "###Error at PaymentAction[openPayment]: " + e;
			return returnString;
		}
		return resultRc;
	}
}