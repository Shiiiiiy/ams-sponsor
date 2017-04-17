package com.uws.sponsor.service;

import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.NationalLoanModel;

public interface INationalLoanService extends IBaseService {
	/**
    * 
    * @Title: INationalLoanService.java 
    * @Package com.uws.sponsor.service 
    * @Description: 查询国家助学贷款学生列表
    * @author xuzh  
    * @date 2015-7-31 上午11:02:33
     */
	public Page queryNationalLoanByStudent(int pageNo, int pageSize, NationalLoanModel nationalLoan, String loginStudentId );

	
	/**
	 * 
	 * @Title: findNationalLoanById
	 * @Description: TODO(通过ID，找到NationalLoan对象)
	 * @param id
	 * @return
	 * @throws
	 */
	public NationalLoanModel findNationalLoanById(String id);
	
	 /**
     * 
    * @Title: INationalLoanService.java 
    * @Package com.uws.sponsor.service 
    * @Description: 修改国家助学贷款学生信息
    * @author xuzh 
    * @date 2015-08-04 下午3:00:28
     */
	public void updateInfos(NationalLoanModel nationalLoan, String[] fileId);
	
	/**
	 * 
	* @Title: INationalLoanService.java 
	* @Package com.uws.sponsor.service 
	* @Description: 保存困难生信息
	* @author xuzh 
	* @date 2015-7-31 下午3:40:51
	 */
	public void saveInfos(NationalLoanModel natinalLoan, String[] fileId);

	/**
	 * 
	* @Title: INationalLoanService.java 
	* @Package com.uws.sponsor.service 
	* @Description: 根据id删除国家助学贷款学生信息
	* @author xuzh 
	* @date 2015-8-5 上午9:43:22
	 */
	public void deleteNationalLoan(String id);

	/**
	 * 
	 * @Title: update
	 * @Description: (更新国家助学贷款信息)
	 * @param nationalLoan
	 * @throws
	 */
	public void update(NationalLoanModel nationalLoan);
	
	
	/**
	 * 
	 * @Title: queryNationalLoanByTeacher
	 * @Description:教师查询国家助学贷款
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
	 * @Title: queryNationalLoanApproveList
	 * @Description: 查询国家助学贷款审核页面
	 * @param pageNo
	 * @param dEFAULT_PAGE_SIZE
	 * @param nationalLoan
	 * @return
	 * @throws
	 */
	public Page queryNationalLoanApproveList(int pageNo, int pageSize, NationalLoanModel nationalLoan);

	/**
	 * 检查该生同一学年是否申请过国家助学贷款
	 * @param id
	 * @param currentStudentId
	 * @param loanYear
	 * @return
	 */
	public boolean isApply(String id, String currentStudentId, String loanYear);

}
