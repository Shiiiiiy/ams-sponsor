package com.uws.sponsor.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.sponsor.WorkApplyModel;
import com.uws.sponsor.dao.IWorkStudyDao;
import com.uws.sponsor.service.IWorkStudyService;
import com.uws.sponsor.util.Constants;
import com.uws.sys.model.Dic;
import com.uws.util.ProjectConstants;
/**
* 
* @Title: WorkStudyDaoImpl.java 
* @Package com.uws.sponsor.dao.impl
* @Description: 勤工助学dao层实现
* @author zhangmx  
* @date 2015-8-10 下午14:41:53
*/
@Repository("workStudyDao")
public class WorkStudyDaoImpl extends BaseDaoImpl implements IWorkStudyDao{
	@Autowired
	public IWorkStudyService workStudyService;
	/**
	 * 勤工助学列表
	 * @param workApply
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@Override
	public Page queryPageWorkStudy(WorkApplyModel workApply, int pageNo,
			int pageSize,String userId) {
		 List<Object> values = new ArrayList<Object>();
	     StringBuffer hql = new StringBuffer("from WorkApplyModel w  where 1=1 ");
	     //删除状态为正常
		 hql.append(" and w.delStatus.id=? ");
		 values.add(Constants.STATUS_NORMAL_DICS.getId());
	    //判断是学生
		 hql.append(" and w.difficultStudentInfo.student.stuNumber= ? ");
         values.add( userId);
	     
		 if(workApply!=null && !"".equals(workApply)){
			 
			 if(workApply.getApplyFile()!=null && !"".equals(workApply.getApplyFile())){
				//学年
				if(DataUtil.isNotNull(workApply.getApplyFile().getSchoolYear())&& !"".equals(workApply.getApplyFile().getSchoolYear())){
					if(!"".equals(workApply.getApplyFile().getSchoolYear().getId())){
						hql.append(" and w.applyFile.schoolYear.id = ?");
						values.add(workApply.getApplyFile().getSchoolYear().getId());
					}
				}
				//学期
				if(DataUtil.isNotNull(workApply.getApplyFile().getTerm())&& !"".equals(workApply.getApplyFile().getTerm())){
					if(!"".equals(workApply.getApplyFile().getTerm().getId())){
						hql.append(" and w.applyFile.term.id = ?");
						values.add(workApply.getApplyFile().getTerm().getId());
					}
				}
			 }
			 //状态
			 if(workApply.getStatus()!=null && !"".equals(workApply.getStatus())){
				 if(!"".equals(workApply.getStatus().getId())){
						hql.append(" and w.status.id = ?");
						values.add(workApply.getStatus().getId());
				 }
			 }
			 if(workApply.getSponsorPosition()!=null && !"".equals(workApply.getSponsorPosition())){
				 if(workApply.getSponsorPosition().getWorkOrg()!=null){
					//用工部门
					 if(com.uws.core.util.StringUtils.hasText(workApply.getSponsorPosition().getWorkOrg().getOrg().getId())){
						 if(!"".equals(workApply.getSponsorPosition().getWorkOrg().getOrg())){
							 hql.append(" and w.sponsorPosition.workOrg.org.id = ?");
								values.add(workApply.getSponsorPosition().getWorkOrg().getOrg().getId());
						 }
					 }
				 }
				
				//岗位名称
				 if(com.uws.core.util.StringUtils.hasText(workApply.getSponsorPosition().getWorkName())){
					 if(!"".equals(workApply.getSponsorPosition().getPositionId())){
						 hql.append(" and w.sponsorPosition.workName like ?");
						 values.add("%" +workApply.getSponsorPosition().getWorkName() +"%");
					 }
				 }
				
				 
			 }
			
			
			 
			 
		}
		
		 hql.append(" order by  w.applyFile.schoolYear desc, w.applyFile.term desc, " +
		 		"w.difficultStudentInfo.student.college,w.difficultStudentInfo.student.major," +
		 		"w.difficultStudentInfo.student.classId,w.difficultStudentInfo.student.stuNumber");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }else{
			 return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
		 }
		      
	}
	/**
	 * 勤工助学查询列表
	 * @param workApply
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@Override
	public Page selectPageWorkStudy(WorkApplyModel workApply, int pageNo,
			int pageSize,String userId,String orgId) {
		 List<Object> values = new ArrayList<Object>();
	     StringBuffer hql = new StringBuffer("from WorkApplyModel w  where 1=1 ");
	     //删除状态为正常
		 hql.append(" and w.delStatus.id=? ");
		 values.add(Constants.STATUS_NORMAL_DICS.getId());
		 //审核通过
		 hql.append("and w.processStatus=?");
		 values.add("PASS");
		 //判断是否为学校领导
		 boolean isSchoolHeader=this.workStudyService.isRightRole(userId, "HKY_SCHOOL_LEADER");
		 //判断是不是学生处的登录
		 boolean isNotStudentOffice=(null!=orgId && !ProjectConstants.STUDNET_OFFICE_ORG_ID.equals(orgId));
	   
		 if(!isSchoolHeader &&isNotStudentOffice)
		 {  
//			 List<BaseAcademyModel> isTeacher= stuJobTeamSetCommonService.isSubsidizeCounsellor(userId);
//			 if(isTeacher!=null&&isTeacher.size()>0){
//				 //资助辅导员
//				 hql.append(" and w.difficultStudentInfo.student.college.id =  ? ");
//		         values.add( isTeacher.get(0).getOrg().getId());
//
//				 
//			 }else 
//			if(CheckUtils.isCurrentOrgEqCollege(orgId)){
//				 //学院
//				 hql.append(" and w.difficultStudentInfo.student.college.id =  ? ");
//		         values.add( orgId);
//			 }else{
				 //用工部门
				 hql.append(" and w.sponsorPosition.workOrg.org.id =  ? ");
		         values.add( orgId);
//			 }
			
		 }
		 if(workApply!=null && !"".equals(workApply)){

				 //1.学生数据的查询
				 if(workApply.getDifficultStudentInfo()!=null && !"".equals(workApply.getDifficultStudentInfo())){
						StudentInfoModel student=workApply.getDifficultStudentInfo().getStudent();
					   //姓名
					   if ( StringUtils.isNotBlank(student.getName())) {
					         hql.append(" and w.difficultStudentInfo.student.name like ? ");
					         values.add("%" + student.getName() + "%");
					    }
					   //学号
					   if ( StringUtils.isNotBlank(student.getStuNumber())) {
					         hql.append(" and w.difficultStudentInfo.student.stuNumber = ? ");
					         values.add(student.getStuNumber());
					    }
					   //学院
					   if ( student.getCollege()!= null && StringUtils.isNotBlank(student.getCollege().getId())) {
					         hql.append(" and w.difficultStudentInfo.student.college.id = ? ");
					         values.add(student.getCollege().getId());
					    }
					   //专业
					   if ( student.getMajor()!= null && StringUtils.isNotBlank(student.getMajor().getId())) {
					         hql.append(" and w.difficultStudentInfo.student.major.id = ? ");
					         values.add(student.getMajor().getId());
					    }
					   //班级
					   if ( student.getClassId()!= null && StringUtils.isNotBlank(student.getClassId().getId())) {
					         hql.append(" and w.difficultStudentInfo.student.classId.id = ? ");
					         values.add(student.getClassId().getId());
					    }
					
				 }
				 //申请信息的查询
				 if(workApply.getApplyFile()!=null && !"".equals(workApply.getApplyFile())){
						
						//学年
						if(DataUtil.isNotNull(workApply.getApplyFile().getSchoolYear())&& !"".equals(workApply.getApplyFile().getSchoolYear())){
							if(!"".equals(workApply.getApplyFile().getSchoolYear().getId())){
								hql.append(" and w.applyFile.schoolYear.id = ?");
								values.add(workApply.getApplyFile().getSchoolYear().getId());
							}
						}
						//学期
						if(DataUtil.isNotNull(workApply.getApplyFile().getTerm())&& !"".equals(workApply.getApplyFile().getTerm())){
							if(!"".equals(workApply.getApplyFile().getTerm().getId())){
								hql.append(" and w.applyFile.term.id = ?");
								values.add(workApply.getApplyFile().getTerm().getId());
							}
						}
				}
				 //岗位状态
				 if(workApply.getPostStatus()!=null && !"".equals(workApply.getPostStatus())){
					 if(!"".equals(workApply.getPostStatus().getId())){
							hql.append(" and w.postStatus.id = ?");
							values.add(workApply.getPostStatus().getId());
					 }
				 }
				//岗位信息查询
				 if(workApply.getSponsorPosition()!=null && !"".equals(workApply.getSponsorPosition())){
					 if(workApply.getSponsorPosition().getWorkOrg()!=null){
						//用工部门
						 if(com.uws.core.util.StringUtils.hasText(workApply.getSponsorPosition().getWorkOrg().getOrg().getId())){
							 if(!"".equals(workApply.getSponsorPosition().getWorkOrg().getOrg())){
								 hql.append(" and w.sponsorPosition.workOrg.org.id = ?");
									values.add(workApply.getSponsorPosition().getWorkOrg().getOrg().getId());
							 }
						 }
					 }
					
					//岗位名称
					 if(com.uws.core.util.StringUtils.hasText(workApply.getSponsorPosition().getWorkName())){
						 if(!"".equals(workApply.getSponsorPosition().getPositionId())){
							 hql.append(" and w.sponsorPosition.workName like ?");
							 values.add("%" +workApply.getSponsorPosition().getWorkName() +"%");
						 }
					 }
				 }
				 
		 }
	     
	
		
		 hql.append(" order by  w.applyFile.schoolYear desc, w.applyFile.term desc, " +
		 		"w.difficultStudentInfo.student.college,w.difficultStudentInfo.student.major," +
		 		"w.difficultStudentInfo.student.classId,w.difficultStudentInfo.student.stuNumber," +
		 		"w.sponsorPosition.workOrg.org.id,w.sponsorPosition.workName");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }else{
			 return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
		 }
		      
	}
	
	
	/**
	 * 根据申请单号查找申请岗位
	 */
	@Override
	public WorkApplyModel queryWorkApplyByApplyFileId(String workApplyFileId){
		return (WorkApplyModel)this.queryUnique("from  WorkApplyModel  where applyFile.workApplyFileId=? and delStatus.id=?", new Object[]{workApplyFileId,Constants.STATUS_NORMAL_DICS.getId()});
	}
	/**
	 * 根据学年、学期、困难生、保存(提交、审核通过)、删除状态正常 找申请岗位
	 * @param schoolYearId
	 * @param termId
	 * @param difficultStudentInfo
	 * @return
	 */
	@Override
	public List<WorkApplyModel> queryWorkApplyByAllStatus(String schoolYearId,
			String termId, String difficultStudentInfoId,String statusId) {
		 List<Object> values = new ArrayList<Object>();
	     StringBuffer hql = new StringBuffer("from WorkApplyModel w " +
	     		"where w.applyFile.schoolYear.id=? and w.applyFile.term.id=? " +
	     		"and w.difficultStudentInfo.id=? and w.delStatus.id=? ");
	     values.add(schoolYearId);
	     values.add(termId);
	     values.add(difficultStudentInfoId);
	     values.add(Constants.STATUS_NORMAL_DICS.getId());
	     if(Constants.STATUS_SAVE_DICS.getId().equals(statusId)){
	    	 //保存
	    	 hql.append(" and  w.status.id=?");
	    	 values.add(statusId);
	     }
	     if(Constants.STATUS_SUBMIT_DICS.getId().equals(statusId)){
	    	 //提交
	    	 hql.append(" and  w.status.id=?");
	    	 values.add(statusId);
	     }
	     if("PASS".equals(statusId)){
	    	 //审核通过
	    	 hql.append(" and  w.processStatus=?");
	    	 values.add(statusId);
	     }
	     if("REJECT".equals(statusId)){
	    	 //审核未通过
	    	 hql.append(" and  w.processStatus=?");
	    	 values.add(statusId);
	     }
	     if("ABANDONED".equals(statusId)){
	    	 //审核废止
	    	 hql.append(" and  w.processStatus=?");
	    	 values.add(statusId);
	     }
	     
	     if(Constants.STATUS_DISMISS.getId().equals(statusId)){
	    	 //离岗
	    	 hql.append(" and  w.postStatus.id=?");
	    	 values.add(statusId);
	     }
	     if(Constants.STATUS_WASTE.getId().equals(statusId)){
	    	 //放弃岗位
	    	 hql.append(" and  w.postStatus.id=?");
	    	 values.add(statusId);
	     }
	     
	     List<WorkApplyModel> list=( List<WorkApplyModel>)this.query(hql.toString(), values.toArray());
	
		return list;
	}

	/**
	 * 根据岗位id 和 确认状态查找困难生
	 * @param positionId
	 * @param postStatus
	 * @return
	 */
	@Override
	public List<WorkApplyModel> queryDiffByPositionIdAndIsPost(
			String positionId, Dic postStatus) {
		List<Object> values = new ArrayList<Object>();
	    StringBuffer hql = new StringBuffer(" from WorkApplyModel where sponsorPosition.positionId=? ");
	    values.add(positionId);
	    
	    //删除状态正常
	  	hql.append(" and delStatus.id=? ");
	  	values.add(Constants.STATUS_NORMAL_DICS.getId());
	  	//在岗状态
		if(postStatus!=null && !"".equals(postStatus)){
			hql.append(" and postStatus.id=?");
			values.add(postStatus.getId());
		}
		
		List<WorkApplyModel> workApplylist;
		if(values.size()==0){
			workApplylist=(List<WorkApplyModel> )this.query(hql.toString(), new Object[0]);

		}else{
			workApplylist=(List<WorkApplyModel> )this.query(hql.toString(), values.toArray());
		}
		
		
		return workApplylist;
		
	}
	
	/**
	 * 勤工助学审批列表
	 * @param workApply
	 * @param pageNo
	 * @param pageSize
	 * @param userId
	 * @return
	 */
	
	@Override
	public Page pageApproveWorkApply(WorkApplyModel workApply, int pageNo,
			int pageSize,String userId,String[] objectIds) {
		 Map<String,Object> values = new HashMap<String,Object>();
		 StringBuffer hql = new StringBuffer("select w from WorkApplyModel w where 1=1 and (w.nextApprover.id = :userId or w.workApplyId in (:objectIds)) ");
		 values.put("userId",userId);
		 values.put("objectIds",objectIds);
	 	 //删除状态正常
		 hql.append(" and delStatus.id= :delId ");
		 values.put("delId",Constants.STATUS_NORMAL_DICS.getId());
	
		 if(workApply!=null && !"".equals(workApply)){
			 if(workApply.getDifficultStudentInfo()!=null && !"".equals(workApply.getDifficultStudentInfo())){
					StudentInfoModel student=workApply.getDifficultStudentInfo().getStudent();
				   //姓名
				   if ( StringUtils.isNotBlank(student.getName())) {
				         hql.append(" and w.difficultStudentInfo.student.name like :stuName ");
				         values.put("stuName","%" +HqlEscapeUtil.escape( student.getName() )+ "%");
				    }
				   //学号
				   if ( StringUtils.isNotBlank(student.getStuNumber())) {
				         hql.append(" and w.difficultStudentInfo.student.stuNumber = :stuNumber ");
				         values.put("stuNumber",student.getStuNumber());
				    }
				   //学院
				   if ( student.getCollege()!= null && StringUtils.isNotBlank(student.getCollege().getId())) {
				         hql.append(" and w.difficultStudentInfo.student.college.id = :collegeId ");
				         values.put("collegeId",student.getCollege().getId());
				    }
				   //专业
				   if ( student.getMajor()!= null && StringUtils.isNotBlank(student.getMajor().getId())) {
				         hql.append(" and w.difficultStudentInfo.student.major.id = :majorId ");
				         values.put("majorId",student.getMajor().getId());
				    }
				   //班级
				   if ( student.getClassId()!= null && StringUtils.isNotBlank(student.getClassId().getId())) {
				         hql.append(" and w.difficultStudentInfo.student.classId.id = :classId ");
				         values.put("classId",student.getClassId().getId());
				    }
			
			 }
			 if(workApply.getApplyFile()!=null && !"".equals(workApply.getApplyFile())){
				//学年
					if(DataUtil.isNotNull(workApply.getApplyFile().getSchoolYear()) &&
							!"".equals(workApply.getApplyFile().getSchoolYear())){
						if(!"".equals(workApply.getApplyFile().getSchoolYear().getId())){
							hql.append(" and w.applyFile.schoolYear.id = :schoolYearId");
							values.put("schoolYearId",workApply.getApplyFile().getSchoolYear().getId());
						}
					}
					//学期
					if(DataUtil.isNotNull(workApply.getApplyFile().getTerm())&& !"".equals(workApply.getApplyFile().getTerm())){
						if(!"".equals(workApply.getApplyFile().getTerm().getId())){
							hql.append(" and w.applyFile.term.id = :termId");
							values.put("termId",workApply.getApplyFile().getTerm().getId());
						}
					}
					
					 if(workApply.getSponsorPosition()!=null && !"".equals(workApply.getSponsorPosition())){
						 if(workApply.getSponsorPosition().getWorkOrg()!=null){
							//用工部门
							 if(com.uws.core.util.StringUtils.hasText(workApply.getSponsorPosition().getWorkOrg().getOrg().getId())){
								 if(!"".equals(workApply.getSponsorPosition().getWorkOrg().getOrg())){
									 hql.append(" and w.sponsorPosition.workOrg.org.id= :orgId");
										values.put("orgId",workApply.getSponsorPosition().getWorkOrg().getOrg().getId());
								 }
							 }
						 }
						
						//岗位名称
						 if(com.uws.core.util.StringUtils.hasText(workApply.getSponsorPosition().getWorkName())){
							 if(!"".equals(workApply.getSponsorPosition().getPositionId())){
								 hql.append(" and w.sponsorPosition.workName like :workName");
								 values.put("workName","%"+HqlEscapeUtil.escape(workApply.getSponsorPosition().getWorkName())+"%");
							 }
						 }
						
						 
					 }
					
			 }
			
			// 审核状态
			if (!StringUtils.isEmpty(workApply.getProcessStatus())&&!"".equals(workApply.getProcessStatus())) {
				if(ProjectConstants.CURRENT_APPROVE_USER_PROCESS_CODE.equals(workApply.getProcessStatus()))
				{
					hql.append(" and w.nextApprover.id = :approveUserId ");
					values.put("approveUserId",userId);
				}
				else
				{
					hql.append(" and w.processStatus = :processStatus ");
					values.put("processStatus",workApply.getProcessStatus());
				}
			}
			
		}
		
		 hql.append(" order by  w.applyFile.schoolYear desc, w.applyFile.term desc,w.processStatus, " +
		 		"w.difficultStudentInfo.student.college,w.difficultStudentInfo.student.major," +
		 		"w.difficultStudentInfo.student.classId,w.sponsorPosition.workOrg.org,w.difficultStudentInfo.student.stuNumber");
	    
	 	if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), values, pageSize, pageNo);
		      
	}
	/**
	 * 勤工助学导出列表
	 * @param workApply
	 * @param pageNo
	 * @param pageSize
	 * @param orgId
	 * @return
	 */
	@Override
	public Page pageExportWorkApply(WorkApplyModel workApply, int pageNo,
			int pageSize, String orgId) {
		 List<Object> values = new ArrayList<Object>();
	     StringBuffer hql = new StringBuffer("from WorkApplyModel w  where 1=1 ");
	 	//删除状态正常
		hql.append(" and delStatus.id=? ");
		values.add(Constants.STATUS_NORMAL_DICS.getId());
		 //审核状态
		hql.append(" and w.processStatus = ?");
		values.add("PASS");
		//用工部门
		//判断是否为学生处的登录
		if(null!=orgId && !ProjectConstants.STUDNET_OFFICE_ORG_ID.equals(orgId))
		{
			 hql.append(" and w.sponsorPosition.workOrg.org.id = ? ");
	         values.add( orgId);
		}
		if(workApply!=null && !"".equals(workApply)){
			 if(workApply.getDifficultStudentInfo()!=null && !"".equals(workApply.getDifficultStudentInfo())){
					StudentInfoModel student=workApply.getDifficultStudentInfo().getStudent();
				   //姓名
				   if ( StringUtils.isNotBlank(student.getName())) {
				         hql.append(" and w.difficultStudentInfo.student.name like ? ");
				         values.add("%" + student.getName() + "%");
				    }
				   //学号
				   if ( StringUtils.isNotBlank(student.getStuNumber())) {
				         hql.append(" and w.difficultStudentInfo.student.stuNumber = ? ");
				         values.add(student.getStuNumber());
				    }
				   //学院
				   if ( student.getCollege()!= null && StringUtils.isNotBlank(student.getCollege().getId())) {
				         hql.append(" and w.difficultStudentInfo.student.college.id = ? ");
				         values.add(student.getCollege().getId());
				    }
				   //专业
				   if ( student.getMajor()!= null && StringUtils.isNotBlank(student.getMajor().getId())) {
				         hql.append(" and w.difficultStudentInfo.student.major.id = ? ");
				         values.add(student.getMajor().getId());
				    }
				   //班级
				   if ( student.getClassId()!= null && StringUtils.isNotBlank(student.getClassId().getId())) {
				         hql.append(" and w.difficultStudentInfo.student.classId.id = ? ");
				         values.add(student.getClassId().getId());
				    }
			
			 }
			 if(workApply.getApplyFile()!=null && !"".equals(workApply.getApplyFile())){
				//学年
					if(DataUtil.isNotNull(workApply.getApplyFile().getSchoolYear()) &&
							!"".equals(workApply.getApplyFile().getSchoolYear())){
						if(!"".equals(workApply.getApplyFile().getSchoolYear().getId())){
							hql.append(" and w.applyFile.schoolYear.id = ?");
							values.add(workApply.getApplyFile().getSchoolYear().getId());
						}
					}
					//学期
					if(DataUtil.isNotNull(workApply.getApplyFile().getTerm())&& !"".equals(workApply.getApplyFile().getTerm())){
						if(!"".equals(workApply.getApplyFile().getTerm().getId())){
							hql.append(" and w.applyFile.term.id = ?");
							values.add(workApply.getApplyFile().getTerm().getId());
						}
					}
					
					 if(workApply.getSponsorPosition()!=null && !"".equals(workApply.getSponsorPosition())){
						 if(workApply.getSponsorPosition().getWorkOrg()!=null){
							//用工部门
							 if(com.uws.core.util.StringUtils.hasText(workApply.getSponsorPosition().getWorkOrg().getOrg().getId())){
								 if(!"".equals(workApply.getSponsorPosition().getWorkOrg().getOrg())){
									 hql.append(" and w.sponsorPosition.workOrg.org.id = ?");
									 values.add(workApply.getSponsorPosition().getWorkOrg().getOrg().getId());
								 }
							 }
						 }
						
						//岗位名称
						 if(com.uws.core.util.StringUtils.hasText(workApply.getSponsorPosition().getWorkName())){
							 if(!"".equals(workApply.getSponsorPosition().getPositionId())){
								 hql.append(" and w.sponsorPosition.workName like ?");
								 values.add("%"+workApply.getSponsorPosition().getWorkName()+"%");
							 }
						 }
						
						 
					 }
					
			 }
			 //岗位状态
			 if(workApply.getPostStatus()!=null && !"".equals(workApply.getPostStatus())){
				 if(!"".equals(workApply.getPostStatus().getId())){
						hql.append(" and w.postStatus.id = ?");
						values.add(workApply.getPostStatus().getId());
				 }
			 }
			
		}
		
		 hql.append(" order by  w.applyFile.schoolYear desc, w.applyFile.term desc, " +
		 		"w.difficultStudentInfo.student.college,w.difficultStudentInfo.student.major," +
		 		"w.difficultStudentInfo.student.classId,w.sponsorPosition.workOrg.org,w.difficultStudentInfo.student.stuNumber");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }else{
			 return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
		 }
		      
	}
	
	
	/**
	 * 根据用工部门查询 岗位申请
	 * @param workOrgId
	 * @param yearId
	 * @param termId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<WorkApplyModel> queryWorkApplyByWorkOrgId(String workOrgId,String workYear,String workMonth,Dic schoolYear,Dic schoolTrem){//
		int yearAndMonth = Integer.parseInt(workYear)*100+Integer.parseInt(workMonth);
		String hql="from WorkApplyModel where sponsorPosition.workOrg.org.id=? and sponsorPosition.workOrg.schoolYear.id=? and sponsorPosition.workOrg.term.id=? and delStatus.id=? and ((((year(confirmDate)*100+month(confirmDate)) <= ? and (year(dismissDate)*100+month(dismissDate)) >= ? and postStatus.id = ? )) or (postStatus.id = ? and (year(confirmDate)*100+month(confirmDate)) <= ?))";
		return this.query(hql, new Object[]{workOrgId,schoolYear.getId(),schoolTrem.getId(),Constants.STATUS_NORMAL_DICS.getId(),yearAndMonth,yearAndMonth,Constants.STATUS_DISMISS.getId(),Constants.STATUS_IS_POST.getId(),yearAndMonth});
	}
	
}
