package org.apache.tools.ant.taskdefs.optional.sitraka;

/**
 * Socket element for connection.
 * <tt>&lt;socket/&gt;</tt> defaults to host 127.0.0.1 and port 4444
 *
 * Otherwise it requires the host and port attributes to be set:
 * <tt>
 * &lt;socket host=&quote;175.30.12.1&quote; port=&quote;4567&quote;/&gt;
 * </tt>
 */
public class Socket {

	/** default to localhost */
	private String host = "127.0.0.1";

	/** default to 4444 */
	private int port = 4444;

	public void setHost(String value){
		host = value;
	}

	public void setPort(Integer value){
		port = value.intValue();
	}

	/** if no host is set, returning ':<port>', will take localhost */
	public String toString(){
		return host + ":" + port;
	}
}
