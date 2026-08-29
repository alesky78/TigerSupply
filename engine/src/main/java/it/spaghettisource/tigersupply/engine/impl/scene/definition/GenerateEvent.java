package it.spaghettisource.tigersupply.engine.impl.scene.definition;

public class GenerateEvent {

	private String name;
	private String time;

	public GenerateEvent(String name,String time){
		this.name = name;
		this.time = time;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("event-> name:"+name+" time:"+time);		
		return buffer.toString();
	}		
	
}
