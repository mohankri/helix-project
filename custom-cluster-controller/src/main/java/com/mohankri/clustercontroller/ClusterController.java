package com.mohankri.clustercontroller;

//import com.mohankri.createsetup.CreateSetup;

import org.apache.helix.HelixManager;
import org.apache.helix.controller.HelixControllerMain;

/**
 * Hello world!
 *
 */
public class ClusterController
{
    public static void main( String[] args )
    {
        try {
            final String clusterName = "file-store-test1";
            final HelixManager manager = HelixControllerMain.startHelixController("localhost:2199",
                    //ClusterSetup.DEFAULT_CLUSTER_NAME, null, HelixControllerMain.STANDALONE);
                    clusterName, "controller", HelixControllerMain.STANDALONE);

            Runtime.getRuntime().addShutdownHook(new Thread() {
               @Override
                public void run() {
                   System.out.println("Disconnect...");
                   manager.disconnect();
               }
            });
             System.out.println("Wait to Join");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
