package com.uws.sponsor.service;

import java.util.List;
import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.TemporaryWorkStudyModel;
import com.uws.user.model.Org;

/**
 * @Title ITemporaryWorkService.java
 * @Package com.uws.sponsor.service
 * @Description 临时工勤工助学Service
 * @author lizj
 * @date 2015-7-31  上午11:40:55
 */
public interface ITemporaryWorkService extends IBaseService {
	/**
	 * 通过ID查询
	 * @param id 主键
	 * @return model
	 */
	public TemporaryWorkStudyModel queryTemporaryWorkStudyById(String id);
	/**
	 * 分页查询
	 * @param temporaryWorkStudyModel 查询条件
	 * @param pageSize 页的大小
	 * @param pageNo 页数
	 * @return
	 */
	public Page queryTemporaryWorkInfo(TemporaryWorkStudyModel temporaryWorkStudyModel,int pageSize, int pageNo,int queryType);
	/**
	 * 条件查询
	 * @param temporaryWorkStudyModel
	 * @return
	 */
	public List<TemporaryWorkStudyModel> queryTemporaryWorkInfoByCond(TemporaryWorkStudyModel temporaryWorkStudyModel);
	/**
	 * 添加
	 * @param temporaryWorkStudyModel
	 */
	public void saveTemporaryWorkInfo(TemporaryWorkStudyModel temporaryWorkStudyModel);
	/**
	 * 修改
	 * @param temporaryWorkStudyModel
	 */
	public void updateTemporaryWorkInfo(TemporaryWorkStudyModel temporaryWorkStudyModel);
	///**
	// * 逻辑删除
	// * @param temporaryWorkStudyModel
	// */
	//public void delTemporaryWorkInfo(TemporaryWorkStudyModel temporaryWorkStudyModel);
	/**
	 * 导入
	 * @param list
	 * @param temporaryWorkStudyModel
	 */
	public void importTemporaryWorkInfo(List<TemporaryWorkStudyModel> list,TemporaryWorkStudyModel temporaryWorkStudyModel,double workSalary,Org org,String userId);
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
