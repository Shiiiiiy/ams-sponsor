package com.uws.sponsor.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.Approver;
import com.uws.core.base.BaseModel;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.IdUtil;
import com.uws.domain.sponsor.AidGrant;
import com.uws.domain.sponsor.DifficultStudentAward;
import com.uws.domain.sponsor.InspirationalAward;
import com.uws.domain.sponsor.JobGrant;
import com.uws.domain.sponsor.OtherAward;
import com.uws.domain.sponsor.SchoolLoan;
import com.uws.domain.sponsor.TuitionWaiver;
import com.uws.sponsor.dao.IStudentAwardDao;
import com.uws.sponsor.service.IStudentAwardService;
import com.uws.sponsor.util.Constants;
import com.uws.user.model.User;
/**
 * 
* @ClassName: StudentAwardServiceImpl 
* @Description: 困难生奖助管理接口实现类
* @author liuchen
* @date 2015-8-10 上午11:45:58 
*
 */
@Service("com.uws.sponsor.service.impl.StudentAwardServiceImpl")
public class StudentAwardServiceImpl implements IStudentAwardService{
	@Autowired
	private IStudentAwardDao studentAwardDao;
	/**
	 * 描述信息: 困难生奖助列表查询实现方法
	 * @param pageNo
	 * @param pageSize
	 * @param studentAward
	 * @return
	 * @see com.uws.sponsor.service.IStudentAwardService#queryStudentAwardList(int, int, com.uws.domain.sponsor.StudentAward)
	 */
	@Override
	public Page queryStudentAwardList(int pageNo, int pageSize,DifficultStudentAward difficultStudentAward,String crrentStudentId) {
		
		return this.studentAwardDao.queryStudentAwardList(pageNo,pageSize,difficultStudentAward,crrentStudentId);
	}
	
	/**
	 * 描述信息: 根据困难生奖助id查询困难生奖助对象
	 * @param id
	 * @return
	 * @see com.uws.sponsor.service.IStudentAwardService#getStudentAwardInfoById(java.lang.String)
	 */
	@Override
	public DifficultStudentAward getStudentAwardInfoById(String id) {
		DifficultStudentAward studentAward =(DifficultStudentAward) this.studentAwardDao.get(DifficultStudentAward.class, id);
		return studentAward;
	}
	
	
	/**
	 * 描述信息: 保存困难生奖助信息方法
	 * @param difficultStudentAward
	 * @see com.uws.sponsor.service.IStudentAwardService#saveStudentAward(com.uws.domain.sponsor.DifficultStudentAward)
	 */
	@Override
	public void saveStudentAward(DifficultStudentAward difficultStudentAward) {
		if (!StringUtils.hasText(difficultStudentAward.getId()))
			difficultStudentAward.setId(IdUtil.getUUIDHEXStr());
		this.studentAwardDao.save(difficultStudentAward);
	}
	
	/**
	 * 描述信息: 修改困难生奖助信息方法
	 * @param difficultStudentAwardPo
	 * @see com.uws.sponsor.service.IStudentAwardService#updateStudentAward(com.uws.domain.sponsor.DifficultStudentAward)
	 */
	@Override
	public void updateStudentAward(DifficultStudentAward difficultStudentAwardPo) {
		this.studentAwardDao.update(difficultStudentAwardPo);
	}
	
	/**
	 * 描述信息:根据不同的奖项类型保存不同的奖项对象。
	 * @param model
	 * @see com.uws.sponsor.service.IStudentAwardService#saveModel(com.uws.core.base.BaseModel)
	 */
	@Override
	public void saveModel(BaseModel model) {
		this.studentAwardDao.save(model);
	}
	
	/**
	 * 
	 * 描述信息: 根据不同的奖项类型修改对象
	 * @param model
	 * @see com.uws.sponsor.service.IStudentAwardService#updateModel(com.uws.core.base.BaseModel)
	 */
	 @Override
    public void updateModel(BaseModel model) {
    	this.studentAwardDao.update(model);
    }
	
	/**
	 * 描述信息:根据奖助id获取单项励志奖学金对象
	 * @param awardId
	 * @return
	 * @see com.uws.sponsor.service.IStudentAwardService#getInspirationalByAwardId(java.lang.String)
	 */
	@Override
	public InspirationalAward getInspirationalByAwardId(String awardId) {
		
		return studentAwardDao.getInspirationalByAwardId(awardId);
	}
	
	
	/**
	 * 描述信息: 根据奖助id获取奖学金对象
	 * @param awardId
	 * @return
	 * @see com.uws.sponsor.service.IStudentAwardService#getAidGrantByAwardId(java.lang.String)
	 */
    @Override
    public AidGrant getAidGrantByAwardId(String awardId) {
    	return studentAwardDao.getAidGrantByAwardId(awardId);
    }
    
    /**
     * 描述信息:根据奖助id获取就业补助对象
     * @param awardId
     * @return
     * @see com.uws.sponsor.service.IStudentAwardService#getJobGrantByAwardId(java.lang.String)
     */
    @Override
    public JobGrant getJobGrantByAwardId(String awardId) {
    	return studentAwardDao.getJobGrantByAwardId(awardId);
    }
    
    /**
     * 描述信息: 根据奖助id获取校内无息贷款对象
     * @param awardId
     * @return
     * @see com.uws.sponsor.service.IStudentAwardService#getSchoolLoanByAwardId(java.lang.String)
     */
    @Override
    public SchoolLoan getSchoolLoanByAwardId(String awardId) {
    	return studentAwardDao.getSchoolLoanByAwardId(awardId);
    }
    
    /**
     * 描述信息:根据奖助id或者学费减免对象
     * @param awardId
     * @return
     * @see com.uws.sponsor.service.IStudentAwardService#getTuitionWaiverByAwardId(java.lang.String)
     */
    @Override
    public TuitionWaiver getTuitionWaiverByAwardId(String awardId) {
    	return studentAwardDao.getTuitionWaiverByAwardId(awardId);
    }
    
    /**
     * 描述信息:查询其他奖项的表单（不包含5个奖项）
     * @param awardId
     * @return
     * @see com.uws.sponsor.service.IStudentAwardService#getOtherAwardByAwardId(java.lang.String)
     */
    @Override
    public OtherAward getOtherAwardByAwardId(String awardId) {
    	return studentAwardDao.getOtherAwardByAwardId(awardId);
    }
    
    
    /**'
     * 描述信息: 困难生奖助审核列表
     * @param pageNo
     * @param pageSize
     * @param difficultStudentAward
     * @param currentUserId
     * @return
     * @see com.uws.sponsor.service.IStudentAwardService#approveStudetAwardList(int, int, com.uws.domain.sponsor.DifficultStudentAward, java.lang.String)
     */
	@Override
	public Page approveStudetAwardList(int pageNo, int pageSize,DifficultStudentAward difficultStudentAward, String currentUserId,String[] objectIds) {
		return this.studentAwardDao.approveStudetAwardList(pageNo,pageSize,difficultStudentAward,currentUserId,objectIds);
	}
	
	/**
	 * 描述信息:判断是否已申请了奖助
	 * @param id
	 * @param currentStudentId
	 * @param schoolYear
	 * @param awardType
	 * @return
	 * @see com.uws.sponsor.service.IStudentAwardService#isExistType(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isExistType(String id,String currentStudentId, String schoolYear, String awardType) {
		return this.studentAwardDao.isExistType(id,currentStudentId,schoolYear,awardType);
	}
	
	
	/**
	 * 描述信息: TODO (方法的用法 实现 描述)
	 * @param pageNo
	 * @param dEFAULT_PAGE_SIZE
	 * @param difficultStudentAward
	 * @return
	 * @see com.uws.sponsor.service.IStudentAwardService#queryStudentAward(int, int, com.uws.domain.sponsor.DifficultStudentAward)
	 */
	@Override
	public Page queryStudentAward(int pageNo, int pageSize,DifficultStudentAward difficultStudentAward) {
		return this.studentAwardDao.queryStudentAward(pageNo,pageSize,difficultStudentAward);
	}
	
	@Override
	public void deleteModel(BaseModel model) {
		this.studentAwardDao.delete(model);
	}
	
	
	@Override
	public void saveMulResult(List<ApproveResult> resultList) {
		if(resultList.size() > 0) {
			for(ApproveResult result : resultList) {
				DifficultStudentAward studentAward = this.getStudentAwardInfoById(result.getObjectId());
				if(DataUtil.isNotNull(result.getProcessStatusCode()) && result.getProcessStatusCode().equals("REJECT")) {
					studentAward.setStatus(Constants.STATUS_SAVE_DICS);//保存状态
				}else if(DataUtil.isNotNull(result.getProcessStatusCode()) && result.getProcessStatusCode().equals("PASS")){
					studentAward.setStatus(Constants.STATUS_PASS_DICS);//审批通过状态
				}
				studentAward.setProcessStatus(result.getProcessStatusCode());
				studentAward.setApproveStatus(result.getApproveStatus());
				User user = null;
				if(DataUtil.isNotNull(result.getNextApproverList()) && result.getNextApproverList().size() > 0) {
					Approver approve = result.getNextApproverList().get(0);
					user = new User(approve.getUserId());
				}
				if(DataUtil.isNotNull(user)) {       //下一节点审批人不为空
					studentAward.setNextApprover(user);
				}else{
					studentAward.setNextApprover(null);
				}
				this.studentAwardDao.update(studentAward);
			}
		}
	}
		

}
