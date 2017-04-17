package com.uws.sponsor.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.sponsor.NationalLoanModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sponsor.service.INationalLoanService;
import com.uws.sponsor.util.Constants;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/** 
* @ClassName: NationalLoanController 
* @Description: (国家助学贷款控制类) 
* @author xuzh
* @date 2015-8-7 下午4:55:31 
*  
*/
@Controller
public class NationalLoanController extends BaseController {
	@Autowired
	private INationalLoanService nationalLoanService;
	private SessionUtil sessionUtil = SessionFactory.getSession(Constants.MENUKEY_NATIONAL_INFO);
	
	@Autowired
	private IStudentCommonService studentCommonService;
	
	@Autowired
	private IBaseDataService baseDataService;
	
	@Autowired
	private ICompService compService;
	
	@Autowired
	private IExcelService excelService;
	
	private DicUtil dicUtil = DicFactory.getDicUtil();
	
	private static Logger log = new LoggerFactory(NationalLoanController.class);
	
	private FileUtil fileUtil = FileFactory.getFileUtil();

	/**
	 * 
	 * @Title: initBinder
	 * @Description: (进行日期类型数据处理)
	 * @param binder
	 * @throws
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	/**
	 * @Title: editNationalLoan
	 * @Description: (学生申请国家助学贷款页面，以及修改国家助学贷款信息页面)
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */ 
	@RequestMapping({ "/sponsor/nationalLoan/opt-add/addNationalLoan", "/sponsor/nationalLoan/opt-update/editNationalLoan"})
	public String editNationalLoan(ModelMap model, HttpServletRequest request ) {
		String id = request.getParameter("id");
		String loginStudentId = sessionUtil.getCurrentUserId();
		StudentInfoModel studentInfo = studentCommonService.queryStudentById(loginStudentId);
		
		if (StringUtils.isEmpty(id)) {
			log.info("新增国家助学贷款学生信息");
		} else {
			NationalLoanModel nationalLoan = this.nationalLoanService.findNationalLoanById(id);
			model.addAttribute("nationalLoan", nationalLoan);
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));
			log.info("修改国家助学贷款信息");
		}
		model.addAttribute("studentInfo", studentInfo);
		model.addAttribute("cardList", dicUtil.getDicInfoList("CARD_TYPE"));
		model.addAttribute("currentYear", SchoolYearUtil.getYearDic());
		model.addAttribute("yearList", dicUtil.getDicInfoList("YEAR"));
		return Constants.MENUKEY_NATIONAL_INFO + "/nationalLoanEdit";
	}
	
	
	/**
	 * 
	 * @Title: checkCodeRepeat
	 * @Description:验证同一学年的学生是否申请过国家助学贷款
	 * @param id
	 * @param schoolYear
	 * @return
	 * @throws
	 */
    @RequestMapping({"/sponsor/nationalLoan/opt-query/loanYearCheck"})
    @ResponseBody
    public String checkApplyRepeat(@RequestParam String id, @RequestParam String loanYear){ 
    	String success = "true";
    	
    	String currentStudentId = sessionUtil.getCurrentUserId();
    	boolean flag = nationalLoanService.isApply(id, currentStudentId, loanYear);
    	if(flag)
    		success = "false";
    	
    	return success;
    }
    
	/**
	 * 
	 * @Title: saveNationalLoan
	 * @Description: (保存国家助学贷款信息)
	 * @param model
	 * @param request
	 * @param nationalLoan
	 * @param fileId
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/sponsor/nationalLoan/opt-save/saveNationalLoan"})
	public String saveNationalLoan(ModelMap model, HttpServletRequest request, NationalLoanModel nationalLoan, String[] fileId) {
		String id = nationalLoan.getId();
		nationalLoan.setStatus(dicUtil.getDicInfo("APPLY_STATUS", "SAVE"));
		
		String loginStudentId = sessionUtil.getCurrentUserId();
		StudentInfoModel studentInfo = new StudentInfoModel();
		studentInfo.setId(loginStudentId);
		
		if(StringUtils.isEmpty(id)) {
			nationalLoan.setStudentInfo(studentInfo);
			this.nationalLoanService.saveInfos(nationalLoan, fileId);
			log.info("国家助学贷款信息新增成功!");
			
		} else {
			NationalLoanModel nationalLoanPo = this.nationalLoanService.findNationalLoanById(id);
			
			BeanUtils.copyProperties(nationalLoan, nationalLoanPo, new String[]{ "studentInfo", "createTime"});
			
			this.nationalLoanService.updateInfos(nationalLoanPo, fileId);
			
			log.info("国家助学贷款信息修改成功!");
		}

		return "redirect:/sponsor/nationalLoan/opt-query/studentNationalLoanList.do";
	}
	/**
	 * 
	 * @Title: submitNationalLoan
	 * @Description: (提交国家助学贷款信息)
	 * @param model
	 * @param request
	 * @param nationalLoan
	 * @param fileId
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/sponsor/nationalLoan/opt-save/submitNationalLoan"})
	public String submitNationalLoan(ModelMap model, HttpServletRequest request, NationalLoanModel nationalLoan, String[] fileId) {
		String id = request.getParameter("id");
		NationalLoanModel nationalLoanPo = null;
		if(id != null && !id.equals("") ){
			nationalLoanPo = this.nationalLoanService.findNationalLoanById(id);
			nationalLoan.setStatus(Constants.STATUS_SUBMIT_DIC);
			BeanUtils.copyProperties(nationalLoan, nationalLoanPo, new String[]{ "studentInfo", "createTime"});
			
			this.nationalLoanService.updateInfos(nationalLoanPo, fileId);
		} else{
			
			String loginStudentId = sessionUtil.getCurrentUserId();
			StudentInfoModel studentInfo = new StudentInfoModel();
			studentInfo.setId(loginStudentId);
			nationalLoan.setStudentInfo(studentInfo);
			
			nationalLoan.setStatus(Constants.STATUS_SUBMIT_DIC);
			
			this.nationalLoanService.saveInfos(nationalLoan, fileId);
		}
		
		log.info("国家助学贷款信息提交成功!");
		return "redirect:/sponsor/nationalLoan/opt-query/studentNationalLoanList.do";
	}
	
	/**
	 * 
	 * @Title: viewNationalLoan
	 * @Description: (查看国家助学贷款信息)
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({"/sponsor/nationalLoan/opt-view/viewNationalLoan"})
	public String viewNationalLoan(ModelMap model, HttpServletRequest request) {
		log.info("查看国家助学贷款学生详细信息");
		String id = request.getParameter("id");
		NationalLoanModel nationalLoan = this.nationalLoanService.findNationalLoanById(id);
		model.addAttribute("nationalLoan", nationalLoan);
		
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(nationalLoan.getId()));
		
		return Constants.MENUKEY_NATIONAL_INFO + "/nationalLoanView";
		
	}

	/**
	 * 进入国家助学贷款审核页面
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping({"/sponsor/nationalLoan/opt-show/nationalLoanApprovePage"})
	public String approveNationalLoanPage(ModelMap model, HttpServletRequest request) {
		log.info("查看国家助学贷款学生详细信息");
		String id = request.getParameter("id");
		NationalLoanModel nationalLoan = this.nationalLoanService.findNationalLoanById(id);
		model.addAttribute("nationalLoan", nationalLoan);
		
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));
		
		return Constants.MENUKEY_NATIONAL_APPROVE + "/approveNationalLoan";
		
		}
	/**
	 * 
	 * @Title: deleteNationalLoan
	 * @Description: (删除国家助学贷款)
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/sponsor/nationalLoan/opt-del/deleteNationalLoan" })
	@ResponseBody
	public String deleteNationalLoan(ModelMap model, HttpServletRequest request, String id) {
		
		this.nationalLoanService.deleteNationalLoan(id);
		
		log.info("删除操作成功！");
		
		return "success" ;
	}

	/**
	 * 
	 * @Title: listNationalLoanByStudent
	 * @Description: (学生查看国家助学贷款列表)
	 * @param model
	 * @param request
	 * @param nationalLoan
	 * @return
	 * @throws
	 */
	@RequestMapping({"/sponsor/nationalLoan/opt-query/studentNationalLoanList"})
	public String listNationalLoanByStudent(ModelMap model, HttpServletRequest request, NationalLoanModel nationalLoan){
		log.info("学生查看国家助学贷款列表");
		String loginStudentId = sessionUtil.getCurrentUserId();
		
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.nationalLoanService.queryNationalLoanByStudent(pageNo, Page.DEFAULT_PAGE_SIZE, nationalLoan, loginStudentId );
		
		model.addAttribute("page", page);
		model.addAttribute("nationalLoan", nationalLoan);
		model.addAttribute("saveStatus", Constants.STATUS_SAVE_DIC );
		model.addAttribute("rejectStatus", Constants.STATUS_REJECT_DIC);
		model.addAttribute("yearList", Constants.yearList);
		model.addAttribute("currentYear", Constants.currentYear);
		model.addAttribute("statusList", Constants.APPLY_STATUS_LIST);
		
		return Constants.MENUKEY_NATIONAL_INFO + "/studentNationalLoanList";
	} 
	
	/**
	 * 
	 * @Title: listNationalLoanByTeacher
	 * @Description: (教师查询国家助学贷款列表)
	 * @param model
	 * @param request
	 * @param student
	 * @param nationalLoan
	 * @return
	 * @throws
	 */
	@RequestMapping({"/sponsor/college/opt-query/collegeNationalLoanList"})
	public String listNationalLoanByTeacher(ModelMap model, HttpServletRequest request, NationalLoanModel nationalLoan){
		
		log.info("教师查询国家助学贷款列表");
		//当前登录教职工的部门ID
		String collegeId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
    	int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
    	Page page = nationalLoanService.queryNationalLoanByTeacher(pageNo ,Page.DEFAULT_PAGE_SIZE, nationalLoan, collegeId);
    	
    	model.addAttribute("page", page);
    	
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	List<BaseMajorModel> majorList = null;
    	List<BaseClassModel> classList = null;	
      
    	if(nationalLoan != null && nationalLoan.getStudentInfo() != null){
    		StudentInfoModel student = nationalLoan.getStudentInfo();
    		
    		if(student.getCollege() != null && StringUtils.isNotEmpty(student.getCollege().getId())){
    			majorList = compService.queryMajorByCollage(student.getCollege().getId());
    		}
    		
    		if(student.getMajor() != null && StringUtils.isNotEmpty(student.getMajor().getId())){
    			classList = compService.queryClassByMajor(student.getMajor().getId());
    		}
    	}
    
		model.addAttribute("collegeList", collegeList);
		if(!ProjectConstants.STUDNET_OFFICE_ORG_ID.equals(collegeId)){
			model.addAttribute("collegeId", collegeId);
			majorList = compService.queryMajorByCollage(collegeId);
		}
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		
		model.addAttribute("nationalLoan", nationalLoan);
		model.addAttribute("yearList", Constants.yearList);
		model.addAttribute("saveStauts",Constants.STATUS_SAVE_DIC);
		
		return Constants.MENUKEY_NATIONAL_INFO + "/collegeNationalLoanList";
	} 
	
	/**
	 * 
	 * @Title: listSchoolApproveNationalLoan
	 * @Description: (学生处审核国家助学贷款信息)
	 * @param model
	 * @param request
	 * @param nationalLoan
	 * @return
	 * @throws
	 */
	@RequestMapping({"/sponsor/approve/opt-query/nationalLoanList"})
	public String listSchoolApproveNationalLoan(ModelMap model, HttpServletRequest request, NationalLoanModel nationalLoan, StudentInfoModel student){
		log.info("学生处审核国家助学贷款列表");
		
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		
		Page page = this.nationalLoanService.queryNationalLoanApproveList(pageNo, Page.DEFAULT_PAGE_SIZE, nationalLoan);
		
		model.addAttribute("page", page);
		//学院
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	//专业
    	List<BaseMajorModel> majorList = null;
    	//班级
    	List<BaseClassModel> classList = null;
    	if(nationalLoan != null && nationalLoan.getStudentInfo() != null ){
    		if(nationalLoan.getStudentInfo().getCollege()!=null && nationalLoan.getStudentInfo().getCollege().getId() != null){
    			majorList = compService.queryMajorByCollage(nationalLoan.getStudentInfo().getCollege().getId());
    		}
    		if( nationalLoan.getStudentInfo().getMajor()!=null && nationalLoan.getStudentInfo().getMajor().getId() != null){
        		classList = compService.queryClassByMajor(nationalLoan.getStudentInfo().getMajor().getId());
        	}
    	}
    	model.addAttribute("saveStatus", Constants.STATUS_SAVE_DIC);
    	model.addAttribute("submitStatus", Constants.STATUS_SUBMIT_DIC);
    	model.addAttribute("confrimStatus", Constants.STATUS_CONFIRM_DIC);
    	model.addAttribute("statusList", Constants.APPLY_STATUS_LIST);
    	model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("nationalLoan", nationalLoan);
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("nowYear", SchoolYearUtil.getYearDic());
		
		return Constants.MENUKEY_NATIONAL_APPROVE + "/listNationalLoan";
	} 
	
	/**
	 * 
	 * @Title: approveNationalLoan
	 * @Description: (学生处审核国家助学贷款信息)
	 * @param model
	 * @param request
	 * @param nationalLoan
	 * @return
	 * @throws
	 */
	@RequestMapping({"/sponsor/approve/opt-approve/updateLoanStatus"})
	public String approveNationalLoan( HttpServletRequest request) {
		String id=request.getParameter("id");
		NationalLoanModel nationalLoan = this.nationalLoanService.findNationalLoanById(id);
		
		String param=request.getParameter("param");
		
		if(param!=null && !param.trim().equals("")){
			if(param.equals("confirm")){
				nationalLoan.setStatus(Constants.STATUS_CONFIRM_DIC);
			}
			if(param.equals("reject")){
				nationalLoan.setStatus(Constants.STATUS_REJECT_DIC);
			}
			
			nationalLoanService.update(nationalLoan);
		}
		
		log.info("国家助学贷款信息审核!");
		
		return "redirect:/sponsor/approve/opt-query/nationalLoanList.do";
	}
	
	/**
	 * 批量确认和打回
	 * @param model
	 * @param request
	 * @param checkedIds
	 * @param isConfirm
	 * @return
	 */
    @ResponseBody
    @RequestMapping({"sponsor/approve/opt-update/confirmNationalLoan", "/sponsor/approve/opt-update/rejectNationalLoan"}) 
    public String approveBatchNationalLoan(ModelMap model, HttpServletRequest request, String checkedIds, String isConfirm) {
    	if(checkedIds != null && !"".equals(checkedIds)){
			String[] ids = checkedIds.split(",");
			NationalLoanModel nationalLoan = new NationalLoanModel();
			for(String id:ids){
				 if(id!=null && !id.equals("")){
					 nationalLoan = nationalLoanService.findNationalLoanById(id);
					 if (nationalLoan != null){
						 if(isConfirm!=null && isConfirm.equals("confirm")) {
 	    		    		nationalLoan.setStatus(Constants.STATUS_CONFIRM_DIC);
 	    		    	 }
 	    		    	 if(isConfirm!=null && isConfirm.equals("reject")) {
 	    		    		nationalLoan.setStatus(Constants.STATUS_REJECT_DIC);
 	    		    	 }
						 nationalLoanService.update(nationalLoan);
					  }
				 }
			}
		}
    	    				
    	return "success";
    }

    
    /***
	 * 导出预处理
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping({"/sponsor/nationalLoan/nsm/exportNationalLoanList"})
	public String exportNationalLoanList(ModelMap model, HttpServletRequest request){
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
	    if (maxNumber <= 500)
	    	model.addAttribute("isMore", "false");
	    else {
	    	model.addAttribute("isMore", "true");
	    }
	    return "/sponsor/nationalLoan/exportNationalLoanList";
	  }
	
    
    
	/***
	   * 导出国家助学贷款
	   * @param model
	   * @param request
	   * @param evaluation
	   * @param response
	   */
	@RequestMapping({"/sponsor/nationalLoan/opt-export/exportNationalLoan"})
	public void exportNationalLoan(ModelMap model, HttpServletRequest request, NationalLoanModel nationalLoan, HttpServletResponse response){
		String exportSize = request.getParameter("nationalLoanQuery_exportSize");
		String exportPage = request.getParameter("nationalLoanQuery_exportPage");
		String collegeId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		Page page = nationalLoanService.queryNationalLoanByTeacher(Integer.parseInt(exportPage) ,Integer.parseInt(exportSize), nationalLoan, collegeId);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
	    List<Map> listMap = new ArrayList<Map>();
	    List<NationalLoanModel> infoList = (List)page.getResult();
	    for(NationalLoanModel n : infoList) {
	    	Map<String, Object> map = new HashMap<String, Object>();
	    	
	    	map.put("loanYear", n.getLoanYear() !=null ? n.getLoanYear().getName() : "" );
	    	map.put("college",  n.getStudentInfo().getCollege() != null ? n.getStudentInfo().getCollege().getName() : "");
	    	map.put("major",    n.getStudentInfo().getMajor() != null ? n.getStudentInfo().getMajor().getMajorName() : "");
	    	map.put("class",    n.getStudentInfo().getClassId() != null ? n.getStudentInfo().getClassId().getClassName() : "");
	    	map.put("studentNum", n.getStudentInfo().getStuNumber() != null ? n.getStudentInfo().getStuNumber() : "");
	    	map.put("name",    n.getStudentInfo().getName() != null ? n.getStudentInfo().getName() : "");
	    	map.put("loanAmount", n.getLoanAmount());
	    	map.put("loanNumYear", n.getLoanNumYear()); 
	    	map.put("cardType", n.getCardType().getName());
	    	map.put("cardNum", n.getCardNum()); 
	    	map.put("applyDate", sdf.format(n.getApplyDate()));
	    	listMap.add(map);
	    }
		    
	    HSSFWorkbook wb;
		try {
			wb = this.excelService.exportData("export_national_loan.xls", "exportNationalLoan", listMap);
			String filename = "国家助学贷款列表" + exportPage + ".xls";
		    response.setContentType("application/x-excel");
		    response.setHeader("Content-disposition", "attachment;filename=" + new String(filename.getBytes("GBK"), "iso-8859-1"));
		    response.setCharacterEncoding("UTF-8");
		    OutputStream ouputStream = response.getOutputStream();
		    wb.write(ouputStream);
		    ouputStream.flush();
		    ouputStream.close();
		} catch (ExcelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
    
}
