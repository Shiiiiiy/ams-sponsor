package com.uws.sponsor.dao.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.AidGrant;
import com.uws.domain.sponsor.DifficultStudentAward;
import com.uws.domain.sponsor.InspirationalAward;
import com.uws.domain.sponsor.JobGrant;
import com.uws.domain.sponsor.OtherAward;
import com.uws.domain.sponsor.SchoolLoan;
import com.uws.domain.sponsor.TuitionWaiver;
import com.uws.sponsor.dao.IStudentAwardDao;
import com.uws.sponsor.util.Constants;
import com.uws.util.ProjectConstants;
/**
 * 
* @ClassName: StudentAwardDaoImpl 
* @Description: 困难生奖助管理Dao实现类
* @author liuchen
* @date 2015-8-10 上午11:49:13 
*
 */
@Repository("com.uws.sponsor.dao.impl.StudentAwardDaoImpl")
public class StudentAwardDaoImpl extends BaseDaoImpl implements IStudentAwardDao{
	/**
	 * 描述信息: 困难生奖助查询列表方法
	 * @param pageNo
	 * @param pageSize
	 * @param studentAward
	 * @return
	 * @see com.uws.sponsor.dao.IStudentAwardDao#queryStudentAwardList(int, int, com.uws.domain.sponsor.StudentAward)
	 */
	@Override
	public Page queryStudentAwardList(int pageNo, int pageSize,DifficultStudentAward difficultStudentAward,String crrentStudentId) {
		 List<Object> values = new ArrayList<Object>();
	     StringBuffer hql = new StringBuffer("from DifficultStudentAward where 1=1");
	     //删除状态为正常
		 hql.append(" and delStatus.id=? ");
		 values.add(Constants.STATUS_NORMAL_DICS.getId());
		 //只能看到自己的申请奖助记录
		 hql.append(" and difficultStudentInfo.student.id=? ");
		 values.add(crrentStudentId);
		 if(difficultStudentAward!=null){
			   if(difficultStudentAward.getAwardType()!= null && StringUtils.isNotBlank(difficultStudentAward.getAwardType().getId())){
				   hql.append(" and awardType.id = ?");
				   values.add(difficultStudentAward.getAwardType().getId());
			   }
			   if (difficultStudentAward.getSchoolYear()!= null  && StringUtils.isNotBlank(difficultStudentAward.getSchoolYear().getId())) {
			         hql.append(" and schoolYear.id= ? ");
			         values.add(difficultStudentAward.getSchoolYear().getId());
			    }
		 }	   
		 
		 hql.append(" order by updateTime desc");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }
		      return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}
	
	/**
	 * 描述信息: 根据奖助id获取单项励志奖学金对象
	 * @param awardId
	 * @return
	 * @see com.uws.sponsor.dao.IStudentAwardDao#getInspirationalByAwardId(java.lang.String)
	 */
	@Override
	public InspirationalAward getInspirationalByAwardId(String awardId) {
		
		return (InspirationalAward)this.queryUnique("from InspirationalAward where studentAward.id=? ", new Object[] {awardId});
	}
	
	/**
	 * 描述信息:根据奖助id获取奖学金对象
	 * @param awardId
	 * @return
	 * @see com.uws.sponsor.dao.IStudentAwardDao#getAidGrantByAwardId(java.lang.String)
	 */
	@Override
	public AidGrant getAidGrantByAwardId(String awardId) {
		return (AidGrant)this.queryUnique("from AidGrant where studentAward.id=? ", new Object[] {awardId});
	}
	
	/**
	 * 描述信息:根据奖助id获取就业补助对象
	 * @param awardId
	 * @return
	 * @see com.uws.sponsor.dao.IStudentAwardDao#getJobGrantByAwardId(java.lang.String)
	 */
	@Override
	public JobGrant getJobGrantByAwardId(String awardId) {
		return (JobGrant)this.queryUnique("from JobGrant where studentAward.id=? ", new Object[] {awardId});
	}
	
	/**
	 * 描述信息:根据奖助id获取校内无息贷款对象
	 * @param awardId
	 * @return
	 * @see com.uws.sponsor.dao.IStudentAwardDao#getSchoolLoanByAwardId(java.lang.String)
	 */
	@Override
	public SchoolLoan getSchoolLoanByAwardId(String awardId) {
		return (SchoolLoan)this.queryUnique("from SchoolLoan where studentAward.id=? ", new Object[] {awardId});
	}

	/**
	 * 描述信息: 根据奖助id或者学费减免对象
	 * @param awardId
	 * @return
	 * @see com.uws.sponsor.dao.IStudentAwardDao#getTuitionWaiverByAwardId(java.lang.String)
	 */
	@Override
	public TuitionWaiver getTuitionWaiverByAwardId(String awardId) {
		return (TuitionWaiver)this.queryUnique("from TuitionWaiver where studentAward.id=? ", new Object[] {awardId});
	}
	
	/**
	 * 描述信息: 查询其他奖项的表单（不包含5个奖项）
	 * @param awardId
	 * @return
	 * @see com.uws.sponsor.dao.IStudentAwardDao#getOtherAwardByAwardId(java.lang.String)
	 */
	@Override
	public OtherAward getOtherAwardByAwardId(String awardId) {
		return (OtherAward)this.queryUnique("from OtherAward where studentAward.id=? ", new Object[] {awardId});
	}
	
	
	@Override
	public Page approveStudetAwardList(int pageNo, int pageSize,DifficultStudentAward difficultStudentAward, String currentUserId,String[] objectIds) {
		 Map<String,Object> values = new HashMap<String,Object>();
	     StringBuffer hql = new StringBuffer("select d from DifficultStudentAward d where 1=1 and (d.nextApprover.id = :currentUserId or d.id in (:objectIds)) ");
	     values.put("currentUserId",currentUserId);
	     values.put("objectIds",objectIds);
	     //删除状态为正常
		 hql.append(" and delStatus.id= :status");
		 values.put("status",Constants.STATUS_NORMAL_DICS.getId());
		 if(difficultStudentAward!=null){
			   if (difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && StringUtils.isNotBlank(difficultStudentAward.getDifficultStudentInfo().getStudent().getName())) {
			         hql.append(" and difficultStudentInfo.student.name like :studentName");
			         values.put("studentName","%" + difficultStudentAward.getDifficultStudentInfo().getStudent().getName() + "%");
			    }
			   if (difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && StringUtils.isNotBlank(difficultStudentAward.getDifficultStudentInfo().getStudent().getStuNumber())) {
			         hql.append(" and difficultStudentInfo.student.stuNumber like :studentNumber ");
			         values.put("studentNumber","%" + difficultStudentAward.getDifficultStudentInfo().getStudent().getStuNumber() + "%");
			    }
			   if(difficultStudentAward.getAwardType()!= null && StringUtils.isNotBlank(difficultStudentAward.getAwardType().getId())){
				   hql.append(" and awardType.id = :awardType");
				   values.put("awardType",difficultStudentAward.getAwardType().getId());
			   }
			   if (difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege()!= null && StringUtils.isNotBlank(difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege().getId())) {
			         hql.append(" and difficultStudentInfo.student.college.id = :college ");
			         values.put("college",difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege().getId());
			    }
			   if (difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor()!= null && StringUtils.isNotBlank(difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor().getId())) {
			         hql.append(" and difficultStudentInfo.student.major.id = :major ");
			         values.put("major",difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor().getId());
			    }
			   if (difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent().getClassId()!= null && StringUtils.isNotBlank(difficultStudentAward.getDifficultStudentInfo().getStudent().getClassId().getId())) {
			         hql.append(" and difficultStudentInfo.student.classId.id = :classId ");
			         values.put("classId",difficultStudentAward.getDifficultStudentInfo().getStudent().getClassId().getId());
			    }
			   if (difficultStudentAward.getSchoolYear()!= null  && StringUtils.isNotBlank(difficultStudentAward.getSchoolYear().getId())) {
			         hql.append(" and schoolYear.id= :schoolYear ");
			         values.put("schoolYear",difficultStudentAward.getSchoolYear().getId());
			    }
			   if (difficultStudentAward.getStatus()!= null  && StringUtils.isNotBlank(difficultStudentAward.getStatus().getId())) {
			         hql.append(" and status.id= :status ");
			         values.put("status",difficultStudentAward.getStatus().getId());
			    }
			   if (difficultStudentAward.getFamilyFinance()!= null  && StringUtils.isNotBlank(difficultStudentAward.getFamilyFinance().getId())) {
			         hql.append(" and familyFinance.id= :familyFinance ");
			         values.put("familyFinance",difficultStudentAward.getFamilyFinance().getId());
			    }
			   // 审核状态
				if (!StringUtils.isEmpty(difficultStudentAward.getProcessStatus())) {
					if(ProjectConstants.CURRENT_APPROVE_USER_PROCESS_CODE.equals(difficultStudentAward.getProcessStatus()))
					{
						hql.append(" and d.nextApprover.id = :approveUserId ");
						values.put("approveUserId",currentUserId);
					}
					else
					{
						hql.append(" and d.processStatus = :processStatus and ( d.nextApprover.id != :approveUserId or d.nextApprover is null )");
						values.put("processStatus",difficultStudentAward.getProcessStatus());
						values.put("approveUserId",currentUserId);
					}
				}
		 }	   
		 hql.append(" order by updateTime desc");
		 if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), values, pageSize, pageNo);
	}
	
	
	@Override
	public boolean isExistType(String id,String currentStudentId, String schoolYear, String awardType) {
		 List<DifficultStudentAward> list = query("from DifficultStudentAward d where d.difficultStudentInfo.id=? and d.schoolYear.id=? and d.awardType.id=? and d.delStatus.id = ? ", new Object[] {currentStudentId,schoolYear,awardType,Constants.STATUS_NORMAL_DICS.getId() });
	     boolean b = false;
	     if ((list != null) && (list.size() > 0)) {
	       for (DifficultStudentAward studentAward : list) {
	         if (!studentAward.getId().equals(id)) {
	           b = true;
	         }
	       }
	     }
	     return b;
	}
	
	
	@Override
	public Page queryStudentAward(int pageNo, int pageSize,DifficultStudentAward difficultStudentAward) {
		 List<Object> values = new ArrayList<Object>();
	     StringBuffer hql = new StringBuffer("from DifficultStudentAward where 1=1");
	     //删除状态为正常
		 hql.append(" and delStatus.id=? ");
		 values.add(Constants.STATUS_NORMAL_DICS.getId());
		 //流程实例为通过的
		 hql.append(" and processStatus=? ");
		 values.add("PASS");
		 if(difficultStudentAward!=null){
			   if (difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && StringUtils.isNotBlank(difficultStudentAward.getDifficultStudentInfo().getStudent().getName())) {
			         hql.append(" and difficultStudentInfo.student.name like ? ");
			         values.add("%" + difficultStudentAward.getDifficultStudentInfo().getStudent().getName() + "%");
			    }
			   if (difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && StringUtils.isNotBlank(difficultStudentAward.getDifficultStudentInfo().getStudent().getStuNumber())) {
			         hql.append(" and difficultStudentInfo.student.stuNumber like ? ");
			         values.add("%" + difficultStudentAward.getDifficultStudentInfo().getStudent().getStuNumber() + "%");
			    }
			   if(difficultStudentAward.getAwardType()!= null && StringUtils.isNotBlank(difficultStudentAward.getAwardType().getId())){
				   hql.append(" and awardType.id = ?");
				   values.add(difficultStudentAward.getAwardType().getId());
			   }
			   if (difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege()!= null && StringUtils.isNotBlank(difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege().getId())) {
			         hql.append(" and difficultStudentInfo.student.college.id = ? ");
			         values.add(difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege().getId());
			    }
			   if (difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor()!= null && StringUtils.isNotBlank(difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor().getId())) {
			         hql.append(" and difficultStudentInfo.student.major.id = ? ");
			         values.add(difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor().getId());
			    }
			   if (difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent().getClassId()!= null && StringUtils.isNotBlank(difficultStudentAward.getDifficultStudentInfo().getStudent().getClassId().getId())) {
			         hql.append(" and difficultStudentInfo.student.classId.id = ? ");
			         values.add(difficultStudentAward.getDifficultStudentInfo().getStudent().getClassId().getId());
			    }
			   if (difficultStudentAward.getSchoolYear()!= null  && StringUtils.isNotBlank(difficultStudentAward.getSchoolYear().getId())) {
			         hql.append(" and schoolYear.id= ? ");
			         values.add(difficultStudentAward.getSchoolYear().getId());
			    }
			   if (difficultStudentAward.getStatus()!= null  && StringUtils.isNotBlank(difficultStudentAward.getStatus().getId())) {
			         hql.append(" and status.id= ? ");
			         values.add(difficultStudentAward.getStatus().getId());
			    }
			   if (difficultStudentAward.getFamilyFinance()!= null  && StringUtils.isNotBlank(difficultStudentAward.getFamilyFinance().getId())) {
			         hql.append(" and familyFinance.id= ? ");
			         values.add(difficultStudentAward.getFamilyFinance().getId());
			    }
		 }	   
		 hql.append(" order by updateTime desc");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }
		      return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}


}
