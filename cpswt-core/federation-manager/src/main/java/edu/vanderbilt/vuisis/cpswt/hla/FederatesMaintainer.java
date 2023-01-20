/*
 * Certain portions of this software are Copyright (C) 2006-present
 * Vanderbilt University, Institute for Software Integrated Systems.
 *
 * Certain portions of this software are contributed as a public service by
 * The National Institute of Standards and Technology (NIST) and are not
 * subject to U.S. Copyright.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above Vanderbilt University copyright notice, NIST contribution
 * notice and this permission and disclaimer notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. THE AUTHORS OR COPYRIGHT HOLDERS SHALL NOT HAVE
 * ANY OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 */

package edu.vanderbilt.vuisis.cpswt.hla;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.vanderbilt.vuisis.cpswt.config.ExperimentConfig;
import edu.vanderbilt.vuisis.cpswt.config.FederateJoinInfo;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FederatesMaintainer
 */
public class FederatesMaintainer {
    private enum CounterDirection {
        Increment(1),
        Decrement(-1);
        private int value;
        CounterDirection(int value) {
            this.value = value;
        }
    }

    private static final Logger logger = LogManager.getLogger(FederatesMaintainer.class);

    private Map<String, FederateJoinInfo> expectedFederatesByType;
    private Map<String, FederateJoinInfo> lateJoinerFederatesByType;

    private final List<FederateInfo> onlineFederates;
    private final List<FederateInfo> resignedFederates;

    private ExperimentConfig originalExperimentConfig;

    public FederatesMaintainer() {
        this.onlineFederates = new ArrayList<>();
        this.resignedFederates = new ArrayList<>();
    }

    public void federateJoined(FederateInfo federateInfo) {
        federateInfo.setJoinTime(DateTime.now());
        this.onlineFederates.add(federateInfo);

        this.maintainExpectedFederateCount(federateInfo, CounterDirection.Decrement);
        this.maintainLateJoinerFederateCount(federateInfo, CounterDirection.Decrement);

        this.logCurrentStatus();
    }

    public void federateResigned(FederateInfo federateInfo, boolean hasTimedOut) {
        this.onlineFederates.remove(federateInfo);

        federateInfo.setResignTime(DateTime.now());
        federateInfo.setTimedOutResign(hasTimedOut);
        this.resignedFederates.add(federateInfo);

        this.maintainLateJoinerFederateCount(federateInfo, CounterDirection.Increment);

        this.logCurrentStatus();
    }

    public void federateResigned(FederateInfo federateInfo) {
        this.federateResigned(federateInfo, false);
    }

    private void maintainExpectedFederateCount(FederateInfo federateInfo, CounterDirection counterDirection) {
        if(!federateInfo.isLateJoiner()) {
            FederateJoinInfo federateJoinInfo = this.expectedFederatesByType.get(federateInfo.getFederateType());

            federateJoinInfo.count += counterDirection.value;
            if (federateJoinInfo.count < 0) {
                logger.warn("Expected federate of type {} joined and exceeded allowed number of instances!", federateInfo.getFederateType());
            }
        }
    }

    private void maintainLateJoinerFederateCount(FederateInfo federateInfo, CounterDirection counterDirection) {
        if (federateInfo.isLateJoiner()) {
            FederateJoinInfo federateJoinInfo = this.lateJoinerFederatesByType.get(federateInfo.getFederateType());

            federateJoinInfo.count += counterDirection.value;
            if (federateJoinInfo.count < 0) {
                logger.warn("Late joiner federate of type {} joined and exceeded allowed number of instances!", federateInfo.getFederateType());
            }
        }
    }

    public FederateInfo getFederateInfo(String federateId) {
        FederateInfo federateInfo = this.onlineFederates
                .stream()
                .filter(f -> f.getFederateId().equalsIgnoreCase(federateId))
                .findFirst()
                .orElse(null);

        if (federateInfo == null) {
            federateInfo = this.resignedFederates
                    .stream()
                    .filter(f -> f.getFederateId().equalsIgnoreCase(federateId))
                    .findFirst()
                    .get();
        }

        return federateInfo;
    }

    int expectedFederatesLeftToJoinCount() {
        return this.expectedFederatesByType.values()
                .stream()
                .mapToInt(o -> o.count)
                .sum();
    }

    void updateFederateJoinInfo(ExperimentConfig experimentConfig) {
        this.expectedFederatesByType = experimentConfig.expectedFederates
                .stream()
                .collect(Collectors.toMap(f -> f.federateType, f -> f));
        this.lateJoinerFederatesByType = experimentConfig.lateJoinerFederates
                .stream()
                .collect(Collectors.toMap(f -> f.federateType, f -> f));

        this.originalExperimentConfig = experimentConfig;
    }

    List<FederateInfo> getOnlineExpectedFederates() {
        return this.onlineFederates
                .stream()
                .filter(fi -> !fi.isLateJoiner())
                .collect(Collectors.toList());
    }

    List<FederateInfo> getOnlineLateJoinerFederates() {
        return this.onlineFederates
                .stream()
                .filter(FederateInfo::isLateJoiner)
                .collect(Collectors.toList());
    }

    List<FederateInfo> getOnlineFederates() {
        return this.onlineFederates;
    }

    List<FederateInfo> getAllMaintainedFederates() {
        return Stream.concat(this.onlineFederates.stream(), this.resignedFederates.stream()).collect(Collectors.toList());
    }

    // TEMP
    void logCurrentStatus() {
        logger.trace("expectedFederateJoinInfo ::");
        for (FederateJoinInfo fji : this.expectedFederatesByType.values()) {
            logger.trace("\t[{}] [{}]", fji.count, fji.federateType);
        }
        if (this.expectedFederatesByType.size() == 0) {
            logger.trace("\t NONE");
        }

        logger.trace("lateJoinerFederateJoinInfo ::");
        for (FederateJoinInfo fji : this.lateJoinerFederatesByType.values()) {
            logger.trace("\t[{}] {}", fji.count, fji.federateType);
        }
        if (this.lateJoinerFederatesByType.size() == 0) {
            logger.trace("\t NONE");
        }

        logger.trace("onlineFederates ::");
        for (FederateInfo fi : this.onlineFederates) {
            logger.trace("\t[{}] :: [JOINED @ {}] :: {}", fi.isLateJoiner() ? "LATEJOINER" : " EXPECTED ", fi.joinTime, fi.getFederateId());
        }
        if (this.onlineFederates.size() == 0) {
            logger.trace("\t NONE");
        }

        logger.trace("resignedFederates ::");
        for (FederateInfo fi : this.resignedFederates) {
            logger.trace("\t[{}] :: [RESIGNED @ {}] :: {} :: {}", fi.isLateJoiner() ? "LATEJOINER" : " EXPECTED ", fi.resignTime, fi.isTimedOutResign() ? "RESIGNED" : "", fi.getFederateId());
        }
        if (this.resignedFederates.size() == 0) {
            logger.trace("\t NONE");
        }
    }
}
