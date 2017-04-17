package com.uws.sponsor.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.uws.apw.service.IFlowInstanceService;
import com.uws.common.dao.ICommonRoleDao;
import com.uws.common.service.IRewardCommonService;
import com.uws.common.service.IStudentCommonService;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.StringUtils;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.AwardInfo;
import com.uws.domain.reward.AwardType;
import com.uws.domain.reward.CountryBurseInfo;
import com.uws.domain.reward.StudentApplyInfo;
import com.uws.domain.sponsor.ScheduleModel;
import com.uws.domain.sponsor.WorkApplyFileModel;
import com.uws.domain.sponsor.WorkApplyModel;
import com.uws.sponsor.dao.ISponsorScheduleDao;
import com.uws.sponsor.dao.IWorkStudyDao;
import com.uws.sponsor.service.ISponsorScheduleService;
import com.uws.sponsor.service.IWorkStudyService;
import com.uws.sponsor.util.Constants;
import com.uws.sys.model.Dic;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.Org;
/**
* 
* @Title: WorkStudyServiceImpl.java 
* @Package com.uws.sponsor.service.impl 
* @Description: 勤工助学service层实现
* @author zhangmx  
* @date 2015-8-10 下午14:41:53
*/
@Service("workStudyService")
public class WorkStudyServiceImpl extends BaseServiceImpl implements IWorkStudyService {
	@Autowired
	public IWorkStudyDao workStudyDao;
	//资助课表管理Service
	@Autowired
	private ISponsorScheduleService sponsorScheduleService;
	@Autowired
	private ISponsorScheduleDao sponsorScheduleDao;
	@Autowired
	private ICommonRoleDao commonRoleDao;
	//附件工具类
  	private FileUtil fileUtil=FileFactory.getFileUtil();
  	//审批 Service
  	@Autowired
  	private IFlowInstanceService flowInstanceService;
    //奖助评优
  	@Autowired
  	private IRewardCommonService rewardCommonService;
  	@Autowired
	private IStudentCommonService studentCommonService;
	/**
	 * 勤工助学列表
	 * @param pageNo
	 * @param pageSize
	 * @param workApply
	 * @return
	 */
	
	@Override
	public Page pageQueryWorkApply(WorkApplyModel workApply,
			int pageNo, int pageSize,String userId) {
	
		return this.workStudyDao.queryPageWorkStudy(workApply, pageNo, pageSize, userId);
	}
	/**
	 * 勤工助学查询列表
	 * @param pageNo
	 * @param pageSize
	 * @param workApply
	 * @return
	 */
	
	@Override
	public Page selectPageWorkApply(WorkApplyModel workApply,
			int pageNo, int pageSize,String userId,String orgId) {
	
		return this.workStudyDao.selectPageWorkStudy(workApply, pageNo, pageSize,userId, orgId);
	}
	
	/**
	 * 保存岗位申请
	 * @param workApply
	 */
	
	@Override
	public void saveWorkApply(WorkApplyModel workApply,WorkApplyFileModel workApplyFile) {
		workApply.setApplyFile(workApplyFile);
		workApply.setDelStatus(Constants.STATUS_NORMAL_DICS);
		this.workStudyDao.save(workApply);
		
	}
		

	/**
	 * 保存岗位申请单
	 * @param workApplyFile
	 */
	@Override
	public void saveWorkApplyFile(WorkApplyFileModel workApplyFile,String[] fileId) {

		workApplyFile.setDelStatus(Constants.STATUS_NORMAL_DICS);
		this.workStudyDao.save(workApplyFile);
		//上传的附件进行处理
		 if (ArrayUtils.isEmpty(fileId)) {
		       return;
		    }
		 for (String id : fileId)
			 this.fileUtil.updateFormalFileTempTag(id, workApplyFile.getWorkApplyFileId());
		
	}
	
	/**
	 * 更新岗位申请
	 * @param workApply
	 */
	@Override
	public void updateWorkApply(WorkApplyModel workApply){
		WorkApplyModel workApplyPo=this.queryWorkApplyById(workApply.getWorkApplyId());
		BeanUtils.copyProperties(workApply, workApplyPo, new String[]{"workApplyId","createTime","delStatus","processStatus"});
		this.workStudyDao.update(workApplyPo);
		
	}
	/**
	 * 更新岗位申请单
	 * @param workApplyFile
	 */
	@Override
	public void updateWorkApplyFile(WorkApplyFileModel workApplyFile,String[] fileId){

		WorkApplyFileModel workApplyFilePo=this.queryWorkApplyFileById(workApplyFile.getWorkApplyFileId());
		BeanUtils.copyProperties(workApplyFile,workApplyFilePo,new String[]{"workApplyFileId","createTime","delStatus"});
		this.workStudyDao.update(workApplyFilePo);
		
		 //上传的附件进行处理
		 if (ArrayUtils.isEmpty(fileId))
			 fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(workApplyFile.getWorkApplyFileId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId())){
		    	   this.fileUtil.deleteFormalFile(ufr);
		       }
		     }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, workApplyFilePo.getWorkApplyFileId());
		     }
	}
	/**
	 * 更新助学课表
	 * @param schedule
	 */
	@Override
	public void updateSchedule(String[] scheduleIds,WorkApplyFileModel workApplyFile){
		List<ScheduleModel> scheduleList = sponsorScheduleService.queryScheduleByApplyId(workApplyFile.getWorkApplyFileId());

		for(ScheduleModel s:scheduleList){
			this.workStudyDao.delete(s);
		}
		this.saveSponsorSchedule(workApplyFile, scheduleIds);
	}

	/**
	 * 根据id查找岗位申请
	 * @param id
	 * @return
	 */
	@Override
	public WorkApplyModel queryWorkApplyById(String workApplyId) {
		WorkApplyModel workApply=(WorkApplyModel) this.workStudyDao.get(WorkApplyModel.class, workApplyId);
		return workApply;
	}
	/**
	 * 根据id查找岗位申请单
	 * @param id
	 * @return
	 */
	@Override
	public WorkApplyFileModel queryWorkApplyFileById(String workApplyFileId) {
		WorkApplyFileModel workApplyFile=(WorkApplyFileModel) this.workStudyDao.get(WorkApplyFileModel.class, workApplyFileId);
		return workApplyFile;
	}
	
	
	
	/**
	 * 根据申请单号查找岗位
	 * @param workApplyFile
	 * @return
	 */
	@Override
	public WorkApplyModel queryWorkApplyByApplyFileId(
			String workApplyFileId) {
		WorkApplyModel workApplyPo=this.workStudyDao.queryWorkApplyByApplyFileId(workApplyFileId);
		return workApplyPo;
	}
	/**
	 * 保存或修改勤工助学
	 * @param currentStudentInfo
	 * @param workApplyIds
	 * @param positionIds
	 * @param flags
	 */
	@Override
	public void saveOrUpdateWorkStudy(WorkApplyModel workApply,WorkApplyFileModel workApplyFile,String[] fileId,
			String flags,String[] scheduleIds,String userId,String userNextId) {
		if(DataUtil.isNotNull(flags) && flags.equals("1")){
			workApply.setStatus(Constants.STATUS_SUBMIT_DICS);
			
			 
		}else{
			workApply.setStatus(Constants.STATUS_SAVE_DICS);//保存状态
			workApply.setDelStatus(Constants.STATUS_NORMAL_DICS);//删除状态为正常
		}
		
		if(StringUtils.hasText(workApply.getWorkApplyId()) && !"".equals(workApply.getWorkApplyId())){
			//更新
			
			//更新1.更新申请单
			this.updateWorkApplyFile(workApplyFile,fileId);
			//更新2.更新申请岗位
			this.updateWorkApply(workApply);
			//更新3.更新课表
			this.updateSchedule(scheduleIds, workApplyFile);
		}else{
			//新增
			
			//1.保存申请单
			this.saveWorkApplyFile(workApplyFile,fileId);
			//2.保存岗位申请
			this.saveWorkApply(workApply, workApplyFile);
			//3 处理课表安排
			saveSponsorSchedule(workApplyFile,scheduleIds);
			
			
			
		}
	}	
	
	
	/**
	 * 根据岗位id 和 确认状态查找困难生
	 * @param positionId
	 * @param postStatus
	 * @return
	 */
	public List<WorkApplyModel> queryDiffByPositionIdAndIsPost(String positionId,Dic postStatus){
		List<WorkApplyModel> list=this.workStudyDao.queryDiffByPositionIdAndIsPost( positionId, postStatus);
		return list;
	}
	/**
	 * 确认岗位
	 * @param workApplyPo
	 * @param workNumber
	 * @param workApplyConfirmList
	 */
	@Override
	public String confirmWork(WorkApplyModel workApplyPo,int workNumber,List<WorkApplyModel> workApplyConfirmList) {
		String isFull="";
		//确认人数为空或者未满的时候
		if(workApplyConfirmList==null || "".equals(workApplyConfirmList)||workApplyConfirmList.size()<workNumber){			  
			workApplyPo.setPostStatus(Constants.STATUS_IS_POST);
			workApplyPo.setConfirmDate(new Date());
			this.workStudyDao.update(workApplyPo);
			
			//根据学年、学期、困难生 状态查找出两个岗位
			List<WorkApplyModel> workApplyList=this.queryWorkApplyByAllStatus(workApplyPo.getApplyFile().getSchoolYear().getId(), 
					workApplyPo.getApplyFile().getTerm().getId(), workApplyPo.getDifficultStudentInfo().getId(),Constants.STATUS_SUBMIT_DICS.getId());
			if(2<=workApplyList.size()){
				//多个岗位---把另外的一个岗位变为不去
				for(WorkApplyModel w:workApplyList){
					if(!workApplyPo.getWorkApplyId().equals(w.getWorkApplyId())){
						//判断该岗位的审批流：是否已经结束 如果结束 改为确定不去 如果没结束 废弃该审批流
						if("PASS".equals(w.getProcessStatus())&& w.getPostStatus()==null){
							w.setPostStatus(Constants.STATUS_WASTE);//放弃岗位
						
						}else if(null==w.getProcessStatus()||"APPROVEING".equals(w.getProcessStatus())){
							//根据w.getWorkApplyId 找到其对应的审批流 把该流程废弃掉
							w.setNextApprover(null);
							w.setProcessStatus("ABANDONED");
							flowInstanceService.deprecatedCurProcess(w.getWorkApplyId(), true);
						}
						this.workStudyDao.update(w);
					}
				}
			 }
		}else{
			//该岗位人数已满
			 isFull="true";

		}
		return isFull;
		
	}
		
		
		
	
		
	
	
	/**
	 * 
	 * @Title: saveSponsorSchedule
	 * @Description: 勤工助学课表信息保存更新
	 * @param workApplyFile
	 * @param scheduleIds
	 * @throws
	 */
	public void saveSponsorSchedule(WorkApplyFileModel workApplyFile,String[] scheduleIds)
    {
	    if(null!=workApplyFile && !"".equals(workApplyFile.getWorkApplyFileId()))
	    {
			sponsorScheduleDao.delAllByApplyId(workApplyFile.getWorkApplyFileId());
			if(!ArrayUtils.isEmpty(scheduleIds)){
				ScheduleModel schedule = null;
				Dic dic = null;
				for(String idStr : scheduleIds)
				{	
					schedule = new ScheduleModel();
					String[] ids = idStr.split("_");
					schedule.setApplyFile(workApplyFile);
					dic = new Dic();
					dic.setId(ids[0]);
					schedule.setLessonDic(dic);
					dic = new Dic();
					dic.setId(ids[1]);
					schedule.setWeekendDic(dic);
					sponsorScheduleDao.save(schedule);	
				}
			}
	    }
    }
	
	/**
	 * 删除岗位申请同时删除申请单
	 * @param workApply
	 */
	@Override
	public void deleteWorkApply(WorkApplyModel workApply) {
		WorkApplyFileModel workApplyFile=workApply.getApplyFile();
		List<ScheduleModel>scheduleList=sponsorScheduleService.queryScheduleByApplyId(workApplyFile.getWorkApplyFileId());

		//删除岗位申请
		this.workStudyDao.delete(workApply);
		//删除助学课表
		for(ScheduleModel s:scheduleList){
			this.workStudyDao.delete(s);
		}
		//删除申请单
		this.workStudyDao.delete(workApplyFile);
	}

	/**
	 * 根据学年、学期、困难生、 状态 查找申请岗位
	 * @param schoolYearId
	 * @param termId
	 * @param difficultStudentInfo
	 * @return
	 */
	@Override
	public List<WorkApplyModel> queryWorkApplyByAllStatus(String schoolYearId,
			String termId, String difficultStudentInfoId,String statusId) {
		List<WorkApplyModel> list=this.workStudyDao.queryWorkApplyByAllStatus(schoolYearId, termId, difficultStudentInfoId,statusId);
		return list;
	}
	

	
	/**
	 * 查找要复制部门 的学生岗位申请单
	 * @param orgId
	 * @param schoolYearId
	 * @param termId
	 * @return
	 */
	@Override
	public List<WorkApplyModel> queryWorkApplyListByOrg(String orgId, String schoolYearId,
			String termId) {
		List<WorkApplyModel> list=(List<WorkApplyModel> )this.workStudyDao.query("from WorkApplyModel" +
				" where sponsorPosition.workOrg.org.id=? and applyFile.schoolYear.id=? " +
				"and applyFile.term.id=? and delStatus.id=? and processStatus=?", 
				new Object[]{orgId,schoolYearId,termId,Constants.STATUS_NORMAL_DICS.getId(),"PASS" });
		return list;
	}
	/**
	 * 复制功能
	 * @param teacherOrg
	 * @param workApplyList
	 * @param nextSchoolYearDic
	 * @param nextTermDic
	 */
	@Override
	public void copyWorkApply(Org teacherOrg,
			List<WorkApplyModel> workApplyList, Dic aimSchoolYearDic,
			Dic aimTermDic) {
		for(WorkApplyModel w:workApplyList){
			//复制申请单
			WorkApplyFileModel aimWorkApplyFile=new WorkApplyFileModel();
	    	BeanUtils.copyProperties(w.getApplyFile(), aimWorkApplyFile,new String[]{"workApplyFileId",
	    		"createTime","approveReason","approveStatus","delStatus"});
	    	aimWorkApplyFile.setSchoolYear(aimSchoolYearDic);
	    	aimWorkApplyFile.setTerm(aimTermDic);
	    	aimWorkApplyFile.setDelStatus(Constants.STATUS_NORMAL_DICS);
	    
	    	aimWorkApplyFile.setWorkApplyFileId(null);
	    	this.workStudyDao.save(aimWorkApplyFile);
	    	//复制申请岗位
	    	WorkApplyModel aimWorkApply=new WorkApplyModel();
	    	BeanUtils.copyProperties(w, aimWorkApply,new String[]{"workApplyId","status","delStatus","approveReason",
	    			"aimApprover","approveStatus","processStatus","postStatus","registerStatus","createTime","confirmDate","dismissDate"});
	    	
	    	aimWorkApply.setStatus(Constants.STATUS_SAVE_DICS);//状态为保存
	    	aimWorkApply.setDelStatus(Constants.STATUS_NORMAL_DICS);//删除状态为正常
	    	aimWorkApply.setWorkApplyId(null);
	    	this.saveWorkApply(aimWorkApply, aimWorkApplyFile);
		}
		
		
		
	}
	/**
	 * 勤工助学审批列表
	 * @param workApply
	 * @param pageNo
	 * @param pageSize
	 * @param orgId
	 * @return
	 */
	@Override
	public Page pageApproveWorkApply(WorkApplyModel workApply, int pageNo,
			int pageSize, String userId,String[] objectIds) {
		Page page=this.workStudyDao.pageApproveWorkApply(workApply, pageNo, pageSize, userId,objectIds);
		return page;
	}
	/**
	 * 勤工助学导出列表
	 * @param workApply
	 * @param pageNo
	 * @param pageSize
	 * @param orgId
	 * @return
	 */
	@Override
	public Page pageExportWorkApply(WorkApplyModel workApply, int pageNo,
			int pageSize, String orgId) {
		Page page=this.workStudyDao.pageExportWorkApply(workApply, pageNo, pageSize, orgId);
		return page;
	}
	/**
	 * 根据困难生、学年、学期来查找岗位申请
	 * @param diffStudentId
	 * @param yearId
	 * @param termId
	 * @param statusId
	 * @return
	 */
	@Override
	public WorkApplyModel queryUniqueWorkApplyByPass(
			String diffStudentId, String yearId, String termId, String statusId) {
		WorkApplyModel workApply=(WorkApplyModel)this.workStudyDao.queryUnique("from WorkApplyModel where difficultStudentInfo.id=? " +
				"and applyFile.schoolYear.id=? and  applyFile.term.id=? and  processStatus=?",
				new Object[]{diffStudentId,yearId,termId,statusId});
		return workApply;
	}

	/**
	 * 离岗
	 * @param workApply
	 */
	@Override
	public void dismissWorkApply(WorkApplyModel workApply) {
		workApply.setPostStatus(Constants.STATUS_DISMISS);
		workApply.setDismissDate(new Date());
		this.updateWorkApply(workApply);
	}
	
	/**
	 * 放弃岗位
	 * @param workApply
	 */
	@Override
	public void wasteWorkApply(WorkApplyModel workApply){
		workApply.setPostStatus(Constants.STATUS_WASTE);
		this.updateWorkApply(workApply);
	}
	
	/**
	 * 根据用工部门查询 岗位申请
	 * @param workOrgId
	 * @param yearId
	 * @param termId
	 * @return
	 */
	@Override
	public List<WorkApplyModel> queryWorkApplyByWorkOrgId(String workOrgId,String workYear,String workMonth,Dic schoolYear,Dic schoolTrem) {
		return this.workStudyDao.queryWorkApplyByWorkOrgId(workOrgId,workYear,workMonth,schoolYear,schoolTrem);
	}


	/**
	 * 根据学生id 获得其所有奖项
	 * @param stuId
	 * @param model
	 */
	@Override
	public void getAwardByStu(String stuId,ModelMap model) {
		StudentInfoModel studentInfo = studentCommonService.queryStudentById(stuId);
		List<CountryBurseInfo> listBurse= rewardCommonService.getStuBurseList(studentInfo);//奖学金信息
		List<StudentApplyInfo> listAward=rewardCommonService.getStuAwardList(studentInfo);
		if(listAward!=null &&listAward.size()>0){
			for(int i=0;i<listAward.size();i++){
				if(listAward.get(i).getAwardTypeId()!=null){
					AwardType awardType = rewardCommonService.getAwardTypeById(listAward.get(i).getAwardTypeId().getId());
					if(DataUtil.isNotNull(awardType.getSecondAwardName())) {
						listAward.get(i).setId(awardType.getSecondAwardName().getName());//存放在id中,方便自己页面获取
					}else{
						AwardInfo awardInfo = this.rewardCommonService.getAwardInfoById(awardType.getAwardInfoId().getId());
						listAward.get(i).setId(awardInfo.getAwardName());//存放在id中,方便自己页面获取
					}
				}
				
			}
		}
		
		model.addAttribute("listBurse", listBurse);
		model.addAttribute("listAward", listAward);
	}
	
	/**
	 * 判断当前用户角色是否指定的角色
	 * @param userId		用户id
	 * @param roleCode	角色编码
	 * @return
	 */
	@Override
	public boolean isRightRole(String userId, String roleCode) {
		return this.commonRoleDao.checkUserIsExist(userId, roleCode);
	}

	
	

	
	

	
	

}
