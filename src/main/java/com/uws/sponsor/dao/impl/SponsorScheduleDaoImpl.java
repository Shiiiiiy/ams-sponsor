package com.uws.sponsor.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.domain.sponsor.ScheduleModel;
import com.uws.sponsor.dao.ISponsorScheduleDao;

/**
 * 
* @ClassName: SponsorScheduleDaoImpl 
* @Description: 资助课表安排 DAO 实现
* @author 联合永道
* @date 2015-8-17 下午4:46:43 
*
 */
@Repository("com.uws.sponsor.dao.impl.SponsorScheduleDaoImpl")
public class SponsorScheduleDaoImpl extends BaseDaoImpl implements ISponsorScheduleDao
{

	/**
	 * 描述信息: 根据申请单编号查询课程安排集合
	 * @param applyId
	 * @return
	 * @see com.uws.sponsor.dao.ISponsorScheduleDao#queryScheduleByApplyId(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
    public List<ScheduleModel> queryScheduleByApplyId(String applyId)
    {
		StringBuffer hql = new StringBuffer("from ScheduleModel where applyFile.id = ? ");
		return this.query(hql.toString(), new Object[]{applyId});
    }

	/**
	 * 描述信息: 删除某个申请单下的学生课表安排信息 
	 * @param applyId
	 * @see com.uws.sponsor.dao.ISponsorScheduleDao#delAllByApplyId(java.lang.String)
	 */
	@Override
    public void delAllByApplyId(String applyId)
    {
	   String hql = " delete from ScheduleModel where applyFile.id = ? ";
		this.executeHql(hql,  new Object[]{applyId});
    }
	
	
}
