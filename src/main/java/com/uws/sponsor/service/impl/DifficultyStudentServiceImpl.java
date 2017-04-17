package com.uws.sponsor.service.impl;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.Approver;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.IdUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.sponsor.DifficultStudentInfo;
import com.uws.sponsor.dao.IDifficultyStudentDao;
import com.uws.sponsor.service.IDifficultyStudentService;
import com.uws.sponsor.util.Constants;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;
/**
* @ClassName: DifficultyStudentServiceImpl 
* @Description: 困难生管理模块接口实现类
* @author liuchen
* @date 2015-8-7 下午16:22:08 
*
*/
@Service("com.uws.sponsor.service.impl.DifficultyStudentServiceImpl")
public class DifficultyStudentServiceImpl implements IDifficultyStudentService{
	
	@Autowired
	private IDifficultyStudentDao difficultyStudentDao;
	
	//附件工具类
	private FileUtil fileUtil=FileFactory.getFileUtil();
	
	/**
     * 
    * @Title: DifficultyStudentServiceImpl.java 
    * @Package com.uws.sponsor.service.impl
    * @Description: 查询困难生信息列表
    * @author liuchen  
    * @date 2015-7-30 下午1:30:00
     */
	@Override
	public Page queryStudetInfoList(int pageNo, int pageSize,DifficultStudentInfo difficultStudentInfo,String currentStudentId) {
		
		return this.difficultyStudentDao.queryStudetInfoList(pageNo,pageSize,difficultStudentInfo,currentStudentId);
	}
	
	/**
	 * 描述信息: 根据id获取困难生对象 (方法的用法 实现 描述)
	 * @param id
	 * @return
	 * @see com.uws.sponsor.service.IDifficultyStudentService#findDifficultStudentById(java.lang.String)
	 */
	@Override
	public DifficultStudentInfo findDifficultStudentById(String id) {
		DifficultStudentInfo studentInfo =(DifficultStudentInfo) difficultyStudentDao.get(DifficultStudentInfo.class, id);
		return studentInfo;
	}
	
	/**
	 * 描述信息: 保存困难生信息 (方法的用法 实现 描述)
	 * @param difficultStudentInfo
	 * @param fileId
	 * @see com.uws.sponsor.service.IDifficultyStudentService#saveInfos(com.uws.domain.sponsor.DifficultStudentInfo, java.lang.String[])
	 */
	@Override
	public void saveInfos(DifficultStudentInfo difficultStudentInfo,String[] fileId) {
		if (!StringUtils.hasText(difficultStudentInfo.getId()))
			difficultStudentInfo.setId(IdUtil.getUUIDHEXStr());
		this.difficultyStudentDao.save(difficultStudentInfo);
		// 上传的附件进行处理
		if (!ArrayUtils.isEmpty(fileId))
		{
			for (String id : fileId)
				this.fileUtil.updateFormalFileTempTag(id,difficultStudentInfo.getId());
		}
	}
	
	/**
	 * 描述信息: 修改困难生信息(方法的用法 实现 描述)
	 * @param difficultStudent
	 * @param fileId
	 * @see com.uws.sponsor.service.IDifficultyStudentService#updateInfos(com.uws.domain.sponsor.DifficultStudentInfo, java.lang.String[])
	 */
	@Override
	public void updateInfos(DifficultStudentInfo difficultStudent,
			String[] fileId) {
		this.difficultyStudentDao.update(difficultStudent);
		//上传的附件进行处理
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(difficultStudent.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, difficultStudent.getId());
		  }
	}
	
	/**
	 * 描述信息: 逻辑删除困难生信息 (方法的用法 实现 描述)
	 * @param difficultStudentInfo
	 * @see com.uws.sponsor.service.IDifficultyStudentService#updateStudentInfo(com.uws.domain.sponsor.DifficultStudentInfo)
	 */
	@Override
	public void updateStudentInfo(DifficultStudentInfo difficultStudentPo) {
		if(DataUtil.isNotNull(difficultStudentPo)){
			this.difficultyStudentDao.update(difficultStudentPo);
		}else{
			this.difficultyStudentDao.save(difficultStudentPo);
		}
	}
	
	/**
     * 
    * @Title: DifficultyStudentServiceImpl.java 
    * @Package com.uws.sponsor.service.impl
    * @Description: 辅导员审核困难生信息列表
    * @author liuchen  
    * @date 2015-8-4 上午10:32:00
     */
	@Override
	public Page instructorApproveStudetInfoList(int pageNo, int pageSize,
			DifficultStudentInfo difficultStudentInfo,String currentUserId,String[] objectIds) {
		
		return this.difficultyStudentDao.instructorApproveStudetInfoList(pageNo,pageSize,difficultStudentInfo,currentUserId,objectIds);
	}
	
	/**
	 * 描述信息:根据当前学生id获取困难生对象
	 * @param currentStudentId
	 * @return
	 * @see com.uws.sponsor.service.IDifficultyStudentService#queryDifficultStudentByStudentId(java.lang.String)
	 */
	@Override
	public DifficultStudentInfo queryDifficultStudentByStudentId(
			String currentStudentId) {
		
		return difficultyStudentDao.queryDifficultStudentByStudentId(currentStudentId);
	}
	
	/**
	 * 描述信息:根据当前审核人查询下一节点审核的对象
	 * @param currentApproveId
	 * @return
	 * @see com.uws.sponsor.service.IDifficultyStudentService#queryDifficultStudentByApproveId(java.lang.String)
	 */
	@Override
	public DifficultStudentInfo queryDifficultStudentByApproveId(
			String currentApproveId) {
		return difficultyStudentDao.queryDifficultStudentByApproveId(currentApproveId);
	}
	
	
	/**
	 * 描述信息:判断是否是困难生
	 * @param studentId
	 * @return
	 * @see com.uws.sponsor.service.IDifficultyStudentService#checkIsDifficutlStudent(java.lang.String)
	 */
	@Override
	public boolean checkIsDifficutlStudent(String studentId) {
		boolean bol = false;
		if(StringUtils.hasText(studentId))
		{
			DifficultStudentInfo difficultStudentInfo = difficultyStudentDao.queryDifficultStudentByStudentId(studentId);
			if(null!=difficultStudentInfo && !"".equals(difficultStudentInfo.getId()))
				bol = true;
		}
		return bol;
	}
	
	
	/**
	 * 描述信息:查询审核通过的困难生列表
	 * @param pageNo
	 * @param pageSize
	 * @param difficultStudentInfo
	 * @return
	 * @see com.uws.sponsor.service.IDifficultyStudentService#queryPassStudetInfoList(int, int, com.uws.domain.sponsor.DifficultStudentInfo)
	 */
	@Override
	public Page queryPassStudetInfoList(int pageNo, int pageSize,DifficultStudentInfo difficultStudentInfo,BaseAcademyModel college,List<BaseClassModel> classList) {
		return this.difficultyStudentDao.queryPassStudetInfoList(pageNo,pageSize,difficultStudentInfo,college,classList);
	}
	
	/**
	 * 描述信息:验证同一学年的学生是否申请过困难生
	 * @param id
	 * @param currentStudentId
	 * @param schoolYear
	 * @return
	 * @see com.uws.sponsor.service.IDifficultyStudentService#isExistType(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isExistType(String id, String currentStudentId,String schoolYear) {
		return this.difficultyStudentDao.isExistType(id,currentStudentId,schoolYear);
	}
	
	
	@Override
	public void saveMulResult(List<ApproveResult> resultList) {
		if(resultList.size() > 0) {
			for(ApproveResult result : resultList) {
				DifficultStudentInfo difficultStudentInfo = this.findDifficultStudentById(result.getObjectId());
				if(DataUtil.isNotNull(result.getProcessStatusCode()) && result.getProcessStatusCode().equals("REJECT")) {
					difficultStudentInfo.setStatus(Constants.STATUS_SAVE_DICS);//保存状态
				}else if(DataUtil.isNotNull(result.getProcessStatusCode()) && result.getProcessStatusCode().equals("PASS")){
					difficultStudentInfo.setStatus(Constants.STATUS_PASS_DICS);//审批通过状态
				}
				difficultStudentInfo.setProcessStatus(result.getProcessStatusCode());
				difficultStudentInfo.setApproveStatus(result.getApproveStatus());
				User user = null;
				if(DataUtil.isNotNull(result.getNextApproverList()) && result.getNextApproverList().size() > 0) {
					Approver approve = result.getNextApproverList().get(0);
					user = new User(approve.getUserId());
				}
				if(DataUtil.isNotNull(user)) {       //下一节点审批人不为空
					difficultStudentInfo.setNextApprover(user);
				}else{
					difficultStudentInfo.setNextApprover(null);
				}
				this.difficultyStudentDao.update(difficultStudentInfo);
			}
		}
	}
	
	
	@Override
	public List<BaseClassModel> queryClassByTeacher(String currentUserId) {
		
		return this.difficultyStudentDao.queryClassByTeacher(currentUserId);
	}

}
