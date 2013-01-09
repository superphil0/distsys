/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTesting;

import ManagementClient.ManagementClientCallback;

/**
 *
 * @author daniela
 */
public class TestManagementClient extends ManagementClientCallback {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8008659861415306492L;

	public TestManagementClient() {
        super();
        System.out.println("tmc started - test console");
    }

    @Override
    protected void sendOutput(String output) {
        System.out.println(output);
    }

}
