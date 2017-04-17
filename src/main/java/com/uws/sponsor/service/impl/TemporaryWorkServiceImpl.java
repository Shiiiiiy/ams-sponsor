package com.uws.sponsor.service.impl;

import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.common.dao.IStudentCommonDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.sponsor.TemporaryWorkStudyModel;
import com.uws.sponsor.dao.ITemporaryWorkDao;
import com.uws.sponsor.service.ITemporaryWorkService;
import com.uws.sponsor.util.Constants;
import com.uws.user.model.Org;
import com.uws.user.model.User;

/**
 * @Title TemporaryWorkServiceImpl.java
 * @Package com.uws.sponsor.service.impl
 * @Description 临时工勤工助学Service
 * @author lizj
 * @date 2015-8-10  上午11:14:44
 */
@Service("temporaryWorkService")
public class TemporaryWorkServiceImpl implements ITemporaryWorkService {
	@Autowired
	private ITemporaryWorkDao temporaryWorkDao;
	@Autowired
	private IStudentCommonDao studentCommonDao;
	/**
	 * 通过ID查询
	 * @param id 主键
	 * @return model
	 */
	@Override
	public TemporaryWorkStudyModel queryTemporaryWorkStudyById(String id) {
		return (TemporaryWorkStudyModel) this.temporaryWorkDao.get(TemporaryWorkStudyModel.class, id);
	}
	/**
	 * 分页查询
	 * @param temporaryWorkStudyModel 查询条件
	 * @param pageSize 页的大小
	 * @param pageNo 页数
	 * @return
	 */
	@Override
	public Page queryTemporaryWorkInfo(TemporaryWorkStudyModel temporaryWorkStudyModel, int pageSize,int pageNo,int queryType) {
		return this.temporaryWorkDao.queryTemporaryWorkInfo(temporaryWorkStudyModel, pageSize, pageNo,queryType);
	}
	/**
	 * 添加
	 * @param temporaryWorkStudyModel
	 */
	@Override
	public void saveTemporaryWorkInfo(TemporaryWorkStudyModel temporaryWorkStudyModel) {
		this.temporaryWorkDao.save(temporaryWorkStudyModel);
	}
	/**
	 * 修改
	 * @param temporaryWorkStudyModel
	 */
	@Override
	public void updateTemporaryWorkInfo(TemporaryWorkStudyModel temporaryWorkStudyModel) {
		this.temporaryWorkDao.update(temporaryWorkStudyModel);
	}
	///**
	// * 逻辑删除
	// * @param temporaryWorkStudyModel
	// */
	//@Override
	//public void delTemporaryWorkInfo(TemporaryWorkStudyModel temporaryWorkStudyModel) {
	//	this.temporaryWorkDao.update(temporaryWorkStudyModel);
	//}
	/**
	 * 导入
	 * @param list
	 * @param temporaryWorkStudyModel
	 */
	@Override
	public void importTemporaryWorkInfo(List<TemporaryWorkStudyModel> list,TemporaryWorkStudyModel temporaryWorkStudyModel,double workSalary,Org org,String userId) {
		for (TemporaryWorkStudyModel workStudyModel : list) {
			StudentInfoModel studentInfoModel = this.studentCommonDao.queryStudentByStudentNo(workStudyModel.getStuNo());
			workStudyModel.setStudentId(studentInfoModel);
			workStudyModel.setSchoolYear(temporaryWorkStudyModel.getSchoolYear());
			workStudyModel.setSchoolTerm(temporaryWorkStudyModel.getSchoolTerm());
			workStudyModel.setWorkMonth(temporaryWorkStudyModel.getWorkMonth());
			workStudyModel.setWorkSalary(workSalary);
			workStudyModel.setTotalSalary(Math.round(workStudyModel.getWorkHour()*workSalary*100)/100.0);
			workStudyModel.setOrgName(temporaryWorkStudyModel.getOrgName());
			workStudyModel.setOrgId(org);
			workStudyModel.setOperator(new User(userId));
			workStudyModel.setStatus(Constants.STATUS_NORMAL_DICS);
			this.temporaryWorkDao.save(workStudyModel);
		}
	}
	/**
	 * 条件查询
	 * @param temporaryWorkStudyModel
	 * @return
	 */
	@Override
	public List<TemporaryWorkStudyModel> queryTemporaryWorkInfoByCond(TemporaryWorkStudyModel temporaryWorkStudyModel) {
		return this.temporaryWorkDao.queryTemporaryWorkInfoByCond(temporaryWorkStudyModel);
	}
	/**
	 * 添加修改排重查询
	 * @param temporaryWorkStudyModelVO
	 * @return
	 */
	@Override
	public List<TemporaryWorkStudyModel> queryAddTemporaryWorkInfoByCond(TemporaryWorkStudyModel temporaryWorkStudyModelVO) {
		return this.temporaryWorkDao.queryAddTemporaryWorkInfoByCond(temporaryWorkStudyModelVO);
	}
	/**
	 * 通过ID数组批量逻辑删除
	 * @param temporaryWorkStudyModel
	 * @param ids
	 */
	@Override
	public void updateMultTemporaryWorkByIds(TemporaryWorkStudyModel temporaryWorkStudyModel, String[] ids) {
		this.temporaryWorkDao.updateMultTemporaryWorkByIds(temporaryWorkStudyModel,ids);
	}
}
