package com.uws.sponsor.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.uws.common.util.SchoolYearUtil;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.sponsor.SponsorPositionModel;
import com.uws.domain.sponsor.WorkOrgModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sponsor.controller.SetWorkController;
import com.uws.sponsor.dao.ISetWorkDao;
import com.uws.sponsor.util.Constants;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.util.ProjectConstants;
/**
* 
* @Title: SetWorkDaoImpl.java 
* @Package com.uws.sponsor.dao.impl
* @Description: 岗位设置dao层实现
* @author zhangmx  
* @date 2015-7-31 下午14:41:53
*/
@Repository("setWorkDao")
public class SetWorkDaoImpl extends BaseDaoImpl implements ISetWorkDao{
	// 日志
    private Logger log = new LoggerFactory(SetWorkController.class);
    /**
	 * 数据字典工具类
	 */
	private static DicUtil dicUtil=DicFactory.getDicUtil();
	/**
	 * 用工部门列表
	 * @param workOrg
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	
	@Override
	public Page queryWorkOrgList(WorkOrgModel workOrg, int pageNo,int pageSize,String currentOrgId) {
		List<String> values=new ArrayList<String>();
		StringBuffer hql=new StringBuffer("from WorkOrgModel w where 1=1 ");
	
		hql.append("and w.delStatus.id =?");
		values.add(Constants.STATUS_NORMAL_DICS.getId());
		hql.append(" and w.org.id = ? ");
        values.add( currentOrgId);
		if(null!=workOrg)
		{
			//部门
			if(DataUtil.isNotNull(workOrg.getOrg())&& !"".equals(workOrg.getOrg())){
				if(workOrg.getOrg().getName()!=null && !"".equals(workOrg.getOrg().getName())){
					 hql.append(" and w.org.name like ? ");
			         values.add("%" + workOrg.getOrg().getName()+ "%");
				}
				if(workOrg.getOrg().getName()!=null && !"".equals(workOrg.getOrg().getName())){
					 hql.append(" and w.org.id = ? ");
			         values.add(workOrg.getOrg().getId());
				}
			}
			//学年
			if(DataUtil.isNotNull(workOrg.getSchoolYear())&& !"".equals(workOrg.getSchoolYear())){
				if(!"".equals(workOrg.getSchoolYear().getId())){
					hql.append(" and w.schoolYear.id = ?");
					values.add(workOrg.getSchoolYear().getId());
				}
			}
			//学期
			if(DataUtil.isNotNull(workOrg.getTerm())&& !"".equals(workOrg.getTerm())){
				if(!"".equals(workOrg.getTerm().getId())){
					hql.append(" and w.term.id = ?");
					values.add(workOrg.getTerm().getId());
				}
			}
			
		}
	
		
	
		
		
		hql.append(" order by w.schoolYear.id desc,w.term,w.org.id");
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
		}
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
		List<String> values=new ArrayList<String>();
		StringBuffer hql=new StringBuffer("from WorkOrgModel w where 1=1 ");
		hql.append("and w.delStatus.id =?");
		values.add(Constants.STATUS_NORMAL_DICS.getId());
		hql.append("and w.processStatus=?");
		values.add("PASS");
		if(null!=workOrg)
		{
			//部门
			if(DataUtil.isNotNull(workOrg.getOrg())&& !"".equals(workOrg.getOrg())){
				if(workOrg.getOrg().getName()!=null && !"".equals(workOrg.getOrg().getName())){
					 hql.append(" and w.org.name like ? ");
			         values.add("%" + workOrg.getOrg().getName()+ "%");
				}
				if(workOrg.getOrg().getName()!=null && !"".equals(workOrg.getOrg().getName())){
					 hql.append(" and w.org.id = ? ");
			         values.add(workOrg.getOrg().getId());
				}
			}
			//学年
			if(DataUtil.isNotNull(workOrg.getSchoolYear())&& !"".equals(workOrg.getSchoolYear())){
				if(!"".equals(workOrg.getSchoolYear().getId())){
					hql.append(" and w.schoolYear.id = ?");
					values.add(workOrg.getSchoolYear().getId());
				}
			}
			//学期
			if(DataUtil.isNotNull(workOrg.getTerm())&& !"".equals(workOrg.getTerm())){
				if(!"".equals(workOrg.getTerm().getId())){
					hql.append(" and w.term.id = ?");
					values.add(workOrg.getTerm().getId());
				}
			}
			
		}
		//判断是否为学生处的登录
		if(null!=currentOrgId && !ProjectConstants.STUDNET_OFFICE_ORG_ID.equals(currentOrgId))
		{
			 hql.append(" and w.org.id = ? ");
	         values.add( currentOrgId);
		}
		
		
		hql.append(" order by w.schoolYear.id desc,w.term,w.org.id");
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
		}
	}

	
	
	/**
	 * 根据学年、学期、部门 (状态)查找用工部门对象
	 * @param orgId
	 * @return
	 */
	@Override
	public WorkOrgModel queryWorkOrgByStatus(String orgId, String yearId,String termId,String statusId) {
		 List<Object> values = new ArrayList<Object>();
	     StringBuffer hql = new StringBuffer("from WorkOrgModel w where w.org.id=? and  " +
				"w.schoolYear.id=? and w.term.id=? and w.delStatus.id=?");
	     values.add(orgId);
	     values.add(yearId);
	     values.add(termId);
	     values.add(Constants.STATUS_NORMAL_DICS.getId());
	     if(DataUtil.isNotNull(statusId)){
	         if("PASS".equals(statusId)){
		    	 hql.append(" and  processStatus=?");
		    	 values.add(statusId);
		     }else{
		    	 hql.append(" and  status.id=?");
		    	 values.add(statusId); 
		     }
	     }
	     WorkOrgModel workOrg=(WorkOrgModel)this.queryUnique(hql.toString(), values.toArray());
	     return workOrg;
	}

	

	/**
	 * 描述信息: 处理删除的工作岗位信息
	 * @param workOrgId
	 * @param currentPositionIds
	 * @see com.uws.sponsor.dao.ISetWorkDao#deletePositionByOrgId(java.lang.String, java.lang.String[])
	 */
	@Override
    public void deletePositionByOrgId(String workOrgId, String[] currentPositionIds)
    {
	    if(StringUtils.hasText(workOrgId))
	    {
	    	Map<String,Object> map = new HashMap<String,Object>();
	    	//hql = "update SponsorPositionModel set delStatus =  " + dicUitl.getDeleteStauts();
	    	StringBuffer hql = new StringBuffer(" delete from SponsorPositionModel where workOrg.workOrgId = '");
	    	//StringBuffer hql = new StringBuffer(" update SponsorPositionModel set delStatus.id='"+Constants.STATUS_DELETED_DICS.getId()+"' where workOrg.workOrgId = '");

	    	hql.append(workOrgId);
	    	hql.append("' ");
	    	if(!ArrayUtils.isEmpty(currentPositionIds))
	    	{
	    		hql.append(" and positionId not in (:currentPositionIds)");
	    		map.put("currentPositionIds", currentPositionIds);
	    	}
	    	this.executeHql(hql.toString(), map);
	    	
	    }
    }


	
	/**
	 * 根据用工部门ID查找岗位列表
	 */
	@Override
	public List<SponsorPositionModel> queryPositionListByWorkOrgId(
			String workOrgId) {
		return (List<SponsorPositionModel> )this.query("from SponsorPositionModel where workOrg.workOrgId=? and delStatus.id=? ", new Object[] { workOrgId,Constants.STATUS_NORMAL_DICS.getId()});
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
		Map<String,Object> values = new HashMap<String,Object>();
		StringBuffer hql = new StringBuffer("select w from WorkOrgModel w where 1=1 and (w.nextApprover.id = :userId or w.workOrgId in (:objectIds)) ");
		values.put("userId",userId);
		values.put("objectIds",objectIds);

		//部门
		if(DataUtil.isNotNull(workOrg.getOrg())&& !"".equals(workOrg.getOrg())){
			if(workOrg.getOrg().getName()!=null && !"".equals(workOrg.getOrg().getName())){
				 hql.append(" and w.org.name like :orgName");
		         values.put("orgName","%" +HqlEscapeUtil.escape( workOrg.getOrg().getName())+ "%");
			}
			if(workOrg.getOrg().getName()!=null && !"".equals(workOrg.getOrg().getName())){
				 hql.append(" and w.org.id = :orgId ");
		         values.put("orgId",workOrg.getOrg().getId());
			}
		}
		//学年
		if(DataUtil.isNotNull(workOrg.getSchoolYear())&& !"".equals(workOrg.getSchoolYear())){
			if(!"".equals(workOrg.getSchoolYear().getId())){
				hql.append(" and w.schoolYear.id = :yearId");
				values.put("yearId",workOrg.getSchoolYear().getId());
			}
			
		}else{
			if(SchoolYearUtil.getYearDic()!=null){
				hql.append("and w.schoolYear.id = :schoolYearId");
				//获取当前学年字典。
		    	Dic nowYearDic=SchoolYearUtil.getYearDic();
				values.put("schoolYearId",nowYearDic.getId());
			}
			
		}
		//学期
		if(DataUtil.isNotNull(workOrg.getTerm())&& !"".equals(workOrg.getTerm())){
			if(!"".equals(workOrg.getTerm().getId())){
				hql.append(" and w.term.id = :termId");
				values.put("termId",workOrg.getTerm().getId());
			}
			
		}
		// 审核状态
		if (workOrg.getProcessStatus()!=null&&!"".equals(workOrg.getProcessStatus())) {
			if(ProjectConstants.CURRENT_APPROVE_USER_PROCESS_CODE.equals(workOrg.getProcessStatus()))
			{
				hql.append(" and w.nextApprover.id = :approveUserId ");
				values.put("approveUserId",userId);
			}
			else
			{
				hql.append(" and w.processStatus = :processStatus ");
				values.put("processStatus",workOrg.getProcessStatus());
			}
		}
		
		hql.append(" order by w.schoolYear desc");
		if(workOrg.getTerm()!=null&&!"".equals(workOrg.getTerm())){
			hql.append(",w.term desc");
		}
		if(DataUtil.isNotNull(workOrg.getOrg())&& !"".equals(workOrg.getOrg())){
			hql.append(",w.org.name");
		}
		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), values, pageSize, pageNo);
	}


	/**
	 * 请求所有的用工部门list
	 * @param yearId
	 * @param termId
	 * @param statusId
	 * @return
	 */
	@Override
	public List<WorkOrgModel> queryWorkOrgList(String yearId, String termId,
			String statusId) {
		 List<WorkOrgModel> list=( List<WorkOrgModel>)this.query("from WorkOrgModel w where w.schoolYear.id=? and w.term.id=? and w.delStatus.id=? and w.processStatus=?", 
				new Object[] {yearId,termId,Constants.STATUS_NORMAL_DICS.getId(),statusId});
		return list;
	}





	


	
	

}

	

	


