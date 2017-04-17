package com.uws.sponsor.controller;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

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
import com.uws.common.util.SchoolYearUtil;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.config.TimeConfigModel;
import com.uws.domain.sponsor.AidGrant;
import com.uws.domain.sponsor.DifficultStudentAward;
import com.uws.domain.sponsor.InspirationalAward;
import com.uws.domain.sponsor.JobGrant;
import com.uws.domain.sponsor.OtherAward;
import com.uws.domain.sponsor.SchoolLoan;
import com.uws.domain.sponsor.TuitionWaiver;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sponsor.service.IDifficultyStudentService;
import com.uws.sponsor.service.IStudentAwardService;
import com.uws.sponsor.util.Constants;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.IDicService;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.User;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;
/**
 * com.uws.sponsor.controller.StudentAwardController;
 * 困难生奖助类
 * 困难生维护，修改，查看，删除等操作
 * @author liuchen
 */
@Controller
public class StudentAwardController extends BaseController{
	
	@Autowired
	private IStudentAwardService studentAwardService;
	//困难生service
	@Autowired
	private IDifficultyStudentService difficultyStudentService;
	//数据字典service
	@Autowired
	private IDicService dicService;
	//基础信息service
	@Autowired
	private IBaseDataService baseDataService;
	//公共的service
	@Autowired
	private ICompService compService;
	//审批 Service
	@Autowired
	private IFlowInstanceService flowInstanceService;
	
	//时间配置管理service
	@Autowired
	private ICommonConfigService commonConfigService;
	
	// 日志
    private Logger log = new LoggerFactory(StudentAwardController.class);
    //数据字典工具类
    private DicUtil dicUtil = DicFactory.getDicUtil();
    // sessionUtil工具类
   	private SessionUtil sessionUtil = SessionFactory.getSession(Constants.MENUKEY_STUDENT_AWARD);
   	
   	@InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    /**
     * @Title: queryStudentAwardList
     * @Description: 困难生奖助信息维护
     * @param model
     * @param request
     * @param studentAward
     * @return
     * @throws
     */
	@RequestMapping("/sponsor/studentAward/opt-query/queryStudentAwardList")
	public String queryStudentAwardList(ModelMap model,HttpServletRequest request,DifficultStudentAward difficultStudentAward){
		log.info("困难生奖助信息查询列表");
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.studentAwardService.queryStudentAwardList(pageNo,Page.DEFAULT_PAGE_SIZE, difficultStudentAward,sessionUtil.getCurrentUserId());
		//学院
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	//专业
    	List<BaseMajorModel> majorList = null;
    	//班级
    	List<BaseClassModel> classList = null;
    	if(difficultStudentAward!= null && difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege()!=null &&
    			com.uws.core.util.StringUtils.hasText(difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege().getId())){
    		majorList = compService.queryMajorByCollage(difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege().getId());
    	}
    	if(difficultStudentAward!= null && difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor()!=null &&
    			com.uws.core.util.StringUtils.hasText(difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor().getId())){
    		classList = compService.queryClassByMajor(difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor().getId());
    	}
		model.addAttribute("page", page);
		model.addAttribute("isDifficult", this.difficultyStudentService.checkIsDifficutlStudent(sessionUtil.getCurrentUserId()));
		model.addAttribute("studentAward", difficultStudentAward);
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("awardList",dicUtil.getDicInfoList("SPONSOR_AWARD_TYPE"));
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("statusList",dicUtil.getDicInfoList("STATUS"));
		model.addAttribute("financeList",dicUtil.getDicInfoList("FAMILY_FINANCE"));
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());
		String sponsorAwardTimeConfigCode = commonConfigService.getSponsorAwardTimeConfigCode();
		String[] awardTimeList = sponsorAwardTimeConfigCode.split("#");
		//验证是否有奖助申请。
		int i =0 ;
		for(String str :awardTimeList)
		{
			if(commonConfigService.checkCurrentDateByCode(str)){
				i++;
			}
		model.addAttribute("setTimeNum",i);
		}
		return Constants.MENUKEY_STUDENT_AWARD+"/studentAwardList";
	}
	
	
	
	
	/**
	 * 
	 * @Title: editStudentAwardInfo
	 * @Description: 新增，修改困难生奖助信息页面
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({"/sponsor/studentAward/opt-add/editStudentAwardInfo","/sponsor/studentAward/opt-update/editStudentAwardInfo" })
	public String editStudentAwardInfo(ModelMap model,HttpServletRequest request){
		String id = request.getParameter("id");
		DifficultStudentAward difficultStudentAward = null; 
		List<Dic> awardList = new ArrayList<Dic>();
		String sponsorAwardTimeConfigCode = commonConfigService.getSponsorAwardTimeConfigCode();//获取奖助配置时间code
		String[] awardTimeList = sponsorAwardTimeConfigCode.split("#");
		List<Dic> dicInfoList = dicUtil.getDicInfoList("SPONSOR_AWARD_TYPE");
		String strCode ="";
		for (Dic dic : dicInfoList) {
			strCode = strCode+dic.getCode() + ",";
		}
		String[] awardCodeList = strCode.split(",");
		//根据当前时间比较设置的奖助时间是否在时间段内，如果在设置的时间段内则吧要填写的内容展示
		//1.单项励志奖项的申请是否在设定时间范围内
		int i =0 ;
		for(String str :awardTimeList)
		{
			if(commonConfigService.checkCurrentDateByCode(str))
				
				awardList.add(dicUtil.getDicInfo("SPONSOR_AWARD_TYPE", awardCodeList[i]));
			i++;
		}
		//2.根据当前登录人获取学生对象的信息
		String currentStudentId = sessionUtil.getCurrentUserId();
		model.addAttribute("difficultStudentInfo",difficultyStudentService.queryDifficultStudentByStudentId(currentStudentId));	
		if(StringUtils.hasText(id))
		{
			difficultStudentAward = this.studentAwardService.getStudentAwardInfoById(id);
			queryGrantByType(model,difficultStudentAward.getId(),difficultStudentAward.getAwardType().getId());
			log.info("进入困难生奖助信息修改页面");
		}else{
			difficultStudentAward = new DifficultStudentAward();
			model.addAttribute("studentAward", difficultStudentAward);
			model.addAttribute("nowYearDic", SchoolYearUtil.getYearDic());
			log.info("进入困难生奖助信息新增页面");
		}
		//3.返回值赋值  5个类型的数据字典
		model.addAttribute("inspirationDic", Constants.STUDENT_AWARD_INSPIRATIONAL);
		model.addAttribute("aidGrantDic", Constants.STUDENT_AWARD_AID_GRANT);
		model.addAttribute("jobGrantDic", Constants.STUDENT_AWARD_JOB_GRANTL);
		model.addAttribute("schoolLoanDic", Constants.STUDENT_AWARD_SCHOOL_LOANL);
		model.addAttribute("tuitionDic", Constants.STUDENT_AWARD_TUITION_WAIVER);
		model.addAttribute("studentAward", difficultStudentAward);
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("genderList",dicUtil.getDicInfoList("GENDER"));
		model.addAttribute("financeList",dicUtil.getDicInfoList("FAMILY_FINANCE"));
		model.addAttribute("awardList",awardList);
		return Constants.MENUKEY_STUDENT_AWARD+"/studentAwardEdit";
	}
	
	/**
	 * 
	 * @Title: queryGrantByType
	 * @Description:根据奖助类型获取不同的值
	 * @param model
	 * @param awardId
	 * @param awardTypeId
	 * @throws
	 */
	private void queryGrantByType(ModelMap model,String awardId , String awardTypeId )
	{
		Dic awardType = dicService.getDic(awardTypeId); 
		if(awardType != null && StringUtils.hasText(awardType.getId()))
		{
				//进入到单项励志奖学金页面
			 if(awardType.getCode().equals("INSPIRATIONAL_AWARD"))
			 {
				InspirationalAward inspirationalAward = this.studentAwardService.getInspirationalByAwardId(awardId);
				model.addAttribute("inspirationalAward", inspirationalAward);
				model.addAttribute("awardKindList",dicUtil.getDicInfoList("AWARD_KIND"));
				model.addAttribute("nationAwardList",dicUtil.getDicInfoList("IS_SINGLE"));
				model.addAttribute("xingZhiAwardList",dicUtil.getDicInfoList("IS_SINGLE"));
				//今入到学校助学金页面	
			 }else if(awardType.getCode().equals("AID_GRANT")){
			    AidGrant aidGrant = this.studentAwardService.getAidGrantByAwardId(awardId);
				model.addAttribute("aidGrant", aidGrant);
			    model.addAttribute("aidLevelList",dicUtil.getDicInfoList("AID_LEVELS"));
				model.addAttribute("isNationalAwardList",dicUtil.getDicInfoList("IS_SINGLE"));
				model.addAttribute("isExamFailList",dicUtil.getDicInfoList("IS_SINGLE"));
			   }else if(awardType.getCode().equals("JOB_GRANT")){
				    JobGrant jobGrant = this.studentAwardService.getJobGrantByAwardId(awardId);
					model.addAttribute("jobGrant", jobGrant);
				    model.addAttribute("difficultTypeList",dicUtil.getDicInfoList("DIFFICULT_TYPE"));
			   }else if(awardType.getCode().equals("SCHOOL_LOAN")){
				    SchoolLoan schoolLoan = this.studentAwardService.getSchoolLoanByAwardId(awardId);
					model.addAttribute("schoolLoan", schoolLoan);
			   }else if(awardType.getCode().equals("TUITION_WAIVER")){
					TuitionWaiver tuitionWaiver = this.studentAwardService.getTuitionWaiverByAwardId(awardId);
					model.addAttribute("tuitionWaiver", tuitionWaiver);
					model.addAttribute("difficultTypeList",dicUtil.getDicInfoList("DIFFICULT_TYPE"));
					model.addAttribute("isExamFailList",dicUtil.getDicInfoList("IS_SINGLE"));
					model.addAttribute("iSEvaluateMidelList",dicUtil.getDicInfoList("IS_SINGLE"));
			   }else{
				    OtherAward otherAward = this.studentAwardService.getOtherAwardByAwardId(awardId);
					model.addAttribute("otherAward",otherAward);
				    model.addAttribute("difficultTypeList",dicUtil.getDicInfoList("DIFFICULT_TYPE"));
			   }
		}
	}
	
	/**
	 * 
	 * @Title: studentAwardView
	 * @Description: 根据选择不同的奖助类型进入到不同的页面
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws
	 */
	@RequestMapping(value="/sponsor/studentAward/nsm/studentAwardView")
	public String studentAwardView(ModelMap model,HttpServletRequest request){
		//修改时获取奖助id
		String awardId = request.getParameter("awardId");
		//根据选择不同的奖助或者不同的奖助内容
		String awardTypeId = request.getParameter("awardType");
		model.addAttribute("flag", request.getParameter("flag"));
		if(StringUtils.hasText(awardTypeId))
		{
			model.addAttribute("awardTypeId", awardTypeId);
		}else{
			model.addAttribute("awardTypeId", null);
		}
		queryGrantByType(model,awardId,awardTypeId);
		model.addAttribute("inspirationDic", Constants.STUDENT_AWARD_INSPIRATIONAL);
		model.addAttribute("aidGrantDic", Constants.STUDENT_AWARD_AID_GRANT);
		model.addAttribute("jobGrantDic", Constants.STUDENT_AWARD_JOB_GRANTL);
		model.addAttribute("schoolLoanDic", Constants.STUDENT_AWARD_SCHOOL_LOANL);
		model.addAttribute("tuitionDic", Constants.STUDENT_AWARD_TUITION_WAIVER);
		return Constants.MENUKEY_STUDENT_AWARD+"/studentAwardTypeDiv";

	   }
	
	
	/**
	 * 
	 * @Title: saveDifficultStudent
	 * @Description: 困难生奖助信息保存，修改方法
	 * @param model
	 * @param request
	 * @param difficultStudentAward
	 * @return
	 * @throws
	 */
	@RequestMapping({"/sponsor/studentAward/opt-save/saveDifficultStudentAward","/sponsor/studentAward/opt-update/saveDifficultStudentAward"})
	public String saveDifficultStudentAward(ModelMap model,HttpServletRequest request,DifficultStudentAward difficultStudentAward,InspirationalAward inspirationalAward,AidGrant aidGrant,
		JobGrant jobGrant,SchoolLoan schoolLoan,TuitionWaiver tuitionWaiver,OtherAward otherAward){
		//修改时获取奖助id
		String id = difficultStudentAward.getId();
		String flags = request.getParameter("flags");
		String awardTypeId = request.getParameter("awardType.id");
		Dic awardTypeDic = dicService.getDic(awardTypeId);
		if(StringUtils.hasText(difficultStudentAward.getId()))
			{
				DifficultStudentAward difficultStudentAwardPo = this.studentAwardService.getStudentAwardInfoById(difficultStudentAward.getId());
				if(null != difficultStudentAwardPo)
				{
					BeanUtils.copyProperties(difficultStudentAward,difficultStudentAwardPo,new String[]{"createTime","delStatus","schoolYear","nextApprover","approveStatus","processStatus"});
					if(DataUtil.isNotNull(flags) && flags.equals("1")){
						difficultStudentAwardPo.setStatus(Constants.STATUS_SUBMIT_DICS);
					}else{
						difficultStudentAwardPo.setStatus(Constants.STATUS_SAVE_DICS);//保存状态
					}
					difficultStudentAwardPo.setDelStatus(Constants.STATUS_NORMAL_DICS);
					difficultStudentAwardPo.setSchoolYear(SchoolYearUtil.getYearDic());
					difficultStudentAwardPo.setDifficultStudentInfo(difficultStudentAward.getDifficultStudentInfo());
					this.studentAwardService.updateStudentAward(difficultStudentAwardPo);
					if(awardTypeDic != null){
						if(awardTypeDic.getCode().equals("INSPIRATIONAL_AWARD")){
							InspirationalAward inspirationalAwardPo = this.studentAwardService.getInspirationalByAwardId(id);
							if(inspirationalAwardPo != null && DataUtil.isNotNull(inspirationalAwardPo.getInspirationalAwardId())){
								 BeanUtils.copyProperties(inspirationalAward,inspirationalAwardPo,new String[]{"studentAward","createTime","delStatus"});
								 this.studentAwardService.updateModel(inspirationalAwardPo);
							}else{
								inspirationalAward.setStudentAward(difficultStudentAward);
								this.studentAwardService.saveModel(inspirationalAward);
							}
							AidGrant aidGrantPo = this.studentAwardService.getAidGrantByAwardId(id);
							if(aidGrantPo != null && DataUtil.isNotNull(aidGrantPo.getAidGrantId())){
								this.studentAwardService.deleteModel(aidGrantPo);
							}
							JobGrant jobGrantPo = this.studentAwardService.getJobGrantByAwardId(id);
		                    if(jobGrantPo != null && DataUtil.isNotNull(jobGrantPo.getJobGrantId())){
		                    	this.studentAwardService.deleteModel(jobGrantPo);
		                    }
		                    SchoolLoan schoolLoanPo = this.studentAwardService.getSchoolLoanByAwardId(id);
		                    if(schoolLoanPo != null && DataUtil.isNotNull(schoolLoanPo.getSchoolLoanId())){
		                    	this.studentAwardService.deleteModel(schoolLoanPo);
		                    }
		                    TuitionWaiver tuitionWaiverPo = this.studentAwardService.getTuitionWaiverByAwardId(id);
		                    if(tuitionWaiverPo != null && DataUtil.isNotNull(tuitionWaiverPo.getTuitionWaiverId())){
		                    	this.studentAwardService.deleteModel(tuitionWaiverPo);
		                    }
		                	OtherAward otherAwardPo = this.studentAwardService.getOtherAwardByAwardId(id);
		                    if(otherAwardPo != null && DataUtil.isNotNull(otherAwardPo.getOtherAwardId())){
		                    	this.studentAwardService.deleteModel(otherAwardPo);
		                    }
						}else if(awardTypeDic.getCode().equals("AID_GRANT")){
							AidGrant aidGrantPo = this.studentAwardService.getAidGrantByAwardId(id);
							if(aidGrantPo != null && DataUtil.isNotNull(aidGrantPo.getAidGrantId())){
								BeanUtils.copyProperties(aidGrant,aidGrantPo,new String[]{"studentAward","createTime","delStatus"});
								this.studentAwardService.updateModel(aidGrantPo);
							}else{
								aidGrant.setStudentAward(difficultStudentAward);
								this.studentAwardService.saveModel(aidGrant);
							}
							//删除其他表的数据
							InspirationalAward inspirationalAwardPo = this.studentAwardService.getInspirationalByAwardId(id);
							if(inspirationalAwardPo != null && DataUtil.isNotNull(inspirationalAwardPo.getInspirationalAwardId())){
								this.studentAwardService.deleteModel(inspirationalAwardPo);
							}
							JobGrant jobGrantPo = this.studentAwardService.getJobGrantByAwardId(id);
		                    if(jobGrantPo != null && DataUtil.isNotNull(jobGrantPo.getJobGrantId())){
		                    	this.studentAwardService.deleteModel(jobGrantPo);
		                    }
		                    SchoolLoan schoolLoanPo = this.studentAwardService.getSchoolLoanByAwardId(id);
		                    if(schoolLoanPo != null && DataUtil.isNotNull(schoolLoanPo.getSchoolLoanId())){
		                    	this.studentAwardService.deleteModel(schoolLoanPo);
		                    }
		                    TuitionWaiver tuitionWaiverPo = this.studentAwardService.getTuitionWaiverByAwardId(id);
		                    if(tuitionWaiverPo != null && DataUtil.isNotNull(tuitionWaiverPo.getTuitionWaiverId())){
		                    	
		                    }
						}else if(awardTypeDic.getCode().equals("JOB_GRANT")){
							JobGrant jobGrantPo = this.studentAwardService.getJobGrantByAwardId(id);
		                    if(jobGrantPo != null && DataUtil.isNotNull(jobGrantPo.getJobGrantId())){
		    					 BeanUtils.copyProperties(jobGrant,jobGrantPo,new String[]{"studentAward","createTime","delStatus"});
		    					 this.studentAwardService.updateModel(jobGrantPo);
		                    }else{
		                    	jobGrant.setStudentAward(difficultStudentAward);
		                    	this.studentAwardService.saveModel(jobGrant);
		                    }
		                   //删除其他表的数据
							InspirationalAward inspirationalAwardPo = this.studentAwardService.getInspirationalByAwardId(id);
							if(inspirationalAwardPo != null && DataUtil.isNotNull(inspirationalAwardPo.getInspirationalAwardId())){
								this.studentAwardService.deleteModel(inspirationalAwardPo);
							}
							AidGrant aidGrantPo = this.studentAwardService.getAidGrantByAwardId(id);
							if(aidGrantPo != null && DataUtil.isNotNull(aidGrantPo.getAidGrantId())){
								this.studentAwardService.deleteModel(aidGrantPo);
							}
		                    SchoolLoan schoolLoanPo = this.studentAwardService.getSchoolLoanByAwardId(id);
		                    if(schoolLoanPo != null && DataUtil.isNotNull(schoolLoanPo.getSchoolLoanId())){
		                    	this.studentAwardService.deleteModel(schoolLoanPo);
		                    }
		                    TuitionWaiver tuitionWaiverPo = this.studentAwardService.getTuitionWaiverByAwardId(id);
		                    if(tuitionWaiverPo != null && DataUtil.isNotNull(tuitionWaiverPo.getTuitionWaiverId())){
		                    	
		                    }
						}else if(awardTypeDic.getCode().equals("SCHOOL_LOAN")){
							SchoolLoan schoolLoanPo = this.studentAwardService.getSchoolLoanByAwardId(id);
		                    if(schoolLoanPo != null && DataUtil.isNotNull(schoolLoanPo.getSchoolLoanId())){
		                    	 BeanUtils.copyProperties(schoolLoan,schoolLoanPo,new String[]{"studentAward","createTime","delStatus"});
		     					 this.studentAwardService.updateModel(schoolLoanPo);
							}else{
								schoolLoan.setStudentAward(difficultStudentAward);
								this.studentAwardService.saveModel(schoolLoan);
							}
		                    //删除其他表的数据
							InspirationalAward inspirationalAwardPo = this.studentAwardService.getInspirationalByAwardId(id);
							if(inspirationalAwardPo != null && DataUtil.isNotNull(inspirationalAwardPo.getInspirationalAwardId())){
								this.studentAwardService.deleteModel(inspirationalAwardPo);
							}
							AidGrant aidGrantPo = this.studentAwardService.getAidGrantByAwardId(id);
							if(aidGrantPo != null && DataUtil.isNotNull(aidGrantPo.getAidGrantId())){
								this.studentAwardService.deleteModel(aidGrantPo);
							}
							JobGrant jobGrantPo = this.studentAwardService.getJobGrantByAwardId(id);
		                    if(jobGrantPo != null && DataUtil.isNotNull(jobGrantPo.getJobGrantId())){
		                    	this.studentAwardService.deleteModel(jobGrantPo);
		                    }
		                    TuitionWaiver tuitionWaiverPo = this.studentAwardService.getTuitionWaiverByAwardId(id);
		                    if(tuitionWaiverPo != null && DataUtil.isNotNull(tuitionWaiverPo.getTuitionWaiverId())){
		                    	
		                    }
						}else if(awardTypeDic.getCode().equals("TUITION_WAIVER")){
							TuitionWaiver tuitionWaiverPo = this.studentAwardService.getTuitionWaiverByAwardId(id);
		                    if(tuitionWaiverPo != null && DataUtil.isNotNull(tuitionWaiverPo.getTuitionWaiverId())){
		                    	BeanUtils.copyProperties(tuitionWaiver,tuitionWaiverPo,new String[]{"studentAward","createTime","delStatus"});
		    					this.studentAwardService.updateModel(tuitionWaiverPo);
							}else{
								tuitionWaiver.setStudentAward(difficultStudentAward);
								this.studentAwardService.saveModel(tuitionWaiver);
							}
		                    //删除其他表的数据
							InspirationalAward inspirationalAwardPo = this.studentAwardService.getInspirationalByAwardId(id);
							if(inspirationalAwardPo != null && DataUtil.isNotNull(inspirationalAwardPo.getInspirationalAwardId())){
								this.studentAwardService.deleteModel(inspirationalAwardPo);
							}
							AidGrant aidGrantPo = this.studentAwardService.getAidGrantByAwardId(id);
							if(aidGrantPo != null && DataUtil.isNotNull(aidGrantPo.getAidGrantId())){
								this.studentAwardService.deleteModel(aidGrantPo);
							}
							JobGrant jobGrantPo = this.studentAwardService.getJobGrantByAwardId(id);
		                    if(jobGrantPo != null && DataUtil.isNotNull(jobGrantPo.getJobGrantId())){
		                    	this.studentAwardService.deleteModel(jobGrantPo);
		                    }
		                    SchoolLoan schoolLoanPo = this.studentAwardService.getSchoolLoanByAwardId(id);
		                    if(schoolLoanPo != null && DataUtil.isNotNull(schoolLoanPo.getSchoolLoanId())){
		                    	this.studentAwardService.deleteModel(schoolLoanPo);
		                    }
						}else{
							OtherAward otherAwardPo = this.studentAwardService.getOtherAwardByAwardId(id);
		                    if(otherAwardPo != null && DataUtil.isNotNull(otherAwardPo.getOtherAwardId())){
		    					 BeanUtils.copyProperties(otherAward,otherAwardPo,new String[]{"studentAward","createTime","delStatus"});
		    					 this.studentAwardService.updateModel(otherAwardPo);
		                    }else{
		                    	otherAward.setStudentAward(difficultStudentAward);
		                    	this.studentAwardService.saveModel(otherAward);
		                    }
		                    //删除其他表的数据
							InspirationalAward inspirationalAwardPo = this.studentAwardService.getInspirationalByAwardId(id);
							if(inspirationalAwardPo != null && DataUtil.isNotNull(inspirationalAwardPo.getInspirationalAwardId())){
								this.studentAwardService.deleteModel(inspirationalAwardPo);
							}
							AidGrant aidGrantPo = this.studentAwardService.getAidGrantByAwardId(id);
							if(aidGrantPo != null && DataUtil.isNotNull(aidGrantPo.getAidGrantId())){
								this.studentAwardService.deleteModel(aidGrantPo);
							}
							JobGrant jobGrantPo = this.studentAwardService.getJobGrantByAwardId(id);
		                    if(jobGrantPo != null && DataUtil.isNotNull(jobGrantPo.getJobGrantId())){
		                    	this.studentAwardService.deleteModel(jobGrantPo);
		                    }
		                    SchoolLoan schoolLoanPo = this.studentAwardService.getSchoolLoanByAwardId(id);
		                    if(schoolLoanPo != null && DataUtil.isNotNull(schoolLoanPo.getSchoolLoanId())){
		                    	this.studentAwardService.deleteModel(schoolLoanPo);
		                    }
		                    TuitionWaiver tuitionWaiverPo = this.studentAwardService.getTuitionWaiverByAwardId(id);
		                    if(tuitionWaiverPo != null && DataUtil.isNotNull(tuitionWaiverPo.getTuitionWaiverId())){
		                    	this.studentAwardService.deleteModel(tuitionWaiverPo);
		                    }
						}
					}
				}
		  }else
		  {
			// 删除状态(保存正常的)
			log.debug("困难生奖助信息新增操作!");
			difficultStudentAward.setDelStatus(Constants.STATUS_NORMAL_DICS);
			if(DataUtil.isNotNull(flags) && flags.equals("1")){
				difficultStudentAward.setStatus(Constants.STATUS_SUBMIT_DICS);
			}else{
				difficultStudentAward.setStatus(Constants.STATUS_SAVE_DICS);//保存状态
			}
			difficultStudentAward.setSchoolYear(SchoolYearUtil.getYearDic());
			this.studentAwardService.saveStudentAward(difficultStudentAward);
			//根据选择的奖助类型保存不一样的内容
			if(awardTypeDic != null){
				if(awardTypeDic.getCode().equals("INSPIRATIONAL_AWARD")){
					inspirationalAward.setStudentAward(difficultStudentAward);
					this.studentAwardService.saveModel(inspirationalAward);
				}else if(awardTypeDic.getCode().equals("AID_GRANT")){
					aidGrant.setStudentAward(difficultStudentAward);
					this.studentAwardService.saveModel(aidGrant);
				}else if(awardTypeDic.getCode().equals("JOB_GRANT")){
					jobGrant.setStudentAward(difficultStudentAward);
					this.studentAwardService.saveModel(jobGrant);
				}else if(awardTypeDic.getCode().equals("SCHOOL_LOAN")){
					schoolLoan.setStudentAward(difficultStudentAward);
					this.studentAwardService.saveModel(schoolLoan);
				}else if(awardTypeDic.getCode().equals("TUITION_WAIVER")){
					tuitionWaiver.setStudentAward(difficultStudentAward);
					this.studentAwardService.saveModel(tuitionWaiver);
				}else{
					otherAward.setStudentAward(difficultStudentAward);
					this.studentAwardService.saveModel(otherAward);
				}
			}	
		 }	
			return "redirect:/sponsor/studentAward/opt-query/queryStudentAwardList.do";
	}
	
	
	
	/**
	 * 初始化当前流程
	 * @Title: saveCurProcess
	 * @Description: 
	 * @param model
	 * @param request
	 * @param objectId			业务主键
	 * @param flags
	 * @param approveStatus		当前节点审批状态
	 * @param processStatusCode	流程当前状态
	 * @param nextApproverId	下一节点办理人
	 */
	@RequestMapping(value = {"/sponsor/studentAward/opt-add/saveCurProcess"},produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String saveCurProcess(ModelMap model,HttpServletRequest request,String objectId,String nextApproverId){
		
		ApproveResult result = new ApproveResult();
		
		if(ProjectConstants.IS_APPROVE_ENABLE){
			try {
				User initiator = new User(this.sessionUtil.getCurrentUserId());//封装发起人
				User nextApprover = new User(nextApproverId);//封装第一级审核人
				//发起审核流程
				result = flowInstanceService.initProcessInstance(objectId,"SPONSOR_DIFFICULT_STUDENT_APPROVE", initiator,nextApprover,ProjectConstants.IS_APPROVE_ENABLE);
						 
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
	 * @Description: 保存当前通过审批操作
	 * @param model
	 * @param request
	 * @param objectId
	 * @param nextApproverId
	 * @return
	 * @throws
	 */
	@RequestMapping(value = {"/sponsor/studentAwardApprove/opt-add/saveApproveAction" },produces = { "text/plain;charset=UTF-8" })
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
	 * 保存困难生奖助审批结果
	 * @Title: saveDifficultApproveResult
	 * @Description: 保存困难生审批结果
	 * @param objectId
	 * @param result
	 * @throws
	 */
	private ApproveResult saveDifficultApproveResult(String objectId,ApproveResult result,String nextApproverId) {
		if(DataUtil.isNotNull(result)){
			//获取保存的困难生
			DifficultStudentAward studentAward = this.studentAwardService.getStudentAwardInfoById(objectId);
			//流程审批状态
			if(null == studentAward)
			{
				studentAward = new DifficultStudentAward();
				studentAward.setId(objectId);
				studentAwardService.saveStudentAward(studentAward);
			}
			studentAward.setApproveStatus(result.getApproveStatus());
			//流程实例状态
			studentAward.setProcessStatus(result.getProcessStatusCode());
			studentAward.setStatus(Constants.STATUS_SUBMIT_DICS);//修改为提交状态
			if(DataUtil.isNotNull(nextApproverId)){
				//下一节点办理人
				User nextApprover = new User();
				nextApprover.setId(nextApproverId);
				studentAward.setNextApprover(nextApprover);
			}else{
				studentAward.setNextApprover(null);
			}
			//保存审批流回显的信息
			studentAwardService.updateStudentAward(studentAward);
		}
		
		return result;
	}
	
	
	/**
	 * 
	 * @Title: applyTimeConfig
	 * @Description:  时间设置列表
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping(value="/sponsor/setDifficultAwardTime/opt-set/difficultAwardTimeList")
	public String applyTimeConfig(ModelMap model,HttpServletRequest request)
	{   
		String sponsorAwardTimeConfigCode = commonConfigService.getSponsorAwardTimeConfigCode();//获取奖助配置时间code
		String[] awardTimeList = sponsorAwardTimeConfigCode.split("#");
		List<TimeConfigModel> timeConfigList = commonConfigService.findByCondition(awardTimeList);
		model.addAttribute("timeConfigList", timeConfigList);
		return Constants.MENUKEY_STUDENT_AWARD+"/setTimeList";
	}
	
	
	
	/**
	 * 
	 * @Title: deleteDifficultStudentAward
	 * @Description:删除困难生奖助信息
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping(value = {"/sponsor/studentAward/opt-del/deleteDifficultStudentAward" },produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String deleteDifficultStudentAward(ModelMap model, HttpServletRequest request) {
		String id = request.getParameter("id");
		DifficultStudentAward difficultStudentAward = this.studentAwardService.getStudentAwardInfoById(id);
		// 删除状态(非正常的)
		Dic statusDeletedDics = Constants.STATUS_DELETED_DICS;
		difficultStudentAward.setDelStatus(statusDeletedDics);
		this.studentAwardService.updateStudentAward(difficultStudentAward);
		log.info("删除操作成功！");
		return "success";
	}
	
	
    /**
     * 
     * @Title: viewStudentAward
     * @Description:困难生奖助查看页面
     * @param model
     * @param request
     * @return
     * @throws
     */
	@RequestMapping("/sponsor/studentAward/opt-view/viewStudentAward")
	public String viewStudentAward(ModelMap model,HttpServletRequest request){
		log.info("困难生奖助查看页面");
		String id = request.getParameter("id");
		List<Dic> awardList = new ArrayList<Dic>();
		if(com.uws.core.util.StringUtils.hasText(id)){
			DifficultStudentAward difficultStudentAward = this.studentAwardService.getStudentAwardInfoById(id);
			queryGrantByType(model,difficultStudentAward.getId(),difficultStudentAward.getAwardType().getId());
			awardList.add(difficultStudentAward.getAwardType());
			model.addAttribute("awardList",awardList);
			model.addAttribute("studentAward", difficultStudentAward);
			List<FlowInstancePo> instanceList = this.flowInstanceService.geCurProcessHistory(id,true);
			model.addAttribute("instanceList",instanceList);
		}else{
			model.addAttribute("studentAward", new DifficultStudentAward());
		}
		model.addAttribute("flag","view");
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("genderList",dicUtil.getDicInfoList("GENDER"));
		model.addAttribute("financeList",dicUtil.getDicInfoList("FAMILY_FINANCE"));
		model.addAttribute("inspirationDic", Constants.STUDENT_AWARD_INSPIRATIONAL);
		model.addAttribute("aidGrantDic", Constants.STUDENT_AWARD_AID_GRANT);
		model.addAttribute("jobGrantDic", Constants.STUDENT_AWARD_JOB_GRANTL);
		model.addAttribute("schoolLoanDic", Constants.STUDENT_AWARD_SCHOOL_LOANL);
		model.addAttribute("tuitionDic", Constants.STUDENT_AWARD_TUITION_WAIVER);
		return Constants.MENUKEY_STUDENT_AWARD+"/studentAwardView";
	}
	
	/**
	 * 
	 * @Title: instructorApproveStudentAward
	 * @Description:审核困难生奖助列表
	 * @param model
	 * @param request
	 * @param difficultStudentInfo
	 * @param queyType
	 * @return
	 * @throws
	 */
	@RequestMapping("/sponsor/approveStudentAward/opt-query/approveDifficultStudentAwardList")
	public String instructorApproveStudentAward(ModelMap model,HttpServletRequest request,DifficultStudentAward difficultStudentAward){
		log.info("困难生审核列表");
	    String currentUserId = sessionUtil.getCurrentUserId();
	    String[] objectIds = flowInstanceService.getObjectIdByProcessKey(Constants.DIFFICULT_STUDENT_APPROVE_FLOW_KEY,currentUserId);
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
	   	Page page = this.studentAwardService.approveStudetAwardList(pageNo,Page.DEFAULT_PAGE_SIZE, difficultStudentAward,currentUserId,objectIds);
		model.addAttribute("page", page);
		//学院
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	//专业
    	List<BaseMajorModel> majorList = null;
    	//班级
    	List<BaseClassModel> classList = null;
    	if(difficultStudentAward!= null && difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege()!=null &&
    			com.uws.core.util.StringUtils.hasText(difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege().getId())){
    		majorList = compService.queryMajorByCollage(difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege().getId());
    	}
    	if(difficultStudentAward!= null && difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor()!=null &&
    			com.uws.core.util.StringUtils.hasText(difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor().getId())){
    		classList = compService.queryClassByMajor(difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor().getId());
    	}
    	model.addAttribute("studentAward", difficultStudentAward);
    	model.addAttribute("currentUserId", sessionUtil.getCurrentUserId());
    	model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("awardList",dicUtil.getDicInfoList("SPONSOR_AWARD_TYPE"));
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("statusList",dicUtil.getDicInfoList("STATUS"));
		model.addAttribute("financeList",dicUtil.getDicInfoList("FAMILY_FINANCE"));
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());
		return Constants.MENUKEY_STUDENT_AWARD_APPROVE+"/approveStudentAwardList";
	} 
	
	
	@RequestMapping({"/sponsor/studentAward/opt-edit/editApproveStudentAward"})
	public String editApproveStudentAward(ModelMap model,HttpServletRequest request,String id){
		log.info("困难生审核页面");
		List<Dic> awardList = new ArrayList<Dic>();
		if(com.uws.core.util.StringUtils.hasText(id)){
			DifficultStudentAward difficultStudentAward = this.studentAwardService.getStudentAwardInfoById(id);
			queryGrantByType(model,difficultStudentAward.getId(),difficultStudentAward.getAwardType().getId());
			awardList.add(difficultStudentAward.getAwardType());
			model.addAttribute("awardList",awardList);
			model.addAttribute("studentAward", difficultStudentAward);
		}else{
			model.addAttribute("studentAward", new DifficultStudentAward());
		}
			List<FlowHistoryPo> instanceList = this.flowInstanceService.getCurProcessHistory(id,ProjectConstants.IS_APPROVE_ENABLE);
			model.addAttribute("instanceList",instanceList);
			model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
			model.addAttribute("genderList",dicUtil.getDicInfoList("GENDER"));
			model.addAttribute("financeList",dicUtil.getDicInfoList("FAMILY_FINANCE"));
			model.addAttribute("inspirationDic", Constants.STUDENT_AWARD_INSPIRATIONAL);
			model.addAttribute("aidGrantDic", Constants.STUDENT_AWARD_AID_GRANT);
			model.addAttribute("jobGrantDic", Constants.STUDENT_AWARD_JOB_GRANTL);
			model.addAttribute("schoolLoanDic", Constants.STUDENT_AWARD_SCHOOL_LOANL);
			model.addAttribute("tuitionDic", Constants.STUDENT_AWARD_TUITION_WAIVER);
			model.addAttribute("flag","view");
			return Constants.MENUKEY_STUDENT_AWARD_APPROVE+"/approveStudentAwardEdit";
		}
		
	
	@RequestMapping({"/sponsor/studentAwardApprove/opt-save/saveStudentAwardApprove"})
	public String saveApproveStudentInfo(ModelMap model,HttpServletRequest request){
		String id = request.getParameter("id");
		String approveReason = request.getParameter("approveReason");
		DifficultStudentAward difficultStudentAwardPo = this.studentAwardService.getStudentAwardInfoById(id);
		if(difficultStudentAwardPo !=null && StringUtils.hasText(difficultStudentAwardPo.getProcessStatus()) && difficultStudentAwardPo.getProcessStatus().equals("PASS")){
			difficultStudentAwardPo.setStatus(Constants.STATUS_PASS_DICS);
		}else if(difficultStudentAwardPo !=null && StringUtils.hasText(difficultStudentAwardPo.getProcessStatus()) && difficultStudentAwardPo.getProcessStatus().equals("REJECT")){
			difficultStudentAwardPo.setStatus(Constants.STATUS_SAVE_DICS);
		}
		difficultStudentAwardPo.setApproveReason(approveReason);
		this.studentAwardService.updateStudentAward(difficultStudentAwardPo);
		return "redirect:/sponsor/approveStudentAward/opt-query/approveDifficultStudentAwardList.do";
	}
	
	  /**
	   * 
	   * @Title: checkCodeRepeat
	   * @Description:根据学年判断困难类型是否申请过
	   * @param id
	   * @param schoolYear
	   * @param awardType
	   * @return
	   * @throws
	   */
	   @RequestMapping({"/sponsor/studentAward/opt-query/awardTypeCheck"})
	   @ResponseBody
	   public String checkCodeRepeat(@RequestParam String id, @RequestParam String schoolYear, @RequestParam String awardType,@RequestParam String difficultStudentId)
	   { 
	     if (this.studentAwardService.isExistType(id,difficultStudentId,schoolYear,awardType)) {
	       return "";
	     }
	     return "true";
	   }
	   
	   /**
	    * 
	    * @Title: queryStudentAward
	    * @Description: 查询审核通过的学生奖助信息
	    * @param model
	    * @param request
	    * @param difficultStudentAward
	    * @return
	    * @throws
	    */
	   @RequestMapping({"/sponsor/studentAwardQuery/opt-query/queryStudentAward"})
	   public String queryStudentAward(ModelMap model,HttpServletRequest request,DifficultStudentAward difficultStudentAward){
		   log.info("困难生查询列表");
			int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		   	Page page = this.studentAwardService.queryStudentAward(pageNo,Page.DEFAULT_PAGE_SIZE, difficultStudentAward);
			model.addAttribute("page", page);
			//学院
	    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
	    	//专业
	    	List<BaseMajorModel> majorList = null;
	    	//班级
	    	List<BaseClassModel> classList = null;
	    	if(difficultStudentAward!= null && difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege()!=null &&
	    			com.uws.core.util.StringUtils.hasText(difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege().getId())){
	    		majorList = compService.queryMajorByCollage(difficultStudentAward.getDifficultStudentInfo().getStudent().getCollege().getId());
	    	}
	    	if(difficultStudentAward!= null && difficultStudentAward.getDifficultStudentInfo()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent()!= null && difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor()!=null &&
	    			com.uws.core.util.StringUtils.hasText(difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor().getId())){
	    		classList = compService.queryClassByMajor(difficultStudentAward.getDifficultStudentInfo().getStudent().getMajor().getId());
	    	}
	    	model.addAttribute("studentAward", difficultStudentAward);
	    	model.addAttribute("collegeList", collegeList);
			model.addAttribute("majorList", majorList);
			model.addAttribute("classList", classList);
			model.addAttribute("awardList",dicUtil.getDicInfoList("SPONSOR_AWARD_TYPE"));
			model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
			model.addAttribute("statusList",dicUtil.getDicInfoList("STATUS"));
			model.addAttribute("financeList",dicUtil.getDicInfoList("FAMILY_FINANCE"));
			return Constants.MENUKEY_STUDENT_AWARD+"/queryStudentAwardList";
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
		@RequestMapping({"/sponsor/studentAwardApproves/opt-query/checkedApproveList"})
		public String checkedApproveList(ModelMap model,DifficultStudentAward difficultStudentAward,String selectedBox,HttpServletRequest request) {
			
			List<DifficultStudentAward> stuList = new ArrayList<DifficultStudentAward>();
			if(selectedBox.indexOf(",") > -1) {
				String[] checkedIds = selectedBox.split(",");
				for(String s : checkedIds) {
					difficultStudentAward = this.studentAwardService.getStudentAwardInfoById(s);
					if(DataUtil.isNotNull(difficultStudentAward)) {
						stuList.add(difficultStudentAward);
					}
				}
			}else if(DataUtil.isNotNull(selectedBox)){
				difficultStudentAward = this.studentAwardService.getStudentAwardInfoById(selectedBox);
				if(DataUtil.isNotNull(difficultStudentAward)) {
					stuList.add(difficultStudentAward);
				}
			}
			model.addAttribute("stuList", stuList);
		    model.addAttribute("objectIds", selectedBox);
			return Constants.MENUKEY_STUDENT_AWARD_APPROVE+"/studentAwardMulApprove";
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
		@RequestMapping({"/sponsor/studentAward/opt-save/saveMutiResult.do"})
		public String saveMutiResult(ModelMap model,HttpServletRequest request,String mulResults) {
			
			List<ApproveResult> list  = this.flowInstanceService.getFormatedResult(mulResults,ProjectConstants.IS_APPROVE_ENABLE);
			if(DataUtil.isNotNull(list) && list.size()>0){
				this.studentAwardService.saveMulResult(list);
			}
			return "redirect:/sponsor/approveStudentAward/opt-query/approveDifficultStudentAwardList.do";
		}
		

}
