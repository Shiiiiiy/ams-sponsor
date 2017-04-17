package com.uws.sponsor.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.sponsor.NationalLoanModel;
import com.uws.sponsor.dao.INationalLoanDao;
import com.uws.sponsor.util.Constants;
import com.uws.util.ProjectConstants;
/**
 * 
* @ClassName: NationalLoanDaoImpl 
* @Description: (国家助学贷款实现类) 
* @author xuzh
* @date 2015-8-13 下午2:42:15 
*
 */
@Repository("nationalLoanDao")
public class NationalLoanDaoImpl extends BaseDaoImpl implements INationalLoanDao {

	/**
	 * 描述信息: (学生查询自己的国家助学贷款信息)
	 * @param pageNo
	 * @param DEFAULT_PAGE_SIZE
	 * @param nationalLoan
	 * @return
	 * @see com.uws.sponsor.dao.INationalLoanDao#approveNationalLoanList(int, int, com.uws.domain.sponsor.NationalLoanModel)
	 */
	@Override
	public Page queryNationalLoanByStudent(int pageNo, int pageSize, NationalLoanModel nationalLoan, String loginStudentId) {
		List<Object> values = new ArrayList<Object>();
	    StringBuffer hql = new StringBuffer("from NationalLoanModel where 1=1 and studentInfo.id = ?");
	    //只查询当前学生的信息
	    values.add(loginStudentId);
	    
	    if (nationalLoan != null ){
	    	if(nationalLoan.getLoanYear() !=null  && StringUtils.isNotEmpty(nationalLoan.getLoanYear().getId()) ) {
		    	hql.append(" and loanYear = ? ");
		        values.add(nationalLoan.getLoanYear());
	    	}
	    	if(nationalLoan.getStatus() != null && StringUtils.isNotEmpty(nationalLoan.getStatus().getId()) ){
	    		hql.append(" and status = ? ");
		        values.add(nationalLoan.getStatus());
	    	}
	    } 
	    
        //排序条件
        hql.append(" order by createTime asc ");
        
        if (values.size() == 0)
            return this.pagedQuery(hql.toString(), pageNo, pageSize);
        else
            return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	     
	}

	/**
	 * 描述信息: (审核国家助学贷款页面)
	 * @param pageNo
	 * @param pageSize
	 * @param nationalLoan
	 * @return
	 * @see com.uws.sponsor.dao.INationalLoanDao#queryNationalLoanApprovePage(int, int, com.uws.domain.sponsor.NationalLoanModel)
	 */
	public Page queryNationalLoanApproveList(int pageNo, int pageSize, NationalLoanModel nationalLoan ){
		List<Object> values = new ArrayList<Object>();

		StringBuffer hql = new StringBuffer("from NationalLoanModel n where 1=1 and status != ? ");
		values.add(Constants.STATUS_SAVE_DIC);
		
	    if( nationalLoan != null ){
	    	//学年
	    	if ( nationalLoan.getLoanYear() != null && StringUtils.isNotEmpty(nationalLoan.getLoanYear().getId()) ) {
		    	hql.append(" and loanYear = ? ");
		        values.add(nationalLoan.getLoanYear());
		    }
	    	//状态
	    	if (nationalLoan.getStatus() != null && StringUtils.isNotEmpty(nationalLoan.getStatus().getId())) {
		    	hql.append(" and status = ? ");
		        values.add(nationalLoan.getStatus());
		    }
	    	
	    	//国家国家助学贷款表中的学生实体接受查询参数
	    	StudentInfoModel studentInfo = nationalLoan.getStudentInfo();
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
		    	if (studentInfo.getClassId()!= null && StringUtils.isNotEmpty(studentInfo.getClassId().getId()) ){
		    		hql.append(" and studentInfo.classId.id = ? ");
			        values.add(studentInfo.getClassId().getId());
		    	}
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
	    }
	    	
        hql.append(" order by n.applyDate asc ");
        
        if (values.size() == 0)
            return this.pagedQuery(hql.toString(), pageNo, pageSize);
        else
            return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
    };
		
	/**
	 * 描述信息: 教师查询国家助学贷款查询
	 * @param pageNo
	 * @param pageSize
	 * @param student
	 * @param nationalLoan
	 * @param currentOrgId
	 * @return
	 * @see com.uws.sponsor.dao.INationalLoanDao#queryNationalLoanPage(java.lang.Integer, java.lang.Integer, com.uws.domain.orientation.StudentInfoModel, com.uws.domain.sponsor.NationalLoanModel, java.lang.String)
	 */
	@Override
    public Page queryNationalLoanByTeacher(Integer pageNo, Integer pageSize,  NationalLoanModel nationalLoan, String currentOrgId ){
		
		List<Object> values = new ArrayList<Object>();
		
	    StringBuffer hql = new StringBuffer("from NationalLoanModel n where 1=1 and n.status = ? ");
	    values.add(Constants.STATUS_CONFIRM_DIC);
	    
	    if(null != nationalLoan)
	    {
	    	if (null != nationalLoan.getLoanYear() && StringUtils.isNotEmpty(nationalLoan.getLoanYear().getId()) ) {
		    	hql.append(" and loanYear = ? ");
		        values.add(nationalLoan.getLoanYear());
		    }
	    
	    	StudentInfoModel studentInfo = nationalLoan.getStudentInfo();
	    	if(studentInfo != null){
		    	if (studentInfo.getCollege() != null && StringUtils.isNotEmpty(studentInfo.getCollege().getId())){
		    		hql.append(" and studentInfo.college.id = ? ");
			        values.add(studentInfo.getCollege().getId());
		    	}
		    	if (studentInfo.getMajor() != null && StringUtils.isNotEmpty(studentInfo.getMajor().getId()) ){
		    		hql.append(" and studentInfo.major.id = ? ");
			        values.add(studentInfo.getMajor().getId());
		    	}
		    	if (studentInfo.getClassId()!= null && StringUtils.isNotEmpty(studentInfo.getClassId().getId()) ){
		    		hql.append(" and studentInfo.classId.id = ? ");
			        values.add(studentInfo.getClassId().getId());
		    	}
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
	   
	    	if (!StringUtils.isEmpty(currentOrgId) && !ProjectConstants.STUDNET_OFFICE_ORG_ID.equals(currentOrgId))
	    	{
	        	hql.append(" and n.studentInfo.college.id = ? ");
		        values.add(currentOrgId);
	        }
	    }
        hql.append(" order by  n.studentInfo.stuNumber asc ");
        
        if (values.size() == 0)
            return this.pagedQuery(hql.toString(), pageNo, pageSize);
        else
            return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
    }
	
	
	/**
	 * 查询当前学生的本学年的申请列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NationalLoanModel> applyList(String currentStudentId, String loanYear) {
		List<Object> values = new ArrayList<Object>();
		
	    StringBuffer hql = new StringBuffer(" from NationalLoanModel n where 1=1 ");
	    
	    if(currentStudentId != null && !currentStudentId.equals("")){
	    	hql.append(" and studentInfo.id = ? ");
	        values.add(currentStudentId);
	    }
	    if(loanYear != null && !loanYear.equals("")){
	    	hql.append(" and loanYear.id = ? ");
	        values.add(loanYear);
	    }
	    
	    hql.append(" order by n.studentInfo.stuNumber asc ");
	    
	    return query(hql.toString(), values.toArray());
	    
	}
	

}


