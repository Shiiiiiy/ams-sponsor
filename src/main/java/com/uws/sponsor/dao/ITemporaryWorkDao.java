package com.uws.sponsor.dao;

import java.util.List;
import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.TemporaryWorkStudyModel;

/**
 * @Title ITemporaryWorkDao.java
 * @Package com.uws.sponsor.dao
 * @Description 临时工勤工助学Dao
 * @author lizj
 * @date 2015-7-31  上午11:41:42
 */
public abstract interface ITemporaryWorkDao extends IBaseDao {
	/**
	 * 分页查询
	 * @param temporaryWorkStudyModel
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	public Page queryTemporaryWorkInfo(TemporaryWorkStudyModel temporaryWorkStudyModel, int pageSize,int pageNo,int queryType);
	/**
	 * 条件查询
	 * @param temporaryWorkStudyModel
	 * @return
	 */
	public List<TemporaryWorkStudyModel> queryTemporaryWorkInfoByCond(TemporaryWorkStudyModel temporaryWorkStudyModel);
	/**
	 * 添加修改排重查询
	 * @param temporaryWorkStudyModelVO
	 * @return
	 */
	public List<TemporaryWorkStudyModel> queryAddTemporaryWorkInfoByCond(TemporaryWorkStudyModel temporaryWorkStudyModelVO);
	/**
	 * 通过ID数组批量逻辑删除
	 * @param temporaryWorkStudyModel
	 * @param ids
	 */
	public void updateMultTemporaryWorkByIds(TemporaryWorkStudyModel temporaryWorkStudyModel, String[] ids);
}
