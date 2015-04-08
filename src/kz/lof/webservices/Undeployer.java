package kz.lof.webservices;

import java.io.File;

public class Undeployer {

	public static void main(String[] args) {
		// список сервисов, которые удалит Undeployer
		final String[] serviceList = {
				"BTIService",
/*				"ForeignersSearchService",
				"HumansSearchService",
				"QuestService",
				"TaxIndService",
				"UDPCamService",
				"UDPService"*/
		};
		// URL того приложения, с которого будут удалены веб-сервисы
		final String axisURL = "http://localhost:15045/WS/AxisServlet";
//=========================================================================================
		for (String service : serviceList) {
			String descriptorPath = "webapps/WS/WEB-INF/deployers/" + service + "/undeploy.wsdd";
			if (!new File(descriptorPath).exists()) {
				System.out.println("Файл " + descriptorPath + " не найден");
				continue;
			}
			String[] params = {"-l" + axisURL, descriptorPath};
			org.apache.axis.client.AdminClient.main(params);
		}
	}

}
