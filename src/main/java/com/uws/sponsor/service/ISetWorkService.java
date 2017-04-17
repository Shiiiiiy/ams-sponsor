package com.uws.sponsor.service;

import java.util.List;

import com.uws.core.base.BaseModel;
import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.PositionListModel;
import com.uws.domain.sponsor.SponsorPositionModel;
import com.uws.domain.sponsor.WorkOrgModel;
import com.uws.sys.model.Dic;
/**
* 
* @Title: ISetWorkService.java 
* @Package com.uws.sponsor.service 
* @Description: 岗位设置service层接口
* @author zhangmx  
* @date 2015-7-31 下午14:41:53
*/
public interface ISetWorkService extends IBaseService {
	/**
	 * 岗位设置--用工部门列表
	 * @param workOrg
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	
	public Page queryPageWorkOrg(WorkOrgModel workOrg,int pageNo,int pageSize,String currentOrgId);
	/**
	 * 查询--用工部门列表
	 * @param workOrg
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	
	public Page selectPageWorkOrg(WorkOrgModel workOrg,int pageNo,int pageSize,String currentOrgId);
	
	/**
	 * 保存用工部门
	 * @param workOrg
	 */
	
	public void saveWorkOrg(WorkOrgModel workOrg);
	/**
	 * 更新用工部门
	 * @param workOrg
	 */
	public void updateWorkOrg(WorkOrgModel workOrg);
	/**
	 * 更新岗位
	 * @param sponsorPosition
	 */
	public void updateSponsorPosition(SponsorPositionModel sponsorPosition);
	/**
	 * 保存岗位
	 * @param sponsorPosition
	 */
	
	public void saveSponsorPosition(SponsorPositionModel sponsorPosition);
	
	/**
	 * 根据id查找岗位
	 * @param id
	 * @return
	 */
	
	public SponsorPositionModel querySponsorPositionById(String id);
	/**
	 *  根据用工部门的id查找用工部门
	 * @param workOrgId
	 * @return
	 */
	public WorkOrgModel queryWorkOrgById(String workOrgId);
	
	
	/**
	 * 根据部门的id、学年、学期查找岗位列表
	 * @param workOrgId
	 * @return
	 */
	public List<SponsorPositionModel> queryPositionList(String orgId,String schoolYearId, String termId,String statusId);
	
	
	/**
	 * 更新或保存
	 * @param workOrg
	 * @param positionList
	 * @param currentIds
	 */
	public void saveOrUpdate(WorkOrgModel workOrg,PositionListModel positionList,String[] currentIds,String flags);
	/**
	 * 根据学年\学期 部门查找用工部门
	 * @param orgId
	 * @param yearId
	 * @param termId
	 * @return
	 */
	public WorkOrgModel queryWorkOrgByStatus(String orgId, String yearId,String termId,String statusId);
	
	/**
	 * 复制岗位
	 * @param workOrg
	 * @param listPosition
	 * @param schoolYearDic
	 * @param termDic
	 */
	public void  copyPosition(WorkOrgModel workOrg,List<SponsorPositionModel> positionList,Dic schoolYearDic,Dic termDic);
	
	
	/**
	 * 删除对象
	 * @param object
	 */
	public void delObject(BaseModel object);
	
	/**
	 * 根据用工部门id 查找岗位列表
	 * @param workOrgId
	 * @return
	 */
	public List<SponsorPositionModel>  queryPositionListByWorkOrgId(String workOrgId);
	
	/**
	 * 学生处审批 用工部门列表
	 * @param workOrg
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	
	public Page queryPageSetWorkApporve(WorkOrgModel workOrg,int pageNo,int pageSize,String userId,String[] objectIds);
	
	/**
	 * 学年 学期 所有的用工部门
	 * @param yearId
	 * @param termId
	 * @param statusId
	 * @return
	 */
	public List<WorkOrgModel> queryWorkOrgList(String yearId,String termId,String statusId);
}
