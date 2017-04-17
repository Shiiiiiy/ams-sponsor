package com.uws.sponsor.service;

import java.util.List;

import com.uws.apw.model.ApproveResult;
import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.sponsor.DifficultStudentInfo;
/**
* 
* @ClassName: IDifficultyStudentService 
* @Description: 困难生管理模块接口
* @author liuchen
* @date 2015-8-7 下午16:21:08 
*
*/
public interface IDifficultyStudentService extends IBaseService{
    /**
     * 
    * @Title: IDifficultyStudentService.java 
    * @Package com.uws.sponsor.service 
    * @Description: 查询困难生信息列表
    * @author liuchen  
    * @date 2015-7-30 下午1:28:33
     */
	public Page queryStudetInfoList(int pageNo, int pageSize,DifficultStudentInfo difficultStudentInfo,String currentStudentId);
    
	/**
	 * 
	* @Title: IDifficultyStudentService.java 
	* @Package com.uws.sponsor.service 
	* @Description: 根据id获取困难生信息
	* @author liuchen 
	* @date 2015-7-30 下午3:04:06
	 */
	public DifficultStudentInfo findDifficultStudentById(String id);
	
	
    /**
     * 
    * @Title: IDifficultyStudentService.java 
    * @Package com.uws.sponsor.service 
    * @Description: 修改困难生信息
    * @author liuchen 
    * @date 2015-7-31 下午3:40:28
     */
	public void updateInfos(DifficultStudentInfo difficultStudent, String[] fileId);
    
	/**
	 * 
	* @Title: IDifficultyStudentService.java 
	* @Package com.uws.sponsor.service 
	* @Description: 保存困难生信息
	* @author liuchen 
	* @date 2015-7-31 下午3:40:51
	 */
	public void saveInfos(DifficultStudentInfo difficultStudentInfo, String[] fileId);
    
	/**
	 * 
	* @Title: IDifficultyStudentService.java 
	* @Package com.uws.sponsor.service 
	* @Description: 根据id修改困难生信息
	* @author liuchen 
	* @date 2015-8-3 上午9:43:22
	 */
	public void updateStudentInfo(DifficultStudentInfo difficultStudentPo);
    
	/**
	 * 
	* @Title: IDifficultyStudentService.java 
	* @Package com.uws.sponsor.service 
	* @Description: 班主任审核学生列表
	* @author liuchen  
	* @date 2015-8-4 上午10:30:11
	 */
	public Page instructorApproveStudetInfoList(int pageNo,int pageSize, DifficultStudentInfo difficultStudentInfo,String currentUserId,String[] objectIds);
    
	/**
	 * 
	 * @Title: queryDifficultStudentByStudentId
	 * @Description: 根据当前学生id获取困难生对象
	 * @param currentStudentId
	 * @return
	 * @throws
	 */
	public DifficultStudentInfo queryDifficultStudentByStudentId(String currentStudentId);
	
	
	public DifficultStudentInfo queryDifficultStudentByApproveId(String currentApproveId);
	
	
	/**
	 * 
	 * @Title: checkIsDifficutlStudent
	 * @Description:判断是否是困难生
	 * @param studentId
	 * @return
	 * @throws
	 */
	public boolean checkIsDifficutlStudent(String studentId);
    
    
    /**
     * 
     * @Title: queryPassStudetInfoList
     * @Description:查询审核通过的列表
     * @param pageNo
     * @param pageSize
     * @param difficultStudentInfo
     * @return
     * @throws
     */
	public Page queryPassStudetInfoList(int pageNo, int pageSize,DifficultStudentInfo difficultStudentInfo,BaseAcademyModel college,List<BaseClassModel> classList);
    
	/**
	 * 
	 * @Title: isExistType
	 * @Description:验证同一学年的学生是否申请过困难生
	 * @param id
	 * @param currentStudentId
	 * @param schoolYear
	 * @return
	 * @throws
	 */
	public boolean isExistType(String id, String currentStudentId,String schoolYear);
    
	/**
	 * 
	 * @Title: saveMulResult
	 * @Description: 批量审核操作
	 * @param list
	 * @throws
	 */
	public void saveMulResult(List<ApproveResult> list);
    
	/**
	 * 
	 * @Title: queryClassByTeacher
	 * @Description:根据当前登录人查询班级
	 * @param currentUserId
	 * @return
	 * @throws
	 */
	public List<BaseClassModel> queryClassByTeacher(String currentUserId);
}
