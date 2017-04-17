
package com.uws.sponsor.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
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
import org.springframework.web.multipart.MultipartFile;

import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStudentCommonService;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.sponsor.OriginLoanModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sponsor.service.IOriginLoanService;
import com.uws.sponsor.util.Constants;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/** 
* @ClassName: OriginLoanController 
* @Description: (生源地助学贷款控制类) 
* @author xuzh
* @date 2015-8-12 下午5:55:31 
*  
*/
@Controller
public class OriginLoanController extends BaseController {
	
	@Autowired
	private IOriginLoanService originLoanService;
	@Autowired
	private IStudentCommonService studentCommonService;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICompService compService;
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	private static Logger log = new LoggerFactory(OriginLoanController.class);
	// 附件工具类
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
	 * 
	 * @Title: listOriginLoan
	 * @Description: TODO(生源地助学贷款查询)
	 * @param model
	 * @param request
	 * @param originLoan
	 * @return
	 * @throws
	 */
	@RequestMapping({"/sponsor/originLoan/opt-query/originLoanList"})
	public String listOriginLoan(ModelMap model, HttpServletRequest request, OriginLoanModel originLoan){
		log.info("生源地助学贷款列表");
		
		//当前登录教职工的部门ID
		String collegeId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
    	int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
    	Page page = originLoanService.queryOriginLoan(pageNo, Page.DEFAULT_PAGE_SIZE, originLoan, collegeId);
    	model.addAttribute("page", page);
    	
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	List<BaseMajorModel> majorList = null;
    	List<BaseClassModel> classList = null;	
      
    	if(originLoan != null && originLoan.getStudentInfo() != null){
    		StudentInfoModel student = originLoan.getStudentInfo();
    		
    		if(student.getCollege() != null && StringUtils.hasText(student.getCollege().getId())){
    			majorList = compService.queryMajorByCollage(student.getCollege().getId());
    		}
    		if(student.getMajor() != null && StringUtils.hasText(student.getMajor().getId()) ){
    			classList = compService.queryClassByMajor(student.getMajor().getId());
    		}
    	}
    
		model.addAttribute("collegeList", collegeList);
		if(collegeId != null && !ProjectConstants.STUDNET_OFFICE_ORG_ID.equals(collegeId)){
			model.addAttribute("collegeId", collegeId);
			majorList = compService.queryMajorByCollage(collegeId);
		}
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("yearList", dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("saveStauts",Constants.STATUS_SAVE_DIC);
		
		//request传递查询条件
		model.addAttribute("originLoan", originLoan);
		return Constants.MENUKEY_ORIGIN_INFO + "/listOriginLoan" ;
	} 
	
	@RequestMapping({"/sponsor/originLoan/opt-view/viewOriginLoan"})
	public String viewOriginLoan(ModelMap model, HttpServletRequest request ) {
		log.info("查看生源地助学贷款信息");
		String id = request.getParameter("id");
		OriginLoanModel originLoan = this.originLoanService.findOriginLoanById(id);
		model.addAttribute("originLoan", originLoan);
		StudentInfoModel studentInfo = studentCommonService.queryStudentById(originLoan.getStudentInfo().getId());
		model.addAttribute("studentInfo", studentInfo);
		model.addAttribute("yearList", dicUtil.getDicInfoList("YEAR"));
		
		return Constants.MENUKEY_ORIGIN_INFO + "/viewOriginLoan";
	}
	
	/**
	 * 
	 * @Title: editOriginLoan
	 * @Description: TODO(更新生源地助学贷款信息)
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({"/sponsor/originLoan/opt-update/editOriginLoan"})
	public String editOriginLoan(ModelMap model, HttpServletRequest request ) {
		log.info("修改生源地助学贷款信息");
		String id = request.getParameter("id");
		OriginLoanModel originLoan = this.originLoanService.findOriginLoanById(id);
		model.addAttribute("originLoan", originLoan);
		
		StudentInfoModel studentInfo = studentCommonService.queryStudentById(originLoan.getStudentInfo().getId());
		model.addAttribute("studentInfo", studentInfo);
		model.addAttribute("yearList", dicUtil.getDicInfoList("YEAR"));
		
		return Constants.MENUKEY_ORIGIN_INFO + "/editOriginLoan";
	}
	
	@RequestMapping({ "/sponsor/originLoan/opt-update/updateOriginLoan"})
	public String saveOriginLoan(ModelMap model, HttpServletRequest request, OriginLoanModel originLoan, String[] fileId) {
		String id = originLoan.getId();
		OriginLoanModel originLoanPo = this.originLoanService.findOriginLoanById(id);
		BeanUtils.copyProperties(originLoan, originLoanPo, new String[]{ "studentInfo", "createTime"});
		this.originLoanService.update(originLoanPo);
		log.info("生源地助学贷款贷款信息修改成功!");
			
		return "redirect:/sponsor/originLoan/opt-query/originLoanList.do";
	}
	
	/**
	 * 
	 * @Title: importOriginLoan
	 * @Description: 导入生源地助学贷款
	 * @param model
	 * @param file
	 * @param maxSize
	 * @param allowedExt
	 * @param request
	 * @param session
	 * @return
	 * @throws Exception
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping({"/sponsor/originLoan/opt-import/importOriginLoan"})
	public String importOriginLoan(ModelMap model, @RequestParam("file") MultipartFile file, String maxSize, String allowedExt, HttpServletRequest request, HttpSession session) throws Exception {
		List errorText = new ArrayList();
		String errorTemp = "";
		MultipartFileValidator validator = new MultipartFileValidator();
		if(DataUtil.isNotNull(allowedExt)) {
			validator.setAllowedExtStr(allowedExt.toLowerCase());
		}
		if(DataUtil.isNotNull(maxSize)) {
			validator.setMaxSize(Long.valueOf(maxSize).longValue());
		}else{
			validator.setMaxSize(20971520);
		}
		String returnValue = validator.validate(file);
		if(!returnValue.equals("")) {
			errorTemp = returnValue;
			errorText.add(errorTemp);
			model.addAttribute("errorText", errorText.size()==0 ? null : errorText);
		    model.addAttribute("importFlag", Boolean.valueOf(true));
		    return "sponsor/originLoan/importOriginLoan";
		}else{
			String tempFileId = this.fileUtil.saveSingleFile(true, file);
			File tempFile = this.fileUtil.getTempRealFile(tempFileId);
			String filePath = tempFile.getAbsolutePath();
			session.setAttribute("filePath", filePath);
			try {
				ImportUtil iu = new ImportUtil();
				 //Excel数据
				List<OriginLoanModel> list = iu.getDataList(filePath, "importOriginLoan", null, OriginLoanModel.class);       
				List<Object[]> arrayList = this.originLoanService.compareData(list); 
				//Excel与已有的重复的数据
				if((arrayList == null) || (arrayList.size() == 0)) {
					this.originLoanService.importOriginData(list);
				} else {
					session.setAttribute("arrayList", arrayList);
					
					Page page = new Page();
					page.setPageSize(Page.DEFAULT_PAGE_SIZE);
					page.setResult(arrayList);
					page.setStart(0L);
					page.setTotalCount(arrayList.size());
					model.addAttribute("page", page);
				}
			} catch (OfficeXmlFileException e) {
				e.printStackTrace();
				errorTemp = "OfficeXmlFileException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (IOException e) {
				e.printStackTrace();
				errorTemp = "IOException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				errorTemp = "IllegalAccessException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (ExcelException e) {
				e.printStackTrace();
				errorTemp = e.getMessage();
				errorText.add(errorTemp);
			} catch (InstantiationException e) {
				e.printStackTrace();
				errorTemp = "InstantiationException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			model.addAttribute("importFlag", Boolean.valueOf(true));
			model.addAttribute("errorText", errorText.size()==0 ? null : errorText);
			return "sponsor/originLoan/importOriginLoan";
		}
	}
	
	/**
	 * 
	 * @Title: compareOriginLoan
	 * @Description: TODO(对比导入数据)
	 * @param model
	 * @param request
	 * @param session
	 * @param pageNo
	 * @return
	 * @throws
	 */
	@RequestMapping(value={"/sponsor/originLoan/opt-query/compareOriginLoan"}, produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String compareOriginLoan(ModelMap model, HttpServletRequest request, HttpSession session, @RequestParam(value="pageNo", required=true) String pageNo) {
		List<Object[]> arrayList = (List<Object[]>)session.getAttribute("arrayList");
		List<Object[]> subList = null;
		int pageno = Integer.parseInt(pageNo);
		int length = arrayList.size();
		if(arrayList.size() >= Page.DEFAULT_PAGE_SIZE * pageno) {
			subList = arrayList.subList(Page.DEFAULT_PAGE_SIZE * (pageno - 1), Page.DEFAULT_PAGE_SIZE * pageno);
		}else{
			subList = arrayList.subList(Page.DEFAULT_PAGE_SIZE * (pageno - 1), length);
		}
		JSONArray array = new JSONArray();
	    JSONObject obj = null;
	    JSONObject json = new JSONObject();
	    for(Object[] infoArray : subList) {
	    	OriginLoanModel info = (OriginLoanModel)infoArray[0];
	    	OriginLoanModel xls = (OriginLoanModel)infoArray[1];
	    	obj = new JSONObject();
	    	obj.put("stuName", info.getStudentInfo().getName());
	    	obj.put("stuNumber", info.getStudentInfo().getStuNumber());
	    	obj.put("loanYear", info.getLoanYear().getName());
	    	obj.put("xlsName", xls.getStuName());
	    	obj.put("xlsNumber", xls.getStuNumber());
	    	obj.put("xlsLoanYear", xls.getLoanYear().getName());
	    	array.add(obj);
	    }
	    json.put("result", array);
	    obj = new JSONObject();
	    obj.put("totalPageCount", Integer.valueOf(length % Page.DEFAULT_PAGE_SIZE == 0 ? 
	    		length / Page.DEFAULT_PAGE_SIZE : length / Page.DEFAULT_PAGE_SIZE + 1));
	    obj.put("previousPageNo", Integer.valueOf(pageno - 1));
	    obj.put("nextPageNo", Integer.valueOf(pageno + 1));
	    obj.put("currentPageNo", Integer.valueOf(pageno));
	    obj.put("pageSize", Integer.valueOf(Page.DEFAULT_PAGE_SIZE));
	    obj.put("totalCount", Integer.valueOf(length));
	    json.put("page", obj);
	    return json.toString();
	}
	
	/**
	 * 
	 * @Title: importOriginLoanData
	 * @Description: (这个方法是对比之后的确认导入数据 )
	 * @param model
	 * @param session
	 * @param compareId
	 * @return
	 * @throws
	 */
	@SuppressWarnings("finally")
	@RequestMapping({"/sponsor/originLoan/opt-query/importConfirmData"})
	public String importOriginLoanData(ModelMap model, HttpSession session, @RequestParam("compareId") String compareId) {
		
		List<Object> errorText = new ArrayList<Object>();
		
		String filePath = session.getAttribute("filePath").toString();
		List<Object[]> arrayList = (List<Object[]>) session.getAttribute("arrayList");
		try {
			this.originLoanService.importLastData(arrayList, filePath, compareId);
		} catch (ExcelException e) {
			errorText.add(0, e.getMessage());
		    errorText = errorText.subList(0, errorText.size() > 20 ? 20 : errorText.size());
		    model.addAttribute("errorText", errorText.size()==0 ? null : errorText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (OfficeXmlFileException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			model.addAttribute("importFlag", Boolean.valueOf(true));
		    return "sponsor/originLoan/importOriginLoan";
		}
	}
	
	/**
	 * 
	 * @Title: deleteOriginLoan
	 * @Description: (删除生源地助学贷款信息)
	 * @param request
	 * @param id
	 * @return
	 * @throws
	 */
	@ResponseBody 
	@RequestMapping(value={"/sponsor/originLoan/opt-delete/deleteOriginLoan"}, produces={"text/plain;charset=UTF-8"})
	public String deleteOriginLoan(HttpServletRequest request, String id)
	{
		if(StringUtils.hasText(id))
			originLoanService.deleteById(id);
		return "success";
	}
	
	
	
}
