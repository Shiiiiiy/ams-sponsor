package com.uws.sponsor.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.OrgWorkHourModel;
import com.uws.domain.sponsor.WorkHourModel;
import com.uws.sponsor.dao.ILaborHourDao;
import com.uws.sponsor.service.ILaborHourService;
/**
 * 
 * @className LaborHourServiceImpl.java
 * @package com.uws.sponsor.service.impl
 * @description 
 * @author lizj
 * @date 2015-8-12  下午3:19:51
 */
@Service("laborHourService")
public class LaborHourServiceImpl extends BaseServiceImpl implements ILaborHourService {
	@Autowired
	private ILaborHourDao laborHourDao;
	/**
	 * 根据ID查询用工工时实体
	 * @param id
	 * @return
	 */
	@Override
	public WorkHourModel getWorkHourById(String id) {
		return (WorkHourModel) this.laborHourDao.get(WorkHourModel.class, id);
	}
	/**
	 * 根据ID查询用工工时部门实体
	 * @param id
	 * @return
	 */
	public OrgWorkHourModel getOrgWorkHourById(String id){
		return (OrgWorkHourModel) this.laborHourDao.get(OrgWorkHourModel.class, id);
	}
	/**
	 * 分页查询
	 * @param laborHourModelVO
	 * @param pageNo
	 * @return
	 */
	@Override
	public Page queryWorkHourPage(WorkHourModel workHourModelVO,int pageSize,int pageNo,int queryType) {
		return this.laborHourDao.queryWorkHourPage(workHourModelVO,pageSize,pageNo,queryType);
	}
	/**
	 * 查询为添加的用工工时   ----- 查询已添加的的用工工时，包括未删除的和审核通过的
	 * @param workHourModelVO
	 * @return
	 */
	@Override
	public List<WorkHourModel> queryWorkHourAddList(WorkHourModel workHourModelVO) {
		return this.laborHourDao.queryWorkHourAddList(workHourModelVO);
	}
	/**
	 * 二级学院审核列表查询
	 * @param workHourModelVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 * @return
	 */
	@Override
	public Page queryWorkHourCollegeApprovePage(WorkHourModel workHourModelVO,int dEFAULT_PAGE_SIZE, int pageNo) {
		return this.laborHourDao.queryWorkHourCollegeApprovePage(workHourModelVO,dEFAULT_PAGE_SIZE, pageNo);
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
		return this.laborHourDao.queryWorkHourApprovePage(workHourModelVO, dEFAULT_PAGE_SIZE, pageNo);
	}
	/**
	 * 用工工时部门工时信息
	 * @param workHourModelVO
	 * @return
	 */
	@Override
	public List<WorkHourModel> queryWorkHourList(WorkHourModel workHourModelVO) {
		return this.laborHourDao.queryWorkHourList(workHourModelVO);
	}
	/**
	 * 用工工时部门信息
	 * @param orgWorkHourModelVO
	 * @return
	 */
	@Override
	public List<OrgWorkHourModel> queryOrgWorkHourList(OrgWorkHourModel orgWorkHourModelVO){
		return this.laborHourDao.queryOrgWorkHourList(orgWorkHourModelVO);
	}
	/**
	 * 保存用工工时
	 * @param workHourModelVO
	 */
	@Override
	public void saveWorkHour(WorkHourModel workHourModelVO) {
		this.laborHourDao.save(workHourModelVO);
	}
	/**
	 * 保存用工工时部门工时信息
	 * @param workHourModelVO
	 */
	@Override
	public void saveOrgWorkHour(OrgWorkHourModel orgWorkHourModelVO){
		this.laborHourDao.save(orgWorkHourModelVO);
	}
	/**
	 * 修改用工实体
	 * @param laborHourModel
	 */
	@Override
	public void updateWorkHour(WorkHourModel workHourModelPO) {
		this.laborHourDao.update(workHourModelPO);
	}
	/**
	 * 分页查询用工工时部门工时信息
	 * @param orgWorkHourModelVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 */
	@Override
	public Page queryOrgWorkHourPage(OrgWorkHourModel orgWorkHourModelVO, int dEFAULT_PAGE_SIZE, int pageNo) {
		return this.laborHourDao.queryOrgWorkHourPage(orgWorkHourModelVO, dEFAULT_PAGE_SIZE, pageNo);
	}
	@Override
	public OrgWorkHourModel queryOrgWorkHourById(String orgWorkHourId) {
		return (OrgWorkHourModel) this.laborHourDao.get(OrgWorkHourModel.class, orgWorkHourId);
	}
	/**
	 * 修改用工工时部门
	 * @param orgWorkHourModelPO
	 */
	@Override
	public void updateOrgWorkHour(OrgWorkHourModel orgWorkHourModelPO) {
		this.laborHourDao.update(orgWorkHourModelPO);
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
		return this.laborHourDao.queryApproveOrgWorkHourPage(orgWorkHourModelVO, Page.DEFAULT_PAGE_SIZE, pageNo,objectIds);
	}
}
