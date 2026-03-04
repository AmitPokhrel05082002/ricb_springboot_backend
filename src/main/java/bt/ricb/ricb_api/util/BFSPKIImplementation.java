/*
# These sample codes are provided for information purposes only. 
It does not imply any recommendation or endorsement by anyone.
  These sample codes are provided for FREE, and no additional support will be provided for these sample pages. 
  There is no warranty and no additional document. USE AT YOUR OWN RISK.
*/

package bt.ricb.ricb_api.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.io.ClassPathResource;

public class BFSPKIImplementation {
	static {
		Security.addProvider((Provider) new BouncyCastleProvider());
	}

	public static String signData(String pvtKeyFileName, String dataToSign, String signatureAlg) throws IOException,
			NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
		PrivateKey privateKey = getPrivateKey(pvtKeyFileName);
		Signature signature = Signature.getInstance(signatureAlg, "BC");
		signature.initSign(privateKey);

		signature.update(dataToSign.getBytes());
		byte[] signatureBytes = signature.sign();

		return byteArrayToHexString(signatureBytes);
	}

	public static boolean verifyData(String pubKeyFileName, String checkSumStr, String checkSumFromMsg,
			String signatureAlg) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
			NumberFormatException, CertificateException, IOException, ParseException {
		Signature sig = null;
		PublicKey pubKey = getBFSPublicKey(pubKeyFileName);
		sig = Signature.getInstance("SHA1withRSA");
		sig.initVerify(pubKey);
		sig.update(checkSumStr.getBytes());
		return sig.verify(HexStringToByteArray(checkSumFromMsg));
	}

	private static PublicKey getPublicKey(X509Certificate X509Cert) {
		return X509Cert.getPublicKey();
	}

	public static PrivateKey getPrivateKey(String pvtKeyFilePath) throws IOException {
		ClassPathResource resource = new ClassPathResource(pvtKeyFilePath);
		InputStreamReader reader = new InputStreamReader(resource.getInputStream());
		try {
			PEMParser pemParser = new PEMParser(reader);
			try {
				Object object = pemParser.readObject();
				if (object instanceof PEMKeyPair) {
					PEMKeyPair pemKeyPair = (PEMKeyPair) object;
					KeyPair keyPair = (new JcaPEMKeyConverter()).getKeyPair(pemKeyPair);
					PrivateKey privateKey = keyPair.getPrivate();

					pemParser.close();
					reader.close();
					return privateKey;
				}
				pemParser.close();
			} catch (Throwable throwable) {
				try {
					pemParser.close();
				} catch (Throwable throwable1) {
					throwable.addSuppressed(throwable1);
				}
				throw throwable;
			}
			reader.close();
		} catch (Throwable throwable) {
			try {
				reader.close();
			} catch (Throwable throwable1) {
				throwable.addSuppressed(throwable1);
			}
			throw throwable;
		}
		return null;
	}

	static char[] hexChar = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar[(b[i] & 0xF0) >>> 4]);
			sb.append(hexChar[b[i] & 0xF]);
		}
		return sb.toString();
	}

	public static byte[] HexStringToByteArray(String strHex) {
		byte[] bytKey = new byte[strHex.length() / 2];
		int y = 0;

		for (int x = 0; x < bytKey.length; x++) {
			String strbyte = strHex.substring(y, y + 2);
			if (strbyte.equals("FF")) {
				bytKey[x] = -1;
			} else {
				bytKey[x] = (byte) Integer.parseInt(strbyte, 16);
			}
			y += 2;
		}
		return bytKey;
	}

	private static X509Certificate getX509Certificate(String pubKeyFileName) throws CertificateException, IOException {
		InputStream inStream = new FileInputStream(pubKeyFileName);

		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

		X509Certificate cert = (X509Certificate) certFactory.generateCertificate(inStream);
		inStream.close();
		return cert;
	}

	private static PublicKey getBFSPublicKey(String file)
			throws CertificateException, IOException, NumberFormatException, ParseException {
		return getPublicKey(getX509Certificate(file));
	}
}