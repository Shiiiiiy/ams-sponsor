package com.uws.sponsor.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.sponsor.DifficultStudentInfo;
/**
* @ClassName: IDifficultyStudentDao 
* @Description: 困难生管理模块Dao接口
* @author liuchen
* @date 2015-8-7 下午16:23:08 
*
*/
public abstract interface IDifficultyStudentDao extends IBaseDao{
    /**
     * 
    * @Title: IDifficultyStudentDao.java 
    * @Package com.uws.sponsor.dao 
    * @Description: 查询困难生信息列表
    * @author liuchen  
    * @date 2015-7-30 下午1:31:19
     */
	Page queryStudetInfoList(int pageNo, int pageSize,DifficultStudentInfo difficultStudentInfo,String currentStudentId);
    
	/**
	 * 
	* @Title: IDifficultyStudentDao.java 
	* @Package com.uws.sponsor.dao 
	* @Description: 辅导员审核学生信息列表
	* @author liuchen
	* @date 2015-8-4 上午10:32:55
	 */
	Page instructorApproveStudetInfoList(int pageNo, int pageSize,DifficultStudentInfo difficultStudentInfo,String currentUserId,String[] objectIds);
    
	/**
	 * 
	 * @Title: queryDifficultStudentByStudentId
	 * @Description:根据当前学生查询困难生对象
	 * @param currentStudentId
	 * @return
	 * @throws
	 */
	DifficultStudentInfo queryDifficultStudentByStudentId(String currentStudentId);
    
	/**
	 * 
	 * @Title: queryDifficultStudentByApproveId
	 * @Description:根据当前审核人查询对象
	 * @param currentApproveId
	 * @return
	 * @throws
	 */
	DifficultStudentInfo queryDifficultStudentByApproveId(
			String currentApproveId);
    
    
	/**
	 * 
	 * @Title: queryPassStudetInfoList
	 * @Description:查询审核通过的困难生
	 * @param pageNo
	 * @param pageSize
	 * @param difficultStudentInfo
	 * @return
	 * @throws
	 */
	Page queryPassStudetInfoList(int pageNo, int pageSize,DifficultStudentInfo difficultStudentInfo,BaseAcademyModel college,List<BaseClassModel> classList);
    
	/**
	 * 
	 * @Title: isExistType
	 * @Description: 验证同一学年的学生是否申请过困难生
	 * @param id
	 * @param currentStudentId
	 * @param schoolYear
	 * @return
	 * @throws
	 */
	boolean isExistType(String id, String currentStudentId, String schoolYear);

	List<BaseClassModel> queryClassByTeacher(String currentUserId);
	
	
	
	

}
