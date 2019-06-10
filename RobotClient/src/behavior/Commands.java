package behavior;
/*
public class commands implements  Í_Commands{
	
	int nrBalls =0;
	int goals=0;
	RegulatedMotor[] motorList = new RegulatedMotor[1];
	

	@Override
	public void start() {
		//maybe some kind of find myself function here
		
		while(goals<10) {
		//maybe shouldn't stop at all, since it may sometimes fail to recognise missing a ball
		if(findBall()) {
			getBall();
		}
		else if(nrBalls>2) {
			 scoreGoal();
		
		
		}
		else {searchMap();}
		}
	}

	@Override
	public boolean findBall() {
		boolean foundBall = false;
		//if this is called, while at max capacity, don't search for balls
		if(nrBalls>3) {return false;}
		
		//something something take a picture and find balls
		
		if(foundBall) {return true;}
		
		
		return false;
		
		
	}

	@Override
	public void getBall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scoreGoal() {
		goals+=nrBalls;
		nrBalls=0;
		
	}

	@Override
	public void searchMap() {
		EV3UltrasonicSensor s = new EV3UltrasonicSensor(SensorPort.S1);
		EV3IRSensor c = new EV3IRSensor(SensorPort.S2);
		int loops = 0;
		boolean stop = true;
		while (loops < 1) {

			stop = true;
			float[] sampleArray = new float[1];
			System.out.println("Ready to Murder!");
			s.enable();
			while (stop) {
				 Motor.A.forward();
				 Motor.B.forward();
				SampleProvider e = s.getDistanceMode();
				e.fetchSample(sampleArray, e.sampleSize() - 1);

				System.out.println("Ultra:" + sampleArray[0]);

				if (sampleArray[0] <= 0.20) {

					Motor.A.stop();
					Motor.B.stop();
					stop = false;
				}
				//here is where it should reverse and change direction
				//Motor.A.backward();
				//Motor.B.backward();
	
				motorList[0]=Motor.A;
				Motor.B.synchronizeWith(motorList);
				Motor.B.startSynchronization();
				Motor.A.rotateTo(3000);
				Motor.B.rotateTo(3000);
				Motor.B.endSynchronization();
				
				
			}
			stop = true;
			while (stop) {
				 Motor.A.backward();
				 Motor.B.backward();
				SampleProvider d = c.getDistanceMode();
				d.fetchSample(sampleArray, d.sampleSize() - 1);

				System.out.println("IR:" + sampleArray[0]);

				if (sampleArray[0] <= 20) {

					Motor.A.stop();
					Motor.B.stop();
					stop = false;
				}
			}
			loops++;
		}
		Motor.B.rotate(720);
		s.close();
		c.close();
		start();


		/*
		 * while (32 != button) {
		 * 
		 * switch (Button.waitForAnyPress()) { // left case 16: { button = 16;
		 * System.out.println("Going Left"); Motor.B.setSpeed(100 *
		 * Battery.getVoltage()); Motor.B.forward(); Motor.A.setSpeed(100 *
		 * Battery.getVoltage()); Motor.A.backward(); break; } // right case 8: { button
		 * = 8; System.out.println("Going Right"); Motor.A.setSpeed(100 *
		 * Battery.getVoltage()); Motor.A.forward(); Motor.B.setSpeed(100 *
		 * Battery.getVoltage()); Motor.B.backward();
		 * 
		 * break; } // escape case 32: { button = 32;
		 * System.out.println("Stopping The Bot"); if (Motor.A.isMoving() ||
		 * Motor.B.isMoving()) { Motor.A.stop(); Motor.B.stop();
		 * 
		 * }
		 * 
		 * break; } // enter case 2: { button = 10;
		 * System.out.println("Entering Pincer Mode"); while (button != 2 && !(button ==
		 * 32)) { switch (Button.waitForAnyPress()) { case 2: { button = 2;
		 * System.out.println("Leaveing Pincer Mode");
		 * 
		 * if (Motor.A.isMoving() || Motor.B.isMoving()) { Motor.A.stop();
		 * Motor.B.stop(); } break; } case 1: {
		 * System.out.println("Closeing The pincers"); Motor.C.setSpeed(100 *
		 * Battery.getVoltage()); Motor.C.rotate(-45); break; } case 4: {
		 * System.out.println("Opeing pincers"); Motor.C.setSpeed(100 *
		 * Battery.getVoltage()); Motor.C.rotate(45);
		 * 
		 * break; } case 8: { System.out.println("Stopping The Bot"); if
		 * (Motor.A.isMoving() || Motor.B.isMoving()) { Motor.A.stop(); Motor.B.stop();
		 * } break; } case 16: { System.out.println("Stopping The Bot"); if
		 * (Motor.A.isMoving() || Motor.B.isMoving()) { Motor.A.stop(); Motor.B.stop();
		 * } break; } case 32: { button = 32; System.out.println("Stopping The Bot"); if
		 * (Motor.A.isMoving() |
		 * | Motor.B.isMoving()) { Motor.A.stop(); Motor.B.stop();
		 * } break; }
		 * 
		 * } } break; } // up case 1: { button = 1; System.out.println("Going Foward");
		 * Motor.A.setSpeed(100 * Battery.getVoltage()); Motor.A.forward();
		 * Motor.B.setSpeed(100 * Battery.getVoltage()); Motor.B.forward();
		 * 
		 * break; } // down case 4: { button = 4; System.out.println("Going Back");
		 * Motor.A.setSpeed(100 * Battery.getVoltage()); Motor.A.backward();
		 * Motor.B.setSpeed(100 * Battery.getVoltage()); Motor.B.backward();
		 * 
		 * break; }
		 * 
		 * } }
		 
		
	}

}
*/