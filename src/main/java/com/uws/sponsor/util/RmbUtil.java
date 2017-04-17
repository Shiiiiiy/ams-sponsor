package com.uws.sponsor.util;

/**
 * Description: 人民币大小写转换:需要输入(String)0.00格式的RMB <br>
 * 通用人民币大小写转换解决方案
 */
public class RmbUtil {

	private final static String[] a_strNumber = { "零", "壹", "贰", "叁", "肆", "伍",
			"陆", "柒", "捌", "玖" };

	private final static String[] a_strModify = { "", "拾", "佰", "仟", "万", "拾",
			"佰", "仟", "亿", "拾", "佰", "仟" };

	private final static String strSign = "负"; // 实际上”+“号永远都不可能出现.不过"-"也没什么意义

	private final static String strDot = "元";

	/**
	 * 功能: 提取符号位. <br>
	 * 说明: 如果输入参数是 "-13.3",调用该函数的返回值是 "负"; <br>
	 * 如果输入参数是 "13.3", 调用该函数的返回值是 ""(空值).
	 * 
	 * @param pValue
	 *            输入值
	 * @return String 负或""
	 */
	static private String getSign(String pValue) {
		return pValue.indexOf("-") == 0 ? strSign : "";
	}

	/**
	 * 功能: 返回分割符号 <br>
	 * 如果参数是"12.3" 调用该函数的返回值是"元" <br>
	 * 如果参数是"12" 调用该函数的返回值是""(空值) <br>
	 * 
	 * @param pValue
	 * @return
	 */
	static private String getDot(String pValue) {
		return pValue.indexOf(".") != -1 ? strDot : "";
	}

	/**
	 * 功能:返回小数部分的汉字(只要两位[角分]) <br>
	 * 说明:如果输入数据是 12.35,调用该函数返回值是 叁角伍分
	 * 
	 * @param pValue
	 *            输入值
	 * @return String 小数部分汉字
	 */
	static private String getFraction(String pValue) {
		String strFraction = null; // 用来保存小数部分的数字串
		int intDotPos = pValue.indexOf(".");
		if (intDotPos == -1) // 没有小数部分.
			return "元整";
		strFraction = pValue.substring(intDotPos + 1);
		if ("00".equals(strFraction))
			return "整"; // 当整数RMB时

		if ("0".equals(strFraction)) // 当小数部分为.0时
			return "整";

		StringBuffer sbResult = new StringBuffer(strFraction.length()); // 开始翻译.
		int maxlength = strFraction.length() > 2 ? 2 : strFraction.length();
		// 已经两位,可以省略这步
		for (int i = 0; i < maxlength; i++) {
			if (i == 0
					&& !("零".equals(a_strNumber[strFraction.charAt(i) - 48]))) { // 1角

				sbResult.append(a_strNumber[strFraction.charAt(i) - 48]);
				if (maxlength == 1) {
					sbResult.append("角整");
				} else {
					sbResult.append("角");
				}
			} else if (i == 0
					&& ("零".equals(a_strNumber[strFraction.charAt(i) - 48]))) {
				// 零(角)
				sbResult.append(a_strNumber[strFraction.charAt(i) - 48]);
			} else if (i == 1
					&& ("零".equals(a_strNumber[strFraction.charAt(i) - 48]))) {
				// --
				sbResult.append("整");
			} else if (i == 1
					&& !("零".equals(a_strNumber[strFraction.charAt(i) - 48]))) {
				// 1分

				sbResult.append(a_strNumber[strFraction.charAt(i) - 48]);
				sbResult.append("分");
			}
		}
		return sbResult.toString();
	}

	/**
	 * 功能:用给定字符串pDest替换字符串pValue中的pSource. <br>
	 * 例:pValue= xy , pSource =x , pDest = 测试 <br>
	 * 调用改函数后 pValue = 测试y <br>
	 * 说明: 如果 pvalue= xxx pSource = xx 处理结果是 x <br>
	 * 这个结果可能与您平时看到的替换函数有点不一样，通常应该是 pSource =xx.
	 * 
	 * @param pValue
	 * @param pSource
	 * @param pDest
	 * @return 经过替换处理的字符串
	 */
	static private void replace(StringBuffer pValue, String pSource,
			String pDest) {
		if (pValue == null || pSource == null || pDest == null)
			return;
		int intPos = 0; // 记录pSource在pValue中的位置
		do {
			intPos = pValue.toString().indexOf(pSource);
			if (intPos == -1) // 没有找到pSource.
				break;
			pValue.delete(intPos, intPos + pSource.length());
			pValue.insert(intPos, pDest);
		} while (true);
	}

	/**
	 * 功能: 返回整数部分的汉字. 如果输入参数是: 234.3,调用该函数的返回值是 贰佰叁拾肆.
	 * 
	 * @param pValue
	 * @return String
	 */
	static private String getInteger(String pValue) {
		String strInteger = null; // 用来保存整数部分数字串

		int intDotPos = pValue.indexOf("."); // 记录"."所在位置

		int intSignPos = pValue.indexOf("-");
		if (intDotPos == -1)
			intDotPos = pValue.length();
		strInteger = pValue.substring(intSignPos + 1, intDotPos); // 取出整数部分
		// 反转整数部分数据
		strInteger = new StringBuffer(strInteger).reverse().toString();
		// -----------------------------------------------------------
		// 开始翻译:
		StringBuffer sbResult = new StringBuffer();
		for (int i = 0; i < strInteger.length(); i++) {
			sbResult.append(a_strModify[i]);
			sbResult.append(a_strNumber[strInteger.charAt(i) - 48]);
		}
		//反转
		sbResult = sbResult.reverse();
		// 这个时候得到的结果不标准，需要调整.
		// 203返回值是 贰佰零拾三个 正确答案是 贰佰零三
		// ----------------------------------------------------------
		// 串调整.
		replace(sbResult, "零拾", "零");
		replace(sbResult, "零佰", "零");
		replace(sbResult, "零仟", "零");
		replace(sbResult, "零万", "万");
		replace(sbResult, "零亿", "亿");
		// 多个”零“调整处理

		replace(sbResult, "零零", "零");
		replace(sbResult, "零零零", "零");
		replace(sbResult, "零零零零万", ""); // 这两句不能颠倒顺序

		replace(sbResult, "零零零零", "");
		replace(sbResult, "壹拾亿", "拾亿"); // 这样读起来更习惯.
		replace(sbResult, "壹拾万", "拾万");
		// --------------------------------------------------------------
		if ("零".equals(sbResult.substring(sbResult.length() - 1))
				&& sbResult.length() != 1) {
			sbResult.deleteCharAt(sbResult.length() - 1);
		}
		if (strInteger.length() == 2) {
			replace(sbResult, "壹拾", "拾");
		}
		return sbResult.toString();
	}

	/**
	 * 功能:主方法---数字到汉字
	 * 
	 * @param pValue
	 *            (String)0.00格式的RMB
	 * @return String
	 */
	static public String NumberToChinese(String pValue) {
		if ("0.00".equals(pValue) || "0.0".equals(pValue) || "0".equals(pValue)) // filter简单情况

			return "零元整";
		StringBuffer sbResult = new StringBuffer(getSign(pValue)
				+ getInteger(pValue) + getDot(pValue) + getFraction(pValue));
		replace(sbResult, "零元", ""); // 0.11=壹角壹分
		return sbResult.toString();
	}

	/**
	 * 这样的处理没有什么实际意义,所以就不写了.
	 * 
	 * @param pValue
	 * @return String
	 */
	public String ChineseToNumber(String pValue) {
		return null;
	}

	public static void main(String[] args) {
		// 单元测试:
		System.err.println("10.21");
		System.err.println(NumberToChinese("10.21"));

	}
}
