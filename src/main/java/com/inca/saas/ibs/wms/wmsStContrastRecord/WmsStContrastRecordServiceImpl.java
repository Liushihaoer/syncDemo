//package com.inca.saas.ibs.wms.wmsStContrastRecord;
//
//import static org.springframework.beans.BeanUtils.copyProperties;
//import static org.springframework.util.StringUtils.isEmpty;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.inca.saas.BaseDao.BeanCopy;
//import com.inca.saas.BaseService;
//import com.inca.saas.CustomerDomainHolder;
//import com.inca.saas.bridge.jms.wms.jzt.v1.stcontrast.PushStContrastService;
//import com.inca.saas.ibs.entity.pub.Factory;
//import com.inca.saas.ibs.entity.pub.Goods;
//import com.inca.saas.ibs.entity.pub.Lot;
//import com.inca.saas.ibs.entity.wms.QWmsStContrastRecord;
//import com.inca.saas.ibs.entity.wms.WmsStContrastRecord;
//import com.inca.saas.ibs.pub.factory.FactoryInfo;
//import com.inca.saas.ibs.pub.goods.GoodsInfo;
//import com.inca.saas.ibs.pub.lot.LotInfo;
//import com.inca.saas.ibs.sys.option.OptionService;
//import com.inca.saas.service.DecimalService;
//import com.inca.saas.support.QueryResult;
//import com.mysema.query.BooleanBuilder;
//import com.mysema.query.types.Predicate;
//
///**
// * WmsStContrastRecordServiceImpl
// * @date: (2016-11-23 22:20:57)
// * @author: <liubin>
// */
//@Service(WmsStContrastRecordService.SERVICE_NAME)
//@Transactional
//public class WmsStContrastRecordServiceImpl extends BaseService implements WmsStContrastRecordService {
//
//	final Log log = LogFactory.getLog(getClass());
//
//	@Autowired
//	WmsStContrastRecordDao wmsStContrastRecordDao;
//	@Autowired
//	DecimalService decimalService;
//
//	public WmsStContrastRecordServiceImpl() {
//		super();
//	}
//	
//
//	@Override
//	@Transactional(rollbackFor = Exception.class)
//	public String synwmsst() throws Exception {
//		String cusDomain = CustomerDomainHolder.get();
//		new WmsAsyncTask().run(new WmsAsyncTaskRunner(){
//
//			@Override
//			public String run() throws Throwable {
//				WmsAsyncTask.current().setMaxStep(100).save();
//				synWmsStContrast(cusDomain);
//				WmsAsyncTask.current().setCurrentStep(10).save();
//				return "";
//			}
//
//		});
//		return cusDomain;
//	}
//	
//	public void synWmsStContrast(String cusDomain) throws Exception {
//		CustomerDomainHolder.set(cusDomain);
//		serviceManager.lookup(PushStContrastService.SERVICE_NAME, PushStContrastService.class).send();
//	}
//	
//}
