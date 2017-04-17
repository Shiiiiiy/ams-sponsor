package com.uws.sponsor.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.OrgWorkHourModel;
import com.uws.domain.sponsor.WorkHourModel;

/**
 * 用工工时
 * @className ILaborHourDao.java
 * @package com.uws.sponsor.dao
 * @description
 * @author lizj
 * @date 2015-8-14  下午4:42:17
 */
public interface ILaborHourDao extends IBaseDao {
	/**
	 * 分页查询
	 * @param workHourModelVO
	 * @param pageNo
	 * @return
	 */
	public Page queryWorkHourPage(WorkHourModel workHourModelVO,int pageSize,int pageNo,int queryType);
	/**
	 * 查询为添加的用工工时   ----- 查询已添加的的用工工时，包括未删除的和审核通过的
	 * @param workHourModelVO
	 * @return
	 */
	public List<WorkHourModel> queryWorkHourAddList(WorkHourModel workHourModelVO);
	/**
	 * 二级学院审核列表查询
	 * @param workHourModelVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 * @return
	 */
	public Page queryWorkHourCollegeApprovePage(WorkHourModel workHourModelVO,int dEFAULT_PAGE_SIZE, int pageNo);
	/**
	 * 审核分页
	 * @param workHourModelVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 * @return
	 */
	public Page queryWorkHourApprovePage(WorkHourModel workHourModelVO, int dEFAULT_PAGE_SIZE, int pageNo);
	/**
	 * 用工工时部门工时信息
	 * @param workHourModelVO
	 * @return
	 */
	public List<WorkHourModel> queryWorkHourList(WorkHourModel workHourModelVO);
	/**
	 * 用工工时部门信息
	 * @param orgWorkHourModelVO
	 * @return
	 */
	public List<OrgWorkHourModel> queryOrgWorkHourList(OrgWorkHourModel orgWorkHourModelVO);
	/**
	 * 分页查询用工工时部门工时信息
	 * @param orgWorkHourModelVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 */
	public Page queryOrgWorkHourPage(OrgWorkHourModel orgWorkHourModelVO, int dEFAULT_PAGE_SIZE, int pageNo);
	/**
	 * 用工审核分页查询
	 * @param orgWorkHourModelVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 * @param objectIds
	 * @return
	 */
	public Page queryApproveOrgWorkHourPage(OrgWorkHourModel orgWorkHourModelVO, int dEFAULT_PAGE_SIZE,int pageNo, String[] objectIds);
}
