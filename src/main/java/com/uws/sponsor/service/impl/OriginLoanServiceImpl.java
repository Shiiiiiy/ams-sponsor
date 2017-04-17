package com.uws.sponsor.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.common.service.IStudentCommonService;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.SpringBeanLocator;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.config.TimeConfigModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.CountryBurseInfo;
import com.uws.domain.sponsor.OriginLoanModel;
import com.uws.sponsor.dao.IOriginLoanDao;
import com.uws.sponsor.service.IOriginLoanService;

@Service("originLoanService")
public class OriginLoanServiceImpl implements IOriginLoanService {
	
	@Autowired
	private IOriginLoanDao originLoanDao;
	@Autowired
	private IStudentCommonService studentCommonService;
	@Override
	
	
	/**
	 * 描述信息: TODO (导入初始的数据)
	 * @param list
	 * @throws ExcelException
	 * @see com.uws.sponsor.service.IOriginLoanService#importOriginData(java.util.List)
	 */
	public void importOriginData(List<OriginLoanModel> list) throws ExcelException{
		for(OriginLoanModel originLoan : list) {
			StudentInfoModel student = this.studentCommonService.queryStudentByStudentNo(originLoan.getStuNumber());
			if (student != null){
				originLoan.setStudentInfo(student);
			} else {
				throw new ExcelException("数据库中，没有该生的学号信息") ;
			}
			this.originLoanDao.save(originLoan);
		}
	}
	
	/**
	 * 
	 * @Title: importOriginLoanData
	 * @Description: TODO(对比之后的数据进行导入)
	 * @param list
	 * @param filePath
	 * @param compareId
	 * @throws OfficeXmlFileException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws ExcelException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws Exception
	 * @throws
	 */
	public void importLastData(List<Object[]> list, String filePath, String compareId) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException, Exception {
		Map<String, OriginLoanModel> map = new HashMap<String, OriginLoanModel>();
		for(Object[] array : list) {
			OriginLoanModel info = (OriginLoanModel)array[0];
			map.put(info.getStudentInfo().getStuNumber() + info.getLoanYear().getId(), info);
		}
		ImportUtil iu = new ImportUtil();
		//Excel数据
		List<OriginLoanModel> infoList = iu.getDataList(filePath, "importOriginLoan", null, OriginLoanModel.class);     
		
		for(OriginLoanModel xls : infoList) {
			String flag = xls.getStuNumber() + xls.getLoanYear().getId();
			
			if(!map.containsKey(flag)) {
				StudentInfoModel student = this.studentCommonService.queryStudentByStudentNo(xls.getStuNumber());
				xls.setStudentInfo(student);
				this.originLoanDao.save(xls);
			} else{
				OriginLoanModel infoPo = (OriginLoanModel) map.get(flag);  
				//已有数据
				//需要更新的记录
				if((StringUtils.isBlank(compareId)) || (!compareId.contains(infoPo.getId()))) {
					infoPo.setContractAmount(xls.getContractAmount());
					infoPo.setCountLoan(xls.getCountLoan());
					infoPo.setEnterYear(xls.getEnterYear());
					infoPo.setGraduationDate(xls.getGraduationDate());
					infoPo.setLoanBank(xls.getLoanBank());
					infoPo.setLoanYear(xls.getLoanYear());
					infoPo.setPaymentAmount(xls.getPaymentAmount());
					this.originLoanDao.update(infoPo);
				}
			}
		}
	}
	
	
	/**
	 * 描述信息: (学院以及学生处查询)
	 * @param pageNo
	 * @param pageSize
	 * @param nationalLoan
	 * @param collegeId
	 * @return
	 * @see com.uws.sponsor.service.IOriginLoanService#queryOriginLoan(int, int, com.uws.domain.sponsor.NationalLoanModel, java.lang.String)
	 */
	public Page queryOriginLoan(int pageNo, int pageSize, OriginLoanModel originLoan, String collegeId) {
		return this.originLoanDao.queryOriginLoan(pageNo, pageSize, originLoan, collegeId );
	}
	
	
	/**
	 * 
	 * @Title: compareData
	 * @Description: (导入之前的数据对比和数据库中已有的数据进行对比)
	 * @param list
	 * @return  返回重复的数据列
	 * @throws OfficeXmlFileException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws ExcelException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws
	 */
	public List<Object[]> compareData(List<OriginLoanModel> list) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException {
		List<Object[]> compareList = new ArrayList<Object[]>();
		Object[] array = (Object[])null;
		long count = this.originLoanDao.countOriginLoan();
		if(count != 0L) {
			for(int i = 0; i < count / 10 + 1L; i++) {
				Page page = this.originLoanDao.queryOriginLoanPage(i+1, 10, new OriginLoanModel());
				List<OriginLoanModel> infoList = (List<OriginLoanModel>)(page.getResult());
				for(OriginLoanModel info : infoList) {
					for(OriginLoanModel xls : list) {
						if((info.getStudentInfo().getStuNumber() + info.getLoanYear().getId()).equals
								(xls.getStuNumber() + xls.getLoanYear().getId()) ){
							array = new Object[]{info,xls};
							compareList.add(array);
							break;
						}
					}
				}
			}
		}
		return compareList;
	}
	/**
	 * 描述信息: 删除生源地助学贷款
	 * @param id
	 * @see com.uws.sponsor.service.IOriginLoanService#deleteById(java.lang.String)
	 */
	public void deleteById(String id) {
		if(StringUtils.isNotEmpty(id))
			originLoanDao.deleteById(OriginLoanModel.class, id);
	}
	
	
	/**
	 * 描述信息:  (通过ID找到生源地助学贷款信息)
	 * @param id
	 * @return
	 * @see com.uws.sponsor.service.IOriginLoanService#findOriginLoanById(java.lang.String)
	 */
	public OriginLoanModel findOriginLoanById(String id) {
		if(StringUtils.isNotEmpty(id))
			return (OriginLoanModel)originLoanDao.get(OriginLoanModel.class, id);
		return null;
	}

	/**
	 * 描述信息:  (更新生源地助学贷款)
	 * @param originLoan
	 * @see com.uws.sponsor.service.IOriginLoanService#update(com.uws.domain.sponsor.OriginLoanModel)
	 */
	public void update(OriginLoanModel originLoan) {
		if(originLoan != null)
			originLoanDao.update(originLoan);
	}
	
}
