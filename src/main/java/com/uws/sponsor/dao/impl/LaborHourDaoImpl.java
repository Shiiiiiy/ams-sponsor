package com.uws.sponsor.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.sponsor.OrgWorkHourModel;
import com.uws.domain.sponsor.WorkHourModel;
import com.uws.sponsor.dao.ILaborHourDao;
import com.uws.sponsor.util.Constants;

/**
 * @className LaborHourDaoImpl.java
 * @package com.uws.sponsor.dao.impl
 * @description 用工部门dao实现
 * @author lizj
 * @date 2015-8-14  下午2:27:58
 */
@Repository("laborHourDao")
public class LaborHourDaoImpl extends BaseDaoImpl implements ILaborHourDao {
	/**
	 * 分页查询
	 * @param workHourModelVO
	 * @param pageNo
	 * @return
	 */
	@Override
	public Page queryWorkHourPage(WorkHourModel workHourModelVO,int pageSize,int pageNo,int queryType) {
		String hql = "from WorkHourModel where 1=1";
		Map<String,Object> params = new HashMap<String,Object>();
		hql += " and status.id = :statusId";
		params.put("statusId", Constants.STATUS_NORMAL_DICS.getId());
		
		if(queryType == Constants.QUERY_TYPE_STUDENT){//学生
			hql += " and studentId.id =:studentId";
			params.put("studentId", workHourModelVO.getStudentId().getId());
		}
		if(queryType == Constants.QUERY_TYPE_TEACHER){//普通教师
			//用工部门
			if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getOrgId()) && DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getOrgId().getId())){
				hql += " and orgWorkHour.orgId.id = :orgWorkHourId";
				params.put("orgWorkHourId", workHourModelVO.getOrgWorkHour().getOrgId().getId());
			}
		}
		if(queryType == Constants.QUERY_TYPE_ADMIN){//管理员
			//用工部门
			if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getOrgId()) && DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getOrgId().getId())){
				hql += " and orgWorkHour.orgId.id = :orgWorkHourId";
				params.put("orgWorkHourId", workHourModelVO.getOrgWorkHour().getOrgId().getId());
			}
		}
		if(DataUtil.isNotNull(workHourModelVO)){
			if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour())){
				//年份
				if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getWorkYear())){
					hql += " and orgWorkHour.workYear = :workYear";
					params.put("workYear", workHourModelVO.getOrgWorkHour().getWorkYear());
				}
				//月份
				if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getWorkMonth())){
					hql += " and orgWorkHour.workMonth = :workMonth";
					params.put("workMonth", workHourModelVO.getOrgWorkHour().getWorkMonth());
				}
				if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getProcessStatus())){
					hql += " and orgWorkHour.processStatus = :processStatus";
					params.put("processStatus", workHourModelVO.getOrgWorkHour().getProcessStatus());
				}
				if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getSubmitStatus())){
					hql += " and orgWorkHour.submitStatus.id = :submitStatusId";
					params.put("submitStatusId", workHourModelVO.getOrgWorkHour().getSubmitStatus().getId());
				}
			}
			//学生相关查询条件
			if(DataUtil.isNotNull(workHourModelVO.getStudentId())){
				if(DataUtil.isNotNull(workHourModelVO.getStudentId().getName())){
					hql += " and studentId.name like :studentName";
					params.put("studentName", "%"+workHourModelVO.getStudentId().getName()+"%");
				}
				if(DataUtil.isNotNull(workHourModelVO.getStudentId().getStuNumber())){
					hql += " and studentId.stuNumber like :studentNum";
					params.put("studentNum", "%"+workHourModelVO.getStudentId().getStuNumber()+"%");
				}
				if(DataUtil.isNotNull(workHourModelVO.getStudentId().getCollege())){
					if(DataUtil.isNotNull(workHourModelVO.getStudentId().getCollege().getId())){
						hql += " and studentId.college.id = :collegeId";
						params.put("collegeId", workHourModelVO.getStudentId().getCollege().getId());
					}
				}
				if(DataUtil.isNotNull(workHourModelVO.getStudentId().getMajor())){
					if(DataUtil.isNotNull(workHourModelVO.getStudentId().getMajor().getId())){
						hql += " and studentId.major.id = :majorId";
						params.put("majorId", workHourModelVO.getStudentId().getMajor().getId());
					}
				}
				if(DataUtil.isNotNull(workHourModelVO.getStudentId().getClassId())){
					if(DataUtil.isNotNull(workHourModelVO.getStudentId().getClassId().getId())){
						hql += " and studentId.classId.id = :classId";
						params.put("classId", workHourModelVO.getStudentId().getClassId().getId());
					}
				}
			}
			//岗位相关查询条件
			if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour())){
				if(DataUtil.isNotNull(workHourModelVO.getSponsorPosition())){
					if(DataUtil.isNotNull(workHourModelVO.getSponsorPosition().getWorkName())){
						hql += " and sponsorPosition.workName like :workName";
						params.put("workName", "%"+workHourModelVO.getSponsorPosition().getWorkName()+"%");
					}
				}
			}
		}
		//按照年份（降序）、月份（降序）、用工部门（升序）、学院（升序）、班级（升序）、学号（升序）排序
		hql += " order by orgWorkHour.workYear desc,orgWorkHour.workMonth desc,orgWorkHour.orgId.id asc,"
				+"studentId.college.id asc,studentId.major.id asc,studentId.classId.id asc";
		return this.pagedQuery(hql, params, pageSize, pageNo);
	}
	/**
	 * 二级学院审核列表查询
	 * @param workHourModelVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 * @return
	 */
	@Override
	public Page queryWorkHourCollegeApprovePage(WorkHourModel workHourModelVO,
			int dEFAULT_PAGE_SIZE, int pageNo) {
		Map<String,Object> map = this.getQueryCondition(workHourModelVO);
		Map<String,Object> params = (Map<String, Object>) map.get("params");
		String hql = (String) map.get("hql");
		hql += " and processStatus is null";
		//按照年份（降序）、月份（降序）、用工部门（升序）、学院（升序）、班级（升序）、学号（升序）排序
		hql += " order by orgWorkHour.workYear desc,orgWorkHour.workMonth desc,orgWorkHour.orgId.id asc,"
				+"studentId.college.id asc,studentId.major.id asc,studentId.classId.id asc";
		return this.pagedQuery(hql, params, dEFAULT_PAGE_SIZE, pageNo);
	}
	/**
	 * 审核分页
	 * @param workHourModelVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 * @return
	 */
	@Override
	public Page queryWorkHourApprovePage(WorkHourModel workHourModelVO,int dEFAULT_PAGE_SIZE, int pageNo) {
		Map<String,Object> map = this.getQueryCondition(workHourModelVO);
		Map<String,Object> params = (Map<String, Object>) map.get("params");
		String hql = (String) map.get("hql");
		//按照年份（降序）、月份（降序）、用工部门（升序）、学院（升序）、班级（升序）、学号（升序）排序
		hql += " order by orgWorkHour.workYear desc,orgWorkHour.workMonth desc,orgWorkHour.orgId.id asc,"
				+"studentId.college.id asc,studentId.major.id asc,studentId.classId.id asc";
		return this.pagedQuery(hql, params, dEFAULT_PAGE_SIZE, pageNo);
	}
	/**
	 * 查询为添加的用工工时   ----- 查询已添加的的用工工时，包括未删除的和审核通过和正在审核的
	 * @param workHourModelVO
	 * @return
	 */
	@Override
	public List<WorkHourModel> queryWorkHourAddList(WorkHourModel workHourModelVO) {
		Map<String,Object> map = this.getQueryCondition(workHourModelVO);
		Map<String,Object> params = (Map<String, Object>) map.get("params");
		String hql = (String) map.get("hql");
		hql += " and (processStatus is null or processStatus = 'PASS' or processStatus = 'APPROVEING' or processStatus = 'REJECT')";
		return this.query(hql, params);
	}
	/**
	 * 用工工时详细信息
	 * @param workHourModelVO
	 * @return
	 */
	@Override
	public List<WorkHourModel> queryWorkHourList(WorkHourModel workHourModelVO) {
		Map<String,Object> map = this.getQueryCondition(workHourModelVO);
		Map<String,Object> params = (Map<String, Object>) map.get("params");
		String hql = (String) map.get("hql");
		//按照年份（降序）、月份（降序）、用工部门（升序）、学院（升序）、班级（升序）、学号（升序）排序 
		hql += " order by orgWorkHour.workYear desc,orgWorkHour.workMonth desc,orgWorkHour.orgId.id asc,"
				+"studentId.college.id asc,studentId.major.id asc,studentId.classId.id asc";
		return this.query(hql, params);
	}
	/**
	 * 用工工时部门信息
	 * @param orgWorkHourModelVO
	 * @return
	 */
	@Override
	public List<OrgWorkHourModel> queryOrgWorkHourList(OrgWorkHourModel orgWorkHourModelVO){
		String hql = "from OrgWorkHourModel where status.id = :statusId";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("statusId", Constants.STATUS_NORMAL_DICS.getId());
		if(DataUtil.isNotNull(orgWorkHourModelVO.getId())){
			hql += " and id = :id";
			params.put("id", orgWorkHourModelVO.getId());
		}else{
			if(DataUtil.isNotNull(orgWorkHourModelVO.getSchoolYear())){
				hql += " and schoolYear.id = :schoolYearId";
				params.put("schoolYearId", orgWorkHourModelVO.getSchoolYear().getId());
			}
			if(DataUtil.isNotNull(orgWorkHourModelVO.getSchoolTerm())){
				hql += " and schoolTerm.id = :schoolTermId";
				params.put("schoolTermId", orgWorkHourModelVO.getSchoolTerm().getId());
			}
			if(DataUtil.isNotNull(orgWorkHourModelVO.getWorkYear())){
				hql += " and workYear = :workYear";
				params.put("workYear", orgWorkHourModelVO.getWorkYear());
			}
			if(DataUtil.isNotNull(orgWorkHourModelVO.getWorkMonth())){
				hql += " and workMonth = :workMonth";
				params.put("workMonth", orgWorkHourModelVO.getWorkMonth());
			}
			if(DataUtil.isNotNull(orgWorkHourModelVO.getOrgId())){
				if(DataUtil.isNotNull(orgWorkHourModelVO.getOrgId().getId())){
					hql += " and orgId.id = :orgId";
					params.put("orgId", orgWorkHourModelVO.getOrgId().getId());
				}
			}
		}
		return this.query(hql, params);
	}
	/**
	 * 拼接查询条件
	 * @param workHourModelVO
	 * @return
	 */
	private Map<String,Object> getQueryCondition(WorkHourModel workHourModelVO){
		Map<String,Object> map = new HashMap<String, Object>();
		String hql = "from WorkHourModel where 1=1";
		Map<String,Object> params = new HashMap<String,Object>();
		hql += " and status.id = :statusId";
		params.put("statusId", Constants.STATUS_NORMAL_DICS.getId());
		if(DataUtil.isNotNull(workHourModelVO)){
			if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour())){
				//用工部门
				if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getOrgId()) && DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getOrgId().getId())){
					hql += " and orgWorkHour.orgId.id = :orgWorkHourId";
					params.put("orgWorkHourId", workHourModelVO.getOrgWorkHour().getOrgId().getId());
				}
				//年份
				if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getWorkYear())){
					hql += " and orgWorkHour.workYear = :workYear";
					params.put("workYear", workHourModelVO.getOrgWorkHour().getWorkYear());
				}
				//月份
				if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getWorkMonth())){
					hql += " and orgWorkHour.workMonth = :workMonth";
					params.put("workMonth", workHourModelVO.getOrgWorkHour().getWorkMonth());
				}
				if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getProcessStatus())){
					hql += " and orgWorkHour.processStatus = :processStatus";
					params.put("processStatus", workHourModelVO.getOrgWorkHour().getProcessStatus());
				}
				if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getSubmitStatus())){
					hql += " and orgWorkHour.submitStatus.id = :submitStatusId";
					params.put("submitStatusId", workHourModelVO.getOrgWorkHour().getSubmitStatus().getId());
				}
			}
			//学生相关查询条件
			if(DataUtil.isNotNull(workHourModelVO.getStudentId())){
				if(DataUtil.isNotNull(workHourModelVO.getStudentId().getName())){
					hql += " and studentId.name like :studentName";
					params.put("studentName", "%"+workHourModelVO.getStudentId().getName()+"%");
				}
				if(DataUtil.isNotNull(workHourModelVO.getStudentId().getStuNumber())){
					hql += " and studentId.stuNumber like :studentNum";
					params.put("studentNum", "%"+workHourModelVO.getStudentId().getStuNumber()+"%");
				}
				if(DataUtil.isNotNull(workHourModelVO.getStudentId().getCollege())){
					if(DataUtil.isNotNull(workHourModelVO.getStudentId().getCollege().getId())){
						hql += " and studentId.college.id = :collegeId";
						params.put("collegeId", workHourModelVO.getStudentId().getCollege().getId());
					}
				}
				if(DataUtil.isNotNull(workHourModelVO.getStudentId().getMajor())){
					if(DataUtil.isNotNull(workHourModelVO.getStudentId().getMajor().getId())){
						hql += " and studentId.major.id = :majorId";
						params.put("majorId", workHourModelVO.getStudentId().getMajor().getId());
					}
				}
				if(DataUtil.isNotNull(workHourModelVO.getStudentId().getClassId())){
					if(DataUtil.isNotNull(workHourModelVO.getStudentId().getClassId().getId())){
						hql += " and studentId.classId.id = :classId";
						params.put("classId", workHourModelVO.getStudentId().getClassId().getId());
					}
				}
			}
			//岗位相关查询条件
			if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour())){
				if(DataUtil.isNotNull(workHourModelVO.getSponsorPosition())){
					if(DataUtil.isNotNull(workHourModelVO.getSponsorPosition().getWorkName())){
						hql += " and sponsorPosition.workName like :workName";
						params.put("workName", "%"+workHourModelVO.getSponsorPosition().getWorkName()+"%");
					}
				}
			}
		}
		map.put("hql", hql);
		map.put("params", params);
		return map;
	}
	/**
	 * 分页查询用工工时部门工时信息
	 * @param orgWorkHourModelVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 */
	@Override
	public Page queryOrgWorkHourPage(OrgWorkHourModel orgWorkHourModelVO,int dEFAULT_PAGE_SIZE, int pageNo) {
		String hql = "from OrgWorkHourModel where status.id = :statusId";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("statusId", Constants.STATUS_NORMAL_DICS.getId());
		if(DataUtil.isNotNull(orgWorkHourModelVO.getWorkYear())){
			hql += " and workYear = :workYear";
			params.put("workYear", orgWorkHourModelVO.getWorkYear());
		}
		if(DataUtil.isNotNull(orgWorkHourModelVO.getWorkMonth())){
			hql += " and workMonth = :workMonth";
			params.put("workMonth", orgWorkHourModelVO.getWorkMonth());
		}
		if(DataUtil.isNotNull(orgWorkHourModelVO.getOrgId())){
			if(DataUtil.isNotNull(orgWorkHourModelVO.getOrgId().getId())){
				hql += " and orgId.id = :orgId";
				params.put("orgId", orgWorkHourModelVO.getOrgId().getId());
			}
		}
		//审核相关查询条件
		if(DataUtil.isNotNull(orgWorkHourModelVO.getNextApprover())){
			if(DataUtil.isNotNull(orgWorkHourModelVO.getNextApprover().getId())){
				hql += " and nextApprover.id = :nextApproverId";
				params.put("nextApproverId", orgWorkHourModelVO.getNextApprover().getId());
			}
		}
		//流程状态查询条件
		if(DataUtil.isNotNull(orgWorkHourModelVO.getProcessStatus())){
			hql += " and processStatus = :processStatus";
			params.put("processStatus", orgWorkHourModelVO.getProcessStatus());
		}
		//保存或提交状态查询条件
		if(DataUtil.isNotNull(orgWorkHourModelVO.getSubmitStatus())){
			hql += " and submitStatus.id = :submitStatusId";
			params.put("submitStatusId", orgWorkHourModelVO.getSubmitStatus().getId());
		}
		return this.pagedQuery(hql, params, dEFAULT_PAGE_SIZE, pageNo);
	}
	/**
	 * 用工审核分页查询
	 * @param orgWorkHourModelVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 * @param objectIds
	 * @return
	 */
	@Override
	public Page queryApproveOrgWorkHourPage(OrgWorkHourModel orgWorkHourModelVO, int dEFAULT_PAGE_SIZE,int pageNo, String[] objectIds) {
		String hql = "from OrgWorkHourModel where status.id = :statusId";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("statusId", Constants.STATUS_NORMAL_DICS.getId());
		if(DataUtil.isNotNull(orgWorkHourModelVO.getWorkYear())){
			hql += " and workYear = :workYear";
			params.put("workYear", orgWorkHourModelVO.getWorkYear());
		}
		if(DataUtil.isNotNull(orgWorkHourModelVO.getWorkMonth())){
			hql += " and workMonth = :workMonth";
			params.put("workMonth", orgWorkHourModelVO.getWorkMonth());
		}
		if(DataUtil.isNotNull(orgWorkHourModelVO.getOrgId())){
			if(DataUtil.isNotNull(orgWorkHourModelVO.getOrgId().getId())){
				hql += " and orgId.id = :orgId";
				params.put("orgId", orgWorkHourModelVO.getOrgId().getId());
			}
		}
		//保存或提交状态查询条件
		if(DataUtil.isNotNull(orgWorkHourModelVO.getSubmitStatus())){
			hql += " and submitStatus.id = :submitStatusId";
			params.put("submitStatusId", orgWorkHourModelVO.getSubmitStatus().getId());
		}//审核相关查询条件
		hql += " and (nextApprover.id = :nextApproverId or id in (:objectIds))";
		params.put("nextApproverId", orgWorkHourModelVO.getNextApprover().getId());
		params.put("objectIds", objectIds);
		hql += " order by workYear desc,workMonth desc,orgId.id asc";
		return this.pagedQuery(hql, params, dEFAULT_PAGE_SIZE, pageNo);
	}
}
