package com.uws.sponsor.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.domain.sponsor.ScheduleModel;

/**
 * 
* @ClassName: ISponsorScheduleDao 
* @Description: 资助课表安排 DAO 接口
* @author 联合永道
* @date 2015-8-17 下午4:45:23 
*
 */
public interface ISponsorScheduleDao extends IBaseDao
{
	/**
	 * 
	 * @Title: queryScheduleByApplyId
	 * @Description: 根据申请单编号查询课程安排集合
	 * @return
	 * @throws
	 */
	public List<ScheduleModel> queryScheduleByApplyId(String applyId);
	
	/**
	 * 
	 * @Title: delAllByApplyId
	 * @Description: 删除某个申请下的课表安排
	 * @param applyId
	 * @throws
	 */
	public void delAllByApplyId(String applyId);
	
}
