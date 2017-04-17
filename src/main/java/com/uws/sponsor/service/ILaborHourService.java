package com.uws.sponsor.service;

import java.util.List;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.OrgWorkHourModel;
import com.uws.domain.sponsor.WorkHourModel;
import com.uws.domain.sponsor.WorkApplyModel;
/**
 * 
 * @className ILaborHourService.java
 * @package com.uws.sponsor.service
 * @description  
 * @author lizj
 * @date 2015-8-12  下午3:33:21
 */
public interface ILaborHourService {
	/**
	 * 根据ID查询用工工时实体
	 * @param id
	 * @return
	 */
	public WorkHourModel getWorkHourById(String id);
	/**
	 * 根据ID查询用工工时部门实体
	 * @param id
	 * @return
	 */
	public OrgWorkHourModel getOrgWorkHourById(String id);
	/**
	 * 分页查询
	 * @param workHourModelVO
	 * @param pageNo
	 * @return
	 */
	public Page queryWorkHourPage(WorkHourModel workHourModelVO,int pageSize,int pageNo,int queryType);
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
	public Page queryWorkHourApprovePage(WorkHourModel workHourModelVO,int dEFAULT_PAGE_SIZE, int pageNo);
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
	 * 查询为添加的用工工时   ----- 查询已添加的的用工工时，包括未删除的和审核通过的
	 * @param workHourModelVO
	 * @return
	 */
	public List<WorkHourModel> queryWorkHourAddList(WorkHourModel workHourModelVO);
	/**
	 * 保存用工工时
	 * @param workHourModelVO
	 */
	public void saveWorkHour(WorkHourModel workHourModelVO);
	/**
	 * 保存用工工时部门工时信息
	 * @param workHourModelVO
	 */
	public void saveOrgWorkHour(OrgWorkHourModel orgWorkHourModelVO);
	/**
	 * 修改用工实体
	 * @param workHourModelPO
	 */
	public void updateWorkHour(WorkHourModel workHourModelPO);
	/**
	 * 分页查询用工工时部门工时信息
	 * @param orgWorkHourModelVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 * @return 
	 */
	public Page queryOrgWorkHourPage(OrgWorkHourModel orgWorkHourModelVO,int dEFAULT_PAGE_SIZE, int pageNo);
	/**
	 * 通过用工工时部门Id查询用工工时部门
	 * @param orgWorkHourId
	 * @return
	 */
	public OrgWorkHourModel queryOrgWorkHourById(String orgWorkHourId);
	/**
	 * 修改用工工时部门
	 * @param orgWorkHourModelPO
	 */
	public void updateOrgWorkHour(OrgWorkHourModel orgWorkHourModelPO);
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