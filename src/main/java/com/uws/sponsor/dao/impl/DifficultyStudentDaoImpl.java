package com.uws.sponsor.dao.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.common.util.SchoolYearUtil;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.sponsor.DifficultStudentInfo;
import com.uws.sponsor.dao.IDifficultyStudentDao;
import com.uws.sponsor.util.Constants;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;
/**
* @ClassName: DifficultyStudentDaoImpl 
* @Description: 困难生管理模块Dao实现类
* @author liuchen
* @date 2015-8-7 下午16:24:08 
*
*/
@Repository("com.uws.sponsor.dao.impl.DifficultyStudentDaoImpl")
public class DifficultyStudentDaoImpl extends BaseDaoImpl implements IDifficultyStudentDao{
	
	//数据字典
	private DicUtil dicUtil = DicFactory.getDicUtil();
	
	/**
    * 
    * @Title: DifficultyStudentDaoImpl.java 
    * @Package com.uws.sponsor.dao.Impl
    * @Description: 查询困难生信息列表
    * @author liuchen
    * @date 2015-7-30 下午1:31:19
     */
	@Override
	public Page queryStudetInfoList(int pageNo, int pageSize,DifficultStudentInfo difficultStudentInfo,String currentStudentId) {
		 List<Object> values = new ArrayList<Object>();
	     StringBuffer hql = new StringBuffer("from DifficultStudentInfo where 1=1");
	     //删除状态为正常
		 hql.append(" and delStatus.id=? ");
		 values.add(Constants.STATUS_NORMAL_DICS.getId());
		 //只能看到自己的申请记录
		 hql.append(" and student.id=? ");
		 values.add(currentStudentId);
		 if(difficultStudentInfo!=null){
			   if (difficultStudentInfo.getDifficultLevel()!= null && StringUtils.isNotBlank(difficultStudentInfo.getDifficultLevel().getId())) {
			         hql.append(" and difficultLevel.id = ? ");
			         values.add(difficultStudentInfo.getDifficultLevel().getId());
			    }
			   if (difficultStudentInfo.getSchoolYear()!= null  && StringUtils.isNotBlank(difficultStudentInfo.getSchoolYear().getId())) {
			         hql.append(" and schoolYear.id= ? ");
			         values.add(difficultStudentInfo.getSchoolYear().getId());
			    }
		}
		 hql.append(" order by updateTime desc");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }
		      return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}
	
	
	@Override
	public Page instructorApproveStudetInfoList(int pageNo, int pageSize,
			DifficultStudentInfo difficultStudentInfo,String currentUserId,String[] objectIds) {
		 Map<String,Object> values = new HashMap<String,Object>();
	     StringBuffer hql = new StringBuffer("select d from DifficultStudentInfo d where 1=1 and (d.nextApprover.id = :currentUserId or d.id in (:objectIds)) ");
	     values.put("currentUserId",currentUserId);
	     values.put("objectIds",objectIds);
	     //删除状态为正常
		 hql.append(" and d.delStatus.id= :status");
		 values.put("status",Constants.STATUS_NORMAL_DICS.getId());
		 //状态为提交状态
		 //hql.append(" and status.id=? ");
		 //values.add(Constants.STATUS_SUBMIT_DICS.getId());
		 //下一节点的人为当前登录人
		// hql.append(" and d.nextApprover.id=? ");
		// values.add(currentUserId);
		 if(difficultStudentInfo!=null){
			   if (difficultStudentInfo.getStudent()!= null && StringUtils.isNotBlank(difficultStudentInfo.getStudent().getName())) {
			         hql.append(" and d.student.name like :studentName ");
			         values.put("studentName","%" + difficultStudentInfo.getStudent().getName() + "%");
			    }
			   if (difficultStudentInfo.getStudent()!= null && StringUtils.isNotBlank(difficultStudentInfo.getStudent().getStuNumber())) {
			         hql.append(" and d.student.stuNumber like :stuNumber ");
			         values.put("stuNumber","%" + difficultStudentInfo.getStudent().getStuNumber() + "%");
			    }
			   if (difficultStudentInfo.getDifficultLevel()!= null && StringUtils.isNotBlank(difficultStudentInfo.getDifficultLevel().getId())) {
			         hql.append(" and d.difficultLevel.id = :difficultLevel ");
			         values.put("difficultLevel",difficultStudentInfo.getDifficultLevel().getId());
			    }
			   if (difficultStudentInfo.getStudent()!= null && difficultStudentInfo.getStudent().getCollege()!= null && StringUtils.isNotBlank(difficultStudentInfo.getStudent().getCollege().getId())) {
			         hql.append(" and d.student.college.id = :college ");
			         values.put("college",difficultStudentInfo.getStudent().getCollege().getId());
			    }
			   if (difficultStudentInfo.getStudent()!= null && difficultStudentInfo.getStudent().getMajor()!= null && StringUtils.isNotBlank(difficultStudentInfo.getStudent().getMajor().getId())) {
			         hql.append(" and d.student.major.id = :major ");
			         values.put("major",difficultStudentInfo.getStudent().getMajor().getId());
			    }
			   if (difficultStudentInfo.getStudent()!= null && difficultStudentInfo.getStudent().getClassId()!= null && StringUtils.isNotBlank(difficultStudentInfo.getStudent().getClassId().getId())) {
			         hql.append(" and d.student.classId.id = :classId ");
			         values.put("classId",difficultStudentInfo.getStudent().getClassId().getId());
			    }
			   if (difficultStudentInfo.getSchoolYear()!= null  && StringUtils.isNotBlank(difficultStudentInfo.getSchoolYear().getId())) {
			         hql.append(" and d.schoolYear.id= :schoolYear ");
			         values.put("schoolYear",difficultStudentInfo.getSchoolYear().getId());
			    }
				// 审核状态
				if (!StringUtils.isEmpty(difficultStudentInfo.getProcessStatus())) {
					if(ProjectConstants.CURRENT_APPROVE_USER_PROCESS_CODE.equals(difficultStudentInfo.getProcessStatus()))
					{
						hql.append(" and d.nextApprover.id = :approveUserId ");
						values.put("approveUserId",currentUserId);
					}
					else
					{
						hql.append(" and d.processStatus = :processStatus and ( d.nextApprover.id != :approveUserId or d.nextApprover is null )");
						values.put("processStatus",difficultStudentInfo.getProcessStatus());
						values.put("approveUserId",currentUserId);
					}
				}
		}
		 hql.append(" order by d.processStatus,d.student.classId,d.updateTime desc");
		 if (values.size() == 0)
				return this.pagedQuery(hql.toString(), pageNo, pageSize);
			else
				return this.pagedQuery(hql.toString(), values, pageSize, pageNo);
	}
	
	
	
	/**
	 * 描述信息: 根据当前登录人id查询困难生信息
	 * @param currentStudentId
	 * @return
	 * @see com.uws.sponsor.dao.IStudentAwardDao#queryDifficultStudentById(java.lang.String)
	 */
	@Override
	public DifficultStudentInfo queryDifficultStudentByStudentId(String currentStudentId) {
		
		return (DifficultStudentInfo)this.queryUnique("from DifficultStudentInfo where student.id=? and schoolYear.id=? and processStatus=?  and delStatus.id = ? ", new Object[] { currentStudentId ,SchoolYearUtil.getYearDic().getId(),"PASS", Constants.STATUS_NORMAL_DICS.getId()});
	}
	
	
	/**
	 * 描述信息: 根据当前登录人查询审核对象
	 * @param currentApproveId
	 * @return
	 * @see com.uws.sponsor.dao.IDifficultyStudentDao#queryDifficultStudentByApproveId(java.lang.String)
	 */
	@Override
	public DifficultStudentInfo queryDifficultStudentByApproveId(String currentApproveId) {
		return (DifficultStudentInfo)this.queryUnique("from DifficultStudentInfo where nextApprover.id=? and schoolYear.id=? and delStatus.id = ? ", new Object[] { currentApproveId ,SchoolYearUtil.getYearDic().getId(), Constants.STATUS_NORMAL_DICS.getId()});
	}
	
	
	@Override
	public Page queryPassStudetInfoList(int pageNo, int pageSize,
			DifficultStudentInfo difficultStudentInfo,BaseAcademyModel college,List<BaseClassModel> classList) {
		 List<Object> values = new ArrayList<Object>();
	     StringBuffer hql = new StringBuffer("from DifficultStudentInfo where 1=1");
	     //删除状态为正常
		 hql.append(" and delStatus.id=? ");
		 values.add(Constants.STATUS_NORMAL_DICS.getId());
		 //最终审核通过的状态
		 hql.append(" and processStatus =? ");
		 values.add("PASS");
		 if(college!=null && !college.getCode().equals(ProjectConstants.STUDNET_OFFICE_ORG_ID)){
			 if (difficultStudentInfo.getStudent()!= null && StringUtils.isNotBlank(difficultStudentInfo.getStudent().getName())) {
		         hql.append(" and student.name like ? ");
		         values.add("%" + difficultStudentInfo.getStudent().getName() + "%");
		    }
		   if (difficultStudentInfo.getStudent()!= null && StringUtils.isNotBlank(difficultStudentInfo.getStudent().getStuNumber())) {
		         hql.append(" and student.stuNumber like ? ");
		         values.add("%" + difficultStudentInfo.getStudent().getStuNumber() + "%");
		    }
		   if (difficultStudentInfo.getDifficultLevel()!= null && StringUtils.isNotBlank(difficultStudentInfo.getDifficultLevel().getId())) {
		         hql.append(" and difficultLevel.id = ? ");
		         values.add(difficultStudentInfo.getDifficultLevel().getId());
		    }
		   if (StringUtils.isNotBlank(college.getId())) {
		         hql.append(" and student.college.id = ? ");
		         values.add(college.getId());
		    }
		   if (difficultStudentInfo.getMajor()!= null && StringUtils.isNotBlank(difficultStudentInfo.getMajor().getId())) {
		         hql.append(" and student.major.id = ? ");
		         values.add(difficultStudentInfo.getMajor().getId());
		    }
		   if (difficultStudentInfo.getClassId()!= null && StringUtils.isNotBlank(difficultStudentInfo.getClassId().getId())) {
		         hql.append(" and student.classId.id = ? ");
		         values.add(difficultStudentInfo.getClassId().getId());
		    }
		   if (difficultStudentInfo.getSchoolYear()!= null  && StringUtils.isNotBlank(difficultStudentInfo.getSchoolYear().getId())) {
		         hql.append(" and schoolYear.id= ? ");
		         values.add(difficultStudentInfo.getSchoolYear().getId());
		    }
		 }
		 else{
			   if (difficultStudentInfo.getStudent()!= null && StringUtils.isNotBlank(difficultStudentInfo.getStudent().getName())) {
			         hql.append(" and student.name like ? ");
			         values.add("%" + difficultStudentInfo.getStudent().getName() + "%");
			    }
			   if (difficultStudentInfo.getStudent()!= null && StringUtils.isNotBlank(difficultStudentInfo.getStudent().getStuNumber())) {
			         hql.append(" and student.stuNumber like ? ");
			         values.add("%" + difficultStudentInfo.getStudent().getStuNumber() + "%");
			    }
			   if (difficultStudentInfo.getDifficultLevel()!= null && StringUtils.isNotBlank(difficultStudentInfo.getDifficultLevel().getId())) {
			         hql.append(" and difficultLevel.id = ? ");
			         values.add(difficultStudentInfo.getDifficultLevel().getId());
			    }
			   if (difficultStudentInfo.getCollege()!= null && StringUtils.isNotBlank(difficultStudentInfo.getCollege().getId())) {
			         hql.append(" and student.college.id = ? ");
			         values.add(difficultStudentInfo.getCollege().getId());
			    }
			   if (difficultStudentInfo.getMajor()!= null && StringUtils.isNotBlank(difficultStudentInfo.getMajor().getId())) {
			         hql.append(" and student.major.id = ? ");
			         values.add(difficultStudentInfo.getMajor().getId());
			    }
			   if (difficultStudentInfo.getClassId()!= null && StringUtils.isNotBlank(difficultStudentInfo.getClassId().getId())) {
			         hql.append(" and student.classId.id = ? ");
			         values.add(difficultStudentInfo.getClassId().getId());
			    }
			   if (difficultStudentInfo.getSchoolYear()!= null  && StringUtils.isNotBlank(difficultStudentInfo.getSchoolYear().getId())) {
			         hql.append(" and schoolYear.id= ? ");
			         values.add(difficultStudentInfo.getSchoolYear().getId());
			    }
		}
		 hql.append(" order by updateTime desc");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }
		      return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}
	
	
	/**
	 * 描述信息:验证同一学年的学生是否申请过困难生
	 * @param id
	 * @param currentStudentId
	 * @param schoolYear
	 * @return
	 * @see com.uws.sponsor.dao.IDifficultyStudentDao#isExistType(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isExistType(String id, String currentStudentId,String schoolYear) {
		List<DifficultStudentInfo> list = query("from DifficultStudentInfo d where d.student.id=? and d.schoolYear.id=? and d.delStatus.id = ? ", new Object[] {currentStudentId,schoolYear,Constants.STATUS_NORMAL_DICS.getId() });
	     boolean b = false;
	     if ((list != null) && (list.size() > 0)) {
	       for (DifficultStudentInfo difficultStudentInfo : list) {
	         if (!difficultStudentInfo.getId().equals(id)) {
	           b = true;
	         }
	       }
	     }
	     return b;
	}
	
	
	@Override
	public List<BaseClassModel> queryClassByTeacher(String currentUserId) {
		
		return this.query("from BaseClassModel c where c.id in (select s.klass.id from StuJobTeamSetModel s where s.teacher.id= ? and (s.teacherType=?) group by s.klass.id)", currentUserId,dicUtil.getDicInfo("TEACHER_TYPE", "HEADMASTER"));
	}
	
	
 }
	

