package com.uws.sponsor.controller;

import java.io.File;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.ICommonConfigService;
import com.uws.common.service.ICommonRoleService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.core.base.BaseController;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.excel.service.IExcelService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.DateUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.sponsor.TemporaryWorkStudyModel;
import com.uws.sponsor.service.ITemporaryWorkService;
import com.uws.sponsor.util.Constants;
import com.uws.sponsor.util.RmbUtil;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.IDicService;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.user.model.Org;
import com.uws.user.model.User;
import com.uws.user.service.IOrgService;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/**
 * @Title TemporaryWorkController.java
 * @Package com.uws.sponsor.controller
 * @Description 临时工勤工助学Controller
 * @date 2015-7-31  上午11:39:31
 */
@Controller
public class TemporaryWorkController extends BaseController {
	@Autowired
	private ITemporaryWorkService temporaryWorkService;
	//查询学院
	@Autowired
	private IBaseDataService baseDateService;
	//查询班级
	@Autowired
	private IExcelService excelService;
	// sessionUtil工具类
	private SessionUtil sessionUtil = SessionFactory.getSession(Constants.MENUKEY_WORK_TEMPORARY);
	
	private FileUtil fileUtil = FileFactory.getFileUtil();
	//字典
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IDicService dicService;
	@Autowired
	private ICommonConfigService commonConfigService;
	@Autowired
	private ICommonRoleService commonRoleService;
	
	@Autowired
	private IOrgService orgService;
	
	/**
	 * 临时勤工助学列表查询
	 * @param modelMap
	 * @param request
	 * @param response
	 * @param temporaryWorkStudyModel
	 * @return
	 */
	@RequestMapping("/sponsor/temporaryWork/opt-query/queryTemporaryWorkList")
	public String queryTemporaryWorkList(ModelMap model,HttpServletRequest request ,HttpServletResponse response, TemporaryWorkStudyModel temporaryWorkStudyModelVO){
		String strPageNo = request.getParameter("pageNo");
		int pageNo = strPageNo != null ? Integer.parseInt(strPageNo) : 1;
		List<BaseAcademyModel> baseAcademyModels = this.baseDateService.listBaseAcademy();//学院
		List<BaseClassModel> baseClassModels = new ArrayList<BaseClassModel>();//班级
		List<Dic> years = this.dicUtil.getDicInfoList("YEAR");//学年
		List<Dic> terms = this.dicUtil.getDicInfoList("TERM");//学期
		if(DataUtil.isNotNull(temporaryWorkStudyModelVO.getStudentId()) && DataUtil.isNotNull(temporaryWorkStudyModelVO.getStudentId()) && DataUtil.isNotNull(temporaryWorkStudyModelVO.getStudentId().getCollege()) && DataUtil.isNotNull(temporaryWorkStudyModelVO.getStudentId().getCollege().getId())){
			baseClassModels = this.baseDateService.listBaseClass("", "", temporaryWorkStudyModelVO.getStudentId().getCollege().getId());
		}
		String userId = sessionUtil.getCurrentUserId();
		//按所在部门 和 角色进行数据过滤
		int queryType = 2;//默认为普通教师
		String orgId = (String)request.getSession().getAttribute("_teacher_orgId");
		//当出现一个人存在多个角色时，按最大的角色进行数据过滤
		if(this.commonRoleService.checkUserIsExist(userId, "HKY_SUTDENT")){//学生
			queryType = Constants.QUERY_TYPE_STUDENT;
			StudentInfoModel studentId = null;
			if(DataUtil.isNotNull(temporaryWorkStudyModelVO.getStudentId())){
				studentId = temporaryWorkStudyModelVO.getStudentId();
			}else{
				studentId = new StudentInfoModel();
			}
			studentId.setId(userId);
			temporaryWorkStudyModelVO.setStudentId(studentId);
		}else{
			if(this.commonRoleService.checkUserIsExist(userId, "ADM") || this.commonRoleService.checkUserIsExist(userId, "HKY_SPONSOR_ADMIN") || this.commonRoleService.checkUserIsExist(userId, "HKY_SCHOOL_LEADER") || DataUtil.isEquals(ProjectConstants.STUDNET_OFFICE_ORG_ID, orgId)){
				//角色：系统管理员   资助管理员_学生处   学校领导  所在部门：学生处
				queryType = Constants.QUERY_TYPE_ADMIN;
			}else{//主要有三个角色HKY_DEPART_WORK_STUDY_AMDIN  HKY_COLLEGE_LEADER  HKY_COMMON_DEPART_LEADER
				queryType = Constants.QUERY_TYPE_TEACHER;
				Org org = new Org();
				org.setId(orgId);
				temporaryWorkStudyModelVO.setOrgId(org);
			}
		}
		Page page = this.temporaryWorkService.queryTemporaryWorkInfo(temporaryWorkStudyModelVO, Page.DEFAULT_PAGE_SIZE, pageNo,queryType);
		model.addAttribute("baseAcademyModels", baseAcademyModels);
		model.addAttribute("baseClassModels", baseClassModels);
		model.addAttribute("years", years);
		model.addAttribute("terms", terms);
		model.addAttribute("temporaryWorkStudyModel", temporaryWorkStudyModelVO);
		model.addAttribute("page",page);
		return Constants.MENUKEY_WORK_TEMPORARY + "temporaryWorkList";
	}
	/**
	 * 添加和修改--跳转页面 
	 * @param modelMap
	 * @param temporaryWorkStudyModel
	 * @return
	 */
	@RequestMapping({"/sponsor/temporaryWork/opt-add/editTemporaryWorkInfo","/sponsor/temporaryWork/opt-update/editTemporaryWorkInfo","/sponsor/temporaryWork/view/queryTemporaryWorkInfo"})
	public String editTemporaryWorkInfo(ModelMap model,HttpServletRequest request ,HttpServletResponse response,TemporaryWorkStudyModel temporaryWorkStudyModelVO){
		String isDetail = request.getParameter("isDetail");
		String id = temporaryWorkStudyModelVO.getId();
		List<Dic> years = this.dicUtil.getDicInfoList("YEAR");//学年
		List<Dic> terms = this.dicUtil.getDicInfoList("TERM");//学期
		model.addAttribute("years", years);
		model.addAttribute("terms", terms);
		if(DataUtil.isNotNull(id)){//修改填充数据
			TemporaryWorkStudyModel temporaryWorkStudyModelPO = this.temporaryWorkService.queryTemporaryWorkStudyById(id);
			model.addAttribute("model", temporaryWorkStudyModelPO);
		}else{//查看填充数据   添加 当前学年和当前学期
			model.addAttribute("nowYear", SchoolYearUtil.getYearDic());
			model.addAttribute("nowTerm", SchoolYearUtil.getCurrentTermDic());
			model.addAttribute("nowMonth", DateUtil.getMonth());
		}
		if(DataUtil.isEquals("true", isDetail)){
			return Constants.MENUKEY_WORK_TEMPORARY + "temporaryWorkView";
		}else{
			return Constants.MENUKEY_WORK_TEMPORARY + "temporaryWorkEdit";
		}
	}
	/**
	 * 修改排重
	 * @param request
	 * @param response
	 * @param temporaryWorkStudyModelVO
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value ={ "/sponsor/temporaryWork/opt-query/queryUpdateTemporaryWork" }, produces ={ "text/plain;charset=UTF-8" })
	public String queryUpdateTemporaryWork(HttpServletRequest request,HttpServletResponse response,TemporaryWorkStudyModel temporaryWorkStudyModelVO){
		String result = "false";
		//同一个学号、同一学年、同一月份、同一用工部门、查询出的TemporaryWorkStudyModel最多只有一个
		List<TemporaryWorkStudyModel> lists = this.temporaryWorkService.queryAddTemporaryWorkInfoByCond(temporaryWorkStudyModelVO);
		if(lists != null && lists.size() > 0){
			for (TemporaryWorkStudyModel temporaryWorkStudyModel : lists) {
				if(!DataUtil.isEquals(temporaryWorkStudyModel.getId(), temporaryWorkStudyModelVO.getId())){
					result = "true";
					break;
				}
			}
		}
		return result;
	}
	/**
	 * 排重：添加
	 * @param request
	 * @param response
	 * @param temporaryWorkStudyModel
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value ={ "/sponsor/temporaryWork/opt-query/queryAddTemporaryWork" }, produces ={ "text/plain;charset=UTF-8" })
	public String queryAddTemporaryWork(HttpServletRequest request,HttpServletResponse response,TemporaryWorkStudyModel temporaryWorkStudyModelVO){
		String result = "false";
		if(this.isExistTemporaryWorkStudyModel(temporaryWorkStudyModelVO)){
			result = "true";
		}else{
			temporaryWorkStudyModelVO.setWorkSalary(Double.parseDouble(this.commonConfigService.getTempWorkSalary()));
			temporaryWorkStudyModelVO.setTotalSalary(Math.round((Double.parseDouble(this.commonConfigService.getTempWorkSalary()) * temporaryWorkStudyModelVO.getWorkHour() * 100))/100.0);//计算总薪资
			temporaryWorkStudyModelVO.setOperator(new User(this.sessionUtil.getCurrentUserId()));//添加操作人
			temporaryWorkStudyModelVO.setStatus(Constants.STATUS_NORMAL_DICS);
			String orgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
			if(DataUtil.isNotNull(orgId)){
				Org org = new Org();//设置当前用户的所在org为该临时职工的实际所在部门 
				org.setId(orgId);
				temporaryWorkStudyModelVO.setOrgId(org);
			}
			this.temporaryWorkService.saveTemporaryWorkInfo(temporaryWorkStudyModelVO);
		}
		
		return result;
	}
	/**
	 * 修改保存
	 * @param modelMap
	 * @param request
	 * @param response
	 * @param temporaryWorkStudyModel
	 * @return
	 */
	@RequestMapping("/sponsor/temporaryWork/opt-save/saveTemporaryWork")
	public String saveTemporaryWork(ModelMap modelMap,HttpServletRequest request,HttpServletResponse response,TemporaryWorkStudyModel temporaryWorkStudyModelVO){
		TemporaryWorkStudyModel temporaryWorkStudyModelPO = new TemporaryWorkStudyModel();
		String id = temporaryWorkStudyModelVO.getId();
		temporaryWorkStudyModelPO = this.temporaryWorkService.queryTemporaryWorkStudyById(id);
		BeanUtils.copyProperties(temporaryWorkStudyModelVO, temporaryWorkStudyModelPO, new String[]{"id","orgId","operator","createTime","totalSalary","workSalary","studentId","status"});
		temporaryWorkStudyModelPO.setWorkHour(temporaryWorkStudyModelVO.getWorkHour());
		temporaryWorkStudyModelPO.setTotalSalary(Math.round(temporaryWorkStudyModelPO.getWorkSalary() * temporaryWorkStudyModelVO.getWorkHour()*100)/100.0);//计算总薪资
		this.temporaryWorkService.updateTemporaryWorkInfo(temporaryWorkStudyModelPO);
		return "redirect:/sponsor/temporaryWork/opt-query/queryTemporaryWorkList.do";
	}
	/**
	 * 逻辑删除
	 * @param temporaryWorkStudyModelVo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/sponsor/temporaryWork/opt-del/delTemporaryWork"},produces={"text/plain;charset=UTF-8"})
	public String delTemporaryWork(TemporaryWorkStudyModel temporaryWorkStudyModelVO){
		TemporaryWorkStudyModel temporaryWorkStudyModelPO = this.temporaryWorkService.queryTemporaryWorkStudyById(temporaryWorkStudyModelVO.getId());
		temporaryWorkStudyModelPO.setStatus(Constants.STATUS_DELETED_DICS);
		this.temporaryWorkService.updateTemporaryWorkInfo(temporaryWorkStudyModelPO);//逻辑删除
		return "success";
	}
	/**
	 * 批量逻辑删除
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/sponsor/temporaryWork/opt-del/delMultTemporaryWork"},produces={"text/plain;charset=UTF-8"})
	public String delMultTemporaryWork(ModelMap modelMap,HttpServletRequest request,HttpServletResponse response,String ids){
		TemporaryWorkStudyModel temporaryWorkStudyModel = new TemporaryWorkStudyModel();
		temporaryWorkStudyModel.setStatus(Constants.STATUS_DELETED_DICS);
		this.temporaryWorkService.updateMultTemporaryWorkByIds(temporaryWorkStudyModel,ids.split(","));
		return "success";
	}
	/**
	 * 导入跳转页面
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/sponsor/temporaryWork/opt-query/importTemporaryWorkInit")
	public String importTemporaryWorkInit(ModelMap modelMap){
		List<Dic> years = this.dicUtil.getDicInfoList("YEAR");//学年
		List<Dic> terms = this.dicUtil.getDicInfoList("TERM");//学期
		modelMap.addAttribute("years", years);
		modelMap.addAttribute("terms", terms);
		return Constants.MENUKEY_WORK_TEMPORARY + "temporaryWorkImport";
	}
	/**
	 * 排重：通过填写的学年、学期、月份和用工部门来判断数据之前是否导入过
	 * @param request
	 * @param response
	 * @param temporaryWorkStudyModelVO
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value ={ "/sponsor/temporaryWork/opt-query/queryImportTemporaryWork" }, produces ={ "text/plain;charset=UTF-8" })
	public String queryImportTemporaryWork(HttpServletRequest request,HttpServletResponse response,TemporaryWorkStudyModel temporaryWorkStudyModelVO){
		String result = "false";
		if(this.isExistTemporaryWorkStudyModel(temporaryWorkStudyModelVO)){
			result = "true";
		}
		return result;
	}
	/**
	 * 排重导入，如果之前导入过则将之前的数据逻辑删除，然后导入；否则直接导入
	 * @param model
	 * @param file
	 * @param maxSize
	 * @param allowedExt
	 * @param request
	 * @param session
	 * @param temporaryWorkStudyModel
	 * @return
	 */
	@RequestMapping("/sponsor/temporaryWork/opt-query/importTemporaryWork")
	public String importTemporaryWork(ModelMap model, @RequestParam("file") MultipartFile file,String maxSize, String allowedExt, HttpServletRequest request, HttpSession session,TemporaryWorkStudyModel temporaryWorkStudyModelVO){
		List errorText = new ArrayList();
		MultipartFileValidator validator = new MultipartFileValidator();
		if (org.apache.commons.lang.StringUtils.isNotEmpty(allowedExt)) {
			validator.setAllowedExtStr(allowedExt.toLowerCase());
		}
		if (org.apache.commons.lang.StringUtils.isNotEmpty(maxSize))
			validator.setMaxSize(Long.valueOf(maxSize).longValue());
		else {
			validator.setMaxSize(setMaxSize());
		}
		String returnValue = validator.validate(file);
		if (!returnValue.equals("")) {
			errorText.add(returnValue);
			model.addAttribute("errorText", errorText);
			model.addAttribute("importFlag", Boolean.valueOf(true));
		}
		String tempFileId = this.fileUtil.saveSingleFile(true, file);
		File tempFile = this.fileUtil.getTempRealFile(tempFileId);
		String filePath = tempFile.getAbsolutePath();
		session.setAttribute("filePath", filePath);
		ImportUtil iu = new ImportUtil();
		try {
			//获取到Excel中的数据
			List<TemporaryWorkStudyModel> oldTemporaryWorkStudyModels = this.temporaryWorkService.queryAddTemporaryWorkInfoByCond(temporaryWorkStudyModelVO);
			//逻辑删除重复提交的数据
			String isSubmit = request.getParameter("isSubmit");//判断其是否为重复提交
			if(DataUtil.isEquals("true", isSubmit)){//重复提交
				for (TemporaryWorkStudyModel m : oldTemporaryWorkStudyModels) {
					m = this.temporaryWorkService.queryTemporaryWorkStudyById(m.getId());
					if(DataUtil.isNotNull(m)){
						m.setStatus(Constants.STATUS_DELETED_DICS);
						this.temporaryWorkService.updateTemporaryWorkInfo(m);
					}
				}
			}
			//保存到数据库
			List<TemporaryWorkStudyModel> newTemporaryWorkStudyModels = iu.getDataList(filePath, "importTemporaryWorkStudy", null, TemporaryWorkStudyModel.class);
			List<TemporaryWorkStudyModel> reTemporaryWorkStudyModels = new ArrayList<TemporaryWorkStudyModel>();
			boolean isRepeat = false;//判断导入的excel
			for (TemporaryWorkStudyModel t : newTemporaryWorkStudyModels) {
				if(newTemporaryWorkStudyModels.contains(t)){
					isRepeat = true;
					reTemporaryWorkStudyModels.add(t);
				}
			}
			if(!isRepeat){//不存在重复数据
				double workSalary = (Double.parseDouble(this.commonConfigService.getTempWorkSalary()));
				Org org = this.orgService.queryOrgById(ProjectSessionUtils.getCurrentTeacherOrgId(request));
				this.temporaryWorkService.importTemporaryWorkInfo(newTemporaryWorkStudyModels,temporaryWorkStudyModelVO,workSalary,org,this.sessionUtil.getCurrentUserId());
			}else{//存在重复的数据
				errorText.add("导入数据存在以下重复数据，请删除后重新导入");
				model.addAttribute("errorText", errorText); 
				model.addAttribute("reTemporaryWorkStudyModels", reTemporaryWorkStudyModels); 
			}
		}catch (ExcelException e) {
			errorText = e.getMessageList();
			errorText = errorText.subList(0, errorText.size() > 20 ? 20 : errorText.size());
			model.addAttribute("errorText", errorText);
		} catch (InstantiationException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace(); 
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			errorText.add("模板配置与数据类型不一致,请与系统管理员联系!");
			errorText = errorText.subList(0, errorText.size() > 20 ? 20 : errorText.size());
			model.addAttribute("errorText", errorText); 
		}
		model.addAttribute("importFlag", Boolean.valueOf(true));
		List<Dic> years = this.dicUtil.getDicInfoList("YEAR");//学年
		List<Dic> terms = this.dicUtil.getDicInfoList("TERM");//学期
		model.addAttribute("years", years);
		model.addAttribute("terms", terms);
		model.addAttribute("temporaryWorkStudyModelVO", temporaryWorkStudyModelVO);
		return Constants.MENUKEY_WORK_TEMPORARY + "temporaryWorkImport";
	}
	/**
	 * 计算导出页数
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/sponsor/temporaryWork/nsm/exportTemporaryWorkList")
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
		return Constants.MENUKEY_WORK_TEMPORARY + "exportTemporaryWorkList";
	}
	/**
	 * 按查询条件导出Excel
	 * @param modelMap
	 * @param request
	 * @param response
	 * @param temporaryWorkStudyModel
	 */
	@RequestMapping("/sponsor/temporaryWork/opt-query/exportTemporaryWork")
	public void exportTemporaryWork(ModelMap model, HttpServletRequest request,HttpServletResponse response, TemporaryWorkStudyModel temporaryWorkStudyModelVO,String userQuery_exportSize,String userQuery_exportPage){
		int pageSize = userQuery_exportSize != null ? Integer.parseInt(userQuery_exportSize) : 25000;
		int pageNo = userQuery_exportPage != null ? Integer.parseInt(userQuery_exportPage) : 1;
		String userId = sessionUtil.getCurrentUserId();
		int queryType = 2;//默认为普通教师
		String orgId = (String)request.getSession().getAttribute("_teacher_orgId");
		//当出现一个人存在多个角色时，按最大的角色进行数据过滤
		if(this.commonRoleService.checkUserIsExist(userId, "HKY_SUTDENT")){//学生
			queryType = Constants.QUERY_TYPE_STUDENT;
			StudentInfoModel studentId = null;
			if(DataUtil.isNotNull(temporaryWorkStudyModelVO.getStudentId())){
				studentId = temporaryWorkStudyModelVO.getStudentId();
			}else{
				studentId = new StudentInfoModel();
			}
			studentId.setId(userId);
			temporaryWorkStudyModelVO.setStudentId(studentId);
		}else{
			if(this.commonRoleService.checkUserIsExist(userId, "ADM") || this.commonRoleService.checkUserIsExist(userId, "HKY_SPONSOR_ADMIN") || this.commonRoleService.checkUserIsExist(userId, "HKY_SCHOOL_LEADER")){
				//系统管理员   资助管理员_学生处   学校领导
				queryType = Constants.QUERY_TYPE_ADMIN;
			}else{//主要有三个角色HKY_DEPART_WORK_STUDY_AMDIN  HKY_COLLEGE_LEADER  HKY_COMMON_DEPART_LEADER
				queryType = Constants.QUERY_TYPE_TEACHER;
				Org org = new Org();
				org.setId(orgId);
				temporaryWorkStudyModelVO.setOrgId(org);
			}
		}
		List<TemporaryWorkStudyModel> list = (List<TemporaryWorkStudyModel>) this.temporaryWorkService.queryTemporaryWorkInfo(temporaryWorkStudyModelVO, pageSize, pageNo,queryType).getResult();
		List exportDataList = new ArrayList();
		double sumSalary =0.0;//工资总计
		String title = "杭州科技职业技术学院学生勤工助学酬金发放表";//表头
		int countColumn = 11;//excel文件中的列数-1
		String excelName = "export_temporaryWork.xls";//导出excel模板的文件名称
		String excelId = "exportTemporaryWork";//bdpExcel中的导出ID
		boolean isTrue = true;
		if(DataUtil.isNotNull(temporaryWorkStudyModelVO.getSchoolYear()) && DataUtil.isNotNull(temporaryWorkStudyModelVO.getSchoolYear().getId()) && DataUtil.isNotNull(temporaryWorkStudyModelVO.getSchoolTerm()) 
				&& DataUtil.isNotNull(temporaryWorkStudyModelVO.getSchoolTerm().getId()) && DataUtil.isNotNull(temporaryWorkStudyModelVO.getWorkMonth())){//拼接导出文件名：yyyy学年MM月
			title = temporaryWorkStudyModelVO.getWorkMonth() + "月" + title;
			Dic dicInfo = this.dicService.getDic(temporaryWorkStudyModelVO.getSchoolYear().getId());
			title=dicInfo.getName()+ "学年"+ title;
			isTrue = false;
		}
		for (int i = 0; i < list.size(); i++) {
			Map<String,Object> map = new HashMap<String, Object>();
			TemporaryWorkStudyModel temporaryWorkStudyModel = list.get(i);
			StudentInfoModel studentInfoModel = temporaryWorkStudyModel.getStudentId();
			map.put("sortId", i+1);
			if(isTrue){
				map.put("schoolYear", this.dicService.getDic(temporaryWorkStudyModel.getSchoolYear().getId()).getName());
				map.put("schoolTerm", this.dicService.getDic(temporaryWorkStudyModel.getSchoolTerm().getId()).getName());
				map.put("workMonth", temporaryWorkStudyModel.getWorkMonth());
				excelId = "exportTemporaryWorkByCond";
				excelName = "export_TemporaryWorkByCond.xls";
				countColumn = 14;
			}
			//map.put("protocalNo", "临时用工");//去掉
			map.put("stuName", studentInfoModel.getName());
			map.put("stuNo", studentInfoModel.getStuNumber());
			map.put("className", studentInfoModel.getClassId().getClassName());
			map.put("collegeName", studentInfoModel.getCollege().getName());
			map.put("orgName", temporaryWorkStudyModel.getOrgName());
			map.put("workHour", temporaryWorkStudyModel.getWorkHour());
			map.put("totalSalary", temporaryWorkStudyModel.getTotalSalary());
			map.put("bank", temporaryWorkStudyModel.getBank());
			map.put("cardNum", temporaryWorkStudyModel.getCardNum());
			map.put("workPerformance", temporaryWorkStudyModel.getWorkPerformance());
			sumSalary += temporaryWorkStudyModel.getTotalSalary();
			exportDataList.add(map);
		}
		HSSFWorkbook wb;
		try {
			wb = this.excelService.dynamicExportData(excelName,excelId, exportDataList);
			
			HSSFSheet sheet = wb.getSheetAt(0);//添加表头列表 
			
			HSSFCellStyle HeadStyle = (HSSFCellStyle) wb.createCellStyle();//表头单元格样式
			HeadStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			HeadStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			HeadStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			HeadStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			HeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
			
			HSSFCellStyle countStyle = (HSSFCellStyle) wb.createCellStyle();//表尾单元格样式
			countStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			countStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			countStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			countStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			countStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
			
			HSSFCellStyle tailStyle = (HSSFCellStyle) wb.createCellStyle();
			tailStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
			
			HSSFFont bigFont = wb.createFont(); //创建大字体
			bigFont.setFontHeightInPoints((short) 24);
			bigFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			HeadStyle.setFont(bigFont); 
			
			HSSFFont smallFont = wb.createFont();//创建小字体
			smallFont.setFontHeightInPoints((short) 12);
			smallFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			
			HSSFRow headRow = sheet.createRow(0);
			for(int i=0;i<countColumn;i++){
				headRow.createCell(i).setCellStyle(countStyle);
			}
			HSSFCell headCell = headRow.createCell(0);// 总结需要合并单元格的地方
			headCell.setCellValue(title);// 跨单元格显示的数据
			headCell.setCellType(HSSFCell.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,countColumn-1));
			
			sheet.getRow(0).setHeightInPoints(35);//设置合并单元格单元格的高度
			headCell.setCellStyle(HeadStyle);
			
			HSSFRow countRow = sheet.createRow(sheet.getLastRowNum()+1);//添加统计行
			for(int i=0;i<countColumn;i++){
				countRow.createCell(i).setCellStyle(countStyle);
			}
			HSSFCell countCell = countRow.getCell(0);
			countRow.setHeightInPoints(20);
			countCell.setCellValue("本页合计："+RmbUtil.NumberToChinese(sumSalary+"")+"                  （小写） "+sumSalary+" 元");
			countCell.setCellType(HSSFCell.CELL_TYPE_STRING);
			countCell.setCellStyle(countStyle);
			sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 0, countColumn-1));
			
			HSSFRow tailRow = sheet.createRow(sheet.getLastRowNum()+1);//添加表尾
			for(int i=0;i<countColumn;i++){
				tailRow.createCell(i);
			}
			HSSFCell tailCell = tailRow.getCell(0);
			tailCell.setCellValue("制表人：                 财务审核人：                财务负责人：               部门负责人：");
			tailCell.setCellType(HSSFCell.CELL_TYPE_STRING);
			tailCell.setCellStyle(tailStyle);
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
	 * 得到上传文件的大小 2MB
	 * @return
	 */
	private int setMaxSize() {
		return 20971520;
	}
	/**
	 * 用于判断TemporaryWorkStudyModel是否在数据库中已经存在
	 * @param temporaryWorkStudyModelVO
	 * @return
	 */
	private boolean isExistTemporaryWorkStudyModel(TemporaryWorkStudyModel temporaryWorkStudyModelVO){
		List<TemporaryWorkStudyModel> lists = this.temporaryWorkService.queryAddTemporaryWorkInfoByCond(temporaryWorkStudyModelVO);
		if(lists != null && lists.size() > 0){
			return true;
		}
		return false;
	}
}
