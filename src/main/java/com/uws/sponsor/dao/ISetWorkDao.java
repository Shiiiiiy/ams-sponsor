package com.uws.sponsor.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.SponsorPositionModel;
import com.uws.domain.sponsor.WorkOrgModel;
/**
* 
* @Title: ISetWorkDao.java 
* @Package com.uws.sponsor.dao
* @Description: 岗位设置dao层接口
* @author zhangmx  
* @date 2015-7-31 下午14:41:53
*/

public interface ISetWorkDao extends IBaseDao { 
	/**
	 * 用工部门列表
	 * @param workOrg
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	
	public Page queryWorkOrgList(WorkOrgModel workOrg,int pageNo,int pageSize,String currentOrgId);

	/**
	 * 查询--用工部门列表
	 * @param workOrg
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	
	public Page selectPageWorkOrg(WorkOrgModel workOrg,int pageNo,int pageSize,String currentOrgId);
	
	
	
	
	/**
	 * 根据用工部门id 查找岗位列表
	 * @param workOrgId
	 * @return
	 */
	public List<SponsorPositionModel>  queryPositionListByWorkOrgId(String workOrgId);

	
	/**
	 * 
	 * @Title: deletePositionByOrgId
	 * @Description: 处理删除的工作岗位信息
	 * @param workOrgId
	 * @param currentPositionIds
	 * @throws
	 */
	public void deletePositionByOrgId(String workOrgId,String[] currentPositionIds);

	/**
	 * 根据部门，学年，学期查找用工部门
	 * @param orgId
	 * @param yearId
	 * @param termId
	 * @return
	 */
	public WorkOrgModel queryWorkOrgByStatus(String orgId, String yearId,String termId,String statusId);
	
	
	
	
	
	/**
	 * 学生处审批  用工部门列表
	 * @param workOrg
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	
	public Page queryPageSetWorkApporve(WorkOrgModel workOrg,int pageNo,int pageSize,String userId,String[] objectIds);
	/**
	 * 请求所有的用工部门list
	 * @param yearId
	 * @param termId
	 * @param statusId
	 * @return
	 */
	public List<WorkOrgModel> queryWorkOrgList(String yearId, String termId,String statusId);

	
}
