package org.fog.test.perfeval;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.entities.Actuator;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.Sensor;
import org.fog.entities.Tuple;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementEdgewards;
import org.fog.placement.ModulePlacementMapping;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.TimeKeeper;
import org.fog.utils.distribution.DeterministicDistribution;

public class Softwareupdate {
	static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
	static List<Sensor> sensors = new ArrayList<Sensor>();
	static List<Actuator> actuators = new ArrayList<Actuator>();
	
	static boolean CLOUD = false;
	
	static int numOfDepts = 4;
	static int numOfMobilesPerDept = 5;
	static double EEG_TRANSMISSION_TIME = 5;
	
	public static void main(String[] args) {

		Log.printLine("Starting Software update...");

		//Collect workshop machine data
		double tranTime=1;
		double BW=200;
				
		int[][][] arr1= new int[][][]{{{2,7,5}},{{1,5,7}},{{1,6,10}},{{1,2,12}},{{2,2,15}}};
		int[][][] arr2= new int[][][]{{{0,4,7}},{{2,3,6}},{{0,8,12}},{{2,4,17}},{{0,4,17}}};
		int[][][] arr3= new int[][][]{{{2,3,9}},{{0,6,9}},{{1,5,11}},{{1,6,19}},{{2,6,6}}};
		int[][][] arr4= new int[][][]{{{1,2,3}},{{1,2,17}},{{2,2,9}},{{0,7,10}},{{0,8,14}}};
		SUA();
		AUA();
		HUA();
		try {
			Log.disable();
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

			CloudSim.init(num_user, calendar, trace_flag);

			String appId = "Software_update"; // identifier of the application
			
			FogBroker broker = new FogBroker("broker");
			
			Application application = createApplication(appId, broker.getId());
			application.setUserId(broker.getId());
			
			createFogDevices(broker.getId(), appId);
			
			ModuleMapping moduleMapping = ModuleMapping.createModuleMapping(); // initializing a module mapping
			
			if(CLOUD){
				// if the mode of deployment is cloud-based
			
				moduleMapping.addModuleToDevice("connector", "cloud"); // fixing all instances of the Connector module to the Cloud
				moduleMapping.addModuleToDevice("concentration_calculator", "cloud"); // fixing all instances of the Concentration Calculator module to the Cloud
				for(FogDevice device : fogDevices){
					if(device.getName().startsWith("m")){
						//moduleMapping.addModuleToDevice("client", device.getName(), 1);  // fixing all instances of the Client module to the Smartphones
						moduleMapping.addModuleToDevice("client", device.getName());  // fixing all instances of the Client module to the Smartphones
					}
				}
			}else{
				
				
				moduleMapping.addModuleToDevice("connector", "cloud"); // fixing all instances of the Connector module to the Cloud
				// rest of the modules will be placed by the Edge-ward placement policy
			}
			
			
			Controller controller = new Controller("master-controller", fogDevices, sensors, 
					actuators);
			
			controller.submitApplication(application, 0, 
					(CLOUD)?(new ModulePlacementMapping(fogDevices, application, moduleMapping))
							:(new ModulePlacementEdgewards(fogDevices, sensors, actuators, application, moduleMapping)));

			TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());

			CloudSim.startSimulation();

			CloudSim.stopSimulation();

			Log.printLine("VRGame finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
		
	}
	
	public static void SUA() {
		double tranTime=1;
		double BW=200;
		
		int[][][] arr1= new int[][][]{{{2,7,5}},{{1,5,7}},{{1,6,10}},{{1,2,12}},{{2,2,15}}};
		int[][][] arr2= new int[][][]{{{0,4,7}},{{2,3,6}},{{0,8,12}},{{2,4,17}},{{0,4,17}}};
		int[][][] arr3= new int[][][]{{{2,3,9}},{{0,6,9}},{{1,5,11}},{{1,6,19}},{{2,6,6}}};
		int[][][] arr4= new int[][][]{{{1,2,3}},{{1,2,17}},{{2,2,9}},{{0,7,10}},{{0,8,14}}};
		//initialize variable
				int toMC1=0;
				int toMC2=0;
				int toMC3=0;
				int toMC4=0;
				int toMC=0;
				
				double simT1=0;
				double simT2=0;
				double simT3=0;
				double simT4=0;
				double simT=0;
				
				double BW1=0;
				double BW2=0;
				double BW3=0;
				double BW4=0;
				double simBW=0;
				
				//Calculate the update time and consumed bandwidth of each line
				for(int i=0;i<5;i++) {
					if(arr1[i][0][0]==1||arr1[i][0][0]==0) {
						toMC1=toMC1++;
						double upt=tranTime*tranTime-arr1[i][0][1]*arr1[i][0][1];
						double trt=tranTime-arr1[i][0][1];
						double t=(upt/trt)*(upt/trt);
						simT1=simT1+Math.sqrt(t);
						BW1=BW1+arr1[i][0][2];	
					}
					if(arr2[i][0][0]==1||arr2[i][0][0]==0) {
						toMC2=toMC2++;
						double upt=tranTime*tranTime-arr2[i][0][1]*arr2[i][0][1];
						double trt=tranTime-arr2[i][0][1];
						double t=(upt/trt)*(upt/trt);
						simT2=simT2+Math.sqrt(t);
						BW2=BW2+arr2[i][0][2];	
					}
					if(arr3[i][0][0]==1||arr3[i][0][0]==0) {
						toMC3=toMC3++;
						double upt=tranTime*tranTime-arr3[i][0][1]*arr3[i][0][1];
						double trt=tranTime-arr3[i][0][1];
						double t=(upt/trt)*(upt/trt);
						simT3=simT3+Math.sqrt(t);
						BW3=BW3+arr3[i][0][2];	
					}
					if(arr4[i][0][0]==1||arr4[i][0][0]==0) {
						toMC4=toMC4++;
						double upt=tranTime*tranTime-arr4[i][0][1]*arr4[i][0][1];
						double trt=tranTime-arr4[i][0][1];
						double t=(upt/trt)*(upt/trt);
						simT4=simT4+Math.sqrt(t);
						BW4=BW4+arr4[i][0][2];	
					}	
				}
				
				//Calculate the total update time and total bandwidth consumed
				simT=(simT1+simT2+simT3+simT4)/(toMC1+toMC2+toMC3+toMC4);
				simBW=BW1+BW2+BW3+BW4;
				toMC=toMC1+toMC2+toMC3+toMC4;
				
				//Judge whether the update conditions are met
				if(simBW<=BW) {
					System.out.println("SUA Update software...");
					double m1=toMC*(20-toMC);
					double simEf=(20-Math.sqrt(m1))/20;//Calculating productivity
					System.out.println("simEf="+simEf);
				}
				else {
					System.out.println("SUA Don't update software...");	
				}
				System.out.println("simT="+simT);
				System.out.println("simBW="+simBW);
	}
	
	public static void AUA() {
		double tranTime=1;
		double BW=200;
		
		int[][][] arr1= new int[][][]{{{2,7,5}},{{1,5,7}},{{1,6,10}},{{1,2,12}},{{2,2,15}}};
		int[][][] arr2= new int[][][]{{{0,4,7}},{{2,3,6}},{{0,8,12}},{{2,4,17}},{{0,4,17}}};
		int[][][] arr3= new int[][][]{{{2,3,9}},{{0,6,9}},{{1,5,11}},{{1,6,19}},{{2,6,6}}};
		int[][][] arr4= new int[][][]{{{1,2,3}},{{1,2,17}},{{2,2,9}},{{0,7,10}},{{0,8,14}}};
		
		//Build a matrix model
		int[][] a = new int[4][5];
		for (int i = 0; i < a[0].length; i++) {  
			a[0][i] = arr1[i][0][0];
			a[1][i] = arr2[i][0][0];
			a[2][i] = arr3[i][0][0];
			a[3][i] = arr4[i][0][0];
		}
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				System.out.print(a[i][j] + " ");
			}
			System.out.println();
		}
		
		//initialize variable
		int toMC1=0;
		int toMC2=0;
		int toMC3=0;
		int toMC4=0;
		int toMC=0;
		
		double updateT1=0;
		double updateT2=0;
		double updateT3=0;
		double updateT4=0;
		double updateT=0;
		
		double BW1=0;
		double BW2=0;
		double BW3=0;
		double BW4=0;
		double asyBW=0;
		
		//Calculate the update time and consumed bandwidth of each line
		for(int i=0;i<5;i++) {
			if(arr1[i][0][0]==1||arr1[i][0][0]==0) {
				toMC1=toMC1++;
				updateT1=updateT1+arr1[i][0][1];
				BW1=BW1+arr1[i][0][2];	
			}
			if(arr2[i][0][0]==1||arr2[i][0][0]==0) {
				toMC2=toMC2++;
				updateT2=updateT2+arr2[i][0][1];
				BW2=BW2+arr2[i][0][2];	
			}
			if(arr3[i][0][0]==1||arr3[i][0][0]==0) {
				toMC3=toMC3++;
				updateT3=updateT3+arr3[i][0][1];
				BW3=BW3+arr3[i][0][2];	
			}
			if(arr4[i][0][0]==1||arr4[i][0][0]==0) {
				toMC4=toMC4++;
				updateT4=updateT4+arr4[i][0][1];
				BW4=BW4+arr4[i][0][2];	
			}
			
			//Calculate the total update time and total bandwidth consumed
			toMC=toMC1+toMC2+toMC3+toMC4;
			updateT=updateT1+updateT2+updateT3+updateT4;
			double asyT1=(tranTime*toMC+updateT)/toMC;
			
			double[][] b = new double[4][1];
			for(i=0;i<4;i++) {
				b[i][0]=asyT1;
			}
			int[][] c = new int[a.length][b[0].length];
			for (int z = 0; z < c.length; z++) {
				for (int j = 0; j < c[0].length; j++) {
					for (int k = 0; k < b.length; k++) {
						c[z][j] += a[z][k] * b[k][j];
					}
				}
			}
			
			double asyT2=c[0][0];
			for(int l=0;l<c.length;l++) {
				if(c[l][0]>asyT2) {
					asyT2=c[l][0];	
				}	
			}
			double asyTime=0;
			if(asyT1<asyT2)
				asyTime=asyT2;
			else
				asyTime=asyT1;
			asyBW=(BW1+BW2+BW3+BW4)/toMC;//Calculate bandwidth for asynchronous updates
			
			//Calculating productivity for asynchronous updates
			double from = 0.0;
	        double to  = toMC;
	        int count = 10;
	        double dx = (to-from)/count;
	        double s = 0.0;
	        double p=2*(20-toMC)/20*20*20*toMC*toMC;
	        for (int m = 0; m < count; m++) {
	            s = s+(20*m*m*p*dx)+(20*20*m*p*dx);
	        }
	        System.out.println(s);
	        
	        //Judge whether the update conditions are met
	        if(asyBW<=BW) {
				System.out.println("AUA Update software...");
				System.out.println("asyEf="+s);
			}
			else {
				System.out.println("AUA Don't update software...");	
			}
			System.out.println("asyT="+asyTime);
			System.out.println("asyBW="+asyBW);
			}
		}
		
	public static void HUA() {
		double tranTime=1;
		double BW=200;
		
		int[][][] arr1= new int[][][]{{{2,7,5}},{{1,5,7}},{{1,6,10}},{{1,2,12}},{{2,2,15}}};
		int[][][] arr2= new int[][][]{{{0,4,7}},{{2,3,6}},{{0,8,12}},{{2,4,17}},{{0,4,17}}};
		int[][][] arr3= new int[][][]{{{2,3,9}},{{0,6,9}},{{1,5,11}},{{1,6,19}},{{2,6,6}}};
		int[][][] arr4= new int[][][]{{{1,2,3}},{{1,2,17}},{{2,2,9}},{{0,7,10}},{{0,8,14}}};
				
		//Build a matrix model
		int[][] a = new int[4][5];
		for (int i = 0; i < a[0].length; i++) {  
			a[0][i] = arr1[i][0][0];
			a[1][i] = arr2[i][0][0];
			a[2][i] = arr3[i][0][0];
			a[3][i] = arr4[i][0][0];
		}
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				System.out.print(a[i][j] + " ");
			}
			System.out.println();
		}
		
		//initialize variable
		int toMC1=0;
		int toMC2=0;
		int toMC3=0;
		int toMC4=0;
		int toMC=0;
				
		double updateT1=0;
		double updateT2=0;
		double updateT3=0;
		double updateT4=0;
		double updateT=0;
				
		double BW1=0;
		double BW2=0;
		double BW3=0;
		double BW4=0;
		
		int toMC11=0;
		int toMC22=0;
		int toMC33=0;
		int toMC44=0;
		
		double updateT11=0;
		double updateT22=0;
		double updateT33=0;
		double updateT44=0;
		double updateTT=0;
				
		double BW11=0;
		double BW22=0;
		double BW33=0;
		double BW44=0;
		
		//Calculate the update time and consumed bandwidth of each line
		for(int i=0;i<5;i++) {
			if(arr1[i][0][0]==1) {
				toMC1=toMC1++;
				updateT1=updateT1+arr1[i][0][1];
				BW1=BW1+arr1[i][0][2];	
			}
			if(arr1[i][0][0]==0) {
				toMC11=toMC11++;
				updateT11=updateT11+arr1[i][0][1];
				BW11=BW11+arr1[i][0][2];	
			}
			
			if(arr2[i][0][0]==1) {
				toMC2=toMC2++;
				updateT2=updateT2+arr2[i][0][1];
				BW2=BW2+arr2[i][0][2];	
			}
			if(arr2[i][0][0]==0) {
				toMC22=toMC22++;
				updateT22=updateT22+arr1[i][0][1];
				BW22=BW22+arr2[i][0][2];
			}
			
			if(arr3[i][0][0]==1) {
				toMC3=toMC3++;
				updateT3=updateT3+arr3[i][0][1];
				BW3=BW3+arr3[i][0][2];	
			}
			if(arr3[i][0][0]==0) {
				toMC33=toMC33++;
				updateT33=updateT33+arr1[i][0][1];
				BW33=BW33+arr3[i][0][2];	
			}
			
			if(arr4[i][0][0]==1) {
				toMC4=toMC4++;
				updateT4=updateT4+arr4[i][0][1];
				BW4=BW4+arr4[i][0][2];	
			}
			if(arr4[i][0][0]==0) {
				toMC44=toMC44++;
				updateT44=updateT44+arr1[i][0][1];
				BW44=BW44+arr4[i][0][2];	
			}
			
			toMC=toMC1+toMC2+toMC3+toMC4+toMC11+toMC22+toMC33+toMC44;
			updateT=updateT1+updateT2+updateT3+updateT4;
			updateTT=updateT11+updateT22+updateT33+updateT44;
			double huaT1=(toMC*tranTime+updateT+updateTT)/toMC;
			
			double[][] b = new double[4][1];
			for(i=0;i<4;i++) {
				b[i][0]=huaT1;
			}
			int[][] c = new int[a.length][b[0].length];
			for (int z = 0; z < c.length; z++) {
				for (int j = 0; j < c[0].length; j++) {
					for (int k = 0; k < b.length; k++) {
						c[z][j] += a[z][k] * b[k][j];
					}
				}
			}
			
			double huaT2=c[0][0];
			for(int l=0;l<c.length;l++) {
				if(c[l][0]>huaT2) {
					huaT2=c[l][0];	
				}	
			}
			double huaTime=0;
			if(huaT1<huaT2)
				huaTime=huaT2;
			else
				huaTime=huaT1;
			double huaBW=(BW1+BW2+BW3+BW4)/toMC+BW11+BW22+BW33+BW44;//Calculate bandwidth for asynchronous updates
			
			//Calculating productivity
			double from = 0.0;
	        double to  = toMC;
	        int count = 10;
	        double dx = (to-from)/count;
	        double s = 0.0;
	        double p=2*(20-toMC)/20*20*20*toMC*toMC;
	        for (int m = 0; m < count; m++) {
	            s = s+(20*m*m*p*dx)+(20*20*m*p*dx);
	        }
	        System.out.println(s);
	        
	      //Judge whether the update conditions are met
	        if(huaBW<=BW) {
				System.out.println("HUA Update software...");
				System.out.println("huaEf="+s);
			}
			else {
				System.out.println("HUA Don't update software...");	
			}
			System.out.println("huaT="+huaTime);
			System.out.println("huaBW="+huaBW);
	   }
		
	}	
	


	/**
	 * Creates the fog devices in the physical topology of the simulation.
	 * @param userId
	 * @param appId
	 */
	private static void createFogDevices(int userId, String appId) {
		FogDevice cloud = createFogDevice("cloud", 44800, 40000, 100, 10000, 0, 0.01, 16*103, 16*83.25); // creates the fog device Cloud at the apex of the hierarchy with level=0
		cloud.setParentId(-1);
		FogDevice proxy = createFogDevice("proxy-server", 2800, 4000, 10000, 10000, 1, 0.0, 107.339, 83.4333); // creates the fog device Proxy Server (level=1)
		proxy.setParentId(cloud.getId()); // setting Cloud as parent of the Proxy Server
		proxy.setUplinkLatency(100); // latency of connection from Proxy Server to the Cloud is 100 ms
		
		fogDevices.add(cloud);
		fogDevices.add(proxy);
		
		for(int i=0;i<numOfDepts;i++){
			addGw(i+"", userId, appId, proxy.getId()); // adding a fog device for every Gateway in physical topology. The parent of each gateway is the Proxy Server
		}
		
	}

	private static FogDevice addGw(String id, int userId, String appId, int parentId){
		FogDevice dept = createFogDevice("d-"+id, 2800, 4000, 10000, 10000, 1, 0.0, 107.339, 83.4333);
		fogDevices.add(dept);
		dept.setParentId(parentId);
		dept.setUplinkLatency(4); // latency of connection between gateways and proxy server is 4 ms
		for(int i=0;i<numOfMobilesPerDept;i++){
			String mobileId = id+"-"+i;
			FogDevice mobile = addMobile(mobileId, userId, appId, dept.getId()); // adding mobiles to the physical topology. Smartphones have been modeled as fog devices as well.
			mobile.setUplinkLatency(2); // latency of connection between the smartphone and proxy server is 4 ms
			fogDevices.add(mobile);
		}
		return dept;
	}
	
	private static FogDevice addMobile(String id, int userId, String appId, int parentId){
		FogDevice mobile = createFogDevice("m-"+id, 1000, 1000, 10000, 270, 3, 0, 87.53, 82.44);
		mobile.setParentId(parentId);
		Sensor eegSensor = new Sensor("s-"+id, "EEG", userId, appId, new DeterministicDistribution(EEG_TRANSMISSION_TIME)); // inter-transmission time of EEG sensor follows a deterministic distribution
		sensors.add(eegSensor);
		Actuator display = new Actuator("a-"+id, userId, appId, "DISPLAY");
		actuators.add(display);
		eegSensor.setGatewayDeviceId(mobile.getId());
		eegSensor.setLatency(6.0);  // latency of connection between EEG sensors and the parent Smartphone is 6 ms
		display.setGatewayDeviceId(mobile.getId());
		display.setLatency(1.0);  // latency of connection between Display actuator and the parent Smartphone is 1 ms
		return mobile;
	}
	
	/**
	 * Creates a vanilla fog device
	 * @param nodeName name of the device to be used in simulation
	 * @param mips MIPS
	 * @param ram RAM
	 * @param upBw uplink bandwidth
	 * @param downBw downlink bandwidth
	 * @param level hierarchy level of the device
	 * @param ratePerMips cost rate per MIPS used
	 * @param busyPower
	 * @param idlePower
	 * @return
	 */
	private static FogDevice createFogDevice(String nodeName, long mips,
			int ram, long upBw, long downBw, int level, double ratePerMips, double busyPower, double idlePower) {
		
		List<Pe> peList = new ArrayList<Pe>();

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating

		int hostId = FogUtils.generateEntityId();
		long storage = 1000000; // host storage
		int bw = 10000;

		PowerHost host = new PowerHost(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerOverbooking(bw),
				storage,
				peList,
				new StreamOperatorScheduler(peList),
				new FogLinearPowerModel(busyPower, idlePower)
			);

		List<Host> hostList = new ArrayList<Host>();
		hostList.add(host);

		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
													// devices by now

		FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
				arch, os, vmm, host, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		FogDevice fogdevice = null;
		try {
			fogdevice = new FogDevice(nodeName, characteristics, 
					new AppModuleAllocationPolicy(hostList), storageList, 10, upBw, downBw, 0, ratePerMips);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		fogdevice.setLevel(level);
		return fogdevice;
	}

	/**
	 * Function to create the EEG Tractor Beam game application in the DDF model. 
	 * @param appId unique identifier of the application
	 * @param userId identifier of the user of the application
	 * @return
	 */
	@SuppressWarnings({"serial" })
	private static Application createApplication(String appId, int userId){
		
		Application application = Application.createApplication(appId, userId); // creates an empty application model (empty directed graph)
		
		/*
		 * Adding modules (vertices) to the application model (directed graph)
		 */
		application.addAppModule("client", 10); // adding module Client to the application model
		application.addAppModule("concentration_calculator", 10); // adding module Concentration Calculator to the application model
		application.addAppModule("connector", 10); // adding module Connector to the application model
		
		/*
		 * Connecting the application modules (vertices) in the application model (directed graph) with edges
		 */
		if(EEG_TRANSMISSION_TIME==10)
			application.addAppEdge("EEG", "client", 2000, 500, "EEG", Tuple.UP, AppEdge.SENSOR); // adding edge from EEG (sensor) to Client module carrying tuples of type EEG
		else
			application.addAppEdge("EEG", "client", 3000, 500, "EEG", Tuple.UP, AppEdge.SENSOR);
		application.addAppEdge("client", "concentration_calculator", 3500, 500, "_SENSOR", Tuple.UP, AppEdge.MODULE); // adding edge from Client to Concentration Calculator module carrying tuples of type _SENSOR
		application.addAppEdge("concentration_calculator", "connector", 100, 1000, 1000, "PLAYER_GAME_STATE", Tuple.UP, AppEdge.MODULE); // adding periodic edge (period=1000ms) from Concentration Calculator to Connector module carrying tuples of type PLAYER_GAME_STATE
		application.addAppEdge("concentration_calculator", "client", 14, 500, "CONCENTRATION", Tuple.DOWN, AppEdge.MODULE);  // adding edge from Concentration Calculator to Client module carrying tuples of type CONCENTRATION
		application.addAppEdge("connector", "client", 100, 28, 1000, "GLOBAL_GAME_STATE", Tuple.DOWN, AppEdge.MODULE); // adding periodic edge (period=1000ms) from Connector to Client module carrying tuples of type GLOBAL_GAME_STATE
		application.addAppEdge("client", "DISPLAY", 1000, 500, "SELF_STATE_UPDATE", Tuple.DOWN, AppEdge.ACTUATOR);  // adding edge from Client module to Display (actuator) carrying tuples of type SELF_STATE_UPDATE
		application.addAppEdge("client", "DISPLAY", 1000, 500, "GLOBAL_STATE_UPDATE", Tuple.DOWN, AppEdge.ACTUATOR);  // adding edge from Client module to Display (actuator) carrying tuples of type GLOBAL_STATE_UPDATE
		
		/*
		 * Defining the input-output relationships (represented by selectivity) of the application modules. 
		 */
		application.addTupleMapping("client", "EEG", "_SENSOR", new FractionalSelectivity(0.9)); // 0.9 tuples of type _SENSOR are emitted by Client module per incoming tuple of type EEG 
		application.addTupleMapping("client", "CONCENTRATION", "SELF_STATE_UPDATE", new FractionalSelectivity(1.0)); // 1.0 tuples of type SELF_STATE_UPDATE are emitted by Client module per incoming tuple of type CONCENTRATION 
		application.addTupleMapping("concentration_calculator", "_SENSOR", "CONCENTRATION", new FractionalSelectivity(1.0)); // 1.0 tuples of type CONCENTRATION are emitted by Concentration Calculator module per incoming tuple of type _SENSOR 
		application.addTupleMapping("client", "GLOBAL_GAME_STATE", "GLOBAL_STATE_UPDATE", new FractionalSelectivity(1.0)); // 1.0 tuples of type GLOBAL_STATE_UPDATE are emitted by Client module per incoming tuple of type GLOBAL_GAME_STATE
	
		/*
		 * Defining application loops to monitor the latency of. 
		 * Here, we add only one loop for monitoring : EEG(sensor) -> Client -> Concentration Calculator -> Client -> DISPLAY (actuator)
		 */
		final AppLoop loop1 = new AppLoop(new ArrayList<String>(){{add("EEG");add("client");add("concentration_calculator");add("client");add("DISPLAY");}});
		List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop1);}};
		application.setLoops(loops);
		
		return application;
	}
}