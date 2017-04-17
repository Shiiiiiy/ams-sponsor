package com.uws.sponsor.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.uws.core.base.BaseServiceImpl;
import com.uws.domain.sponsor.ScheduleModel;
import com.uws.sponsor.dao.ISponsorScheduleDao;
import com.uws.sponsor.service.ISponsorScheduleService;

/**
 * 
* @ClassName: SponsorScheduleServiceImpl 
* @Description: 资助课表安排 Service 实现
* @author 联合永道
* @date 2015-8-17 下午4:48:30 
*
 */
@Service("com.uws.sponsor.service.impl.SponsorScheduleServiceImpl")
public class SponsorScheduleServiceImpl extends BaseServiceImpl implements ISponsorScheduleService
{
	@Autowired
	private ISponsorScheduleDao sponsorScheduleDao;

	/**
	 * 描述信息: 根据申请单编号查询课程安排集合
	 * @return
	 * @see com.uws.sponsor.service.ISponsorScheduleService#queryScheduleByApplyId()
	 */
	@Override
    public List<ScheduleModel> queryScheduleByApplyId(String applyId)
    {
		if(StringUtils.hasText(applyId))
			return sponsorScheduleDao.queryScheduleByApplyId(applyId);
		return null;
    }

	/**
	 * 描述信息: 保存某个申请下的学生课表信息
	 * @param applyId
	 * @param scheduleList
	 * @see com.uws.sponsor.service.ISponsorScheduleService#saveSponsorSchedule(java.lang.String, java.util.List)
	 */
	@Override
    public void saveSponsorSchedule(String applyId,List<ScheduleModel> scheduleList)
    {
	    /*
	     * 步骤：没有状态记录，也不会有其他外键关联 故：
	     * 		1 删除申请单下的所有的课程安排
		 *		2 保存插入
	     */
		sponsorScheduleDao.delAllByApplyId(applyId);
		for(ScheduleModel schedule : scheduleList)
			sponsorScheduleDao.save(schedule);
    }
	
}
