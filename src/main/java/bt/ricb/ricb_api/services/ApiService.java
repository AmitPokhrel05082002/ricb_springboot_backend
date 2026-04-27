package bt.ricb.ricb_api.services;

import bt.ricb.ricb_api.models.AddressDto;
import bt.ricb.ricb_api.models.BankDetailsDto;
import bt.ricb.ricb_api.models.CcdbCustomerDto;
import bt.ricb.ricb_api.models.FamilyRelationDto;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.xml.sax.SAXException;

@Service
public class ApiService {
    @Autowired
    RestTemplate restTemplate;
    @Value("${api.bearer.token}")
    private String bearerToken;

    public String getcustomerdetails(String cid) throws Exception {
        String apiUrl = "http://ccdb.ricb.bt/api/getcustomerdetails/" + cid;

        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            HttpGet request = new HttpGet(apiUrl);

            request.setHeader("Authorization", "Bearer " + this.bearerToken);

            CloseableHttpResponse response = httpClient.execute((HttpUriRequest) request);

            try {
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);

                String str1 = responseBody;
                if (response != null)
                    response.close();
                if (httpClient != null)
                    httpClient.close();
                return str1;
            } catch (Throwable throwable) {
                if (response != null)
                    try {
                        response.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                throw throwable;
            }
        } catch (Throwable throwable) {
            if (httpClient != null)
                try {
                    httpClient.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
            throw throwable;
        }
    }

    public ResponseEntity<String> sendSms(String smsContent, String mobileNo) {
        String apiUrl = "http://smsgw.ricb.bt/api/gateway.aspx";
        String username = "ricb";
        String passphrase = "12345678";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("action", new Object[]{"send"}).queryParam("username", new Object[]{username})
                .queryParam("passphrase", new Object[]{passphrase})
                .queryParam("message", new Object[]{smsContent}).queryParam("phone", new Object[]{mobileNo});

        return this.restTemplate.getForEntity(builder.toUriString(), String.class, new Object[0]);
    }

    public ResponseEntity<String> sendSmsTcell(String smsContent, String mobileNo) {
        String apiUrl = "http://smsgw.ricb.bt/api/gateway.aspx";
        String username = "admin";
        String passphrase = "123456";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("action", new Object[]{"send"}).queryParam("username", new Object[]{username})
                .queryParam("passphrase", new Object[]{passphrase})
                .queryParam("message", new Object[]{smsContent}).queryParam("phone", new Object[]{mobileNo});

        return this.restTemplate.getForEntity(builder.toUriString(), String.class, new Object[0]);
    }


    public String getCreditAccount(String cidNo)
            throws ParserConfigurationException, SAXException, IOException, JSONException {
        String apiUrl = "http://192.168.0.205/EFIMORICBCMS/ServiceIntegration/eFIMOIntegration.asmx/GetLoansData_JSON";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl).queryParam("customerid",
                new Object[]{cidNo});
        ResponseEntity<String> accountData = this.restTemplate.getForEntity(builder.toUriString(), String.class,
                new Object[0]);
        String data = (String) accountData.getBody();
        JSONObject jsonObject = XML.toJSONObject(data);
        String pageName = jsonObject.getJSONObject("string").getString("content");
        return pageName;
    }


    public String createCustomer(CcdbCustomerDto customer) throws Exception {

        String apiUrl = "http://ccdb.ricb.bt/api/create_customer_mobile";

        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(customer);

        System.out.println("REQUEST JSON = " + jsonBody); // 🔥 debug

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpPost request = new HttpPost(apiUrl);

            request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));

            request.setHeader("Authorization", "Bearer " + this.bearerToken);
            request.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            }
        }
    }

    private String formatFamilyDetails(List<FamilyRelationDto> families) {
        StringBuilder formatted = new StringBuilder();
        for (FamilyRelationDto family : families) {
            formatted.append("{").append("name:\"").append(family.getName()).append("\",").append("relationship_id:\"")
                    .append(family.getRelationship_id()).append("\",").append("nationality_id:\"")
                    .append(family.getNationality_id()).append("\",").append("cid_no:\"").append(family.getCid_no())
                    .append("\",").append("contact_no:\"").append(family.getContact_no()).append("\"").append("},");
        }
        if (formatted.length() > 0) {
            formatted.setLength(formatted.length() - 1);
        }
        return formatted.toString();
    }

    private String formatBankDetails(List<BankDetailsDto> bankDetailsList) {
        StringBuilder formatted = new StringBuilder();

        for (BankDetailsDto bankDetail : bankDetailsList) {
            formatted.append("{").append("bank_id:\"").append(bankDetail.getBank_id()).append("\",")
                    .append("bank_name:\"").append(bankDetail.getBank_name()).append("\",").append("account_no:\"")
                    .append(bankDetail.getAccount_no()).append("\",").append("account_type_id:\"")
                    .append(bankDetail.getAccount_type_id()).append("\"").append("},");
        }

        if (formatted.length() > 0) {
            formatted.setLength(formatted.length() - 1);
        }

        return formatted.toString();
    }

    private String formatAddressDetails(List<AddressDto> addressList) {

        if (addressList == null || addressList.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < addressList.size(); i++) {

            AddressDto addressDetails = addressList.get(i);

            sb.append("{")
                    .append("address_type_id:\"").append(addressDetails.getAddress_type_id()).append("\",")
                    .append("mailing_address:\"").append(addressDetails.getMailing_address()).append("\",")
                    .append("country_id:\"").append(addressDetails.getCountry_id()).append("\",")
                    .append("dzongkhag_id:\"").append(addressDetails.getDzongkhag_id()).append("\",")
                    .append("dungkhag_id:\"").append(addressDetails.getDungkhag_id()).append("\",")
                    .append("gewog_id:\"").append(addressDetails.getGewog_id()).append("\",")
                    .append("village_id:\"").append(addressDetails.getVillage_id()).append("\"")
                    .append("}");

            if (i < addressList.size() - 1) {
                sb.append(",");
            }
        }

        sb.append("]");

        return sb.toString();
    }
}