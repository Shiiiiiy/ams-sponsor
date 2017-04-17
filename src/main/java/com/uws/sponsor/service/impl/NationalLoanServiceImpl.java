package com.uws.sponsor.service.impl;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.sponsor.NationalLoanModel;
import com.uws.sponsor.dao.INationalLoanDao;
import com.uws.sponsor.service.INationalLoanService;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;

@Service("nationalLoanService")
public class NationalLoanServiceImpl implements INationalLoanService {
	
	//数据字典工具类
    private DicUtil dicUtil = DicFactory.getDicUtil();
	//附件工具类
	private FileUtil fileUtil=FileFactory.getFileUtil();
	
	@Autowired
	private INationalLoanDao nationalLoanDao;
	/**
     * 
    * @Title: NationalLoanServiceImpl.java 
    * @Package com.uws.sponsor.service.impl
    * @Description: 学生查询国家助学贷款学生列表
    * @author xuzh  
    * @date 2015-7-31 上午11:05:00
     */
	@Override
	public Page queryNationalLoanByStudent(int pageNo, int pageSize, NationalLoanModel nationalLoan, String loginStudentId ) {
		return this.nationalLoanDao.queryNationalLoanByStudent(pageNo, pageSize, nationalLoan, loginStudentId);
	}
	
	
	/**
	 * 根据id获取国家助学贷款学生信息
	 */
	@Override
	public NationalLoanModel findNationalLoanById(String id) {
		NationalLoanModel nationalLoan =(NationalLoanModel) nationalLoanDao.get(NationalLoanModel.class, id);
		return nationalLoan;
	}
	
	/**
	 * saveNationalLoan(保存国家助学贷款信息)
	 */
	@Override
	public void saveInfos(NationalLoanModel nationalLoan,
			String[] fileId) {
		this.nationalLoanDao.save(nationalLoan);
		//上传的附件进行处理
		 if (ArrayUtils.isEmpty(fileId)) {
		       return;
		    }
       for (String id : fileId)
       this.fileUtil.updateFormalFileTempTag(id, nationalLoan.getId());
	}
	
	
	/**
	 * updateNationalLoanInfo(修改国家助学贷款信息)
	 */
	@Override
	public void updateInfos(NationalLoanModel nationalLoan,
			String[] fileId) {
		 this.nationalLoanDao.update(nationalLoan);
		 //上传的附件进行处理
		 if (ArrayUtils.isEmpty(fileId))
			 fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(nationalLoan.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId())){
		    	   this.fileUtil.deleteFormalFile(ufr);
		       }
		     }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, nationalLoan.getId());
		     }
	}

	/*
	 * 删除国家助学贷款信息
	 * @see com.uws.sponsor.service.INationalLoanService#deleteNationalLoan(java.lang.String)
	 */
	public void deleteNationalLoan(String id) {
		if(id != null)
			this.nationalLoanDao.deleteById(NationalLoanModel.class, id);
	}

	public void update(NationalLoanModel nationalLoan) {
		nationalLoanDao.update(nationalLoan);
	}

	/**
	 * 描述信息: TODO (教师查询国家助学贷款)
	 * @param pageNo
	 * @param pageSize
	 * @param nationalLoan
	 * @param currentOrgId
	 * @param student
	 * @return
	 * @see com.uws.sponsor.service.INationalLoanService#queryNationalLoanPage(java.lang.Integer, java.lang.Integer, com.uws.domain.sponsor.NationalLoanModel, java.lang.String, com.uws.domain.orientation.StudentInfoModel)
	 */
    public Page queryNationalLoanByTeacher(Integer pageNo, Integer pageSize, NationalLoanModel nationalLoan, String currentOrgId) {
		 return nationalLoanDao.queryNationalLoanByTeacher(pageNo, pageSize, nationalLoan, currentOrgId);
    }

	/**
	 * 描述信息: 国家助学贷款审核列表页面
	 * @param pageNo
	 * @param dEFAULT_PAGE_SIZE
	 * @param nationalLoan
	 * @return
	 * @see com.uws.sponsor.service.INationalLoanService#queryNationalLoanApprovePage(int, int, com.uws.domain.sponsor.NationalLoanModel)
	 */
	public Page queryNationalLoanApproveList(int pageNo, int pageSize, NationalLoanModel nationalLoan) {
		return nationalLoanDao.queryNationalLoanApproveList(pageNo, pageSize, nationalLoan);
	}

	/**
	 * 验证该生同一学年是否申请过国家助学贷款信息
	 */
	@Override
	public boolean isApply(String id, String currentStudentId, String loanYear) {
		List<NationalLoanModel> nationalLoans = nationalLoanDao.applyList(currentStudentId, loanYear);
		
		if(nationalLoans == null || nationalLoans.size() == 0)
			return false;
		if(nationalLoans != null && nationalLoans.size() == 1 ){
			for(NationalLoanModel nationalLoan: nationalLoans){
				if(id != null && nationalLoan.getId().equals(id))
					return false;
			}
		}
		
		return true;
   }
	
}
