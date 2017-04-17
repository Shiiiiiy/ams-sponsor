package com.uws.sponsor.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.uws.core.base.BaseModel;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.sponsor.PositionListModel;
import com.uws.domain.sponsor.SponsorPositionModel;
import com.uws.domain.sponsor.WorkOrgModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sponsor.controller.SetWorkController;
import com.uws.sponsor.dao.ISetWorkDao;
import com.uws.sponsor.service.ISetWorkService;
import com.uws.sponsor.util.Constants;
import com.uws.sys.model.Dic;
/**
* 
* @Title: SetWorkServiceImpl.java 
* @Package com.uws.sponsor.service.impl 
* @Description: 岗位设置service层实现
* @author zhangmx  
* @date 2015-7-31 下午14:41:53
*/
@Service("setWorkService")
public class SetWorkServiceImpl extends BaseServiceImpl implements ISetWorkService {
	@Autowired
	public ISetWorkDao setWorkDao;
	
	// 日志
    private Logger log = new LoggerFactory(SetWorkController.class);
	/**
	 * 用工部门列表
	 * @param sponsorPosition
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@Override
	public Page queryPageWorkOrg(WorkOrgModel workOrg, int pageNo,int pageSize,String currentOrgId) {
		Page page=this.setWorkDao.queryWorkOrgList(workOrg, pageNo, pageSize, currentOrgId);
		return page;
	}
	/**
	 * 查询--用工部门列表
	 * @param sponsorPosition
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@Override
	public Page selectPageWorkOrg(WorkOrgModel workOrg, int pageNo,
			int pageSize, String currentOrgId) {
		Page page=this.setWorkDao.selectPageWorkOrg(workOrg, pageNo, pageSize, currentOrgId);
		return page;
	}

	/**
	 * 保存用工部门
	 * @param workOrg
	 */
	@Override
	public void saveWorkOrg(WorkOrgModel workOrg) {
		workOrg.setDelStatus(Constants.STATUS_NORMAL_DICS);
		this.setWorkDao.save(workOrg);
	}
	/**
	 * 更新用工部门
	 * @param workOrg
	 */
	@Override
	public void updateWorkOrg(WorkOrgModel workOrg) {

		 WorkOrgModel workOrgPo = this.queryWorkOrgById(workOrg.getWorkOrgId());
		 BeanUtils.copyProperties(workOrg,workOrgPo,new String[]{"workOrgId","createTime","delStatus","processStatus"});
		 this.setWorkDao.update(workOrgPo);
		
	}
	/**
	 * 更新岗位
	 * @param sponsorPosition
	 */
	@Override
	public void updateSponsorPosition(SponsorPositionModel sponsorPosition) {
		SponsorPositionModel positionPo = this.querySponsorPositionById(sponsorPosition.getPositionId());
		 BeanUtils.copyProperties(sponsorPosition,positionPo,new String[]{"positionId","createTime","delStatus"});
		
		 this.setWorkDao.update(positionPo);
		
	}
	/**
	 * 保存岗位
	 * @param sponsorPosition
	 */
	@Override
	public void saveSponsorPosition(SponsorPositionModel sponsorPosition) {
		sponsorPosition.setDelStatus(Constants.STATUS_NORMAL_DICS);
		this.setWorkDao.save(sponsorPosition);
	}
	/**
	 * 根据id查找岗位
	 * @param id
	 * @return
	 */
	@Override
	public SponsorPositionModel querySponsorPositionById(String id) {
		SponsorPositionModel sponsorPosition=(SponsorPositionModel) this.setWorkDao.get(SponsorPositionModel.class, id);
		
		return sponsorPosition;
	}
	
	
	/**
	 * 根据部门的id查找岗位列表
	 * @param workOrgId
	 * @return
	 */
	@Override
	public List<SponsorPositionModel> queryPositionList(String orgId,String schoolYearId,String termId,String statusId) {
		
		WorkOrgModel workOrg=this.queryWorkOrgByStatus(orgId, schoolYearId, termId,statusId);
		if(workOrg==null){
			return null;
		}
		List<SponsorPositionModel> positionList=this.setWorkDao.queryPositionListByWorkOrgId(workOrg.getWorkOrgId());
		return positionList;
	}
	/**
	 *  根据用工部门的id查找用工部门
	 * @param workOrgId
	 * @return
	 */
	@Override
	public WorkOrgModel queryWorkOrgById(String workOrgId) {
		WorkOrgModel workOrgPo=(WorkOrgModel) this.setWorkDao.get(WorkOrgModel.class, workOrgId);
	
		return workOrgPo;
	}



	@Override
	public void saveOrUpdate(WorkOrgModel workOrg,PositionListModel positionListModel, String[] currentIds,String flags) {
		
		/*
		 * 1、 判断 workOrg 有没有Id 有更新，没有保存  
		 * 
		 * 2、 如果workOrg有  ID   根据 Id 和  currentIds 更新Position 的id没有在currentIds里的数据 的状态为deletstatus
		 * 
		 * 3、for positionList 判断 是否null 和 有没有positionId ，有 ID update， 没有Id insert
		 */
		
		if(null!=workOrg && !"".equals(workOrg))
		{
			log.info("部门id"+workOrg.getWorkOrgId());
			log.info(""+currentIds);
			
			if(DataUtil.isNotNull(flags) && flags.equals("1")){
				workOrg.setStatus(Constants.STATUS_SUBMIT_DICS);
			}else{
				workOrg.setStatus(Constants.STATUS_SAVE_DICS);//保存状态
			}
			// 1、 判断 workOrg 有没有Id 有更新，没有保存  
			if(workOrg.getWorkOrgId() != null && !"".equals(workOrg.getWorkOrgId())){
				// 更新
				
				this.updateWorkOrg(workOrg);
			}else{
				//保存
				this.saveWorkOrg(workOrg);
			}
				
			
			//2 如果workOrg有  ID   根据 Id 和  currentIds 更新Position 的id没有在currentIds里的数据 的状态为deletstatus
			
			setWorkDao.deletePositionByOrgId(workOrg.getWorkOrgId(),currentIds);
			
			//3、for positionList 判断 是否null 和 有没有positionId ，有 ID update， 没有Id insert
			
			if(null!=positionListModel)
			{
				List<SponsorPositionModel> positionList = positionListModel.getPositionList();
				if(positionList!=null && !"".equals(positionList)){
					for(SponsorPositionModel position : positionList)
					{
						if(null!=position && StringUtils.hasText(position.getWorkName()))
						{
							position.setWorkOrg(workOrg);
							if(StringUtils.hasText(position.getPositionId())){
								this.updateSponsorPosition(position);
							}
							else
							{ 
								position.setWorkOrg(workOrg);
								this.saveSponsorPosition(position);
							}
						}
					}
				}
			}	
		

			
		}
		
	}
	
	/**
	 * 根据学年\学期 部门查找用工部门
	 * @param orgId
	 * @param yearId
	 * @param termId
	 * @return
	 */
	@Override
	public WorkOrgModel queryWorkOrgByStatus(String orgId, String yearId,String termId,String statusId) {
		
		return this.setWorkDao.queryWorkOrgByStatus(orgId,yearId,termId,statusId);
	}


	/**
	 * 根据学年\学期 部门复制岗位
	 * @param orgId
	 * @param yearId
	 * @param termId
	 * @return
	 */
	@Override
	public void copyPosition(WorkOrgModel sourceWorkOrg,
		List<SponsorPositionModel> positionList,Dic aimSchoolYearDic,Dic aimTermDic ) {
		//1.复制用工部门信息
		WorkOrgModel aimWorkOrg=new  WorkOrgModel();
    	BeanUtils.copyProperties(sourceWorkOrg, aimWorkOrg,new String[]{"workOrgId","createTime","status","delStatus","nextApprover","approveStatus","processStatus","approveReason"});
    	aimWorkOrg.setSchoolYear(aimSchoolYearDic);
		aimWorkOrg.setTerm(aimTermDic);
		aimWorkOrg.setStatus(Constants.STATUS_SAVE_DICS);
		aimWorkOrg.setWorkOrgId(null);
		this.saveWorkOrg(aimWorkOrg);//保存时候有设置删除状态
		//2.复制岗位信息
    	for(SponsorPositionModel sourcePosition:positionList){
    		SponsorPositionModel aimPosition=new SponsorPositionModel();
        	BeanUtils.copyProperties(sourcePosition, aimPosition,new String[]{"positionId","createTime","delStatus"});
        	
        	aimPosition.setWorkOrg(aimWorkOrg);
        	aimPosition.setPositionId(null);
    		this.saveSponsorPosition(aimPosition);
    		
    	}
		
		
	}
	
	
	/**
	 * 根据用工部门id 查找岗位列表
	 * @param workOrgId
	 * @return
	 */
	@Override
	public List<SponsorPositionModel> queryPositionListByWorkOrgId(
			String workOrgId) {
		List<SponsorPositionModel> positionList=this.setWorkDao.queryPositionListByWorkOrgId(workOrgId);
		return positionList;
	}
	
	/**
	 * 审批 用工部门列表
	 * @param workOrg
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	
	@Override
	public Page queryPageSetWorkApporve(WorkOrgModel workOrg,
			int pageNo, int pageSize,String userId,String[] objectIds) {
		Page page=this.setWorkDao.queryPageSetWorkApporve(workOrg, pageNo, pageSize, userId, objectIds);
		return page;
	}
	/**
	 * 删除对象
	 */
	@Override
	public void delObject(BaseModel object) {
		this.setWorkDao.delete(object);
		
	}
	/**
	 * 学年 学期 所有的用工部门
	 * @param yearId
	 * @param termId
	 * @param statusId
	 * @return
	 */
	@Override
	public List<WorkOrgModel> queryWorkOrgList(String yearId, String termId,
			String statusId) {
		List<WorkOrgModel> workOrgList=this.setWorkDao.queryWorkOrgList( yearId,  termId, statusId);
		return workOrgList;
	}


}
