package model;

import javafx.geometry.Dimension2D;

public class ScreenSettings {
	
	private int ID;
	
	private String name;
	
	private String title;
	
	private Dimension2D windowSize;
	
	private Dimension2D minWindowSize;
	
	
	
	public ScreenSettings(ScreenType screen, Dimension2D size, Dimension2D minSize) {
		this.ID = screen.getID();
		this.name = screen.getName();
		this.title = screen.getTitle();
		this.windowSize = size;
		this.minWindowSize = minSize;
	}	
	


	/**
	 * @return the ID
	 */
	public int getID() {
		return this.ID;
	}

	/**
	 * @return the windowTitle
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	public double getWidth(){
		return this.windowSize.getWidth();
	}
	
	public double getHeight(){
		return this.windowSize.getHeight();
	}
	
	public double getMinWidth(){
		return this.minWindowSize.getWidth();
	}
	
	public double getMinHeight(){
		return this.minWindowSize.getHeight();
	}
	
	public String toString(){
		return "ScreenConfig (" + this.ID + ", " + this.name + ", " + this.title + ")";
	}
	
}
