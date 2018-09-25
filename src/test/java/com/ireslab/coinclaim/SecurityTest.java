package com.ireslab.coinclaim;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class SecurityTest {

	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			IOException, UnrecoverableKeyException, NoSuchPaddingException, InvalidKeyException {

		String password = "co!ncl@im";

		KeyStore keyStore = KeyStore.getInstance("JCEKS");

		FileInputStream fileInputStream = new FileInputStream(new File(
				"F:\\IresLab\\Ammbr\\Coin Claim\\SourceCode_github\\ammbr-coinclaim\\master\\testing\\cckeystore.JCEKS"));

		keyStore.load(fileInputStream, password.toCharArray());

		Key key = keyStore.getKey("ccsecretkey", password.toCharArray());
		System.out.println(key.getAlgorithm() + " " + key.getFormat());

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		
		//System.out.println(cipher.doFinal("NitinMalik"));
	}
}
