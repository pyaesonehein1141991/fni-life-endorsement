//package org.ace.insurance.travel.expressTravel;
//
//import java.util.List;
//
//import org.ace.insurance.payment.service.interfaces.IPaymentService;
//import org.ace.insurance.product.service.interfaces.IProductService;
//import org.ace.insurance.report.motorOld.MotorProposalReport;
//import org.ace.insurance.system.common.city.City;
//import org.ace.insurance.system.common.city.service.interfaces.ICityService;
//import org.ace.insurance.system.common.occurrence.Occurrence;
//import org.ace.insurance.system.common.occurrence.service.interfaces.IOccurrenceService;
//import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
//import org.ace.insurance.workflow.persistence.interfaces.IWorkFlowDAO;
//import org.apache.log4j.Logger;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//public class ExpressDetailReportTest {
//	private static Logger logger = Logger.getLogger(MotorProposalReport.class);
//	private static ITravelProposalService travelProposalService;
//	private static ICityService cityService;
//	private static IPaymentService paymentService;
//	private static IWorkFlowDAO workFlowDAO;
//	private static IProductService productService;
//	private static IOccurrenceService occurrenceService;
//
//	@BeforeClass
//	public static void init() {
//		logger.info("ExpressDetailReportTest is started.........................................");
//		ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
//		BeanFactory factory = context;
//		travelProposalService = (ITravelProposalService) factory.getBean("TravelProposalService");
//		cityService = (ICityService) factory.getBean("CityService");
//		workFlowDAO = (IWorkFlowDAO) factory.getBean("WorkFlowDAO");
//		productService = (IProductService) factory.getBean("ProductService");
//		occurrenceService = (IOccurrenceService) factory.getBean("OccurrenceService");
//		logger.info("ExpressDetailReportTest instance has been loaded.");
//	}
//
//	public static void main(String[] args) {
//		org.junit.runner.JUnitCore.main(ExpressDetailReportTest.class.getName());
//	}
//
//	@Test
//	public void test() {
//		TravelProposal p = new TravelProposal();
//		City formCity = cityService.findCityById("ISSYS0050001000000028131032013");
//		City toCity = cityService.findCityById("ISSYS005HO000000028221062013");
//		Occurrence occurrence = occurrenceService.findOccurrenceById("ISSYS040001000000004229062014");
//		List<TravelExpress> trExpresses = travelProposalService.findExpressList();
//		Tour tour = new Tour();
//		tour.setTravelExpress(trExpresses.get(0));
//		tour.setOccurrence(occurrence);
//
//		// List<Tour> tourList = new ArrayList<Tour>();
//		// tourList.add(tour);
//		// trExpresses.get(0).setTourList(tourList);
//		// p.setExpressList(trExpresses);
//		// p.setProduct(productService.findProductById("ISPRD003001000000002115052014"));
//		// WorkFlowDTO workFlowDTO = new WorkFlowDTO();
//		// travelProposalService.addNewTravelProposal(p, workFlowDTO);
//		// travelProposalService.findTourList();
//		// for (TravelExpress travelExpress : trExpresses) {
//		// System.out.println(travelExpress.getId());
//		// }
//
//	}
//}
