package com.uws.sponsor.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.sponsor.OriginLoanModel;
import com.uws.sponsor.dao.IOriginLoanDao;
import com.uws.util.ProjectConstants;

@Repository("originLoanDao")
public class OriginLoanDaoImpl extends BaseDaoImpl implements IOriginLoanDao{
	/**
	 * 描述信息: (学院以及学生处查询生源地助学贷款)
	 * @param pageNo
	 * @param pageSize
	 * @param originLoan
	 * @param currentOrgId
	 * @return
	 * @see com.uws.sponsor.dao.IOriginLoanDao#queryOriginLoan(int, int, com.uws.domain.sponsor.OriginLoanModel, java.lang.String)
	 */
	public Page queryOriginLoan(int pageNo, int pageSize, OriginLoanModel originLoan, String currentOrgId ){
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from OriginLoanModel where 1=1 ");
		if(null != originLoan){
	    	if (null != originLoan.getLoanYear() && StringUtils.isNotEmpty(originLoan.getLoanYear().getId()) ) {
		    	hql.append(" and loanYear = ? ");
		        values.add(originLoan.getLoanYear());
		    }
    
    	//生源地助学贷款表中的学生实体接受查询参数
    	StudentInfoModel studentInfo = originLoan.getStudentInfo();
    	if(studentInfo != null){
    		//获取需要查询的学院
	    	if (studentInfo.getCollege() != null && StringUtils.isNotEmpty(studentInfo.getCollege().getId())){
	    		hql.append(" and studentInfo.college.id = ? ");
		        values.add(studentInfo.getCollege().getId());
	    	}
    		//获取需要查询的专业
	    	if (studentInfo.getMajor() != null && StringUtils.isNotEmpty(studentInfo.getMajor().getId()) ){
	    		hql.append(" and studentInfo.major.id = ? ");
		        values.add(studentInfo.getMajor().getId());
	    	}
	    	//获取需要查询的班级
	    	if (studentInfo.getClassId()!= null && StringUtils.isNotEmpty(studentInfo.getClassId().getId()) ){
	    		hql.append(" and studentInfo.classId.id = ? ");
		        values.add(studentInfo.getClassId().getId());
	    	}
	    	//获取需要查询的学号
			if (StringUtils.isNotEmpty(studentInfo.getStuNumber())) 
			{
				hql.append(" and studentInfo.stuNumber like ? ");
				if (HqlEscapeUtil.IsNeedEscape(studentInfo.getStuNumber())) 
				{
					values.add("%" + HqlEscapeUtil.escape(studentInfo.getStuNumber()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				} else
					values.add("%" + studentInfo.getStuNumber() + "%");
			}
			//获取需要查询的姓名
			if (StringUtils.isNotEmpty(studentInfo.getName())) 
			{
				hql.append(" and studentInfo.name like ? ");
				if (HqlEscapeUtil.IsNeedEscape(studentInfo.getName())) 
				{
					values.add("%" + HqlEscapeUtil.escape(studentInfo.getName()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				} else
					values.add("%" + studentInfo.getName() + "%");
			}
    	}
   
		//各个学院单独查询
    	if (!StringUtils.isEmpty(currentOrgId) && !ProjectConstants.STUDNET_OFFICE_ORG_ID.equals(currentOrgId))
    	{
        	hql.append(" and studentInfo.college.id = ? ");
	        values.add(currentOrgId);
        }
    }
    //排序条件
    hql.append(" order by studentInfo.stuNumber asc ");
    
    if (values.size() == 0)
        return this.pagedQuery(hql.toString(), pageNo, pageSize);
    else
        return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}

	public long countOriginLoan() {
		String hql = "select count(*) from OriginLoanModel where 1 = 1";
		return this.queryCount(hql, new Object[]{});
	}

	/**
	 * 描述信息: (生源地助学贷款查询)
	 * @param pageNo
	 * @param pageSize
	 * @param originLoan
	 * @return
	 * @see com.uws.sponsor.dao.IOriginLoanDao#queryOriginLoanPage(int, int, com.uws.domain.sponsor.OriginLoanModel)
	 */
	public Page queryOriginLoanPage(int pageNo, int pageSize, OriginLoanModel originLoan) {
		StringBuffer hql = new StringBuffer(" from OriginLoanModel where 1=1 ");
		return this.pagedQuery(hql.toString(), pageNo, pageSize);
	}
}


