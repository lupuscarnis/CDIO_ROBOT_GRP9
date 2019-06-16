package application;

public class staticFXCont {
	private	 FXController fx;
	private static  staticFXCont sfx;

	public  FXController getfxInstance() {
		
		
		return fx;
	}
	public static staticFXCont getInstance() {
		if(sfx == null) {
			sfx = new staticFXCont();
					return sfx;
		}
		
		return sfx ;
	}	
	
	private staticFXCont() {
		// TODO Auto-generated constructor stub
	}
	public void setFXController(FXController s) {
		fx = s;
	}
	
	

}
