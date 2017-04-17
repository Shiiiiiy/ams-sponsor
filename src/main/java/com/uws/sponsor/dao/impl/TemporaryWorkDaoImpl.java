package com.uws.sponsor.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.sponsor.TemporaryWorkStudyModel;
import com.uws.sponsor.controller.TemporaryWorkController;
import com.uws.sponsor.dao.ITemporaryWorkDao;
import com.uws.sponsor.util.Constants;

/**
 * @Title TemporaryWorkDaoImpl.java
 * @Package com.uws.sponsor.dao.impl
 * @Description 临时工勤工助学Dao
 * @date 2015-8-10  上午11:16:53
 */
@Repository("temporaryWorkDao")
public class TemporaryWorkDaoImpl extends BaseDaoImpl implements ITemporaryWorkDao {
	/**
	 * 分页查询
	 * @param temporaryWorkStudyModel
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	@Override
	public Page queryTemporaryWorkInfo(TemporaryWorkStudyModel temporaryWorkStudyModel, int pageSize,int pageNo,int queryType) {
		String hql = "from TemporaryWorkStudyModel where status.id = :status";//逻辑删除
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status",Constants.STATUS_NORMAL_DICS.getId());
		//添加权限控制：数据过滤
		if(queryType == Constants.QUERY_TYPE_STUDENT){
			hql += " and studentId.id = :studentId";
			params.put("studentId",temporaryWorkStudyModel.getStudentId().getId());
		}
		if(queryType == Constants.QUERY_TYPE_ADMIN){
			if(DataUtil.isNotNull(temporaryWorkStudyModel) && DataUtil.isNotNull(temporaryWorkStudyModel.getOrgId()) && DataUtil.isNotNull(temporaryWorkStudyModel.getOrgId().getId())){
				hql += " and orgId.id = :orgId";
				params.put("orgId",temporaryWorkStudyModel.getOrgId().getId());
			}
		}
		if(queryType == Constants.QUERY_TYPE_TEACHER){
			if(DataUtil.isNotNull(temporaryWorkStudyModel) && DataUtil.isNotNull(temporaryWorkStudyModel.getOrgId()) && DataUtil.isNotNull(temporaryWorkStudyModel.getOrgId().getId())){
				hql += " and orgId.id = :orgId";
				params.put("orgId",temporaryWorkStudyModel.getOrgId().getId());
			}
		}
		//添加用工部门条件
		if(DataUtil.isNotNull(temporaryWorkStudyModel.getOrgName())){
			hql += " and orgName like :orgName";
			params.put("orgName","%"+temporaryWorkStudyModel.getOrgName()+"%");
		}
		//添加月份条件
		if(DataUtil.isNotNull(temporaryWorkStudyModel.getWorkMonth())){
			hql += " and workMonth = :workMonth";
			params.put("workMonth",temporaryWorkStudyModel.getWorkMonth());
		}
		//添加学年
		if(DataUtil.isNotNull(temporaryWorkStudyModel.getSchoolYear())){
			if(DataUtil.isNotNull(temporaryWorkStudyModel.getSchoolYear().getId())){
				hql += " and schoolYear.id = :schoolYear";
				params.put("schoolYear",temporaryWorkStudyModel.getSchoolYear().getId());
			}
		}
		//添加学期
		if(DataUtil.isNotNull(temporaryWorkStudyModel.getSchoolTerm())){
			if(DataUtil.isNotNull(temporaryWorkStudyModel.getSchoolTerm().getId())){
				hql += " and schoolTerm.id = :schoolTerm";
				params.put("schoolTerm",temporaryWorkStudyModel.getSchoolTerm().getId());
			}
		}
		StudentInfoModel studentInfoModel = temporaryWorkStudyModel.getStudentId();
		if(studentInfoModel != null){
			//添加学生姓名条件
			if(DataUtil.isNotNull(studentInfoModel.getName())){
				hql += " and studentId.name like :studentName";
				params.put("studentName","%"+studentInfoModel.getName()+"%");
			}
			//添加学号条件
			if(DataUtil.isNotNull(studentInfoModel.getStuNumber())){
				hql += " and studentId.stuNumber like :stuNumber";
				params.put("stuNumber","%"+studentInfoModel.getStuNumber()+"%");
			}
			//添加学院条件
			BaseAcademyModel baseAcademyModel = studentInfoModel.getCollege();
			if(baseAcademyModel != null){
				if(DataUtil.isNotNull(baseAcademyModel.getId())){
					hql += " and studentId.college.id = :collegeId";
					params.put("collegeId",baseAcademyModel.getId());
				}
			}
			//添加班级条件
			BaseClassModel baseClassModel = studentInfoModel.getClassId();
			if(baseClassModel != null){
				if(DataUtil.isNotNull(baseClassModel.getId())){
					hql += " and studentId.classId.id = :classId";
					params.put("classId",baseClassModel.getId());
				}
			}
		}
		hql += " order by schoolYear.code desc,workMonth asc,orgId.id asc,studentId.college.id asc,studentId.classId.id asc,studentId.stuNumber asc";
		return pagedQuery(hql, params, pageSize, pageNo);
	}
	/**
	 * 条件查询
	 * @param temporaryWorkStudyModel
	 * @return
	 */
	@Override
	public List<TemporaryWorkStudyModel> queryTemporaryWorkInfoByCond(TemporaryWorkStudyModel temporaryWorkStudyModel) {
		Map<String,Object> conditions = this.getCondition(temporaryWorkStudyModel);
		String hql = (String) conditions.get("hql");
		Map<String, Object> params = (Map<String, Object>) conditions.get("params");
		hql += " order by schoolYear.code desc,workMonth asc,orgId.id asc,studentId.college.id asc,studentId.classId.id asc,studentId.stuNumber asc";
		return this.query(hql, params);
	}
	/**
	 * 拼接查询条件
	 * @param temporaryWorkStudyModel
	 * @return
	 */
	private Map<String,Object> getCondition(TemporaryWorkStudyModel temporaryWorkStudyModel){
		Map<String,Object> conditions = new HashMap<String, Object>();
		
		String hql = "from TemporaryWorkStudyModel where status.id = :status";//逻辑删除
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status",Constants.STATUS_NORMAL_DICS.getId());
		//添加用工部门的查询条件
		if(DataUtil.isNotNull(temporaryWorkStudyModel.getOrgId())){
			if(DataUtil.isNotNull(temporaryWorkStudyModel.getOrgId().getId())){
				hql += " and orgId.id = :orgId";
				params.put("orgId",temporaryWorkStudyModel.getOrgId().getId());
			}
		}
		//添加用工部门条件
		if(DataUtil.isNotNull(temporaryWorkStudyModel.getOrgName())){
			hql += " and orgName = :orgName";
			params.put("orgName",temporaryWorkStudyModel.getOrgName());
		}
		//添加月份条件
		if(DataUtil.isNotNull(temporaryWorkStudyModel.getWorkMonth())){
			hql += " and workMonth = :workMonth";
			params.put("workMonth",temporaryWorkStudyModel.getWorkMonth());
		}
		//添加学年
		if(DataUtil.isNotNull(temporaryWorkStudyModel.getSchoolYear())){
			if(DataUtil.isNotNull(temporaryWorkStudyModel.getSchoolYear().getId())){
				hql += " and schoolYear.id = :schoolYear";
				params.put("schoolYear",temporaryWorkStudyModel.getSchoolYear().getId());
			}
		}
		//添加学期
		if(DataUtil.isNotNull(temporaryWorkStudyModel.getSchoolTerm())){
			if(DataUtil.isNotNull(temporaryWorkStudyModel.getSchoolTerm().getId())){
				hql += " and schoolTerm.id = :schoolTerm";
				params.put("schoolTerm",temporaryWorkStudyModel.getSchoolTerm().getId());
			}
		}
		StudentInfoModel studentInfoModel = temporaryWorkStudyModel.getStudentId();
		if(studentInfoModel != null){
			//添加学生姓名条件
			if(DataUtil.isNotNull(studentInfoModel.getName())){
				hql += " and studentId.name like :studentName";
				params.put("studentName","%"+studentInfoModel.getName()+"%");
			}
			//添加学号条件
			if(DataUtil.isNotNull(studentInfoModel.getStuNumber())){
				hql += " and studentId.stuNumber like :stuNumber";
				params.put("stuNumber","%"+studentInfoModel.getStuNumber()+"%");
			}
			//添加学院条件
			BaseAcademyModel baseAcademyModel = studentInfoModel.getCollege();
			if(baseAcademyModel != null){
				if(DataUtil.isNotNull(baseAcademyModel.getId())){
					hql += " and studentId.college.id = :collegeId";
					params.put("collegeId",baseAcademyModel.getId());
				}
			}
			//添加班级条件
			BaseClassModel baseClassModel = studentInfoModel.getClassId();
			if(baseClassModel != null){
				if(DataUtil.isNotNull(baseClassModel.getId())){
					hql += " and studentId.classId.id = :classId";
					params.put("classId",baseClassModel.getId());
				}
			}
		}
		conditions.put("hql", hql);
		conditions.put("params", params);
		return conditions;
	}
	/**
	 * 添加修改排重查询
	 * @param temporaryWorkStudyModelVO
	 * @return
	 */
	@Override
	public List<TemporaryWorkStudyModel> queryAddTemporaryWorkInfoByCond(TemporaryWorkStudyModel temporaryWorkStudyModel) {
		//temporaryWorkStudyModelVO.setSchoolTerm(null);
		String hql = "from TemporaryWorkStudyModel where status.id = :status";//逻辑删除
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status",Constants.STATUS_NORMAL_DICS.getId());
		//添加用工部门条件
		if(DataUtil.isNotNull(temporaryWorkStudyModel.getOrgName())){
			hql += " and orgName = :orgName";
			params.put("orgName",temporaryWorkStudyModel.getOrgName());
		}
		//添加月份条件
		if(DataUtil.isNotNull(temporaryWorkStudyModel.getWorkMonth())){
			hql += " and workMonth = :workMonth";
			params.put("workMonth",temporaryWorkStudyModel.getWorkMonth());
		}
		//添加学年
		if(DataUtil.isNotNull(temporaryWorkStudyModel.getSchoolYear())){
			if(DataUtil.isNotNull(temporaryWorkStudyModel.getSchoolYear().getId())){
				hql += " and schoolYear.id = :schoolYear";
				params.put("schoolYear",temporaryWorkStudyModel.getSchoolYear().getId());
			}
		}
		StudentInfoModel studentInfoModel = temporaryWorkStudyModel.getStudentId();
		if(studentInfoModel != null){
			//添加学号条件
			if(DataUtil.isNotNull(studentInfoModel.getId())){
				hql += " and studentId.id = :stuId";
				params.put("stuId",studentInfoModel.getId());
			}
		}
		hql += " order by schoolYear.code desc,workMonth asc,orgId.id asc,studentId.college.id asc,studentId.classId.id asc,studentId.stuNumber asc";
		return this.query(hql, params);
	}
	/**
	 * 通过ID数组批量逻辑删除
	 * @param temporaryWorkStudyModel
	 * @param ids
	 */
	@Override
	public void updateMultTemporaryWorkByIds(TemporaryWorkStudyModel temporaryWorkStudyModel, String[] ids) {
		String hql = "update TemporaryWorkStudyModel set status.id = :statusId where id in (:id)";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("statusId",temporaryWorkStudyModel.getStatus().getId());
		params.put("id", ids);
		this.executeHql(hql, params);
	}

}
