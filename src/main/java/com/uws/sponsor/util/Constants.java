package com.uws.sponsor.util;

import java.util.List;

import com.uws.common.util.SchoolYearUtil;
import com.uws.core.util.DateUtil;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

/**
 * 资助管理通用常量
 * @author liuchen
 *
 */
public class Constants {
	
	/**
	 * 数据字典工具类
	 */
	private static DicUtil dicUtil=DicFactory.getDicUtil();
	
	
	/**
	 *困难生信息维护返回页面公共路径
	 */
	public static final String MENUKEY_STUDENT_INFO = "/sponsor/difficultStudentInfo";
	
	/**
	 *困难生审核信息返回页面公共路径
	 */
	public static final String MENUKEY_STUDENT_APPROVE = "/sponsor/difficultStudentInfo/approve";
	
	
	/**
	 *困难生奖助信息维护返回页面公共路径
	 */
	public static final String MENUKEY_STUDENT_AWARD = "/sponsor/studentAward";
	
	
	/**
	 *困难生奖助审核信息返回页面公共路径
	 */
	public static final String MENUKEY_STUDENT_AWARD_APPROVE = "/sponsor/studentAward/approve";
	
	
	/**
	 * 国家助学贷款返回页面公共路径
	 * @author xuzh
	 */
	public static final String MENUKEY_NATIONAL_INFO = "/sponsor/nationalLoan";
	

	/**
	 * 生源地助学贷款返回页面公共路径
	 * @author xuzh
	 */
	public static final String MENUKEY_ORIGIN_INFO = "/sponsor/originLoan";
	
	/**
	 * 国家助学贷款审核返回页面公共路径
	 */
	public static final String MENUKEY_NATIONAL_APPROVE = "/sponsor/nationalLoan/approve";
	
	/**
	 * 获取当前学年
	 * @author xuzh
	 */
	public static final Dic currentYear =  SchoolYearUtil.getYearDic();
	
	/**
	 * 勤工助学--临时工助学维护返回页面公共路径
	 * @author lizj
	 */
	public static final String MENUKEY_WORK_TEMPORARY = "/sponsor/work/temporary/";
	/**
	 * 用工工时返回页面公共路径
	 * @author lizj
	 */
	public static final String MENUKEY_WORK_LABORHOUR = "/sponsor/work/laborHour/";
	
	/**
	 * 岗位设置维护返回页面公共路径
	 * @author zhangmx
	 */
	public static final String MENUKEY_SET_WORK = "/sponsor/work/setWork";
	/**
	 *岗位设置审核信息返回页面公共路径
	 * @author zhangmx
	 */
	public static final String MENUKEY_SET_WORK_APPROVE = "/sponsor/work/setWork/approve";
	/**
	 * 勤工助学维护返回页面公共路径
	 * @author zhangmx
	 */
	public static final String MENUKEY_WORK_STUDY = "/sponsor/work/workStudy";
	/**
	 * 勤工助学维护审核返回页面公共路径
	 * @author zhangmx
	 */
	public static final String MENUKEY_WORK_STUDY_APPROVE = "/sponsor/work/workStudy/approve";
	
	
	/**
	 * 【系统数据字典_逻辑删除_正常状态】
	 */
	public static final Dic STATUS_NORMAL_DICS=dicUtil.getDicInfo("STATUS_NORMAL_DELETED","NORMAL");
	
	/**
	 * 【系统数据字典_逻辑删除_删除状态】
	 */
	public static final Dic STATUS_DELETED_DICS=dicUtil.getDicInfo("STATUS_NORMAL_DELETED","DELETED");
	
	/**
	 * 获取系统维护的学年列表
	 */
	public static final List<Dic> yearList = dicUtil.getDicInfoList("YEAR");
	
	/**
	 * 【系统数据字典_保存状态_保存】
	 */
	public static final Dic STATUS_SAVE_DICS=dicUtil.getDicInfo("STATUS","SAVE");
	
	/**
	 * 【系统数据字典_保存状态_提交】
	 */
	public static final Dic STATUS_SUBMIT_DICS=dicUtil.getDicInfo("STATUS","SUBMIT");
	
	/**
	 * 【系统数据字典_保存状态_审核通过】
	 */
	public static final Dic STATUS_PASS_DICS=dicUtil.getDicInfo("STATUS","PASS");
	
	/**
	 * 确认岗位——去
	 */
	public static final Dic STATUS_GO_DICS=dicUtil.getDicInfo("STATUS_CONFIRM", "GO");
	/**
	 * 确认岗位——不去
	 */
	public static final Dic STATUS_NOT_GO_DICS=dicUtil.getDicInfo("STATUS_CONFIRM", "NOT_GO");
	/**
	 * 在岗
	 */
	public static final Dic STATUS_IS_POST=dicUtil.getDicInfo("POST_TYPE", "STATUS_IS_POST");
	/**
	 * 离岗
	 */
	public static final Dic STATUS_DISMISS=dicUtil.getDicInfo("POST_TYPE", "STATUS_DISMISS");
	/**
	 * 放弃岗
	 */
	public static final Dic STATUS_WASTE=dicUtil.getDicInfo("POST_TYPE", "STATUS_WASTE");
	
	
	/**
	 * 【系统数据字典_申请状态_已保存】
	 */
	public static final Dic STATUS_SAVE_DIC=dicUtil.getDicInfo("APPLY_STATUS", "SAVE");
	
	/**
	 * 【系统数据字典_申请状态_已提交】
	 */
	public static final Dic STATUS_SUBMIT_DIC=dicUtil.getDicInfo("APPLY_STATUS","SUBMIT");
	
	/**
	 * 【系统数据字典_申请状态_已确认】
	 */
	public static final Dic STATUS_CONFIRM_DIC=dicUtil.getDicInfo("APPLY_STATUS","CONFIRM");
	
	/**
	 * 【系统数据字典_申请状态_已打回】
	 */
	public static final Dic STATUS_REJECT_DIC=dicUtil.getDicInfo("APPLY_STATUS","REJECT");

	/**
	 * 【系统数据字典_申请状态_状态列表】
	 */
	public static final List<Dic> APPLY_STATUS_LIST= dicUtil.getDicInfoList("APPLY_STATUS");
	
	/**
	 * 【困难生获奖类型---单项励志奖学金】
	 * @liuchen
	 */
	public static final Dic STUDENT_AWARD_INSPIRATIONAL = dicUtil.getDicInfo("SPONSOR_AWARD_TYPE","INSPIRATIONAL_AWARD");
	
	/**
	 * 【困难生获奖类型---学校助学金】
	 * @liuchen
	 */
	public static final Dic STUDENT_AWARD_AID_GRANT = dicUtil.getDicInfo("SPONSOR_AWARD_TYPE","AID_GRANT");
	
	/**
	 * 【困难生获奖类型---就业补助】
	 * @liuchen
	 */
	public static final Dic STUDENT_AWARD_JOB_GRANTL = dicUtil.getDicInfo("SPONSOR_AWARD_TYPE","JOB_GRANT");
	
	/**
	 * 【困难生获奖类型---校内无息借款】
	 * @liuchen
	 */
	public static final Dic STUDENT_AWARD_SCHOOL_LOANL = dicUtil.getDicInfo("SPONSOR_AWARD_TYPE","SCHOOL_LOAN");
	
	/**
	 * 【困难生获奖类型--学费减免】
	 * @liuchen
	 */
	public static final Dic STUDENT_AWARD_TUITION_WAIVER = dicUtil.getDicInfo("SPONSOR_AWARD_TYPE","TUITION_WAIVER");

	// 奖助申请时间的code数组
	//public static final String[] SPONSOR_APPLY_TIME_CODE_ARRY =  new String[]{"SET_INSPIRATIONAL_TIME","SET_AID_TIME","STE_JOB_TIME","SET_SCHOOL_LOAN_TIME","SET_TUITION_TIME","SET_OTHER_AWARD"};
	
	// 奖助申请DicCode数组
	//public static final String[] SPONSOR_APPLY_DIC_CODE_ARRY =  new String[]{"INSPIRATIONAL_AWARD","AID_GRANT","JOB_GRANT","SCHOOL_LOAN","TUITION_WAIVER","OTHER_AWARD"};
	
	//困难生审批流程的KEY   
	public static final String DIFFICULT_STUDENT_APPROVE_FLOW_KEY = "SPONSOR_DIFFICULT_STUDENT_APPROVE";
	/**
	 * 【角色对应的查询方式】
	 */
	public static final int QUERY_TYPE_STUDENT = 1;
	public static final int QUERY_TYPE_TEACHER = 2;
	public static final int QUERY_TYPE_ADMIN = 3;

}
