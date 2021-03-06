/**
 * 
 */
package rinde.sim.pdptw.fabrirecht;

import java.util.Collection;
import java.util.Set;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import rinde.sim.core.graph.Point;
import rinde.sim.core.model.pdp.DefaultPDPModel;
import rinde.sim.core.model.pdp.PDPModel;
import rinde.sim.core.model.pdp.twpolicy.TardyAllowedPolicy;
import rinde.sim.core.model.road.PlaneRoadModel;
import rinde.sim.core.model.road.RoadModel;
import rinde.sim.pdptw.common.DynamicPDPTWProblem.StopCondition;
import rinde.sim.pdptw.common.DynamicPDPTWScenario;
import rinde.sim.pdptw.common.VehicleDTO;
import rinde.sim.scenario.TimedEvent;
import rinde.sim.util.TimeWindow;

/**
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public class FabriRechtScenario extends DynamicPDPTWScenario {
  private static final long serialVersionUID = 8654500529284785728L;
  public final Point min;
  public final Point max;
  public final TimeWindow timeWindow;
  public final VehicleDTO defaultVehicle;

  // empty scenario
  public FabriRechtScenario(Point pMin, Point pMax, TimeWindow pTimeWindow,
      VehicleDTO pDefaultVehicle) {
    super();
    min = pMin;
    max = pMax;
    timeWindow = pTimeWindow;
    defaultVehicle = pDefaultVehicle;
  }

  /**
   * @param pEvents
   * @param pSupportedTypes
   */
  public FabriRechtScenario(Collection<? extends TimedEvent> pEvents,
      Set<Enum<?>> pSupportedTypes, Point pMin, Point pMax,
      TimeWindow pTimeWindow, VehicleDTO pDefaultVehicle) {
    super(pEvents, pSupportedTypes);
    min = pMin;
    max = pMax;
    timeWindow = pTimeWindow;
    defaultVehicle = pDefaultVehicle;
  }

  @Override
  public TimeWindow getTimeWindow() {
    return timeWindow;
  }

  @Override
  public long getTickSize() {
    return 1L;
  }

  @Override
  public StopCondition getStopCondition() {
    return StopCondition.TIME_OUT_EVENT;
  }

  @Override
  public RoadModel createRoadModel() {
    return new PlaneRoadModel(min, max, getDistanceUnit(),
        Measure.valueOf(100d, getSpeedUnit()));
  }

  @Override
  public PDPModel createPDPModel() {
    return new DefaultPDPModel(new TardyAllowedPolicy());
  }

  @Override
  public Unit<Duration> getTimeUnit() {
    return NonSI.MINUTE;
  }

  @Override
  public Unit<Velocity> getSpeedUnit() {
    return SI.KILOMETRE.divide(NonSI.MINUTE).asType(Velocity.class);
  }

  @Override
  public Unit<Length> getDistanceUnit() {
    return SI.KILOMETER;
  }

  @Override
  public ProblemClass getProblemClass() {
    return FabriRechtProblemClass.SINGLETON;
  }

  @Override
  public String getProblemInstanceId() {
    return "1";
  }

  public enum FabriRechtProblemClass implements ProblemClass {
    SINGLETON;

    @Override
    public String getId() {
      return "fabrirecht";
    }
  }
}
