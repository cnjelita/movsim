/*
 * Copyright by Ralph Germ (http://www.ralphgerm.de)
 */
package org.movsim.simulator.roadnetwork;

import java.util.ArrayList;
import java.util.List;

import org.movsim.input.model.simulation.TrafficLightData;
import org.movsim.input.model.simulation.TrafficLightsInput;
import org.movsim.output.fileoutput.FileTrafficLightRecorder;
import org.movsim.simulator.vehicles.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TrafficLights.
 */
public class TrafficLights {

    /** The Constant logger. */
    final static Logger logger = LoggerFactory.getLogger(TrafficLights.class);

    /** The n dt. */
    private final int nDt;

    /** The traffic lights. */
    private List<TrafficLight> trafficLights;

    /** The traffic light recorder. */
    private FileTrafficLightRecorder fileTrafficLightRecorder = null;

    /**
     * Instantiates a new traffic lights impl.
     * 
     * @param projectName
     *            the project name
     * @param trafficLightsInput
     *            the traffic lights input
     */
    
    
    public TrafficLights(String projectName, TrafficLightsInput trafficLightsInput) {

        initTrafficLights(trafficLightsInput);

        nDt = trafficLightsInput.getnDtSample();

        if (projectName!=null && trafficLightsInput.isWithLogging()) {
            fileTrafficLightRecorder = new FileTrafficLightRecorder(projectName, nDt, trafficLights);
        }

    }

    /**
     * Inits the traffic lights.
     * 
     * @param trafficLightsInput
     *            the traffic lights input
     */
    private void initTrafficLights(TrafficLightsInput trafficLightsInput) {
        trafficLights = new ArrayList<TrafficLight>();
        final List<TrafficLightData> trafficLightData = trafficLightsInput.getTrafficLightData();
        for (final TrafficLightData tlData : trafficLightData) {
            trafficLights.add(new TrafficLight(tlData));
        }
    }

    /**
     * Update.
     *
     * @param iterationCount the itime
     * @param time the time
     * @param roadSegment
     */
    public void update(long iterationCount, double time, RoadSegment roadSegment) {

        if (!trafficLights.isEmpty()) {
            // first update traffic light status
            for (final TrafficLight trafficLight : trafficLights) {
                trafficLight.update(time);
            }
            // second update vehicle status approaching traffic lights
        	final int laneCount = roadSegment.laneCount();
            for (int lane = 0; lane < laneCount; ++lane) {
            	final LaneSegment laneSegment = roadSegment.laneSegment(lane);
                for (final Vehicle vehicle : laneSegment) {
                    for (final TrafficLight trafficLight : trafficLights) {
                        vehicle.updateTrafficLight(time, trafficLight);
                    }
                }
            }
        }

        if (fileTrafficLightRecorder != null) {
            fileTrafficLightRecorder.update(iterationCount, time, trafficLights);
        }
    }

    /**
     * Gets the traffic lights.
     * 
     * @return the traffic lights
     */
    public List<TrafficLight> getTrafficLights() {
        return trafficLights;
    }
}