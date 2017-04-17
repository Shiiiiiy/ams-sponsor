package com.uws.sponsor.service;

import java.io.IOException;
import java.util.List;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.uws.core.base.IBaseService;
import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.OriginLoanModel;

public interface IOriginLoanService extends IBaseService {

	/**
	 * @Title: queryOriginLoan
	 * @Description: (学院以及学生处查询生源地助学贷款列表)
	 * @param pageNo
	 * @param pageSize
	 * @param OriginLoan
	 * @param collegeId
	 * @return
	 * @throws
	 */
	public Page queryOriginLoan(int pageNo, int pageSize, OriginLoanModel originLoan, String collegeId);

    /**
     * 
     * @Title: compareData
     * @Description: 比较数据重复
     * @param paramList
     * @return
     * @throws OfficeXmlFileException
     * @throws IOException
     * @throws IllegalAccessException
     * @throws ExcelException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws
     */
	public List<Object[]> compareData(List<OriginLoanModel> paramList) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException;
	
	/**
	 * 
	 * @Title: importOriginData
	 * @Description: TODO(导入最初始的数据)
	 * @param list
	 * @throws ExcelException
	 * @throws
	 */
	public void importOriginData(List<OriginLoanModel> list) throws ExcelException;

	
	/**
	 * 
	 * @Title: importLastData
	 * @Description: TODO(导入最近的数据)
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
	public void importLastData(List<Object[]> list, String filePath, String compareId)
			throws OfficeXmlFileException, IOException, IllegalAccessException,
			ExcelException, InstantiationException, ClassNotFoundException,
			Exception;
	/**
	 * 
	 * @Title: deleteById
	 * @Description: (通过ID删除生源地助学贷款信息)
	 * @param id
	 * @throws
	 */
	public void deleteById(String id);

	/**
	 * 
	 * @Title: findOriginLoanById
	 * @Description: (通过ID找打生源地助学贷款信息)
	 * @param id
	 * @return
	 * @throws
	 */
	public OriginLoanModel findOriginLoanById(String id);

	/**
	 * 
	 * @Title: update
	 * @Description: 更新生源地助学贷款信息
	 * @param originLoan
	 * @throws
	 */
	public void update(OriginLoanModel originLoan);

	
	
}
