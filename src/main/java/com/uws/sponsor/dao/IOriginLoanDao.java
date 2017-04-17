package com.uws.sponsor.dao;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.OriginLoanModel;

public abstract interface IOriginLoanDao extends IBaseDao {
	/**
	 * 
	 * @Title: queryOriginLoan
	 * @Description: TODO(学院及学生处查询生源地助学贷款)
	 * @param pageNo
	 * @param pageSize
	 * @param OriginLoan
	 * @param collegeId
	 * @return
	 * @throws
	 */
	public Page queryOriginLoan(int pageNo,int pageSize, OriginLoanModel originLoan, String collegeId );
	/**
	 * 
	 * @Title: countOriginLoan
	 * @Description: 统计数据库中数据的条数
	 * @return
	 * @throws
	 */
	public long countOriginLoan(); 
	
	public Page queryOriginLoanPage(int pageNo, int pageSize, OriginLoanModel originLoan);
}
