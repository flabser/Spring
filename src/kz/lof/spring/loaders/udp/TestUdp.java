package kz.lof.spring.loaders.udp;

import kz.lof.constants.OrgType;
import kz.lof.dataengine.Database;
import kz.lof.env.Environment;
import kz.lof.log.Log4jLogger;
import kz.lof.server.Server;
import org.junit.Test;

public class TestUdp {
    @Test
    public void testTest(){
        test();
    }

    public void test(){
        try {
            Server.logger = new Log4jLogger("");
            Environment.init();
            Environment.orgMap.get(OrgType.UDP).setidb(new Database(OrgType.UDP));

            Udp u = new Udp();
            u.init(Environment.orgMap.get(OrgType.UDP));
            u.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
