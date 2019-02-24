/*
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.virtualvehicle;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.assistedinject.Assisted;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.opentcs.common.LoopbackAdapterConstants;
import org.opentcs.components.kernel.services.InternalVehicleService;
import org.opentcs.data.ObjectPropConstants;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.Route;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.LoadHandlingDevice;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.SimVehicleCommAdapter;
import org.opentcs.drivers.vehicle.VehicleCommAdapterPanel;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.drivers.vehicle.messages.SetSpeedMultiplier;
import org.opentcs.util.CyclicTask;
import org.opentcs.util.ExplainedBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zisha
 */
public class AGVCommAdapter
    extends BasicVehicleCommAdapter
    implements SimVehicleCommAdapter {

  /**
   * The name of the load handling device set by this adapter.
   */
  public static final String LHD_NAME = "default";
  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(AGVCommAdapter.class);
  /**
   * The time by which to advance the velocity controller per step (in ms).
   */
  private static final int ADVANCE_TIME = 500;
  /**
   * This instance's configuration.
   *
   */
  private final AITVVehicleConfiguration configuration;
  /**
   * The adapter components factory.
   */
  private final AGVAdapterComponentFactory componentsFactory;

  /**
   * The task simulating the virtual vehicle's behaviour.
   */
  private CyclicTask vehicleSimulationTask;
  /**
   * The boolean flag to check if execution of the next command is allowed.
   */
  private boolean singleStepExecutionAllowed;
  /**
   * The vehicle to this comm adapter instance.
   */
  private final Vehicle vehicle;
  /**
   * Whether the loopback adapter is initialized or not.
   */
  private boolean initialized;

  // wake up port
  public static final int PORT = 9;

  Socket clientSocket;

  private static boolean completePathsent = false;

  private AITVTCPCommunication serialCommunication;

  // check if vehicle is connected or not
  private boolean isveheicleconnected;


  /**
   * The kernel's vehicle service.
   */
  private final InternalVehicleService vehicleService;

  /**
   *
   *
   * /**
   * Creates a new instance.
   *
   * @param componentsFactory The factory providing additional components for this adapter.
   * @param configuration This class's configuration.
   * @param vehicle The vehicle this adapter is associated with.
   */
  @Inject
  public AGVCommAdapter(AGVAdapterComponentFactory componentsFactory,
                        AITVVehicleConfiguration configuration,
                        @Nonnull InternalVehicleService vehicleService,
                        @Assisted Vehicle vehicle) {
    super(new LoopbackVehicleModel(vehicle),
          configuration.commandQueueCapacity(),
          1,
          configuration.rechargeOperation());
    this.vehicle = requireNonNull(vehicle, "vehicle");
    this.vehicleService = requireNonNull(vehicleService, "vehicleService");
    this.configuration = requireNonNull(configuration, "configuration");
    this.componentsFactory = requireNonNull(componentsFactory, "componentsFactory");

  }

  @Override
  public void initialize() {
    if (isInitialized()) {
      return;
    }
    super.initialize();

    String initialPos
        = vehicle.getProperties().get(LoopbackAdapterConstants.PROPKEY_INITIAL_POSITION);
    if (initialPos == null) {
      @SuppressWarnings("deprecation")
      String deprecatedInitialPos
          = vehicle.getProperties().get(ObjectPropConstants.VEHICLE_INITIAL_POSITION);
      initialPos = deprecatedInitialPos;
    }
    if (initialPos != null) {
      initVehiclePosition(initialPos);
    }

    getProcessModel().setVehicleIpAddress("127.0.0.1", this);
    getProcessModel().setVehicleState(Vehicle.State.IDLE);

    try {
      final UDPpositionalserver getpositiondata = new UDPpositionalserver();
      if (!getpositiondata.isAlive()) {

        getpositiondata.start();

      }
    }
    catch (SocketException ex) {
      java.util.logging.Logger.getLogger(AGVCommAdapter.class.getName()).log(Level.SEVERE, null, ex);
    }

    initialized = true;
  }

  @Override
  public boolean isInitialized() {

    return initialized;
  }

  /*
  
  This Method is Used For Starting Up the Computer Inside the AITV when it is Connected.
  This Method is also Called When The Vehicle Is Idle or in Parking Position..
  This Method is also Called When Vehicle is Idle for n duration of time.
  
  
   */
  private void wakeMeUpByLanPacket() {

    //addition wake up
    LOG.debug("Wake Up Method Executed....");

    if (vehicle.hasState(Vehicle.State.CHARGING)) {

      String ipStr = "192.168.6.55";
      String macStr = "20-46-A1-03-59-EF";

      try {
        byte[] macBytes = getMacBytes(macStr);
        byte[] bytes = new byte[6 + 16 * macBytes.length];
        for (int i = 0; i < 6; i++) {
          bytes[i] = (byte) 0xff;
        }
        for (int i = 6; i < bytes.length; i += macBytes.length) {
          System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
        }

        InetAddress address = InetAddress.getByName(ipStr);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
        socket.close();

        System.out.println("Wake-on-LAN packet sent.");
      }
      catch (Exception e) {
        System.out.println("Failed to send Wake-on-LAN packet: + e");
        System.exit(1);
      }

    }

  }
  
  /*
  
      This Method is a aprt of wakeMeUpByLanPacket()............
  
  
   */
  

  private static byte[] getMacBytes(String macStr)
      throws IllegalArgumentException {
    byte[] bytes = new byte[6];
    String[] hex = macStr.split("(\\:|\\-)");
    if (hex.length != 6) {
      throw new IllegalArgumentException("Invalid MAC address.");
    }
    try {
      for (int i = 0; i < 6; i++) {
        bytes[i] = (byte) Integer.parseInt(hex[i], 16);
      }
    }
    catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid hex digit in MAC address.");
    }
    return bytes;
  }

  @Override
  public void terminate() {
    if (!isInitialized()) {
      LOG.debug("Not initialized.");
      return;
    }

    super.terminate();
    initialized = false;
  }

  @Override
  public synchronized void enable() {
    if (isEnabled()) {

      LOG.debug("vehicle communications ennabled");

    }

    getProcessModel().getVelocityController().addVelocityListener(getProcessModel());
    getProcessModel().setVehicleState(Vehicle.State.IDLE);

   
    // Create task for vehicle simulation.
    vehicleSimulationTask = new VehicleSimulationTask();
    Thread simThread = new Thread(vehicleSimulationTask, getName() + "-simulationTask");
    simThread.start();

    LOG.debug("vehicle communications ennabled");

    super.enable();

  }

  @Override
  public synchronized void disable() {
    if (!isEnabled()) {

      LOG.debug("vehicle communications dissabled");

      getProcessModel().getVelocityController().removeVelocityListener(getProcessModel());

    }

    getProcessModel().setVehicleState(Vehicle.State.UNAVAILABLE);

    super.disable();
  }

  @Override
  public LoopbackVehicleModel getProcessModel() {
    return (LoopbackVehicleModel) super.getProcessModel();
  }

  @Override
  public synchronized void sendCommand(MovementCommand cmd) {
    requireNonNull(cmd, "cmd");

    String CompleteRoute = "This is Complete Path:-";

    for (MovementCommand m : getCommandQueue()) {

      System.out.println(m.getStep());

      Route.Step p = m.getStep();

      if (m.getStep().toString() != null) {

        CompleteRoute += "  --> " + m.getStep().toString() + p.getSourcePoint().getPosition();
        System.out.println(CompleteRoute);
      }

    }
    try {
      AITVTCPCommunication.sendCommand(this, CompleteRoute);
      completePathsent = true;

    }
    catch (IOException ex) {
      java.util.logging.Logger.getLogger(AGVCommAdapter.class.getName()).log(Level.SEVERE, null, ex);
    }

    try {
      AITVTCPCommunication.sendCommand(this, cmd.toString());
    }
    catch (IOException ex) {
      java.util.logging.Logger.getLogger(AGVCommAdapter.class.getName()).log(Level.SEVERE, null, ex);
    }

    // Reset the execution flag for single-step mode.
    singleStepExecutionAllowed = false;
    // Don't do anything else - the command will be put into the sentQueue
    // automatically, where it will be picked up by the simulation task.
  }

  @Override
  public void processMessage(Object message) {
    // Process LimitSpeeed message which might pause the vehicle.

    System.out.println("message" + message);

    if (message instanceof SetSpeedMultiplier) {
      SetSpeedMultiplier lsMessage = (SetSpeedMultiplier) message;
      int multiplier = lsMessage.getMultiplier();
      getProcessModel().setVehiclePaused(multiplier == 0);
    }

  }

  @Override
  public synchronized void initVehiclePosition(String newPos) {
    getProcessModel().setVehiclePosition(newPos);
  }

  @Override
  public synchronized ExplainedBoolean canProcess(List<String> operations) {
    requireNonNull(operations, "operations");

    final boolean canProcess = isEnabled();
    final String reason = canProcess ? "" : "adapter not enabled";
    return new ExplainedBoolean(canProcess, reason);
  }

  @Override
  public void execute(AdapterCommand command) {

    System.out.println("I am Executed");

    super.execute(command);

//To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setSimTimeFactor(double factor)
      throws IllegalArgumentException {
    SimVehicleCommAdapter.super.setSimTimeFactor(factor); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  protected synchronized boolean canSendNextCommand() {
    return super.canSendNextCommand()
        && (!getProcessModel().isSingleStepModeEnabled() || singleStepExecutionAllowed);
  }

  @Override
  protected synchronized void connectVehicle() {

    
    /*
     Wait Till The Time The Vehicle Starts Up.
    */
    wakeMeUpByLanPacket();
    
    
     
    /*
    
       This Activites the AITV To Be Used For Utilization..........    
    */
    
    vehicleService.updateVehicleIntegrationLevel(vehicle.getReference(), Vehicle.IntegrationLevel.TO_BE_UTILIZED);

    LOG.debug("This is my ip address now " + vehicle.getvehicleipaddress());

    try {

      /// this is the code for UDP positional 
      AITVTCPCommunicationFactory serialCommunicationFactory;
      serialCommunicationFactory = new AITVTCPCommunicationFactory(this, vehicle.getName(), vehicle.getvehicleipaddress());
      this.serialCommunication = serialCommunicationFactory.getSerialCommunication();

      isveheicleconnected = true;

      LOG.debug("communication established");
    }
    catch (IOException e) {
      System.out.println("Error!" + isveheicleconnected);
      isveheicleconnected = false;
    }

  }

  @Override
  protected synchronized void disconnectVehicle() {

    //  AITVTCPCommunication.disconnectAdapter(this);
    try {
      AITVTCPCommunication.disconnectAdapter(this, vehicle.getName());
    }
    catch (IOException ex) {
      java.util.logging.Logger.getLogger(AGVCommAdapter.class.getName()).log(Level.SEVERE, null, ex);
    }

  }

  @Override
  protected synchronized boolean isVehicleConnected() {

    LOG.debug("I am connected" + isveheicleconnected);
    return isveheicleconnected;
  }

  @Override
  protected VehicleProcessModelTO createCustomTransferableProcessModel() {
    return new LoopbackVehicleModelTO()
        .setLoadOperation(getProcessModel().getLoadOperation())
        .setMaxAcceleration(getProcessModel().getMaxAcceleration())
        .setMaxDeceleration(getProcessModel().getMaxDecceleration())
        .setMaxFwdVelocity(getProcessModel().getMaxFwdVelocity())
        .setMaxRevVelocity(getProcessModel().getMaxRevVelocity())
        .setOperatingTime(getProcessModel().getOperatingTime())
        .setSingleStepModeEnabled(getProcessModel().isSingleStepModeEnabled())
        .setUnloadOperation(getProcessModel().getUnloadOperation())
        .setVehiclePaused(getProcessModel().isVehiclePaused())
        .setVehicleIpAddress(getProcessModel().getvehicleipaddress());

  }

  /**
   * Triggers a step in single step mode.
   */
  public synchronized void trigger() {
    singleStepExecutionAllowed = true;
  }

  @Override
  protected List<VehicleCommAdapterPanel> createAdapterPanels() {
    return Arrays.asList(componentsFactory.createPanel(this));

  }

  /**
   * A task simulating a vehicle's behaviour.
   */
  private class VehicleSimulationTask
      extends CyclicTask {

    /**
     * The time that has passed for the velocity controller whenever
     * <em>advanceTime</em> has passed for real.
     */
    private int simAdvanceTime;

    /**
     * Creates a new VehicleSimluationTask.
     */
    private VehicleSimulationTask() {
      super(0);
    }

    @Override
    protected void runActualTask() {
      final MovementCommand curCommand;

      synchronized (AGVCommAdapter.this) {
        curCommand = getSentQueue().peek();
      }
      simAdvanceTime = (int) (ADVANCE_TIME * configuration.simulationTimeFactor());
      if (curCommand == null) {
        Uninterruptibles.sleepUninterruptibly(ADVANCE_TIME, TimeUnit.MILLISECONDS);
        getProcessModel().getVelocityController().advanceTime(simAdvanceTime);
      }
      else {
        // If we were told to move somewhere, simulate the journey.
        LOG.debug("Processing MovementCommand...");
        final Route.Step curStep = curCommand.getStep();

        System.out.println("Called Here" + getCommandQueue().size());

        try {
          // Simulate the movement.

          simulateMovement(curStep);

          System.out.println("Confirmation of command executed" + " : " + vehicle.getName());

        }
        catch (IOException ex) {
          java.util.logging.Logger.getLogger(AGVCommAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Simulate processing of an operation.
        if (!curCommand.isWithoutOperation()) {
          simulateOperation(curCommand.getOperation());
        }
        LOG.debug("Processed MovementCommand.");
        if (!isTerminated()) {
          // Set the vehicle's state back to IDLE, but only if there aren't 
          // any more movements to be processed.
          if (getSentQueue().size() <= 1 && getCommandQueue().isEmpty()) {
            getProcessModel().setVehicleState(Vehicle.State.IDLE);
          }
          // Update GUI.
          synchronized (AGVCommAdapter.this) {

            MovementCommand sentCmd = getSentQueue().poll();
            // If the command queue was cleared in the meantime, the kernel
            // might be surprised to hear we executed a command we shouldn't
            // have, so we only peek() at the beginning of this method and
            // poll() here. If sentCmd is null, the queue was probably cleared
            // and we shouldn't report anything back.
            if (sentCmd != null && sentCmd.equals(curCommand)) {
              // Let the vehicle manager know we've finished this command.

              System.out.println("Command Execute Code Is Called....... ");
              getProcessModel().commandExecuted(curCommand);
              AGVCommAdapter.this.notify();

              System.out.println("Command Execute End ");
            }
          }
        }
      }
    }

    /**
     * Simulates the vehicle's movement. If the method parameter is null,
     * then the vehicle's state is failure and some false movement
     * must be simulated. In the other case normal step
     * movement will be simulated.
     *
     * @param step A step
     * @throws InterruptedException If an exception occured while sumulating
     */
    private void simulateMovement(Route.Step step)
        throws IOException {
      if (step.getPath() == null) {
        return;
      }

      System.out.println();

      Vehicle.Orientation orientation = step.getVehicleOrientation();
      long pathLength = step.getPath().getLength();
      int maxVelocity;
      switch (orientation) {
        case BACKWARD:
          maxVelocity = step.getPath().getMaxReverseVelocity();
          break;
        default:
          maxVelocity = step.getPath().getMaxVelocity();
          break;
      }
      String pointName = step.getDestinationPoint().getName();

      getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
      getProcessModel().getVelocityController().addWayEntry(new VelocityController.WayEntry(pathLength,
                                                                                            maxVelocity,
                                                                                            pointName,
                                                                                            orientation));
      // Advance the velocity controller by small steps until the
      // controller has processed all way entries.
      while (getProcessModel().getVelocityController().hasWayEntries() && !isTerminated()) {
        VelocityController.WayEntry wayEntry = getProcessModel().getVelocityController().getCurrentWayEntry();

        System.out.println("Wayentries simulate movement code 1 ");
        Uninterruptibles.sleepUninterruptibly(ADVANCE_TIME, TimeUnit.MILLISECONDS);
        getProcessModel().getVelocityController().advanceTime(simAdvanceTime);
        VelocityController.WayEntry nextWayEntry = getProcessModel().getVelocityController().getCurrentWayEntry();
        if (wayEntry != nextWayEntry) {

          // Let the vehicle manager know that the vehicle has reached
          // the way entry's destination point.
          System.out.println("Wayentries simulate movement code " + wayEntry.getDestPointName());

          AITVTCPCommunication.getPosition(AGVCommAdapter.this, vehicle.getName());

          getProcessModel().setVehiclePosition(wayEntry.getDestPointName());

        }
      }
    }

    /**
     * Simulates an operation.
     *
     * @param operation A operation
     * @throws InterruptedException If an exception occured while simulating
     */
    private void simulateOperation(String operation) {
      requireNonNull(operation, "operation");

      if (isTerminated()) {
        return;
      }

      LOG.debug("Operating...");
      final int operatingTime = getProcessModel().getOperatingTime();
      getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
      for (int timePassed = 0; timePassed < operatingTime && !isTerminated();
           timePassed += simAdvanceTime) {
        Uninterruptibles.sleepUninterruptibly(ADVANCE_TIME, TimeUnit.MILLISECONDS);
        getProcessModel().getVelocityController().advanceTime(simAdvanceTime);
      }
      if (operation.equals(getProcessModel().getLoadOperation())) {
        // Update load handling devices as defined by this operation
        getProcessModel().setVehicleLoadHandlingDevices(
            Arrays.asList(new LoadHandlingDevice(LHD_NAME, true)));
      }
      else if (operation.equals(getProcessModel().getUnloadOperation())) {
        getProcessModel().setVehicleLoadHandlingDevices(
            Arrays.asList(new LoadHandlingDevice(LHD_NAME, false)));
      }
    }
  }

}
