package org.apache.synapse.util;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * This is a thread-safe version of the Axiom UUIDGenerator
 * to be used until it is fixed in the next Axiom release
 */
public class UUIDGenerator {
    /** This class will give UUIDs for axis2. */

    private static String baseUUID = null;
    private static long incrementingValue = 0;

    private static Random myRand = null;
    private static boolean useNano = false;

    /**
     * MD5 a random string with localhost/date etc will return 128 bits construct a string of 18
     * characters from those bits.
     *
     * @return string
     */
    public static String getUUID() {
        if (baseUUID == null) {
            baseUUID = getInitialUUID();
            baseUUID = "urn:uuid:" + baseUUID;
        }
        if (++incrementingValue >= Long.MAX_VALUE) {
            incrementingValue = 0;
        }

        if (useNano) {
            return baseUUID + (System.nanoTime() + incrementingValue) +
                Integer.toString(myRand.nextInt());
        } else {

            return baseUUID + (System.currentTimeMillis() + incrementingValue +
                Integer.toString(myRand.nextInt()));
        }

    }

    protected static String getInitialUUID() {

        try {
            if (System.class.getMethod("nanoTime", new Class[0]) != null) {
                useNano = true;
            }
        } catch (NoSuchMethodException ignore) {}

        if (myRand == null) {
            myRand = new Random();
        }
        long rand = myRand.nextLong();
        String sid;
        try {
            sid = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            sid = Thread.currentThread().getName();
        }
        StringBuffer sb = new StringBuffer();
        sb.append(sid);
        sb.append(":");
        sb.append(Long.toString(rand));
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
        }
        md5.update(sb.toString().getBytes());
        byte[] array = md5.digest();
        StringBuffer sb2 = new StringBuffer();
        for (int j = 0; j < array.length; ++j) {
            int b = array[j] & 0xFF;
            sb2.append(Integer.toHexString(b));
        }
        int begin = myRand.nextInt();
        if (begin < 0) begin = begin * -1;
        begin = begin % 8;
        return sb2.toString().substring(begin, begin + 18).toUpperCase();
    }
}
