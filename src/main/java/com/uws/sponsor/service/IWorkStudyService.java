package com.uws.sponsor.service;

import java.util.List;

import org.springframework.ui.ModelMap;

import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.WorkApplyFileModel;
import com.uws.domain.sponsor.WorkApplyModel;
import com.uws.sys.model.Dic;
import com.uws.user.model.Org;

/**
* 
* @Title: IWorkStudyService.java 
* @Package com.uws.sponsor.service 
* @Description: 勤工助学service接口层
* @author zhangmx  
* @date 2015-8-10 下午14:41:53
*/
public interface IWorkStudyService extends IBaseService {
	/**
	 * 勤工助学列表
	 * @param pageNo
	 * @param pageSize
	 * @param workApply
	 * @return
	 */
	
	public Page pageQueryWorkApply(WorkApplyModel workApply,int pageNo, int pageSize,String userId);
	
	/**
	 * 勤工助学查询列表
	 * @param pageNo
	 * @param pageSize
	 * @param workApply
	 * @return
	 */
	
	public Page selectPageWorkApply(WorkApplyModel workApply,int pageNo, int pageSize,String userId,String orgId);
	
	/**
	 * 保存岗位申请
	 * @param workApply
	 */
	public void saveWorkApply(WorkApplyModel workApply,WorkApplyFileModel workApplyFile);
	/**
	 * 保存岗位申请单
	 * @param workApplyFile
	 */
	public void saveWorkApplyFile(WorkApplyFileModel workApplyFile,String[] fileId);
	/**
	 * 保存课表
	 * @param workApplyFile
	 * @param scheduleIds
	 */
	public void saveSponsorSchedule(WorkApplyFileModel workApplyFile,String[] scheduleIds);
	
	/**
	 * 根据id查找岗位申请
	 * @param id
	 * @return
	 */
	public WorkApplyModel queryWorkApplyById(String workApplyId);
	/**
	 * 根据id查找岗位申请单
	 * @param id
	 * @return
	 */
	public WorkApplyFileModel queryWorkApplyFileById(String workApplyFileId );
	
	/**
	 * 更新岗位申请
	 * @param workApply
	 */
	public void updateWorkApply(WorkApplyModel workApply);
	/**
	 * 更新岗位申请单
	 * @param workApplyFile
	 */
	public void updateWorkApplyFile(WorkApplyFileModel workApplyFile,String[] fileId);
	/**
	 * 更新助学课表
	 * @param schedule
	 */
	public void updateSchedule(String[] scheduleIds,WorkApplyFileModel workApplyFile);
	/**
	 * 根据申请号查找申请岗位
	 * @param workApplyFile
	 * @return
	 */
	public WorkApplyModel queryWorkApplyByApplyFileId(String workApplyFileId);
	
	/**
	 * 保存或修改勤工助学
	 * @param currentStudentInfo
	 * @param workApplyIds
	 * @param positionIds
	 * @param flags
	 */
	public void saveOrUpdateWorkStudy(WorkApplyModel workApply,WorkApplyFileModel workApplyFile,String[] fileId,
		String flags,String[] scheduleIds,String userId,String userNextId);
	/**
	 * 删除岗位申请
	 * @param workApply
	 */
	public void deleteWorkApply(WorkApplyModel workApply);	
	
	/**
	 * 根据学年、学期、困难生 、状态 查找申请岗位
	 * @param schoolYearId
	 * @param termId
	 * @param difficultStudentInfo
	 * @return
	 */
	public List<WorkApplyModel> queryWorkApplyByAllStatus(String schoolYearId,String termId,String difficultStudentInfoId,String statusId);
	
	/**
	 * 根据岗位id 和 确认状态查找困难生
	 * @param positionId
	 * @param postStatus
	 * @return
	 */
	public List<WorkApplyModel> queryDiffByPositionIdAndIsPost(String positionId,Dic postStatus);
	
	/**
	 * 确认岗位
	 * @param workApplyPo
	 * @param workNumber
	 * @param workApplyConfirmList
	 */
	
	public String confirmWork(WorkApplyModel workApplyPo,int workNumber,List<WorkApplyModel> workApplyConfirmList);
	
	/**
	 * 查找要复制部门 的学生岗位申请单
	 * @param orgId
	 * @param schoolYearId
	 * @param termId
	 * @return
	 */
	public List<WorkApplyModel> queryWorkApplyListByOrg(String orgId,String schoolYearId,String termId);
	/**
	 * 复制功能
	 * @param teacherOrg
	 * @param workApplyList
	 * @param nextSchoolYearDic
	 * @param nextTermDic
	 */
	public void copyWorkApply (Org teacherOrg,List<WorkApplyModel> workApplyList,Dic nextSchoolYearDic,Dic nextTermDic);
	/**
	 * 勤工助学审批列表
	 * @param workApply
	 * @param pageNo
	 * @param pageSize
	 * @param orgId
	 * @return
	 */
	public Page pageApproveWorkApply(WorkApplyModel workApply, int pageNo,int pageSize,String userId,String[] objectIds);
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
	 * 根据困难生、学年、学期来查找岗位申请
	 * @param diffStudentId
	 * @param yearId
	 * @param termId
	 * @param statusId
	 * @return
	 */
	public WorkApplyModel queryUniqueWorkApplyByPass(String diffStudentId,String yearId,String termId,String statusId);
	
	/**
	 * 申请单--离岗
	 * @param workApply
	 */
	public void dismissWorkApply(WorkApplyModel workApply);
	/**
	 * 放弃岗位
	 * @param workApply
	 */
	public void wasteWorkApply(WorkApplyModel workApply);
	
	
	/**
	 * 根据用工部门查询 困难生
	 * @param workOrgId
	 * @param yearId
	 * @param termId
	 * @return
	 */
	public List<WorkApplyModel> queryWorkApplyByWorkOrgId(String workOrgId,String workYear,String workMonth,Dic schoolYear,Dic schoolTrem);
	
	
	/**
	 *  根据学生id 获得其所有奖项
	 * @param stuId
	 * @param model
	 */
	public void getAwardByStu(String stuId,ModelMap model);
	/**
	 * 判断当前用户角色是否指定的角色
	 * @param userId		用户id
	 * @param roleCode	角色编码
	 * @return
	 */
	public boolean isRightRole(String userId,String roleCode);
}
