package com.uws.sponsor.service;

import java.util.List;

import com.uws.core.base.IBaseService;
import com.uws.domain.sponsor.ScheduleModel;

/**
 * 
* @ClassName: ISponsorScheduleService 
* @Description:  资助课表安排 Service 接口
* @author 联合永道
* @date 2015-8-17 下午4:47:34 
*
 */
public interface ISponsorScheduleService extends IBaseService
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
	 * @Title: saveSponsorSchedule
	 * @Description: 保存某个申请下的课表信息
	 * @param applyId
	 * @param scheduleList
	 * @throws
	 */
	public void saveSponsorSchedule(String applyId,List<ScheduleModel> scheduleList);
	
	
}
