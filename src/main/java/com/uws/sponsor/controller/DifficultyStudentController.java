package com.uws.sponsor.controller;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.FlowHistoryPo;
import com.uws.apw.model.FlowInstancePo;
import com.uws.apw.service.IFlowInstanceService;
import com.uws.apw.util.JsonUtils;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.ICommonConfigService;
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
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.config.TimeConfigModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.sponsor.DifficultStudentInfo;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sponsor.service.IDifficultyStudentService;
import com.uws.sponsor.util.Constants;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.IDicService;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;
/**
 * 
* @ClassName: DifficultyStudentController 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author liuchen
* @date 2015-8-12 下午3:10:31 
*
 */
@Controller
public class DifficultyStudentController extends BaseController{
	
	@Autowired
	private IDifficultyStudentService difficultyStudentService;
	//学生service
	@Autowired
	private IStudentCommonService studentCommonService;
	@Autowired
	private IExcelService excelService;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICompService compService;
	//审批 Service
	@Autowired
	private IFlowInstanceService flowInstanceService;
	@Autowired
	private IDicService dicService;
	//时间配置管理service
	@Autowired
	private ICommonConfigService commonConfigService;

	// 日志
    private Logger log = new LoggerFactory(DifficultyStudentController.class);
    // sessionUtil工具类
  	private SessionUtil sessionUtil = SessionFactory.getSession(Constants.MENUKEY_STUDENT_INFO);
    //数据字典工具类
    private DicUtil dicUtil = DicFactory.getDicUtil();
    //附件工具类
  	private FileUtil fileUtil=FileFactory.getFileUtil();
  	
  	@InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    
  	/**
  	 * 
  	* @Title: DifficultyStudentController.java 
  	* @Package com.uws.sponsor.controller 
  	* @param  DifficultStudentInfo(困难生实体)
  	* @Description: 查询困难生信息列表
  	* @author liuchen  
  	* @date 2015-7-30 下午1:25:22
  	 */
	@RequestMapping("/sponsor/difficultStudent/opt-query/queryDifficultStudentList")
	public String listDifficultStudent(ModelMap model,HttpServletRequest request,DifficultStudentInfo difficultStudentInfo){
		log.info("困难生维护信息查询列表");
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.difficultyStudentService.queryStudetInfoList(pageNo,Page.DEFAULT_PAGE_SIZE, difficultStudentInfo,sessionUtil.getCurrentUserId());
		//学院
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	//专业
    	List<BaseMajorModel> majorList = null;
    	//班级
    	List<BaseClassModel> classList = null;
    	if(difficultStudentInfo!= null && difficultStudentInfo.getStudent()!= null && difficultStudentInfo.getStudent().getCollege()!=null &&
    			com.uws.core.util.StringUtils.hasText(difficultStudentInfo.getStudent().getCollege().getId())){
    		majorList = compService.queryMajorByCollage(difficultStudentInfo.getStudent().getCollege().getId());
    	}
    	if(difficultStudentInfo!= null && difficultStudentInfo.getStudent()!= null && difficultStudentInfo.getStudent().getMajor()!=null &&
    			com.uws.core.util.StringUtils.hasText(difficultStudentInfo.getStudent().getMajor().getId())){
    		classList = compService.queryClassByMajor(difficultStudentInfo.getStudent().getMajor().getId());
    	}
    	model.addAttribute("page", page);
    	model.addAttribute("difficultStudentInfo", difficultStudentInfo);
    	//判断申请是否在设定时间范围内
    	model.addAttribute("isSetTime",commonConfigService.checkCurrentDateByCode("SET_TIME_DIFFICULT_STUDENT"));
    	//时间设置实体类
    	model.addAttribute("timeConfigModel", commonConfigService.findByCondition("SET_TIME_DIFFICULT_STUDENT"));
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("difficultList",dicUtil.getDicInfoList("DIFFICULT_LEVEL"));
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());
		return Constants.MENUKEY_STUDENT_INFO+"/difficultStudentInfoList";
	}
	
	
	/**
	 * 新增，和修改页面跳转的方法(新增困难生信息页面)
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping({"/sponsor/difficultStudent/opt-add/editStudentInfo","/sponsor/difficultStudent/opt-update/editStudentInfo" })
	public String editStudentInfo(ModelMap model,HttpServletRequest request){
		String id = request.getParameter("id");
		//根据当前登录人获取学生对象的信息
		String currentStudentId = sessionUtil.getCurrentUserId();
		if(com.uws.core.util.StringUtils.hasText(currentStudentId)){
			StudentInfoModel studentInfo = studentCommonService.queryStudentById(currentStudentId);
			if(studentInfo!=null){
				model.addAttribute("studentInfo",studentInfo);
			}
		}else{
			model.addAttribute("studentInfo",new StudentInfoModel());
		}
		if(com.uws.core.util.StringUtils.hasText(id)){
			DifficultStudentInfo difficultStudent = this.difficultyStudentService.findDifficultStudentById(id);
			model.addAttribute("difficultStudent", difficultStudent);
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(difficultStudent.getId()));
			log.info("修改困难生信息");
		}else{
			DifficultStudentInfo difficultStudent = new DifficultStudentInfo();
			model.addAttribute("difficultStudent",difficultStudent);
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(null));
			//新增页面学年默认是当期学年
			Dic yearDic = SchoolYearUtil.getYearDic();
			model.addAttribute("nowYearDic", yearDic);
			model.addAttribute("noDic",dicUtil.getDicInfo("YES_OR_NO","NO"));
			model.addAttribute("singleDic",dicUtil.getDicInfo("IS_SINGLE","NO"));
			model.addAttribute("accountDic",dicUtil.getDicInfo("ACCOUNT_TYPE","ACCOUNT_FARMING"));
			log.info("新增困难生信息");
		}
		//字典封装方法
		this.setAttributeDic(model);
		return Constants.MENUKEY_STUDENT_INFO+"/difficultStudentInfoEdit";
	}
	
	/**
	 * 
	 * @Title: checkCodeRepeat
	 * @Description:验证同一学年的学生是否申请过困难生
	 * @param id
	 * @param schoolYear
	 * @return
	 * @throws
	 */
    @RequestMapping(value = {"/sponsor/difficultStudent/opt-query/schoolYearCheck"},produces = { "text/plain;charset=UTF-8"})
    @ResponseBody
    public String checkCodeRepeat(@RequestParam String id, @RequestParam String schoolYear)
    { 
		 String currentStudentId = sessionUtil.getCurrentUserId();
	     if (this.difficultyStudentService.isExistType(id,currentStudentId,schoolYear)) {
	       return "";
	     }
	     return "true";
    }
	
	
	/**
	 * 
	 * @Title: saveDifficultStudent
	 * @Description: 保存和提交确定方法
	 * @param model
	 * @param request
	 * @param difficultStudentInfo
	 * @param fileId
	 * @param flags
	 * @return
	 * @throws
	 */
	@RequestMapping(value = {"/sponsor/difficultStudent/opt-save/saveDifficultStudent","/sponsor/difficultStudent/opt-update/saveDifficultStudent"})
	public String saveDifficultStudent(ModelMap model,HttpServletRequest request,DifficultStudentInfo difficultStudentInfo,String[] fileId,String flags){
		
		if(StringUtils.hasText(difficultStudentInfo.getId()))
		{
				DifficultStudentInfo difficultStudent = this.difficultyStudentService.findDifficultStudentById(difficultStudentInfo.getId());
				if(null != difficultStudent)
				{
					BeanUtils.copyProperties(difficultStudentInfo,difficultStudent,new String[]{"createTime","nextApprover","approveStatus","processStatus"});
					if(DataUtil.isNotNull(flags) && flags.equals("1")){
						difficultStudent.setStatus(Constants.STATUS_SUBMIT_DICS);
					}else{
						difficultStudent.setStatus(Constants.STATUS_SAVE_DICS);//保存状态
					}
					difficultStudent.setDelStatus(Constants.STATUS_NORMAL_DICS);
					this.difficultyStudentService.updateInfos(difficultStudent,fileId);
				}
			
		}
		else //保存
		{
			difficultStudentInfo.setDelStatus(Constants.STATUS_NORMAL_DICS);
			if(DataUtil.isNotNull(flags) && flags.equals("1")){
				difficultStudentInfo.setStatus(Constants.STATUS_SUBMIT_DICS);
			}else{
				difficultStudentInfo.setStatus(Constants.STATUS_SAVE_DICS);//保存状态
			}
			this.difficultyStudentService.saveInfos(difficultStudentInfo,fileId);
		}
			return "redirect:/sponsor/difficultStudent/opt-query/queryDifficultStudentList.do";
	}
	
	
	/**
	 * 
	* @Title: DifficultyStudentController.java 
	* @Package com.uws.sponsor.controller 
	* @Description: 提交信息 方法废弃 
	* @author liuchen 
	* @date 2015-7-31 下午3:39:46
	 */
	@Deprecated
	@ResponseBody
	@RequestMapping(value = {"/sponsor/difficultStudent/opt-save/submitDifficultStudent" },produces = { "text/plain;charset=UTF-8"})
	public String submitDifficultStudent(ModelMap model,HttpServletRequest request,DifficultStudentInfo difficultStudentInfo,String[] fileId,String flags){
		if( StringUtils.hasText(difficultStudentInfo.getId()))
		{
			log.debug("困难生信息更新操作!");
			DifficultStudentInfo difficultStudent = this.difficultyStudentService.findDifficultStudentById(difficultStudentInfo.getId());
			BeanUtils.copyProperties(difficultStudentInfo,difficultStudent,new String[]{"student","createTime","delStatus","nextApprover","approveStatus","processStatus"});
			difficultStudent.setStatus(Constants.STATUS_SAVE_DICS);//保存状态
			this.difficultyStudentService.updateInfos(difficultStudent,fileId);
		}else{
			// 删除状态(保存正常的)
			log.debug("困难生信息新增操作!");
			difficultStudentInfo.setDelStatus(Constants.STATUS_NORMAL_DICS);
			difficultStudentInfo.setStatus(Constants.STATUS_SAVE_DICS);//保存状态
			this.difficultyStudentService.saveInfos(difficultStudentInfo,fileId);
		}
			return difficultStudentInfo.getId();
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
	@ResponseBody
	@RequestMapping(value = {"/sponsor/diffapprove/opt-add/saveCurProcess"},produces = { "text/plain;charset=UTF-8" })
	public String saveCurProcess(ModelMap model,HttpServletRequest request,String objectId,String nextApproverId){
		ApproveResult result = new ApproveResult();
		if(ProjectConstants.IS_APPROVE_ENABLE){
			try {
				User initiator = new User(this.sessionUtil.getCurrentUserId());//封装发起人
				User nextApprover = new User(nextApproverId);//封装第一级审核人
				result = this.flowInstanceService.initProcessInstance(objectId, "SPONSOR_DIFFICULT_STUDENT_APPROVE",initiator,nextApprover,ProjectConstants.IS_APPROVE_ENABLE);
				result = this.saveDifficultApproveResult(objectId,result,nextApproverId);
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
	 * 保存当前通过审批操作
	 * @Title: saveApproveAction
	 * @Description:保存当前通过审批操作
	 * @param model
	 * @param request
	 * @param objectId
	 * @param nextApproverId
	 * @return
	 * @throws
	 */
	@RequestMapping(value = {"/sponsor/diffapprove/opt-add/saveApproveAction" },produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String saveApproveAction(ModelMap model,HttpServletRequest request,String objectId,String nextApproverId,String approveStatus,String processStatusCode){
		ApproveResult result = new ApproveResult();
		if(ProjectConstants.IS_APPROVE_ENABLE){
			try {
				  result.setApproveStatus(approveStatus);
				  result.setProcessStatusCode(processStatusCode);
				  this.saveDifficultApproveResult(objectId,result,nextApproverId);
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
	 * 保存困难生审批结果
	 * @Title: saveDifficultApproveResult
	 * @Description: 保存困难生审批结果
	 * @param objectId
	 * @param result
	 * @throws
	 */
	private ApproveResult saveDifficultApproveResult(String objectId,ApproveResult result,String nextApproverId) {
		if(DataUtil.isNotNull(result)){
			//获取保存的困难生
			DifficultStudentInfo difficultStudentPo = this.difficultyStudentService.findDifficultStudentById(objectId);
			if(null == difficultStudentPo)
			{
				difficultStudentPo = new DifficultStudentInfo();
				difficultStudentPo.setId(objectId);
				difficultyStudentService.saveInfos(difficultStudentPo, null);
			}
			//流程审批状态
			difficultStudentPo.setApproveStatus(result.getApproveStatus());
			//流程实例状态
			difficultStudentPo.setProcessStatus(result.getProcessStatusCode());
			difficultStudentPo.setStatus(Constants.STATUS_SUBMIT_DICS);//修改为提交状态
			if(DataUtil.isNotNull(nextApproverId)){
				//下一节点办理人
				User nextApprover = new User();
				nextApprover.setId(nextApproverId);
				difficultStudentPo.setNextApprover(nextApprover);
			}else{
				difficultStudentPo.setNextApprover(null);
			}
			//保存审批流回显的信息
			difficultyStudentService.updateStudentInfo(difficultStudentPo);
		}
		
		return result;
	}


	/**
	 * 根据id删除困难生信息
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = {"/sponsor/difficultStudent/opt-del/deleteDifficultStudentInfo" },produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String deleteDifficultStudentInfo(ModelMap model, HttpServletRequest request) {
		String id = request.getParameter("id");
		DifficultStudentInfo difficultStudent = this.difficultyStudentService.findDifficultStudentById(id);
		// 删除状态(非正常的)
		Dic statusDeletedDics = Constants.STATUS_DELETED_DICS;
		difficultStudent.setDelStatus(statusDeletedDics);
		this.difficultyStudentService.updateStudentInfo(difficultStudent);
		log.info("删除操作成功！");
		return "success";
	}
	
	
	/**
	 * 
	* @Title: DifficultyStudentController.java 
	* @Package com.uws.sponsor.controller 
	* @Description: 查看困难生详细信息
	* @author liuchen 
	* @date 2015-8-3 下午2:14:11
	 */
	@RequestMapping("/sponsor/difficultStudent/opt-view/viewDifficultStudentInfo")
	public String viewDifficultStudentInfo(ModelMap model,HttpServletRequest request)
	{   
		log.info("查看困难生信息");
		String id = request.getParameter("id");
		if(com.uws.core.util.StringUtils.hasText(id)){
			//根据困难生ID获取困难生信息
			DifficultStudentInfo difficultStudent = this.difficultyStudentService.findDifficultStudentById(id);
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));
			model.addAttribute("difficultStudent", difficultStudent);
			List<FlowHistoryPo> instanceList = this.flowInstanceService.getCurProcessHistory(id,ProjectConstants.IS_APPROVE_ENABLE);
			model.addAttribute("instanceList",instanceList);
		}else{
			model.addAttribute("difficultStudent", new DifficultStudentInfo());
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));
		}
		this.setAttributeDic(model);
		return Constants.MENUKEY_STUDENT_INFO+"/difficultStudentInfoView";
	}
	
	/**
	 * 
	* @Title: DifficultyStudentController.java 
	* @Package com.uws.sponsor.controller 
	* @Description: 困难生审核列表
	* @author liuchen  
	* @date 2015-8-4 上午10:29:35
	 */
	@RequestMapping("/sponsor/approveDifficultStudent/opt-query/approveDifficultStudentList")
	public String approveStudentInfo(ModelMap model,HttpServletRequest request,DifficultStudentInfo difficultStudentInfo){
		log.info("困难生审核列表");
	    String currentUserId = sessionUtil.getCurrentUserId();
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		String[] objectIds = flowInstanceService.getObjectIdByProcessKey(Constants.DIFFICULT_STUDENT_APPROVE_FLOW_KEY,currentUserId);
		Page page = this.difficultyStudentService.instructorApproveStudetInfoList(pageNo,Page.DEFAULT_PAGE_SIZE, difficultStudentInfo,currentUserId,objectIds);
		model.addAttribute("page", page);
		//学院
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	//专业
    	List<BaseMajorModel> majorList = null;
    	//班级
    	List<BaseClassModel> classList = null;
    	if(difficultStudentInfo!= null && difficultStudentInfo.getStudent()!= null && difficultStudentInfo.getStudent().getCollege()!=null &&
    			com.uws.core.util.StringUtils.hasText(difficultStudentInfo.getStudent().getCollege().getId())){
    		majorList = compService.queryMajorByCollage(difficultStudentInfo.getStudent().getCollege().getId());
    	}
    	if(difficultStudentInfo!= null && difficultStudentInfo.getStudent()!= null && difficultStudentInfo.getStudent().getMajor()!=null &&
    			com.uws.core.util.StringUtils.hasText(difficultStudentInfo.getStudent().getMajor().getId())){
    		classList = compService.queryClassByMajor(difficultStudentInfo.getStudent().getMajor().getId());
    	}
    	//时间设置实体类
    	TimeConfigModel timeConfigModel=commonConfigService.findByCondition("SET_TIME_DIFFICULT_STUDENT");
    	model.addAttribute("timeConfigModel", timeConfigModel);
    	model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("difficultStudentInfo", difficultStudentInfo);
		model.addAttribute("difficultList",dicUtil.getDicInfoList("DIFFICULT_LEVEL"));
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("currentUserId", sessionUtil.getCurrentUserId());
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());
		return Constants.MENUKEY_STUDENT_APPROVE+"/approveStudentInfoList";
	} 
	
	/**
	 * 判断是否审核
	 * @Title: isApprove
	 * @Description: 判断是否已审核
	 * @param objectId
	 * @param userId
	 * @return
	 * @throws
	 */
	public boolean isApprove(String objectId, String userId){
		boolean bol = false;
		if(StringUtils.hasText(userId))
		{
			FlowInstancePo fipo = this.flowInstanceService.getFlowInstancePo(objectId,sessionUtil.getCurrentUserId());
			if(null!=fipo && !"".equals(fipo.getId()))
				if(fipo.getApproveToken().equals("AVAILABLE")){
					return bol = true;
				}else{
					return bol = false;
				}
		}
		return bol;
	}
	
	
	/**
	 * 
	* @Title: DifficultyStudentController.java 
	* @Package com.uws.sponsor.controller 
	* @Description: 辅导员,二级学院，学生处审核困难生信息页面
	* @author liuchen 
	* @date 2015-8-4 下午2:08:52
	 */
	@RequestMapping({"/sponsor/difficultStudentApprove/opt-edit/editDifficultStudentApprove"})
	public String editApproveStudentInfo(ModelMap model,HttpServletRequest request,String id){
		log.info("困难生审核页面");
		if(com.uws.core.util.StringUtils.hasText(id)){
			DifficultStudentInfo difficultStudent = this.difficultyStudentService.findDifficultStudentById(id);
			model.addAttribute("difficultStudent", difficultStudent);
		}else{
			model.addAttribute("difficultStudent", new DifficultStudentInfo());
		}
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));
			this.setAttributeDic(model);
			List<FlowHistoryPo> instanceList = this.flowInstanceService.getCurProcessHistory(id,true);
			model.addAttribute("instanceList",instanceList);
			return Constants.MENUKEY_STUDENT_APPROVE+"/approveStudentInfoEdit";
	}
	
	/**
	 * 
	 * @Title: saveApproveStudentInfo
	 * @Description: 保存审核的信息
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({"/sponsor/difficultStudentApprove/opt-save/saveDifficultStudentApprove"})
	public String saveApproveStudentInfo(ModelMap model,HttpServletRequest request){
		String id = request.getParameter("id");
		String levelId = request.getParameter("difficultLevel.id");
		String approveReason = request.getParameter("approveReason");
		DifficultStudentInfo difficultStudentPo = this.difficultyStudentService.findDifficultStudentById(id);
		if(difficultStudentPo !=null && StringUtils.hasText(difficultStudentPo.getProcessStatus()) && difficultStudentPo.getProcessStatus().equals("PASS")){
			difficultStudentPo.setStatus(Constants.STATUS_PASS_DICS);
		}else if(difficultStudentPo !=null && StringUtils.hasText(difficultStudentPo.getProcessStatus()) && difficultStudentPo.getProcessStatus().equals("REJECT")){
			difficultStudentPo.setStatus(Constants.STATUS_SAVE_DICS);
		}
		Dic difficultLevel = new Dic();
		difficultLevel.setId(levelId);
		difficultStudentPo.setDifficultLevel(difficultLevel);
		difficultStudentPo.setApproveReason(approveReason);
		this.difficultyStudentService.updateStudentInfo(difficultStudentPo);
		return "redirect:/sponsor/approveDifficultStudent/opt-query/approveDifficultStudentList.do";
	}
	
	
	
	
	/**
	 * 
	* @Title: DifficultyStudentController.java 
	* @Package com.uws.sponsor.controller 
	* @Description: 困难生统计列表页面
	* @author liuchen  
	* @date 2015-8-5 上午11:33:59
	 */
	@RequestMapping({"/sponsor/countDifficultStudent/opt-count/countDifficultStudentInfo"})
	public String countDifficultStudent(ModelMap model,HttpServletRequest request,DifficultStudentInfo difficultStudentInfo){
		log.info("困难生统计列表");
		String yearId=request.getParameter("schoolYear.id");
		if(com.uws.core.util.StringUtils.hasText(yearId)){
			Dic yearDic = dicService.getDic(yearId);
			model.addAttribute("yearDic", yearDic);
		}else if(yearId == null){
			//获取当前学年字典。
			Dic nowYearDic = SchoolYearUtil.getYearDic();
			difficultStudentInfo.setSchoolYear(nowYearDic);
			model.addAttribute("yearDic", nowYearDic);
		}
		List<BaseClassModel> claList = null;
		BaseAcademyModel college = new BaseAcademyModel();
		String collegeId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		college = this.baseDataService.findAcademyById(collegeId);
		if(collegeId !=null && !collegeId.equals(ProjectConstants.STUDNET_OFFICE_ORG_ID))
		{
			//claList = this.difficultyStudentService.queryClassByTeacher(sessionUtil.getCurrentUserId());//判断是否带班
			difficultStudentInfo.setCollege(college);
			model.addAttribute("collegeStatus", "false");
			model.addAttribute("collegeId", collegeId);
			model.addAttribute("majorStatus", "false");
	    }	
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.difficultyStudentService.queryPassStudetInfoList(pageNo,Page.DEFAULT_PAGE_SIZE, difficultStudentInfo,college,claList);
		//学院
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	//专业
    	List<BaseMajorModel> majorList = null;
    	//班级
    	List<BaseClassModel> classList = null;
   
    	if(difficultStudentInfo!= null && difficultStudentInfo.getCollege()!=null && com.uws.core.util.StringUtils.hasText(difficultStudentInfo.getCollege().getId())){
    		majorList = compService.queryMajorByCollage(difficultStudentInfo.getCollege().getId());
    	}
    	if(difficultStudentInfo!= null && difficultStudentInfo.getMajor()!=null && com.uws.core.util.StringUtils.hasText(difficultStudentInfo.getMajor().getId())){
    	    classList = compService.queryClassByMajor(difficultStudentInfo.getMajor().getId());
    	}
		model.addAttribute("page", page);
		model.addAttribute("difficultStudentInfo", difficultStudentInfo);
		model.addAttribute("difficultList",dicUtil.getDicInfoList("DIFFICULT_LEVEL"));
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		return Constants.MENUKEY_STUDENT_INFO+"/countDifficultStudentInfoList";
	}
	
	/**
	 * 
	* @Title: DifficultyStudentController.java 
	* @Package com.uws.sponsor.controller 
	* @Description: 公共的数据字典存放
	* @author liuchen  
	* @date 2015-8-7 下午3:29:36
	 */
	private void setAttributeDic(ModelMap model)
	{
		model.addAttribute("genderList",dicUtil.getDicInfoList("GENDER"));
		model.addAttribute("accountList",dicUtil.getDicInfoList("ACCOUNT_TYPE"));
		model.addAttribute("surveyList",dicUtil.getDicInfoList("YES_OR_NO"));
		model.addAttribute("financeList",dicUtil.getDicInfoList("FAMILY_FINANCE"));
		model.addAttribute("villageList",dicUtil.getDicInfoList("YES_OR_NO"));
		model.addAttribute("townList",dicUtil.getDicInfoList("YES_OR_NO"));
		model.addAttribute("countyList",dicUtil.getDicInfoList("YES_OR_NO"));
		model.addAttribute("isStudnetList",dicUtil.getDicInfoList("STUDENT_LOAN"));
		model.addAttribute("isSingleList",dicUtil.getDicInfoList("IS_SINGLE"));
		model.addAttribute("lowList",dicUtil.getDicInfoList("YES_OR_NO"));
		model.addAttribute("aidList",dicUtil.getDicInfoList("YES_OR_NO"));
		model.addAttribute("disablilityList",dicUtil.getDicInfoList("IS_SINGLE"));
		model.addAttribute("disaLevelList",dicUtil.getDicInfoList("DISABILITY_LEVEL"));
		model.addAttribute("martyrList",dicUtil.getDicInfoList("IS_SINGLE"));
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("isOrphanList",dicUtil.getDicInfoList("IS_SINGLE"));
		model.addAttribute("difficultList",dicUtil.getDicInfoList("DIFFICULT_LEVEL"));
	}
	
	
	/**
	 * 
	 * @Title: exportDifficultStudnetInfoList
	 * @Description: 导出页面
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping(value="/sponsor/difficultStudent/nsm/exportDifficultStudentView")
	public String exportDifficultStudnetInfoList(ModelMap model,HttpServletRequest request){
		int exportSize=Integer.valueOf(request.getParameter("exportSize")).intValue();
		int pageTotalCount=Integer.valueOf(request.getParameter("pageTotalCount")).intValue();
		int maxNumber=0;
		if(pageTotalCount<exportSize){
			maxNumber=1;
		}else if(pageTotalCount % exportSize == 0){
			maxNumber=pageTotalCount / exportSize;
		}else{
			maxNumber=pageTotalCount / exportSize + 1;
		}
		model.addAttribute("exportSize",Integer.valueOf(exportSize));
		model.addAttribute("maxNumber",Integer.valueOf(maxNumber));
		//为了能将导入的数据效率高，判断每次导入数据500条
		if(maxNumber<500){
			model.addAttribute("isMore", "false");
		}else{
			model.addAttribute("isMore", "true");
		}
		 return Constants.MENUKEY_STUDENT_INFO+"/exportDifficultStudentView";
	}
	
	
	/**
	 * 导出数据
	 * @param model
	 * @param request
	 * @param exportDifficultStudentInfo
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/sponsor/opt-export/exportDifficultStudentInfo")
	public void exportDifficultStudentInfo(ModelMap model,HttpServletRequest request,DifficultStudentInfo difficultStudentInfo,HttpServletResponse response){
		String exportSize=request.getParameter("studentPoQuery_exportSize");
		String exportPage=request.getParameter("studentPoQuery_exportPage");
		String yearId=request.getParameter("schoolYear.id");
		String collegeId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		BaseAcademyModel college = this.baseDataService.findAcademyById(collegeId);
		List<BaseClassModel> classList = this.difficultyStudentService.queryClassByTeacher(sessionUtil.getCurrentUserId());
		Page page = this.difficultyStudentService.queryPassStudetInfoList(Integer.parseInt(exportPage),Integer.parseInt(exportSize),difficultStudentInfo,college,classList);
		List<Map> listMap= new ArrayList<Map>();
		List<DifficultStudentInfo> studentList = (List<DifficultStudentInfo>) page.getResult();
		//遍历导出的数据，并将数据放入map对象中
		for(DifficultStudentInfo s:studentList){
			Map<String, Object> newmap = new HashMap<String, Object>();
			newmap.put("difficultLevel", s.getDifficultLevel()!=null?s.getDifficultLevel().getName():"");	
			newmap.put("name", s.getStudent()!=null?s.getStudent().getName():"");	
			newmap.put("genderDic", s.getStudent()!=null?(s.getStudent().getGenderDic()!=null?s.getStudent().getGenderDic().getName():""):"");
			newmap.put("stuNumber", s.getStudent()!=null?s.getStudent().getStuNumber():"");
			newmap.put("collegeName", s.getStudent()!=null?(s.getStudent().getCollege()!=null?s.getStudent().getCollege().getName():""):"");
			newmap.put("className", s.getStudent()!=null?(s.getStudent().getClassId()!=null?s.getStudent().getClassId().getClassName():""):"");
			newmap.put("certificateCode",s.getStudent()!=null?s.getStudent().getCertificateCode():"");
			newmap.put("homeTel", s.getStudent()!=null?s.getStudent().getHomeTel():"");
			newmap.put("phone1", s.getStudent()!=null?s.getStudent().getPhone1():"");
			newmap.put("qq", s.getStudent()!=null?s.getStudent().getQq():"");
			newmap.put("villageProve", s.getVillageProve()!=null?s.getVillageProve().getName():"");
			newmap.put("townProve", s.getTownProve()!=null?s.getTownProve().getName():"");
			newmap.put("countyProve",s.getCountyProve()!=null?s.getCountyProve().getName():"");
			newmap.put("isStudentLoan", s.getIsStudentLoan()!=null?s.getIsStudentLoan().getName():"");
			newmap.put("lowAssuranceLevel", s.getLowAssuranceLevel()!=null?s.getLowAssuranceLevel().getName():"");
			newmap.put("aidCertificateLevel", s.getAidCertificateLevel()!=null?s.getAidCertificateLevel().getName():"");
			newmap.put("disabilityCertificateKind", s.getDisabilityCertificateKind()!=null?s.getDisabilityCertificateKind().getName():"");
			newmap.put("disabilityCertificateLevel", s.getDisabilityCertificateLevel());
			newmap.put("applyReason", s.getApplyReason());
			listMap.add(newmap);
		}
		
		try {
			HSSFWorkbook wb=this.excelService.dynamicExportData("export_difficultStudent.xls", "exportDifficultStudent", listMap);
			//添加总数列表 
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFRow headRow = null;
			headRow = sheet.createRow(0);
			String title = null;
			String yearName = null;
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
			//如果没有选择学年，默认导出当前学年
			if(com.uws.core.util.StringUtils.hasText(yearId)){
				Dic yearDic = dicService.getDic(yearId);
				title=yearDic.getName()+"学年"+"经济困难生情况统计表";
			}else{
				//获取当前学年字典。
				Calendar cal = Calendar.getInstance();
				int year = cal.get(Calendar.YEAR);
				Dic nowYearDic = dicUtil.getDicInfo("YEAR", String.valueOf(year));
				yearName = nowYearDic.getName();
				title=yearName+"学年"+"经济困难生情况统计表";
			}
			// 总结需要合并单元格的
			HSSFCell headCell = headRow.createCell(0);
			headCell.setCellValue(title);// 跨单元格显示的数据
			headCell.setCellType(HSSFCell.CELL_TYPE_STRING);
            sheet.addMergedRegion(new CellRangeAddress(0,0, 0, 19));
            //设置合并单元格单元格的高度
            sheet.getRow(0).setHeightInPoints(30);
            headCell.setCellStyle(styleBold);
			String filename = "困难生信息第"+exportPage+"页.xls";
			response.setContentType("application/x-excel");     
			response.setHeader("Content-disposition", "attachment;filename=" +new String (filename.getBytes("GBK"),"iso-8859-1"));
			response.setCharacterEncoding("UTF-8");
			OutputStream ouputStream = response.getOutputStream();     
			wb.write(ouputStream);
			ouputStream.flush(); 
			ouputStream.close(); 
			
		} catch (ExcelException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @Title: checkedApproveList
	 * @Description: 批量审批
	 * @param model
	 * @param difficultStudentInfo
	 * @param selectedBox
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({"/sponsor/difficultStudentApproves/opt-query/checkedApproveList"})
	public String checkedApproveList(ModelMap model,DifficultStudentInfo difficultStudentInfo,String selectedBox,HttpServletRequest request) {
		
		List<DifficultStudentInfo> stuList = new ArrayList<DifficultStudentInfo>();
		if(selectedBox.indexOf(",") > -1) {
			String[] checkedIds = selectedBox.split(",");
			for(String s : checkedIds) {
				difficultStudentInfo = this.difficultyStudentService.findDifficultStudentById(s);
				if(DataUtil.isNotNull(difficultStudentInfo)) {
					stuList.add(difficultStudentInfo);
				}
			}
		}else if(DataUtil.isNotNull(selectedBox)){
			difficultStudentInfo = this.difficultyStudentService.findDifficultStudentById(selectedBox);
			if(DataUtil.isNotNull(difficultStudentInfo)) {
				stuList.add(difficultStudentInfo);
			}
		}
		model.addAttribute("stuList", stuList);
	    model.addAttribute("objectIds", selectedBox);
		return Constants.MENUKEY_STUDENT_APPROVE+"/stuMulApprove";
	}
	
	/**
	 * 
	 * @Title: saveMutiResult
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param model
	 * @param request
	 * @param mulResults
	 * @return
	 * @throws
	 */
	@RequestMapping({"/sponsor/difficultStudent/opt-save/saveMutiResult.do"})
	public String saveMutiResult(ModelMap model,HttpServletRequest request,String mulResults) {
		
		List<ApproveResult> list  = this.flowInstanceService.getFormatedResult(mulResults,ProjectConstants.IS_APPROVE_ENABLE);
		if(DataUtil.isNotNull(list) && list.size()>0){
			this.difficultyStudentService.saveMulResult(list);
		}
		return "redirect:/sponsor/approveDifficultStudent/opt-query/approveDifficultStudentList.do";
	}
	
}