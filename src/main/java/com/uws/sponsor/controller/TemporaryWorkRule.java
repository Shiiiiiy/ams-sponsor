package com.uws.sponsor.controller;

import java.util.Map;

import com.uws.common.service.IStudentCommonService;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.rule.IRule;
import com.uws.core.excel.vo.ExcelColumn;
import com.uws.core.excel.vo.ExcelData;
import com.uws.core.util.DataUtil;
import com.uws.core.util.SpringBeanLocator;
import com.uws.domain.orientation.StudentInfoModel;

/**
 * @className TemporaryWorkRule.java
 * @package com.uws.sponsor.controller
 * @description 用于Excel导入
 * @author lizj
 * @date 2015-9-15  上午9:46:03
 */
public class TemporaryWorkRule implements IRule {

	@Override
	public void format(ExcelData data, ExcelColumn column, Map initData) {
		
	}

	@Override
	public void operation(ExcelData data, ExcelColumn column, Map initData, Map<String, ExcelData> eds, int site){
		
	}
	/**
	 * 验证导入的学生是否存在
	 */
	@Override
	public void validate(ExcelData data, ExcelColumn column, Map initData) throws ExcelException {
		if(DataUtil.isEquals("学号", column.getName())){
			IStudentCommonService studentCommonService = (IStudentCommonService)SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
			String stuNo = data.getValue().toString();
			StudentInfoModel studentInfoModel = studentCommonService.queryStudentByStudentNo(stuNo);
			if(!DataUtil.isNotNull(studentInfoModel)){
				String isText = data.getId().replaceAll("\\$", "");
				throw new ExcelException(isText + "单元格值("+ data.getValue().toString() + ")与在系统中没有找到匹配的学号，请修正后重新上传；<br/>");
			}
		}
	}
}
