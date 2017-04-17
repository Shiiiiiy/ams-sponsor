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
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.FlowInstancePo;
import com.uws.apw.service.IFlowInstanceService;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.ICommonConfigService;
import com.uws.common.service.ICommonRoleService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.service.IExcelService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.DateUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.sponsor.OrgWorkHourModel;
import com.uws.domain.sponsor.WorkHourModel;
import com.uws.domain.sponsor.WorkApplyModel;
import com.uws.domain.sponsor.WorkHourModelList;
import com.uws.sponsor.service.ILaborHourService;
import com.uws.sponsor.service.IWorkStudyService;
import com.uws.sponsor.util.Constants;
import com.uws.sponsor.util.RmbUtil;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.Org;
import com.uws.user.model.User;
import com.uws.core.session.SessionFactory;
import com.uws.user.service.IOrgService;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/**
 * @className LaborHourController.java
 * @package com.uws.sponsor.controller
 * @description 用工工时Controller
 * @date 2015-8-12  下午3:08:34
 */
@Controller
public class LaborHourController extends BaseController {
	//查询部门
	@Autowired
	private IOrgService orgService;
	@Autowired
	private ICompService compService;
	//excel操作
	@Autowired
	private IExcelService excelService;
	//查询学院
	@Autowired
	private IBaseDataService baseDateService;
	@Autowired
	private IWorkStudyService workStudyService;
	@Autowired
	private ILaborHourService laborHourService;
	@Autowired
	private ICommonRoleService commonRoleService;
	@Autowired
	private IFlowInstanceService flowInstanceService;
	@Autowired
	private ICommonConfigService commonConfigService;
	private DicUtil dicUtil = DicFactory.getDicUtil();
	//查询学生
	@Autowired
	private IStudentCommonService studentCommonService;
	//session
	private SessionUtil sessionUtil = SessionFactory.getSession(Constants.MENUKEY_WORK_LABORHOUR);
	
	/**
	 * 列表查询
	 * @param model
	 * @param request
	 * @param response
	 * @param workHourModelVO
	 * @return
	 */
	@RequestMapping("/sponsor/laborHour/opt-query/queryLaborHourList")
	public String queryLaborHourList(ModelMap model,HttpServletRequest request,HttpServletResponse response,WorkHourModel workHourModelVO){
		List<Org> orgs = this.orgService.queryOrg();
		int pageNo = this.getPageNo(request.getParameter("page"));
		List<BaseMajorModel> majors = new ArrayList<BaseMajorModel>();
		List<BaseClassModel> classes = new ArrayList<BaseClassModel>();
		String orgId = (String)request.getSession().getAttribute("_teacher_orgId");
		String userId = sessionUtil.getCurrentUserId();
		List<BaseAcademyModel> colleges =  this.baseDateService.listBaseAcademy();
		//数据回显 查询专业信息
		if(DataUtil.isNotNull(workHourModelVO.getStudentId()) && DataUtil.isNotNull(workHourModelVO.getStudentId().getCollege()) && DataUtil.isNotNull(workHourModelVO.getStudentId().getCollege().getId())){
			majors = this.compService.queryMajorByCollage(workHourModelVO.getStudentId().getCollege().getId());
			if(DataUtil.isNotNull(workHourModelVO.getStudentId()) && DataUtil.isNotNull(workHourModelVO.getStudentId().getMajor()) && DataUtil.isNotNull(workHourModelVO.getStudentId().getMajor().getId())){
				classes = this.compService.queryClassByMajor(workHourModelVO.getStudentId().getMajor().getId());
			}
		}
		//填充默认查询条件
//		int isSchoolTeacher = 1;
//		if(DataUtil.isEquals(ProjectConstants.STUDNET_OFFICE_ORG_ID, orgId) || DataUtil.isNull(orgId)){
//			if(ProjectSessionUtils.checkIsStudent(request)){
//				isSchoolTeacher = 3;//学生
//			}else{
//				isSchoolTeacher = 1;//学生处、校领导、系统管理员
//			}
//		}else{
//			isSchoolTeacher = 2;//二级学院、用工部门
//		}
//		if(DataUtil.isNotNull(workHourModelVO) && DataUtil.isNotNull(workHourModelVO.getOrgWorkHour())){
//			if(isSchoolTeacher == 2){
//				workHourModelVO.getOrgWorkHour().setOrgId(this.orgService.queryOrgById(orgId));
//			}
//			workHourModelVO.getOrgWorkHour().setProcessStatus("PASS");
//			workHourModelVO.getOrgWorkHour().setSubmitStatus(Constants.STATUS_SUBMIT_DICS);
//		}else{
//			OrgWorkHourModel orgWorkHour = new OrgWorkHourModel();
//			if(isSchoolTeacher == 2){
//				orgWorkHour.setOrgId(this.orgService.queryOrgById(orgId));
//			}
//			orgWorkHour.setProcessStatus("PASS");
//			orgWorkHour.setSubmitStatus(Constants.STATUS_SUBMIT_DICS);
//			workHourModelVO.setOrgWorkHour(orgWorkHour);
//		}
		
		//按所在部门 和 角色进行数据过滤
		int queryType = 2;//默认为普通教师
		OrgWorkHourModel orgWorkHour = null;
		//当出现一个人存在多个角色时，按最大的角色进行数据过滤；
		if(this.commonRoleService.checkUserIsExist(userId, "HKY_SUTDENT")){//学生
			queryType = Constants.QUERY_TYPE_STUDENT;
			StudentInfoModel studentId = null;
			
			if(DataUtil.isNotNull(workHourModelVO.getStudentId())){//学生学号条件
				studentId = workHourModelVO.getStudentId();
			}else{
				studentId = new StudentInfoModel();
			}
			studentId.setId(userId);
			workHourModelVO.setStudentId(studentId);
			
			if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour())){//审核通过条件
				orgWorkHour = workHourModelVO.getOrgWorkHour();
			}else{
				orgWorkHour = new OrgWorkHourModel();
			}
			orgWorkHour.setProcessStatus("PASS");
			orgWorkHour.setSubmitStatus(Constants.STATUS_SUBMIT_DICS);
			workHourModelVO.setOrgWorkHour(orgWorkHour);
		}else{
			if(this.commonRoleService.checkUserIsExist(userId, "ADM") || this.commonRoleService.checkUserIsExist(userId, "HKY_SPONSOR_ADMIN") 
					|| this.commonRoleService.checkUserIsExist(userId, "HKY_SCHOOL_LEADER") || DataUtil.isEquals(ProjectConstants.STUDNET_OFFICE_ORG_ID, orgId)){
				//角色：系统管理员   资助管理员_学生处   学校领导  所在部门：学生处
				queryType = Constants.QUERY_TYPE_ADMIN;
				if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour())){//审核通过条件
					orgWorkHour = workHourModelVO.getOrgWorkHour();
				}else{
					orgWorkHour = new OrgWorkHourModel();
				}
				orgWorkHour.setProcessStatus("PASS");
				orgWorkHour.setSubmitStatus(Constants.STATUS_SUBMIT_DICS);
				workHourModelVO.setOrgWorkHour(orgWorkHour);
			}else{//主要有三个角色HKY_DEPART_WORK_STUDY_AMDIN  HKY_COLLEGE_LEADER  HKY_COMMON_DEPART_LEADER
				queryType = Constants.QUERY_TYPE_TEACHER;
				
				if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour())){//审核通过条件 和 教师所在部门
					orgWorkHour = workHourModelVO.getOrgWorkHour();
				}else{
					orgWorkHour = new OrgWorkHourModel();
				}
				orgWorkHour.setOrgId(this.orgService.queryOrgById(orgId));
				orgWorkHour.setProcessStatus("PASS");
				orgWorkHour.setSubmitStatus(Constants.STATUS_SUBMIT_DICS);
				workHourModelVO.setOrgWorkHour(orgWorkHour);
			}
		}
		Page page = this.laborHourService.queryWorkHourPage(workHourModelVO,Page.DEFAULT_PAGE_SIZE,pageNo,queryType);
		model.addAttribute("page", page);
		model.addAttribute("orgs", orgs);
		model.addAttribute("majors", majors);
		model.addAttribute("classes", classes);
		model.addAttribute("collegess", colleges);
		model.addAttribute("isSchoolTeacher", queryType);
		model.addAttribute("workHourModelVO", workHourModelVO);
		model.addAttribute("approveMap", ProjectSessionUtils.getApproveProcessStatus());
		return Constants.MENUKEY_WORK_LABORHOUR + "laborHourList";
	}
	/**
	 * 用工工时填报跳转页面
	 * @param model
	 * @param request
	 * @param response
	 * @param workHourModelVO
	 * @return
	 */
	@RequestMapping("/sponsor/addLaborHour/opt-query/queryOrgWorkHourPage")
	public String queryOrgWorkHourPage(ModelMap model,HttpServletRequest request,HttpServletResponse response,OrgWorkHourModel orgWorkHourModelVO){
		String orgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		int pageNo = this.getPageNo(request.getParameter("page"));
		String isQuery = request.getParameter("isQuery");
		if(DataUtil.isNull(isQuery)){//添加默认查询条件
			this.setDefaultOrgWorkHour(orgWorkHourModelVO, orgId);
		}
		Page page = this.laborHourService.queryOrgWorkHourPage(orgWorkHourModelVO,Page.DEFAULT_PAGE_SIZE,pageNo);
		model.addAttribute("page", page);
		model.addAttribute("orgWorkHourModelVO", orgWorkHourModelVO);
		model.addAttribute("approveMap", ProjectSessionUtils.getApproveProcessStatus());
		return Constants.MENUKEY_WORK_LABORHOUR + "laborHourEditList";
	}
	/**
	 * 增加跳转
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/sponsor/laborHour/opt-add/queryLaborHour")
	public String queryAddLaborHour(ModelMap model,HttpServletRequest request,HttpServletResponse response){
		String isQuery = request.getParameter("isQuery");
		String orgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		String yearAndMonth = DateUtil.getYear()+"-"+DateUtil.getMonth();
		List<Dic> years = this.dicUtil.getDicInfoList("YEAR");//学年
		List<Dic> terms = this.dicUtil.getDicInfoList("TERM");//学期
		OrgWorkHourModel orgWorkHourModelVO = new OrgWorkHourModel();
		orgWorkHourModelVO.setOrgId(this.orgService.queryOrgById(orgId));
		orgWorkHourModelVO.setSchoolYear(SchoolYearUtil.getYearDic());
		orgWorkHourModelVO.setSchoolTerm(SchoolYearUtil.getCurrentTermDic());
		orgWorkHourModelVO.setYearAndMonth(yearAndMonth);
		model.addAttribute("years", years);
		model.addAttribute("terms", terms);
		model.addAttribute("isQuery", isQuery);
		model.addAttribute("orgWorkHourModelPO", orgWorkHourModelVO);
		model.addAttribute("nowDate", yearAndMonth);
		return Constants.MENUKEY_WORK_LABORHOUR + "laborHourEdit";
	}
	/**
	 * 修改跳转
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/sponsor/laborHour/opt-update/editLaborHour")
	public String queryUpdLaborHour(ModelMap model,HttpServletRequest request,HttpServletResponse response,OrgWorkHourModel orgWorkHourModelVO){
		String isQuery = request.getParameter("isQuery");
		List<Dic> years = this.dicUtil.getDicInfoList("YEAR");//学年
		List<Dic> terms = this.dicUtil.getDicInfoList("TERM");//学期
		orgWorkHourModelVO = this.laborHourService.queryOrgWorkHourById(orgWorkHourModelVO.getId());
		orgWorkHourModelVO.setYearAndMonth(orgWorkHourModelVO.getWorkYear()+"-"+orgWorkHourModelVO.getWorkMonth());
		model.addAttribute("years", years);
		model.addAttribute("terms", terms);
		model.addAttribute("isQuery", isQuery);
		model.addAttribute("orgWorkHourModelPO", orgWorkHourModelVO);
		model.addAttribute("orgWorkHourModelList", orgWorkHourModelVO.getWorkHourModels());
		model.addAttribute("nowDate", DateUtil.getYear()+"-"+DateUtil.getMonth());
		return Constants.MENUKEY_WORK_LABORHOUR + "laborHourEdit";
	}
	/**
	 * 填报、修改
	 * @param model
	 * @param request
	 * @param response
	 * @param workHourModelVO
	 * @return
	 */
	@RequestMapping("/sponsor/laborHour/opt-add/editLaborHour")
	public String editLaborHour(ModelMap model,HttpServletRequest request,HttpServletResponse response,OrgWorkHourModel orgWorkHourModelVO){
		String isQuery = request.getParameter("isQuery");
		String orgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		
		List<Dic> years = this.dicUtil.getDicInfoList("YEAR");//学年
		List<Dic> terms = this.dicUtil.getDicInfoList("TERM");//学期
		String yearAndMonth[] = orgWorkHourModelVO.getYearAndMonth().split("-");
		orgWorkHourModelVO.setWorkYear(yearAndMonth[0]);
		orgWorkHourModelVO.setWorkMonth(yearAndMonth[1]);
		List<OrgWorkHourModel> lists = this.laborHourService.queryOrgWorkHourList(orgWorkHourModelVO);
		OrgWorkHourModel orgWorkHourModelPO = null;
		if (lists == null || lists.size() == 0) {//为添加过工时，需要从岗位申请表中取出参加该用工部门中的所有的学生（默认是当前学年学期的，如果当前时间不在某个学期，则查询当前学年）
			orgWorkHourModelPO = new OrgWorkHourModel();
			BeanUtils.copyProperties(orgWorkHourModelVO, orgWorkHourModelPO);
			//查询岗位申请表
			List<WorkApplyModel> workApplyModels = this.workStudyService.queryWorkApplyByWorkOrgId(orgId,orgWorkHourModelVO.getWorkYear(),orgWorkHourModelVO.getWorkMonth(),orgWorkHourModelVO.getSchoolYear(),orgWorkHourModelVO.getSchoolTerm());
			List<WorkHourModel> workHourModels = new ArrayList<WorkHourModel>();
			for (WorkApplyModel wam : workApplyModels) {
				WorkHourModel whm = new WorkHourModel();
				whm.setOrgWorkHour(orgWorkHourModelPO);//添加用工工时部门信息
				whm.setStudentId(wam.getDifficultStudentInfo().getStudent());//添加学生
				whm.setSponsorPosition(wam.getSponsorPosition());//添加助学岗位
				whm.setCardNum(wam.getDifficultStudentInfo().getStudent().getBankCode());//银行卡账号
				whm.setWorkSalary(Double.parseDouble(this.commonConfigService.getStudyWorkSalary()));
				whm.setStatus(Constants.STATUS_NORMAL_DICS);
				workHourModels.add(whm);
			}
			model.addAttribute("orgWorkHourModelList", workHourModels);
		}else{//已经添加过工时
			orgWorkHourModelPO = lists.get(0);
			orgWorkHourModelPO.setYearAndMonth(orgWorkHourModelVO.getYearAndMonth());
			model.addAttribute("orgWorkHourModelList", orgWorkHourModelPO.getWorkHourModels());
		}
		model.addAttribute("orgWorkHourModelPO", orgWorkHourModelPO);
		model.addAttribute("years", years);
		model.addAttribute("terms", terms);
		model.addAttribute("nowDate", DateUtil.getYear()+"-"+DateUtil.getMonth());
		model.addAttribute("isQuery", isQuery);
		return Constants.MENUKEY_WORK_LABORHOUR + "laborHourEdit";
	}
	/**
	 * 添加和修改  保存   提交
	 * @param model
	 * @param request
	 * @param response
	 * @param workHourModelVO
	 * @return
	 */
	@RequestMapping("/sponsor/laborHour/opt-save/saveLaborHourList")
	public String saveLaborHour(ModelMap model,HttpServletRequest request,HttpServletResponse response,WorkHourModelList workHourModelList){
		String saveState= request.getParameter("saveState");
		String userId = request.getParameter("userId");//下一节点办理人ID
		OrgWorkHourModel orgWorkHourModel = null;
		if(DataUtil.isNotNull(workHourModelList.getOrgWorkHourModel().getId())){//修改
			orgWorkHourModel = this.laborHourService.queryOrgWorkHourById(workHourModelList.getOrgWorkHourModel().getId());
			for (WorkHourModel workHourModelVO : workHourModelList.getWorkHourList()){
				WorkHourModel workHourModel = this.laborHourService.getWorkHourById(workHourModelVO.getId());
				workHourModel.setWorkHour(workHourModelVO.getWorkHour());
				workHourModel.setTotalSalary(workHourModelVO.getTotalSalary());
				this.laborHourService.updateWorkHour(workHourModel);
			}
		}else{//添加
			orgWorkHourModel = new OrgWorkHourModel();
			BeanUtils.copyProperties(workHourModelList.getOrgWorkHourModel(), orgWorkHourModel);
			orgWorkHourModel.setStatus(Constants.STATUS_NORMAL_DICS);//删除状态为正常
			orgWorkHourModel.setSubmitStatus(Constants.STATUS_SAVE_DIC);//保存状态为修改
			this.laborHourService.saveOrgWorkHour(orgWorkHourModel);
			for (WorkHourModel workHourModelVO : workHourModelList.getWorkHourList()) {
				WorkHourModel workHourModel = new WorkHourModel();
				BeanUtils.copyProperties(workHourModelVO, workHourModel);
				StudentInfoModel studentInfoModel = this.studentCommonService.queryStudentById(workHourModelVO.getStudentId().getId());
				studentInfoModel.setId(workHourModelVO.getStudentId().getId());
				workHourModel.setStudentId(studentInfoModel);//保存学生
				workHourModel.setStatus(Constants.STATUS_NORMAL_DICS);
				workHourModel.setCardNum(studentInfoModel.getBankCode());
				workHourModel.setOrgWorkHour(orgWorkHourModel);
				this.laborHourService.saveWorkHour(workHourModel);
			}
		}
		if(DataUtil.isEquals("0", saveState)){//保存状态
			orgWorkHourModel.setSubmitStatus(Constants.STATUS_SAVE_DICS);
			this.laborHourService.updateOrgWorkHour(orgWorkHourModel);
		}
		if(DataUtil.isEquals("1", saveState)){//提交状态
			orgWorkHourModel.setSubmitStatus(Constants.STATUS_SUBMIT_DICS);
			this.laborHourService.updateOrgWorkHour(orgWorkHourModel);
			this.initCurProcessInstance(orgWorkHourModel, userId);
		}
		return "redirect:/sponsor/addLaborHour/opt-query/queryOrgWorkHourPage.do";
	}
	/**
	 * 初始化当前流程
	 * @param workHourModel
	 * @param userId
	 */
	private void initCurProcessInstance(OrgWorkHourModel orgWorkHourModel,String userId){
		User initiator = new User(this.sessionUtil.getCurrentUserId());//发起人
		User nextApprover = new User(userId);//下一接节点办理人
		//实例化流程 
		ApproveResult approveResult = this.flowInstanceService.initProcessInstance(orgWorkHourModel.getId(), "SPONSOR_LABOR_WORK_APPROVE", initiator, nextApprover, ProjectConstants.IS_APPROVE_ENABLE);
		orgWorkHourModel.setNextApprover(nextApprover);
		orgWorkHourModel.setApproverStatus(approveResult.getApproveStatus());
		orgWorkHourModel.setProcessStatus("APPROVEING");
		//初始化下一节点办理人
		this.flowInstanceService.initNextApprover(orgWorkHourModel.getId(), userId, ProjectConstants.IS_APPROVE_ENABLE);
	}
	/**
	 * 逻辑删除
	 * @param model
	 * @param request
	 * @param response
	 * @param workHourModelVO
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/sponsor/laborHour/opt-del/delLaborHour"},produces={"text/plain;charset=UTF-8"})
	public String delLaborHour(ModelMap model,HttpServletRequest request,HttpServletResponse response,OrgWorkHourModel orgWorkHourModelVO){
		OrgWorkHourModel orgWorkHourModelPO = this.laborHourService.queryOrgWorkHourById(orgWorkHourModelVO.getId());
		if(DataUtil.isEquals(Constants.STATUS_SAVE_DICS.getId(), orgWorkHourModelPO.getSubmitStatus().getId())){//处于保存状态的数据才可修改
			orgWorkHourModelPO.setStatus(Constants.STATUS_DELETED_DICS);
			this.laborHourService.updateOrgWorkHour(orgWorkHourModelPO);
		}
		return "success";
	}
	/**
	 * 查看详细页面跳转
	 * @param model
	 * @param request
	 * @param response
	 * @param workHourModelVO
	 * @return
	 */
	@RequestMapping("/sponsor/laborHour/view/viewLaborHour")
	public String viewLaborHour(ModelMap model,HttpServletRequest request,HttpServletResponse response,WorkHourModel workHourModelVO){
		WorkHourModel workHourModelPO = this.laborHourService.getWorkHourById(workHourModelVO.getId());
		model.addAttribute("model",workHourModelPO);
		return Constants.MENUKEY_WORK_LABORHOUR + "laborHourView";
	}
	/**
	 * 查看全部的某部门某年某月的用工工时
	 * @param model
	 * @param request
	 * @param response
	 * @param orgWorkHourModelVO
	 * @return
	 */
	@RequestMapping("/sponsor/laborHour/view/queryLaborHourAllList")
	public String queryLaborHourAllList(ModelMap model,HttpServletRequest request,HttpServletResponse response,OrgWorkHourModel orgWorkHourModelVO){
		OrgWorkHourModel orgWorkHourModelPO = this.laborHourService.getOrgWorkHourById(orgWorkHourModelVO.getId());
		List<FlowInstancePo> flowInstances = this.flowInstanceService.geCurProcessHistory(orgWorkHourModelPO.getId(), ProjectConstants.IS_APPROVE_ENABLE);
		model.addAttribute("flowInstances", flowInstances);
		model.addAttribute("orgWorkHourModelPO",orgWorkHourModelPO);
		model.addAttribute("orgWorkHourModelList",orgWorkHourModelPO.getWorkHourModels());
		return Constants.MENUKEY_WORK_LABORHOUR + "laborHourAllView";
	}
	/**
	 * 审批列表
	 * @param model
	 * @param request
	 * @param response
	 * @param workHourModelVO
	 * @return
	 */
	@RequestMapping("/sponsor/approveSchoolLaborHour/opt-approve/querySchoolWorkHourList")
	public String querySchoolWorkHourList(ModelMap model,HttpServletRequest request,HttpServletResponse response,OrgWorkHourModel orgWorkHourModelVO){
		//只有学生处的教师才可以跳转到审批列表页面
		String currentUserId = sessionUtil.getCurrentUserId();
		int pageNo = this.getPageNo(request.getParameter("page"));
		String orgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		boolean isSchoolTeacher = true;
		if(!DataUtil.isEquals(ProjectConstants.STUDNET_OFFICE_ORG_ID, orgId)){
			isSchoolTeacher = false;
			if(DataUtil.isNotNull(orgWorkHourModelVO)){
				orgWorkHourModelVO.setOrgId(this.orgService.queryOrgById(orgId));
			}
		}
		List<Org> orgs = this.orgService.queryOrg();
		orgWorkHourModelVO.setSubmitStatus(Constants.STATUS_SUBMIT_DICS);
		orgWorkHourModelVO.setNextApprover(new User(currentUserId));
		String[] objectIds = flowInstanceService.getObjectIdByProcessKey("SPONSOR_LABOR_WORK_APPROVE",currentUserId);
		Page page = this.laborHourService.queryApproveOrgWorkHourPage(orgWorkHourModelVO, Page.DEFAULT_PAGE_SIZE, pageNo,objectIds);
		model.addAttribute("page", page);
		model.addAttribute("orgs", orgs);
		model.addAttribute("currentUserId", currentUserId);
		model.addAttribute("isSchoolTeacher", isSchoolTeacher);
		model.addAttribute("orgWorkHourModelVO", orgWorkHourModelVO);
		model.addAttribute("approveMap", ProjectSessionUtils.getApproveProcessStatus());
		return Constants.MENUKEY_WORK_LABORHOUR + "approve/schoolApproveLaborHourList";
	}
	/**
	 * 跳转到审批页面
	 * @param model
	 * @param request
	 * @param response
	 * @param workHourModelVO
	 * @return
	 */
	@RequestMapping("/sponsor/approveLaborHour/opt-approve/queryApproveWorkHourListInfo")
	public String queryApproveWorkHourListInfo(ModelMap model,HttpServletRequest request,HttpServletResponse response,OrgWorkHourModel orgWorkHourModelVO){
		OrgWorkHourModel orgWorkHourModelPO = this.laborHourService.queryOrgWorkHourById(orgWorkHourModelVO.getId());
		model.addAttribute("orgWorkHourModelPO",orgWorkHourModelPO);
		model.addAttribute("orgWorkHourModelList",orgWorkHourModelPO.getWorkHourModels());
		return Constants.MENUKEY_WORK_LABORHOUR + "approve/collegeApproveLaborHourInfo";
	}
	/**
	 * 保存审批结果
	 * @param model
	 * @param request
	 * @param id
	 * @param userId
	 * @param nextApproverId
	 * @param approveStatus
	 * @param processStatusCode
	 * @param suggest
	 * @return
	 */
	@RequestMapping("/sponsor/approveLaborHour/opt-save/saveApproveLaborHour")
	public String saveApproveLaborHour(ModelMap model,HttpServletRequest request,String id,String nextApproverId,String approveKey,String approveStatus,String processStatusCode,String approveSeq){
		OrgWorkHourModel orgWorkHourModel = this.laborHourService.queryOrgWorkHourById(id);//保存workhour实体
		User nextApprover = new User(nextApproverId);
		orgWorkHourModel.setNextApprover(nextApprover);
		orgWorkHourModel.setApproverStatus(approveStatus);
		orgWorkHourModel.setProcessStatus(processStatusCode);
		this.laborHourService.updateOrgWorkHour(orgWorkHourModel);
		return "redirect:/sponsor/approveSchoolLaborHour/opt-approve/querySchoolWorkHourList.do";
	}
	/**
	 * 计算导出页数
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/sponsor/laborHour/nsm/exportLaborHourList")
	public String exportLaborHourList(ModelMap model,HttpServletRequest request,HttpServletResponse response){
		int exportSize = Integer.valueOf(request.getParameter("exportSize")).intValue();
		int pageTotalCount = Integer.valueOf(request.getParameter("pageTotalCount")).intValue();
		int maxNumber = 0;
		if(pageTotalCount < exportSize){
			maxNumber = 1;
		}else if (pageTotalCount % exportSize == 0)
			maxNumber = pageTotalCount / exportSize;
		else{
			maxNumber = pageTotalCount / exportSize + 1;
		}
		model.addAttribute("exportSize", Integer.valueOf(exportSize));
		model.addAttribute("maxNumber", Integer.valueOf(maxNumber));
		if (maxNumber <= 500)
			model.addAttribute("isMore", "false");
		else {
			model.addAttribute("isMore", "true");
		}
		return Constants.MENUKEY_WORK_LABORHOUR + "exportLaborHourList";
	}
	/**
	 * 列表页面导出----导出审批状态通过的数据  
	 * @param model
	 * @param request
	 * @param response
	 * @param workHourModelVO
	 */
	@RequestMapping("/sponsor/laborHour/opt-query/exportLaborHourList")
	public void exportLaborHourList(ModelMap model,HttpServletRequest request,HttpServletResponse response,WorkHourModel workHourModelVO,String userQuery_exportSize,String userQuery_exportPage){
		int pageSize = userQuery_exportSize != null ? Integer.parseInt(userQuery_exportSize) : 25000;
		int pageNo = userQuery_exportPage != null ? Integer.parseInt(userQuery_exportPage) : 1;
		String orgId = (String)request.getSession().getAttribute("_teacher_orgId");
		String userId = sessionUtil.getCurrentUserId();
		//按所在部门 和 角色进行数据过滤
		int queryType = 2;//默认为普通教师
		OrgWorkHourModel orgWorkHour = null;
		//当出现一个人存在多个角色时，按最大的角色进行数据过滤；
		if(this.commonRoleService.checkUserIsExist(userId, "HKY_SUTDENT")){//学生
			queryType = Constants.QUERY_TYPE_STUDENT;
			StudentInfoModel studentId = null;
			
			if(DataUtil.isNotNull(workHourModelVO.getStudentId())){//学生学号条件
				studentId = workHourModelVO.getStudentId();
			}else{
				studentId = new StudentInfoModel();
			}
			studentId.setId(userId);
			workHourModelVO.setStudentId(studentId);
			
			if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour())){//审核通过条件
				orgWorkHour = workHourModelVO.getOrgWorkHour();
			}else{
				orgWorkHour = new OrgWorkHourModel();
			}
			orgWorkHour.setProcessStatus("PASS");
			orgWorkHour.setSubmitStatus(Constants.STATUS_SUBMIT_DICS);
			workHourModelVO.setOrgWorkHour(orgWorkHour);
		}else{
			if(this.commonRoleService.checkUserIsExist(userId, "ADM") || this.commonRoleService.checkUserIsExist(userId, "HKY_SPONSOR_ADMIN") 
					|| this.commonRoleService.checkUserIsExist(userId, "HKY_SCHOOL_LEADER") || DataUtil.isEquals(ProjectConstants.STUDNET_OFFICE_ORG_ID, orgId)){
				//角色：系统管理员   资助管理员_学生处   学校领导  所在部门：学生处
				queryType = Constants.QUERY_TYPE_ADMIN;
				if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour())){//审核通过条件
					orgWorkHour = workHourModelVO.getOrgWorkHour();
				}else{
					orgWorkHour = new OrgWorkHourModel();
				}
				orgWorkHour.setProcessStatus("PASS");
				orgWorkHour.setSubmitStatus(Constants.STATUS_SUBMIT_DICS);
				workHourModelVO.setOrgWorkHour(orgWorkHour);
			}else{//主要有三个角色HKY_DEPART_WORK_STUDY_AMDIN  HKY_COLLEGE_LEADER  HKY_COMMON_DEPART_LEADER
				queryType = Constants.QUERY_TYPE_TEACHER;
				
				if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour())){//审核通过条件 和 教师所在部门
					orgWorkHour = workHourModelVO.getOrgWorkHour();
				}else{
					orgWorkHour = new OrgWorkHourModel();
				}
				orgWorkHour.setOrgId(this.orgService.queryOrgById(orgId));
				orgWorkHour.setProcessStatus("PASS");
				orgWorkHour.setSubmitStatus(Constants.STATUS_SUBMIT_DICS);
				workHourModelVO.setOrgWorkHour(orgWorkHour);
			}
		}
		Page page = this.laborHourService.queryWorkHourPage(workHourModelVO,pageSize,pageNo,queryType);
		this.exportExcel((List<WorkHourModel>)page.getResult(), workHourModelVO, response);
	}
	/**
	 * TODO:用工工时填报页面列表页面导出----导出只要填报过的工时都可以导出 现无此功能
	 * @param model
	 * @param request
	 * @param response
	 * @param workHourModelVO
	 */
	@RequestMapping("/sponsor/laborHour/opt-query/exportEditLaborHourList")
	public void exportEditLaborHourList(ModelMap model,HttpServletRequest request,HttpServletResponse response,WorkHourModel workHourModelVO,String userQuery_exportSize,String userQuery_exportPage){
		int pageSize = userQuery_exportSize != null ? Integer.parseInt(userQuery_exportSize) : 25000;
		int pageNo = userQuery_exportPage != null ? Integer.parseInt(userQuery_exportPage) : 1;
//		Page page = this.laborHourService.queryWorkHourPage(workHourModelVO,pageSize,pageNo,queryType);
//		this.exportExcel((List<WorkHourModel>)page.getResult(), workHourModelVO, response);
	}
	/**
	 * 导出公共方法
	 * @param excelName
	 * @param excelId
	 * @param listMap
	 */
	private void exportExcel(List<WorkHourModel> laborHourList,WorkHourModel workHourModelVO,HttpServletResponse response){
		List listMap = new ArrayList();
		double sumSalary = 0.0;
		String title = "杭州科技职业技术学院学生勤工助学酬金发放表";
		boolean isTrue = true;
		int countColumn = 12;//excel文件中的列数-1
		String excelName = "export_laborHour.xls";//导出excel模板的文件名称
		String excelId = "exportLaborHour";//bdpExcel中的导出ID
		if(DataUtil.isNotNull(workHourModelVO.getOrgWorkHour()) && DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getWorkYear()) && DataUtil.isNotNull(workHourModelVO.getOrgWorkHour().getWorkMonth())){//生成导出文件名
			title = workHourModelVO.getOrgWorkHour().getWorkYear() + "年" + workHourModelVO.getOrgWorkHour().getWorkMonth() + "月" + title;
			excelName = "export_laborHourByCond.xls";
			excelId = "exportLaborHourByCond";
			countColumn = 10;
			isTrue = false;
		}
		for (int i = 0; i < laborHourList.size(); i++) {
			Map<String, Object> newMap = new HashMap<String, Object>();
			WorkHourModel workHourModel = laborHourList.get(i);
			newMap.put("sortId", i+1);
			//newMap.put("protocalNo", workHourModel.getAgreementNum());
			if(isTrue){
				newMap.put("schoolYear", workHourModel.getOrgWorkHour().getWorkYear());
				newMap.put("workMonth", workHourModel.getOrgWorkHour().getWorkMonth());
			}
			newMap.put("stuName", workHourModel.getStudentId().getName());
			newMap.put("stuNo", workHourModel.getStudentId().getStuNumber());
			newMap.put("className", workHourModel.getStudentId().getClassId().getClassName());
			newMap.put("collegeName", workHourModel.getStudentId().getCollege().getName());
			newMap.put("orgName", workHourModel.getOrgWorkHour().getOrgId().getName());
			newMap.put("workHour", workHourModel.getWorkHour());
			newMap.put("totalSalary", workHourModel.getTotalSalary());
			newMap.put("bank", workHourModel.getStudentId().getBank());
			newMap.put("cardNum", workHourModel.getStudentId().getBankCode());
			sumSalary += workHourModel.getTotalSalary();
			listMap.add(newMap);
		}
		try {
			HSSFWorkbook wb = this.excelService.dynamicExportData(excelName,excelId, listMap);
			
			HSSFSheet sheet = wb.getSheetAt(0);//添加sheet
			
			HSSFCellStyle HeadStyle = (HSSFCellStyle) wb.createCellStyle();//表头单元格样式
			HeadStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			HeadStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			HeadStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			HeadStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			HeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
			
			HSSFCellStyle countStyle = (HSSFCellStyle) wb.createCellStyle();//统计单元格样式
			countStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			countStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			countStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			countStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			countStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
			
			HSSFCellStyle tailStyle = (HSSFCellStyle) wb.createCellStyle();//表尾单元格样式
			tailStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
			
			HSSFFont bigFont = wb.createFont();//创建大字体
			bigFont.setFontHeightInPoints((short) 24);
			bigFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			HeadStyle.setFont(bigFont); 
			
			HSSFRow headRow = sheet.createRow(0);
			for(int i=0;i<countColumn;i++){
				headRow.createCell(i).setCellStyle(HeadStyle);
			}
			HSSFCell headCell = headRow.createCell(0);//表头需要合并单元格的地方
			headCell.setCellValue(title);// 跨单元格显示的数据
			headCell.setCellType(HSSFCell.CELL_TYPE_STRING);
			headCell.setCellStyle(HeadStyle);
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,countColumn-1));
			sheet.getRow(0).setHeightInPoints(35);//设置合并单元格单元格的高度
			
			HSSFRow countRow = sheet.createRow(sheet.getLastRowNum()+1);//添加统计行
			for(int i=0;i<countColumn;i++){
				countRow.createCell(i).setCellStyle(countStyle);
			}
			HSSFCell countCell = countRow.getCell(0);
			countCell.setCellValue("本页合计："+RmbUtil.NumberToChinese(sumSalary+"")+"                  （小写） "+sumSalary+" 元");
			countCell.setCellType(HSSFCell.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 0, countColumn-1));
			
			HSSFRow tailRow = sheet.createRow(sheet.getLastRowNum()+1);//添加表尾
			tailRow.setHeightInPoints(24);
			for(int i=0;i<countColumn;i++){
				tailRow.createCell(i);
			}
			HSSFCell tailCell = tailRow.getCell(0);
			tailCell.setCellValue("制表人：                 财务审核人：                财务负责人：               部门负责人：");
			tailCell.setCellStyle(tailStyle);
			tailCell.setCellType(HSSFCell.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 0, countColumn-1));
			
			String filename = title + ".xls";
			response.setContentType("application/x-excel");
			response.setHeader("Content-disposition", "attachment;filename=" + new String(filename.getBytes("GBK"), "iso-8859-1"));
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
	 * 为orgWorkHour设置默认值
	 * @param orgWorkHourModelVO
	 * @param orgId
	 */
	private void setDefaultOrgWorkHour(OrgWorkHourModel orgWorkHourModelVO,String orgId){
		orgWorkHourModelVO.setWorkYear(DateUtil.getYear());
		orgWorkHourModelVO.setWorkMonth(DateUtil.getMonth());//保持前台添加月份的统一性:09
		orgWorkHourModelVO.setOrgId(this.orgService.queryOrgById(orgId));
	}
	/**
	 * 返回页数
	 * @param pageNo
	 * @return
	 */
	private int getPageNo(String pageNo){
		return DataUtil.isNotNull(pageNo) ? Integer.parseInt(pageNo) : 1;
	}
}
