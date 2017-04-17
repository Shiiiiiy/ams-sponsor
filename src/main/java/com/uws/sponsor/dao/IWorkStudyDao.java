package com.uws.sponsor.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.WorkApplyModel;
import com.uws.sys.model.Dic;

/**
* 
* @Title: IWorkStudyDao.java 
* @Package com.uws.sponsor.dao
* @Description: 勤工助学dao层接口
* @author zhangmx  
* @date 2015-8-10 下午14:41:53
*/

public interface IWorkStudyDao extends IBaseDao {
	
	/**
	 * 勤工助学列表
	 * @param workApply
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	
	public Page queryPageWorkStudy(WorkApplyModel workApply,int pageNo,int pageSize,String userId);
	/**
	 * 勤工助学查询列表
	 * @param workApply
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	
	public Page selectPageWorkStudy(WorkApplyModel workApply,int pageNo,int pageSize,String userId,String orgId);
	
	/**
	 * 根据申请单号查找申请岗位
	 * @param workApplyFile
	 * @return
	 */
	public WorkApplyModel queryWorkApplyByApplyFileId(String workApplyFileId);
	
	/**
	 * 根据学年、学期、困难生(状态)查找申请岗位
	 * @param schoolYearId
	 * @param termId
	 * @param difficultStudentInfo
	 * @return
	 */
	public List<WorkApplyModel> queryWorkApplyByAllStatus(String schoolYearId,String termId,String difficultStudentInfoId,String statusId);
	
	
	
	/**
	 * 根据岗位id 和 确认岗位、未离岗查找困难生
	 * @param positionId
	 * @param confirmStatus
	 * @return
	 */
	public List<WorkApplyModel> queryDiffByPositionIdAndIsPost(String  positionId, Dic postStatus);
	/**
	 * 勤工助学审批列表
	 * @param workApply
	 * @param pageNo
	 * @param pageSize
	 * @param userId
	 * @return
	 */
	public Page pageApproveWorkApply(WorkApplyModel workApply, int pageNo,
			int pageSize,String userId,String[] objectIds);
	
	/**
	 * 勤工助学导出列表
	 * @param workApply
	 * @param pageNo
	 * @param pageSize
	 * @param orgId
	 * @return
	 */
	public Page pageExportWorkApply(WorkApplyModel workApply, int pageNo,
			int pageSize,String orgId);
	/**
	 * 根据用工部门查询 困难生
	 * @param workOrgId
	 * @param yearId
	 * @param termId
	 * @return
	 */
	public List<WorkApplyModel> queryWorkApplyByWorkOrgId(String workOrgId,String workYear,String workMonth,Dic schoolYear,Dic schoolTrem);

}
