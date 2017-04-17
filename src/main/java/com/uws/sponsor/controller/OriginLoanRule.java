package com.uws.sponsor.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.uws.common.service.IStudentCommonService;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.rule.IRule;
import com.uws.core.excel.vo.ExcelColumn;
import com.uws.core.excel.vo.ExcelData;
import com.uws.core.util.SpringBeanLocator;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

/**
 * 
* @ClassName: Oricom.uws.sponsor.controller.OriginLoanRule
ginLoanRule 
* @Description: (对生源地助学贷款信息导入的验证) 
* @author xuzh
* @date 2015-8-27 下午6:28:48 
*
 */
public class OriginLoanRule implements IRule {
	
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	
	private String getString(int site, Map<String, ExcelData> eds, String key) {
		String s = "";
		String keyName = "$" + key + "$" + site;
		if ((eds.get(keyName) != null) && (((ExcelData) eds.get(keyName)).getValue() != null)) {
			s = s + (String) ((ExcelData) eds.get(keyName)).getValue();
		}
		return s.trim();
	}

	@Override
	public void format(ExcelData arg0, ExcelColumn arg1, Map arg2) {

	}

	@Override
	public void operation(ExcelData data, ExcelColumn column, Map arg2,
			Map<String, ExcelData> eds, int site) {
		
		if ("loanYear".equals(column.getName())) {
			List<Dic> dicYear = this.dicUtil.getDicInfoList("YEAR");
			String yearValue = getString(site, eds, "C");
			for (Dic dic : dicYear){
				if (yearValue.equals(dic.getCode()) ) {
					data.setValue(dic);
					break;
				}
			}
		}

	}

	/**
	 * 描述信息: (学号校验，查看系统中是否有该学生)
	 * @param data
	 * @param column
	 * @param arg2
	 * @throws ExcelException
	 * @see com.uws.core.excel.rule.IRule#validate(com.uws.core.excel.vo.ExcelData, com.uws.core.excel.vo.ExcelColumn, java.util.Map)
	 */
	public void validate(ExcelData data, ExcelColumn column, Map map)
			throws ExcelException {
		boolean flag = false;
		IStudentCommonService studentCommonService = (IStudentCommonService)SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
		if ("学号".equals(column.getName())) {
			String number = data.getValue().toString();
			StudentInfoModel studentInfo = studentCommonService.queryStudentByStudentNo(number);
			if (studentInfo == null) {
				String isText = data.getId().replaceAll("\\$", "");
				throw new ExcelException(isText + "单元格值("
						+ data.getValue().toString()
						+ ")与在系统中没有找到匹配的学号，请修正后重新上传；<br/>");
			}
		}

		if ("贷款学年".equals(column.getName())) {
			String yearValue = data.getValue().toString();
			List<Dic> dicYear = this.dicUtil.getDicInfoList("YEAR");
			for (Dic dic : dicYear){
				if (yearValue.equals(dic.getCode())){
					flag = true;
					break;
				}
			}
			if( !flag){
				String isText = data.getId().replaceAll("\\$", "");
				throw new ExcelException(isText + "单元格属性值(" + data.getValue().toString() + ")与在系统中没有找到匹配的贷款学年，请修正后重新上传；<br/>");
			}
		}
	}

}
