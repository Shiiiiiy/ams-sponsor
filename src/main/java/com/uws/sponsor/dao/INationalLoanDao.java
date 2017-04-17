package com.uws.sponsor.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.NationalLoanModel;

public interface INationalLoanDao extends IBaseDao {

	/**
	 * 
	 * @Title: queryNationalLoanByTeacher
	 * @Description:教师查询国家助学贷款列表
	 * @param pageNo
	 * @param pageSize
	 * @param student
	 * @param nationalLoan
	 * @param currentOrgId
	 * @return
	 * @throws
	 */
	public Page queryNationalLoanByTeacher(Integer pageNo, Integer pageSize, NationalLoanModel nationalLoan, String currentOrgId);
	
	/**
	 * 
	 * @Title: queryNationalLoanByStudent
	 * @Description: (学生查询国家助学贷款)
	 * @param pageNo
	 * @param pageSize
	 * @param nationalLoan
	 * @return
	 * @throws
	 */
	Page queryNationalLoanByStudent(int pageNo, int pageSize, NationalLoanModel nationalLoan, String loginStudentId);
	
	/**
	 * 
	 * @Title: queryNationalLoanApproveList
	 * @Description: 学生处国家助学贷款审核页面
	 * @param pageNo
	 * @param pageSize
	 * @param nationalLoan
	 * @return
	 * @throws
	 */
	public Page queryNationalLoanApproveList(int pageNo, int pageSize, NationalLoanModel nationalLoan);
	
	/**
	 * 查询当前学生的本学年的申请列表
	 * @param currentStudentId
	 * @param loanYear
	 * @return
	 */
	public List<NationalLoanModel> applyList(String currentStudentId, String loanYear);

}
