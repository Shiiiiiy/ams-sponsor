package com.uws.sponsor.service;

import java.util.List;

import com.uws.apw.model.ApproveResult;
import com.uws.core.base.BaseModel;
import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.AidGrant;
import com.uws.domain.sponsor.DifficultStudentAward;
import com.uws.domain.sponsor.InspirationalAward;
import com.uws.domain.sponsor.JobGrant;
import com.uws.domain.sponsor.SchoolLoan;
import com.uws.domain.sponsor.TuitionWaiver;
import com.uws.domain.sponsor.OtherAward;
/**
 * 
* @ClassName: IStudentAwardService 
* @Description: 困难生奖助信息接口
* @author liuchen
* @date 2015-8-10 上午11:42:38 
*
 */
public abstract interface IStudentAwardService extends IBaseService{
    /**
     * 
     * @Title: queryStudentAwardList
     * @Description: 查询困难生奖助信息列表方法
     * @param pageNo
     * @param pageSize
     * @param studentAward
     * @return
     * @throws
     */
	public Page queryStudentAwardList(int pageNo, int pageSize,DifficultStudentAward studentAward,String crrentStudentId);
	
    /**
     * 
     * @Title: getStudentAwardInfoById
     * @Description: 根据ID查询困难生奖助对象
     * @param id
     * @return
     * @throws
     */
	public DifficultStudentAward getStudentAwardInfoById(String id);
    
	/**
	 * 
	 * @Title: updateStudentAward
	 * @Description: 修改困难生奖助信息接口方法
	 * @param difficultStudentAwardPo
	 * @throws
	 */
	public void updateStudentAward(DifficultStudentAward difficultStudentAwardPo);
    
	/**
	 * 
	 * @Title: saveStudentAward
	 * @Description: 保存困难生奖助信息接口方法
	 * @param difficultStudentAward
	 * @throws
	 */
	public void saveStudentAward(DifficultStudentAward difficultStudentAward);
	
    /**
     * 
     * @Title: saveModel（封装的方法）
     * @Description:保存各个奖助需要填写的内容 
     * @param model
     * @throws
     */
	void saveModel(BaseModel model);
	
	/**
	 * 
	 * @Title: updateModel
	 * @Description:修改各个奖项填写的内容
	 * @param model
	 * @throws
	 */
	public void updateModel(BaseModel model);
	
    /**
     * 
     * @Title: getInspirationalByAwardId
     * @Description:根据奖助id查询单项励志奖学金对象
     * @param awardId
     * @return
     * @throws
     */
	public InspirationalAward getInspirationalByAwardId(String awardId);
	
    /**
     * 
     * @Title: getAidGrantByAwardId
     * @Description: 根据奖助id查询学校助学金对象
     * @param awardId
     * @return
     * @throws
     */
	public AidGrant getAidGrantByAwardId(String awardId);
    
	/**
	 * 
	 * @Title: getJobGrantByAwardId
	 * @Description:根据困难生奖助id获取就业补助对象
	 * @param awardId
	 * @return
	 * @throws
	 */
	public JobGrant getJobGrantByAwardId(String awardId);
    
	/**
	 * 
	 * @Title: getSchoolLoanByAwardId
	 * @Description: 根据困难生奖助id获取校内无息借款对象
	 * @param awardId
	 * @return
	 * @throws
	 */
	public SchoolLoan getSchoolLoanByAwardId(String awardId);
    
	/**
	 * 
	 * @Title: getTuitionWaiverByAwardId
	 * @Description:根据奖助id获取学费减免对象
	 * @param awardId
	 * @return
	 * @throws
	 */
	public TuitionWaiver getTuitionWaiverByAwardId(String awardId);
	
    
	/**
	 * 
	 * @Title: approveStudetAwardList
	 * @Description:困难生奖助审核列表
	 * @param pageNo
	 * @param dEFAULT_PAGE_SIZE
	 * @param difficultStudentInfo
	 * @param currentUserId
	 * @return
	 * @throws
	 */
	public Page approveStudetAwardList(int pageNo, int pageSize,DifficultStudentAward difficultStudentAward, String currentUserId,String[] objectIds);
    
	/**
	 * 
	 * @Title: isExistType
	 * @Description:判断是否已申请了奖助类型
	 * @param id
	 * @param currentStudentId
	 * @param schoolYear
	 * @param awardType
	 * @return
	 * @throws
	 */
	public boolean isExistType(String id,String currentStudentId, String schoolYear, String awardType);
    
	
	/**
	 * 
	 * @Title: queryStudentAward
	 * @Description:查询奖助信息
	 * @param pageNo
	 * @param dEFAULT_PAGE_SIZE
	 * @param difficultStudentAward
	 * @return
	 * @throws
	 */
	public Page queryStudentAward(int pageNo, int dEFAULT_PAGE_SIZE,
			DifficultStudentAward difficultStudentAward);

	public void deleteModel(BaseModel model);
    
	/**
	 * 批量保存审核奖助信息
	 * @Title: saveMulResult
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param list
	 * @throws
	 */
	public void saveMulResult(List<ApproveResult> list);
    
	/**
	 * 
	 * @Title: getOtherAwardByAwardId
	 * @Description: 查询其他奖项的表单（不包含5个奖项）
	 * @param awardId
	 * @return
	 * @throws
	 */
	public OtherAward getOtherAwardByAwardId(String awardId);

    
	

}
