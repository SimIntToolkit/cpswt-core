package org.cpswt.hla;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cpswt.config.ExperimentConfig;
import org.cpswt.config.FederateJoinInfo;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FederatesMaintainer
 */
public class FederatesMaintainer {
    private static final Logger logger = LogManager.getLogger(FederatesMaintainer.class);

    private List<FederateJoinInfo> expectedFederateJoinInfo;
    private List<FederateJoinInfo> lateJoinerFederateJoinInfo;
    private final List<FederateInfo> onlineFederates;
    private final List<FederateInfo> resignedFederates;

    private ExperimentConfig originalExperimentConfig;

    public FederatesMaintainer() {
        this.expectedFederateJoinInfo = new ArrayList<>();
        this.lateJoinerFederateJoinInfo = new ArrayList<>();
        this.onlineFederates = new ArrayList<>();
        this.resignedFederates = new ArrayList<>();
    }

    public void federateJoined(FederateInfo federateInfo) {
        federateInfo.setJoinTime(DateTime.now());
        this.onlineFederates.add(federateInfo);

        if(federateInfo.isLateJoiner()) {
            FederateJoinInfo federateJoinInfo = this.lateJoinerFederateJoinInfo
                    .stream()
                    .filter(fji -> fji.federateType.equalsIgnoreCase(federateInfo.getFederateType()))
                    .findFirst()
                    .get();

            if(federateJoinInfo != null && federateJoinInfo.count > 0) {
                federateJoinInfo.count--;
            }
            else {
                logger.warn("Late joiner federate of type {} joined and exceeded allowed number of instances!", federateInfo.getFederateType());
            }
        }
        else {
            FederateJoinInfo federateJoinInfo = this.expectedFederateJoinInfo
                    .stream()
                    .filter(fji -> fji.federateType.equalsIgnoreCase(federateInfo.getFederateType()))
                    .findFirst()
                    .get();

            if(federateJoinInfo != null && federateJoinInfo.count > 0) {
                federateJoinInfo.count--;
            }
            else {
                logger.warn("Expected federate of type {} joined and exceeded allowed number of instances!", federateInfo.getFederateType());
            }
        }
    }

    public void federateResigned(FederateInfo federateInfo) {
        this.onlineFederates.remove(federateInfo);

        federateInfo.setResignTime(DateTime.now());
        this.resignedFederates.add(federateInfo);
    }

    public FederateInfo getFederateInfo(String federateId) {
        FederateInfo federateInfo = this.onlineFederates
                .stream()
                .filter(f -> f.getFederateId().equalsIgnoreCase(federateId))
                .findFirst()
                .get();

        if(federateInfo == null) {
            federateInfo = this.resignedFederates
                    .stream()
                    .filter(f -> f.getFederateId().equalsIgnoreCase(federateId))
                    .findFirst()
                    .get();
        }

        return federateInfo;
    }

    public int expectedFederatesLeftToJoinCount() {
        int cnt = this.expectedFederateJoinInfo
                .stream()
                .mapToInt(o -> o.count)
                .sum();
        return cnt;
    }

    public void updateFederateJoinInfo(ExperimentConfig experimentConfig) {
        this.expectedFederateJoinInfo.addAll(experimentConfig.expectedFederates);
        this.lateJoinerFederateJoinInfo.addAll(experimentConfig.lateJoinerFederates);

        this.originalExperimentConfig = experimentConfig;
    }

    public List<FederateInfo> getOnlineExpectedFederates() {
        return this.onlineFederates
                .stream()
                .filter(fi -> !fi.isLateJoiner())
                .collect(Collectors.toList());
    }

    public List<FederateInfo> getOnlineLateJoinerFederates() {
        return this.onlineFederates
                .stream()
                .filter(fi -> fi.isLateJoiner())
                .collect(Collectors.toList());
    }

    // TEMP
    public void logCurrentStatus() {
        logger.trace("expectedFederateJoinInfo ::");
        for(FederateJoinInfo fji : this.expectedFederateJoinInfo) {
            logger.trace("\t[{}] [{}]", fji.count, fji.federateType);
        }

        logger.trace("lateJoinerFederateJoinInfo ::");
        for(FederateJoinInfo fji : this.lateJoinerFederateJoinInfo) {
            logger.trace("\t[{}] {}", fji.count, fji.federateType);
        }

        logger.trace("onlineFederates ::");
        for(FederateInfo fi : this.onlineFederates) {
            logger.trace("\t[{}] :: [JOINED @ {}] :: {}", fi.isLateJoiner() ? "LATEJOINER" : " EXPECTED ", fi.joinTime, fi.getFederateId());
        }

        logger.trace("resignedFederates ::");
        for(FederateInfo fi : this.resignedFederates) {
            logger.trace("\t[{}] :: [RESIGNED @ {}] :: {}", fi.isLateJoiner() ? "LATEJOINER" : " EXPECTED ", fi.resignTime, fi.getFederateId());
        }
    }
}
