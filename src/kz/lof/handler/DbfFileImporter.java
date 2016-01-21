package kz.lof.handler;

import kz.lof.env.Organization;
import kz.lof.scheduler.AbstractDaemon;


public class DbfFileImporter extends AbstractDaemon{

	public int process(Organization org) {
		System.out.println("DbfFileImporter fake");
		return 0;
	}

	@Override
	public int process() {
		System.out.println("DbFileImporter fake");
		return 0;
	}

}
