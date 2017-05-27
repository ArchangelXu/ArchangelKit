package studio.archangel.toolkit3.utils.security;

import java.security.MessageDigest;

/**
 * Created by xmk on 16/5/16.
 */
public class MD5 {
	/**
	 * MD5数字签名
	 *
	 * @param src 待加密字符串
	 * @return
	 */
	public static String md5(String src) {
		// 定义数字签名方法, 可用：MD5, SHA-1
		MessageDigest md = null;
		byte[] b = null;
		try {
			md = MessageDigest.getInstance("MD5");
			b = md.digest(src.getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return byte2HexStr(b);
	}

	/**
	 * BASE64编码
	 * @param src
	 * @return
	 * @throws Exception
	 */

	/**
	 * 字节数组转化为大写16进制字符串
	 *
	 * @param b
	 * @return
	 */
	private static String byte2HexStr(byte[] b) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			String s = Integer.toHexString(b[i] & 0xFF);
			if (s.length() == 1) {
				sb.append("0");
			}
			sb.append(s.toUpperCase());
		}
		return sb.toString();
	}

}
