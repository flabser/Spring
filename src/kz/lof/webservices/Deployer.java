package kz.lof.webservices;

import java.io.File;

import org.apache.axis.client.AdminClient;

public class Deployer {

	public static void main(String[] args) throws Exception {
		// список сервисов, которые опубликует Deployer
		final String[] serviceList = {
//				"BTIService",
//				"ForeignersSearchService",
//				"HumansSearchService",
//				"QuestService",
//				"TaxIndService",
//				"TaxPayService",
//				"UDPCamService",
//				"UDPService",
//		        "GKZService",
		        "UAIGService",
//			"SimpleService"
//				"SOAPMonitorService"
		};
		// URL того приложения, в которое будут опубликованы сервисы
		final String axisURL = "http://localhost:15045/WS";
//=====================================================================================
		for (String service : serviceList) {
			String descriptorPath = "webapps/WS/WEB-INF/deployers/" + service + "/deploy.wsdd";
			if (!new File(descriptorPath).exists()) {
				System.out.println("Файл " + descriptorPath + " не найден");
				continue;
			}
			String[] params = {"-l" + axisURL + "/AxisServlet", "-utemp_user", "-wtemp_password", descriptorPath};
			AdminClient ac = new AdminClient();
			ac.process(params);
		}
	}

}
