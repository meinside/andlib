/*
 Copyright (c) 2010, Sungjin Han <meinside@gmail.com>
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of meinside nor the names of its contributors may be
    used to endorse or promote products derived from this software without
    specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 */

package kr.pe.meinside.android.helper;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import outsourced.Base64Coder;

import android.util.Log;

/**
 * 
 * @author meinside@gmail.com
 * @since 09.11.24.
 * 
 * last update 09.12.22.
 *
 */
final public class StringCodec
{
	/**
	 * 
	 * @param original
	 * @return null if fails
	 */
	public static String urlencode(String original)
	{
		try
		{
			//return URLEncoder.encode(original, "utf-8");
			//fixed: to comply with RFC-3986
			return URLEncoder.encode(original, "utf-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
		}
		catch(UnsupportedEncodingException e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return null;
	}
	
	/**
	 * 
	 * @param encoded
	 * @return null if fails
	 */
	public static String urldecode(String encoded)
	{
		try
		{
			return URLDecoder.decode(encoded, "utf-8");
		}
		catch(UnsupportedEncodingException e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return null;
	}
	
	/**
	 * 
	 * @param original
	 * @param key
	 * @return null if fails
	 */
	public static String hmacSha1Digest(String original, String key)
	{
		return hmacSha1Digest(original.getBytes(), key.getBytes());
	}
	
	/**
	 * 
	 * @param original
	 * @param key
	 * @return null if fails
	 */
	public static String hmacSha1Digest(byte[] original, byte[] key)
	{
		try
		{
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(key, "HmacSHA1"));
			byte[] rawHmac = mac.doFinal(original);
			return new String(Base64Coder.encode(rawHmac));
		}
		catch (Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return null;
	}
	
	/**
	 * 
	 * @param original
	 * @return null if fails
	 */
	public static String md5sum(byte[] original)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(original, 0, original.length);
			StringBuffer md5sum = new StringBuffer(new BigInteger(1, md.digest()).toString(16));
			while(md5sum.length() < 32)
				md5sum.insert(0, "0");
			return md5sum.toString();
		}
		catch(NoSuchAlgorithmException e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return null;
	}
	
	/**
	 * 
	 * @param original
	 * @return null if fails
	 */
	public static String md5sum(String original)
	{
		return md5sum(original.getBytes());
	}
	
	/**
	 * AES encrypt function
	 * 
	 * @param original
	 * @param key 16, 24, 32 bytes available
	 * @param iv initial vector (16 bytes) - if null: ECB mode, otherwise: CBC mode
	 * @return
	 */
	public static byte[] aesEncrypt(byte[] original, byte[] key, byte[] iv)
	{
		if(key == null || (key.length != 16 && key.length != 24 && key.length != 32))
		{
			Log.e(LogHelper.where(), "key's bit length is not 128/192/256");
			return null;
		}
		if(iv != null && iv.length != 16)
		{
			Log.e(LogHelper.where(), "iv's bit length is not 16");
			return null;
		}

		try
		{
			SecretKeySpec keySpec = null;
			Cipher cipher = null;
			if(iv != null)
			{
				keySpec = new SecretKeySpec(key, "AES/CBC/PKCS7Padding");
				cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
				cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
			}
			else	//if(iv == null)
			{
				keySpec = new SecretKeySpec(key, "AES/ECB/PKCS7Padding");
				cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
				cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			}

			return cipher.doFinal(original);
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return null;
	}

	/**
	 * AES decrypt function
	 * 
	 * @param encrypted
	 * @param key 16, 24, 32 bytes available
	 * @param iv initial vector (16 bytes) - if null: ECB mode, otherwise: CBC mode
	 * @return
	 */
	public static byte[] aesDecrypt(byte[] encrypted, byte[] key, byte[] iv)
	{
		if(key == null || (key.length != 16 && key.length != 24 && key.length != 32))
		{
			Log.e(LogHelper.where(), "key's bit length is not 128/192/256");
			return null;
		}
		if(iv != null && iv.length != 16)
		{
			Log.e(LogHelper.where(), "iv's bit length is not 16");
			return null;
		}

		try
		{
			SecretKeySpec keySpec = null;
			Cipher cipher = null;
			if(iv != null)
			{
				keySpec = new SecretKeySpec(key, "AES/CBC/PKCS7Padding");
				cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
				cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
			}
			else	//if(iv == null)
			{
				keySpec = new SecretKeySpec(key, "AES/ECB/PKCS7Padding");
				cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
				cipher.init(Cipher.DECRYPT_MODE, keySpec);
			}

			return cipher.doFinal(encrypted);
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return null;
	}
	
	/**
	 * generates RSA key pair
	 * 
	 * @param keySize
	 * @param publicExponent public exponent value (can be RSAKeyGenParameterSpec.F0 or F4)
	 * @return
	 */
	public static KeyPair generateRsaKeyPair(int keySize, BigInteger publicExponent)
	{
		KeyPair keys = null;
		try
		{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(keySize, publicExponent);
			keyGen.initialize(spec);
			keys = keyGen.generateKeyPair();
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return keys;
	}
	
	/**
	 * generates a RSA public key with given modulus and public exponent
	 * 
	 * @param modulus (must be positive? don't know exactly)
	 * @param publicExponent
	 * @return
	 */
	public static PublicKey generateRsaPublicKey(BigInteger modulus, BigInteger publicExponent)
	{
		try
		{
			return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return null;
	}
	
	/**
	 * generates a RSA private key with given modulus and private exponent
	 * 
	 * @param modulus (must be positive? don't know exactly)
	 * @param privateExponent
	 * @return
	 */
	public static PrivateKey generateRsaPrivateKey(BigInteger modulus, BigInteger privateExponent)
	{
		try
		{
			return KeyFactory.getInstance("RSA").generatePrivate(new RSAPrivateKeySpec(modulus, privateExponent));
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return null;
	}
	
	/**
	 * RSA encrypt function (RSA / ECB / PKCS1-Padding)
	 * 
	 * @param original
	 * @param key
	 * @return
	 */
	public static byte[] rsaEncrypt(byte[] original, PublicKey key)
	{
		try
		{
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(original);
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return null;
	}
	
	/**
	 * RSA decrypt function (RSA / ECB / PKCS1-Padding)
	 * 
	 * @param encrypted
	 * @param key
	 * @return
	 */
	public static byte[] rsaDecrypt(byte[] encrypted, PrivateKey key)
	{
		try
		{
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(encrypted);
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return null;
	}

	/**
	 * converts given byte array to a hex string
	 * 
	 * @param bytes
	 * @return
	 */
	public static String byteArrayToHexString(byte[] bytes)
	{
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<bytes.length; i++)
		{
			if(((int)bytes[i] & 0xff) < 0x10)
				buffer.append("0");
			buffer.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buffer.toString();
	}
	
	/**
	 * converts given hex string to a byte array
	 * (ex: "0D0A" => {0x0D, 0x0A,})
	 * 
	 * @param str
	 * @return
	 */
	public static final byte[] hexStringToByteArray(String str)
	{
		int i = 0;
		byte[] results = new byte[str.length() / 2];
		for (int k = 0; k < str.length();)
		{
			results[i] = (byte)(Character.digit(str.charAt(k++), 16) << 4);
			results[i] += (byte)(Character.digit(str.charAt(k++), 16));
			i++;
		} 
		return results;
	}
}
