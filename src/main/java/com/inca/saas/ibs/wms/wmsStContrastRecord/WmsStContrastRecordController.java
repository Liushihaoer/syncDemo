//package com.inca.saas.ibs.wms.wmsStContrastRecord;
//
//import javax.servlet.http.HttpSession;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.SessionAttributes;
//
//import com.inca.saas.BaseController;
//import com.inca.saas.attachment.AttachmentPage;
//import com.inca.saas.attachment.AttachmentTable;
//import com.inca.saas.bridge.platform.BridgeService;
//import com.inca.saas.bridge.ws.fl.listen.FLStockContrastService;
//import com.inca.saas.datasource.DataSource;
//import com.inca.saas.export.ExportPage;
//import com.inca.saas.export.ExportTables;
//import com.inca.saas.ibs.entity.wms.WmsStContrastRecord;
//import com.inca.saas.ibs.sys.option.OptionService;
//import com.inca.saas.support.QueryResult;
//import com.inca.saas.ui.tablesetup.TableSetupPage;
//import com.inca.saas.ui.tablesetup.TableSetupPageTable;
///**
// * WmsStContrastRecordView
// * @date: (2016-11-23 22:20:57)
// * @author: <liubin>
// */
//@Controller
//@RequestMapping(WmsStContrastRecordController.FUNC_PATH)
//@SessionAttributes({WmsStContrastRecordController.SESSION_ATTR_QUERY})
//
//public class WmsStContrastRecordController extends BaseController {
//	
//	final Log log = LogFactory.getLog(getClass());
//	
//	public static final String FUNC_PATH = "/IBSWMS006";
//	
//	public static final String SESSION_ATTR_QUERY = "wms_wmsStContrastRecord_query";
//	
//	@ModelAttribute("funcPath")
//	String funcPath() {
//		return FUNC_PATH;
//	}
//
//	protected WmsStContrastRecordService getWmsStContrastRecordService() {
//		return serviceManager.lookup(WmsStContrastRecordService.SERVICE_NAME, WmsStContrastRecordService.class);
//	}
//	
//	OptionService getOptionService() {
//		return serviceManager.lookup(OptionService.SERVICE_NAME, OptionService.class);
//	}
//	
//	@ModelAttribute("IBS_STATUS_options")
//	public java.util.Map<String, String> IBS_STATUS_options() {
//		return getOptionService().load("IBS_STATUS");
//	}
//	@ModelAttribute("PUB_GOODS_STATUS_options")
//	public java.util.Map<String, String> PUB_GOODS_STATUS_options() {
//		return getOptionService().load("PUB_GOODS_STATUS");
//	}
//	@RequestMapping({"/"})
//	@TableSetupPage(tables=@TableSetupPageTable(id="datatable",table=WmsStContrastRecordTableSetup.class))
//	@AttachmentPage(tables=@AttachmentTable(model=WmsStContrastRecord.class))
//	@ExportPage(tables = {
//			@ExportTables(id = "datatable", entity =WmsStContrastRecord.class, searcher = WmsStContrastRecordService.SERVICE_NAME, notIncludeCol = "status,memo,createUserName") })
//	public String home(Model model, HttpSession session) throws Exception {
//		WmsStContrastRecordQuery query = (WmsStContrastRecordQuery) session.getAttribute(SESSION_ATTR_QUERY);
//		if (null == query) {
//			query = new WmsStContrastRecordQuery();
//			model.addAttribute(SESSION_ATTR_QUERY, query);
//		}
//		
//		return "ibs/wms/wmsStContrastRecord/home";
//	}
//	
//	@RequestMapping(value = "/search")
//	@ResponseBody
//	@DataSource("slave")
//	public QueryResult<WmsStContrastRecordView> search(@ModelAttribute(SESSION_ATTR_QUERY) WmsStContrastRecordQuery query, BindingResult bind,Model model) throws Exception {
//		QueryResult<WmsStContrastRecordView> result;
//		try {
//			result = getWmsStContrastRecordService().search(query);
//		} catch (Exception ex) {
//			log.error("search error", ex);
//			result = new QueryResult<>(query, "查询异常:" + ex.getMessage());
//		}
//		
//		return result;
//	}
//	
//	/*private List<WmsStContrastRecordView> getDataList(WmsStContrastRecordQuery query) throws Exception {
//		QueryResult<WmsStContrastRecordView> result = getWmsStContrastRecordService().search(query);
//		List<WmsStContrastRecordView> list = result.getData();
//		if (null == list || list.isEmpty()) {
//			throw new Exception(getMessage("无数据"));
//		}
//		return list;
//	}*/
//	
//	
//	@RequestMapping("/synwmsst")
//	@ResponseBody
//	public  String doEnable() throws Exception {
//		
//		try {
//			String isCykyWmsBriData = serviceManager.lookup(BridgeService.SERVICE_NAME, BridgeService.class)
//					.getBridgeInfo("WMS_FL_V1", false);
//			if (isCykyWmsBriData != null && isCykyWmsBriData.equals("true")) {
//				serviceManager.lookup(FLStockContrastService.SERVICE_NAME,FLStockContrastService.class).stockContrast();
//				return "1";
//			} else {
//				return getWmsStContrastRecordService().synwmsst();
//			}
//		} catch (Exception ex) {
//			log.error("启用 error", ex);
//			return ex.getMessage();
//		}
//	}
//
//	
//	@ResponseBody
//	@RequestMapping(value = "/taskStatus")
//	public WmsAsyncTask taskStatus(String cusDomain) {
//		WmsAsyncTask task = WmsAsyncTask.get(cusDomain);
//		return task;
//	}
//	
//}
