package com.uws.sponsor.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.FlowHistoryPo;
import com.uws.apw.service.IFlowInstanceService;
import com.uws.apw.util.JsonUtils;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.service.IExcelService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.StringUtils;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.sponsor.DifficultStudentInfo;
import com.uws.domain.sponsor.ScheduleModel;
import com.uws.domain.sponsor.SponsorPositionModel;
import com.uws.domain.sponsor.WorkApplyFileModel;
import com.uws.domain.sponsor.WorkApplyModel;
import com.uws.domain.sponsor.WorkOrgModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sponsor.service.IDifficultyStudentService;
import com.uws.sponsor.service.ISetWorkService;
import com.uws.sponsor.service.ISponsorScheduleService;
import com.uws.sponsor.service.IWorkStudyService;
import com.uws.sponsor.util.Constants;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;
import com.uws.user.service.IOrgService;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;
/**
* 
* @Title: WorkStudyController.java 
* @Package com.uws.sponsor.controller 
* @Description: 勤工助学controller
* @author zhangmx  
* @date 2015-8-10 下午14:41:53
*/
@Controller
public class WorkStudyController extends BaseController{
	@Autowired
	public IWorkStudyService workStudyService;
	@Autowired
	public ISetWorkService SetWorkService;
	//困难学生service
	@Autowired
	private IDifficultyStudentService difficultyStudentService;
	//资助课表管理Service
	@Autowired
	private ISponsorScheduleService sponsorScheduleService;
	//审批 Service
	@Autowired
	private IFlowInstanceService flowInstanceService;
	@Autowired
	private IStudentCommonService studentCommonService;
	@Autowired
	private IExcelService excelService;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICompService compService;
	//获取岗位
	@Autowired
	public ISetWorkService setWorkService;
	
	// 日志
    private Logger log = new LoggerFactory(WorkStudyController.class);
    // sessionUtil工具类
  	private SessionUtil sessionUtil = SessionFactory.getSession(Constants.MENUKEY_WORK_STUDY);
    //数据字典工具类
    private DicUtil dicUtil = DicFactory.getDicUtil();
    //附件工具类
  	private FileUtil fileUtil=FileFactory.getFileUtil();
  	@Autowired
	private IOrgService orgService;
    
   /**
    * 勤工助学岗位列表
    * @param model
    * @param request
    * @param workApply
    * @return
    */
	@RequestMapping(value={"/sponsor/workStudy/opt-query/queryWorkStudyList"})
    public String listWorkStudy(ModelMap model,HttpServletRequest request,WorkApplyModel workApply,HttpSession session){
		log.info("勤工助学-----勤工助学列表");
		//根据当前登录人获取学生对象的信息
		String userId = sessionUtil.getCurrentUserId();
		
		//获取当前用户
		DifficultStudentInfo currentStudentInfo=null;
		if(com.uws.core.util.StringUtils.hasText(userId)){
			currentStudentInfo = difficultyStudentService.queryDifficultStudentByStudentId(userId);
			if(currentStudentInfo!=null){
				session.setAttribute("currentStudentInfo",currentStudentInfo);
			}
		}

		//保存查询条件
		sessionUtil.setSessionAttribute("workApply", workApply);
		
		//判断是否可以申请、第几次申请
		canApply(currentStudentInfo, model);
		
		//默认=当前学年 默认=当前学期--当学期不为空的时候
		if( null==workApply.getApplyFile() ){
			WorkApplyFileModel workApplyFile=new WorkApplyFileModel();
			workApplyFile.setSchoolYear(SchoolYearUtil.getYearDic());
			workApply.setApplyFile(workApplyFile);
			if(null!=SchoolYearUtil.getCurrentTermDic()){
				workApplyFile.setTerm(SchoolYearUtil.getCurrentTermDic());
				workApply.setApplyFile(workApplyFile);
			}
		}else if(null==workApply.getApplyFile().getSchoolYear()){
			workApply.getApplyFile().setSchoolYear(SchoolYearUtil.getYearDic());
			workApply.setApplyFile(workApply.getApplyFile());
		}else if(null==workApply.getApplyFile().getTerm()){
			if(null!=SchoolYearUtil.getCurrentTermDic()){
				WorkApplyFileModel workApplyFile=new WorkApplyFileModel();
				workApplyFile.setTerm(SchoolYearUtil.getCurrentTermDic());
				workApply.setApplyFile(workApplyFile);
			}
		}
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.workStudyService.pageQueryWorkApply(workApply,pageNo, Page.DEFAULT_PAGE_SIZE, userId);
		
		//学院
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	//专业
    	List<BaseMajorModel> majorList = null;
    	//班级
    	List<BaseClassModel> classList = null;
    	
    	if(workApply!= null&& !"".equals(workApply) && workApply.getDifficultStudentInfo()!=null && !"".equals(workApply.getDifficultStudentInfo())){
    		if(workApply.getDifficultStudentInfo().getStudent().getCollege()!=null &&
        		StringUtils.hasText(workApply.getDifficultStudentInfo().getStudent().getCollege().getId())){
        		majorList = compService.queryMajorByCollage(workApply.getDifficultStudentInfo().getStudent().getCollege().getId());
        	}
        	if(workApply.getDifficultStudentInfo().getStudent().getMajor()!=null &&
        			StringUtils.hasText(workApply.getDifficultStudentInfo().getStudent().getMajor().getId())){
        		classList = compService.queryClassByMajor(workApply.getDifficultStudentInfo().getStudent().getMajor().getId());
        	}
    	}
    	
    	model.addAttribute("page", page);
		model.addAttribute("workApply", workApply);
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
		model.addAttribute("statusList",dicUtil.getDicInfoList("STATUS"));
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());

		return Constants.MENUKEY_WORK_STUDY+"/workStudyList";
    }
   /**
    * 勤工助学查询
    * @param model
    * @param request
    * @param workApply
    * @return
    */
	@RequestMapping(value={"/sponsor/selectWorkStudy/opt-query/selectWorkStudyList"})
    public String selectWorkStudyList(ModelMap model,HttpServletRequest request,WorkApplyModel workApply,HttpSession session){
		log.info("勤工助学-----勤工助学查询");
		//根据当前登录人获取学生对象的信息
		String userId = sessionUtil.getCurrentUserId();
		//获取当前用户所在的部门Id
		String teacherOrgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		
		//保存查询条件
		sessionUtil.setSessionAttribute("workApply", workApply);
		model.addAttribute("workApply", workApply);
		//默认=当前学年 默认=当前学期--当学期不为空的时候
		if( null==workApply.getApplyFile() ){
			WorkApplyFileModel workApplyFile=new WorkApplyFileModel();
			workApplyFile.setSchoolYear(SchoolYearUtil.getYearDic());
			workApply.setApplyFile(workApplyFile);
			if(null!=SchoolYearUtil.getCurrentTermDic()){
				workApplyFile.setTerm(SchoolYearUtil.getCurrentTermDic());
				workApply.setApplyFile(workApplyFile);
			}
		}else if(null==workApply.getApplyFile().getSchoolYear()){
			workApply.getApplyFile().setSchoolYear(SchoolYearUtil.getYearDic());
			workApply.setApplyFile(workApply.getApplyFile());
		}else if(null==workApply.getApplyFile().getTerm()){
			if(null!=SchoolYearUtil.getCurrentTermDic()){
				WorkApplyFileModel workApplyFile=new WorkApplyFileModel();
				workApplyFile.setTerm(SchoolYearUtil.getCurrentTermDic());
				workApply.setApplyFile(workApplyFile);
			}
		}
		//默认在岗岗位
	//	if( null==workApply.getPostStatus() ){
	//		workApply.setPostStatus(Constants.STATUS_IS_POST);
	//	}
		
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.workStudyService.selectPageWorkApply(workApply,pageNo, Page.DEFAULT_PAGE_SIZE, userId,teacherOrgId);
		//学院
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	//专业
    	List<BaseMajorModel> majorList = null;
    	//班级
    	List<BaseClassModel> classList = null;
    	if(workApply!= null&& !"".equals(workApply) && workApply.getDifficultStudentInfo()!=null && !"".equals(workApply.getDifficultStudentInfo())){
    		if(workApply.getDifficultStudentInfo().getStudent().getCollege()!=null &&
        		StringUtils.hasText(workApply.getDifficultStudentInfo().getStudent().getCollege().getId())){
        		majorList = compService.queryMajorByCollage(workApply.getDifficultStudentInfo().getStudent().getCollege().getId());
        	}
        	if(workApply.getDifficultStudentInfo().getStudent().getMajor()!=null &&
        			StringUtils.hasText(workApply.getDifficultStudentInfo().getStudent().getMajor().getId())){
        		classList = compService.queryClassByMajor(workApply.getDifficultStudentInfo().getStudent().getMajor().getId());
        	}
    	}
    	model.addAttribute("postList", dicUtil.getDicInfoList("POST_TYPE"));
    	model.addAttribute("page", page);
		model.addAttribute("workApply", workApply);
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
		model.addAttribute("statusList",dicUtil.getDicInfoList("STATUS"));
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());
		 //判断是否为学校领导
		boolean isSchoolHeader=this.workStudyService.isRightRole(userId, "HKY_SCHOOL_LEADER");
		model.addAttribute("isSchoolHeader", isSchoolHeader);
		//学生处的组织机构
		model.addAttribute("studentOfficId", ProjectConstants.STUDNET_OFFICE_ORG_ID);
		if(StringUtils.hasText(teacherOrgId))
			model.addAttribute("userOrg",orgService.queryOrgById(teacherOrgId));
		return Constants.MENUKEY_WORK_STUDY+"/selectWorkStudyList";
    }

	/**
	 * 判断是否可以申请
	 * @param currentStudentInfo
	 * @param model
	 */
	public void canApply(DifficultStudentInfo currentStudentInfo,ModelMap model){
		String isDifficult="do";
		String score="do";
		String selectTwoPosition="do";
		String notSurePosition="do";
		String termStr="do";
		if(SchoolYearUtil.getCurrentTermDic()!=null){
			if(currentStudentInfo!=null){
				//上学期补考不及格 不能申请
				List<WorkApplyModel> workApplyList=this.workStudyService.queryWorkApplyByAllStatus(
							SchoolYearUtil.getYearDic().getId(), SchoolYearUtil.getCurrentTermDic().getId(), currentStudentInfo.getId(),Constants.STATUS_SUBMIT_DICS.getId());
				if(workApplyList!=null && workApplyList.size()>0){
					
					//已经有在岗岗位的 不能申请
					for(WorkApplyModel w:workApplyList){
						if(w.getPostStatus()!=null && Constants.STATUS_IS_POST.getId().equals(w.getPostStatus().getId()))
							notSurePosition="notDo";
					}
				}
				
				List<WorkApplyModel>  workApplyList1=this.workStudyService.queryWorkApplyByAllStatus(
						SchoolYearUtil.getYearDic().getId(), SchoolYearUtil.getCurrentTermDic().getId(), currentStudentInfo.getId(),Constants.STATUS_SAVE_DICS.getId());//保存的数量
				List<WorkApplyModel>  workApplyList2=this.workStudyService.queryWorkApplyByAllStatus(
						SchoolYearUtil.getYearDic().getId(), SchoolYearUtil.getCurrentTermDic().getId(), currentStudentInfo.getId(),Constants.STATUS_SUBMIT_DICS.getId());//提交的数量
				List<WorkApplyModel>  workApplyList3=this.workStudyService.queryWorkApplyByAllStatus(
						SchoolYearUtil.getYearDic().getId(), SchoolYearUtil.getCurrentTermDic().getId(), currentStudentInfo.getId(),Constants.STATUS_DISMISS.getId());//离岗的数量
				List<WorkApplyModel>  workApplyList4=this.workStudyService.queryWorkApplyByAllStatus(
						SchoolYearUtil.getYearDic().getId(), SchoolYearUtil.getCurrentTermDic().getId(), currentStudentInfo.getId(),Constants.STATUS_WASTE.getId());//放弃岗的数量
				List<WorkApplyModel>  workApplyList5=this.workStudyService.queryWorkApplyByAllStatus(
						SchoolYearUtil.getYearDic().getId(), SchoolYearUtil.getCurrentTermDic().getId(), currentStudentInfo.getId(),"ABANDONED");//流程废止
				int number=workApplyList1.size()+workApplyList2.size()-workApplyList3.size()-workApplyList4.size()-workApplyList5.size();
				//已经选择过两次的 不能申请			
				if(number>=2){
					selectTwoPosition="notDo";
				}
			
			}else{
				//该学生不是困难生不能申请
				isDifficult="notDo";
			}
		}else{
			termStr="notDo";
		}
		
		model.addAttribute("isDifficult", isDifficult);
    	model.addAttribute("score", score);
    	model.addAttribute("selectTwoPosition", selectTwoPosition);
    	model.addAttribute("notSurePosition", notSurePosition);
    	model.addAttribute("termStr", termStr);
	}
    /**
     * 新增/编辑 勤工助学
     * @param model
     * @param request
     * @param workApply
     * @return
     */
	@RequestMapping(value={"/sponsor/workStudy/opt-add/editWorkStudy","/sponsor/workStudy/opt-update/editWorkStudy"})
	public String  editWorkStudy(ModelMap model,HttpServletRequest request,WorkApplyModel workApply,HttpSession session){
		log.info("勤工助学-----新增/编辑 勤工助学");
		Map<String,String> weekMap = new HashMap<String,String>();
		String userId = sessionUtil.getCurrentUserId();
		String workApplyId=request.getParameter("workApplyId");
		DifficultStudentInfo currentStudentInfo=null;
		//根据当前登录人获取学生对象的信息
		if(com.uws.core.util.StringUtils.hasText(userId)){
			 currentStudentInfo = difficultyStudentService.queryDifficultStudentByStudentId(userId);
			if(currentStudentInfo!=null){
				session.setAttribute("currentStudentInfo",currentStudentInfo);
				model.addAttribute("currentStudentInfo",currentStudentInfo);
			}
		}else{
			model.addAttribute("currentStudentInfo",new DifficultStudentInfo());
		}

		//根据当前登录人获取学生的上学期成绩及综合表现？？ 
		//根据当前登录人获取学生的曾经获得的奖助和奖学金情况？？
		if(com.uws.core.util.StringUtils.hasText(userId)){
			this.workStudyService.getAwardByStu(userId,model);
			
		}
		
		if(StringUtils.hasText(workApplyId)){
			//跳转到修改页面
			//1.根据传入的 岗位申请ID 查找到 岗位申请
			WorkApplyModel workApplyPo=this.workStudyService.queryWorkApplyById(workApplyId);
			//2.根据 岗位申请 查找到唯一对应的 岗位申请单
			WorkApplyFileModel workApplyFilePo=workApplyPo.getApplyFile();
			
			//3.根据岗位申请单的主键 查找到对应的助学课表
			//获得课表列表 并处理
			List<ScheduleModel> scheduleList = sponsorScheduleService.queryScheduleByApplyId(workApplyFilePo.getWorkApplyFileId());

			if(!CollectionUtils.isEmpty(scheduleList))
			{
				for(ScheduleModel schedule : scheduleList)
				{
					weekMap.put(schedule.getWeekendDic().getId()+"_"+schedule.getLessonDic().getId(),"true");
				}
			}
			model.addAttribute("workApply", workApplyPo);
			model.addAttribute("workApplyFile", workApplyFilePo);
			List<UploadFileRef> fileList=this.fileUtil.getFileRefsByObjectId(workApplyFilePo.getWorkApplyFileId());
			model.addAttribute("uploadFileRefList", fileList);

		}
		
		//当前学年学期的用工列表
		if(SchoolYearUtil.getCurrentTermDic()!=null){
			List<WorkOrgModel> workOrgList=setWorkService.queryWorkOrgList(SchoolYearUtil.getYearDic().getId(),
					SchoolYearUtil.getCurrentTermDic().getId(),"PASS");
			model.addAttribute("workOrgList", workOrgList);
		}
		
		
		//获取当前学年字典。
    	model.addAttribute("nowYearDic", SchoolYearUtil.getYearDic());
    	//获取当前学期
    	model.addAttribute("nowTermDic", SchoolYearUtil.getCurrentTermDic());
    	//获取学年、学期数据字典
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
		model.addAttribute("genderList",dicUtil.getDicInfoList("GENDER"));

		/*
		 * 资助申请课表的详细星期、课节数数据字典
		 */
		model.addAttribute("weekList", dicUtil.getDicInfoList("HKY_WEEKEDN"));
		model.addAttribute("lessonList", dicUtil.getDicInfoList("HKY_CLASS_NUMBER"));
		model.addAttribute("weekMap", weekMap);
		
		return Constants.MENUKEY_WORK_STUDY+"/editWorkStudy";
	}
	/**
	 * 提交信息
	 * @param model
	 * @param request
	 * @param workApply
	 * @param workApplyFile
	 * @param schedule
	 * @param fileId
	 * @param flags
	 * @return
	 */
	@RequestMapping(value = {"/sponsor/workStudyApprove/opt-save/submitWorkStudy" },produces = { "text/plain;charset=UTF-8"})
	@ResponseBody
	public String submitWorkStudy(ModelMap model,HttpServletRequest request,WorkApplyModel workApply,WorkApplyFileModel workApplyFile,String[] fileId,String flags){
		    String[] scheduleIds = request.getParameterValues("lessonCheckBox");
			
			workApply.setStatus(Constants.STATUS_SAVE_DICS);
		
			if(StringUtils.hasText(workApply.getWorkApplyId())){
				this.workStudyService.updateWorkApplyFile(workApplyFile, fileId);
				this.workStudyService.updateWorkApply(workApply);
				//更新3.更新课表
				this.workStudyService.updateSchedule(scheduleIds, workApplyFile);
				
			}else{
				this.workStudyService.saveWorkApplyFile(workApplyFile, fileId);
				this.workStudyService.saveWorkApply(workApply, workApplyFile);
				//3 处理课表安排
				
				this.workStudyService.saveSponsorSchedule(workApplyFile,scheduleIds);
			}
			
			return workApply.getWorkApplyId()+"_"+workApply.getApplyFile().getWorkApplyFileId();
	}
	/**
	 * 把保存 改为 提交
	 * @param workApplyId
	 * @return
	 */
	@RequestMapping(value={"/sponsor/workStudy/opt-update/addSubmitStatus"})
	public String addSubmit(String workApplyId){
		WorkApplyModel workApplyPo=this.workStudyService.queryWorkApplyById(workApplyId);
		workApplyPo.setStatus(Constants.STATUS_SUBMIT_DICS);
		workApplyPo.setProcessStatus("APPROVEING");
		this.workStudyService.updateWorkApply(workApplyPo);
		return "redirect:/sponsor/workStudy/opt-query/queryWorkStudyList.do"; 
	}
	/**
	 * 保存勤工助学
	 * @param workApply
	 * @param workApplyFile
	 * @param schedule
	 * @return
	 */
	@RequestMapping(value={"/sponsor/workStudy/opt-save/saveWorkStudy","/sponsor/workStudy/opt-update/saveWorkStudy"})
	public String saveWorkStudy(WorkApplyModel workApply,WorkApplyFileModel workApplyFile,String[] fileId,
			HttpServletRequest request,HttpSession session,String userNextId){
		log.info("勤工助学-----保存勤工助学");
		//根据当前登录人获取学生对象的信息
		String userId = sessionUtil.getCurrentUserId();
		String flags = request.getParameter("flags");
		//课表
		String[] scheduleIds = request.getParameterValues("lessonCheckBox");
		//保存、更新勤工助学
		this.workStudyService.saveOrUpdateWorkStudy(workApply,workApplyFile,fileId,flags,scheduleIds,userId,userNextId);
		
		return "redirect:/sponsor/workStudy/opt-query/queryWorkStudyList.do"; 
	}
	/**
	 * 申请的岗位是否重复
	 * @param currentStudentInfo
	 * @param positionId
	 * @return
	 */
	 @ResponseBody
	 @RequestMapping(value={"/sponsor/workStudy/opt-add/isRepeatPosition"},produces = { "text/plain;charset=UTF-8" })
	 public String isRepeatPosition(String currentStudentInfoId, String positionId,String workApplyId,String yearId,String termId){
		List<WorkApplyModel> workApplyList= this.workStudyService.queryWorkApplyByAllStatus(yearId, termId, currentStudentInfoId,Constants.STATUS_SUBMIT_DICS.getId());

		if(workApplyList!=null && workApplyList.size()>0 ){
			for(WorkApplyModel w:workApplyList){
				if(StringUtils.hasText(workApplyId)){
					if(!workApplyId.equals(w.getWorkApplyId())){
						String id=w.getSponsorPosition().getPositionId();
						if(id.equals(positionId)){
							//重复
							return "false";
						}
					}
				}else{
					if(positionId.equals(w.getSponsorPosition().getPositionId())){
						return "false";
					}
				}
				
			}
			
		}
		return "success";
	 }
	/**
	 * 查看功能
	 * @param model
	 * @param workApplyId
	 * @return
	 */
	@RequestMapping(value={"/sponsor/workStudy/opt-view/viewWorkStudy"})
	public String viewWorkStudy(ModelMap model,String workApplyId){
		log.info("勤工助学-----查看功能");
		WorkApplyModel workApplyPo=this.workStudyService.queryWorkApplyById(workApplyId);
		WorkApplyFileModel workApplyFilePo=this.workStudyService.queryWorkApplyFileById(workApplyPo.getApplyFile().getWorkApplyFileId());
		//根据岗位申请单的主键 查找到对应的助学课表
		//获得课表列表 并处理
		Map<String,String> weekMap = new HashMap<String,String>();
		List<ScheduleModel> scheduleList = sponsorScheduleService.queryScheduleByApplyId(workApplyFilePo.getWorkApplyFileId());
			
		if(!CollectionUtils.isEmpty(scheduleList))
		{
			for(ScheduleModel schedule : scheduleList)
			{
				weekMap.put(schedule.getWeekendDic().getId()+"_"+schedule.getLessonDic().getId(),"true");
			}
		}	
		//奖助
		if(workApplyPo.getDifficultStudentInfo()!=null&&workApplyPo.getDifficultStudentInfo().getStudent()!=null){
			this.workStudyService.getAwardByStu(workApplyPo.getDifficultStudentInfo().getStudent().getId(),model);

		}
		
		model.addAttribute("difficultStudentInfo",workApplyPo.getDifficultStudentInfo());
		model.addAttribute("workApply", workApplyPo);
		model.addAttribute("workApplyFile", workApplyFilePo);
		/*
		 * 资助申请课表的详细星期、课节数数据字典
		 */
		model.addAttribute("weekList", dicUtil.getDicInfoList("HKY_WEEKEDN"));
		model.addAttribute("lessonList", dicUtil.getDicInfoList("HKY_CLASS_NUMBER"));
		model.addAttribute("weekMap", weekMap);
		List<UploadFileRef> fileList=this.fileUtil.getFileRefsByObjectId(workApplyFilePo.getWorkApplyFileId());
		model.addAttribute("uploadFileRefList", fileList);
		return Constants.MENUKEY_WORK_STUDY+"/viewWorkStudy";
	}
	

	   
    /**
 	* 确认在岗操作
 	* @param model
 	* @param request
 	* @param workApplyId
 	* @return
 	*/
	@ResponseBody
    @RequestMapping(value={"/sponsor/workStudy/opt-update/confirmWorkStudy"},produces = { "text/plain;charset=UTF-8" })
    public String confirmWorkStudy(ModelMap model,HttpServletRequest request,String workApplyId){
		log.info("勤工助学-----确认岗位操作");
		WorkApplyModel workApplyPo=this.workStudyService.queryWorkApplyById(workApplyId);
    	//查找该岗位需要的人数
    	int workNumber=workApplyPo.getSponsorPosition().getWorkNumber();
    	//查找已经在岗的人数
    	List<WorkApplyModel> workApplyConfirmList=this.workStudyService.queryDiffByPositionIdAndIsPost( 
    			workApplyPo.getSponsorPosition().getPositionId(),Constants.STATUS_IS_POST);
    	//确认岗位
    	String isFull=this.workStudyService.confirmWork(workApplyPo,workNumber,workApplyConfirmList);
    	
    	return isFull;
    }
	
	
	/**
 	* 确认在岗操作
 	* @param model
 	* @param request
 	* @param workApplyId
 	* @return
 	*/
	@ResponseBody
    @RequestMapping(value={"/sponsor/workStudy/opt-query/positionCheck"},produces = { "text/plain;charset=UTF-8" })
    public String positionCheck(ModelMap model,HttpServletRequest request,String positionId){
		log.info("验证在岗人数是否已满");
		//WorkApplyModel workApplyPo=this.workStudyService.queryWorkApplyById(workApplyId);
		SponsorPositionModel sponsorPosition = SetWorkService.querySponsorPositionById(positionId);
		Integer workNumber =0;
		if(null !=sponsorPosition && !StringUtils.hasText(sponsorPosition.getId()))
		{
			//查找该岗位需要的人数
			workNumber = sponsorPosition.getWorkNumber();
		}
    	//查找已经在岗的人数
    	List<WorkApplyModel> workApplyConfirmList=this.workStudyService.queryDiffByPositionIdAndIsPost(positionId,Constants.STATUS_IS_POST);
    	if(null !=workApplyConfirmList && workApplyConfirmList.size()>0 && workApplyConfirmList.size()==workNumber)
    	{
    	   return "false";
    	}
    	return "true";
    }
	
	
	/**
	 * 放弃岗位操作
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping({"/sponsor/workStudy/opt-update/wasteWorkStudy"})
	public String wasteWorkApply(ModelMap model, HttpServletRequest request){
		String workApplyId=request.getParameter("workApplyId");
		WorkApplyModel workApplyPo=this.workStudyService.queryWorkApplyById(workApplyId);
		this.workStudyService.wasteWorkApply(workApplyPo);
		return "success";
	}
    /**
     * 删除勤工助学
     * @param model
     * @param request
     * @param workApplyId
     * @return
     */
    @ResponseBody
    @RequestMapping(value={"/sponsor/workStudy/opt-del/deleteWorkStudy"}, produces={"text/plain;charset=UTF-8"})
    public String deleteWorkStudy(ModelMap model,HttpServletRequest request,String workApplyId){
    	log.info("勤工助学-----删除勤工助学");
    	WorkApplyModel workApply=this.workStudyService.queryWorkApplyById(workApplyId);
    	this.workStudyService.deleteWorkApply(workApply);
	  
    	return "success";
    }
  /*  *//**
     * 跳转到复制岗位页面
     * @param model
     * @param request
     * @param session
     * @return
     *//*
    @RequestMapping(value={"/sponsor/workStudy/opt-formCopy/copyWorkStudy"})
    public String editCopyWorkStudy(ModelMap model,HttpServletRequest request,HttpSession session){
    	log.info("勤工助学----- 跳转到复制岗位页面");
    	String userId = sessionUtil.getCurrentUserId();
   	    StudentInfoModel currentStudentInfo=studentCommonService.queryStudentById(userId);
   	 	if(currentStudentInfo!=null){
			model.addAttribute("currentStudentInfo",currentStudentInfo);
		}
    	model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
    	model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
    	//获取当前学年字典。
    	model.addAttribute("nowYearDic", SchoolYearUtil.getYearDic());
    	//获取当前学期
    	if(SchoolYearUtil.getCurrentTermDic()!=null){
        	model.addAttribute("nowTermDic", SchoolYearUtil.getCurrentTermDic());
    	}
    	return  Constants.MENUKEY_WORK_STUDY+"/copyWorkStudy";
    	
    }


    *//**
     * 保存复制
     * @param model
     * @param request
     * @param session
     * @return
     *//*
    @ResponseBody
    @RequestMapping(value={"/sponsor/workStudy/opt-save/saveCopyWorkStudy"},produces = { "text/plain;charset=UTF-8" })
    public String saveCopyWorkStudy(ModelMap model,HttpServletRequest request,HttpSession session){
    	log.info("勤工助学----- 保存复制");
    	//获取页面参数
    	String studentId=request.getParameter("studentId");
    	String sourceSchoolYearId=request.getParameter("sourceSchoolYearId");
    	String aimSchoolYearId=request.getParameter("aimSchoolYearId");
    	String sourceTermId=request.getParameter("sourceTermId");
    	String aimTermId=request.getParameter("aimTermId");
    	//根据页面Id查找对应的数据字典
    	Dic aimSchoolYearDic = dicService.getDic(aimSchoolYearId);
    	Dic aimTermDic = dicService.getDic(aimTermId);
    	
    	//根据学年、学生查找困难生
    	String diffStudentId=null;
    	//根据源学年、学期、困难生 id 岗位审核通过 的条件找到岗位申请与岗位申请单 复制给目标学年、学期
    	WorkApplyModel workApplyPo=this.workStudyService.queryUniqueWorkApplyByPass(diffStudentId,sourceSchoolYearId,sourceTermId,"PASS");
    	if(workApplyPo==null){
    		return "isnull";
    	}
    	List<WorkApplyModel> aimWorkApplyList=this.workStudyService.queryWorkApplyByAllStatus(diffStudentId,aimSchoolYearId,aimTermId,null);
    	if(aimWorkApplyList!=null && aimWorkApplyList.size()!=0){
    		return "value";
    	}
    	
    	//复制功能
    	//复制申请单
		WorkApplyFileModel aimWorkApplyFile=new WorkApplyFileModel();
    	BeanUtils.copyProperties(workApplyPo.getApplyFile(), aimWorkApplyFile,new String[]{"workApplyFileId","createTime","delStatus"});
    	aimWorkApplyFile.setSchoolYear(aimSchoolYearDic);
    	aimWorkApplyFile.setTerm(aimTermDic);
    	aimWorkApplyFile.setDelStatus(Constants.STATUS_NORMAL_DICS);
    
    	aimWorkApplyFile.setWorkApplyFileId(null);
    	this.workStudyService.saveWorkApplyFile(aimWorkApplyFile,null);
    	//复制申请岗位
    	WorkApplyModel aimWorkApply=new WorkApplyModel();
    	BeanUtils.copyProperties(workApplyPo, aimWorkApply,new String[]{"workApplyId","status","delStatus","nextApprover","approveStatus",
    			"processStatus","approveReason","postStatus","createTime"});
    	
    	aimWorkApply.setStatus(Constants.STATUS_SAVE_DICS);//状态为保存
    	aimWorkApply.setDelStatus(Constants.STATUS_NORMAL_DICS);//删除状态为正常
    	aimWorkApply.setWorkApplyId(null);
    	this.workStudyService.saveWorkApply(aimWorkApply, aimWorkApplyFile);
    	model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
    	model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
    	return  "success";
    	
    }
*/  
    /* *//**
     * 跳转到复制岗位页面--部门复制
     * @param model
     * @param request
     * @param session
     * @return
     *//*
    @RequestMapping(value={"/sponsor/workStudy/opt-formCopy/copyWorkStudy"})
    public String editCopyWorkStudy(ModelMap model,HttpServletRequest request,HttpSession session){
    	
    	//获取当前用户所在的部门Id
    	String teacherOrgId= ProjectSessionUtils.getsourceTeacherOrgId(request);
    	//根据部门ID查找部门
    	Org teacherOrg = orgService.queryOrgById(teacherOrgId);
    	
    	model.addAttribute("teacherOrg", teacherOrg);
    	model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
    	model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
    	//获取当前学年字典。
    	model.addAttribute("nowYearDic", SchoolYearUtil.getYearDic());
    	//获取当前学期
    	model.addAttribute("nowTermDic", SchoolYearUtil.getsourceTermDic());
    	return  Constants.MENUKEY_WORK_STUDY+"/copyWorkStudy";
    	
    }
    *//**
     * 保存复制功能
     * @param model
     * @param request
     * @param session
     * @return
     *//*
    @ResponseBody
    @RequestMapping(value={"/sponsor/workStudy/opt-save/saveCopyWorkStudy"},produces = { "text/plain;charset=UTF-8" })
    public String saveCopyWorkStudy(ModelMap model,HttpServletRequest request,HttpSession session){
    	//获取当前用户所在的部门
    	String orgId=request.getParameter("orgId");
    	Org teacherOrg = orgService.queryOrgById(orgId);
    	//获取页面参数
    	String sourceSchoolYearId=request.getParameter("sourceSchoolYearId");
    	String aimSchoolYearId=request.getParameter("aimSchoolYearId");
    	String sourceTermId=request.getParameter("sourceTermId");
    	String aimTermId=request.getParameter("aimTermId");
    	//根据页面Id查找对应的数据字典
    	Dic sourceSchoolYearDic = dicService.getDic(sourceSchoolYearId);
    	Dic sourceTermDic = dicService.getDic(sourceTermId);
    	Dic aimSchoolYearDic = dicService.getDic(aimSchoolYearId);
    	Dic aimTermDic = dicService.getDic(aimTermId);
    	
    	//查找要复制的部门的申请岗位（审核通过）
    	List<WorkApplyModel> workApplyList=this.workStudyService.queryWorkApplyListByOrg(teacherOrg.getId(), sourceSchoolYearDic.getId(), sourceTermDic.getId());
    	if(workApplyList==null){
    		return "isnull";
    	}
    	List<WorkApplyModel> aimList=this.workStudyService.queryWorkApplyListByOrg(teacherOrg.getId(), aimSchoolYearDic.getId(), aimTermDic.getId());

    	if(aimList!=null){
    		return "value";
    	}
    	
    	//复制功能
    	this.workStudyService.copyWorkApply(teacherOrg,workApplyList,aimSchoolYearDic,nextTermDic);
    	
    	model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
    	model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
    	return  "success";
    	
    }*/
   
  
   /**
    * 勤工助学审核岗位列表
    * @param model
    * @param request
    * @param workApply
    * @return
    */
	@RequestMapping(value={"/sponsor/workStudyApprove/opt-approve/approveWorkStudyList"})
    public String approveWorkStudyList(ModelMap model,HttpServletRequest request,WorkApplyModel workApply,HttpSession session){
		log.info("勤工助学审核列表");
		//根据当前登录人信息
		String userId = sessionUtil.getCurrentUserId();
		
		//保存查询条件
		sessionUtil.setSessionAttribute("workApply", workApply);
		//默认=当前学年 默认=当前学期--当学期不为空的时候
		if( null==workApply.getApplyFile() ){
			WorkApplyFileModel workApplyFile=new WorkApplyFileModel();
			workApplyFile.setSchoolYear(SchoolYearUtil.getYearDic());
			workApply.setApplyFile(workApplyFile);
			if(null!=SchoolYearUtil.getCurrentTermDic()){
				workApplyFile.setTerm(SchoolYearUtil.getCurrentTermDic());
				workApply.setApplyFile(workApplyFile);
			}
		}else if(null==workApply.getApplyFile().getSchoolYear()){
			workApply.getApplyFile().setSchoolYear(SchoolYearUtil.getYearDic());
			workApply.setApplyFile(workApply.getApplyFile());
		}else if(null==workApply.getApplyFile().getTerm()){
			if(null!=SchoolYearUtil.getCurrentTermDic()){
				WorkApplyFileModel workApplyFile=new WorkApplyFileModel();
				workApplyFile.setTerm(SchoolYearUtil.getCurrentTermDic());
				workApply.setApplyFile(workApplyFile);
			}
		}
		
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		String[] objectIds = flowInstanceService.getObjectIdByProcessKey("SPONSOR_WORK_STUDY_APPROVE",userId);
		
		Page page = this.workStudyService.pageApproveWorkApply(workApply,pageNo, Page.DEFAULT_PAGE_SIZE, userId, objectIds);
		//学院
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	//专业
    	List<BaseMajorModel> majorList = null;
    	//班级
    	List<BaseClassModel> classList = null;
    	
    	if(workApply!= null&& !"".equals(workApply) && workApply.getDifficultStudentInfo()!=null && !"".equals(workApply.getDifficultStudentInfo())){
    		if(workApply.getDifficultStudentInfo().getStudent().getCollege()!=null &&
        			StringUtils.hasText(workApply.getDifficultStudentInfo().getStudent().getCollege().getId())){
        		majorList = compService.queryMajorByCollage(workApply.getDifficultStudentInfo().getStudent().getCollege().getId());
        	}
        	if(workApply.getDifficultStudentInfo().getStudent().getMajor()!=null &&
        			StringUtils.hasText(workApply.getDifficultStudentInfo().getStudent().getMajor().getId())){
        		classList = compService.queryClassByMajor(workApply.getDifficultStudentInfo().getStudent().getMajor().getId());
        	}
    	}
    	model.addAttribute("userId", userId);
    	model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());
    	model.addAttribute("page", page);
		model.addAttribute("workApply", workApply);
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));

		return Constants.MENUKEY_WORK_STUDY_APPROVE+"/approveWorkStudyList";
    }
	 /**
     * 编辑审核页面（公用）
     * @param model
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(value={"/sponsor/approveWorkStudy/opt-edit/editApproveWorkStudy"})
    public String editApproveWorkStudy(ModelMap model,HttpServletRequest request,String workApplyId){
    	log.info("勤工助学-----编辑审核页面");
    	if(StringUtils.hasText(workApplyId)){
    		Map<String,String> weekMap = new HashMap<String,String>();
			//跳转到修改页面
			//1.根据传入的 岗位申请ID 查找到 岗位申请
			WorkApplyModel workApplyPo=this.workStudyService.queryWorkApplyById(workApplyId);
			//2.根据 岗位申请 查找到唯一对应的 岗位申请单
			WorkApplyFileModel workApplyFilePo=workApplyPo.getApplyFile();
			
			//4.根据岗位申请单的主键 查找到对应的助学课表
			List<ScheduleModel> scheduleList = sponsorScheduleService.queryScheduleByApplyId(workApplyFilePo.getWorkApplyFileId());
			if(!CollectionUtils.isEmpty(scheduleList))
			{
				for(ScheduleModel schedule : scheduleList)
				{
					weekMap.put(schedule.getWeekendDic().getId()+"_"+schedule.getLessonDic().getId(),"true");
				}
			}
			//奖助
			if(workApplyPo.getDifficultStudentInfo()!=null&&workApplyPo.getDifficultStudentInfo().getStudent()!=null){
				this.workStudyService.getAwardByStu(workApplyPo.getDifficultStudentInfo().getStudent().getId(),model);
			}
			//附件
			List<FlowHistoryPo> flowInstances = this.flowInstanceService.getCurProcessHistory(workApplyPo.getId(), ProjectConstants.IS_APPROVE_ENABLE);
			model.addAttribute("flowInstances",flowInstances);
			
			model.addAttribute("workApply",workApplyPo);
			model.addAttribute("workApplyFile", workApplyFilePo);
			model.addAttribute("scheduleList", scheduleList);
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(workApplyFilePo.getWorkApplyFileId()));

			model.addAttribute("weekList", dicUtil.getDicInfoList("HKY_WEEKEDN"));
			model.addAttribute("lessonList", dicUtil.getDicInfoList("HKY_CLASS_NUMBER"));
			model.addAttribute("weekMap", weekMap);
		}
    	
    	return Constants.MENUKEY_WORK_STUDY_APPROVE+"/approveWorkStudyEdit";
    }
    /**
	 * 初始化当前流程
	 * @Title: saveCurProcess
	 * @Description: 初始化当前流程
	 * @param model
	 * @param request
	 * @param objectId			业务主键
	 * @param flags
	 * @param approveStatus		当前节点审批状态
	 * @param processStatusCode	流程当前状态
	 * @param nextApproverId	下一节点办理人
	 */
	@RequestMapping(value = {"/sponsor/workStudyApprove/opt-add/saveCurProcess"},produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String saveCurProcess(ModelMap model,HttpServletRequest request,String objectId,String nextApproverId){
		
		ApproveResult result = new ApproveResult();
		
		if(ProjectConstants.IS_APPROVE_ENABLE){
			try {
				User initiator = new User(this.sessionUtil.getCurrentUserId());//封装发起人
				User nextApprover = new User(nextApproverId);//封装第一级审核人
				//发起审核流程
				result = flowInstanceService.initProcessInstance(objectId,"SPONSOR_WORK_STUDY_APPROVE", 
						 initiator,nextApprover,ProjectConstants.IS_APPROVE_ENABLE);
				result = this.saveApproveStatusWorkStudy(objectId,result,nextApproverId);
				result.setResultFlag("success");
			} catch (Exception e) {
				result.setResultFlag("error");
			}
		}else{
			result.setResultFlag("deprecated");
	    }

		JSONObject json=JsonUtils.getJsonObject(result);
		return JsonUtils.jsonObject2Json(json);
	}
	
    /**
	 * 保存当前通过审批操作(保存在审批表中)
	 * @Title: saveApproveAction
	 * @Description:保存当前通过审批操作
	 * @param model
	 * @param request
	 * @param objectId
	 * @param nextApproverId
	 * @return
	 * @throws
	 */
	@RequestMapping(value = {"/sponsor/workStudyApprove/opt-add/saveApproveAction" },produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String saveApproveAction(ModelMap model,HttpServletRequest request,String objectId,String nextApproverId,String approveStatus,String processStatusCode){
		ApproveResult result = new ApproveResult();
		if(ProjectConstants.IS_APPROVE_ENABLE){
			try {
				  result.setApproveStatus(approveStatus);
				  result.setProcessStatusCode(processStatusCode);
				  this.saveApproveStatusWorkStudy(objectId,result,nextApproverId);
				  result.setResultFlag("success");
			} catch (Exception e) {
				result.setResultFlag("error");
				e.printStackTrace();
			}
		}else{
			result.setResultFlag("deprecated");
	    }
		JSONObject json=JsonUtils.getJsonObject(result);
		return JsonUtils.jsonObject2Json(json);
	}
	
  /**
   * 保存当前审核结果（保存在审批流程表、自己表中 审核状态\实例状态）
   * @param objectId
   * @param result
   * @param nextApproverId
   * @return
   */
	
	public ApproveResult saveApproveStatusWorkStudy(
			String objectId,ApproveResult result,String nextApproverId){
		if(DataUtil.isNotNull(result)){
			//获取实体保存
			WorkApplyModel workApplyPo =this.workStudyService.queryWorkApplyById(objectId);
			//流程审批状态
			workApplyPo.setApproveStatus(result.getApproveStatus());
			//流程实例状态
			workApplyPo.setProcessStatus(result.getProcessStatusCode());
			
			if(DataUtil.isNotNull(nextApproverId)){
				//下一节点办理人
				User nextApprover = new User(nextApproverId);
				workApplyPo.setNextApprover(nextApprover);
			}
			if("REJECT".equals(workApplyPo.getProcessStatus()) ||"PASS".equals(workApplyPo.getProcessStatus())){
				workApplyPo.setNextApprover(null);
				
			}
			
			//保存审批流回显的信息
			this.workStudyService.updateWorkApply(workApplyPo);
		}
		
		return result;
	}
	/**
	 * 
	 * @Title: saveApproveReasonWorkStudy
	 * @Description: 保存审核的信息（保存在自己表中 审核理由）
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({"/sponsor/workStudyApprove/opt-save/saveApproveReasonWorkStudy"})
	public String saveApproveReasonWorkStudy(ModelMap model,HttpServletRequest request){
		String workApplyId = request.getParameter("workApplyId");
		
		String approveReason = request.getParameter("approveReason");
		
		WorkApplyModel workApplyPo=this.workStudyService.queryWorkApplyById(workApplyId);
		
		workApplyPo.setApproveReason(approveReason);
		
		this.workStudyService.updateWorkApply(workApplyPo);
		
		return "redirect:/sponsor/workStudyApprove/opt-approve/approveWorkStudyList.do";
						
	}
	/**
	 * 单个离岗
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping({"/sponsor/workStudyApprove/opt-del/dismissWorkApply"})
	public String dismissWorkApply(ModelMap model, HttpServletRequest request){
		log.info("勤工助学-----单个离岗");
		String workApplyId=request.getParameter("workApplyId");
		WorkApplyModel workApplyPo=this.workStudyService.queryWorkApplyById(workApplyId);
		this.workStudyService.dismissWorkApply(workApplyPo);
		return "success";
	}

	
	/**
	 * 级联获取岗位列表
	 * @param orgId
	 * @param yearId
	 * @param termId
	 * @return
	 */
	@RequestMapping(value={"/sponsor/workStudy/opt-add/getPositionList"},produces = { "text/plain;charset=UTF-8"})
	@ResponseBody
	public String queryPositionList(String orgId,String yearId,String termId){
		log.info("勤工助学-----级联获取岗位列表");
		WorkOrgModel workOrg=this.setWorkService.queryWorkOrgByStatus(orgId, yearId, termId,"PASS");
		if(workOrg==null){
			return null;
		}
		List<SponsorPositionModel> positions=this.setWorkService.queryPositionListByWorkOrgId(workOrg.getWorkOrgId());
		String[][]value = new String[positions.size()][2];
		for(int i=0;i<positions.size();i++){
			value[i][0]=positions.get(i).getPositionId();
			value[i][1]=positions.get(i).getWorkName();
			
		}
			
		JSONArray ja = JSONArray.fromObject(value);
		return ja.toString();
		
	}
	/**
	 * 判断流程是否禁止
	 * @param workApplyId
	 * @return
	 */
	@RequestMapping(value={"/sponsor/workStudyApprove/opt-query/isAbandoned"},produces = { "text/plain;charset=UTF-8"})
	@ResponseBody
	public String queryPositionList(String workApplyId){
		log.info("勤工助学-----判断流程是否废止");
		WorkApplyModel workApplyPo=this.workStudyService.queryWorkApplyById(workApplyId);
		if(workApplyPo!=null){
			if("ABANDONED".equals(workApplyPo.getProcessStatus())){
				return "isAbandoned";
			}else{
				return "notAbandoned";
			}
		}else{
			return "notAbandoned";
		}
		
	}
	/**
	 * 导出列表
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping({"/sponsor/workStudyApprove/opt-query/nsm/exportWorkApplyList"})
	  public String exportWorkStudyList(ModelMap model, HttpServletRequest request){
		log.info("勤工助学-----导出列表");
	    int exportSize = Integer.valueOf(request.getParameter("exportSize")).intValue();
	    int pageTotalCount = Integer.valueOf(request.getParameter("pageTotalCount")).intValue();
	    int maxNumber = 0;
	    if (pageTotalCount < exportSize)
	      maxNumber = 1;
	    else if (pageTotalCount % exportSize == 0)
	      maxNumber = pageTotalCount / exportSize;
	    else {
	      maxNumber = pageTotalCount / exportSize + 1;
	    }
	    model.addAttribute("exportSize", Integer.valueOf(exportSize));
	    model.addAttribute("maxNumber", Integer.valueOf(maxNumber));
	    if (maxNumber < 500)
	      model.addAttribute("isMore", "false");
	    else {
	      model.addAttribute("isMore", "true");
	    }
	    return Constants.MENUKEY_WORK_STUDY_APPROVE+"/exportWorkStudyList";
	  }
	/**
	 * 导出数据
	 * @param model
	 * @param request
	 * @param workApplyPo
	 * @param response
	 */
	  @RequestMapping({"/sponsor/workStudyApprove/opt-query/exportWorkStudy"})
	  public void exportWorkStudy(ModelMap model, HttpServletRequest request, WorkApplyModel workApply, HttpServletResponse response){
		log.info("勤工助学-----导出数据");
	
		//获取当前用户所在的部门Id
		String teacherOrgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
	    String exportSize = request.getParameter("selectWorkStudyQuery_exportSize");
	    String exportPage = request.getParameter("selectWorkStudyQuery_exportPage");

		Page page=this.workStudyService.pageExportWorkApply(workApply, Integer.parseInt(exportPage), Integer.parseInt(exportSize), teacherOrgId);

	    List<Map> listMap = new ArrayList<Map>();
	    List<WorkApplyModel> workApplyList = (List<WorkApplyModel>)page.getResult();
	    for(int i=0;i<workApplyList.size();i++){
	      WorkApplyModel d=workApplyList.get(i);
	      Map<String,Object> newmap = new HashMap<String,Object>();
	      newmap.put("sortId", i+1);
	      newmap.put("schoolYear", d.getApplyFile()!=null ? (d.getApplyFile().getSchoolYear()!=null ?d.getApplyFile().getSchoolYear().getName(): ""):"");
	      newmap.put("term", d.getApplyFile()!=null ?(d.getApplyFile().getTerm()!=null ? (d.getApplyFile().getTerm().getName()):""):"");
	      newmap.put("collegeName", d.getDifficultStudentInfo()!=null ? (d.getDifficultStudentInfo().getStudent()!=null ?(d.getDifficultStudentInfo().getStudent().getCollege()!=null ?d.getDifficultStudentInfo().getStudent().getCollege().getName():""):""):"");
	      newmap.put("className", d.getDifficultStudentInfo()!=null ? (d.getDifficultStudentInfo().getStudent()!=null ?(d.getDifficultStudentInfo().getStudent().getClassId()!=null ? d.getDifficultStudentInfo().getStudent().getClassId().getClassName():""):""):"");
	      newmap.put("stuNumber", d.getDifficultStudentInfo()!=null ?(d.getDifficultStudentInfo().getStudent()!=null ? (d.getDifficultStudentInfo().getStudent().getStuNumber()!=null ? d.getDifficultStudentInfo().getStudent().getStuNumber():""):""):"" );
	      newmap.put("stuName", d.getDifficultStudentInfo()!=null ?(d.getDifficultStudentInfo().getStudent()!=null ? (d.getDifficultStudentInfo().getStudent().getName()!=null ? d.getDifficultStudentInfo().getStudent().getName():""):""):"" );
	      newmap.put("workOrg", d.getSponsorPosition()!=null ? (d.getSponsorPosition().getWorkOrg()!=null ? (d.getSponsorPosition().getWorkOrg().getOrg()!=null ? d.getSponsorPosition().getWorkOrg().getOrg().getName():""):""):"");
	      newmap.put("workName",d.getSponsorPosition()!=null? d.getSponsorPosition().getWorkName():"");
	      newmap.put("postStatusName",d.getPostStatus()!=null? d.getPostStatus().getName():"待确认");
	      listMap.add(newmap);
	    }
	    try
	    {
	      HSSFWorkbook wb = this.excelService.exportData("export_workStudy.xls", "exportWorkStudy",listMap);
	      	//单元格样式
	      HSSFCellStyle styleBold = (HSSFCellStyle) wb.createCellStyle();
		  styleBold.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		  styleBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		  styleBold.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		  styleBold.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		  styleBold.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
		  HSSFFont f = wb.createFont(); f.setFontHeightInPoints((short) 24);
		  //字号
		  f.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		  //加粗 
		  styleBold.setFont(f); 
		
	      String filename = "勤工助学"+exportPage+"页.xls";
	      response.setContentType("application/x-excel");
	      response.setHeader("Content-disposition", "attachment;filename=" + new String(filename.getBytes("GBK"), "iso-8859-1"));
	      response.setCharacterEncoding("UTF-8");
	      OutputStream ouputStream = response.getOutputStream();
	      wb.write(ouputStream);
	      ouputStream.flush();
	      ouputStream.close();
	    }
	    catch (ExcelException e)
	    {
	      e.printStackTrace();
	    }
	    catch (InstantiationException e) {
	      e.printStackTrace();
	    }
	    catch (IOException e) {
	      e.printStackTrace();
	    }
	    catch (IllegalAccessException e) {
	      e.printStackTrace();
	    }
	    catch (SQLException e) {
	      e.printStackTrace();
	    }
	    catch (ClassNotFoundException e) {
	      e.printStackTrace();
	    }
	    catch (URISyntaxException e) {
	      e.printStackTrace();
	    }
	  }

	 
}
