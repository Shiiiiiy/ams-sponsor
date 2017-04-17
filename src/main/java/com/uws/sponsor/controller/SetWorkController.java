package com.uws.sponsor.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.FlowHistoryPo;
import com.uws.apw.service.IFlowInstanceService;
import com.uws.apw.util.JsonUtils;
import com.uws.common.util.SchoolYearUtil;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.StringUtils;
import com.uws.domain.sponsor.PositionListModel;
import com.uws.domain.sponsor.SponsorPositionModel;
import com.uws.domain.sponsor.WorkOrgModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sponsor.service.ISetWorkService;
import com.uws.sponsor.util.Constants;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.IDicService;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.Org;
import com.uws.user.model.User;
import com.uws.user.service.IOrgService;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/**
* 
* @Title: SetWorkController.java 
* @Package com.uws.sponsor.controller 
* @Description: 岗位设置controller
* @author zhangmx  
* @date 2015-7-31 下午14:41:53
*/
@Controller
public class SetWorkController extends BaseController {
	@Autowired
	private ISetWorkService setWorkService;
	@Autowired
	private IOrgService orgService;
	@Autowired
	private IDicService dicService;
	//审批 Service
	@Autowired
	private IFlowInstanceService flowInstanceService;
	// 日志
    private Logger log = new LoggerFactory(SetWorkController.class);
    // sessionUtil工具类
  	private SessionUtil sessionUtil = SessionFactory.getSession(Constants.MENUKEY_SET_WORK);
  	//数据字典工具类
    private DicUtil dicUtil = DicFactory.getDicUtil();
  
   
    /**
     * 用工部门列表方法
     * @param model
     * @param request
     * @param workOrg
     * @param session 保存当前用户所在的部门
     * @return
     */
  	@RequestMapping(value={"/sponsor/setWork/opt-query/queryWorkOrgList"})
	public String listWorkOrg(ModelMap model,HttpServletRequest request,WorkOrgModel workOrg){
  		log.info("助学岗位设置--用工部门列表");
		//获取当前用户所在的部门Id
		String teacherOrgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		//默认=当前学年
		if( null==workOrg.getSchoolYear()){
			workOrg.setSchoolYear(SchoolYearUtil.getYearDic());
		}
		//默认=当前学期
		if( null==workOrg.getTerm()){
			if(null!=SchoolYearUtil.getCurrentTermDic()){
				workOrg.setTerm(SchoolYearUtil.getCurrentTermDic());
			}
		}
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.setWorkService.queryPageWorkOrg(workOrg,pageNo,Page.DEFAULT_PAGE_SIZE,teacherOrgId);

		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
		model.addAttribute("page", page);
		model.addAttribute("workOrg", workOrg);
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());

		//学生处的组织机构
		model.addAttribute("studentOfficId", ProjectConstants.STUDNET_OFFICE_ORG_ID);
		if(StringUtils.hasText(teacherOrgId))
			model.addAttribute("userOrg",orgService.queryOrgById(teacherOrgId));
		
		return  Constants.MENUKEY_SET_WORK+"/setWorkList";
	}
    /**
     * 查询用工部门列表方法
     * @param model
     * @param request
     * @param workOrg
     * @param session 保存当前用户所在的部门
     * @return
     */
  	@RequestMapping(value={"/sponsor/selectSetWork/opt-query/selectWorkOrgList"})
	public String selectWorkOrgList(ModelMap model,HttpServletRequest request,WorkOrgModel workOrg){
  		log.info("助学岗位设置--用工部门列表");
		//获取当前用户所在的部门Id
		String teacherOrgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		//默认=当前学年
		if( null==workOrg.getSchoolYear()){
			workOrg.setSchoolYear(SchoolYearUtil.getYearDic());
		}
		//默认=当前学期
		if( null==workOrg.getTerm()){
			if(null!=SchoolYearUtil.getCurrentTermDic()){
				workOrg.setTerm(SchoolYearUtil.getCurrentTermDic());
			}
		}
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.setWorkService.selectPageWorkOrg(workOrg,pageNo,Page.DEFAULT_PAGE_SIZE,teacherOrgId);

		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
		model.addAttribute("page", page);
		model.addAttribute("workOrg", workOrg);
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());

		//学生处的组织机构
		model.addAttribute("studentOfficId", ProjectConstants.STUDNET_OFFICE_ORG_ID);
		if(StringUtils.hasText(teacherOrgId))
			model.addAttribute("userOrg",orgService.queryOrgById(teacherOrgId));
		
		return  Constants.MENUKEY_SET_WORK+"/selectSetWorkList";
	}

  	
	
  	/**
  	 * 跳转到添加/编辑用工岗位页面
  	 * @param model
  	 * @param id
  	 * @param request
  	 * @return
  	 */
	@RequestMapping(value={"/sponsor/setWork/opt-add/editSetWork","/sponsor/setWork/opt-update/editSetWork"})
	public String editSetWork(ModelMap model,HttpServletRequest request,String workOrgId,String yearId,String termId)
	{	log.info("助学岗位设置----跳转到添加/编辑用工岗位页面");
		String isUpdate =request.getParameter("isUpdate");
		WorkOrgModel workOrgPo= null;
		List<SponsorPositionModel> allPosition = null;
		if(StringUtils.hasText(workOrgId))
		{
			workOrgPo=this.setWorkService.queryWorkOrgById(workOrgId);
			allPosition=this.setWorkService.queryPositionListByWorkOrgId(workOrgId);
		}
		else
		{
			//获取当前用户所在的部门Id
			String teacherOrgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
//			Dic yearDic = new Dic();
//			Dic termDic = new Dic();
//			if(StringUtils.hasText(yearId) && StringUtils.hasText(termId))
//			{
//				workOrgPo=this.setWorkService.queryWorkOrgByStatus(teacherOrgId,yearId,termId,null);
//				yearDic.setId(yearId);
//				termDic.setId(termId);
//			}
//			else 
//			{
//				yearDic = SchoolYearUtil.getYearDic();
//				termDic = SchoolYearUtil.getCurrentTermDic();
//				if(termDic!=null){
//					workOrgPo=this.setWorkService.queryWorkOrgByStatus(teacherOrgId, yearDic.getId(), termDic.getId(),null);
//				}
//			}	
//			if(null!=workOrgPo)
//				allPosition=this.setWorkService.queryPositionListByWorkOrgId(workOrgPo.getWorkOrgId());
//			else
//			{
				workOrgPo = new WorkOrgModel();
//				workOrgPo.setTerm(termDic);
//				workOrgPo.setSchoolYear(yearDic);
				workOrgPo.setOrg(orgService.queryOrgById(teacherOrgId));
//			}
		}

		//获取学年、学期数据字典
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
		model.addAttribute("workOrg",workOrgPo);
		model.addAttribute("allPosition",allPosition);
		
		model.addAttribute("isUpdate", isUpdate);
		return  Constants.MENUKEY_SET_WORK+"/editSetWork";
	}
	/**
	 * 通过页面学期、学年、部门加载数据
	 * @param model
	 * @param workOrg
	 * @param request
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/sponsor/setWork/opt-query/isExisWorkOrg"}, produces={"text/plain;charset=UTF-8"})
	public String isExisWorkOrg(ModelMap model,HttpServletRequest request,String orgId,String yearId,String termId)
	{
		log.info("助学岗位设置---通过页面学期、学年、部门加载数据");
		String result = "";
		WorkOrgModel workOrgPo=this.setWorkService.queryWorkOrgByStatus(orgId,yearId,termId,null);
		if(null!=workOrgPo)
			result = workOrgPo.getWorkOrgId();
		return result;
	}
	
	/**
	 * 提交信息
	 * @param model
	 * @param request
	 * @param workOrg
	 * @param positionList
	 * @param flags
	 * @return
	 */
	@RequestMapping(value = {"/sponsor/setWorkApprove/opt-save/submitSetWork" },produces = { "text/plain;charset=UTF-8"})
	@ResponseBody
	public String submitSetWork(ModelMap model,HttpServletRequest request,WorkOrgModel workOrg,PositionListModel positionListModel,String flags){
	
			log.info("助学岗位设置----提交信息");
			workOrg.setStatus(Constants.STATUS_SAVE_DICS);
			
			if(StringUtils.hasText(workOrg.getWorkOrgId())){
				this.setWorkService.updateWorkOrg(workOrg);
				
			}else{
				this.setWorkService.saveWorkOrg(workOrg);
			}
			if(null!=positionListModel)
			{
				List<SponsorPositionModel> positionList = positionListModel.getPositionList();
				if(positionList!=null && !"".equals(positionList)){
					for(SponsorPositionModel position : positionList)
					{
						if(null!=position && StringUtils.hasText(position.getWorkName()))
						{
							position.setWorkOrg(workOrg);
							
							if(StringUtils.hasText(position.getPositionId())){
								this.setWorkService.updateSponsorPosition(position);
							}
							else
							{ 
								position.setWorkOrg(workOrg);
								this.setWorkService.saveSponsorPosition(position);
							}
						}
					}
				}
				
			}	
		

			
			return workOrg.getWorkOrgId();
	}
	/**
	 * 把保存 改为 提交
	 * @param workOrgId
	 * @return
	 */
	@RequestMapping(value={"/sponsor/setWork/opt-update/addSubmitStatus"})
	public String addSubmit(String workOrgId){
		WorkOrgModel workOrgPo=this.setWorkService.queryWorkOrgById(workOrgId);
		workOrgPo.setStatus(Constants.STATUS_SUBMIT_DICS);
		workOrgPo.setProcessStatus("APPROVEING");
		this.setWorkService.updateWorkOrg(workOrgPo);
		return "redirect:/sponsor/setWork/opt-query/queryWorkOrgList.do"; 
	}
	
	/**
	 * 批量保存、修改用工岗位方法
	 * @param model
	 * @param request
	 * @param workOrg
	 * @param positionList 封装助学岗位集合的对象
	 * @return
	 */
    @RequestMapping(value={"/sponsor/setWork/opt-save/saveSetWork","/sponsor/setWork/opt-update/saveSetWork"}, produces={"text/plain;charset=UTF-8"})
	public String saveSetWork(HttpServletRequest request,WorkOrgModel workOrg, PositionListModel positionList)
	{	log.info("助学岗位设置----批量保存、修改用工岗位方法");
		String flags = request.getParameter("flags");
		String[] currentPositionIds = request.getParameterValues("currentPositionId");
		setWorkService.saveOrUpdate(workOrg, positionList, currentPositionIds,flags);
		
		return "redirect:/sponsor/setWork/opt-query/queryWorkOrgList.do";
	}
    
    
   /**
    * 逻辑删除用工岗位方法
    * @param model
    * @param id
    * @return
    */
    @RequestMapping(value={"/sponsor/setWork/opt-del/delSetWork"}, produces={"text/plain;charset=UTF-8"})
    @ResponseBody
    public String delSetWork(ModelMap model,String id) 
    {	log.info("助学岗位设置----逻辑删除用工岗位方法");
    	if(StringUtils.hasText(id))
    	{
    		SponsorPositionModel sponsorPositionPo=this.setWorkService.querySponsorPositionById(id);
    		sponsorPositionPo.setDelStatus(Constants.STATUS_DELETED_DICS);
    		setWorkService.updateSponsorPosition(sponsorPositionPo);
    	}
    	return "success";
    }
    
    /**
     * 实际删除用工部门方法
     * @param model
     * @param id
     * @return
     */
     @RequestMapping(value={"/sponsor/setWork/opt-del/deleteWorkOrg"}, produces={"text/plain;charset=UTF-8"})
     @ResponseBody
     public String deleteWorkOrg(ModelMap model,String workOrgId) 
     {	log.info("助学岗位设置----实际删除用工部门方法");
     	if(StringUtils.hasText(workOrgId))
     		
     	{
     		WorkOrgModel workOrgPo=this.setWorkService.queryWorkOrgById(workOrgId);
     		List<SponsorPositionModel> listPosition=this.setWorkService.queryPositionListByWorkOrgId(workOrgId);
     		for(SponsorPositionModel s:listPosition){
     			this.setWorkService.delObject(s);
     		}
     		this.setWorkService.delObject(workOrgPo);
     		return "success";
     	}else{
     		return "false";
     	}
     	
     }
     
    /**
     * 查看岗位信息
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value={"/sponsor/setWork/opt-view/viewSetWork"})
	public String viewSetWorkInfo(ModelMap model,HttpServletRequest request){
    	log.info("助学岗位设置----查看岗位信息");
    	String workOrgId = request.getParameter("workOrgId");
    	WorkOrgModel workOrgPo=this.setWorkService.queryWorkOrgById(workOrgId);
		if(StringUtils.hasText(workOrgId)){
			List<SponsorPositionModel>	allPosition= this.setWorkService.queryPositionListByWorkOrgId(workOrgId);
			model.addAttribute("allPosition",allPosition);
		}
		List<FlowHistoryPo> instanceList = this.flowInstanceService.getCurProcessHistory(workOrgPo.getWorkOrgId(),ProjectConstants.IS_APPROVE_ENABLE);
		model.addAttribute("instanceList",instanceList);
		model.addAttribute("workOrg",workOrgPo);
		return  Constants.MENUKEY_SET_WORK+"/viewSetWork";
    	
    }
    /**
     * 跳转到复制岗位页面
     * @param model
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(value={"/sponsor/setWork/opt-formCopy/copySetWork"})
    public String editCopySetWork(ModelMap model,HttpServletRequest request,HttpSession session){
    	log.info("助学岗位设置----跳转到复制岗位页面");
    	//获取当前用户所在的部门Id
    	String teacherOrgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
    	//根据部门ID查找部门
    	Org teacherOrg = orgService.queryOrgById(teacherOrgId);
    	
    	model.addAttribute("teacherOrg", teacherOrg);
    	model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
    	model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
    	//获取当前学年字典。
    	model.addAttribute("nowYearDic", SchoolYearUtil.getYearDic());
    	//获取当前学期
    	model.addAttribute("nowTermDic", SchoolYearUtil.getCurrentTermDic());
    	return  Constants.MENUKEY_SET_WORK+"/copySetWork";
    	
    }
    /**
     * 保存复制功能
     * @param model
     * @param request
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping(value={"/sponsor/setWork/opt-save/saveCopySetWork"},produces = { "text/plain;charset=UTF-8" })
    public String saveCopySetWork(ModelMap model,HttpServletRequest request,HttpSession session){
    	log.info("助学岗位设置----保存复制功能");
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
    	
    	//查找要复制的岗位部门
    	WorkOrgModel sourceWorkOrg=this.setWorkService.queryWorkOrgByStatus(teacherOrg.getId(), sourceSchoolYearDic.getId(), sourceTermDic.getId(),"PASS");
    	if(sourceWorkOrg==null){
    		return "isnull";
    	}
    	//根据岗位部门找到其对应的所有岗位
    	List<SponsorPositionModel> positions=this.setWorkService.queryPositionList(teacherOrg.getId(), sourceSchoolYearDic.getId(), sourceTermDic.getId(),"PASS");
    
    	WorkOrgModel aimWorkOrg=this.setWorkService.queryWorkOrgByStatus(teacherOrg.getId(), aimSchoolYearDic.getId(), aimTermDic.getId(),null);
    	if(aimWorkOrg!=null){
    		return "value";
    	}
    	
    	//复制功能
    	this.setWorkService.copyPosition(sourceWorkOrg,positions,aimSchoolYearDic,aimTermDic);
    	
    	model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
    	model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
    	return  "success";
    	
    }
   
 
    /**
     * 审核列表
     * approve query list
     * @param model
     * @param request
     * @param workOrg
     * @return
     */
  	@RequestMapping(value={"/sponsor/approveSetWork/opt-approve/approveSetWorkList"})
	public String workOrgApproveList(ModelMap model,HttpServletRequest request,WorkOrgModel workOrg){
  		log.info("助学岗位设置----审核列表");
  		//根据当前登录人信息
		String userId = sessionUtil.getCurrentUserId();
		//获取当前用户所在的部门Id
		String teacherOrgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
  		//默认当前学年
		if( null==workOrg.getSchoolYear()){
			workOrg.setSchoolYear(SchoolYearUtil.getYearDic());
		}
		//默认=当前学期
		if( null==workOrg.getTerm()){
			if(null!=SchoolYearUtil.getCurrentTermDic()){
				workOrg.setTerm(SchoolYearUtil.getCurrentTermDic());
			}
		}
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		String[] objectIds = flowInstanceService.getObjectIdByProcessKey("SPONSOR_SET_WORK_APPROVE",userId);

		Page page = this.setWorkService.queryPageSetWorkApporve(workOrg,pageNo,Page.DEFAULT_PAGE_SIZE,userId,objectIds);
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
		model.addAttribute("page", page);
		model.addAttribute("workOrg", workOrg);
		model.addAttribute("userId", userId);
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());
		model.addAttribute("studentOfficId", ProjectConstants.STUDNET_OFFICE_ORG_ID);
		if(StringUtils.hasText(teacherOrgId))
			model.addAttribute("userOrg",orgService.queryOrgById(teacherOrgId));
		
		return  Constants.MENUKEY_SET_WORK_APPROVE+"/approveSetWorkList";
	}
    
    
	
	 /**
     * 编辑审核页面
     * @param model
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(value={"/sponsor/approveSetWork/opt-edit/editApproveSetWork"}, produces={"text/plain;charset=UTF-8"})
    public String editApproveSetWork(ModelMap model,HttpServletRequest request,String workOrgId){
    	log.info("助学岗位设置----编辑审核页面");
    	if(StringUtils.hasText(workOrgId)){
			WorkOrgModel workOrg=this.setWorkService.queryWorkOrgById(workOrgId);
			List<SponsorPositionModel> allPosition=this.setWorkService.queryPositionListByWorkOrgId(workOrgId);
			model.addAttribute("workOrg", workOrg);
			model.addAttribute("allPosition", allPosition);
		}
    	return Constants.MENUKEY_SET_WORK_APPROVE+"/approveSetWorkEdit";
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
	@RequestMapping(value = {"/sponsor/setWorkApprove/opt-add/saveCurProcess"},produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String saveCurProcess(ModelMap model,HttpServletRequest request,String objectId,String nextApproverId){
		
		ApproveResult result = new ApproveResult();
		
		if(ProjectConstants.IS_APPROVE_ENABLE){
			try {
				User initiator = new User(this.sessionUtil.getCurrentUserId());//封装发起人
				User nextApprover = new User(nextApproverId);//封装第一级审核人
				
				//发起审核流程
				result = flowInstanceService.initProcessInstance(objectId,"SPONSOR_SET_WORK_APPROVE", 
						 initiator,nextApprover,ProjectConstants.IS_APPROVE_ENABLE);
				result = this.saveApproveStatusSetWork(objectId,result,nextApproverId);
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
	@RequestMapping(value = {"/sponsor/setWorkApprove/opt-add/saveApproveAction" },produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String saveApproveAction(ModelMap model,HttpServletRequest request,String objectId,String nextApproverId,String approveStatus,String processStatusCode){
		ApproveResult result = new ApproveResult();
		if(ProjectConstants.IS_APPROVE_ENABLE){
			try {
				  result.setApproveStatus(approveStatus);
				  result.setProcessStatusCode(processStatusCode);
				  this.saveApproveStatusSetWork(objectId,result,nextApproverId);
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
	
	public ApproveResult saveApproveStatusSetWork(
			String objectId,ApproveResult result,String nextApproverId){
		if(DataUtil.isNotNull(result)){
			
			//获取实体保存
			WorkOrgModel workOrgPo =this.setWorkService.queryWorkOrgById(objectId);
			//流程审批状态
			workOrgPo.setApproveStatus(result.getApproveStatus());
			//流程实例状态
			workOrgPo.setProcessStatus(result.getProcessStatusCode());
			
			if(DataUtil.isNotNull(nextApproverId)){
				//下一节点办理人
				User nextApprover = new User(nextApproverId);
				workOrgPo.setNextApprover(nextApprover);
			}
			if("REJECT".equals(workOrgPo.getProcessStatus())){
				workOrgPo.setNextApprover(null);
				
			}
			//保存审批流回显的信息
			this.setWorkService.updateWorkOrg(workOrgPo);
		}
		
		return result;
	}
	/**
	 * 
	 * @Title: saveApproveReasonSetWork
	 * @Description: 保存审核的信息（保存在自己表中 审核理由）
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({"/sponsor/setWorkApprove/opt-save/saveApproveReasonSetWork"})
	public String saveApproveReasonSetWork(ModelMap model,HttpServletRequest request){
		String workOrgId = request.getParameter("workOrgId");
		String approveReason = request.getParameter("approveReason");
		
		WorkOrgModel workOrgPo=this.setWorkService.queryWorkOrgById(workOrgId);
		
		workOrgPo.setApproveReason(approveReason);
		if( "PASS".equals(workOrgPo.getProcessStatus())||"REJECT".equals(workOrgPo.getProcessStatus())){
			workOrgPo.setNextApprover(null);
		}
		this.setWorkService.updateWorkOrg(workOrgPo);
		
		return "redirect:/sponsor/approveSetWork/opt-approve/approveSetWorkList.do";
						
	}
		    
    
   
    
  
}
