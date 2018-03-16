package org.apache.synapse.statistics;

import org.apache.synapse.SynapseConstants;

import java.util.*;

/**
 * To collect statistics
 */

public class StatisticsCollector {

    /**  A synchronized map for holding sequence statistics  */
    private Map sequenceStatistics = Collections.synchronizedMap(new HashMap());

    /**  A synchronized map for holding end point statistics */
    private Map endpointStatistics = Collections.synchronizedMap(new HashMap());

    /**  A synchronized map for holding proxy services statistics */
    private Map proxyServicesStatistics = Collections.synchronizedMap(new HashMap());

    /**
     * To report the statistics related to a  EndPonit
     *
     * @param keyOfStatistic - key for hold Statistic
     * @param isResponse     - A boolean value that indicate whether message flow is in or out
     * @param inTime         - The processing start time
     * @param outTime        - The processing end time
     * @param isFault        - A boolean value that indicate whether falut has occured or not
     */
    public void reportForEndPoint(String keyOfStatistic, boolean isResponse, long inTime,
                                  long outTime, boolean isFault) {
        StatisticsHolder statisticsHolder =
                (StatisticsHolder) endpointStatistics.get(keyOfStatistic);
        if (statisticsHolder == null) {
            statisticsHolder = new StatisticsHolder();
            statisticsHolder.setKey(keyOfStatistic);
            statisticsHolder.setStatisticsCategory(SynapseConstants.ENDPOINT_STATISTICS);
            endpointStatistics.put(keyOfStatistic, statisticsHolder);
        }
        statisticsHolder.update(isResponse, inTime, outTime, isFault);

    }

    /**
     * To report the statistics related to a  ProxyService
     *
     * @param keyOfStatistic - key for hold Statistic
     * @param isResponse     - A boolean value that indicate whether message flow is in or out
     * @param inTime         - The processing start time
     * @param outTime        - The processing end time
     * @param isFault        - A boolean value that indicate whether falut has occured or not
     */
    public void reportForProxyService(String keyOfStatistic, boolean isResponse, long inTime,
                                      long outTime, boolean isFault) {
        StatisticsHolder statisticsHolder =
                (StatisticsHolder) proxyServicesStatistics.get(keyOfStatistic);
        if (statisticsHolder == null) {
            statisticsHolder = new StatisticsHolder();
            statisticsHolder.setKey(keyOfStatistic);
            statisticsHolder.setStatisticsCategory(SynapseConstants.PROXYSERVICE_STATISTICS);
            proxyServicesStatistics.put(keyOfStatistic, statisticsHolder);
        }
        statisticsHolder.update(isResponse, inTime, outTime, isFault);
    }

    /**
     * To report the statistics related to a  Sequence
     *
     * @param keyOfStatistic - key for hold Statistic
     * @param isResponse     - A boolean value that indicate whether message flow is in or out
     * @param inTime         - The processing start time
     * @param outTime        - The processing end time
     * @param isFault        - A boolean value that indicate whether falut has occured or not
     */
    public void reportForSequence(String keyOfStatistic, boolean isResponse, long inTime,
                                  long outTime, boolean isFault) {
        StatisticsHolder statisticsHolder =
                (StatisticsHolder) sequenceStatistics.get(keyOfStatistic);
        if (statisticsHolder == null) {
            statisticsHolder = new StatisticsHolder();
            statisticsHolder.setKey(keyOfStatistic);
            statisticsHolder.setStatisticsCategory(SynapseConstants.SEQUENCE_STATISTICS);
            sequenceStatistics.put(keyOfStatistic, statisticsHolder);
        }
        statisticsHolder.update(isResponse, inTime, outTime, isFault);
    }

    /**
     * To access all sequence statistics
     *
     * @return all sequence statistics
     */
    public Map getSequenceStatistics() {
        return sequenceStatistics;
    }

    /**
     * To access all proxy services statistics
     *
     * @return all proxy services statistics
     */
    public Map getProxyServiceStatistics() {
        return proxyServicesStatistics;
    }

    /**
     * To access all endpoint statistics
     *
     * @return all endpoint statistics
     */
    public Map getEndPointStatistics() {
        return endpointStatistics;
    }

    /**
     * To reset the sequence statistics
     */
    public void resetSequenceStatistics() {
        this.sequenceStatistics.clear();
    }

    /**
     * To reset the proxy service statistics
     */
    public void resetProxyServiceStatistics() {
        this.proxyServicesStatistics.clear();
    }

    /**
     * To reset the endpoint statistics
     */
    public void resetEndPointStatistics() {
        this.endpointStatistics.clear();
    }
}
