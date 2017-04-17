package com.uws.sponsor.dao;

import com.uws.core.base.BaseModel;
import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.AidGrant;
import com.uws.domain.sponsor.DifficultStudentAward;
import com.uws.domain.sponsor.DifficultStudentInfo;
import com.uws.domain.sponsor.InspirationalAward;
import com.uws.domain.sponsor.JobGrant;
import com.uws.domain.sponsor.OtherAward;
import com.uws.domain.sponsor.SchoolLoan;
import com.uws.domain.sponsor.TuitionWaiver;
/**
 * 
* @ClassName: IStudentAwardDao 
* @Description: 困难生奖助管理Dao层
* @author liuchen
* @date 2015-8-10 上午11:48:12 
*
 */
public abstract interface IStudentAwardDao extends IBaseDao{
    /**
     * 
     * @Title: queryStudentAwardList
     * @Description: 查看困难生奖助信息列表
     * @param pageNo
     * @param pageSize
     * @param difficultStudentAward
     * @return
     * @throws
     */
	public Page queryStudentAwardList(int pageNo, int pageSize,DifficultStudentAward difficultStudentAward,String crrentStudentId);
	
    /**
     * 
     * @Title: getInspirationalByAwardId
     * @Description:根据奖助id获取单项励志奖学金对象
     * @param awardId
     * @return
     * @throws
     */
	public InspirationalAward getInspirationalByAwardId(String awardId);
    
	/**
	 * 
	 * @Title: getAidGrantByAwardId
	 * @Description: 根据奖助id获取奖学金对象
	 * @param awardId
	 * @return
	 * @throws
	 */
	public AidGrant getAidGrantByAwardId(String awardId);
    
	/**
	 * 
	 * @Title: getJobGrantByAwardId
	 * @Description: 根据奖助id获取就业补助对象
	 * @param awardId
	 * @return
	 * @throws
	 */
	public JobGrant getJobGrantByAwardId(String awardId);
	
	/**
	 * 
	 * @Title: getSchoolLoanByAwardId
	 * @Description: 根据奖助id获取校内无息贷款对象
	 * @param awardId
	 * @return
	 * @throws
	 */
	public SchoolLoan getSchoolLoanByAwardId(String awardId);
    
	/**
	 * 
	 * @Title: getTuitionWaiverByAwardId
	 * @Description:根据奖助id或者学费减免对象
	 * @param awardId
	 * @return
	 * @throws
	 */
	public TuitionWaiver getTuitionWaiverByAwardId(String awardId);
	
	/**
	 * 
	 * @Title: getOtherAwardByAwardId
	 * @Description: :查询其他奖项的表单（不包含5个奖项）
	 * @param awardId
	 * @return
	 * @throws
	 */
	public OtherAward getOtherAwardByAwardId(String awardId);

    
	/**
	 * 
	 * @Title: approveStudetAwardList
	 * @Description: 困难生奖助审核列表
	 * @param pageNo
	 * @param pageSize
	 * @param difficultStudentAward
	 * @param currentUserId
	 * @return
	 * @throws
	 */
	public Page approveStudetAwardList(int pageNo, int pageSize,DifficultStudentAward difficultStudentAward, String currentUserId,String[] objectIds);

	public boolean isExistType(String id,String currentStudentId, String schoolYear, String awardType);

	public Page queryStudentAward(int pageNo, int pageSize,DifficultStudentAward difficultStudentAward);

	
	

}
