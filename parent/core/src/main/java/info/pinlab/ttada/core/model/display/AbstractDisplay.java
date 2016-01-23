package info.pinlab.ttada.core.model.display;




public abstract class AbstractDisplay implements Display{
	transient int hash = 0;
	private String brief = null;
	@Override
	public void setBrief(String brief){
		this.brief = brief;
	}
	@Override
	public String getBrief(){
		return brief;
	}
	
	@Override
	public Class<? super Display> getInterfaceClass() {
		return Display.class;
	}

	
}
